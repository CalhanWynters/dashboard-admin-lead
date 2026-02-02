package com.github.calhanwynters.dashboard_admin_lead.common;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import jakarta.persistence.Embeddable;

@Embeddable
public record Actor(String value) {

    public static final Actor SYSTEM = new Actor("SYSTEM");

    public Actor {
        // This handles null check, blank check, and returns a stripped string
        value = DomainGuard.notBlank(value, "Actor Identity");
    }

    public static Actor of(String identity) {
        return new Actor(identity);
    }
}
