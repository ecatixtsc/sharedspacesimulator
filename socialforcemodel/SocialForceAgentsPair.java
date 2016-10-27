package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.socialforcemodel;

/**
 * The class indicates the agents involved in the social force model
 * interaction.
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
 * @param <K>
 *            The class type of current agent
 * @param <V>
 *            The class type of the agent to interact with
 */
public class SocialForceAgentsPair<K, V> {
	/** The class type of current agent. */
	private K currentAgentType;

	/** The class type of the agent to interact with. */
	private V otherAgentType;

	/**
	 * The social force model configuration according to the type of agents
	 * involved in the interaction.
	 * 
	 * @param newCurrentAgentType
	 *            current agent type
	 * @param newOtherAgentType
	 *            the type of agent the current agent is interacting with
	 */
	public SocialForceAgentsPair(final K newCurrentAgentType, final V newOtherAgentType) {
		currentAgentType = newCurrentAgentType;
		otherAgentType = newOtherAgentType;
	}

	/**
	 * Get the current agent type.
	 * 
	 * @return the current agent type
	 */
	public final K getCurrentAgentType() {
		return currentAgentType;
	}

	/**
	 * Get the type of agent the current agent is interacting with.
	 * 
	 * @return the type of agent the current agent is interacting with
	 */
	public final V getOtherAgentType() {
		return otherAgentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		if (currentAgentType != null && otherAgentType != null) {
			return currentAgentType.hashCode() * otherAgentType.hashCode();
		} else {
			return this.hashCode();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(final Object newObject) {
		if (newObject == this) {
			return true;
		}

		if (newObject instanceof SocialForceAgentsPair<?, ?>) {
			final SocialForceAgentsPair<?, ?> objectToCOmpare = (SocialForceAgentsPair<?, ?>) newObject;

			if (getCurrentAgentType().equals(objectToCOmpare.getCurrentAgentType())
					&& getOtherAgentType().equals(objectToCOmpare.getOtherAgentType())) {

				return true;
			}
		}

		return false;
	}
}
