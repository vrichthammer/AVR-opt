package de.uu.es.avr.creatorDecoder;

import java.util.LinkedList;
import java.util.List;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.core.genotype.IntegerGenotype;
import org.opt4j.core.problem.Decoder;

import de.uu.es.avr.AvrUtils;
import de.uu.es.avr.AvrWCRTProblem;
import de.uu.es.avr.model.AvrTask;
import de.uu.es.avr.model.EventSequence;

/**
 * The {@link AbstractAvrDoubleDecoder} decodes {@link Genotype}s of the
 * {@link AvrWCRTProblem} encoded by the {@link AvrDoubleCreator} into an
 * {@link EventSequence} (the corresponding {@link Phentoype}.
 * 
 * @author vrichthammer
 *
 */
public abstract class AbstractAvrDoubleDecoder implements Decoder<CompositeGenotype<String, Genotype>, EventSequence> {

	protected AvrWCRTProblem avrProblem;

	/**
	 * Transforms a double encoding of absolute rotation speed values into an
	 * {@link EventSequence}. Checks and annotates, whether the genotype is
	 * feasible for the given {@link AvrWCRTProblem}.
	 * 
	 * @param genotype
	 *            the genotype to transform
	 * @return the event sequence
	 */
	public EventSequence getEventSequence(CompositeGenotype<String, Genotype> genotype) {

		List<AvrTask> eventSequence = new LinkedList<AvrTask>();

		// i.1) decode first task
		int task0_index = ((IntegerGenotype) genotype.get(AvrUtils.TASK_0)).get(0);
		AvrTask first = (avrProblem.getTask(task0_index)).getEvent();

		// i.2) decode omega0
		double omega0_rel = ((DoubleGenotype) genotype.get(AvrUtils.OMEGA_0)).get(0);
		double omega0_count = first.getOmega0s().length;

		int omega0_index = (int) (omega0_rel * omega0_count);

		// an encoding 1.0 should refer to last execution mode in list of
		// rotation speeds
		if (omega0_index == omega0_count) {
			omega0_index -= 1;
		}

		// i.3) decode and store first event with rotation speed
		first.setOmega(first.getOmega0(omega0_index));
		eventSequence.add(first);

		// ii) decode and store events_1..n with omega_1..n
		DoubleGenotype omegas = genotype.get(AvrUtils.OMEGAS);

		for (int i = 0; i < omegas.size(); i++) {
			int index = (task0_index + 1 + i) % avrProblem.getTasksetSize();

			AvrTask next = avrProblem.getTask(index).getEvent();
			next.setOmega(omegas.get(i));

			eventSequence.add(next);
		}

		// iii) check and set feasibility of event sequence
		EventSequence sequence = new EventSequence(eventSequence);
		sequence.setFeasible(AvrUtils.isValid(sequence, avrProblem));

		return sequence;
	}
}
