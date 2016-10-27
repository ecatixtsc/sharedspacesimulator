package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.configuration.odmatrices;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.agents.AgentBehaviour;
import uk.org.catapult.ts.cav.utils.ResourceUtilities;
import uk.org.catapult.ts.cav.utils.ResourceUtilities.ResourceExport;
import uk.org.catapult.ts.cav.utils.TimeUtilities;
import uk.org.catapult.ts.cav.utils.UtilityLogger;

/**
 * Create Workbook instance holding reference to .xlsx file.
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
public final class ReaderOfOriginDestinationMatricesFromExcel {
	/** Logger. */
	private static final Logger LOGGER = UtilityLogger.getLogger(ReaderOfOriginDestinationMatricesFromExcel.class);

	/** Constructor. */
	private ReaderOfOriginDestinationMatricesFromExcel() {

	}

	/**
	 * A worksheet can hold time period matrix or period length information.
	 * Load all the origin-destination matrices in a Map<Integer,
	 * OriginDestinationMatrix>. The Key holds the time period converted into
	 * seconds associated to the matrix.
	 * 
	 * @param startSimulationTimeText
	 *            simulation start time
	 * @param tickLengthSeconds
	 *            tick length in seconds
	 * @param agentType
	 *            agent type
	 * @param excelFilePath
	 *            the path to the excel file
	 * @return the map of matrices
	 * @throws Exception
	 *             on error
	 */
	public static Map<Integer, OriginDestinationMatrix<?>> readMatrices(final String startSimulationTimeText,
			final float tickLengthSeconds, final Class<?> agentType, final String excelFilePath) throws Exception {

		if (excelFilePath == null || excelFilePath.isEmpty()) {
			return null;
		}

		String cleanFilePath = excelFilePath.replaceAll("\\t", "").replaceAll("\\n", " ");
		File file = ResourceUtilities.getResource(cleanFilePath.trim(), ResourceExport.FILE);

		if (!file.exists()) {
			throw new Exception("The Origin/Destination file path is incorrect!");
		}

		Integer startSimulationTimeSeconds = sanitizeTextToFitTimeDescriptionExtractTimeInSeconds(
				startSimulationTimeText);

		Map<Integer, OriginDestinationMatrix<?>> mapOfMatrices = new TreeMap<Integer, OriginDestinationMatrix<?>>();

		FileInputStream fileInputStream = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

		int numberOfSheets = workbook.getNumberOfSheets();

		for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
			XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
			String workSheetName = sheet.getSheetName();
			int numberOfRows = sheet.getPhysicalNumberOfRows();
			int numberOfColumns = 0;
			Integer matrixTime = sanitizeTextToFitTimeDescriptionExtractTimeInSeconds(workSheetName);

			if (matrixTime == null || matrixTime < startSimulationTimeSeconds || numberOfRows < 2
					|| sheet.getRow(1).getPhysicalNumberOfCells() < 2) {
				continue;
			}
			matrixTime = matrixTime - startSimulationTimeSeconds;
			numberOfColumns = sheet.getRow(0).getPhysicalNumberOfCells();

			OriginDestinationMatrix<?> originDestinationMatrix = new OriginDestinationMatrix<>(agentType, numberOfRows,
					numberOfColumns);

			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					Object cellValue = null;

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						cellValue = new Float(cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						cellValue = cell.getStringCellValue().trim();
						break;
					case Cell.CELL_TYPE_BLANK:
					default:
						break;
					}

					File fileWithPaths = null;
					Comment comment = cell.getCellComment();
					if (comment != null) {
						RichTextString str = comment.getString();

						fileWithPaths = ResourceUtilities.getResource(str.getString().trim(), ResourceExport.FILE);

						if (!fileWithPaths.exists() || !fileWithPaths.isFile()) {
							createTemplateXmlPathFile(fileWithPaths);
							fileWithPaths = null;
						}
					}

					originDestinationMatrix.addCell(row.getRowNum(), cell.getColumnIndex(), cellValue, fileWithPaths);
				}
			}

			originDestinationMatrix.generateRandomColorForAgents();
			originDestinationMatrix.prepareMatrixData(tickLengthSeconds);
			mapOfMatrices.put(matrixTime, originDestinationMatrix);
		}

		workbook.close();
		fileInputStream.close();

		return mapOfMatrices;
	}

	/**
	 * Create xml template file for path definition.
	 * 
	 * @param xmlFile
	 *            xml file
	 */
	public static void createTemplateXmlPathFile(final File xmlFile) {
		AgentBehaviour.generateXmlPathExample(xmlFile.getAbsolutePath());
	}

	/**
	 * Clean the worksheet name in order to fit time description. First remove
	 * all the non-numeric characters, ensure the string has exactly four
	 * characters (example: 0120 for 01:20, 1200 for 12:00 ). Convert string
	 * time format to time in seconds.
	 * 
	 * @param workSheetname
	 *            worsheet name
	 * @return time in seconds
	 */
	public static Integer sanitizeTextToFitTimeDescriptionExtractTimeInSeconds(final String workSheetname) {
		Integer matrixTimeInSeconds = null;
		String cleanTime = null;
		StringBuilder timeDescription = new StringBuilder("");
		final int threeCharactersText = 3;
		final int fourCharactersText = 4;
		final int minutesStartPosition = 2;
		final int minutesEndPosition = 4;

		try {
			cleanTime = workSheetname.replaceAll("[^0-9]", "");

			switch (cleanTime.length()) {
			case threeCharactersText:
				timeDescription.append("0");
			case fourCharactersText:
				timeDescription.append(cleanTime);
				break;
			default:
				break;
			}

			cleanTime = timeDescription.toString();
			if (!cleanTime.isEmpty()) {
				int hours = Integer.parseInt(cleanTime.substring(0, 2));
				int minutes = Integer.parseInt(cleanTime.substring(minutesStartPosition, minutesEndPosition));

				matrixTimeInSeconds = (int) (TimeUtilities.hoursToSeconds(hours)
						+ TimeUtilities.minutesToSeconds(minutes));
			}
		} catch (Exception ex) {
			LOGGER.info("Error while sanitizing OD matrices worksheet names.", ex);
		}

		return matrixTimeInSeconds;
	}
}
