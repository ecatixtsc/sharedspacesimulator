package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents;

/**
 * Static information about bicycle configurations.
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
public final class BicycleConstraints {
	/** Autonomous vehicle's force field radius, x axis. */
	public static final float RADIUS = 0.6f;

	/** Max radius. */
	public static final float MAX_RADIUS = 0.75f;

	/** Min radius. */
	public static final float MIN_RADIUS = 0.65f;

	/** Autonomous vehicle length. */
	public static final float LENGTH = 0.72f;

	/** Autonomous vehicle width. */
	public static final float WIDTH = 1.5f;

	/** Vechicle maximum speed in meters / second. */
	public static final float MAX_SPEED = 9;

	/** Limits the social force and introduces limited steering ability. */
	public static final float MAX_FORCE = 0.15f;

	/** Typical maximum steering angle in radians. */
	public static final float MAXIMUM_STEERING_ANGLE = 0.575959f;

	/** Relaxation coefficient for calcuating the target attractive force. */
	public static final float RELAXATION_TIME = 1.0f;

	/**
	 * Constant A used for calculating the repulsive force between pedestrians
	 * according to the Social Force Model.
	 */
	public static final float BICYCLES_INTERACTION_STRENGTH_A = 3.0f;

	/**
	 * Constant B used for calculating the repulsive force between pedestrians
	 * according to the Social Force Model.
	 */
	public static final float BICYCLES_INTERACTION_RANGE_B = 0.2f;

	/**
	 * LAMBDA used for used for calculating the repulsive force between
	 * pedestrians according to the Social Force Model.
	 */
	public static final float ANISOTROPIC_CONSTANT_LAMBDA = 1.0f;

	/**
	 * Constant determining the magnitude of the repulsive force from the walls.
	 */
	public static final float WALL_REPULSION_MAGNITUDE_U = 5.0f;

	/** Pedestrian max velocity factor. */
	public static final float MAX_VELOCITY_FACTOR = 1.3f;

	/** Wall repulsion radius. */
	public static final float WALL_REPULSION_RADIUS = 0.7f;

	/**
	 * Relaxation time [s]. The inverse of the relaxation time gives the bicycle
	 * the stering behaviour. A bicycle can steer better than a vehicle but less
	 * than a pedestrian.
	 */
	public static final float RELAXATION_TIME_TAU = 1f;

	/** Social Force Model range of influence. */
	public static final float REPULSIVE_FORCE_MAX_DISTANCE = 20;

	/**
	 * Private constructor necessary in utility classes.
	 */
	private BicycleConstraints() {

	}

	/**
	 * Get the relaxation time.
	 * 
	 * @return relaxation time
	 */
	public static float getRelaxationTime() {
		return RELAXATION_TIME_TAU;
	}
}