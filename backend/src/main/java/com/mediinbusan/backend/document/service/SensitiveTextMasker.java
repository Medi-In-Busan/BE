package com.mediinbusan.backend.document.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 번역 API로 내보내기 전에 원문에서 고유식별번호를 가린다.
 *
 * <p>OCR 결과 원문은 문서를 촬영한 본인에게 TLS로 돌려주는 것이라 그대로 두지만, 번역은 사정이
 * 다르다 — 원문이 통째로 Papago(NAVER Cloud)라는 <b>제3자</b>로 나간다. 앱 언어가 한국어가
 * 아니면 항상 번역을 타는데 이 기능의 주 사용자가 바로 외국인이라, 마스킹을 앱 표시 단계에만
 * 두면 주민등록번호가 매번 외부로 나간다. 번역 품질은 번호를 가려도 달라지지 않으므로
 * 여기서 잘라내는 편이 맞다.
 *
 * <p>가리는 규칙과 결과 문자열 모양은 Android {@code feature/documentscan/SensitiveTextMasking.kt}와
 * <b>동일하게 유지해야 한다</b> — 앱은 화면에 그릴 때 같은 마스킹을 한 번 더 걸고, 두 규칙이
 * 어긋나면 번역문 카드에서만 가려지는/안 가려지는 값이 생겨 사용자가 혼란스러워진다.
 */
public final class SensitiveTextMasker {

    /**
     * 숫자 사이 구분자. OCR이 하이픈 앞뒤에 공백을 끼워 넣는 표기(`900101 - 1234567`)까지 받되,
     * 가로 공백만 허용한다. 개행까지 받으면 줄 끝 6자리와 다음 줄 첫 7자리가 주민번호로 오인된다.
     */
    private static final String NUMBER_SEPARATOR = "[-.\\h]{0,3}";

    /** {@code YYMMDD-Nxxxxxx} 형태의 주민등록번호/외국인등록번호. 뒤 6자리만 가린다. */
    private static final Pattern RESIDENT_REGISTRATION_NUMBER =
        Pattern.compile("(?<!\\d)(\\d{6})" + NUMBER_SEPARATOR + "(\\d)\\d{6}(?!\\d)");

    /** 010으로 시작하는 휴대전화번호. 병원 대표번호(051-, 1588- 등)는 대상이 아니다. */
    private static final Pattern MOBILE_PHONE_NUMBER =
        Pattern.compile("(?<!\\d)(010)" + NUMBER_SEPARATOR + "\\d{4}" + NUMBER_SEPARATOR + "(\\d{4})(?!\\d)");

    private SensitiveTextMasker() {
    }

    public static String mask(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String masked = RESIDENT_REGISTRATION_NUMBER.matcher(text)
            .replaceAll(matchResult -> Matcher.quoteReplacement(matchResult.group(1) + "-" + matchResult.group(2) + "******"));
        return MOBILE_PHONE_NUMBER.matcher(masked)
            .replaceAll(matchResult -> Matcher.quoteReplacement(matchResult.group(1) + "-****-" + matchResult.group(2)));
    }
}
