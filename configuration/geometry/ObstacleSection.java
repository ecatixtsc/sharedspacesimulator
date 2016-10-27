package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry;

import processing.core.PVector;

/**
 * The edge or segment of an obstacle. The obstacles are open polygons (walls)
 * or closed polygons (buildings).
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
public class ObstacleSection {
	/** Vector from the world origin to start of section/segment. */
	private PVector vectorToStartPoint = null;

	/** Vector from the world origin to end point of section/segment. */
	private PVector vectorToEndPoint = null;

	/** Section/segment length. */
	private float length = 0;

	/**
	 * Constructor.
	 * 
	 * @param newVectorToStartPoint
	 *            vector form the origin to the start section point
	 * @param newVectorToEndPoint
	 *            vector form the origin to the end section point
	 */
	public ObstacleSection(final PVector newVectorToStartPoint, final PVector newVectorToEndPoint) {
		vectorToStartPoint = newVectorToStartPoint;
		vectorToEndPoint = newVectorToEndPoint;

		length = PVector.dist(vectorToStartPoint, vectorToEndPoint);
	}

	/**
	 * Get vector to start point of the section.
	 * 
	 * @return vector to start point of the section
	 */
	public final PVector getVectorToStartPoint() {
		return vectorToStartPoint;
	}

	/**
	 * Get vector to end point of the section.
	 * 
	 * @return vector to end point of the section
	 */
	public final PVector getVectorToEndPoint() {
		return vectorToEndPoint;
	}

	/**
	 * Get section/segment length.
	 * 
	 * @return section length
	 */
	public final float getLength() {
		return length;
	}

}
