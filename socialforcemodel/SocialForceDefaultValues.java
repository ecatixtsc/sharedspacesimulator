package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.socialforcemodel;

/**
 * Default values for Social Force Model parameters.
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
public final class SocialForceDefaultValues {

	/** Default interaction strength. */
	public static final float DEFAULT_INTERACTION_STRENGTH = 3.0f;

	/** Default interaction strength pedestrian-pedestrian. */
	public static final float DEFAULT_INTERACTION_STRENGTH_PEDESTRIAN_TO_PEDESTRIAN = 3.0f;

	/** Default interaction strength pedestrian-pod. */
	public static final float DEFAULT_INTERACTION_STRENGTH_PEDESTRIAN_TO_POD = 1.29f;

	/** Default interaction strength pedestrian-bike. */
	public static final float DEFAULT_INTERACTION_STRENGTH_PEDESTRIAN_TO_BIKE = 3.0f;

	/** Default interaction strength pod-pod. */
	public static final float DEFAULT_INTERACTION_STRENGTH_POD_TO_POD = 3.0f;

	/** Default interaction strength pod-pedestrian. */
	public static final float DEFAULT_INTERACTION_STRENGTH_POD_TO_PEDESTRIAN = 1.29f;

	/** Default interaction strength pod-bike. */
	public static final float DEFAULT_INTERACTION_STRENGTH_POD_TO_BIKE = 3.0f;

	/** Default interaction strength bike-bike. */
	public static final float DEFAULT_INTERACTION_STRENGTH_BIKE_TO_BIKE = 3.0f;

	/** Default interaction strength bike-bike. */
	public static final float DEFAULT_INTERACTION_STRENGTH_BIKE_TO_POD = 3.0f;

	/** Default interaction strength bike-bike. */
	public static final float DEFAULT_INTERACTION_STRENGTH_BIKE_TO_PEDESTRIAN = 3.0f;

	/** Default interaction range. */
	public static final float DEFAULT_INTERACTION_RANGE = 0.2f;

	/** Default interaction range pedestrian-pedestrian. */
	public static final float DEFAULT_INTERACTION_RANGE_PEDESTRIAN_TO_PEDESTRIAN = 0.2f;

	/** Default interaction range pedestrian-pod. */
	public static final float DEFAULT_INTERACTION_RANGE_PEDESTRIAN_TO_POD = 0.96f;

	/** Default interaction range pedestrian-bike. */
	public static final float DEFAULT_INTERACTION_RANGE_PEDESTRIAN_TO_BIKE = 0.2f;

	/** Default interaction range pod-pod. */
	public static final float DEFAULT_INTERACTION_RANGE_POD_TO_POD = 0.2f;

	/** Default interaction range pod-pedestrian. */
	public static final float DEFAULT_INTERACTION_RANGE_POD_TO_PEDESTRIAN = 0.96f;

	/** Default interaction range pod-bike. */
	public static final float DEFAULT_INTERACTION_RANGE_POD_TO_BIKE = 0.2f;

	/** Default interaction range bike-bike. */
	public static final float DEFAULT_INTERACTION_RANGE_BIKE_TO_BIKE = 0.2f;

	/** Default interaction range bike-pod. */
	public static final float DEFAULT_INTERACTION_RANGE_BIKE_TO_POD = 0.2f;

	/** Default interaction range bike-pedestrian. */
	public static final float DEFAULT_INTERACTION_RANGE_BIKE_TO_PEDESTRIAN = 0.2f;

	/** Default anisotropic constant. */
	public static final float DEFAULT_ANISOTROPIC_CONSTANT = 0.75f;

	/** Default maximum velocity factor. */
	public static final float DEFAULT_MAXIMUM_VELOCITY = 1.3f;

	/**
	 * The maximum range for the social forces influence. The limitation
	 * improves performance while saving computational time. [meters]
	 */
	public static final float DEFAULT_MAX_RANGE_SFM = 20;

	/**
	 * Constructor.
	 */
	private SocialForceDefaultValues() {

	}
}
