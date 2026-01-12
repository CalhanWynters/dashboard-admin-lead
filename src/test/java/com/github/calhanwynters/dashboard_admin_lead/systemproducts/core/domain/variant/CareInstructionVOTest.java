package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CareInstructionVO: Hardened Domain Validation (2026 Standard)")
public class CareInstructionVOTest {
    @Nested
    @DisplayName("Length & Safety Buffer Constraints")
    class LengthConstraints {

        @Test
        @DisplayName("Should fail immediately if raw input exceeds 750 characters (Safety Buffer)")
        void safetyBufferFailure() {
            String excessiveInput = "A".repeat(751);
            assertThatThrownBy(() -> new CareInstructionVO(excessiveInput))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Input raw data exceeds safety buffer.");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 4, 501})
        @DisplayName("Should fail if processed length is outside [5, 500]")
        void lengthBoundaries(int length) {
            String input = "- " + "x".repeat(Math.max(0, length - 2));
            assertThatThrownBy(() -> new CareInstructionVO(input))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Prefix Style-Locking (Bullet Consistency)")
    class PrefixStyleLocking {

        @ParameterizedTest
        @ValueSource(strings = {"-", "*", "•", "1.", "15."})
        @DisplayName("Happy Path: Consistent prefix styles should pass")
        void consistentStyles(String prefix) {
            String input = prefix + " Line 1\n" + prefix + " Line 2";
            assertThatNoException().isThrownBy(() -> new CareInstructionVO(input));
        }

        @Test
        @DisplayName("Should fail specifically on mixed styles with a descriptive message")
        void mixedStylesFailure() {
            String mixedInput = "- Line 1\n* Line 2";

            assertThatThrownBy(() -> new CareInstructionVO(mixedInput))
                    .isInstanceOf(IllegalArgumentException.class)
                    // This now tests that the CONSISTENCY logic was triggered
                    .hasMessageContaining("Prefix style mismatch")
                    .hasMessageContaining("Line 2");
        }

        @Test
        @DisplayName("Should fail if the first line has no valid prefix at all")
        void missingInitialPrefix() {
            String invalidStart = "Clean with soap"; // No bullet

            assertThatThrownBy(() -> new CareInstructionVO(invalidStart))
                    .isInstanceOf(IllegalArgumentException.class)
                    // This tests that the INITIAL STYLE detection logic was triggered
                    .hasMessageContaining("must start with a valid bullet");
        }
    }

    @Nested
    @DisplayName("Lexical Security & Unicode Support")
    class LexicalSecurity {

        @Test
        @DisplayName("Should allow Unicode/International characters and safe punctuation")
        void allowedCharacters() {
            String input = "• Lâvage à 30°C (Drying: [Low]!)";
            assertThatNoException().isThrownBy(() -> new CareInstructionVO(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {"<script>", "{hash}", "drop table;", "proxy => gate"})
        @DisplayName("Should reject potential injection characters (<, >, {, }, ;)")
        void injectionRejection(String malicious) {
            String input = "- Safe content " + malicious;
            assertThatThrownBy(() -> new CareInstructionVO(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Instructions contain forbidden characters.");
        }
    }

    @Nested
    @DisplayName("Normalization & Robustness")
    class Normalization {

        @Test
        @DisplayName("Should strip leading/trailing whitespace and treat as equivalent")
        void whitespaceNormalization() {
            var vo1 = new CareInstructionVO("- Clean Only");
            var vo2 = new CareInstructionVO("  - Clean Only  ");

            assertThat(vo1).isEqualTo(vo2);
            assertThat(vo1.instructions()).isEqualTo("- Clean Only");
        }

        @ParameterizedTest
        @CsvSource({
                "'- Line 1\\n- Line 2', '- Line 1\\r\\n- Line 2'"
        })
        @DisplayName("Should handle OS-specific line endings consistently")
        void lineEndingRobustness(String unix, String windows) {
            var voUnix = new CareInstructionVO(unix.replace("\\n", "\n"));
            var voWin = new CareInstructionVO(windows.replace("\\r\\n", "\r\n"));

            assertThat(voUnix).isEqualTo(voWin);
        }
    }
}
