package de.uu.es.avr.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class EventSequenceTest {

	private static List<AvrTask> taskSet;

	@BeforeClass
	public static void setup() {
		AvrTask task = mock(AvrTask.class);
		when(task.getId()).thenReturn("id1");
		when(task.getOmega()).thenReturn(1.0);

		List<AvrTask> tasks = new LinkedList<AvrTask>();
		tasks.add(task);

		taskSet = tasks;
	}

	@Test
	public void eventSequenceFeasibleTest() {
		EventSequence eventSequence = new EventSequence(taskSet, true);
		assertEquals(eventSequence.isFeasible(), true);

		assertEquals(eventSequence.toString(), "id1: 1.0 ");

		eventSequence.remove();
		assertEquals(eventSequence.toString(), "empty");
	}

	@Test
	public void eventSequenceInfeasibleTest() {
		EventSequence eventSequence = new EventSequence(taskSet);
		eventSequence.setFeasible(false);

		assertEquals(eventSequence.isFeasible(), false);
		assertEquals(eventSequence.toString(), "infeasible");
	}
}