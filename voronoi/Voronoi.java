package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A class to compute Voronoi diagrams.
 * 
 * Reference: http://www.math.colostate.edu/
 */
public class Voronoi {

	/** Array index of left edge. */
	private static final int LEFTEDGE = 0;

	/** Array index of right edge. */
	private static final int RIGHTEDGE = 1;

	/** A distance within which we consider points equal. */
	private final double epsilon;

	/** The minimum distance between sites. */
	private final double minimumSiteDistance;

	/** The minimum X coordinate of any site. */
	private double minimumXCoordinate;

	/** The maximum X coordinate of any site. */
	private double maximumXCoordinate;

	/** The minimum Y coordinate of any site. */
	private double minimumYCoordinate;

	/** The maximum Y coordinate of any site. */
	private double maximumYCoordinate;

	/** The width of the bounding box. */
	private double boundingBoxWidth;

	/** The height of the bounding box. */
	private double boundingBoxHeight;

	/** A hash table to store half-edges scrolling the Y axis. */
	private HalfEdgeStorageY halfEdgeStorageY;

	/** A hash table to store half-edges scrolling the X axis. */
	private HalfEdgeStorageX halfEdgeStorageX;

	/** The number of sites. */
	private int numberOfSites;

	/** The square root of (the number of sites + 4). */
	private int sqrtNumberOfSites;

	/** The sorted list of sites. */
	private Site[] sites;

	/** The index of the site being processed. */
	private int currentSiteIndex;

	/** The number of vertices. */
	private int numberOfVertices;

	/** The number of edges. */
	private int numberOfEdges;

	/** Site located South. */
	private Site bottomsite;

	/** The set of all edges in the graph. */
	private final List<GraphEdge> allGraphEdges;

	/** Percentage value. */
	private static final int SCALE = 100;

	/**
	 * Constructs a new Voronoi.
	 *
	 * @param minSiteSpacing
	 *            the minimum distance between sites - any sites closer than
	 *            this distance will be considered a single site
	 */
	public Voronoi(final double minSiteSpacing) {
		this.currentSiteIndex = 0;
		this.sites = null;
		this.minimumSiteDistance = minSiteSpacing;
		this.epsilon = minSiteSpacing / SCALE;
		this.numberOfVertices = 0;
		this.numberOfEdges = 0;
		this.numberOfSites = 0;
		this.sqrtNumberOfSites = 0;
		this.allGraphEdges = new LinkedList<GraphEdge>();
	}

	/**
	 * Generates the Voronoi diagram using Fortune's algorithm.
	 *
	 * @param points
	 *            an array of coordinates for each site
	 * @return the list of graph edges in the Voronoi diagram
	 */
	public final List<GraphEdge> generateVoronoiDiagram(final Vertex[] points) {
		GraphEdge edge;

		// Remove any prior run's output
		allGraphEdges.clear();

		// Compute bounding box (only used to clip edges in the future)
		computeBoundingBox(points);

		// Create a list of sites from the input list of points that is sorted
		// by increasing Y coordinate (and increasing X for values of equal Y)
		makeSortedSiteList(points);

		// Build the Voronoi diagram
		voronoiDiagramBuild();

		// Remove any edges whose end and start points are the same
		for (int edgeIndex = allGraphEdges.size() - 1; edgeIndex >= 0; edgeIndex--) {
			edge = allGraphEdges.get(edgeIndex);

			if (isSame(edge.getxCoordinate1(), edge.getyCoordinate1(), edge.getxCoordinate2(),
					edge.getyCoordinate2())) {
				allGraphEdges.remove(edgeIndex);
			}
		}

		return allGraphEdges;
	}

	/**
	 * Computes the bounding box of a set of points.
	 *
	 * @param points
	 *            the points
	 */
	private void computeBoundingBox(final Vertex[] points) {
		minimumXCoordinate = Double.MAX_VALUE;
		minimumYCoordinate = Double.MAX_VALUE;
		maximumXCoordinate = -Double.MAX_VALUE;
		maximumYCoordinate = -Double.MAX_VALUE;

		for (Vertex currentVertex : points) {
			if (currentVertex.getxCoordinate() < minimumXCoordinate) {
				minimumXCoordinate = currentVertex.getxCoordinate();
			}

			if (currentVertex.getxCoordinate() > maximumXCoordinate) {
				maximumXCoordinate = currentVertex.getxCoordinate();
			}

			if (currentVertex.getyCoordinate() < minimumYCoordinate) {
				minimumYCoordinate = currentVertex.getyCoordinate();
			}

			if (currentVertex.getyCoordinate() > maximumYCoordinate) {
				maximumYCoordinate = currentVertex.getyCoordinate();
			}
		}

		boundingBoxHeight = maximumYCoordinate - minimumYCoordinate;
		boundingBoxWidth = maximumXCoordinate - minimumXCoordinate;
	}

