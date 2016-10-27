package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

/**
 * Reference: http://www.math.colostate.edu/.
 */
public class HalfEdgeStorageX {
	/** Array index of left edge. */
	private static final int LEFTEDGE = 0;

	/** Array index of right edge. */
	private static final int RIGHTEDGE = 1;

	/** The minimum X coordinate of any site. */
	private final double minimumX;

	/** The width of the bounding box. */
	private final double width;

	/** The length of the hash table. */
	private final int hashsize;

	/** The hash table. */
	private Halfedge[] halfEdgesHashTable;

	/** The leftmost element in the list. */
	private Halfedge leftEnd;

	/** The rightmost element in the list. */
	private Halfedge rightEnd;

	/**
	 * Constructor.
	 *
	 * @param sqrtNsites
	 *            the square root of (the number of sites + 4)
	 * @param minimumXOfAnySite
	 *            the minimum X value of any site
	 * @param totalXWidthOfAllSites
	 *            the total X width spanned by all sites
	 */
	public HalfEdgeStorageX(final int sqrtNsites, final double minimumXOfAnySite, final double totalXWidthOfAllSites) {
		this.minimumX = minimumXOfAnySite;
		this.width = totalXWidthOfAllSites;

		this.hashsize = 2 * sqrtNsites;
		this.halfEdgesHashTable = new Halfedge[this.hashsize];

		for (int i = 0; i < this.hashsize; i += 1) {
			this.halfEdgesHashTable[i] = null;
		}

		this.leftEnd = new Halfedge(null, 0);
		this.rightEnd = new Halfedge(null, 0);

		this.leftEnd.setHalfEdgeLeft(null);
		this.leftEnd.setHalfEdgeRight(rightEnd);

		this.rightEnd.setHalfEdgeLeft(leftEnd);
		this.rightEnd.setHalfEdgeRight(null);

		this.halfEdgesHashTable[0] = leftEnd;
		this.halfEdgesHashTable[hashsize - 1] = rightEnd;
	}

	/**
	 * Inserts an edge in the linked list to the right of a specified edge.
	 *
	 * @param leftHalfEdge
	 *            the edge that should be to the left of the inserted edge
	 * @param edgeToInsert
	 *            the edge to insert
	 */
	public final void insert(final Halfedge leftHalfEdge, final Halfedge edgeToInsert) {
		edgeToInsert.setHalfEdgeLeft(leftHalfEdge);
		edgeToInsert.setHalfEdgeRight(leftHalfEdge.getHalfEdgeRight());
		leftHalfEdge.getHalfEdgeRight().setHalfEdgeLeft(edgeToInsert);
		leftHalfEdge.setHalfEdgeRight(edgeToInsert);
	}

	/**
	 * Deletes an edge from the linked list.
	 *
	 * @param edge
	 *            the edge to delete
	 */
	public final void deleteEdge(final Halfedge edge) {
		edge.getHalfEdgeLeft().setHalfEdgeRight(edge.getHalfEdgeRight());
		edge.getHalfEdgeRight().setHalfEdgeLeft(edge.getHalfEdgeLeft());
		edge.setDeleted(true);
	}

	/**
	 * Gets entry from hash table, pruning any deleted nodes.
	 *
	 * @param index
	 *            the index in the hash table
	 * @return the found entry
	 */
	public final Halfedge getHashTableEntry(final int index) {
		Halfedge edge;

		if ((index < 0) || (index >= hashsize)) {
			edge = null;
		} else {
			edge = halfEdgesHashTable[index];

			if ((edge != null) && edge.isDeleted()) {
				halfEdgesHashTable[index] = null;
				edge = null;
			}
		}

		return edge;
	}

	/**
	 * Left boundary.
	 *
	 * @param vertex
	 *            the vertex
	 * @return the located half-edge
	 */
	public final Halfedge leftBoundary(final Vertex vertex) {
		int bucket;
		Halfedge halfEdge;

		// identify the bucket near the point based on its X coordinate
		bucket = (int) ((vertex.getxCoordinate() - minimumX) / width * hashsize);

		// check the bucket range
		if (bucket < 0) {
			bucket = 0;
		} else if (bucket >= this.hashsize) {
			bucket = this.hashsize - 1;
		}

		halfEdge = getHashTableEntry(bucket);

		if (halfEdge == null) {

			// if the HE isn't found, search backwards and forwards in the hash
			// map for the first non-null entry
			for (int i = 1; i < this.hashsize; i += 1) {
				halfEdge = getHashTableEntry(bucket - i);

				if (halfEdge != null) {
					break;
				}

				halfEdge = getHashTableEntry(bucket + i);

				if (halfEdge != null) {
					break;
				}
			}
		}

		if (halfEdge != null) {
			// search linear list of half edges for the correct one
			if ((halfEdge == this.leftEnd) || ((halfEdge != this.rightEnd) && rightOf(halfEdge, vertex))) {

				// keep going right on the list until either the end is
				// reached, or you find the 1st edge which the point isn't to
				// the right of
				do {
					halfEdge = halfEdge.getHalfEdgeRight();
				} while ((halfEdge != this.rightEnd) && rightOf(halfEdge, vertex));

				halfEdge = halfEdge.getHalfEdgeLeft();
			} else {

				// if the point is to the left of the HalfEdge, then search
				// left for the HE just to the left of the point
				do {
					halfEdge = halfEdge.getHalfEdgeLeft();
				} while ((halfEdge != this.leftEnd) && !rightOf(halfEdge, vertex));
			}
		}

		// update hash table and reference counts
		if ((bucket > 0) && (bucket < (hashsize - 1))) {
			this.halfEdgesHashTable[bucket] = halfEdge;
		}

		return halfEdge;
	}

