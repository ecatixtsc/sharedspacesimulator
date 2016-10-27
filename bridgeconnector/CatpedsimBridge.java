package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.bridgeconnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import uk.org.catapult.ts.cav.configuration.microsimulator.MicroSimulatorConfiguration;
import uk.org.catapult.ts.cav.configuration.microsimulator.pedestrian.CatpedsimConfiguration;
import uk.org.catapult.ts.cav.microsimulator.MicroSimulatorBridge;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.AgentState;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAgent;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAutonomousVehicle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimPedestrian;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimVehicle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimCrossing;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimObstacle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.ShapeSection;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.ListUtilities;
import uk.org.catapult.ts.cav.model.Building;
import uk.org.catapult.ts.cav.model.Pedestrian;
import uk.org.catapult.ts.cav.model.Vehicle;
import uk.org.catapult.ts.cav.model.identifier.UniqueIdentifier;
import uk.org.catapult.ts.cav.model.identifier.UniqueIdentifierProvider;
import uk.org.catapult.ts.cav.model.simulation.SimulationObject;
import uk.org.catapult.ts.cav.model.simulation.SimulationTimeUnit;
import uk.org.catapult.ts.cav.model.spatial.Vector2D;
import uk.org.catapult.ts.cav.pubsub.PublisherIdProvider;
import uk.org.catapult.ts.cav.utils.TimeUtilities;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * Implementation of a bridge to Catpedsim. CatpedsimBridge is an Observer class
 * to the events happening in the logic layer or the manager of the simulation.
 * When events are notofied from the logic layer the events are received in the
 * update() method.
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
public class CatpedsimBridge implements MicroSimulatorBridge, Observer {

	/**
	 * Logger for the class.
	 */
	private static final Logger LOGGER = UtilityLogger.getLogger(CatpedsimBridge.class);

	/**
	 * Catpedsim simulator.
	 */
	private CatpedsimSimulatorConnector simulator = new CatpedsimSimulatorConnector();

	/**
	 * Notifies the bridge the advance time step computation was finished.
	 */
	private AtomicLong advanceTimeStepCompleted = new AtomicLong(0);

	/**
	 * Publisher id for use on pedestrian creation.
	 */
	private String publisherId;

	/**
	 * Simulator configuration.
	 */
	private CatpedsimConfiguration catpedsimConfiguration;

	/** Start simulation time. */
	private long simulationStartTimeNanoseconds = 0;

	/** End simulation time. */
	private long simulationEndTimeNanoseconds = 0;

	/** Flag indicating whether walls have been published. */
	private boolean wallsAlreadyPublished = false;

	/** Flag indicating whether obstacles have been published. */
	private boolean obstaclesAlreadyPublished = false;

	/**
	 * Constructor given a configuration. Test purposes.
	 * 
	 * @param configuration
	 *            The configuration to use.
	 * 
	 */
	@Inject
	public CatpedsimBridge(final CatpedsimConfiguration configuration) {
		catpedsimConfiguration = configuration;
		publisherId = configuration.getPublisherId();
	}

