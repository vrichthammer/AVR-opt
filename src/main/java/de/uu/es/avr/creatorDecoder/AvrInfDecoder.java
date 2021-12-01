package de.uu.es.avr.creatorDecoder;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;

import com.google.inject.Inject;

import de.uu.es.avr.AvrWCRTProblem;
import de.uu.es.avr.model.EventSequence;

/**
 * Decodes {@link Genotype}s of the {@AvrWCRTProblem} encoded using the
 * {@link AvrDoubleCreator} into an {@link EventSeqence} (the corresponding
 * {@link Genotype}). Since the encoding is not feasible by construction and no
 * repair is applied during decoding, the decoded {@link EventSequence} may be
 * infeasible.
 * 
 * @author vrichthammer
 *
 */
public class AvrInfDecoder extends AbstractAvrDoubleDecoder {

	/**
	 * Creates the {@link AvrInfDecoder}.
	 * 
	 * @param avrProblem
	 *            the AVR problem
	 */
	@Inject
	public AvrInfDecoder(AvrWCRTProblem avrProblem) {
		this.avrProblem = avrProblem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.problem.Decoder#decode(org.opt4j.core.Genotype)
	 */
	@Override
	public EventSequence decode(CompositeGenotype<String, Genotype> genotype) {
		return getEventSequence(genotype);
	}
}
