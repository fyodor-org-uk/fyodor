---
layout: default
tagline: "What terrible tragedies realism inflicts on people"
image: dice-splash
title: Introduction
---

#Fyodor User Guide - Introduction#

Fyodor is built on 3 main concepts

- Generators
- the RDG class
- Ranges

##Generators##
Random data is created by generators, in Fyodor these are objects implementing the `Generator` interface:

{% highlight java %}
public interface Generator<T> {
    public T next();
}
{% endhighlight %}

As you can see it's a simple interface, classes implementing it are expected to act as never-ending
iterators over an infinite collection, each call to `next()` produces a random object of 
the specified type.  For example `new StringGenerator(22)` will give me back a `Generator` that will create
a random 22 character long string with each call to `next()`:

```
Generator<String> generator = new StringGenerator(22);

for (int i = 0; i < 10; i++){
    System.out.println(generator.next());
}

{VVR}%:K<R0gR^82r>nZ\(
NS}2N/z7gCY&rmxMcK|4-7
COM\*E?jwdi5?tt%mvNI_#
;\^BO]7{0r^2q!C@x91\k/
oN=Dg+yl2R\Td49+ifk927
2lJ-]ICoRi6^mJ^$-?(t-E
ORnt91y-A`{l6@fjBOikr|
PzjvT(:%+a09wlpA>8>;_i
9@o!|Q+Zf;L`c<{Lp.7?uM
~V1fpQSYJiDN-iD7(^Q&gT
```

##RDG class##
The `RDG` class (it stands for Random Data Generators) is Fyodor's public API and what you should 
normally use to interact with it. It consists of static methods that return `Generator`s, the 
main benefit of this instead of creating new instances of `Generator`s directly is that it lets us 
manage changes to the underlying implementations without breaking the public 
API, as well as gracefully deprecating parts of the codebase as needed. 
 
One of the other benefits of using `RDG` is that you can (and should)
extend it to provide your own implementations of `Generator<T>` on top of those provided by Fyodor. 

##Ranges##
`Range<T>` is a Fyodor class that holds a lower and upper endpoint that is commonly used to create
`Generator`s that provide a range of random values. It's currently quite simple with all instances
acting as closed ranges (i.e. inclusive of lower and upper endpoints), the simplest way to create
one being the `closed(T, T)` static method. For instance `RDG.string(Range.closed(10, 20))` will 
return a generator that creates a random string between 10 and 20 characters in length with 
each call to `next()`:

```
Genrator<String> generator = RDG.string(Range.closed(10,20));

for (int i = 0; i < 10; i++){
    System.out.println(generator.next());
}
        
?z#2!qudyYEt&N
boYXS'yymAtj
Ikm,BD$|5oy;`0cK[
E,,u16c:;z
k|I{X=0rD&wN4W3Wz1pt
#E%:%t4w1!J
5yVUM76k8-VB}
WkNKrhV:VfqR\<-4:
-0([A5}kI@VW/5$fUV
aqth37yvdm8[~&
```

Next Page: [Number Generators]({{ site.baseurl }}/user-guide/generators)

