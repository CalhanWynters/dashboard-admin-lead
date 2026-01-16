package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.helpers.BaseDomainGuardTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NameTest extends BaseDomainGuardTest {

    @Nested
    class NullAndBlankChecks {

        @Test
        public void testNullNameThrowsException() {
            assertThrowsDomainRuleViolation(() -> new Name(null),
                    "Name is required.", "VAL-001", "EXISTENCE");
        }

        @Test
        public void testBlankNameThrowsException() {
            assertThrowsDomainRuleViolation(() -> new Name(" "),
                    "Name is blank.", "VAL-010", "TEXT_CONTENT");
        }
    }
}