package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import uk.org.catapult.ts.cav.utils.ResourceUtilities;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * Utility class for time formatting, checking the operating system type,
 * Generating random etc.
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
public final class Utils {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = UtilityLogger.getLogger(Utils.class);

	/**
	 * Date-time formatter pattern.
	 */
	private static final String PATTERN_DATE_TIME = "dd.MM.yyyy HH:mm:ss";

	/**
	 * The application relative file path.
	 */
	public static final String APPLICATION_PATH = System.getProperty("user.dir");

	/**
	 * The operating system's specific file path separator.
	 */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	/**
	 * Date-time formatter.
	 */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN_DATE_TIME);

	/** Random. */
	private static Random random = new Random();

	/** Default tolerance for checking if float values are equal. */
	public static final float FLOAT_TOLERANCE = 0.000001f;

	/**
	 * It returns the absolute path for the geometry file.
	 * 
	 * @param geometryRelativeFilePath
	 *            create the geometry file absolute path
	 * @return the absolute path
	 */
	public static URI getAbsolutePathFromRelative(final String geometryRelativeFilePath) {
		return ResourceUtilities
				.getResourceURI(geometryRelativeFilePath.replaceAll("\n", " ").replaceAll("\t", "").trim());
	}

	/**
	 * Finds whether OS is windows.
	 * 
	 * @return true if windows, false otherwise
	 */
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();

		return (os.indexOf("win") >= 0);
	}

	/**
	 * Adjust look and feel according to the operating system.
	 * 
	 * @throws UnsupportedLookAndFeelException
	 *             on paint error
	 */
	public static void setDefaultLookAndFeel() throws UnsupportedLookAndFeelException {
		try {
			// if OS is Windows we set the windows look and feel
			if (isWindows()) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else {
				// otherwise we set metal look and feel
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
		}
	}

	/**
	 * Return an image from an image URL.
	 * 
	 * @param resource
	 *            URL to image resource
	 * @return Image object from an URL
	 * @throws IOException
	 */
	public static Image getImageFromResource(final URL resource) {
		Image img = null;
		try {
			img = ImageIO.read(resource);
		} catch (IOException ex) {
			LOGGER.info("Failed getting an image from a resource file.", ex);
		}
		return img;
	}

	/**
	 * Float range random.
	 * 
	 * @param low
	 *            low float range
	 * @param high
	 *            high float range
	 * @return the random value in the provided interval
	 */
	public static float random(final float low, final float high) {
		if (low >= high) {
			return low;
		}
		float difference = high - low;
		return random(difference) + low;
	}

	/**
	 * Random float (0,high).
	 * 
	 * @param high
	 *            the maximum boundary
	 * @return random float between 0, high
	 */
	public static float random(final float high) {
		// avoid an infinite loop when 0 or NaN are passed in
		if (high == 0 || high != high) {
			return 0;
		}

		Random internalRandom = new Random();
		final float floatingPointAccuracy = 0.0000001f;

		float value = 0;
		do {
			value = internalRandom.nextFloat() * high;
		} while (Math.abs(value - high) < floatingPointAccuracy);
		return value;
	}

	/**
	 * Create a time stamp. Converts the system's milliseconds to a human
	 * readable time.
	 * 
	 * @return timestamp in a human readable form
	 */
	public static String createFormattedTimeStamp() {
		String stamp = "";
		try {
			stamp = DATE_FORMAT.format(new Date(System.currentTimeMillis()));
		} catch (Exception ex) {
			LOGGER.info("Failed creating a time stamp.", ex);
			stamp = "Time stamp could not be created";
		}
		return stamp;
	}

	/**
	 * Add "numberOfCharacters" whitespaces to the right side of the given
	 * string.
	 * 
	 * @param text
	 *            given string
	 * @param numberOfCharacters
	 *            the number of whitespaces to add
	 * @return the final string
	 */
	public static String padStringWithSpacesRight(final String text, final int numberOfCharacters) {
		return String.format("%1$-" + numberOfCharacters + "s", text);
	}

	/**
	 * Add "numberOfCharacters" whitespaces to the left side of the given
	 * string.
	 * 
	 * @param text
	 *            given string
	 * @param numberOfCharacters
	 *            the number of whitespaces to add
	 * @return the final string
	 */
	public static String padStringWithSpaceLeft(final String text, final int numberOfCharacters) {
		return String.format("%1$" + numberOfCharacters + "s", text);
	}

	/**
	 * Generate random pastel color.
	 * 
	 * @return random pastel color
	 */
	public static Color generateRandomPastelColor() {
		final float hue = getRandom().nextFloat();
		final float saturation = (random.nextInt(2000) + 1000) / 10000f;
		final float luminance = 0.9f;
		return Color.getHSBColor(hue, saturation, luminance);
	}

	/**
	 * Generate random dark color.
	 * 
	 * @return random pastel color
	 */
	public static Color generateRandomDarkColor() {
		final float hue = getRandom().nextFloat();
		final float saturation = 1f;
		final float luminance = 1f;
		return Color.getHSBColor(hue, saturation, luminance).darker();
	}

	/**
	 * Get the Random object.
	 * 
	 * @return random object
	 */
	public static Random getRandom() {
		return random;
	}

	/**
	 * Convert a millisecond duration to a string format.
	 * 
	 * @param milliseconds
	 *            A duration to convert to a string form
	 * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
	 */
	public static String getDurationBreakdown(final long milliseconds) {
		if (milliseconds < 0) {
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}

		long ms = milliseconds;

		long days = TimeUnit.MILLISECONDS.toDays(ms);
		ms -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(ms);
		ms -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(ms);
		ms -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
		ms -= TimeUnit.SECONDS.toMillis(seconds);
		long leftMilliseconds = TimeUnit.MILLISECONDS.toMillis(ms);

		StringBuilder sb = new StringBuilder();
		sb.append(days);
		sb.append(" Days ");
		sb.append(hours);
		sb.append(" Hours ");
		sb.append(minutes);
		sb.append(" Minutes ");
		sb.append(seconds);
		sb.append(" Seconds ");
		sb.append(leftMilliseconds);
		sb.append(" Milliseconds");

		return (sb.toString());
	}

	/**
	 * Private constructor required in utility classes.
	 */
	private Utils() {

	}
}
