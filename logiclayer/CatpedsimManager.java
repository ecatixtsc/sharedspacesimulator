package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.logiclayer;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.sun.javafx.geom.Vec3f;

import processing.core.PConstants;
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
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.bridgeconnector.LogicLayerEventsToBridge;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimCrossing;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimGeometry;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimObstacle;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.CatpedsimTransitionZone;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.LoadGeometryFromXmlModel;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.ShapeSection;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry.TransitionZoneTypes;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.odmatrices.OriginDestinationMatrix;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.odmatrices.ReaderOfOriginDestinationMatricesFromExcel;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.odmatrices.ReaderOfTransitionMatrices;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.odmatrices.TransitionZoneMatrix;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.path.XmlPath;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.shortestpath.AStarPathFinder;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.shortestpath.NodePath;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.socialforcemodel.SocialForceAgentsPair;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.socialforcemodel.SocialForceParameters;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.LengthUnits;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.RegexPatterns;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.SubClassesFinder;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.Trigonometry;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.Utils;
import uk.org.catapult.ts.cav.model.Pedestrian;
import uk.org.catapult.ts.cav.model.Vehicle;
import uk.org.catapult.ts.cav.model.identifier.StringIdentifier;
import uk.org.catapult.ts.cav.model.identifier.UniqueIdentifier;
import uk.org.catapult.ts.cav.model.simulation.SimulationTimeUnit;
import uk.org.catapult.ts.cav.model.spatial.Vector2D;
import uk.org.catapult.ts.cav.utils.TimeUtilities;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * The simulation's logic layer is handled by a manager object.
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
public class CatpedsimManager extends Observable {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = UtilityLogger.getLogger(CatpedsimManager.class);

	/**
	 * Initialisation information.
	 */
	private CatpedsimConfiguration initialisation = null;

	/**
	 * A Star Path Finder map and mechanism.
	 */
	private AStarPathFinder aStarPathFinder = null;

	/**
	 * Geometry information.
	 */
	private CatpedsimGeometry geometry = null;

	/**
	 * World width in meters.
	 */
	private float worldWidth = 0;

	/**
	 * World depth in meters.
	 */
	private float worldDepth = 0;

	/**
	 * When the simulation started.
	 */
	private long simulationStartTime = 0;

	/**
	 * Window width in pixels.
	 */
	private int windowWidth = 0;

	/**
	 * World depth in pixels.
	 */
	private int windowDepth = 0;

	/**
	 * Tick length in seconds. Useful for calculating speed.
	 */
	private float tickLengthSeconds = 0;

	/**
	 * The total number of ticks since the begining of the simulation.
	 */
	private TickCounter numberOfTicks = new TickCounter();

	/**
	 * A map holding the lists of agents. A new list is created for every
	 * subclass of CatpedsimAgent found in the current package.
	 */
	private final Map<Class<?>, List<CatpedsimAgent>> mapOfAgentsLists = new HashMap<Class<?>, List<CatpedsimAgent>>();

	/**
	 * A map holding absolute counters for each type of agent. It helps
	 * generating unique agent indexes. generating unique
	 */
	private final Map<Class<?>, AtomicLong> mapOfAgentsCounters = new HashMap<Class<?>, AtomicLong>();

	/**
	 * A map indexed by time in seconds holding origin-destination matrices and
	 * information about the type of agent.
	 * 
	 */
	private final Map<Class<?>, Map<Integer, OriginDestinationMatrix<?>>> agentsOriginDestinationMatricesWithTimeKey = new HashMap<>();

	/**
	 * Transition zone distribution matrix index by agent type.
	 */
	private final Map<Class<?>, TransitionZoneMatrix> transferZonesDistributionMatrix = new HashMap<Class<?>, TransitionZoneMatrix>();

	/**
	 * A map of social force models based on the agents engaged in the
	 * interaction.
	 */
	private final Map<SocialForceAgentsPair<?, ?>, SocialForceParameters> socialForceInteractionMap = new HashMap<>();

	/**
	 * Desired target.
	 */
	private PVector desiredTarget = null;

	/**
	 * Pedestrian running at the highest speed per tick length. Meters per tick
	 * length.
	 */
	private static float pedestrianHighestVelocity = 0;

	/**
	 * Pedestrian running at the walking speed per tick length. Meters per tick
	 * length.
	 */
	private static float pedestrianWalkingVelocity = 0;

	/**
	 * Pedestrian running at the marathon speed per tick length. Meters per tick
	 * length.
	 */
	private static float pedestrianMarathonVelocity = 0;

	/**
	 * Pedestrian running at the jogging speed per tick length. Meters per tick
	 * length.
	 */
	private static float pedestrianJoggingVelocity = 0;

	/**
	 * Load initialisation file. Load the geometry file. Set the world
	 * dimensions and the window dimensions. Convert other length units to
	 * meters. Store tick configurations in milliseconds. Initialise all the
	 * pedestrians. Initialise the library interrogating the system information.
	 * 
	 * If a origin-destination matrices are present, ignore the number of
	 * agents.
	 * 
	 * @param newWindowWidth
	 *            world width
	 * @param newWindowDepth
	 *            world depth
	 * @param configuration
	 *            configuration model
	 * @param catpedsimBridge
	 *            bridge to the simulation, it observes events coming from this
	 *            manager
	 * @throws Exception
	 *             on error
	 */
	public CatpedsimManager(final int newWindowWidth, final int newWindowDepth,
			final CatpedsimConfiguration configuration, final CatpedsimBridge catpedsimBridge) throws Exception {

		windowWidth = newWindowWidth;
		windowDepth = newWindowDepth;

		initialisation = configuration;

		buildGeometry(configuration);
		loadTransitionZonesDistribution(configuration);

		tickLengthSeconds = (float) TimeUtilities.convertTime(initialisation.getTickLength(),
				initialisation.getTickUnit(), SimulationTimeUnit.SECONDS);

		pedestrianWalkingVelocity = tickLengthSeconds * PedestrianConstraints.PEDESTRIAN_WALKING_SPEED;
		pedestrianJoggingVelocity = tickLengthSeconds * PedestrianConstraints.PEDESTRIAN_JOGGING_SPEED;
		pedestrianMarathonVelocity = tickLengthSeconds * PedestrianConstraints.PEDESTRIAN_MARATHON_SPEED;
		pedestrianHighestVelocity = tickLengthSeconds * PedestrianConstraints.PEDESTRIAN_RUNNING_AT_HIGHEST_SPPED;

		LengthUnits lengthUnit = initialisation.getWorldTileUnit();
		worldWidth = (int) LengthUnits.convertToMeters(lengthUnit, initialisation.getWorldTileWidth());
		worldDepth = (int) LengthUnits.convertToMeters(lengthUnit, initialisation.getWorldTileDepth());

		scanAllAgentSubClassesBuildAgentsListsAndSocialForces();
		buildDepartureProfilesForAllAgents(configuration);

		addObserver(catpedsimBridge);
	}

	/**
	 * Load transition zones distribution.
	 * 
	 * @param configuration
	 *            configuration object
	 */
	private void loadTransitionZonesDistribution(final CatpedsimConfiguration configuration) {
		try {
			List<Class<?>> agentSubclassList = SubClassesFinder.subclassScan(CatpedsimAgent.class, "");

			for (Class<?> currentAgentSubclass : agentSubclassList) {
				String transitionZoneMatrixFile = configuration.getTransitionZoneMatrixFilePath(currentAgentSubclass);

				TransitionZoneMatrix transitionZoneMatrix = ReaderOfTransitionMatrices
						.readMatrices(configuration.getSimulationStartTime(), transitionZoneMatrixFile);

				transferZonesDistributionMatrix.put(currentAgentSubclass, transitionZoneMatrix);
			}

		} catch (Exception e) {
			LOGGER.warn("The transition zone excel file is corrupt", e);
		}
	}

	/**
	 * Collect all the subclasses of CatpedsimAgent. Create a list for each
	 * group.
	 * 
	 * @throws Exception
	 *             class not found
	 */
	private void scanAllAgentSubClassesBuildAgentsListsAndSocialForces() throws Exception {
		mapOfAgentsLists.clear();

		List<Class<?>> agentSubclassList = SubClassesFinder.subclassScan(CatpedsimAgent.class, "");

		for (Class<?> currentAgentSubclass : agentSubclassList) {
			mapOfAgentsLists.put(currentAgentSubclass, new LinkedList<CatpedsimAgent>());
			mapOfAgentsCounters.put(currentAgentSubclass, new AtomicLong(0));

			agentsOriginDestinationMatricesWithTimeKey.put(currentAgentSubclass,
					new HashMap<Integer, OriginDestinationMatrix<?>>());

			for (Class<?> otherAgentSubclass : agentSubclassList) {
				SocialForceAgentsPair<?, ?> socialForceAgentsPair = new SocialForceAgentsPair<>(currentAgentSubclass,
						otherAgentSubclass);

				socialForceInteractionMap.put(socialForceAgentsPair, new SocialForceParameters(socialForceAgentsPair));
			}
		}
	}

