package de.uu.es.avr;

import static org.opt4j.core.Objective.Sign.MAX;
import static org.opt4j.core.Objective.Sign.MIN;

import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;

import com.google.inject.Inject;

import de.uu.es.avr.model.AvrTask;
import de.uu.es.avr.model.EventSequence;

/**
 * The {@link AvrEvaluator} assesses the quality of an {@link EventSequence},
 * the phenotype of the {@link AvrWCRTProblem}. Optimization objectives are the
 * minimization of overall time, as well as maximization of the worst-case
 * execution time (WCET) of the sequence.
 * 
 * @author vrichthammer
 * 
 */
public class AvrEvaluator implements Evaluator<EventSequence> {

	protected final AvrWCRTProblem avrProblem;

	// the maximal and minimal acceleration of the system
	protected final double accMax;
	protected final double accMin;

	// the optimization objectives
	protected final Objective mu = new Objective("mu", MIN);
	protected final Objective sum_c = new Objective("sum_c", MAX);

	/**
	 * Creates an {@link AvrEvaluator} used to assess the quality of a generated
	 * {@link EventSequence}. Optimization objectives are the minimization of
	 * overall event-sequence time, as well as maxmization of the worst-case
	 * execution time (WCET) of the sequence.
	 * 
	 * @param avrProblem
	 *            the AVR problem specification
	 */
	@Inject
	public AvrEvaluator(AvrWCRTProblem avrProblem) {
		this.avrProblem = avrProblem;
		this.accMax = avrProblem.getAccMax();
		this.accMin = avrProblem.getAccMin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.problem.Evaluator#evaluate(java.lang.Object)
	 */
	@Override
	public Objectives evaluate(EventSequence events) {

		Objectives obj = new Objectives();

		if (!events.isFeasible()) {
			obj.add(mu, Objective.INFEASIBLE);
			obj.add(sum_c, Objective.INFEASIBLE);

			return obj;
		}

		double muVal = mu(events);
		obj.add(mu, muVal);

		double sum_cVal = sum_c(events);
		obj.add(sum_c, sum_cVal);

		return obj;
	}

	/**
	 * Calculates the time of an event sequence, minimizing time between any two
	 * consecutive events in the sequence.
	 *
	 * @param events
	 *            the event sequence
	 * @return the time of the event sequence
	 */
	protected double mu(EventSequence events) {

		double sum = 0;

		for (int i = 0; i <= events.size() - 2; i++) {

			AvrTask event1 = events.get(i);
			AvrTask event2 = events.get(i + 1);

			double deltaPhi = AvrUtils.deltaPhi(event1, event2);

			sum += mu(deltaPhi, event1, event2);
		}

		return sum;
	}

	/**
	 * Calculates the minimal time between two consecutive events.
	 *
	 * @param deltaPhi
	 *            the rotational angle between the events
	 * @param event1
	 *            the first event
	 * @param event2
	 *            the subsequent event
	 * @return the minimal time
	 */
	protected double mu(double deltaPhi, AvrTask event1, AvrTask event2) {

		double omega1 = event1.getOmega();
		double omega2 = event2.getOmega();

		double nom = 2 * accMax * accMin * deltaPhi - accMax * omega2 * omega2 + accMin * omega1 * omega1;
		double denom = (accMin - accMax);

		double root = Math.sqrt(nom / denom);
		root *= (accMin - accMax);

		double sum = root + (accMax * omega2 - accMin * omega1);

		double result = 1000 * sum / (accMin * accMax);

		return result;
	}

	/**
	 * Calculates the overall worst case execution time (WCET) of an event
	 * sequence.
	 *
	 * @param events
	 *            the event sequence
	 * @return the overall WCET
	 */
	protected int sum_c(EventSequence events) {

		int sum = 0;

		for (AvrTask task : events) {
			double omega = task.getOmega();
			double c = task.getCfunction().getC(omega);
			sum += c;
		}
		return sum;
	}
}
