package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents;

/**
 * Autonomous vehicle constants.
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
public final class AutonomousVehicleConstraints {
	/**
	 * Constant A used for calculating the repulsive force between autonomous
	 * vehicles according to the Social Force Model.
	 */
	public static final float AV_TO_AV_INTERACTION_STRENGTH_A = 3.0f;

	/**
	 * Constant A used for calculating the repulsive force between autonomous
	 * vehicles and pedestrians according to the Social Force Model.
	 */
	public static final float AV_TO_PEDESTRIAN_INTERACTION_STRENGTH_A = 3.0f;

	/**
	 * Constant A used for calculating the repulsive force between autonomous
	 * vehicles and bicycles according to the Social Force Model.
	 */
	public static final float AV_TO_BICYCLE_INTERACTION_STRENGTH_A = 3.0f;

	/**
	 * Constant B used for calculating the repulsive force between autonoums
	 * vechicles according to the Social Force Model.
	 */
	public static final float AV_TO_AV_INTERACTION_RANGE_B = 0.2f;

	/**
	 * Constant B used for calculating the repulsive force between autonoums
	 * vechicles according to the Social Force Model.
	 */
	public static final float AV_TO_PEDESTRIAN_INTERACTION_RANGE_B = 0.2f;

	/**
	 * Constant B used for calculating the repulsive force between autonoums
	 * vechicles according to the Social Force Model.
	 */
	public static final float AV_TO_BICYCLE_INTERACTION_RANGE_B = 0.2f;

	/**
	 * LAMBDA used for used for calculating the repulsive force between
	 * pedestrians according to the Social Force Model. The property control the
	 * anisotropic propety of the repulsive force.
	 */
	public static final float AV_TO_PEDESTRIAN_ANISOTROPIC_CONSTANT_LAMBDA = 0.75f;

	/**
	 * LAMBDA used for used for calculating the repulsive force between
	 * pedestrians according to the Social Force Model. The property control the
	 * anisotropic propety of the repulsive force.
	 */
	public static final float AV_TO_AV_ANISOTROPIC_CONSTANT_LAMBDA = 0.75f;

	/**
	 * LAMBDA used for used for calculating the repulsive force between
	 * pedestrians according to the Social Force Model. The property control the
	 * anisotropic propety of the repulsive force.
	 */
	public static final float AV_TO_BICYCLE_ANISOTROPIC_CONSTANT_LAMBDA = 0.75f;

	/**
	 * Constant determining the magnitude of the repulsive force from the walls.
	 */
	public static final float WALL_REPULSION_MAGNITUDE_U = 5.0f;

	/** Pedestrian max velocity factor. */
	public static final float MAX_VELOCITY_FACTOR = 1.3f;

	/** Wall repulsion radius. */
	public static final float WALL_REPULSION_RADIUS = 0.6f;

	/** Relaxation coefficient for calcuating the target attractive force. */
	public static final float RELAXATION_TIME = 1.0f;

	/**
	 * The maximum distance for the repulsive forces to have an impact. [meters]
	 */
	public static final float REPULSIVE_FORCE_MAX_DISTANCE = 20;

	/** Autonomous vehicle's force field radius, x axis. */
	public static final float RADIUS = 1.2f;

	/** Max radius. */
	public static final float MAX_RADIUS = 0.75f;

	/** Min radius. */
	public static final float MIN_RADIUS = 0.65f;

	/** Autonomous vehicle length [m]. */
	public static final float LENGTH = 1.93f;

	/** Autonomous vehicle width [m]. */
	public static final float WIDTH = 1.40f;

	/** Vechicle maximum speed in meters / second. */
	public static final float MAXIMUM_SPEED = 5;

	/** Limits the social force and introduces limited steering ability. */
	public static final float MAXIMUM_FORCE = 0.15f;

	/** Typical maximum steering angle in radians. */
	public static final float MAXIMUM_STEERING_ANGLE = 0.575959f;

	/** Vechicle maximum speed in meters / second. */
	public static final float MAX_SPEED = 9;

	/**
	 * Private constructor necessary in utility classes.
	 */
	private AutonomousVehicleConstraints() {

	}
}