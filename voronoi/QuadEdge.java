package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

/**
 * A QuadEdge, consisting of four edges linked appropriately. The edges form a
 * cyclical, doubly-linked list, and each edge has a link to the next edge in a
 * counterclockwise edge list around its origin vertex.
 * 
 * 
 * 
 * Reference: http://www.math.colostate.edu/
 */
public class QuadEdge {

	/** The "canonical" edge in this QuadEdge structure. */
	private Edge edge;

	/**
	 * A particularly elegant data structure for polyhedra is the quad-edge data
	 * structure, invented by Guibas and Stolfi. It can't represent all
	 * collections of polygons; it is limited to manifolds (surfaces where the
	 * neighborhood of each point is topologically equivalent to a disk; edges
	 * are always shared by two faces). In the quad-edge data structure, there
	 * are classes for vertices, edges, and faces, but edges play the leading
	 * role. The edges store complete topological information; all of the
	 * topological information stored by the faces and vertices is redundant
	 * with information in the edges. Figuratively speaking, the edges form the
	 * skeleton, and the vertices and faces are optional decorations, hanging
	 * off of the edges. Vertices hold most of the geometric (shape)
	 * information.
	 */
	public QuadEdge() {
		Edge edge2;
		Edge edge3;
		Edge edge4;

		edge = new Edge();
		edge2 = new Edge();
		edge3 = new Edge();
		edge4 = new Edge();

		edge.setNextCounterclockwiseEdge(edge2);
		edge2.setNextCounterclockwiseEdge(edge3);
		edge3.setNextCounterclockwiseEdge(edge4);
		edge4.setNextCounterclockwiseEdge(edge);

		edge.setNextClockwiseEdge(edge4);
		edge4.setNextClockwiseEdge(edge3);
		edge3.setNextClockwiseEdge(edge2);
		edge2.setNextClockwiseEdge(edge);

		edge.setNextEdge(edge);
		edge2.setNextEdge(edge4);
		edge3.setNextEdge(edge3);
		edge4.setNextEdge(edge2);
	}

	/**
	 * Get edge.
	 * 
	 * @return edge
	 */
	public final Edge getEdge() {
		return edge;
	}

	/**
	 * Set new edge.
	 * 
	 * @param newEdge
	 *            new edge
	 */
	public final void setEdge(final Edge newEdge) {
		this.edge = newEdge;
	}
}
