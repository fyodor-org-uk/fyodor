package uk.org.fyodor;

import org.junit.Rule;
import org.junit.rules.TestRule;
import uk.org.fyodor.rule.FyodorTestRule;

public class BaseTestWithRule {

    @Rule
    public TestRule rule = new FyodorTestRule();
}
