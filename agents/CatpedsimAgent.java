package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.sun.javafx.geom.Vec3f;

import processing.core.PVector;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimCrossing;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimGeometry;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimObstacle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimTransitionZone;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.ShapeSection;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.path.XmlPath;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.path.XmlVertex;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.logiclayer.CatpedsimManager;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.shortestpath.AStarPathFinder;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.shortestpath.NodePath;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.socialforcemodel.Forces;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.socialforcemodel.SocialForceParameters;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.RegexPatterns;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.Trigonometry;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.Utils;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.visual.VisualConstants;
import uk.org.catapult.ts.cav.model.identifier.StringIdentifier;
import uk.org.catapult.ts.cav.model.spatial.Coordinate2D;
import uk.org.catapult.ts.cav.model.spatial.Vector2D;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * A class representing any agent active during the simulation. Ackermann
 * steering geometry, literature about a car steering ability.
 * 
 * https://en.wikipedia.org/wiki/Ackermann_steering_geometry
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
public class CatpedsimAgent implements Cloneable {

	/** Logger for the class. */
	private static final Logger LOGGER = UtilityLogger.getLogger(CatpedsimAgent.class);

	/** The agent's unique identifier. */
	private StringIdentifier uniqueIdentifier;

	/** Agent's publisher id. */
	private String publisherId = null;

	/** Agent name. */
	private String name = "";

	/** Color for agent representation. */
	private Color colorToRepresent = Color.BLACK;

	/** Reached the destination distance. [m] */
	private float reachedTheTargetDistance = 2;

	/** Close to the destination distance. [m] */
	private float closeToTargetDistance = 1;

	/**
	 * Position vector.
	 * 
	 * In geometry, a position or position vector, also known as location vector
	 * or radius vector, is a Euclidean vector that represents the position of a
	 * point P in space in relation to an arbitrary reference origin O.
	 * Reference: https://en.wikipedia.org/wiki/Position_(vector)
	 * 
	 */
	private final PVector positionVector = new PVector(0, 0, 0);

	/** Velocity vector. */
	private final PVector velocity = new PVector(0, 0, 0);

	/** Acceleration vector. */
	private final PVector acceleration = new PVector(0, 0, 0);

	/**
	 * The vehicle desires to move towards the target at maximum speed. The
	 * magnitude of the desired velocity vector is always the maximum speed.
	 */
	private float maximumSpeed;

	/**
	 * Limits the social force. Steering ability can be controlled by limiting
	 * the magnitude of the steering force.
	 */
	private float maxForce = 0;

	/**
	 * Wander = random steering. The goal of wandering is not simply random
	 * motion, but rather o sense of moving in one direction for a while.
	 */
	private Wander wanderValue = new Wander();

	/**
	 * The area occupied by an agent.
	 */
	private float radius = 0;

	/**
	 * Vector from the origin to the agent target area. Agents could have the
	 * same target or individual targets.
	 */
	private PVector targetVector = null;

	/** Intermediate vector to target. */
	private PVector intermediateTargetVector = null;

	/** Real time generated path. */
	private LinkedList<NodePath> astarPath = null;

	/** The list of agents the current agent is transporting. */
	private final LinkedList<StringIdentifier> transportsAgents = new LinkedList<StringIdentifier>();

	/** List of agent behaviours in chronological order. */
	private AgentBehaviour sequenceOfBehaviours = new AgentBehaviour(AgentState.STOP_AND_WAIT);

	/** Agent moves towards an exit. */
	private CatpedsimCrossing agentExit = null;

	/** Agent transition zone. */
	private CatpedsimTransitionZone transitionZone = null;

	/** Agent's current speed meters/second. */
	private float currentSpeed = 0;

	/** Agent heading reflects body position. */
	private Vector2D headingTo = new Vector2D(0, 0);

	/**
	 * The relaxation time scales the force, determining how fast an agent
	 * changes velocity and returns to its desired velocity after having been
	 * walking slower because of obstacles etc. The relaxation time is a model
	 * parameter that in principle can vary for each agent.
	 */
	private float relaxationTime = 1.0f;

	/** Default wall repulsion parameter. */
	private final float defaultWallRepulsionRadius = 0.7f;

	/**
	 * Wall repulsion radius used in the calculation of the Social Force Model,
	 * repulsion from the walls.
	 */
	private float wallRepulsionRadius = defaultWallRepulsionRadius;

	/** Default wall repulsion magnitude. */
	private final float defaultWallRepulsionMagnitude = 10.0f;

	/**
	 * Constant determining the magnitude of the repulsive force from the walls.
	 */
	private float wallRepulsionMagnitude = defaultWallRepulsionMagnitude;

	/** Typical maximum steering angle. */
	private float maximumSteeringAngle = 0;

	/**
	 * Simple constructor.
	 */
	public CatpedsimAgent() {

	}

	/**
	 * Constructor.
	 * 
	 * @param newPublisherId
	 *            publisher id
	 * @param newName
	 *            name of the agent
	 * @param unewUniqueIdentifier
	 *            unique agent identifier
	 * @param agentPosition3D
	 *            agent x coordinate, agent y coordinate, agent z coordinate in
	 *            floating point precision
	 * @param newMaximumSpeed
	 *            maximum speed
	 */
	public CatpedsimAgent(final String newPublisherId, final String newName,
			final StringIdentifier unewUniqueIdentifier, final Vec3f agentPosition3D, final float newMaximumSpeed) {

		setPublisherId(newPublisherId);
		setName(newName);
		setUniqueIdentifier(unewUniqueIdentifier);
		getPositionVector().set(agentPosition3D.x, agentPosition3D.y, agentPosition3D.z);
		setMaximumSpeed(newMaximumSpeed);
	}

	/**
	 * Constructor.
	 * 
	 * @param newPublisherId
	 *            publisher id
	 * @param newName
	 *            name of the agent
	 * @param color
	 *            agent color
	 * @param unewUniqueIdentifier
	 *            unique agent identifier
	 * @param agentPosition3D
	 *            agent x coordinate, agent y coordinate, agent z coordinate in
	 *            floating point precision
	 * @param newMaximumSpeed
	 *            maximum speed
	 */
	public CatpedsimAgent(final String newPublisherId, final String newName, final Color color,
			final StringIdentifier unewUniqueIdentifier, final Vec3f agentPosition3D, final float newMaximumSpeed) {
		this(newPublisherId, newName, unewUniqueIdentifier, agentPosition3D, newMaximumSpeed);
		setColorToRepresent(color);
	}

