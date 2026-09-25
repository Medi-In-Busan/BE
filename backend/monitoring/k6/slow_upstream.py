"""외부 API가 느리거나 응답하지 않는 상황을 로컬에서 흉내 내는 가짜 서버.

모든 요청을 DELAY초 붙잡고 있다가 503을 돌려준다(캐시에 가짜 번역이 쌓이지 않도록 실패로 응답).
Papago/TourAPI 대신 이 서버를 가리키게 하면 실제 한도를 쓰지 않고 병목 1~4번을 재현할 수 있다.

    python slow_upstream.py            # 기본 9999 포트, 60초 지연
    python slow_upstream.py 9999 600   # 600초 지연 — 사실상 "응답 없음"(타임아웃 부재 #3 확인용)
"""
import sys
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

PORT = int(sys.argv[1]) if len(sys.argv) > 1 else 9999
DELAY = float(sys.argv[2]) if len(sys.argv) > 2 else 60.0


class SlowHandler(BaseHTTPRequestHandler):
    def _slow(self):
        length = int(self.headers.get("Content-Length") or 0)
        if length:
            self.rfile.read(length)
        time.sleep(DELAY)
        body = b'{"error":"slow upstream"}'
        self.send_response(503)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    do_GET = _slow
    do_POST = _slow

    def log_message(self, fmt, *args):
        sys.stderr.write("slow-upstream: " + (fmt % args) + "\n")


if __name__ == "__main__":
    print(f"slow upstream on :{PORT}, delay={DELAY}s")
    ThreadingHTTPServer(("0.0.0.0", PORT), SlowHandler).serve_forever()
