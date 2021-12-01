package de.uu.es.avr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uu.es.avr.AvrUtils.DoubleRange;

public class DoubleRangeTest {

	@Test
	public void doubleRangeTest() {
		DoubleRange range = new DoubleRange(0.0, 1.0);
		range.setLower(0.5);
		assertEquals(range.getLower(), 0.5, 0.0);

		range.setUpper(2.0);
		assertEquals(range.getUpper(), 2.0, 0.0);
	}

	@Test(expected = AssertionError.class)
	public void doubleRangeInfeasibleTest() {
		DoubleRange range = new DoubleRange(1.0, 0.0);
	}

	@Test(expected = AssertionError.class)
	public void setInfeasibleUpperTest() {
		DoubleRange range = new DoubleRange(0.0, 1.0);
		range.setUpper(-1.0);
	}

	@Test(expected = AssertionError.class)
	public void setInfeasibleLowerTest() {
		DoubleRange range = new DoubleRange(0.0, 1.0);
		range.setLower(2.0);
	}

}
