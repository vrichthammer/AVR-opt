package de.uu.es.avr.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.BeforeClass;
import org.junit.Test;

public class AvrTaskTest {

	private static AvrTask task;

	@BeforeClass
	public static void setup() {
		double phi = 0.5;
		C c = mock(C.class);
		when(c.getBoundaries()).thenReturn(new double[] { 0.0, 1.0, 2.0 });

		task = new AvrTask("id", phi, c);
	}

	@Test
	public void avrTaskSetupTest() {
		assertEquals(task.getId(), "id");
		assertEquals(task.getOmega0(0), 1.0, 0.0);
	}

	@Test
	public void decodeTest() {
		task.setOmega(0.5);
		assertEquals(task.getOmega(), 0.5, 0.0);
	}

	@Test
	public void taskCopyTest() {
		AvrTask copy = task.getEvent();

		assertEquals(copy.getId(), task.getId());
		assertEquals(copy.getPhi(), task.getPhi(), 0.0);
		assertEquals(copy.getCfunction(), task.getCfunction());
	}

	@Test(expected = AssertionError.class)
	public void getOmegaMinTest() {
		task.getOmega0(-1);
	}

	@Test(expected = AssertionError.class)
	public void getOmegaMaxTest() {
		task.getOmega0(100);
	}
}
