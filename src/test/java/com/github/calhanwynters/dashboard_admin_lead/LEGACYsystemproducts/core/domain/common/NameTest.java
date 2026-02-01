package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.helpers.BaseDomainGuardTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NameTest extends BaseDomainGuardTest {

    @Nested
    class NullAndBlankChecks {

        @Test
        public void testNullNameThrowsException() {
            assertThrowsDomainRuleViolation(() -> new Name(null),
                    "featuresName is required.", "VAL-001", "EXISTENCE");
        }

        @Test
        public void testBlankNameThrowsException() {
            assertThrowsDomainRuleViolation(() -> new Name(" "),
                    "featuresName is blank.", "VAL-010", "TEXT_CONTENT");
        }
    }
}