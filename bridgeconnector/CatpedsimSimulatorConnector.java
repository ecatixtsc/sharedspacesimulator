package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.bridgeconnector;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import processing.core.PApplet;
import uk.org.catapult.ts.cav.configuration.microsimulator.pedestrian.CatpedsimConfiguration;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAgent;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimPedestrian;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimCrossing;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimObstacle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.nonvisual.CatpedsimNonVisual;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.visual.CatpedsimApplet;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.visual.VisualConstants;
import uk.org.catapult.ts.cav.model.Pedestrian;
import uk.org.catapult.ts.cav.model.Vehicle;
import uk.org.catapult.ts.cav.model.identifier.UniqueIdentifier;
import uk.org.catapult.ts.cav.model.simulation.SimulationTimeUnit;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * CatPedSim connector, it could be called in silent mode or GUI.
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
public class CatpedsimSimulatorConnector {
	/** Logger for the class. */
	private static final Logger LOGGER = UtilityLogger.getLogger(CatpedsimSimulatorConnector.class);

	/** Non-visual simulation. */
	private CatpedsimNonVisual nonVisualSimulation = null;

	/** Visual simulation (GUI). */
	private CatpedsimApplet visualSimulation = null;

	/** If the simulation is using Gui. */
	private boolean usingGui = true;

	/**
	 * Start the simulator in silent mode or using the Gui.
	 * 
	 * 
	 * @param configuration
	 *            the simualtion configuration
	 * @param catpedsimBridge
	 *            the bridge to the simulation is observing the events raised in
	 *            manager
	 * @throws Exception
	 *             on error
	 */
	public final void startCatpedsim(final CatpedsimConfiguration configuration, final CatpedsimBridge catpedsimBridge)
			throws Exception {
		usingGui = configuration.isUsingGUI();

		if (usingGui) {
			startGui(configuration, catpedsimBridge);
		} else {
			startInSilentMode(configuration, catpedsimBridge);
		}
	}

	/**
	 * Start simulation in silent mode, without animation.
	 * 
	 * @param configuration
	 *            simulation configuration loaded form the xml file
	 * @param catpedsimBridge
	 *            the bridge to the simulation is observing the events raised in
	 *            manager
	 * @throws Exception
	 *             on error starting the simulation in silent mode
	 */
	private void startInSilentMode(final CatpedsimConfiguration configuration, final CatpedsimBridge catpedsimBridge)
			throws Exception {
		nonVisualSimulation = new CatpedsimNonVisual();
		nonVisualSimulation.setSimulationConfiguration(configuration, catpedsimBridge);
		new Thread(nonVisualSimulation).start();
	}

	/**
	 * Create and display GUI.
	 * 
	 * @param configuration
	 *            simulation configuration
	 * @param catpedsimBridge
	 *            the bridge to the simulation is observing the events raised in
	 *            manager
	 * @throws Exception
	 *             on error
	 */
	private void startGui(final CatpedsimConfiguration configuration, final CatpedsimBridge catpedsimBridge)
			throws Exception {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			LOGGER.warn("Could not start the GUI.", ex);
		} catch (InstantiationException ex) {
			LOGGER.warn("Could not start the GUI.", ex);
		} catch (IllegalAccessException ex) {
			LOGGER.warn("Could not start the GUI.", ex);
		} catch (UnsupportedLookAndFeelException ex) {
			LOGGER.warn("Could not start the GUI.", ex);
		}

