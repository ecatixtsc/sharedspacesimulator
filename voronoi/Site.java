package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.voronoi;

/**
 * A site or vertex in the Voronoi diagram. In Catpedsim's case a site is the
 * corner/point of an obstacle.
 * 
 * Reference: http://www.math.colostate.edu/
 */
public class Site extends Vertex implements Comparable<Site> {

	/** The index of the site. */
	private int siteIndex;

	/**
	 * Constructs a new Site.
	 *
	 * @param xCoordinate
	 *            the X coordinate
	 * @param yCoordinate
	 *            the Y coordinate
	 * @param newSiteIndex
	 *            the site number
	 */
	public Site(final double xCoordinate, final double yCoordinate, final int newSiteIndex) {
		super(xCoordinate, yCoordinate);
		siteIndex = newSiteIndex;
	}

	/**
	 * Compares this object with the specified object for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * @param object
	 *            the object to be compared
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	public final int compareTo(final Site object) {
		int result;

		if (getyCoordinate() < object.getyCoordinate()) {
			result = -1;
		} else if (getyCoordinate() > object.getyCoordinate()) {
			result = 1;
		} else if (getxCoordinate() < object.getxCoordinate()) {
			result = -1;
		} else if (getxCoordinate() > object.getxCoordinate()) {
			result = 1;
		} else {
			result = 0;
		}

		return result;
	}

	/**
	 * Tests this object for equality with another object. To be equal, the
	 * other object must be a site, and must have the same X and Y coordinates.
	 * The site number is not used in comparison.
	 *
	 * @param object
	 *            the object to compare
	 */
	@Override
	public final boolean equals(final Object object) {
		Site site;
		boolean equal;

		if (object instanceof Site) {
			site = (Site) object;
			equal = (site.getxCoordinate() == getxCoordinate()) && (site.getyCoordinate() == getyCoordinate());
		} else {
			equal = false;
		}

		return equal;
	}

	/**
	 * Generates the hash code of the site.
	 *
	 * @return the hash code
	 */
	@Override
	public final int hashCode() {
		return (int) Double.doubleToLongBits(getxCoordinate() + getyCoordinate());
	}

	/**
	 * Get site index.
	 * 
	 * @return site index
	 */
	public final int getSiteIndex() {
		return siteIndex;
	}

	/**
	 * Set site index.
	 * 
	 * @param newSiteIndex
	 *            site index
	 */
	public final void setSiteIndex(final int newSiteIndex) {
		this.siteIndex = newSiteIndex;
	}
}