	/**
	 * Tests if the specified point is to right of the specified half edge.
	 *
	 * @param halfEdge
	 *            the half edge
	 * @param point
	 *            the point
	 * @return true if point is to the right of edge
	 */
	private boolean rightOf(final Halfedge halfEdge, final Vertex point) {
		VoronoiEdge voronoiEdge;
		Site topsite;
		boolean rightOfSite;
		boolean above;
		boolean fast;
		double distanceXAxisBetweenPoints;
		double distanceYAxisBetweenPoints;
		double distanceXAxisBetweenSites;
		double t1;
		double t2;
		double t3;
		double yl;
		boolean result;

		voronoiEdge = halfEdge.getEdge();
		topsite = voronoiEdge.getSiteBisecting()[1];
		rightOfSite = (point.getxCoordinate() > topsite.getxCoordinate());

		if (rightOfSite && (halfEdge.getEdgePosition() == LEFTEDGE)) {
			result = true;
		} else if (!rightOfSite && (halfEdge.getEdgePosition() == RIGHTEDGE)) {
			result = false;
		} else {

			if (voronoiEdge.getCoefficientA() == 1.0) {
				distanceYAxisBetweenPoints = point.getyCoordinate() - topsite.getyCoordinate();
				distanceXAxisBetweenPoints = point.getxCoordinate() - topsite.getxCoordinate();
				fast = false;

				if ((!rightOfSite & (voronoiEdge.getCoefficientB() < 0.0))
						| (rightOfSite & (voronoiEdge.getCoefficientB() >= 0.0))) {
					above = distanceYAxisBetweenPoints >= (voronoiEdge.getCoefficientB() * distanceXAxisBetweenPoints);
					fast = above;
				} else {
					above = (point.getxCoordinate()
							+ (point.getyCoordinate() * voronoiEdge.getCoefficientB())) > voronoiEdge.getCoefficientC();

					if (voronoiEdge.getCoefficientB() < 0.0) {
						above = !above;
					}

					if (!above) {
						fast = true;
					}
				}

				if (!fast) {
					distanceXAxisBetweenSites = topsite.getxCoordinate()
							- (voronoiEdge.getSiteBisecting()[0]).getxCoordinate();
					above = (voronoiEdge.getCoefficientB() * ((distanceXAxisBetweenPoints * distanceXAxisBetweenPoints)
							- (distanceYAxisBetweenPoints * distanceYAxisBetweenPoints))) < (distanceXAxisBetweenSites
									* distanceYAxisBetweenPoints
									* (1.0 + (2.0 * distanceXAxisBetweenPoints / distanceXAxisBetweenSites)
											+ (voronoiEdge.getCoefficientB() * voronoiEdge.getCoefficientB())));

					if (voronoiEdge.getCoefficientB() < 0.0) {
						above = !above;
					}
				}
			} else {
				yl = voronoiEdge.getCoefficientC() - (voronoiEdge.getCoefficientA() * point.getxCoordinate());
				t1 = point.getyCoordinate() - yl;
				t2 = point.getxCoordinate() - topsite.getxCoordinate();
				t3 = yl - topsite.getyCoordinate();
				above = (t1 * t1) > ((t2 * t2) + (t3 * t3));
			}

			if (halfEdge.getEdgePosition() == LEFTEDGE) {
				result = above;
			} else {
				result = !above;
			}
		}

		return result;
	}

	/**
	 * Get he leftmost element in the list.
	 * 
	 * @return the leftmost element in the list
	 */
	public final Halfedge getLeftEnd() {
		return leftEnd;
	}

	/**
	 * Get the rightmost element in the list.
	 * 
	 * @return the rightmost element in the list.
	 */
	public final Halfedge getRightEnd() {
		return rightEnd;
	}
}
