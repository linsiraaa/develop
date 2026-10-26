const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

const staticDir = path.resolve(__dirname, '../../main/resources/static');
const html = fs.readFileSync(path.join(staticDir, 'index.html'), 'utf8');
const css = fs.readFileSync(path.join(staticDir, 'css/visual.css'), 'utf8');

test('大屏存在独立背景层，且不依赖 body 的 z-index:-2', () => {
    // body 自身仍是 .ksh（保持既有结构），但它带 z-index:-2，不能承载伪元素背景
    assert.match(css, /\.ksh\{[^}]*z-index:-2/);

    // 必须有一个独立的 fixed 背景层，z-index 为 0（不参与负层叠）
    assert.match(css, /\.bg-tech\{[^}]*position:fixed/);
    assert.match(css, /\.bg-tech\{[^}]*z-index:0/);

    // HTML 中该层存在，且在 .visual 之前
    const bgIdx = html.indexOf('bg-tech');
    const visualIdx = html.indexOf('class="visual"');
    assert.ok(bgIdx > -1, '缺少 .bg-tech 背景层元素');
    assert.ok(bgIdx < visualIdx, '背景层必须在内容之前');
});

test('科技感背景包含网格与流动光效，且尊重减少动效偏好', () => {
    // 网格纹理
    assert.match(css, /\.bg-tech::before[\s\S]*?linear-gradient/);
    // 流动/漂移动画
    assert.match(css, /@keyframes\s+bgGridFlow|@keyframes\s+bgAurora/);
    // 减少动效偏好必须关闭动画
    assert.match(css, /@media\s*\(prefers-reduced-motion:\s*reduce\)[\s\S]*?animation[^;]*none|animation-duration:\s*0\.01ms/);
});

test('背景层不拦截鼠标事件', () => {
    assert.match(css, /\.bg-tech\{[^}]*pointer-events:none/);
});

test('加载遮罩仍覆盖在最上层', () => {
    assert.match(css, /#load\{[^}]*z-index:999/);
});
