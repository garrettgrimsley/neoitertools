package net.ericaro.neoitertools;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ericaro.neoitertools.combinatorics.Combinatorics;
import net.ericaro.neoitertools.iterators.ChainIterator;
import net.ericaro.neoitertools.iterators.CharSequenceIterator;
import net.ericaro.neoitertools.iterators.CycleIterator;
import net.ericaro.neoitertools.iterators.EmptyIterator;
import net.ericaro.neoitertools.iterators.EnumerateIterator;
import net.ericaro.neoitertools.iterators.FilterIterator;
import net.ericaro.neoitertools.iterators.GenericArrayIterator;
import net.ericaro.neoitertools.iterators.GroupByIterator;
import net.ericaro.neoitertools.iterators.IteratorIterable;
import net.ericaro.neoitertools.iterators.MapIterator;
import net.ericaro.neoitertools.iterators.RangeIterator;
import net.ericaro.neoitertools.iterators.RepeatIterator;
import net.ericaro.neoitertools.iterators.SingletonIterator;
import net.ericaro.neoitertools.iterators.SliceIterator;
import net.ericaro.neoitertools.iterators.TakeWhileIterator;
import net.ericaro.neoitertools.iterators.Zip2Iterator;
import net.ericaro.neoitertools.iterators.ZipIterator;
import net.ericaro.neoitertools.primitives.BooleanIterator;
import net.ericaro.neoitertools.primitives.ByteIterator;
import net.ericaro.neoitertools.primitives.CharacterIterator;
import net.ericaro.neoitertools.primitives.DoubleIterator;
import net.ericaro.neoitertools.primitives.FloatIterator;
import net.ericaro.neoitertools.primitives.IntegerIterator;
import net.ericaro.neoitertools.primitives.LongIterator;
import net.ericaro.neoitertools.primitives.ShortIterator;

/**
 * <p>
 * This module implements a number of iterator building blocks inspired by
 * constructs from the Python programming languages. Each has been recast in a
 * form suitable for Java.
 * </p>
 * 
 * The module standardizes a core set of fast, memory efficient tools that are
 * useful by themselves or in combination. Standardization helps avoid the
 * readability and reliability problems which arise when many different
 * individuals create their own slightly varying implementations, each with
 * their own quirks and naming conventions.
 * 
 * The tools are designed to combine readily with one another. This makes it
 * easy to construct more specialized tools succinctly and efficiently in pure
 * Java.
 * 
 * 
 * @author eric
 * 
 */
public class Iterators {

	/**
	 * Return True if all elements of the iterator are evaluated to true with
	 * the Predicate (or if the iterator is empty).
	 * 
	 * @param <T>
	 * @param iterator
	 * @param predicate
	 * @return
	 */
	public static <T> boolean all(Iterator<T> iterator, Predicate<T> predicate) {
		while (iterator.hasNext())
			if (!predicate.map(iterator.next()))
				return false;
		return true;
	}

	/**
	 * Return True if any element of the iterator is mapped to true. If the
	 * iterator is empty, return False.
	 * 
	 * @param iterator
	 * @param predicate
	 * @return
	 */
	public static <T> boolean any(Iterator<T> iterator, Predicate<T> predicate) {
		while (iterator.hasNext())
			if (predicate.map(iterator.next()))
				return true;
		return false;
	}

	
	

	/**
	 * Make an iterator that returns elements from the first iterable until it
	 * is exhausted, then proceeds to the next iterable, until all of the
	 * iterables are exhausted. Used for treating consecutive sequences as a
	 * single sequence.
	 * 
	 * We do not use varargs due to an inner flaw in varargs that make them hard/impossible
	 * to combine with generics
	 * 
	 * @param iterators
	 *            an iterator over iterators
	 * @return
	 */
	public static <T> Iterator<T> chain(final Iterator<Iterator<T>> iterators) {
		return new ChainIterator<T>(iterators);
	}

	/**
	 * chain together two iterators.
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static <T> Iterator<T> chain(Iterator<T> i1, Iterator<T> i2) {
		List<Iterator<T>> list = new LinkedList<Iterator<T>>();
		list.add(i1);
		list.add(i2);
		return chain(list.iterator());
	}

	/**
	 * Return r length subsequences of elements from the input iterator.
	 * 
	 * Combinations are emitted in lexicographic sort order. So, if the input
	 * iterator is sorted, the combination tuples will be produced in sorted
	 * order.
	 * 
	 * Elements are treated as unique based on their position, not on their
	 * value. So if the input elements are unique, there will be no repeat
	 * values in each combination.
	 * 
	 * @param iterator
	 * @param r
	 * @return iterator over combinations as list
	 */
	public static <T> Iterator<List<T>> combinations(Iterator<T> iterator, int r) {
		List<T> list = list(iterator);
		return Combinatorics.applied(list,
				Combinatorics.combinations(list.size(), r));
	}

