package de.uu.es.avr.creatorDecoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleGenotype;

import de.uu.es.avr.AvrUtils.DoubleRange;
import de.uu.es.avr.AvrWCRTProblem;
import de.uu.es.avr.model.AvrTask;

public class AvrCorrBoundDecoderTest {

	private static AvrWCRTProblem problem;

	@BeforeClass
	public static void setup() {
		problem = new AvrWCRTProblem("specs/testTaskSet.xml", 0.0, 1.0);
	}

	@Test
	public void correctingStrategyUpperTest() {

		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(1.0);

		int searchSpaceSize = 2;
		AvrDoubleCreator creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
		AvrCorrBoundDecoder decoder = new AvrCorrBoundDecoder(problem);

		int testIndex = 0;
		CompositeGenotype<String, Genotype> genotype = creator.create();
		problem.getTask(1).setOmega(523);

		// correct to lower boundary
		double upperBound = 1.0;

		AvrTask decoded = decoder.correctingStrategy(genotype, problem.getTask(1), testIndex,
				new DoubleRange(0, upperBound));

		// check that phenotype is corrected
		assertEquals(upperBound, decoded.getOmega(), 0);

		// check that genotype is corrected
		DoubleGenotype decodedGenotype = (DoubleGenotype) genotype.get("OMEGAS");
		double correctedEntry = decodedGenotype.get(testIndex);
		assertEquals(upperBound, correctedEntry, 0.0);
	}

	@Test
	public void correctingStrategyLowerTest() {

		// init
		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.0);

		int searchSpaceSize = 2;
		AvrDoubleCreator creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
		AvrCorrBoundDecoder decoder = new AvrCorrBoundDecoder(problem);

		int testIndex = 0;
		CompositeGenotype<String, Genotype> genotype = creator.create();
		problem.getTask(1).setOmega(104);

		// correct to lower boundary
		double lowerBound = 105.0;

		AvrTask decoded = decoder.correctingStrategy(genotype, problem.getTask(1), testIndex,
				new DoubleRange(lowerBound, 110));

		// check that phenotype is corrected
		assertEquals(lowerBound, decoded.getOmega(), 0);

		// check that genotype is corrected
		DoubleGenotype decodedGenotype = (DoubleGenotype) genotype.get("OMEGAS");
		double correctedEntry = decodedGenotype.get(testIndex);
		assertEquals(lowerBound, correctedEntry, 0.0);
	}

	@Test
	public void noCorrectionTest() {

		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(1.0);

		int searchSpaceSize = 2;
		AvrDoubleCreator creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
		AvrCorrBoundDecoder decoder = new AvrCorrBoundDecoder(problem);

		// set omega, so that no correction required
		problem.getTask(1).setOmega(523);

		AvrTask decoded = decoder.correctingStrategy(creator.create(), problem.getTask(1), 0, new DoubleRange(0, 523));

		assertEquals(523.0, decoded.getOmega(), 0.0);
	}
}
