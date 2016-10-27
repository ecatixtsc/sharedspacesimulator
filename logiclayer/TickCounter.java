package uk.org.catapult.ts.cav.microsimulator.pedestrian.catpedsim.logiclayer;

/**
 * Internal tick counter. Number of ticks since the beginng of the simulation.
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
public class TickCounter {
	/**
	 * The current block index. The block fills in when the value Long.MAX_VALUE
	 * is achieved.
	 * 
	 * The final number of ticks is :
	 * 
	 * (numberOfBlocks x Long.MAX_VALUE) + completionInCurrentBlock.
	 */
	private int currentBlock = 0;

	/**
	 * Number of ticks in the uncompleted block.
	 */
	private long completionInCurrentBlock = 0;

	/**
	 * Set current block index.
	 * 
	 * 
	 */
	public final void incrementTickCount() {
		if (completionInCurrentBlock == Long.MAX_VALUE) {
			currentBlock++;
		}
		completionInCurrentBlock++;
	}

	/**
	 * Get the information about the total number of ticks.
	 * 
	 * @return get text information about the number of tick
	 */
	public final String getInformation() {
		StringBuilder information = new StringBuilder();

		if (currentBlock > 0) {
			information.append(currentBlock + " x " + completionInCurrentBlock + " + ");
		}
		information.append(completionInCurrentBlock);

		return information.toString();
	}

	/**
	 * Get number of ticks. The number is % MAX_LONG.
	 * 
	 * @return the number of ticks as long
	 */
	public final long getNumberOfTicksAsLong() {
		return completionInCurrentBlock;
	}
}
