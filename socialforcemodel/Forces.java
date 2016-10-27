package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.socialforcemodel;

import java.util.LinkedList;
import java.util.List;

import processing.core.PVector;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAgent;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimObstacle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.ShapeSection;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.Trigonometry;

/**
 * The forces of interaction between agents, autonomous cars, bicycles and
 * walls/buildings according to the Social Force Model.
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
public final class Forces {

	/**
	 * Private constructor for the utility class.
	 */
	private Forces() {

	}

	/**
	 * Compute the attractive force between an agent and its target destination.
	 * 
	 * Agents change their speed vectors dynamically due to the stimulus of
	 * surrounding environment. A deviation of the current speed vector from the
	 * desired speed vector leads to a force to recover to the desired speed
	 * vector within a certain relaxation time .
	 * 
	 * @param agent
	 *            the current agent
	 * @param targetVector
	 *            agent target
	 * @return the target attractive force
	 * 
	 * @throws Exception
	 *             on error
	 */
	public static PVector computeTargetAttraction(final CatpedsimAgent agent, final PVector targetVector)
			throws Exception {
		PVector seekTargetForce = new PVector(0, 0, 0);

		PVector desiredVelocity = PVector.sub(targetVector, agent.getPositionVector());
		desiredVelocity.normalize();
		desiredVelocity.mult(agent.getMaximumSpeed());
		seekTargetForce = PVector.sub(desiredVelocity, agent.getVelocity());
		seekTargetForce.div(agent.getRelaxationTime());

		return seekTargetForce;
	}

	/**
	 * Returns the repulsive force between all other agents and the current one.
	 * It is assumed that each conflicting agent within the subject agent's
	 * visual range will generate an elliptical force field that results in
	 * repulsive effect to the subject agent.
	 * 
	 * @param agent
	 *            the current agent
	 * @param agents
	 *            the list of all agents
	 * @param socialForceParameters
	 *            the social force model acting on the agent to agent repulsion
	 * @return the sepparation force between agents
	 */
	public static PVector computeAgentToAgentsRepulsion(final CatpedsimAgent agent, final List<CatpedsimAgent> agents,
			final SocialForceParameters socialForceParameters) {
		PVector sepparationForce = new PVector(0, 0, 0);

		if (agents.size() <= 1 || socialForceParameters.getInteractionStrength() == 0
				|| socialForceParameters.getInteractionRange() == 0) {
			return sepparationForce;
		}

		for (CatpedsimAgent anotherAgent : agents) {
			float agentsDistance = PVector.dist(agent.getPositionVector(), anotherAgent.getPositionVector());

			if (agentsDistance == 0) {
				continue;
			}

			if (agentsDistance <= socialForceParameters.getMaxDistanceForSocialForcesInfluence()) {
				PVector repulsiveForce = computeAgentToAgentRepulsiveForce(agent, anotherAgent, agentsDistance,
						socialForceParameters);

				if (agent.getVelocity().mag() > 0 && socialForceParameters.getAnisotropicConstant() < 1.0) {
					PVector agentsRelativeVector = PVector.sub(anotherAgent.getPositionVector(),
							agent.getPositionVector());

					float cosine = agent.getVelocity().dot(agentsRelativeVector)
							/ (agent.getVelocity().mag() * agentsRelativeVector.mag());

					repulsiveForce.mult((socialForceParameters.getAnisotropicConstant()
							+ (1 - socialForceParameters.getAnisotropicConstant()) * ((1 + cosine) / 2)));

				}
				sepparationForce.add(repulsiveForce);
			}
		}

		return sepparationForce;
	}

	/**
	 * Compute agent to agent repulsive force.
	 * 
	 * @param agent
	 *            current
	 * @param anotherAgent
	 *            the agent to interact with
	 * @param distanceBetweenAgents
	 *            the distance between agents
	 * @param socialForceParameters
	 *            social force model parameters
	 * 
	 * @return agent to agent repulsive force
	 */
	private static PVector computeAgentToAgentRepulsiveForce(final CatpedsimAgent agent,
			final CatpedsimAgent anotherAgent, final float distanceBetweenAgents,
			final SocialForceParameters socialForceParameters) {

		float radius = agent.getRadius() + anotherAgent.getRadius();

		PVector agentsRelativeVector = PVector.sub(agent.getPositionVector(), anotherAgent.getPositionVector());
		agentsRelativeVector.normalize();
		agentsRelativeVector.mult((float) (socialForceParameters.getInteractionStrength()
				* Math.exp((radius - distanceBetweenAgents) / socialForceParameters.getInteractionRange())));

		return agentsRelativeVector;
	}

	/**
	 * Compute the repulsive force between the current agent and walls and
	 * obstacles.
	 * 
	 * @param agent
	 *            current agent
	 * @param walls
	 *            list of walls
	 * @param obstacles
	 *            list of obstacles
	 * @return the repulsive force between the current agent and the walls
	 */
	public static PVector computeWallsAndObstaclesRepulsion(final CatpedsimAgent agent,
			final List<CatpedsimObstacle> walls, final List<CatpedsimObstacle> obstacles) {
		PVector totalWallRepulsiveForce = new PVector(0, 0, 0);

		List<PVector> wallRepulsionVectorToPoints = findWallsRepulsionVectorToPoints(agent, walls);
		List<PVector> obstaclesRepulsionVectorToPoints = findObstaclesRepulsionVectorToPoints(agent, obstacles);

		List<PVector> totalRepulsionVectors = new LinkedList<PVector>(wallRepulsionVectorToPoints);
		totalRepulsionVectors.addAll(obstaclesRepulsionVectorToPoints);

		for (PVector repulsionVectorToPoint : totalRepulsionVectors) {
			PVector repulsionForceFromWall = computeWallRepulsionForce(agent, repulsionVectorToPoint);
			totalWallRepulsiveForce.add(repulsionForceFromWall);
		}
		wallRepulsionVectorToPoints.clear();

		return totalWallRepulsiveForce;
	}

	/**
	 * Compute the repulsion force from the walls. The repulsive force from the
	 * walls is the force that measures the agent's desire to avoid the wall,
	 * and prevents agents from walking into, or even through, walls in the
	 * simulation. As with the interaction force between agents, the repulsive
	 * force from the walls functions without taking into account the physical
	 * forces between walls and agents.
	 * 
	 * @param agent
	 *            current agent
	 * @param repulsionVectorToPoint
	 *            the set of points from which repulsion should be calculated
	 *            (one for each wall).
	 * @return wall repulsion force
	 */
	public static PVector computeWallRepulsionForce(final CatpedsimAgent agent, final PVector repulsionVectorToPoint) {

		PVector repulsionVector = PVector.sub(agent.getPositionVector(), repulsionVectorToPoint);
		float repulsionLength = repulsionVector.mag();
		repulsionVector.normalize();
		float agentWallRepulsionRadius = agent.getWallRepulsionRadius();
		float repulsionStrength = (float) ((1 / agentWallRepulsionRadius) * agent.getWallRepulsionMagnitude()
				* Math.exp(-repulsionLength / agentWallRepulsionRadius));
		repulsionVector.mult(repulsionStrength);

		return repulsionVector;
	}

	/**
	 * Compute a set of vectors to points from which repulsion to walls should
	 * be calculated.
	 * 
	 * For each wall, calculate the projection of the vector pointing from the
	 * wall’s starting point to the agent, unto the vector pointing from the
	 * wall’s starting point to its endpoint.
	 * 
	 * If this point is part of the wall, add it to the set of points repulsion
	 * should be calculated from, and add the wall’s two endpoints to the set of
	 * already used endpoints.
	 * 
	 * If the projected point is not part of the wall, instead the wall endpoint
	 * closest to the agent is added to the set endpoints that are candidates
	 * for repulsion.
	 * 
	 * After having gone through all walls, for each point in the set of
	 * candidate endpoints, check if this point is already in the set of used
	 * endpoints. If it is, discard it. Otherwise, check if the endpoint is
	 * shared with another wall (i.e. if it appears twice in the set of
	 * candidate endpoints). If the endpoint is either shared with another wall
	 * or closer to the agent than the agent’s radius, add it to the set of
	 * repulsion points and the set of used endpoints.
	 * 
	 * @param agent
	 *            current agent
	 * @param walls
	 *            list of walls
	 * @return vectors to points from which repulsion to walls and obstacles
	 *         should be calculated
	 */
	private static List<PVector> findWallsRepulsionVectorToPoints(final CatpedsimAgent agent,
			final List<CatpedsimObstacle> walls) {

		List<PVector> repulsionVectorToPoints = new LinkedList<PVector>();
		List<PVector> possibleVectorToPoints = new LinkedList<PVector>();
		List<PVector> usedVectorToPoints = new LinkedList<PVector>();

		for (int indexWall = 0; indexWall < walls.size(); indexWall++) {
			CatpedsimObstacle wall = walls.get(indexWall);

			for (int indexSection = 0; indexSection < wall.getObstacleSections().length; indexSection++) {
				ShapeSection obstacleSection = wall.getObstacleSections()[indexSection];
				PVector positionVector = agent.getPositionVector();

				float projectionLength = Trigonometry.vectorProjectionLength(obstacleSection.getVectorToStartPoint(),
						obstacleSection.getVectorToEndPoint(), positionVector);

				if (projectionLength < 0) {
					possibleVectorToPoints.add(obstacleSection.getVectorToStartPoint());
				} else if (projectionLength > obstacleSection.getLength()) {
					possibleVectorToPoints.add(obstacleSection.getVectorToEndPoint());
				} else {
					repulsionVectorToPoints.add(PVector.add(obstacleSection.getVectorToStartPoint(),
							PVector.mult(
									PVector.sub(obstacleSection.getVectorToEndPoint(),
											obstacleSection.getVectorToStartPoint()),
									projectionLength / obstacleSection.getLength())));
					usedVectorToPoints.add(obstacleSection.getVectorToStartPoint());
					usedVectorToPoints.add(obstacleSection.getVectorToEndPoint());
				}
			}
		}

		for (PVector possible : possibleVectorToPoints) {

			if (usedVectorToPoints.contains(possible)) {
				boolean free = false;

				for (PVector otherPossible : possibleVectorToPoints) {
					if (possible != otherPossible && otherPossible.equals(possible)) {
						free = true;
						break;
					}
				}

				if (!free || PVector.sub(agent.getPositionVector(), possible).mag() < agent.getRadius()) {
					repulsionVectorToPoints.add(possible);
					usedVectorToPoints.add(possible);
				}
			}
		}

		possibleVectorToPoints.clear();
		usedVectorToPoints.clear();

		return repulsionVectorToPoints;
	}

	/**
	 * Compute a set of vectors to points from which repulsion to obstacles
	 * should be calculated.
	 * 
	 * @param agent
	 *            current agent
	 * @param obstacles
	 *            list of obstacles
	 * @return vectors to points from which repulsion to walls and obstacles
	 *         should be calculated
	 */
	private static List<PVector> findObstaclesRepulsionVectorToPoints(final CatpedsimAgent agent,
			final List<CatpedsimObstacle> obstacles) {

		List<PVector> repulsionVectorToPoints = new LinkedList<PVector>();
		List<PVector> possibleVectorToPoints = new LinkedList<PVector>();
		List<PVector> usedVectorToPoints = new LinkedList<PVector>();

		for (int indexWall = 0; indexWall < obstacles.size(); indexWall++) {
			CatpedsimObstacle obstacle = obstacles.get(indexWall);

			for (int indexSection = 0; indexSection < obstacle.getObstacleSections().length; indexSection++) {
				ShapeSection obstacleSection = obstacle.getObstacleSections()[indexSection];
				PVector positionVector = agent.getPositionVector();

				float projectionLength = Trigonometry.vectorProjectionLength(obstacleSection.getVectorToStartPoint(),
						obstacleSection.getVectorToEndPoint(), positionVector);

				if (projectionLength < 0) {
					possibleVectorToPoints.add(obstacleSection.getVectorToStartPoint());
				} else if (projectionLength > obstacleSection.getLength()) {
					possibleVectorToPoints.add(obstacleSection.getVectorToEndPoint());
				} else {
					repulsionVectorToPoints.add(PVector.add(obstacleSection.getVectorToStartPoint(),
							PVector.mult(
									PVector.sub(obstacleSection.getVectorToEndPoint(),
											obstacleSection.getVectorToStartPoint()),
									projectionLength / obstacleSection.getLength())));
					usedVectorToPoints.add(obstacleSection.getVectorToStartPoint());
					usedVectorToPoints.add(obstacleSection.getVectorToEndPoint());
				}
			}
		}

		for (PVector possible : possibleVectorToPoints) {

			if (usedVectorToPoints.contains(possible)) {
				boolean free = false;

				for (PVector otherPossible : possibleVectorToPoints) {
					if (possible != otherPossible && otherPossible.equals(possible)) {
						free = true;
						break;
					}
				}

				if (!free || PVector.sub(agent.getPositionVector(), possible).mag() < agent.getRadius()) {
					repulsionVectorToPoints.add(possible);
					usedVectorToPoints.add(possible);
				}
			}
		}

		possibleVectorToPoints.clear();
		usedVectorToPoints.clear();

		return repulsionVectorToPoints;
	}
}