	/**
	 * equivalent to count(0)
	 * 
	 * @see Iterators#count(int)
	 * 
	 * @return
	 */
	public static Iterator<Integer> count() {
		return count(0);
	}

	/**
	 * Make an iterator that returns consecutive integers starting with n.
	 * 
	 * @param n
	 * @return
	 */
	public static Iterator<Integer> count(final int n) {
		return new RangeIterator(0, 1, Integer.MAX_VALUE); 
	}

	/**
	 * Make an iterator returning elements from the iterator and saving a copy
	 * of each. When the iterator is exhausted, return elements from the saved
	 * copy. Repeats indefinitely.
	 * 
	 * @param iterator
	 * @return an iterator returning elements from the iterator over and over
	 *         again.
	 */
	public static <T> Iterator<T> cycle(Iterator<T> iterator) {
		return new CycleIterator<T>(iterator);
	}

	/**
	 * Make an iterator that drops elements from the iterator as long as the
	 * predicate is true. Afterwards, returns every element. Note, the iterator
	 * does not produce any output until the predicate first becomes false, so
	 * it may have a lengthy start-up time.
	 * 
	 * @param <T>
	 * @param iterator
	 * @param predicate
	 * @return
	 */
	public static <T> Iterator<T> dropwhile(final Iterator<T> iterator,
			final Predicate<T> predicate) {
		// consume the unwanted elements
		while (iterator.hasNext()) {
			T t = iterator.next();
			if (!predicate.map(t)) {
				// hey , t is the first element to return, then follow on the
				// iterator
				return chain(new SingletonIterator<T>(t), iterator);
			}
		}
		return new EmptyIterator<T>();
	}

	/**
	 * Return an {@link Iterator} of Index object. The next() method of the
	 * iterator returned by enumerate() returns an Index containing a count
	 * (from start which defaults to 0) and the corresponding value obtained
	 * from iterating over iterator. enumerate() is useful for obtaining an
	 * indexed series: (0, seq[0]), (1, seq[1]), (2, seq[2]), .... For example:
	 * 
	 * <pre>
	 * for (Index index : enumerate(seasons))
	 * 	System.out.println(index.i + &quot; &quot; + index.value);
	 * </pre>
	 * 
	 * gives:
	 * 
	 * <pre>
	 * 		0 Spring
	 * 		1 Summer
	 * 		2 Fall
	 * 		3 Winter
	 * </pre>
	 * 
	 * @param iterator
	 * @return
	 */
	public static <T> Iterator<Index<T>> enumerate(final Iterator<T> iterator) {

		return new EnumerateIterator<T>(iterator);
	}

	/**
	 * Make an iterator that filters elements from iterator returning only those
	 * for which the predicate is True.
	 * 
	 * 
	 * @param predicate
	 * @param iterator
	 * @return
	 */
	public static <T> Iterator<T> filter(final Predicate<T> predicate,
			final Iterator<T> iterator) {
		return new FilterIterator<T>(iterator, predicate);
	}

	/**
	 * Make an iterator that filters elements from iterator returning only those
	 * for which the predicate is False.
	 * 
	 * 
	 * @param predicate
	 * @param iterator
	 * @return
	 */
	public static <T> Iterator<T> filterfalse(final Predicate<T> predicate,
			final Iterator<T> iterator) {
		return new FilterIterator<T>(iterator, predicate, true);
		}

	/**
	 * Make an iterator that returns consecutive keys and groups from the source
	 * iterator. The key is a function computing a key value for each element.
	 * Generally, the iterator needs to already be sorted on the same key
	 * function.
	 * 
	 * The operation of groupby() is similar to the uniq filter in Unix. It
	 * generates a break or new group every time the value of the key function
	 * changes (which is why it is usually necessary to have sorted the data
	 * using the same key function). That behavior differs from SQL’s GROUP BY
	 * which aggregates common elements regardless of their input order.
	 * 
	 * The returned group is itself an iterator that shares the underlying
	 * iterator with groupby(). Because the source is shared, when the groupby()
	 * object is advanced, the previous group is no longer visible. So, if that
	 * data is needed later, it should be stored as a list.
	 * 
	 * @param iterator
	 *            the source iterator
	 * @param key
	 *            the key mapper
	 * @return an iterator that returns consecutive keys and groups from the
	 *         source iterator
	 */
	public static <T, K> Iterator<Couple<K, Iterator<T>>> groupby(
			Iterator<T> iterator, Mapper<T, K> key) {
		return new GroupByIterator<K, T>(iterator, key);
	}

