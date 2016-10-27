package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.path.XmlPath;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * Additional agent behaviour. The agent contains a chronological list of
 * behaviours.
 * 
 * Example: traveling towards a transfer zone, boarding another agent, traveling
 * to an intermediate destination, unboarding, moving autonomously again.
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
@XmlRootElement(name = "agentBehaviour")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "agentBehaviour")
public class AgentBehaviour {

	/** Logger for the class. */
	private static final Logger LOGGER = UtilityLogger.getLogger(AgentBehaviour.class);

	/** Paths to follow. */
	@XmlElement(name = "pathSection")
	private final LinkedList<XmlPath> pathsToFollow = new LinkedList<XmlPath>();

	/**
	 * Constructor.
	 */
	public AgentBehaviour() {

	}

	/**
	 * Constructor.
	 * 
	 * @param state
	 *            agent state
	 */
	public AgentBehaviour(final AgentState state) {
		XmlPath pathItem = new XmlPath();
		pathItem.setAgentState(state);
		pathsToFollow.add(pathItem);
	}

	/**
	 * Make a copy of the newBehaviour.
	 * 
	 * @return a copy a the AgentBehaviour object
	 */
	public final AgentBehaviour getCopy() {
		AgentBehaviour agentBehaviourCopy = new AgentBehaviour();

		LinkedList<XmlPath> newPath = agentBehaviourCopy.getPathToFollow();
		newPath.clear();

		for (XmlPath pathSection : pathsToFollow) {
			XmlPath newPathSection = new XmlPath(pathSection);
			newPath.add(newPathSection);
		}
		return agentBehaviourCopy;
	}

	/**
	 * Get the path to follow characteristic to a certain behaviour.
	 * 
	 * @return returns the path to follow within a behaviour
	 */
	public final LinkedList<XmlPath> getPathToFollow() {
		return pathsToFollow;
	}

	/**
	 * Get the current behaviour.
	 * 
	 * @return path to follow for the current behaviour
	 */
	public final XmlPath getCurrentBehaviour() {
		try {
			return pathsToFollow.getFirst();
		} catch (Exception ex) {
			LOGGER.warn("This behaviour has no path", ex);
			return null;
		}
	}

	/**
	 * Remove current behaviour.
	 */
	public final void removeCurrentBehaviour() {
		try {
			pathsToFollow.removeFirst();
		} catch (Exception ex) {
			LOGGER.info("This behaviour has no path", ex);
		}
	}

	/**
	 * Check if the current behaviour has a path pre-computed.
	 * 
	 * @return true if there is a path
	 */
	public final boolean isPathInTheCurrentBehaviour() {
		boolean isPath = false;

		if (pathsToFollow != null && !pathsToFollow.isEmpty()) {
			isPath = true;
		}

		return isPath;
	}

	/**
	 * Checks if the behaviour contains elements.
	 * 
	 * @return true if the behaviour contains elements
	 */
	public final boolean isEmpty() {
		return pathsToFollow.isEmpty();
	}

	/**
	 * Generate Xml path file example.
	 * 
	 * @param filePath
	 *            file path
	 */
	public static void generateXmlPathExample(final String filePath) {
		AgentBehaviour pathObject = new AgentBehaviour();
		XmlPath path = new XmlPath();
		pathObject.addPath(path);

		try {
			File file = new File(filePath);
			JAXBContext jaxbContext = JAXBContext.newInstance(AgentBehaviour.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(pathObject, file);

		} catch (Exception e) {
			LOGGER.warn("The xml path file does not exist or it is incorrect!", e);
		}
	}

	/**
	 * Add a new path in the behaviour.
	 * 
	 * @param path
	 *            new path to add in the lsit
	 */
	private void addPath(final XmlPath path) {
		pathsToFollow.add(path);
	}

	/**
	 * Load the xml pre-computed path.
	 * 
	 * @param filePath
	 *            file path
	 * @return xml path
	 */
	public static AgentBehaviour loadXmlPath(final String filePath) {
		AgentBehaviour loadedBehaviour = null;

		try {
			InputStream inputStream = new FileInputStream(filePath);

			JAXBContext jaxbContext = JAXBContext.newInstance(AgentBehaviour.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			loadedBehaviour = (AgentBehaviour) unmarshaller.unmarshal(inputStream);
		} catch (Exception ex) {
			LOGGER.warn("Error while loading the pre-computed path.", ex);
		}

		return loadedBehaviour;
	}

	/**
	 * Free the memory.
	 */
	public final void clear() {
		for (XmlPath path : pathsToFollow) {
			path.clear();
		}
		pathsToFollow.clear();
	}

	/**
	 * Set current state.
	 * 
	 * @param newState
	 *            agent state
	 */
	public final void setCurrentState(final AgentState newState) {
		XmlPath current = getCurrentBehaviour();

		if (current != null) {
			current.setAgentState(newState);
		}
	}
}