package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry;

import java.util.Iterator;
import java.util.LinkedList;

import processing.core.PVector;
import uk.org.catapult.ts.cav.model.identifier.StringIdentifier;

/**
 * Transfer zones pedestrians to pods or pedestrians to bicycles. The transfer
 * zones will always maintain maxVehiclesAllowed agents eclosed in the area.
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
public class CatpedsimTransitionZone {

	/**
	 * The maximum number of agents allowed in the transition zone. If the
	 * number is large than PURGE, the rest of the agents are removed from the
	 * simulation.
	 */
	public static final int PURGE = 3;

	/** The number of maximum agents allowed inside. */
	private int maxAgentsAllowed = 1;

	/** Transition zone type. */
	private TransitionZoneTypes zoneType = null;

	/** Transition zone name. */
	private String transitionZoneName = null;

	/** List of enclosed agents. */
	private final LinkedList<StringIdentifier> enclosedAgents = new LinkedList<StringIdentifier>();

	/** A section of a transition zone. A section is a segment. */
	private final LinkedList<ShapeSection> sections = new LinkedList<ShapeSection>();

	/**
	 * Constructor.
	 * 
	 * @param newType
	 *            transition zone type
	 * @param newName
	 *            zone name/caption
	 */
	public CatpedsimTransitionZone(final TransitionZoneTypes newType, final String newName) {
		zoneType = newType;
		transitionZoneName = newName;
	}

	/**
	 * Add a section to a transition zone.
	 * 
	 * @param startSectionVector
	 *            section start vector
	 * @param endSectionVector
	 *            section end vector
	 */
	public final void addSection(final PVector startSectionVector, final PVector endSectionVector) {
		ShapeSection section = new ShapeSection(startSectionVector, endSectionVector);
		sections.add(section);
	}

	@Override
	public final String toString() {
		StringBuilder text = new StringBuilder();

		for (ShapeSection section : sections) {
			text.append(section.getVectorToStartPoint() + " " + section.getVectorToEndPoint() + "\n");
		}

		return text.toString().trim();
	}

	/**
	 * Get sections.
	 * 
	 * @return section list
	 */
	public final LinkedList<ShapeSection> getSections() {
		return sections;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	/**
	 * Get the maximum of agents allowed.
	 * 
	 * @return the maximum of agents allowed
	 */
	public final int getMaxAgentsAllowed() {
		return maxAgentsAllowed;
	}

	/**
	 * Set the maximum of agents allowed.
	 * 
	 * @param newMaxAgentsAllowed
	 *            the maximum of agents allowed
	 */
	public final void setMaxAgentsAllowed(final int newMaxAgentsAllowed) {
		this.maxAgentsAllowed = newMaxAgentsAllowed;
	}

	/**
	 * Get the list of agents parked in the current transition zone.
	 * 
	 * @return the list of agents parked in the current transition zone
	 */
	public final LinkedList<StringIdentifier> getEnclosedAgents() {
		return enclosedAgents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(final Object newTransitionZone) {

		try {
			LinkedList<ShapeSection> newSectionsList = ((CatpedsimTransitionZone) newTransitionZone).getSections();

			if (newSectionsList.size() != sections.size()) {
				return false;
			}

			Iterator<ShapeSection> it1 = sections.iterator();
			Iterator<ShapeSection> it2 = newSectionsList.iterator();

			while (it1.hasNext() && it2.hasNext()) {
				ShapeSection section1 = it1.next();
				ShapeSection section2 = it2.next();

				if (!section1.equals(section2)) {
					return false;
				}
			}

		} catch (Exception ex) {
			return false;
		}

		return true;
	}

	/**
	 * Add unique identifier of the agent enclosed in the transition area.
	 * 
	 * @param uniqueIdentifier
	 *            unique identifier of the agent enclosed in the transition area
	 */
	public final void addEnclosedAgent(final StringIdentifier uniqueIdentifier) {
		synchronized (enclosedAgents) {
			enclosedAgents.add(uniqueIdentifier);
		}
	}

	/**
	 * Get the number of sections.
	 * 
	 * @return the number of sections
	 */
	public final int getNumberOfSections() {
		return sections.size();
	}

	/**
	 * Get the longest section.
	 * 
	 * @return the longest transit zone section
	 */
	public final ShapeSection getLongest() {
		ShapeSection longestSection = null;
		float maximumLength = 0;

		for (ShapeSection section : sections) {
			if (maximumLength < section.getLength()) {
				maximumLength = section.getLength();
				longestSection = section;
			}
		}
		return longestSection;
	}

	/**
	 * Get the shortest section.
	 * 
	 * @return the shortest section
	 */
	public final ShapeSection getShortest() {
		ShapeSection shortestSection = null;
		float minimumLength = Float.MAX_VALUE;

		for (ShapeSection section : sections) {
			if (minimumLength > section.getLength()) {
				minimumLength = section.getLength();
				shortestSection = section;
			}
		}
		return shortestSection;
	}

	/**
	 * Get section heading.
	 * 
	 * @param section
	 *            transition zone section
	 * @return section heading
	 */
	public static final PVector getSectionDirection(final ShapeSection section) {
		PVector vector = PVector.sub(section.getVectorToStartPoint(), section.getVectorToEndPoint());
		vector.normalize();

		return vector;
	}

	/**
	 * Get the vector to centroid.
	 * 
	 * @return vector to centroid
	 */
	public final PVector getVectorToCentroid() {
		PVector vectorToCentroid = new PVector(0, 0, 0);
		int numberOfVertices = 0;

		for (ShapeSection section : sections) {
			PVector start = section.getVectorToStartPoint();
			PVector end = section.getVectorToEndPoint();

			vectorToCentroid.add(start);
			vectorToCentroid.add(end);
			numberOfVertices += 2;
		}
		if (numberOfVertices > 0) {
			vectorToCentroid.div(numberOfVertices);
		}

		return vectorToCentroid;
	}

	/**
	 * Get zone type. Pod zone or bike zone.
	 * 
	 * @return zone type
	 */
	public final TransitionZoneTypes getZoneType() {
		return zoneType;
	}

	/**
	 * Get the transition zone name.
	 * 
	 * @return transition zone name
	 */
	public final String getTransitionZoneName() {
		return transitionZoneName;
	}
}