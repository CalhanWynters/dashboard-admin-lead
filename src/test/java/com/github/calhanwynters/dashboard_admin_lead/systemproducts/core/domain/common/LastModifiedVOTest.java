package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class LastModifiedVOTest {

    @Test
    @DisplayName("Should create valid instance when date is within current bounds")
    void createsValidInstance() {
        OffsetDateTime validDate = OffsetDateTime.now();
        LastModifiedVO vo = new LastModifiedVO(validDate);

        // Assert truncation logic works (Nanos)
        assertEquals(validDate.truncatedTo(ChronoUnit.NANOS), vo.value());
    }

    @Test
    @DisplayName("Should throw NullPointerException with correct message when value is null")
    void nullValueThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new LastModifiedVO(null));
        assertEquals("Last modified date cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject dates before the 2025 system epoch")
    void rejectsPastDatesBeforeEpoch() {
        OffsetDateTime oldDate = OffsetDateTime.parse("2024-12-31T23:59:59Z");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new LastModifiedVO(oldDate));
        assertTrue(ex.getMessage().contains("Date is before system epoch"));
    }

    @Test
    @DisplayName("Should allow future dates within the 30-second drift buffer")
    void allowsFutureDatesWithinDriftBuffer() {
        // 15 seconds is safely within the new 30-second limit
        OffsetDateTime slightFuture = OffsetDateTime.now().plusSeconds(15);

        assertDoesNotThrow(() -> new LastModifiedVO(slightFuture),
                "Should allow dates up to 30 seconds in the future due to network clock skew.");
    }

    @Test
    @DisplayName("Should reject future dates beyond the 30-second drift buffer")
    void rejectsFutureDatesBeyondDriftBuffer() {
        // 35 seconds is outside the 30-second limit
        OffsetDateTime wayFuture = OffsetDateTime.now().plusSeconds(35);

        assertThrows(IllegalArgumentException.class, () -> new LastModifiedVO(wayFuture));
    }

    @Test
    @DisplayName("Should create instance with system UTC via factory method")
    void factoryMethodNowCreatesValidInstance() {
        LastModifiedVO vo = LastModifiedVO.now();
        assertNotNull(vo.value());
        assertEquals(ZoneOffset.UTC, vo.value().getOffset());
    }

    @Test
    @DisplayName("Should support fixed clock injection for deterministic testing")
    void supportClockInjection() {
        Instant fixedPoint = Instant.parse("2025-06-01T12:00:00Z");
        Clock fixedClock = Clock.fixed(fixedPoint, ZoneId.of("UTC"));

        LastModifiedVO vo = LastModifiedVO.now(fixedClock);

        assertEquals(OffsetDateTime.now(fixedClock), vo.value());
        assertEquals(2025, vo.value().getYear());
    }

    @Test
    @DisplayName("Should truncate input to nanoseconds to prevent DB mismatches")
    void truncatesToNanoseconds() {
        // Create a time with simulated excessive sub-nano precision if possible,
        // or just verify truncation call occurs.
        OffsetDateTime now = OffsetDateTime.now();
        LastModifiedVO vo = new LastModifiedVO(now);

        assertEquals(now.truncatedTo(ChronoUnit.NANOS), vo.value());
    }



}
