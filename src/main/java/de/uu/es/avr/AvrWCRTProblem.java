package de.uu.es.avr;

import java.util.List;

import org.opt4j.core.start.Constant;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.uu.es.avr.io.TaskSetReader;
import de.uu.es.avr.model.AvrTask;

/**
 * The {@link AvrWCRTProblem} specification to determine worst-case response
 * time (WCRT) for pre-emptive, fixed priority task sets containing
 * {@link AvrTask}s using multi-objective optimization. Contains the task set to
 * analyze as well as physical constraints of the system (e.g. min/max
 * acceleration).
 * 
 * @author vrichthammer
 * 
 */
@Singleton
public class AvrWCRTProblem {

	protected final double accMax; // e.g. 100.0 * Math.PI;
	protected final double accMin; // e.g. -100.0 * Math.PI;

	// the task-set specification
	protected final List<AvrTask> taskset;
	protected final int tasksetSize;

	static TaskSetReader reader = new TaskSetReader();

	// minimal and maximal omega values over all tasks, used for absolute
	// encoding
	protected final double minOmega;
	protected final double maxOmega;

	/**
	 * Constructs an {@link AvrWCRTProblem} specification.
	 * 
	 * @param filename
	 *            the xml specification of the task set
	 * @param accMax
	 *            the maximal acceleration
	 * @param accMin
	 *            the minimal acceleration
	 */
	@Inject
	public AvrWCRTProblem(@Constant(value = "filename", namespace = AvrWCRTProblem.class) String filename,
			@Constant(value = "accMin", namespace = AvrWCRTProblem.class) double accMin,
			@Constant(value = "accMax", namespace = AvrWCRTProblem.class) double accMax) {

		assert accMin < accMax : "AvrWCRTProblem: minimal acceleration must be smaller than maxmimal acceleration";

		this.accMax = accMax;
		this.accMin = accMin;

		// import task set from xml specification
		this.taskset = reader.read(filename);
		this.tasksetSize = taskset.size();

		this.minOmega = minOmega(taskset);
		this.maxOmega = maxOmega(taskset);
	}

	/**
	 * Returns the {@link AvrTask} at the specified index from the task set.
	 *
	 * @param index
	 *            the index
	 * @return the AVR task
	 */
	public AvrTask getTask(int index) {
		return this.taskset.get(index);
	}

	/**
	 * Returns the minimal rotation speed over all {@link AvrTask}s in the task
	 * set.
	 *
	 * @param taskset
	 *            the task set
	 * @return the overall minimal rotation speed
	 */
	private double minOmega(List<AvrTask> taskset) {
		double min = Double.MAX_VALUE;

		for (AvrTask task : taskset) {
			double minOmega = task.minOmega();
			if (minOmega < min) {
				min = minOmega;
			}
		}
		return min;
	}

	/**
	 * Returns the maximal rotation speed over all {@link AvrTask}s in the task
	 * set.
	 *
	 * @param taskset
	 *            the task set
	 * @return the overall maximal rotation speed
	 */
	private double maxOmega(List<AvrTask> taskset) {
		double max = Double.MIN_VALUE;

		for (AvrTask task : taskset) {
			double maxOmega = task.maxOmega();
			if (maxOmega > max) {
				max = maxOmega;
			}
		}
		return max;
	}

	/**
	 * Returns the maximal acceleration of the system.
	 *
	 * @return the maximal acceleration
	 */
	public double getAccMax() {
		return this.accMax;
	}

	/**
	 * Returns the minimal acceleration of the system.
	 *
	 * @return the minimal acceleration
	 */
	public double getAccMin() {
		return this.accMin;
	}

	/**
	 * Returns the task set.
	 *
	 * @return the task set
	 */
	public List<AvrTask> getTaskset() {
		return this.taskset;
	}

	/**
	 * Returns the number of {@link AvrTask}s in the task set.
	 *
	 * @return the size of the task set
	 */
	public int getTasksetSize() {
		return tasksetSize;
	}

	/**
	 * Returns the minimal rotation speed over all tasks.
	 *
	 * @return the minimal rotation speed
	 */
	public double getMinOmega() {
		return minOmega;
	}

	/**
	 * Returns the maximal rotation speed over all tasks.
	 *
	 * @return the maximal rotation speed
	 */
	public double getMaxOmega() {
		return maxOmega;
	}
}
