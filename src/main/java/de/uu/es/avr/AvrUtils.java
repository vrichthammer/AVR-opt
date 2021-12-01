package de.uu.es.avr;

import de.uu.es.avr.model.AvrTask;
import de.uu.es.avr.model.EventSequence;

/**
 * Contains utility methods and constants for the optimization and analysis of
 * the {@link AvrWCRTProblem}.
 * 
 * @author vrichthammer
 *
 */
public class AvrUtils {

	// constants for encodings
	public static final String TASK_0 = "TASK_0";
	public static final String OMEGA_0 = "OMEGA_0";
	public static final String OMEGAS = "OMEGAS";
	public static final String RELATIVE = "RELATIVE";

	/**
	 * Checks whether a sequence of rotation speeds is feasible for a given
	 * {@link AvrProblem}. An {@link EventSequence} is feasible, if any two
	 * consecutive rotation speeds are possible within physical acceleration
	 * limits of the system.
	 *
	 * @param eventSequence
	 *            the decoded event sequence with fixed rotation speed per event
	 * @param avrProblem
	 *            the AVR problem
	 * @return true if the sequence of rotation speeds in the event sequence is
	 *         feasible
	 */
	public static boolean isValid(EventSequence eventSequence, AvrWCRTProblem avrProblem) {

		if (eventSequence == null) {
			return false;
		}

		if (eventSequence.size() <= 1) {
			return true;
		}

		AvrTask curr = eventSequence.get(0);
		AvrTask next;

		// check all combinations of consecutive rotation speeds (at least two
		// events in sequence)
		for (int i = 1; i < eventSequence.size(); i++) {
			next = eventSequence.get(i);

			DoubleRange boundaries = findNextRange(curr, next, avrProblem);
			boolean validRange = checkAndAdaptBoundaries(boundaries, next.minOmega(), next.maxOmega());

			if (!validRange) {
				return false;
			}

			boolean inRange = checkInRange(next.getOmega(), boundaries);

			if (!inRange) {
				return false;
			}
			curr = next;
		}
		// all combinations of consecutive rotation speeds valid
		return true;
	}

	/**
	 * Finds the next feasible range of rotation speeds, given an event and the
	 * type of the subsequent task for an {@link AvrWCRTProblem}. The
	 * feasibility depends on the rotation speed at the event's occurrence, the
	 * type of subsequent task, and the minimal and maximal acceleration
	 * possible in the system.
	 *
	 *
	 * @param curr
	 *            the current event
	 * @param next
	 *            the subsequent event
	 * @param avrProblem
	 *            the AVR problem specification
	 * @return the feasible range of subsequent rotation speeds
	 */
	public static DoubleRange findNextRange(AvrTask curr, AvrTask next, AvrWCRTProblem avrProblem) {

		double omega = curr.getOmega();
		double deltaPhi = AvrUtils.deltaPhi(curr, next);

		double lower = findBound(omega, avrProblem.getAccMin(), deltaPhi);
		double upper = findBound(omega, avrProblem.getAccMax(), deltaPhi);

		return new DoubleRange(lower, upper);
	}

	/**
	 * Returns the rotation-speed bound for the subsequent event, given a
	 * current rotation speed, the minimal/maximal acceleration, and the release
	 * angle between the two events.
	 * 
	 * The bound is calculated using angular acceleration: bound = sqrt(omega^2
	 * + 2 * acceleration * deltaPhi)
	 *
	 * @param omega
	 *            the current rotation speed
	 * @param acceleration
	 *            the minimal/maximal acceleration
	 * @param deltaPhi
	 *            the release angle between analyzed events
	 * @return the bound
	 */
	public static double findBound(double omega, double acceleration, double deltaPhi) {

		double result = omega * omega + 2 * acceleration * deltaPhi;
		result = Math.sqrt(result);

		return result;
	}

	/**
	 * Returns true, if a rotation speed {@code omega} lies within the given
	 * boundaries.
	 *
	 * @param omega
	 *            the rotation speed
	 * @param boundaries
	 *            the boundaries
	 * @return true, if rotation speed is within the boundaries
	 */
	public static boolean checkInRange(double omega, DoubleRange boundaries) {
		return omega >= boundaries.getLower() && omega <= boundaries.getUpper();
	}

