package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.CatpedsimAgent;

/**
 * Finds all the subclasses for a certain class in a specified package.
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
public final class SubClassesFinder {

	/**
	 * Construcor.
	 */
	private SubClassesFinder() {

	}

	/**
	 * Find all the subclasses for the class baseClass in the package
	 * scanningInPackage.
	 * 
	 * @param baseClass
	 *            base class
	 * @param scanningInPackage
	 *            the package to look into
	 * @return list of sublcasses
	 * @throws ClassNotFoundException
	 *             the class wasn't found
	 */
	public static List<Class<?>> subclassScan(final Class<?> baseClass, final String scanningInPackage)
			throws ClassNotFoundException {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
		provider.addIncludeFilter(new AssignableTypeFilter(CatpedsimAgent.class));

		List<Class<?>> listOfSubclasses = new LinkedList<>();

		Set<BeanDefinition> components = provider.findCandidateComponents(scanningInPackage);
		for (BeanDefinition component : components) {
			Class<?> foundClass = Class.forName(component.getBeanClassName());

			if (!foundClass.equals(baseClass)) {
				listOfSubclasses.add(foundClass);
			}
		}
		return listOfSubclasses;
	}
}
