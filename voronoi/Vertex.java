package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

/**
 * A vertex in a Delaunay triangulation.
 * 
 * Reference: http://www.math.colostate.edu/
 */
public class Vertex {

	/** Distance within which we consider a vertex to fall on an edge. */
	public static final double EPSILON = 1e-6;

	/** The vertex X coordinate. */
	private final double xCoordinate;

	/** The vertex Y coordinate. */
	private final double yCoordinate;

	/**
	 * Flag indicating the vertex is bogus. It is bogus when a point falls on
	 * the edge of the surrounding triangle.
	 */
	private final boolean isBogus;

	/**
	 * Constructs a new Vertex.
	 *
	 * @param newXCoordinate
	 *            the X coordinate
	 * @param newYCoordinate
	 *            the Y coordinate
	 */
	public Vertex(final double newXCoordinate, final double newYCoordinate) {
		this(newXCoordinate, newYCoordinate, false);
	}

	/**
	 * Constructs a new Vertex.
	 *
	 * @param newXCoordinate
	 *            the X coordinate
	 * @param newYCoordinate
	 *            the Y coordinate
	 * @param bogus
	 *            true if the vertex is bogus
	 */
	public Vertex(final double newXCoordinate, final double newYCoordinate, final boolean bogus) {
		xCoordinate = newXCoordinate;
		yCoordinate = newYCoordinate;
		isBogus = bogus;
	}

	/**
	 * Returns the square of the distance of this vertex from the origin.
	 *
	 * @return the distance from the origin squared
	 */
	public final double lengthSquared() {
		return (xCoordinate * xCoordinate) + (yCoordinate * yCoordinate);
	}

	/**
	 * Tests whether a point lies to the right of an edge.
	 *
	 * @param edge
	 *            the edge
	 * @return true if the point lies to the right of the edge; false otherwise
	 */
	public final boolean isRightOf(final Edge edge) {
		return isCounterClockwise(this, edge.dataDestinationVertex(), edge.getOriginVertex());
	}

	/**
	 * Tests whether a point lies to the left of an edge.
	 *
	 * @param edge
	 *            the edge
	 * @return true if the point lies to the left of the edge; false otherwise
	 */
	public final boolean isLeftOf(final Edge edge) {
		return isCounterClockwise(this, edge.getOriginVertex(), edge.dataDestinationVertex());
	}

	/**
	 * Tests whether this point lies on an edge (within some small epsilon).
	 *
	 * @param edge
	 *            the edge
	 * @return true if the point lies on the edge; false otherwise
	 */
	public final boolean isOnEdge(final Edge edge) {
		Vertex origin;
		Vertex destination;
		double tx;
		double ty;
		double length;
		double lineA;
		double lineB;
		double lineC;
		boolean onEdge;

		origin = edge.getOriginVertex();
		destination = edge.dataDestinationVertex();

		tx = destination.xCoordinate - origin.xCoordinate;
		ty = destination.yCoordinate - origin.yCoordinate;
		length = Math.sqrt((tx * tx) + (ty * ty));

		lineA = ty / length;
		lineB = -tx / length;
		lineC = -((lineA * origin.xCoordinate) + (lineB * origin.yCoordinate));

		onEdge = Math.abs((lineA * xCoordinate) + (lineB * yCoordinate) + lineC) < EPSILON;

		return onEdge;
	}

	/**
	 * Tests whether this point lies within the circle circumscribing a
	 * triangle.
	 *
	 * @param pt1
	 *            the first point defining the triangle
	 * @param pt2
	 *            the second point defining the triangle
	 * @param pt3
	 *            the third point defining the triangle
	 * @return true if this point is inside the circumscribing circle; false if
	 *         not
	 */
	public final boolean isInCircle(final Vertex pt1, final Vertex pt2, final Vertex pt3) {
		return ((pt1.lengthSquared() * triArea(pt2, pt3, this)) - (pt2.lengthSquared() * triArea(pt1, pt3, this))
				+ (pt3.lengthSquared() * triArea(pt1, pt2, this)) - (lengthSquared() * triArea(pt1, pt2, pt3))) > 0;
	}

	/**
	 * Generates the string representation of the vertex.
	 *
	 * @return the string representation
	 */
	@Override
	public final String toString() {
		return "(" + (float) xCoordinate + "," + (float) yCoordinate + ")";
	}

