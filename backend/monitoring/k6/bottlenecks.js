// 병목 1~4번 재현용 부하 스크립트. 사용법은 monitoring/README.md.
//  - detail    : 다국어 장소 상세(번역 전역 락 #1, 커넥션 2개 점유 #2, Papago 타임아웃 부재 #3)
//  - crowding  : 혼잡도 카탈로그(혼잡도 전역 락 #4)
//  - bystander : 번역·혼잡도와 무관한 병원 목록 — 위 병목이 무관한 API까지 번지는지 보는 대조군
import http from 'k6/http';
import { check } from 'k6';

const BASE = __ENV.BASE_URL || 'http://host.docker.internal:8080';
const DURATION = __ENV.DURATION || '2m';
const LANGS = ['en', 'ja', 'zh'];
// 실제 Papago 키로 돌리면 캐시 미스마다 번역 한도를 쓴다 — 표본 수 × 3개 언어가 최대 번역 건수다.
const SAMPLE = Number(__ENV.SAMPLE || 50);

export const options = {
  scenarios: {
    detail: { executor: 'constant-vus', exec: 'detail', vus: Number(__ENV.DETAIL_VUS || 20), duration: DURATION },
    crowding: { executor: 'constant-vus', exec: 'crowding', vus: Number(__ENV.CROWDING_VUS || 5), duration: DURATION },
    bystander: {
      executor: 'constant-arrival-rate', exec: 'bystander',
      rate: 5, timeUnit: '1s', duration: DURATION, preAllocatedVUs: 20, maxVUs: 200,
    },
  },
  summaryTrendStats: ['avg', 'p(95)', 'max'],
};

export function setup() {
  const res = http.get(`${BASE}/api/wellness/places`, { timeout: '120s' });
  const ids = res.json().map((place) => place.contentId).filter(Boolean);
  for (let i = ids.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [ids[i], ids[j]] = [ids[j], ids[i]];
  }
  return { ids: ids.slice(0, SAMPLE) };
}

export function detail(data) {
  const id = data.ids[Math.floor(Math.random() * data.ids.length)];
  const lang = LANGS[Math.floor(Math.random() * LANGS.length)];
  const res = http.get(`${BASE}/api/wellness/places/${id}?language=${lang}`, { tags: { name: 'detail' }, timeout: '300s' });
  check(res, { 'detail 200': (r) => r.status === 200 });
}

export function crowding() {
  const res = http.get(`${BASE}/api/wellness/tourism/catalog/CROWDING?language=ko`, { tags: { name: 'crowding' }, timeout: '300s' });
  check(res, { 'crowding 200': (r) => r.status === 200 });
}

export function bystander() {
  const res = http.get(`${BASE}/api/hospitals?size=20`, { tags: { name: 'bystander' }, timeout: '300s' });
  check(res, { 'hospitals 200': (r) => r.status === 200 });
}
