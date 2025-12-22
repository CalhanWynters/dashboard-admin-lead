package com.github.calhanwynters.dashboard_admin_lead;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularityTests {
    static ApplicationModules modules = ApplicationModules.of(DashboardAdminLeadApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
}