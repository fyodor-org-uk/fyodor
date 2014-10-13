---
layout: user-guide
tagline: “Talking nonsense is the sole privilege mankind possesses over the other organisms. It's by talking nonsense that one gets to the truth! I talk nonsense, therefore I'm human”
image: dice-splash
title: Joda DateTime
---

#Fyodor User Guide - Joda DateTime#

### `LocalDate`

#### Overview

The simplest way to generate a date is using:

```java
LocalDate date = RDG.localDate().next();
```

This will give you a date between 01/01/0000 and 31/12/2999 which isn't always very useful, so you can customise the range of possible dates using a `uk.org.fyodor.jodatime.range.LocalDateRange`:

```java
LocalDate date = RDG.localDate(LocalDateRange.closed(LocalDate.now().minusDays(14), LocalDate.now().plusDays(14))).next();

```

The default lower-bound and upper-bound for `LocalDateRange` is 01/01/0000 to 31/12/2999, and although the range may hold values outside of these constraints, 
when generating local-dates from this range the bounds will be limited to these constraints.

#### Getting more sophisticated

Although these methods serve a purpose, their use is often un-helpful (entire date-range) or a bit clumsy (specified date range). 
To make these generators easier to use there are convenient static factory methods on `LocalDateRange` which will help when constructing ranges.

To generate a future or past date

```java
LocalDate future = RDG.localDate(LocalDateRange.inTheFuture()).next();
LocalDate past = RDG.localDate(LocalDateRange.inThePast()).next();
```

The future date will be from tomorrow up to 31/12/2999, and the past date will be from 01/01/0000 up to yesterday.

You can also generate future/past dates with the `greaterThan` and `lessThan` static factory methods:

```java
LocalDate future = RDG.localDate(LocalDateRange.greaterThan(LocalDate.now())).next();
LocalDate past = RDG.localDate(LocalDateRange.lessThan(LocalDate.now())).next();
```

#### Ages

Often it is useful to generate a date of birth, or some date in the past that is some age, or is between some age-range. You can achieve this easily with the `aged` static factory method:

```java
LocalDate dateOfBirth = RDG.localDate(LocalDateRange.aged(Range.closed(Years.years(18), Years.years(68)))).next();
```

You can do the same with the `org.joda.time.Months` and `org.joda.time.Days` but you cannot mix days, months and years within a single range method.

```java
LocalDate past = RDG.localDate(LocalDateRange.aged(Range.closed(Months.months(18), Months.months(36)))).next();
LocalDate past = RDG.localDate(LocalDateRange.aged(Range.closed(Days.days(7), Days.days(14)))).next();
```

The `Range.fixed` method works for these periods too, and allows you to create a date that is an exact number of years, months or days old

```java
LocalDate past = RDG.localDate(LocalDateRange.aged(Range.fixed(Years.years(1)))).next();
LocalDate past = RDG.localDate(LocalDateRange.aged(Range.fixed(Months.months(1)))).next();
LocalDate past = RDG.localDate(LocalDateRange.aged(Range.fixed(Days.days(7)))).next();
```

#### Caveats
* Future dates greater than today always start tomorrow, and past dates less than today always end yesterday. When constructing ranges greater-then or less-than some date the difference *is always 1 day*.
* The smallest date that can be generated is 01/01/0000 and the largest is 31/12/2999. A range outside of these bounds will be limited to these bounds.
* `Years`, `Months` and `Days` cannot be mixed in a single range method as they cannot be compared to each other.
