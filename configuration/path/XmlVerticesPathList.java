package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.path;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Vertex from the path.
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verticesPathList")
public class XmlVerticesPathList {

	/** List of points to follow. */
	@XmlElement(type = XmlVertex.class, name = "vertex")
	private final LinkedList<XmlVertex> vertices = new LinkedList<XmlVertex>();

	/**
	 * Add vertex.
	 * 
	 * @param vertex
	 *            vertex to add in the list
	 */
	public final void add(final XmlVertex vertex) {
		vertices.add(vertex);
	}

	/**
	 * Get path size.
	 * 
	 * @return path size
	 */
	public final int size() {
		return vertices.size();
	}

	/**
	 * Get the first point in the pre-computed path.
	 * 
	 * @return the first point in the pre-computed path
	 */
	public final XmlVertex getFirst() {
		if (vertices.isEmpty()) {
			return null;
		} else {
			return vertices.getFirst();
		}
	}

	/**
	 * Remove the first element in the pre-computed path. Once an agent achives
	 * a target , it has to be removed.
	 * 
	 * @return the number of intermediate targets left in the agent's list
	 */
	public final int removeFirst() {
		if (vertices.size() >= 2) {
			vertices.removeFirst();
		} else {
			vertices.clear();
		}
		return vertices.size();
	}

	/**
	 * Add a new path.
	 * 
	 * @param newPathList
	 *            new path list
	 */
	public final void addAll(final XmlVerticesPathList newPathList) {
		vertices.clear();
		vertices.addAll(newPathList.vertices);
	}

	/**
	 * Clear the xml path object.
	 */
	public final void clear() {
		vertices.clear();
	}

	/**
	 * Get the last element in the path.
	 * 
	 * @return get Last path
	 */
	public final XmlVertex getLast() {
		return vertices.getLast();
	}
}
