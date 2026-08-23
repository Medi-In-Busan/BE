# Wellness Resources

## Data Sources

- TourAPI: `https://apis.data.go.kr/B551011/KorService2`
- Kakao Local: `https://dapi.kakao.com/v2/local/search/keyword.json`

## Busan Region Codes

- 일반 TourAPI(Kor/Eng/Jpn/Chs/Wellness/무장애)는 `lDongRegnCd=26`을 사용한다.
- 구 단위 일반 TourAPI 조회에는 `lDongSignguCd`를 사용한다.
- 연관 관광지, 중심 관광지, 집중률 예측 API는 `areaCd=26`과 5자리 `signguCd`를 사용한다.
- 코드의 단일 출처는 `backend/.../wellness/service/BusanTourismCodes.java`다.

## Image Policy

- TourAPI `firstimage` / `firstimage2` URLs are stored as remote `imageUrl`.
- Original remote image resolution is not normalized by the backend.
- Android wellness list/detail should render remote images in a fixed 16:9 container.
- Recommended display size:
  - List card: `360x202dp`
  - Detail hero: `360x202dp` minimum, full-width 16:9
- Kakao Local does not provide place images through the keyword search response, so `imageUrl` is `null`.

## Text Policy

- TourAPI list ingestion stores title, address, tel, coordinates, modified date, and image URL.
- Kakao Local ingestion stores place name, address, coordinates, phone, category mapping, and Kakao place URL in `description`.
- Long descriptive copy should be supplied by curated seed data or a later detail-enrichment job.
