/*
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.visual;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;
import uk.org.catapult.ts.cav.configuration.microsimulator.pedestrian.CatpedsimConfiguration;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.AgentBehaviour;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.AgentState;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.AutonomousVehicleConstraints;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.BicycleConstraints;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAgent;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAutonomousVehicle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimBicycle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimPedestrian;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimVehicle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.PedestrianConstraints;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.bridgeconnector.CatpedsimBridge;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimCrossing;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimObstacle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimTransitionZone;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.ShapeSection;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.TransitionZoneTypes;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.path.XmlPath;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.logiclayer.CatpedsimManager;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.shortestpath.NodePath;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.Utils;
import uk.org.catapult.ts.cav.model.Pedestrian;
import uk.org.catapult.ts.cav.model.Vehicle;
import uk.org.catapult.ts.cav.model.identifier.UniqueIdentifier;
import uk.org.catapult.ts.cav.model.simulation.SimulationTimeUnit;
import uk.org.catapult.ts.cav.utils.TimeUtilities;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * Note: for P3D renderer make sure you have the latest Display Adapter Driver.
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
public class CatpedsimApplet extends PApplet {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = UtilityLogger.getLogger(CatpedsimApplet.class);

	/**
	 * The simulation's logic layer.
	 */
	private CatpedsimManager simManager = null;

	/**
	 * Scale the world to fit the window and define an active area. The aspect
	 * ration has to be maintained. Active area width.
	 */
	private int activeAreaWidth = 0;

	/**
	 * Active area depth.
	 */
	private int activeAreaDepth = 0;

	/**
	 * The minimum coordinate of the active area along the X axis.
	 */
	private float activeAreaXMin = 0;

	/**
	 * The minimum coordinate of the active area along the Y axis.
	 */
	private float activeAreaYMin = 0;

	/** Zoom manager. */
	private final ZoomManager zoomManager = new ZoomManager();

	/**
	 * Text font type and size.
	 */
	private PFont font = null;

	/** Overall background color. */
	private final int backgroundColorRgb = 255;

	/**
	 * Collect statistics about the animation.
	 */
	private Statistics statistics;

	/**
	 * Stop calculating next agents action and display information about the
	 * current state of the agents.
	 */
	private boolean stoppedDuringDetailedInfo = false;

	/**
	 * Display agent names when the key t is pressed.
	 */
	private boolean toggleDetailedInfo = false;

	/**
	 * End simulation.
	 */
	public final void close() {
		stop();
		if (simManager != null) {
			simManager.clean();
		}
		dispose();
	}

	/**
	 * Reset evacuation and desired target settings.
	 */
	public final void clean() {
		simManager.clean();
	}

	/**
	 * Set the animation area size, tick unit and length, world dimension.
	 *
	 * @param newWidth
	 *            animation area width in pixels
	 * @param newDepth
	 *            animation area depth in pixels
	 * @param configuration
	 *            new configuration
	 * @param catpedsimBridge
	 *            a brige to the simulation, it observes events comming from the
	 *            logic layer
	 * @throws Exception
	 *             on error
	 */
	public final void setSimulationConfiguration(final int newWidth, final int newDepth,
			final CatpedsimConfiguration configuration, final CatpedsimBridge catpedsimBridge) throws Exception {
		simManager = new CatpedsimManager(newWidth, newDepth, configuration, catpedsimBridge);
		zoomManager.setOrigin(width / 2, height / 2);
	}

	/**
	 * Calculate the active area that maintains the world dimensions aspect
	 * ratio.
	 */
	public final void calculateActiveArea() {
		float worldUnitX = 1;
		float worldUnitY = 1;

		if (simManager.getWorldDepth() < simManager.getWorldWidth()) {
			worldUnitX = (float) simManager.getWorldWidth() / (float) simManager.getWorldDepth();
		} else {
			worldUnitY = (float) simManager.getWorldDepth() / (float) simManager.getWorldWidth();
		}

		float scale = 0;
		if (width >= height) {
			scale = (float) height / (float) worldUnitY;

			if (worldUnitX * scale > width) {
				scale = (float) width / (float) worldUnitX;
			}
		} else {
			scale = (float) width / (float) worldUnitX;

			if (scale * worldUnitY > height) {
				scale = (float) height / (float) worldUnitY;
			}
		}

		activeAreaWidth = (int) (worldUnitX * scale);
		activeAreaDepth = (int) (worldUnitY * scale);

		activeAreaXMin = (width - activeAreaWidth) / 2;
		activeAreaYMin = (height - activeAreaDepth) / 2;
	}

	/**
	 * The formula to calculate frames/seconds is:
	 * 
	 * frames / sec = 1 / (sec / frame).
	 * 
	 * @return frame rate / second
	 */
	public final float convertTickLengthInfoToFrameRatePerSecond() {
		return (1 / simManager.getTickLengthSeconds());
	}

	/**
	 * Set up the window width, depth and the type of renderer.
	 * 
	 * Types of renderers : JAVA2D, P2D, P3D
	 */
	@Override
	public final void settings() {
		size(simManager.getWindowWidth(), simManager.getWindowDepth(), JAVA2D);
	}

	/**
	 * Center the window on the screen.
	 */
	private void centreWindow() {
		frame.setLocationRelativeTo(null);
	}

	/**
	 * Set up the simulation area, the colour mode, the world background.
	 * Arrange the pedestrians randomly.
	 */
	public final void setup() {
		font = createFont(VisualConstants.DEFAULT_FONT, VisualConstants.TEXT_SIZE_10);
		surface.setResizable(true);
		surface.setTitle(VisualConstants.WINDOW_TITLE);
		centreWindow();

		smooth();
		strokeWeight(0);
		colorMode(RGB, VisualConstants.COLOR_RANGE);
		background(0);
		imageMode(CENTER);

		frameRate(convertTickLengthInfoToFrameRatePerSecond());

		try {
			calculateActiveArea();
		} catch (Exception ex) {
			LOGGER.warn("Error initilising the visualization.", ex);
		}

		statistics = new Statistics();

		noLoop();
	}

	/**
	 * Load the world's background. Load walls and obstacles (buildings).
	 * 
	 * @throws Exception
	 *             on error
	 */
	private void loadGeography() throws Exception {
		representWalls();
		representObstacles();
		representCrossings();
		representTransitionZones();
	}

	/**
	 * Read the geometry object and extract all the walls. The walls are stored
	 * in meters, map as to decide where on the world should be positioned.
	 * 
	 * @throws Exception
	 *             on error
	 */
	private void representWalls() throws Exception {
		for (int indexWall = 0; indexWall < simManager.numberOfWalls(); indexWall++) {
			CatpedsimObstacle wall = simManager.getWallFromListAtPosition(indexWall);

			beginShape();
			stroke(0);
			strokeWeight(1 / zoomManager.getScale());

			for (int indexSection = 0; indexSection < wall.getObstacleSections().length; indexSection++) {
				ShapeSection section = wall.getObstacleSections()[indexSection];

				PVector startVertex = convertCoordinatesToScreenPositions(section.getVectorToStartPoint().x,
						section.getVectorToStartPoint().y);
				PVector endVertex = convertCoordinatesToScreenPositions(section.getVectorToEndPoint().x,
						section.getVectorToEndPoint().y);

				vertex(startVertex.x, startVertex.y);
				vertex(endVertex.x, endVertex.y);

				line(startVertex.x, startVertex.y, endVertex.x, endVertex.y);
			}
			endShape();
		}
	}

	/**
	 * Read the geometry object and extract all the obstacles.
	 */
	private void representObstacles() {

		for (int indexObstacle = 0; indexObstacle < simManager.getNumberOfObstacles(); indexObstacle++) {
			CatpedsimObstacle obstacle = simManager.getObstacleFromListAtPosition(indexObstacle);

			beginShape();
			noStroke();
			fill(0);

			for (int indexSection = 0; indexSection < obstacle.getObstacleSections().length; indexSection++) {
				ShapeSection section = obstacle.getObstacleSections()[indexSection];

				PVector startVertex = convertCoordinatesToScreenPositions(section.getVectorToStartPoint().x,
						section.getVectorToStartPoint().y);
				PVector endVertex = convertCoordinatesToScreenPositions(section.getVectorToEndPoint().x,
						section.getVectorToEndPoint().y);

				vertex(startVertex.x, startVertex.y);
				vertex(endVertex.x, endVertex.y);
			}
			endShape(CLOSE);

			if (obstacle.isClosed()) {
				float textWidth = textWidth(obstacle.getName());
				textFont(font);
				textSize(VisualConstants.TEXT_SIZE_12);
				fill(Color.GREEN.getRGB());
				PVector vectorToTextPosition = new PVector(obstacle.getVectorToCentroid().x - textWidth / 2,
						(simManager.getWorldDepth() - obstacle.getVectorToCentroid().y));
				text(obstacle.getName(), vectorToTextPosition.x, vectorToTextPosition.y);
			}
		}
	}

	/**
	 * Represent visually all the crossings.
	 * 
	 * @throws Exception
	 *             on error
	 */
	private void representCrossings() throws Exception {
		for (int i = 0; i < simManager.numberOfCrossings(); i++) {
			CatpedsimCrossing crossing = simManager.getCrossingFromListAtPosition(i);

			beginShape();
			stroke(Color.RED.getRGB());
			strokeWeight(2 / zoomManager.getScale());

			PVector startVertex = convertCoordinatesToScreenPositions(crossing.getVertexAtIndex(0).x,
					crossing.getVertexAtIndex(0).y);
			PVector endVertex = convertCoordinatesToScreenPositions(crossing.getVertexAtIndex(1).x,
					crossing.getVertexAtIndex(1).y);

			drawDashLine(g, startVertex.x, startVertex.y, endVertex.x, endVertex.y,
					new float[] { 2 / zoomManager.getScale(), (2 + 2) / zoomManager.getScale() });
			endShape();
		}
	}

	/**
	 * Represent visually all transit zones.
	 * 
	 * @throws Exception
	 *             on error
	 */
	private void representTransitionZones() throws Exception {

		final float defaultStrokeWeightForTransitionZones = 0.7f;

		Map<TransitionZoneTypes, Map<String, CatpedsimTransitionZone>> transitionZones = simManager
				.getTransitionZones();

		for (Map.Entry<TransitionZoneTypes, Map<String, CatpedsimTransitionZone>> mapEntry : transitionZones
				.entrySet()) {
			TransitionZoneTypes transitionType = mapEntry.getKey();
			Color color = transitionType.getColor();

			Map<String, CatpedsimTransitionZone> zoneMap = mapEntry.getValue();

			for (Map.Entry<String, CatpedsimTransitionZone> zones : zoneMap.entrySet()) {
				CatpedsimTransitionZone transitionZone = (CatpedsimTransitionZone) zones.getValue();
				LinkedList<ShapeSection> zoneSections = transitionZone.getSections();

				for (ShapeSection section : zoneSections) {
					beginShape();
					stroke(color(color.getRGB()));
					strokeWeight(defaultStrokeWeightForTransitionZones / zoomManager.getScale());

					PVector startVertex = convertCoordinatesToScreenPositions(section.getVectorToStartPoint().x,
							section.getVectorToStartPoint().y);
					PVector endVertex = convertCoordinatesToScreenPositions(section.getVectorToEndPoint().x,
							section.getVectorToEndPoint().y);

					vertex(startVertex.x, startVertex.y);
					vertex(endVertex.x, endVertex.y);

					endShape(CLOSE);
				}
			}
		}
	}

	/**
	 * Main animation thread. The desired area is set with a mouse click, if
	 * none is defined the target is random and the pedestrians move
	 * chaotically.
	 */
	public final void draw() {
		background(backgroundColorRgb);
		noStroke();
		smooth();

		try {
			pushMatrix();
			translate(zoomManager.getxOrigin(), zoomManager.getyOrigin());
			scale(zoomManager.getScale());
			rotate(zoomManager.getAngle());
			loadGeography();

			if (stoppedDuringDetailedInfo) {
				drawAllAgents();
				displayAgentInformation();
			} else if (toggleDetailedInfo) {
				simManager.generateDepartures();
				simManager.fillTransferZonesWithAgents();
				simManager.calculateAllAgentsNextAction();
				simManager.updateTransportedAgentsPositions();
				displayMinimumInformation();
			} else {
				simManager.generateDepartures();
				simManager.fillTransferZonesWithAgents();
				simManager.calculateAllAgentsNextAction();
				simManager.updateTransportedAgentsPositions();
				drawAllAgents();
			}
		} catch (Exception e) {
			LOGGER.warn("Error while calculating the next step. ", e);
		} finally {
			popMatrix();
			statistics.display();
		}

		simManager.incrementTickCount();
		simManager.notifyAdvanceTimeStepCompleted();
	}

	/**
	 * Display a top label with agent minimum information.
	 */
	private void displayMinimumInformation() {
		if (toggleDetailedInfo) {
			Iterator<Entry<Class<?>, List<CatpedsimAgent>>> agentMapIterator = simManager.getMapOfAgentsLists()
					.entrySet().iterator();

			while (agentMapIterator.hasNext()) {
				Map.Entry<Class<?>, List<CatpedsimAgent>> agentMapEntry = (Map.Entry<Class<?>, List<CatpedsimAgent>>) agentMapIterator
						.next();
				Class<?> agentClassType = agentMapEntry.getKey();
				List<CatpedsimAgent> listOfSelectedAgents = simManager.getListOfAgents(agentClassType);

				if (listOfSelectedAgents == null) {
					continue;
				}

				try {
					synchronized (listOfSelectedAgents) {
						for (CatpedsimAgent agent : listOfSelectedAgents) {
							boolean isNotTransported = true;
							AgentBehaviour sequenceOfBehaviours = agent.getSequenceOfBehaviours();

							if (sequenceOfBehaviours != null) {
								XmlPath currentPath = sequenceOfBehaviours.getCurrentBehaviour();

								if (currentPath != null && currentPath.getAgentState()
										.equals(AgentState.TRANSPORTED_BY_ANOTHER_AGENT)) {
									isNotTransported = false;
								}
							}

							if (isNotTransported) {
								if (agentClassType.equals(CatpedsimPedestrian.class)) {
									representPedestrianVisually(agent);
								} else if (agentClassType.equals(CatpedsimAutonomousVehicle.class)) {
									representAutonomousVehicleVisually(agent);
								} else if (agentClassType.equals(CatpedsimBicycle.class)) {
									representBicyclesVisually(agent);
								}
							}
							displayAgentMinimumInformation(agent, isNotTransported);
						}
					}
				} catch (Exception ex) {
					LOGGER.warn("Error while displaying pedestrians. ", ex);
				}
			}
		}
	}

	/**
	 * Display agent with name floating on the top.
	 * 
	 * @param agent
	 *            agent object
	 * @param isNotTransported
	 *            agent is not transported
	 */
	private void displayAgentMinimumInformation(final CatpedsimAgent agent, final boolean isNotTransported) {
		textFont(font);
		textSize(VisualConstants.TEXT_SIZE_10 / zoomManager.getScale());
		noStroke();
		rectMode(CORNER);
		PVector screen = convertCoordinatesToScreenPositions(agent.getPositionVector().x, agent.getPositionVector().y);
		String agentIndex = CatpedsimAgent.extractSimpleName(agent.getName()).replaceAll("[^\\d]", "");
		String informationToDisplay = "";

		if (agent instanceof CatpedsimPedestrian) {
			informationToDisplay = "Ped " + agentIndex;
		} else if (agent instanceof CatpedsimAutonomousVehicle) {
			informationToDisplay = "Pod " + agentIndex;
		} else if (agent instanceof CatpedsimBicycle) {
			informationToDisplay = "Bike  " + agentIndex;
		}

		int rectWidth = (int) ((textWidth(informationToDisplay) * 2));
		int rectHeight = (int) ((textAscent() + textDescent()));
		float xCoordinate = screen.x;
		float yCoordinate = screen.y - rectHeight;

		if (!isNotTransported) {
			xCoordinate = screen.x;
			yCoordinate = screen.y;
		}

		fill(agent.getColorToRepresent().getRGB());
		text(informationToDisplay, xCoordinate, yCoordinate, rectWidth, rectHeight);
	}

	/**
	 * Draw all the agents present in the simulation. The safest way to loop
	 * though a Hash Map is to use an iterator.
	 * 
	 * @throws Exception
	 *             on error
	 */
	private void drawAllAgents() throws Exception {
		Iterator<Entry<Class<?>, List<CatpedsimAgent>>> agentMapIterator = simManager.getMapOfAgentsLists().entrySet()
				.iterator();

		while (agentMapIterator.hasNext()) {
			Map.Entry<Class<?>, List<CatpedsimAgent>> agentMapEntry = (Map.Entry<Class<?>, List<CatpedsimAgent>>) agentMapIterator
					.next();
			Class<?> agentClassType = agentMapEntry.getKey();
			List<CatpedsimAgent> listOfSelectedAgents = simManager.getListOfAgents(agentClassType);

			if (listOfSelectedAgents == null) {
				continue;
			}

			try {
				synchronized (listOfSelectedAgents) {
					for (CatpedsimAgent agent : listOfSelectedAgents) {
						agent.toString();

						boolean drawAgent = true;
						AgentBehaviour sequenceOfBehaviours = agent.getSequenceOfBehaviours();

						if (sequenceOfBehaviours != null) {
							XmlPath currentPath = sequenceOfBehaviours.getCurrentBehaviour();

							if (currentPath != null
									&& currentPath.getAgentState().equals(AgentState.TRANSPORTED_BY_ANOTHER_AGENT)) {
								drawAgent = false;
							}
						}

						if (drawAgent) {
							if (agentClassType.equals(CatpedsimPedestrian.class)) {
								representPedestrianVisually(agent);
							} else if (agentClassType.equals(CatpedsimAutonomousVehicle.class)) {
								representAutonomousVehicleVisually(agent);
							} else if (agentClassType.equals(CatpedsimBicycle.class)) {
								representBicyclesVisually(agent);
							} else if (agentClassType.equals(CatpedsimVehicle.class)) {
								representExternalVehicleVisually(agent);
							}
						}
					}
				}
			} catch (Exception ex) {
				LOGGER.warn("Error while displaying pedestrians. ", ex);
			}

		}
	}

	/**
	 * Represent the pedestrian visually. Convert from the world map to window.
	 * 
	 * @param catpedsimAgent
	 *            current pedestrian
	 * 
	 */
	private void representPedestrianVisually(final CatpedsimAgent catpedsimAgent) {
		noStroke();
		strokeWeight(1.0f / zoomManager.getScale());
		fill(0);

		try {
			PVector screenPosition = convertCoordinatesToScreenPositions(catpedsimAgent.getPositionVector().x,
					catpedsimAgent.getPositionVector().y);
			float x = screenPosition.x;
			float y = screenPosition.y;

			pedestrianAsEllipse(catpedsimAgent, x, y);
		} catch (Exception ex) {
			LOGGER.warn("Error representing the pedestrians.", ex);
		}
	}

	/**
	 * Represent the autonomous vehicle visually. Convert from the world map to
	 * window.
	 * 
	 * @param catpedsimAgent
	 *            current autonomous agent
	 * 
	 */
	private void representBicyclesVisually(final CatpedsimAgent catpedsimAgent) {
		stroke(0);
		strokeWeight(VisualConstants.STROKE_WEIGHT_AGENTS / zoomManager.getScale());

		try {
			PVector screenPosition = convertCoordinatesToScreenPositions(catpedsimAgent.getPositionVector().x,
					catpedsimAgent.getPositionVector().y);
			float x = screenPosition.x;
			float y = screenPosition.y;

			PVector heading = catpedsimAgent.getVelocity().copy();
			heading.set(heading.x, -heading.y, heading.z);

			float bicycleLength = convertYDimensionFromMetersToPixels(BicycleConstraints.LENGTH);

			float theta = heading.heading();
			pushMatrix();
			translate(x, y);
			rotate(theta);
			beginShape();
			line(-bicycleLength, 0, bicycleLength, 0);
			stroke(Color.RED.getRGB());
			line(0, 0, bicycleLength, 0);
			endShape();
			popMatrix();

		} catch (Exception ex) {
			LOGGER.warn("Error representing the bicycles.", ex);
		}
	}

	/**
	 * Draw the external vehicle. Convert from the world map to window.
	 * 
	 * @param newCatpedsimAgent
	 *            agent object
	 */
	private void representExternalVehicleVisually(final CatpedsimAgent newCatpedsimAgent) {
		final int red = 128;
		final int green = 0;
		final int blue = 0;
		stroke(red, green, blue);
		strokeWeight(VisualConstants.STROKE_WEIGHT_AGENTS / zoomManager.getScale());

		try {
			PVector screenPosition = convertCoordinatesToScreenPositions(newCatpedsimAgent.getPositionVector().x,
					newCatpedsimAgent.getPositionVector().y);
			float x = screenPosition.x;
			float y = screenPosition.y;

			PVector heading = newCatpedsimAgent.getVelocity().copy();
			heading.set(heading.x, -heading.y, heading.z);

			float theta = heading.heading() + PI / 2;
			float rectWidth = convertXDimensionFromMetersToPixels(AutonomousVehicleConstraints.WIDTH);
			float rectDepth = convertYDimensionFromMetersToPixels(AutonomousVehicleConstraints.LENGTH);

			pushMatrix();
			translate(x, y);
			rotate(theta);
			beginShape();
			rectMode(CENTER);
			fill(Color.YELLOW.getRGB());
			rect(0, 0, rectWidth, rectDepth);
			stroke(Color.GREEN.getRGB());
			line(rectWidth / 2, -rectDepth / 2, -rectWidth / 2, -rectDepth / 2);
			endShape();
			popMatrix();

		} catch (Exception ex) {
			LOGGER.warn("Error representing the autonomous vehicles", ex);
		}
	}

	/**
	 * Represent the autonomous vehicle visually. Convert from the world map to
	 * window.
	 * 
	 * @param catpedsimAgent
	 *            current autonomous agent
	 * 
	 */
	private void representAutonomousVehicleVisually(final CatpedsimAgent catpedsimAgent) {
		stroke(Color.BLUE.getRGB());
		strokeWeight(VisualConstants.STROKE_WEIGHT_AGENTS / zoomManager.getScale());

		try {
			PVector screenPosition = convertCoordinatesToScreenPositions(catpedsimAgent.getPositionVector().x,
					catpedsimAgent.getPositionVector().y);
			float x = screenPosition.x;
			float y = screenPosition.y;

			PVector heading = catpedsimAgent.getVelocity().copy();
			heading.set(heading.x, -heading.y, heading.z);

			float theta = heading.heading() + PI / 2;
			float rectWidth = convertXDimensionFromMetersToPixels(AutonomousVehicleConstraints.WIDTH);
			float rectDepth = convertYDimensionFromMetersToPixels(AutonomousVehicleConstraints.LENGTH);

			pushMatrix();
			translate(x, y);
			rotate(theta);
			beginShape();
			rectMode(CENTER);
			fill(Color.RED.getRGB());
			rect(0, 0, rectWidth, rectDepth);
			stroke(Color.GREEN.getRGB());
			line(rectWidth / 2, -rectDepth / 2, -rectWidth / 2, -rectDepth / 2);
			endShape();
			popMatrix();
		} catch (Exception ex) {
			LOGGER.warn("Error representing the autonomous vehicles", ex);
		}
	}

	/**
	 * Convert cartesian coordinates to screen positions.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return vector to screen position
	 */
	public final PVector convertCoordinatesToScreenPositions(final float x, final float y) {
		float x1 = x;
		float y1 = simManager.getWorldDepth() - y;

		float x2 = activeAreaXMin + map(x1, 0, simManager.getWorldWidth() - 1, 0, activeAreaWidth - 1);
		float y2 = activeAreaYMin + map(y1, 0, simManager.getWorldDepth() - 1, 0, activeAreaDepth - 1);

		return new PVector(x2, y2);
	}

	/**
	 * Convert x coordinates from meters to screen pixels.
	 * 
	 * @param dimensionMeters
	 *            x coordinate in meters
	 * @return x coordinate in pixels
	 */
	private float convertXDimensionFromMetersToPixels(final float dimensionMeters) {
		return map(dimensionMeters, 0, simManager.getWorldWidth() - 1, 0, activeAreaWidth - 1);
	}

	/**
	 * Convert y coordinates from meters to screen pixels.
	 * 
	 * @param dimensionMeters
	 *            y coordinate in meters
	 * @return y coordinate in pixels
	 */
	private float convertYDimensionFromMetersToPixels(final float dimensionMeters) {
		return map(dimensionMeters, 0, simManager.getWorldDepth() - 1, 0, activeAreaDepth - 1);
	}

	/**
	 * Display as dots the agent precomputed path. Used for debugging purposes.
	 * 
	 * @param agent
	 *            agent
	 */
	@SuppressWarnings("unused")
	private void showAgentPath(final CatpedsimAgent agent) {
		if (agent.getPathToFollow() == null) {
			return;
		}

		for (int i = 0; i < agent.getPathToFollow().size(); i++) {
			NodePath node = agent.getPathToFollow().get(i);
			PVector position = convertCoordinatesToScreenPositions(node.getX(), node.getY());

			pushMatrix();
			translate(position.x, position.y);
			stroke(agent.getColorToRepresent().getRGB());
			line(0, -VisualConstants.SMALL_DOT_IN_PIXELS, 0, VisualConstants.SMALL_DOT_IN_PIXELS);
			line(VisualConstants.SMALL_DOT_IN_PIXELS, 0, -VisualConstants.SMALL_DOT_IN_PIXELS, 0);
			popMatrix();
		}
	}

	/**
	 * Represent the pedestrian as an ellipse.
	 * 
	 * @param catpedsimAgent
	 *            agent
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	private void pedestrianAsEllipse(final CatpedsimAgent catpedsimAgent, final float x, final float y) {
		PVector heading = catpedsimAgent.getVelocity().copy();
		heading.set(heading.x, -heading.y, heading.z);

		float theta = heading.heading() + PI / 2;
		float radius = convertXDimensionFromMetersToPixels(PedestrianConstraints.SHOULDER_WIDTH);

		fill(catpedsimAgent.getColorToRepresent().getRGB());
		strokeWeight(VisualConstants.STROKE_WEIGHT_AGENTS / zoomManager.getScale());
		pushMatrix();
		translate(x, y);
		rotate(theta);
		rectMode(CENTER);
		ellipse(0, 0, radius, radius);
		popMatrix();
	}

	/**
	 * Display agent information in a rectangle.
	 * 
	 * @param agentInformation
	 *            text agent information
	 */
	private void displayAgentInformation(final String agentInformation) {
		textFont(font);
		textSize(VisualConstants.TEXT_SIZE_10);
		noStroke();
		rectMode(CORNER);
		final int xOffset = 20;
		final int yOffset = 10;
		final int transparency = 125;

		float xCoordinate = mouseX - xOffset;
		float yCoordinate = mouseY - yOffset;
		int rectWidth = (int) (VisualConstants.TEXT_RECTANGLE_WIDTH / zoomManager.getScale());
		int rectHeight = (int) ((textAscent() + textDescent()) * (agentInformation.split("\n").length + 2));
		fill(VisualConstants.LIGHT_GRAY_RGB, transparency);
		rect(xCoordinate, yCoordinate, rectWidth, rectHeight);
		fill(0);
		text(agentInformation, xCoordinate, yCoordinate, rectWidth, rectHeight);
	}

	/**
	 * Display agent information.
	 */
	public final void displayAgentInformation() {
		if (stoppedDuringDetailedInfo) {
			PVector mouseVector = new PVector(mouseX, mouseY);

			final float maxDistancePixels = 5;

			for (Entry<Class<?>, List<CatpedsimAgent>> entry : simManager.getMapOfAgentsLists().entrySet()) {
				List<CatpedsimAgent> listOfAgents = entry.getValue();

				for (CatpedsimAgent agent : listOfAgents) {
					PVector agentScreenVector = convertCoordinatesToScreenPositions(agent.getPositionVector().x,
							agent.getPositionVector().y);

					if (agent.isTransported()) {
						continue;
					}

					if (PVector.dist(agentScreenVector, mouseVector) <= maxDistancePixels) {
						displayAgentInformation(agent.toString());
					}
				}
			}
		}
	}

	/**
	 * Zooom in an out in the animation.
	 * 
	 * The mouseWheel() function returns positive values when the mouse wheel is
	 * rotated down (toward the user), and negative values for the other
	 * direction (up or away from the user). On OS X with "natural" scrolling
	 * enabled, the values are opposite.
	 */
	@Override
	public final void mouseWheel(final processing.event.MouseEvent event) {
		float eventCount = event.getCount();

		if (eventCount > 0) {
			float previousScale = zoomManager.getScale();
			float currentScale = zoomManager.decrementScale();

			if (Math.abs(previousScale - currentScale) > Utils.FLOAT_TOLERANCE) {
				zoomManager.offsetOrigin(mouseX, mouseY);
			}
		} else if (eventCount < 0) {
			zoomManager.incrementScale();
			zoomManager.offsetOrigin(-pmouseX, -pmouseY);
		}
	}

	/**
	 * Change the target are for pedestrians during mouse drag.
	 */
	@Override
	public final void mouseDragged() {
		zoomManager.offsetOrigin(mouseX - pmouseX, mouseY - pmouseY);
	}

	/**
	 * Key pressed event. When the key p,P is pressed, the simulation pauses and
	 * the user can investigate the animation stat.
	 * 
	 * P = pause
	 * 
	 * T = toggle information
	 * 
	 * E = evacuation
	 */
	@Override
	public final void keyPressed() {
		if (key == 'p' || key == 'P') {
			stoppedDuringDetailedInfo = !stoppedDuringDetailedInfo;
		} else if (key == 't' || key == 'T') {
			toggleDetailedInfo = !toggleDetailedInfo;
		} else if (key == 'e' || key == 'E') {
			simManager.randomEvacuation();
		}
	}

	/**
	 * Displays information about the pedestrians.
	 *
	 * @author Ecaterina McCormick on 23 Nov 2015
	 *
	 */
	class Statistics {
		/** Text height. */
		private float textHeight = 0;

		/** X coordinate where the text starts. */
		private static final float TEXT_X_COORDINATE_START = 15;

		/** Text scale. */
		private static final float INCREASE_TEXT_PROPORTION = 3 / 10;

		/** Offset start ext for every line. */
		private static final float TEXT_LINE_START_X = 10;

		/** Distance between the lines. */
		private static final float DISTANCE_BETWEEN_LINES = 5;

		/**
		 * Constructor.
		 */
		Statistics() {
			textSize(VisualConstants.TEXT_SIZE_10);
			textHeight = textAscent() + textDescent();
			textHeight += textHeight * INCREASE_TEXT_PROPORTION;
		}

		/**
		 * Display information about the simulation.
		 * 
		 * @throws Exception
		 *             on error
		 */
		void display() {
			textFont(font);
			textSize(VisualConstants.TEXT_SIZE_10);
			noStroke();

			int indexText = 1;
			fill(0);
			textSize(VisualConstants.TEXT_SIZE_12);

			text("Tick Length: " + simManager.getTickLengthSeconds() + " (s) ", TEXT_X_COORDINATE_START,
					height - textHeight * (indexText++));

			text("World: " + simManager.getWorldWidth() + " x " + simManager.getWorldDepth() + " m ",
					TEXT_X_COORDINATE_START, height - textHeight * (indexText++));

			final int pedestrianPoint1X = 0;
			final int pedestrianPoint1Y = -6;
			final int pedestrianPoint2X = -3;
			final int pedestrianPoint2Y = 6;
			final int pedestrianPoint3X = 3;
			final int pedestrianPoint3Y = 6;
			fill(0);
			pushMatrix();
			translate(TEXT_LINE_START_X, height - textHeight * indexText - DISTANCE_BETWEEN_LINES);
			beginShape();
			vertex(pedestrianPoint1X, -pedestrianPoint1Y);
			vertex(pedestrianPoint2X, pedestrianPoint2Y);
			vertex(pedestrianPoint3X, pedestrianPoint3Y);
			endShape(CLOSE);
			popMatrix();

			fill(0);
			text("Pedestrians: " + simManager.getNumberOfPedestrians(), TEXT_X_COORDINATE_START,
					height - textHeight * (indexText++));

			text("Bicycles: " + simManager.getNumberOfBicycles(), TEXT_X_COORDINATE_START,
					height - textHeight * (indexText++));

			stroke(Color.BLUE.getRGB());
			strokeWeight(2f / zoomManager.getScale());
			fill(Color.RED.getRGB());
			rectMode(CENTER);
			final int podHeight = 9;
			rect(TEXT_LINE_START_X, height - textHeight * indexText - DISTANCE_BETWEEN_LINES, DISTANCE_BETWEEN_LINES,
					podHeight);

			fill(0);
			text("Pods: " + simManager.getNumberOfAutonomousVehicles(), TEXT_X_COORDINATE_START,
					height - textHeight * (indexText++));

			text("Ticks: " + simManager.getTickCounterInformation(), TEXT_X_COORDINATE_START,
					height - textHeight * (indexText++));
		}
	}

	/**
	 * Add pedestrian in the current simulation.
	 *
	 * @param publisherId
	 *            publisher id that is adding the pedestrian
	 * @param name
	 *            name of the pedestrian
	 * @param
	 * @param position
	 *            position position in the cartesian space
	 */
	public final void addPedestrian(final String publisherId, final String name, final Point2D position) {
		simManager.addPedestrian(publisherId, name, position);
	}

	/**
	 * Add pedestrian at a random location.
	 * 
	 * @param publisherId
	 *            publisher id that is adding the pedestrian
	 * @param name
	 *            name of the pedestrian
	 * @throws Exception
	 *             on error
	 */
	public final void addPedestrian(final String publisherId, final String name) throws Exception {
		simManager.addPedestrian(publisherId, name);
	}

	/**
	 * Add new pedestrian.
	 * 
	 * @param publisherId
	 *            id of the simulator that is adding the pedestrian
	 * @param pedestrian
	 *            pedestrian to be added
	 * @throws Exception
	 *             on error
	 */
	public final void addPedestrian(final String publisherId, final Pedestrian pedestrian) throws Exception {
		simManager.addPedestrianFromTheExternalPedestrianModel(publisherId, pedestrian);
	}

	/**
	 * Update pedestrian.
	 * 
	 * @param publisherId
	 *            id of the simulator that is updating the pedestrian
	 * @param pedestrian
	 *            pedestrian to be updated
	 * @throws Exception
	 *             on error
	 */
	public final void updatePedestrian(final String publisherId, final Pedestrian pedestrian) throws Exception {
		simManager.addOrUpdatePedestrian(publisherId, pedestrian);
	}

	/**
	 * Generate random pedestrians.
	 * 
	 * @param publisherId
	 *            who is adding the pedestrian
	 * @throws Exception
	 *             on error
	 */
	public final void generateRandomAgents(final String publisherId) throws Exception {
		if (simManager != null) {
			simManager.initialiseRandomPedestrians(publisherId);
			simManager.initialiseRandomAutonomousVechicles(publisherId);
			simManager.initialiseRandomBicycles(publisherId);
		}
	}

	/**
	 * Get the list of all the agents.
	 * 
	 * @param agentType
	 *            type of agent
	 * @return list of pedestrians
	 */
	public final List<CatpedsimAgent> getListOfAgents(final Class<?> agentType) {
		List<CatpedsimAgent> agents = null;
		if (simManager != null) {
			agents = simManager.getListOfAgents(agentType);
		}
		return agents;
	}

	/**
	 * Get the list of walls in the environment.
	 * 
	 * @return list of walls
	 */
	public final List<CatpedsimObstacle> getListOfWalls() {
		List<CatpedsimObstacle> walls = null;
		if (simManager != null) {
			walls = simManager.getListOfWalls();
		}
		return walls;
	}

	/**
	 * Get the list of obstacles in the environment.
	 * 
	 * @return list of obstacles
	 */
	public final List<CatpedsimObstacle> getListOfObstacles() {
		List<CatpedsimObstacle> obstacles = null;
		if (simManager != null) {
			obstacles = simManager.getListOfWalls();
		}
		return obstacles;
	}

	/**
	 * Advance a number of steps in the simulation. When the iterations are
	 * finished, notify (unlock) the caller. When the simulation tick unit is
	 * not the same as the tick unit used in advanceTimeStep convert to internal
	 * conventions.
	 * 
	 * @param timestep
	 *            time step to advance
	 * @param unit
	 *            time unit
	 */
	@SuppressWarnings("unused")
	public final void advanceTimeStep(final double timestep, final SimulationTimeUnit unit) {
		try {
			float newTimeStep = (float) timestep;

			if (!unit.equals(simManager.getSimulationTimeUnit())) {
				float tickUnitResolution = (float) (TimeUtilities.convertTime(1, simManager.getSimulationTimeUnit(),
						SimulationTimeUnit.SECONDS) / TimeUtilities.convertTime(1, unit, SimulationTimeUnit.SECONDS));

				newTimeStep = (float) (tickUnitResolution * timestep);
			}

			redraw();
		} catch (Exception e) {
			LOGGER.warn("Could not get the simulation time unit, check the simulation was properly initialized! ", e);
		}
	}

	/**
	 * Get internal tick count.
	 * 
	 * @return internal ticks count
	 */
	public final long getInternalTickCount() {
		return simManager.getNumberOfTicksAsLong();
	}

	/**
	 * Draw a dashed line with given set of dashes and gap lengths. x0 starting
	 * x-coordinate of line. y0 starting y-coordinate of line. x1 ending
	 * x-coordinate of line. y1 ending y-coordinate of line. spacing array
	 * giving lengths of dashes and gaps in pixels; an array with values {5, 3,
	 * 9, 4} will draw a line with a 5-pixel dash, 3-pixel gap, 9-pixel dash,
	 * and 4-pixel gap. if the array has an odd number of entries, the values
	 * are recycled, so an array of {5, 3, 2} will draw a line with a 5-pixel
	 * dash, 3-pixel gap, 2-pixel dash, 5-pixel gap, 3-pixel dash, and 2-pixel
	 * gap, then repeat.
	 * 
	 * @param myWorldBackground
	 *            world background
	 * @param linePointStartX
	 *            start point for line, x coordinate
	 * @param linePointStartY
	 *            start point for line, y coordinate
	 * @param linePointEndX
	 *            end point for line, x coordinate
	 * @param linePointEndY
	 *            point for line, y coordinate
	 * @param spacing
	 *            line spacing
	 */
	private void drawDashLine(final PGraphics myWorldBackground, final float linePointStartX,
			final float linePointStartY, final float linePointEndX, final float linePointEndY, final float[] spacing) {

		float distance = dist(linePointStartX, linePointStartY, linePointEndX, linePointEndY);
		float[] xSpacing = new float[spacing.length];
		float[] ySpacing = new float[spacing.length];
		float starLineX = linePointStartX;
		float startLineY = linePointStartY;

		float drawn = 0.0f;

		if (distance > 0) {
			int i = 0;
			boolean drawLine = true;

			for (i = 0; i < spacing.length; i++) {
				xSpacing[i] = lerp(0, (linePointEndX - starLineX), spacing[i] / distance);
				ySpacing[i] = lerp(0, (linePointEndY - startLineY), spacing[i] / distance);
			}

			i = 0;
			while (drawn < distance) {
				if (drawLine) {
					myWorldBackground.line(starLineX, startLineY, starLineX + xSpacing[i], startLineY + ySpacing[i]);
				}
				starLineX += xSpacing[i];
				startLineY += ySpacing[i];

				drawn = drawn + mag(xSpacing[i], ySpacing[i]);
				i = (i + 1) % spacing.length;
				drawLine = !drawLine;
			}
		}
	}

	/**
	 * Get a random exit from the geometry of the simulator.
	 * 
	 * @return a random exit
	 */
	public final CatpedsimCrossing getRandomExit() {
		return simManager.getRandomExit();
	}

	/**
	 * Update external vehicle.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param vehicle
	 *            vehicle object
	 */
	public final void updateExternalVehicle(final String publisherId, final Vehicle vehicle) {
		try {
			simManager.updateOrAddExternalVehicle(publisherId, vehicle);
		} catch (Exception ex) {
			LOGGER.warn("The external vehicle could not be updated.", ex);
		}
	}

	/**
	 * Get agent by name.
	 * 
	 * @param identifier
	 *            agent unique identifier
	 * @return agent object
	 */
	public final CatpedsimAgent getAgentByName(final UniqueIdentifier identifier) {
		return simManager.getAgentByName(identifier);
	}

	/**
	 * Delete agent based on unique identifier with external request.
	 * 
	 * @param uniqueIdentifier
	 *            unique identifier for the agent to be deleted
	 * @throws Exception
	 *             on error
	 */
	public final void deleteExternalAgent(final UniqueIdentifier uniqueIdentifier) throws Exception {
		if (simManager != null) {
			simManager.deleteExternalAgent(uniqueIdentifier);
		} else {
			throw new Exception("The Manager in the logic layer is null!");
		}
	}

	/**
	 * Zoom, pan tilt manager.
	 * 
	 * @author Ecaterina McCormick
	 *
	 */
	private class ZoomManager {
		/** Minimum scale or maximum level of zoom out. */
		private final float minimumScale = 0.5f;

		/** Scale factor. */
		private float scale = 1;

		/** The x coordinate of the translation point. */
		private float xOrigin = 0;

		/** The y coordinate of the translation point. */
		private float yOrigin = 0;

		/** Angle of graphic rotation. */
		private float angle = 0;

		/** The step allowed during the incremental zoom. */
		private final float zoomStep = 1f;

		/**
		 * Get the scale value.
		 * 
		 * @return scale value
		 */
		public float getScale() {
			return scale;
		}

		/**
		 * Decrement scale with one step.
		 * 
		 * @return the value of the new scale
		 */
		public float decrementScale() {
			scale = scale - zoomStep;
			if (scale < minimumScale) {
				scale = minimumScale;
			}
			return scale;
		}

		/**
		 * Increment scale with one step.
		 */
		public void incrementScale() {
			scale = scale + zoomStep;
		}

		/**
		 * Set a new origin for the translation process.
		 * 
		 * @param newXOrigin
		 *            new x coordinate for translation
		 * @param newYOrigin
		 *            new y coordinate for translation
		 */
		public void setOrigin(final float newXOrigin, final float newYOrigin) {
			xOrigin = newXOrigin;
			yOrigin = newYOrigin;
		}

		/**
		 * Osset the transaltion origin.
		 * 
		 * @param offsetX
		 *            x value for the offset
		 * @param offsetY
		 *            y value for the offset
		 */
		public void offsetOrigin(final float offsetX, final float offsetY) {
			xOrigin = xOrigin + offsetX;
			yOrigin = yOrigin + offsetY;
		}

		/**
		 * Get x translation origin.
		 * 
		 * @return x translation origin
		 */
		public float getxOrigin() {
			return xOrigin;
		}

		/**
		 * Get y translation origin.
		 * 
		 * @return y translation origin
		 */
		public float getyOrigin() {
			return yOrigin;
		}

		/**
		 * Get the rotation angle.
		 * 
		 * @return rotation angle
		 */
		public float getAngle() {
			return angle;
		}
	}
}