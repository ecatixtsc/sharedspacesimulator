package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.shortestpath;

import java.util.ArrayList;

/**
 * A* algorithm for the shortest path.
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
public class NodePath {
	/** Coordinates on the X, Y axis. */
	private float x, y;

	/** g(n) is the cost of the path from the start node to n. */
	private float g;

	/**
	 * h(n) is a heuristic that estimates the cost of the cheapest path from n
	 * to the goal.
	 */
	private float h;

	/** Node position. */
	private int p;

	/** Array of node objects. */
	private ArrayList<NodePath> neighbours;

	/** Cost multiplier for each corresponding node. */
	private ArrayList<Float> nCost;

	/**
	 * Constructor.
	 * 
	 * @param newX
	 *            new coordinate on the X axis
	 * @param newY
	 *            new coordinate on the Y axis
	 */
	public NodePath(final float newX, final float newY) {
		x = newX;
		y = newY;
		g = 0;
		h = 0;
		setP(-1);
		setNeighbours(new ArrayList<NodePath>());
		setnCost(new ArrayList<Float>());
	}

	/**
	 * Add neighbour in the list of nodes..
	 * 
	 * @param newNode
	 *            new neighbour to add in the node list
	 * @param cost
	 *            node cost
	 */
	final void addNeighbour(final NodePath newNode, final float cost) {
		getNeighbours().add(newNode);
		getnCost().add(new Float(cost));
	}

	/**
	 * Get X coordinate.
	 * 
	 * @return X coordinate
	 */
	public final float getX() {
		return x;
	}

	/**
	 * Set X coordinate.
	 * 
	 * @param newXCoordinate
	 *            X coordinate
	 */
	public final void setX(final float newXCoordinate) {
		this.x = newXCoordinate;
	}

	/**
	 * Get Y coordinate.
	 * 
	 * @return y coordinate
	 */
	public final float getY() {
		return y;
	}

	/**
	 * Set Y coordinate.
	 * 
	 * @param newYCoordinate
	 *            new X coordinate
	 */
	public final void setY(final float newYCoordinate) {
		this.y = newYCoordinate;
	}

	/**
	 * Get g coeffcient.
	 * 
	 * @return g coeffcient
	 */
	public final float getG() {
		return g;
	}

	/**
	 * Set g coefficient.
	 * 
	 * @param newGCoefficient
	 *            new g coefficient
	 */
	public final void setG(final float newGCoefficient) {
		this.g = newGCoefficient;
	}

	/**
	 * Get h coefficient.
	 * 
	 * @return h coefficient
	 */
	public final float getH() {
		return h;
	}

	/**
	 * Set h coefficient.
	 * 
	 * @param newHCoefficient
	 *            new h coefficient
	 */
	public final void setH(final float newHCoefficient) {
		this.h = newHCoefficient;
	}

	/**
	 * Get node position.
	 * 
	 * @return node position
	 */
	public final int getP() {
		return p;
	}

	/**
	 * Set node position.
	 * 
	 * @param newPosition
	 *            new node positon
	 */
	public final void setP(final int newPosition) {
		this.p = newPosition;
	}

	/**
	 * Get the list of neighbours.
	 * 
	 * @return the list of neighbours
	 */
	public final ArrayList<NodePath> getNeighbours() {
		return neighbours;
	}

	/**
	 * Set a new list of neighbours.
	 * 
	 * @param newNeighbourList
	 *            new list of neighbours
	 */
	public final void setNeighbours(final ArrayList<NodePath> newNeighbourList) {
		this.neighbours = newNeighbourList;
	}

	/**
	 * Get cost.
	 * 
	 * @return cost
	 */
	public final ArrayList<Float> getnCost() {
		return nCost;
	}

	/**
	 * Set new cost.
	 * 
	 * @param newCost
	 *            new cost
	 */
	public final void setnCost(final ArrayList<Float> newCost) {
		this.nCost = newCost;
	}
}
