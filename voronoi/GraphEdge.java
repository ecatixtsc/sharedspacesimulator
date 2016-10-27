package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

/**
 * An edge in the Voronoi graph. A Voronoi edge falls on the perpendicular
 * bisector of two neighbouring sites.
 * 
 * Reference: http://www.math.colostate.edu/
 */
public class GraphEdge {

	/** The start X coordinate. */
	private double xCoordinate1;

	/** The start Y coordinate. */
	private double yCoordinate1;

	/** The end X coordinate. */
	private double xCoordinate2;

	/** The endY coordinate. */
	private double yCoordinate2;

	/** The index of the site to the left of the edge. */
	private int siteIndexLeftOfEdge;

	/** The index of the site to the right of the edge. */
	private int siteIndexRightOfEdge;

	/**
	 * Get X coordinate for vertex 1.
	 * 
	 * @return X coordinate for vertex 1
	 */
	public final double getxCoordinate1() {
		return xCoordinate1;
	}

	/**
	 * Set X coordinate for vertex 1.
	 * 
	 * @param newXCoordinate1
	 *            X coordinate for vertex 1
	 */
	public final void setxCoordinate1(final double newXCoordinate1) {
		xCoordinate1 = newXCoordinate1;
	}

	/**
	 * Get Y coordinate for vertex 1.
	 * 
	 * @return Y coordinate for vertex 1.
	 */
	public final double getyCoordinate1() {
		return yCoordinate1;
	}

	/**
	 * Set Y coordinate for vertex 1.
	 * 
	 * @param newYCoordinate1
	 *            Y coordinate for vertex 1.
	 */
	public final void setyCoordinate1(final double newYCoordinate1) {
		yCoordinate1 = newYCoordinate1;
	}

	/**
	 * Get X coordinate for vertex 2.
	 * 
	 * @return X coordinate for vertex 2
	 */
	public final double getxCoordinate2() {
		return xCoordinate2;
	}

	/**
	 * Set X coordinate for vertex 2.
	 * 
	 * @param newXCoordinate2
	 *            X coordinate for vertex 2
	 */
	public final void setxCoordinate2(final double newXCoordinate2) {
		xCoordinate2 = newXCoordinate2;
	}

	/**
	 * Get Y coordinate for vertex 2.
	 * 
	 * @return Y coordinate for vertex 2
	 */
	public final double getyCoordinate2() {
		return yCoordinate2;
	}

	/**
	 * Set Y coordinate for vertex 2.
	 * 
	 * @param newYCoordinate2
	 *            Y coordinate for vertex 2
	 */
	public final void setyCoordinate2(final double newYCoordinate2) {
		yCoordinate2 = newYCoordinate2;
	}

	/**
	 * Get the index of the site to the left of the edge.
	 * 
	 * @return the index of the site to the left of the edge
	 */
	public final int getSiteIndexLeftOfEdge() {
		return siteIndexLeftOfEdge;
	}

	/**
	 * Set the index of the site to the left of the edge.
	 * 
	 * @param newSiteIndexLeftOfEdge
	 *            the index of the site to the left of the edge
	 */
	public final void setSiteIndexLeftOfEdge(final int newSiteIndexLeftOfEdge) {
		siteIndexLeftOfEdge = newSiteIndexLeftOfEdge;
	}

	/**
	 * Get the index of the site to the right of the edge.
	 * 
	 * @return the index of the site to the right of the edge
	 */
	public final int getSiteIndexRightOfEdge() {
		return siteIndexRightOfEdge;
	}

	/**
	 * Set the index of the site to the right of the edge.
	 * 
	 * @param newSiteIndexRightOfEdge
	 *            the index of the site to the right of the edge
	 */
	public final void setSiteIndexRightOfEdge(final int newSiteIndexRightOfEdge) {
		siteIndexRightOfEdge = newSiteIndexRightOfEdge;
	}
}
