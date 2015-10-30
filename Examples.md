Here are almost pure java examples. Every statement after the >>> is a pure java statement, that can produce the given results.

read the [actual working source code](http://code.google.com/p/neoitertools/source/browse/trunk/neoitertools/src/test/java/net/ericaro/neoitertools/ItertoolsTest.java).

```

>>> count();
0 , 1 , 2 , 3 , 4 , ...
>>> all(range(0, 10, 2);
true
>>> any(range(0, 10);
true
>>> chain(range(3), range(3,5));
0 , 1 , 2 , 3 , 4 , ...
>>> map(str, combinations(iter("ABCD"), 2));
AB , AC , AD , BC , BD , CD , ...
>>> count(6);
6 , 7 , 8 , 9 , ...
>>> cycle(iter("ABCD");
A , B , C , D , A , B , C , D , ...
>>> dropwhile(lt(5), iter(binomial) )
6 , 4 , 1 , ...
>>> for(Index<Character>  pair: in( enumerate(iter("ABCD")))){
...     System.out.println(pair.i+" "+pair.value);
0 A
1 B
2 C
3 D
>>> filter(pair, range(10))
1 , 3 , 5 , 7 , 9 , ...
>>> filterfalse(pair, range(10))
0 , 2 , 4 , 6 , 8 , ...
>>> map(key, groupby(iter("AAABBBCCDAABBB"), id))
A , B , C , D , A , B , ...
>>> for(int i : in(range(3)))
...			System.out.println("i");
0
1
2
[1, 2]
>>> for(Character c: in( iter("ABCD")))
...		System.out.println(c);
A
B
C
D
>>> iter(yieldFunction);
0 , 1 , 2 , 3 , 4 , 5 , ...
>>> list(range(3)) ;
[0, 1, 2]
>>> list(count(),3) ;
[0, 1, 2]
>>> map(pair,range(10) );
true , false , true , false , ...
>>> range(3)
0 , 1 , 2 , ...
>>> range(1,3)
1 , 2 , ...
>>> range(1,10,5)
1 , 6 , ...
>>> slice(repeat("Hello"), 5)
hello , hello , hello , hello , hello , ...
>>> repeat("Hello", 5)
hello , hello , hello , hello , hello , ...
>>> reversed(range(5))
4 , 3 , 2 , 1 , 0 , ...
>>> slice(count(),5)
0 , 1 , 2 , 3 , 4 , ...
>>> slice(count(),3,5)
3 , 4 , ...
>>> slice(count(),1,6,2)
1 , 3 , 5 , ...
>>> sorted(iter("ACBED"))
A , B , C , D , E , ...
>>> takewhile(count(), lt(5))
0 , 1 , 2 , 3 , 4 , ...
>>> for( Pair<Integer, Character>  p: in( zip(range(4), iter("ABCDE") )))
...		System.out.println(p.f0+" -> "+ p.f1);
0 -> A
1 -> B
2 -> C
3 -> D
```