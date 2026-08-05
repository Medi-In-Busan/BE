-- 122건 큐레이션 병원 데이터 시딩 (db/seed/hospitals.json 기준, PowerShell 스크립트로 생성)
-- _specialtyHint_doNotTrust, contact 래핑 등 신뢰 불가/불필요한 필드는 이 시딩에서 제외한다.

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('1', '123', '더큰아이소아청소년과의원', 'CLINIC', '부산광역시 해운대구 해운대로 794 (좌동, 엘리움) 201호~203호 더큰아이소아청소년과의원', 35.1688205, 129.175752, '0507-1307-7880', 'https://thebigchild.mycafe24.com', '월 09:00-18:30 / 화 09:00-18:30 / 수 09:00-18:30 / 목 09:00-18:30 / 금 09:00-18:30 / 토 09:00-15:00 / 일 휴진 (평일 점심시간 13:00-14:00, 토요일 13:00-13:30, 매년 5월 1일 근로자의 날 휴무)', '모든 어린이의 바른 성장을 도와드립니다. 일반진료·학생검진은 2층, 성장클리닉은 9층에서 진행합니다. 토·일·공휴일 소아청소년과 전문의 진료 및 예방접종, 성인 진료/수액 치료 가능. 성장·성조숙증클리닉 전화예약 051-711-7878 (평일 10시~18시), 학생검진·영유아검진은 네이버 예약 이용.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('2', '125', '부산미남병원', 'HOSPITAL', '부산광역시 동래구 아시아드대로 213 (온천동, 류엔미즈메디컬) 부산미남병원 11층 행정실', 35.2033139, 129.0661278, '051-501-8288', 'https://youtube.com/@부산미남병원', '월~금 09:00-18:00 (접수마감 17:30) / 토 09:00-13:00 (접수마감 12:30) / 일요일 격주 정기휴무, 08/15 광복절 휴무 (점심시간 13:00-14:00, 공휴일 휴진)', '지하철 미남역 1번출구(도보 200m)에 위치한 관절·척추 중점진료 병원. 비수술 우선치료 원칙. 정형외과·신경외과·내과 진료. 관절(어깨/무릎/로봇인공관절/족부족관절/고관절/주관절/골절/인대파열), 척추(비수술우선치료/양방향내시경수술/최소침습척추수술), 내과(골다공증/당뇨병/고혈압/고지혈증/건강검진). 재활치료센터(도수치료/물리치료/운동치료/체외충격파치료), 국가건강검진(위·대장 내시경).', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('3', '126', '힘내라병원', 'HOSPITAL', '부산광역시 동구 범일로 85 (범일동, 삼미85 빌딩) 범일로 85', 35.1377451, 129.0589825, '0507-1471-9501', 'http://www.himnaera.co.kr/', '월 09:00-18:00 / 화~금 09:00-17:30 (점심시간 12:30-13:30) / 토 09:00-13:00 / 일 휴진 (08/15 광복절 휴무)', '부산 정형외과 힘내라병원은 척추·관절질환을 겪는 환자들을 대상으로 2015년부터 연구와 논문을 통해 적합한 치료법을 찾아 질 높은 의료 서비스를 제공하고 있습니다. 전화문의 051-711-9500.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '중동'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('4', '127', '넬의원', 'CLINIC', '부산광역시 부산진구 서면로 60 (부전동) 센텀메디컬타워 1층', 35.15612861189069, 129.05802660906062, '0507-1330-1717', 'https://www.instagram.com/busan.nell_clinic/', '월·수·목 10:00-19:00 / 화·금 10:00-21:00 / 토 10:00-15:00 (점심시간 13:00-14:00) / 일 휴진 (08/15 광복절 휴무)', '자신감 있는 피부, 아름다움을 위한 선택 서면피부과 넬의원. 피부 상태 분석 후 맞춤 솔루션 제안. 진료·시술과목: 줄기세포, 리프팅, 필러, 보톡스, 레이저, 스킨부스터, 맞춤피부클리닉, 바디클리닉, 제모, 맨즈케어. 보유 장비: 써마지FLX, 울쎄라, 리팟레이저, 인모드, 버츄, 올리지오 등.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('5', '128', '맘모스 헤어라인의원', 'CLINIC', '부산광역시 부산진구 중앙대로 664 (부전동, 이랜드 PEER 서면) 2층 맘모스헤어라인의원', 35.15198223650285, 129.0595970915131, '0507-1419-7883', 'https://tinyurl.com/bd26863u', '월~금 10:00-19:00 (점심시간 12:00-13:00, 접수마감 18:30) / 토 09:00-15:00 (접수마감 14:30) / 일 휴진', '환자에게 가장 옳은 것을 먼저 생각하는 헤어라인·모발이식 전문의원. 헤어라인 수술은 자연스러움을 우선하며, 정수리 모발이식은 탈모 진행과 약물치료 반응을 충분히 확인 후 진행. LDM Triple, Needle RF 등 최신 장비 활용. 원장이 상담부터 수술까지 직접 책임. 전화상담 051-817-7878.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('6', '129', '신세계안과의원', 'CLINIC', '부산광역시 부산진구 가야대로 783-1 (부전동) 신세계안과 3.4.5층', 35.157921755422564, 129.05738249217484, '0507-1447-8290', 'http://www.cleareye.co.kr/', '월~금 09:30-17:30 (점심시간 12:30-14:00) / 토 09:30-12:30 / 일 휴진 (08/15 광복절, 08/17 대체공휴일 휴무)', '백내장수술, 라식, 라섹, 렌즈삽입술, 노안수술뿐 아니라 녹내장·안구건조증 등 안과 외래진료 진행. 오픈수술실 운영, 1:1 맞춤 시력교정수술 추천, 당일 검사·당일 수술 가능.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, '몽골');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('7', '130', '밝은성모안과의원', 'CLINIC', '부산광역시 부산진구 중앙대로 723 (부전동) 5층, 6층', 35.157165147974425, 129.05839772359224, '051-804-1522', 'https://oklasikbusan.com/', '월·화·수·금 10:00-18:00 (점심시간 12:30-14:00, 접수마감 17:30) / 목 휴진 / 토 09:00-14:30 (접수마감 14:00) / 일 휴진 (08/15 광복절, 08/17 대체공휴일 휴무)', '2000년 개원, 26년 역사의 라식·라섹 전문 안과. 근시 퇴행 재수술 평생 무료 보장. 스마트라식·투데이라섹·드림렌즈·노안교정·백내장 등 진료.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('8', '131', '제우스남성의원', 'CLINIC', '부산광역시 부산진구 서면문화로 10 (부전동, 영광도서) 10층 제우스남성의원', 35.158840795207595, 129.05759140388938, '0507-1404-8575', 'https://www.youtube.com/@zeusclinic', '월~금 09:30-17:30 / 토 09:30-14:30 / 일 휴진 (08/15 광복절, 08/17 대체공휴일 휴무, 1·3·5주 토요일은 수술상담만 가능)', '발기부전, 음경확대술, 귀두확대술, 고주파 조루수술, 정관·포경수술, 전립선비대증 등 비뇨기과 전반 및 남성체형(여유증, 지방흡입 등) 전문. 전직원 남성 구성, 에어샤워 소독 시스템. 문의 010-4051-8275 / 051-817-8575.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('9', '133', '굿모닝성모안과의원', 'CLINIC', '부산광역시 부산진구 새싹로 2, 굿모닝성모안과의원 4F ~ 6F', 35.15861087449089, 129.05908883399474, '051-809-3131', 'https://eyedoc.co.kr', '월~금 09:00-18:00 (점심시간 13:00-14:00, 접수마감 17:30) / 토 09:00-13:00 (접수마감 12:30) / 일 휴진', '2000년 개원한 1세대 안과. 본원(서면역13번출구)에서 외래진료·검사, 분원(서면역15번출구)에서 수술 진행. 백내장, 녹내장, 망막질환, 소아안과, 안구건조증 등 진료 및 라식·라섹·스마일라식·렌즈삽입술 등 수술.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('10', '134', '갤럭시성형외과', 'CLINIC', '부산광역시 부산진구 서면로68번길 1 (부전동) 9층', 35.15687240978119, 129.05805685310372, '0507-1496-1242', 'http://icogps.co.kr/', '월·화·목 10:00-19:00 (접수마감 18:30) / 수·금 10:00-20:00 (접수마감 19:30) / 토 10:00-16:00 (접수마감 15:30) / 일 휴진 (08/15 광복절 휴무)', '28년차 성형외과 전문의가 이끄는 부산·경남 대표 성형외과. 수술실·진료실 CCTV 운영으로 대리수술 우려 해소. 눈·코·재수술·안면윤곽·가슴성형·지방흡입·리프팅·필러·보톡스 등 진료, 일본·중국·베트남·러시아·태국어 무료 통역 지원.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('11', '135', '라인업치과병원', 'DENTAL_HOSPITAL', '부산광역시 부산진구 서면문화로 26 (부전동) 상가3층', 35.1596980231424, 129.05634433564634, '0507-1413-6363', 'http://lineupdent.co.kr/', '목·금 09:30-19:00 / 토 09:30-17:00 / 일 휴진 (08/17, 08/23 휴무 — 평일 정확한 진료시간은 홈페이지 재확인 필요)', '서면역 9번출구 위치. 구강악안면외과 전문의·치과마취통증의학박사가 진료하는 병원급 치과. 임플란트(3D CT 기반), 치아교정(인비절라인 등), 양악수술·안면윤곽, 수면치과치료, 사랑니발치, 보철·보존치료 등 전 진료과목 원내에서 케어. 입원실 운영으로 수술 후 회복 지원. 상담문의 051-802-6363.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL'), (@hid, 'PLASTIC_SURGERY'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('12', '138', '바른병원', 'HOSPITAL', '부산광역시 서구 보수대로 9 (충무동1가) 4층', 35.09771873363063, 129.0249558625573, '0507-1387-0119', 'http://www.barunosh.co.kr', '월~금 09:00-19:00 (점심시간 12:30-13:30, 접수마감 18:30) / 토 09:00-14:00 (점심시간 12:00-12:30, 접수마감 13:30) / 일 휴진 (내과·신경과 진료스케줄은 별도 문의)', '2011년부터 이어온 관절·척추 중심 병원. 대학병원 출신 정형외과 의료진의 세분화된 맞춤 진료, 신경과·내과·건강검진까지 특화. 24시간 간호인력의 보호자 없는 병동 운영, 로봇인공관절수술 장비 마코(MAKO) 도입.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('13', '139', 'OK의원', 'CLINIC', '부산광역시 사하구 낙동대로 416 (당리동) OK 의원', 35.10409035862561, 128.97295392475326, '0507-1345-7785', 'https://www.instagram.com/ok_medical_/', '월~금 09:00-17:00 (점심시간 13:00-14:00) / 토 09:00-13:00 / 일 휴진 (08/15 광복절, 08/17 대체공휴일 휴무)', '정형외과·내과·외과 복합 진료 의원. 외과(하지정맥류, 치질, 유방 등), 내과(위·대장내시경, 건강검진), 정형외과(통증·관절·척추 비수술 치료) 협진.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('14', '140', '유케이성형외과', 'CLINIC', '부산광역시 해운대구 해운대로 365 (우동) 6층 유케이성형외과', 35.170419687369545, 129.13743616526054, '051-710-8250', 'http://ukps.co.kr', '월·화·수·금 09:30-18:30 (점심시간 13:00-14:00, 접수마감 18:00) / 목 14:00-20:00 (접수마감 19:30) / 토 10:00-15:00 (접수마감 14:30) / 일 휴진 (08/15 광복절, 08/17 대체공휴일 휴무)', '해운대 성형외과. 눈성형(쌍꺼풀·상하안검·재수술), 코성형, 가슴성형, 동안성형(이마거상), 리프팅(실리프팅·슈링크·울쎄라·써마지), 필러·보톡스·스킨부스터 등 진료.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('15', '142', '부산자생한방병원', 'KOREAN_MEDICINE_HOSPITAL', '부산광역시 사하구 낙동남로 1427 (하단동, 일성빌딩) 6-9층', 35.10664616520833, 128.967730786104, '1577-0007', 'https://busan.jaseng.co.kr', '월~금 09:00-20:00 / 토 09:00-18:00 (점심시간 13:00-14:00, 접수마감 12:00) / 일 휴진 (진료마감: 평일 18:30, 토·공휴일 17:00)', '한방 비수술 척추디스크치료 전문. 설립이념: 긍휼지심(矜恤之心)의 의료철학. 진료과목: 한방재활의학과, 침구과, 한방내과, 한방부인과, 한방이비인후피부과, 영상의학과. 주요질환: 척추디스크·허리디스크·척추관협착증, 목디스크·일자목, 턱관절장애, 관절척추 퇴행성질환 등. 치료법: 추나요법, 동작침, 신바로한약/약침, 도수치료 등. 국내 최다 척추 환자가 선택한 한방병원(2022년 기준 230만명).', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE'), (@hid, 'SKIN_BEAUTY'), (@hid, 'OBSTETRICS_GYNECOLOGY'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, '몽골');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('16', '145', '삼성리한여성의원', 'CLINIC', '부산광역시 해운대구 해운대로 802 (좌동, 웅신시네아트) 2층 203호', 35.16913331163789, 129.17664471850986, '0507-1352-0107', 'http://rehan.co.kr/', '월·수·금 09:30-18:30 (접수마감 18:00) / 화·목 09:30-20:00 (접수마감 19:30) / 토 09:30-13:30 (접수마감 13:00) (점심시간 13:00-14:00) / 일 휴진 (08/15~16, 08/17 대체공휴일 휴무)', '해운대산부인과 삼성리한여성의원. 삼성서울병원 출신 산부인과 전문의 박주영 대표원장의 1:1 맞춤 진료. 여성성형(소음순·대음순수술, 질축소술 등), 여성 타이트닝 시술, 브라질리언 레이저 제모, 피부/비만 클리닉, 부인과 진료·검진(자궁경부암, 질염, 임신초음파 등), 갱년기 호르몬 치료 등. 매주 화·목 오후 8시까지 야간진료.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY'), (@hid, 'OBSTETRICS_GYNECOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('17', '148', 'K성형외과의원', 'CLINIC', '부산광역시 부산진구 범일로 186 (범천동) 3층, 4층', 35.146666687725535, 129.05962511691683, '051-638-1004', 'https://blog.naver.com/sominhwang', '외래진료 09:00-22:00 (점심 12:30-13:30 또는 17:00-18:00, 요일별 상이) / 외상진료 365일 09:00-22:00 연중무휴 (정확한 시간은 홈페이지 확인)', '외상 봉합 중심 야간진료(밤 10시까지) 운영. 미용재건성형(안검하수, 안검내반, 비중격만곡증 등 보험수술 가능), 얼굴골절(코·안와·상하악골절), 피부종양, 리프팅, 지방성형, 흉터레이저 등 진료. 입원실·마취전문의 상주·CT/X-ray 구비. 재건진료(외상)는 당일예약 불가.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('18', '149', '새론의원', 'CLINIC', '부산광역시 연제구 중앙대로 1077 (연산동, 상계빌딩) 5층', 35.184327791611864, 129.07984552354674, '0507-1373-1616', 'http://thesaeron.kr', '월·금 10:00-19:00 / 화·목 10:00-21:00 (야간진료, 점심시간 13:00-14:00) / 수·일 휴진 / 토 09:00-13:00 (08/15 광복절 휴무)', '줄기세포·재생의학 기반 항노화 전문의원(보건복지부 지정 첨단재생의료 실시기관). 피부 항노화(PRP·BMAC·SVF 및 지방이식), 탈모클리닉(성장인자·PRP·줄기세포), 비수술 관절재생치료, 항노화 정맥시술 등 진료.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('19', '150', '서면좋은사람안과의원', 'CLINIC', '부산광역시 부산진구 서면문화로 10 (부전동, 영광도서) 15, 16층', 35.158840795207595, 129.05759140388938, '051-711-6600', 'https://www.goodpersoneye.com/', '월·화·목·금 09:00-18:00 (점심시간 13:00-14:30) / 수 09:00-13:00 / 토 09:00-14:00 / 일 휴진', '국가보훈부 지정 보훈위탁병원, 병무청 지정 병역명문가예우시설, 보건복지부 지정 외국인환자유치기관, 부산보훈병원 협력의료기관. 첨단 의료장비와 임상 검증을 거친 정교한 진단·수술 제공.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('20', '151', '메디우먼여성의원', 'CLINIC', '부산광역시 해운대구 해운대로 602 (우동, 해피투모로우마린) 메디우먼 1층,2층,5층', 35.162262250022174, 129.15728359269553, '051-731-4800', 'http://www.mediwoman.com/', '월 09:00 - 18:00, 13:00 - 14:00 휴게시간, 17:30 접수마감 / 화 09:00 - 18:00, 13:00 - 14:00 휴게시간, 17:30 접수마감 / 수 09:00 - 18:00, 13:00 - 14:00 휴게시간, 17:30 접수마감 / 목 09:00 - 18:00, 13:00 - 14:00 휴게시간, 17:30 접수마감 / 금 09:00 - 18:00, 13:00 - 14:00 휴게시간, 17:30 접수마감 / 토 09:00 - 13:00, 12:30 접수마감 / 일 정기휴무 (매주 일요일), 08/15 광복절 휴무', '- 5층 : 부인과 / 여성질환클리닉 / 여성성형클리닉 / 임신중절 / 유방 갑상선외과 / 자궁근종 하이푸 클리닉 / 건강검진센터 / 피부 관리 클리닉 / 탈모 클리닉 / 체형 관리 클리닉 - 4층 : 수술실 / 시술실 / 입원실 해운대에서 20년 동안 지켜온 메디우먼여성의원. 25년차 산부인과 전문의 백동훈 대표원장, 자궁근종/자궁선근증 비수술치료 하이푸시술, 유방/갑상선 이홍주 원장, 피부 클리닉 운영.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY'), (@hid, 'OBSTETRICS_GYNECOLOGY'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('21', '152', '라라피부과의원', 'CLINIC', '부산광역시 해운대구 APEC로 17, 센텀리더스마크 4층 라라피부과의원', 35.165932204360644, 129.13241020896632, '0507-1346-7057', 'http://lalaskinclinic.com/', '월 10:00-20:00 (점심 13:00-14:30) / 화~금 동일 / 토 10:00-15:00 / 일 정기휴무, 08/15 광복절, 08/17 대체공휴일 휴무', '강민철 대표원장. 해운대 센텀 위치. 대학교수 출신 피부과전문의. 센텀 최대규모 질환센터, 고압산소치료 가능, 미용센터 분리 운영.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('22', '67', '그린한의원', 'KOREAN_MEDICINE_CLINIC', '부산 수영구 수영로 382 (남천동, 광림빌딩) 4층 그린한의원', 35.140595503071765, 129.1067605840964, '0507-1359-1163', 'http://greenhani.kr/', '월~금 09:00-19:00 (점심 13:00-14:00, 접수마감 18:00) / 토 09:00-13:00 / 일 정기휴무', '그린한의원에 방문하시면 전통 한방 쑥뜸(소쿠리뜸/장뜸)을 경험하실 수 있습니다. 사상체질에 따른 진단 및 처방 가능(동의수세보원 근거).', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('23', '68', '단골병원', 'HOSPITAL', '부산 금정구 중앙대로 1829 (구서동) 5F~10F', 35.24768812610975, 129.09194496266713, '051-967-5222', 'https://cafe.naver.com/gjchildren2', '월·화·목·금 09:00-23:00 (점심 12:30-14:00) / 수 정기휴무 / 토·일 09:00-18:00', '2026년 7월 3일부터 단골병원 4층에서 진료를 시작합니다. 1층 부설 아동발달센터.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('24', '70', '굿윌치과병원', 'DENTAL_HOSPITAL', '부산 부산진구 중앙대로 686 (부전동) 4F 굿윌치과병원 서면', 35.15394952620155, 129.05958685961224, '0507-1380-6001', 'http://sm.egoodwill.co.kr/', '월·화·목 09:30-21:00 / 수·금 09:30-18:30 / 토 09:30-17:00 / 일 정기휴무, 08/15 광복절 휴무', '부산서면 굿윌치과. 28년 경력. 치의학박사가 집도하는 임플란트, 인비절라인 임상자문의사가 진행하는 치아교정. 국가구강검진기관, 외국인환자유치선도의료기관.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('25', '71', '더페이스성형외과의원', 'CLINIC', '부산 부산진구 부전로66번길 22 (부전동) 2~6, 8~9층 더페이스성형외과', 35.15577610508977, 129.05587860114724, '051-817-3806', 'http://www.theface-dr.co.kr/', '월~금 10:00-19:00 (점심 13:00-14:00) / 토 10:00-17:00 / 일 정기휴무, 08/15 광복절 휴무', '부산 서면역 7번 출구 더페이스성형외과의원. 눈/코/리프팅/중년/재수술/레이저/쁘띠. 정밀 검사와 진단을 바탕으로 꼭 필요한 수술만 시행, 1:1 케어.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('26', '72', '노블레스성형외과의원', 'CLINIC', '부산 부산진구 서면로68번길 1 (부전동) 4,5,6,7층', 35.15687240978119, 129.05805685310372, '0507-1397-1329', NULL, '화~금 09:30-19:00 (점심 12:30-14:00) / 토 09:00-15:00 / 월·일 정기휴무, 08/15 광복절 휴무', '서면역 7번 출구 노블레스타워 8F 노블레스피부과. 2015년 개원, 피부과 전문의 최규원 원장. 흉터/색소/혈관질환치료 인증기관.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('27', '73', '아이노유 성형외과의원', 'CLINIC', '부산 부산진구 가야대로 783 (부전동) 성원빌딩 14층 아이노유 성형외과 의원', 35.15814246053682, 129.05723237538626, '0507-1470-9922', 'https://iknowu.co.kr', '월~목 10:00-19:00 / 금 10:00-20:00 / 토 10:00-17:00 / 일 정기휴무, 08/15, 08/17 휴무', '가슴성형, 코성형 위주 + 눈성형, 리프팅, 필러, 보톡스, 레이저. 연세대 세브란스병원 출신 성형외과 전문의. 대리 수술없이 수술방 LIVE CCTV 참관 가능.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('28', '74', '리앤하이피부과의원', 'CLINIC', '부산 해운대구 해운대로 624 (우동) 2층 리엔하이피부과', 35.16356268965791, 129.15926314004207, '051-714-2135', 'http://www.rienhighdermatology.com', '월·화·목 10:00-19:00 / 수 14:00-19:00 / 금 10:00-20:30 / 토 09:30-13:30 / 일 정기휴무, 08/15, 08/17 휴무', '해운대역 1,3번 출구 앞 2층. 피부과전문의 직접 시술, 모발이식, 눈밑지방재배치, 리프팅/탄력, 색소, 흉터 치료.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('29', '75', '한국한의원', 'KOREAN_MEDICINE_CLINIC', '부산 동래구 충렬대로 154 (온천동) 한국한의원', 35.20448112717723, 129.07694225131547, '051-501-0025', 'http://koreaomc.co.kr/', '월~수 09:00-18:00 / 목 13:00-20:00 / 금·토 격주휴진 / 일 정기휴무, 08/10, 08/15 휴무', 'HK한국한의원. 첨단 검사·치료 시스템과 전통 한방 결합. 원스톱 검진 시스템, 원내 탕전실 한약 관리, 예약제 운영.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('30', '76', '아이엠성형외과의원', 'CLINIC', '부산 부산진구 서면로 59 (부전동, 삼정노블빌딩) 3층 아이엠성형외과', 35.15602874450151, 129.0575514370966, '051-809-0911', 'http://www.ps-iam.co.kr/m/', '월~금 10:00-19:00 / 토 10:00-17:00 / 일 정기휴무, 08/15, 08/17 휴무', '부산 서면 위치. 눈성형(학생쌍수, 중년쌍꺼풀, 눈재수술), 코성형(재수술), 지방이식, 실리프팅, 안면거상술, 인모드리프팅. 전문의 1:1 상담 기반.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('31', '77', '인제대학교부산백병원', 'GENERAL_HOSPITAL', '부산 부산진구 복지로 75 (개금동, 인제대학교 부산백병원) 인제대학교 부산백병원', 35.14552399951067, 129.02086448428625, '051-890-6114', 'http://www.paik.ac.kr/busan/', '월~금 08:00-17:00 (점심 12:00-13:30) / 토 08:00-12:00 / 일 정기휴무 (응급센터 24시간)', NULL, NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('32', '78', '라임부부치과의원', 'DENTAL_CLINIC', '부산 동래구 중앙대로 1325 (온천동, 이센타워) 11층 라임부부치과', 35.20525842014867, 129.07750606448298, '0507-1403-2857', 'https://blog.naver.com/limelimedent', '월~금 09:10-18:30 (점심 12:30-14:00) / 토 09:10-13:00 / 일 정기휴무, 08/15 광복절 휴무', '가족같은 배려와 따뜻한 진료를 지향합니다.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('33', '79', '수정안과의원', 'CLINIC', '부산광역시 북구 만덕대로 5 (덕천동) 수정안과', 35.21049854947729, 129.0058274393652, '051-331-7575', 'http://www.crystaleye.co.kr', '월~금 09:00-18:00 (점심 13:00-14:00) / 토 09:00-13:00 / 일 정기휴무', 'Since 1989. 시력교정수술 1세대 안과. 안과전문의 11인 협진시스템.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('34', '80', '다리안 흉부외과', 'CLINIC', '부산광역시 부산진구 서면로 74 (부전동, 아이온시티빌딩) 17층', 35.1571999012627, 129.05812990266668, '051-807-7744', 'https://blog.naver.com/2013dariahn', '월~금 09:00-17:00 (점심 12:30-14:00) / 토 09:00-15:00 / 일 정기휴무, 08/15, 08/17 휴무', '부산하지정맥류 전문 - 서면 17년째. 심장혈관흉부외과 전문의 1:1 진료, 13,000례 이상 레이저 수술 경험.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('35', '81', '이학철한의원', 'KOREAN_MEDICINE_CLINIC', '부산광역시 부산진구 당감로 32 (당감동) 이학철한의원', 35.16443194407143, 129.03955613781764, '051-896-3883', NULL, '월~금 10:00-18:00 (점심 13:00-14:30) / 토 10:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '레이저 침과 물리치료 등 각종 한방과 진료, 성장과 골다공증 프로그램 전문.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('36', '82', '비스타안과', 'CLINIC', '부산광역시 해운대구 좌동순환로 505 (중동, 센트럴메디타워) 11층 비스타안과', 35.165669364272375, 129.16822491680387, '051-710-7100', 'http://www.vistaeye.co.kr', '월~금 09:30-18:00 (점심 13:00-14:00) / 토 09:30-14:00 / 일 정기휴무, 08/15, 08/17 휴무', '시력교정과 노안·백내장 중점, 안과전문의 2인 진료. 중동역 7번출구 센트럴메디타워 11층.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('37', '83', '부산힘찬병원', 'HOSPITAL', '부산광역시 동래구 충렬대로 255 (수안동, 동래힘찬병원) 부산힘찬병원', 35.201432437193915, 129.0871802543263, '1899-2555', 'https://blog.naver.com/busanhimchan', '월~금 08:30-17:30 (점심 12:30-13:30) / 토 08:30-12:30 / 일 정기휴무, 08/15 광복절 휴무', '부산힘찬병원 로봇인공관절센터 개소. 정형외과, 신경외과, 내과, 영상의학과, 마취통증의학과.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('38', '84', '박원욱병원', 'HOSPITAL', '부산광역시 수영구 광안로 4 (광안동, 광안동에스케이뷰) 상가건물 박원욱병원', 35.15694781645581, 129.11337348178532, '1544-7582', 'http://www.parkspine.co.kr/', '월 09:00-18:00 / 화~금 09:00-17:00 (점심 12:30-13:30) / 토 09:00-13:00 / 일 정기휴무', '당신의 척추, 관절을 책임지는 병원. 정형외과, 신경과, 마취/통증의학과, 가정의학과, 영상의학과 운영.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('39', '85', '케이피부과', 'CLINIC', '부산광역시 수영구 수영로 697 (수영동, 홍인빌딩) 6층 케이피부과', 35.16781449284181, 129.11572949712922, '0507-1372-5511', 'http://pf.kakao.com/_ZxeLWj', '월·금 10:00-20:00 / 화~목 10:00-19:00 / 토 09:30-14:30 / 일 정기휴무, 08/15 광복절 휴무', '수영역 1번 출구 도보 1분. 써마지, 울쎄라, 리쥬란, 스킨부스터, 보톡스, 필러, 기미/잡티/색소치료, 레이저 제모 등.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('40', '86', '포시즌성형외과', 'CLINIC', '부산광역시 부산진구 중앙대로 694 (부전동, 쥬디스태화) 8층', 35.154709489584306, 129.05960381416617, '0507-1337-7979', 'http://www.4seasonsps.com', '월~금 10:00-19:00 / 토 10:00-17:00 / 일 정기휴무, 08/15 광복절 휴무', '서면 쥬디스태화 위치. 눈/코재수술, 눈성형, 코성형, 리프팅, 이마거상/축소, 미니거상, 지방흡입 중점.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('41', '89', '부산미래IFC의원', 'CLINIC', '부산광역시 남구 전포대로 133 (문현동) 부산 IFC 3,4층', 35.148312812684196, 129.0654511867169, '051-710-2000', 'http://mrhealth-b.co.kr/', '월~금 07:30-16:30 / 토 07:30-12:30 / 일 정기휴무, 08/15, 08/17 휴무', '연세미래IFC 검진센터. 부산 최대 규모, 여성전용 검진 공간. 3.0T 필립스 MRI, 대학병원급 내시경센터.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('42', '91', '브니엘산부인과', 'CLINIC', '부산광역시 해운대구 달맞이길 30 (중동, 엘시티) 포디움동 2073호', 35.16056851607763, 129.16835358685938, '051-742-9977', 'https://www.inbeauty.co.kr/', '월~금 10:00-18:00 / 토 10:00-16:00 / 일 정기휴무, 08/15 광복절 휴무', '해운대 엘시티몰 위치. 여성 성형 중점 병원, 예약진료. 전신마취를 하지 않음.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY'), (@hid, 'OBSTETRICS_GYNECOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('43', '93', '바른윤곽치과병원', 'DENTAL_HOSPITAL', '부산광역시 부산진구 서면문화로 10 (부전동, 영광도서) 13,14층 바른윤곽치과병원', 35.158840795207595, 129.05759140388938, '051-714-2822', 'https://naver.me/xgNQPEco', '월~수·금 09:00-18:00 / 목 09:00-21:00 / 토 09:00-14:00 / 일 정기휴무, 08/15 광복절 휴무', '서면치과 중 병원급 의료기관. 구강악안면외과 전문의 2인 + 치과보존과 전문의 협진. 임플란트, 고난도 매복 사랑니 발치.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('44', '94', '아이시티안과', 'CLINIC', '부산광역시 부산진구 중앙대로 686 (부전동) 경암빌딩 6층', 35.15394952620155, 129.05958685961224, '051-817-0006', 'http://www.eyecity.co.kr/', '월·화·목 09:30-18:00 / 수 정기휴무 / 금 09:30-19:00 / 토 09:30-17:00 / 일 정기휴무', '15년 이상 대표원장 직접 수술 집도. 부산 스마일라식/노안백내장 전문. 존슨앤존슨 실크스마일 인증병원.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('45', '95', '부산큰병원', 'HOSPITAL', '부산광역시 부산진구 가야대로 494 (개금동, 대도빌딩) 부산큰병원(지하1~4층)', 35.152844410471346, 129.02617584758033, '0507-1457-0123', 'http://www.busankeunh.co.kr', '월 09:00-18:00 / 화~금 09:00-17:00 (점심 12:30-13:30) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '부산진구 개금 위치. 척추, 관절 수술 중점 병원. 척추내시경 클리닉, 로봇인공관절수술 클리닉. 정형외과, 신경외과, 내과.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, '러시아'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('46', '96', '성재영 이즈피부과', 'CLINIC', '부산광역시 부산진구 서면문화로 10 (부전동, 영광도서) 4층 , 401', 35.158840795207595, 129.05759140388938, '051-807-6684', 'http://pf.kakao.com/_xjyKAK/chat', '월·화 10:00-19:00 / 수 정기휴무 / 목·금 10:00-20:00 / 토 09:30-16:00 / 일 정기휴무, 08/15, 08/17 휴무', '20년 경력 피부과 전문의 성재영 원장. 1:1 정밀 진단, 안티에이징 전문.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('47', '97', '아루다의원', 'CLINIC', '부산광역시 부산진구 가야대로784번길 21 (부전동) 3층', 35.15652169775825, 129.0573378155017, '0507-1329-5970', 'https://arudaclinic.com/', '월~금 10:00-19:00 (점심 13:00-14:00) / 토 10:00-16:00 / 일 정기휴무, 08/15 광복절 휴무', '예약이 마감된 경우 대기자가 많아 개별 연락 어려움. 본원으로 연락 시 취소 예약 안내.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('48', '98', '태성형외과의원', 'CLINIC', '부산광역시 해운대구 구남로 27 (중동, 해운대오피스) 4층 (구남빌딩)', 35.16176417629546, 129.1608922891637, '051-746-7004', 'http://www.pstae.co.kr/html/', '월~금 09:00-18:00 (점심 13:00-14:00) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '20년 경력 성형외과 전문의 김기태 원장. 가슴성형, 리프팅, 눈성형, 문신제거, 지방흡입 + CLS시스템/줄기세포 치료 접목.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('49', '100', '보리은백한의원', 'KOREAN_MEDICINE_CLINIC', '부산광역시 부산진구 가야대로 749-1 (부전동, 신동아오피스텔) 2층 203호 보리은백한의원', 35.158150218028794, 129.05367555542705, '051-806-3588', 'https://blog.naver.com/dbceunbaek', '월·화·목·금 08:30-18:30 / 수·토 08:30-12:30 / 일 정기휴무, 08/15, 08/17 휴무', '가야대로 749-1 신동아오피스텔 1층 주차장 이용(1시간 30분 무료).', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('50', '101', '성모안과병원', 'HOSPITAL', '부산광역시 해운대구 해운대로 409-1 (우동, 1동) 성모안과병원', 35.1674892, 129.1411482, '1600-0775', 'https://www.sungmo.co.kr/', '월~금 09:00-17:30 / 토 09:00-12:00 / 일 정기휴무, 08/15, 08/17 휴무', '진료예약 및 절차 안내: 일반진료는 본관2층, 망막진료는 본관3층 접수. 라식진료는 본관 2층 SMILE 라식센터.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'ja');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('51', '102', '큰솔2병원', 'HOSPITAL', '부산광역시 사상구 학장로 189 (학장동) 큰솔2병원', 35.143617422487154, 128.98724166634594, '0507-1325-0302', 'https://www.keunsol.co.kr/', '월~금 09:00-18:00 (점심 12:30-13:30) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '부산재활전문 병원. 진료과목: 재활의학과, 내과, 인공신장실.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('52', '103', '미테크성형외과의원', 'CLINIC', '부산광역시 부산진구 중앙대로 704-1 (부전동) 3층', 35.155614760586694, 129.0595666661339, '051-714-4673', 'http://metechps.co.kr', '월~토 09:30-21:00 / 일 정기휴무 (08/17 정상 진료)', '서면 위치. 모든 의료진 성형외과 전문의. 평일/토요일 매일 야간진료(9시까지), 대표원장 직접 상담.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('53', '105', '나르샤 병원', 'HOSPITAL', '부산광역시 동래구 중앙대로1493번길 6 (온천동) 나르샤 병원', 35.219086328790794, 129.08461238375128, '051-715-7878', 'http://www.narshahospital.co.kr', '월 09:00-18:00 / 화~금 09:00-17:00 (점심 12:30-13:30) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '신규환자 전화/네이버 예약, 재진환자 전화예약, 외국인환자 Kakao 영어채팅/Naver 영어예약 가능.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('54', '106', '글로리여성의원', 'CLINIC', '부산광역시 부산진구 서면로 48 (부전동, 현경빌딩) 글로리여성의원 3,5,6F', 35.15495779467077, 129.05803783246176, '0507-1310-7115', 'http://glorywoman.co.kr', '화·수·금 09:30-18:30 / 목 정기휴무 / 토 09:30-15:30 / 일 정기휴무', '서면 글로리여성의원. 여성질환검진, 임신관련 진료, 여성성형(질이완증 치료, 질타이트닝시술, 소음순수술 등).', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY'), (@hid, 'OBSTETRICS_GYNECOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('55', '107', '연산당당한방병원', 'KOREAN_MEDICINE_HOSPITAL', '부산광역시 연제구 월드컵대로 82 (연산동) 연산당당한방병원', 35.18183621315095, 129.08397785809328, '0507-1396-7581', 'https://yesdang-ys.com', '한방 매일 09:00-20:30(토·일 09:00-16:00), 내과·정형외과는 요일별 상이 (365일 입원수속 가능)', '연산역 인근 의과·한의과 통합 한방병원. 250평 규모 도수·재활·체형교정 센터, 종합건강검진센터, 98병상 입원실. 365일 연중무휴 진료.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('56', '108', '바노바기의원 부산', 'CLINIC', '부산광역시 부산진구 서면로 74 (부전동, 아이온시티빌딩) 1001, 1803호', 35.1571999012627, 129.05812990266668, '051-711-6508', 'https://banobagiclinic1.com/', '월~금 10:00-20:00 / 토 10:00-16:00 / 일 정기휴무, 08/15 광복절 휴무', '1,2호선 서면역 5,7번출구 도보 10초. Since 2000, 바노바기 브랜드. 정품/정량 사용, 1회용 캐뉼라·니들 사용.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, '베트남'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('57', '109', '에이지의원', 'CLINIC', '부산광역시 부산진구 중앙대로 745-1 (부전동, 에이지빌딩) 에이지의원 2,3층', 35.159272583556486, 129.05996172019124, '0507-1404-5524', 'https://에이지의원.com/', '월·화·목·금 10:00-21:00 / 수 정기휴무 / 토 10:00-17:00 / 일 정기휴무', '모발이식, 문신제거, 도수치료, 피부미용 중점. 모발이식센터, 도수치료센터, 피부미용센터(피코웨이, 텐써마·슈링크·인모드 등).', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('58', '110', '마이플한의원', 'KOREAN_MEDICINE_CLINIC', '부산광역시 부산진구 중앙대로691번길 5 (부전동) 천우빌딩 4층', 35.154267042212446, 129.05865662062587, '0507-1391-3364', 'http://miple-diet.com', '월~수·금 10:00-19:00 / 목 정기휴무 / 토 10:00-14:00 / 일 정기휴무, 08/15, 08/17 휴무', '20년 임상 경력 한의사 직접 조제 한약 + 식단/생활습관 교정. 원내 직접 조제, 대사기능 회복 근본 치료 지향.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('59', '111', '유앤아이치과병원', 'DENTAL_HOSPITAL', '부산광역시 연제구 중앙대로 1090 (연산동, 프라임시티) 프라임시티빌딩7층', 35.18480238419076, 129.0810619387717, '0507-1370-0131', 'http://www.uni-dental.co.kr/', '월~수 09:30-21:00 / 목·금 09:30-19:00 / 토 09:30-15:00 / 일 정기휴무, 08/15 광복절 휴무', '부산 연산동 위치. 2014년 대학병원급 장비 도입. 검진·협진·사후관리 시스템 갖춘 치과병원.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, '몽골');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('60', '112', '서면더존한방병원', 'KOREAN_MEDICINE_HOSPITAL', '부산광역시 부산진구 중앙대로 708 부산파이낸스빌딩 B1~3층,2,9,10층', 35.1560157333692, 129.05948558114602, '051-791-0070', 'https://thezoneh.com', '월~금 09:30-21:00 / 토 09:30-17:00 / 일 10:00-17:00 (365일 입원수속 가능)', '서면역 2번출구. 한의학 x 의학 협진. 추나요법/한약처방/약침, 도수·물리치료, 체외충격파. 교통사고·산재보험 치료, 재활치료, 한방내과·소아과·부인과.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE'), (@hid, 'OBSTETRICS_GYNECOLOGY'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('61', '113', '온종합병원', 'GENERAL_HOSPITAL', '부산광역시 부산진구 가야대로 721 (당감동, 온병원) 온종합병원', 35.1580814100282, 129.04952820143245, '051-607-0114', 'https://blog.naver.com/on_hospital', '월 09:00-18:00 / 화~금 09:00-17:00 / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '부산 서면 유일 도심형 종합병원, 부암역 6번출구 연결. VIP종합검진센터, 응급의료기관.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('62', '114', '센텀이룸여성의원', 'CLINIC', '부산광역시 해운대구 센텀2로 20 (우동, 센텀타워메디컬) 10, 11층 1003, 1103호', 35.16715893788813, 129.13206238150946, '051-758-8275', 'https://blog.naver.com/ct-eroom', '월·화·목·금 09:00-19:00 / 수 09:00-16:30 / 토 09:00-12:30 / 일 정기휴무, 08/15 광복절 휴무', '난임치료 분야 의료진, 시험관아기·인공수정·부인과내시경 등 진료. 심적·정신적 안정을 위한 배려와 친절한 서비스.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OBSTETRICS_GYNECOLOGY'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, '러시아'), (@hid, '몽골');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('63', '116', '닥터앰버치과의원', 'DENTAL_CLINIC', '부산광역시 해운대구 달맞이길117번가길 153 (중동) 제이빌딩 3층, 닥터앰버치과', 35.15869184310434, 129.1817904603385, '051-951-0075', NULL, '화·수·목 10:00-17:00 / 월·금·토 정기휴무 / 일 정기휴무, 08/15 광복절 휴무', '예약제로 진료. 연세대-미국 UCLA-Beverly Hills-서울 경력, 2022년 10월 해운대 달맞이 오픈. 미국/한국 치과 면허 보유, 영어 진료 가능.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('64', '117', '리셋치과의원', 'CLINIC', '부산광역시 부산진구 가야대로 753 (부전동, 부전동 메디컬센터) 12층', 35.1579428203534, 129.05402372542522, '0507-1386-8882', 'https://litt.ly/reset.dent', '월·수·금 10:00-19:00 / 화·목 10:00-21:00 / 토 10:00-16:00 / 일 정기휴무, 08/15 광복절 휴무', '미니쉬 멤버스 클리닉 지정 병원, 보건복지부 인증 전문의 협진, 고난도/재수술 임플란트(2만건 이상 식립 경력), 3D-CT·구강스캐너 디지털 진단.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('65', '118', '예쁜라인성형외과의원', 'CLINIC', '부산광역시 부산진구 서면로 69-1 (부전동) 2층, 예쁜라인성형외과의원', 35.15701480447021, 129.05753526706417, '051-806-9007', 'http://예쁜라인성형외과.com', '월·화·수·금 10:00-18:30 / 목 정기휴무 / 토 10:00-16:30 / 일 정기휴무, 08/15, 08/17 휴무', '입술필러+입꼬리보톡스, 안면거상수술, 쌍꺼풀+눈매교정+앞트임수술, 필러+실리프팅, 코끝성형, 슈링크, 리쥬란힐러. 대학병원 출신 원장 상주, 재수술 전문.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '베트남'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('66', '120', '경인한의원', 'KOREAN_MEDICINE_CLINIC', '부산광역시 사하구 다대로 401 (다대동) 1, 2층', 35.067355073382515, 128.97988884570887, '0507-1318-1433', 'https://youtube.com/@TV-jz4kg', '월~수 09:30-18:30 (점심 12:30-14:00) / 목~토 휴무(격주) / 일 정기휴무, 08/15 광복절 휴무', '경인한의원 박태열 원장, 임상 38년차 한의학박사. 방광암 수술 후 재발관리, 항암후유증 관리, 만성방광염, 요실금 등 진료. 공신단 직접 조제.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'ja');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('67', '122', '세화병원', 'HOSPITAL', '부산광역시 동래구 미남로132번길 28 (온천동, 세화병원) 세화병원', 35.20590434079108, 129.07118433243733, '1544-3662', 'http://www.swmedi.com', '월~금 08:00-17:00 (점심 13:00-14:00) / 토 08:00-13:00 / 일 정기휴무 (공휴일은 예약된 시술만 진행)', '난임 중점 병원, 개원 39주년. 난임 명의 이상찬 병원장 외 6인 난임 전문 의료진. 인공수정·시험관아기시술·냉동배아이식, 가임력 보존클리닉 운영.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OBSTETRICS_GYNECOLOGY'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('68', '1', '좋은문화병원', 'GENERAL_HOSPITAL', '부산광역시 동구 범일로 119(범일동)', 35.140682131441146, 129.05884228673625, '051-644-2002', 'http://www.moonhwa.or.kr/', '월 09:00-18:00 / 화~금 09:00-17:00 / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '동구 범일동 위치, 은성의료재단 산하 12개 좋은병원 모태, 1978년 개원. 부인과암·유방암·갑상선암 조기진단부터 수술까지, 4세대 다빈치 로봇수술 시스템. 종합건강검진센터 운영.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OBSTETRICS_GYNECOLOGY'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '기타'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('69', '2', '좋은강안병원', 'GENERAL_HOSPITAL', '부산광역시 수영구 수영로 493(남천동)', 35.1497288483022, 129.110340557052, '051-625-0900', 'https://www.gang-an.or.kr/', '월 09:00-18:00 / 화~금 09:00-17:00 / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '수영구 남천동 위치, 은성의료재단 산하 종합병원. 응급실 24시간 운영. 내과·외과·정형외과·신경과 등 협력 진료.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '몽골'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('70', '3', '동아대학교병원', 'TERTIARY_HOSPITAL', '부산광역시 서구 대신공원로 26(동대신동3가)', 35.119895667552214, 129.01667105921672, '051-240-2000', 'http://www.damc.or.kr/', '월~금 08:00-17:00 / 토·일 정기휴무, 08/15, 08/17 휴무 (응급의료센터 365일 24시간)', '사랑과 인술로 늘 함께하는 동아대학교병원.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('71', '6', '부민병원', 'GENERAL_HOSPITAL', '부산광역시 북구 만덕대로 59(덕천동, 외 2필지, 380-9)', 35.21198146554539, 129.01117166185534, '1670-0082', 'http://bumin.co.kr/busan/', '월 09:00-18:00 / 화~금 09:00-17:00 / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '관절·척추·내과 중심 종합병원. 관절센터, 척추센터, 심뇌혈관센터, 재활운동치료센터, 외상골절센터, 소화기센터 등. 총 385병상, 지역응급의료기관 24시간 운영.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('72', '7', '하늘성형외과의원', 'CLINIC', '부산 부산진구 서면로 62-1 (부전동) 에이스메디칼타워 3-10층', 35.15630244516409, 129.05803317357228, '051-806-0090', 'https://www.skydoctor.co.kr/', '월~금 10:00-19:00 / 토 10:00-17:00 / 일 정기휴무, 08/15 광복절 휴무', '의사경력 32년 대표원장, 각 분야별 세분화 진료. 34년 경력 마취통증의학과 전문의 상주.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '몽골'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('73', '8', '밝은세상안과', 'CLINIC', '부산광역시 부산진구 서면로 74(부전동, 아이온시티 8~11층)', 35.1571999012627, 129.05812990266668, '051-805-1100', 'http://www.iloveeye.co.kr/', '월·화·목·금 09:00-18:00 / 수 정기휴무 / 토 09:00-16:00 / 일 정기휴무, 08/17 휴무', 'SINCE1997, 28년 이상 60만건 이상 수술 경험. 밝은세상AI 런칭. 스마일프로/자이스 스마일/2DAY라섹, ICL, 레이저 다초점 백내장, 드림렌즈.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('74', '9', '고운세상 김양제 장봉석피부과의원', 'CLINIC', '부산 부산진구 중앙대로 686 (부전동) (부전동, 5층)', 35.15394952620155, 129.05958685961224, '051-805-1004', 'http://www.doctorkim.com/', '월~금 09:30-19:00 (점심 13:00-14:30) / 토 09:30-15:30 / 일 정기휴무, 08/15, 08/17 휴무', NULL, NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('75', '11', '삼육부산병원', 'GENERAL_HOSPITAL', '부산광역시 서구 대티로 170, 1층(서대신동2가)', 35.11235146131552, 129.01133003396814, '1566-3675', 'http://www.symcb.co.kr/', '월~목 08:30-17:30 / 금 08:30-12:30 / 토 정기휴무 / 일 08:30-17:30 (외래 정상진료)', '1951년 개원, 75년 역사 서구 종합병원, 세계 180개 글로벌 메디컬 네트워크. 일요일 외래 정상진료, 보건복지부 인증의료기관.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('76', '13', '부산대학교병원', 'TERTIARY_HOSPITAL', '부산광역시 서구 구덕로 179(아미동1가, 부산대학교병원)', 35.1010140297246, 129.01868452994532, '051-240-7000', 'https://www.pnuh.or.kr/', '월~금 08:30-17:30 / 토·일 정기휴무, 08/15, 08/17 휴무 (주말/공휴일 응급·분만환자 24시간)', '1956년 11월 개원, 국립대학교병원. 2025년 9월 기준 1,188병상 운용 상급종합병원.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('77', '14', '고신대학교 복음병원', 'GENERAL_HOSPITAL', '부산광역시 서구 감천로 262 (암남동)', 35.080595643066346, 129.01554644131926, '051-990-6114', 'http://kosintv.kr', NULL, NULL, NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '몽골'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('78', '15', '인제대학교 해운대백병원', 'GENERAL_HOSPITAL', '부산광역시 해운대구 해운대로 875(좌동)', 35.17319453096552, 129.18222316967916, '051-797-0100', 'http://haeundae.paik.ac.kr', NULL, NULL, NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('79', '16', '스마일정경우비뇨기과의원', 'CLINIC', '부산광역시 해운대구 센텀2로 20, 203호(우동, 센텀타워메디컬)', 35.16715893788813, 129.13206238150946, '051-744-8181', 'http://bmu.co.kr/index.asp', '월~금 09:00-18:00 (점심 13:00-14:00) / 토 09:00-12:00 / 일 정기휴무, 08/15, 08/17 휴무', '전 동아대병원 비뇨의학과 과장/주임교수. 리줌, 전립선, 성기능, 요실금, 결석, 남성수술.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '필리핀'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('80', '17', '아이사랑산부인과의원', 'CLINIC', '부산광역시 부산진구 가야대로 470(개금동)', 35.15285334981367, 129.02342868855396, '051-890-7000', 'https://blog.naver.com/isarang7000', '월~금 09:00-18:00 (점심 13:00-14:00) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무 (분만·응급산모 24시간)', '제왕절개 분만률 낮은 의료기관 선정. 2004~2008년 5년 연속 자연분만률 높은 의료기관. 복강경수술 1200례 이상.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OBSTETRICS_GYNECOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'ja');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('81', '18', '마리아의원', 'CLINIC', '부산광역시 연제구 월드컵대로 125(연산동, 더웰타워 7층~8층)', 35.18527025640391, 129.08184440890432, '051-441-6555', 'https://www.mariababy.com/', '월~금 07:30-16:30 (점심 12:00-13:30) / 토 07:30-12:30 / 일 정기휴무, 08/15, 08/17 휴무', '난임 전문 마리아병원. 인공수정, 시험관아기시술, 반복적자연유산, 가임력 보존센터. 2016년 임신성공률 부산 1위(49.39%).', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OBSTETRICS_GYNECOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('82', '19', '부산성모병원', 'GENERAL_HOSPITAL', '부산광역시 남구 용호로 232번길 25-14 (용호동)', 35.109795450989935, 129.10929468914765, '051-933-7777', 'https://bsm.or.kr', '월~금 08:30-17:30 (점심 12:30-13:30) / 토 08:30-12:30 / 일 정기휴무, 08/15 광복절 휴무', '1951년 성분도병원으로 시작, 70년 넘는 역사. 4회 연속 보건복지부 의료기관 인증. 지역응급의료기관 3년 연속 A등급.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'ja'), (@hid, '기타'), (@hid, '독일');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('83', '20', '이샘병원', 'HOSPITAL', '부산광역시 부산진구 황령대로 12, 지하1층(일부), 4층, 5층(일부), 6~8층, 9층(일부)(범천동)', 35.147616228875684, 129.06115153353653, '051-631-2110', 'http://www.medisam.net/', '월~목 08:00-18:00 / 금 08:00-17:30 / 토 08:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '갑상선, 당뇨병, 신장내과, 소화기내과, 종합건강검진센터, 일반외과, 가정의학과, 영상의학과, 병리과, 신경과, 직업환경의학과 진료.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('84', '21', '센텀소중한눈안과의원', 'CLINIC', '부산광역시 해운대구 센텀남대로 50 (우동, 센텀임페리얼타워 1402호)', 35.168108866466504, 129.13130127644922, '051-920-7788', 'https://lasikexcimer.com/', '월~목 09:00-18:30 / 금 09:00-19:00 / 토 09:00-14:00 / 일 정기휴무, 08/15, 08/17 휴무', '센텀 소중한눈안과. 시력교정술(스마일/라식/라섹/ICL), 백내장/노안, 안질환(안구건조증/녹내장/황반변성/당뇨망막병증), 드림렌즈.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('85', '22', '김병준 레다스 흉부외과의원', 'CLINIC', '부산광역시 부산진구 서면문화로 10(부전동, 11~12, 13층 일부)', 35.158840795207595, 129.05759140388938, '051-634-8275', 'http://www.kbjledas.com/', '월·화·목·금·토 09:00-18:00 / 수 정기휴무 / 일 정기휴무, 08/15, 08/17 휴무', '22년 부산 하지정맥류 중점 의료기관. 28년 경력 심장혈관흉부외과 전문의 김병준 원장. 서면역 9번출구 영광도서 12층.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('86', '23', '동남권원자력의학원', 'GENERAL_HOSPITAL', '부산광역시 기장군 장안읍 좌동길 40', 35.32101167255867, 129.24344228947507, '051-720-5114', 'https://www.dirams.re.kr/', '월~금 09:00-17:30 (점심 12:30-13:30) / 토·일 정기휴무 (응급실 00:00-24:00 연중무휴)', '과학기술정보통신부 산하 공공기관. 첨단 의생명연구 수행, 특화된 암 진료, 건강검진 등 의료서비스 제공.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('87', '24', '스마일누네빛안과의원', 'CLINIC', '부산광역시 부산진구 가야대로 772(부전동)', 35.1565757882055, 129.05708933440053, '1800-7722', 'https://blog.naver.com/smilenune', '월·화·목·금 09:00-19:00 / 수 09:00-18:00 / 토 08:30-14:00 / 일 정기휴무', '부산 롯데호텔 옆. 누네메디타워 7-13F 각 층별 진료센터. 스마일/스마일프로/라식/라섹/렌즈삽입술, 노안·백내장·망막질환. 6인 안과전문의.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('88', '25', '부산성소병원', 'HOSPITAL', '부산광역시 남구 수영로 175 (대연동)', 35.13509890400894, 129.08577742820736, '051-633-1123', 'http://www.seongso.co.kr/', '월~금 09:00-17:30 (점심 13:00-14:00) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '1986년 박희두외과의원 개원, 1991년 부산성소의원, 2012년 205병상 신축. 갑상선 질환·정형외과·건강검진 중점.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('89', '26', '부산본병원', 'HOSPITAL', '부산광역시 사하구 승학로 8 (당리동)', 35.10619763280931, 128.96983095779964, '1599-8275', 'http://www.bonhospital.co.kr/', '월~금 09:00-20:00 (점심 13:00-14:00) / 토·일 09:00-13:00 (정형외과 24시간 야간진료)', '서부산권 유일 관절 전문병원. 2025년 11월 기준 정형외과 수술건수 100,000례 달성. 보건복지부 지정 관절전문병원.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('90', '27', '뉴라인성형외과의원', 'CLINIC', '부산광역시 부산진구 부전로66번길 40 (부전동)', 35.15571905773854, 129.05690385708652, '051-806-0100', 'https://ulgulgab.com/newLine', '월~금 10:00-19:00 / 토 10:00-17:00 / 일 정기휴무, 08/15 광복절 휴무', '대표원장 손희동. 진료과목: 눈성형, 코성형, 기능 코성형, 리프팅, 안면거상, 재수술, 가슴성형, 남자성형, 여유증, 쁘띠시술, 피부관리.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('91', '29', '굿모닝백이안과의원', 'CLINIC', '부산광역시 부산진구 중앙대로 724(부전동,하나금융프라자3,6,7,8층)', 35.157229678323006, 129.05996169268985, '0507-1397-0214', 'https://102eye.com/', '월~금 09:30-18:00 (점심 13:00-14:00) / 토 09:30-13:00 / 일 정기휴무, 08/17 휴무', '33년 경력 백태민 대표원장. 14만건 이상 안종합수술실적. 노안백내장센터, 소아안과, 시력교정, 녹내장, 안성형.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY'), (@hid, 'OPHTHALMOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('92', '30', '세일병원', 'HOSPITAL', '부산광역시 동구 중앙대로 317 (초량동)', 35.124840344443385, 129.04459294990906, '051-464-8110', 'https://www.seailhosp.com/', '월~금 09:00-18:00 (점심 12:30-13:30) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '1963년 개원, 1983년 전국 최초 정형외과 중점 병원 선도. 정형외과 기본 + 내과, 재활의학과, 도수치료센터. 24시간 응급실 운영.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('93', '31', '정성훈성형외과의원', 'CLINIC', '부산광역시 부산진구 서면로 64(부전동)', 35.156491351874, 129.05807888268373, '051-806-1115', 'http://www.jshps.co.kr', '월·화·수·금 10:00-18:00 / 목 10:00-12:00 / 토 10:00-16:00 / 일 정기휴무, 08/15, 08/17 휴무', '부산 서면 BS메디컬센터 위치. 정성훈 원장(성형외과 전문의/의학박사).', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('94', '32', '삼성뉴방외과의원', 'CLINIC', '부산광역시 해운대구 해운대로 794(좌동, 엘리움 6층 603호)', 35.16879374659007, 129.17574102921088, '0507-1432-3652', 'https://samsungyubang.com/', '월~금 08:30-17:30 (점심 12:30-13:30) / 토 08:30-13:30 / 일 정기휴무, 08/15, 08/17 휴무', '내시경 유방수술 지향. 유방암 조기 진단, 내시경수술을 통한 보존·복원. 장산역 11번 출구 앞 엘리움빌딩 6층.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('95', '33', '디에이치성형외과의원', 'CLINIC', '부산광역시 부산진구 가야대로 784번길 21, 5-7층(부전동)', 35.15652169775825, 129.0573378155017, '1644-7520', 'http://www.danielhanps.com/', '월~금 10:00-19:00 / 토 10:00-17:00 / 일 정기휴무, 08/15 광복절 휴무', '서면역 위치. 강남 유명 성형외과 출신 의료진. 가슴성형, 양악수술, 안면윤곽, 눈-코재수술 + 안와감압술, 화상치료, 비염수술, 유방재건.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('96', '34', '대동병원', 'GENERAL_HOSPITAL', '부산광역시 동래구 충렬대로 187 (명륜동)', 35.20423547963387, 129.08019339187132, '051-554-1233', 'http://www.ddh.co.kr/', '월~금 09:00-17:00 (점심 12:30-13:30) / 토 09:00-12:00 / 일 정기휴무', '1945년 개원, 20개 진료과·10개 전문센터. 700명 이상 직원, 100여명 의사. 대동대학교 간호학과 설립, 부산시 의료 수련 허브.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('97', '35', '로덴치과의원(경성대,부경대점)', 'DENTAL_CLINIC', '부산광역시 남구 용소로 7, 5,6층(대연동, 청라빌딩)', 35.136674714912374, 129.1006320371681, '0507-1422-2775', 'https://blog.naver.com/dental_roden', '월·수 09:30-18:30 / 화 09:30-20:30 / 목 14:00-20:30 / 금·토 정기휴무 / 일 정기휴무, 08/15 광복절 휴무', '수영역 2번 출구, 같은 자리 12년 진료. 첫 내원 시 구강 내 사진 및 정밀 진단 상담.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('98', '36', 'BS더바디성형외과의원', 'CLINIC', '부산광역시 부산진구 서면로 51, 4층(부전동, 4,5,6,7층)', 35.155383548094555, 129.05756876533604, '051-818-9300', 'http://www.thebodyps.co.kr/', '월~금 09:00-19:00 / 토 09:00-17:00 / 일 정기휴무', '다양한 임상경험의 전문 의료진, 첨단 전문의료장비 및 응급상황대비장비 구비. 마취과 전문의 상주.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('99', '37', '서울청담성형외과의원', 'CLINIC', '부산광역시 동래구 아시아드대로 239-1 (온천동, 월드 메디컬 타워) 6층', 35.205506230540415, 129.0673802553793, '1599-7533', 'http://www.seoulcd.com', '월~수 정기휴무 / 목·금 10:00-19:00 / 토 10:00-16:00 / 일 정기휴무, 08/12, 08/15, 08/17, 08/19 휴무', '서울대 출신 19년 경력 성형외과전문의 임주환 대표원장. 눈성형, 중년눈성형, 이물질제거, 동안성형, 얼굴지방성형, 쁘띠성형. 수술 결과 책임보장제 시행.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, '중동'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('100', '38', '부강 강치과의원', 'DENTAL_CLINIC', '부산광역시 해운대구 센텀3로 20, 2층 203-1호(우동, 벡스코비즈니스호텔)', 35.16780747551577, 129.13281773933468, '051-242-2080', 'https://cafe.naver.com/cyoho111', '월·화·목·금 09:30-18:30 / 수 09:30-21:00 / 토 09:30-14:00 / 일 정기휴무, 08/15, 08/17 휴무', '기능과 심미 균형 잡힌 치과 치료. 투명교정(정밀 스캔 장비), 보철/임플란트(국내외 인증 프리미엄 라인업), 불필요한 과잉진료 없는 진료 철학.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('101', '39', '미주치과병원', 'DENTAL_HOSPITAL', '부산광역시 동래구 충렬대로 114(온천동)', 35.206226212221296, 129.07300842425235, '0507-1373-5124', 'http://www.mijudentalhp.com/', '월·화·수·금 09:00-18:00 / 목 정기휴무 / 토·일 09:00-14:00', '예약은 전화로 안내. 토요일·일요일에도 진료. 1994년 치과병원으로 개원.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('102', '40', '밝은눈안과병원', 'HOSPITAL', '부산광역시 부산진구 가야대로 729(부암동)', 35.15791396813579, 129.05137252829763, '051-1670-3993', 'https://mbusan.bgneye.co.kr', '월~금 09:00-18:00 (점심 13:00-14:00) / 토 09:00-17:00 / 일 정기휴무 (수요일은 노안·백내장센터만 운영)', '부산 서면 위치, B1F-8F 전 층 안과 병원. 스마일라식 100,000안 달성 인증병원. ZEISS 공식 인증센터. 9인 안과 전문의, 망막 전문의 상주.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OPHTHALMOLOGY'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('103', '42', '동의대부속 한방병원', 'KOREAN_MEDICINE_HOSPITAL', '부산 부산진구 양정로 62 (양정동, 동의의료원, 동의병원, 동의대학교한방병원) 동의대학교부속한방병원', 35.170093221793756, 129.07682819072966, '051-850-8673', 'https://www.demc.kr/hb/main/', '월~금 08:30-17:30 / 토 08:30-12:30 / 일 정기휴무, 08/15 광복절 휴무 (주말·공휴일 09:00-17:00)', '1990년부터 양방·한방 협진. 뇌혈관질환·안면마비 등 신경계통 질환, 척추관절질환 근육계통 질환 진료 역량 보유.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE'), (@hid, 'REHABILITATION');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('104', '43', '동의병원', 'GENERAL_HOSPITAL', '부산 부산진구 양정로 62 (양정동, 동의의료원, 동의병원, 동의대학교한방병원) 동의병원', 35.170093221793756, 129.07682819072966, '051-867-5101', 'https://www.demc.kr/', '월~금 08:30-17:30 / 토 08:30-12:30 / 일 정기휴무, 08/15 광복절 휴무', NULL, NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('105', '46', '피브엔 의원', 'CLINIC', '부산광역시 부산진구 가야대로 494, 503호(개금동, 5층)', 35.152844410471346, 129.02617584758033, '051-892-0005', 'http://www.pvn.co.kr/', '월~목 10:00-19:30 / 금 정기휴무 / 토 09:00-15:00 / 일 정기휴무, 08/12, 08/19 휴무', '30년 경력 도정화 대표 원장. 메타뷰 3D 피부진단시스템. 리프팅, 안티에이징, 색소질환, 흉터/켈로이드, 여드름 등 진료.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('106', '47', '박지호한의원', 'KOREAN_MEDICINE_CLINIC', '부산광역시 부산진구 가야대로 480, 2층(개금동)', 35.15287012767709, 129.02457065639618, '051-895-0006', 'https://www.instagram.com/parkjiho306/', '월~수·금 07:00-19:00 (점심 12:30-14:00) / 목 07:00-12:30 / 토·일 07:00-13:30', NULL, NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ORIENTAL_MEDICINE');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('107', '48', '일신기독병원', 'GENERAL_HOSPITAL', '부산 동구 정공단로 27 (좌천동, 일신기독병원) -', 35.135376204841464, 129.05394135494782, '051-630-0300', 'http://www.ilsin.or.kr/', '월~금 08:30-17:30 (점심 12:30-13:30) / 토 08:30-12:30 / 일 정기휴무, 08/15 광복절 휴무', '1952년 호주 의료선교사가 부산 서구 천막 진료소로 시작. 산부인과·소아청소년과·내과·외과 등 다수 진료과. 신생아집중치료실(NICU), 종합건강증진센터 운영.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(키워드매칭)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OBSTETRICS_GYNECOLOGY'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('108', '49', '청맥병원', 'HOSPITAL', '부산광역시 부산진구 중앙대로 716-1, 지하1층 ~ 지상6층(부전동)', 35.156665756773336, 129.05957344009028, '051-804-1119', 'https://www.youtube.com/@vascular_cheongmac', '월~금 09:00-18:00 (점심 12:30-13:30) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '부산혈관외과병원. 정맥질환, 동맥질환, 투석혈관, 여성질환(자궁근종 색전술) 특화 진료. 대학병원급 진단·치료 장비, 부산대·동아대·인제대 협력체계.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OBSTETRICS_GYNECOLOGY'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('109', '50', '의료법인 행도의료재단 해동병원', 'GENERAL_HOSPITAL', '부산광역시 영도구 태종로 133(봉래동3가)', 35.09195031747751, 129.04386580070866, '051-410-6300', 'https://www.hdh.co.kr/', '월 08:30-17:30 / 화~금 08:30-17:00 (점심 12:30-13:30) / 토 08:30-12:30 / 일 정기휴무, 08/15 광복절 휴무', '고객 감동 서비스를 지향하는 병원.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('110', '51', '시원항병원', 'HOSPITAL', '부산광역시 북구 금곡대로 27, 5층~10층(덕천동, 더청명빌딩)', 35.21233724660533, 129.00413926746103, '051-331-7275', 'http://시원항.com', '월 09:00-18:00 / 화~금 09:00-17:00 (점심 13:00-14:00) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '외과전문의 진료, 대장항문외과 세부전문의. 대학병원급 첨단 복강경 수술 시스템, 골반저 질환치료센터, 응급수술 가능, 통합암센터.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('111', '52', '정근안과병원', 'HOSPITAL', '부산광역시 부산진구 가야대로 767, 4층(부전동)', 35.158010742858025, 129.05550558993514, '051-668-8000', 'http://www.koreaeye.co.kr', '월~금 09:30-18:00 (점심 13:00-14:00) / 토 09:30-13:00 / 일 정기휴무, 08/15 광복절 휴무', '노안, 백내장, 라식, 라섹, 안성형 수술 전문. 스마트5초라식, 네비게이션 라식수술. 서면 롯데백화점 맞은편 위치.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY'), (@hid, 'OPHTHALMOLOGY'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('112', '53', '모델라인 의원', 'CLINIC', '부산광역시 부산진구 중앙대로 712, 6층(부전동)', 35.15624332714064, 129.05948209784563, '0507-1304-0691', 'http://bs.modellinemc.co.kr', '월·수·목·금 10:00-19:00 / 화 10:00-20:00 / 토·일 정기휴무 (매월 2·3주 금요일 09:00-18:00)', '팔지방흡입, 복부지방흡입, 허벅지지방흡입 등 부위별 체형 고려 지방흡입 전문.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('113', '54', '리오성형외과의원', 'CLINIC', '부산 부산진구 부전로66번길 34 (부전동) 3,4층', 35.155734945202425, 129.05654875090522, '051-818-0025', 'http://www.rio25.co.kr/', '월~금 10:00-18:30 / 토 09:30-15:00 / 일 정기휴무, 08/15 광복절 휴무', '1:1맞춤 진료, 담당집도의 책임제. 대리수술 배제, 멸균소독 시스템. 눈성형, 코성형, 이마거상, 안면거상, 지방이식/흡입, 가슴성형, 실리프팅.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'SKIN_BEAUTY'), (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '베트남'), (@hid, '기타');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('114', '56', 'ABC성형외과의원', 'CLINIC', '부산 부산진구 가야대로 769 (부전동, ABC메디컬센터) 11층', 35.15795628234134, 129.05577950700416, '051-817-0100', 'http://www.abcclinic.com/', '월~금 09:30-18:30 (점심 12:30-14:00) / 토 09:30-13:30 / 일 정기휴무', '서면 롯데백화점 정문 맞은편 ABC메디컬센터 11층. 성형외과 전문의 진료, 고품격 의료서비스.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('115', '57', '예바치과교정과치과의원', 'DENTAL_CLINIC', '부산광역시 부산진구 중앙대로 736 (부전동) 4층', 35.15823863487188, 129.0599364529087, '051-809-2861', 'https://sm.prettymiso.com/', '월~금 10:30-19:00 (점심 13:00-14:30) / 토 09:00-16:00 / 일 정기휴무, 08/15, 08/17 휴무', '치아교정 단일 과목 집중 교정 치과. 치과교정과 전문의 3인 진료, 인비절라인 공식 인증. 서면역 10번 출구 앞 경암센터 4층.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('116', '58', '해운대부민병원', 'GENERAL_HOSPITAL', '부산 해운대구 해운대로 584 (우동, 해운대 부민병원) 해운대부민병원', 35.161400865618084, 129.1555229376506, '1670-0082', 'https://blog.naver.com/buminhaeundae', '월 09:00-18:00 / 화~금 09:00-17:00 / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '지하 4층, 지상 13층 규모, 32명 의료진, 300병상 종합병원. 미국 HSS와 의료기술 협력, 스위맥스 등 재활 장비. 24시간 응급의료기관.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'zh'), (@hid, '러시아');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('117', '59', '예쁜미소바른이치과의원', 'DENTAL_CLINIC', '부산 동래구 동래로 25 (온천동) 이즈메디컬 4층', 35.212491722752375, 129.0783802102912, '0507-1402-2825', 'https://dc.prettymiso.com/', '월~금 11:00-19:00 (점심 13:00-14:30) / 토 10:00-16:00 / 일 정기휴무, 08/15 광복절 휴무', '예바치과교정과 네트워크 지점, 15년째 덕천에서 교정 전문. 치과교정과전문의 여자대표원장 2인.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'DENTAL');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('118', '60', '부산우리들병원', 'HOSPITAL', '부산 동래구 충렬대로 286 (낙민동) 동래우리들병원', 35.1999855000387, 129.0902234463966, '051-559-2000', 'http://busan.wooridul.co.kr/', '월~금 09:00-17:30 / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '부산,울산,경남 척추전문병원. 척추질환(허리/목/등), 관절질환(어깨/팔꿈치/손목/고관절/발목/무릎). 원인치료, 최소절개 지향.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'ja');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('119', '61', '의료법인정화의료재단 봉생기념병원', 'GENERAL_HOSPITAL', '부산 동구 중앙대로 401 (좌천동, 봉생병원) 봉생병원', 35.1309581452016, 129.0501761485346, '051-664-4000', 'http://www.bsmh.or.kr', NULL, '사람을 먼저 생각하는 신뢰의 의술, 봉생병원.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(종합병원 추정), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('120', '62', '이룸여성의원', 'CLINIC', '부산 부산진구 서면로 25 (부전동, 서면삼한골든뷰) 6층 이룸여성의원', 35.152946738926246, 129.05727330443096, '051-803-2616', 'http://www.babyeroom.com/', '월·수·금 08:30-19:00 / 화·목 08:30-16:30 / 토 08:30-12:30 / 일 정기휴무, 08/15 광복절 휴무', '임신의 희망을 드리는 난임진료 병원 이룸여성의원입니다.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'OBSTETRICS_GYNECOLOGY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('121', '63', '프라임병원', 'HOSPITAL', '부산 사하구 낙동남로 1410 (하단동) 프라임병원', 35.105887309601286, 128.96584084297137, '0507-1429-7833', 'http://www.disc21.co.kr/', '월~금 09:00-18:00 (점심 12:30-13:30) / 토 09:00-13:00 / 일 정기휴무, 08/15 광복절 휴무', '디스크 치료 특화 병원. 최신 진단 기기, 최첨단 디지털 영상 수술, 메덱스 운동치료 프로그램. 가능한 수술하지 않고 치료.', NULL, '2026-08-03', 'kyh', '느슨한 매칭으로 추가된 보조 태그: 재활(키워드매칭), 건강검진(종합병원 추정)');
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'ETC'), (@hid, 'REHABILITATION'), (@hid, 'HEALTH_CHECKUP');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko');