	/**
	 * Build the departure profiles. If the origin-destination map exists and
	 * contains at least one matrix, clear the number of agents in the
	 * configuration for it is redundant.
	 * 
	 * @param configuration
	 *            the configuration information
	 * 
	 */
	private void buildDepartureProfilesForAllAgents(final CatpedsimConfiguration configuration) {
		for (Entry<Class<?>, List<CatpedsimAgent>> mapOfAgentsIterator : mapOfAgentsLists.entrySet()) {

			Class<?> agentType = mapOfAgentsIterator.getKey();
			Map<Integer, OriginDestinationMatrix<?>> agentMatricesWithTimeKey = null;

			try {
				agentMatricesWithTimeKey = ReaderOfOriginDestinationMatricesFromExcel.readMatrices(
						configuration.getSimulationStartTime(), tickLengthSeconds, agentType,
						configuration.getAgentsOriginDestinationMatricesFilePath(agentType));
			} catch (Exception e) {
				LOGGER.warn("Error while loading the OD matrices for agent " + agentType.getSimpleName(), e);
			} finally {
				if (agentMatricesWithTimeKey != null && agentMatricesWithTimeKey.size() > 0) {
					configuration.clearAgentsNumber(agentType);
					agentsOriginDestinationMatricesWithTimeKey.get(agentType).clear();
					agentsOriginDestinationMatricesWithTimeKey.get(agentType).putAll(agentMatricesWithTimeKey);
				} else {
					generateRandomAgents(configuration.getPublisherId());
				}
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
		initialiseRandomPedestrians(publisherId);
		initialiseRandomAutonomousVechicles(publisherId);
		initialiseRandomBicycles(publisherId);
	}

	/**
	 * Build the geometry out of the XML file.
	 * 
	 * @param configuration
	 *            configuration object
	 */
	private void buildGeometry(final CatpedsimConfiguration configuration) {
		geometry = LoadGeometryFromXmlModel.readXMLGeometry(configuration.getConfigurationFilename());
		geometry.computeCentroidsSegmentLengthsAndBoundaries();
	}

	/**
	 * At the begining of the simulation all the transfer zones are filled with
	 * the maximum agents allowed.
	 */
	public final void fillTransferZonesWithAgents() {
		try {
			if (!Objects.isNull(geometry)) {
				for (Map.Entry<TransitionZoneTypes, Map<String, CatpedsimTransitionZone>> mapEntry : geometry
						.getTransitionZones().entrySet()) {

					TransitionZoneTypes transitionType = mapEntry.getKey();
					Map<String, CatpedsimTransitionZone> zoneMap = mapEntry.getValue();

					for (Map.Entry<String, CatpedsimTransitionZone> zone : zoneMap.entrySet()) {

						CatpedsimTransitionZone transitionZone = zone.getValue();
						LinkedList<StringIdentifier> enclosedAgents = transitionZone.getEnclosedAgents();
						int maximumEnclosedAgents = transitionZone.getMaxAgentsAllowed();
						if (transitionZone.getNumberOfSections() == 0) {
							continue;
						}

						int readyToPurge = enclosedAgents.size() - CatpedsimTransitionZone.PURGE;
						if (readyToPurge > 0) {
							if (transitionType.equals(TransitionZoneTypes.AUTONOMOUS_VEHICLE_RACK)) {
								for (int i = 0; i < readyToPurge; i++) {
									StringIdentifier idToRemove = enclosedAgents.removeFirst();
									deleteAgentWithStringIdentifier(idToRemove, CatpedsimAutonomousVehicle.class);
								}
							} else if (transitionType.equals(TransitionZoneTypes.BICYCLE_RACK)) {
								for (int i = 0; i < readyToPurge; i++) {
									StringIdentifier idToRemove = enclosedAgents.removeFirst();
									deleteAgentWithStringIdentifier(idToRemove, CatpedsimBicycle.class);
								}
							}
						} else {
							while (enclosedAgents.size() < maximumEnclosedAgents) {
								String newAgentName = null;
								StringIdentifier uniqueIdentifier = null;
								ShapeSection shortestSection = transitionZone.getShortest();
								PVector vectorToPosition = transitionZone.getVectorToCentroid();

								Vec3f agentLocation = null;
								int indexAgent = 0;
								CatpedsimAgent agent = null;
								PVector sectionHeading = CatpedsimTransitionZone.getSectionDirection(shortestSection);
								Vector2D heading = new Vector2D(sectionHeading.x, sectionHeading.y);
								sectionHeading.rotate(PConstants.PI / 2);

								if (transitionType.equals(TransitionZoneTypes.AUTONOMOUS_VEHICLE_RACK)) {
									vectorToPosition.add(sectionHeading);

									agentLocation = new Vec3f(vectorToPosition.x, vectorToPosition.y,
											vectorToPosition.z);
									newAgentName = generateUniqueName(CatpedsimAutonomousVehicle.class.getSimpleName(),
											initialisation.getPublisherId(), mapOfAgentsCounters
													.get(CatpedsimAutonomousVehicle.class).incrementAndGet());
									uniqueIdentifier = new StringIdentifier(newAgentName);

									agent = new CatpedsimAutonomousVehicle(initialisation.getPublisherId(),
											newAgentName, uniqueIdentifier, agentLocation,
											tickLengthSeconds * AutonomousVehicleConstraints.MAX_SPEED,
											TransitionZoneTypes.AUTONOMOUS_VEHICLE_RACK.getColor(),
											new AgentBehaviour(AgentState.STOP_AND_WAIT), heading);

									mapOfAgentsLists.get(CatpedsimAutonomousVehicle.class).add(agent);

									transitionZone.addEnclosedAgent(agent.getUniqueIdentifier());

								} else if (transitionType.equals(TransitionZoneTypes.BICYCLE_RACK)) {
									indexAgent = mapOfAgentsLists.get(CatpedsimBicycle.class).size();
									sectionHeading.mult(indexAgent * BicycleConstraints.WIDTH * 2);
									vectorToPosition.add(sectionHeading);

									agentLocation = new Vec3f(vectorToPosition.x, vectorToPosition.y,
											vectorToPosition.z);
									newAgentName = generateUniqueName(CatpedsimBicycle.class.getSimpleName(),
											initialisation.getPublisherId(), indexAgent);
									uniqueIdentifier = new StringIdentifier(newAgentName);

									agent = new CatpedsimBicycle(initialisation.getPublisherId(), newAgentName,
											uniqueIdentifier, agentLocation,
											tickLengthSeconds * BicycleConstraints.MAX_SPEED,
											TransitionZoneTypes.BICYCLE_RACK.getColor(),
											new AgentBehaviour(AgentState.STOP_AND_WAIT), heading);

									mapOfAgentsLists.get(CatpedsimBicycle.class).add(agent);

									transitionZone.addEnclosedAgent(agent.getUniqueIdentifier());
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error during filling in the transit zones!", ex);
		}
	}

	/**
	 * Delete an agent from the internal list based on its unique identifier.
	 * 
	 * @param idToRemove
	 *            unique identifier
	 * @param agentClass
	 *            type of agent
	 */
	private void deleteAgentWithStringIdentifier(final StringIdentifier idToRemove, final Class<?> agentClass) {

		if (mapOfAgentsLists.containsKey(agentClass)) {
			List<CatpedsimAgent> listOfAgents = mapOfAgentsLists.get(agentClass);

			for (Iterator<CatpedsimAgent> iterator = listOfAgents.iterator(); iterator.hasNext();) {
				CatpedsimAgent agent = iterator.next();
				if (agent.getUniqueIdentifier().equals(idToRemove)) {
					iterator.remove();
					return;
				}
			}
		}
	}

	/**
	 * Delete from the simulation the agent with the specified unique
	 * identifier.
	 * 
	 * @param idToRemove
	 *            unique idetifier for the agent to be removed
	 * 
	 */
	private void deleteAgentWithStringIdentifier(final StringIdentifier idToRemove) {
		for (Map.Entry<Class<?>, List<CatpedsimAgent>> entry : mapOfAgentsLists.entrySet()) {
			Class<?> agentClass = entry.getKey();
			List<CatpedsimAgent> listOfAgents = mapOfAgentsLists.get(agentClass);

			for (Iterator<CatpedsimAgent> iterator = listOfAgents.iterator(); iterator.hasNext();) {
				CatpedsimAgent agent = iterator.next();
				if (agent.getUniqueIdentifier().equals(idToRemove)) {
					iterator.remove();
					return;
				}
			}

		}
	}

	/**
	 * Get pedestrian running velocity per tick length.
	 * 
	 * @return pdestrian running speed
	 */
	public static float getPedestrianRunningHighestSpeed() {
		return pedestrianHighestVelocity;
	}

	/**
	 * Get pedestrian walking velocity per tick length.
	 * 
	 * @return pedestrian walking speed
	 */
	public final float getPedestrianWalkingVelocity() {
		return pedestrianWalkingVelocity;
	}

	/**
	 * Get pedestrian marathon velocity per tick length.
	 * 
	 * @return pedestrian walking speed
	 */
	public static float getPedestrianRunningMarathonVelocity() {
		return pedestrianMarathonVelocity;
	}

	/**
	 * Simulation running in silent mode (without GUI). There is no simulation
	 * window in this instance and windowWidth=0, windowDepth=0
	 * 
	 * @param configuration
	 *            simulator configuration model
	 * @param catpedsimBridge
	 *            bridge to the simulation, it observes events coming from this
	 *            manager
	 * @throws Exception
	 *             on error
	 */
	public CatpedsimManager(final CatpedsimConfiguration configuration, final CatpedsimBridge catpedsimBridge)
			throws Exception {
		this(0, 0, configuration, catpedsimBridge);
	}

	/**
	 * Get the desired target, the driving force for the pedestrians.
	 * 
	 * @return a vector with the x,y,z coordinates
	 */
	public final PVector getDesiredTarget() {
		return desiredTarget;
	}

	/**
	 * Set new target. Rearrange the pedestrians so that the closest to the
	 * target come first in the pedestrians list and start moving first.
	 * 
	 * @param newDesiredTarget
	 *            new target in the cartesian space
	 */
	public final void setDesiredTarget(final PVector newDesiredTarget) {
		this.desiredTarget = newDesiredTarget;

		synchronized (mapOfAgentsLists.get(CatpedsimPedestrian.class)) {
			Collections.sort(mapOfAgentsLists.get(CatpedsimPedestrian.class),
					new PedestrianToTargetComparator(desiredTarget));
		}
	}

	/**
	 * Generate random pedestrian location while making sure they are not placed
	 * on buildings.
	 *
	 * @return PVector generate a random location on a map
	 */
	private PVector generateRandomLocation() {
		PVector newVectorToPosition = new PVector(Utils.random(1, worldWidth - 1), Utils.random(1, worldDepth - 1));

		while (!checkIfAgentLocationIsPlacedInAccessibleArea(newVectorToPosition)) {
			newVectorToPosition = new PVector(Utils.random(1, worldWidth - 1), Utils.random(1, worldDepth - 1));
		}

		return newVectorToPosition;
	}

	/**
	 * In case of an emergency, the pedestrians are choosing the closest exit in
	 * order to leave the premises as fast as possible.
	 */
	public final void evacuatePedestriansToClosestExit() {
		List<CatpedsimCrossing> listOfCrossings = geometry.getCrossings();

		for (Map.Entry<Class<?>, List<CatpedsimAgent>> entry : mapOfAgentsLists.entrySet()) {
			Class<?> agentClass = entry.getKey();
			List<CatpedsimAgent> listOfAgents = mapOfAgentsLists.get(agentClass);

			for (Iterator<CatpedsimAgent> iterator = listOfAgents.iterator(); iterator.hasNext();) {
				CatpedsimAgent agent = iterator.next();
				float minimumDistance = Float.MAX_VALUE;
				CatpedsimCrossing closestExit = null;

				for (CatpedsimCrossing exit : listOfCrossings) {
					float distance = PVector.dist(exit.getCentroid(), agent.getPositionVector());

					if (distance < minimumDistance) {
						closestExit = exit;
						minimumDistance = distance;
					}
				}

				if (closestExit != null) {
					agent.setAgentExit(closestExit);
					agent.setSequenceOfBehaviours(null);
				}
			}
		}
	}

	/**
	 * Random evacuation of agents. The agents do not choose the closest exit
	 * but a random one from the list
	 */
	public final void randomEvacuation() {
		List<CatpedsimCrossing> listOfCrossings = geometry.getCrossings();

		final int tenPercent = 10 / 100;

		for (Map.Entry<Class<?>, List<CatpedsimAgent>> entry : mapOfAgentsLists.entrySet()) {
			Class<?> agentClass = entry.getKey();
			List<CatpedsimAgent> listOfAgents = mapOfAgentsLists.get(agentClass);
			CatpedsimCrossing chosenExit = null;

			for (int i = 0; i < listOfAgents.size(); i++) {
				CatpedsimAgent agent = listOfAgents.get(i);
				int part = listOfAgents.size() * tenPercent;

				if (i >= listOfAgents.size() - part) {
					chosenExit = listOfCrossings.get(1);
				} else {
					chosenExit = listOfCrossings.get(0);
				}

				if (chosenExit != null) {
					agent.setAgentExit(chosenExit);
					agent.setSequenceOfBehaviours(null);
				}
			}
		}
	}

	/**
	 * Check if an agent is not placed on walls or obstacles.
	 * 
	 * @param vectorToLocation
	 *            vector to location
	 * @return true of the agent is added in an accesible area
	 */
	public final boolean checkIfAgentLocationIsPlacedInAccessibleArea(final PVector vectorToLocation) {
		boolean accesible = true;

		for (CatpedsimObstacle wall : geometry.getWalls()) {
			for (int indexSection = 0; indexSection < wall.getObstacleSections().length; indexSection++) {
				ShapeSection wallSection = wall.getObstacleSections()[indexSection];

				if (Trigonometry.checkIfPointBelongsToSegment(vectorToLocation, wallSection.getVectorToStartPoint(),
						wallSection.getVectorToEndPoint())) {
					return false;
				}
			}
		}

		for (CatpedsimObstacle obstacle : geometry.getObstacles()) {
			if (Trigonometry.pointInsideClosedPolygon(obstacle.getObstacleSections(), vectorToLocation)) {
				return false;
			}

			for (int indexSection = 0; indexSection < obstacle.getObstacleSections().length; indexSection++) {
				ShapeSection wallSection = obstacle.getObstacleSections()[indexSection];

				if (Trigonometry.checkIfPointBelongsToSegment(vectorToLocation, wallSection.getVectorToStartPoint(),
						wallSection.getVectorToEndPoint())) {
					return false;
				}
			}
		}

		return accesible;
	}

	/**
	 * Iterator through the lists of agents and calculate next mode.
	 * 
	 * @exception Exception
	 *                on error
	 */
	public final void calculateAllAgentsNextAction() throws Exception {

		for (Entry<Class<?>, List<CatpedsimAgent>> mapOfAgentsIterator : mapOfAgentsLists.entrySet()) {

			Class<?> currentAgentType = mapOfAgentsIterator.getKey();
			List<CatpedsimAgent> listOfAgents = mapOfAgentsIterator.getValue();

			synchronized (listOfAgents) {
				for (Iterator<CatpedsimAgent> iterator = listOfAgents.iterator(); iterator.hasNext();) {
					CatpedsimAgent currentAgent = iterator.next();

					if (currentAgent.isStopped() || currentAgent.isTransported() || currentAgent.isNotActive()) {
						continue;
					}

					try {
						boolean arrivedAtDestination = currentAgent.computeAttractiveForceToTarget(aStarPathFinder,
								geometry);

						if (arrivedAtDestination) {
							CatpedsimTransitionZone currentAgentTransitionZone = currentAgent.getTransitionZone();

							if (currentAgentTransitionZone == null) {
								if (currentAgentType.equals(CatpedsimAutonomousVehicle.class)) {
									removeAllTransportedAgentsFromTheSimulation(currentAgent, currentAgentType);
								}
								iterator.remove();
								continue;
							} else {
								changeAgentState(currentAgentType, currentAgent, currentAgentTransitionZone);
								continue;
							}
						}

						AgentBehaviour behaviour = currentAgent.getSequenceOfBehaviours();
						XmlPath currentPath = null;
						if (behaviour != null) {
							currentPath = behaviour.getCurrentBehaviour();
						}

						if ((currentPath != null && currentPath.canMove()) || currentPath == null) {

							for (Entry<Class<?>, List<CatpedsimAgent>> mapOfOtherAgentsIterator : mapOfAgentsLists
									.entrySet()) {

								Class<?> agentToInteractType = mapOfOtherAgentsIterator.getKey();
								List<CatpedsimAgent> listOfAgentsToInteract = mapOfOtherAgentsIterator.getValue();

								if (listOfAgentsToInteract.size() > 0) {
									currentAgent.computeRepulsiveForcesBetweenAgents(listOfAgentsToInteract,
											socialForceInteractionMap.get(new SocialForceAgentsPair<>(currentAgentType,
													agentToInteractType)));
								}
							}
							currentAgent.computeRepulsiveForcesBetweenAgentAndWallsObstacles(geometry);
							currentAgent.moveAgent(geometry, new Point2D.Double(worldWidth, worldDepth), 1);
						}

					} catch (Exception e) {
						LOGGER.warn("Error while computing the action for the next tick.", e);
					}
				}
			}
		}
	}

	/**
	 * Remove from the simulation all transported agents.
	 * 
	 * @param currentAgent
	 *            current agent
	 * @param agentClassType
	 *            agent class type
	 */
	private void removeAllTransportedAgentsFromTheSimulation(final CatpedsimAgent currentAgent,
			final Class<?> agentClassType) {

		try {
			LinkedList<StringIdentifier> transporting = currentAgent.getTransportsAgents();
			if (transporting != null && !transporting.isEmpty()) {

				for (StringIdentifier id : transporting) {
					deleteAgentWithStringIdentifier(id);
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while removing the transported agents from the simulation", ex);
		}
	}

	/**
	 * Change agent state when it arrives in a transition zone.
	 * 
	 * @param currentAgentType
	 *            current agent type
	 * @param currentAgent
	 *            current agent object
	 * @param transitionZone
	 *            transition zone the agent reached
	 */
	private void changeAgentState(final Class<?> currentAgentType, final CatpedsimAgent currentAgent,
			final CatpedsimTransitionZone transitionZone) {

		try {
			if (currentAgentType.equals(CatpedsimPedestrian.class)) {
				if (transitionZone.getZoneType().equals(TransitionZoneTypes.AUTONOMOUS_VEHICLE_RACK)) {

					LinkedList<StringIdentifier> availablePodsInTransitZone = transitionZone.getEnclosedAgents();

					if (!availablePodsInTransitZone.isEmpty()) {

						StringIdentifier podId = availablePodsInTransitZone.getLast();
						CatpedsimAgent parkedPod = lookupAgentWithStringIdentifier(podId,
								CatpedsimAutonomousVehicle.class);

						if (parkedPod != null) {
							TransitionZoneMatrix transitionMatrix = transferZonesDistributionMatrix
									.get(currentAgentType);

							int transitionMatrixRow = transitionMatrix
									.lookupEntranceInTransitionMatrix(transitionZone.getTransitionZoneName());
							if (transitionMatrixRow == -1) {
								return;
							}

							int transitionMatrixColumn = transitionMatrix
									.probabilisticDistributionInTransitionArea(transitionMatrixRow);
							if (transitionMatrixColumn == -1) {
								return;
							}

							AgentBehaviour transportBehaviour = transitionMatrix
									.getMatrixBehaviour(transitionMatrixRow, transitionMatrixColumn).getCopy();

							CatpedsimTransitionZone nextTransitionZone = lookupTransitionZone(
									transitionMatrix.getExit(transitionMatrixColumn));

							transportBehaviour.getCurrentBehaviour().setAgentState(AgentState.TRANSPORT_OTHER_AGENTS);
							parkedPod.removeAllBehaviours();
							parkedPod.setSequenceOfBehaviours(transportBehaviour);
							parkedPod.addAgentInTransportedList(currentAgent.getUniqueIdentifier());
							parkedPod.setTransitionZone(nextTransitionZone);

							currentAgent.removeAllBehaviours();
							currentAgent.setSequenceOfBehaviours(
									new AgentBehaviour(AgentState.TRANSPORTED_BY_ANOTHER_AGENT));
							currentAgent.setTransitionZone(nextTransitionZone);
							currentAgent.setAgentExit(null);
							currentAgent.setVectorToAgentsTarget(null);

							availablePodsInTransitZone.removeLast();
						}
					}
				} else if (transitionZone.getZoneType().equals(TransitionZoneTypes.BICYCLE_RACK)) {
					changePedestrianToBicycle(currentAgent, transitionZone);
				}
			} else if (currentAgentType.equals(CatpedsimAutonomousVehicle.class)) {
				if (transitionZone.getZoneType().equals(TransitionZoneTypes.AUTONOMOUS_VEHICLE_RACK)) {

					LinkedList<StringIdentifier> availablePodsInTransitZone = transitionZone.getEnclosedAgents();
					availablePodsInTransitZone.add(currentAgent.getUniqueIdentifier());

					currentAgent.removeAllBehaviours();
					currentAgent.setSequenceOfBehaviours(new AgentBehaviour(AgentState.STOP_AND_WAIT));
					currentAgent.setTransitionZone(null);
					currentAgent.setAgentExit(null);
					currentAgent.setVectorToAgentsTarget(null);

					LinkedList<StringIdentifier> transportedAgents = currentAgent.getTransportsAgents();

					for (Iterator<StringIdentifier> transportedAgentsIterator = transportedAgents
							.iterator(); transportedAgentsIterator.hasNext();) {

						StringIdentifier transportedAgentId = transportedAgentsIterator.next();
						transportedAgentsIterator.remove();

						CatpedsimAgent transportedAgent = lookupAgentWithStringIdentifier(transportedAgentId,
								CatpedsimPedestrian.class);

						TransitionZoneMatrix transitionMatrix = transferZonesDistributionMatrix.get(currentAgentType);

						int transitionMatrixRow = transitionMatrix
								.lookupEntranceInTransitionMatrix(transitionZone.getTransitionZoneName());
						if (transitionMatrixRow == -1) {
							return;
						}
						int transitionMatrixColumn = transitionMatrix
								.probabilisticDistributionInTransitionArea(transitionMatrixRow);
						if (transitionMatrixColumn == -1) {
							return;
						}

						AgentBehaviour transportBehaviour = transitionMatrix
								.getMatrixBehaviour(transitionMatrixRow, transitionMatrixColumn).getCopy();

						transportBehaviour.getCurrentBehaviour()
								.setAgentState(AgentState.MOVE_AUTONOMOUSLY_WITHOUT_CONSTRAINTS);
						transportedAgent.removeAllBehaviours();
						transportedAgent.setSequenceOfBehaviours(transportBehaviour);
						transportedAgent.setPositionVector(currentAgent.getPositionVector().copy());

						CatpedsimTransitionZone nextTargetTransportedAgent = lookupTransitionZone(
								transitionMatrix.getExit(transitionMatrixColumn));
						transportedAgent.setTransitionZone(nextTargetTransportedAgent);
					}
				}
			} else if (currentAgentType.equals(CatpedsimBicycle.class)) {
				if (transitionZone.getZoneType().equals(TransitionZoneTypes.BICYCLE_RACK)) {
					changeBicycleToPedestrian(currentAgent, transitionZone);
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("The agent state on the transition area coun't be changed.", ex);
		}
	}

	/**
	 * Change bicycle to pedestrian.
	 * 
	 * @param currentAgent
	 *            current agent object
	 * @param transitionZone
	 *            transition zone object
	 */
	private void changeBicycleToPedestrian(final CatpedsimAgent currentAgent,
			final CatpedsimTransitionZone transitionZone) {

		LinkedList<StringIdentifier> availableBikesInTransitZone = transitionZone.getEnclosedAgents();
		availableBikesInTransitZone.add(currentAgent.getUniqueIdentifier());

		currentAgent.removeAllBehaviours();
		currentAgent.setSequenceOfBehaviours(new AgentBehaviour(AgentState.STOP_AND_WAIT));
		currentAgent.setTransitionZone(null);
		currentAgent.setAgentExit(null);
		currentAgent.setVectorToAgentsTarget(null);

		LinkedList<StringIdentifier> transportedAgents = currentAgent.getTransportsAgents();

		for (Iterator<StringIdentifier> transportedAgentsIterator = transportedAgents
				.iterator(); transportedAgentsIterator.hasNext();) {

			StringIdentifier transportedAgentId = transportedAgentsIterator.next();
			transportedAgentsIterator.remove();

			CatpedsimAgent transportedAgent = lookupAgentWithStringIdentifier(transportedAgentId,
					CatpedsimPedestrian.class);

			TransitionZoneMatrix transitionMatrix = transferZonesDistributionMatrix.get(CatpedsimBicycle.class);

			int transitionMatrixRow = transitionMatrix
					.lookupEntranceInTransitionMatrix(transitionZone.getTransitionZoneName());
			if (transitionMatrixRow == -1) {
				return;
			}
			int transitionMatrixColumn = transitionMatrix
					.probabilisticDistributionInTransitionArea(transitionMatrixRow);
			if (transitionMatrixColumn == -1) {
				return;
			}

			AgentBehaviour transportBehaviour = transitionMatrix
					.getMatrixBehaviour(transitionMatrixRow, transitionMatrixColumn).getCopy();

			transportBehaviour.getCurrentBehaviour().setAgentState(AgentState.MOVE_AUTONOMOUSLY_WITHOUT_CONSTRAINTS);
			transportedAgent.removeAllBehaviours();
			transportedAgent.setSequenceOfBehaviours(transportBehaviour);
			transportedAgent.setPositionVector(currentAgent.getPositionVector().copy());

			CatpedsimTransitionZone nextTargetTransportedAgent = lookupTransitionZone(
					transitionMatrix.getExit(transitionMatrixColumn));
			transportedAgent.setTransitionZone(nextTargetTransportedAgent);
		}
	}

	/**
	 * Pedestrian is boarding a bicycle.
	 * 
	 * @param currentAgent
	 *            pedestrian object
	 * @param transitionZone
	 *            bicycle transition zone
	 */
	private void changePedestrianToBicycle(final CatpedsimAgent currentAgent,
			final CatpedsimTransitionZone transitionZone) {
		LinkedList<StringIdentifier> availableBikesInTransitZone = transitionZone.getEnclosedAgents();

		if (!availableBikesInTransitZone.isEmpty()) {

			StringIdentifier bikeId = availableBikesInTransitZone.getLast();
			CatpedsimAgent parkedBike = lookupAgentWithStringIdentifier(bikeId, CatpedsimBicycle.class);

			if (parkedBike != null) {
				TransitionZoneMatrix transitionMatrix = transferZonesDistributionMatrix.get(CatpedsimPedestrian.class);

				int transitionMatrixRow = transitionMatrix
						.lookupEntranceInTransitionMatrix(transitionZone.getTransitionZoneName());
				if (transitionMatrixRow == -1) {
					return;
				}

				int transitionMatrixColumn = transitionMatrix
						.probabilisticDistributionInTransitionArea(transitionMatrixRow);
				if (transitionMatrixColumn == -1) {
					return;
				}

				AgentBehaviour transportBehaviour = transitionMatrix
						.getMatrixBehaviour(transitionMatrixRow, transitionMatrixColumn).getCopy();

				CatpedsimTransitionZone nextTransitionZone = lookupTransitionZone(
						transitionMatrix.getExit(transitionMatrixColumn));

				transportBehaviour.getCurrentBehaviour().setAgentState(AgentState.TRANSPORT_OTHER_AGENTS);
				parkedBike.removeAllBehaviours();
				parkedBike.setSequenceOfBehaviours(transportBehaviour);
				parkedBike.addAgentInTransportedList(currentAgent.getUniqueIdentifier());
				parkedBike.setTransitionZone(nextTransitionZone);

				currentAgent.removeAllBehaviours();
				currentAgent.setSequenceOfBehaviours(new AgentBehaviour(AgentState.TRANSPORTED_BY_ANOTHER_AGENT));
				currentAgent.setTransitionZone(nextTransitionZone);
				currentAgent.setAgentExit(null);
				currentAgent.setVectorToAgentsTarget(null);

				availableBikesInTransitZone.removeLast();
			}
		}
	}

	/**
	 * Lookup agent with the specified unique identifier.
	 * 
	 * @param identifier
	 *            unique identifier to search
	 * @param agentType
	 *            agent type
	 * @return agent object
	 */
	private CatpedsimAgent lookupAgentWithStringIdentifier(final StringIdentifier identifier,
			final Class<?> agentType) {
		if (mapOfAgentsLists.containsKey(agentType)) {
			List<CatpedsimAgent> listOfAgents = mapOfAgentsLists.get(agentType);

			for (CatpedsimAgent agentInList : listOfAgents) {
				if (agentInList.getUniqueIdentifier().equals(identifier)) {
					return agentInList;
				}
			}
		}

		return null;
	}

	/**
	 * Based on the origin-destiantion matrices introduce new agents in the
	 * simulation at the specified entrances, moving towards the specified
	 * exits. When an origin-destination matrix is exhausted delete it from the
	 * simulation shcedule.
	 * 
	 * An entrance is always a CatpedsimCrossing but an exit could be a
	 * CatpedsimTransition zone.
	 */
	public final void generateDepartures() {
		int simulationTimeSeconds = (int) (numberOfTicks.getNumberOfTicksAsLong() * tickLengthSeconds);

		try {
			for (Map.Entry<Class<?>, Map<Integer, OriginDestinationMatrix<?>>> allAgentsOdMatricesEntry : agentsOriginDestinationMatricesWithTimeKey
					.entrySet()) {

				Map<Integer, OriginDestinationMatrix<?>> odMatricesForAgentWithTimeKey = allAgentsOdMatricesEntry
						.getValue();

				for (Iterator<Map.Entry<Integer, OriginDestinationMatrix<?>>> odMatricesForAgentIterator = odMatricesForAgentWithTimeKey
						.entrySet().iterator(); odMatricesForAgentIterator.hasNext();) {

					Map.Entry<Integer, OriginDestinationMatrix<?>> mapEntry = odMatricesForAgentIterator.next();
					Integer matrixTime = mapEntry.getKey();

					if (simulationTimeSeconds >= matrixTime) {
						OriginDestinationMatrix<?> originDestinationMatrix = mapEntry.getValue();

						boolean matrixExhausted = true;

						for (int indexRow = 1; indexRow < originDestinationMatrix.getNumberOfRows(); indexRow++) {
							Object entrance = originDestinationMatrix.getEntrance(indexRow);

							for (int indexColumn = 1; indexColumn < originDestinationMatrix
									.getNumberOfColumns(); indexColumn++) {

								Object agentsFlow = originDestinationMatrix.getMatrixCell(indexRow, indexColumn);
								Color agentColor = originDestinationMatrix.getColor(indexRow, indexColumn);
								Object exit = originDestinationMatrix.getExit(indexColumn);

								if (agentsFlow != null && agentsFlow instanceof Float) {
									float totalAgentsToDepart = (Float) originDestinationMatrix.getMatrixCell(indexRow,
											indexColumn);
									float readyToDepart = originDestinationMatrix.getNumberToDepart(indexRow,
											indexColumn);
									int deploy = (int) readyToDepart;

									if (totalAgentsToDepart > 0) {
										identifyEntranceExitAndGenerateAgents(
												originDestinationMatrix.getAgentClassType(), deploy, entrance, exit,
												agentColor,
												originDestinationMatrix.getPrecomputedPath(indexRow, indexColumn));

										originDestinationMatrix.updateReadyToDepart(indexRow, indexColumn, deploy);
										originDestinationMatrix.updateMatrixCell(indexRow, indexColumn,
												totalAgentsToDepart - deploy);

										matrixExhausted = false;
									}
								}
							}
						}

						if (matrixExhausted) {
							originDestinationMatrix.clear();
							odMatricesForAgentIterator.remove();
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while generating agents departure profiles.", ex);
		}
	}

	/**
	 * Find the entrance and the exit in the map of crossings and generate
	 * locations for the agents to be spawned. The entrance is always a
	 * CatpedsimCrossing, the exit could be a CatpedsimCrossing or a
	 * CatoedsimTransition.
	 * 
	 * @param agentClassType
	 *            type of agent
	 * @param agentsToDepartThisTick
	 *            the number of agents departing the current tick
	 * @param entranceId
	 *            entrance id
	 * @param exitId
	 *            exit id
	 * @param agentColor
	 *            agent color
	 * @param precomputedPath
	 *            pre-computed path and behaviour
	 * @throws Exception
	 *             on error
	 */
	private void identifyEntranceExitAndGenerateAgents(final Object agentClassType, final int agentsToDepartThisTick,
			final Object entranceId, final Object exitId, final Color agentColor, final AgentBehaviour precomputedPath)
			throws Exception {

		CatpedsimCrossing entrance = null;
		CatpedsimCrossing exit = null;

		for (CatpedsimCrossing crossing : geometry.getCrossings()) {
			if (entranceId instanceof String) {
				if (((String) entranceId).equalsIgnoreCase(crossing.getName())) {
					entrance = crossing;
				}
			} else if (entranceId instanceof Float) {
				if (crossing.getName().matches(RegexPatterns.ANY_NUMBER)) {
					Float entranceIdFloat = Float.parseFloat(crossing.getName());

					if (entranceIdFloat.equals(entranceId)) {
						entrance = crossing;
					}
				}
			}

			if (exitId instanceof String) {
				if (((String) exitId).equalsIgnoreCase(crossing.getName())) {
					exit = crossing;
				}
			} else if (exitId instanceof Float) {
				if (crossing.getName().matches(RegexPatterns.ANY_NUMBER)) {
					Float exitIdFloat = Float.parseFloat(crossing.getName());

					if (exitIdFloat.equals(exitId)) {
						exit = crossing;
					}
				}
			}

			if (!Objects.isNull(entrance) && !Objects.isNull(exit)) {
				break;
			}
		}

		if (entrance == null) {
			LOGGER.warn("Origin-destination matrix " + ((Class<?>) agentClassType).getSimpleName()
					+ " error, the entrance  " + entranceId.toString() + " does not exist");
			return;
		}

		if (exit != null) {
			distributeAgentsAtEntranceForExit(agentClassType, agentsToDepartThisTick, entrance, exit, agentColor,
					precomputedPath);
		} else {
			CatpedsimTransitionZone exitThroughTransitionZone = lookupTransitionZone(exitId);

			if (exitThroughTransitionZone != null) {
				distributeAgentsAtEntranceForTransitionZone(agentClassType, agentsToDepartThisTick, entrance,
						exitThroughTransitionZone, agentColor, precomputedPath);
			} else {
				LOGGER.warn("Origin-destination matrix " + ((Class<?>) agentClassType).getSimpleName()
						+ " error, the exit/transition zone " + exitId.toString() + " does not exist");
				return;
			}
		}
	}

	/**
	 * Search transition zone based on caption.
	 * 
	 * @param zoneName
	 *            transition zone name
	 * @return transition zone object
	 */
	private CatpedsimTransitionZone lookupTransitionZone(final Object zoneName) {
		CatpedsimTransitionZone transition = null;
		String caption = null;

		if (zoneName instanceof String) {
			caption = (String) zoneName;
		} else if (zoneName instanceof Float) {
			Integer zoneId = ((Float) zoneName).intValue();
			caption = Integer.toString(zoneId);
		} else {
			return null;
		}

		for (Map.Entry<TransitionZoneTypes, Map<String, CatpedsimTransitionZone>> mapEntry : geometry
				.getTransitionZones().entrySet()) {

			Map<String, CatpedsimTransitionZone> zoneMap = mapEntry.getValue();

			for (Map.Entry<String, CatpedsimTransitionZone> zone : zoneMap.entrySet()) {
				if (zone.getKey().equals(caption)) {
					return zone.getValue();
				}
			}
		}

		return transition;
	}

	/**
	 * Calculate the maximum speed per tick length an agent can achieve.
	 * 
	 * @param agentClass
	 *            type of the agent
	 * @return the agent speed per tick length
	 */
	private float calculateMaximumSpeedAccordingToAgentType(final Class<?> agentClass) {
		float maximumSpeed = pedestrianWalkingVelocity;

		if (agentClass.equals(CatpedsimPedestrian.class)) {
			maximumSpeed = Utils.random(pedestrianWalkingVelocity, pedestrianMarathonVelocity);
		} else if (agentClass.equals(CatpedsimAutonomousVehicle.class)) {
			maximumSpeed = Utils.random(pedestrianJoggingVelocity, pedestrianHighestVelocity);
		} else if (agentClass.equals(CatpedsimBicycle.class)) {
			maximumSpeed = Utils.random(pedestrianMarathonVelocity, pedestrianHighestVelocity);
		}

		return maximumSpeed;
	}

	/**
	 * Distribute agents at the entrance moving towards a transition zone.
	 * 
	 * @param agentClassType
	 *            type of agent
	 * @param agentsToDepartThisTick
	 *            the number of agents to depart at the current tick
	 * @param entrance
	 *            entrance object
	 * @param exitThroughTransitionZone
	 *            agent's intermediate target is a transition zone
	 * @param agentColor
	 *            agent color
	 * @param precomputedPath
	 *            pre-computed path
	 * @throws Exception
	 *             on error
	 */
	private void distributeAgentsAtEntranceForTransitionZone(final Object agentClassType,
			final int agentsToDepartThisTick, final CatpedsimCrossing entrance,
			final CatpedsimTransitionZone exitThroughTransitionZone, final Color agentColor,
			final AgentBehaviour precomputedPath) throws Exception {

		Class<?> agentClass = (Class<?>) agentClassType;
		Constructor<?> constructorForAgentClass = null;

		float maximumSpeed = calculateMaximumSpeedAccordingToAgentType(agentClass);

		if (precomputedPath != null) {
			constructorForAgentClass = agentClass
					.getConstructor(new Class[] { String.class, String.class, StringIdentifier.class, Vec3f.class,
							float.class, CatpedsimTransitionZone.class, Color.class, AgentBehaviour.class });

		} else {
			constructorForAgentClass = agentClass.getConstructor(new Class[] { String.class, String.class,
					StringIdentifier.class, Vec3f.class, float.class, CatpedsimTransitionZone.class, Color.class });
		}

		PVector direction = PVector.sub(entrance.getVertexAtIndex(1), entrance.getVertexAtIndex(0));
		direction.normalize();

		for (int indexAgent = 0; indexAgent < agentsToDepartThisTick; indexAgent++) {
			float stepFromEntrance = entrance.getCrosingLength() / 2;
			PVector offsetFromVertex0 = PVector.mult(direction, stepFromEntrance);
			offsetFromVertex0.add(entrance.getVertexAtIndex(0));

			String newAgentName = generateUniqueName(agentClass.getSimpleName(), initialisation.getPublisherId(),
					mapOfAgentsCounters.get(agentClass).incrementAndGet());
			StringIdentifier uniqueIdentifier = new StringIdentifier(newAgentName);

			Vec3f agentLocation = new Vec3f(offsetFromVertex0.x, offsetFromVertex0.y, offsetFromVertex0.z);

			if (precomputedPath != null) {
				AgentBehaviour newBehaviour = precomputedPath.getCopy();

				CatpedsimAgent newAgent = (CatpedsimAgent) constructorForAgentClass.newInstance(
						initialisation.getPublisherId(), newAgentName, uniqueIdentifier, agentLocation, maximumSpeed,
						exitThroughTransitionZone, agentColor, newBehaviour);

				if (agentClass.equals(CatpedsimBicycle.class)) {
					newBehaviour.getCurrentBehaviour().setAgentState(AgentState.TRANSPORT_OTHER_AGENTS);

					StringIdentifier pedestrianRidingBikeId = generateTransportedPedestrian(
							initialisation.getPublisherId(), agentLocation, exitThroughTransitionZone);

					newAgent.addAgentInTransportedList(pedestrianRidingBikeId);
				}

				mapOfAgentsLists.get(agentClassType).add(newAgent);
			} else {
				mapOfAgentsLists.get(agentClassType)
						.add((CatpedsimAgent) constructorForAgentClass.newInstance(initialisation.getPublisherId(),
								newAgentName, uniqueIdentifier, agentLocation, maximumSpeed, exitThroughTransitionZone,
								agentColor));
			}
		}
	}

	/**
	 * Generate a pedestrian with a transported state.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param agentLocation
	 *            agent location
	 * @param exitThroughTransitionZone
	 *            the agent's target is a transition zone
	 * @return the unique identifier for the generated agent
	 */
	private StringIdentifier generateTransportedPedestrian(final String publisherId, final Vec3f agentLocation,
			final CatpedsimTransitionZone exitThroughTransitionZone) {

		Vec3f newAgentLocation = new Vec3f();
		newAgentLocation.set(agentLocation);

		try {
			float maximumSpeed = calculateMaximumSpeedAccordingToAgentType(CatpedsimPedestrian.class);

			String newAgentName = generateUniqueName(CatpedsimPedestrian.class.getSimpleName(), publisherId,
					mapOfAgentsCounters.get(CatpedsimPedestrian.class).incrementAndGet());
			StringIdentifier uniqueIdentifier = new StringIdentifier(newAgentName);

			AgentBehaviour newBehaviour = new AgentBehaviour(AgentState.TRANSPORTED_BY_ANOTHER_AGENT);

			CatpedsimPedestrian transportedPedestrian = new CatpedsimPedestrian(publisherId, newAgentName,
					uniqueIdentifier, newAgentLocation, maximumSpeed, exitThroughTransitionZone, Color.BLACK,
					newBehaviour);

			mapOfAgentsLists.get(CatpedsimPedestrian.class).add(transportedPedestrian);

			return transportedPedestrian.getUniqueIdentifier();
		} catch (Exception ex) {
			LOGGER.warn("Could not generate a transported pedestrian", ex);
			return null;
		}
	}

	/**
	 * Compute locations for the agents to be spawned at specified entrances.
	 * 
	 * @param agentClassType
	 *            type of agent
	 * @param agentsToDepartThisTick
	 *            the number of agents to depart at the current tick
	 * @param entrance
	 *            entrance object
	 * @param exit
	 *            exit object
	 * @param agentColor
	 *            agent color
	 * @param precomputedPath
	 *            pre-computed path
	 * @throws Exception
	 *             on error
	 */
	private void distributeAgentsAtEntranceForExit(final Object agentClassType, final int agentsToDepartThisTick,
			final CatpedsimCrossing entrance, final CatpedsimCrossing exit, final Color agentColor,
			final AgentBehaviour precomputedPath) throws Exception {

		Class<?> agentClass = (Class<?>) agentClassType;
		Constructor<?> constructorForAgentClass = null;
		float maximumSpeed = calculateMaximumSpeedAccordingToAgentType(agentClass);

		if (precomputedPath != null) {
			constructorForAgentClass = agentClass
					.getConstructor(new Class[] { String.class, String.class, StringIdentifier.class, Vec3f.class,
							float.class, CatpedsimCrossing.class, Color.class, AgentBehaviour.class });
		} else if (aStarPathFinder != null) {
			constructorForAgentClass = agentClass
					.getConstructor(new Class[] { String.class, String.class, StringIdentifier.class, Vec3f.class,
							float.class, CatpedsimCrossing.class, Color.class, LinkedList.class });
		} else {
			constructorForAgentClass = agentClass.getConstructor(new Class[] { String.class, String.class,
					StringIdentifier.class, Vec3f.class, float.class, CatpedsimCrossing.class, Color.class });
		}

		PVector direction = PVector.sub(entrance.getVertexAtIndex(1), entrance.getVertexAtIndex(0));
		direction.normalize();

		for (int indexAgent = 0; indexAgent < agentsToDepartThisTick; indexAgent++) {
			float stepFromEntrance = entrance.getCrosingLength() / 2;

			PVector offsetFromVertex0 = PVector.mult(direction, stepFromEntrance);
			offsetFromVertex0.add(entrance.getVertexAtIndex(0));

			String newAgentName = generateUniqueName(agentClass.getSimpleName(), initialisation.getPublisherId(),
					mapOfAgentsCounters.get(agentClass).incrementAndGet());
			StringIdentifier uniqueIdentifier = new StringIdentifier(newAgentName);

			Vec3f agentLocation = new Vec3f(offsetFromVertex0.x, offsetFromVertex0.y, offsetFromVertex0.z);

			PVector exitVector = PVector.add(exit.getVertexAtIndex(0), exit.getVertexAtIndex(1));
			exitVector.div(2);

			if (precomputedPath != null) {
				mapOfAgentsLists.get(agentClassType)
						.add((CatpedsimAgent) constructorForAgentClass.newInstance(initialisation.getPublisherId(),
								newAgentName, uniqueIdentifier, agentLocation, maximumSpeed, exit, agentColor,
								precomputedPath.getCopy()));
			} else if (aStarPathFinder != null) {
				LinkedList<NodePath> generatedPath = AStarPathFinder.calculateShortestPath(aStarPathFinder,
						offsetFromVertex0, exitVector);

				mapOfAgentsLists.get(agentClassType)
						.add((CatpedsimAgent) constructorForAgentClass.newInstance(initialisation.getPublisherId(),
								newAgentName, uniqueIdentifier, agentLocation, maximumSpeed, exit, agentColor,
								generatedPath));
			} else {
				mapOfAgentsLists.get(agentClassType)
						.add((CatpedsimAgent) constructorForAgentClass.newInstance(initialisation.getPublisherId(),
								newAgentName, uniqueIdentifier, agentLocation, maximumSpeed, exit, agentColor));

			}
		}
	}

	/**
	 * Smooth path.
	 * 
	 * @param path
	 *            initial path
	 * @return smooth path
	 */
	@SuppressWarnings("unused")
	private LinkedList<NodePath> smoothPath(final LinkedList<NodePath> path) {
		if (path == null || path.isEmpty()) {
			return null;
		}

		LinkedList<NodePath> smoothPath = new LinkedList<NodePath>();
		NodePath lastAddedNode = path.get(path.size() - 1);
		smoothPath.add(lastAddedNode);

		for (int indexNode = path.size() - 2; indexNode >= 0; indexNode--) {
			NodePath node = path.get(indexNode);

			if (checkIfNodesIntersectWall(lastAddedNode, node) || checkIfNodesIntersectObstacle(lastAddedNode, node)) {
				lastAddedNode = path.get(indexNode + 1);
				smoothPath.addFirst(lastAddedNode);
			}
		}
		smoothPath.addFirst(path.get(0));

		path.clear();
		return smoothPath;
	}

	/**
	 * Check if two nodes intersect a wall.
	 * 
	 * @param lastAddedNode
	 *            start node
	 * @param node
	 *            next node
	 * @return true if the nodes intersect a wall
	 */
	private boolean checkIfNodesIntersectWall(final NodePath lastAddedNode, final NodePath node) {
		boolean state = false;

		for (CatpedsimObstacle wall : geometry.getWalls()) {
			for (int indexSection = 0; indexSection < wall.getObstacleSections().length; indexSection++) {
				ShapeSection wallSection = wall.getObstacleSections()[indexSection];

				try {
					if (Trigonometry.checkIfLinesIntersect(new PVector(node.getX(), node.getY()),
							new PVector(lastAddedNode.getX(), lastAddedNode.getY()),
							wallSection.getVectorToStartPoint(), wallSection.getVectorToEndPoint())) {
						return true;
					}
				} catch (Exception ex) {
					LOGGER.warn("Error while checking the path nodes intersect the walls.", ex);
				}
			}
		}

		return state;
	}

	/**
	 * Check if the segment composed of two path nodes intersect obstacles.
	 * 
	 * @param lastAddedNode
	 *            last added path node
	 * @param node
	 *            new node to check
	 * @return true if the segment composed of two nodes intersect obstacles
	 */
	private boolean checkIfNodesIntersectObstacle(final NodePath lastAddedNode, final NodePath node) {
		boolean state = false;

		for (CatpedsimObstacle obstacle : geometry.getObstacles()) {
			for (int indexSection = 0; indexSection < obstacle.getObstacleSections().length; indexSection++) {
				ShapeSection wallSection = obstacle.getObstacleSections()[indexSection];
				try {
					if (Trigonometry.checkIfLinesIntersect(new PVector(node.getX(), node.getY()),
							new PVector(lastAddedNode.getX(), lastAddedNode.getY()),
							wallSection.getVectorToStartPoint(), wallSection.getVectorToEndPoint())) {
						return true;
					}
				} catch (Exception ex) {
					LOGGER.warn("Error while checking the path nodes intersect obstacles.", ex);
				}
			}
		}

		return state;
	}

	/**
	 * Get the total number of pedestrians.
	 * 
	 * @return number of pedestrians
	 */
	public final int getNumberOfPedestrians() {
		int numberOfPedestrians = -1;

		synchronized (mapOfAgentsLists.get(CatpedsimPedestrian.class)) {
			numberOfPedestrians = mapOfAgentsLists.get(CatpedsimPedestrian.class).size();
		}

		return numberOfPedestrians;
	}

	/**
	 * Get the total number of autonomous vehicles.
	 * 
	 * @return number of autonomous vehicles
	 */
	public final int getNumberOfAutonomousVehicles() {
		int numberOfAutonomousVehicles = -1;
		if (mapOfAgentsLists.get(CatpedsimAutonomousVehicle.class) != null) {
			synchronized (mapOfAgentsLists.get(CatpedsimAutonomousVehicle.class)) {
				numberOfAutonomousVehicles = mapOfAgentsLists.get(CatpedsimAutonomousVehicle.class).size();
			}
		}
		return numberOfAutonomousVehicles;
	}

	/**
	 * Get the number of bicycles.
	 * 
	 * @return the number of bicycles
	 */
	public final int getNumberOfBicycles() {
		int numberOfBicycles = -1;
		if (mapOfAgentsLists.get(CatpedsimBicycle.class) != null) {
			synchronized (mapOfAgentsLists.get(CatpedsimBicycle.class)) {
				numberOfBicycles = mapOfAgentsLists.get(CatpedsimBicycle.class).size();
			}
		}
		return numberOfBicycles;
	}

	/**
	 * Get list of agents.
	 * 
	 * @param agentType
	 *            agent type
	 * @return list of agent of the specified type
	 */
	public final List<CatpedsimAgent> getListOfAgents(final Class<?> agentType) {
		try {
			return mapOfAgentsLists.get(agentType);
		} catch (Exception ex) {
			LOGGER.warn("Error while getting the list of agents.", ex);
		}
		return null;
	}

	/**
	 * Get the list of walls.
	 * 
	 * @return list of walls
	 */
	public final List<CatpedsimObstacle> getListOfWalls() {
		return geometry.getWalls();
	}

	/**
	 * Get the list of obstacles.
	 * 
	 * @return list of obstacles
	 */
	public final List<CatpedsimObstacle> getListOfObstacles() {
		return geometry.getObstacles();
	}

	/**
	 * Get agent from the list at specified index.
	 * 
	 * @param index
	 *            index in the agents list
	 * @param agentType
	 *            type of agent
	 * @return agent object
	 */
	public final CatpedsimAgent getAgentAtIndex(final int index, final Class<?> agentType) {
		CatpedsimAgent autonomousVehicle = null;

		try {
			autonomousVehicle = mapOfAgentsLists.get(agentType).get(index);
		} catch (Exception ex) {
			LOGGER.warn("Error while getting an agent at specified index in the list.", ex);
		}

		return autonomousVehicle;
	}

	/**
	 * Generate pedestrians in a linear arrangement.
	 * 
	 * @param topLeft
	 *            the top left corner of the arrangement
	 * @param nofColumns
	 *            number of columns
	 * @param nofRows
	 *            number of rows
	 */
	public final void generatePedestriansInLinearArrangement(final PVector topLeft, final int nofColumns,
			final int nofRows) {
		PVector location = PVector.random2D();
		float maximumPedestrianSpeed = Utils.random(pedestrianWalkingVelocity, pedestrianJoggingVelocity);

		if (mapOfAgentsLists.get(CatpedsimPedestrian.class).size() >= nofColumns * nofRows) {
			return;
		}

		try {
			synchronized (mapOfAgentsLists.get(CatpedsimPedestrian.class)) {
				for (int row = 0; row < nofRows; row++) {
					for (int col = 0; col < nofColumns; col++) {
						int padding = 0;

						if (row >= nofRows / 2) {
							padding = 2;
						}

						location.set(topLeft.x + col * PedestrianConstraints.SHOULDER_WIDTH,
								topLeft.y + row * PedestrianConstraints.SHOULDER_WIDTH + padding);

						String newAgentName = generateUniqueName(CatpedsimPedestrian.class.getSimpleName(),
								initialisation.getPublisherId(),
								mapOfAgentsCounters.get(CatpedsimPedestrian.class).incrementAndGet());
						StringIdentifier uniqueIdentifier = new StringIdentifier(newAgentName);

						mapOfAgentsLists.get(CatpedsimPedestrian.class)
								.add(new CatpedsimPedestrian(initialisation.getPublisherId(), newAgentName,
										uniqueIdentifier, new Vec3f(location.x, location.y, location.z),
										maximumPedestrianSpeed));
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while initializing the random pedestrians.", ex);
		}
	}

	/**
	 * Initialise the pedestrian's location and age. The access on the
	 * pedestrian list is synchronised in order to avoid simultaneous operations
	 * on the same object.
	 * 
	 * @param publisherId
	 *            who is adding the pedestrian
	 * @throws Exception
	 *             could arise during an illegal operation on the pedestrian
	 *             list
	 */
	public final void initialiseRandomPedestrians(final String publisherId) {
		PVector location;
		float maximumPedestrianSpeed = Utils.random(pedestrianWalkingVelocity, pedestrianJoggingVelocity);

		List<CatpedsimAgent> listOfPedestrians = mapOfAgentsLists.get(CatpedsimPedestrian.class);
		int numberOfPedestrians = initialisation.getNumberOfPedestrians();

		if (listOfPedestrians == null || numberOfPedestrians == 0 || listOfPedestrians.size() >= numberOfPedestrians) {
			return;
		}

		int pedestrianNumberToGenerate = numberOfPedestrians - listOfPedestrians.size();

		try {
			synchronized (mapOfAgentsLists.get(CatpedsimPedestrian.class)) {
				for (int i = 0; i < pedestrianNumberToGenerate; i++) {
					location = generateRandomLocation();

					String newAgentName = generateUniqueName(CatpedsimPedestrian.class.getSimpleName(),
							initialisation.getPublisherId(),
							mapOfAgentsCounters.get(CatpedsimPedestrian.class).incrementAndGet());
					StringIdentifier uniqueIdentifier = new StringIdentifier(newAgentName);

					mapOfAgentsLists.get(CatpedsimPedestrian.class)
							.add(new CatpedsimPedestrian(publisherId, newAgentName, uniqueIdentifier,
									new Vec3f(location.x, location.y, location.z), maximumPedestrianSpeed));
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while initializing the random pedestrians.", ex);
		}
	}

	/**
	 * Initialise the autonomous vehicle. The access on the autonomous vehicle
	 * list is synchronised in order to avoid simultaneous operations on the
	 * same object.
	 * 
	 * @param publisherId
	 *            who is adding the autonomous vehicle
	 * @throws Exception
	 *             could arise during an illegal operation on the autonomous
	 *             vehicle list
	 */
	public final void initialiseRandomAutonomousVechicles(final String publisherId) {
		PVector location;
		float maximumPedestrianSpeed = Utils.random(pedestrianJoggingVelocity, pedestrianHighestVelocity);
		Class<?> agentType = CatpedsimAutonomousVehicle.class;
		int numberOfExpectedAutonomousVehicles = initialisation.getNumberOfAutonomousVehicles();

		try {
			synchronized (mapOfAgentsLists.get(agentType)) {
				List<CatpedsimAgent> listOfAgents = mapOfAgentsLists.get(agentType);
				listOfAgents.clear();

				for (int i = 0; i < numberOfExpectedAutonomousVehicles; i++) {
					location = generateRandomLocation();
					String newAgentName = generateUniqueName(agentType.getSimpleName(), initialisation.getPublisherId(),
							mapOfAgentsCounters.get(agentType).incrementAndGet());
					StringIdentifier uniqueIdentifier = new StringIdentifier(newAgentName);

					listOfAgents.add(new CatpedsimAutonomousVehicle(publisherId, newAgentName, uniqueIdentifier,
							new Vec3f(location.x, location.y, location.z), maximumPedestrianSpeed));
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while initializing the random autonomous vehicles.", ex);
		}
	}

	/**
	 * Initialise the bicycles. The access on the bicycle list is synchronised
	 * in order to avoid simultaneous operations on the same object.
	 * 
	 * @param publisherId
	 *            who is adding the bicycle
	 * @throws Exception
	 *             could arise during an illegal operation on the bicycle list
	 */
	public final void initialiseRandomBicycles(final String publisherId) {
		PVector location;
		float maximumPedestrianSpeed = Utils.random(pedestrianJoggingVelocity, pedestrianHighestVelocity);

		try {
			synchronized (mapOfAgentsLists.get(CatpedsimBicycle.class)) {
				for (int i = 0; i < initialisation.getNumberOfBicycles(); i++) {
					location = generateRandomLocation();
					String newAgentName = generateUniqueName(CatpedsimBicycle.class.getSimpleName(),
							initialisation.getPublisherId(),
							mapOfAgentsCounters.get(CatpedsimBicycle.class).incrementAndGet());
					StringIdentifier uniqueIdentifier = new StringIdentifier(newAgentName);

					mapOfAgentsLists.get(CatpedsimBicycle.class).add(new CatpedsimBicycle(publisherId, newAgentName,
							uniqueIdentifier, new Vec3f(location.x, location.y, location.z), maximumPedestrianSpeed));
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while initializing the random bicycles.", ex);
		}
	}

	/**
	 * Add a named pedestrian. The pedestrian velocity is a random number
	 * between walking velocity and jogging velocity.
	 * 
	 * @param publisherId
	 *            who is adding the pedestrian
	 * @param name
	 *            pedestrian name
	 * @throws Exception
	 *             on error
	 */
	public final void addPedestrian(final String publisherId, final String name) throws Exception {

		synchronized (mapOfAgentsLists.get(CatpedsimPedestrian.class)) {
			PVector location = generateRandomLocation();
			StringIdentifier uniqueIdentifier = new StringIdentifier(name);
			float maximumPedestrianSpeed = Utils.random(pedestrianJoggingVelocity, pedestrianHighestVelocity);

			mapOfAgentsLists.get(CatpedsimPedestrian.class).add(new CatpedsimPedestrian(publisherId, name,
					uniqueIdentifier, new Vec3f(location.x, location.y, location.z), maximumPedestrianSpeed));
		}
	}

	/**
	 * Add pedestrian from the external pedestrian model. The pedestrian
	 * velocity is a random number between walking velocity and jogging
	 * velocity.
	 * 
	 * If a pedestrian from the same publisher id and with the same name already
	 * exists, just update the pedestrian data.
	 * 
	 * @param publisherId
	 *            the id of the simulator that is adding the pedestrian
	 * @param pedestrian
	 *            pedestrian to add
	 * @throws Exception
	 *             on error
	 */
	public final void addPedestrianFromTheExternalPedestrianModel(final String publisherId, final Pedestrian pedestrian)
			throws Exception {
		synchronized (mapOfAgentsLists.get(CatpedsimPedestrian.class)) {
			CatpedsimPedestrian existingPedestrian = null;

			for (int i = 0; i < mapOfAgentsLists.get(CatpedsimPedestrian.class).size(); i++) {
				CatpedsimPedestrian internalPedestrian = (CatpedsimPedestrian) mapOfAgentsLists
						.get(CatpedsimPedestrian.class).get(i);

				if (internalPedestrian.getUniqueIdentifier().equals(pedestrian.getUniqueIdentifier())) {
					existingPedestrian = internalPedestrian;
					break;
				}
			}
			float maximumPedestrianSpeed = Utils.random(pedestrianJoggingVelocity, pedestrianHighestVelocity);

			if (existingPedestrian == null) {
				mapOfAgentsLists
						.get(CatpedsimPedestrian.class).add(
								new CatpedsimPedestrian(publisherId, pedestrian.getName(),
										(StringIdentifier) pedestrian.getUniqueIdentifier(),
										new Vec3f((float) pedestrian.getCurrentPosition().getX(),
												(float) pedestrian.getCurrentPosition().getY(), 0),
										maximumPedestrianSpeed));
			} else {
				existingPedestrian.setPositionVector(new PVector((float) pedestrian.getCurrentPosition().getX(),
						(float) pedestrian.getCurrentPosition().getY()));
				existingPedestrian.setCurrentSpeed((float) pedestrian.getCurrentSpeed());
				existingPedestrian.setCurrentHeading(pedestrian.getCurrentHeading());
			}
		}
	}

	/**
	 * Update pedestrian from the external model. Find id the pedestrian exists.
	 * If it doesn't, create it in the simulation space.
	 * 
	 * @param publisherId
	 *            the id of the simulator that is updating the pedestrian
	 * @param pedestrian
	 *            pedestrian to update
	 * @throws Exception
	 *             on error
	 */
	public final void addOrUpdatePedestrian(final String publisherId, final Pedestrian pedestrian) throws Exception {
		synchronized (mapOfAgentsLists.get(CatpedsimPedestrian.class)) {
			CatpedsimPedestrian existingPedestrian = null;

			for (int i = 0; i < mapOfAgentsLists.get(CatpedsimPedestrian.class).size(); i++) {
				CatpedsimPedestrian internalPedestrian = (CatpedsimPedestrian) mapOfAgentsLists
						.get(CatpedsimPedestrian.class).get(i);

				if (internalPedestrian.getUniqueIdentifier().equals(pedestrian.getUniqueIdentifier())) {
					existingPedestrian = internalPedestrian;
					break;
				}
			}
			float maximumPedestrianSpeed = Utils.random(pedestrianJoggingVelocity, pedestrianHighestVelocity);

			if (existingPedestrian == null) {
				mapOfAgentsLists
						.get(CatpedsimPedestrian.class).add(
								new CatpedsimPedestrian(publisherId, pedestrian.getName(),
										(StringIdentifier) pedestrian.getUniqueIdentifier(),
										new Vec3f((float) pedestrian.getCurrentPosition().getX(),
												(float) pedestrian.getCurrentPosition().getY(), 0),
										maximumPedestrianSpeed));
			} else {
				existingPedestrian.setPositionVector(new PVector((float) pedestrian.getCurrentPosition().getX(),
						(float) pedestrian.getCurrentPosition().getY()));
				existingPedestrian.setCurrentSpeed((float) pedestrian.getCurrentSpeed());
				existingPedestrian.setCurrentHeading(pedestrian.getCurrentHeading());
			}
		}
	}

	/**
	 * Add pedestrian in the current simulation.
	 *
	 * @param publisherId
	 *            who is adding the pedestrian
	 * @param name
	 *            name of the pedestrian
	 * @param position
	 *            position on the map
	 */
	public final void addPedestrian(final String publisherId, final String name, final Point2D position) {
		synchronized (mapOfAgentsLists.get(CatpedsimPedestrian.class)) {
			StringIdentifier uniqueIdentifier = new StringIdentifier(name);
			float maximumPedestrianSpeed = Utils.random(pedestrianJoggingVelocity, pedestrianHighestVelocity);

			mapOfAgentsLists.get(CatpedsimPedestrian.class)
					.add(new CatpedsimPedestrian(publisherId, name, uniqueIdentifier,
							new Vec3f((float) position.getX(), (float) position.getY(), 0), maximumPedestrianSpeed));
		}
	}

	/**
	 * Gets the world width in meters.
	 * 
	 * @return world width
	 */
	public final float getWorldWidth() {
		return worldWidth;
	}

	/**
	 * Gets the world depth in meters.
	 * 
	 * @return world depth
	 */
	public final float getWorldDepth() {
		return worldDepth;
	}

	/**
	 * Gets animation width in pixels.
	 * 
	 * @return animation pixels width
	 */
	public final int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * Gets the animation window depth in pixels.
	 * 
	 * @return window depth value
	 */
	public final int getWindowDepth() {
		return windowDepth;
	}

	/**
	 * Clean the animation purpose, the evacuation state or the target area.
	 */
	public final void clean() {
		desiredTarget = null;
		if (mapOfAgentsLists.get(CatpedsimPedestrian.class) != null) {
			mapOfAgentsLists.get(CatpedsimPedestrian.class).clear();
		}
		geometry.clearGeometryConfiguration();
	}

	/**
	 * Print information about all the pedestrians location.
	 * 
	 * @return information about all the pedestrians
	 */
	public final String printAllPedestrians() {
		StringBuilder pedestrianInformation = new StringBuilder();

		synchronized (mapOfAgentsLists.get(CatpedsimPedestrian.class)) {
			if (mapOfAgentsLists.get(CatpedsimPedestrian.class) == null) {
				return "There is no pedestrian in the current configuration";
			}

			for (int i = 0; i < mapOfAgentsLists.get(CatpedsimPedestrian.class).size(); i++) {
				pedestrianInformation.append(mapOfAgentsLists.get(CatpedsimPedestrian.class).get(i).toString() + "\n");
			}
		}

		return pedestrianInformation.toString();
	}

	/**
	 * Gets the geometry object containing information about wall and buildings.
	 * 
	 * @return geometry object
	 */
	public final CatpedsimGeometry getGeometry() {
		return geometry;
	}

	/**
	 * Gets the total number of walls.
	 * 
	 * @return number of walls
	 */
	public final int numberOfWalls() {
		int numberOfWalls = 0;
		try {
			numberOfWalls = geometry.getNumberOfWalls();
		} catch (Exception ex) {
			LOGGER.warn("Error while reading the number of walls.", ex);
		}
		return numberOfWalls;
	}

	/**
	 * Gets the total number of obstacles.
	 * 
	 * @return number of obstacles
	 */
	public final int getNumberOfObstacles() {
		int numberOfObstacles = 0;
		try {
			numberOfObstacles = geometry.getNumberOfObstacles();
		} catch (Exception ex) {
			LOGGER.warn("Error while reading the number of obstacles.", ex);
		}
		return numberOfObstacles;
	}

	/**
	 * Gets the wall from the list at the specified index.
	 * 
	 * @param index
	 *            index in the wall list
	 * @return wall object
	 */
	public final CatpedsimObstacle getWallFromListAtPosition(final int index) {
		CatpedsimObstacle wall = null;
		try {
			wall = geometry.getWallFromListAtIndex(index);
		} catch (Exception ex) {
			LOGGER.warn("Get wall from the list at the specified position.", ex);
		}
		return wall;
	}

	/**
	 * Gets the obstacle at the specified index.
	 * 
	 * @param index
	 *            index in the obstacle list
	 * @return obstacle object
	 */
	public final CatpedsimObstacle getObstacleFromListAtPosition(final int index) {
		CatpedsimObstacle obstacle = null;
		try {
			obstacle = geometry.getObstacleFromListAtIndex(index);
		} catch (Exception ex) {
			LOGGER.warn("Get obstacle from the list at the specified position.", ex);
		}
		return obstacle;
	}

	/**
	 * Gets the simulation start time in order to calculate duration.
	 * 
	 * @return simulation start time
	 */
	public final long getSimulationStartTime() {
		return simulationStartTime;
	}

	/**
	 * Sets the simulation start time. Called at the begining of the simulation.
	 * 
	 * @param newSimulationStartTime
	 *            Simulation start time
	 */
	public final void setSimulationStartTime(final long newSimulationStartTime) {
		this.simulationStartTime = newSimulationStartTime;
	}

	/**
	 * Get the simulation time unit.
	 * 
	 * @return simulation time unit
	 * @throws Exception
	 *             on error, initialisation == null
	 */
	public final SimulationTimeUnit getSimulationTimeUnit() throws Exception {
		return initialisation.getTickUnit();
	}

	/**
	 * gets the tick length in seconds.
	 * 
	 * @return tick length in seconds
	 */
	public final float getTickLengthSeconds() {
		return tickLengthSeconds;
	}

	/**
	 * Report to the bridge the advance time step was completed using
	 * LogicLayerEventsToBridge enum class.
	 */
	public final void notifyAdvanceTimeStepCompleted() {
		setChanged();
		notifyObservers(LogicLayerEventsToBridge.ADVANCE_TIME_STEP_COMPLETED);
	}

	/**
	 * Generate an unique for every pedestrian identifier. It consists of
	 * publisher id, current time stamp in nanoseconds and an index.
	 * 
	 * @param agentClass
	 *            agent class
	 * @param publisherId
	 *            publisher id
	 * @param agentCounter
	 *            agent counter
	 * @return unique string identifier
	 */
	public static final synchronized String generateUniqueName(final String agentClass, final String publisherId,
			final long agentCounter) {
		return agentClass + agentCounter + RegexPatterns.FULL_STOP + publisherId + RegexPatterns.FULL_STOP
				+ System.nanoTime();
	}

	/**
	 * Get the number of crossings.
	 * 
	 * @return the number of crossings
	 */
	public final int numberOfCrossings() {
		int numberOfCrossings = 0;
		try {
			numberOfCrossings = geometry.getNumberOfCrossings();
		} catch (Exception ex) {
			LOGGER.warn("Error while reading the number of crossings.", ex);
		}
		return numberOfCrossings;
	}

	/**
	 * Get crossing from the list at the specified index.
	 * 
	 * @param indexInCrossingList
	 *            index in the list of corssings/ entrances
	 * @return crossing
	 */
	public final CatpedsimCrossing getCrossingFromListAtPosition(final int indexInCrossingList) {
		CatpedsimCrossing crossing = null;
		try {
			crossing = geometry.getCrossingFromListAtIndex(indexInCrossingList);
		} catch (Exception ex) {
			LOGGER.warn("Error while getting a wall from the list at the specified position.", ex);
		}
		return crossing;
	}

	/**
	 * Increment tick count.
	 */
	public final void incrementTickCount() {
		numberOfTicks.incrementTickCount();
	}

	/**
	 * Get tick information as string.
	 * 
	 * @return tick counter information
	 */
	public final String getTickCounterInformation() {
		return numberOfTicks.getInformation();
	}

	/**
	 * Get tick number as long.
	 * 
	 * @return number of ticks
	 */
	public final long getNumberOfTicksAsLong() {
		return numberOfTicks.getNumberOfTicksAsLong();
	}

	/**
	 * Get the map of agents lists.
	 * 
	 * @return the map of agents lists
	 */
	public final Map<Class<?>, List<CatpedsimAgent>> getMapOfAgentsLists() {
		return mapOfAgentsLists;
	}

	/**
	 * A star shortest path finder.
	 * 
	 * @param newAStarPathFinder
	 *            the a* path finder
	 */
	public final void setGridFortTheShortestPath(final AStarPathFinder newAStarPathFinder) {
		aStarPathFinder = newAStarPathFinder;
	}

	/**
	 * Get transition zones from teh geometry.
	 * 
	 * @return transition zones
	 */
	public final Map<TransitionZoneTypes, Map<String, CatpedsimTransitionZone>> getTransitionZones() {
		try {
			return geometry.getTransitionZones();
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Get a random exit from the geometry of the simulator.
	 * 
	 * @return a random exit
	 */
	public final CatpedsimCrossing getRandomExit() {
		List<CatpedsimCrossing> crossings = geometry.getCrossings();
		return crossings.get(new Random().nextInt(crossings.size()));
	}

	/**
	 * Iterate through all the agents that transport other agents and update the
	 * positions of the transported ones.
	 */
	public final void updateTransportedAgentsPositions() {
		for (Entry<Class<?>, List<CatpedsimAgent>> mapOfAgentsIterator : mapOfAgentsLists.entrySet()) {
			List<CatpedsimAgent> listOfAgents = mapOfAgentsIterator.getValue();

			for (Iterator<CatpedsimAgent> iterator = listOfAgents.iterator(); iterator.hasNext();) {
				CatpedsimAgent currentAgent = iterator.next();
				PVector positionVector = currentAgent.getPositionVector();

				if (currentAgent.isTransporting()) {
					List<StringIdentifier> transportedAgents = currentAgent.getTransportsAgents();

					for (StringIdentifier agentId : transportedAgents) {
						updateTransportedAgentPosition(agentId, positionVector);
					}
				}
			}
		}
	}

	/**
	 * Updated agent position when identified by unique id.
	 * 
	 * @param agentId
	 *            agent unique identifier
	 * @param positionVector
	 *            agent position vector
	 */
	private void updateTransportedAgentPosition(final StringIdentifier agentId, final PVector positionVector) {
		for (Entry<Class<?>, List<CatpedsimAgent>> mapOfAgentsIterator : mapOfAgentsLists.entrySet()) {
			List<CatpedsimAgent> listOfAgents = mapOfAgentsIterator.getValue();

			for (Iterator<CatpedsimAgent> iterator = listOfAgents.iterator(); iterator.hasNext();) {
				CatpedsimAgent currentAgent = iterator.next();
				StringIdentifier currentAgentId = currentAgent.getUniqueIdentifier();

				if (currentAgentId.equals(agentId) && currentAgent.isTransported()) {
					currentAgent.copyPositionVector(positionVector);
					return;
				}
			}
		}
	}

	/**
	 * Update external vehicle. If the vehicle already exists in the lsit of
	 * vehicles update the position, if the vehicle doesn't exist create a new
	 * one based on external vehicle parameters.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param vehicle
	 *            vehicle object
	 * @throws Exception
	 *             on error
	 */
	public final void updateOrAddExternalVehicle(final String publisherId, final Vehicle vehicle) throws Exception {
		Class<?> agentClass = CatpedsimVehicle.class;
		List<CatpedsimAgent> listOfAgents = mapOfAgentsLists.get(agentClass);
		if (listOfAgents == null) {
			return;
		}

		PVector externalVehicleLocation = new PVector((float) vehicle.getCurrentPosition().getX(),
				(float) vehicle.getCurrentPosition().getY());

		synchronized (listOfAgents) {
			Iterator<CatpedsimAgent> iterator = mapOfAgentsLists.get(agentClass).iterator();

			while (iterator.hasNext()) {
				CatpedsimVehicle internalVehicle = (CatpedsimVehicle) iterator.next();

				if (internalVehicle.getUniqueIdentifier().equals(vehicle.getUniqueIdentifier())) {
					internalVehicle.setPositionVector(externalVehicleLocation);
					internalVehicle.setCurrentSpeed((float) vehicle.getCurrentSpeed());
					return;
				}
			}

			final float metersPerSecondsMaxManufacturerSpeed = 27.77f;
			StringIdentifier uniqueIdentifier = new StringIdentifier(vehicle.getVehicleName());
			float maximumSpeed = convertMetersPerSpeedToTickVelocity(metersPerSecondsMaxManufacturerSpeed);

			CatpedsimVehicle newInternalVechicle = new CatpedsimVehicle(publisherId, vehicle.getVehicleName(),
					uniqueIdentifier,
					new Vec3f(externalVehicleLocation.x, externalVehicleLocation.y, externalVehicleLocation.z),
					maximumSpeed);
			listOfAgents.add(newInternalVechicle);
		}
	}

	/**
	 * Add a generic vehicle to the list.
	 * 
	 * @param publisherId
	 *            publisher id
	 * @param vehicle
	 *            vehicle object
	 * @throws Exception
	 *             on update vehicle error
	 */
	@SuppressWarnings("unused")
	private void addCatpedsimVehicle(final String publisherId, final Vehicle vehicle) throws Exception {
		Class<?> agentClass = CatpedsimVehicle.class;
		final float metersPerSecondsMaxManufaturerSpeed = 27.77f;

		synchronized (mapOfAgentsLists.get(agentClass)) {
			PVector location = generateRandomLocation();
			StringIdentifier uniqueIdentifier = new StringIdentifier(vehicle.getVehicleName());
			float maximumSpeed = convertMetersPerSpeedToTickVelocity(metersPerSecondsMaxManufaturerSpeed);

			mapOfAgentsLists.get(agentClass).add(new CatpedsimVehicle(publisherId, vehicle.getVehicleName(),
					uniqueIdentifier, new Vec3f(location.x, location.y, location.z), maximumSpeed));
		}
	}

	/**
	 * Convert tick speed.
	 * 
	 * @param speed
	 *            vehicle speed in meters/second.
	 * @return velocity per tick
	 */
	private float convertMetersPerSpeedToTickVelocity(final float speed) {
		return tickLengthSeconds * PedestrianConstraints.PEDESTRIAN_MARATHON_SPEED;
	}

	/**
	 * Get agent by name.
	 * 
	 * @param identifier
	 *            agent unique identifier
	 * @return agent object
	 */
	public final CatpedsimAgent getAgentByName(final UniqueIdentifier identifier) {
		for (Map.Entry<Class<?>, List<CatpedsimAgent>> entry : mapOfAgentsLists.entrySet()) {

			Class<?> agentClass = entry.getKey();
			List<CatpedsimAgent> listOfAgents = mapOfAgentsLists.get(agentClass);

			for (Iterator<CatpedsimAgent> iterator = listOfAgents.iterator(); iterator.hasNext();) {
				CatpedsimAgent agent = iterator.next();

				if (agent.getUniqueIdentifier().equals(identifier)) {
					return agent;
				}
			}
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
		if (uniqueIdentifier == null) {
			return;
		}

		for (Entry<Class<?>, List<CatpedsimAgent>> mapOfAgentsIterator : mapOfAgentsLists.entrySet()) {
			List<CatpedsimAgent> listOfAgents = mapOfAgentsIterator.getValue();

			synchronized (listOfAgents) {
				for (Iterator<CatpedsimAgent> iterator = listOfAgents.iterator(); iterator.hasNext();) {
					CatpedsimAgent currentAgent = iterator.next();

					if (currentAgent.getUniqueIdentifier().equals(uniqueIdentifier)) {
						iterator.remove();
					}
				}
			}
		}
	}

}