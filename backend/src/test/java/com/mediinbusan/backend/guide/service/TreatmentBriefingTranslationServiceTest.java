package com.mediinbusan.backend.guide.service;

import com.mediinbusan.backend.document.client.PapagoTranslationApiException;
import com.mediinbusan.backend.document.client.PapagoTranslationAuthenticationException;
import com.mediinbusan.backend.document.client.PapagoTranslationClient;
import com.mediinbusan.backend.guide.dto.TreatmentBriefingTranslateRequest;
import com.mediinbusan.backend.guide.dto.TreatmentBriefingTranslateResponse;
import com.mediinbusan.backend.guide.exception.UnsupportedTranslationLanguageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreatmentBriefingTranslationServiceTest {

    @Mock
    private PapagoTranslationClient papago;

    private TreatmentBriefingTranslationService service;

    @Test
    void 필드별로_개별_번역해_반환한다() {
        service = new TreatmentBriefingTranslationService(papago);
        when(papago.translate("Dermatology consultation", "en", "ko")).thenReturn("피부과 상담");
        when(papago.translate("I have had a headache since yesterday.", "en", "ko")).thenReturn("어제부터 두통이 있습니다.");
        when(papago.translate("Penicillin", "en", "ko")).thenReturn("페니실린");
        when(papago.translate("Ibuprofen", "en", "ko")).thenReturn("이부프로펜");
        when(papago.translate("Please speak slowly.", "en", "ko")).thenReturn("천천히 말씀해 주세요.");

        TreatmentBriefingTranslateResponse response = service.translate(new TreatmentBriefingTranslateRequest(
            "en", "Dermatology consultation", "I have had a headache since yesterday.",
            "Penicillin", "Ibuprofen", "Please speak slowly."
        ));

        assertThat(response.visitPurpose()).isEqualTo("피부과 상담");
        assertThat(response.symptoms()).isEqualTo("어제부터 두통이 있습니다.");
        assertThat(response.allergy()).isEqualTo("페니실린");
        assertThat(response.medication()).isEqualTo("이부프로펜");
        assertThat(response.memo()).isEqualTo("천천히 말씀해 주세요.");
    }

    @Test
    void zh는_Papago_언어코드_zh_CN으로_변환된다() {
        service = new TreatmentBriefingTranslationService(papago);
        when(papago.translate(any(), any(), any())).thenReturn("번역됨");

        service.translate(new TreatmentBriefingTranslateRequest("zh", "목적", "", "", "", ""));

        verify(papago).translate("목적", "zh-CN", "ko");
    }

    @Test
    void source가_ko이면_Papago를_호출하지_않고_원문을_그대로_반환한다() {
        service = new TreatmentBriefingTranslationService(papago);

        TreatmentBriefingTranslateResponse response = service.translate(new TreatmentBriefingTranslateRequest(
            "ko", "피부과 상담", "두통", "없음", "없음", "메모"
        ));

        assertThat(response.visitPurpose()).isEqualTo("피부과 상담");
        assertThat(response.symptoms()).isEqualTo("두통");
        verify(papago, never()).translate(any(), any(), any());
    }

    @Test
    void 빈_필드나_null_필드는_Papago를_호출하지_않는다() {
        service = new TreatmentBriefingTranslationService(papago);
        when(papago.translate(eq("Ibuprofen"), any(), any())).thenReturn("이부프로펜");

        TreatmentBriefingTranslateResponse response = service.translate(new TreatmentBriefingTranslateRequest(
            "en", "", null, "  ", "Ibuprofen", null
        ));

        assertThat(response.visitPurpose()).isEmpty();
        assertThat(response.symptoms()).isNull();
        assertThat(response.allergy()).isEqualTo("  ");
        assertThat(response.medication()).isEqualTo("이부프로펜");
        assertThat(response.memo()).isNull();
        verify(papago, times(1)).translate(any(), any(), any());
    }

    @Test
    void 번역_인증_실패시_해당_필드는_원문을_반환한다() {
        service = new TreatmentBriefingTranslationService(papago);
        when(papago.translate(any(), any(), any())).thenThrow(new PapagoTranslationAuthenticationException("인증 실패"));

        TreatmentBriefingTranslateResponse response = service.translate(new TreatmentBriefingTranslateRequest(
            "en", "Dermatology consultation", "", "", "", ""
        ));

        assertThat(response.visitPurpose()).isEqualTo("Dermatology consultation");
    }

    @Test
    void 번역_API_실패시_해당_필드는_원문을_반환한다() {
        service = new TreatmentBriefingTranslationService(papago);
        when(papago.translate(any(), any(), any())).thenThrow(new PapagoTranslationApiException("호출 실패"));

        TreatmentBriefingTranslateResponse response = service.translate(new TreatmentBriefingTranslateRequest(
            "ja", "皮膚科相談", "", "", "", ""
        ));

        assertThat(response.visitPurpose()).isEqualTo("皮膚科相談");
    }

    @Test
    void 지원하지_않는_sourceLang은_거부된다() {
        service = new TreatmentBriefingTranslationService(papago);

        assertThatThrownBy(() -> service.translate(new TreatmentBriefingTranslateRequest(
            "fr", "목적", "", "", "", ""
        ))).isInstanceOf(UnsupportedTranslationLanguageException.class);
        verify(papago, never()).translate(any(), any(), any());
    }

    @Test
    void sourceLang이_없으면_거부된다() {
        service = new TreatmentBriefingTranslationService(papago);

        assertThatThrownBy(() -> service.translate(new TreatmentBriefingTranslateRequest(
            null, "목적", "", "", "", ""
        ))).isInstanceOf(UnsupportedTranslationLanguageException.class);
    }
}
