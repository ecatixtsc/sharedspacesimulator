package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

/**
 * A hash table to store half-edges.
 * 
 * Reference: http://www.math.colostate.edu/
 */
public class HalfEdgeStorageY {
	/** The minimum Y coordinate of any site. */
	private final double minimumYSiteCoordinate;

	/** The height of the bounding box. */
	private final double boundingBoxHeight;

	/** The number of half-edges in the hash table. */
	private int numberOfHalfEdges;

	/** The minimum hash bucket. */
	private int minimumHashBucket;

	/** Hash size. */
	private final int hashsize;

	/** Hash table to store half-edges. */
	private final Halfedge[] edgesHashTable;

	/**
	 * Constructor.
	 *
	 * @param sqrtNumberOfSites
	 *            the square root of (the number of sites + 4)
	 * @param minimumYOfAnySite
	 *            the minimum Y value of any site
	 * @param totalYHeightOfAllSites
	 *            the total Y height spanned by all sites
	 */
	public HalfEdgeStorageY(final int sqrtNumberOfSites, final double minimumYOfAnySite,
			final double totalYHeightOfAllSites) {
		this.minimumYSiteCoordinate = minimumYOfAnySite;
		this.boundingBoxHeight = totalYHeightOfAllSites;

		this.numberOfHalfEdges = 0;
		this.minimumHashBucket = 0;
		final int sqrtFactor = 4;
		this.hashsize = sqrtFactor * sqrtNumberOfSites;
		this.edgesHashTable = new Halfedge[hashsize];

		for (int i = 0; i < hashsize; i += 1) {
			edgesHashTable[i] = new Halfedge();
		}
	}

	/**
	 * Determines the bucket where a half-edge should be stored. The bucket is
	 * based on the y* value, and runs from 0 (y* is at the minimum y) to the
	 * hash-size (y* is at the maximum y).
	 *
	 * <p>
	 * The minimum hash bucket is updated if the result is smaller than the
	 * current minimum.
	 *
	 * @param halfEdge
	 *            the half-edge
	 * @return the bucket
	 */
	public final int bucket(final Halfedge halfEdge) {
		int bucket;

		bucket = (int) ((halfEdge.getYstar() - minimumYSiteCoordinate) / boundingBoxHeight * hashsize);

		if (bucket < 0) {
			bucket = 0;
		}

		if (bucket >= hashsize) {
			bucket = hashsize - 1;
		}

		if (bucket < minimumHashBucket) {
			minimumHashBucket = bucket;
		}

		return bucket;
	}

	/**
	 * Adds the HalfEdge to the ordered linked list of vertices in its
	 * appropriate bucket.
	 *
	 * @param halfEdgeToAdd
	 *            the half edge to add
	 * @param halfEdgeVertex
	 *            the vertex the half edge is associated with
	 * @param distanceToNearestSite
	 *            the offset from y to y* (distance to nearest site)
	 */
	public final void insertHalfEdge(final Halfedge halfEdgeToAdd, final Site halfEdgeVertex,
			final double distanceToNearestSite) {
		Halfedge last;
		Halfedge next;

		halfEdgeToAdd.setVertex(halfEdgeVertex);
		halfEdgeToAdd.setYstar(halfEdgeVertex.getyCoordinate() + distanceToNearestSite);

		// Get the first entry in the bucket this half edge should be in
		// (based on its y-start value)
		last = edgesHashTable[bucket(halfEdgeToAdd)];

		// Insert the half edge in the bucket's linked list such that the
		// items in the linked list are sorted by increasing y-star
		next = last.getNextHalfEdge();

		while ((next != null)
				&& ((halfEdgeToAdd.getYstar() > next.getYstar()) || ((halfEdgeToAdd.getYstar() == next.getYstar())
						&& (halfEdgeVertex.getxCoordinate() > next.getVertex().getxCoordinate())))) {
			last = next;
			next = last.getNextHalfEdge();
		}

		halfEdgeToAdd.setNextHalfEdge(last.getNextHalfEdge());
		last.setNextHalfEdge(halfEdgeToAdd);

		numberOfHalfEdges += 1;
	}

	/**
	 * Removes the HalfEdge from the list of vertices.
	 *
	 * @param halfEdgeToRemove
	 *            the half edge to remove
	 */
	public final void deleteHalfEdge(final Halfedge halfEdgeToRemove) {
		Halfedge last;

		if (halfEdgeToRemove.getVertex() != null) {
			last = edgesHashTable[bucket(halfEdgeToRemove)];

			while (last.getNextHalfEdge() != halfEdgeToRemove) {
				last = last.getNextHalfEdge();
			}

			last.setNextHalfEdge(halfEdgeToRemove.getNextHalfEdge());
			numberOfHalfEdges -= 1;
			halfEdgeToRemove.setVertex(null);
		}
	}

	/**
	 * Tests whether the hashtable is empty.
	 *
	 * @return true if the hash table is empty
	 */
	public final boolean hashTableIsEmpty() {
		return (numberOfHalfEdges == 0);
	}

	/**
	 * Gets the vertex in the hashtable with the minimum y-star value.
	 *
	 * @return the vertex
	 */
	public final Vertex minimumVertex() {
		while (edgesHashTable[minimumHashBucket].getNextHalfEdge() == null) {
			minimumHashBucket += 1;
		}

		return new Vertex(edgesHashTable[minimumHashBucket].getNextHalfEdge().getVertex().getxCoordinate(),
				edgesHashTable[minimumHashBucket].getNextHalfEdge().getYstar());
	}

	/**
	 * Gets the half edge with the minimum y-star value and deletes that half
	 * edge from the hashtable.
	 *
	 * @return the half-edge
	 */
	public final Halfedge extractMinimum() {
		Halfedge currentHalfEdge;

		currentHalfEdge = edgesHashTable[minimumHashBucket].getNextHalfEdge();
		edgesHashTable[minimumHashBucket].setNextHalfEdge(currentHalfEdge.getNextHalfEdge());
		numberOfHalfEdges -= 1;

		return currentHalfEdge;
	}
}
