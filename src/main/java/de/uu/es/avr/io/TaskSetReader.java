package de.uu.es.avr.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.uu.es.avr.model.AvrTask;
import de.uu.es.avr.model.C;

/**
 * Imports a task set from an xml specification.
 * 
 * @author vrichthammer
 *
 */
public class TaskSetReader {

	/**
	 * Reads task set from a file.
	 *
	 * @param filename
	 *            the file name
	 * @return the task set
	 */
	public List<AvrTask> read(String filename) {
		return read(new File(filename));
	}

	/**
	 * Reads task set from a file.
	 *
	 * @param file
	 *            the file
	 * @return the task set
	 */
	public List<AvrTask> read(File file) {
		try {
			return read(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads task set from an input stream.
	 *
	 * @param in
	 *            the input stream
	 * @return the task set
	 */
	public List<AvrTask> read(InputStream in) {
		try {
			nu.xom.Builder parser = new nu.xom.Builder();
			nu.xom.Document doc = parser.build(in);

			nu.xom.Element eSpec = doc.getRootElement();

			return toTaskSet(eSpec);
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * Converts an XML element to a task set.
	 *
	 * @param eSpecification
	 *            the XML element
	 * @return the task set
	 */
	public List<AvrTask> toTaskSet(nu.xom.Element eSpecification) {
		try {

			List<AvrTask> taskset = new LinkedList<AvrTask>();

			nu.xom.Elements eTasks = eSpecification.getChildElements("task");

			for (nu.xom.Element eTask : iterable(eTasks)) {
				AvrTask task = toTask(eTask);
				taskset.add(task);
			}

			// check validity of task set (tasks ordered by offsets)
			AvrTask prev;
			AvrTask curr;
			for (int i = 0; i < taskset.size() - 1; i++) {
				prev = taskset.get(i);
				curr = taskset.get(i + 1);
				assert prev.getPhi() < curr.getPhi() : "specification error: tasks not ordered by offset!";
			}

			return taskset;

		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * Converts a task from xml element to an {@link AvrTask}.
	 *
	 * @param eTask
	 *            the xml element
	 * @return the AVR task
	 * 
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	protected AvrTask toTask(nu.xom.Element eTask)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {

		nu.xom.Elements ePhi = eTask.getChildElements("phi");
		assert ePhi.size() == 1 : "specification error: multiple values for phi specified!";

		double phi = Double.parseDouble((ePhi.get(0)).getAttributeValue("value"));

		nu.xom.Elements eC = eTask.getChildElements("c");
		assert eC.size() == 1 : "specification error: multiple values for c specified!";

		String cs = (eC.get(0)).getAttributeValue("value");
		double[] cArray = toDoubleArray(cs);

		nu.xom.Elements eBoundaries = eTask.getChildElements("boundaries");
		assert eBoundaries.size() == 1 : "specification error: multiple values for boundaries list specified!";

		String boundaries = (eBoundaries.get(0)).getAttributeValue("value");
		double[] boundariesArray = toDoubleArray(boundaries);

		C c = new C(cArray, boundariesArray);

		String id = eTask.getAttributeValue("id");
		return new AvrTask(id, phi, c);
	}

	/**
	 * Converts a {@link String} of doubles into an array.
	 *
	 * @param doubles
	 *            the doubles
	 * @return the double array
	 */
	private double[] toDoubleArray(String doubles) {

		String[] elements = doubles.split("[|]");
		double[] doubleArray = new double[elements.length];

		for (int i = 0; i < elements.length; i++) {
			doubleArray[i] = Double.parseDouble(elements[i]);
		}
		return doubleArray;
	}

	/**
	 * Transforms an {@code Elements} object into a set of iterable
	 * {@code Element} objects.
	 *
	 * @param elements
	 *            the elements object
	 * @return the iterable element objects
	 */
	public static Iterable<nu.xom.Element> iterable(final nu.xom.Elements elements) {
		return new Iterable<nu.xom.Element>() {
			@Override
			public Iterator<nu.xom.Element> iterator() {
				return new Iterator<nu.xom.Element>() {
					int c = 0;

					@Override
					public boolean hasNext() {
						return elements.size() > c;
					}

					@Override
					public nu.xom.Element next() {
						return elements.get(c++);
					}

					@Override
					public void remove() {
						throw new RuntimeException("invalid operation: remove");
					}
				};
			}
		};
	}
}