	/**
	 * Constructor.
	 * 
	 * @param newPublisherId
	 *            publisher id
	 * @param newName
	 *            name of the agent
	 * @param color
	 *            agent color
	 * @param unewUniqueIdentifier
	 *            unique agent identifier
	 * @param agentPosition3D
	 *            agent x coordinate, agent y coordinate, agent z coordinate in
	 *            floating point precision
	 * @param newMaximumSpeed
	 *            maximum speed
	 * @param path
	 *            path list to follow
	 */
	public CatpedsimAgent(final String newPublisherId, final String newName, final Color color,
			final StringIdentifier unewUniqueIdentifier, final Vec3f agentPosition3D, final float newMaximumSpeed,
			final LinkedList<NodePath> path) {
		this(newPublisherId, newName, unewUniqueIdentifier, agentPosition3D, newMaximumSpeed);

		setColorToRepresent(color);
		setPathToFollow(path);
	}

	/**
	 * Constructor.
	 * 
	 * @param newPublisherId
	 *            publisher id
	 * @param agentName
	 *            agent name
	 * @param newUniqueIdentifier
	 *            agent unique identifier
	 * @param agentPosition3D
	 *            agent position in the cartesian space
	 * @param maximumVelocity
	 *            maximum velocity
	 * @param transition
	 *            the agent destination is a transition area
	 * @param color
	 *            agent color
	 * @param preDefinedBehaviour
	 *            a series of pre-defined behaviours
	 */
	public CatpedsimAgent(final String newPublisherId, final String agentName,
			final StringIdentifier newUniqueIdentifier, final Vec3f agentPosition3D, final float maximumVelocity,
			final CatpedsimTransitionZone transition, final Color color, final AgentBehaviour preDefinedBehaviour) {

		this(newPublisherId, agentName, newUniqueIdentifier, agentPosition3D, maximumVelocity);

		setColorToRepresent(color);
		setSequenceOfBehaviours(preDefinedBehaviour);
		setTransitionZone(transition);
	}

	/**
	 * Constructor.
	 * 
	 * @param newPublisherId
	 *            publisher id
	 * @param newName
	 *            agent name
	 * @param color
	 *            agent color for easy identification
	 * @param unewUniqueIdentifier
	 *            agent unique identifier
	 * @param agentPosition3D
	 *            agent position in the cartesian space
	 * @param newMaximumSpeed
	 *            new maximum agent speed
	 * @param preDefinedBehaviour
	 *            a series of pre-defined behaviours
	 */
	public CatpedsimAgent(final String newPublisherId, final String newName, final Color color,
			final StringIdentifier unewUniqueIdentifier, final Vec3f agentPosition3D, final float newMaximumSpeed,
			final AgentBehaviour preDefinedBehaviour) {
		this(newPublisherId, newName, unewUniqueIdentifier, agentPosition3D, newMaximumSpeed);

		setColorToRepresent(color);
		setSequenceOfBehaviours(preDefinedBehaviour);
	}

	/**
	 * Set a new path to follow.
	 * 
	 * @param newPathToFollow
	 *            new path to follow
	 */
	public final void setPathToFollow(final LinkedList<NodePath> newPathToFollow) {
		this.astarPath = newPathToFollow;
	}

