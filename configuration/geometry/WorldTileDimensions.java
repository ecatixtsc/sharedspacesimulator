package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import proguard.annotation.KeepClassMemberNames;
import proguard.annotation.KeepName;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.LengthUnits;

/**
 * Reads word dimensions from the XML file.
 * 
 * Example:
 * <worldDimensions width="50" depth="50" unitOfLength="m" acceptedUnitsOfLength
 * ="km m cm">
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
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "WorldTileDimensions", propOrder = { "width", "depth", "height", "unit", "acceptedUnitsOfLength" })
@KeepName
@KeepClassMemberNames
public class WorldTileDimensions implements Serializable {

	/**
	 * For serialisation purposes.
	 */
	private static final long serialVersionUID = 6212330435073215806L;

	/**
	 * World dimension unit as read from the XML.
	 */
	private LengthUnits unit = LengthUnits.METERS;
	/**
	 * World width (x axis).
	 */
	private float width = 0.0f;
	/**
	 * World depth (y axis).
	 */
	private float depth = 0.0f;
	/**
	 * World height (z axis).
	 */
	private float height = 0.0f;
	/**
	 * A description of accepted length units.
	 */
	private String acceptedUnitsOfLength = null;

	/**
	 * Constructor.
	 */
	public WorldTileDimensions() {
		StringBuilder buildAcceptedUnitsOfLength = new StringBuilder();

		for (LengthUnits myLengthUnit : LengthUnits.values()) {
			buildAcceptedUnitsOfLength.append(myLengthUnit.name() + " ");
		}

		acceptedUnitsOfLength = buildAcceptedUnitsOfLength.toString().trim();
	}

	/**
	 * Constructor.
	 * 
	 * @param newWorldTileWidth
	 *            world tile width
	 * @param newWorldTileDepth
	 *            world tile depth
	 */
	public WorldTileDimensions(final float newWorldTileWidth, final float newWorldTileDepth) {
		this();
		width = newWorldTileWidth;
		depth = newWorldTileDepth;
	}

	/**
	 * Gets the world width.
	 * 
	 * @return world width
	 */
	@XmlAttribute(name = "width")
	public final float getWidth() {
		return width;
	}

	/**
	 * Sets the world width.
	 * 
	 * @param newWidth
	 *            to width to be set
	 */
	public final void setWidth(final float newWidth) {
		this.width = newWidth;
	}

	/**
	 * gets world depth.
	 * 
	 * @return world depth
	 */
	@XmlAttribute(name = "depth")
	public final float getDepth() {
		return depth;
	}

	/**
	 * Sets the world depth.
	 * 
	 * @param newDepth
	 *            world depth
	 */
	public final void setDepth(final float newDepth) {
		this.depth = newDepth;
	}

	/**
	 * Get world height.
	 * 
	 * @return world height (z axis)
	 */
	@XmlAttribute(name = "height")
	public final float getHeight() {
		return height;
	}

	/**
	 * Set world height.
	 * 
	 * @param newHeight
	 *            world height (z axis)
	 */
	public final void setHeight(final float newHeight) {
		this.height = newHeight;
	}

	/**
	 * Gets world length unit.
	 * 
	 * @return world length unit
	 */
	@XmlAttribute(name = "unitOfLength")
	public final LengthUnits getUnit() {
		return unit;
	}

	/**
	 * Sets world length unit.
	 * 
	 * @param newUnit
	 *            length unit
	 */
	public final void setUnit(final LengthUnits newUnit) {
		this.unit = newUnit;
	}

	/**
	 * Gets accepted length units. A short guide for the user.
	 * 
	 * @return accepted unit lengths
	 */
	@XmlAttribute(name = "acceptedUnitsOfLength")
	public final String getAcceptedUnitsOfLength() {
		return acceptedUnitsOfLength;
	}

	/**
	 * Sets accepted length units. A short guide for the user.
	 * 
	 * @param newAcceptedUnitsOfLength
	 *            accepted unit lengths
	 */
	public final void setAcceptedUnitsOfLength(final String newAcceptedUnitsOfLength) {
		this.acceptedUnitsOfLength = newAcceptedUnitsOfLength;
	}
}
