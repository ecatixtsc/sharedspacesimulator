package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.logiclayer;

import java.util.Comparator;

import processing.core.PVector;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimPedestrian;

/**
 * Compare two pedestrians in order to establish who is the closest to the
 * target.
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
public class PedestrianToTargetComparator implements Comparator<Object> {
	/**
	 * The target location for all the pedestrians.
	 */
	private PVector target = null;

	/**
	 * Set the target for comparison.
	 * 
	 * @param t
	 *            target location
	 */
	public PedestrianToTargetComparator(final PVector t) {
		this.target = t;
	}

	/**
	 * Compare two pedestrians in order to establish which one is closest to the
	 * target.
	 * 
	 * @param object1
	 *            the first object to be compared
	 * @param object2
	 *            the second object to be compared
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second
	 */
	@Override
	public final int compare(final Object object1, final Object object2) {

		CatpedsimPedestrian pedestrian1 = (CatpedsimPedestrian) object1;
		CatpedsimPedestrian pedestrian2 = (CatpedsimPedestrian) object2;

		float distance1 = PVector.dist(pedestrian1.getPositionVector(), target);
		float distance2 = PVector.dist(pedestrian2.getPositionVector(), target);

		if (distance1 < distance2) {
			return -1;
		}
		if (distance1 > distance2) {
			return 1;
		}

		return 0;
	}
}
