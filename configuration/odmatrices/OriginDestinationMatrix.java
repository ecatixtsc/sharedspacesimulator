package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.odmatrices;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import org.apache.log4j.Logger;

import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.AgentBehaviour;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.RegexPatterns;
import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.utils.Utils;
import uk.org.catapult.ts.cav.utils.TimeUtilities;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * The matrix holds combinations of entrance, exit and traffic flow. The static
 * arrays are the fastest structures to iterate through.
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
 * @param <T>
 */
public class OriginDestinationMatrix<T> {
	/** Logger. */
	private static final Logger LOGGER = UtilityLogger.getLogger(OriginDestinationMatrix.class);

	/** Indicates the current matrix belongs to a certain type of agent. */
	private T agentClassType;

	/** The origin-destination matrix. */
	private Object[][] matrix = null;

	/** Agents ready to depart. */
	private float[][] toDepart = null;

	/** Rate of departures per tick. */
	private float[][] rateOfDeparturesPerTick = null;

	/**
	 * Represent the agents in color in order to identify them during spwaning.
	 */
	private Color[][] colorMatrix = null;

	/**
	 * Matrix with comments. Every cell contains a link to a file.
	 */
	private AgentBehaviour[][] commentsWithFilePaths = null;

	/** Period length converted to seconds. */
	private float periodLengthInSeconds = 0;

	/**
	 * Constructor.
	 * 
	 * @param agentType
	 *            of agent
	 * @param numberOfRows
	 *            number of rows in the matrix
	 * @param numberOfColumns
	 *            number of columns in the matrix
	 */
	public OriginDestinationMatrix(final T agentType, final int numberOfRows, final int numberOfColumns) {
		agentClassType = agentType;

		matrix = new Object[numberOfRows][numberOfColumns];
		commentsWithFilePaths = new AgentBehaviour[numberOfRows][numberOfColumns];
		toDepart = new float[numberOfRows][numberOfColumns];
		rateOfDeparturesPerTick = new float[numberOfRows][numberOfColumns];
	}

