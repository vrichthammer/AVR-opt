package de.uu.es.avr.creatorDecoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;

import de.uu.es.avr.AvrWCRTProblem;
import de.uu.es.avr.model.EventSequence;

public class AbstractAvrDoubleDecoderCorrectingTest {

	private static AvrDoubleCreator creator;
	private static AvrCorrMeanDecoder decoder;
	private static AvrWCRTProblem problem;

	@BeforeClass
	public static void setup() {
		problem = new AvrWCRTProblem("specs/testTaskSet.xml", 0.0, 1.0);

		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(1.0);

		int searchSpaceSize = 2;
		creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
		decoder = new AvrCorrMeanDecoder(problem);
	}

	@Test
	public void decodeNoCorrectionTest() {
		EventSequence decoded = decoder.decode(creator.create());

		assertEquals(2, decoded.size());
		assertNotSame(decoded.get(0), decoded.get(1));
		assertEquals("task1", decoded.get(0).getId());
		assertEquals("task2", decoded.get(1).getId());

		assertEquals(523, decoded.get(0).getOmega(), 0.0);
	}

	@Test
	public void decodeCorrectingTest() {
		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.0);

		int searchSpaceSize = 2;
		AvrDoubleCreator creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
		AvrCorrMeanDecoder decoder = new AvrCorrMeanDecoder(problem);

		CompositeGenotype<String, Genotype> genotype = creator.create();
		problem.getTask(1).setOmega(523);

		EventSequence decoded = decoder.decode(genotype);

		assertEquals(2, decoded.size());
		assertNotSame(decoded.get(0), decoded.get(1));
		assertEquals("task1", decoded.get(0).getId());
		assertEquals("task2", decoded.get(1).getId());
	}
}
