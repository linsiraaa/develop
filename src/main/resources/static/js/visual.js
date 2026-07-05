//交通流量

var data = {
    id: 'multipleBarsLines',
    legendBar: ['高速公路', '城镇公路'],
    symbol: ' ', //数值是否带百分号        --默认为空 ''
    legendLine: ['环比', '同比'],
    xAxis: ['2014', '2015', '2016', '2017', '2018',
        '2019'
    ],
    yAxis: [
        [8, 10, 10, 11, 4, 13],
        [10, 7, 8, 8, 7, 9]
    ],
    lines: [
        [10, 10, 9, 11, 7, 4],
        [6, 12, 12, 2, 4, 4]
    ],
    barColor: ['#009883', '#e66922'], //柱子颜色 必填参数
    lineColor: ['#fd6665', '#fba73b'], // 折线颜色

}

var myData = (function test() {
    let yAxis = data.yAxis || []
    let lines = data.lines || []
    let legendBar = data.legendBar || []
    let legendLine = data.legendLine || []
    var symbol = data.symbol || ' '
    let seriesArr = []
    let legendArr = []
    yAxis && yAxis.forEach((item, index) => {
        legendArr.push({
            name: legendBar && legendBar.length > 0 && legendBar[index]
        })
        seriesArr.push({
            name: legendBar && legendBar.length > 0 && legendBar[index],
            type: 'bar',
            barGap: '0.5px',
            data: item,
            barWidth: data.barWidth || 12,
            label: {
                normal: {
                    show: false,
                    formatter: '{c}' + symbol,
                    position: 'top',
                    textStyle: {
                        color: '#000',
                        fontStyle: 'normal',
                        fontFamily: '微软雅黑',
                        textAlign: 'left',
                        fontSize: 11,
                    },
                },
            },
            itemStyle: { //图形样式
                normal: {
                    barBorderRadius:0,
                    borderWidth:1,
                    borderColor:'#ddd',
                    color: data.barColor[index]
                },
            }
        })
    })

    lines && lines.forEach((item, index) => {
        legendArr.push({
            name: legendLine && legendLine.length > 0 && legendLine[index]
        })
        seriesArr.push({
            name: legendLine && legendLine.length > 0 && legendLine[index],
            type: 'line',
            data: item,
            itemStyle: {
                normal: {
                    color: data.lineColor[index],
                    lineStyle: {
                        width: 2,//折线宽度
                        type: 'solid',
                    }
                }
            },
            label: {
                normal: {
                    show: false, //折线上方label控制显示隐藏
                    position: 'top',
                }
            },
            symbol: 'circle',
            symbolSize: 5
        })
    })

    return {
        seriesArr,
        legendArr
    }
})()
//交通流量