	/**
	 * Set a sequence of pre-defined behaviours.
	 * 
	 * @param newSequenceOfBehaviours
	 *            sequence of pre-defined behaviours
	 */
	public final void setSequenceOfBehaviours(final AgentBehaviour newSequenceOfBehaviours) {
		this.sequenceOfBehaviours = newSequenceOfBehaviours;

		if (newSequenceOfBehaviours != null) {
			XmlVertex targetVertex = newSequenceOfBehaviours.getCurrentBehaviour().getLast();
			if (targetVertex != null) {
				PVector exitVector = new PVector(targetVertex.getX(), targetVertex.getY());
				setVectorToAgentsTarget(exitVector);
			}
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param newPublisherId
	 *            publisher id
	 * @param newAgentName
	 *            the name of the agent
	 * @param newUniqueIdentifier
	 *            agent unique identifier
	 * @param agentPosition3D
	 *            agent x coordinate, agent y coordinate, agent z coordinate in
	 *            floating point precision
	 * @param agentMaximumVelocity
	 *            agent maximum velocity
	 * @param exit
	 *            agent exit segment
	 */
	public CatpedsimAgent(final String newPublisherId, final String newAgentName,
			final StringIdentifier newUniqueIdentifier, final Vec3f agentPosition3D, final float agentMaximumVelocity,
			final CatpedsimCrossing exit) {
		this(newPublisherId, newAgentName, newUniqueIdentifier, agentPosition3D, agentMaximumVelocity);
		setAgentExit(exit);
	}

	/**
	 * Get agent exit.
	 * 
	 * @return agent exit line
	 */
	public final CatpedsimCrossing getAgentExit() {
		return agentExit;
	}

	/**
	 * Set Agent exit. Select an exit point on the exit segment.
	 * 
	 * @param exit
	 *            agent exit represented as a segment
	 */
	public final void setAgentExit(final CatpedsimCrossing exit) {
		agentExit = exit;

		if (exit != null) {
			targetVector = PVector.add(exit.getVertexAtIndex(0), exit.getVertexAtIndex(1));
			targetVector.div(2);
		}
	}

	/**
	 * Get the transition zone.
	 * 
	 * @return the transition zone
	 */
	public final CatpedsimTransitionZone getTransitionZone() {
		return transitionZone;
	}

	/**
	 * Set the transition zone.
	 * 
	 * @param newTransitionZone
	 *            the transition zone
	 */
	public final void setTransitionZone(final CatpedsimTransitionZone newTransitionZone) {
		this.transitionZone = newTransitionZone;
	}

	/**
	 * Constrain stering angle.
	 * 
	 * @param timeStep
	 *            tiem step in secodns
	 * @return new velocity
	 */
	@SuppressWarnings("unused")
	private PVector contrainVelocityAngle(final float timeStep) {
		PVector computedVelocity = PVector.add(velocity, PVector.mult(acceleration, timeStep));

		if (maximumSteeringAngle > 0) {
			float theta = computedVelocity.heading() - velocity.heading();

			if (theta != 0 && Math.abs(theta) > getMaximumSteeringAngle()) {
				if (theta > 0) {
					computedVelocity.rotate(getMaximumSteeringAngle() - theta);
				} else {
					computedVelocity.rotate(theta - getMaximumSteeringAngle());
				}
			}
		}
		return computedVelocity;
	}

	/**
	 * Applying Newton's second law.
	 *
	 * @param force
	 *            the social force
	 */
	public final void applyForce(final PVector force) {
		acceleration.add(force);
	}

	/**
	 * Check wall avoidance. If the pressure of the repuslive force generated by
	 * other agents is pushing the current agent to go though a wall, stop the
	 * current agent.
	 * 
	 * 
	 * @param geometry
	 *            simulation geometry
	 * @param nextStepVectorToPosition
	 *            next step vector
	 * @return returns true if the agent is avoiding the walls and false if the
	 *         agent is in the danger of collision
	 */
	@SuppressWarnings("unused")
	private boolean avoidWallsAndBuildIntermediateTarget(final CatpedsimGeometry geometry,
			final PVector nextStepVectorToPosition) {
		boolean avoided = true;

		float distanceToClosestWall = Float.MAX_VALUE;
		ShapeSection closestWallSection = null;

		for (CatpedsimObstacle wall : geometry.getWalls()) {
			for (int indexSection = 0; indexSection < wall.getObstacleSections().length; indexSection++) {
				ShapeSection wallSection = wall.getObstacleSections()[indexSection];

				try {
					if (Trigonometry.checkIfLinesIntersect(positionVector, nextStepVectorToPosition,
							wallSection.getVectorToStartPoint(), wallSection.getVectorToEndPoint())) {
						acceleration.set(0, 0, 0);
						avoided = false;
					}

					if (targetVector != null && Trigonometry.checkIfLinesIntersect(positionVector, targetVector,
							wallSection.getVectorToStartPoint(), wallSection.getVectorToEndPoint())) {
						float distance = Trigonometry.distanceFromPointToSegment(positionVector,
								wallSection.getVectorToStartPoint(), wallSection.getVectorToEndPoint());

						if (distance < distanceToClosestWall) {
							distanceToClosestWall = distance;
							closestWallSection = wallSection;
						}
					}
				} catch (Exception ex) {
					LOGGER.warn("Error occured while checking the agent does not cross a wall or obstacle.", ex);
				}
			}
		}

		if (closestWallSection != null) {
			float distance1 = PVector.dist(positionVector, closestWallSection.getVectorToStartPoint())
					+ PVector.dist(closestWallSection.getVectorToStartPoint(), targetVector);
			float distance2 = PVector.dist(positionVector, closestWallSection.getVectorToEndPoint())
					+ PVector.dist(closestWallSection.getVectorToEndPoint(), targetVector);

			if (distance1 >= distance2) {
				final float positionOffset = 5f;
				PVector offset = PVector.sub(closestWallSection.getVectorToEndPoint(),
						closestWallSection.getVectorToStartPoint());
				offset.normalize();
				offset.mult(positionOffset);

				intermediateTargetVector = closestWallSection.getVectorToEndPoint();
			} else {
				final float positionOffset = 1.5f;
				PVector offset = PVector.sub(closestWallSection.getVectorToStartPoint(),
						closestWallSection.getVectorToEndPoint());
				offset.normalize();
				offset.mult(positionOffset);

				intermediateTargetVector = closestWallSection.getVectorToStartPoint();
			}
		}

		return avoided;
	}

	/**
	 * Avoid walls.
	 * 
	 * @param nextStepVectorToPosition
	 *            next step vector
	 * @param geometry
	 *            map geometry
	 * @return returns true if the agent is avoiding the walls and false if the
	 *         agent is in the danger of collision
	 */
	private boolean avoidWalls(final CatpedsimGeometry geometry, final PVector nextStepVectorToPosition) {
		boolean avoided = true;

		for (CatpedsimObstacle wall : geometry.getWalls()) {
			for (int indexSection = 0; indexSection < wall.getObstacleSections().length; indexSection++) {
				ShapeSection wallSection = wall.getObstacleSections()[indexSection];

				try {
					if (Trigonometry.checkIfLinesIntersect(positionVector, nextStepVectorToPosition,
							wallSection.getVectorToStartPoint(), wallSection.getVectorToEndPoint())) {
						return false;
					}
				} catch (Exception ex) {
					LOGGER.warn("Error occured while checking an agent does not cross a wall.", ex);
				}
			}
		}

		return avoided;
	}

	/**
	 * Avoid obstacles.
	 * 
	 * @param nextStepVectorToPosition
	 *            next step vector
	 * @param geometry
	 *            map geometry
	 * @return returns true if the agent is avoiding the walls and false if the
	 *         agent is in the danger of collision
	 */
	private boolean avoidObstacles(final CatpedsimGeometry geometry, final PVector nextStepVectorToPosition) {
		boolean avoided = true;

		for (CatpedsimObstacle obstacle : geometry.getObstacles()) {
			for (int indexSection = 0; indexSection < obstacle.getObstacleSections().length; indexSection++) {
				ShapeSection obstacleSection = obstacle.getObstacleSections()[indexSection];

				try {
					if (Trigonometry.checkIfLinesIntersect(positionVector, nextStepVectorToPosition,
							obstacleSection.getVectorToStartPoint(), obstacleSection.getVectorToEndPoint())) {
						return false;
					}
				} catch (Exception ex) {
					LOGGER.warn("Error occured while checking the agent does not cross an obstacle.", ex);
				}
			}
		}

		return avoided;
	}

	/**
	 * Check obstacles avoidance.
	 * 
	 * @param geometry
	 *            simulation geometry
	 * @param nextStepVectorToPosition
	 *            next position in the list of intermediate positions
	 * @return returns true if the agent is avoiding the obstacles and false if
	 *         the agent is in the danger of collision
	 */
	@SuppressWarnings("unused")
	private boolean avoidObstaclesAndBuildIntermediateTarget(final CatpedsimGeometry geometry,
			final PVector nextStepVectorToPosition) {
		boolean avoided = true;

		float distanceToClosestToObstacle = Float.MAX_VALUE;
		ShapeSection closestObstacleSection = null;

		for (CatpedsimObstacle obstacle : geometry.getObstacles()) {
			for (int indexSection = 0; indexSection < obstacle.getObstacleSections().length; indexSection++) {
				ShapeSection obstacleSection = obstacle.getObstacleSections()[indexSection];

				try {
					if (Trigonometry.checkIfLinesIntersect(positionVector, nextStepVectorToPosition,
							obstacleSection.getVectorToStartPoint(), obstacleSection.getVectorToEndPoint())) {
						acceleration.set(0, 0, 0);
						avoided = false;
					}

					if (targetVector != null && Trigonometry.checkIfLinesIntersect(positionVector, targetVector,
							obstacleSection.getVectorToStartPoint(), obstacleSection.getVectorToEndPoint())) {

						float distance = Trigonometry.distanceFromPointToSegment(positionVector,
								obstacleSection.getVectorToStartPoint(), obstacleSection.getVectorToEndPoint());

						if (distance < distanceToClosestToObstacle) {
							distanceToClosestToObstacle = distance;
							closestObstacleSection = obstacleSection;
						}
					}
				} catch (Exception ex) {
					LOGGER.warn("Error occured while checking the agent is avoiding obstacles.", ex);
				}
			}
		}

		if (closestObstacleSection != null) {
			float distance1 = PVector.dist(positionVector, closestObstacleSection.getVectorToStartPoint())
					+ PVector.dist(closestObstacleSection.getVectorToStartPoint(), targetVector);
			float distance2 = PVector.dist(positionVector, closestObstacleSection.getVectorToEndPoint())
					+ PVector.dist(closestObstacleSection.getVectorToEndPoint(), targetVector);

			if (distance1 >= distance2) {
				intermediateTargetVector = closestObstacleSection.getVectorToEndPoint();
			} else {
				intermediateTargetVector = closestObstacleSection.getVectorToStartPoint();
			}
		}

		return avoided;
	}

	/**
	 * Compute current agent speed.
	 * 
	 * @param tickLengthSeconds
	 *            tick length in seconds
	 * @return current speed
	 */
	private float computeCurrentSpeed(final float tickLengthSeconds) {
		float speed = 0;

		try {
			speed = velocity.mag() / tickLengthSeconds;
		} catch (Exception ex) {
		}

		return speed;
	}

	/**
	 * Euler's integration method adaptation. It checks the agents don't cross
	 * walls when the crowd pressure is too high.
	 * 
	 * Choosing a suitable time step size is important when applying Eulers
	 * method. There are two main trade-offs in selecting this step size:
	 * precision of the approximation and numerical rounding errors. If the step
	 * size is too large, Eulers method will yield an approximation that differ
	 * too much from the actual values of the equations. In our case this would
	 * result in pedestrians changing direction and velocity too slowly.
	 * However, lowering the time step size will, apart from resulting in longer
	 * calculation times, potentially increase the error introduced by rounding
	 * errors. Rounding errors occur because numerical calculations have a
	 * finite precision and so results have to be truncated. The more
	 * calculations occur, the larger this error becomes. This means that
	 * setting the time step too low will result in too many errors from this
	 * source.
	 * 
	 * In order to increase computational performance the chosen timeStep is 1
	 * and the speed is limited to the maximum value.
	 * 
	 * 
	 * @param geometry
	 *            simulation geometry consisting of walls, buildings and
	 *            entrances/exits
	 * @param worldBoundary
	 *            world boundary
	 * @param timeStep
	 *            tick length in seconds
	 */
	public final void moveAgent(final CatpedsimGeometry geometry, final Point2D worldBoundary, final float timeStep) {
		if (maxForce != 0) {
			acceleration.limit(maxForce);
		}
		velocity.add(PVector.mult(acceleration, timeStep));
		velocity.limit(maximumSpeed);

		PVector nextStepVectorToPosition = PVector.add(positionVector, velocity);
		if (!avoidWalls(geometry, nextStepVectorToPosition) || !avoidObstacles(geometry, nextStepVectorToPosition)) {
			acceleration.set(0, 0, 0);
			return;
		}

		positionVector.add(PVector.mult(velocity, timeStep));
		computeHeadingToBasedOnVelocity();
		currentSpeed = computeCurrentSpeed(timeStep);
		acceleration.set(0, 0, 0);
	}

	/**
	 * An agent has a goal (or goals) and can select an action (or a combination
	 * of actions) based on that goal. The agent takes a look at its environment
	 * and calculates an action based on a desire to reach a target.
	 * 
	 * @param astarPathFinder
	 *            AStarPathFinder object
	 * @param geometry
	 *            geometry object
	 * @return true if it arrived at the destination
	 * @throws Exception
	 *             on error
	 */
	public final boolean computeAttractiveForceToTarget(final AStarPathFinder astarPathFinder,
			final CatpedsimGeometry geometry) throws Exception {
		boolean arrivedAtDestination = false;

		final float factorForFinalTarget = 10;
		final float factorForIntermediateTarget = 1.6f;

		reachedTheTargetDistance = factorForFinalTarget * getRadius();
		closeToTargetDistance = factorForIntermediateTarget * getRadius();

		if (targetVector != null && PVector.dist(targetVector, positionVector) <= reachedTheTargetDistance) {
			return true;
		}

		if (Objects.isNull(targetVector)) {
			intermediateTargetVector = createRandomTarget();
		} else {
			intermediateTargetVector = targetVector;
		}

		if (!Objects.isNull(sequenceOfBehaviours) && !sequenceOfBehaviours.isEmpty()) {
			XmlPath pathSection = sequenceOfBehaviours.getCurrentBehaviour();
			XmlVertex nextTarget = pathSection.getFirst();

			if (nextTarget != null) {
				float distanceFromTheIntermediateTarget = PVector.dist(positionVector,
						new PVector(nextTarget.getX(), nextTarget.getY()));

				if (distanceFromTheIntermediateTarget <= closeToTargetDistance) {
					if (pathSection.size() == 1) {
						pathSection.clear();
						return true;
					} else {
						pathSection.removeFirst();
						nextTarget = pathSection.getFirst();
					}
				}

				intermediateTargetVector = new PVector(nextTarget.getX(), nextTarget.getY());
			} else {
				return true;
			}
		} else if (!Objects.isNull(astarPath) && !astarPath.isEmpty()) {
			if (PVector.dist(positionVector,
					new PVector(astarPath.getLast().getX(), astarPath.getLast().getY())) <= 1) {
				astarPath.removeLast();
			}

			intermediateTargetVector = new PVector(astarPath.getLast().getX(), astarPath.getLast().getY());
		}

		PVector finalForce = new PVector(0, 0, 0);
		finalForce.add(Forces.computeTargetAttraction(this, intermediateTargetVector));
		applyForce(finalForce);
		intermediateTargetVector = null;

		return arrivedAtDestination;
	}

	/**
	 * Simple form of path finding.
	 * 
	 * @param geometry
	 *            object holding the simulation geometry
	 * @return path finding
	 */
	@SuppressWarnings("unused")
	private PVector buildIntermediateTarget(final CatpedsimGeometry geometry) {
		PVector buildATarget = null;

		ArrayList<CatpedsimObstacle> allObstacles = new ArrayList<CatpedsimObstacle>(geometry.getObstacles());
		allObstacles.addAll(geometry.getWalls());

		float distanceToClosestObstacle = Float.MAX_VALUE;
		ShapeSection closestObstacleSection = null;

		for (CatpedsimObstacle obstacle : allObstacles) {
			for (int indexSection = 0; indexSection < obstacle.getObstacleSections().length; indexSection++) {
				ShapeSection obstacleSection = obstacle.getObstacleSections()[indexSection];

				try {
					if (targetVector != null && Trigonometry.checkIfLinesIntersect(positionVector, targetVector,
							obstacleSection.getVectorToStartPoint(), obstacleSection.getVectorToEndPoint())) {

						float distance = Trigonometry.distanceFromPointToSegment(positionVector,
								obstacleSection.getVectorToStartPoint(), obstacleSection.getVectorToEndPoint());

						if (distance < distanceToClosestObstacle) {
							distanceToClosestObstacle = distance;
							closestObstacleSection = obstacleSection;
						}
					}
				} catch (Exception ex) {
					LOGGER.warn("Error occured while checking the agent is avoiding obstacles.", ex);
				}
			}
		}

		if (closestObstacleSection != null) {
			float distance1 = PVector.dist(positionVector, closestObstacleSection.getVectorToStartPoint());
			float distance2 = PVector.dist(positionVector, closestObstacleSection.getVectorToEndPoint());

			if (distance1 >= distance2) {
				buildATarget = closestObstacleSection.getVectorToEndPoint();
			} else {
				buildATarget = closestObstacleSection.getVectorToStartPoint();
			}
		}
		return buildATarget;
	}

	/**
	 * Compute the repulsive social force acting between agents.
	 * 
	 * @param agents
	 *            the list of agents
	 * @param socialForceParameters
	 *            the particular social force model acting on agent-agents
	 * @throws Exception
	 *             on error
	 */
	public final void computeRepulsiveForcesBetweenAgents(final List<CatpedsimAgent> agents,
			final SocialForceParameters socialForceParameters) throws Exception {
		PVector finalForce = new PVector(0, 0, 0);
		finalForce.add(Forces.computeAgentToAgentsRepulsion(this, agents, socialForceParameters));
		applyForce(finalForce);
	}

	/**
	 * Compute the repulsive forces between the current agent and
	 * walls/obstacles.
	 * 
	 * @param geometry
	 *            the world geometry
	 */
	public final void computeRepulsiveForcesBetweenAgentAndWallsObstacles(final CatpedsimGeometry geometry) {
		PVector finalForce = new PVector(0, 0, 0);
		finalForce.add(Forces.computeWallsAndObstaclesRepulsion(this, geometry.getWalls(), geometry.getObstacles()));
		applyForce(finalForce);
	}

	/**
	 * Get the unique identifier.
	 * 
	 * @return the unique identifier
	 */
	public final StringIdentifier getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	/**
	 * Set the unique identifier.
	 * 
	 * @param newUniqueIdentifier
	 *            unique identifier
	 */
	public final void setUniqueIdentifier(final StringIdentifier newUniqueIdentifier) {
		this.uniqueIdentifier = newUniqueIdentifier;
	}

	/**
	 * Sets publisher id.
	 * 
	 * @return publisher id
	 */
	public final String getPublisherId() {
		return publisherId;
	}

	/**
	 * Gets publisher id.
	 * 
	 * @param newPublisherId
	 *            publisher id
	 */
	public final void setPublisherId(final String newPublisherId) {
		this.publisherId = newPublisherId;
	}

	/**
	 * Gets the agent's name.
	 * 
	 * @return agent's name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the agent's name.
	 * 
	 * @param newName
	 *            agent's name
	 */
	public final void setName(final String newName) {
		this.name = newName;
	}

	/**
	 * Gets the agent's position vector.
	 * 
	 * @return position vector
	 */
	public final PVector getPositionVector() {
		return positionVector;
	}

	/**
	 * Gets current 2D position.
	 * 
	 * @return pedestrian position as Coordinate2D
	 */
	public final Coordinate2D getCurrentPosition() {
		return new Coordinate2D(positionVector.x, positionVector.y);
	}

	/**
	 * Sets agent's position vector.
	 * 
	 * @param newPositionVector
	 *            set position vector
	 */
	public final void setPositionVector(final PVector newPositionVector) {
		positionVector.set(newPositionVector);
	}

	/**
	 * Gets the agent's velocity.
	 * 
	 * @return velocity agent's velocity
	 */
	public final PVector getVelocity() {
		return velocity;
	}

	/**
	 * Sets the agent's velocity.
	 * 
	 * @param newVelocity
	 *            the agent's velocity
	 */
	public final void setVelocity(final PVector newVelocity) {
		velocity.set(newVelocity);
	}

	/**
	 * Gets the agent's acceleration.
	 * 
	 * @return agent's acceleration
	 */
	public final PVector getAcceleration() {
		return acceleration;
	}

	/**
	 * Sets the agent's acceleration.
	 * 
	 * @param newAcceleration
	 *            agent's acceleration
	 */
	public final void setAcceleration(final PVector newAcceleration) {
		acceleration.set(newAcceleration);
	}

	/**
	 * Gets the max speed.
	 * 
	 * @return agent's max speed
	 */
	public final float getMaximumSpeed() {
		return maximumSpeed;
	}

	/**
	 * Sets the max speed.
	 * 
	 * @param newMaxSpeed
	 *            agent's max speed
	 */
	public final void setMaximumSpeed(final float newMaxSpeed) {
		this.maximumSpeed = newMaxSpeed;
	}

	/**
	 * Gets the radius from the circle of potential movement.
	 * 
	 * @return circle's radius
	 */
	public final float getRadius() {
		return radius;
	}

	/**
	 * Create random agent target.
	 * 
	 * @return vector to random target
	 */
	public final PVector createRandomTarget() {
		float wandertheta = Utils.random(-wanderValue.getChangeDirection(), wanderValue.getChangeDirection());

		PVector circleloc = getVelocity().copy();
		circleloc.normalize();
		circleloc.mult(wanderValue.getWanderDistanceFromCurrentLocation());
		circleloc.add(getPositionVector());

		PVector circleOffSet = new PVector((float) (wanderValue.getWanderRadius() * Math.cos(wandertheta)),
				(float) (wanderValue.getWanderRadius() * Math.sin(wandertheta)));
		return PVector.add(circleloc, circleOffSet);
	}

	/**
	 * Sets the radius from the circle of potential movement.
	 * 
	 * @param newCircleRadius
	 *            circle's radius
	 */
	public final void setRadius(final float newCircleRadius) {
		this.radius = newCircleRadius;
	}

	/**
	 * Get current agent speed.
	 * 
	 * @return current speed in meters /second
	 */
	public final float getCurrentSpeed() {
		return currentSpeed;
	}

	/**
	 * Set current agent speed.
	 * 
	 * @param newCurrentSpeed
	 *            current speed in m/s
	 */
	public final void setCurrentSpeed(final float newCurrentSpeed) {
		this.currentSpeed = newCurrentSpeed;
	}

	/**
	 * Set current agent's target destination.
	 * 
	 * @param currentHeading
	 *            current heading
	 */
	public final void setCurrentHeading(final Vector2D currentHeading) {
		headingTo = currentHeading;
		velocity.set((float) currentHeading.getX(), (float) currentHeading.getY(), 0);
	}

	/**
	 * Calculate headingTo based on velocity.
	 */
	private void computeHeadingToBasedOnVelocity() {
		if (velocity.x != 0 || velocity.y != 0) {
			try {
				PVector unitVelocity = velocity.copy();
				unitVelocity.normalize();
				headingTo = new Vector2D(unitVelocity.x, unitVelocity.y);
			} catch (Exception ex) {
				LOGGER.warn("Error occured while calculating the agent's heading.", ex);
			}
		}
	}

	/**
	 * Get current agent's heading or the orientation.
	 * 
	 * @return agent heading
	 */
	public final Vector2D getCurrentHeading() {
		return headingTo;
	}

	/**
	 * Get the vector to target.
	 * 
	 * @return vector from the origin to the target point
	 */
	public final PVector getVectorToTarget() {
		return targetVector;
	}

	/**
	 * Set the vector to target.
	 * 
	 * @param vectorToAgentsTarget
	 *            vector from the origin to the target point
	 */
	public final void setVectorToAgentsTarget(final PVector vectorToAgentsTarget) {
		this.targetVector = vectorToAgentsTarget;
	}

	/**
	 * Get relaxation time used to calculate the attractive force to the target.
	 * 
	 * @return relaxation time
	 */
	public final float getRelaxationTime() {
		return relaxationTime;
	}

	/**
	 * Set relaxation time used to calculate the attractive force to the target.
	 * 
	 * @param newRelaxationTime
	 *            new relaxation time value
	 */
	public final void setRelaxationTime(final float newRelaxationTime) {
		relaxationTime = newRelaxationTime;
	}

	/**
	 * Get repulsion to walls radius.
	 * 
	 * @return repulsion to walls radius
	 */
	public final float getWallRepulsionRadius() {
		return wallRepulsionRadius;
	}

	/**
	 * Set the repulsion to walls radius.
	 * 
	 * @param newWallRepulsionRadius
	 *            new repulsion to walls radius value
	 */
	public final void setWallRepulsionRadius(final float newWallRepulsionRadius) {
		this.wallRepulsionRadius = newWallRepulsionRadius;
	}

	/**
	 * Get repulsion to wall magnitude.
	 * 
	 * @return wall/obstacle repulsion magnitude
	 */
	public final float getWallRepulsionMagnitude() {
		return wallRepulsionMagnitude;
	}

	/**
	 * Get agent color for representation purposes.
	 * 
	 * @return color
	 */
	public final Color getColorToRepresent() {
		return colorToRepresent;
	}

	/**
	 * Set agent color for representation purposes.
	 * 
	 * @param newColorToRepresent
	 *            new agent color
	 */
	public final void setColorToRepresent(final Color newColorToRepresent) {
		this.colorToRepresent = newColorToRepresent;
	}

	/**
	 * Set wall repulsion magnitude.
	 * 
	 * @param newWallRepulsionMagnitude
	 *            wall repulsion magnitude
	 */
	public final void setWallRepulsionMagnitude(final float newWallRepulsionMagnitude) {
		this.wallRepulsionMagnitude = newWallRepulsionMagnitude;
	}

	/**
	 * Set the maximum force.
	 * 
	 * @param newMaxForce
	 *            maximum force acting on agents
	 */
	public final void setMaxForce(final float newMaxForce) {
		this.maxForce = newMaxForce;
	}

	/**
	 * Calcuate the typical maximum steering angle.
	 * 
	 * @param wheelbase
	 *            wheelbase value
	 * @param turningCircle
	 *            turning circle as specified by the manufacturers
	 * @param carWidth
	 *            car width
	 * @return maximum steering angle
	 */
	public final float calculateMaximumSteeringAngle(final float wheelbase, final float turningCircle,
			final float carWidth) {
		return (float) Math.atan(wheelbase / (turningCircle - carWidth));
	}

	/**
	 * Get the maximum steering angle.
	 * 
	 * @return maximum steering angle
	 */
	public final float getMaximumSteeringAngle() {
		return maximumSteeringAngle;
	}

	/**
	 * Set the maximum steering angle.
	 * 
	 * @param newMaximumSteeringAngle
	 *            maximum steering angle
	 */
	public final void setMaximumSteeringAngle(final float newMaximumSteeringAngle) {
		this.maximumSteeringAngle = newMaximumSteeringAngle;
	}

	/**
	 * Get the path an agent has to follow.
	 * 
	 * @return path to follow
	 */
	public final List<NodePath> getPathToFollow() {
		return astarPath;
	}

	/**
	 * Wander = random steering. The goal of wandering is not simply random
	 * motion, but rather o sense of moving in one direction for a while.
	 */
	private class Wander {
		/** Wander radius. */
		private float wanderRadius = 2.0f;

		/** Wander distance from the current location. */
		private float wanderDistanceFromCurrentLocation = 2.0f;

		/** Change heading direction. */
		private float changeDirection = VisualConstants.CHANGE_DIRECTION_ANGLE_RADIANS;

		/**
		 * Get wander radius.
		 * 
		 * @return wander radius
		 */
		public float getWanderRadius() {
			return wanderRadius;
		}

		/**
		 * Create random agent target.
		 * 
		 * @return vector to random target
		 */
		@SuppressWarnings("unused")
		public final PVector wander() {
			float wandertheta = Utils.random(-1f, 1f);

			PVector circleloc = getVelocity().copy();
			circleloc.normalize();
			circleloc.mult(wanderValue.getWanderDistanceFromCurrentLocation());
			circleloc.add(getPositionVector());

			PVector circleOffSet = new PVector((float) (wanderValue.getWanderRadius() * Math.cos(wandertheta)),
					(float) (wanderValue.getWanderRadius() * Math.sin(wandertheta)));
			return PVector.add(circleloc, circleOffSet);
		}

		/**
		 * Set wander radius.
		 * 
		 * @param newWanderRadius
		 *            wander radius
		 */
		@SuppressWarnings("unused")
		public void setWanderRadius(final float newWanderRadius) {
			this.wanderRadius = newWanderRadius;
		}

		/**
		 * Get wander distance from the current location.
		 * 
		 * @return wander distance from the current location
		 */
		public float getWanderDistanceFromCurrentLocation() {
			return wanderDistanceFromCurrentLocation;
		}

		/**
		 * Set wander distance from the current location.
		 * 
		 * @param newWanderDistanceFromCurrentLocation
		 *            wander distance from the current location
		 */
		@SuppressWarnings("unused")
		public void setWanderDistanceFromCurrentLocation(final float newWanderDistanceFromCurrentLocation) {
			this.wanderDistanceFromCurrentLocation = newWanderDistanceFromCurrentLocation;
		}

		/**
		 * Get change direction angle.
		 * 
		 * @return the angle reflecting the cange in direction
		 */
		public float getChangeDirection() {
			return changeDirection;
		}

		/**
		 * Set the change of direction.
		 * 
		 * @param changeDirectionNew
		 *            change direction
		 */
		@SuppressWarnings("unused")
		public void setChangeDirection(final float changeDirectionNew) {
			this.changeDirection = changeDirectionNew;
		}

	}

	/**
	 * Checks if the agent is stopped.
	 * 
	 * @return true if the agent is stopped
	 */
	public final boolean isStopped() {
		boolean stopped = false;

		if (sequenceOfBehaviours != null) {
			XmlPath path = sequenceOfBehaviours.getCurrentBehaviour();

			if (path != null && path.getAgentState().equals(AgentState.STOP_AND_WAIT)) {
				stopped = true;
			}
		}

		return stopped;
	}

	/**
	 * Remove all the pre-defined bahaviours.
	 */
	public final void removeAllBehaviours() {
		if (sequenceOfBehaviours != null) {
			sequenceOfBehaviours.clear();
		}
	}

	/**
	 * Get the sequence of predefined behaviours for the current agent.
	 * 
	 * @return the sequence of predefined behaviours for the current agent
	 */
	public final AgentBehaviour getSequenceOfBehaviours() {
		return sequenceOfBehaviours;
	}

	/**
	 * Add agent in the list of transported agents.
	 * 
	 * @param newUniqueIdentifier
	 *            agent in the list of transported agents
	 */
	public final void addAgentInTransportedList(final StringIdentifier newUniqueIdentifier) {
		transportsAgents.add(newUniqueIdentifier);
	}

	/**
	 * Get the list of agent the current agent is transporting.
	 * 
	 * @return the list of agent the current agent is transporting
	 */
	public final LinkedList<StringIdentifier> getTransportsAgents() {
		return transportsAgents;
	}

	/**
	 * Check if the agent is transported by other agents.
	 * 
	 * @return true if the agent is transported by other agents
	 */
	public final boolean isTransported() {
		boolean transported = false;

		if (sequenceOfBehaviours != null) {
			XmlPath path = sequenceOfBehaviours.getCurrentBehaviour();

			if (path != null && path.getAgentState().equals(AgentState.TRANSPORTED_BY_ANOTHER_AGENT)) {
				transported = true;
			}
		}

		return transported;
	}

	/**
	 * Check if the agent is active (moved by the current simulator) or not
	 * active (move by external simulators).
	 * 
	 * @return true if the agent is move by external simulators, false otherwise
	 */
	public final boolean isNotActive() {
		boolean notActive = false;

		if (sequenceOfBehaviours != null) {
			XmlPath path = sequenceOfBehaviours.getCurrentBehaviour();

			if (path != null && path.getAgentState().equals(AgentState.NOT_ACTIVE)) {
				notActive = true;
			}
		}

		return notActive;
	}

	/**
	 * Check if the agent is moving autonomoulsy.
	 * 
	 * @return true if the agent is transported by other agents
	 */
	public final boolean isMovingAutonomously() {
		boolean moving = false;

		if (sequenceOfBehaviours != null) {
			XmlPath path = sequenceOfBehaviours.getCurrentBehaviour();

			if (path != null && path.getAgentState().equals(AgentState.MOVE_AUTONOMOUSLY_WITHOUT_CONSTRAINTS)) {
				moving = true;
			}
		}

		return moving;
	}

	/**
	 * Check if the agent is transported by other agents.
	 * 
	 * @return true if the agent is transported by other agents
	 */
	public final boolean isMovingToPickUpPoint() {
		boolean movingToPickUpPoint = false;

		if (sequenceOfBehaviours != null) {
			XmlPath path = sequenceOfBehaviours.getCurrentBehaviour();

			if (path != null && path.getAgentState().equals(AgentState.MOVE_TO_PICK_UP_POINT)) {
				movingToPickUpPoint = true;
			}
		}

		return movingToPickUpPoint;
	}

	/**
	 * Check if the agent is transporting other agents.
	 * 
	 * @return true if the agent is transporting other agents
	 */
	public final boolean isTransporting() {
		boolean transporting = false;

		if (sequenceOfBehaviours != null) {
			XmlPath path = sequenceOfBehaviours.getCurrentBehaviour();

			if (path != null && path.getAgentState().equals(AgentState.TRANSPORT_OTHER_AGENTS)) {
				transporting = true;
			}
		}

		return transporting;
	}

	/**
	 * Copy the position vector to the local position vector.
	 * 
	 * @param positionVector2
	 *            new position vector to set
	 */
	public final void copyPositionVector(final PVector positionVector2) {
		positionVector.set(positionVector2);
	}

	/**
	 * Simplify and return agent name.
	 * 
	 * @param nameToParse
	 *            name to parse
	 * @return agent simple name
	 */
	public static String extractSimpleName(final String nameToParse) {
		String splitName = nameToParse;

		if (splitName.contains(RegexPatterns.FULL_STOP)) {
			String[] splits = splitName.split("\\" + RegexPatterns.FULL_STOP);
			splitName = splits[0];
		}

		return splitName;
	}

	@Override
	public final String toString() {
		StringBuilder agentInformation = new StringBuilder();

		agentInformation.append("Name: " + extractSimpleName(getName()) + "\n");

		AgentBehaviour behaviour = getSequenceOfBehaviours();
		if (behaviour != null) {

			if (isMovingToPickUpPoint()) {
				agentInformation.append("Moving to pick up point");
			} else if (isMovingAutonomously()) {
				agentInformation.append("Moving autonomously");
			} else if (isTransported()) {
				agentInformation.append("Transported ");
			} else if (isStopped()) {
				agentInformation.append("Stopped ");
			} else if (isTransporting()) {
				agentInformation.append("Transporting: ");

				LinkedList<StringIdentifier> transporting = getTransportsAgents();
				for (StringIdentifier identifier : transporting) {
					agentInformation
							.append(" " + agentInformation.append(extractSimpleName(identifier.toString())) + " ");
				}
			} else {
				agentInformation.append("Agent state is not defined");
			}

			agentInformation.append("\n");

		}
		agentInformation.append("Publisher Id: " + getPublisherId() + "\n");
		agentInformation.append("Unique Id: " + getUniqueIdentifier() + "\n");
		agentInformation
				.append("Location vector (x,y): " + getPositionVector().x + ", " + getPositionVector().y + "\n");
		agentInformation.append("Speed: " + this.getCurrentSpeed() + " meters/second \n");
		agentInformation
				.append("Heading to (x,y): " + getCurrentHeading().getX() + ", " + getCurrentHeading().getY() + "\n");

		return agentInformation.toString();
	}

	/**
	 * Change agent state.
	 * 
	 * @param newState
	 *            new agent state
	 */
	public final void setAgentState(final AgentState newState) {
		if (sequenceOfBehaviours != null) {
			sequenceOfBehaviours.setCurrentState(newState);
		}
	}

	/**
	 * Clone the current CatpedsimAgent object.
	 * 
	 * @return a new agent object
	 * @throws CloneNotSupportedException
	 */
	@Override
	public final CatpedsimAgent clone() throws CloneNotSupportedException {
		super.clone();
		CatpedsimAgent newAgent = new CatpedsimAgent();
		newAgent.setAcceleration(acceleration);
		newAgent.setAgentExit(agentExit);
		newAgent.setColorToRepresent(colorToRepresent);
		newAgent.setCurrentHeading(getCurrentHeading());
		newAgent.setCurrentSpeed(getCurrentSpeed());
		newAgent.setMaxForce(maxForce);
		newAgent.setMaximumSpeed(maximumSpeed);
		newAgent.setMaximumSteeringAngle(maximumSteeringAngle);
		newAgent.setName(name);
		newAgent.setPathToFollow(astarPath);
		newAgent.setPositionVector(positionVector);
		newAgent.setPublisherId(publisherId);
		newAgent.setRadius(radius);
		newAgent.setRelaxationTime(relaxationTime);
		newAgent.setSequenceOfBehaviours(sequenceOfBehaviours);
		newAgent.setTransitionZone(transitionZone);
		newAgent.setVectorToAgentsTarget(targetVector);
		newAgent.setVelocity(velocity);
		newAgent.setWallRepulsionRadius(wallRepulsionRadius);

		newAgent.setUniqueIdentifier(new StringIdentifier(CatpedsimManager.generateUniqueName(
				CatpedsimAgent.class.getSimpleName(), getPublisherId(), System.currentTimeMillis())));
		return newAgent;
	}
}