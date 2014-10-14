---
layout: default
tagline: “Talking nonsense is the sole privilege mankind possesses over the other organisms. It's by talking nonsense that one gets to the truth! I talk nonsense, therefore I'm human”
image: dice-splash
title: Collections
---

#Fyodor User Guide - Collections#

## Overview

The collection generators allow you to generate collections/arrays of values that themselves are provided by generators.

A simple example is

```java
List<Integer> integers = RDG.list(RDG.integer()).next();
```

This will produce a list with 15 (default size when not specified) elements.

Fyodor can currently generate Lists, Sets, Arrays and Maps, and each collection type may be generated without any size requirements. The default in this case is a collection of size 15.
You may provide your own fixed integer size, or you may provide a size range which will be used to generate the size randomly.

### Arrays

The generator for arrays requires the raw type so we can create a new array instance. It is the only collection generator that has this requirement.

```java
array(Class<? extends T> classOfT, Generator<? extends T> generatorOfT);
array(Class<? extends T> classOfT, Generator<? extends T> generatorOfT, int size);
array(Class<? extends T> classOfT, Generator<? extends T> generatorOfT, Range<Integer> sizeRange);
```
Example:

```java
Integer[] arrayOfIntegers = RDG.array(Integer.class, RDG.integer()).next();
```

### Lists

Generates a `java.util.LinkedList`

```java
list(Generator<? extends T> generatorOfT);
list(Generator<? extends T> generatorOfT, int size);
list(Generator<? extends T> generatorOfT, Range<Integer> sizeRange);
```

Example:

```java
List<Integer> listOfIntegers = list(RDG.integer()).next();
```

### Sets

Generates a `java.util.HashSet`

The SetGenerator will try hard to generate a set of the required size, but this may not be possible if the element generator doesn't provide enough unique values for the size requirement.
If after many attempts the set size has not been achieved, an exception will be thrown.

```java
set(Generator<? extends T> generatorOfT);
set(Generator<? extends T> generatorOfT, int size);
set(Generator<? extends T> generatorOfT, Range<Integer> sizeRange);
```

Example:

```java
Set<Integer> setOfIntegers = RDG.set(RDG.integer()).next();
```

### Maps

Generates a `java.util.HashMap`

Similar behaviour to the SetGenerator, it will aim to achieve the required size, throwing an exception if it is unable to do so.

It requires two generators, one for the key elements and one for the value elements.

```java
map(Generator<? extends K> generatorOfK, Generator<? extends V> generatorOfV);
map(Generator<? extends K> generatorOfK, Generator<? extends V> generatorOfV, int size);
map(Generator<? extends K> generatorOfK, Generator<? extends V> generatorOfV, Range<Integer> sizeRange);
```

Example:

```java
Map<Integer, Long> mapOfIntegersToLongs = RDG.map(RDG.integer(), RDG.longVal()).next();
```

### Example usages:

#### Generics and Sub-types

The element generators may be one that generates an instance extending type `T`, for example:

```java
abstract class Person {
}

class Employee extends Person {
}

class Dilbert extends Employee {
}

class Manager extends Person {
}
```

`T` in the following example is `Person`, where `Employee`, `Dilbert` and `Manager` are sub-types:

```java
List<Person> people = RDG.list(MyRDG.<Person>employee()).next();

List<Person> people = RDG.list(MyRDG.<Person>manager()).next();

List<Person> dilberts = RDG.list(MyRDG.<Person>dilbert()).next();
```

#### Generating complex collections

The list generators can be composed to create more complex collections, where the elements are themselves collections. 
For example a table, or a map of strings to lists.

```java
List<List<Integer>> table = RDG.list(RDG.list(RDG.integer())).next();

Map<String, List<Integer>> integersByString = RDG.map(RDG.string(), RDG.list(RDG.integer())).next();
```

#### Using custom generators

Combined with your own custom generators, you can generate interesting collections of your own objects.

```java
List<Thundercat> thundercats = RDG.list(MyRDG.thundercat()).next();
```

### Caveats:
1. Lists are generated (including all elements) on each call to `next()`, be careful with the size of lists you choose the generate.
2. Set and Map generators will try and achieve full-size, and will throw an exception if they cannot do so.

Next Page: [Joda DateTime Generators]({{ site.baseurl }}/user-guide/joda-date-time)