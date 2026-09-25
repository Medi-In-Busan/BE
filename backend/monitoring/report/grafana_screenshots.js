// Grafana 대시보드/패널 스크린샷. 사용법은 같은 폴더 README.md.
const puppeteer = require('puppeteer-core');
const OUT = process.argv[2];
const FROM = new Date(process.env.FROM).getTime();
const TO = new Date(process.env.TO).getTime();
const BASE = 'http://localhost:3000';
const UID = 'mediinbusan-backend-bottlenecks';
const auth = 'Basic ' + Buffer.from(process.env.GRAFANA_AUTH || 'admin:admin').toString('base64');
const panels = [
  [2, '01-api-p95'], [3, '02-api-rps-5xx'],
  [5, '03-lock-queue'], [6, '04-lock-wait'], [7, '05-lock-held'],
  [9, '06-hikari-active-pending'], [10, '07-hikari-acquire'], [11, '08-hikari-usage-timeout'],
  [13, '09-external-api-p95'], [17, '10-tomcat-threads'],
  [19, '11-translation-cache-hit'], [21, '12-crowding-snapshot-build'],
];
(async () => {
  const browser = await puppeteer.launch({
    executablePath: process.env.CHROME || 'C:/Program Files/Google/Chrome/Application/chrome.exe',
    headless: true, args: ['--lang=ko-KR'],
  });
  const page = await browser.newPage();
  await page.setExtraHTTPHeaders({ Authorization: auth });
  const range = `from=${FROM}&to=${TO}&timezone=browser&theme=dark`;
  // 대시보드 전체
  await page.setViewport({ width: 1600, height: 2200, deviceScaleFactor: 1.5 });
  await page.goto(`${BASE}/d/${UID}/?${range}&kiosk&refresh=`, { waitUntil: 'networkidle0', timeout: 90000 });
  await new Promise(r => setTimeout(r, 4000));
  await page.screenshot({ path: `${OUT}/00-dashboard-full.png`, fullPage: true });
  // 패널 단위
  await page.setViewport({ width: 1000, height: 420, deviceScaleFactor: 2 });
  for (const [id, name] of panels) {
    await page.goto(`${BASE}/d-solo/${UID}/?${range}&panelId=${id}`, { waitUntil: 'networkidle0', timeout: 60000 });
    await new Promise(r => setTimeout(r, 2500));
    await page.screenshot({ path: `${OUT}/${name}.png` });
    console.log('saved', name);
  }
  await browser.close();
})().catch(e => { console.error(e); process.exit(1); });
