package com.smile.core.asserts;

import com.smile.core.reporter.Reporter;
import org.apache.commons.lang3.StringUtils;
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
        reportAssertResult(assertionError.isPresent(), formattedMessage);
    }

    public void assertNotNull(Object object, String message) {
        Optional<AssertionError> assertionError = softAssertResult(() -> Assert.assertNotNull(object, message));
        reportAssertResult(assertionError.isPresent(), message);
    }

    private void reportAssertResult(boolean isFailed, String message) {
        if (isFailed) {
            reporter.log(FAIL, message);
        } else {
            reporter.log(PASS, message);
        }
    }

    private String getEqualsFormattedMessage(Object actual, Object expected, String message) {
        return """
                %s %s
                A: %s %s
                E: %s %s
                """.formatted(StringUtils.isEmpty(message) ? "" : message, lineBreak(), actual, lineBreak(), expected, lineBreak());
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
