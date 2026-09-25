"""개선 전/후 k6 실행을 같은 상대 시간축(각 실행 시작 기준 초)으로 겹쳐 그린다.

    python compare_figure.py 2026-09-25T16:28:00 2026-09-25T17:45:05 180 out.png

인자: 개선 전 시작 시각, 개선 후 시작 시각, 그릴 길이(초), 출력 경로.
하단 수치 표(ROWS)는 그 두 실행의 값으로 직접 고쳐 쓴다(k6 요약 + 서버 지표).
"""
import json, sys, urllib.request, urllib.parse, datetime as dt
import matplotlib; matplotlib.use("Agg")
import matplotlib.pyplot as plt

plt.rcParams["font.family"] = "Malgun Gothic"; plt.rcParams["axes.unicode_minus"] = False
BEFORE = int(dt.datetime.fromisoformat(sys.argv[1]).timestamp())
AFTER = int(dt.datetime.fromisoformat(sys.argv[2]).timestamp())
SPAN = int(sys.argv[3]); OUT = sys.argv[4]

def series(expr, start):
    u = "http://localhost:9090/api/v1/query_range?" + urllib.parse.urlencode(
        {"query": expr, "start": start, "end": start + SPAN, "step": 5})
    r = json.load(urllib.request.urlopen(u))["data"]["result"]
    if not r:
        return [0, SPAN], [0, 0]
    vals = r[0]["values"]
    return [float(t) - start for t, _ in vals], [float(v) for _, v in vals]

def increase(expr, start):
    # 카운터는 첫 오류 전엔 시계열이 없으므로 0에서 시작하도록 앞에 0을 붙이고, 실행 시작 시점 값을 뺀다.
    t, v = series(expr, start)
    base = v[0] if t and t[0] <= 5 else 0.0
    return [0.0] + t, [0.0] + [x - base for x in v]

ROWS = [
    ("다국어 상세 성공률 (서버 기준)", "37%  (28 / 76)", "100%  (202,781 / 202,781)"),
    ("다국어 상세 처리량", "0.6 req/s", "약 1,400 req/s"),
    ("다국어 상세 p95 (서버)", "30 s", "4 ms"),
    ("병원 목록(무관 API) 500", "18건", "0건"),
    ("커넥션 대기 max / 획득 타임아웃", "157 / 66건", "0 / 0건"),
    ("Tomcat busy 스레드 max", "161 / 200", "19 / 200"),
]

BG, FG, GRID = "#111217", "#d8d9da", "#2a2d35"
RED, GREEN = "#f2495c", "#73bf69"
fig = plt.figure(figsize=(16, 9.6), facecolor=BG)
fig.suptitle("MediIn Busan 백엔드 — DB 커넥션 풀 교착 수정 전/후 (k6, 같은 시나리오)",
             color="white", fontsize=18, fontweight="bold", x=0.02, ha="left", y=0.975)
fig.text(0.02, 0.928, "다국어 상세 20 VU + 혼잡도 5 VU + 병원 목록 5 req/s, 2분 · 실제 Papago/TourAPI · Hikari 풀 10 · "
         "수정: Papago 호출을 트랜잭션 밖으로(요청당 커넥션 2개 → 순간 1개) + 번역 전역 락 제거",
         color="#9fa7b3", fontsize=10.5)

def style(ax, title):
    ax.set_facecolor(BG); ax.set_title(title, color="white", loc="left", fontsize=12.5, pad=8)
    ax.tick_params(colors=FG); [s.set_color(GRID) for s in ax.spines.values()]
    ax.grid(color=GRID, lw=0.6); ax.set_xlim(0, SPAN); ax.set_xlabel("테스트 시작 후 경과 (초)", color="#9fa7b3")

def pair(ax, expr, cumulative=False, legend_loc="upper right"):
    get = increase if cumulative else series
    tb, vb = get(expr, BEFORE); ta, va = get(expr, AFTER)
    if cumulative:
        ax.step(tb, vb, where="post", color=RED, lw=2, label="수정 전"); ax.step(ta, va, where="post", color=GREEN, lw=2.4, label="수정 후")
    else:
        ax.fill_between(tb, vb, color=RED, alpha=.18); ax.plot(tb, vb, color=RED, lw=2, label="수정 전")
        ax.plot(ta, va, color=GREEN, lw=2.4, label="수정 후")
    ax.legend(facecolor=BG, labelcolor=FG, edgecolor=GRID, loc=legend_loc)

ax1 = fig.add_axes([0.05, 0.53, 0.28, 0.33]); style(ax1, "커넥션 대기 (Hikari pending)")
pair(ax1, "sum(hikaricp_connections_pending)")
ax2 = fig.add_axes([0.37, 0.53, 0.28, 0.33]); style(ax2, "Tomcat 요청 스레드 사용")
pair(ax2, "sum(tomcat_threads_busy_threads)"); ax2.axhline(200, color="#8ab8ff", ls="--", lw=1)
ax3 = fig.add_axes([0.69, 0.53, 0.28, 0.33]); style(ax3, "HTTP 500 누적 (전체 API)")
pair(ax3, 'sum(http_server_requests_seconds_count{status="500"})', cumulative=True, legend_loc="upper left")

ax4 = fig.add_axes([0.05, 0.07, 0.92, 0.36]); ax4.set_facecolor(BG); ax4.axis("off")
ax4.text(0.0, 0.97, "지표", color="#9fa7b3", fontsize=12, transform=ax4.transAxes)
ax4.text(0.56, 0.97, "수정 전", color=RED, fontsize=12, fontweight="bold", ha="right", transform=ax4.transAxes)
ax4.text(0.99, 0.97, "수정 후", color=GREEN, fontsize=12, fontweight="bold", ha="right", transform=ax4.transAxes)
for i, (k, b, a) in enumerate(ROWS):
    y = 0.84 - i * 0.145
    ax4.plot([0, 1], [y - 0.07, y - 0.07], color=GRID, lw=0.8, transform=ax4.transAxes)
    ax4.text(0.0, y, k, color=FG, fontsize=13, va="center", transform=ax4.transAxes)
    ax4.text(0.56, y, b, color="white", fontsize=14, va="center", ha="right", transform=ax4.transAxes)
    ax4.text(0.99, y, a, color="white", fontsize=14, fontweight="bold", va="center", ha="right", transform=ax4.transAxes)
fig.text(0.02, 0.018, "k6 클라이언트 측 연결 실패 29건(서버 미도달, 초당 1,600건을 Docker Desktop 포워딩으로 보낸 부하 발생기 한계)은 서버 지표에 0건. "
         "처리량 증가분의 대부분은 번역 캐시 히트(Papago 호출 162건).", color="#9fa7b3", fontsize=10)
fig.savefig(OUT, dpi=150, facecolor=BG); print(OUT)
