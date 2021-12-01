package de.uu.es.avr.model;

/**
 * The {@link Task} models the abstract superclass of possible task types to
 * analyse (@see PeriodicTask, AvrTask).
 * 
 * @author vrichthammer
 *
 */
public abstract class Task {

	private static final long serialVersionUID = 1L;
	protected final String id;

	/**
	 * Constructs a task with an identifier.
	 * 
	 * @param id
	 *            the id
	 */
	public Task(String id) {
		this.id = id;
	}

	/**
	 * Creates a task instance (event) of a {@Task}, i.e. a deep copy.
	 *
	 * @return the deep copy
	 */
	public abstract Task getEvent();

	/**
	 * Returns the id of the task.
	 *
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
}