INSERT INTO hospital (reg_no, original_reg_no, name, institution_type, address, latitude, longitude, phone, website, business_hours, description_ko, description_en, verified_at, verified_by, notes) VALUES ('122', '65', '쉬즈성형외과의원', 'CLINIC', '부산 중구 광복로 73 (광복동2가, 비비안 광복점) 4-5층', 35.09885669670619, 129.0329530127892, '051-710-0788', 'http://shesps.com/', '월~금 09:30-19:30 (점심 12:00-13:00) / 토 09:30-17:30 / 일 정기휴무, 08/15 광복절 휴무', '2000년 부산 중구 남포동 개원, 성형외과 전문의 의료기관. 눈주름·얼굴주름수술 주요 수술, 안티에이징 전문. 메디컬 스킨케어 프로그램 병행.', NULL, '2026-08-03', 'kyh', NULL);
SET @hid = LAST_INSERT_ID();
INSERT INTO hospital_specialty (hospital_id, specialty) VALUES (@hid, 'PLASTIC_SURGERY');
INSERT INTO hospital_target_country (hospital_id, country_value) VALUES (@hid, 'ko'), (@hid, 'en'), (@hid, 'zh'), (@hid, 'ja'), (@hid, '러시아'), (@hid, '몽골'), (@hid, '베트남');