	/**
	 * Computes twice the area of the oriented triangle pt1, pt2, pt3 (the area
	 * is positive if the triangle is oriented counterclockwise).
	 *
	 * @param point1
	 *            the first point
	 * @param point2
	 *            the second point
	 * @param point3
	 *            the third point
	 * @return twice the oriented area
	 */
	public static double triArea(final Vertex point1, final Vertex point2, final Vertex point3) {
		return ((point2.xCoordinate - point1.xCoordinate) * (point3.yCoordinate - point1.yCoordinate))
				- ((point2.yCoordinate - point1.yCoordinate) * (point3.xCoordinate - point1.xCoordinate));
	}

	/**
	 * Tests whether the points of a triangle are in counterclockwise order.
	 *
	 * @param point1
	 *            the first point
	 * @param point2
	 *            the second point
	 * @param point3
	 *            the third point
	 * @return true if triangle (pt1, pt2, pt3) is in counterclockwise order
	 */
	public static boolean isCounterClockwise(final Vertex point1, final Vertex point2, final Vertex point3) {
		return triArea(point1, point2, point3) > 0;
	}

	/**
	 * Creates a vertex that lies at the center of the circle that circumscribes
	 * a triangle.
	 *
	 * @param triangleVertex1
	 *            the first vertex of the triangle
	 * @param triangleVertex2
	 *            the second vertex of the triangle
	 * @param triangleVertex3
	 *            the third vertex of the triangle
	 * @return the vertex at the circumcenter
	 */
	public static Vertex circumcenter(final Vertex triangleVertex1, final Vertex triangleVertex2,
			final Vertex triangleVertex3) {
		double lengthSquared1;
		double lengthSquared2;
		double lengthSquared3;
		double denom;
		double xNumer;
		double yNumer;
		Vertex center;

		lengthSquared1 = triangleVertex1.lengthSquared();
		lengthSquared2 = triangleVertex2.lengthSquared();
		lengthSquared3 = triangleVertex3.lengthSquared();

		denom = 2 * ((triangleVertex1.xCoordinate * (triangleVertex2.yCoordinate - triangleVertex3.yCoordinate))
				+ (triangleVertex2.xCoordinate * (triangleVertex3.yCoordinate - triangleVertex1.yCoordinate))
				+ (triangleVertex3.xCoordinate * (triangleVertex1.yCoordinate - triangleVertex2.yCoordinate)));

		xNumer = (lengthSquared1 * (triangleVertex2.yCoordinate - triangleVertex3.yCoordinate))
				+ (lengthSquared2 * (triangleVertex3.yCoordinate - triangleVertex1.yCoordinate))
				+ (lengthSquared3 * (triangleVertex1.yCoordinate - triangleVertex2.yCoordinate));
		yNumer = (lengthSquared1 * (triangleVertex3.xCoordinate - triangleVertex2.xCoordinate))
				+ (lengthSquared2 * (triangleVertex1.xCoordinate - triangleVertex3.xCoordinate))
				+ (lengthSquared3 * (triangleVertex2.xCoordinate - triangleVertex1.xCoordinate));

		center = new Vertex(xNumer / denom, yNumer / denom);

		return center;
	}

	/**
	 * Get the X coordinate for the current vertex.
	 * 
	 * @return X coordinate for the current vertex
	 */
	public final double getxCoordinate() {
		return xCoordinate;
	}

	/**
	 * Get the Y coordinate for the current vertex.
	 * 
	 * @return Y coordinate for the current vertex
	 */
	public final double getyCoordinate() {
		return yCoordinate;
	}

	/**
	 * Check if the vertex is bogus.
	 * 
	 * @return true if the vertex is bogus
	 */
	public final boolean isBogus() {
		return isBogus;
	}

	/**
	 * The distance between the current vertex and a specified vertex.
	 * 
	 * @param anotherVertex
	 *            another vertex
	 * @return the distance between the current vertex and a specified vertex
	 */
	public final double distance(final Vertex anotherVertex) {
		double distance = 0;

		if (anotherVertex != null) {
			double dx = this.getxCoordinate() - anotherVertex.getxCoordinate();
			double dy = this.getyCoordinate() - anotherVertex.getyCoordinate();
			distance = Math.sqrt(dx * dx + dy * dy);
		}

		return distance;
	}
}
