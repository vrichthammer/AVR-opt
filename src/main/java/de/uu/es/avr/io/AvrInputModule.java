package de.uu.es.avr.io;

import org.opt4j.core.config.annotations.File;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.config.annotations.Order;
import org.opt4j.core.start.Constant;
import org.opt4j.core.start.Opt4JModule;

import de.uu.es.avr.AvrWCRTProblem;

/**
 * The {@AvrInputModule} allows to specify the input specification of the
 * {@link AvrWCRTProblem} as well as physical constraints of the system.
 * 
 * @author vrichthammer
 *
 */
public class AvrInputModule extends Opt4JModule {

	@Order(1)
	@Info("The file containing the task set specification.")
	@File
	@Constant(namespace = AvrWCRTProblem.class, value = "filename")
	protected String filename = "";

	@Order(2)
	@Info("The maximal acceleration.")
	@Constant(namespace = AvrWCRTProblem.class, value = "accMax")
	protected double accMax = 100.0 * Math.PI;

	@Order(3)
	@Info("The minimal acceleration.")
	@Constant(namespace = AvrWCRTProblem.class, value = "accMin")
	protected double accMin = -100.0 * Math.PI;

	@Override
	protected void config() {
		bind(AvrWCRTProblem.class).in(SINGLETON);
	}

	/**
	 * Returns the file name of the input specification.
	 *
	 * @return the file name
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets the file name of the input specification.
	 *
	 * @param filename
	 *            the file name
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Returns the maximal acceleration of the system.
	 *
	 * @return the maximal acceleration
	 */
	public double getAccMax() {
		return accMax;
	}

	/**
	 * Sets the maximal acceleration of the system.
	 *
	 * @param accMax
	 *            the maximal acceleration
	 */
	public void setAccMax(double accMax) {
		this.accMax = accMax;
	}

	/**
	 * Returns the minimal acceleration of the system.
	 *
	 * @return the minimal acceleration
	 */
	public double getAccMin() {
		return accMin;
	}

	/**
	 * Sets the minimal acceleration of the system.
	 *
	 * @param accMin
	 *            the minimal acceleration
	 */
	public void setAccMin(double accMin) {
		this.accMin = accMin;
	}
}
