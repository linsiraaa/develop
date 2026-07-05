var myChart8;
var option8;
var allData = { citys: [], moveLines: [] };

// 额外的蓝色装饰城市点（全国主要城市）
var decorativeCities = [
    {name: '哈尔滨', value: [126.642464, 45.756967, 15], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '长春', value: [125.324501, 43.886841, 12], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '呼和浩特', value: [111.749181, 40.842585, 10], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '乌鲁木齐', value: [87.617733, 43.792818, 8], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '兰州', value: [103.823557, 36.058039, 12], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '银川', value: [106.278179, 38.46637, 8], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '西宁', value: [101.778916, 36.623178, 7], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '拉萨', value: [91.132212, 29.660361, 5], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '昆明', value: [102.712251, 25.040609, 15], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '贵阳', value: [106.713478, 26.578343, 14], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '南宁', value: [108.320004, 22.82402, 13], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '海口', value: [110.33119, 20.031971, 10], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '福州', value: [119.306239, 26.075302, 16], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '南昌', value: [115.892151, 28.676493, 15], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '重庆', value: [106.504962, 29.533155, 20], symbolSize: 6, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '天津', value: [117.190182, 39.125596, 22], symbolSize: 6, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '北京', value: [116.405285, 39.904989, 25], symbolSize: 7, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '上海', value: [121.472644, 31.231706, 25], symbolSize: 7, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '厦门', value: [118.11022, 24.490474, 12], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '珠海', value: [113.552724, 22.255899, 10], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '东莞', value: [113.746262, 23.046237, 12], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '佛山', value: [113.122717, 23.028762, 11], symbolSize: 5, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '温州', value: [120.672111, 28.000575, 10], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '常州', value: [119.946973, 31.772752, 10], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '徐州', value: [117.184811, 34.261792, 9], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '临沂', value: [118.326443, 35.065282, 8], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '包头', value: [109.840405, 40.658168, 7], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '鄂尔多斯', value: [109.99029, 39.817179, 6], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '吉林', value: [126.55302, 43.843577, 8], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '齐齐哈尔', value: [123.953486, 47.348079, 7], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '绑阳', value: [104.741722, 31.46402, 8], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '宜宾', value: [104.630825, 28.760189, 7], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '遵义', value: [106.937265, 27.706626, 7], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '桂林', value: [110.299121, 25.274215, 9], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '柳州', value: [109.411703, 24.314617, 8], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '三亚', value: [109.508268, 18.247872, 6], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '大理', value: [100.225668, 25.589449, 5], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '丽江', value: [100.233026, 26.872108, 5], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '延安', value: [109.49081, 36.596537, 6], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '榆林', value: [109.741193, 38.290162, 5], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '酒泉', value: [98.510795, 39.744023, 4], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '敦煌', value: [94.661967, 40.142128, 3], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '喀什', value: [75.989138, 39.467664, 4], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '伊宁', value: [81.317946, 43.912889, 4], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '克拉玛依', value: [84.873946, 45.595886, 5], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '牡丹江', value: [129.618602, 44.582962, 6], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '佳木斯', value: [130.361634, 46.809606, 5], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '赤峰', value: [118.956806, 42.275317, 6], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '通辽', value: [122.263119, 43.617429, 5], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '锦州', value: [121.135742, 41.119269, 7], symbolSize: 4, itemStyle: {normal: {color: '#00bfff'}}},
    {name: '丹东', value: [124.383044, 40.124296, 6], symbolSize: 3, itemStyle: {normal: {color: '#00bfff'}}}
];

// 初始化地图函数
function initChinaMap() {
    myChart8 = echarts.init(document.getElementById('main8'));

    // 基础配置（数据留空，通过loadMapDataForMain8填充）
    option8 = {
        title: {
            text: '',
            left: 'center',
            textStyle: { color: '#fff' }
        },
        legend: {
            show: false,
            orient: 'vertical',
            top: 'bottom',
            left: 'right',
            data: ['地点', '线路'],
            textStyle: { color: '#fff' }
        },
        geo: {
            map: 'china',
            label: { emphasis: { show: false } },
            roam: true,
            itemStyle: {
                normal: {
                    areaColor: 'rgba(7,21,57,0.5)',
                    borderColor: '#0177ff'
                },
                emphasis: {
                    areaColor: '#071537'
                }
            }
        },
        series: [
            {
                name: '地点',
                type: 'effectScatter',
                coordinateSystem: 'geo',
                zlevel: 2,
                rippleEffect: { brushType: 'stroke' },
                label: {
                    emphasis: {
                        show: true,
                        position: 'right',
                        formatter: '{b}'
                    }
                },
                symbolSize: function(val) {
                    return val[2] > 50 ? 10 : val[2] > 20 ? 6 : 3;
                },
                showEffectOn: 'render',
                itemStyle: {
                    normal: {
                        color: '#46bee9' // 会被数据覆盖
                    }
                },
                data: [] // 初始为空
            },
            {
                name: '线路',
                type: 'lines',
                coordinateSystem: 'geo',
                zlevel: 2,
                large: true,
                effect: {
                    show: true,
                    constantSpeed: 30,
                    symbol: 'pin',
                    symbolSize: 3,
                    trailLength: 0
                },
                lineStyle: {
                    normal: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#58B3CC' },
                            { offset: 1, color: '#F58158' }
                        ], false),
                        width: 1,
                        opacity: 0.2,
                        curveness: 0.1
                    }
                },
                data: [] // 初始为空
            }
        ]
    };

    // 首次渲染
    myChart8.setOption(option8);

    // 窗口resize事件
    window.addEventListener('resize', function() {
        myChart8.resize();
    });
}

