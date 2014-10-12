---
layout: user-guide
tagline: “A man who lies to himself, and believes his own lies, becomes unable to recognize truth"
image: dice-splash
title: Reproducing Test Failures
---

#Reproducing Test Failures#

Reproducibility is important when using random data within tests, fyodor currently supports this for junit.

### JUnit

The fyodor-junit module supplies a junit test rule (`uk.org.fyodor.random.FyodorTestRule`) that does two important things:

1. Enhances test failure exceptions with the seed used to generate random values
2. Allows you to control the seed for a test using the `uk.org.fyodor.random.Seed` annotation

To add this functionality to your test simply add the test rule

```java
public class SomeTest {

    @Rule
    public TestRule rule = new FyodorTestRule();

    @org.junit.Test
    public void someTest() {
        Assert.assertTrue(RDG.boolean().next());
    }
}
```

The above test should fail ~50% of the time, and when it does the failure exception looks something like:

```
java.lang.AssertionError
	...
	at uk.org.fyodor.SomeTest.someTest(SomeTest.java:17)
	...
Caused by: uk.org.fyodor.random.FailedWithSeedException: Test failed with seed 1410125843980
	...
```

Now we have the seed `1410125843980` used during this test we can apply this to our test and reproduce the failure.

```java
public class SomeTest {

    @Rule
    public TestRule rule = new FyodorTestRule();

    @Test
    @Seed(1410125843980l)
    public void someTest() {
        Assert.assertTrue(RDG.boolean().next());
    }
}
```

Executing this test produces the same exception output above, every-time.
