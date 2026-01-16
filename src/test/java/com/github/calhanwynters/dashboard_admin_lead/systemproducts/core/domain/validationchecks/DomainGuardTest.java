package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainRuleViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

class DomainGuardTest {

    private final Instant fixedNow = Instant.parse("2026-01-14T20:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedNow, ZoneId.of("UTC"));

    @Nested
    @DisplayName("Temporal (Drift) Validation")
    class TemporalTests {
        @Test
        void inPast_shouldAllowSmallFutureDrift() {
            // Success Case: 400ms in future < 500ms tolerance: Pass
            Instant slightFuture = fixedNow.plusMillis(400);
            assertThatCode(() -> DomainGuard.inPast(slightFuture, "eventDate", fixedClock))
                    .doesNotThrowAnyException();

            // Failure Case: 600ms in future > 500ms tolerance: Fail
            Instant tooFarFuture = fixedNow.plusMillis(600);
            assertThatThrownBy(() -> DomainGuard.inPast(tooFarFuture, "eventDate", fixedClock))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;
                        assertThat(dre.getMessage()).contains("eventDate cannot be in the future");

                        // Assert against the Optional values
                        assertThat(dre.getErrorCode()).contains("VAL-008");
                        assertThat(dre.getViolatedRule()).contains("TEMPORAL");
                    });
        }

