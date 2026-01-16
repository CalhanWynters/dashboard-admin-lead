package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.pageinfo;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.helpers.BaseDomainGuardTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DescriptionTest extends BaseDomainGuardTest {

    @Test
    public void testValidDescriptionCreation() {
        // Valid description
        Description description = new Description("This is a valid description.");
        assertEquals("This is a valid description.", description.text());
    }

    @Nested
    class NullChecks {
        @Test
        public void testNullDescriptionThrowsException() {
            assertThrowsDomainRuleViolation(() -> new Description(null),
                    "Description is required.", "VAL-001", "EXISTENCE");
        }
        @Test
        public void testBlankDescriptionThrowsException() {
            assertThrowsDomainRuleViolation(() -> new Description(" "),
                    "Description is blank.", "VAL-010", "TEXT_CONTENT");
        }
    }
}