// 数据加载函数
function loadMapDataForMain8() {
    $.ajax({
        url: "/api/ads-out-of-henan/all",
        method: "GET",
        beforeSend: function() {
            myChart8.showLoading();
        },
        success: function(data) {
            // 清空旧数据
            allData.citys = [];
            allData.moveLines = [];

            const citySet = new Set();
            const henanCities = ['郑州', '洛阳', '开封', '安阳', '许昌', '南阳', '新乡',
                '焦作', '平顶山', '商丘', '濮阳', '周口', '漯河', '鹤壁',
                '三门峡', '驻马店', '信阳', '济源'];

            data.forEach(route => {
                const {
                    fromCity, fromLongitude, fromLatitude, fromWeight, fromSymbolSize,
                    toCity, toLongitude, toLatitude, toWeight, toSymbolSize
                } = route;

                // 处理起点城市
                if (!citySet.has(fromCity)) {
                    citySet.add(fromCity);
                    allData.citys.push({
                        name: fromCity,
                        value: [fromLongitude, fromLatitude, fromWeight],
                        symbolSize: fromSymbolSize,
                        itemStyle: {
                            normal: {
                                color: henanCities.includes(fromCity) ? "#F58158" : "#58B3CC"
                            }
                        }
                    });
                }

                // 处理终点城市
                if (!citySet.has(toCity)) {
                    citySet.add(toCity);
                    allData.citys.push({
                        name: toCity,
                        value: [toLongitude, toLatitude, toWeight],
                        symbolSize: toSymbolSize,
                        itemStyle: {
                            normal: {
                                color: henanCities.includes(toCity) ? "#F58158" : "#58B3CC"
                            }
                        }
                    });
                }

                // 添加连线
                allData.moveLines.push({
                    fromName: fromCity,
                    toName: toCity,
                    coords: [
                        [fromLongitude, fromLatitude],
                        [toLongitude, toLatitude]
                    ]
                });
            });

            // 生成装饰城市到河南城市的连线
            var decorativeLines = [];
            var henanCityCoords = {};
            
            // 收集河南城市坐标
            allData.citys.forEach(function(city) {
                if (henanCities.includes(city.name)) {
                    henanCityCoords[city.name] = [city.value[0], city.value[1]];
                }
            });
            
            // 为每个装饰城市随机连接1-2个河南城市
            var henanCityNames = Object.keys(henanCityCoords);
            if (henanCityNames.length > 0) {
                decorativeCities.forEach(function(city) {
                    // 随机选择1-2个河南城市连接
                    var numConnections = Math.floor(Math.random() * 2) + 1;
                    var shuffled = henanCityNames.slice().sort(function() { return 0.5 - Math.random(); });
                    
                    for (var i = 0; i < Math.min(numConnections, shuffled.length); i++) {
                        var targetCity = shuffled[i];
                        decorativeLines.push({
                            fromName: city.name,
                            toName: targetCity,
                            coords: [
                                [city.value[0], city.value[1]],
                                henanCityCoords[targetCity]
                            ]
                        });
                    }
                });
            }

            // 更新图表数据（合并装饰城市点和连线）
            var finalCities = decorativeCities.concat(allData.citys);
            var finalLines = decorativeLines.concat(allData.moveLines);
            option8.series[0].data = finalCities;
            option8.series[1].data = finalLines;
            myChart8.setOption(option8);
            
            console.log('出省车辆已更新:', new Date().toLocaleTimeString());
        },
        error: function(err) {
            console.error("出省车辆数据加载失败:", err);
        },
        complete: function() {
            myChart8.hideLoading();
        }
    });
}

// 每30秒刷新出省车辆数据
setInterval(loadMapDataForMain8, 30000);



