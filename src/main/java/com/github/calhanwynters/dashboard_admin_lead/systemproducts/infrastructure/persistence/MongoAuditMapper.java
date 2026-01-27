package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.CreatedAt;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.LastModified;
import org.bson.Document; // Correct import for MongoDB BSON Documents

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public final class MongoAuditMapper {

    /**
     * Maps MongoDB audit fields to Domain Value Objects.
     * Safely handles null documents and converts legacy Date types to 2026-standard OffsetDateTime.
     */
    public static AuditMetadata mapAudit(Document d) {
        if (d == null) {
            return AuditMetadata.create();
        }

        // getDate is a valid method on org.bson.Document
        Date createdAt = d.getDate("createdAt");
        Date updatedAt = d.getDate("updatedAt");

        // Convert to OffsetDateTime (UTC) to satisfy your Domain Constructors
        OffsetDateTime created = createdAt != null
                ? createdAt.toInstant().atOffset(ZoneOffset.UTC)
                : OffsetDateTime.now(ZoneOffset.UTC);

        OffsetDateTime modified = updatedAt != null
                ? updatedAt.toInstant().atOffset(ZoneOffset.UTC)
                : created;

        return AuditMetadata.reconstitute(
                new CreatedAt(created),
                new LastModified(modified)
        );
    }
}