	/**
	 * Creates a list of Site objects whose coordinates are a given set of
	 * points, and that is sorted in order of increasing Y (then increasing X if
	 * Y coordinates are the same).
	 *
	 * @param points
	 *            the list of points
	 */
	private void makeSortedSiteList(final Vertex[] points) {
		numberOfSites = points.length;
		final int quad = 4;
		sqrtNumberOfSites = (int) Math.sqrt(numberOfSites + quad);

		sites = new Site[numberOfSites];
		currentSiteIndex = 0;

		for (int indexSite = 0; indexSite < numberOfSites; indexSite++) {
			sites[indexSite] = new Site(points[indexSite].getxCoordinate(), points[indexSite].getyCoordinate(),
					indexSite);
		}

		Arrays.sort(sites);
	}

	/**
	 * Gets the next site to be processed.
	 *
	 * @return the next site; null if there are no more sites
	 */
	private Site nextSiteToProcess() {
		Site site;

		if (currentSiteIndex < numberOfSites) {
			site = sites[currentSiteIndex];
			currentSiteIndex += 1;
		} else {
			site = null;
		}

		return site;
	}

	/**
	 * Constructs the bisector between two sites, as a line with equation ax +
	 * by = cParam.
	 *
	 * <p>
	 * The midpoint of the line is (s1 + s2)/2.
	 *
	 * <p>
	 * For a line changing more in Y than in X, we seek a formula for the
	 * bisector of the form ax + y = cParam, and for a line changing more in X
	 * than in Y, we seek a formula for the bisector of the form x + by =
	 * cParam.
	 *
	 * <p>
	 * For |dy|>|dx|, the slope of the bisector is -dx/dy, and so the equation
	 * of the line is
	 *
	 * <pre>
	 * y - (yPos1+yPos2)/2 = (-dx/dy)[x - (xPos1+xPos2)/2]
	 * (dx/dy)x + y = [(dx/dy)(xPos1+xPos2) + (yPos1+yPos2)]/2
	 * (dx/dy)x + y = [((xPos2-xPos1)(xPos1+xPos2)/(yPos2-yPos1)) + (yPos1+yPos2)]/2
	 * (dx/dy)x + y = [xPos2^2 - xPos1^2 + yPos2^2 - yPos1^2] / 2dy
	 * </pre>
	 *
	 * <p>
	 * For |dx|>|dy|, the y-x slope of the bisector is -dy/dx, and so the
	 * equation of the line is
	 *
	 * <pre>
	 * x - (xPos1+xPos2)/2 = (-dy/dx)[y - (yPos1+yPos2)/2]
	 * x + (dy/dx)y = [(dy/dx)(yPos1+yPos2) + (xPos1+xPos2)]/2
	 * x + (dy/dx)y = [((yPos2-yPos1)/(xPos2-xPos1))(yPos1+yPos2) + (xPos1+xPos2)]/2
	 * x + (dy/dx)y = [xPos2^2 - xPos1^2 + yPos2^2 - yPos1^2] / 2dx
	 * </pre>
	 *
	 * @param site1
	 *            the first site
	 * @param site2
	 *            the second site
	 * @return the bisector
	 */
	private VoronoiEdge bisectSites(final Site site1, final Site site2) {
		double distanceXAxisBetweenSites;
		double distanceYAxisBetweenSites;
		double absoluteDistanceX;
		double absoluteDistanceY;
		double distanceBetweenSites;
		VoronoiEdge newedge;

		newedge = new VoronoiEdge();

		newedge.getSiteBisecting()[0] = site1;
		newedge.getSiteBisecting()[1] = site2;

		newedge.getEdgeEndPoints()[0] = null;
		newedge.getEdgeEndPoints()[1] = null;

		distanceXAxisBetweenSites = site2.getxCoordinate() - site1.getxCoordinate();
		distanceYAxisBetweenSites = site2.getyCoordinate() - site1.getyCoordinate();

		if (distanceXAxisBetweenSites > 0) {
			absoluteDistanceX = distanceXAxisBetweenSites;
		} else {
			absoluteDistanceX = -distanceXAxisBetweenSites;
		}

		if (distanceYAxisBetweenSites > 0) {
			absoluteDistanceY = distanceYAxisBetweenSites;
		} else {
			absoluteDistanceY = -distanceYAxisBetweenSites;
		}

		final double factor = 0.5;
		distanceBetweenSites = ((site2.getxCoordinate() * site2.getxCoordinate())
				- (site1.getxCoordinate() * site1.getxCoordinate()) + (site2.getyCoordinate() * site2.getyCoordinate())
				- (site1.getyCoordinate() * site1.getyCoordinate())) * factor;

		if (absoluteDistanceX > absoluteDistanceY) {
			newedge.setCoefficientA(1.0f);
			newedge.setCoefficientB(distanceYAxisBetweenSites / distanceXAxisBetweenSites);
			newedge.setCoefficientC(distanceBetweenSites / distanceXAxisBetweenSites);
		} else {
			newedge.setCoefficientB(1.0f);
			newedge.setCoefficientA(distanceXAxisBetweenSites / distanceYAxisBetweenSites);
			newedge.setCoefficientC(distanceBetweenSites / distanceYAxisBetweenSites);
		}

		newedge.setEdgeNumber(numberOfEdges);
		numberOfEdges += 1;

		return newedge;
	}

