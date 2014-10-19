---
layout: default
tagline: “Talking nonsense is the sole privilege mankind possesses over the other organisms. It's by talking nonsense that one gets to the truth! I talk nonsense, therefore I'm human”
image: dice-splash
title: Generators
---

#Fyodor User Guide - Number Generators#

Fyodor has generators for `Integer`, `BigDecimal`, `Long` and `Double` types.

##Integer##
`RDG` provides the following methods that return a `Generator<Integer>`:

{% highlight java %}
RDG.integer();
RDG.integer(int);
RDG.integer(Range<Integer>);
{% endhighlight %}

`RDG.integer().next()`  generates a random integer ranging from 
`Integer.MIN_VALUE` to `Integer.MAX_VALUE`.

`RDG.integer(int).next()` generates a random integer between 0 and the integer suppied.

`RDG.integer(Range<Integer>).next()` generates a random integer in the supplied `Range<Integer>`.

##BigDecimal##
`RDG` provides the following methods that return a `Generator<BigDecimal>`:

{% highlight java %}
RDG.bigDecimal();
RDG.bigDecimal(double);
RDG.bigDecimal(long);
RDG.bigDecimal(BigDecimal);
RDG.bigDecimal(Range<BigDecimal>);
RDG.bigDecimal(Range<BigDecimal>, int);
{% endhighlight %}

`RDG.bigDecimal().next()` generates a `BigDecimal` ranging from `Double.MIN_VALUE` to `Double.MAX_VALUE`

`RDG.bigDecimal(double).next()` generates a `BigDecimal` with a scale of 2 ranging from 0 to the `double` supplied.

`RDG.bigDecimal(long).next()` generates a `BigDecimal` with a scale of 2 ranging from 0 to the `long` supplied.

`RDG.bigDecimal(BigDecimal).next()` generates a `BigDecimal` with a scale of 2 ranging from 0 to the `BigDecimal` supplied.

`RDG.bigDecimal(Range<BigDecimal>).next()` generates a `BigDecimal` with a scale of 2 in the `Range<BigDecimal>` supplied.

`RDG.bigDecimal(Range<BigDecimal>, int).next()` generates a `BigDecimal` in the `Range<BigDecimal>` supplied with a scale of the `int` supplied.

##Double##
`RDG` provides the following methods that return a `Generator<Double>`:

{% highlight java %}
RDG.doubleVal();
RDG.doubleVal(double);
RDG.doubleVal(Range<Double>);
{% endhighlight %}    

`RDG.doubleVal().next()` generates a `Double` ranging from `Double.MIN_VALUE` to `Double.MAX_VALUE`

`RDG.doubleVal(double).next()` generates a `Double` ranging from 0 to the `double` supplied.

`RDG.doubleVal(Range<Double>).next()` generates a `Double` in the `Range<Double>` supplied.

##Long##

`RDG` provides the following methods that return a `Generator<Long>`:

{% highlight java %}
RDG.longVal();
RDG.longVal(long);
RDG.longVal(Range<Long>);
{% endhighlight %}    

`RDG.longVal().next()` generates a `Long` ranging from `Long.MIN_VALUE` to `Long.MAX_VALUE`

`RDG.longVal(long).next()` generates a `Long` ranging from 0 to the `long` supplied.

`RDG.longVal(Range<Long>).next()` generates a `Long` in the `Range<Long>` supplied.



<br/>
Next Page: [String Generators]({{ site.baseurl }}/user-guide/string-generators)