//访豫车辆
function loadMapDataForMain9() {
    $.ajax({
        url: '/api/realtime/in-to-henan',
        type: 'GET',
        dataType: 'json',
        success: function(apiData) {
            // 动态生成geoCoordMap
            var geoCoordMap = {};
            // 动态生成geoCoordData
            var geoCoordData = [];

            // 处理API返回的数据（从Redis获取的实时数据）
            apiData.forEach(function(item) {
                geoCoordMap[item.from_city_name] = [item.from_longitude, item.from_latitude];
                geoCoordData.push({
                    name: item.from_city_name,
                    value: item.vehicle_count
                });
            });

            // 按车辆数量降序排序，获取前5名
            var sortedData = geoCoordData.slice().sort(function(a, b) {
                return b.value - a.value;
            });
            var top5Cities = sortedData.slice(0, 5).map(function(item) {
                return item.name;
            });

            // 数据转换函数
            var convertData = function(data) {
                var res = [];
                for (var i = 0; i < data.length; i++) {
                    var geoCoord = geoCoordMap[data[i].name];
                    if (geoCoord) {
                        res.push({
                            name: data[i].name,
                            value: geoCoord.concat(data[i].value)
                        });
                    }
                }
                return res;
            };

            // 获取Top5城市的数据（用于涟漪效果）
            var top5Data = geoCoordData.filter(function(item) {
                return top5Cities.indexOf(item.name) !== -1;
            });

            // 获取非Top5城市的数据
            var otherData = geoCoordData.filter(function(item) {
                return top5Cities.indexOf(item.name) === -1;
            });

            // 计算最大值用于visualMap
            var maxValue = sortedData.length > 0 ? sortedData[0].value : 5000;

            // 完整的option配置
            var option9 = {
                title: {
                    text: ' ',
                    sublink: ' ',
                    x:'center',
                    textStyle: {
                        color: '#fff'
                    }
                },
                tooltip: {
                    trigger: 'item',
                    backgroundColor: 'rgba(0,0,0,0.7)',
                    borderColor: '#00d4ff',
                    borderWidth: 1,
                    textStyle: {
                        color: '#fff'
                    },
                    formatter: function (params) {
                        var rank = top5Cities.indexOf(params.name);
                        var rankText = rank !== -1 ? ' 🔥TOP' + (rank + 1) : '';
                        return params.name + rankText + '<br/>访豫车辆: ' + params.value[2] + ' 辆';
                    }
                },
                visualMap: {
                    min: 0,
                    max: maxValue,
                    calculable: true,
                    inRange: {
                        color: ['#50a3ba', '#eac736', '#d94e5d']
                    },
                    textStyle: {
                        color: '#fff'
                    },
                    left: 10,
                    bottom: 20
                },
                geo: {
                    map: 'china',
                    roam: false,
                    label: {
                        emphasis: {
                            show: false
                        }
                    },
                    itemStyle: {
                        normal: {
                            areaColor: 'rgba(20,50,80,0.6)',
                            borderColor: '#0af',
                            borderWidth: 1
                        },
                        emphasis: {
                            areaColor: 'rgba(0,100,150,0.5)'
                        }
                    }
                },
                series: [
                    // 普通城市散点
                    {
                        name: '访豫车辆',
                        type: 'scatter',
                        coordinateSystem: 'geo',
                        data: convertData(otherData),
                        symbolSize: function(val) {
                            return Math.max(8, val[2] / 400);
                        },
                        label: {
                            show: false
                        },
                        itemStyle: {
                            color: '#ffdb5c',
                            shadowBlur: 10,
                            shadowColor: '#ffdb5c'
                        }
                    },
                    // Top5城市涟漪效果
                    {
                        name: 'Top5城市',
                        type: 'effectScatter',
                        coordinateSystem: 'geo',
                        data: convertData(top5Data),
                        symbolSize: function(val) {
                            return Math.max(15, val[2] / 300);
                        },
                        showEffectOn: 'render',
                        rippleEffect: {
                            brushType: 'stroke',
                            scale: 4,
                            period: 3
                        },
                        label: {
                            show: true,
                            position: 'right',
                            formatter: '{b}',
                            color: '#fff',
                            fontSize: 10
                        },
                        itemStyle: {
                            color: '#ff4500',
                            shadowBlur: 20,
                            shadowColor: '#ff4500'
                        },
                        zlevel: 1
                    }
                ]
            };

            // 初始化图表（保存到全局变量以便刷新）
            if (!window.myChart9) {
                window.myChart9 = echarts.init(document.getElementById('main9'));
                window.addEventListener('resize', function() {
                    if (window.myChart9) window.myChart9.resize();
                });
            }
            window.myChart9.setOption(option9, true);
            
            console.log('访豫车辆已更新:', new Date().toLocaleTimeString(), 'Top5:', top5Cities.join(', '));
        },
        error: function(xhr, status, error) {
            console.error('访豫车辆数据加载失败:', status, error);
        }
    });
}

// 每30秒刷新访豫车辆数据
setInterval(loadMapDataForMain9, 30000);

//////////////////////访豫车辆 end