package de.uu.es.avr.io;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uu.es.avr.model.AvrTask;

public class TaskSetReaderTest {

	TaskSetReader reader = new TaskSetReader();

	List<AvrTask> taskset = reader.read("specs/testTaskSet.xml");

	@Test
	public void testRead() {
		assertEquals(taskset.size(), 2);
	}

	@Test
	public void testTaskFunctions() {
		AvrTask task = taskset.get(0);

		assertEquals(task.getPhi(), 0.0, 0.0);
		assertEquals(task.getCfunction().getCs().length, 4, 0.0);
		assertEquals(task.getCfunction().getC(105), 15.0, 0.0);
		assertEquals(task.getCfunction().getC(210), 13.0, 0.0);
		assertEquals(task.getCfunction().getC(315), 12.0, 0.0);
		assertEquals(task.getCfunction().getC(419), 6.0, 0.0);

		assertEquals(task.maxOmega(), 523, 0.0);
		assertEquals(task.minOmega(), 104, 0.0);

		assertEquals(task.getOmega0s().length, 4, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBoundaries() {
		AvrTask task = taskset.get(0);
		task.getCfunction().getC(525);
	}

	@Test
	public void testTask() {
		AvrTask task = taskset.get(0);

		assertEquals(task.getOmega0(0), 209, 0.0);
		assertEquals(task.getId(), "task1");

		task.setOmega(0.5);
		assertEquals(task.getOmega(), 0.5, 0.0);

		AvrTask copy = task.getEvent();
		assertEquals(task.getId(), copy.getId());
		assertEquals(task.getPhi(), copy.getPhi(), 0.0);
		assertEquals(task.getCfunction(), copy.getCfunction());
	}
}
