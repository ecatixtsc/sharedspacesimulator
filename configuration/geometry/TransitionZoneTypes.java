package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry;

import java.awt.Color;

/**
 * Types of transition zones.
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
public enum TransitionZoneTypes {
	/** Autonomus vehicle rack. */
	AUTONOMOUS_VEHICLE_RACK("PodRack"),

	/** Bicycle rack. */
	BICYCLE_RACK("CycleRack");

	/** String value with transit zone caption. */
	private String value;

	/** Transit zone colour. */
	private Color color = Color.BLACK;

	/**
	 * Get transit zone caption.
	 * 
	 * @return transit zone caption
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Constructor.
	 * 
	 * @param newValue
	 *            transition zone caption
	 */
	TransitionZoneTypes(final String newValue) {
		value = newValue;
		final int zoneId0 = 0;
		final int zoneId1 = 1;
		final int zoneId3 = 3;

		switch (ordinal()) {
		case zoneId0:
			color = Color.GREEN;
			break;
		case zoneId1:
			color = Color.MAGENTA;
			break;
		case zoneId3:
			color = Color.ORANGE;
			break;
		default:
			break;
		}
	}

	/**
	 * Get transition zone color.
	 * 
	 * @return transition zone color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Construcotr.
	 */
	TransitionZoneTypes() {

	}
}