option1 = {
    title: {
        show: true,
        text: data.title,
        subtext: data.subTitle,
        link: '1111'
    },
    tooltip: {
        trigger: 'axis',
        formatter: function(params) {
            var time = '';
            var str = '';
            for (var i of params) {
                time = i.name.replace(/\n/g, '') + '<br/>';
                if (i.data == 'null' || i.data == null) {
                    str += i.seriesName + '：无数据' + '<br/>'
                } else {
                    str += i.seriesName + '：' + i.data + symbol + '%<br/>'
                }

            }
            return time + str;
        },
        axisPointer: {
            type: 'none'
        },
    },
    legend: {
        right: data.legendRight || '30%',
        top: 0,
        right:10,
        itemGap: 16,
        itemWidth: 10,
        itemHeight: 10,
        data: myData.legendArr,
        textStyle: {
            color: '#fff',
            fontStyle: 'normal',
            fontFamily: '微软雅黑',
            fontSize: 12,
        }
    },
    grid: {
        x: 0,
        y: 30,
        x2: 0,
        y2: 25,
    },
    xAxis: {
        type: 'category',
        data: data.xAxis,
        axisTick: {
            show: false,
        },

        axisLine: {
            show: false,
        },
        axisLabel: {       //轴标
            show: true,
            interval: '0',
            textStyle: {
                lineHeight:5,
                padding: [2, 2, 0, 2],
                height: 50,
                fontSize: 12,
                color:'#fff',
            },
            rich: {
                Sunny: {
                    height: 50,
                    // width: 60,
                    padding: [0, 5, 0, 5],
                    align: 'center',
                },
            },
            formatter: function(params, index) {
                var newParamsName = "";
                var splitNumber = 5;
                var paramsNameNumber = params && params.length;
                if (paramsNameNumber && paramsNameNumber <= 4) {
                    splitNumber = 4;
                } else if (paramsNameNumber >= 5 && paramsNameNumber <= 7) {
                    splitNumber = 4;
                } else if (paramsNameNumber >= 8 && paramsNameNumber <= 9) {
                    splitNumber = 5;
                } else if (paramsNameNumber >= 10 && paramsNameNumber <= 14) {
                    splitNumber = 5;
                } else {
                    params = params && params.slice(0, 15);
                }

                var provideNumber = splitNumber; //一行显示几个字
                var rowNumber = Math.ceil(paramsNameNumber / provideNumber) || 0;
                if (paramsNameNumber > provideNumber) {
                    for (var p = 0; p < rowNumber; p++) {
                        var tempStr = "";
                        var start = p * provideNumber;
                        var end = start + provideNumber;
                        if (p == rowNumber - 1) {
                            tempStr = params.substring(start, paramsNameNumber);
                        } else {
                            tempStr = params.substring(start, end) + "\n";
                        }
                        newParamsName += tempStr;
                    }

                } else {
                    newParamsName = params;
                }
                params = newParamsName
                return '{Sunny|' + params + '}';
            },
            color: '#687284',
        },

    },
    yAxis: {
        axisLine: {
            show: false
        },
        axisTick: {
            show: false
        },
        axisLabel: {
            show: false
        },
        splitLine: {
            show: true,
            lineStyle: {
                color: '#F1F3F5',
                type: 'solid'
            },
            interval: 2
        },
        splitNumber: 4,
    },
    series: myData.seriesArr
}
//////////////////////交通流量 end

//交通工具流量


//////////////////////交通工具流量 end

//本月发生事件1
var color = ['#e9df3d', '#f79c19', '#21fcd6', '#08c8ff', '#df4131'];
var data = [{
        "name": "超速",
        "value": 30
    },
    {
        "name": "闯红灯",
        "value": 30
    },
    {
        "name": "闯禁行",
        "value": 42
    },
    {
        "name": "违停",
        "value": 50
    },
    {
        "name": "逆行",
        "value": 34
    }
];

var max = data[0].value;
data.forEach(function(d) {
    max = d.value > max ? d.value : max;
});

var renderData = [{
    value: [],
    name: "告警类型TOP5",
    symbol: 'none',
    lineStyle: {
        normal: {
            color: '#ecc03e',
            width: 2
        }
    },
    areaStyle: {
        normal: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0,
                [{
                    offset: 0,
                    color: 'rgba(203, 158, 24, 0.8)'
                }, {
                    offset: 1,
                    color: 'rgba(190, 96, 20, 0.8)'
                }],
                false)
        }
    }
}];


data.forEach(function(d, i) {
    var value = ['', '', '', '', ''];
    value[i] = max,
    renderData[0].value[i] = d.value;
    renderData.push({
        value: value,
        symbol: 'circle',
        symbolSize: 12,
        lineStyle: {
            normal: {
                color: 'transparent'
            }
        },
        itemStyle: {
            normal: {
                color: color[i],
            }
        }
    })
})
var indicator = [];

data.forEach(function(d) {
    indicator.push({
        name: d.name,
        max: max,
        color: '#fff'
    })
})




//收费站收费排行1
var myChart4, myChart41;
var highwayData = { roads: [], data: [] };

