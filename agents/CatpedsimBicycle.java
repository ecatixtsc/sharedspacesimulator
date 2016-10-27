package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents;

import java.awt.Color;

import com.sun.javafx.geom.Vec3f;

import processing.core.PVector;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimCrossing;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimTransitionZone;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.path.XmlVertex;
import uk.org.catapult.ts.cav.model.identifier.StringIdentifier;
import uk.org.catapult.ts.cav.model.spatial.Vector2D;

/**
 * Bicycle class.
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
public class CatpedsimBicycle extends CatpedsimAgent {

	/**
	 * Constructor.
	 * 
	 * @param newPublisherId
	 *            agent publisher id
	 * @param newName
	 *            agent name
	 * @param unewUniqueIdentifier
	 *            unique identifier
	 * @param agentPosition3D
	 *            agent x coordinate, agent y coordinate, agent z coordinate in
	 *            floating point precision
	 * @param newMaximumSpeed
	 *            agent maximum speed
	 */
	public CatpedsimBicycle(final String newPublisherId, final String newName,
			final StringIdentifier unewUniqueIdentifier, final Vec3f agentPosition3D, final float newMaximumSpeed) {
		super(newPublisherId, newName, unewUniqueIdentifier, agentPosition3D, newMaximumSpeed);

		setRadius(BicycleConstraints.RADIUS);
		setRelaxationTime(BicycleConstraints.RELAXATION_TIME);
		setWallRepulsionRadius(BicycleConstraints.WALL_REPULSION_RADIUS);
		setWallRepulsionMagnitude(BicycleConstraints.WALL_REPULSION_MAGNITUDE_U);
		setMaxForce(BicycleConstraints.MAX_FORCE);
		setMaximumSteeringAngle(BicycleConstraints.MAXIMUM_STEERING_ANGLE);
	}

	/**
	 * Constructor.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param agentName
	 *            name of the pedestrian
	 * @param uniqueIdentifier
	 *            unique pedestrian identifier
	 * @param agentPosition3D
	 *            agent x coordinate, agent y coordinate, agent z coordinate in
	 *            floating point precision
	 * @param maximumVelocity
	 *            maximum speed
	 * @param exit
	 *            exit area
	 */
	public CatpedsimBicycle(final String publisherId, final String agentName, final StringIdentifier uniqueIdentifier,
			final Vec3f agentPosition3D, final float maximumVelocity, final CatpedsimCrossing exit) {
		this(publisherId, agentName, uniqueIdentifier, agentPosition3D, maximumVelocity);
		setAgentExit(exit);
	}

	/**
	 * Constructor.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param agentName
	 *            name of the pedestrian
	 * @param uniqueIdentifier
	 *            unique pedestrian identifier
	 * @param agentPosition3D
	 *            agent x coordinate, agent y coordinate, agent z coordinate in
	 *            floating point precision
	 * @param maximumVelocity
	 *            maximum speed
	 * @param exit
	 *            exit area
	 * @param color
	 *            agent color
	 * @param preDefinedBehaviour
	 *            pre-defined behaviours
	 */
	public CatpedsimBicycle(final String publisherId, final String agentName, final StringIdentifier uniqueIdentifier,
			final Vec3f agentPosition3D, final float maximumVelocity, final CatpedsimCrossing exit, final Color color,
			final AgentBehaviour preDefinedBehaviour) {
		this(publisherId, agentName, uniqueIdentifier, agentPosition3D, maximumVelocity, exit);

		setColorToRepresent(color);
		setSequenceOfBehaviours(preDefinedBehaviour);
	}

	/**
	 * Constructor.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param agentName
	 *            name of the pedestrian
	 * @param uniqueIdentifier
	 *            unique pedestrian identifier
	 * @param agentPosition3D
	 *            agent x coordinate, agent y coordinate, agent z coordinate in
	 *            floating point precision
	 * @param maximumVelocity
	 *            maximum speed
	 * @param transition
	 *            agent destination is a transition area
	 * @param color
	 *            agent color
	 * @param preDefinedBehaviour
	 *            pre-defined behaviours
	 */
	public CatpedsimBicycle(final String publisherId, final String agentName, final StringIdentifier uniqueIdentifier,
			final Vec3f agentPosition3D, final float maximumVelocity, final CatpedsimTransitionZone transition,
			final Color color, final AgentBehaviour preDefinedBehaviour) {

		this(publisherId, agentName, uniqueIdentifier, agentPosition3D, maximumVelocity);

		setColorToRepresent(color);
		setSequenceOfBehaviours(preDefinedBehaviour);
		setTransitionZone(transition);

		XmlVertex target = preDefinedBehaviour.getCurrentBehaviour().getLast();
		if (target != null) {
			PVector exitVector = new PVector(target.getX(), target.getY());
			setVectorToAgentsTarget(exitVector);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param newAgentName
	 *            name of the pedestrian
	 * @param uniqueIdentifier
	 *            unique pedestrian identifier
	 * @param agentLocation
	 *            agent x coordinate, agent y coordinate, agent z coordinate in
	 *            floating point precision
	 * @param maxVelocity
	 *            maximum speed
	 * @param color
	 *            agent color
	 * @param behaviour
	 *            pre-defined behaviours
	 * @param headingTo
	 *            agent heading or orientation
	 */
	public CatpedsimBicycle(final String publisherId, final String newAgentName,
			final StringIdentifier uniqueIdentifier, final Vec3f agentLocation, final float maxVelocity,
			final Color color, final AgentBehaviour behaviour, final Vector2D headingTo) {
		this(publisherId, newAgentName, uniqueIdentifier, agentLocation, maxVelocity);
		setColorToRepresent(color);
		setSequenceOfBehaviours(behaviour);
		setCurrentHeading(headingTo);
	}
}