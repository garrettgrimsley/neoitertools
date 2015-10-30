# Structure #

## Top Level Classes ##

neoitertools is based on Top level interfaces and classes, lying in the top level package net.ericaro.neoitertools:

  * Protocol definition interface: interfaces that are used to define a [protocol](AboutGenerators.md) for iterators.
    * [Generator](Generator.md) : define the simplest generator protocol shared between java and python
    * YieldGenerator : defines the python generator protocol. But it can be used to implements java Iterators or Iterable
    * [Lambda](Lambda.md) : the way to implement a lambda statement. until java7 and the promised Functor, it's the only way to do.
    * [Operator](Operator.md) : operator ( like in reduce)
    * [Yield](Yield.md) : to implement generator based on the [yield statement](YieldStatement.md)
  * Immutable classes used as helper in interface definitions
    * [Index](Index.md) : java way to handle enumerate
    * [Pair](Pair.md)  : a 2-tuple with mixed types.
  * [Itertools](Itertools.md) the Helper class that defines all the idiomatic functions that makes neoitertools really natural for Pythonistas.


## Sub Packages ##

  * net.ericaro.neoitertools.**generators** : contains all the [generators](AboutGenerators.md) used to implement the main class [Itertools](Itertools.md) . It's possible to use them independently of [Itertools](Itertools.md).
  * net.ericaro.neoitertools.generators.**combinatorics** : contains classes for the non trivial algorithms, of permutations, subsets etc. They are design to work as Generator of `int[]`, and therefore can be reused for other purposes.
  * net.ericaro.neoitertools.generators.**primitives** : contains generated classes to handle generator from primitive type arrays.

# Goals #

We have decided that the best way to port itertools was
  * first to implement, and test, in Java the algorithms, and the protocols that make itertools so successful.
  * second, to provide an Helper class that mimics itertools tricks, and help making the code as clean as possible.
  * third, to provide pure Java/Typed Langage tricks to help writing good idiomatic codes.

We believe that the first goal is nearly fulfilled. But, the second goal is not complete, because
  * coping with Types leads to very long and ugly Type definition
  * coping with the full-object thing of Java leads to useless code
> > for instance, this Java code
```
           List<Integer> list = list( map(new Lambda<Integer, Integer>(){
    		public Integer map(Integer arg) {
    				return arg*arg;
    			}}, range(5)));
```
> > is equivalent to
```
    list ( map (lambda x: x*x,  range(5) )
```
> > or even worst using the generator idiom that's hard to port:
```
    [ x*x for x in range(5) ]
```
  * The third goal is only starting with the addition of `iter` methods relative to what can be seen as sequences in java  ( arrays , string, iterator etc.)