// 初始化高速公路图表
function initHighwayChart() {
    myChart4 = echarts.init(document.getElementById('main4'));
    myChart41 = echarts.init(document.getElementById('main41'));

    const base64Symbol = "image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAABaCAYAAAA4qEECAAADYElEQVR4nO2dz0sUYRjHP7tIdAmxQ1LdlhCKMohAIsgiyEuHjkUEFQTlpejS/xCCBB06RBGBBKIG4cGyH0qHBKE9eKyFqBQPRQeNCt06vGNY7bq7szPfeZLnAwuzM+/zgw/DDvMu70wOIVveLscJOwycA44A24CfwAfgKXAbeFVvovlC/o/vuVwuTj+x0FWiYdGbgXvA8RrjHgAXgIVaCbMU3SKr1BhtwEtgZx1jTwI7gG7ga5pNNUO+9pBMuEN9klfYD9xMqZdEsCj6AHAiRtxZYFeyrSSHRdGnYsblCD8jJrEoek8TsbsT6yJhLIrelFFsqlgUPZtRbKpYFP2kidjxxLpIGIuiB4AvMeLmgJGEe0kMi6I/AVdjxPVSx91hVlgUDXAXuEaY16jFMnAJeJhqR01iVTTAdeAYUFxjzBRwCLgl6agJrM51rAO7AP2EmbxthPO8vfAc2Ams84axLpoCGKLrH1mm8eC6KPAGaAL2Fpj7AZgY7T9DfhRY/wc4eflPmH+OjOynI8uEGbpukXlJ4Dz84V8aWWHcj46q4thFzCNTjJRren2UrlLWPM3WYjuAMYIk/tq2oCx9lK5Q11YLboFGARaxXVX0woMtpfK0uuTWvRFoFNcsxKdhF5kqEX3iuuthbQXtehG/gdMG2kvlm/B1xUuWoSLFmFF9CRwg2TnM4pRzskEc8bGiugR4ArhNjkpJqKcJv51sSJ63eOiRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEWvTHKvs/p1izWu5qvaSCWvTlCvtmgeEUaw5TeUVtpV5SQy16COgBRoHXhMWb3aS7PnAhqjEQ1RwFeuYL+aEUa/5DFmtYHkefOEwQVmcBvKD+FQNvgNN/P+pHiV8MRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEixbhokVYEx3nudGKXE1jTfS6xUWLcNEiXLQIFy3CRYtw0SJctAgXLcJFi3DRIv430eUq2+axJvp7jePPqmzHySXFmuhHwFKVYzNA/6rv/VR/s9BSlMsM1kTPEN4DPkU4I8vAO6APOAgsrhq7GO3ri8aUo5ipKIep1zv9AtipgOACGIrLAAAAAElFTkSuQmCC";

    const optionBase = {
        title: {
            text: " ",
            left: "center",
            y: "10",
            textStyle: { color: "#fff" }
        },
        grid: {
            left: 80,  // 给长名称更多显示空间
            top: 0,
            bottom: 10
        },
        tooltip: {
            trigger: "item",
            textStyle: { fontSize: 12 },
            formatter: function(params) {
                return "第" + (params.dataIndex + 1) + "名: " + params.name + "<br/>收费: " + params.value + " 元";
            }
        },
        xAxis: {
            max: function(value) {
                return value.max * 1.3;  // 动态设置最大值，留出30%空间显示数字
            },
            splitLine: { show: false },
            axisLine: { show: false },
            axisLabel: { show: false },
            axisTick: { show: false }
        },
        yAxis: [{
            type: "category",
            inverse: true,
            data: [],
            axisLine: { show: false },
            axisTick: { show: false },
            axisLabel: {
                margin: 10,
                inside: false,
                align: 'right',
                verticalAlign: 'middle',
                textStyle: {
                    color: "#fff",
                    fontSize: 14,
                    fontWeight: 'normal',
                }
            }
        }],
        series: [{
            type: "bar",
            barWidth: 18,
            barMaxWidth: '60%',  // 限制柱状图最大宽度
            itemStyle: {
                color: function(params) {
                    var colors = ['#ff6b6b', '#ffa502', '#ffd93d', '#3498db', '#3498db'];
                    return colors[params.dataIndex] || '#3498db';
                },
                borderRadius: [0, 10, 10, 0]
            },
            label: {
                show: true,
                position: "right",
                color: "#fff",
                fontSize: 12,
                formatter: '{c} 元',
                offset: [5, 0]  // 向右偏移5像素
            },
            data: []
        }]
    };

    myChart4.setOption({ ...optionBase });
    myChart41.setOption({ ...optionBase });

    $.ajax({
        url: "/api/highway/top5/toll",
        method: "GET",
        beforeSend: function () {
            myChart4.showLoading();
            myChart41.showLoading();
        },
        success: function (data) {
            highwayData.roads = [];
            highwayData.data = [];

            // 排序（从大到小）
            const sorted = data.sort((a, b) => b.tollAmount - a.tollAmount);

            // 添加排名序号到名称
            sorted.forEach((item, index) => {
                var rank = index + 1;
                highwayData.roads.push(rank + '. ' + item.stationName);
                highwayData.data.push((item.tollAmount).toFixed(0));
            });

            const option4 = JSON.parse(JSON.stringify(optionBase));
            const option41 = JSON.parse(JSON.stringify(optionBase));

            // 图一：显示带排名的收费站名称和柱状图
            option4.yAxis[0].data = highwayData.roads;
            option4.series[0].data = highwayData.data;

            // 图二：同样显示完整内容
            option41.yAxis[0].data = highwayData.roads;
            option41.series[0].data = highwayData.data;

            myChart4.setOption(option4);
            myChart41.setOption(option41);
        },
        error: function (err) {
            console.error("加载高速公路数据失败:", err);
        },
        complete: function () {
            myChart4.hideLoading();
            myChart41.hideLoading();
        }
    });

    window.addEventListener('resize', function () {
        myChart4 && myChart4.resize();
        myChart41 && myChart41.resize();
    });
}

