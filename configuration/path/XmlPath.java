package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.path;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.AgentState;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * Load from an XML file a pre-compute path.
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
@XmlRootElement(name = "pathConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "path")
public class XmlPath {

	/** Logger. */
	private static final Logger LOGGER = UtilityLogger.getLogger(XmlPath.class);

	/** Agent state. */
	@XmlElement(name = "agentState")
	private AgentState agentState = AgentState.STOP_AND_WAIT;

	/** Entrance id. */
	@XmlElement(name = "entrance")
	private XmlCrossing entrance = new XmlCrossing();

	/** Exit id. */
	@XmlElement(name = "exit")
	private XmlCrossing exit = new XmlCrossing();

	/** List of intermediate pre-computed points. */
	@XmlElement(name = "pathOfVertices")
	private final XmlVerticesPathList path = new XmlVerticesPathList();

	/**
	 * Constructor.
	 */
	public XmlPath() {
	}

	/**
	 * Set agent new state.
	 * 
	 * @param newAgentState
	 *            agent state
	 */
	public final void setAgentState(final AgentState newAgentState) {
		this.agentState = newAgentState;
	}

	/**
	 * Constructor.
	 * 
	 * @param object
	 *            new path object
	 */
	public XmlPath(final XmlPath object) {
		agentState = object.agentState;
		entrance = object.entrance;
		exit = object.exit;
		path.addAll(object.path);
	}

	/**
	 * Get agent state.
	 * 
	 * @return agent state
	 */
	public final AgentState getAgentState() {
		return agentState;
	}

	/**
	 * Generate Xml path file example.
	 * 
	 * @param filePath
	 *            file path
	 */
	public static void generateXmlPathExample(final String filePath) {
		XmlPath pathObject = new XmlPath();

		try {
			File file = new File(filePath);
			JAXBContext jaxbContext = JAXBContext.newInstance(XmlPath.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(pathObject, file);
		} catch (Exception e) {
			LOGGER.warn("The xml path file does not exist or it is incorrect!", e);
		}
	}

	/**
	 * Load the xml pre-computed path.
	 * 
	 * @param filePath
	 *            file path
	 * @return xml path
	 */
	public static XmlPath loadXmlPath(final String filePath) {
		XmlPath loadedPath = null;

		try {
			InputStream inputStream = new FileInputStream(filePath);

			JAXBContext jaxbContext = JAXBContext.newInstance(XmlPath.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			loadedPath = (XmlPath) unmarshaller.unmarshal(inputStream);
		} catch (Exception ex) {
			LOGGER.warn("Error while loading the pre-computed path.", ex);
		}

		return loadedPath;
	}

	/**
	 * Check if the path contains points.
	 * 
	 * @return true if the path contains points, false otherwise
	 */
	public final boolean hasElements() {
		if (path == null || path.size() == 0) {
			return false;
		}

		return true;
	}

	/**
	 * Get the first element in the list.
	 * 
	 * @return the begining of the path
	 */
	public final XmlVertex getFirst() {
		XmlVertex result = null;

		try {
			result = path.getFirst();
		} catch (Exception ex) {
		}

		return result;
	}

	/**
	 * Remove the first element in the list.
	 * 
	 * @return the number of intermediate targets left in the agent's list
	 */
	public final int removeFirst() {
		return path.removeFirst();
	}

	/**
	 * Clean the xml path object.
	 */
	public final void clear() {
		path.clear();
	}

	/**
	 * Get the last point in the path list.
	 * 
	 * @return get last target in the pre-computed list
	 */
	public final XmlVertex getLast() {
		try {
			return path.getLast();
		} catch (Exception ex) {
			LOGGER.info("There is no pre-computed path!");
			return null;
		}
	}

	/**
	 * Check if the agent can move.
	 * 
	 * @return true if the agent can move
	 */
	public final boolean canMove() {
		return agentState.equals(AgentState.MOVE_AUTONOMOUSLY_WITHOUT_CONSTRAINTS)
				|| agentState.equals(AgentState.MOVE_TO_PICK_UP_POINT)
				|| agentState.equals(AgentState.TRANSPORT_OTHER_AGENTS);
	}

	/**
	 * Get path size.
	 * 
	 * @return path size
	 */
	public final int size() {
		return path.size();
	}
}
