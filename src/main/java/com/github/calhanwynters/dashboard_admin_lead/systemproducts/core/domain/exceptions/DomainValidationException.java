package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions;

public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}