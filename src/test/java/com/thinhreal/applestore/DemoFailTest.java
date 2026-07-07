package com.thinhreal.applestore;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demo test class — run ONLY this class to see what a failure looks like:
 *
 *   mvn test -Dtest=DemoFailTest
 *
 * Your real tests (UserEntityTest, CategoryServiceTest) are unchanged and still pass.
 */
class DemoFailTest {

    @Test
    void demo_intentionalFailure_showsWhatFailLooksLike() {
        // Given
        String expected = "Smartphones";
        String actual = "Laptops";

        // When / Then — this assertion fails on purpose
        assertThat(actual)
                .as("Demo: expected vs actual mismatch")
                .isEqualTo(expected);
    }
}
