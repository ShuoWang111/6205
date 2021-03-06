/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.info6205.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import edu.neu.coe.info6205.sort.BaseHelper;
import edu.neu.coe.info6205.sort.GenericSort;
import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.HelperFactory;
import edu.neu.coe.info6205.sort.SortWithHelper;
import edu.neu.coe.info6205.sort.elementary.InsertionSort;

import java.util.Random;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running
 * time of algorithms. It is part of the repository for the INFO6205 class,
 * taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular,
 * UnaryOperator&lt;T&gt; (a function of T => T), Consumer&lt;T&gt; (essentially
 * a function of T => Void) and Supplier&lt;T&gt; (essentially a function of
 * Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 * <li>The pre-function which prepares the input to the study function (field
 * fPre) (may be null);</li>
 * <li>The study function itself (field fRun) -- assumed to be a mutating
 * function since it does not return a result;</li>
 * <li>The post-function which cleans up and/or checks the results of the study
 * function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and
 * the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which
 *            you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

	/**
	 * Calculate the appropriate number of warmup runs.
	 *
	 * @param m the number of runs.
	 * @return at least 2 and at most m/10.
	 */
	static int getWarmupRuns(int m) {
		return Integer.max(2, Integer.min(10, m / 10));
	}

	/**
	 * Run function f m times and return the average time in milliseconds.
	 *
	 * @param supplier a Supplier of a T
	 * @param m        the number of times the function f will be called.
	 * @return the average number of milliseconds taken for each run of function f.
	 */
	@Override
	public double runFromSupplier(Supplier<T> supplier, int m) {
//		logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
		// Warmup phase
		final Function<T, T> function = t -> {
			fRun.accept(t);
			return t;
		};
		new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);

		// Timed phase
		return new Timer().repeat(m, supplier, function, fPre, fPost);
	}

	/**
	 * Constructor for a Benchmark_Timer with option of specifying all three
	 * functions.
	 *
	 * @param description the description of the benchmark.
	 * @param fPre        a function of T => T. Function fPre is run before each
	 *                    invocation of fRun (but with the clock stopped). The
	 *                    result of fPre (if any) is passed to fRun.
	 * @param fRun        a Consumer function (i.e. a function of T => Void).
	 *                    Function fRun is the function whose timing you want to
	 *                    measure. For example, you might create a function which
	 *                    sorts an array. When you create a lambda defining fRun,
	 *                    you must return "null."
	 * @param fPost       a Consumer function (i.e. a function of T => Void).
	 */
	public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
		this.description = description;
		this.fPre = fPre;
		this.fRun = fRun;
		this.fPost = fPost;
	}

	/**
	 * Constructor for a Benchmark_Timer with option of specifying all three
	 * functions.
	 *
	 * @param description the description of the benchmark.
	 * @param fPre        a function of T => T. Function fPre is run before each
	 *                    invocation of fRun (but with the clock stopped). The
	 *                    result of fPre (if any) is passed to fRun.
	 * @param fRun        a Consumer function (i.e. a function of T => Void).
	 *                    Function fRun is the function whose timing you want to
	 *                    measure. For example, you might create a function which
	 *                    sorts an array.
	 */
	public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
		this(description, fPre, fRun, null);
	}

	/**
	 * Constructor for a Benchmark_Timer with only fRun and fPost Consumer
	 * parameters.
	 *
	 * @param description the description of the benchmark.
	 * @param fRun        a Consumer function (i.e. a function of T => Void).
	 *                    Function fRun is the function whose timing you want to
	 *                    measure. For example, you might create a function which
	 *                    sorts an array. When you create a lambda defining fRun,
	 *                    you must return "null."
	 * @param fPost       a Consumer function (i.e. a function of T => Void).
	 */
	public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
		this(description, null, fRun, fPost);
	}

	/**
	 * Constructor for a Benchmark_Timer where only the (timed) run function is
	 * specified.
	 *
	 * @param description the description of the benchmark.
	 * @param f           a Consumer function (i.e. a function of T => Void).
	 *                    Function f is the function whose timing you want to
	 *                    measure. For example, you might create a function which
	 *                    sorts an array.
	 */
	public Benchmark_Timer(String description, Consumer<T> f) {
		this(description, null, f, null);
	}

	private final String description;
	private final UnaryOperator<T> fPre;
	private final Consumer<T> fRun;
	private final Consumer<T> fPost;

	final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);

	
	
	
	public static void main(String[] args) {
		int n = 1000000; //length of array
		//random
		Random rd = new Random(); // creating Random object
		Integer[] arr_random = new Integer[n];
		for (int i = 0; i < n; i++) {
			arr_random[i] = rd.nextInt(); // storing random integers in an array
//    		System.out.println(arr_random[i]);
		}

		// ordered
		Integer[] arr_ordered = new Integer[n];
		for (int i = 0; i < n; i++) {
			arr_ordered[i] = i;
//    		System.out.println(arr_ordered[i]);
		}

		// reverse-ordered
		Integer[] arr_reverse_ordered = new Integer[n];
		for (int i = 0; i < n; i++) {
			arr_reverse_ordered[i] = n - i;
//    		System.out.println(arr_reverse_ordered[i]); 
		}

		// partially-ordered

		Integer[] arr_partially_ordered = new Integer[n];

		for (int i = 0; i < n; i++) {
			if ((i / 10) % 2 == 0) {
				arr_partially_ordered[i] = arr_ordered[i];
			} else {
				arr_partially_ordered[i] = arr_reverse_ordered[i];
			}
//    		System.out.println(arr_partially_ordered[i]);
		}
		
		InsertionSort sorter = new InsertionSort();
		int m = 100; //times of repeat
		
	
		
		// random
		// random 10
		Benchmark_Timer<Integer[]> bm_random_10 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_random, 0, 10);});
		double time_random_10 = bm_random_10.runFromSupplier(() -> arr_random, m);
		System.out.println("Sort 10 random numbers -- average time in milliseconds: "+time_random_10);
		
		
		// random 100
		Benchmark_Timer<Integer[]> bm_random_100 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_random, 0, 100);});
		double time_random_100 = bm_random_100.runFromSupplier(() -> arr_random, m);
		System.out.println("Sort 100 random numbers -- average time in milliseconds: "+time_random_100);
		
		
		
		// random 1000
		Benchmark_Timer<Integer[]> bm_random_1000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_random, 0, 1000);});
		double time_random_1000 = bm_random_1000.runFromSupplier(() -> arr_random, m);
		System.out.println("Sort 1000 random numbers -- average time in milliseconds: "+time_random_1000);
		
		
		// random 10000
		Benchmark_Timer<Integer[]> bm_random_10000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_random, 0, 10000);});
		double time_random_10000 = bm_random_10000.runFromSupplier(() -> arr_random, m);
		System.out.println("Sort 10000 random numbers -- average time in milliseconds: "+time_random_10000);
		
		
		// random 100000
		Benchmark_Timer<Integer[]> bm_random_100000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_random, 0, 100000);});
		double time_random_100000 = bm_random_100000.runFromSupplier(() -> arr_random, m);
		System.out.println("Sort 100000 random numbers -- average time in milliseconds: "+time_random_100000);
