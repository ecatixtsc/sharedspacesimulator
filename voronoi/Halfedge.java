package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

/**
 * Half edge voronoi cell.
 * 
 * Reference: http://www.math.colostate.edu/
 *
 */
public class Halfedge {
	/** Left half edge. */
	private Halfedge halfEdgeLeft;

	/** Right half edge. */
	private Halfedge halfEdgeRight;

	/** Current edge. */
	private VoronoiEdge edge;

	/** Deleted half edge. */
	private boolean deleted;

	/** Edge position. */
	private int edgePosition;

	/** Voronoi site. */
	private Site vertex;

	/** Beach-line y start position. */
	private double yStart;

	/** The next half-edge in the hash table. */
	private Halfedge nextHalfEdge;

	/**
	 * Constructs a new Halfedge.
	 */
	public Halfedge() {
		this.setNextHalfEdge(null);
	}

	/**
	 * Constructor.
	 *
	 * @param newEdge
	 *            the edge that bisects
	 * @param newEdgePosition
	 *            edge position
	 */
	public Halfedge(final VoronoiEdge newEdge, final int newEdgePosition) {
		this();

		this.edge = newEdge;
		this.edgePosition = newEdgePosition;
		this.setVertex(null);
	}

	/**
	 * Get the left half edge .
	 * 
	 * @return the left half edge
	 */
	public final Halfedge getHalfEdgeLeft() {
		return halfEdgeLeft;
	}

	/**
	 * Get the right half edge.
	 * 
	 * @return the right half edge
	 */
	public final Halfedge getHalfEdgeRight() {
		return halfEdgeRight;
	}

	/**
	 * Get the current edge.
	 * 
	 * @return current edge
	 */
	public final VoronoiEdge getEdge() {
		return edge;
	}

	/**
	 * Check if the edge is already deleted.
	 * 
	 * @return true of the edge is deleted
	 */
	public final boolean isDeleted() {
		return deleted;
	}

	/**
	 * Get edge position.
	 * 
	 * @return edge position
	 */
	public final int getEdgePosition() {
		return edgePosition;
	}

	/**
	 * Get site vertex.
	 * 
	 * @return site vertex
	 */
	public final Site getVertex() {
		return vertex;
	}

	/**
	 * Get Y star.
	 * 
	 * @return y star
	 */
	public final double getYstar() {
		return yStart;
	}

	/**
	 * Get next half edge.
	 * 
	 * @return next half edge
	 */
	public final Halfedge getNextHalfEdge() {
		return nextHalfEdge;
	}

	/**
	 * Set left half edge.
	 * 
	 * @param newHalfEdgeLeft
	 *            left half edge
	 */
	public final void setHalfEdgeLeft(final Halfedge newHalfEdgeLeft) {
		this.halfEdgeLeft = newHalfEdgeLeft;
	}

	/**
	 * Set right half edge.
	 * 
	 * @param newHalfEdgeRight
	 *            right half edge
	 */
	public final void setHalfEdgeRight(final Halfedge newHalfEdgeRight) {
		this.halfEdgeRight = newHalfEdgeRight;
	}

	/**
	 * Mark half edge as deleted.
	 * 
	 * @param deletedState
	 *            marked true of the half edge is deleted
	 */
	public final void setDeleted(final boolean deletedState) {
		this.deleted = deletedState;
	}

	/**
	 * Set vertex.
	 * 
	 * @param newVertex
	 *            set new vertex
	 */
	public final void setVertex(final Site newVertex) {
		this.vertex = newVertex;
	}

	/**
	 * Set Y Star.
	 * 
	 * @param newYstar
	 *            new y star
	 */
	public final void setYstar(final double newYstar) {
		this.yStart = newYstar;
	}

	/**
	 * Set next half edge.
	 * 
	 * @param newNextHalfEdge
	 *            next hald edge
	 */
	public final void setNextHalfEdge(final Halfedge newNextHalfEdge) {
		this.nextHalfEdge = newNextHalfEdge;
	}
}
