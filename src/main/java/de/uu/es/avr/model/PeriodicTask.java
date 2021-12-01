package de.uu.es.avr.model;

/**
 * The {@link PeriodicTask} models a periodic task that is released once every
 * {@code period}.
 * 
 * @author vrichthammer
 *
 */
public class PeriodicTask extends Task {

	private final double executionTime;
	private final double period;
	private final double deadline;

	/**
	 * Creates a periodic {@link Task} with an {@code executionTime, period} and
	 * {@link deadline}.
	 * 
	 * @param id
	 *            the id
	 * @param executionTime
	 *            the execution time
	 * @param period
	 *            the period
	 * @param deadline
	 *            the deadline
	 */
	public PeriodicTask(String id, double executionTime, double period, double deadline) {
		super(id);

		this.executionTime = executionTime;
		this.period = period;
		this.deadline = deadline;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uu.avrOpt.model.Task#getEvent()
	 */
	@Override
	public PeriodicTask getEvent() {
		return new PeriodicTask(this.id, this.executionTime, this.period, this.deadline);
	}

	/**
	 * Returns the execution time of the {@link PeriodicTask}.
	 * 
	 * @return the execution time.
	 */
	public double getExecutionTime() {
		return this.executionTime;
	}

	/**
	 * Returns the period of the {@link PeriodicTask}.
	 * 
	 * @return the period
	 */
	public double getPeriod() {
		return this.period;
	}

	/**
	 * Returns the deadline of the {@link PeriodicTask}.
	 * 
	 * @return the deadline
	 */
	public double getDeadline() {
		return this.deadline;
	}
}
