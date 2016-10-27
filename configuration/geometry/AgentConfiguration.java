package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import proguard.annotation.KeepName;
import proguard.annotation.KeepPublicClassMemberNames;

/**
 * The agent configuration ifnromation.
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
@XmlRootElement(name = "agentConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AgentConfiguration")
@KeepName
@KeepPublicClassMemberNames
public class AgentConfiguration {

	/** Number of agents introduced at the simulation start. */
	@XmlElement(name = "numberOfAgents", required = false)
	private int numberOfAgents = 0;

	/** The path to the origin-destination matrices. */
	@XmlElement(name = "originDestinationMatricesFilePath", required = false)
	private String originDestinationMatricesFilePath = null;

	/**
	 * The transition zones distribution excel file for Catpedsim.
	 */
	@XmlElement(name = "transitionZonesDistribution", required = false)
	private String transitionZonesDistribution = null;

	/**
	 * Get number of agents.
	 * 
	 * @return number of agents
	 */
	public final int getNumberOfAgents() {
		return numberOfAgents;
	}

	/**
	 * Get file path for the origin-destination matrices.
	 * 
	 * @return file path for the origin-destination matrices
	 */
	public final String getOriginDestinationMatricesFilePath() {
		return originDestinationMatricesFilePath;
	}

	/**
	 * Get the transition zones matrices.
	 * 
	 * @return the transition zones matrices
	 */
	public final String getTransitionZonesDistribution() {
		return transitionZonesDistribution;
	}

	/**
	 * Set the number of agents.
	 * 
	 * @param newNumberOfAgents
	 *            new number of agents
	 */
	public final void setNumberOfAgents(final int newNumberOfAgents) {
		this.numberOfAgents = newNumberOfAgents;
	}
}
