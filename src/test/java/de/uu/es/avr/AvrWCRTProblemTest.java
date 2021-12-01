package de.uu.es.avr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AvrWCRTProblemTest {

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
