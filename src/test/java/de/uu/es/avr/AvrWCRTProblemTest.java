package de.uu.es.avr;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uu.es.avr.model.AvrTask;

public class AvrWCRTProblemTest {

	private static AvrTask task0;
	private static AvrTask task1;

	private static List<AvrTask> taskSet;

	@Test
	public void minOmegaTest() {
		AvrWCRTProblem problem = new AvrWCRTProblem("specs/testTaskSet.xml", 0.0, 1.0);
		assertEquals(104.0, problem.getMinOmega(), 0.0);
	}

	@Test
	public void maxOmegaTest() {
		AvrWCRTProblem problem = new AvrWCRTProblem("specs/testTaskSet.xml", 0.0, 1.0);
		assertEquals(523.0, problem.getMaxOmega(), 0.0);
	}

	@Test(expected = AssertionError.class)
	public void infeasibleInitTest() {
		new AvrWCRTProblem("specs/testTaskSet.xml", 1.0, 0.0);
	}
}
