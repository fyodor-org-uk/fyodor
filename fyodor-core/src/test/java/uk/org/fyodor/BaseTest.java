package uk.org.fyodor;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class BaseTest {

    private static boolean print = false;
    List<Object> printMe;

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

    @BeforeClass
    public static void shouldPrint() {
        print = System.getenv("DEV") != null;
    }
}
