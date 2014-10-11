package uk.org.fyodor;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import uk.org.fyodor.rule.FyodorTestRule;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class BaseTestWithRule {

    private static boolean print = false;
    List<Object> printMe;

    @Rule
    public TestRule rule = new FyodorTestRule();

    @BeforeClass
    public static void shouldPrint() {
        print = System.getenv("DEV") != null;
    }

    @Before
    public void createPrintMe() {
        printMe = newArrayList();
    }

    @After
    public void clearPrintMe() {
        if (print) {
            for (Object o : printMe) {
                System.out.println(o);
            }
        }
    }

    protected void print(Object o) {
        printMe.add(o);
    }
}
