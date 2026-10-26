const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');

const root = path.resolve(__dirname, '../../main/resources/static');
const html = fs.readFileSync(path.join(root, 'index.html'), 'utf8');
const visual = fs.readFileSync(path.join(root, 'js/visual.js'), 'utf8');

function evaluateFunction(source, name) {
    const match = source.match(new RegExp('function ' + name + '\\([^)]*\\) \\{[\\s\\S]*?^ {0,4}\\}', 'm'));
    assert.ok(match, `${name} function exists`);
    return vm.runInNewContext(`(${match[0]})`);
}

test('traffic month labels and tooltip show 26年 for all months', () => {
    const label = evaluateFunction(html, 'formatTrafficMonth');
    assert.equal(label('2025-01'), '26年1月');
    assert.equal(label('2025-08'), '26年8月');
    assert.equal(label('2025-12'), '26年12月');
    assert.match(html, /let time = formatTrafficMonth\(params\[0\]\.name\)/);
    assert.match(html, /return formatTrafficMonth\(value\)/);
});

test('ring values: input is 万辆，第一行显示 亿辆（保留三位小数），第二行显示 万辆（带千分位）', () => {
    const format = evaluateFunction(visual, 'formatVehiclesInYi');
    // 输入是万辆，换算为亿辆 = 输入 / 10000
    assert.equal(format(412557), '41.256亿');      // 412557 万 = 41.2557 亿 → 41.256 亿
    assert.equal(format(172087), '17.209亿');     // 172087 万 = 17.2087 亿 → 17.209 亿
    assert.equal(format(15890), '1.589亿');       // 15890 万 = 1.589 亿
    assert.equal(format(1039578), '103.958亿');   // 1039578 万 = 103.9578 亿 → 103.958 亿
    assert.match(visual, /function formatRingLabel\(title, count\)/);
    assert.match(visual, /formatVehiclesInYi\(count\)/);
    // 第二行应为原始数值 + '万辆'，带千分位
    assert.match(visual, /Math\.round\(Number\(count\)\)\.toLocaleString\('zh-CN'\).*万辆/);
    assert.match(visual, /formatRingLabel\('车辆总数', data\.vehicleTotal\)/);
    assert.match(visual, /formatRingLabel\('今日上线', data\.todayOnline\)/);
    assert.match(visual, /formatRingLabel\('今日报警', data\.todayAlarm\)/);
    assert.match(visual, /text:.*\\n.*formatVehiclesInYi|text: titleText/);
});

test('traffic labels remain readable at 12 categories', () => {
    assert.match(html, /axisLabel:\s*\{\s*interval: 0,\s*rotate: 60,/);
    assert.match(html, /grid: \{ y2: 75 \}/);
});

test('event radar keeps its original radius and animates updates', () => {
    assert.match(html, /radius: '75%'[\s\S]*?type: 'radar'/);
    assert.match(html, /animationDurationUpdate: [1-9]\d*/);
    assert.match(html, /animationEasingUpdate: 'cubicOut'/);
    assert.match(html, /myChart3\.setOption\(option3\);/);
});
