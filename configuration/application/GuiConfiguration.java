package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.application;

/**
 * GUI configuration: background, shapes and logo path.
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
public final class GuiConfiguration {

	/**
	 * GUI logo.
	 */
	public static final String LOGO_PATH = "catpedsim/images/pedestrian.png";

	/**
	 * Human shapes.
	 */
	public static final String[] HUMAN_SHAPES = { "catpedsim/images/human1.svg", "catpedsim/images/human2.svg",
			"catpedsim/images/human3.svg", "catpedsim/images/human4.svg" };

	/**
	 * Bicycle shapes.
	 */
	public static final String[] BICYCLE_SHAPES = { "catpedsim/images/bicycle.svg" };

	/**
	 * Private constructor required by the utility classes.
	 */
	private GuiConfiguration() {

	}
}