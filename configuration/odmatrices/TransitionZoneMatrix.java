package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.odmatrices;

import java.io.File;
import java.util.Objects;
import java.util.Random;

import org.apache.log4j.Logger;

import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.AgentBehaviour;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.Utils;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * Distribution matrix for all the transition areas. Example of the distribution
 * matrix:
 * 
 * 5 A B C
 * 
 * A 100 0 0
 * 
 * B 0 100 0
 * 
 * C 23 70 7
 * 
 * The cell (0,0) represents time period in minutes, otherwise specified. A, B ,
 * C represent entrances/exists and the rest of the matrix contains the
 * distribution (in percetage) of agents over the time period.
 * 
 * For example, from the number of agents arriving at entrance C, 23% depart to
 * zone A, 70% depart to zone B, 7% remain in zone C.
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
public class TransitionZoneMatrix {
	/** Logger. */
	private static final Logger LOGGER = UtilityLogger.getLogger(TransitionZoneMatrix.class);

	/** The original-destination matrix. */
	private Object[][] matrix = null;

	/** Matrix with comments. Every cell contains a link to a file. */
	private AgentBehaviour[][] commentsWithFilePaths = null;

	/** Percentage value. */
	private static final int PERCENTAGE = 100;

	/**
	 * Constructor.
	 * 
	 * @param numberOfRows
	 *            number of rows
	 * @param numberOfColumns
	 *            number of columns
	 */
	public TransitionZoneMatrix(final int numberOfRows, final int numberOfColumns) {
		matrix = new Object[numberOfRows][numberOfColumns];
		commentsWithFilePaths = new AgentBehaviour[numberOfRows][numberOfColumns];
	}

