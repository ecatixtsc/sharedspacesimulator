package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.geometry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import processing.core.PVector;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * The simulator's geometry consists of a list of walls (open polygons), a list
 * of obstacles (closed polygons) and a list of crossings(vectors to the end
 * points of a segment).
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
public class CatpedsimGeometry {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = UtilityLogger.getLogger(CatpedsimGeometry.class);

	/**
	 * The list of walls: open polygons.
	 */
	private final List<CatpedsimObstacle> walls = new LinkedList<CatpedsimObstacle>();

	/**
	 * The list of obstacles: closed polygons.
	 */
	private final List<CatpedsimObstacle> obstacles = new LinkedList<CatpedsimObstacle>();

	/**
	 * The list of doors or entrances.
	 */
	private final List<CatpedsimCrossing> crossings = new LinkedList<CatpedsimCrossing>();

	/**
	 * Map of transit zones indexed by type of zone.
	 */
	private final Map<TransitionZoneTypes, Map<String, CatpedsimTransitionZone>> transitionZones = new HashMap<>();

	/** Mark the area were all the walls/obstacles can be found. */
	private Bounds geometryBounds;

	/**
	 * The total number of walls (represented as open polygons).
	 * 
	 * @return number of walls
	 */
	public final int getNumberOfWalls() {
		return walls.size();
	}

	/**
	 * The total number of obstacles(represented closed polygons).
	 * 
	 * @return number of walls
	 */
	public final int getNumberOfObstacles() {
		return obstacles.size();
	}

	/**
	 * Get number of crossings in the simulation.
	 * 
	 * @return number of crossings
	 */
	public final int getNumberOfCrossings() {
		return crossings.size();
	}

	/**
	 * Get a list of entrances or crossings.
	 * 
	 * @return the list of entrances
	 */
	public final List<CatpedsimCrossing> getCrossings() {
		return crossings;
	}

	/**
	 * Add new wall.
	 * 
	 * @param wall
	 *            wall object
	 */
	public final void addWall(final CatpedsimObstacle wall) {
		synchronized (walls) {
			walls.add(wall);
		}
	}

	/**
	 * Add new obstacle.
	 * 
	 * @param obstacle
	 *            obstacle object
	 */
	public final void addObstacle(final CatpedsimObstacle obstacle) {
		synchronized (obstacles) {
			obstacles.add(obstacle);
		}
	}

	/**
	 * Add a new entrance.
	 * 
	 * @param newCrossing
	 *            the vectors pointing to the end points of the entrance segment
	 */
	public final void addCrossing(final CatpedsimCrossing newCrossing) {
		synchronized (crossings) {
			crossings.add(newCrossing);
		}
	}

	/**
	 * Get wall at requested index from the list of walls.
	 * 
	 * @param index
	 *            the position of the wall in the list
	 * @return wall object
	 */
	public final CatpedsimObstacle getWallFromListAtIndex(final int index) {
		CatpedsimObstacle wall = null;
		if (walls == null) {
			return wall;
		}

		synchronized (walls) {
			if (index < walls.size() && index >= 0) {
				wall = walls.get(index);
			}
		}

		return wall;
	}

	/**
	 * Get obstacle at requested index from the list of obstacles.
	 * 
	 * @param index
	 *            index of obstacle in the list
	 * @return obstacle object
	 */
	public final CatpedsimObstacle getObstacleFromListAtIndex(final int index) {
		CatpedsimObstacle wall = null;
		if (obstacles == null) {
			return wall;
		}

		synchronized (obstacles) {
			if (index < obstacles.size() && index >= 0) {
				wall = obstacles.get(index);
			}
		}

		return wall;
	}

