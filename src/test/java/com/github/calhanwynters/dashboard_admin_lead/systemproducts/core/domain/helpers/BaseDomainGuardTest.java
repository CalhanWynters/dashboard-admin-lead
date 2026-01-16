package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.helpers;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainRuleViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class BaseDomainGuardTest {

    protected void assertThrowsDomainRuleViolation(Runnable runnable, String expectedMessage, String expectedErrorCode, String expectedRuleName) {
        DomainRuleViolationException exception = assertThrows(DomainRuleViolationException.class, runnable::run);

        assertEquals(expectedMessage, exception.getMessage());

        // Extract the value from Optional before comparison
        assertEquals(expectedErrorCode, exception.getErrorCode().orElse(null));
        assertEquals(expectedRuleName, exception.getViolatedRule().orElse(null));
    }
}