// 刷新收费站排行数据
function refreshTollRankData() {
    $.ajax({
        url: "/api/highway/top5/toll",
        method: "GET",
        success: function (data) {
            highwayData.roads = [];
            highwayData.data = [];

            const sorted = data.sort((a, b) => b.tollAmount - a.tollAmount);

            sorted.forEach((item, index) => {
                var rank = index + 1;
                highwayData.roads.push(rank + '. ' + item.stationName);
                highwayData.data.push((item.tollAmount).toFixed(0));
            });

            myChart4.setOption({
                yAxis: [{ data: highwayData.roads }],
                series: [{ data: highwayData.data }]
            });
            myChart41.setOption({
                yAxis: [{ data: highwayData.roads }],
                series: [{ data: highwayData.data }]
            });

            console.log('收费站排行已更新:', new Date().toLocaleTimeString());
        }
    });
}

// 每10秒自动刷新收费站排行
setInterval(refreshTollRankData, 10000);

// 初始化水平滚动 Swiper（限制只能在两页之间滑动，禁用自动播放）
function initSwiper2() {
    const swiper = new Swiper('.visual_swiper2', {
        autoplay: false,
        direction: 'horizontal',
        loop: false,
        speed: 500,
        slidesPerView: 1,
        spaceBetween: 30,
        initialSlide: 0, // 默认显示第一个 slide（文字那页）
        pagination: {
            el: '.swiper-pagination',
            clickable: true,
            type: 'bullets'
        },
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev'
        },
        on: {
            slideChange: function () {
                if (this.activeIndex > 1) this.slideTo(1, 0, false);
                if (this.activeIndex < 0) this.slideTo(0, 0, false);
            }
        }
    });
}
//////////////////////收费站收费排行2 end

//今日实时收费
// 模拟各城市数据




//////////////////////今日实时收费
let cityData = {
    '全省数据': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 },
    'city1': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 },
    'city2': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 },
    'city3': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 },
    'city4': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 },
    'city5': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 },
    'city6': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 },
    'city7': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 },
    'city8': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 },
    'city9': { vehicleTotal: 0, todayOnline: 0, todayAlarm: 0 }
};

let currentCity = '全省';

