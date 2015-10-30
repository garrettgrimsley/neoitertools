Python uses an [Iterator protocol](http://docs.python.org/glossary.html#term-iterator) that is quite different from the [Iterator protocol](http://download.oracle.com/javase/6/docs/api/java/util/Iterator.html) as defined in Java.

The main difference is that
  * Java forces a `hasNext` method to tell whether or not the iterator is exhausted.
  * Python has a single `next` that throws an exception as soon as the iterator is exhausted.

Both protocols are not fully compatible, that the reason why we have introduced the [generator](Generator.md) interface that is in charge to implement the Python iterator protocol into java.

# Generator  from an Iterator #

It's very easy to implement generator from Java Iterator:
```
public T next() throws NoSuchElementException {
		return iterator.next();
	}
```

# Iterator from a Generator #

The requirement made by Java to know if there is a next element ( kind of `better safe than sorry` )make things much more complicated.

The only implementation is to pre-fetch the next item so that it is possible to tell if the operation was a success or not.

But this "pre-fetching" has dangerous side effects. In particular the GroupByGenerator.