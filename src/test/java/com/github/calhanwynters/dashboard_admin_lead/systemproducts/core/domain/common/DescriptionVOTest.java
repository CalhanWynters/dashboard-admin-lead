package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DescriptionVOTest {

    @Test
    void constructsWithValidValue() {
        String text = "This is a valid product text.";
        DescriptionVO vo = new DescriptionVO(text);
        assertEquals(text.strip(), vo.text());
    }

    @Test
    void nullValueThrowsNpe() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> new DescriptionVO(null));

        assertEquals("Description cannot be null", ex.getMessage());
    }

    @Test
    void tooShortThrowsIllegalArgumentException() {
        String tooShort = "short";
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new DescriptionVO(tooShort));
        assertTrue(ex.getMessage().contains("at least"));
    }

    @Test
    void tooLongThrowsIllegalArgumentException() {
        String longDescription = "x".repeat(2010); // Create a string longer than allowed
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new DescriptionVO(longDescription));
        assertTrue(ex.getMessage().contains("at most"));
    }

    @Test
    void invalidCharactersThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new DescriptionVO("Valid text!@#"));
        assertTrue(ex.getMessage().contains("forbidden characters"));
    }

    @Test
    void forbiddenWordsThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new DescriptionVO("This text contains forbiddenWord1."));
        assertTrue(ex.getMessage().contains("Forbidden Words"));
    }

    @Test
    void truncateShorterThanMaxReturnsSameInstance() {
        DescriptionVO vo = new DescriptionVO("This text is fine.");
        DescriptionVO truncated = vo.truncate(100);
        assertSame(vo, truncated);
    }

    @Test
    void truncateLongerProducesEllipsizedValue() {
        DescriptionVO vo = new DescriptionVO("a".repeat(100)); // Valid long text
        DescriptionVO t = vo.truncate(10);
        assertEquals(10, t.text().length());
        assertTrue(t.text().endsWith("..."));
    }

    @Test
    void factoryMethodCreatesValidInstance() {
        DescriptionVO vo = new DescriptionVO("          A nicely trimmed text.  ");
        assertEquals("A nicely trimmed text.", vo.text());
    }

}

