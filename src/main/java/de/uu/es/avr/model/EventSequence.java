package de.uu.es.avr.model;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;

import de.uu.es.avr.AvrWCRTProblem;

/**
 * The {@link EventSequence} represents the phenotype of the
 * {@link AvrWCRTProblem}. It is a sequence of events ({@link AvrTask}
 * instances) with decoded execution modes, maximizing interference.
 * 
 * @author vrichthammer
 *
 */
public class EventSequence extends LinkedList<AvrTask> {

	private static final long serialVersionUID = 1L;

	protected boolean feasible;

	/**
	 * Constructs an event sequence with a list of AVR task instances.
	 * 
	 * @param eventSequence
	 *            the event sequence
	 */
	@Inject
	public EventSequence(List<AvrTask> eventSequence) {
		this.addAll(eventSequence);
	}

	/**
	 * Constructs an event sequence with a list of AVR task instances,
	 * indicating whether the combination of decoded consecutive rotation speeds
	 * is feasible.
	 * 
	 * @param eventSequence
	 *            the event sequence
	 * @param feasible
	 *            the feasibility
	 */
	@Inject
	public EventSequence(List<AvrTask> eventSequence, boolean feasible) {
		this(eventSequence);
		this.feasible = feasible;
	}

	/**
	 * Returns whether the sequence of decoded rotation speeds is feasible.
	 *
	 * @return the feasibility
	 */
	public boolean isFeasible() {
		return this.feasible;
	}

	/**
	 * Sets the feasibility of an event sequence (during decoding).
	 *
	 * @param feasible
	 *            the feasibility
	 */
	public void setFeasible(boolean feasible) {
		this.feasible = feasible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() {

		if (!this.feasible) {
			return "infeasible";
		}

		if (this.size() == 0) {
			return "empty";
		}

		String result = "";

		for (AvrTask event : this) {
			result += event.getId() + ": " + event.getOmega() + " ";
		}
		return result;
	}
}
