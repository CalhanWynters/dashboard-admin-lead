package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions;

import java.util.Optional;

/**
 * Standard domain exception for architecture in 2026.
 * This exception signals business rule violations as enforced by the DomainGuard class.
 */
public class DomainRuleViolationException extends RuntimeException {

    private final String errorCode; // Optional code representing the specific error
    private final String violatedRule; // Specific rule that was violated

    /**
     * Constructor for creating an exception with a message, error code, and violated rule.
     *
     * @param message The error message describing the violation (must not be null or empty).
     * @param errorCode An optional code representing the specific error (can be null).
     * @param violatedRule An optional string representing the rule that was violated (can be null).
     * @throws IllegalArgumentException if the message is null or empty.
     */
    public DomainRuleViolationException(String message, String errorCode, String violatedRule) {
        super(message);
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        this.errorCode = errorCode;
        this.violatedRule = violatedRule;
    }

    /**
     * Constructor for creating an exception with a message, cause, error code, and violated rule.
     *
     * @param message The error message describing the violation (must not be null or empty).
     * @param cause The cause of the exception.
     * @param errorCode An optional code representing the specific error (can be null).
     * @param violatedRule An optional string representing the rule that was violated (can be null).
     * @throws IllegalArgumentException if the message is null or empty.
     */
    public DomainRuleViolationException(String message, Throwable cause, String errorCode, String violatedRule) {
        super(message, cause);
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        this.errorCode = errorCode;
        this.violatedRule = violatedRule;
    }

    /**
     * Gets the error code for this exception.
     *
     * @return An Optional containing the error code if present; otherwise, empty.
     */
    public Optional<String> getErrorCode() {
        return Optional.ofNullable(errorCode);
    }

    /**
     * Gets the rule that was violated.
     *
     * @return An Optional containing the violated rule if present; otherwise, empty.
     */
    public Optional<String> getViolatedRule() {
        return Optional.ofNullable(violatedRule);
    }

    /**
     * Gets a diagnostic summary of the exception, including error code, rule, and message.
     *
     * @return A formatted string summarizing the diagnostic details of the violation.
     */
    public String getDiagnosticSummary() {
        return "[%s] Rule '%s' failed: %s".formatted(
                getErrorCode().orElse("N/A"),
                getViolatedRule().orElse("Unknown Rule"),
                getMessage()
        );
    }
}
