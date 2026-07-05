// 初始化图表容器
import axios from "./jquery.min.js";

var chartContainers = {
    main1: null,
    // 其他图表容器...
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 初始化图表容器对象
    chartContainers.main1 = echarts.init(document.getElementById('main1'));

    // 加载交通流量数据
    loadTrafficFlowData();
});

// 加载交通流量数据
function loadTrafficFlowData() {
    axios.get('/api/traffic-flow')
        .then(response => {
            const data = response.data;
            renderTrafficFlowChart(data);
        })
        .catch(error => {
            showError('交通流量数据加载失败');
        });
}

// 渲染交通流量图表
function renderTrafficFlowChart(data) {
    const option = {
        title: { text: '交通流量趋势' },
        tooltip: { trigger: 'axis' },
        legend: { data: ['高速公路', '城镇公路'] },
        xAxis: { data: data.xAxis },
        yAxis: {},
        series: [
            { name: '高速公路', type: 'bar', data: data.highwayData },
            { name: '城镇公路', type: 'bar', data: data.urbanData },
            { name: '环比', type: 'line', data: data.lines[0].data },
            { name: '同比', type: 'line', data: data.lines[1].data }
        ]
    };

    chartContainers.main1.setOption(option);
}

// 错误处理函数
function showError(message) {
    alert(message);
    console.error(message);
}