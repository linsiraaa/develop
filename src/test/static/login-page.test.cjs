const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

const root = path.resolve(__dirname, '../../main/resources/static');
const page = fs.readFileSync(path.join(root, 'login2.html'), 'utf8');

test('登录注册页仍调用既有后端接口与参数名', () => {
    assert.match(page, /\/loginAction/);
    assert.match(page, /\/registerAction/);
    // 参数名必须是后端 @RequestParam 期望的 username / password
    assert.match(page, /\.set\('username',\s*username\)/);
    assert.match(page, /\.set\('password',\s*password\)/);
    // 必须用 POST 表单编码，与 @RequestParam 绑定方式一致
    assert.match(page, /method:\s*'POST'/);
    assert.match(page, /x-www-form-urlencoded/);
    // 登录成功仍跳转大屏
    assert.match(page, /location\.href\s*=\s*'index\.html'/);
});

test('错误提示改为页内显示，不再使用 alert 阻塞弹窗', () => {
    assert.doesNotMatch(page, /\balert\s*\(/);
    assert.match(page, /id="login-error"/);
    assert.match(page, /id="register-error"/);
});

test('表单具备可访问性与键盘提交能力', () => {
    // 每个输入框都有 label 或 aria-label
    assert.match(page, /aria-label="用户名"/);
    assert.match(page, /aria-label="密码"/);
    // 用 form submit 事件而非裸 button click，保证回车可提交
    assert.match(page, /addEventListener\('submit'/);
    assert.match(page, /type="submit"/);
});

test('不依赖外部 CDN 资源，离线也能正常渲染', () => {
    assert.doesNotMatch(page, /apps\.bdimg\.com/);
    assert.doesNotMatch(page, /cdnjs\.cloudflare\.com/);
    assert.doesNotMatch(page, /code\.jquery\.com/);
});

test('适配移动端与减少动效偏好', () => {
    assert.match(page, /@media[^{]*max-width/);
    assert.match(page, /prefers-reduced-motion/);
});

test('密码可见性切换存在且默认隐藏', () => {
    assert.match(page, /type="password"/);
    assert.match(page, /toggle-password/);
});

test('提交函数引用的每个元素 id 都真实存在（回归：曾因 form.id 拼接导致点击无响应）', () => {
    // 收集页面里真实存在的 id
    const ids = new Set([...page.matchAll(/id="([^"]+)"/g)].map(m => m[1]));

    // 收集 JS 里所有 getElementById('...') 的字符串字面量参数
    const literalRefs = [...page.matchAll(/getElementById\(\s*'([^']+)'\s*\)/g)].map(m => m[1]);
    for (const ref of literalRefs) {
        assert.ok(ids.has(ref), `getElementById('${ref}') 指向不存在的 id`);
    }

    // 拼接式引用必须用正确的 prefix，不能从 form.id 反推
    assert.doesNotMatch(page, /getElementById\(form\.id \+/, '不得用 form.id 拼接元素 id（login-form + -error ≠ login-error）');
    assert.match(page, /submitForm\(loginForm, '\/loginAction', 'login'\)/);
    assert.match(page, /submitForm\(registerForm, '\/registerAction', 'register'\)/);

    // 两个 prefix 拼出的 id 必须全部存在
    for (const prefix of ['login', 'register']) {
        for (const suffix of ['-error', '-username', '-password']) {
            const want = prefix + suffix;
            assert.ok(ids.has(want), `缺少元素 id: ${want}`);
        }
    }
    // 注册表单额外需要确认密码框
    assert.ok(ids.has('register-confirm'), '缺少元素 id: register-confirm');
});
