package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.visual;

/**
 * Constants for the visualization. Example: color, size, shape, transparency.
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
public final class VisualConstants {
	/** Default window width. */
	public static final int DEFAULT_WINDOW_WIDTH = 950;

	/** Default window depth. */
	public static final int DEFAULT_WINDOW_DEPTH = 650;

	/** Default font. */
	public static final String DEFAULT_FONT = "Georgia";

	/** Window title. */
	public static final String WINDOW_TITLE = "Catpedsim";

	/** Stroke weight for the bicycle's outline. */
	public static final float STROKE_WEIGHT_AGENTS = 1.5f;

	/** Text size 10. */
	public static final int TEXT_SIZE_10 = 10;

	/** Text size 12. */
	public static final int TEXT_SIZE_12 = 12;

	/** The size of a small dot in pixels. */
	public static final int SMALL_DOT_IN_PIXELS = 3;

	/** Color range value. */
	public static final int COLOR_RANGE = 255;

	/** Light gray color, RGB code. */
	public static final int LIGHT_GRAY_RGB = 120;

	/** Text rectangle width for the simulation information. */
	public static final int TEXT_RECTANGLE_WIDTH = 350;

	/** Change direction angle in radians. */
	public static final float CHANGE_DIRECTION_ANGLE_RADIANS = 0.1f;

	/** PI/2 value in radians. */
	public static final float HALF_PI = 90;

	/** 2*PI value in radians. */
	public static final float TWO_PI = 360;

	/** PI value in radians. */
	public static final float PI = 180;

	/** PI/3 radians. */
	public static final float ONE_THIRD_PI = 60;

	/**
	 * Constructor.
	 */
	private VisualConstants() {

	}
}
