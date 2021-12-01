package de.uu.es.avr.creatorDecoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uu.es.avr.AvrWCRTProblem;
import de.uu.es.avr.model.EventSequence;

public class AvrInfDecoderTest {

	private static AvrDoubleCreator creator;
	private static AvrInfDecoder decoder;
	private static AvrWCRTProblem problem;

	@BeforeClass
	public static void setup() {
		problem = new AvrWCRTProblem("specs/testTaskSet.xml", 0.0, 1.0);

		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(1.0);

		int searchSpaceSize = 2;
		creator = new AvrDoubleCreator(problem, random, searchSpaceSize);
		decoder = new AvrInfDecoder(problem);
	}

	@Test
	public void decodeTest() {
		EventSequence decoded = decoder.decode(creator.create());

		assertEquals(2, decoded.size());
		assertNotSame(decoded.get(0), decoded.get(1));
		assertEquals("task1", decoded.get(0).getId());
		assertEquals("task2", decoded.get(1).getId());

		assertEquals(523, decoded.get(0).getOmega(), 0.0);
	}

	@Test
	public void decodeExtremaTest() {
		Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.0);

		int searchSpaceSize = 2;
		AvrRelativeCreatorDecoder creator = new AvrRelativeCreatorDecoder(problem, random, searchSpaceSize);

		EventSequence decoded = creator.decode(creator.create());

		assertEquals("task1", decoded.get(0).getId());
		assertEquals("task2", decoded.get(1).getId());

		assertEquals(209, decoded.get(0).getOmega(), 0.0);
	}
}