	/**
	 * Turns a {@link CharSequence} into an {@link Iterator}
	 * 
	 * @param seq
	 *            any {@link CharSequence}
	 * @return
	 */
	public static Iterator<Character> iter(final CharSequence seq) {
		return new CharSequenceIterator(seq);

	}

	/**
	 * Turn any Iterable into an Iterator
	 * 
	 * @param chars
	 * @return
	 */
	public static <T> Iterator<T> iter(Iterable<T> iterable) {
		return iterable.iterator();
	}

	/**
	 * Turns any byte[] array into an iterable
	 * 
	 * @param array
	 * @return an Iterator over array
	 */
	public static Iterator<Byte> iter(byte[] array) {
		return new ByteIterator(array);

	}

	/**
	 * Turns any char[] array into an iterable
	 * 
	 * @param array
	 * @return an Iterator over array
	 */
	public static Iterator<Character> iter(char[] array) {
		return new CharacterIterator(array);

	}

	/**
	 * Turns any short[] array into an iterable
	 * 
	 * @param array
	 * @return an Iterator over array
	 */
	public static Iterator<Short> iter(short[] array) {
		return new ShortIterator(array);

	}

	/**
	 * Turns any int[] array into an iterable
	 * 
	 * @param array
	 * @return an Iterator over array
	 */
	public static Iterator<Integer> iter(int[] array) {
		return new IntegerIterator(array);

	}

	/**
	 * Turns any long[] array into an iterable
	 * 
	 * @param array
	 * @return an Iterator over array
	 */
	public static Iterator<Long> iter(long[] array) {
		return new LongIterator(array);

	}

	/**
	 * Turns any float[] array into an iterable
	 * 
	 * @param array
	 * @return an Iterator over array
	 */
	public static Iterator<Float> iter(float[] array) {
		return new FloatIterator(array);

	}

	/**
	 * Turns any double[] array into an iterable
	 * 
	 * @param array
	 * @return an Iterator over array
	 */
	public static Iterator<Double> iter(double[] array) {
		return new DoubleIterator(array);

	}

	/**
	 * Turns any boolean[] array into an iterable
	 * 
	 * @param array
	 * @return an Iterator over array
	 */
	public static Iterator<Boolean> iter(boolean[] array) {
		return new BooleanIterator(array);

	}

	/**
	 * Turns any object array into an Iterator
	 * 
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T> Iterator<T> iter(final T[] t) {
		return new GenericArrayIterator<T>(t);
	}

	/**
	 * turns any {@link Iterator} into a list (that can be modified)
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T> List<T> list(Iterator<T> iterator) {
		List<T> list = new LinkedList<T>();
		while (iterator.hasNext())
			list.add(iterator.next());
		return list;
	}

	/**
	 * Apply {@link Mapper} to every item of <code>iterator</code> and return an
	 * iterator of the results.
	 * 
	 * @param mapper
	 * @param iterator
	 * @return
	 */
	public static <I, O> Iterator<O> map(final Mapper<I, O> mapper,
			final Iterator<I> iterator) {

		return new MapIterator<O, I>(iterator, mapper);

	}

	/**
	 * Return successive full length permutations of elements in the iterator.
	 * 
	 * 
	 * Permutations are emitted in lexicographic sort order. So, if the input
	 * iterable is sorted, the permutation list will be produced in sorted
	 * order.
	 * 
	 * Elements are treated as unique based on their position, not on their
	 * value. So if the input elements are unique, there will be no repeat
	 * values in each permutation.
	 * 
	 * @param iterator
	 * @return iterator of permuted list
	 */
	public static <T> Iterator<List<T>> permutations(Iterator<T> iterator) {
		List<T> list = list(iterator);
		return Combinatorics.applied(list,
				Combinatorics.permutations(list.size()));
	}

