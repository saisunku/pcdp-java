package edu.coursera.parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.*;

/**
 * Class wrapping methods for implementing reciprocal array sum in parallel.
 */
public final class ReciprocalArraySum {

    /**
     * Default constructor.
     */
    private ReciprocalArraySum() {
    }

    /**
     * Sequentially compute the sum of the reciprocal values for a given array.
     *
     * @param input Input array
     * @return The sum of the reciprocals of the array input
     */
    protected static double seqArraySum(final double[] input) {
        double sum = 0;

        // Compute sum of reciprocals of array elements
        for (int i = 0; i < input.length; i++) {
            sum += 1 / input[i];
        }

        return sum;
    }

    /**
     * Computes the size of each chunk, given the number of chunks to create
     * across a given number of elements.
     *
     * @param nChunks The number of chunks to create
     * @param nElements The number of elements to chunk across
     * @return The default chunk size
     */
    private static int getChunkSize(final int nChunks, final int nElements) {
        // Integer ceil
        return (nElements + nChunks - 1) / nChunks;
    }

    /**
     * Computes the inclusive element index that the provided chunk starts at,
     * given there are a certain number of chunks.
     *
     * @param chunk The chunk to compute the start of
     * @param nChunks The number of chunks created
     * @param nElements The number of elements to chunk across
     * @return The inclusive index that this chunk starts at in the set of
     *         nElements
     */
    private static int getChunkStartInclusive(final int chunk,
            final int nChunks, final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        return chunk * chunkSize;
    }

    /**
     * Computes the exclusive element index that the provided chunk ends at,
     * given there are a certain number of chunks.
     *
     * @param chunk The chunk to compute the end of
     * @param nChunks The number of chunks created
     * @param nElements The number of elements to chunk across
     * @return The exclusive end index for this chunk
     */
    private static int getChunkEndExclusive(final int chunk, final int nChunks,
            final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        final int end = (chunk + 1) * chunkSize;
        if (end > nElements) {
            return nElements;
        } else {
            return end;
        }
    }

    private static class ReciprocalArraySumTask extends RecursiveAction {
	private static final int SEQ_THRESH = 500_000;
        private final int startIndexInclusive;
        private final int endIndexExclusive;
        private final double[] input;
        private double value;

        ReciprocalArraySumTask(final int setStartIndexInclusive,
                final int setEndIndexExclusive, final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
        }

        public double getValue() {
            return value;
        }

        @Override
        protected void compute() {
	    if (endIndexExclusive - startIndexInclusive < SEQ_THRESH) {
	      for (int i = startIndexInclusive; i < endIndexExclusive; i++) {
	          value += 1 / input[i];
	      }
	    } else {
	        int mid = (startIndexInclusive + endIndexExclusive) / 2;
	        ReciprocalArraySumTask left = new ReciprocalArraySumTask(startIndexInclusive, mid, input);
	        ReciprocalArraySumTask right = new ReciprocalArraySumTask(mid, endIndexExclusive, input);
	        left.fork();
	        right.compute();
	        left.join();
	        value = left.getValue() + right.getValue();
	    }
        }
    }

    protected static double parArraySum(final double[] input) {
        assert input.length % 2 == 0;

        return parManyTaskArraySum(input, 2);
    }

    protected static double parManyTaskArraySum(final double[] input,
            final int numTasks) {

	ReciprocalArraySumTask task = new ReciprocalArraySumTask(0, input.length, input);
	ForkJoinPool pool = new ForkJoinPool(numTasks);
	pool.invoke(task);
	return task.getValue();

	// // chunking solution - copied from https://www.coursera.org/learn/parallel-programming-in-java/discussions/weeks/1/threads/G2QGgNY5EeiXqQqmFRLNkA
	// double sum = 0;
	// List<Callable<ReciprocalArraySumTask>> reciprocalArraySumTaskCallableArrayList = new ArrayList<>();
        // for (int i=0; i < numTasks; i++) {
        //     int startIndexInclusive = getChunkStartInclusive(i, numTasks, input.length);
        //     int endIndexExcluse = getChunkEndExclusive(i, numTasks, input.length);
        //     Callable<ReciprocalArraySumTask> reciprocalArraySumTaskCallable = () -> {
        //         ReciprocalArraySumTask reciprocalArraySumTask = new ReciprocalArraySumTask(startIndexInclusive, endIndexExcluse, input);
        //         reciprocalArraySumTask.compute();
        //         return reciprocalArraySumTask;
        //     };
        //     reciprocalArraySumTaskCallableArrayList.add(reciprocalArraySumTaskCallable);
        // }

	// ForkJoinPool pool = new ForkJoinPool(numTasks);
        // List<Future<ReciprocalArraySumTask>> reciprocalArraySumTaskFutureList = pool.invokeAll(reciprocalArraySumTaskCallableArrayList);

        // try {
        //     // Compute sum of reciprocals of array elements
        //     for (Future<ReciprocalArraySumTask> task : reciprocalArraySumTaskFutureList) {
        //         sum += task.get().getValue();
        //     }
        // } catch (Exception exp) {
        //     System.out.println(exp);
        // }

        // return sum;

    }
}
