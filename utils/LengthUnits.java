package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils;

import javax.xml.bind.annotation.XmlType;

import proguard.annotation.KeepClassMemberNames;
import proguard.annotation.KeepName;

/**
 * LengthUnits is used for converting the world dimensions. The dimensions could
 * be read in kilometres, meters and centimetres but they are internally
 * converted to meters. The XML line example:
 * <worldDimensions width="50" height="50" unitOfLength="m"
 * acceptedUnitsOfLength="km m cm"/>
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
@XmlType(name = "LengthUnits")
@KeepName
@KeepClassMemberNames
public enum LengthUnits {
	/**
	 * Length unit in kilometres.
	 */
	KILOMETERS("km"),
	/**
	 * Length unit in meters.
	 */
	METERS("m"),
	/**
	 * Length unit in centimeters.
	 */
	CENTIMETERS("cm");

	/** Hundreds. */
	private static final int HUNDREDS = 100;

	/** Thousands. */
	private static final int THOUSANDS = 1000;

	/**
	 * The unitSymbol holds the length unit.
	 */
	private String unitSymbol = null;

	/**
	 * Constructor.
	 * 
	 * @param symbol
	 *            the length unit
	 */
	LengthUnits(final String symbol) {
		this.unitSymbol = symbol;
	}

	/**
	 * Retrieve the length unit as a text.
	 * 
	 * @return the length unit as a text
	 */
	public String getUnitSymbol() {
		return unitSymbol;
	}

	/**
	 * Convert to meters.
	 * 
	 * @param currentUnit
	 *            current length unit as enumeration
	 * @param value
	 *            the tick length value
	 * @return the tick length value
	 */
	public static float convertToMeters(final LengthUnits currentUnit, final float value) {
		float result = -1;

		if (currentUnit == null) {
			return result;
		}
		if (currentUnit.equals(LengthUnits.KILOMETERS)) {
			result = value * THOUSANDS;
		}
		if (currentUnit.equals(LengthUnits.CENTIMETERS)) {
			result = value / HUNDREDS;
		}
		if (currentUnit.equals(LengthUnits.METERS)) {
			result = value;
		}

		return result;
	}
}
