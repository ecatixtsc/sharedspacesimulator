package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.socialforcemodel;

import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAutonomousVehicle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimBicycle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimPedestrian;

/**
 * Social force model constants and ranges. Every type of agent will be unsing a
 * specific SocialForceModelParameters object.
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
public class SocialForceParameters {

	/** Interaction strength constant or A. */
	private float interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH;

	/** Interaction range constant or B. */
	private float interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE;

	/** Anisotropic constant or lambda. */
	private float anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;

	/** Max velocity factor. */
	private float maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;

	/**
	 * The range for the social forces influence. The limitation improves
	 * performance while saving computational time.
	 */
	private float maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;

	/**
	 * The constructor is initializing all the social force modell constants.
	 * 
	 * @param newInteractionStrength
	 *            interaction strength
	 * @param newInteractionRange
	 *            interaction range
	 * @param newAnisotropicConstant
	 *            anisotropic constant
	 * @param newMaxVelocityFactor
	 *            max velocity factor
	 * @param newMaxDistanceForSocialForcesInfluence
	 *            maximum distance for the social force influence
	 */
	public SocialForceParameters(final float newInteractionStrength, final float newInteractionRange,
			final float newAnisotropicConstant, final float newMaxVelocityFactor,
			final float newMaxDistanceForSocialForcesInfluence) {

		interactionStrength = newInteractionStrength;
		interactionRange = newInteractionRange;
		anisotropicConstant = newAnisotropicConstant;
		maxVelocityFactor = newMaxVelocityFactor;
		maxDistanceForSocialForcesInfluence = newMaxDistanceForSocialForcesInfluence;
	}

	/**
	 * Constructor.
	 * 
	 * @param socialForceAgentsPair
	 *            the type of agents involved in interaction
	 */
	public SocialForceParameters(final SocialForceAgentsPair<?, ?> socialForceAgentsPair) {
		if (socialForceAgentsPair.getCurrentAgentType().equals(CatpedsimPedestrian.class)) {
			if (socialForceAgentsPair.getOtherAgentType().equals(CatpedsimPedestrian.class)) {
				interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH_PEDESTRIAN_TO_PEDESTRIAN;
				interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE_PEDESTRIAN_TO_PEDESTRIAN;
				anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;
				maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;
				maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;
			} else if (socialForceAgentsPair.getOtherAgentType().equals(CatpedsimAutonomousVehicle.class)) {
				interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH_PEDESTRIAN_TO_POD;
				interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE_PEDESTRIAN_TO_POD;
				anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;
				maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;
				maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;
			} else if (socialForceAgentsPair.getOtherAgentType().equals(CatpedsimBicycle.class)) {
				interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH_PEDESTRIAN_TO_BIKE;
				interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE_PEDESTRIAN_TO_BIKE;
				anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;
				maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;
				maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;
			}
		} else if (socialForceAgentsPair.getCurrentAgentType().equals(CatpedsimAutonomousVehicle.class)) {
			if (socialForceAgentsPair.getOtherAgentType().equals(CatpedsimAutonomousVehicle.class)) {
				interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH_POD_TO_POD;
				interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE_POD_TO_POD;
				anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;
				maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;
				maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;
			} else if (socialForceAgentsPair.getOtherAgentType().equals(CatpedsimPedestrian.class)) {
				interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH_POD_TO_PEDESTRIAN;
				interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE_POD_TO_PEDESTRIAN;
				anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;
				maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;
				maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;
			} else if (socialForceAgentsPair.getOtherAgentType().equals(CatpedsimBicycle.class)) {
				interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH_POD_TO_BIKE;
				interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE_POD_TO_BIKE;
				anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;
				maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;
				maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;
			}
		} else if (socialForceAgentsPair.getCurrentAgentType().equals(CatpedsimBicycle.class)) {
			if (socialForceAgentsPair.getOtherAgentType().equals(CatpedsimBicycle.class)) {
				interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH_BIKE_TO_BIKE;
				interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE_BIKE_TO_BIKE;
				anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;
				maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;
				maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;
			} else if (socialForceAgentsPair.getOtherAgentType().equals(CatpedsimAutonomousVehicle.class)) {
				interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH_BIKE_TO_POD;
				interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE_BIKE_TO_POD;
				anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;
				maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;
				maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;
			} else if (socialForceAgentsPair.getOtherAgentType().equals(CatpedsimPedestrian.class)) {
				interactionStrength = SocialForceDefaultValues.DEFAULT_INTERACTION_STRENGTH_BIKE_TO_PEDESTRIAN;
				interactionRange = SocialForceDefaultValues.DEFAULT_INTERACTION_RANGE_BIKE_TO_PEDESTRIAN;
				anisotropicConstant = SocialForceDefaultValues.DEFAULT_ANISOTROPIC_CONSTANT;
				maxVelocityFactor = SocialForceDefaultValues.DEFAULT_MAXIMUM_VELOCITY;
				maxDistanceForSocialForcesInfluence = SocialForceDefaultValues.DEFAULT_MAX_RANGE_SFM;
			}
		}
	}

	/**
	 * Get social force interaction strength.
	 * 
	 * @return social force interaction strength
	 */
	public final float getInteractionStrength() {
		return interactionStrength;
	}

	/**
	 * Get social force interaction range.
	 * 
	 * @return ocial force interaction range
	 */
	public final float getInteractionRange() {
		return interactionRange;
	}

	/**
	 * Get social force anisotropic constant.
	 * 
	 * @return social force anisotropic constant
	 */
	public final float getAnisotropicConstant() {
		return anisotropicConstant;
	}

	/**
	 * Get the maximum velocity factor.
	 * 
	 * @return the maximum velocity factor
	 */
	public final float getMaxVelocityFactor() {
		return maxVelocityFactor;
	}

	/**
	 * Get the maximum distance for the social force influence.
	 * 
	 * @return the maximum distance for the social force influence
	 */
	public final float getMaxDistanceForSocialForcesInfluence() {
		return maxDistanceForSocialForcesInfluence;
	}
}