	/**
	 * Checks whether given rotation-speed bounds are feasible for an
	 * {@link AvrTask}. Adjusts the bounds to the {@link AvrTask}'s
	 * minimal/maximal feasible rotation speeds, if necessary.
	 *
	 * @param boundaries
	 *            the bounds
	 * @param task
	 *            the AVR task
	 * @return true if feasible bounds could be set
	 */
	public static boolean checkAndAdaptBoundaries(DoubleRange boundaries, AvrTask task) {
		return checkAndAdaptBoundaries(boundaries, task.minOmega(), task.maxOmega());
	}

	/**
	 * Checks whether rotation-speed bounds are feasible given a fixed minimal
	 * and maximal rotation speed. Adjusts the bounds, if necessary. Returns,
	 * whether feasible bounds were constructed.
	 *
	 * @param boundaries
	 *            the bounds
	 * @param minOmega
	 *            the minimal rotation speed
	 * @param maxOmega
	 *            the maximal rotation speed
	 * @return true if feasible bounds could be set
	 */
	public static boolean checkAndAdaptBoundaries(DoubleRange boundaries, double minOmega, double maxOmega) {

		double lower = boundaries.getLower();
		double upper = boundaries.getUpper();

		assert (lower <= upper) : "checkAndAdaptBoundaries: something went wrong: lowerBoundary > upperBoundary";

		// check for infeasibility: boundaries must be \in (minOmega; maxOmega]
		if (lower > maxOmega || upper <= minOmega) {
			return false;
		}

		// adapt boundaries (calculates intersection of valid ranges)
		boundaries.setLower(Math.max(lower, minOmega));
		boundaries.setUpper(Math.min(upper, maxOmega));

		return true;
	}

	/**
	 * Calculates the rotational angle between two consecutive events,
	 * correcting for cases where the full rotation of 2*PI is crossed.
	 *
	 * @param curr
	 *            the first event
	 * @param next
	 *            the subsequent event
	 * @return the rotational angle between the events
	 */
	public static double deltaPhi(AvrTask curr, AvrTask next) {
		double phi1 = curr.getPhi();
		double phi2 = next.getPhi();

		// calculate delta
		double deltaPhi = phi2 - phi1;

		// correct for curr, next in different rotations
		deltaPhi = deltaPhi - 2 * Math.PI * (Math.ceil(deltaPhi / (2 * Math.PI)) - 1);

		return deltaPhi;
	}

	/**
	 * The {@link DoubleRange} represents a rotation-speed interval, limited by
	 * a lower and upper boundary.
	 * 
	 * @author vrichthammer
	 *
	 */
	public static class DoubleRange {
		protected double lower;
		protected double upper;

		/**
		 * Constructs a {@link DoubleRange} from a lower and upper bound.
		 * 
		 * @param lower
		 *            the lower bound
		 * @param upper
		 *            the upper bound
		 */
		public DoubleRange(double lower, double upper) {

			assert lower <= upper : "Constructing DoubleRange: Lower boundary must be smaller or equal than upper boundary.";
			this.lower = lower;
			this.upper = upper;
		}

		/**
		 * Returns the lower bound of the range.
		 *
		 * @return the lower bound
		 */
		public double getLower() {
			return lower;
		}

		/**
		 * Sets the lower bound of the range.
		 *
		 * @param lower
		 *            the lower bound
		 */
		public void setLower(double lower) {
			assert lower <= this.upper : "setLower() impossible: argument larger than upper bound";
			this.lower = lower;
		}

		/**
		 * Returns the upper bound of the range.
		 *
		 * @return the upper bound
		 */
		public double getUpper() {
			return upper;
		}

		/**
		 * Sets the upper bound of the range.
		 *
		 * @param upper
		 *            the upper bound
		 */
		public void setUpper(double upper) {
			assert upper >= this.lower : "setUpper() impossible: argument smaller than lower bound";
			this.upper = upper;
		}
	}
}
