CREATE TABLE wellness_place (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_id     VARCHAR(50)  NOT NULL,
    name           VARCHAR(200) NOT NULL,
    place_type     VARCHAR(30)  NOT NULL,
    address        VARCHAR(300) NOT NULL,
    latitude       DOUBLE,
    longitude      DOUBLE,
    image_url      VARCHAR(500),
    description    TEXT,
    phone_number   VARCHAR(30),
    modified_date  DATE,
    CONSTRAINT uk_wellness_place_content_id UNIQUE (content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_wellness_place_type ON wellness_place (place_type);

INSERT INTO wellness_place (content_id, name, place_type, address, latitude, longitude, image_url, description, phone_number, modified_date) VALUES
('place-1', '해운대 해수욕장', 'TOURIST_ATTRACTION', '부산 해운대구 해운대해변로 264', 35.1587, 129.1604, NULL, '진료 후 가볍게 바닷가를 걸으며 회복 시간을 보내기 좋은 부산 대표 관광지입니다.', NULL, '2026-08-03'),
('place-2', '동백섬 산책로', 'WALK', '부산광역시 해운대구 동백로 67', 35.1532, 129.1515, NULL, '완만한 해안 산책로와 전망 포인트가 있어 무리하지 않는 회복형 코스에 적합합니다.', NULL, '2026-08-03'),
('place-3', '스파랜드 센텀시티', 'SPA', '부산광역시 해운대구 센텀남대로 35', 35.1688, 129.1295, NULL, '휴식 중심 일정에 넣기 좋은 도심형 스파 시설입니다. 시술 직후 이용 가능 여부는 의료진 안내를 우선하세요.', '1668-2850', '2026-08-03'),
('place-4', '센텀시티 카페 거리', 'RESTAURANT', '부산광역시 해운대구 센텀 일대', 35.1697, 129.1326, NULL, '대기 시간이나 진료 후 짧은 휴식에 맞춰 들르기 쉬운 카페·가벼운 식사 권역입니다.', NULL, '2026-08-03'),
('place-5', '신세계 센텀시티', 'SHOPPING', '부산광역시 해운대구 센텀남대로 35', 35.1688, 129.1295, NULL, '실내 이동 중심이라 날씨 영향을 덜 받는 쇼핑·식사·휴식 복합 공간입니다.', '1588-1234', '2026-08-03');
