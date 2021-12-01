package de.uu.es.avr.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PeriodicTaskTest {

	@Test
	public void getEventTest() {
		double executionTime = 1.0;
		double period = 2.0;
		double deadline = 2.0;

		PeriodicTask task = new PeriodicTask("id", executionTime, period, deadline);
		PeriodicTask event = task.getEvent();

		assertEquals(task.getId(), event.getId());
		assertEquals(task.getExecutionTime(), event.getExecutionTime(), 0.0);
		assertEquals(task.getPeriod(), event.getPeriod(), 0.0);
		assertEquals(task.getDeadline(), event.getDeadline(), 0.0);
	}
}