	/**
	 * 
	 */
	public final void computeCentroidsSegmentLengthsAndBoundaries() {
		computeWallsCentroidsAndSegmentLengths();
		computeObstaclesCentroidsAndSegmentLengths();

		float geometryXMin = Float.MAX_VALUE;
		float geometryYMin = Float.MAX_VALUE;
		float geometryXMax = Float.MIN_VALUE;
		float geometryYMax = Float.MIN_VALUE;

		for (CatpedsimObstacle wall : walls) {
			ShapeSection[] sections = wall.getObstacleSections();

			for (int indexSection = 0; indexSection < sections.length; indexSection++) {
				geometryXMin = Math.min(geometryXMin, sections[indexSection].getVectorToStartPoint().x);
				geometryXMin = Math.min(geometryXMin, sections[indexSection].getVectorToEndPoint().x);

				geometryYMin = Math.min(geometryYMin, sections[indexSection].getVectorToStartPoint().y);
				geometryYMin = Math.min(geometryYMin, sections[indexSection].getVectorToEndPoint().y);

				geometryXMax = Math.max(geometryXMax, sections[indexSection].getVectorToStartPoint().x);
				geometryXMax = Math.max(geometryXMax, sections[indexSection].getVectorToEndPoint().x);

				geometryYMax = Math.max(geometryYMax, sections[indexSection].getVectorToStartPoint().y);
				geometryYMax = Math.max(geometryYMax, sections[indexSection].getVectorToEndPoint().y);
			}
		}

		for (CatpedsimObstacle obstacle : obstacles) {
			ShapeSection[] sections = obstacle.getObstacleSections();

			for (int indexSection = 0; indexSection < sections.length; indexSection++) {
				geometryXMin = Math.min(geometryXMin, sections[indexSection].getVectorToStartPoint().x);
				geometryXMin = Math.min(geometryXMin, sections[indexSection].getVectorToEndPoint().x);

				geometryYMin = Math.min(geometryYMin, sections[indexSection].getVectorToStartPoint().y);
				geometryYMin = Math.min(geometryYMin, sections[indexSection].getVectorToEndPoint().y);

				geometryXMax = Math.max(geometryXMax, sections[indexSection].getVectorToStartPoint().x);
				geometryXMax = Math.max(geometryXMax, sections[indexSection].getVectorToEndPoint().x);

				geometryYMax = Math.max(geometryYMax, sections[indexSection].getVectorToStartPoint().y);
				geometryYMax = Math.max(geometryYMax, sections[indexSection].getVectorToEndPoint().y);
			}
		}

		geometryBounds = new BoundingBox(geometryXMin, geometryYMin, geometryXMax - geometryXMin,
				geometryYMax - geometryYMin);
	}

	/**
	 * Get obstacles/walls bounding box.
	 * 
	 * @return obstacles/walls bounding box
	 */
	public final Bounds getGeometryBounds() {
		return geometryBounds;
	}

	/**
	 * Calculate the centroid (centre of gravity) for all the walls (open
	 * polygons) and the length of its constituent segment.
	 */
	public final void computeWallsCentroidsAndSegmentLengths() {
		if (walls == null) {
			return;
		}

		synchronized (walls) {
			for (CatpedsimObstacle wall : walls) {
				try {
					wall.buildSectionObjectsAndComputeCentroid();
				} catch (Exception e) {
					LOGGER.warn("Error occured while computing the centroid.", e);
				}
			}
		}
	}

	/**
	 * Calculate the centroid (centre of gravity) for all the obstacles (closed
	 * polygons).
	 */
	public final void computeObstaclesCentroidsAndSegmentLengths() {
		if (obstacles == null) {
			return;
		}

		synchronized (obstacles) {
			for (CatpedsimObstacle obstacle : obstacles) {
				try {
					obstacle.buildSectionObjectsAndComputeCentroid();
				} catch (Exception e) {
					LOGGER.warn("Error occured while computing the centroid and segments lengths.", e);
				}
			}
		}
	}

	/**
	 * Get the list with all the walls.
	 * 
	 * @return the list of walls
	 */
	public final List<CatpedsimObstacle> getWalls() {
		return walls;
	}

	/**
	 * Get the list with all the obstacles.
	 * 
	 * @return the list of obstacles
	 */
	public final List<CatpedsimObstacle> getObstacles() {
		return obstacles;
	}

