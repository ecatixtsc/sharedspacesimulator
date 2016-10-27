package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.path;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vertex")
public class XmlVertex {

	/** Point x coordinate. */
	@XmlAttribute(name = "px", required = true)
	private float x = 0;

	/** Point y coordinate. */
	@XmlAttribute(name = "py", required = true)
	private float y = 0;

	/** Vertex id. */
	@XmlAttribute(name = "id", required = false)
	private int idVertex = 0;

	/**
	 * Constructor.
	 */
	public XmlVertex() {

	}

	/**
	 * Get the coordinate on the X axis.
	 * 
	 * @return the coordinate on the X axis
	 */
	public final float getX() {
		return x;
	}

	/**
	 * Set the coordinate on the X axis.
	 * 
	 * @param newX
	 *            the coordinate on the X axis
	 */
	public final void setX(final float newX) {
		this.x = newX;
	}

	/**
	 * Get the coordinate on the Y axis.
	 * 
	 * @return the coordinate on the Y axis
	 */
	public final float getY() {
		return y;
	}

	/**
	 * Set the coordinate on the Y axis.
	 * 
	 * @param newY
	 *            the coordinate on the Y axis
	 */
	public final void setY(final float newY) {
		this.y = newY;
	}

	/**
	 * Constructor.
	 * 
	 * @param newX
	 *            the coordinate on the X axis
	 * @param newY
	 *            the coordinate on the Y axis
	 * @param newId
	 *            vertex id
	 */
	public XmlVertex(final int newX, final int newY, final int newId) {
		x = newX;
		y = newY;
		idVertex = newId;
	}
}
