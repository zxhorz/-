'use strict';

angular.module('svgd3Module').directive('svgd3Directive', function () {
    return {
        restrict: 'EA',
        scope:false,
        replace: false,
        controller: d3Controller
    };
    function d3Controller($scope, $http, $timeout, $state, infoDataService) {
        //在codebrowser页面会画出d3图，在sys doc 不会画图
        var presvg = d3.select('#dependencySvg');
        //生成svg元素
        presvg.append("svg")
            .attr("id", "shownSVG")
            .style("width", '100%')
            .style("height", 440);

        $scope.d3link = function () {
            $scope.$watch('d3url', function (newValue) {
                if (newValue !== undefined) {
                    var svg = d3.select("#shownSVG");
                    var width = 560;
                    var height = 570;
                    $scope.d3width = width;
                    var color = d3.scaleOrdinal(d3.schemeCategory20);

                    //固定每个圆圈节点的颜色
                    color.domain(["Program", "Copybook", "Table", "File"]);

                    //建立一个力导向图
                    var simulation = d3.forceSimulation()
                        .force("link", d3.forceLink().id(function (d) { return d.id; }).distance(100))
                        .force("charge", d3.forceManyBody())
                        .force("center", d3.forceCenter(width / 3, height / 2));    //设置图形中心位置

                    svg.selectAll('*').remove();
                    var graph = JSON.parse(newValue);
                    var marker =
                        svg.append("marker")
                            //.attr("id", function(d) { return d; })
                            .attr("id", "resolved")
                            //.attr("markerUnits","strokeWidth")//设置为strokeWidth箭头会随着线的粗细发生变化
                            .attr("markerUnits", "userSpaceOnUse")
                            .attr("viewBox", "0 -5 10 10")//坐标系的区域
                            .attr("refX", 50)//箭头坐标
                            .attr("refY", 0)
                            .attr("markerWidth", 8)//标识的大小
                            .attr("markerHeight", 8)
                            .attr("orient", "auto")//绘制方向，可设定为：auto（自动确认方向）和 角度值
                            .attr("stroke-width", 2)//箭头宽度
                            .append("path")
                            .attr("d", "M0,-5L10,0L0,5")//箭头的路径
                            .attr('fill', '#999');//箭头颜色

                    var link = svg.append("g")
                        // .attr("class", "links")
                        .attr("style", "stroke-opacity: 0.6;stroke: #999;")
                        .selectAll("line")
                        .data(graph.links)
                        .enter().append("line")
                        .attr("stroke-width", function (d) { return Math.sqrt(d.value); })
                        .attr("marker-end", "url(#resolved)");

                    var node = svg.append("g")
                        .attr("class", "nodes")
                        .selectAll("circle")
                        .data(graph.nodes)
                        .enter().append("circle")
                        .attr("r", 8)
                        .attr("fill", function (d) { return color(d.group); })
                        .call(d3.drag()
                            .on("start", dragstarted)
                            .on("drag", dragged)
                            .on("end", dragended)
                        ).on('click', function () {
                            var objArr = d3.select(this).data();
                            var group = objArr[0].group;
                            if (group === "Program") {
                                $state.go('detail', { tab: 'Program' });
                                infoDataService.setProgramInfo(objArr[0].id);
                            }
                        });
                    var text = svg.append("g")
                        .attr("class", "text")
                        .selectAll("text")
                        .data(graph.nodes)
                        .enter()
                        .append("text")
                        .text(function (d) {
                            // var str = d.id.split("_");
                            // var l = str.length;
                            // return str[l - 1];
                            return d.id;
                        });

                    node.append("title")
                        .text(function (d) { return d.id; });
                    simulation
                        .nodes(graph.nodes)
                        .on("tick", ticked);

                    simulation.force("link")
                        .links(graph.links);

                    function ticked() {
                        link
                            .attr("x1", function (d) { return d.source.x; })
                            .attr("y1", function (d) { return d.source.y; })
                            .attr("x2", function (d) { return d.target.x; })
                            .attr("y2", function (d) { return d.target.y; });

                        node
                            .attr("cx", function (d) { return d.x; })
                            .attr("cy", function (d) { return d.y; });
                        text
                            .attr("x", function (d) { return d.x + 10; })
                            .attr("y", function (d) { return d.y - 10; });
                    }

                    //添加图例，说明每个点的意义
                    var l = graph.mark.length;
                    var ser = [];
                    for (var i = 0; i < l; i++) {
                        var x = "";
                        ser.push({ "seriesName": graph.mark[i].name, "group": graph.mark[i].group })
                    }
                    var seriesData = { series: ser };
                    addLegend();

                    function addLegend() {
                        var seriesNames = getSeriesName(seriesData);

                        //每个节点和对应文字的分组
                        var legend = svg.selectAll(".legend")
                            .data(seriesNames)//seriesNames.slice().reverse()
                            .enter().append("g")
                            .attr("class", "legend")
                            .attr("transform", function (d, i) { return "translate(0," + i * 0 + ")"; });

                        legend.append("circle")
                            .attr("cx", function (d, i) {
                                return 30;
                            })
                            .attr("cy", function (d, i) {
                                return i > 0 ? 50 + 25 * i : 50;
                            })
                            .attr("r", function (d) { return 6; })
                            .data(seriesData.series)
                            .style("fill", function (d) {
                                return color(d.group);
                            });

                        legend.append("text")
                            .attr("x", 80)
                            .attr("y", function (d, i) {
                                return i > 0 ? 50 + 25 * i : 50;
                            })
                            .data(seriesData.series)
                            .attr("dy", ".35em")
                            .style("text-anchor", "middle")
                            // .style("text-anchor", "end")
                            .text(function (d) { return d.seriesName; });
                    };
                    /**返回系列名
                     * @参数 data 柱图数据
                     */
                    function getSeriesName(data) {
                        var len = data.series.length;
                        var seriesName = [];
                        for (var i = 0; i < len; i++) {
                            seriesName.push(data.series[i].seriesName);
                        }
                        return seriesName;
                    }
                    // });
                    $scope.getdependencybase64 = function () {
                        // var serializer = new XMLSerializer();
                        var svgString = new XMLSerializer().serializeToString(svg.node());
                        // var source = '<?xml version="1.0" standalone="no"?>\r\n' + serializer.serializeToString(svg.node());
                        var image = new Image();
                        var s = new Blob([svgString], {
                            type: "image/svg+xml;charset=utf-8,"
                        });
                        var DOMURL = self.URL || self.webkitURL || self;
                        var url = DOMURL.createObjectURL(s);
                        image.src = url;
                        var canvas = document.createElement("canvas");
                        canvas.width = 560;
                        canvas.height = 570;

                        var context = canvas.getContext("2d");
                        context.fillStyle = '#fff';
                        context.fillRect(0, 0, 10000, 10000);
                        image.onload = function () {
                            context.drawImage(image, 0, 0);
                            var a = document.createElement("a");
                            a.href = canvas.toDataURL("image/png");
                            $scope.denpendencyimage64 = a.href.substring(22);
                        }
                    }
                }

                function dragstarted(d) {
                    if (!d3.event.active) simulation.alphaTarget(0.3).restart();
                    d.fx = d.x;
                    d.fy = d.y;
                }

                function dragged(d) {
                    d.fx = d3.event.x;
                    d.fy = d3.event.y;
                }

                function dragended(d) {
                    if (!d3.event.active) simulation.alphaTarget(0);
                    d.fx = null;
                    d.fy = null;
                }
            });
        }
        $scope.d3link();
    }
})