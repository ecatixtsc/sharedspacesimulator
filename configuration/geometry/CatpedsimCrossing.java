package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import processing.core.PVector;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * Crossing in the geometry.
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
public class CatpedsimCrossing {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = UtilityLogger.getLogger(CatpedsimCrossing.class);

	/** Crossing name. */
	private String name;

	/** Vector for the crossing. */
	private List<PVector> vertices = new LinkedList<PVector>();

	/** The crossing length. */
	private float crosingLength = 0;

	/**
	 * Constructor.
	 * 
	 * @param newName
	 *            crossing name
	 */
	public CatpedsimCrossing(final String newName) {
		name = newName;
	}

	/**
	 * Get crossing name.
	 * 
	 * @return crossing name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Add vertices in the crossing configuration. After the crossing is
	 * defined, calculate the crossing length and sort the vertices in the order
	 * of its proximity to the origin.
	 * 
	 * @param x
	 *            coordinate on the x axis
	 * @param y
	 *            coordinate on the y axis
	 */
	public final void addVertex(final float x, final float y) {
		vertices.add(new PVector(x, y));

		if (vertices.size() == 2) {
			crosingLength = PVector.dist(vertices.get(0), vertices.get(1));
		}
	}

	/**
	 * Get vertex at indicated index.
	 * 
	 * @param index
	 *            index in the list of vertices
	 * @return vector
	 */
	public final PVector getVertexAtIndex(final int index) {
		PVector vertex = null;
		try {
			vertex = vertices.get(index);
		} catch (Exception ex) {
			LOGGER.warn("Error occured while accessing the crossing's vertex.", ex);
		}
		return vertex;
	}

	/**
	 * Number of vertices.
	 * 
	 * @return number of vertices
	 */
	public final int getNumberOfVertices() {
		return vertices.size();
	}

	/**
	 * Returns the crossing length.
	 * 
	 * @return crossing length
	 */
	public final float getCrosingLength() {
		return crosingLength;
	}

	/**
	 * Get crossing centroid.
	 * 
	 * @return centroid of the crossing shape
	 */
	public final PVector getCentroid() {
		if (vertices == null || vertices.size() < 2) {
			return null;
		}

		PVector middleVector = PVector.add(vertices.get(0), vertices.get(1));
		middleVector.div(2);

		return middleVector;
	}
}