	/**
	 * Return successive r-length permutations of elements in the iterator.
	 * 
	 * Permutations are emitted in lexicographic sort order. So, if the input
	 * iterable is sorted, the permutation list will be produced in sorted
	 * order.
	 * 
	 * Elements are treated as unique based on their position, not on their
	 * value. So if the input elements are unique, there will be no repeat
	 * values in each permutation.
	 * 
	 * @param iterator
	 * @param r
	 * @return
	 */
	public static <T> Iterator<List<T>> permutations(Iterator<T> iterator, int r) {
		List<T> list = list(iterator);
		return Combinatorics.applied(list,
				Combinatorics.sublists(list.size(), r));
	}

	/**
	 * equivalent to
	 * 
	 * <pre>
	 * range(0, end, 1)
	 * </pre>
	 * 
	 * @see Iterators#range(int, int, int)
	 * @param end
	 * @return
	 */
	public static Iterator<Integer> range(final int end) {
		return range(0, end, 1);
	}

	/**
	 * equivalent to
	 * 
	 * <pre>
	 * range(start, end, 1)
	 * </pre>
	 * 
	 * @see Iterators#range(int, int, int)
	 * @param start
	 * @param end
	 * @return
	 */
	public static Iterator<Integer> range(int start, int end) {
		return range(start, end, 1);
	}

	/**
	 * This is a versatile function to create iterator containing arithmetic
	 * progressions. It is most often used in for loops. The full form returns
	 * an iterator over Integers [start, start + step, start + 2 * step, ...].
	 * <ul>
	 * <li>If step is positive, the last element is the largest start + i * step
	 * less than stop;</li>
	 * <li>if step is negative, the last element is the smallest start + i *
	 * step greater than stop.</li>
	 * <li>step must not be zero (or else InvalidParameterException is raised).</li>
	 * Example:
	 * 
	 * <pre>
	 * range(0, 30, 5);
	 * </pre>
	 * 
	 * gives
	 * 
	 * <pre>
	 * [0, 5, 10, 15, 20, 25]
	 * </pre>
	 * 
	 * <pre>
	 * range(0, 10, 3);
	 * </pre>
	 * 
	 * gives
	 * 
	 * <pre>
	 * [0, 3, 6, 9];
	 * </pre>
	 * 
	 * <pre>
	 * range(0, -10, -1);
	 * </pre>
	 * 
	 * gives
	 * 
	 * <pre>
	 * [0, -1, -2, -3, -4, -5, -6, -7, -8, -9];
	 * </pre>
	 * 
	 * @param start
	 * @param end
	 * @param step
	 * @return
	 */
	public static Iterator<Integer> range(final int start, final int end,
			final int step) throws InvalidParameterException {
		return new RangeIterator(start, step, end);
	}

	/**
	 * equivalent to reduce(operator, iterator, null);
	 * 
	 * @param <T>
	 * @param operator
	 * @param iterator
	 * @return
	 */
	public static <T> T reduce(Operator<T> operator, Iterator<T> iterator) {
		return reduce(operator, iterator, null);
	}

	/**
	 * Apply function of two arguments cumulatively to the items of iterator,
	 * from left to right, so as to reduce the iterator to a single value. For
	 * example,
	 * 
	 * <pre>
	 * Operator&lt;Integer&gt; iadd = new Operator&lt;Integer&gt;() {
	 * 	public Integer operate(Integer t1, Integer t2) {
	 * 		return t1 + t2;
	 * 	}
	 * };
	 * Integer res = reduce(iadd, range(1, 6), 0);
	 * </pre>
	 * 
	 * calculates
	 * 
	 * <pre>
	 * ((((1 + 2) + 3) + 4) + 5)
	 * </pre>
	 * 
	 * The left argument, x, is the accumulated value and the right argument, y,
	 * is the update value from the iterator. If the initializer is not null, it
	 * is placed before the items of the iterator in the calculation, and serves
	 * as a default when the iterable is empty. If initializer is null and
	 * iterator contains only one item, the first item is returned.
	 * 
	 * @param operator
	 * @param iterator
	 * @param initializer
	 * @return
	 */
	public static <T> T reduce(Operator<T> operator, Iterator<T> iterator,
			T initializer) {
		if (!iterator.hasNext())
			return initializer;
		if (initializer == null)
			initializer = iterator.next();
		while (iterator.hasNext())
			initializer = operator.operate(initializer, iterator.next());
		return initializer;
	}

