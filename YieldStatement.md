Python has a very very nice [yield statement](http://docs.python.org/reference/simple_stmts.html#grammar-token-yield_stmt).

Creating a Java port of python's [itertools](http://docs.python.org/release/2.7/library/itertools.html) without a support for the same yield statement would be simple nonsense.

# Getting Started #

Let assume you want to use the yield statement to generate the [Fibonacci Suite](http://en.wikipedia.org/wiki/Fibonacci_number).

This is what a Pythonista would do

```
	def fib():
	  i,j=1,1
	  yield j
	  yield i
	  while True:
	    i,j = i+j,i
	    yield i
```


Let's see how to do it in Java now, with neoitertools.


## the Body ##

Java is much more verbose than python, as it has to carry all the types all the way. So let's start focusing on the body implementation first

```
				int i=1;
				int j=1;
				yield(j);
				yield(i);
				while(true){
					i=i+j;
					j=i-j;
				    yield(i);
				}
```

It's not as _clean_ as python, but it's Java, and frankly, it's quite clean.


## the Object Definition ##

Java beeing pure object, it's not possible to define generators as function, so in neoitertools we provide a simple interface

```
	public interface Yield<R,V> {
		public void generate();
	}
```

where `R`, and `V` are used in the yield function as:
```
	public R yield(V value);
```

Then, the body previously defined has a place to be:

```
	new Yield<Void, Integer>() {
			public void generate() {
				int i=1;
				int j=1;
				yield(j);
				yield(i);
				while(true){
					i=i+j;
					j=i-j;
				    yield(i);
				}
				
			}
		}

```

## the Generator ##


[Itertools](Itertools.md) class provides both the `yield` function, and an `iter` function that turn the Yield object in a Generator. So the whole code is :

```
	Generator<Integer> g = iter(new Yield<Void, Integer>() {

			public void generate() {
				int i=1;
				int j=1;
				yield(j);
				yield(i);
				while(true){
					i=i+j;
					j=i-j;
				    yield(i);
				}
				
			}
		});
		
		System.out.println(  list(g, 10) );
```

that procudes, as expected :
` [1, 1, 2, 3, 5, 8, 13, 21, 34, 55] `

[read the full code](http://code.google.com/p/neoitertools/source/browse/trunk/neoitertools/src/test/java/net/ericaro/neoitertools/YieldTest.java).

## Advanced Features ##

The real yield statement in python can be used to receive a value from the `next` call. We also provide support for this kind of yield: see YieldGenerator class.

But this feature is not compatible with any of the standard Java interfaces (like Iterator, or Iterable ).