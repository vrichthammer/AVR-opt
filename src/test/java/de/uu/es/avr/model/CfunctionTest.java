package de.uu.es.avr.model;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class CfunctionTest {

	private static C cfunction;
	private static double[] boundaries = { 0.0, 0.5, 0.75 };
	private static double[] cs = { 15.0, 13.0 };

	@BeforeClass
	public static void setup() {
		cfunction = new C(cs, boundaries);
	}

	@Test
	public void determineIndexTest() {
		assertEquals(cfunction.getC(0.6), 13.0, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void determineIndexInfTest() {
		cfunction.getC(-1.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void determineIndexInfeasibleTest() {
		cfunction.getC(1.0);
	}

	@Test(expected = AssertionError.class)
	public void infeasibleSetupTest() {
		C c = new C(null, null);
	}

	@Test(expected = AssertionError.class)
	public void infeasibleCSetupTest() {
		C c = new C(null, boundaries);
	}

	@Test(expected = AssertionError.class)
	public void infeasibleBoundSetupTest() {
		C c = new C(cs, null);
	}

	@Test(expected = AssertionError.class)
	public void infeasibleSizeSetupTest() {

		double[] bounds = new double[0];
		C c = new C(cs, bounds);
	}

	@Test(expected = AssertionError.class)
	public void incompatibleSizesTest() {

		double[] bounds = new double[2];
		C c = new C(cs, bounds);
	}

}
