package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

/**
 * A single edge in a QuadEdge data structure.
 * 
 * According to the literature a quad edge is a collection of four oriented
 * edges. Each oriented edge points from one endpoint (tail, origin) to the
 * other endpoint (head, destination) and separates two faces, left and right.
 * In other words a quad-edge is a data structure that has information on how it
 * is connected to other edges, who are the edges on the right, left, tail and
 * head.
 * 
 * Reference: http://www.math.colostate.edu/
 */
public class Edge {

	/** The next edge of 4 (counterclockwise) in the edge QuadEdge. */
	private Edge nextCounterclockwiseEdge;

	/** The next edge of 4 (clockwise) in the edge QuadEdge. */
	private Edge nextClockwiseEdge;

	/** The next edge with the same origin vertex. */
	private Edge nextEdgeSameOrigin;

	/** Data associated with the edge (origin vertex point). */
	private Vertex originVertex;

	/**
	 * Gets the edge from the right face to the left face relative to this edge
	 * (the edge that is rotated 90 degrees from this edge).
	 *
	 * @return the edge
	 */
	public final Edge rotateEdgeCounterclockwise() {
		return getNextCounterclockwiseEdge();
	}

	/**
	 * Gets the edge from the left face to the right face relative to this edge
	 * (think of this as the edge that is rotated -90 degrees from this edge).
	 *
	 * @return the edge
	 */
	public final Edge invertRotateEdge() {
		return getNextClockwiseEdge();
	}

	/**
	 * Gets the edge that connects the same two vertices as this edge, but with
	 * opposite orientation.
	 *
	 * @return the edge
	 */
	public final Edge symmetricEdge() {
		return getNextCounterclockwiseEdge().getNextCounterclockwiseEdge();
	}

	/**
	 * Gets the next edge we find that leaves this edge's origin vertex as we go
	 * around that vertex counterclockwise from this edge.
	 *
	 * @return the edge
	 */
	public final Edge nextEdgeFromOrigin() {
		return getNextEdge();
	}

	/**
	 * Gets the next edge we find that leaves this edge's origin vertex as we go
	 * around that vertex clockwise from this edge.
	 *
	 * @return the edge
	 */
	public final Edge previousEdgeFromOrigin() {
		return getNextCounterclockwiseEdge().getNextEdge().getNextCounterclockwiseEdge();
	}

	/**
	 * Gets the next edge we find that enters this edge's destination vertex as
	 * we go around that vertex counterclockwise from this edge.
	 *
	 * @return the edge
	 */
	public final Edge nextEdgeFromDestination() {
		return getNextCounterclockwiseEdge().getNextCounterclockwiseEdge().getNextEdge().getNextCounterclockwiseEdge()
				.getNextCounterclockwiseEdge();
	}

	/**
	 * Gets the next edge we find that enters this edge's destination vertex as
	 * we go around that vertex clockwise from this edge.
	 *
	 * @return the edge
	 */
	public final Edge previousEdgeFromDestination() {
		return getNextClockwiseEdge().getNextEdge().getNextClockwiseEdge();
	}

	/**
	 * Gets the edge that follows this edge in a counterclockwise traversal of
	 * the face to the left of this edge.
	 *
	 * @return the edge
	 */
	public final Edge leftNextEdge() {
		return getNextClockwiseEdge().getNextEdge().getNextCounterclockwiseEdge();
	}

	/**
	 * Gets the edge that precedes this edge in a counterclockwise traversal of
	 * the face to the left of this edge.
	 *
	 * @return the edge
	 */
	public final Edge leftPreviousEdge() {
		return getNextEdge().getNextCounterclockwiseEdge().getNextCounterclockwiseEdge();
	}

	/**
	 * Gets the edge that follows this edge in a counterclockwise traversal of
	 * the face to the right of this edge (a traversal goes opposite the
	 * direction of this edge).
	 *
	 * @return the edge
	 */
	public final Edge nextRotatedEdge() {
		return getNextCounterclockwiseEdge().getNextEdge().getNextClockwiseEdge();
	}

	/**
	 * Gets the edge that precedes this edge in a counterclockwise traversal of
	 * the face to the right of this edge (note that such a traversal goes
	 * opposite the direction of this edge).
	 *
	 * @return the edge
	 */
	public final Edge previousRotatedEdge() {
		return getNextCounterclockwiseEdge().getNextCounterclockwiseEdge().getNextEdge();
	}

	/**
	 * Gets the data associated with this edge's destination vertex.
	 *
	 * @return the destination vertex data
	 */
	public final Vertex dataDestinationVertex() {
		return getNextCounterclockwiseEdge().getNextCounterclockwiseEdge().originVertex;
	}

	/**
	 * Gets the data associated with the face to the left of this edge.
	 *
	 * @return the left face data
	 */
	public final Vertex leftVertexData() {
		return getNextCounterclockwiseEdge().originVertex;
	}

	/**
	 * Gets the data associated with the face to the right of this edge.
	 *
	 * @return the right face data
	 */
	public final Vertex rightVertexData() {
		return getNextClockwiseEdge().originVertex;
	}

	/**
	 * Sets the end points of this edge. The points of the dual space (faces)
	 * can be set using rotateEdge().setEndPoints.
	 *
	 * @param newOriginVertex
	 *            the origin vertex
	 * @param dest
	 *            the destination vertex
	 */
	public final void setEndPoints(final Vertex newOriginVertex, final Vertex dest) {
		originVertex = newOriginVertex;
		symmetricEdge().originVertex = dest;
	}

