---
layout: default
title:  "Setting up fixtures to test against"
date:   11-11-2013
category: automated testing
excerpt: One of the challenges testing a system with a large domain and a complex data model is setting up fixtures to test against.  Once we move beyond unit tests and want to create real objects with real data - either as part of the setup stage of an automated test or for manually testing - then things become more challenging.
image: what-is-scrum.gif
homepageStyle: 2
featured: yes
---




###Problem###

One of the challenges testing a system with a large domain and a complex data model is setting up fixtures to test against.

Once we move beyond unit tests and want to create real objects with real data – either as part of the setup stage of an automated test or for manually testing – then things become more challenging:

* The data items we care about for the test may not all live on the object; our test might be interested in the payroll cut-off date for an employee for instance, but concerning ourselves with the details of where that data goes and the object graph needed to correctly set it all up is error prone and time consuming
* Extra code in our tests concerned with fixture details just adds noise to the test: it makes it harder to understand and adds to the maintenance burden.
* False-negatives: To get valid fixtures we might well have to set up other bits of data and other objects that our test just doesn’t care about, otherwise our tests fail for reasons unrelated to our test scenario
* False-positives: Tests might unwittingly pass because we’ve left fields empty that we don’t (think we) care about for the purposes of our test e.g. our test only works properly because something is null or false etc.

###Initial Solution###

As an example let’s imagine a simple employee that we want to create for our test:

<!--code-->
public class Employee {
String firstname;
String surname;
LocalDate dateOfBirth;
BigDecimal basicSalary;
Address homeAddress;
}

And some typical test code to create one:

<!--code-->
public class EmployeeTest {
private final LocalDate DATE_OF_BIRTH = new LocalDate().minusYears(50);
private Employee employee;
@Before
public void setUpEmployee(){
employee = new Employee();
employee.setDateOfBirth(DATE_OF_BIRTH);
}
}

For our test purposes we’re only interested in the age of our employee, but you can see we’re already getting bogged down with concerns about the rest of the data on our employee:

* What if the database requires non-null values for firstname and surname?
* What if the address is required too? Just what’s involved with making an Address object?
* Will it matter if the salary is not set to anything? I’m not interested in salary here but maybe I should make something up in case having a null salary makes something weird happen elsewhere.
* You can imagine how quickly this complexity escalates with real code.

###Improvement - The Builder###

Using the Builder Pattern to create our fixtures can address some of these issues:

* All the fields in our object are pre-populated
* We only need to override the contents of the fields that we care about
* Use method chaining ending with build() to create our object.

<!--code-->
public class EmployeeBuilder {
private LocalDate dateOfBirth = new LocalDate().minusYears(30);
private String firstname = “Firstname”;
private String surname = “Surname”;
private BigDecimal basicSalary = new BigDecimal(20000);
private Address homeAddress = AddressBuilder.addressBuilder().build();
private EmployeeBuilder(){}

public static EmployeeBuilder employeeBuilder(){
return new EmployeeBuilder();
}

public EmployeeBuilder firstname(String firstname) {
this.firstname = firstname;
return this;
}

public EmployeeBuilder surname(String surname) {
this.surname = surname;
return this;
}

public EmployeeBuilder age(int age) {
this.dateOfBirth = new LocalDate().minusYears(age);
return this;
}

public EmployeeBuilder dateOfBirth(int age) {
this.dateOfBirth = new LocalDate().minusYears(age);
return this;
}

public EmployeeBuilder basicSalary(BigDecimal salary){
this.basicSalary = basicSalary;
return this;
}

public EmployeeBuilder basicSalary(Integer salary) {
this.basicSalary = new BigDecimal(salary);
return this;
}

public EmployeeBuilder homeAddress(Address address) {
this.homeAddress = homeAddress;
return this;
}

public Employee build(){
Employee employee = new Employee();
employee.setFirstname(firstname);
employee.setSurname(surname);
employee.setDateOfBirth(dateOfBirth);
employee.setBasicSalary(basicSalary);
employee.setHomeAddress(homeAddress);
return employee;
}
}

This makes our test setup look a bit nicer and takes away some of the concerns we were having:

