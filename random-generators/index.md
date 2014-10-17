---
layout: default
image: dice-splash
tagline: “Much unhappiness has come into the world because of bewilderment and things left unsaid”
title: Random Data Generators
---

#Random Data Generators and Builders#
Setting up fixtures - the objects and data you're testing against - for your tests can be difficult, time-consuming and error-prone
especially in large Enterprise, legacy or unfamiliar (or all three!) systems.
Once we start writing integration style tests where our objects may be persisted, (de)serialized or sent over the wire
then things can really start to become challenging and frustrating.

* **Data** affecting our test can potentially be all over the object graph, having to create
multiple objects correctly, each with their own fields, types and idiosyncracies can be frustrating 
as well as a minefield.
* **Extra code** in our tests concerned with fixture details just adds noise to the test: 
it makes it harder to understand, more prone to human error and adds to the maintenance burden.
* **False-negatives:** To get valid fixtures we might well have to understand and set up data and/or other 
objects that our test just doesn’t care about, otherwise our tests fail for reasons unrelated 
to our test scenario
* **False-positives:** Tests might unwittingly pass because we’ve left fields empty that we don’t 
(think we) care about e.g. our test only works properly because something is null or false etc.

###For Example...###

Let’s imagine a simple car that we want to create for a test:

{% highlight java %}
public class Car {
    String name;
    String description;
    String productCode;
    Manufacturer manufacturer;
    LocalDate dateOfManufacture;
    BigDecimal price;
    Engine engine;
}
{% endhighlight %}
And some typical test code to create one:

{% highlight java %}
public class SomeTest {
    private final LocalDate DATE_OF_MANUFACTURE = new LocalDate().minusYears(10);
    private Car car;

    @Before
    public void setUpCar() {
        car = new Car();
        car.setDateOfManufacture(DATE_OF_MANUFACTURE);
    }
}
{% endhighlight %}

Our test's only interested in the age of our car but already some questions spring to mind
about the rest of it:

* if we don't populate name and description will it get persisted/serialized etc. OK? 
Do I need to start creating "TestCar1", "TestCar2" etc.? How important is it to have a productCode?
What format is it in, if any? Are there any rules about valid characters and string sizes?
* what's the Engine?  And just what's involved with making an Engine object anyway?
* I’m not interested in price here but what is it, is it always the same currency regardless 
of manufacturer? What if my car doesn't have a price or it's zero? How many different code paths can my car 
end up going down because of different prices?
* You can imagine how quickly this complexity escalates with real objects in real codebases.

<small>(And on top of all this we might not even care about the car anyway, we might just need an 
old car to set something else up)</small>

###The Builder###

Using Builders to create our fixtures can address some of these issues:

* All the fields in our object are sensibly pre-populated (where it makes sense to do so)
* In our test code we only need to override the contents of the fields that we care about so readers
can quickly pick up what's important in the context of the test
* We can provide a fluent interface to create our object, hopefully expressing a lot of semantic meaning to 
a reader of the test.
* We can reuse them throughout our test suite.  This lets us
 * write tests more quickly, letting us concentrate on stuff that matters
 * gives our tests a consistent structure, making them more readable
 * encapsulate rules and logic specific to objects in one place (e.g. allowed characters in the
 name of a car)

{% highlight java %}
public class CarBuilder {
    String name = "SuperBadDog";
    String description = "Baddest Super Dog";
    String productCode = "HUFS-SH7E9BE-743-D";
    Manufacturer manufacturer = Manufacturer.TOYOTA;
    LocalDate dateOfManufacture = new LocalDate().minusYears(10);
    BigDecimal price = new BigDecimal(20000);
    Engine engine = EngineBuilder.engineBuilder().build();
    
    private CarBuilder(){}
    
    public static CarBuilder carBuilder() {
        return new CarBuilder();
    }
    
    public static CarBuilder expensiveCarBuilder() {
        CarBuilder builder = carBuilder();
        return builder.withPrice(100000);
    }
    
    public CarBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public CarBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
     
    public CarBuilder withManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }
    
    public CarBuilder withDateOfManufacture(LocalDate date) {
        this.dateOfManufacture = date;
        return this;
    }
    
    public CarBuilder withAge(Integer age) {
        return withDateOfManufacture(new LocalDate().minusYears(age);
    }
    
    public CarBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
    
    public CarBuilder withPrice(Integer price) {
        return withPrice(new BigDecimal(price));
    }
    
    public CarBuilder withEngine(Engine engine) {
        this.engine = engine;
        return this;
    }
    
    public Car build(){
        Car car = new Car();
        car.setName(name);
        car.setDescription(description);
        car.setManufacturer(manufacturer);
        car.setDateOfManufacture(dateOfManufacture);
        car.setPrice(price);
        car.setEngine(engine);
        return car;
    }
}
{% endhighlight %}