// 加载今日车辆统计数据
function loadVehicleStatsData() {
    $.ajax({
        url: '/api/charge/stats',
        type: 'GET',
        dataType: 'json',
        success: function(res) {
            const newCityData = {};

            // 1. 组装 cityData - 全省数据
            if (res.provinceTotal && res.provinceTotal.cityName) {
                newCityData[res.provinceTotal.cityName] = {
                    vehicleTotal: res.provinceTotal.vehicleTotal,
                    todayOnline: res.provinceTotal.todayOnline,
                    todayAlarm: res.provinceTotal.todayAlarm
                };
            }

            // 2. 组装各城市数据
            if (Array.isArray(res.topCities)) {
                res.topCities.forEach(city => {
                    newCityData[city.cityName] = {
                        vehicleTotal: city.vehicleTotal,
                        todayOnline: city.todayOnline,
                        todayAlarm: city.todayAlarm
                    };
                });
            }

            // 3. 替换全局 cityData
            cityData = newCityData;
            
            // 4. 计算全省最大值（用于饼图占比）
            if (res.provinceTotal) {
                maxVehicleTotal = res.provinceTotal.vehicleTotal || 1;
                maxTodayOnline = res.provinceTotal.todayOnline || 1;
                maxTodayAlarm = res.provinceTotal.todayAlarm || 1;
            }
            
            console.log('加载的城市数据:', cityData);

            // 4. 更新页面中的 a 标签内容
            const $aTags = $('.visualSssf_left a');
            const cityList = [];

            if (res.provinceTotal && res.provinceTotal.cityName) {
                cityList.push(res.provinceTotal.cityName);
            }

            if (Array.isArray(res.topCities)) {
                res.topCities.forEach(city => {
                    cityList.push(city.cityName);
                });
            }

            $aTags.each(function(index) {
                const cityName = cityList[index];
                if (cityName) {
                    $(this).attr('data-city', cityName).text(cityName);
                    // 如果是第一个（全省），设置为当前选中
                    if (index === 0) {
                        currentCity = cityName;
                    }
                } else {
                    $(this).hide();
                }
            });

            // 5. 自动更新当前选中城市的图表
            if (typeof refreshVehicleCharts === 'function') {
                refreshVehicleCharts();
            }

            console.log('今日车辆统计已更新:', new Date().toLocaleTimeString());
        },
        error: function() {
            console.error('数据加载失败');
        }
    });
}

// 初始加载
loadVehicleStatsData();

// 每10秒自动刷新今日车辆统计
setInterval(loadVehicleStatsData, 10000);

// 全省最大值，用于计算占比
var maxVehicleTotal = 1;
var maxTodayOnline = 1;
var maxTodayAlarm = 1;

// 初始化图表实例（确保在DOM加载完成后执行）
$(document).ready(function() {
    // 初始化三个饼图实例
    const myChart5 = echarts.init(document.getElementById('main5'));
    const myChart6 = echarts.init(document.getElementById('main6'));
    const myChart7 = echarts.init(document.getElementById('main7'));

    // 创建环形图配置，percent是0-100的百分比
    function createRingOption(titleText, percent, color1, color2) {
        var p = Math.min(100, Math.max(0, percent));
        return {
            title: {
                text: titleText,
                left: 'center',
                top: 'center',
                textStyle: {
                    color: '#fff',
                    fontSize: 14
                }
            },
            series: [{
                type: 'pie',
                radius: ['60%', '70%'],
                silent: true,
                label: { show: false },
                data: [
                    {
                        value: p,
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                                { offset: 0, color: color1 },
                                { offset: 1, color: color2 }
                            ])
                        }
                    },
                    {
                        value: 100 - p,
                        itemStyle: { color: 'rgba(255,255,255,0.1)' }
                    }
                ]
            }]
        };
    }

    // 设置初始图表
    myChart5.setOption(createRingOption('车辆总数', 50, '#1d54f7', '#68eaf9'));
    myChart6.setOption(createRingOption('今日上线', 50, '#02df94', '#28d3d0'));
    myChart7.setOption(createRingOption('今日报警', 50, '#eb3600', '#d0a00e'));

    // 添加城市点击事件
    $('.visualSssf_left a').on('click', function() {
        $('.visualSssf_left a').removeClass('active');
        $(this).addClass('active');
        currentCity = $(this).attr('data-city') || $(this).text();
        console.log('点击城市:', currentCity);
        updateCharts();
    });

    // 更新所有图表
    function updateCharts() {
        var data = cityData[currentCity];
        console.log('更新图表, 城市:', currentCity, '数据:', data);
        
        if (!data) {
            console.log('未找到数据，可用城市:', Object.keys(cityData));
            return;
        }

        // 基于全省最大值计算百分比
        var totalPercent = (data.vehicleTotal / maxVehicleTotal) * 100;
        var onlinePercent = (data.todayOnline / maxTodayOnline) * 100;
        var alarmPercent = (data.todayAlarm / maxTodayAlarm) * 100;

        myChart5.setOption(createRingOption(
            '车辆总数：' + data.vehicleTotal + '万辆',
            totalPercent,
            '#1d54f7', '#68eaf9'
        ));

        myChart6.setOption(createRingOption(
            '今日上线：' + data.todayOnline + '万辆',
            onlinePercent,
            '#02df94', '#28d3d0'
        ));

        myChart7.setOption(createRingOption(
            '今日报警：' + data.todayAlarm + '万辆',
            alarmPercent,
            '#eb3600', '#d0a00e'
        ));
    }

    // 全局刷新函数
    window.refreshVehicleCharts = function() {
        updateCharts();
    };

    // 窗口大小改变时重绘图表
    $(window).on('resize', function() {
        myChart5.resize();
        myChart6.resize();
        myChart7.resize();
    });
});

