package com.mediinbusan.backend.document.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SensitiveTextMaskerTest {

    @Test
    @DisplayName("주민등록번호는 뒤 6자리를 가린다")
    void masksResidentRegistrationNumber() {
        assertThat(SensitiveTextMasker.mask("주민등록번호 000926-3812347")).isEqualTo("주민등록번호 000926-3******");
        assertThat(SensitiveTextMasker.mask("900101 - 1234567")).isEqualTo("900101-1******");
        assertThat(SensitiveTextMasker.mask("0009263812347")).isEqualTo("000926-3******");
    }

    @Test
    @DisplayName("휴대전화번호는 가운데 네 자리를 가린다")
    void masksMobilePhoneNumber() {
        assertThat(SensitiveTextMasker.mask("전 화 : 01028367417")).isEqualTo("전 화 : 010-****-7417");
        assertThat(SensitiveTextMasker.mask("010-1234-5678")).isEqualTo("010-****-5678");
    }

    @Test
    @DisplayName("병원 대표번호와 일반 숫자는 건드리지 않는다")
    void keepsNonIdentifyingNumbers() {
        assertThat(SensitiveTextMasker.mask("전 화 : 051-240-2000")).isEqualTo("전 화 : 051-240-2000");
        assertThat(SensitiveTextMasker.mask("대표번호 1588-7000")).isEqualTo("대표번호 1588-7000");
        assertThat(SensitiveTextMasker.mask("병록번호 12257")).isEqualTo("병록번호 12257");
    }

    @Test
    @DisplayName("줄이 다르면 서로 무관한 숫자로 본다")
    void doesNotJoinNumbersAcrossLines() {
        String text = "요양기관번호  123456\n1234567 원장";

        assertThat(SensitiveTextMasker.mask(text)).isEqualTo(text);
    }
}
