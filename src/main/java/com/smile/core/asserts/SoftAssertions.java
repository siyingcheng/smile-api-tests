package com.smile.core.asserts;

import com.smile.core.reporter.Reporter;
import org.testng.Assert;

import java.util.Optional;

import static com.aventstack.extentreports.Status.FAIL;
import static com.aventstack.extentreports.Status.PASS;
import static com.smile.core.reporter.HtmlMarkup.lineBreak;

public class SoftAssertions {
    private final Reporter reporter = Reporter.getInstance();


    public void assertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, "");
    }


    public void assertEquals(Object actual, Object expected, String message) {
        Optional<AssertionError> assertionError = softAssertResult(() -> Assert.assertEquals(actual, expected, message));
        String formattedMessage = getEqualsFormattedMessage(actual, expected, message);
        if (assertionError.isPresent()) {
            reporter.log(FAIL, formattedMessage);
        } else {
            reporter.log(PASS, formattedMessage);
        }
    }

    private String getEqualsFormattedMessage(Object actual, Object expected, String message) {
        return """
                %s %s
                A: %s %s
                E: %s %s
                """.formatted(message, lineBreak(), actual, lineBreak(), expected, lineBreak());
    }

    private Optional<AssertionError> softAssertResult(SoftAssertWrapper wrapper) {
        try {
            wrapper.doAssert();
            return Optional.empty();
        } catch (AssertionError e) {
            return Optional.of(e);
        }
    }

    public void assertFalse(boolean condition) {
        assertFalse(condition, null);
    }

    public void assertFalse(boolean condition, String message) {
        if (condition) {
            reporter.log(FAIL, message);
        } else {
            reporter.log(PASS, message);
        }
    }

    public void assertTrue(boolean condition) {
        assertTrue(condition, null);
    }

    public void assertTrue(boolean condition, String message) {
        if (condition) {
            reporter.log(PASS, message);
        } else {
            reporter.log(FAIL, message);
        }
    }
}
