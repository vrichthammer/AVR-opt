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

public class AvrCorrRandDecoderTest {

	private static AvrWCRTProblem problem;

	@BeforeClass
	public static void setup() {
		problem = new AvrWCRTProblem("specs/testTaskSet.xml", 0.0, 1.0);
	}

	@Test
	public void correctingStrategyTest() {

		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(1.0);

		int searchSpaceSize = 2;
		AvrDoubleCreator creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
		AvrCorrRandDecoder decoder = new AvrCorrRandDecoder(problem, random);

		int testIndex = 0;
		CompositeGenotype<String, Genotype> genotype = creator.create();
		problem.getTask(1).setOmega(523);

		AvrTask decoded = decoder.correctingStrategy(genotype, problem.getTask(1), testIndex, new DoubleRange(0, 1));

		// check that phenotype is corrected
		assertEquals(1.0, decoded.getOmega(), 0);

		// check that genotype is corrected
		DoubleGenotype decodedGenotype = (DoubleGenotype) genotype.get("OMEGAS");
		double correctedEntry = decodedGenotype.get(testIndex);
		assertEquals(1.0, correctedEntry, 0.0);
	}

	@Test
	public void correctingStrategyExtremaTest() {

		// init
		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.0);

		int searchSpaceSize = 2;
		AvrDoubleCreator creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
		AvrCorrRandDecoder decoder = new AvrCorrRandDecoder(problem, random);

		int testIndex = 0;
		CompositeGenotype<String, Genotype> genotype = creator.create();
		problem.getTask(1).setOmega(104);

		AvrTask decoded = decoder.correctingStrategy(genotype, problem.getTask(1), testIndex, new DoubleRange(0, 1));

		// check that phenotype is corrected
		assertEquals(0.0 + Double.MIN_VALUE, decoded.getOmega(), 0);

		// check that genotype is corrected
		DoubleGenotype decodedGenotype = (DoubleGenotype) genotype.get("OMEGAS");
		double correctedEntry = decodedGenotype.get(testIndex);
		assertEquals(0.0 + Double.MIN_VALUE, correctedEntry, 0.0);
	}

	@Test
	public void noCorrectionTest() {

		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(1.0);

		int searchSpaceSize = 2;
		AvrDoubleCreator creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
		AvrCorrRandDecoder decoder = new AvrCorrRandDecoder(problem, random);

		// set omega, so that no correction required
		problem.getTask(1).setOmega(523);

		AvrTask decoded = decoder.correctingStrategy(creator.create(), problem.getTask(1), 0, new DoubleRange(0, 523));

		assertEquals(523.0, decoded.getOmega(), 0.0);
	}
}
