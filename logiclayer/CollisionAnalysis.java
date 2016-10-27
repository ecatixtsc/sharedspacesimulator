package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.logiclayer;

import processing.core.PVector;

/**
 * 
 * @author Ecaterina McCormick, ecaterina.mccormick@ts.catapult.org.uk
 * 
 *         Copyright © <2016> TRANSPORT SYSTEMS CATAPULT
 * 
 *         Permission is hereby granted, free of charge, to any person obtaining
 *         a copy of this software and associated documentation files (the
 *         "Software"), to deal in the Software without restriction, including
 *         without limitation the rights to use, copy, modify, merge, publish,
 *         distribute, sublicense, and/or sell copies of the Software, and to
 *         permit persons to whom the Software is furnished to do so, subject to
 *         the following conditions:
 * 
 *         The above copyright notice and this permission notice shall be
 *         included in all copies or substantial portions of the Software. Users
 *         of the Software shall acknowledge Transport Systems Catapult as the
 *         source of the Software in any publication that refers to it.
 * 
 *         THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *         EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *         MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *         NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *         BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 *         ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *         CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *         SOFTWARE.
 *
 */
public final class CollisionAnalysis {

	/**
	 * Calculate the squared distance of a point P to a line segment A-B and
	 * return the nearest line point S.
	 * 
	 * @param segmentPoint1
	 *            point 1 of the A-B segment
	 * @param segmentPoint2
	 *            point 2 of the segment A-B
	 * @param myPoint
	 *            the point
	 * @param projectionPoint
	 *            nearest line point S
	 * @return squared distance of a point P to a line segment A-B
	 */
	public static float squaredDistancePointToLineSegment(final PVector segmentPoint1, final PVector segmentPoint2,
			final PVector myPoint, final PVector projectionPoint) {
		float vx = myPoint.x - segmentPoint1.x, vy = myPoint.y - segmentPoint1.y;
		float ux = segmentPoint2.x - segmentPoint1.x, uy = segmentPoint2.y - segmentPoint1.y;
		float det = vx * ux + vy * uy;

		if (det <= 0) {
			projectionPoint.set(segmentPoint1);
			return vx * vx + vy * vy;
		}
		float len = ux * ux + uy * uy;
		if (det >= len) {
			projectionPoint.set(segmentPoint2);
			return squareNumber(segmentPoint2.x - myPoint.x) + squareNumber(segmentPoint2.y - myPoint.y);
		}

		float ex = (float) (ux / Math.sqrt(len));
		float ey = (float) (uy / Math.sqrt(len));
		float f = ex * vx + ey * vy;
		projectionPoint.x = segmentPoint1.x + f * ex;
		projectionPoint.y = segmentPoint1.y + f * ey;

		return (squareNumber(ux * vy - uy * vx) / len);
	}

	/**
	 * Squares a number (multiplies a number by itself). The result is always a
	 * positive number, as multiplying two negative numbers always yields a
	 * positive result. For example, -1 * -1 = 1.
	 * 
	 * @param number
	 *            number to square
	 * @return square of number
	 */
	public static float squareNumber(final float number) {
		return number * number;
	}

	/**
	 * Private constructor for utility classes.
	 */
	private CollisionAnalysis() {

	}
}
