package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils;

import java.awt.geom.Line2D;

import processing.core.PVector;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.ShapeSection;

/**
 * Trigonometry utilities.
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
public final class Trigonometry {

	/**
	 * The intersection point between two segments. Useful in determining if two
	 * path are intersecting.
	 * 
	 * @param p1Start
	 *            start point of line 1
	 * @param p1End
	 *            end point of line 1
	 * @param p2Start
	 *            start point of line 2
	 * @param p2End
	 *            end point if line 2
	 * @return true if this line segment and the specified line segment
	 *         intersect each other; false otherwise.
	 */
	public static boolean checkIfLinesIntersect(final PVector p1Start, final PVector p1End, final PVector p2Start,
			final PVector p2End) {
		Line2D line1 = new Line2D.Float(p1Start.x, p1Start.y, p1End.x, p1End.y);
		Line2D line2 = new Line2D.Float(p2Start.x, p2Start.y, p2End.x, p2End.y);
		return line2.intersectsLine(line1);
	}

	/**
	 * Calculates the distance from a point to a segment.
	 * 
	 * @param vectorToPoint
	 *            vector from the origin to the point of interest
	 * @param segmentPointStartVector
	 *            vector from the origin to the segment's start point
	 * @param segmentPointEndVector
	 *            vector from the origin to the segment's end point
	 * @return the distance from a point to a segment
	 */
	public static float distanceFromPointToSegment(final PVector vectorToPoint, final PVector segmentPointStartVector,
			final PVector segmentPointEndVector) {
		return (float) Line2D.ptSegDist(segmentPointStartVector.x, segmentPointStartVector.y, segmentPointEndVector.x,
				segmentPointEndVector.y, vectorToPoint.x, vectorToPoint.y);
	}

	/**
	 * Compute the position vector of the normal point on a segment.
	 * 
	 * @param positionVector
	 *            position vector to the point of interest
	 * @param positionVectorLineStart
	 *            position vector to the line start point
	 * @param positionVectorLineEnd
	 *            position vector to the line end point
	 * @return vector to the normal point
	 */
	public static PVector getVectorToNormalPoint(final PVector positionVector, final PVector positionVectorLineStart,
			final PVector positionVectorLineEnd) {
		PVector positionVectorToNormalPoint = new PVector();

		PVector vectorFromPointToStartLine = PVector.sub(positionVector, positionVectorLineStart);
		PVector vectorFromPointToEndLine = PVector.sub(positionVector, positionVectorLineEnd);

		vectorFromPointToEndLine.normalize();
		vectorFromPointToEndLine.mult(vectorFromPointToStartLine.dot(vectorFromPointToEndLine));

		positionVectorToNormalPoint = PVector.add(positionVectorLineStart, vectorFromPointToEndLine);

		return positionVectorToNormalPoint;
	}

	/**
	 * Calculate the projection of C unto the vector pointing from A to B,
	 * returning the length along AB that this point is found.
	 * 
	 * @param vectorA
	 *            vector A
	 * @param vectorB
	 *            vector B
	 * @param vectorC
	 *            vector C
	 * @return vector projection
	 */
	public static float vectorProjectionLength(final PVector vectorA, final PVector vectorB, final PVector vectorC) {
		PVector acVector = PVector.sub(vectorC, vectorA);
		PVector abVector = PVector.sub(vectorB, vectorA);
		return PVector.dot(acVector, abVector) / abVector.mag();
	}

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
	private Trigonometry() {

	}

	/**
	 * Check a point is inside a polygon.
	 * 
	 * @param polygonSections
	 *            array containing obstacle sections
	 * @param vectorToPoint
	 *            vector from the origin to the point of interest
	 * @return true if the point is inside the polygon
	 */
	public static boolean pointInsideClosedPolygon(final ShapeSection[] polygonSections, final PVector vectorToPoint) {
		int indexSection = 0;
		boolean isInside = false;

		for (indexSection = 0; indexSection < polygonSections.length; indexSection++) {
			ShapeSection section = polygonSections[indexSection];

			if ((section.getVectorToStartPoint().y < vectorToPoint.y
					&& section.getVectorToEndPoint().y >= vectorToPoint.y
					|| section.getVectorToEndPoint().y < vectorToPoint.y
							&& section.getVectorToStartPoint().y >= vectorToPoint.y)
					&& (section.getVectorToStartPoint().x <= vectorToPoint.x
							|| section.getVectorToEndPoint().x <= vectorToPoint.x)) {
				isInside ^= (section.getVectorToStartPoint().x + (vectorToPoint.y - section.getVectorToStartPoint().y)
						/ (section.getVectorToEndPoint().y - section.getVectorToStartPoint().y)
						* (section.getVectorToEndPoint().x - section.getVectorToStartPoint().x) < vectorToPoint.x);
			}
		}

		return isInside;
	}

	/**
	 * Check the point is inside a segment.
	 * 
	 * @param vectorToPoint
	 *            vector from the origin to the specified point
	 * @param start
	 *            vector from the origin to the segment start point
	 * @param end
	 *            vector from the origin to the segment end point
	 * @return true if the specified point belongs to the segment
	 */
	public static boolean checkIfPointBelongsToSegment(final PVector vectorToPoint, final PVector start,
			final PVector end) {
		boolean isInside = false;

		float diffPointToStartX = vectorToPoint.x - start.x;
		float diffPointToStartY = vectorToPoint.y - start.y;

		float diffPointToEndX = vectorToPoint.x - end.x;
		float diffPointToEndY = vectorToPoint.y - end.y;

		final float floatingPointAccuracy = 0.0000001f;

		if (Math.abs(
				(diffPointToStartX / diffPointToStartY - diffPointToEndX / diffPointToEndY)) < floatingPointAccuracy
				&& ((diffPointToStartX < 0) != (diffPointToEndX < 0))) {
			isInside = true;
		}

		return isInside;

	}

	/**
	 * Calculate the distance between two points in the cartesian space.
	 * 
	 * @param x1
	 *            point 1 coordinate on the x axis
	 * @param y1
	 *            point 1 coordinate on the y axis
	 * @param x2
	 *            point 2 coordinate on the x axis
	 * @param y2
	 *            point 2 coordinate on the y axis
	 * @return the distance between two points
	 */
	public static float distanceBetweenPoints(final float x1, final float y1, final float x2, final float y2) {
		return (float) Math.sqrt(Math.abs(y2 - y1) + Math.abs(x2 - x1));
	}
}
