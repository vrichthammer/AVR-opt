package de.uu.es.avr;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.opt4j.core.DoubleValue;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Value;

import de.uu.es.avr.model.AvrTask;
import de.uu.es.avr.model.C;
import de.uu.es.avr.model.EventSequence;

public class AvrEvaluatorTest {

	private static AvrEvaluator evaluator;
	private static AvrTask event1;
	private static AvrTask event2;

	private static double resultMu;
	private static double resultC;
	private static double deltaPhi;

	private static List<AvrTask> taskSet;
	private static EventSequence events;

	@BeforeClass
	public static void setup() {

		AvrWCRTProblem problem = mock(AvrWCRTProblem.class);
		when(problem.getAccMax()).thenReturn(100.0);
		when(problem.getAccMin()).thenReturn(-100.0);

		double phi1 = 0.0;
		double phi2 = 1.0;

		deltaPhi = Math.PI * (phi2 - phi1);

		C cfunction = mock(C.class);
		when(cfunction.getBoundaries()).thenReturn(new double[] { 0.0, 1.0 });
		when(cfunction.getC(Mockito.anyDouble())).thenReturn(1.0);

		event1 = new AvrTask("id1", phi1, cfunction);
		event1.setOmega(1.0);

		event2 = new AvrTask("id2", phi2, cfunction);
		event2.setOmega(2.0);

		resultMu = (-30 + 20 * Math.sqrt(100 * Math.PI + 2.5));
		resultC = 2.0;

		evaluator = new AvrEvaluator(problem);

		taskSet = new LinkedList<AvrTask>();
		taskSet.add(event1);
		taskSet.add(event2);

		events = new EventSequence(taskSet, true);
	}

	@Test
	public void muTest() {
		assertEquals(resultMu, evaluator.mu(events), 0.0001);
	}

	@Test
	public void muPartTest() {
		assertEquals(resultMu, evaluator.mu(deltaPhi, event1, event2), 0.0001);
	}

	@Test
	public void sum_cTest() {
		assertEquals(resultC, evaluator.sum_c(events), 0.0);
	}

	@Test
	public void evaluateTest() {
		Objectives objectives = evaluator.evaluate(new EventSequence(taskSet, true));
		assertEquals(2, objectives.size());
	}

	@Test
	public void evaluateInfeasibleTest() {
		EventSequence events = mock(EventSequence.class);
		when(events.isFeasible()).thenReturn(false);

		Objectives objectives = evaluator.evaluate(events);

		for (Entry<Objective, Value<?>> obj : objectives) {
			assertEquals(new DoubleValue(null), obj.getValue());
		}
	}
}
