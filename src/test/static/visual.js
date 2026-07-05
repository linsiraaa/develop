// //交通流量
//
// var data = {
//     id: 'multipleBarsLines',
//     legendBar: ['高速公路', '城镇公路'],
//     symbol: ' ', //数值是否带百分号        --默认为空 ''
//     legendLine: ['环比', '同比'],
//     xAxis: ['2014', '2015', '2016', '2017', '2018',
//         '2019'
//     ],
//     yAxis: [
//         [8, 10, 10, 11, 4, 13],
//         [10, 7, 8, 8, 7, 9]
//     ],
//     lines: [
//         [10, 10, 9, 11, 7, 4],
//         [6, 12, 12, 2, 4, 4]
//     ],
//     barColor: ['#009883', '#e66922'], //柱子颜色 必填参数
//     lineColor: ['#fd6665', '#fba73b'], // 折线颜色
//
// }
//
// var myData = (function test() {
//     let yAxis = data.yAxis || []
//     let lines = data.lines || []
//     let legendBar = data.legendBar || []
//     let legendLine = data.legendLine || []
//     var symbol = data.symbol || ' '
//     let seriesArr = []
//     let legendArr = []
//     yAxis && yAxis.forEach((item, index) => {
//         legendArr.push({
//             name: legendBar && legendBar.length > 0 && legendBar[index]
//         })
//         seriesArr.push({
//             name: legendBar && legendBar.length > 0 && legendBar[index],
//             type: 'bar',
//             barGap: '0.5px',
//             data: item,
//             barWidth: data.barWidth || 12,
//             label: {
//                 normal: {
//                     show: false,
//                     formatter: '{c}' + symbol,
//                     position: 'top',
//                     textStyle: {
//                         color: '#000',
//                         fontStyle: 'normal',
//                         fontFamily: '微软雅黑',
//                         textAlign: 'left',
//                         fontSize: 11,
//                     },
//                 },
//             },
//             itemStyle: { //图形样式
//                 normal: {
//                     barBorderRadius:0,
//                     borderWidth:1,
//                     borderColor:'#ddd',
//                     color: data.barColor[index]
//                 },
//             }
//         })
//     })
//
//     lines && lines.forEach((item, index) => {
//         legendArr.push({
//             name: legendLine && legendLine.length > 0 && legendLine[index]
//         })
//         seriesArr.push({
//             name: legendLine && legendLine.length > 0 && legendLine[index],
//             type: 'line',
//             data: item,
//             itemStyle: {
//                 normal: {
//                     color: data.lineColor[index],
//                     lineStyle: {
//                         width: 2,//折线宽度
//                         type: 'solid',
//                     }
//                 }
//             },
//             label: {
//                 normal: {
//                     show: false, //折线上方label控制显示隐藏
//                     position: 'top',
//                 }
//             },
//             symbol: 'circle',
//             symbolSize: 5
//         })
//     })
//
//     return {
//         seriesArr,
//         legendArr
//     }
// })()
// //交通流量
//
// option1 = {
//     title: {
//         show: true,
//         text: data.title,
//         subtext: data.subTitle,
//         link: '1111'
//     },
//     tooltip: {
//         trigger: 'axis',
//         formatter: function(params) {
//             var time = '';
//             var str = '';
//             for (var i of params) {
//                 time = i.name.replace(/\n/g, '') + '<br/>';
//                 if (i.data == 'null' || i.data == null) {
//                     str += i.seriesName + '：无数据' + '<br/>'
//                 } else {
//                     str += i.seriesName + '：' + i.data + symbol + '%<br/>'
//                 }
//
//             }
//             return time + str;
//         },
//         axisPointer: {
//             type: 'none'
//         },
//     },
//     legend: {
//         right: data.legendRight || '30%',
//         top: 0,
//         right:10,
//         itemGap: 16,
//         itemWidth: 10,
//         itemHeight: 10,
//         data: myData.legendArr,
//         textStyle: {
//             color: '#fff',
//             fontStyle: 'normal',
//             fontFamily: '微软雅黑',
//             fontSize: 12,
//         }
//     },
//     grid: {
//         x: 0,
//         y: 30,
//         x2: 0,
//         y2: 25,
//     },
//     xAxis: {
//         type: 'category',
//         data: data.xAxis,
//         axisTick: {
//             show: false,
//         },
//
//         axisLine: {
//             show: false,
//         },
//         axisLabel: {       //轴标
//             show: true,
//             interval: '0',
//             textStyle: {
//                 lineHeight:5,
//                 padding: [2, 2, 0, 2],
//                 height: 50,
//                 fontSize: 12,
//                 color:'#fff',
//             },
//             rich: {
//                 Sunny: {
//                     height: 50,
//                     // width: 60,
//                     padding: [0, 5, 0, 5],
//                     align: 'center',
//                 },
//             },
//             formatter: function(params, index) {
//                 var newParamsName = "";
//                 var splitNumber = 5;
//                 var paramsNameNumber = params && params.length;
//                 if (paramsNameNumber && paramsNameNumber <= 4) {
//                     splitNumber = 4;
//                 } else if (paramsNameNumber >= 5 && paramsNameNumber <= 7) {
//                     splitNumber = 4;
//                 } else if (paramsNameNumber >= 8 && paramsNameNumber <= 9) {
//                     splitNumber = 5;
//                 } else if (paramsNameNumber >= 10 && paramsNameNumber <= 14) {
//                     splitNumber = 5;
//                 } else {
//                     params = params && params.slice(0, 15);
//                 }
//
//                 var provideNumber = splitNumber; //一行显示几个字
//                 var rowNumber = Math.ceil(paramsNameNumber / provideNumber) || 0;
//                 if (paramsNameNumber > provideNumber) {
//                     for (var p = 0; p < rowNumber; p++) {
//                         var tempStr = "";
//                         var start = p * provideNumber;
//                         var end = start + provideNumber;
//                         if (p == rowNumber - 1) {
//                             tempStr = params.substring(start, paramsNameNumber);
//                         } else {
//                             tempStr = params.substring(start, end) + "\n";
//                         }
//                         newParamsName += tempStr;
//                     }
//
//                 } else {
//                     newParamsName = params;
//                 }
//                 params = newParamsName
//                 return '{Sunny|' + params + '}';
//             },
//             color: '#687284',
//         },
//
//     },
//     yAxis: {
//         axisLine: {
//             show: false
//         },
//         axisTick: {
//             show: false
//         },
//         axisLabel: {
//             show: false
//         },
//         splitLine: {
//             show: true,
//             lineStyle: {
//                 color: '#F1F3F5',
//                 type: 'solid'
//             },
//             interval: 2
//         },
//         splitNumber: 4,
//     },
//     series: myData.seriesArr
// }
// //////////////////////交通流量 end
//
// //交通工具流量
//
//
// //////////////////////交通工具流量 end
//
// //本月发生事件1
// var color = ['#e9df3d', '#f79c19', '#21fcd6', '#08c8ff', '#df4131'];
// var data = [{
//         "name": "超速",
//         "value": 30
//     },
//     {
//         "name": "闯红灯",
//         "value": 30
//     },
//     {
//         "name": "闯禁行",
//         "value": 42
//     },
//     {
//         "name": "违停",
//         "value": 50
//     },
//     {
//         "name": "逆行",
//         "value": 34
//     }
// ];
//
// var max = data[0].value;
// data.forEach(function(d) {
//     max = d.value > max ? d.value : max;
// });
//
// var renderData = [{
//     value: [],
//     name: "告警类型TOP5",
//     symbol: 'none',
//     lineStyle: {
//         normal: {
//             color: '#ecc03e',
//             width: 2
//         }
//     },
//     areaStyle: {
//         normal: {
//             color: new echarts.graphic.LinearGradient(0, 0, 1, 0,
//                 [{
//                     offset: 0,
//                     color: 'rgba(203, 158, 24, 0.8)'
//                 }, {
//                     offset: 1,
//                     color: 'rgba(190, 96, 20, 0.8)'
//                 }],
//                 false)
//         }
//     }
// }];
//
//
// data.forEach(function(d, i) {
//     var value = ['', '', '', '', ''];
//     value[i] = max,
//     renderData[0].value[i] = d.value;
//     renderData.push({
//         value: value,
//         symbol: 'circle',
//         symbolSize: 12,
//         lineStyle: {
//             normal: {
//                 color: 'transparent'
//             }
//         },
//         itemStyle: {
//             normal: {
//                 color: color[i],
//             }
//         }
//     })
// })
// var indicator = [];
//
// data.forEach(function(d) {
//     indicator.push({
//         name: d.name,
//         max: max,
//         color: '#fff'
//     })
// })
//
//
//
//
// //收费站收费排行1
// var spirit = '../images.ksh45.png';
//
// var maxData = 200;
//
// option4 = {
//    "title": {
//       "text": " ",
//       "left": "center",
//       "y": "10",
//       "textStyle": {
//         "color": "#fff"
//       }
//     },
//
//     "grid": {
//       "left": 30,
//       "top": 0,
//       "bottom": 10
//     },
//     "tooltip": {
//       "trigger": "item",
//       "textStyle": {
//         "fontSize": 12
//       },
//       "formatter": "{b0}:{c0}"
//     },
//     "xAxis": {
//       "max": 100,
//       "splitLine": {
//         "show": false
//       },
//       "axisLine": {
//         "show": false
//       },
//       "axisLabel": {
//         "show": false
//       },
//       "axisTick": {
//         "show": false
//       }
//     },
//     "yAxis": [
//       {
//         "type": "category",
//         "inverse": false,
//         "data": [
//           "信阳",
//           "许昌",
//           "焦作",
//           "商丘",
//           "周口",
//         ],
//         "axisLine": {
//           "show": false
//         },
//         "axisTick": {
//           "show": false
//         },
//         "axisLabel": {
//           "margin": -4,
//           "textStyle": {
//             "color": "#fff",
//             "fontSize": 16.25
//           }
//         }
//       },
//
//     ],
//     "series": [
//       {
//         "type": "pictorialBar",
//         "symbol": "image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAABaCAYAAAA4qEECAAADYElEQVR4nO2dz0sUYRjHP7tIdAmxQ1LdlhCKMohAIsgiyEuHjkUEFQTlpejS/xCCBB06RBGBBKIG4cGyH0qHBKE9eKyFqBQPRQeNCt06vGNY7bq7szPfeZLnAwuzM+/zgw/DDvMu70wOIVveLscJOwycA44A24CfwAfgKXAbeFVvovlC/o/vuVwuTj+x0FWiYdGbgXvA8RrjHgAXgIVaCbMU3SKr1BhtwEtgZx1jTwI7gG7ga5pNNUO+9pBMuEN9klfYD9xMqZdEsCj6AHAiRtxZYFeyrSSHRdGnYsblCD8jJrEoek8TsbsT6yJhLIrelFFsqlgUPZtRbKpYFP2kidjxxLpIGIuiB4AvMeLmgJGEe0kMi6I/AVdjxPVSx91hVlgUDXAXuEaY16jFMnAJeJhqR01iVTTAdeAYUFxjzBRwCLgl6agJrM51rDAO7AP2EmbxthPO8vfAc2Ams84axLpoCGKLrH1mm8eC6KPAGaAL2Fpj7AZgY7T9DfhRY/wc4eflPmH+OjOynI8uEGbpukXlJ4Dz84V8aWWHcj46q4thFzCNTjJRren2UrlLWPM3WYjuAMYIk/tq2oCx9lK5Q11YLboFGARaxXVX0woMtpfK0uuTWvRFoFNcsxKdhF5kqEX3iuuthbQXtehG/gdMG2kvlm/B1xUuWoSLFmFF9CRwg2TnM4pRzskEc8bGiugR4ArhNjkpJqKcJv51sSJ63eOiRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEWvTHKvs/p1izWu5qvaSCWvTlCvtmgeEUaw5TeUVtpV5SQy16COgBRoHXhMWb3aS7PnAhqjEQ1RwFeuYL+aEUa/5DFmtYHkefOEwQVmcBvKD+FQNvgNN/P+pHiV8MRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEixbhokVYEx3nudGKXE1jTfS6xUWLcNEiXLQIFy3CRYtw0SJctAgXLcJFi3DRIv430eUq2+axJvp7jePPqmzHySXFmuhHwFKVYzNA/6rv/VR/s9BSlMsM1kTPEN4DPkU4I8vAO6APOAgsrhq7GO3ri8aUo5ipKIep1zv9AtipgOACGIrLAAAAAElFTkSuQmCC",
//         "symbolRepeat": "fixed",
//         "symbolMargin": "5%",
//         "symbolClip": true,
//         "symbolSize": 22.5,
//         "symbolPosition": "start",
//         "symbolOffset": [
//           20,
//           0
//         ],
//         "symbolBoundingData": 300,
//         "data": [
//           13,
//           42,
//           67,
//           81,
//           86,
//
//         ],
//         "z": 10
//       },
//       {
//         "type": "pictorialBar",
//         "itemStyle": {
//           "normal": {
//             "opacity": 0.3
//           }
//         },
//         "label": {
//           "normal": {
//             "show": false
//           }
//         },
//         "animationDuration": 0,
//         "symbolRepeat": "fixed",
//         "symbolMargin": "5%",
//         "symbol": "image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAABaCAYAAAA4qEECAAADYElEQVR4nO2dz0sUYRjHP7tIdAmxQ1LdlhCKMohAIsgiyEuHjkUEFQTlpejS/xCCBB06RBGBBKIG4cGyH0qHBKE9eKyFqBQPRQeNCt06vGNY7bq7szPfeZLnAwuzM+/zgw/DDvMu70wOIVveLscJOwycA44A24CfwAfgKXAbeFVvovlC/o/vuVwuTj+x0FWiYdGbgXvA8RrjHgAXgIVaCbMU3SKr1BhtwEtgZx1jTwI7gG7ga5pNNUO+9pBMuEN9klfYD9xMqZdEsCj6AHAiRtxZYFeyrSSHRdGnYsblCD8jJrEoek8TsbsT6yJhLIrelFFsqlgUPZtRbKpYFP2kidjxxLpIGIuiB4AvMeLmgJGEe0kMi6I/AVdjxPVSx91hVlgUDXAXuEaY16jFMnAJeJhqR01iVTTAdeAYUFxjzBRwCLgl6agJrM51rDAO7AP2EmbxthPO8vfAc2Ams84axLpoCGKLrH1mm8eC6KPAGaAL2Fpj7AZgY7T9DfhRY/wc4eflPmH+OjOynI8uEGbpukXlJ4Dz84V8aWWHcj46q4thFzCNTjJRren2UrlLWPM3WYjuAMYIk/tq2oCx9lK5Q11YLboFGARaxXVX0woMtpfK0uuTWvRFoFNcsxKdhF5kqEX3iuuthbQXtehG/gdMG2kvlm/B1xUuWoSLFmFF9CRwg2TnM4pRzskEc8bGiugR4ArhNjkpJqKcJv51sSJ63eOiRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEWvTHKvs/p1izWu5qvaSCWvTlCvtmgeEUaw5TeUVtpV5SQy16COgBRoHXhMWb3aS7PnAhqjEQ1RwFeuYL+aEUa/5DFmtYHkefOEwQVmcBvKD+FQNvgNN/P+pHiV8MRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEixbhokVYEx3nudGKXE1jTfS6xUWLcNEiXLQIFy3CRYtw0SJctAgXLcJFi3DRIv430eUq2+axJvp7jePPqmzHySXFmuhHwFKVYzNA/6rv/VR/s9BSlMsM1kTPEN4DPkU4I8vAO6APOAgsrhq7GO3ri8aUo5ipKIep1zv9AtipgOACGIrLAAAAAElFTkSuQmCC",
//         "symbolSize": 22.5,
//         "symbolBoundingData": 300,
//         "symbolPosition": "start",
//         "symbolOffset": [
//           20,
//           0
//         ],
//         "data": [
//           100,
//          80,
//           60,
//           40,
//           20,
//
//         ],
//         "z": 5
//       }
//     ]
// };
//
//
// // Make dynamic data.
// function random() {
//     return +(Math.random() * (maxData - 10)).toFixed(1);
// }
// setInterval(function () {
//     var dynamicData = [random(), random(), random(), random(),random(), random(), random(), random(),random(),random()];
//     myChart.setOption({
//         series: [{
//             data: dynamicData.slice()
//         }, {
//             data: dynamicData.slice()
//         }]
//     })
// }, 3000)
// //////////////////////收费站收费排行2 end
//
// //收费站收费排行2
// var spirit = '../images.ksh45.png';
//
// var maxData = 200;
//
// option41 = {
//    "title": {
//       "text": " ",
//       "left": "center",
//       "y": "10",
//       "textStyle": {
//         "color": "#fff"
//       }
//     },
//
//     "grid": {
//       "left": 30,
//       "top": 0,
//       "bottom": 10
//     },
//     "tooltip": {
//       "trigger": "item",
//       "textStyle": {
//         "fontSize": 12
//       },
//       "formatter": "{b0}:{c0}"
//     },
//     "xAxis": {
//       "max": 100,
//       "splitLine": {
//         "show": false
//       },
//       "axisLine": {
//         "show": false
//       },
//       "axisLabel": {
//         "show": false
//       },
//       "axisTick": {
//         "show": false
//       }
//     },
//     "yAxis": [
//       {
//         "type": "category",
//         "inverse": false,
//         "data": [
//           "郑州",
//           "洛阳",
//           "开封",
//           "新乡",
//           "南阳",
//         ],
//         "axisLine": {
//           "show": false
//         },
//         "axisTick": {
//           "show": false
//         },
//         "axisLabel": {
//           "margin": -4,
//           "textStyle": {
//             "color": "#fff",
//             "fontSize": 16.25
//           }
//         }
//       },
//
//     ],
//     "series": [
//       {
//         "type": "pictorialBar",
//         "symbol": "image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAABaCAYAAAA4qEECAAADYElEQVR4nO2dz0sUYRjHP7tIdAmxQ1LdlhCKMohAIsgiyEuHjkUEFQTlpejS/xCCBB06RBGBBKIG4cGyH0qHBKE9eKyFqBQPRQeNCt06vGNY7bq7szPfeZLnAwuzM+/zgw/DDvMu70wOIVveLscJOwycA44A24CfwAfgKXAbeFVvovlC/o/vuVwuTj+x0FWiYdGbgXvA8RrjHgAXgIVaCbMU3SKr1BhtwEtgZx1jTwI7gG7ga5pNNUO+9pBMuEN9klfYD9xMqZdEsCj6AHAiRtxZYFeyrSSHRdGnYsblCD8jJrEoek8TsbsT6yJhLIrelFFsqlgUPZtRbKpYFP2kidjxxLpIGIuiB4AvMeLmgJGEe0kMi6I/AVdjxPVSx91hVlgUDXAXuEaY16jFMnAJeJhqR01iVTTAdeAYUFxjzBRwCLgl6agJrM51rDAO7AP2EmbxthPO8vfAc2Ams84axLpoCGKLrH1mm8eC6KPAGaAL2Fpj7AZgY7T9DfhRY/wc4eflPmH+OjOynI8uEGbpukXlJ4Dz84V8aWWHcj46q4thFzCNTjJRren2UrlLWPM3WYjuAMYIk/tq2oCx9lK5Q11YLboFGARaxXVX0woMtpfK0uuTWvRFoFNcsxKdhF5kqEX3iuuthbQXtehG/gdMG2kvlm/B1xUuWoSLFmFF9CRwg2TnM4pRzskEc8bGiugR4ArhNjkpJqKcJv51sSJ63eOiRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEWvTHKvs/p1izWu5qvaSCWvTlCvtmgeEUaw5TeUVtpV5SQy16COgBRoHXhMWb3aS7PnAhqjEQ1RwFeuYL+aEUa/5DFmtYHkefOEwQVmcBvKD+FQNvgNN/P+pHiV8MRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEixbhokVYEx3nudGKXE1jTfS6xUWLcNEiXLQIFy3CRYtw0SJctAgXLcJFi3DRIv430eUq2+axJvp7jePPqmzHySXFmuhHwFKVYzNA/6rv/VR/s9BSlMsM1kTPEN4DPkU4I8vAO6APOAgsrhq7GO3ri8aUo5ipKIep1zv9AtipgOACGIrLAAAAAElFTkSuQmCC",
//         "symbolRepeat": "fixed",
//         "symbolMargin": "5%",
//         "symbolClip": true,
//         "symbolSize": 22.5,
//         "symbolPosition": "start",
//         "symbolOffset": [
//           20,
//           0
//         ],
//         "symbolBoundingData": 300,
//         "data": [
//           51,
//           32,
//           82,
//           42,
//           81,
//
//         ],
//         "z": 10
//       },
//       {
//         "type": "pictorialBar",
//         "itemStyle": {
//           "normal": {
//             "opacity": 0.3
//           }
//         },
//         "label": {
//           "normal": {
//             "show": false
//           }
//         },
//         "animationDuration": 0,
//         "symbolRepeat": "fixed",
//         "symbolMargin": "5%",
//         "symbol": "image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAABaCAYAAAA4qEECAAADYElEQVR4nO2dz0sUYRjHP7tIdAmxQ1LdlhCKMohAIsgiyEuHjkUEFQTlpejS/xCCBB06RBGBBKIG4cGyH0qHBKE9eKyFqBQPRQeNCt06vGNY7bq7szPfeZLnAwuzM+/zgw/DDvMu70wOIVveLscJOwycA44A24CfwAfgKXAbeFVvovlC/o/vuVwuTj+x0FWiYdGbgXvA8RrjHgAXgIVaCbMU3SKr1BhtwEtgZx1jTwI7gG7ga5pNNUO+9pBMuEN9klfYD9xMqZdEsCj6AHAiRtxZYFeyrSSHRdGnYsblCD8jJrEoek8TsbsT6yJhLIrelFFsqlgUPZtRbKpYFP2kidjxxLpIGIuiB4AvMeLmgJGEe0kMi6I/AVdjxPVSx91hVlgUDXAXuEaY16jFMnAJeJhqR01iVTTAdeAYUFxjzBRwCLgl6agJrM51rDAO7AP2EmbxthPO8vfAc2Ams84axLpoCGKLrH1mm8eC6KPAGaAL2Fpj7AZgY7T9DfhRY/wc4eflPmH+OjOynI8uEGbpukXlJ4Dz84V8aWWHcj46q4thFzCNTjJRren2UrlLWPM3WYjuAMYIk/tq2oCx9lK5Q11YLboFGARaxXVX0woMtpfK0uuTWvRFoFNcsxKdhF5kqEX3iuuthbQXtehG/gdMG2kvlm/B1xUuWoSLFmFF9CRwg2TnM4pRzskEc8bGiugR4ArhNjkpJqKcJv51sSJ63eOiRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEWvTHKvs/p1izWu5qvaSCWvTlCvtmgeEUaw5TeUVtpV5SQy16COgBRoHXhMWb3aS7PnAhqjEQ1RwFeuYL+aEUa/5DFmtYHkefOEwQVmcBvKD+FQNvgNN/P+pHiV8MRbhoES5ahIsW4aJFuGgRLlqEixbhokW4aBEuWoSLFuGiRbhoES5ahIsW4aJFuGgRLlqEixbhokVYEx3nudGKXE1jTfS6xUWLcNEiXLQIFy3CRYtw0SJctAgXLcJFi3DRIv430eUq2+axJvp7jePPqmzHySXFmuhHwFKVYzNA/6rv/VR/s9BSlMsM1kTPEN4DPkU4I8vAO6APOAgsrhq7GO3ri8aUo5ipKIep1zv9AtipgOACGIrLAAAAAElFTkSuQmCC",
//         "symbolSize": 22.5,
//         "symbolBoundingData": 300,
//         "symbolPosition": "start",
//         "symbolOffset": [
//           20,
//           0
//         ],
//         "data": [
//           51,
//           32,
//           82,
//           42,
//           81,
//
//         ],
//         "z": 5
//       }
//     ]
// };
//
//
// // Make dynamic data.
// function random() {
//     return +(Math.random() * (maxData - 10)).toFixed(1);
// }
// setInterval(function () {
//     var dynamicData = [random(), random(), random(), random(),random(), random(), random(), random(),random(),random()];
//     myChart.setOption({
//         series: [{
//             data: dynamicData.slice()
//         }, {
//             data: dynamicData.slice()
//         }]
//     })
// }, 3000)
// //////////////////////收费站收费排行2 end
//
// //今日实时收费
// // 模拟各城市数据
//
//
//
// const cityData = {
//     '全省数据': { vehicleTotal: 32, todayOnline: 24, todayAlarm: 12 },
//     '郑州': { vehicleTotal: 12, todayOnline: 9, todayAlarm: 45 },
//     '开封': { vehicleTotal: 9, todayOnline: 7, todayAlarm: 25 },
//     '洛阳': { vehicleTotal: 11, todayOnline: 8, todayAlarm: 35 },
//     '新乡': { vehicleTotal: 80, todayOnline: 6, todayAlarm: 18 },
//     '许昌': { vehicleTotal: 10, todayOnline: 7, todayAlarm: 30 },
//     '焦作': { vehicleTotal: 9, todayOnline: 8, todayAlarm: 22 },
//     '信阳': { vehicleTotal: 11, todayOnline: 9, todayAlarm: 4 },
//     '商丘': { vehicleTotal: 8, todayOnline: 6, todayAlarm: 28 },
//     '长治': { vehicleTotal: 7, todayOnline: 5, todayAlarm: 15 }
// };
//
// let currentCity = '信阳';
// // 饼图通用配置函数
// function createPieOption(titleText, value, colorStops) {
//     return {
//         title: {
//             text: titleText,
//             subtext: '',
//             left: 'center',
//             top: 'center',
//             textStyle: {
//                 color: '#fff',
//                 fontSize: 16,
//                 fontFamily: 'PingFangSC-Regular'
//             },
//             itemGap: -1
//         },
//         series: [{
//             name: 'pie1',
//             type: 'pie',
//             clockWise: true,
//             radius: ['65%', '70%'],
//             itemStyle: {
//                 normal: {
//                     label: { show: false },
//                     labelLine: { show: false }
//                 }
//             },
//             hoverAnimation: false,
//             data: [{
//                 value: value,
//                 name: 'completed',
//                 itemStyle: {
//                     normal: {
//                         borderWidth: 8,
//                         borderColor: {
//                             colorStops: colorStops || [{
//                                 offset: 0,
//                                 color: '#1d54f7'
//                             }, {
//                                 offset: 1,
//                                 color: '#68eaf9'
//                             }]
//                         },
//                         color: {
//                             colorStops: colorStops || [{
//                                 offset: 0,
//                                 color: '#1d54f7'
//                             }, {
//                                 offset: 1,
//                                 color: '#68eaf9'
//                             }]
//                         },
//                         label: { show: false },
//                         labelLine: { show: false }
//                     }
//                 }
//             }, {
//                 name: 'gap',
//                 value: 100 - value,
//                 itemStyle: {
//                     normal: {
//                         label: { show: false },
//                         labelLine: { show: false },
//                         color: 'rgba(0, 0, 0, 0)',
//                         borderColor: 'rgba(0, 0, 0, 0)',
//                         borderWidth: 0
//                     }
//                 }
//             }]
//         }]
//     };
// }
// // 初始化图表实例（确保在DOM加载完成后执行）
// $(document).ready(function() {
//     // 初始化三个饼图实例
//     const myChart5 = echarts.init(document.getElementById('main5'));
//     const myChart6 = echarts.init(document.getElementById('main6'));
//     const myChart7 = echarts.init(document.getElementById('main7'));
//
//     // 初始化图表配置
//     const option5 = createPieOption('车辆总数', 80, [
//         { offset: 0, color: '#1d54f7' },
//         { offset: 1, color: '#68eaf9' }
//     ]);
//
//     const option6 = createPieOption('今日上线', 20, [
//         { offset: 0, color: '#02df94' },
//         { offset: 1, color: '#28d3d0' }
//     ]);
//
//     const option7 = createPieOption('今日报警', 46, [
//         { offset: 0, color: '#eb3600' },
//         { offset: 1, color: '#d0a00e' }
//     ]);
//
//     // 设置初始图表
//     myChart5.setOption(option5);
//     myChart6.setOption(option6);
//     myChart7.setOption(option7);
//
//     // 添加城市点击事件（确保元素已加载）
//     $('.visualSssf_left a').on('click', function() {
//         // 移除所有active类
//         $('.visualSssf_left a').removeClass('active');
//         // 当前点击元素添加active类
//         $(this).addClass('active');
//         // 获取点击的城市
//         currentCity = $(this).data('city');
//         // 更新图表
//         updateCharts(myChart5, myChart6, myChart7, option5, option6, option7);
//     });
//
//     // 更新所有图表（接收图表实例和配置作为参数）
//     function updateCharts(chart5, chart6, chart7, opt5, opt6, opt7) {
//         const data = cityData[currentCity];
//
//         // 更新车辆总数饼图
//         opt5.title.text = `车辆总数：${data.vehicleTotal}万辆`;
//         opt5.series[0].data[0].value = data.vehicleTotal;
//         opt5.series[0].data[1].value = 100 - data.vehicleTotal;
//         chart5.setOption(opt5);
//
//         // 更新今日上线饼图
//         opt6.title.text = `今日上线：${data.todayOnline}万辆`;
//         opt6.series[0].data[0].value = data.todayOnline;
//         opt6.series[0].data[1].value = 100 - data.todayOnline;
//         chart6.setOption(opt6);
//
//         // 更新今日报警饼图
//         opt7.title.text = `今日报警：${data.todayAlarm}万辆`;
//         opt7.series[0].data[0].value = data.todayAlarm;
//         opt7.series[0].data[1].value = 100 - data.todayAlarm;
//         chart7.setOption(opt7);
//     }
//
//     // 窗口大小改变时重绘图表
//     $(window).on('resize', function() {
//         myChart5.resize();
//         myChart6.resize();
//         myChart7.resize();
//     });
// });
//
// //////////////////////今日实时收费 end
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
