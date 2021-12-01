package de.uu.es.avr.creatorDecoder;

import java.util.LinkedList;
import java.util.List;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.core.genotype.IntegerGenotype;

import de.uu.es.avr.AvrUtils;
import de.uu.es.avr.AvrUtils.DoubleRange;
import de.uu.es.avr.AvrWCRTProblem;
import de.uu.es.avr.model.AvrTask;
import de.uu.es.avr.model.EventSequence;

/**
 * The {@link AbstractAvrDoubleDecoderCorrecting} decodes {@link Genotype}s of
 * the {@link AvrWCRTProblem} encoded by the {@link AvrDoubleCreator} into an
 * {@link EventSequence}. In case the generated {@link EventSequence} is
 * infeasible, a correcting scheme is applied to repair infeasible parts of the
 * {@link Genotype} as well as the {@link EventSequence}.
 * 
 * @author vrichthammer
 *
 */
public abstract class AbstractAvrDoubleDecoderCorrecting extends AbstractAvrDoubleDecoder {

	/**
	 * Allows to integrate various correcting strategies during decoding.
	 *
	 * @param genotype
	 *            the genotype
	 * @param task
	 *            the task requiring correction of its rotation speed
	 * @param index
	 *            the task's position in the event sequence
	 * @param bounds
	 *            the feasible bounds for the task's rotation speed
	 * @return the corrected task
	 */
	public abstract AvrTask correctingStrategy(CompositeGenotype<String, Genotype> genotype, AvrTask task, int index,
			DoubleRange bounds);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.problem.Decoder#decode(org.opt4j.core.Genotype)
	 */
	@Override
	public EventSequence decode(CompositeGenotype<String, Genotype> genotype) {

		EventSequence eventSequence = getEventSequence(genotype);

		if (AvrUtils.isValid(eventSequence, this.avrProblem)) {
			return eventSequence;
		}

		// try to correct and check if correction was successful
		return correct(genotype);
	}

	/**
	 * Finds the position(s) in the {@link Genotype}/{@link EventSequence}
	 * requiring repair. Applies a correcting strategy to try and correct them.
	 * Returns the decoded {@link EventSequence} and whether correction was
	 * successful.
	 *
	 * @param genotype
	 *            the genotype to repair
	 * @return the decoded event sequence, including information on whether
	 *         repair was successful
	 */
	public EventSequence correct(CompositeGenotype<String, Genotype> genotype) {

		List<AvrTask> events = new LinkedList<AvrTask>();

		// i) decodes first task and omega0 (always valid, by encoding)
		int task0_index = ((IntegerGenotype) genotype.get(AvrUtils.TASK_0)).get(0);
		AvrTask first = (avrProblem.getTask(task0_index)).getEvent();

		double omega0_rel = ((DoubleGenotype) genotype.get(AvrUtils.OMEGA_0)).get(0);
		double omega0N = first.getOmega0s().length;
		int omega0_index = (int) (omega0_rel * omega0N);

		// encoding 1.0 should refer to last execution mode in list of rotation
		// speeds
		if (omega0_index == omega0N) {
			omega0_index -= 1;
		}
		first.setOmega(first.getOmega0(omega0_index));
		events.add(first);

		// ii) decodes event1..n with omega1..n while correcting infeasible
		// entries
		DoubleGenotype omegas = genotype.get(AvrUtils.OMEGAS);

		AvrTask curr = first;
		AvrTask next;

		// ii.1) checks all combinations of consecutive rotation speeds
		for (int i = 0; i < omegas.size(); i++) {

			int index = (task0_index + 1 + i) % avrProblem.getTasksetSize();
			next = avrProblem.getTask(index).getEvent();

			DoubleRange boundaries = AvrUtils.findNextRange(curr, next, avrProblem);
			boolean validRange = AvrUtils.checkAndAdaptBoundaries(boundaries, next.minOmega(), next.maxOmega());

			if (!validRange) {
				return new EventSequence(events, false);
			}

			double nextOmega = omegas.get(i);
			boolean inRange = AvrUtils.checkInRange(nextOmega, boundaries);

			// ii.2) corrects next AVR task and genotype, if infeasible
			if (!inRange) {
				next = correctingStrategy(genotype, next, i, boundaries);
			}
			events.add(next);
			curr = next;
		}

		// iii) checks and sets feasibility of decoded event sequence
		EventSequence eventSequence = new EventSequence(events);
		eventSequence.setFeasible(AvrUtils.isValid(eventSequence, avrProblem));

		return eventSequence;
	}
}
