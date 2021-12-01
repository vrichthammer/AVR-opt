package de.uu.es.avr;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.uu.es.avr.AvrUtils.DoubleRange;
import de.uu.es.avr.model.AvrTask;
import de.uu.es.avr.model.EventSequence;

public class AvrUtilsTest {

	@Test
	public void isValidNullTest() {
		assertEquals(false, AvrUtils.isValid(null, null));
	}

	@Test
	public void isValidOneEventTest() {
		EventSequence events = mock(EventSequence.class);
		when(events.size()).thenReturn(1);

		assertEquals(true, AvrUtils.isValid(events, null));
	}

	@Test
	public void isValidTest() {

		AvrWCRTProblem problem = mock(AvrWCRTProblem.class);
		when(problem.getAccMin()).thenReturn(0.0);
		when(problem.getAccMax()).thenReturn(2.0);

		AvrTask task0 = mock(AvrTask.class);
		when(task0.getPhi()).thenReturn(0.0);
		when(task0.getOmega()).thenReturn(2.0);

		AvrTask task1 = mock(AvrTask.class);
		when(task1.getPhi()).thenReturn(3.0);
		when(task1.getOmega()).thenReturn(2.0);
		when(task1.minOmega()).thenReturn(2.0);
		when(task1.maxOmega()).thenReturn(4.0);

		List<AvrTask> taskSet = new LinkedList<AvrTask>();
		taskSet.add(task0);
		taskSet.add(task1);

		EventSequence events = new EventSequence(taskSet, true);

		assertEquals(true, AvrUtils.isValid(events, problem));
	}

	@Test
	public void findNextRangeTest() {
		AvrWCRTProblem problem = mock(AvrWCRTProblem.class);
		when(problem.getAccMin()).thenReturn(0.0);
		when(problem.getAccMax()).thenReturn(2.0);

		AvrTask curr = mock(AvrTask.class);
		when(curr.getPhi()).thenReturn(0.0);
		when(curr.getOmega()).thenReturn(2.0);

		AvrTask next = mock(AvrTask.class);
		when(next.getPhi()).thenReturn(3.0);

		assertEquals(2.0, AvrUtils.findNextRange(curr, next, problem).getLower(), 0.0);
		assertEquals(4.0, AvrUtils.findNextRange(curr, next, problem).getUpper(), 0.0);
	}

	@Test
	public void findBoundTest() {
		double omega = 2.0;
		double acceleration = 2.0;
		double deltaPhi = 3.0;

		assertEquals(AvrUtils.findBound(omega, acceleration, deltaPhi), 4.0, 0.0);
	}

	@Test
	public void inRangeTest() {
		DoubleRange bounds = new DoubleRange(0.0, 1.0);
		assertEquals(true, AvrUtils.checkInRange(0.5, bounds));

		assertEquals(false, AvrUtils.checkInRange(-0.5, bounds));
		assertEquals(false, AvrUtils.checkInRange(1.5, bounds));
	}

	@Test
	public void deltaPhiTest() {
		AvrTask t0 = mock(AvrTask.class);
		when(t0.getPhi()).thenReturn(0.0);

		AvrTask t1 = mock(AvrTask.class);
		when(t1.getPhi()).thenReturn(1.0);

		assertEquals(1.0, AvrUtils.deltaPhi(t0, t1), 0.0);
	}

	@Test
	public void checkAndAdaptBoundariesTest() {
		DoubleRange boundaries = new DoubleRange(0.0, 1.0);
		assertEquals(true, AvrUtils.checkAndAdaptBoundaries(boundaries, 0.5, 0.75));
		assertEquals(0.5, boundaries.getLower(), 0.0);
		assertEquals(0.75, boundaries.getUpper(), 0.0);
	}

	@Test
	public void checkAndAdaptBoundariesTaskTest() {
		AvrTask t = mock(AvrTask.class);
		when(t.minOmega()).thenReturn(0.0);
		when(t.maxOmega()).thenReturn(1.0);

		DoubleRange boundaries = new DoubleRange(0.0, 1.0);

		assertEquals(true, AvrUtils.checkAndAdaptBoundaries(boundaries, t));
	}

	@Test
	public void checkAndAdaptBoundariesIncorrectableTest() {
		DoubleRange boundaries = new DoubleRange(0.0, 1.0);
		assertEquals(false, AvrUtils.checkAndAdaptBoundaries(boundaries, 2.0, -1.0));
	}

	@Test(expected = AssertionError.class)
	public void checkAndAdaptBoundariesInfeasibleTest() {
		DoubleRange boundaries = new DoubleRange(1.0, 0.0);
		AvrUtils.checkAndAdaptBoundaries(boundaries, 0.0, 1.0);

	}
}
