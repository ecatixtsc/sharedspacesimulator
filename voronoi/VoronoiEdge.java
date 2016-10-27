package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

/**
 * An intermediate data structure used in building the edges between regions in
 * a Voronoi diagram.
 * 
 * Reference: http://www.math.colostate.edu/
 */
class VoronoiEdge {

	/** 'a' parameter in ax + by = c equation of the line. */
	private double coefficientA = 0;

	/** 'b' parameter in ax + by = c equation of the line. */
	private double coefficientB = 0;

	/** 'c' parameter in ax + by = c equation of the line. */
	private double coefficientC = 0;

	/** The end points of this edge. */
	private final Site[] edgeEndPoints;

	/** The sites this edge is the bisector of. */
	private final Site[] siteBisecting;

	/** The edge number. */
	private int edgeNumber;

	/**
	 * Constructs a new VoronoiEdge.
	 */
	VoronoiEdge() {
		this.edgeEndPoints = new Site[2];
		this.siteBisecting = new Site[2];
	}

	/**
	 * Get A coefficient.
	 * 
	 * @return A coefficient
	 */
	public double getCoefficientA() {
		return coefficientA;
	}

	/**
	 * Set the A coefficient.
	 * 
	 * @param newCoefficientA
	 *            new A coefficient
	 */
	public void setCoefficientA(final double newCoefficientA) {
		this.coefficientA = newCoefficientA;
	}

	/**
	 * Get the B coefficient.
	 * 
	 * @return B coefficient
	 */
	public double getCoefficientB() {
		return coefficientB;
	}

	/**
	 * Set the B coefficient.
	 * 
	 * @param newCoefficientB
	 *            new B coefficient
	 */
	public void setCoefficientB(final double newCoefficientB) {
		this.coefficientB = newCoefficientB;
	}

	/**
	 * Get C coefficient.
	 * 
	 * @return C coefficient
	 */
	public double getCoefficientC() {
		return coefficientC;
	}

	/**
	 * Set C coefficient.
	 * 
	 * @param newCoefficientC
	 *            C coefficient
	 */
	public void setCoefficientC(final double newCoefficientC) {
		this.coefficientC = newCoefficientC;
	}

	/**
	 * Get edge number.
	 * 
	 * @return edge number
	 */
	public int getEdgeNumber() {
		return edgeNumber;
	}

	/**
	 * Set the edge number.
	 * 
	 * @param newEdgeNumber
	 *            new edge number
	 */
	public void setEdgeNumber(final int newEdgeNumber) {
		this.edgeNumber = newEdgeNumber;
	}

	/**
	 * Get edge points.
	 * 
	 * @return edge points array
	 */
	public Site[] getEdgeEndPoints() {
		return edgeEndPoints;
	}

	/**
	 * Set the site bisecting the edge.
	 * 
	 * @return the site bisecting the edge
	 */
	public Site[] getSiteBisecting() {
		return siteBisecting;
	}
}