	/**
	 * Prepare agents flows.
	 * 
	 * @param tickLengthSeconds
	 *            tick length in seconds
	 */
	public final void prepareMatrixData(final float tickLengthSeconds) {
		try {
			for (int indexRow = 1; indexRow < matrix.length; indexRow++) {
				for (int indexColumn = 1; indexColumn < matrix[indexRow].length; indexColumn++) {
					Object cell = matrix[indexRow][indexColumn];

					if (cell != null && cell instanceof Float) {
						float totalDepartures = (Float) cell;

						if (totalDepartures >= 1) {
							toDepart[indexRow][indexColumn] = 1;
							rateOfDeparturesPerTick[indexRow][indexColumn] = (totalDepartures * tickLengthSeconds)
									/ periodLengthInSeconds;
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("OD matrix get error.", ex);
		}
	}

	/**
	 * Get agent type.
	 * 
	 * @return agent type
	 */
	public final T getAgentClassType() {
		return agentClassType;
	}

	/**
	 * Get the number of entrances/origin.
	 * 
	 * @return the number of entrances
	 */
	public final int getNumberOfEntrances() {
		if (getNumberOfRows() - 1 < 0) {
			return 0;
		} else {
			return getNumberOfRows();
		}
	}

	/**
	 * Get the number of exists.
	 * 
	 * @return the number of exits
	 */
	public final int getNumberOfExits() {
		if (getNumberOfColumns() - 1 < 0) {
			return 0;
		} else {
			return getNumberOfColumns();
		}
	}

	/**
	 * Get the number of rows in the matrix.
	 * 
	 * @return the number of rows
	 */
	public final int getNumberOfRows() {
		int numberOfRows = 0;

		try {
			numberOfRows = matrix.length;
		} catch (Exception ex) {
			LOGGER.info("The origin-destination matrix is not allocated.", ex);
		}

		return numberOfRows;
	}

	/**
	 * Get the number of columns.
	 * 
	 * @return the number of columns
	 */
	public final int getNumberOfColumns() {
		int numberOfExits = 0;

		try {
			numberOfExits = matrix[0].length;
		} catch (Exception ex) {
			LOGGER.info("The origin-destination matrix is not allocated.", ex);
		}

		return numberOfExits;
	}

	/**
	 * Add a cell in the matrix as an Object. It could be String or Float. The
	 * cell (0,0) stores the period length.
	 * 
	 * @param rowIndex
	 *            index of the row
	 * @param columnIndex
	 *            index of the column
	 * @param cellValue
	 *            cell value
	 * @param fileToPath
	 *            xml file path
	 */
	public final void addCell(final int rowIndex, final int columnIndex, final Object cellValue,
			final File fileToPath) {
		try {

			matrix[rowIndex][columnIndex] = cellValue;
			if (fileToPath != null) {
				commentsWithFilePaths[rowIndex][columnIndex] = AgentBehaviour.loadXmlPath(fileToPath.getAbsolutePath());
			}

			if (rowIndex == 0 && columnIndex == 0) {
				periodLengthInSeconds = extractPeriodLengthConvertToSeconds(cellValue);
				if (periodLengthInSeconds == 0) {
					throw new Exception("The OD matrix time period was 0!");
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error while filling in the origin-destination matrix for " + agentClassType, ex);
		}
	}

	/**
	 * Generate random color for agents in order to make them recognizable
	 * during spawning.
	 */
	public final void generateRandomColorForAgents() {
		try {
			colorMatrix = new Color[getNumberOfRows()][getNumberOfColumns()];

			for (int indexRow = 0; indexRow < matrix.length; indexRow++) {
				for (int indexColumn = 0; indexColumn < matrix[indexRow].length; indexColumn++) {

					if (matrix[indexRow][indexColumn] != null) {
						colorMatrix[indexRow][indexColumn] = Utils.generateRandomDarkColor();
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.info("Error while generatings agents random colors.", ex);
		}
	}

	/**
	 * Extract period length from the position (0,0), convert it to seconds. If
	 * the time period unit is not specified the program will assume it is
	 * minutes.
	 * 
	 * @param cellValue
	 *            the text containing the cell information
	 * @return period length in seconds
	 * @throws Exception
	 *             on error
	 */
	private float extractPeriodLengthConvertToSeconds(final Object cellValue) throws Exception {
		float convertedPeriodLength = 0;

		if (!Objects.isNull(cellValue)) {
			if (cellValue instanceof Float) {
				convertedPeriodLength = (float) TimeUtilities.minutesToSeconds(((Float) cellValue).floatValue());
			}

			if (cellValue instanceof String) {
				String cleanCellText = cellValue.toString().replaceAll("[^a-zA-Z0-9.]", "");

				float periodLengthValue = Float
						.parseFloat(cleanCellText.replaceAll(RegexPatterns.TEXT_WITH_DIGITS_ONLY, ""));
				String periodLengthUnit = cleanCellText.replaceAll(RegexPatterns.TEXT_WITH_LETTERS_NO_DIGITS, "")
						.toLowerCase();

				if (periodLengthUnit.contains("minutes") || periodLengthUnit.isEmpty()) {
					convertedPeriodLength = (float) TimeUtilities.minutesToSeconds(periodLengthValue);
				} else if (periodLengthUnit.contains("seconds")) {
					convertedPeriodLength = periodLengthValue;
				}
			}
		}

		return convertedPeriodLength;
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

		matrixInformation.append(agentClassType.toString());

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

		for (int indexRow = 0; indexRow < rateOfDeparturesPerTick.length; indexRow++) {
			for (int indexColumn = 0; indexColumn < rateOfDeparturesPerTick[indexRow].length; indexColumn++) {
				matrixInformation.append(padStringWithSpacesToReachLengthOrTruncateIfLarger(
						rateOfDeparturesPerTick[indexRow][indexColumn], maxCharactersToDisplayInTheCell));
			}
			matrixInformation.append("\n");
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
	 * Clone origin-destination matrix.
	 * 
	 * @param sourceMatrix
	 *            source matrix
	 * @return destination matrix
	 */
	public final Object[][] cloneMatrix(final Object[][] sourceMatrix) {
		Object[][] destinationMatrix = new Object[sourceMatrix.length][];
		for (int i = 0; i < sourceMatrix.length; i++) {
			destinationMatrix[i] = Arrays.copyOf(sourceMatrix[i], sourceMatrix[i].length);
		}
		return destinationMatrix;
	}

	/**
	 * Get entrance for the origin-destination matrix.
	 * 
	 * @param indexRow
	 *            row index
	 * @return entrance
	 */
	public final Object getEntrance(final int indexRow) {
		Object entrance = null;

		if (indexRow > 0) {
			try {
				entrance = matrix[indexRow][0];
			} catch (Exception ex) {
				LOGGER.info("Origin-destination matrix request out of boundary!", ex);
			}
		}

		return entrance;
	}

	/**
	 * Get exit for the origin-destination matrix.
	 * 
	 * @param indexColumn
	 *            index column
	 * @return exit
	 */
	public final Object getExit(final int indexColumn) {
		Object exit = null;

		if (indexColumn > 0) {
			try {
				exit = matrix[0][indexColumn];
			} catch (Exception ex) {
				LOGGER.info("Origin-destination matrix request out of boundary!", ex);
			}
		}

		return exit;
	}

	/**
	 * Update cell value.
	 * 
	 * @param indexRow
	 *            index of row
	 * @param indexColumn
	 *            index of column
	 * @param numberOfAgents
	 *            the new cell value
	 */
	public final void updateMatrixCell(final int indexRow, final int indexColumn, final float numberOfAgents) {
		try {
			matrix[indexRow][indexColumn] = numberOfAgents;
		} catch (Exception ex) {
			LOGGER.info("Origin-destination matrix request out of boundary!", ex);
		}
	}

	/**
	 * Get agent color.
	 * 
	 * @param indexRow
	 *            index row
	 * @param indexColumn
	 *            index column
	 * @return agent color
	 */
	public final Color getColor(final int indexRow, final int indexColumn) {
		Color color = Color.BLACK;

		try {
			color = colorMatrix[indexRow][indexColumn];
		} catch (Exception ex) {
			LOGGER.info("The color is not defined for this path.");
		}

		return color;
	}

	/**
	 * Get the precomputed path from the cell comment.
	 * 
	 * @param indexRow
	 *            index row
	 * @param indexColumn
	 *            index column
	 * @return agent behaviour
	 */
	public final AgentBehaviour getPrecomputedPath(final int indexRow, final int indexColumn) {
		try {
			return commentsWithFilePaths[indexRow][indexColumn];
		} catch (Exception ex) {
			LOGGER.warn("Error while reading from cells; comments!", ex);
			return null;
		}
	}

	/**
	 * Get the number of agetns to depart.
	 * 
	 * @param indexRow
	 *            index row
	 * @param indexColumn
	 *            index column
	 * @return the number of agetns to depart
	 */
	public final float getNumberToDepart(final int indexRow, final int indexColumn) {
		try {
			return toDepart[indexRow][indexColumn];
		} catch (Exception ex) {
			LOGGER.warn("Error while reading from to depart array!", ex);
			return 0;
		}
	}

	/**
	 * Update the number of agents to depart.
	 * 
	 * @param indexRow
	 *            index row
	 * @param indexColumn
	 *            index column
	 * @param deployed
	 *            number of agents deployed
	 */
	public final void updateReadyToDepart(final int indexRow, final int indexColumn, final float deployed) {
		try {
			float value = toDepart[indexRow][indexColumn];
			toDepart[indexRow][indexColumn] = value - deployed + rateOfDeparturesPerTick[indexRow][indexColumn];
		} catch (Exception ex) {
			LOGGER.warn("Error while reading from to depart array!", ex);
		}
	}

	/**
	 * Free the space.
	 */
	public final void clear() {
		matrix = null;
		toDepart = null;
		rateOfDeparturesPerTick = null;
		colorMatrix = null;

		try {
			for (int indexRow = 1; indexRow < matrix.length; indexRow++) {
				for (int indexColumn = 1; indexColumn < matrix[indexRow].length; indexColumn++) {
					(commentsWithFilePaths[indexRow][indexColumn]).clear();
				}
			}
		} catch (Exception e) {
			LOGGER.info("The matrix didn't contain any paths");
		}
	}
}