	/**
	 * Half edge left region.
	 *
	 * @param halfEdge
	 *            Half Edge
	 * @return site on the left of half edge
	 */
	private Site leftRegion(final Halfedge halfEdge) {
		if (halfEdge.getEdge() == null) {
			return bottomsite;
		}

		if (halfEdge.getEdgePosition() == LEFTEDGE) {
			return halfEdge.getEdge().getSiteBisecting()[LEFTEDGE];
		} else {
			return halfEdge.getEdge().getSiteBisecting()[RIGHTEDGE];
		}
	}

	/**
	 * Push Graph Edge to the cell.
	 *
	 * @param leftSite
	 *            left side
	 * @param rightSite
	 *            right side
	 * @param xCoordinate1
	 *            coordinate 1 on the X axis
	 * @param yCoordinate1
	 *            coordinate 1 on the Y axis
	 * @param xCoordinate2
	 *            coordinate 2 on the X axis
	 * @param yCoordinate2
	 *            coordinate 2 on the Y axis
	 */
	private void pushGraphEdge(final Site leftSite, final Site rightSite, final double xCoordinate1,
			final double yCoordinate1, final double xCoordinate2, final double yCoordinate2) {
		GraphEdge newEdge;

		newEdge = new GraphEdge();
		allGraphEdges.add(newEdge);

		newEdge.setxCoordinate1(xCoordinate1);
		newEdge.setyCoordinate1(yCoordinate1);
		newEdge.setxCoordinate2(xCoordinate2);
		newEdge.setyCoordinate2(yCoordinate2);

		newEdge.setSiteIndexLeftOfEdge(leftSite.getSiteIndex());
		newEdge.setSiteIndexRightOfEdge(rightSite.getSiteIndex());
	}

