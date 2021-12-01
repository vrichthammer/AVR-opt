package de.uu.es.avr.creatorDecoder;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.core.genotype.IntegerGenotype;
import org.opt4j.core.problem.Creator;
import org.opt4j.core.problem.Decoder;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

import de.uu.es.avr.AvrUtils;
import de.uu.es.avr.AvrUtils.DoubleRange;
import de.uu.es.avr.AvrWCRTProblem;
import de.uu.es.avr.model.AvrTask;
import de.uu.es.avr.model.EventSequence;

/**
 * The {@link AvrRelativeCreatorDecoder} encodes {@link Genotype}s for the
 * {@link AvrWCRTProblem} using a relative encoding strategy that guarantees
 * feasiblity of the created {@link Genotype}s by construction. Instead of
 * directy encoding absolute rotation-speed values, a double value \in [0,1] is
 * used for all events (except the first); this value is mapped to the feasible
 * rotation-speed range of the event during decoding that can be determined once
 * its preceding event has been decoded.
 * 
 * @author vrichthammer
 *
 */
public class AvrRelativeCreatorDecoder implements Creator<CompositeGenotype<String, Genotype>>,
		Decoder<CompositeGenotype<String, Genotype>, EventSequence> {

	protected final double[] lowerBounds;
	protected final double[] upperBounds;

	protected final AvrWCRTProblem avrProblem;

	protected final Random random;
	protected final int n;

	/**
	 * Creates the {@link AvrRelativeCreatorDecoder}.
	 * 
	 * @param avrProblem
	 *            the AVR problem
	 * @param random
	 *            the random number generator
	 * @param n
	 *            the search-space size / length of the event sequence to
	 *            analyze
	 */
	@Inject
	public AvrRelativeCreatorDecoder(AvrWCRTProblem avrProblem, Random random,
			@Constant(namespace = AvrWCRTProblem.class, value = "n") int n) {

		this.avrProblem = avrProblem;
		this.random = random;
		this.n = n;

		this.lowerBounds = new double[n - 1];
		this.upperBounds = new double[n - 1];

		// initializes boundaries for events 1..n:
		// for relative encoding: boundaries [0; 1]
		for (int i = 0; i < lowerBounds.length; i++) {
			lowerBounds[i] = 0;
			upperBounds[i] = 1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.problem.Creator#create()
	 */
	@Override
	public CompositeGenotype<String, Genotype> create() {

		CompositeGenotype<String, Genotype> genotype = new CompositeGenotype<String, Genotype>();

		// i.1) selects task that starts event sequence
		IntegerGenotype task0 = new IntegerGenotype(0, avrProblem.getTasksetSize() - 1);
		task0.init(random, 1);
		genotype.put(AvrUtils.TASK_0, task0);

		// i.2) optimizes rotation speed for first task (map to discrete values)
		DoubleGenotype omega0 = new DoubleGenotype(0, 1);
		omega0.init(random, 1);
		genotype.put(AvrUtils.OMEGA_0, omega0);

		// ii) optimizes omega1..n using relative encoding [0,1] for each event
		DoubleBounds bounds = new DoubleBounds(lowerBounds, upperBounds);

		DoubleGenotype omegas = new DoubleGenotype(bounds);
		omegas.init(random, n - 1);
		genotype.put(AvrUtils.RELATIVE, omegas);

		return genotype;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.problem.Decoder#decode(org.opt4j.core.Genotype)
	 */
	public EventSequence decode(CompositeGenotype<String, Genotype> genotype) {

		// decodes event sequence (with concrete rotation speeds) from relative
		// genotype: phenotype is valid by construction
		List<AvrTask> eventSequence = new LinkedList<AvrTask>();

		// i.1) decodes first event
		int task0_index = ((IntegerGenotype) genotype.get(AvrUtils.TASK_0)).get(0);
		AvrTask event0 = (avrProblem.getTask(task0_index)).getEvent();

		// i.2) decodes first rotation speed omega0 from relative encoding
		double omega0_rel = ((DoubleGenotype) genotype.get(AvrUtils.OMEGA_0)).get(0);
		int omega0_count = event0.getOmega0s().length;

		int indOmega0 = (int) (omega0_rel * omega0_count);

		// relative encoding 1.0 should refer to last execution mode in list of
		// rotation speeds
		if (indOmega0 == omega0_count) {
			indOmega0 -= 1;
		}

		event0.setOmega(event0.getOmega0(indOmega0));
		eventSequence.add(event0);

		// ii) decodes event1..n and rotation speeds omega1..n
		DoubleGenotype omegas = genotype.get(AvrUtils.RELATIVE);
		AvrTask currEvent = event0;

		for (int i = 0; i < omegas.size(); i++) {

			int index = (task0_index + 1 + i) % avrProblem.getTasksetSize();
			AvrTask nextEvent = avrProblem.getTask(index).getEvent();

			// finds and verifies rotation-speed boundaries for nextEvent
			DoubleRange range = AvrUtils.findNextRange(currEvent, nextEvent, avrProblem);
			AvrUtils.checkAndAdaptBoundaries(range, nextEvent.minOmega(), nextEvent.maxOmega());

			double lower = range.getLower();
			double upper = range.getUpper();

			double factor = omegas.get(i);
			double decodedOmega = lower + (upper - lower) * factor;

			// minimally shifts decoded omega, if it lands on the lower bound
			// (must be \in (lower, upper])
			if (decodedOmega == lower) {
				decodedOmega += Double.MIN_VALUE;
			}

			// stores decoded event with rotation speed
			nextEvent.setOmega(decodedOmega);
			eventSequence.add(nextEvent);

			currEvent = nextEvent;
		}

		// returns feasible event sequence (by construction)
		return new EventSequence(eventSequence, true);
	}
}
