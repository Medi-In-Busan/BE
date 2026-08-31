#!/bin/bash
# 최초 배포 시 Let's Encrypt 인증서를 발급받는 부트스트랩 스크립트. EC2에서
# docker-compose.prod.yml과 같은 디렉터리(~/mediinbusan-deploy)에 놓고 실행한다.
#
# - .github/workflows/backend-deploy.yml의 deploy 잡이 매 배포마다 이 스크립트를 호출하지만,
#   인증서가 이미 존재하면 아무 것도 하지 않고 즉시 종료한다(idempotent) — Let's Encrypt는
#   동일 도메인에 대해 주당 발급 횟수 제한이 있어서 매 배포마다 재발급하면 안 된다.
# - nginx 컨테이너가 아직 떠 있지 않은(=인증서가 없는) 시점에만 호출되므로, certbot의
#   standalone 모드로 80번 포트를 직접 점유해서 인증서를 받는다. 이후 갱신은
#   docker-compose.prod.yml의 certbot 서비스(webroot 모드, 12시간마다 renew 시도)가 맡는다.
set -e

: "${DOMAIN:?DOMAIN 환경변수가 필요합니다}"
: "${CERTBOT_EMAIL:?CERTBOT_EMAIL 환경변수가 필요합니다}"

cd "$(dirname "$0")/.."   # docker-compose.prod.yml이 있는 디렉터리 기준으로 동작

if docker compose -f docker-compose.prod.yml run --rm --entrypoint sh certbot \
     -c "test -d /etc/letsencrypt/live/$DOMAIN" >/dev/null 2>&1; then
  echo "[init-letsencrypt] $DOMAIN 인증서가 이미 존재함 — 발급 건너뜀"
  exit 0
fi

echo "[init-letsencrypt] $DOMAIN 인증서 신규 발급 (standalone, 80번 포트 임시 사용)"
docker compose -f docker-compose.prod.yml run --rm -p 80:80 --entrypoint " \
  certbot certonly --standalone \
    -d $DOMAIN \
    --email $CERTBOT_EMAIL \
    --rsa-key-size 4096 \
    --agree-tos \
    --non-interactive" certbot

echo "[init-letsencrypt] 발급 완료"
