"""Prometheus 데이터로 부하 테스트 요약 그림을 만든다.

    python summary_figure.py 2026-09-25T16:28:00 2026-09-25T16:31:15 out.png

④ 핵심 수치 표(rows)는 해당 실행의 값으로 직접 고쳐 쓴다(k6 요약과 대시보드에서 확인).
"""
import json,sys,urllib.request,urllib.parse,datetime as dt
import matplotlib; matplotlib.use("Agg")
import matplotlib.pyplot as plt, matplotlib.dates as md
plt.rcParams["font.family"]="Malgun Gothic"; plt.rcParams["axes.unicode_minus"]=False
S=int(dt.datetime.fromisoformat(sys.argv[1]).timestamp()); E=int(dt.datetime.fromisoformat(sys.argv[2]).timestamp())
def q(expr):
    u="http://localhost:9090/api/v1/query_range?"+urllib.parse.urlencode({"query":expr,"start":S,"end":E,"step":5})
    r=json.load(urllib.request.urlopen(u))["data"]["result"]
    return {tuple(sorted((k,v) for k,v in s["metric"].items() if k not in("application","instance","job","__name__"))):
            ([dt.datetime.fromtimestamp(float(t)) for t,_ in s["values"]],[float(v) for _,v in s["values"]]) for s in r}
def one(expr): return list(q(expr).values())[0]
pend=one("sum(hikaricp_connections_pending)"); act=one("sum(hikaricp_connections_active)")
busy=one("sum(tomcat_threads_busy_threads)")
e500=q('sum by (uri)(http_server_requests_seconds_count{status="500"})')
lockq=q("mediinbusan_lock_queue")

BG="#111217"; FG="#d8d9da"; GRID="#2a2d35"
fig=plt.figure(figsize=(16,9),facecolor=BG)
fig.suptitle("MediIn Busan 백엔드 — k6 부하 테스트 베이스라인 (2026-09-25, 개선 전)",color="white",fontsize=18,fontweight="bold",x=0.02,ha="left",y=0.975)
fig.text(0.02,0.925,"다국어 상세 20 VU + 혼잡도 5 VU + 병원 목록 5 req/s, 2분 · 실제 Papago/TourAPI · Hikari 풀 10 · Tomcat 스레드 200",color="#9fa7b3",fontsize=11)
def style(ax,title):
    ax.set_facecolor(BG); ax.set_title(title,color="white",loc="left",fontsize=12.5,pad=8)
    ax.tick_params(colors=FG); [s.set_color(GRID) for s in ax.spines.values()]
    ax.grid(color=GRID,lw=0.6); ax.xaxis.set_major_formatter(md.DateFormatter("%H:%M:%S"))
ax1=fig.add_axes([0.05,0.52,0.43,0.33]); style(ax1,"① DB 커넥션 풀 고갈 — 대기 157, 사용 10/10")
ax1.fill_between(*pend,color="#f2cc0c",alpha=.25); ax1.plot(*pend,color="#f2cc0c",lw=2,label="pending (커넥션 대기)")
ax1.plot(*act,color="#73bf69",lw=2,label="active (사용 중)"); ax1.axhline(10,color="#8ab8ff",ls="--",lw=1.2,label="풀 크기 10")
ax1.legend(facecolor=BG,labelcolor=FG,edgecolor=GRID,loc="upper left")
ax2=fig.add_axes([0.55,0.52,0.42,0.33]); style(ax2,"② 요청 스레드 적체 — Tomcat busy 161/200")
ax2.fill_between(*busy,color="#ff9830",alpha=.25); ax2.plot(*busy,color="#ff9830",lw=2,label="busy threads")
ax2.axhline(200,color="#f2495c",ls="--",lw=1.2,label="max 200"); ax2.set_ylim(0,215); ax2.legend(facecolor=BG,labelcolor=FG,edgecolor=GRID,loc="upper left")
ax3=fig.add_axes([0.05,0.08,0.43,0.33]); style(ax3,"③ HTTP 500 누적 — 30 s(커넥션 타임아웃) 주기로 계단식 증가")
names={"/api/wellness/places/{contentId}":("다국어 장소 상세","#f2495c"),"/api/hospitals":("병원 목록 (대조군)","#8ab8ff")}
for k,(t,v) in e500.items():
    uri=dict(k).get("uri"); 
    if uri in names: ax3.step([dt.datetime.fromtimestamp(S)]+t,[0.0]+v,where="post",lw=2,color=names[uri][1],label=names[uri][0])
ax3.legend(facecolor=BG,labelcolor=FG,edgecolor=GRID,loc="upper left")
ax4=fig.add_axes([0.55,0.08,0.42,0.33]); ax4.set_facecolor(BG); ax4.axis("off")
ax4.set_title("④ 핵심 수치",color="white",loc="left",fontsize=12.5,pad=8)
rows=[("다국어 상세 실패율","48 / 76 (63%)","#f2495c"),
      ("병원 목록(무관 API) p95 / 실패","29.9 s / 18건","#f2495c"),
      ("커넥션 획득 타임아웃","66건 (30 s 대기 후)","#f2cc0c"),
      ("혼잡도 락 대기 max / 점유 max","126 s / 61 s","#ff9830"),
      ("혼잡도 스냅샷 생성","31.3 s (TourAPI 호출 합 약 4 s)","#ff9830"),
      ("외부 API p95 (Papago / TourAPI)","0.44 s / 0.29 s — 외부는 정상","#73bf69")]
for i,(k,v,c) in enumerate(rows):
    y=0.9-i*0.155
    ax4.add_patch(plt.Rectangle((0,y-0.06),0.012,0.11,color=c,transform=ax4.transAxes))
    ax4.text(0.03,y,k,color="#9fa7b3",fontsize=11.5,transform=ax4.transAxes,va="center")
    ax4.text(0.99,y,v,color="white",fontsize=13,fontweight="bold",transform=ax4.transAxes,va="center",ha="right")
fig.text(0.02,0.015,"원인: 읽기 전용 트랜잭션이 커넥션을 쥔 채 REQUIRES_NEW로 2번째 커넥션을 요청 → 동시 요청이 풀을 나눠 가진 채 서로를 기다림(풀 교착, 30 s 타임아웃으로만 풀림). 외부 API는 빨랐다 — 병목은 내부 구조.",color="#9fa7b3",fontsize=10.5)
out=sys.argv[3]
fig.savefig(out,dpi=150,facecolor=BG); print(out)