	/**
	 * Clips a line to the bounding box. If the distance between the two points
	 * this line was created from is less than the square root of 2, then ignore
	 * it.
	 *
	 * @param edge
	 *            the edge to clip
	 */
	private void clipLine(final VoronoiEdge edge) {
		double pointMinimumXCoordinate, pointMaximumXCoordinate, pointMinimumYCoordinate, pointMaximumYCoordinate;
		Site site1;
		Site site2;
		double xCoordinate1 = 0;
		double xCoordinate2 = 0;
		double yCoordinate1 = 0;
		double yCoordinate2 = 0;

		xCoordinate1 = edge.getSiteBisecting()[0].getxCoordinate();
		xCoordinate2 = edge.getSiteBisecting()[1].getxCoordinate();
		yCoordinate1 = edge.getSiteBisecting()[0].getyCoordinate();
		yCoordinate2 = edge.getSiteBisecting()[1].getyCoordinate();

		if (Math.sqrt(((xCoordinate2 - xCoordinate1) * (xCoordinate2 - xCoordinate1))
				+ ((yCoordinate2 - yCoordinate1) * (yCoordinate2 - yCoordinate1))) < minimumSiteDistance) {
			return;
		}

		pointMinimumXCoordinate = minimumXCoordinate;
		pointMaximumXCoordinate = maximumXCoordinate;
		pointMinimumYCoordinate = minimumYCoordinate;
		pointMaximumYCoordinate = maximumYCoordinate;

		if ((edge.getCoefficientA() == 1.0) && (edge.getCoefficientB() >= 0.0)) {
			site1 = edge.getEdgeEndPoints()[1];
			site2 = edge.getEdgeEndPoints()[0];
		} else {
			site1 = edge.getEdgeEndPoints()[0];
			site2 = edge.getEdgeEndPoints()[1];
		}

		if (edge.getCoefficientA() == 1.0) {
			yCoordinate1 = pointMinimumYCoordinate;

			if ((site1 != null) && (site1.getyCoordinate() > pointMinimumYCoordinate)) {
				yCoordinate1 = site1.getyCoordinate();
			}

			if (yCoordinate1 > pointMaximumYCoordinate) {
				yCoordinate1 = pointMaximumYCoordinate;
			}

			xCoordinate1 = edge.getCoefficientC() - (edge.getCoefficientB() * yCoordinate1);
			yCoordinate2 = pointMaximumYCoordinate;

			if ((site2 != null) && (site2.getyCoordinate() < pointMaximumYCoordinate)) {
				yCoordinate2 = site2.getyCoordinate();
			}

			if (yCoordinate2 < pointMinimumYCoordinate) {
				yCoordinate2 = pointMinimumYCoordinate;
			}

			xCoordinate2 = (edge.getCoefficientC()) - ((edge.getCoefficientB()) * yCoordinate2);

			if (((xCoordinate1 > pointMaximumXCoordinate) & (xCoordinate2 > pointMaximumXCoordinate))
					| ((xCoordinate1 < pointMinimumXCoordinate) & (xCoordinate2 < pointMinimumXCoordinate))) {
				return;
			}

			if (xCoordinate1 > pointMaximumXCoordinate) {
				xCoordinate1 = pointMaximumXCoordinate;
				yCoordinate1 = (edge.getCoefficientC() - xCoordinate1) / edge.getCoefficientB();
			}

			if (xCoordinate1 < pointMinimumXCoordinate) {
				xCoordinate1 = pointMinimumXCoordinate;
				yCoordinate1 = (edge.getCoefficientC() - xCoordinate1) / edge.getCoefficientB();
			}

			if (xCoordinate2 > pointMaximumXCoordinate) {
				xCoordinate2 = pointMaximumXCoordinate;
				yCoordinate2 = (edge.getCoefficientC() - xCoordinate2) / edge.getCoefficientB();
			}

			if (xCoordinate2 < pointMinimumXCoordinate) {
				xCoordinate2 = pointMinimumXCoordinate;
				yCoordinate2 = (edge.getCoefficientC() - xCoordinate2) / edge.getCoefficientB();
			}
		} else {
			xCoordinate1 = pointMinimumXCoordinate;

			if ((site1 != null) && (site1.getxCoordinate() > pointMinimumXCoordinate)) {
				xCoordinate1 = site1.getxCoordinate();
			}

			if (xCoordinate1 > pointMaximumXCoordinate) {
				xCoordinate1 = pointMaximumXCoordinate;
			}

			yCoordinate1 = edge.getCoefficientC() - (edge.getCoefficientA() * xCoordinate1);
			xCoordinate2 = pointMaximumXCoordinate;

			if ((site2 != null) && (site2.getxCoordinate() < pointMaximumXCoordinate)) {
				xCoordinate2 = site2.getxCoordinate();
			}

			if (xCoordinate2 < pointMinimumXCoordinate) {
				xCoordinate2 = pointMinimumXCoordinate;
			}

			yCoordinate2 = edge.getCoefficientC() - (edge.getCoefficientA() * xCoordinate2);

			if (((yCoordinate1 > pointMaximumYCoordinate) & (yCoordinate2 > pointMaximumYCoordinate))
					| ((yCoordinate1 < pointMinimumYCoordinate) & (yCoordinate2 < pointMinimumYCoordinate))) {
				return;
			}

			if (yCoordinate1 > pointMaximumYCoordinate) {
				yCoordinate1 = pointMaximumYCoordinate;
				xCoordinate1 = (edge.getCoefficientC() - yCoordinate1) / edge.getCoefficientA();
			}

			if (yCoordinate1 < pointMinimumYCoordinate) {
				yCoordinate1 = pointMinimumYCoordinate;
				xCoordinate1 = (edge.getCoefficientC() - yCoordinate1) / edge.getCoefficientA();
			}

			if (yCoordinate2 > pointMaximumYCoordinate) {
				yCoordinate2 = pointMaximumYCoordinate;
				xCoordinate2 = (edge.getCoefficientC() - yCoordinate2) / edge.getCoefficientA();
			}

			if (yCoordinate2 < pointMinimumYCoordinate) {
				yCoordinate2 = pointMinimumYCoordinate;
				xCoordinate2 = (edge.getCoefficientC() - yCoordinate2) / edge.getCoefficientA();
			}
		}

		pushGraphEdge(edge.getSiteBisecting()[0], edge.getSiteBisecting()[1], xCoordinate1, yCoordinate1, xCoordinate2,
				yCoordinate2);
	}

	/**
	 * Detect end point.
	 *
	 * @param edge
	 *            current edge
	 * @param endPointPosition
	 *            end point position
	 * @param site
	 *            site the point belongs to
	 */
	private void endpoint(final VoronoiEdge edge, final int endPointPosition, final Site site) {

		edge.getEdgeEndPoints()[endPointPosition] = site;

		if (edge.getEdgeEndPoints()[RIGHTEDGE - endPointPosition] == null) {
			return;
		}

		clipLine(edge);
	}

	/**
	 * Identify an edge right region.
	 *
	 * @param halfEdge
	 *            half edge under query
	 * @return the right site for the half edge
	 */
	private Site rightRegion(final Halfedge halfEdge) {
		if (halfEdge.getEdge() == null) {
			return bottomsite;
		}

		if (halfEdge.getEdgePosition() == LEFTEDGE) {
			return halfEdge.getEdge().getSiteBisecting()[RIGHTEDGE];
		} else {
			return halfEdge.getEdge().getSiteBisecting()[LEFTEDGE];
		}
	}