        @Test
        void inFuture_shouldValidateCorrectly() {
            Instant future = fixedNow.plusSeconds(10);
            assertThatCode(() -> DomainGuard.inFuture(future, "startDate", fixedClock))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Strings & Regex Validation")
    class StringTests {

        @Test
        void notBlank_shouldReturnStrippedValue() {
            String input = "  valid_input  ";
            String result = DomainGuard.notBlank(input, "username");
            assertThat(result).isEqualTo("valid_input");
        }

        @Test
        void lengthBetween_shouldStripAndValidateRange() {
            String input = "  abc  "; // stripped length is 3
            String result = DomainGuard.lengthBetween(input, 2, 5, "username");
            assertThat(result).isEqualTo("abc");

            assertThatThrownBy(() -> DomainGuard.lengthBetween(" a ", 5, 10, "username"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;
                        // Assert against the Optional return type
                        assertThat(dre.getErrorCode()).contains("VAL-002");
                        assertThat(dre.getMessage()).contains("username size must be between 5 and 10");
                    });
        }

        @Test
        void matches_shouldValidateAgainstPattern() {
            Pattern alphanumeric = Pattern.compile("^[a-zA-Z0-9]+$");

            // 1. Success Case
            assertThatCode(() -> DomainGuard.matches("User123", alphanumeric, "username"))
                    .doesNotThrowAnyException();

            // 2. Failure Case: Handle the Optional and the correct field name (violatedRule)
            assertThatThrownBy(() -> DomainGuard.matches("User_123!", alphanumeric, "username"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;
                        // Verifying the Optional values
                        assertThat(dre.getErrorCode()).contains("VAL-004");
                        assertThat(dre.getViolatedRule()).contains("SYNTAX");
                        assertThat(dre.getMessage()).contains("username format is invalid");
                    });
        }
    }

    @Nested
    @DisplayName("Numbers Validation")
    class NumberTests {

        @Test
        void bigDecimal_shouldValidateNonNegativeAndPositive() {
            // Success Case
            assertThatCode(() -> DomainGuard.nonNegative(BigDecimal.ZERO, "price"))
                    .doesNotThrowAnyException();

            // Failure Case
            assertThatThrownBy(() -> DomainGuard.positive(BigDecimal.ZERO, "price"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;
                        // Assert against the Optional return types and correct property name
                        assertThat(dre.getErrorCode()).contains("VAL-011");
                        assertThat(dre.getViolatedRule()).contains("SEMANTICS");
                        assertThat(dre.getMessage()).contains("price must be strictly positive");
                    });
        }

        @Test
        void longPositive_shouldValidateOverload() {
            // 1. Success Case
            assertThatCode(() -> DomainGuard.positive(10L, "stockCount"))
                    .doesNotThrowAnyException();

            // 2. Failure Case
            assertThatThrownBy(() -> DomainGuard.positive(0L, "stockCount"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;
                        // Verify the Optional values and the correct property name
                        assertThat(dre.getErrorCode()).contains("VAL-013");
                        assertThat(dre.getViolatedRule()).contains("RANGE");
                        assertThat(dre.getMessage()).contains("stockCount must be a positive number");
                    });
        }

        @Test
        void range_shouldEnforceInclusiveBoundaries() {
            // 1. Boundary Success (Inclusive)
            assertThatCode(() -> DomainGuard.range(10, 10, 20, "priority"))
                    .doesNotThrowAnyException();
            assertThatCode(() -> DomainGuard.range(20, 10, 20, "priority"))
                    .doesNotThrowAnyException();

            // 2. Just Outside (Failure)
            assertThatThrownBy(() -> DomainGuard.range(9, 10, 20, "priority"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;
                        assertThat(dre.getErrorCode()).contains("VAL-007");
                        assertThat(dre.getViolatedRule()).contains("RANGE");
                    });

            assertThatThrownBy(() -> DomainGuard.range(21, 10, 20, "priority"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;
                        assertThat(dre.getErrorCode()).contains("VAL-007");
                        assertThat(dre.getViolatedRule()).contains("RANGE");
                    });
        }
        @Test
        void positive_shouldValidateInteger() {
            // Success case
            assertThatCode(() -> DomainGuard.positive(10, "stockCount"))
                    .doesNotThrowAnyException();

            // Failure case: zero should throw an exception
            assertThatThrownBy(() -> DomainGuard.positive(0, "stockCount"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;
                        assertThat(dre.getErrorCode()).contains("VAL-013");
                        assertThat(dre.getViolatedRule()).contains("RANGE");
                        assertThat(dre.getMessage()).contains("stockCount must be a positive number (received: 0).");
                    });

            // Failure case: negative value should throw an exception
            assertThatThrownBy(() -> DomainGuard.positive(-5, "stockCount"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;
                        assertThat(dre.getErrorCode()).contains("VAL-013");
                        assertThat(dre.getViolatedRule()).contains("RANGE");
                        assertThat(dre.getMessage()).contains("stockCount must be a positive number (received: -5).");
                    });
        }
    }

    @Nested
    @DisplayName("Collections Validation")
    class CollectionTests {

        @Test
        void notEmpty_shouldValidateCollectionState() {
            List<String> valid = List.of("item");

            // Success Case
            assertThat(DomainGuard.notEmpty(valid, "items"))
                    .hasSize(1)
                    .containsExactly("item");

            // Failure Case
            assertThatThrownBy(() -> DomainGuard.notEmpty(List.of(), "items"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;

                        // 1. Assert against Optional return types using .hasValue() for clarity
                        assertThat(dre.getErrorCode()).hasValue("VAL-003");
                        assertThat(dre.getViolatedRule()).hasValue("COLLECTION_MIN_SIZE");

                        // 2. Updated message check to account for the punctuation in the Guard script
                        assertThat(dre.getMessage()).isEqualTo("items must contain at least one element.");
                    });
        }

        @Test
        void noNullElements_shouldDetectNulls() {
            // Using Arrays.asList because List.of() is null-hostile in Java 21+
            List<String> withNull = java.util.Arrays.asList("a", null, "c");

            assertThatThrownBy(() -> DomainGuard.noNullElements(withNull, "items"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;

                        // Assert against Optional return types
                        assertThat(dre.getErrorCode()).contains("VAL-012");
                        assertThat(dre.getViolatedRule()).contains("COLLECTION_INTEGRITY");

                        // Message check
                        assertThat(dre.getMessage()).contains("items contains null elements.");
                    });
        }

        @Test
        void notEmpty_shouldWorkWithSets() {
            Set<Integer> validSet = Set.of(1, 2, 3);

            // Works exactly like the List version
            assertThat(DomainGuard.notEmpty(validSet, "userRoles")).hasSize(3);

            assertThatThrownBy(() -> DomainGuard.notEmpty(Set.of(), "userRoles"))
                    .isInstanceOf(DomainRuleViolationException.class);
        }

        @Test
        void noNullElements_shouldWorkWithSets() {
            // HashSet allows one null element, perfect for testing the guard in 2026
            Set<String> setWithNull = new HashSet<>();
            setWithNull.add(null);

            assertThatThrownBy(() -> DomainGuard.noNullElements(setWithNull, "permissions"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;

                        // Asserting against the Optional return type
                        assertThat(dre.getErrorCode()).contains("VAL-012");
                        // Asserting against the correct property name in your Exception class
                        assertThat(dre.getViolatedRule()).contains("COLLECTION_INTEGRITY");
                    });
        }
    }

    @Nested
    @DisplayName("Generic Validation")
    class GenericTests {
        @Test
        void ensure_shouldHandleConditions() {
            // 1. Success Case: Use static call and provide all 4 parameters
            assertThatCode(() -> DomainGuard.ensure(true, "Always true", "VAL-100", "GENERIC_OK"))
                    .doesNotThrowAnyException();

            // 2. Failure Case: Verify custom metadata using satisfies to handle Optionals
            assertThatThrownBy(() -> DomainGuard.ensure(1 + 1 == 3, "Math failed", "VAL-999", "MATH_ERROR"))
                    .isInstanceOf(DomainRuleViolationException.class)
                    .satisfies(ex -> {
                        DomainRuleViolationException dre = (DomainRuleViolationException) ex;

                        // Assert against the Optional return types
                        assertThat(dre.getErrorCode()).contains("VAL-999");
                        assertThat(dre.getViolatedRule()).contains("MATH_ERROR");

                        // Message check
                        assertThat(dre.getMessage()).isEqualTo("Math failed");
                    });
        }
    }
}