//		
//		// random 200000
//		Benchmark_Timer<Integer[]> bm_random_200000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_random, 0, 200000);});
//		double time_random_200000 = bm_random_200000.runFromSupplier(() -> arr_random, m);
//		System.out.println("Sort 200000 random numbers -- average time in milliseconds: "+time_random_200000);
//		
//		// random 300000
//		Benchmark_Timer<Integer[]> bm_random_300000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_random, 0, 300000);});
//		double time_random_300000 = bm_random_300000.runFromSupplier(() -> arr_random, m);
//		System.out.println("Sort 300000 random numbers -- average time in milliseconds: "+time_random_300000);
//		
//		// random 400000
//		Benchmark_Timer<Integer[]> bm_random_400000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_random, 0, 400000);});
//		double time_random_400000 = bm_random_400000.runFromSupplier(() -> arr_random, m);
//		System.out.println("Sort 400000 random numbers -- average time in milliseconds: "+time_random_400000);
//		// random 500000
//		Benchmark_Timer<Integer[]> bm_random_500000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_random, 0, 500000);});
//		double time_random_500000 = bm_random_500000.runFromSupplier(() -> arr_random, m);
//		System.out.println("Sort 500000 random numbers -- average time in milliseconds: "+time_random_500000);
				
				
		// ordered
		// ordered 10
		Benchmark_Timer<Integer[]> bm_ordered_10 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_ordered, 0, 10);});
		double time10 = bm_ordered_10.runFromSupplier(() -> arr_ordered, m);
		System.out.println("Sort 10 ordered numbers -- average time in milliseconds: "+time10);
		
		// ordered 100
		Benchmark_Timer<Integer[]> bm_ordered_100 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_ordered, 0, 100);});
		double time100 = bm_ordered_100.runFromSupplier(() -> arr_ordered, m);
		System.out.println("Sort 100 ordered numbers -- average time in milliseconds: "+time100);
		
		// ordered 1000
		Benchmark_Timer<Integer[]> bm_ordered_1000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_ordered, 0, 1000);});
		double time1000 = bm_ordered_1000.runFromSupplier(() -> arr_ordered, m);
		System.out.println("Sort 1000 ordered numbers -- average time in milliseconds: "+time1000);
		
		// ordered 10000
		Benchmark_Timer<Integer[]> bm_ordered_10000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_ordered, 0, 10000);});
		double time10000 = bm_ordered_10000.runFromSupplier(() -> arr_ordered, m);
		System.out.println("Sort 10000 ordered numbers -- average time in milliseconds: "+time10000);
		
		// ordered 100000
		Benchmark_Timer<Integer[]> bm_ordered_100000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_ordered, 0, 100000);});
		double time100000 = bm_ordered_100000.runFromSupplier(() -> arr_ordered, m);
		System.out.println("Sort 100000 ordered numbers -- average time in milliseconds: "+time100000);
		
