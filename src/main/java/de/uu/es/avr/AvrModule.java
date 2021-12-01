package de.uu.es.avr;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.config.annotations.Order;
import org.opt4j.core.problem.Creator;
import org.opt4j.core.problem.Decoder;
import org.opt4j.core.problem.ProblemModule;
import org.opt4j.core.start.Constant;

import de.uu.es.avr.creatorDecoder.AvrCorrBoundDecoder;
import de.uu.es.avr.creatorDecoder.AvrCorrMeanDecoder;
import de.uu.es.avr.creatorDecoder.AvrCorrRandDecoder;
import de.uu.es.avr.creatorDecoder.AvrDoubleCreator;
import de.uu.es.avr.creatorDecoder.AvrInfDecoder;
import de.uu.es.avr.creatorDecoder.AvrRelativeCreatorDecoder;

/**
 * The {@link AvrModule} is used for the configuration of the AVR problem. It
 * contains the length of the event sequence to analyze and dimensions of the
 * problem as well as the {@link Decoder} strategy.
 * 
 * @author vrichthammer
 * 
 */
@Icon(Icons.PROBLEM)
@Info("The AVR problem specification.")
public class AvrModule extends ProblemModule {

	@Order(1)
	@Info("The number of events to analyze / length of event sequence.")
	@Constant(namespace = AvrWCRTProblem.class, value = "n")
	protected int n = 2;

	@Info("The used encoding/decoding")
	@Order(2)
	protected AvrDec decoder = AvrDec.RELATIVE;

	/**
	 * Constructs a {@link QueensModule}.
	 */
	public AvrModule() {
		super();
	}

	/**
	 * The {@link Decoder} strategy for the AVR WCRT problem.
	 * 
	 * @author vrichthammer
	 * 
	 */
	public enum AvrDec {
		/**
		 * Uses the {@link AvrRelativeCreatorDecoder}.
		 */
		RELATIVE,
		/**
		 * Uses the {@link AvrDoubleCreator} with {@link AvrCorrRandomDecoder}.
		 */
		CORR_RANDOM,
		/**
		 * Uses the {@link AvrDoubleCreator} with {@link AvrCorrMidDecoder}.
		 */
		CORR_MEAN,
		/**
		 * Uses the {@link AvrDoubleCreator} with {@link AvrCorrBoundDecoder}.
		 */
		CORR_BOUNDARY,
		/**
		 * Uses the {@link AvrDoubleCreator} with {@link AvrInfDecoder}.
		 */
		INFEASIBLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.start.Opt4JModule#config()
	 */
	@Override
	public void config() {

		Class<? extends Creator<?>> creatorClass = null;
		Class<? extends Decoder<?, ?>> decoderClass = null;

		switch (decoder) {
		case RELATIVE:
			creatorClass = AvrRelativeCreatorDecoder.class;
			decoderClass = AvrRelativeCreatorDecoder.class;
			break;
		case CORR_RANDOM:
			creatorClass = AvrDoubleCreator.class;
			decoderClass = AvrCorrRandDecoder.class;
			break;
		case CORR_BOUNDARY:
			creatorClass = AvrDoubleCreator.class;
			decoderClass = AvrCorrBoundDecoder.class;
			break;
		case CORR_MEAN:
			creatorClass = AvrDoubleCreator.class;
			decoderClass = AvrCorrMeanDecoder.class;
			break;
		case INFEASIBLE:
			creatorClass = AvrDoubleCreator.class;
			decoderClass = AvrInfDecoder.class;
			break;
		default:
			creatorClass = AvrRelativeCreatorDecoder.class;
			decoderClass = AvrRelativeCreatorDecoder.class;
			break;
		}

		bindProblem(creatorClass, decoderClass, AvrEvaluator.class);
	}

	/**
	 * Returns the size of the search space / length of the event sequence to
	 * analyze.
	 *
	 * @return the search-space size
	 */
	public int getN() {
		return n;
	}

	/**
	 * Sets the size of the search space.
	 *
	 * @param n
	 *            the search-space size
	 */
	public void setN(int n) {
		this.n = n;
	}

	/**
	 * Returns the selected encoding/decoding strategy.
	 *
	 * @return the encoding/decoding strategy
	 */
	public AvrDec getDecoder() {
		return decoder;
	}

	/**
	 * Sets the encoding/decoding strategy.
	 *
	 * @param decoder
	 *            the encoding/decoding strategy
	 */
	public void setDecoder(AvrDec decoder) {
		this.decoder = decoder;
	}
}