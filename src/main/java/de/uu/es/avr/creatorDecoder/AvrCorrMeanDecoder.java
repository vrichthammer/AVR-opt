package de.uu.es.avr.creatorDecoder;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleGenotype;

import com.google.inject.Inject;

import de.uu.es.avr.AvrUtils;
import de.uu.es.avr.AvrUtils.DoubleRange;
import de.uu.es.avr.AvrWCRTProblem;
import de.uu.es.avr.model.AvrTask;

/**
 * The {@link AvrCorrMeanDecoder} repairs a {@link Genotype} by correcting any
 * infeasible rotation speed to the mean of the feasible rotation-speed range.
 * 
 * @author vrichthammer
 *
 */
public class AvrCorrMeanDecoder extends AbstractAvrDoubleDecoderCorrecting {

	/**
	 * Constructs the {@AvrCorrMidDecoder}.
	 * 
	 * @param avrProblem
	 *            the AVR problem
	 */
	@Inject
	public AvrCorrMeanDecoder(AvrWCRTProblem avrProblem) {
		this.avrProblem = avrProblem;
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

			// correct to mean of valid interval
			double corrected = lower + (upper - lower) * 0.5;

			// correct genotype and event
			omegas.set(index, corrected);
			event.setOmega(corrected);
		}
		return event;
	}
}