//		// ordered 200000
//		Benchmark_Timer<Integer[]> bm_ordered_200000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_ordered, 0, 200000);});
//		double time200000 = bm_ordered_200000.runFromSupplier(() -> arr_ordered, m);
//		System.out.println("Sort 200000 ordered numbers -- average time in milliseconds: "+time200000);
//		// ordered 300000
//		Benchmark_Timer<Integer[]> bm_ordered_300000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_ordered, 0, 300000);});
//		double time300000 = bm_ordered_300000.runFromSupplier(() -> arr_ordered, m);
//		System.out.println("Sort 300000 ordered numbers -- average time in milliseconds: "+time300000);
//		// ordered 400000
//		Benchmark_Timer<Integer[]> bm_ordered_400000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_ordered, 0, 400000);});
//		double time400000 = bm_ordered_400000.runFromSupplier(() -> arr_ordered, m);
//		System.out.println("Sort 400000 ordered numbers -- average time in milliseconds: "+time400000);
//		
//		// ordered 500000
//		Benchmark_Timer<Integer[]> bm_ordered_500000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_ordered, 0, 500000);});
//		double time500000 = bm_ordered_500000.runFromSupplier(() -> arr_ordered, m);
//		System.out.println("Sort 500000 ordered numbers -- average time in milliseconds: "+time500000);
		
		
		// reverse-ordered
		// reverse-ordered 10
		Benchmark_Timer<Integer[]> bm_reverse_ordered_10 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_reverse_ordered, 0, 10);});
		double time_reverse_ordered_10 = bm_reverse_ordered_10.runFromSupplier(() -> arr_reverse_ordered, m);
		System.out.println("Sort 10 reverse_ordered numbers -- average time in milliseconds: "+time_reverse_ordered_10);
		
		// reverse-ordered 100
		Benchmark_Timer<Integer[]> bm_reverse_ordered_100 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_reverse_ordered, 0, 100);});
		double time_reverse_ordered_100 = bm_reverse_ordered_100.runFromSupplier(() -> arr_reverse_ordered, m);
		System.out.println("Sort 100 reverse_ordered numbers -- average time in milliseconds: "+time_reverse_ordered_100);
		
		// reverse-ordered 1000
		Benchmark_Timer<Integer[]> bm_reverse_ordered_1000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_reverse_ordered, 0, 1000);});
		double time_reverse_ordered_1000 = bm_reverse_ordered_1000.runFromSupplier(() -> arr_reverse_ordered, m);
		System.out.println("Sort 1000 reverse_ordered numbers -- average time in milliseconds: "+time_reverse_ordered_1000);
		
		// reverse-ordered 10000
		Benchmark_Timer<Integer[]> bm_reverse_ordered_10000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_reverse_ordered, 0, 10000);});
		double time_reverse_ordered_10000 = bm_reverse_ordered_10000.runFromSupplier(() -> arr_reverse_ordered, m);
		System.out.println("Sort 10000 reverse_ordered numbers -- average time in milliseconds: "+time_reverse_ordered_10000);
		
		// reverse-ordered 100000
		Benchmark_Timer<Integer[]> bm_reverse_ordered_100000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_reverse_ordered, 0, 100000);});
		double time_reverse_ordered_100000 = bm_reverse_ordered_100000.runFromSupplier(() -> arr_reverse_ordered, m);
		System.out.println("Sort 100000 reverse_ordered numbers -- average time in milliseconds: "+time_reverse_ordered_100000);
		
