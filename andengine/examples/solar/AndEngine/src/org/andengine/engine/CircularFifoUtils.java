package org.andengine.engine;

import org.andengine.engine.BoundedFifoBuffer.BoundedFifoBufferIterator;

public class CircularFifoUtils {
	private static final BoundedFifoBufferIterator sIterator = new BoundedFifoBufferIterator(null);

	public static final float avgFloat(CircularFifoBuffer pBuffer) {
		return sumFloat(pBuffer) / pBuffer.size();
	}

	public static final float sumFloat(CircularFifoBuffer pBuffer) {
		float sum = 0;

		synchronized (sIterator) {
			for (sIterator.reinitialize(pBuffer); sIterator.hasNext();) {
				final Float val = (Float) sIterator.next();
				sum += val;
			}
		}
		return sum;
	}

	public static final long avgLong(CircularFifoBuffer pBuffer) {
		return sumLong(pBuffer) / pBuffer.size();
	}

	public static final long sumLong(CircularFifoBuffer pBuffer) {
		long sum = 0;

		synchronized (sIterator) {
			for (sIterator.reinitialize(pBuffer); sIterator.hasNext();) {
				final Long val = (Long) sIterator.next();
				sum += val;
			}
		}
		return sum;
	}

	public static final int avgInteger(CircularFifoBuffer pBuffer) {
		return sumInteger(pBuffer) / pBuffer.size();
	}

	public static final int sumInteger(CircularFifoBuffer pBuffer) {
		int sum = 0;

		synchronized (sIterator) {
			for (sIterator.reinitialize(pBuffer); sIterator.hasNext();) {
				final Integer val = (Integer) sIterator.next();
				sum += val;
			}
		}
		return sum;
	}
}
