package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils;

import java.util.List;
import java.util.stream.Collectors;

import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAgent;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.bridgeconnector.CatpedsimBridge.ConvertAgentFromInternalModelToExternalModel;

/**
 * List Utilities.
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
public final class ListUtilities {

	/**
	 * Private constructor.
	 */
	private ListUtilities() {

	}

	/**
	 * Transforms a list of one type List
	 * <Q>to a list of a different type List<V>.
	 * 
	 * @param <Q>
	 *            class type
	 * @param <K>
	 *            class type
	 * @param <V>
	 *            class type
	 * @param list
	 *            the original source
	 * @param convertPedestrianFromInternalModelToExternalModel
	 *            the trnaformed list
	 * @return a list with the new elements
	 */
	@SuppressWarnings("unchecked")
	public static <K, V, Q extends K> List<V> transform(final List<CatpedsimAgent> list,
			final ConvertAgentFromInternalModelToExternalModel convertPedestrianFromInternalModelToExternalModel) {
		if (null == list) {
			return null;
		}
		return ((List<V>) list.stream().map(convertPedestrianFromInternalModelToExternalModel)
				.collect(Collectors.toList()));
	}
}
