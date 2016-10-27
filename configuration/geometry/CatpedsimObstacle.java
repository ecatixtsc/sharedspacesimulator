package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry;

import java.util.LinkedList;
import java.util.List;

import processing.core.PVector;

/**
 * An obstacle is always a polygon. A close polygon is a building, an open
 * polygon is a wall.
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
public class CatpedsimObstacle {

	/**
	 * Obstacle name, label or id.
	 */
	private String name;

	/**
	 * Closed polygon or not.
	 */
	private boolean isClosed = false;

	/**
	 * The polygon's composing vector's points.
	 */
	private List<PVector> vertices = new LinkedList<PVector>();

	/**
	 * Vector originating in (0,0) to the position of the centroid.
	 */
	private PVector vectorToCentroid = null;

	/**
	 * Array with all the obstacle sections. It represents every edge of the
	 * polygon. It is defined as a static array for performance issues.
	 */
	private ShapeSection[] obstacleSections = null;

	/**
	 * Constructor.
	 * 
	 * @param obstacleName
	 *            the name of the obstacle
	 * @param closed
	 *            the type of the polygon
	 */
	public CatpedsimObstacle(final String obstacleName, final boolean closed) {
		this.name = obstacleName;
		this.isClosed = closed;
	}

	/**
	 * Add a new vertex to the current polygon.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 */
	public final void addVertex(final float x, final float y) {
		vertices.add(new PVector(x, y));
	}

	/**
	 * Return true if the polygon is closed, false otherwise.
	 * 
	 * @return if closed state
	 */
	public final boolean isClosed() {
		return isClosed;
	}

	/**
	 * Build all section of the polygon with their length, calculate centroid.
	 * 
	 * @throws Exception
	 *             on error
	 */
	public final void buildSectionObjectsAndComputeCentroid() throws Exception {
		int numberOfSections = 0;
		int numberOfVertices = vertices.size();

		if (numberOfVertices % 2 != 0) {
			return;
		}

		numberOfSections = numberOfVertices / 2;

		int obstacleIndex = 0;

		obstacleSections = new ShapeSection[numberOfSections];
		vectorToCentroid = new PVector(0, 0, 0);

		for (int indexVertex = 0; indexVertex < numberOfVertices - 1; indexVertex += 2) {
			PVector startVertex = vertices.get(indexVertex);
			PVector endVertex = vertices.get(indexVertex + 1);

			vectorToCentroid.set(vectorToCentroid.x + startVertex.x + endVertex.x,
					vectorToCentroid.y + startVertex.y + endVertex.y, 0);
			obstacleSections[obstacleIndex++] = new ShapeSection(startVertex, endVertex);
		}

		vectorToCentroid.set(vectorToCentroid.x / numberOfVertices, vectorToCentroid.y / numberOfVertices, 0);
		vertices.clear();
	}

	/**
	 * Get the array holding all the polygon's sections.
	 * 
	 * @return array with the polygon's sections
	 */
	public final ShapeSection[] getObstacleSections() {
		return obstacleSections;
	}

	/**
	 * Gets a vector originating in (0,0) to the position of the centroid.
	 * 
	 * @return a vector object with the centroid's coordinates
	 */
	public final PVector getVectorToCentroid() {
		return vectorToCentroid;
	}

	/**
	 * Get obstacle's name/caption.
	 * 
	 * @return obstacle name
	 */
	public final String getName() {
		return name;
	}
}
