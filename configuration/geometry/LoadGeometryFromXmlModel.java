package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;

import processing.core.PVector;
import uk.org.catapult.ts.cav.utils.ResourceUtilities;
import uk.org.catapult.ts.cav.utils.UtilityLogger;
import uk.org.catapult.ts.cav.utils.ResourceUtilities.ResourceExport;

/**
 * The static method of this class reads the geometry.xml file created with the
 * JuPedSim/ JPSEditor. It contains information about walls and buildings.
 * 
 * Wall example: <polygon caption="wall"> <vertex px="10" py="20"/>
 * <vertex px="32" py="0"/>
 * 
 * Obstacle example: <obstacle id="0" caption= "my obstacle" closed="1">
 * <polygon> <vertex px="32.6861" py= "21.7638"/>
 * <vertex px="32.6861" py="12.1359"/>
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
public final class LoadGeometryFromXmlModel {

	/** Logger. */
	private static final Logger LOGGER = UtilityLogger.getLogger(LoadGeometryFromXmlModel.class);

	/** A wall description as a polygon. <polygon caption="wall"> */
	private static final String XML_ELEMENT_POLYGON = "polygon";

	/** The number of attributes in the xml element describing the wall. */
	private static final int NUMBER_OF_ATTRIBUTES_IN_WALL = 1;

	/** Attribute position for the wall's caption (name). */
	private static final int WALL_ATTRIBUTE_ID_FOR_CAPTION = 0;

	/** Vertex XML element. */
	private static final String XML_ELEMENT_VERTEX = "vertex";

	/** Obstacle XML element. */
	private static final String XML_ELEMENT_OBSTACLE = "obstacle";

	/**
	 * Attribute position for the obstacle's name.
	 */
	private static final int OBSTACLE_ATTRIBUTE_ID_FOR_CAPTION = 1;

	/**
	 * Attribute position where the obstacle's type exist. It could be a closed
	 * or open polygon.
	 */
	private static final int OBSTACLE_ATTRIBUTE_ID_CLOSED = 2;

	/** The number of attributes the obstacle contains. */
	private static final int NUMBER_OF_ATTRIBUTES_IN_OBSTACLE = 3;

	/** The vertex X coordinate. */
	private static final int POSITION_VERTEX_X = 0;
	/**
	 * The vertex Y coordinate.
	 */
	private static final int POSITION_VERTEX_Y = 1;
	/** The vertex Z coordinate. */
	@SuppressWarnings("unused")
	private static final int POSITION_VERTEX_Z = 2;

	/** XML element for crossing. */
	private static final String XML_ELEMENT_CROSSING = "crossing";

	/** The position of the attribute Id in the Crossing element. */
	private static final int POSITION_ATTRIBUTE_ID_IN_CROSSING = 0;

	/**
	 * The number of attributes the element "crossing" contains.
	 */
	private static final int NUMBER_OF_ATTRIBUTES_IN_CROSSING = 3;

	/**
	 * The number of attributes the element "crossing" contains.
	 */
	private static final int NUMBER_OF_ATTRIBUTES_IN_TRANSITION_ZONES = 3;

	/** XML element for transition zones. */
	private static final String XML_ELEMENT_TRANSITION_ZONE = "transition";

	/** Position of the transition id attribute. */
	private static final int POSITION_XML_ATTRIBUTE_TRANSITION_ID = 0;

	/** Position of the transition caption. */
	private static final int POSITION_XML_ATTRIBUTE_TRANSITION_CAPTION = 1;

	/** Position of the transition type. */
	private static final int POSITION_XML_ATTRIBUTE_TRANSITION_TYPE = 2;

	/** Private constructor. */
	private LoadGeometryFromXmlModel() {
	}

	/**
	 * Read XML data from a JPSVIS configuration. Walls are open polygons,
	 * buildings are closed polygons and crossings are segments.
	 * 
	 * @param geometryRelativeFilePath
	 *            relative geometry file path
	 * 
	 * @return an object reflecting the data read from the XML file
	 */
	public static CatpedsimGeometry readXMLGeometry(final String geometryRelativeFilePath) {
		CatpedsimGeometry geometry = new CatpedsimGeometry();

		XMLInputFactory factory = XMLInputFactory.newInstance();

		try {
			File file = ResourceUtilities.getResource(geometryRelativeFilePath.trim(), ResourceExport.FILE);
			FileInputStream stream = new FileInputStream(file);

			XMLStreamReader xmlReader = factory.createXMLStreamReader(stream);

			while (xmlReader.hasNext()) {
				int eventType = xmlReader.next();

				if (eventType == XMLStreamReader.START_ELEMENT) {
					String elementName = xmlReader.getLocalName();

					if (elementName.equals(XML_ELEMENT_OBSTACLE)
							&& xmlReader.getAttributeCount() == NUMBER_OF_ATTRIBUTES_IN_OBSTACLE) {
						extractObstacles(xmlReader, geometry);
					}

					if (elementName.equals(XML_ELEMENT_POLYGON)
							&& xmlReader.getAttributeCount() == NUMBER_OF_ATTRIBUTES_IN_WALL) {
						extractWalls(xmlReader, geometry);
					}

					if (elementName.equals(XML_ELEMENT_CROSSING)
							&& xmlReader.getAttributeCount() == NUMBER_OF_ATTRIBUTES_IN_CROSSING) {
						extractCrossings(xmlReader, geometry);
					}

					if (elementName.equals(XML_ELEMENT_TRANSITION_ZONE)
							&& xmlReader.getAttributeCount() >= NUMBER_OF_ATTRIBUTES_IN_TRANSITION_ZONES) {
						extractTransitionZone(xmlReader, geometry);
					}
				}
			}

			xmlReader.close();
			stream.close();

		} catch (Exception e) {
			LOGGER.info("Error occured while reading the geometry from the xml file.", e);
		}

		return geometry;
	}

	/**
	 * Extract transfer zones pedestrians to pods and pedestrian to bicyles.
	 * 
	 * @param xmlReader
	 *            xml reader
	 * @param geometry
	 *            geometry object
	 * @exception Exception
	 *                on error
	 */
	private static void extractTransitionZone(final XMLStreamReader xmlReader, final CatpedsimGeometry geometry)
			throws Exception {
		String zoneSectionId = xmlReader.getAttributeValue(POSITION_XML_ATTRIBUTE_TRANSITION_ID).trim();
		String zoneName = xmlReader.getAttributeValue(POSITION_XML_ATTRIBUTE_TRANSITION_CAPTION).trim();
		String type = xmlReader.getAttributeValue(POSITION_XML_ATTRIBUTE_TRANSITION_TYPE).trim();

		PVector startSection = extractVertex(xmlReader);
		PVector endSection = extractVertex(xmlReader);

		if (startSection != null && endSection != null) {
			if (type.equalsIgnoreCase(TransitionZoneTypes.AUTONOMOUS_VEHICLE_RACK.getValue())) {
				geometry.addTransitionZone(TransitionZoneTypes.AUTONOMOUS_VEHICLE_RACK, zoneName, zoneSectionId,
						startSection, endSection);
			}
			if (type.equalsIgnoreCase(TransitionZoneTypes.BICYCLE_RACK.getValue())) {
				geometry.addTransitionZone(TransitionZoneTypes.BICYCLE_RACK, zoneName, zoneSectionId, startSection,
						endSection);
			}
		}
	}

	/**
	 * Extract vertices form the xml file.
	 * 
	 * @param xmlReader
	 *            xml reader
	 * @return vector to point
	 * @throws Exception
	 *             on error
	 */
	private static PVector extractVertex(final XMLStreamReader xmlReader) throws Exception {
		PVector vertex = null;

		while (xmlReader.hasNext()) {
			xmlReader.next();

			if (xmlReader.getEventType() == XMLStreamReader.START_ELEMENT
					&& xmlReader.getLocalName().equals(XML_ELEMENT_VERTEX) && xmlReader.getAttributeCount() >= 2) {

				String coordinateX = xmlReader.getAttributeValue(POSITION_VERTEX_X);
				String coordinateY = xmlReader.getAttributeValue(POSITION_VERTEX_Y);

				vertex = new PVector(Float.parseFloat(coordinateX), Float.parseFloat(coordinateY));
				break;
			}
		}

		return vertex;
	}

	/**
	 * Extract the obstacles from the XML file.
	 * 
	 * @param xmlReader
	 *            readerer to the XML stream
	 * @param geometry
	 *            read the XML information into a geometry object
	 * @throws Exception
	 *             on parsing error
	 */
	private static void extractObstacles(final XMLStreamReader xmlReader, final CatpedsimGeometry geometry)
			throws Exception {

		String obstacleName = xmlReader.getAttributeValue(OBSTACLE_ATTRIBUTE_ID_FOR_CAPTION);
		boolean closed = !Boolean.getBoolean(xmlReader.getAttributeValue(OBSTACLE_ATTRIBUTE_ID_CLOSED));

		CatpedsimObstacle obstacle = new CatpedsimObstacle(obstacleName, closed);
		geometry.addObstacle(obstacle);

		while (xmlReader.hasNext()) {
			xmlReader.next();

			if (xmlReader.getEventType() == XMLStreamReader.START_ELEMENT
					&& xmlReader.getLocalName().equals(XML_ELEMENT_POLYGON) && xmlReader.getAttributeCount() == 0) {

				while (xmlReader.hasNext()) {
					xmlReader.next();

					if (xmlReader.getEventType() == XMLStreamReader.END_ELEMENT
							&& xmlReader.getLocalName().equals(XML_ELEMENT_OBSTACLE)) {
						return;
					}

					if (xmlReader.getEventType() == XMLStreamReader.START_ELEMENT
							&& xmlReader.getLocalName().equals(XML_ELEMENT_VERTEX)
							&& xmlReader.getAttributeCount() >= 2) {

						String coordinateX = xmlReader.getAttributeValue(POSITION_VERTEX_X);
						String coordinateY = xmlReader.getAttributeValue(POSITION_VERTEX_Y);

						obstacle.addVertex(Float.parseFloat(coordinateX), Float.parseFloat(coordinateY));
					}
				}
			}
		}
	}

	/**
	 * Extract the walls from the XML file.
	 * 
	 * @param xmlReader
	 *            readerer to the XML stream
	 * @param geometry
	 *            read the XML information into a geometry object
	 * @throws Exception
	 *             on parsing error
	 */
	private static void extractWalls(final XMLStreamReader xmlReader, final CatpedsimGeometry geometry)
			throws Exception {
		String wallName = xmlReader.getAttributeValue(WALL_ATTRIBUTE_ID_FOR_CAPTION);

		CatpedsimObstacle newWall = new CatpedsimObstacle(wallName, false);
		geometry.addWall(newWall);

		while (xmlReader.hasNext()) {
			xmlReader.next();

			if (xmlReader.getEventType() == XMLStreamReader.END_ELEMENT
					&& xmlReader.getLocalName().equals(XML_ELEMENT_POLYGON)) {
				break;
			}

			if (xmlReader.getEventType() == XMLStreamReader.START_ELEMENT
					&& xmlReader.getLocalName().equals(XML_ELEMENT_VERTEX)
					&& xmlReader.getAttributeCount() > POSITION_VERTEX_Y) {

				String coordinateX = xmlReader.getAttributeValue(POSITION_VERTEX_X);
				String coordinateY = xmlReader.getAttributeValue(POSITION_VERTEX_Y);

				newWall.addVertex(Float.parseFloat(coordinateX), Float.parseFloat(coordinateY));
			}
		}
	}

	/**
	 * Extract from the XML file the crossings and add it to the geometry
	 * object.
	 * 
	 * @param xmlReader
	 *            the reader parsing the XML file
	 * @param geometry
	 *            geometry object
	 * @throws Exception
	 *             on error while parsing
	 */
	private static void extractCrossings(final XMLStreamReader xmlReader, final CatpedsimGeometry geometry)
			throws Exception {
		String crossingId = xmlReader.getAttributeValue(POSITION_ATTRIBUTE_ID_IN_CROSSING);

		CatpedsimCrossing newCrossing = new CatpedsimCrossing(crossingId);
		geometry.addCrossing(newCrossing);

		while (xmlReader.hasNext()) {
			xmlReader.next();

			if (xmlReader.getEventType() == XMLStreamReader.END_ELEMENT
					&& xmlReader.getLocalName().equals(XML_ELEMENT_CROSSING)) {
				return;
			}

			if (xmlReader.getEventType() == XMLStreamReader.START_ELEMENT
					&& xmlReader.getLocalName().equals(XML_ELEMENT_VERTEX)
					&& xmlReader.getAttributeCount() > POSITION_VERTEX_Y) {

				String coordinateX = xmlReader.getAttributeValue(POSITION_VERTEX_X);
				String coordinateY = xmlReader.getAttributeValue(POSITION_VERTEX_Y);

				newCrossing.addVertex(Float.parseFloat(coordinateX), Float.parseFloat(coordinateY));
			}
		}
	}
}
