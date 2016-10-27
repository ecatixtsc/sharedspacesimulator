package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * A set of utilities for user interface construction.
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
 */
public final class UIUtilities {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private UIUtilities() {
	}

	/**
	 * Gets a Graphics2D object that is compatible with the current desktop.
	 *
	 * @return the Graphics2D belonging to the current environment
	 */
	public static Graphics2D getGraphics() {
		GraphicsEnvironment graphicEnvironment;
		BufferedImage bufferedImage;

		graphicEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		bufferedImage = graphicEnvironment.getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1,
				1);
		return graphicEnvironment.createGraphics(bufferedImage);
	}

	/**
	 * Places a component (typically a frame or dialog) at a particular position
	 * within the desktop.
	 *
	 * @param frame
	 *            the frame to position
	 * @param xAxisCoordinate
	 *            the x position (0.0 is left, 1.0 is right, 0.5 is centered)
	 * @param yAxisCoordinate
	 *            the y position (0.0 is top, 1.0 is bottom, 0.5 is centered)
	 */
	public static void positionFrame(final Component frame, final double xAxisCoordinate,
			final double yAxisCoordinate) {
		Dimension screen;
		Dimension size;
		int xPixel;
		int yPixel;

		screen = Toolkit.getDefaultToolkit().getScreenSize();
		size = frame.getSize();

		xPixel = (int) ((screen.width - size.width) * xAxisCoordinate);
		yPixel = (int) ((screen.height - size.height) * yAxisCoordinate);

		frame.setLocation(xPixel, yPixel);
	}
}