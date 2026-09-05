package com.mediinbusan.app.feature.documentscan

/**
 * OCR로 읽어낸 문서 텍스트에서 민감한 식별번호를 가린다.
 *
 * 진단서·처방전에는 주민등록번호(외국인등록번호)와 본인 휴대전화번호가 그대로 인쇄돼 있는데,
 * 이 화면은 병원 창구에서 화면을 남에게 보여주며 쓰는 용도라 기본값이 노출이면 곤란하다.
 * 서버에는 원문이 저장되지 않으므로(backend/document는 길이만 로깅한다) 남은 노출 경로는
 * 화면과 클립보드 둘뿐이고, 둘 다 여기서 가린다. 사용자가 눈 아이콘으로 직접 해제할 수 있다.
 *
 * 가리는 대상을 최소한으로 묶은 이유:
 * - 주민등록번호: 유일 식별자이고 이 앱의 목적(문서 읽기·번역)에는 전혀 필요 없다.
 * - 010 휴대전화: 본인 번호다. 반면 병원 대표번호(051-, 1588- 등)는 환자가 실제로 걸어야 하므로
 *   가리지 않는다 — 010 접두사가 그 둘을 가르는 기준이다.
 * 그 외(주소, 이름, 병명)는 문서를 읽는 목적 자체라 가리지 않는다.
 */

/**
 * `YYMMDD-Nxxxxxx` 형태의 주민등록번호/외국인등록번호. 뒤 6자리만 가리고 성별 자리까지는 남긴다
 * (공공기관 마스킹 관례와 같다). 앞뒤 lookaround로 더 긴 숫자열의 일부를 잘라 먹지 않게 막는다.
 */
private val ResidentRegistrationNumber = Regex("""(?<!\d)(\d{6})[-\s]?(\d)\d{6}(?!\d)""")

/** 010으로 시작하는 휴대전화번호. 하이픈/공백은 OCR이 붙였다 뗐다 하므로 선택으로 둔다. */
private val MobilePhoneNumber = Regex("""(?<!\d)(010)[-.\s]?\d{4}[-.\s]?(\d{4})(?!\d)""")

fun maskSensitiveText(text: String): String =
    text
        .replace(ResidentRegistrationNumber) { match ->
            "${match.groupValues[1]}-${match.groupValues[2]}${"*".repeat(6)}"
        }
        .replace(MobilePhoneNumber) { match ->
            "${match.groupValues[1]}-****-${match.groupValues[2]}"
        }