//////////////////////今日实时收费 end

//顶部监控
function renderConTopBoxes(dataList) {
    const container = document.getElementById("conTopContainer");
    if (!container) {
        // 容器不存在，跳过渲染
        return;
    }
    container.innerHTML = ''; // 清空旧内容

    dataList.forEach(item => {
        const boxClass = item.boxClass || 'visual_conTop_box';

        const detailHtml = item.details.map(detail => {
            const icon = detail.iconClass ? `<i class="fa ${detail.iconClass}"></i>` : '';
            return `<p class="${detail.aClass || ''}">${detail.label}<span>${detail.value || ''}</span>${icon}</p>`;
        }).join('');

        const html = `
            <div class="${boxClass}">
                <img src="images/top-border1.png" />
                <div class="visual_conTop_box_title">${item.title}</div>
                <div class="visual_conTop_box_num">${item.value}</div>
                <div class="visual_conTop_box_bottom">${detailHtml}</div>
            </div>
        `;

        container.insertAdjacentHTML('beforeend', html);
    });
}

function loadConTopData() {
    $.ajax({
        url: '/api/dashboard/top-data',
        type: 'GET',
        success: function(response) {
            renderConTopBoxes(response);
        },
        error: function(xhr) {
            console.error('获取顶部数据失败：', xhr);
        }
    });
}

// 页面加载后执行一次
window.addEventListener('DOMContentLoaded', loadConTopData);

// 每10秒自动刷新顶部监控数据
setInterval(loadConTopData, 10000);

// ==================== 客货运量实时刷新 ====================
function loadVehicleFreightData() {
    $.ajax({
        url: '/api/vehicle/summary',
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            // 更新大型车
            if (response.large) {
                $('.sfzcll_box[data-type="大型车"] .passenger-volume').text(response.large.passengerVolume || 0);
                $('.sfzcll_box[data-type="大型车"] .freight-volume').text(response.large.freightVolume || 0);
            }
            // 更新中型车
            if (response.medium) {
                $('.sfzcll_box[data-type="中型车"] .passenger-volume').text(response.medium.passengerVolume || 0);
                $('.sfzcll_box[data-type="中型车"] .freight-volume').text(response.medium.freightVolume || 0);
            }
            // 更新小型车
            if (response.small) {
                $('.sfzcll_box[data-type="小型车"] .passenger-volume').text(response.small.passengerVolume || 0);
                $('.sfzcll_box[data-type="小型车"] .freight-volume').text(response.small.freightVolume || 0);
            }
            console.log('客货运量已更新:', new Date().toLocaleTimeString());
        },
        error: function(xhr, status, error) {
            console.error('获取客货运量失败:', error);
        }
    });
}

// 页面加载后执行
window.addEventListener('DOMContentLoaded', loadVehicleFreightData);

// 每10秒刷新客货运量
setInterval(loadVehicleFreightData, 10000);


















