package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents;

/**
 * Some static information about pedestrians. It also sets parameters for
 * pedestrian-pedestrian repulsive forces from [Helbing & Molnar Phys. Rev. E
 * 51, 4282 (1995)]
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
public final class PedestrianConstraints {

	/** Average shoulder width [meters]. */
	public static final float SHOULDER_WIDTH = 0.5f;

	/** Height min [meters]. */
	public static final float HEIGHT_MIN = 1.10f;

	/** Height max [meters]. */
	public static final float HEIGHT_MAX = 1.90f;

	/**
	 * Pedestrian walking speed. 1.4 meters/s
	 * 
	 * https://en.wikipedia.org/wiki/Preferred_walking_speed
	 */
	public static final float PEDESTRIAN_WALKING_SPEED = 1.4f;

	/** Pedestrian jogging speed. 3 meters/s */
	public static final float PEDESTRIAN_JOGGING_SPEED = 3;

	/** Pedestrian marathon running speed. 6 meters/s */
	public static final float PEDESTRIAN_MARATHON_SPEED = 6;

	/** Pedestrian high running speed. 9 meters/s */
	public static final float PEDESTRIAN_RUNNING_AT_HIGHEST_SPPED = 9;

	/** Pedestrian force field radius. */
	public static final float RADIUS = 0.3f;

	/**
	 * Constant used for calculating the repulsive force pedestrian to
	 * pedestrian according to the Social Force Model.
	 */
	public static final float PEDESTRIAN_TO_PEDESTRIAN_INTERACTION_STRENGTH_A = 3.0f;

	/**
	 * Constant used for calculating the repulsive force pedestrian to
	 * autonomous vehicle according to the Social Force Model.
	 */
	public static final float PEDESTRIAN_TO_AUTONOMOUSVEHICLE_INTERACTION_STRENGTH_A = 3.0f;

	/**
	 * Constant used for calculating the repulsive force pedestrian to bicycle
	 * according to the Social Force Model.
	 */
	public static final float PEDESTRIAN_TO_BICYCLE_INTERACTION_STRENGTH_A = 3.0f;

	/**
	 * Constant B used for calculating the repulsive force between pedestrians
	 * according to the Social Force Model.
	 */
	public static final float PEDESTRIAN_TO_PEDESTRIAN_INTERACTION_RANGE_B = 0.2f;

	/**
	 * Constant B used for calculating the repulsive force between pedestrians
	 * according to the Social Force Model.
	 */
	public static final float PEDESTRIAN_TO_AUTONOMOUSVEHICLE_INTERACTION_RANGE_B = 0.2f;

	/**
	 * Constant B used for calculating the repulsive force between pedestrians
	 * according to the Social Force Model.
	 */
	public static final float PEDESTRIAN_TO_BICYCLE_INTERACTION_RANGE_B = 0.2f;

	/**
	 * LAMBDA used for used for calculating the repulsive force between
	 * pedestrians according to the Social Force Model. The property control the
	 * anisotropic propety of the repulsive force.
	 */
	public static final float PEDESTRIAN_TO_PEDESTRIAN_ANISOTROPIC_CONSTANT_LAMBDA = 0.75f;

	/**
	 * LAMBDA used for used for calculating the repulsive force between
	 * pedestrians and autonomous agents according to the Social Force Model.
	 * The property control the anisotropic propety of the repulsive force.
	 */
	public static final float PEDESTRIAN_TO_AUTONOMOUSVEHICLE_ANISOTROPIC_CONSTANT_LAMBDA = 0.75f;

	/**
	 * LAMBDA used for used for calculating the repulsive force between
	 * pedestrians and bicycles according to the Social Force Model. The
	 * property control the anisotropic propety of the repulsive force.
	 */
	public static final float PEDESTRIAN_TO_BICYCLE_ANISOTROPIC_CONSTANT_LAMBDA = 0.75f;

	/**
	 * Constant determining the magnitude of the repulsive force from the walls.
	 */
	public static final float WALL_REPULSION_MAGNITUDE_U = 5.0f;

	/** Pedestrian max velocity factor. */
	public static final float MAX_VELOCITY_FACTOR = 1.3f;

	/** Wall repulsion radius. */
	public static final float WALL_REPULSION_RADIUS = 0.3f;

	/** Relaxation coefficient for calcuating the target attractive force. */
	public static final float RELAXATION_TIME = 1.0f;

	/**
	 * The maximum distance for the pedestrian repulsive forces to have an
	 * impact. [meters]
	 */
	public static final float REPULSIVE_PEDESTRIANS_FORCE_MAX_DISTANCE = 20;

	/** Limits the social force and introduces limited steering ability. */
	public static final float MAX_FORCE = 0.05f;

	/**
	 * Gets the pedestrian walking speed per tick length unit. It is used during
	 * calculating speed.
	 * 
	 * @param tickLengthInSeconds
	 *            tick length in seconds
	 * @return how many meters a walking pedestrian can achieve in a tick length
	 */
	public static float getPedestrianWalkingSpeedPerTickLength(final float tickLengthInSeconds) {
		return (tickLengthInSeconds / PEDESTRIAN_WALKING_SPEED);
	}

	/**
	 * Gets the pedestrian running at the highest speed per tick length unit. It
	 * is used during calculating speed.
	 * 
	 * @param tickLengthInSeconds
	 *            tick length in seconds
	 * @return how many meters a walking pedestrian can achieve in a tick length
	 */
	public static float getPedestrianRunningHighestSpeed(final float tickLengthInSeconds) {
		return (tickLengthInSeconds / PEDESTRIAN_RUNNING_AT_HIGHEST_SPPED);
	}

	/**
	 * Private constructor for utility classes.
	 */
	private PedestrianConstraints() {

	}
}