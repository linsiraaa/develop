(function (window, $) {
    'use strict';

    var TOKEN_KEY = 'traffic_jwt';

    window.trafficAuth = {
        getToken: function () {
            return window.localStorage.getItem(TOKEN_KEY);
        },
        setToken: function (token) {
            if (token) window.localStorage.setItem(TOKEN_KEY, token);
        },
        clearToken: function () {
            window.localStorage.removeItem(TOKEN_KEY);
        }
    };

    // 为所有数据请求自动添加 Bearer JWT。
    $.ajaxSetup({
        beforeSend: function (xhr) {
            var token = window.trafficAuth.getToken();
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        },
        error: function (xhr, status, error) {
            if (xhr.status === 401 && window.location.pathname.indexOf('login2.html') === -1) {
                window.trafficAuth.clearToken();
                window.location.href = '/login2.html';
            }
        }
    });
})(window, window.jQuery);
