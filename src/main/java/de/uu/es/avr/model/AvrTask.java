package de.uu.es.avr.model;

import com.google.inject.Inject;

/**
 * The {@link AvrTask} models an adaptive variable-rate task.
 * 
 * @author vrichthammer
 *
 */
public class AvrTask extends Task {

	// release angle of events (task instances)
	protected final double phi;

	// execution modes of the task
	protected final C cFunction;

	// list of possible worst-case rotation speeds for the initial instance of
	// the task
	protected final double[] omega0;

	// rotation speed after decoding, set for each released event
	protected double decodedOmega;

	/**
	 * Constructs an {@link AvrTask} with its id, release angle, and execution
	 * modes.
	 * 
	 * @param id
	 *            the id
	 * @param phi
	 *            the release angle
	 * @param c
	 *            the execution modes
	 */
	@Inject
	public AvrTask(String id, double phi, C c) {

		super(id);

		this.phi = phi * Math.PI;
		this.cFunction = c;

		double[] boundaries = this.cFunction.getBoundaries();

		// feasible values for worst-case starting speed omega0 are all possible
		// execution-mode boundaries
		// (except the lowest).
		this.omega0 = new double[boundaries.length - 1];
		for (int i = 1; i < boundaries.length; i++) {
			omega0[i - 1] = boundaries[i];
		}
	}

	/**
	 * Returns the maximal rotation speed of the task.
	 *
	 * @return the maximal rotation speed
	 */
	public double maxOmega() {
		return this.cFunction.getMaxOmega();
	}

	/**
	 * Returns the minimal rotation speed of the task.
	 *
	 * @return the minimal rotation speed
	 */
	public double minOmega() {
		return this.cFunction.getMinOmega();
	}

	/**
	 * Returns the release angle of the task.
	 *
	 * @return the release angle
	 */
	public double getPhi() {
		return this.phi;
	}

	/**
	 * Returns the execution modes of the task.
	 *
	 * @return the execution modes
	 */
	public C getCfunction() {
		return this.cFunction;
	}

	/**
	 * Returns the array of possible rotation speeds for the first event (task
	 * instance).
	 *
	 * @return the array of rotation speeds
	 */
	public double[] getOmega0s() {
		return this.omega0;
	}

	/**
	 * Returns the rotation speed for the first event from the array of rotation
	 * speeds, given an array index.
	 *
	 * @param index
	 *            the index selecting the rotation speed
	 * @return the rotation speed
	 */
	public double getOmega0(int index) {
		assert index >= 0 && index < omega0.length : "omega0-index out of bounds";
		return this.omega0[index];
	}

	/**
	 * Returns the decoded rotation speed of an event (task instance).
	 *
	 * @return the rotation speed
	 */
	public double getOmega() {
		return this.decodedOmega;
	}

	/**
	 * Sets the rotation speed of an event (task instance).
	 *
	 * @param decodedOmega
	 *            the rotation speed
	 */
	public void setOmega(double decodedOmega) {
		this.decodedOmega = decodedOmega;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uu.avrOpt.model.Task#getEvent()
	 */
	@Override
	public AvrTask getEvent() {
		return new AvrTask(this.id, this.phi / Math.PI, this.cFunction);
	}
}