	/**
	 * Computes the distance between two sites.
	 *
	 * @param site1
	 *            the first site
	 * @param site2
	 *            the second site
	 * @return the distance between the sites
	 */
	private double dist(final Site site1, final Site site2) {

		double distanceX;
		double distanceY;

		distanceX = site1.getxCoordinate() - site2.getxCoordinate();
		distanceY = site1.getyCoordinate() - site2.getyCoordinate();

		return Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
	}

	/**
	 * Creates a new site where the HalfEdges halfEdge1 and halfEdge2 intersect.
	 *
	 * @param halfEdge1
	 *            half edge 1
	 * @param halfEdge2
	 *            half edge 2
	 * @return intersection site of the two edges
	 */
	private Site intersect(final Halfedge halfEdge1, final Halfedge halfEdge2) {

		VoronoiEdge edge1;
		VoronoiEdge edge2;
		VoronoiEdge edge3;
		Halfedge edge;
		double det;
		double xint;
		double yint;
		boolean rightOfSite;

		edge1 = halfEdge1.getEdge();
		edge2 = halfEdge2.getEdge();

		if ((edge1 == null) || (edge2 == null)) {
			return null;
		}

		if (edge1.getSiteBisecting()[1] == edge2.getSiteBisecting()[1]) {
			return null;
		}

		det = (edge1.getCoefficientA() * edge2.getCoefficientB()) - (edge1.getCoefficientB() * edge2.getCoefficientA());

		final double minDeterminant = -1.0e-10;
		final double maxDeterminant = 1.0e-10;

		if ((minDeterminant < det) && (det < maxDeterminant)) {
			return null;
		}

		xint = ((edge1.getCoefficientC() * edge2.getCoefficientB())
				- (edge2.getCoefficientC() * edge1.getCoefficientB())) / det;
		yint = ((edge2.getCoefficientC() * edge1.getCoefficientA())
				- (edge1.getCoefficientC() * edge2.getCoefficientA())) / det;

		if ((edge1.getSiteBisecting()[1].getyCoordinate() < edge2.getSiteBisecting()[1].getyCoordinate())
				|| ((edge1.getSiteBisecting()[1].getyCoordinate() == edge2.getSiteBisecting()[1].getyCoordinate())
						&& (edge1.getSiteBisecting()[1].getxCoordinate() < edge2.getSiteBisecting()[1]
								.getxCoordinate()))) {
			edge = halfEdge1;
			edge3 = edge1;
		} else {
			edge = halfEdge2;
			edge3 = edge2;
		}

		rightOfSite = xint >= edge3.getSiteBisecting()[1].getxCoordinate();

		if ((rightOfSite && (edge.getEdgePosition() == LEFTEDGE))
				|| (!rightOfSite && (edge.getEdgePosition() == RIGHTEDGE))) {
			return null;
		}

		return new Site(xint, yint, -1);
	}