	/**
	 * Prepare the transition matrix for random distributions. For every row,
	 * assign an interval for generating random values.
	 */
	public final void prepareMatrixForRandomDistributions() {
		try {
			for (int row = 1; row < matrix.length; row++) {
				int sum = 0;

				for (int column = 1; column < matrix[0].length; column++) {
					Object cell = matrix[row][column];

					if (cell != null && cell instanceof Float) {
						int cellPercentage = ((Float) cell).intValue();
						matrix[row][column] = new Float(cellPercentage + sum);
						sum += cellPercentage;
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while preparing the ");
		}
	}

	/**
	 * Add cell in the transition zone distribution matrix.
	 * 
	 * @param rowIndex
	 *            index row
	 * @param columnIndex
	 *            index column
	 * @param cellValue
	 *            cell value
	 * @param fileToPath
	 *            file to pre-computed path
	 */
	public final void addCell(final int rowIndex, final int columnIndex, final Object cellValue,
			final File fileToPath) {
		try {
			matrix[rowIndex][columnIndex] = cellValue;
			if (fileToPath != null) {
				commentsWithFilePaths[rowIndex][columnIndex] = AgentBehaviour.loadXmlPath(fileToPath.getAbsolutePath());
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while filling in the transition zone distribution matrix", ex);
		}
	}

	/**
	 * Get a cell from the matrix.
	 * 
	 * @param rowIndex
	 *            index of the row
	 * @param columnIndex
	 *            index of the column
	 * @return the cell value
	 */
	public final Object getMatrixCell(final int rowIndex, final int columnIndex) {
		try {
			return matrix[rowIndex][columnIndex];
		} catch (Exception ex) {
			LOGGER.info("Error while reading the OD matrices cells.", ex);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		StringBuilder matrixInformation = new StringBuilder();

		final int maxCharactersToDisplayInTheCell = 10;
		matrixInformation.append("\n");

		for (int indexRow = 0; indexRow < matrix.length; indexRow++) {
			for (int indexColumn = 0; indexColumn < matrix[indexRow].length; indexColumn++) {
				matrixInformation.append(padStringWithSpacesToReachLengthOrTruncateIfLarger(
						matrix[indexRow][indexColumn], maxCharactersToDisplayInTheCell));
			}
			matrixInformation.append("\n");
		}

		for (int indexRow = 0; indexRow < commentsWithFilePaths.length; indexRow++) {
			for (int indexColumn = 0; indexColumn < commentsWithFilePaths[indexRow].length; indexColumn++) {
				AgentBehaviour paths = commentsWithFilePaths[indexRow][indexColumn];
				if (paths != null) {
					matrixInformation.append("Path " + indexRow + "," + indexColumn + " : "
							+ commentsWithFilePaths[indexRow][indexColumn] + "\n");
				}
			}
		}

		return matrixInformation.toString();
	}

	/**
	 * Right pad string with whitespaces to reach a certain "n" size. If the
	 * size is larger, truncate string.
	 * 
	 * @param object
	 *            object value to append to the string
	 * @param finalLengthOfText
	 *            final sting length
	 * @return the final string
	 */
	private String padStringWithSpacesToReachLengthOrTruncateIfLarger(final Object object,
			final int finalLengthOfText) {

		String text = "";

		if (Objects.isNull(object)) {
			text = Utils.padStringWithSpacesRight("", finalLengthOfText);
		} else {
			text = "" + object;
		}

		if (text != null && !text.isEmpty()) {
			int lengthText = text.length();
			if (lengthText > finalLengthOfText) {
				text = text.substring(0, finalLengthOfText);
			} else if (lengthText < finalLengthOfText) {
				text = Utils.padStringWithSpacesRight(text, finalLengthOfText);
			}
		}

		return text;
	}

	/**
	 * Get the behaviour from the transtition zone matrix.
	 * 
	 * @param rowIndex
	 *            index row
	 * @param columnIndex
	 *            index column
	 * @return agent pre-defined behaviour
	 */
	public final AgentBehaviour getMatrixBehaviour(final int rowIndex, final int columnIndex) {
		try {
			return commentsWithFilePaths[rowIndex][columnIndex];
		} catch (Exception ex) {
			LOGGER.warn("Error while reading the OD matrices cells.", ex);
			return null;
		}
	}

	/**
	 * Get the row index for the specified entrance name.
	 * 
	 * @param transitionZoneName
	 *            transition zone name
	 * @return row index for the specified entrance name
	 */
	public final int lookupEntranceInTransitionMatrix(final String transitionZoneName) {
		String trimmedZoneName = transitionZoneName.trim();

		try {
			int columnIndex = 0;

			for (int row = 1; row < matrix.length; row++) {
				Object cellValue = matrix[row][columnIndex];
				String cellString = null;

				if (cellValue != null) {
					if (cellValue instanceof Float) {
						cellString = Integer.toString(((Float) cellValue).intValue());
					} else if (cellValue instanceof String) {
						cellString = (String) cellValue;
					}

					if (trimmedZoneName.equalsIgnoreCase(cellString.trim())) {
						return row;
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while searching in the transition zone matrix.", ex);
		}

		return -1;
	}

	/**
	 * Generate distribution of agents in the transition areas based on the
	 * distribution matrices.
	 * 
	 * 
	 * @param transitionMatrixRow
	 *            transition zone name
	 * @return the column index in the transition matrix representing the target
	 */
	public final int probabilisticDistributionInTransitionArea(final int transitionMatrixRow) {
		try {
			int random = new Random().nextInt(PERCENTAGE + 1);

			for (int columnIndex = 1; columnIndex < matrix[0].length; columnIndex++) {
				Object cellValue = matrix[transitionMatrixRow][columnIndex];

				if (cellValue != null && cellValue instanceof Float) {
					int value = Math.min(((Float) cellValue).intValue(), PERCENTAGE);

					if (value == PERCENTAGE) {
						return columnIndex;
					} else if (random < value) {
						return columnIndex;
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error reading from the transition area distribution matrix", ex);
		}

		return -1;
	}

	/**
	 * Get exit name.
	 * 
	 * @param transitionMatrixColumn
	 *            index column
	 * @return exit name
	 */
	public final String getExit(final int transitionMatrixColumn) {
		String exit = null;
		try {
			Object cell = matrix[0][transitionMatrixColumn];

			if (cell instanceof String) {
				exit = (String) cell;
			} else if (cell instanceof Float) {
				exit = Integer.toString(((Float) cell).intValue());
			}
		} catch (Exception ex) {
			LOGGER.info("The exit in the transition distibution matrix doesn't exit.", ex);
		}

		return exit;
	}
}