//		// reverse-ordered 200000
//		Benchmark_Timer<Integer[]> bm_reverse_ordered_200000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_reverse_ordered, 0, 200000);});
//		double time_reverse_ordered_200000 = bm_reverse_ordered_200000.runFromSupplier(() -> arr_reverse_ordered, m);
//		System.out.println("Sort 200000 reverse_ordered numbers -- average time in milliseconds: "+time_reverse_ordered_200000);
//		
//		// reverse-ordered 300000
//		Benchmark_Timer<Integer[]> bm_reverse_ordered_300000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_reverse_ordered, 0, 300000);});
//		double time_reverse_ordered_300000 = bm_reverse_ordered_300000.runFromSupplier(() -> arr_reverse_ordered, m);
//		System.out.println("Sort 300000 reverse_ordered numbers -- average time in milliseconds: "+time_reverse_ordered_300000);
//		// reverse-ordered 400000
//		Benchmark_Timer<Integer[]> bm_reverse_ordered_400000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_reverse_ordered, 0, 400000);});
//		double time_reverse_ordered_400000 = bm_reverse_ordered_400000.runFromSupplier(() -> arr_reverse_ordered, m);
//		System.out.println("Sort 400000 reverse_ordered numbers -- average time in milliseconds: "+time_reverse_ordered_400000);
//		// reverse-ordered 500000
//		Benchmark_Timer<Integer[]> bm_reverse_ordered_500000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_reverse_ordered, 0, 500000);});
//		double time_reverse_ordered_500000 = bm_reverse_ordered_500000.runFromSupplier(() -> arr_reverse_ordered, m);
//		System.out.println("Sort 500000 reverse_ordered numbers -- average time in milliseconds: "+time_reverse_ordered_500000);
		
		//partially
		//partially ordered 10
		Benchmark_Timer<Integer[]> bm_partially_ordered_10 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_partially_ordered, 0, 10);});
		double time_partially_ordered_10 = bm_reverse_ordered_10.runFromSupplier(() -> arr_partially_ordered, m);
		System.out.println("Sort 10 partially_ordered numbers -- average time in milliseconds: "+time_partially_ordered_10);
	
		//partially ordered 100
		Benchmark_Timer<Integer[]> bm_partially_ordered_100 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_partially_ordered, 0, 100);});
		double time_partially_ordered_100 = bm_reverse_ordered_100.runFromSupplier(() -> arr_partially_ordered, m);
		System.out.println("Sort 100 partially_ordered numbers -- average time in milliseconds: "+time_partially_ordered_100);
		
		
		//partially ordered 1000
		Benchmark_Timer<Integer[]> bm_partially_ordered_1000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_partially_ordered, 0, 1000);});
		double time_partially_ordered_1000 = bm_reverse_ordered_1000.runFromSupplier(() -> arr_partially_ordered, m);
		System.out.println("Sort 1000 partially_ordered numbers -- average time in milliseconds: "+time_partially_ordered_1000);
		
		//partially ordered 10000
		Benchmark_Timer<Integer[]> bm_partially_ordered_10000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_partially_ordered, 0, 10000);});
		double time_partially_ordered_10000 = bm_reverse_ordered_10000.runFromSupplier(() -> arr_partially_ordered, m);
		System.out.println("Sort 10000 partially_ordered numbers -- average time in milliseconds: "+time_partially_ordered_10000);
		
		//partially ordered 100000
		Benchmark_Timer<Integer[]> bm_partially_ordered_100000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_partially_ordered, 0, 100000);});
		double time_partially_ordered_100000 = bm_partially_ordered_100000.runFromSupplier(() -> arr_partially_ordered, m);
		System.out.println("Sort 100000 partially_ordered numbers -- average time in milliseconds: "+time_partially_ordered_100000);
//		//partially ordered 200000
//		Benchmark_Timer<Integer[]> bm_partially_ordered_200000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_partially_ordered, 0, 200000);});
//		double time_partially_ordered_200000 = bm_partially_ordered_100000.runFromSupplier(() -> arr_partially_ordered, m);
//		System.out.println("Sort 200000 partially_ordered numbers -- average time in milliseconds: "+time_partially_ordered_200000);
//		
//		//partially ordered 300000
//		Benchmark_Timer<Integer[]> bm_partially_ordered_300000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_partially_ordered, 0, 300000);});
//		double time_partially_ordered_300000 = bm_partially_ordered_100000.runFromSupplier(() -> arr_partially_ordered, m);
//		System.out.println("Sort 300000 partially_ordered numbers -- average time in milliseconds: "+time_partially_ordered_300000);
//		
//		//partially ordered 400000
//		Benchmark_Timer<Integer[]> bm_partially_ordered_400000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_partially_ordered, 0, 400000);});
//		double time_partially_ordered_400000 = bm_partially_ordered_100000.runFromSupplier(() -> arr_partially_ordered, m);
//		System.out.println("Sort 400000 partially_ordered numbers -- average time in milliseconds: "+time_partially_ordered_400000);
//		
//		//partially ordered 500000
//		Benchmark_Timer<Integer[]> bm_partially_ordered_500000 = new Benchmark_Timer<Integer[]>("InsertionSort", f -> {sorter.sort(arr_partially_ordered, 0, 500000);});
//		double time_partially_ordered_500000 = bm_partially_ordered_100000.runFromSupplier(() -> arr_partially_ordered, m);
//		System.out.println("Sort 500000 partially_ordered numbers -- average time in milliseconds: "+time_partially_ordered_500000);
				
	}

}