	/**
	 * Build the Voronoi diagram.
	 */
	private void voronoiDiagramBuild() {
		Site newsite;
		Site bottomSite;
		Site topSite;
		Site temporarySite;
		Site p;
		Site v;
		Vertex newintstar = null;
		int pm;
		Halfedge leftEnd;
		Halfedge rbnd;
		Halfedge llbnd;
		Halfedge rightEnd;
		Halfedge bisector;
		VoronoiEdge edge;

		halfEdgeStorageY = new HalfEdgeStorageY(sqrtNumberOfSites, minimumYCoordinate, boundingBoxHeight);
		halfEdgeStorageX = new HalfEdgeStorageX(sqrtNumberOfSites, minimumXCoordinate, boundingBoxWidth);

		bottomsite = nextSiteToProcess();
		newsite = nextSiteToProcess();

		while (true) {
			if (!halfEdgeStorageY.hashTableIsEmpty()) {
				newintstar = halfEdgeStorageY.minimumVertex();
			}

			if ((newsite != null)
					&& (halfEdgeStorageY.hashTableIsEmpty() || (newsite.getyCoordinate() < newintstar.getyCoordinate())
							|| ((newsite.getyCoordinate() == newintstar.getyCoordinate())
									&& (newsite.getxCoordinate() < newintstar.getxCoordinate())))) {

				leftEnd = halfEdgeStorageX.leftBoundary((newsite));
				rbnd = leftEnd.getHalfEdgeRight();
				bottomSite = rightRegion(leftEnd);
				edge = bisectSites(bottomSite, newsite);
				bisector = new Halfedge(edge, LEFTEDGE);
				halfEdgeStorageX.insert(leftEnd, bisector);
				p = intersect(leftEnd, bisector);

				if (p != null) {
					halfEdgeStorageY.deleteHalfEdge(leftEnd);
					halfEdgeStorageY.insertHalfEdge(leftEnd, p, dist(p, newsite));
				}

				leftEnd = bisector;
				bisector = new Halfedge(edge, RIGHTEDGE);
				halfEdgeStorageX.insert(leftEnd, bisector);
				p = intersect(bisector, rbnd);

				if (p != null) {
					halfEdgeStorageY.insertHalfEdge(bisector, p, dist(p, newsite));
				}

				newsite = nextSiteToProcess();
			} else if (halfEdgeStorageY.hashTableIsEmpty()) {
				break;
			} else {
				leftEnd = halfEdgeStorageY.extractMinimum();
				llbnd = leftEnd.getHalfEdgeLeft();
				rbnd = leftEnd.getHalfEdgeRight();
				rightEnd = rbnd.getHalfEdgeRight();
				bottomSite = leftRegion(leftEnd);
				topSite = rightRegion(rbnd);

				v = leftEnd.getVertex();
				v.setSiteIndex(numberOfVertices);
				this.numberOfVertices += 1;

				endpoint(leftEnd.getEdge(), leftEnd.getEdgePosition(), v);
				endpoint(rbnd.getEdge(), rbnd.getEdgePosition(), v);
				halfEdgeStorageX.deleteEdge(leftEnd);
				halfEdgeStorageY.deleteHalfEdge(rbnd);
				halfEdgeStorageX.deleteEdge(rbnd);
				pm = LEFTEDGE;

				if (bottomSite.getyCoordinate() > topSite.getyCoordinate()) {
					temporarySite = bottomSite;
					bottomSite = topSite;
					topSite = temporarySite;
					pm = RIGHTEDGE;
				}

				edge = bisectSites(bottomSite, topSite);
				bisector = new Halfedge(edge, pm);
				this.halfEdgeStorageX.insert(llbnd, bisector);

				endpoint(edge, RIGHTEDGE - pm, v);
				p = intersect(llbnd, bisector);

				if (p != null) {
					halfEdgeStorageY.deleteHalfEdge(llbnd);
					halfEdgeStorageY.insertHalfEdge(llbnd, p, dist(p, bottomSite));
				}

				p = intersect(bisector, rightEnd);

				if (p != null) {
					halfEdgeStorageY.insertHalfEdge(bisector, p, dist(p, bottomSite));
				}
			}
		}

		for (leftEnd = halfEdgeStorageX.getLeftEnd().getHalfEdgeRight(); leftEnd != halfEdgeStorageX
				.getRightEnd(); leftEnd = leftEnd.getHalfEdgeRight()) {
			edge = leftEnd.getEdge();
			clipLine(edge);
		}
	}

	/**
	 * Given a list of vertices and a list of voronoi edges, computes a set of
	 * polygons, each representing a Voronoi region, then finds the vertex in
	 * that region and constructs a map from vertex to region.
	 *
	 * @param vertices
	 *            the list of vertices
	 * @param edges
	 *            the list of Voronoi edges
	 * @return the constructed map
	 */
	public final Map<Vertex, Path2D> makeVoronoiPolygons(final Vertex[] vertices, final List<GraphEdge> edges) {
		Map<Vertex, Path2D> map;

		map = new HashMap<Vertex, Path2D>(edges.size());

		for (int edgeIndex = 0; edgeIndex < edges.size(); edgeIndex++) {
			findPolygon(edgeIndex, vertices, edges, map, true);
			findPolygon(edgeIndex, vertices, edges, map, false);
		}

		return map;
	}

