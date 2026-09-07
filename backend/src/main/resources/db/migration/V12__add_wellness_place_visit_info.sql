-- 장소 상세 화면의 "방문 정보" 6칸.
--
-- 지금까지 wellness_place에 저장한 TourAPI 값은 areaBasedList2(이름·주소·좌표·사진)와
-- detailCommon2에서 뽑은 tel/overview 둘뿐이었다. 정작 방문자가 알아야 하는 영업시간·휴무일·
-- 대표메뉴·이용요금·주차는 detailIntro2(콘텐츠 타입별로 필드명이 다른 별도 오퍼레이션)에 있는데
-- 한 번도 호출하지 않아서, 상세 화면이 "사진 + 이름 + 지도"에서 더 나아가지 못했다.
--
-- 부산맛집정보(getFoodKr)도 마찬가지로 영업시간(USAGE_DAY_WEEK_AND_TIME)·대표메뉴(RPRSNTV_MENU)·
-- 홈페이지(HOMEPAGE_URL)를 같이 내려주는데, 그동안 대표메뉴만 description 앞에 "대표메뉴: ..."로
-- 이어붙이고 나머지는 버렸다 — 문자열로 접합돼 있어 화면에서 따로 뽑아 쓸 수가 없었다.
--
-- 왜 6칸으로 정규화하는가: 소스마다(그리고 TourAPI는 콘텐츠 타입마다) 필드명이 전부 다르지만
-- 화면에 그릴 항목은 같다. 원본 필드명을 그대로 컬럼으로 두면 타입 수만큼 컬럼이 늘고 화면이
-- "관광지면 usetime, 음식점이면 opentimefood"를 알아야 한다 — 그 차이는 수집기(WellnessVisitInfo)가
-- 흡수하고 여기서부터는 6칸으로만 흐르게 한다.
--
-- 기존 행은 전부 NULL이다: 값이 채워지려면 ingest(POST /api/wellness/ingest)를 다시 돌려야 한다.
-- 이미 description에 "대표메뉴: ..."가 접합돼 저장된 부산맛집 행도 그 재수집 때 정리된다
-- (updateFrom이 description을 통째로 새 값으로 덮어쓴다) — SQL로 문자열을 잘라내지 않는 이유는
-- H2(MySQL 모드)와 실제 MySQL에서 문자열 함수 동작이 갈려 마이그레이션이 환경별로 달라지기 때문이다.
ALTER TABLE wellness_place ADD COLUMN business_hours VARCHAR(500);
ALTER TABLE wellness_place ADD COLUMN rest_date VARCHAR(300);
ALTER TABLE wellness_place ADD COLUMN signature_menu VARCHAR(500);
ALTER TABLE wellness_place ADD COLUMN usage_fee VARCHAR(500);
ALTER TABLE wellness_place ADD COLUMN parking_info VARCHAR(500);
ALTER TABLE wellness_place ADD COLUMN homepage_url VARCHAR(500);