		visualSimulation = new CatpedsimApplet();
		visualSimulation.setSimulationConfiguration(VisualConstants.DEFAULT_WINDOW_WIDTH,
				VisualConstants.DEFAULT_WINDOW_DEPTH, configuration, catpedsimBridge);

		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				PApplet.runSketch(new String[] { CatpedsimApplet.class.getName() }, visualSimulation);
			}
		});
	}

	/**
	 * Generate randon pedestrian in the simulation.
	 * 
	 * @param publisherId
	 *            publisher id
	 * 
	 * @throws Exception
	 *             on error
	 * 
	 */
	public final void generateRandomPedestrians(final String publisherId) throws Exception {
		if (usingGui && (visualSimulation != null)) {
			visualSimulation.generateRandomAgents(publisherId);
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			nonVisualSimulation.generateRandomAgents(publisherId);
		}
	}

	/**
	 * Advance one animation tick.
	 * 
	 * @param timestep
	 *            time step to advance
	 * @param unit
	 *            time step unit
	 * @throws Exception
	 *             on error
	 */
	public final void advanceTimeStep(final double timestep, final SimulationTimeUnit unit) throws Exception {
		if (usingGui) {
			visualSimulation.advanceTimeStep(timestep, unit);
		} else {
			nonVisualSimulation.advanceTimeStep(timestep, unit);
		}
	}

	/**
	 * Gets the list of pedestrians.
	 * 
	 * @return the list of pedestrians in the animation
	 */
	public final List<CatpedsimAgent> getListOfPedestrians() {
		List<CatpedsimAgent> pedestrians = null;

		if (usingGui && (visualSimulation != null)) {
			pedestrians = visualSimulation.getListOfAgents(CatpedsimPedestrian.class);
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			pedestrians = nonVisualSimulation.getListOfAgents(CatpedsimPedestrian.class);
		}

		return pedestrians;
	}

	/**
	 * End and close the simulation.
	 */
	public final void close() {
		if (usingGui && (visualSimulation != null)) {
			visualSimulation.close();
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			nonVisualSimulation.close();
		}
	}

	/**
	 * Add pedestrian in the simulation. The pedestrian from the external model
	 * has to be converted to the pedestrian from the internal model.
	 * 
	 * @param publisherId
	 *            id of the simulator that is adding the pedestrian
	 * @param pedestrian
	 *            pedestrian in external format
	 * @throws Exception
	 *             on error
	 */
	protected final void addPedestrian(final String publisherId, final Pedestrian pedestrian) throws Exception {
		if (usingGui && (visualSimulation != null)) {
			visualSimulation.addPedestrian(publisherId, pedestrian);
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			nonVisualSimulation.addPedestrian(publisherId, pedestrian);
		}
	}

	/**
	 * Update pedestrian with new information.
	 * 
	 * 
	 * @param publisherId
	 *            id of the simulator that is updating the pedestrian
	 * @param pedestrian
	 *            pedestrian in external format
	 * @throws Exception
	 *             on error
	 */
	protected final void updatePedestrian(final String publisherId, final Pedestrian pedestrian) throws Exception {
		if (usingGui && (visualSimulation != null)) {
			visualSimulation.updatePedestrian(publisherId, pedestrian);
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			nonVisualSimulation.updatePedestrian(publisherId, pedestrian);
		}
	}

	/**
	 * Update pedestrian with new information.
	 * 
	 * 
	 * @param pedestrian
	 *            pedestrian in external format
	 * @throws Exception
	 *             on error
	 */
	protected final void updatePedestrian(final Pedestrian pedestrian) throws Exception {
		updatePedestrian(pedestrian.getPublisherId(), pedestrian);
	}

	/**
	 * Updates external vehicles.
	 * 
	 * @param vehicle
	 *            vehicle
	 * @throws Exception
	 *             on error
	 */
	public final void updateExternalVehicle(final Vehicle vehicle) throws Exception {
		updateExternalVehicle(vehicle.getPublisherId(), vehicle);
	}

	/**
	 * Updates external vehicles based on publisher id.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param vehicle
	 *            vehicle object
	 */
	private void updateExternalVehicle(final String publisherId, final Vehicle vehicle) {
		if (usingGui && (visualSimulation != null)) {
			visualSimulation.updateExternalVehicle(publisherId, vehicle);
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			nonVisualSimulation.updateVehicle(publisherId, vehicle);
		}
	}

	/**
	 * Internal tick count.
	 * 
	 * @return internal tick count
	 * 
	 */
	public final long getInternalTickCount() {
		if (usingGui && (visualSimulation != null)) {
			return visualSimulation.getInternalTickCount();
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			return nonVisualSimulation.getInternalTickCount();
		}

		return 0;
	}

	/**
	 * Get a random exit from the geometry used in the simulator.
	 * 
	 * @return a random exit
	 */
	public final CatpedsimCrossing getRandomExit() {
		if (usingGui && (visualSimulation != null)) {
			return visualSimulation.getRandomExit();
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			return nonVisualSimulation.getRandomExit();
		}

		return null;
	}

	/**
	 * Get an agent by its name.
	 * 
	 * @param identifier
	 *            to look for
	 * @return the agent with the given name.
	 */
	public final CatpedsimAgent getAgentByName(final UniqueIdentifier identifier) {
		if (usingGui && (visualSimulation != null)) {
			return visualSimulation.getAgentByName(identifier);
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			return nonVisualSimulation.getAgentByName(identifier);
		}

		return null;
	}

	/**
	 * Get the list of agents based on type.
	 * 
	 * @param agentClass
	 *            type of agent
	 * @return list of agents of the specified class
	 */
	public final List<CatpedsimAgent> getListOfAgents(final Class<?> agentClass) {
		if (usingGui && (visualSimulation != null)) {
			return visualSimulation.getListOfAgents(agentClass);
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			return nonVisualSimulation.getListOfAgents(agentClass);
		}

		return null;
	}

	/**
	 * Get the list of walls in the environment.
	 * 
	 * @return list of walls as Catpedsim obstacles.
	 */
	public final List<CatpedsimObstacle> getListOfWalls() {
		if (usingGui && (visualSimulation != null)) {
			return visualSimulation.getListOfWalls();
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			return nonVisualSimulation.getListOfWalls();
		}

		return null;
	}

	/**
	 * Get the list of obstacles in the environment.
	 * 
	 * @return list of obstacles as Catpedsim obstacles.
	 */
	public final List<CatpedsimObstacle> getListOfObstacles() {
		if (usingGui && (visualSimulation != null)) {
			return visualSimulation.getListOfObstacles();
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			return nonVisualSimulation.getListOfObstacles();
		}

		return null;
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
		if (usingGui && (visualSimulation != null)) {
			visualSimulation.deleteExternalAgent(uniqueIdentifier);
		}

		if (!usingGui && (nonVisualSimulation != null)) {
			nonVisualSimulation.deleteExternalAgent(uniqueIdentifier);
		}

	}
}