	/**
	 * Follows an edge around the polygon to its left, connecting subsequent
	 * edges to form a polygon,then adds that polygon to the map based on the
	 * vertex it contains.
	 *
	 * @param edgeIndex
	 *            the edge
	 * @param vertices
	 *            the list of vertices
	 * @param edges
	 *            the list of edges
	 * @param map
	 *            the map from vertex to containing polygon
	 * @param isLeft
	 *            true to look for edges that follow the polygon to the left of
	 *            the starting edge; false to follow the polygon to the right
	 */
	private void findPolygon(final int edgeIndex, final Vertex[] vertices, final List<GraphEdge> edges,
			final Map<Vertex, Path2D> map, final boolean isLeft) {
		GraphEdge root;
		GraphEdge terminal;
		Vertex start;
		Vertex end;
		Vertex prior;
		Vertex current;
		List<Vertex> list;
		double angle;
		double maxAngle;
		double dx1;
		double dx2;
		double dy1;
		double dy2;
		GraphEdge best = null;
		double bestX = 0;
		double bestY = 0;
		boolean finished;
		boolean found;
		int len;
		Line2D line;
		Path2D poly;

		root = edges.get(edgeIndex);
		list = new LinkedList<Vertex>();
		finished = false;

		start = new Vertex(root.getxCoordinate1(), root.getyCoordinate1());
		list.add(start);
		end = new Vertex(root.getxCoordinate2(), root.getyCoordinate2());
		list.add(end);

		prior = start;
		current = end;
		terminal = root;

		outer: for (;;) {
			maxAngle = 0;
			dx1 = current.getxCoordinate() - prior.getxCoordinate();
			dy1 = current.getyCoordinate() - prior.getyCoordinate();
			found = false;

			for (GraphEdge test : edges) {

				if ((test == root) || (test == terminal)) {
					continue;
				}

				if ((Math.abs(test.getxCoordinate1() - current.getxCoordinate()) < epsilon)
						&& (Math.abs(test.getyCoordinate1() - current.getyCoordinate()) < epsilon)) {

					if ((Math.abs(test.getxCoordinate2() - start.getxCoordinate()) < epsilon)
							&& (Math.abs(test.getyCoordinate2() - start.getyCoordinate()) < epsilon)) {
						finished = true;
						break outer;
					}

					if (isLeft == isCcw(prior.getxCoordinate(), prior.getyCoordinate(), current.getxCoordinate(),
							current.getyCoordinate(), test.getxCoordinate2(), test.getyCoordinate2())) {

						dx2 = test.getxCoordinate2() - test.getxCoordinate1();
						dy2 = test.getyCoordinate2() - test.getyCoordinate1();

						angle = Math.abs(Math.acos(((dx1 * dx2) + (dy1 * dy2)) / (Math.sqrt((dx1 * dx1) + (dy1 * dy1)))
								/ (Math.sqrt((dx2 * dx2) + (dy2 * dy2)))));

						if (angle > maxAngle) {
							found = true;
							maxAngle = angle;
							best = test;
							bestX = test.getxCoordinate2();
							bestY = test.getyCoordinate2();
						}
					}
				} else if ((Math.abs(test.getxCoordinate2() - current.getxCoordinate()) < epsilon)
						&& (Math.abs(test.getyCoordinate2() - current.getyCoordinate()) < epsilon)) {

					if ((Math.abs(test.getxCoordinate1() - start.getxCoordinate()) < epsilon)
							&& (Math.abs(test.getyCoordinate1() - start.getyCoordinate()) < epsilon)) {
						finished = true;
						break outer;
					}

					if (isLeft == isCcw(prior.getxCoordinate(), prior.getyCoordinate(), current.getxCoordinate(),
							current.getyCoordinate(), test.getxCoordinate1(), test.getyCoordinate1())) {

						dx2 = test.getxCoordinate1() - test.getxCoordinate2();
						dy2 = test.getyCoordinate1() - test.getyCoordinate2();
						angle = Math.abs(Math.acos(((dx1 * dx2) + (dy1 * dy2)) / (Math.sqrt((dx1 * dx1) + (dy1 * dy1)))
								/ (Math.sqrt((dx2 * dx2) + (dy2 * dy2)))));

						if (angle > maxAngle) {
							found = true;
							maxAngle = angle;
							best = test;
							bestX = test.getxCoordinate1();
							bestY = test.getyCoordinate1();
						}
					}
				}
			}

			if (found) {
				prior = current;
				current = new Vertex(bestX, bestY);
				list.add(current);
				terminal = best;

			} else {
				break;
			}
		}

		if (!finished) {

			prior = end;
			current = start;

			outer: for (;;) {
				maxAngle = 0;
				dx1 = current.getxCoordinate() - prior.getxCoordinate();
				dy1 = current.getyCoordinate() - prior.getyCoordinate();
				found = false;

				for (GraphEdge test : edges) {

					if ((test == root) || (test == terminal)) {
						continue;
					}

					if ((Math.abs(test.getxCoordinate1() - current.getxCoordinate()) < this.epsilon)
							&& (Math.abs(test.getyCoordinate1() - current.getyCoordinate()) < this.epsilon)) {

						if ((Math.abs(test.getxCoordinate2() - end.getxCoordinate()) < this.epsilon)
								&& (Math.abs(test.getyCoordinate2() - end.getyCoordinate()) < this.epsilon)) {
							finished = true;
							break outer;
						}

						if (isLeft == isCcw(current.getxCoordinate(), current.getyCoordinate(), prior.getxCoordinate(),
								prior.getyCoordinate(), test.getxCoordinate2(), test.getyCoordinate2())) {

							dx2 = test.getxCoordinate2() - test.getxCoordinate1();
							dy2 = test.getyCoordinate2() - test.getyCoordinate1();
							angle = Math.abs(Math.acos(((dx1 * dx2) + (dy1 * dy2))
									/ (Math.sqrt((dx1 * dx1) + (dy1 * dy1))) / (Math.sqrt((dx2 * dx2) + (dy2 * dy2)))));

							if (angle > maxAngle) {
								found = true;
								maxAngle = angle;
								best = test;
								bestX = test.getxCoordinate2();
								bestY = test.getyCoordinate2();
							}
						}
					} else if ((Math.abs(test.getxCoordinate2() - current.getxCoordinate()) < this.epsilon)
							&& (Math.abs(test.getyCoordinate2() - current.getyCoordinate()) < this.epsilon)) {

						if ((Math.abs(test.getxCoordinate1() - end.getxCoordinate()) < this.epsilon)
								&& (Math.abs(test.getyCoordinate1() - end.getyCoordinate()) < this.epsilon)) {
							finished = true;
							break outer;
						}

						if (isLeft == isCcw(current.getxCoordinate(), current.getyCoordinate(), prior.getxCoordinate(),
								prior.getyCoordinate(), test.getxCoordinate1(), test.getyCoordinate1())) {

							dx2 = test.getxCoordinate1() - test.getxCoordinate2();
							dy2 = test.getyCoordinate1() - test.getyCoordinate2();
							angle = Math.abs(Math.acos(((dx1 * dx2) + (dy1 * dy2))
									/ (Math.sqrt((dx1 * dx1) + (dy1 * dy1))) / (Math.sqrt((dx2 * dx2) + (dy2 * dy2)))));

							if (angle > maxAngle) {
								found = true;
								maxAngle = angle;
								best = test;
								bestX = test.getxCoordinate1();
								bestY = test.getyCoordinate1();
							}
						}
					}
				}

				if (found) {
					prior = current;
					current = new Vertex(bestX, bestY);
					list.add(0, current);
					terminal = best;
				} else {
					finished = true;

					break;
				}
			}
		}

		if (finished) {
			Vertex vert1 = null;
			Vertex vert2 = null;

			len = list.size();

			if (len > 2) {

				poly = new Path2D.Double();

				for (int i = 1; i < len; i++) {
					vert1 = list.get(i - 1);
					vert2 = list.get(i);
					line = new Line2D.Double(vert1.getxCoordinate(), vert1.getyCoordinate(), vert2.getxCoordinate(),
							vert2.getyCoordinate());
					poly.append(line, true);
				}

				for (Vertex v : vertices) {

					if (poly.contains(v.getxCoordinate(), v.getyCoordinate())) {
						map.put(v, poly);
					}
				}
			}
		}
	}