	/**
	 * Initialise the simulation.
	 */
	@Override
	public final void initialise() throws Exception {
		simulationStartTimeNanoseconds = System.nanoTime();
		simulator.startCatpedsim(catpedsimConfiguration, this);
		simulator.generateRandomPedestrians(publisherId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.org.catapult.ts.cav.microsim.MicroSimulation#getInterestedClasses()
	 */
	@Override
	public final List<Class<? extends PublisherIdProvider>> getInterestedClasses() {
		return Arrays.asList(Vehicle.class, Pedestrian.class);
	}

	/**
	 * Gets Catpedsim configuration.
	 */
	@Override
	public final MicroSimulatorConfiguration getConfiguration() {
		return catpedsimConfiguration;
	}

	/**
	 * Gets published classes.
	 */
	@Override
	public final List<Class<? extends PublisherIdProvider>> getPublishedClasses() {
		return Arrays.asList(Pedestrian.class, Vehicle.class, Building.class);
	}

	/*
	 * Convert the list of Catpedsim objects to a list of Pedestrian objects.
	 * 
	 * @see uk.org.catapult.ts.cav.microsim.MicroSimulation#getUpdatedObjects()
	 */
	@Override
	public final List<Object> getUpdatedObjects() {
		List<Object> returnList = new LinkedList<Object>();

		List<Object> pedestrianList = ListUtilities.transform(simulator.getListOfAgents(CatpedsimPedestrian.class),
				new ConvertAgentFromInternalModelToExternalModel());

		List<Object> autonomousVehiclesList = ListUtilities.transform(
				simulator.getListOfAgents(CatpedsimAutonomousVehicle.class),
				new ConvertAgentFromInternalModelToExternalModel());

		List<Object> otherVehiclesList = ListUtilities.transform(simulator.getListOfAgents(CatpedsimVehicle.class),
				new ConvertAgentFromInternalModelToExternalModel());

		List<Building> walls = convertObstaclesToModelBuilding(simulator.getListOfWalls());
		List<Building> obstacles = convertObstaclesToModelBuilding(simulator.getListOfObstacles());

		if (pedestrianList != null && !pedestrianList.isEmpty()) {
			returnList.addAll(pedestrianList);
		}

		if (autonomousVehiclesList != null && !autonomousVehiclesList.isEmpty()) {
			returnList.addAll(autonomousVehiclesList);
		}

		if (otherVehiclesList != null && !otherVehiclesList.isEmpty()) {
			returnList.addAll(otherVehiclesList);
		}

		if (!wallsAlreadyPublished && !walls.isEmpty()) {
			returnList.addAll(walls);
			wallsAlreadyPublished = true;
		}

		if (!obstaclesAlreadyPublished && !obstacles.isEmpty()) {
			returnList.addAll(obstacles);
			obstaclesAlreadyPublished = true;
		}

		return returnList;
	}

	/**
	 * Gets the list of pedestrians.
	 * 
	 * @return list of pedestrians in simulation format
	 */
	public final List<CatpedsimAgent> getListOfPedestrians() {
		List<CatpedsimAgent> returnList = null;
		try {
			returnList = simulator.getListOfPedestrians();
		} catch (Exception ex) {
			LOGGER.info("Could not read the list of pedestrians.", ex);
		}

		return returnList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.org.catapult.ts.cav.microsim.MicroSimulation#advanceTime(double,
	 * double, uk.org.catapult.ts.cav.model.simulation.SimulationTimeUnit)
	 */
	@Override
	public final void advanceTimeStep(final double currentTime, final double timestep, final SimulationTimeUnit unit)
			throws Exception {
		simulator.advanceTimeStep(timestep, unit);
		try {
			synchronized (advanceTimeStepCompleted) {
				advanceTimeStepCompleted.wait((long) TimeUtilities.convertTime(catpedsimConfiguration.getTickLength(),
						catpedsimConfiguration.getTickUnit(), SimulationTimeUnit.MILLISECONDS));
			}
		} catch (Exception ex) {
			LOGGER.info("The configuration object is null", ex);
		}
	}

	/**
	 * Disconnects the simulator.
	 */
	@Override
	public final void disconnect() throws Exception {
		synchronized (advanceTimeStepCompleted) {
			advanceTimeStepCompleted.notify();
		}
		simulator.close();
		simulationEndTimeNanoseconds = System.nanoTime();
		LOGGER.info(
				"Simulation time in nanoseconds: " + (simulationStartTimeNanoseconds - simulationEndTimeNanoseconds));
	}

	@Override
	public void connect() throws Exception {

	}

	/**
	 * Safely update external objects in Catpedsim simulation.
	 */
	@Override
	public final void safelyUpdateObjectOfInterest(final Object objectOfInterest) {
		if (objectOfInterest instanceof Vehicle) {
			safelyUpdateAVehicle((Vehicle) objectOfInterest);
		} else if (objectOfInterest instanceof Pedestrian) {
			safelyUpdateAPedestrian((Pedestrian) objectOfInterest);
		}
	}

	/**
	 * Adds or updates a vehicle into the simulator.
	 * 
	 * @param vehicle
	 *            The vehicle to add.
	 */
	private void safelyUpdateAVehicle(final Vehicle vehicle) {
		try {
			simulator.updateExternalVehicle(vehicle);
		} catch (Exception e) {
			LOGGER.warn("Vehicle " + vehicle.getVehicleName() + " publisher id " + vehicle.getPublisherId()
					+ " could not be updated!", e);
		}
	}

	/**
	 * Adds or updates a pedestrian into the simulator.
	 * 
	 * @param pedestrian
	 *            The pedestrian to add.
	 */
	private void safelyUpdateAPedestrian(final Pedestrian pedestrian) {
		try {
			simulator.updatePedestrian(pedestrian);
		} catch (Exception e) {
			LOGGER.warn("Pedestrian " + pedestrian.getName() + " publisher id " + pedestrian.getPublisherId()
					+ " could not be updated!", e);
		}
	}

	/**
	 * The notifications comming from the logic layer are received in the update
	 * method.
	 */
	@Override
	public final void update(final Observable observable, final Object raisedEvent) {

		if (raisedEvent instanceof LogicLayerEventsToBridge) {
			LogicLayerEventsToBridge eventFromTheManager = (LogicLayerEventsToBridge) raisedEvent;

			switch (eventFromTheManager) {
			case ADVANCE_TIME_STEP_COMPLETED:
				synchronized (advanceTimeStepCompleted) {
					advanceTimeStepCompleted.incrementAndGet();
					advanceTimeStepCompleted.notify();
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Gets the publisher id.
	 * 
	 * @return publichser id
	 */
	public final String getPublisherId() {
		return publisherId;
	}

	/**
	 * ConvertPedestrianFromInternalModelToExternalModel converts a pedestrian
	 * from the internal model to the external model.
	 * 
	 * @author Ecaterina McCormick
	 *
	 */
	public class ConvertAgentFromInternalModelToExternalModel implements Function<CatpedsimAgent, Object> {
		/**
		 * Applies the conversion to the catpedsimPedestrian object.
		 */
		@Override
		public final Object apply(final CatpedsimAgent catpedsimAgent) {
			if (catpedsimAgent instanceof CatpedsimPedestrian) {
				return convertInternalPedestrianToModelPedestrian(catpedsimAgent);
			} else if (catpedsimAgent instanceof CatpedsimAutonomousVehicle) {
				return convertInternalVehicleToModelVehicle(catpedsimAgent);
			} else if (catpedsimAgent instanceof CatpedsimVehicle) {
				return convertInternalVehicleToModelVehicle(catpedsimAgent);
			}

			return null;
		}

	}

	/**
	 * Convert internal to external vehicle.
	 * 
	 * @param catpedsimAgent
	 *            catpedsim agent object
	 * @return external vehicle object
	 */
	private Vehicle convertInternalVehicleToModelVehicle(final CatpedsimAgent catpedsimAgent) {
		if (catpedsimAgent instanceof CatpedsimAutonomousVehicle || catpedsimAgent instanceof CatpedsimVehicle) {
			Vehicle vehicle = new Vehicle(catpedsimAgent.getName(), (double) catpedsimAgent.getCurrentSpeed(),
					new Vector2D(catpedsimAgent.getVelocity().x, catpedsimAgent.getVelocity().y),
					catpedsimAgent.getClass().getSimpleName(), publisherId, "", 0, 0, "",
					Integer.toString(catpedsimAgent.getColorToRepresent().getRGB()),
					catpedsimAgent.getCurrentPosition());

			return vehicle;
		}

		return null;
	}

	/**
	 * Convert the simulation pedestrian model to the Object Model format.
	 * 
	 * @param catpedsimAgent
	 *            Pedestrian object in the CatpedsimPedestrian format
	 * @return Pedestrian object in the Object Model format
	 */
	private Pedestrian convertInternalPedestrianToModelPedestrian(final CatpedsimAgent catpedsimAgent) {
		if (catpedsimAgent instanceof CatpedsimPedestrian) {
			Pedestrian pedestrian = new Pedestrian(publisherId, catpedsimAgent.getName(),
					catpedsimAgent.getCurrentPosition(), catpedsimAgent.getCurrentHeading(),
					catpedsimAgent.getCurrentSpeed());

			return pedestrian;
		}
		return null;

	}

	/**
	 * Convert the simulation obstacles model to the Object Model format.
	 * 
	 * @param obstacles
	 *            Obstacles object in the CatpedsimPedestrian format
	 * @return List of Building object in the Object Model format
	 */
	private List<Building> convertObstaclesToModelBuilding(final List<CatpedsimObstacle> obstacles) {
		List<Building> buildings = new ArrayList<>();
		for (CatpedsimObstacle wall : obstacles) {
			buildings.add(convertInternalObstacleToModelBuilding(wall));
		}
		return buildings;
	}

	/**
	 * Convert the simulation obstacle model to the Object Model format.
	 * 
	 * @param obstacle
	 *            Obstacle object in the CatpedsimPedestrian format
	 * @return Building object in the Object Model format
	 */
	private Building convertInternalObstacleToModelBuilding(final CatpedsimObstacle obstacle) {
		List<Double> xCoords = new ArrayList<>();
		List<Double> yCoords = new ArrayList<>();
		List<Double> zCoords = new ArrayList<>();
		List<Double> heights = new ArrayList<>();

		ShapeSection lastSection = null;
		for (ShapeSection section : obstacle.getObstacleSections()) {
			xCoords.add((double) section.getVectorToStartPoint().x);
			yCoords.add((double) section.getVectorToStartPoint().y);
			zCoords.add((double) section.getVectorToStartPoint().z);
			heights.add(0.0);
			lastSection = section;
		}
		if (lastSection != null) {
			xCoords.add((double) lastSection.getVectorToEndPoint().x);
			yCoords.add((double) lastSection.getVectorToEndPoint().y);
			zCoords.add((double) lastSection.getVectorToEndPoint().z);
			heights.add(0.0);
		}

		Building building = new Building(publisherId, obstacle.getName(), null, 0, 0, false);
		building.setVertices(xCoords, yCoords, zCoords, heights);
		return building;
	}

	/**
	 * During release ownership the simulator receives a notification that it
	 * shouldn't compute the specified agent position.
	 */
	@Override
	public final SimulationObject releaseOwnership(final UniqueIdentifier identifier) {
		CatpedsimAgent agent = simulator.getAgentByName(identifier);
		if (agent != null) {
			agent.setAgentState(AgentState.NOT_ACTIVE);

			return convertInternalPedestrianToModelPedestrian(agent);
		}
		return null;
	}

	@Override
	public final void takeOwnership(final SimulationObject object) {
		CatpedsimAgent agent = simulator.getAgentByName(object.getUniqueIdentifier());
		if (agent != null) {
			CatpedsimCrossing exit = simulator.getRandomExit();
			agent.setAgentExit(exit);
			LOGGER.info("Assigning exit " + exit + " to agent " + agent);
		}
	}

	/**
	 * Count the internal ticks.
	 * 
	 * @return internal ticks counted
	 */
	public final long getInternalTickCount() {
		return simulator.getInternalTickCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.org.catapult.ts.cav.microsimulator.MicroSimulatorBridge#
	 * safelyDeleteObjectOfInterest(java.lang.Object)
	 */
	@Override
	public final void safelyDeleteObjectOfInterest(final Object objectOfInterest) {
		UniqueIdentifier uniqueIdentifier = null;

		if (objectOfInterest instanceof UniqueIdentifierProvider) {
			uniqueIdentifier = ((UniqueIdentifierProvider) objectOfInterest).getUniqueIdentifier();
		}

		if (uniqueIdentifier == null) {
			return;
		}

		try {
			simulator.deleteExternalAgent(uniqueIdentifier);
		} catch (Exception e) {
			LOGGER.warn("Agent " + uniqueIdentifier + " could not be deleted!", e);
		}
	}

	@Override
	public void postAdvanceTimeStep(final double currentTime, final double tickLength, final SimulationTimeUnit unit) {
		// TODO Auto-generated method stub
	}
}