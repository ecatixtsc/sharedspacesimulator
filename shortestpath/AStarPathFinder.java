package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.shortestpath;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import processing.core.PVector;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.Trigonometry;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * A* algorithm for finding the shortest path.
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

public class AStarPathFinder {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = UtilityLogger.getLogger(AStarPathFinder.class);

	/**
	 * Network grid represents an obstacle as -1 and calculates a cost
	 * dependendat on the position on the grid.
	 * 
	 * Example: nodeMap[0][0] has cost 0, nodeMap[1][0] has cost 1,
	 * nodeMap[2][0] has cost 2, if there is an obstacle nodeMap[3][0] has cost
	 * -1
	 */
	private int[][] nodeMap = null;

	/** Grid resolution. */
	private int scale = 1;

	/**
	 * The set of currently discovered nodes still to be evaluated. Initially,
	 * only the start node is known.
	 */
	private LinkedList<NodePath> openSet = new LinkedList<NodePath>();

	/** The set of nodes already evaluated. */
	private LinkedList<NodePath> closedSet = new LinkedList<NodePath>();

	/**
	 * A list of precomputed nodes. For each node, it indicates which node it
	 * can most efficiently be reached from.
	 */
	private LinkedList<NodePath> nodes = new LinkedList<NodePath>();

	/** The path of nodes from start node to end node. */
	private LinkedList<NodePath> path = new LinkedList<NodePath>();

	/**
	 * Constructor. Init the grid of obstacles and accessible space.
	 * 
	 * @param map
	 *            a map of obstacle and accessible areas
	 * @param newScale
	 *            grid resolution
	 */
	public AStarPathFinder(final int[][] map, final int newScale) {
		nodeMap = new int[map.length][map[0].length];
		scale = newScale;

		NodePath newNode = null;

		for (int ix = 0; ix < map[0].length; ix += 1) {
			for (int iy = 0; iy < map.length; iy += 1) {
				nodeMap[iy][ix] = -1;

				if (map[iy][ix] == 1) {
					nodes.add(new NodePath(ix * scale, iy * scale));
					nodeMap[iy][ix] = nodes.size() - 1;

					try {
						if (ix > 0) {
							if (nodeMap[iy][ix - 1] != -1) {
								newNode = (NodePath) nodes.getLast();
								float cost = 1;
								newNode.addNeighbour((NodePath) nodes.get(nodeMap[iy][ix - 1]), cost);
								((NodePath) nodes.get(nodeMap[iy][ix - 1])).addNeighbour(newNode, cost);
							}
						}
						if (iy > 0) {
							if (nodeMap[iy - 1][ix] != -1) {
								newNode = (NodePath) nodes.getLast();
								float cost = 1;
								newNode.addNeighbour((NodePath) nodes.get(nodeMap[iy - 1][ix]), cost);
								((NodePath) nodes.get(nodeMap[iy - 1][ix])).addNeighbour(newNode, cost);
							}
						}
					} catch (Exception ex) {
						LOGGER.warn("Error while building the AStar grid map.", ex);
					}
				}
			}
		}
	}

	/**
	 * Get the scale between the real world and the grid.
	 * 
	 * @return scale
	 */
	public final int getScale() {
		return scale;
	}

