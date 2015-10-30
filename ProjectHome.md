_neoitertools_ is a port of Python [itertools](http://docs.python.org/release/2.6/library/itertools.html) algorithms and concepts to Java.

  * It implements all the generators available in itertools:
    * range
    * groupby
    * filter
    * ...
  * It implements the famous **yield** statement.
  * It provides an Helper class to provide really clean code


```
   for ( int i : in (range(10) )){
       yield (i*i);
   }
```
Read carefully, yes, this is pure Java ( I've just hidden the imports).

There is a lot [more example](Examples.md).


# How to get it #

## With Maven ##

```
<dependency>
  <groupId>net.ericaro</groupId>
  <artifactId>neoitertools</artifactId>
  <version>1.0.0</version>
</dependency>
```
It's available in the maven's central repo

To access nightly build snapshots, add the sonatype's snapshot repository: `http://oss.sonatype.org/content/repositories/snapshots`

## Download ##

> Direct [Download neoitertools-1.0.0](http://neoitertools.googlecode.com/files/neoitertools-1.0.0.jar). Or visit our [download section](http://code.google.com/p/neoitertools/downloads/list).


# History #

  * 2011-05-07 : tests+ some examples, and release the 1.0.0 to maven central
  * 2011-04-25 : move all algorithm as Generator + they have been all tested + documentation with their own wiki, plus some global documentation.
  * 2011-04-24 : added support for all available itertools function but "product"
  * 2011-04-23 : added support for **THE** yield statement, whoo both for generators and iterators
  * 2011-04-21 : added support for permutations, and combinations
  * 2011-04-20 : project inception, first beta release to maven central