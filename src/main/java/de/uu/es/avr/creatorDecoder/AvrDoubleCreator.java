package de.uu.es.avr.creatorDecoder;

import java.util.Random;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.core.genotype.IntegerGenotype;
import org.opt4j.core.problem.Creator;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

import de.uu.es.avr.AvrUtils;
import de.uu.es.avr.AvrWCRTProblem;

/**
 * The {@link AvrDoubleCreator} creates an absolute encoding of
 * {@link DoubleGenotype}s for the {@link AvrWCRTProblem} with search-space size
 * (length of event sequence) {@code n}.
 * 
 * @author vrichthammer
 * 
 */
public class AvrDoubleCreator implements Creator<CompositeGenotype<String, Genotype>> {

	protected final AvrWCRTProblem avrProblem;

	protected final Random random;
	protected final int n;

	/**
	 * Initializes the {@link AvrDoubleCreator} that generates an absolute
	 * encoding of genotypes for the {@link AvrWCRTProblem}.
	 * 
	 * @param avrProblem
	 *            the AVR problem specification
	 * @param random
	 *            the random number generator
	 * @param n
	 *            the size of the search space / length of event sequence to
	 *            analyse
	 */
	@Inject
	public AvrDoubleCreator(AvrWCRTProblem avrProblem, Random random,
			@Constant(namespace = AvrWCRTProblem.class, value = "n") int n) {
		super();

		this.avrProblem = avrProblem;
		this.random = random;
		this.n = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.problem.Creator#create()
	 */
	@Override
	public CompositeGenotype<String, Genotype> create() {

		CompositeGenotype<String, Genotype> genotype = new CompositeGenotype<String, Genotype>();

		// i.1) select task that starts event sequence
		IntegerGenotype task0 = new IntegerGenotype(0, avrProblem.getTasksetSize() - 1);
		task0.init(random, 1);
		genotype.put(AvrUtils.TASK_0, task0);

		// i.2) optimize rotation speed for first task (relative value, mapped
		// to set of initial speeds during decoding)
		DoubleGenotype omega0 = new DoubleGenotype(0, 1);
		omega0.init(random, 1);
		genotype.put(AvrUtils.OMEGA_0, omega0);

		// ii) optimize omega1..n
		double[] lowerBounds = new double[n - 1];
		double[] upperBounds = new double[n - 1];

		// ii.1) initialize boundaries for events 1..n:
		for (int i = 0; i < lowerBounds.length; i++) {
			lowerBounds[i] = avrProblem.getMinOmega();
			upperBounds[i] = avrProblem.getMaxOmega();
		}
		DoubleBounds bounds = new DoubleBounds(lowerBounds, upperBounds);

		// ii.2) initialize genotype with random absolute rotation-speed values
		// (within boundaries)
		DoubleGenotype omegas = new DoubleGenotype(bounds);
		omegas.init(random, n - 1);
		genotype.put(AvrUtils.OMEGAS, omegas);

		return genotype;
	}
}
