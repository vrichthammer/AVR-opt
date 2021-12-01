package de.uu.es.avr.model;

/**
 * {@C} is a step-wise function representing the execution modes of an
 * {@link AvrTask}, depending on rotation speed.
 * 
 * @author vrichthammer
 *
 */
public class C {

	// worst case execution times of execution modes
	protected final double[] cs; // e.g. {15, 13, 12, 6};

	// boundaries of rotation speed intervals
	protected final double[] boundaries; // e.g. {104.72, 209.44, 314.159,
											// 418.879, 523.599};

	// minimal rotation speed
	protected final double minOmega;

	// maximal rotation speed
	protected final double maxOmega;

	/**
	 * Creates the step-wise function representing worst case execution times
	 * (WCETs) of an {@link AvrTask} depending on rotation speeds.
	 * 
	 * @param cs
	 *            the WCETs of the execution modes
	 * @param boundaries
	 *            the boundaries of the rotation-speed intervals
	 */
	public C(double[] cs, double[] boundaries) {
		this.cs = cs;
		this.boundaries = boundaries;

		assert boundaries != null
				&& boundaries.length > 1 : "boundaries must not be null and must have at least 2 boundary entries";
		assert cs != null && cs.length == boundaries.length - 1 : "WCET array must not be null or has incorrect length";

		this.minOmega = boundaries[0];
		this.maxOmega = boundaries[boundaries.length - 1];
	}

	/**
	 * Returns the WCET for a given rotation speed.
	 * 
	 * @param omega
	 *            the rotation speed
	 * @return the WCET
	 */
	public double getC(double omega) {

		int index = determineIndex(omega);
		return cs[index];
	}

	/**
	 * Returns the index of the execution mode of a given rotation speed in the
	 * array of WCETs.
	 *
	 * @param omega
	 *            the rotation speed
	 * @return the index of the execution mode
	 */
	private int determineIndex(double omega) {

		if (omega <= this.minOmega || omega > this.maxOmega) {
			throw new IllegalArgumentException(
					" getC(): omega " + omega + " out of range (" + this.minOmega + ", " + this.maxOmega + ")");
		}

		int index = 0;

		for (int i = 1; i < this.boundaries.length; i++) {
			if (omega <= this.boundaries[i]) {
				index = i - 1;
				break;
			}
		}
		return index;
	}

	/**
	 * Returns the WCETs of an {@link AvrTask} for all execution modes.
	 * 
	 * @return the WCETs
	 */
	public double[] getCs() {
		return cs;
	}

	/**
	 * Returns the execution mode boundaries of an {@link AVRTask}.
	 * 
	 * @return the execution mode boundaries
	 */
	public double[] getBoundaries() {
		return boundaries;
	}

	/**
	 * Returns the minimal rotation speed.
	 * 
	 * @return the minimal rotation speed
	 */
	public double getMinOmega() {
		return minOmega;
	}

	/**
	 * Returns the maximal rotation speed.
	 * 
	 * @return the maximal rotation speed
	 */
	public double getMaxOmega() {
		return maxOmega;
	}
}