	/**
	 * Clear geometry configuration.
	 */
	public final void clearGeometryConfiguration() {
		if (walls != null) {
			synchronized (walls) {
				walls.clear();
			}
		}
		if (obstacles != null) {
			synchronized (obstacles) {
				obstacles.clear();
			}
		}
	}

	/**
	 * Get crossing from the list at the specified index.
	 * 
	 * @param indexInCrossingList
	 *            index in the list
	 * @return crossing
	 */
	public final CatpedsimCrossing getCrossingFromListAtIndex(final int indexInCrossingList) {
		CatpedsimCrossing crossing = null;
		if (crossings == null) {
			return crossing;
		}

		synchronized (crossings) {
			if (indexInCrossingList < crossings.size() && indexInCrossingList >= 0) {
				crossing = crossings.get(indexInCrossingList);
			}
		}

		return crossing;
	}

	/**
	 * Add transition zones. If the transition zone already exists, update with
	 * new vertices, if it doesn't exist create a new entry.
	 * 
	 * @param transitionZoneType
	 *            trasition zone type
	 * @param zoneName
	 *            zone name or id
	 * @param zoneSectionId
	 *            section of the transition zone
	 */

	/**
	 * Add transition zones. If the transition zone already exists, update with
	 * new vertices, if it doesn't exist create a new entry.
	 * 
	 * @param transitionZoneType
	 *            trasition zone type
	 * @param zoneName
	 *            zone name or id
	 * @param zoneSectionId
	 *            section of the transition zone
	 * @param vertex1
	 *            vector to start section
	 * @param vertex2
	 *            vector to end section
	 */
	public final void addTransitionZone(final TransitionZoneTypes transitionZoneType, final String zoneName,
			final String zoneSectionId, final PVector vertex1, final PVector vertex2) {

		Map<String, CatpedsimTransitionZone> mapOfZonesIndexedByName = null;
		CatpedsimTransitionZone zone = null;

		if (transitionZones.containsKey(transitionZoneType)) {
			mapOfZonesIndexedByName = transitionZones.get(transitionZoneType);

			if (mapOfZonesIndexedByName.containsKey(zoneName)) {
				zone = mapOfZonesIndexedByName.get(zoneName);

				if (!Objects.isNull(zone)) {
					zone.addSection(vertex1, vertex2);
				}
			} else {
				zone = new CatpedsimTransitionZone(transitionZoneType, zoneName);
				zone.addSection(vertex1, vertex2);
				mapOfZonesIndexedByName.put(zoneName, zone);
			}
		} else {
			mapOfZonesIndexedByName = new HashMap<String, CatpedsimTransitionZone>();
			zone = new CatpedsimTransitionZone(transitionZoneType, zoneName);
			zone.addSection(vertex1, vertex2);

			transitionZones.put(transitionZoneType, mapOfZonesIndexedByName);
			mapOfZonesIndexedByName.put(zoneName, zone);
		}
	}

	/**
	 * Get transition zones.
	 * 
	 * @return transition zones map
	 */
	public final Map<TransitionZoneTypes, Map<String, CatpedsimTransitionZone>> getTransitionZones() {
		return transitionZones;
	}

	/**
	 * Display information about the transition zones.
	 * 
	 * @return transition zones as text
	 */
	public final String getTransitionZonesToString() {
		StringBuilder transitionsToString = new StringBuilder("Transition zones:");

		for (Map.Entry<TransitionZoneTypes, Map<String, CatpedsimTransitionZone>> mapEntry : transitionZones
				.entrySet()) {
			TransitionZoneTypes transitionType = mapEntry.getKey();
			Map<String, CatpedsimTransitionZone> zoneMap = mapEntry.getValue();

			transitionsToString.append("\n" + transitionType.getValue() + ":\n");

			for (Map.Entry<String, CatpedsimTransitionZone> zones : zoneMap.entrySet()) {
				transitionsToString.append("Zone name: " + zones.getKey() + "\n");
				transitionsToString.append(((CatpedsimTransitionZone) zones.getValue()).toString());
			}
			transitionsToString.append("\n");
		}

		return transitionsToString.toString();
	}
}