package de.uu.es.avr.creatorDecoder;

import java.util.Random;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleGenotype;

import com.google.inject.Inject;

import de.uu.es.avr.AvrUtils;
import de.uu.es.avr.AvrUtils.DoubleRange;
import de.uu.es.avr.AvrWCRTProblem;
import de.uu.es.avr.model.AvrTask;

/**
 * The {@link AvrCorrRandDecoder} repairs a {@link Genotype} by correcting any
 * infeasible rotation speed to a random value within the feasible
 * rotation-speed range.
 * 
 * @author vrichthammer
 *
 */
public class AvrCorrRandDecoder extends AbstractAvrDoubleDecoderCorrecting {

	Random random;

	/**
	 * Creates the {@link AvrCorrRandDecoder}.
	 * 
	 * @param avrProblem
	 *            the AVR problem
	 * @param random
	 *            the random number generator
	 */
	@Inject
	public AvrCorrRandDecoder(AvrWCRTProblem avrProblem, Random random) {
		this.avrProblem = avrProblem;
		this.random = random;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uu.avrOpt.creatorDecoder.AbstractAvrDoubleDecoderCorrecting#
	 * correctingStrategy(org.opt4j.genotype.CompositeGenotype,
	 * uu.avrOpt.model.AvrTask, int, uu.avrOpt.AvrUtils.DoubleRange)
	 */
	@Override
	public AvrTask correctingStrategy(CompositeGenotype<String, Genotype> genotype, AvrTask event, int index,
			DoubleRange boundaries) {

		double lower = boundaries.getLower();
		double upper = boundaries.getUpper();

		DoubleGenotype omegas = genotype.get(AvrUtils.OMEGAS);
		double omega = omegas.get(index);

		// correct if genotype entry out of valid range
		if (omega <= lower || omega > upper) {

			System.out.println("if");
			// correct to random value in valid interval
			double factor = random.nextDouble();
			double corrected = lower + (upper - lower) * factor;

			// minimally shift corrected value, so that it is \in (lower, upper]
			if (factor == 0) {
				corrected += Double.MIN_VALUE;
			}

			// correct genotype and event
			omegas.set(index, corrected);
			event.setOmega(corrected);
		}
		return event;
	}
}