public class EmployeeTest {
private final Integer AGE = 50;
private Employee employee;
@Before
public void setUpEmployee(){
employee = EmployeeBuilder.employeeBuilder().age(AGE).build();
}
}

A nice feature of builders is that you can add and overload methods to make fixture setup simpler:

* We’ve overloaded basicSalary() so you can supply a simple Integer and it will convert it to the required BigDecimal for you
* The dateOfBirth(LocalDate) method is accompanied by an age(Integer) method so you can make your test code simpler and more readable by avoiding the boilerplate of massaging a LocalDate
* Finally you may have noticed that the homeAddress field is populated initially with … an AddressBuilder!

###Better Data###

We’ve made our test setup a lot better with the builder but we can do better with the quality of the default data. At the moment every test that uses our builder is going to get an Employee with exactly the same default attributes which may create its own false-positive problem for us.

What we need is random data:

public class EmployeeBuilder {
private Date dateOfBirth = Random.pastDate(years(17), years(64)).next();
private String firstname = Random.string(50).next();
private String surname = Random.string(50).next();
private BigDecimal basicSalary = Random.bigDecimal(1000000).next();
private Address homeAddress = AddressBuilder.addressBuilder().build();
…
}

Well this looks exciting! Let’s take a look at the Random class:

public class Random {
public static Generator<String> string = new StringGenerator(10);
    public static Generator<Integer> integer = new IntegerGenerator(Integer.MAX_VALUE);
        public static Generator<BigDecimal> bigDecimal = new BigDecimalGenerator(999999, 2);
            …
            }

            It’s basically a collection of static Generator<T> members and some static helper methods.

                Let’s look at the Generator<T> and the IntegerGenerator classes:

                    public abstract class Generator<T> implements Iterator<T> {
                        @Override
                        public boolean hasNext() {
                        return true;
                        }
                        @Override
                        public void remove() {
                        throw new UnsupportedOperationException();
                        }
                        }

                        class IntegerGenerator extends Generator<Integer> {
                            private static java.util.Random random = new java.util.Random();
                            private Integer max;
                            public IntegerGenerator(Integer max) {
                            this.max = max;
                            }
                            @Override
                            public Integer next() {
                            return random.nextInt(max);
                            }
                            }

                            Generator<T> is a never-ending abstract Iterator<T> implementation leaving it up to the extending classes to implement next().

                                IntegerGenerator simply returns a new random integer between 0 and the specified maximum for each call to next(). You can see the default version provided by Random.integer uses Integer.MAX_VALUE when it creates it and it also provides a utility method to create your own IntegerGenerator with a different maximum if needed:

                                public static Generator<Integer> integer(Integer max) {
                                    return new IntegerGenerator(max);
                                    }


                                    And that is the general idea with all the other types we want to generate.

                                    The default Random.string implementation creates a StringGenerator that returns a 10-character long random string, Random.string(Integer) will give you a StringGenerator that generates a random string of the given length if you need it. There are also generators for dates, selecting enums at random and so on.

                                    Generating data with tighter formatting is also a useful addition for tests - here’s an email address generator:

                                    public class EmailAddressGenerator extends Generator<String> {
                                        @Override
                                        public String next() {
                                        return format("%s@%s.%s", Random.string(10).next(), Random.string(10).next(), Random.values("com", "co.uk", "gov.uk" , "org", "net").next());
                                        }
                                        }

                                        A postcode generator:

                                        public class PostcodeGenerator extends Generator<String> {
                                            @Override
                                            public String next() {
                                            return format("%s%s%01d %01d%s",
                                            random(1, "ABCDEFGHIJKLMNOPRSTUWYZ"),
                                            random(1, "ABCDEFGHKLMNOPQRSTUVWXY"),
                                            Random.integer(9).next(),
                                            Random.integer(9).next(),
                                            random(2, "ABDEFGHJLNPQRSTUWXYZ"));
                                            }
                                            }

                                            And a random URI generator:

                                            public class UriGenerator extends Generator<URI> {
                                                @Override
                                                public URI next() {
                                                try {
                                                return new URI(String.format("http://%s.%s", Random.string.next(), Random.values("com", "co.uk", "org").next()));
                                                } catch (URISyntaxException e) {
                                                throw new RuntimeException(e);
                                                }
                                                }
                                                }