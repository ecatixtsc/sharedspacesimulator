package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils;

import java.awt.Color;
import java.util.Random;

import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.visual.VisualConstants;

/**
 * A class that generates a sweep of color through a range of hues, and can
 * convert a data value to a color given a value range.
 * 
 * It generates a range of 100 distinct colors by shifting the hue, you can use
 * them to paint the agents, the zones or for demonstration/ test purposes in
 * voronoi diagrams.
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
public class GradientColors {

	/** The gradient colors. */
	private final transient Color[] colors;

	/** NUber of colors. */
	private static final int COLOR_RANGE = 100;

	/**
	 * Constructs a new Gradient using a given number of colors.
	 *
	 * @param numberOfColors
	 *            the number of colors
	 * @param numberOfHueCycles
	 *            the number of cycles about hue to use (if this is 1 or less, a
	 *            single cycle with lightness 0.5 will be used; otherwise, we
	 *            will spiral up from black to white through the given number of
	 *            hue cycles)
	 */
	public GradientColors(final int numberOfColors, final int numberOfHueCycles) {
		float fraction;
		float hue;
		float lightness;
		colors = new Color[numberOfColors];

		for (int i = 0; i < numberOfColors; i++) {
			hue = VisualConstants.HALF_PI + ((VisualConstants.TWO_PI * i * numberOfHueCycles) / numberOfColors);

			while (hue > VisualConstants.TWO_PI) {
				hue -= VisualConstants.TWO_PI;
			}

			fraction = (float) i / numberOfColors;

			final double scale = 12;
			final double offset = 0.5;
			if (i == 0) {
				lightness = 0;
			} else {
				lightness = (float) (offset - (Math.log((1 / fraction) - 1) / scale));
			}

			colors[i] = colorFromHSL(hue, 1.0f, lightness);
		}
	}

	/**
	 * Gets the number of colors.
	 *
	 * @return the number of colors
	 */
	public final int getNumColors() {
		return this.colors.length;
	}

	/**
	 * Gets a particular color.
	 *
	 * @param index
	 *            the index of the color to get
	 * @return the color
	 */
	public final Color getColor(final int index) {
		if (index >= 0 && index < colors.length) {
			return colors[index];
		} else {
			Random random = new Random();
			return new Color(random.nextInt(COLOR_RANGE), random.nextInt(COLOR_RANGE), random.nextInt(COLOR_RANGE));
		}
	}

	/**
	 * Given a hue in the range 0-1 and a lightness in the range 0-1, generates
	 * the corresponding Color. Saturation is assumed to be maximum.
	 *
	 * @param hue
	 *            the hue angle (0-360)
	 * @param saturation
	 *            the saturation
	 * @param lightness
	 *            the lightness
	 * @return the color
	 */
	private Color colorFromHSL(final float hue, final float saturation, final float lightness) {

		float temp1;
		float temp2;
		float[] color;
		final double maxLightness = 0.5;
		final float hueOffset = 120;

		if (saturation == 0) {
			color = new float[] { lightness, lightness, lightness };
		} else {
			if (lightness < maxLightness) {
				temp2 = 2 * (lightness + saturation) * lightness;
			} else {
				temp2 = lightness + saturation - (lightness * saturation);
			}

			temp1 = (2 * lightness) - temp2;

			color = new float[] { computeColor(temp1, temp2, hue + hueOffset), computeColor(temp1, temp2, hue),
					computeColor(temp1, temp2, hue - hueOffset) };
		}

		return new Color(color[0], color[1], color[2]);
	}

	/**
	 * Computes a color component.
	 *
	 * @param temp1
	 *            lightness/saturation factor 1
	 * @param temp2
	 *            lightness/saturation factor 2
	 * @param hue
	 *            the hue component (0-360)
	 * @return the color component
	 */
	private float computeColor(final float temp1, final float temp2, final float hue) {

		float hue2;
		float color;
		final int radians240 = 240;

		if (hue < 0) {
			hue2 = hue + VisualConstants.TWO_PI;
		} else if (hue > VisualConstants.TWO_PI) {
			hue2 = hue - VisualConstants.TWO_PI;
		} else {
			hue2 = hue;
		}

		if (hue2 < VisualConstants.ONE_THIRD_PI) {
			color = temp1 + ((temp2 - temp1) * hue2 / VisualConstants.ONE_THIRD_PI);
		} else if (hue2 < VisualConstants.PI) {
			color = temp2;
		} else if (hue2 < radians240) {
			color = temp1 + ((temp2 - temp1) * (radians240 - hue2) / VisualConstants.ONE_THIRD_PI);
		} else {
			color = temp1;
		}

		if (color > 1) {
			color = 1;
		} else if (color < 0) {
			color = 0;
		}

		return color;
	}
}