	/**
	 * Return a reverse iterator. The whole iterator is stored, so be careful
	 * when used.
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T> Iterator<T> reversed(Iterator<T> iterator) {
		List<T> list = list(iterator);
		Collections.reverse(list);
		return list.iterator();
	}

	/**
	 * Make an iterator that returns object over and over again. Runs
	 * indefinitely Used as argument to imap() for invariant function
	 * parameters. Also used with izip() to create constant fields in a tuple
	 * record.
	 * 
	 * @param object
	 * @return an iterator that returns object over and over again.
	 */
	public static <T> Iterator<T> repeat(T object) {
		return new RepeatIterator<T>(object);
	}

	/**
	 * Make an iterator that returns object over and over again. Runs
	 * indefinitely unless the times argument is specified. Used as argument to
	 * imap() for invariant function parameters. Also used with izip() to create
	 * constant fields in a tuple record.
	 * 
	 * @param object
	 * @param times
	 * @return an iterator that returns object over and over again.
	 */
	public static <T> Iterator<T> repeat(T object, int times) {
		return new RepeatIterator<T>(object, times);
	}

	/**
	 * equivalent to {@link Iterators#slice}(0, stop, 1);
	 * 
	 * @param <T>
	 * @param iterator
	 * @param start
	 * @param stop
	 * @return
	 */
	public static <T> Iterator<T> slice(final Iterator<T> iterator,
			final int stop) {
		return new SliceIterator<T>(iterator, 0, stop, 1);
	}

	/**
	 * equivalent to {@link Iterators#slice}(start, stop, 1);
	 * 
	 * @param <T>
	 * @param iterator
	 * @param start
	 * @param stop
	 * @return
	 */
	public static <T> Iterator<T> slice(final Iterator<T> iterator,
			final int start, final int stop) {
		return slice(iterator, start, stop, 1);
	}

	/**
	 * Make an iterator that returns selected elements from the iterator. If
	 * start is non-zero, then elements from the iterator are skipped until
	 * start is reached. Afterward, elements are returned consecutively unless
	 * step is set higher than one which results in items being skipped. It
	 * stops at the specified position. slice() does not support negative values
	 * for start, stop, or step.
	 * 
	 * @param iterator
	 * @param start
	 * @param stop
	 * @param step
	 * @return
	 */
	public static <T> Iterator<T> slice(final Iterator<T> iterator,
			final int start, final int stop, final int step) {
		return new SliceIterator<T>(iterator, start, stop, step);
	}


	/**
	 * return a sorted Iterator in natural ascending order of T.
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T extends Comparable<? super T>> Iterator<T> sorted(
			Iterator<T> iterator) {
		List<T> list = list(iterator);
		Collections.sort(list);
		return list.iterator();
	}

	/**
	 * Return a new sorted iterator from the items in iterator.
	 * 
	 * cmp specifies a custom Comparator of K. key specifies a {@link Mapper}
	 * that is used to extract a comparison key (K) from each iterator element.
	 * reverse is a boolean value. If set to True, then the list elements are
	 * sorted as if each comparison were reversed.
	 * 
	 * @param <T>
	 * @param <K>
	 * @param iterator
	 * @param key
	 * @param reverse
	 * @return
	 */
	public static <T, K> Iterator<T> sorted(Iterator<T> iterator,
			final Comparator<? super K> cmp, final Mapper<T, K> key,
			final boolean reverse) {
		// maps T into (K,T) to perform the sort
		Mapper<T, Couple<K, T>> valueToKeyValue = new Mapper<T, Couple<K, T>>() {
			public Couple<K, T> map(T arg) {
				return new Couple<K, T>(key.map(arg), arg);
			}
		};

		// Adversely maps (K,T) into T
		Mapper<Couple<K, T>, T> keyValueToValue = new Mapper<Couple<K, T>, T>() {
			public T map(Couple<K, T> arg) {
				return arg.f1;
			}
		};

		// use comparator of K to compare (K,T)
		Comparator<Couple<K, T>> keyComparator = new Comparator<Couple<K, T>>() {

			public int compare(Couple<K, T> o1, Couple<K, T> o2) {
				return (reverse ? -1 : 1) * cmp.compare(o1.f0, o2.f0);
			}

		};

		List<Couple<K, T>> list = list(map(valueToKeyValue, iterator));
		Collections.sort(list, keyComparator);

		return map(keyValueToValue, list.iterator());
	}