	/**
	 * Generates the string representation of the edge.
	 *
	 * @return the string representation
	 */
	@Override
	public final String toString() {
		return "{" + getOriginVertex() + ":" + dataDestinationVertex() + "}";
	}

	/**
	 * Splice, is used to connect quad-edge structures together, to split them
	 * apart, and to rearrange them in other ways. Implementation of the Splice
	 * topological primitive from Guibas & Stolfi (1985). If the two edges have
	 * the same origin vertex (they are parts of different loops that leave that
	 * vertex), the vertex is split. If the two edges have different origin
	 * vertices, those vertices are merged.
	 *
	 * @param edge1
	 *            the first edge
	 * @param edge2
	 *            the second edge
	 */
	public static void splice(final Edge edge1, final Edge edge2) {
		Edge nextCounterclockwiseOfEdge1Next;
		Edge nextCounterclockwiseOfEdge2Next;
		Edge nextEdge2;
		Edge nextEdge1;
		Edge nextEdgeBeta;
		Edge nextEdgeAlpha;

		nextCounterclockwiseOfEdge1Next = edge1.getNextEdge().getNextCounterclockwiseEdge();
		nextCounterclockwiseOfEdge2Next = edge2.getNextEdge().getNextCounterclockwiseEdge();

		nextEdge2 = edge2.getNextEdge();
		nextEdge1 = edge1.getNextEdge();
		nextEdgeBeta = nextCounterclockwiseOfEdge2Next.getNextEdge();
		nextEdgeAlpha = nextCounterclockwiseOfEdge1Next.getNextEdge();

		edge1.setNextEdge(nextEdge2);
		edge2.setNextEdge(nextEdge1);
		nextCounterclockwiseOfEdge1Next.setNextEdge(nextEdgeBeta);
		nextCounterclockwiseOfEdge2Next.setNextEdge(nextEdgeAlpha);
	}

	/**
	 * Deletes the edge, adjusting the references of surrounding edges as
	 * needed.
	 */
	public final void delete() {
		splice(this, previousEdgeFromOrigin());
		splice(symmetricEdge(), symmetricEdge().previousEdgeFromOrigin());
	}

	/**
	 * Add a new edge connecting the destination of this edge to the origin of
	 * edge2 in such a way that all three have the same left face after the
	 * connection is complete. The data pointers of the new edge are set to the
	 * appropriate vertices from the existing edges.
	 *
	 * @param edge2
	 *            the second edge
	 * @return the new edge
	 */
	public final Edge connect(final Edge edge2) {
		Edge edge;

		edge = new QuadEdge().getEdge();
		Edge.splice(edge, leftNextEdge());
		Edge.splice(edge.symmetricEdge(), edge2);
		edge.setEndPoints(dataDestinationVertex(), edge2.getOriginVertex());

		return edge;
	}

	/**
	 * Turns the edge counterclockwise inside its enclosing quadrilateral,
	 * updating data pointers appropriately.
	 */
	public final void swap() {
		Edge aEdge;
		Edge bEdge;

		aEdge = previousEdgeFromOrigin();
		bEdge = symmetricEdge().previousEdgeFromOrigin();

		Edge.splice(this, aEdge);
		Edge.splice(symmetricEdge(), bEdge);
		Edge.splice(this, aEdge.leftNextEdge());
		Edge.splice(symmetricEdge(), bEdge.leftNextEdge());

		setEndPoints(aEdge.dataDestinationVertex(), bEdge.dataDestinationVertex());
	}

	/**
	 * Get the next edge of 4 (counterclockwise) in the edge QuadEdge.
	 * 
	 * @return the next edge of 4 (counterclockwise) in the edge QuadEdge
	 */
	public final Edge getNextCounterclockwiseEdge() {
		return nextCounterclockwiseEdge;
	}

	/**
	 * Set the next edge of 4 (counterclockwise) in the edge QuadEdge.
	 * 
	 * @return the next edge of 4 (counterclockwise) in the edge QuadEdge
	 */
	public final Edge getNextClockwiseEdge() {
		return nextClockwiseEdge;
	}

	/**
	 * Get the next edge counterclockwise from the origin vertex.
	 * 
	 * @return the next edge counterclockwise from the origin vertex
	 */
	public final Edge getNextEdge() {
		return nextEdgeSameOrigin;
	}

	/**
	 * Set the next edge counterclockwise from the origin vertex.
	 * 
	 * @return the next edge counterclockwise from the origin vertex
	 */
	public final Vertex getOriginVertex() {
		return originVertex;
	}

	/**
	 * Set the next edge of 4 (counterclockwise) in the edge QuadEdge.
	 * 
	 * @param newNextCounterclockwiseEdge
	 *            the next edge of 4 (counterclockwise) in the edge QuadEdge
	 */
	public final void setNextCounterclockwiseEdge(final Edge newNextCounterclockwiseEdge) {
		this.nextCounterclockwiseEdge = newNextCounterclockwiseEdge;
	}

	/**
	 * Set the next edge of 4 (clockwise) in the edge QuadEdge.
	 * 
	 * @param newNextClockwiseEdge
	 *            The next edge of 4 (clockwise) in the edge QuadEdge
	 */
	public final void setNextClockwiseEdge(final Edge newNextClockwiseEdge) {
		this.nextClockwiseEdge = newNextClockwiseEdge;
	}

	/**
	 * Set the next edge counterclockwise from the origin vertex.
	 * 
	 * @param newNextEdge
	 *            The next edge counterclockwise from the origin vertex
	 */
	public final void setNextEdge(final Edge newNextEdge) {
		this.nextEdgeSameOrigin = newNextEdge;
	}
}
