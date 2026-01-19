package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.helpers.BaseDomainGuardTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LabelTest extends BaseDomainGuardTest {

    @Nested
    class NullAndBlankChecks {

        @Test
        public void testNullLabelValueThrowsException() {
            assertThrowsDomainRuleViolation(() -> new Label(null),
                    "Label Value is required.", "VAL-001", "EXISTENCE");
        }

        @Test
        public void testBlankLabelValueThrowsException() {
            assertThrowsDomainRuleViolation(() -> new Label(" "),
                    "Label Value is blank.", "VAL-010", "TEXT_CONTENT");
        }
    }
}