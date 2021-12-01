package de.uu.es.avr.creatorDecoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;

import de.uu.es.avr.AvrWCRTProblem;

public class AvrDoubleCreatorTest {

	private static AvrDoubleCreator creator;
	private static AvrWCRTProblem problem;

	@BeforeClass
	public static void setup() {
		problem = new AvrWCRTProblem("specs/testTaskSet.xml", 0.0, 1.0);

		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(1.0);

		int searchSpaceSize = 2;
		creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
	}

	@Test
	public void createTest() {
		CompositeGenotype<String, Genotype> genotype = creator.create();

		assertEquals(3, genotype.size());
		assertEquals(true, genotype.keySet().contains("OMEGAS"));
		assertEquals(true, genotype.keySet().contains("OMEGA_0"));
		assertEquals(true, genotype.keySet().contains("TASK_0"));
	}
}
