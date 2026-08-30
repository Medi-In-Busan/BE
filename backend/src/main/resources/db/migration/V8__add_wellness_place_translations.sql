-- 부산맛집정보(getFoodEn/getFoodJa/getFoodZhs) 다국어 응답을 저장하기 위한 컬럼.
-- Hospital의 description_en/zh/ja(V4)와 같은 규칙을 따른다: 원문(한국어) name/address/description은
-- 컬럼명 그대로 두고, 번역이 있는 언어만 _en/_zh/_ja 컬럼에 채운다(없으면 NULL — 조회 시 ko로 폴백,
-- WellnessDtoMapper 참고). 중국어는 이 앱이 지원하는 4개 언어(core/datastore/SupportedLanguage.kt:
-- KO/EN/ZH/JA) 규칙에 맞춰 간체(getFoodZhs) 하나만 받는다 — 번체(getFoodZht)는 대응하는 언어 슬롯이
-- 없어 수집 대상에서 제외한다.
ALTER TABLE wellness_place ADD COLUMN name_en VARCHAR(200);
ALTER TABLE wellness_place ADD COLUMN name_zh VARCHAR(200);
ALTER TABLE wellness_place ADD COLUMN name_ja VARCHAR(200);
ALTER TABLE wellness_place ADD COLUMN address_en VARCHAR(300);
ALTER TABLE wellness_place ADD COLUMN address_zh VARCHAR(300);
ALTER TABLE wellness_place ADD COLUMN address_ja VARCHAR(300);
ALTER TABLE wellness_place ADD COLUMN description_en TEXT;
ALTER TABLE wellness_place ADD COLUMN description_zh TEXT;
ALTER TABLE wellness_place ADD COLUMN description_ja TEXT;
