package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.nonvisual;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import uk.org.catapult.ts.cav.configuration.microsimulator.pedestrian.CatpedsimConfiguration;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAgent;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.bridgeconnector.CatpedsimBridge;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimCrossing;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimObstacle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.logiclayer.CatpedsimManager;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.logiclayer.TickCounter;
import uk.org.catapult.ts.cav.model.Pedestrian;
import uk.org.catapult.ts.cav.model.Vehicle;
import uk.org.catapult.ts.cav.model.identifier.UniqueIdentifier;
import uk.org.catapult.ts.cav.model.simulation.SimulationTimeUnit;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * CatPedSim simulator in a non-visual mode.
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
public class CatpedsimNonVisual implements Runnable {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = UtilityLogger.getLogger(CatpedsimNonVisual.class);

	/**
	 * If the simulation is terminated, the main thread should be closed.
	 */
	private boolean kill = false;

	/**
	 * 
	 */
	private CatpedsimManager simManager = null;

	/**
	 * The total number of ticks since the begining of the simulation.
	 */
	private TickCounter numberOfTicks = new TickCounter();

	/**
	 * Sets the simulation configuration. The animation area has 0 width and 0
	 * depth since it runs without GUI.
	 * 
	 * @param configuration
	 *            simulation configuration
	 * @param catpedsimBridge
	 *            bridge bridge
	 * @throws Exception
	 *             on error
	 */
	public final void setSimulationConfiguration(final CatpedsimConfiguration configuration,
			final CatpedsimBridge catpedsimBridge) throws Exception {
		simManager = new CatpedsimManager(0, 0, configuration, catpedsimBridge);
	}

	/**
	 * Close simulation.
	 */
	public final void close() {
		kill = true;
		waitForNotification();
	}

	/**
	 * Wait for notification to run another simulation step.
	 */
	@Override
	public final void run() {
		while (!kill) {
			waitForNotification();
			try {
				step();
			} catch (Exception e) {
				LOGGER.info("Error during step calculation", e);
			}
			numberOfTicks.incrementTickCount();
			simManager.notifyAdvanceTimeStepCompleted();
		}
	}

	/**
	 * Signal advance step.
	 */
	public final synchronized void advanceTimeStep() {
		notify();
	}

	/**
	 * Wait for notification.
	 */
	private synchronized void waitForNotification() {
		try {
			wait((long) (2 * simManager.getTickLengthSeconds()));
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Calculate animation step.
	 * 
	 * @throws Exception
	 *             on error
	 */
	private synchronized void step() throws Exception {
		simManager.generateDepartures();
		simManager.fillTransferZonesWithAgents();
		simManager.calculateAllAgentsNextAction();
		simManager.updateTransportedAgentsPositions();

		logInformationAboutAllAgents();

	}

	/**
	 * Log information about all the agents.
	 */
	private void logInformationAboutAllAgents() {
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
						String information = agent.toString();
						System.out.println(information);
					}
				}
			} catch (Exception ex) {
				LOGGER.warn("Error during loggin agent infromation", ex);
			}
		}
	}

	/**
	 * Generate random pedestrians in the simulation.
	 * 
	 * @param publisherId
	 *            publisher id for the pedestrians
	 * @throws Exception
	 *             on error
	 */
	public final void generateRandomAgents(final String publisherId) {
		if (simManager != null) {
			simManager.initialiseRandomPedestrians(publisherId);
			simManager.initialiseRandomAutonomousVechicles(publisherId);
			simManager.initialiseRandomBicycles(publisherId);
		}
	}

	/**
	 * Advance simulation by a certain amount of steps.
	 * 
	 * @param timestep
	 *            time step
	 * @param unit
	 *            time unit
	 */
	public void advanceTimeStep(final double timestep, final SimulationTimeUnit unit) {

	}

	/**
	 * Add a new pedestrian in the simulation.
	 * 
	 * @param publisherId
	 *            id of the simulation that is adding the pedestrian
	 * @param pedestrian
	 *            HLA pedestrian
	 * @throws Exception
	 *             on error
	 */
	public final void addPedestrian(final String publisherId, final Pedestrian pedestrian) throws Exception {
		simManager.addPedestrianFromTheExternalPedestrianModel(publisherId, pedestrian);
	}

	/**
	 * Update new pedestrian in the simulation.
	 * 
	 * @param publisherId
	 *            id of the simulation that is updating the pedestrian
	 * @param pedestrian
	 *            HLA pedestrian
	 * @throws Exception
	 *             on error
	 */
	public final void updatePedestrian(final String publisherId, final Pedestrian pedestrian) throws Exception {
		simManager.addOrUpdatePedestrian(publisherId, pedestrian);
	}

	/**
	 * Get internal tick count.
	 * 
	 * @return tick count
	 */
	public final long getInternalTickCount() {
		return numberOfTicks.getNumberOfTicksAsLong();
	}

	/**
	 * Get the list of agents based on agent type.
	 * 
	 * @param agentType
	 *            agent class type
	 * @return list of agents
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
	public final void updateVehicle(final String publisherId, final Vehicle vehicle) {
		try {
			simManager.updateOrAddExternalVehicle(publisherId, vehicle);
		} catch (Exception ex) {
			LOGGER.warn("The external vehicle could not be updated", ex);
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
		if (simManager != null) {
			return simManager.getAgentByName(identifier);
		}
		return null;
	}

	/**
	 * Delete agent based on unique identifier with external request.
	 * 
	 * @param uniqueIdentifier
	 *            unique identifier fot the agent to delete
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
}