package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents;

import com.sun.javafx.geom.Vec3f;

import uk.org.catapult.ts.cav.model.identifier.StringIdentifier;

/**
 * Catedsim general vehicle.
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
public class CatpedsimVehicle extends CatpedsimAgent {

	/**
	 * Constructor.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param vehicleName
	 *            vehicle name
	 * @param uniqueIdentifier
	 *            vehicle unique identifier
	 * @param vec3f
	 *            location vector
	 * @param maximumSpeed
	 *            maximum speed
	 */
	public CatpedsimVehicle(final String publisherId, final String vehicleName, final StringIdentifier uniqueIdentifier,
			final Vec3f vec3f, final float maximumSpeed) {
		super(publisherId, vehicleName, uniqueIdentifier, vec3f, maximumSpeed);

		setRadius(AutonomousVehicleConstraints.RADIUS);
		setRelaxationTime(AutonomousVehicleConstraints.RELAXATION_TIME);
		setWallRepulsionRadius(AutonomousVehicleConstraints.WALL_REPULSION_RADIUS);
		setWallRepulsionMagnitude(AutonomousVehicleConstraints.WALL_REPULSION_MAGNITUDE_U);
		setMaxForce(AutonomousVehicleConstraints.MAXIMUM_FORCE);
		setMaximumSteeringAngle(AutonomousVehicleConstraints.MAXIMUM_STEERING_ANGLE);
	}
}