	/**
	 * A* Pathfinding Algorithm Finds short path from node[iStart] to
	 * node[iEnd].
	 * 
	 * @param x1
	 *            point 1 coordinate on the x axis
	 * @param y1
	 *            point 1 coordinate on the y axis
	 * @param x2
	 *            point 2 coordinate on the x axis
	 * @param y2
	 *            point 2 coordinate on the y axis
	 * @return true if a path was found
	 */
	public final boolean astar(final int x1, final int y1, final int x2, final int y2) {
		try {
			openSet.clear();
			closedSet.clear();
			path.clear();

			int scaleXCoordinate1 = (int) (x1 / scale);
			int scaledYCoordinate1 = (int) (y1 / scale);
			int scaledXCoordinate2 = (int) (x2 / scale);
			int scaledYCoordinate2 = (int) (y2 / scale);

			int iStart = nodeMap[scaledYCoordinate1][scaleXCoordinate1];
			int iEnd = nodeMap[scaledYCoordinate2][scaledXCoordinate2];

			if (iStart == -1 || iEnd == -1) {
				return false;
			}

			NodePath endNode = nodes.get(iEnd);
			float endX = ((NodePath) endNode).getX();
			float endY = ((NodePath) endNode).getY();

			openSet.add(((NodePath) nodes.get(iStart)));
			NodePath initialNode = openSet.get(0);
			((NodePath) initialNode).setP(-1);
			((NodePath) initialNode).setG(0);
			((NodePath) initialNode).setH(Trigonometry.distanceBetweenPoints(((NodePath) openSet.get(0)).getX(),
					((NodePath) openSet.get(0)).getY(), endX, endY));

			NodePath currentNode;
			float tentativeGScore;
			boolean tentativeIsBetter;
			float lowest = Float.MAX_VALUE;
			int lowId = -1;

			while (openSet.size() > 0) {
				lowest = Float.MAX_VALUE;
				for (int a = 0; a < openSet.size(); a++) {
					NodePath openSetCandidate = (NodePath) openSet.get(a);

					if ((openSetCandidate.getG() + (openSet.get(a)).getH()) <= lowest) {
						lowest = (openSetCandidate.getG() + openSetCandidate.getH());
						lowId = a;
					}
				}
				currentNode = (NodePath) openSet.get(lowId);

				if ((currentNode.getX() == endX) && (currentNode.getY() == endY)) {
					NodePath d = (NodePath) openSet.get(lowId);
					while (d.getP() != -1) {
						path.add(d);
						d = (NodePath) nodes.get(d.getP());
					}
					return true;
				}
				closedSet.add((NodePath) openSet.get(lowId));
				openSet.remove(lowId);
				for (int n = 0; n < currentNode.getNeighbours().size(); n++) {
					if (closedSet.contains((NodePath) currentNode.getNeighbours().get(n))) {
						continue;
					}
					tentativeGScore = currentNode.getG() + Trigonometry.distanceBetweenPoints(currentNode.getX(),
							currentNode.getY(), ((NodePath) currentNode.getNeighbours().get(n)).getX(),
							((NodePath) currentNode.getNeighbours().get(n)).getY())
							* ((Float) currentNode.getnCost().get(n));
					if (!openSet.contains((NodePath) currentNode.getNeighbours().get(n))) {
						openSet.add((NodePath) currentNode.getNeighbours().get(n));
						tentativeIsBetter = true;
					} else if (tentativeGScore < ((NodePath) currentNode.getNeighbours().get(n)).getG()) {
						tentativeIsBetter = true;
					} else {
						tentativeIsBetter = false;
					}

					if (tentativeIsBetter) {
						((NodePath) currentNode.getNeighbours().get(n))
								.setP(nodes.indexOf((NodePath) closedSet.get(closedSet.size() - 1)));
						((NodePath) currentNode.getNeighbours().get(n)).setG(tentativeGScore);
						((NodePath) currentNode.getNeighbours().get(n)).setH(Trigonometry.distanceBetweenPoints(
								((NodePath) currentNode.getNeighbours().get(n)).getX(),
								((NodePath) currentNode.getNeighbours().get(n)).getY(), endX, endY));
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while creating the AStar path.", ex);
		}

		return false;
	}

	/**
	 * Get the list of pre-coputed path.
	 * 
	 * @return path to follow
	 */
	public final LinkedList<NodePath> getPath() {
		return path;
	}

	/**
	 * Calculated the shortest path using the A* algorithm.
	 * 
	 * @param currentPathFinder
	 *            current path finder object holding the grid
	 * @param start
	 *            vector to start position
	 * @param end
	 *            vector to end position
	 * @return the path as a list of nodes
	 */
	public static LinkedList<NodePath> calculateShortestPath(final AStarPathFinder currentPathFinder,
			final PVector start, final PVector end) {
		LinkedList<NodePath> computedPath = null;

		if (currentPathFinder != null) {
			synchronized (currentPathFinder) {
				if (currentPathFinder.astar((int) start.x, (int) start.y, (int) end.x, (int) end.y)) {
					computedPath = new LinkedList<NodePath>();
					computedPath.addAll(currentPathFinder.getPath());
					currentPathFinder.clear();
				}
			}
		}

		return computedPath;
	}

	/**
	 * Clear path details.
	 */
	private void clear() {
		openSet.clear();
		closedSet.clear();
		path.clear();
	}
}