This makes our test setup look a bit nicer and takes away some of the concerns we were having:

{% highlight java %}
public class SomeTest {
    private final Integer AGE = 10;
    private Car car;

    @Before
    public void setUpCar(){
        car = CarBuilder.carBuilder().withAge(AGE).build();
    }
}
{% endhighlight %}

A nice feature of builders is that you can add and overload methods to make your fixture setup 
simpler, more readable and more meaningful:

* We’ve overloaded `withPrice()` so you can supply a simple `Integer` 
to avoid the boilerplate involved with using `BigDecimal`s in the test code
* The `setDateOfManufacture(LocalDate)` method is accompanied by an `aged(Range)` method so you can make your test code simpler and more readable by avoiding the boilerplate of massaging a `LocalDate`
* You can provide multiple static constructor methods to convey meaning in your test code as well
 as standardising data set up for common scenarios, here we have an `expensiveCarBuilder()` as 
 well as the bog-standard `carBuilder()` 
* Finally you may have noticed that the `engine` field is populated initially with … an `EngineBuilder`!

###Better Data###

We’ve made our test setup a lot better with the builder but we can do better with the quality 
of the data. At the moment every test that uses our builder is going to get a Car with exactly 
the same default attributes which may create its own false-positive problem for us.

What we need is random data:

{% highlight java %}
public class CarBuilder {
    private String name = RDG.string(15).next();
    private String description = RDG.string(50).next();
    private String productCode = RDG.productCode().next();
    private Manufacturer manufacturer = RDG.values(Manufacturers.values());
    private Date dateOfManufacture = RDG.localDate(aged(closed(years(3), years(15)))).next();
    private BigDecimal price = RDG.bigDecimal(50000).next();
    private Engine engine = EngineBuilder.engineBuilder().build();
    ...
}
{% endhighlight %}

Well this looks exciting! Let’s take a look at some of this `RDG` (RandomDataGenerator) class:

{% highlight java %}
public class RDG {

    public static Generator<Integer> integer = integer(Integer.MAX_VALUE);

    public static Generator<Integer> integer(Integer max) {
        return new IntegerGenerator(max);
    }

    public static Generator<Integer> integer(Range<Integer> range) {
        return new IntegerGenerator(range);
    }

    public static Generator<String> string = string(30);

    public static Generator<String> string(Integer max) {
        return new StringGenerator(max);
    }
    ...
}
{% endhighlight %}

It’s basically a big collection of static methods returning `Generator<T>` objects. `Generator<T>`s
are the core of Fyodor, a simple interface with one method `next()`, expecting
implementing classes to be effectively a never-ending iterator of random values:

{% highlight java %}
public interface Generator<T> {
    public T next();
}
{% endhighlight %}

Let's take a look at the `IntegerGenerator`:

{% highlight java %}
class IntegerGenerator implements Generator<Integer> {

    private final Integer min;
    private final Integer max;

    IntegerGenerator(Integer max) {
        this.max = max;
        this.min = 0;
    }

    IntegerGenerator(Range<Integer> range) {
        this.min = range.lowerBound();
        this.max = range.upperBound();
    }

    @Override
    public Integer next() {
        return randomValues().randomInteger(min, max);
    }
}
{% endhighlight %}

Quite self-explanatory hopefully but notice the call to `randomValues()` in its `next()` 
implementation, this is Fyodor's internal way of managing the source of randomness for its generators. 
 This gives it the ability to reproduce test failures or any other scenario by using a specific 
 seed value - using random data to create more effective tests is all well and good but it can 
 be frustrating to see intermittent failures and have no way to track them down.

Finally, notice in our builder that we're populating our product code with a call to `RDG.productCode()`
to get a `Generator<String>`.  The product code generator is actually a bespoke generator written for
our `Car`'s specially-formatted product code:

{% highlight java %}
public class ProductCodeGenerator implements Generator<String> {

    private Generator<String> firstBit = RDG.string(5, CharacterSetFilter.LettersOnly);
    private Generator<String> secondBit = RDG.string(7, CharacterSetFilter.LettersAndDigits);
    private Generator<Integer> number = RDG.integer(Range.closed(100, 999));
    private Generator<String> lastBit = RDG.string(1, "DXZ");

    public String next() {
        return String.format("%s-%s-%d-%s",
                firstBit.next().toUpperCase(),
                secondBit.next().toUpperCase(),
                number.next(),
                lastBit.next());
    }
}
{% endhighlight %}

By extending Fyodor's core `RDG` class with our own we can seamlessly add bespoke generators specific to our
domain.  

The methods on the `RDG` class form Fyodor's public API for creating random data generators,
check out <a href="{{ site.baseurl }}/user-guide">the user guide</a> for what it can do, how to use it and extending it to create your own generators.