	/**
	 * Computes twice the area of the oriented triangle (xPos1, yPos1), (xPos2,
	 * yPos2), (x3, y3) (the area is positive if the triangle is oriented
	 * counterclockwise).
	 *
	 * @param xPos1
	 *            the first point X coordinate
	 * @param yPos1
	 *            the first point Y coordinate
	 * @param xPos2
	 *            the second point X coordinate
	 * @param yPos2
	 *            the second point Y coordinate
	 * @param xPos3
	 *            the third point X coordinate
	 * @param yPos3
	 *            the third point Y coordinate
	 * @return twice the oriented area
	 */
	public static final double triArea(final double xPos1, final double yPos1, final double xPos2, final double yPos2,
			final double xPos3, final double yPos3) {
		return ((xPos2 - xPos1) * (yPos3 - yPos1)) - ((yPos2 - yPos1) * (xPos3 - xPos1));
	}

	/**
	 * Tests whether the points of a triangle are in counterclockwise order.
	 *
	 * @param xPos1
	 *            the first point X coordinate
	 * @param yPos1
	 *            the first point Y coordinate
	 * @param xPos2
	 *            the second point X coordinate
	 * @param yPos2
	 *            the second point Y coordinate
	 * @param xPos3
	 *            the third point X coordinate
	 * @param yPos3
	 *            the third point Y coordinate
	 * @return true if triangle (xPos1, yPos1), (xPos2, yPos2), (x3, y3) is in
	 *         counterclockwise order
	 */
	public static final boolean isCcw(final double xPos1, final double yPos1, final double xPos2, final double yPos2,
			final double xPos3, final double yPos3) {
		return triArea(xPos1, yPos1, xPos2, yPos2, xPos3, yPos3) > 0;
	}

	/**
	 * Tests whether two points are at the same location; two points are
	 * considered to be at the same location when their X and Y coordinates both
	 * differ by less than epsilon.
	 *
	 * @param xPos1
	 *            the first point X coordinate
	 * @param yPos1
	 *            the first point Y coordinate
	 * @param xPos2
	 *            the second point X coordinate
	 * @param yPos2
	 *            the second point Y coordinate
	 * @return true if the points are at the same location; false if not
	 */
	private boolean isSame(final double xPos1, final double yPos1, final double xPos2, final double yPos2) {
		return (Math.abs(xPos2 - xPos1) < this.epsilon) && (Math.abs(yPos2 - yPos1) < this.epsilon);
	}
}