	/**
	 * Return a new sorted iterator from the items in iterator. the comparator
	 * is used to sort the iterator.
	 * 
	 * @param <T>
	 * @param <K>
	 * @param iterator
	 * @param key
	 * @param reverse
	 * @return
	 */
	public static <T> Iterator<T> sorted(Iterator<T> iterator,
			Comparator<? super T> cmp) {
		List<T> list = list(iterator);
		Collections.sort(list, cmp);
		return list.iterator();
	}

	/**
	 * Return a new sorted iterator from the items in iterator. the Key Mapper
	 * is used to extract a key from T, and that key natural order is used to
	 * sort the whole iterator.
	 * 
	 * @param <T>
	 * @param <K>
	 * @param iterator
	 * @param key
	 * @param reverse
	 * @return
	 */
	public static <T, K extends Comparable<? super K>> Iterator<T> sorted(
			Iterator<T> iterator, final Mapper<T, K> key, final boolean reverse) {
		return sorted(iterator, new Comparator<K>() {

			public int compare(K o1, K o2) {
				return o1.compareTo(o2);
			}
		}, key, reverse);
	}

	
	/**
	 * Turn any Iterator of Character into a String
	 * 
	 * @param chars
	 * @return
	 */
	public static String string(Iterator<Character> chars) {
		return stringBuilder(chars).toString();
	}

	/**
	 * Turn any Iterator of Character into a StringBuilder
	 * 
	 * @param chars
	 * @return
	 */
	public static StringBuilder stringBuilder(Iterator<Character> chars) {
		StringBuilder sb = new StringBuilder();
		while (chars.hasNext())
			sb.append(chars.next().charValue());
		return sb;
	}
	
	/**
	 * Make an iterator that returns elements from the iterator as long as the
	 * predicate is true.
	 * 
	 * @param iterator
	 * @param predicate
	 * @return an iterator that returns elements from the iterator as long as
	 *         the predicate is true.
	 */
	public static <T> Iterator<T> takewhile(final Iterator<T> iterator,
			final Predicate<T> predicate) {
		return new TakeWhileIterator<T>(iterator, predicate);
	}

	/**
	 * Return n independent iterators from a single iterable.
	 * 
	 * @param iterator
	 *            the source iterator
	 * @param n
	 *            number of independent iterators
	 * @return an unmodifiable list of iterators.
	 */
	public static <T> List<Iterator<T>> tee(Iterator<T> iterator, int n) {
		// create the iterator provider
		final IteratorIterable<T> ii = new IteratorIterable<T>(iterator);

		return tuple(map(new Mapper<Integer, Iterator<T>>() {
			public Iterator<T> map(Integer arg) {
				return ii.iterator();
			}
		}, range(n)));
	}

	/**
	 * Turns any Iterator into a "tuple", here an unmodifiable {@link List}
	 * 
	 * @param iterator
	 * @return a
	 */
	public static <T> List<T> tuple(Iterator<T> iterator) {
		return Collections.unmodifiableList(list(iterator));
	}

	/**
	 * This function returns an {@link Iterator} of tuple (unmodifiable List) ,
	 * where the i-th couple contains the i-th element from each of the argument
	 * iterators. The returned iterator is truncated in length to the length of
	 * the shortest argument sequence.
	 * 
	 * Due to static typing of java, it is not possible to provide a generic
	 * length of iterator and at the same time provide mixed-type tuples,
	 * therefore every iterator must be of type <code>T</code>. To have
	 * two-mixed type use {@link Iterators#zip(Iterator, Iterator)}
	 * 
	 * 
	 * @param <T>
	 * @param iterators
	 * @return
	 */
	public static <T> Iterator<List<T>> zip(Iterator<Iterator<T>> iterators) {
		final List<Iterator<T>> iteratorList = list(iterators);
		return new ZipIterator<T>(iteratorList);
	}

	/**
	 * This function returns an {@link Iterator} of Couples, where the i-th
	 * couple contains the i-th element from each of the argument iterators. The
	 * returned iterator is truncated in length to the length of the shortest
	 * argument sequence.
	 * 
	 * Due to static typing of java, it is not possible to provide a generic
	 * length of iterator and at the same time provide mixed-type tuples.
	 * 
	 * @param iterator1
	 * @param iterator2
	 * @return
	 */
	public static <T1, T2> Iterator<Couple<T1, T2>> zip(
			final Iterator<T1> iterator1, final Iterator<T2> iterator2) {

		return new Zip2Iterator<T1, T2>(iterator1, iterator2);
	}

	// TODO product
}
