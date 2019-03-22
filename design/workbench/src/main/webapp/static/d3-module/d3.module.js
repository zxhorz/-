angular.module('d3ForceService', ['floatService'])
    .factory('d3ForceShow', function (floatCalculation, $http) {
        var D3force = function (rid) {
            this.selectedNodes = {};
            this.$element = angular.element('#' + rid);
            this.options = {
                width: 40,
                height: 10,
                charge: -400,
                linkDistance: 10,
                linkStrength: 1,
                scale: [0.01, 6],
                showTitle: false,
                showAbbr: true,
                gravity: 0.1,
                r: 15,
                minR: 4,
                maxR: 11,
                click: null,
                x: 150,
                y: 150,
                randomId: rid,
                //初始化完成，有可能会调用多次初始化
                complete: null,
                //颜色取值
                colors: ['blue',
                    'purple',
                    'maroon',
                    'darkgray',
                    'yellow',
                    'green',
                    'chartreuse',
                    'cyan',
                    'aqua',
                    'fuchsia',
                    'gray',
                    'lime',
                    'navy',
                    'olive',
                    'red',
                    'silver',
                    'teal'
                ],
                labelList: null
            };
            this.currentZoom = 1;
            this.init();
        };

        (function () {
            this.constructor = D3force;
            //*********************d3 展示
            this._createFilter = function () {
                var glow = this.svg.append('filter')
                    .attr('x', '-50%')
                    .attr('y', '-50%')
                    .attr('width', '100%')
                    .attr('height', '100%')
                    .attr('id', 'blue-glow');

                glow.append('feColorMatrix')
                    .attr('type', 'matrix')
                    .attr('values', '0 0 0 0  0 ' + '0 0 0 0  0 ' + '0 0 0 0  .7 ' + '0 0 0 1  0 ');

                glow.append('feGaussianBlur')
                    .attr('stdDeviation', 3)
                    .attr('result', 'coloredBlur');

                glow.append('feMerge').selectAll('feMergeNode')
                    .data(['coloredBlur', 'SourceGraphic'])
                    .enter().append('feMergeNode')
                    .attr('in', String);
            };

            //绘制连接线箭头
            this._createArraw = function () {
                var self = this,
                    options = self.options;
                self.svg.append('defs')
                    .selectAll('marker')
                    .data(['end', 'yellow_end', 'grey_end', 'green_end'])
                    .enter()
                    .append('marker')
                    .attr('id', function (d) {
                        return d + options.randomId;
                    })
                    .attr('class', function (d) {
                        return d;
                    })
                    .attr('viewBox', '0 -5 10 10')
                    .attr('refX', 21)
                    .attr('refY', 0)
                    .attr('markerWidth', 6)
                    .attr('markerHeight', 6)
                    .attr('orient', 'auto')
                    .append('path')
                    .attr('d', 'M0,-5L10,0L0,5');
            };

            //绘制左上角图例
            this._createColorMark = function () {
                var self = this,
                    options = self.options,
                    index = 0,
                    dataset = [];

                for (var name in options.labelList) {
                    dataset.push({
                        name: options.labelList[name].name,
                        clz: options.labelList[name].color
                    });
                }
                var colorMark = self.svg.append('g')
                    .attr('class', 'color-mark')
                    .selectAll('g')
                    .data(dataset)
                    .enter()
                    .append('g')
                    .attr('class', function (d) {
                        var cls = 'mark-node ';
                        cls += d.clz;
                        return cls;
                    })
                    .attr('transform', function () {
                        return 'translate(' + 20 + ',' + ((index++) * 20 + 20) + ')';
                    });
                colorMark.append('text')
                    .attr('x', 10)
                    .attr('y', 5)
                    .text(function (d) {
                        var name = d.name;
                        d3.select(this).attr('title', name);
                        return name;
                    });

                colorMark.append('circle')
                    //设置圆点的半径，圆点的度越大weight属性值越大，可以对其做一点数学变换
                    .attr('r', function () {
                        return 6;
                    });
            };

            this.init = function () {
                var self = this,
                    options = self.options;
                //拖动时的偏移量
                self.currentOffset = {
                    x: 0,
                    y: 0
                };
                //缓存修改过的节点
                self.changedNodes = {};
                //清空html
                self.$element.html('');

                self.svg = d3.select(self.$element[0])
                    .append('svg')
                    .attr('class', 'graph')
                    .attr('width', '100%')
                    .attr('height', '100%');
                self._createFilter();
                self._createArraw();

                self.svgContainer = self.svg
                    .append('g')
                    .attr('class', 'container');

                registerHandler(self);

                self._createForce();

                if (options.x !== void 0 && options.y !== void 0) {
                    self.currentOffset.x = options.x;
                    self.currentOffset.y = options.y;

                    self.setSvgOffset(self.currentOffset.x, self.currentOffset.y);
                }
                self.links = self.svgContainer.selectAll('.link');
                self.nodes = self.svgContainer.selectAll('.node');
                self.dataset = {
                    nodes: {},
                    links: []
                };

                self.forceNodes = self.force.nodes();
                self.forceLinks = self.force.links();
                //成功以后回调
                return self;
            };

            this._createForce = function () {
                var self = this,
                    options = self.options;
                self.force = d3.layout.force()
                    .charge(options.charge)
                    .linkStrength(options.linkStrength)
                    .linkDistance(options.linkDistance)
                    .gravity(options.gravity)
                    //设置有效空间的大小
                    .size([options.width / 2, options.height / 2]);
                self.force.drag = self.force.drag().on('dragstart', function (d) {
                    //屏蔽掉右键拖动
                    if (d3.event.sourceEvent && d3.event.sourceEvent.which === 3) {
                        return false;
                    }
                    d.fixed = true;
                    self.draged = true;
                    d.dx = 0;
                    d3.event.sourceEvent.stopPropagation();
                }).on('drag', function (d) {
                    d.positionConstraints.x += d3.event.dx / self.currentZoom;
                    d.positionConstraints.y += d3.event.dy / self.currentZoom;

                    //检测位置的变化
                    d.dx -= Math.abs(d3.event.dx);
                    if (d3.event.dx) {
                        self.tick(1);
                    }
                }).on('dragend', function (d) {
                    self.draged = false;
                    if (!d.dx) {
                        //如果node 位置未发生变化
                        //则触发单击事件

                        if (self.selectedNodes[d.id]) {
                            //如果当前节点已选中则取消选择
                            self.unselectNode(d);
                            self.dehighlight();
                        } else {
                            //如果按住ctrl 键则进入多选状态
                            if (!(d3.event.sourceEvent && d3.event.sourceEvent.ctrlKey)) {
                                self.unselectAll();
                            }
                            self.currentSelectedNode = d;
                            self.selectNode(d);
                            self.highlight(d);
                        }
                        this.dragend = false;
                        //触发节点的单击事件
                        options.click && options.click.call(this, d, 'node');
                    } else {
                        this.dragend = true;
                        //如果位置发生变化
                        //则拖动该节点
                        self.tick(1);
                        //添加 changedNodes 数据
                        //save状态时需要此数据
                        self.changedNodes[d.id] = {};
                        self.changedNodes[d.id].x = d.positionConstraints.x;
                        self.changedNodes[d.id].y = d.positionConstraints.y;
                    }
                });

                var _svgDragDx, _svgDragFlag;
                var svgDrag = d3.behavior.drag()
                    .on('dragstart', function () {
                        if (d3.event.sourceEvent && d3.event.sourceEvent.which === 3) {
                            return false;
                        }
                        _svgDragDx = 0;
                        _svgDragFlag = true;
                        //设置手型
                        self.setCursor('pointer');
                    })
                    .on('drag', function () {
                        //检测位置是否有变动
                        if (_svgDragFlag && d3.event.dx) {
                            _svgDragDx -= Math.abs(d3.event.dx);
                            var _x = self.currentOffset.x + d3.event.dx,
                                _y = self.currentOffset.y + d3.event.dy;
                            self.setSvgOffset(_x, _y);
                            self.currentOffset.x = _x;
                            self.currentOffset.y = _y;
                        }
                    })
                    .on('dragend', function () {

                        if (_svgDragDx) {
                            //触发 graphchange
                            self.$element.trigger('graphchange');
                        }
                        _svgDragFlag = false;
                        _svgDragDx = 0;
                        //取消手型
                        self.setCursor('default');
                    });
                self.svg.call(svgDrag);


                //X比例尺
                var xScale = d3.scale.linear()
                    .domain([0, options.width])
                    .range([0, options.width]);
                //Y比例尺
                var yScale = d3.scale.linear()
                    .domain([0, options.height])
                    .range([0, options.height]);
                //放大缩小比例尺
                var zoomScale = d3.scale.linear()
                    .domain(options.scale)
                    .range(options.scale)
                    .clamp(true);

                self.zoom = d3.behavior.zoom()
                    .x(xScale)
                    .y(yScale)
                    .scaleExtent(options.scale)
                    .on('zoom', function (increment) {
                        var newZoom = increment
                            === undefined ? d3.event.scale : zoomScale(self.currentZoom + increment);

                        self.scale(newZoom);
                    });
                //放大与缩小
                self.svg.call(self.zoom);
            };

            //清除选中状态
            this.unselectNode = function (d) {
                var self = this,
                    d3_this = self.$element.find('#node_' + d.id)[0];


                //删除节点
                delete self.selectedNodes[d.id];

                d3.select(d3_this).classed('selected', false);
                return self;
            };
            this.setSvgOffset = function (x, y, delay) {
                var self = this,
                    _x = self.currentOffset.x,
                    _y = self.currentOffset.y;
                if (delay) {
                    $(self.svgContainer[0]).css({
                        left: _x,
                        top: _y
                    }).animate({
                        left: x,
                        top: y
                    }, {
                            duration: 300,
                            step: function (a, b) {
                                var x1, y1;
                                if (b.prop === 'top') {
                                    x1 = _x;
                                    y1 = a;
                                } else {
                                    x1 = a;
                                    y1 = _y;
                                }
                                _x = x1;
                                _y = y1;
                                self.svgContainer
                                    .attr('transform', function () {
                                        return 'translate(' +
                                            x1 + ',' + y1 + ')';
                                    });
                            },
                            complete: function () {
                                self.currentOffset.x = x;
                                self.currentOffset.y = y;
                                self.$element.trigger('graphchange');
                            }
                        });
                } else {
                    x && y && self.svgContainer
                        .attr('transform', function () {
                            return 'translate(' +
                                x + ',' + y + ')';
                        });
                    self.currentOffset.x = x;
                    self.currentOffset.y = y;
                }
                return self;
            };

            //注册svg的点击事件
            function registerHandler(self) {
                //注册容器的单击事件
                self.$element.on('click', function (e) {
                    if (!$(e.target).closest('.node').length) {
                        //取消高亮的节点
                        self.dehighlight();
                    }
                }).on('mousemove', function (e) {
                    //拖拽
                    var offset = self.$element.offset();
                    self.mousemoveOffsetX = e.pageX - offset.left;
                    self.mousemoveOffsetY = e.pageY - offset.top;
                });
            }

            //neo4j获取页面数据
            this.getData = function (url, callback) {
                if (url && url.status === 200) {
                    callback && callback.call(this, url);
                } else {
                    alert('Server error ,please refresh the page.');
                }
            };

            this.formatData = function (data) {
                var self = this;
                var dataset = {};

                dataset.nodes = self.obj2Ary(data.nodes);
                var _links = data.links;
                dataset.links = _links;
                return dataset;
            };

            //对象转数组
            this.obj2Ary = function (obj) {
                var ary = [];
                for (var name in obj) {
                    ary.push(obj[name]);
                }
                return ary;
            };

            //增加节点
            this.addNode = function (node) {
                var options = this.options;
                node.positionConstraints = {
                    x: floatCalculation.mul(node.x, options.width),
                    y: floatCalculation.mul(node.y, options.height)
                };

                this.forceNodes.push(node);

                //将node存储在dataset中
                if (!this.dataset.nodes[node.id]) {
                    this.dataset.nodes[node.id] = node;
                }
                return this;
            };

            //绘制节点
            this.addNodes = function (nodes) {
                if (Object.prototype.toString.call(nodes) === '[object Array]') {
                    var self = this;
                    angular.forEach(nodes, function (node) {
                        self.addNode(node);
                    });
                }
                return this;
            };

            //增加连线
            this.addLink = function (link) {
                var _link = {},
                    sourceId = link.source,
                    source = this.getNodeById(sourceId),
                    targetId = link.target,
                    target = this.getNodeById(targetId);

                if (source && target) {
                    _link.source = source;
                    _link.target = target;
                    this.forceLinks.push(_link);
                }

                return this;
            };

            //增加多个连线
            this.addLinks = function (links) {
                if (Object.prototype.toString.call(links) === '[object Array]') {
                    var self = this;
                    links.forEach(function (link) {

                        self.addLink(link);
                    });

                }
                return this;
            };

            //查找节点
            this.getNodeById = function (id) {
                var self = this;
                if (id !== void 0 && self.dataset) {
                    return self.dataset.nodes[id];
                }
                return null;
            };

            //查找link
            this.getLinkById = function (sourceId, targetId) {
                var links = this.forceLinks;
                for (var i in links) {
                    if (links[i].source.id === sourceId && links[i].target.id === targetId) {
                        return links[i];
                    }
                }
                return null;
            };

            //svg页面刷新，重新绘制svg
            this.update = function (mode) {
                var self = this,
                    options = self.options;

                self.links = self.svgContainer.selectAll('.link').data(self.forceLinks, function (d) {
                    return d.source.id + '-' + d.target.id;
                });
                var _links =
                    self.links.enter()
                        .insert('g', '.node')
                        .attr('class', 'link')
                        .attr('id', function (d) {
                            return 'link_' + d.source.id + '_' + d.target.id;
                        })
                        .on('click', function (d) {
                            options.click && options.click.call(this, d, 'link');
                        });

                _links.append('line')
                    .attr('marker-end', function (d) {
                        return self.getMarker(d);
                    })
                    .append('title')
                    .html(function (d) {
                        return d.name || '';
                    });

                // Delete removed links
                self.links.exit().remove();
                // Update node data
                self.nodes = self.svgContainer.selectAll('.node').data(self.forceNodes, function (d) {
                    return d.id;
                });

                var timer, newNodes =
                    self.nodes.enter()
                        .append('g')
                        .attr('id', function (d) {
                            return 'node_' + d.id;
                        })
                        .attr('data-id', function (d) {
                            $(this).data('node', d);
                            return d.id;
                        })
                        .attr('class', function (d) {
                            return self.getNodeClass(d);
                        })
                        //可以拖动
                        .call(self.force.drag)
                        .on('mouseover', function (d) {

                            clearTimeout(timer);
                            timer = setTimeout(function () {
                                //高亮当前节点
                                //如果拖动节点，则mouseover事件无效
                                if (!self.draged) {
                                    self.highlight(d);
                                }
                            }, 150);

                            d3.select(this)
                                .select('circle')
                                .transition()
                                .duration(200)
                                .attr('r', function () {
                                    return self.getR(self.currentZoom) + 5;
                                });
                            d.mouseover = true;
                        })
                        .on('mouseout', function (d) {
                            clearTimeout(timer);
                            //取消高亮当前节点
                            if (!self.draged && self.currentSelectedNode !== d) {
                                self.dehighlight();
                            }

                            d3.select(this)
                                .select('circle')
                                .transition()
                                .duration(200)
                                .attr('r', function () {
                                    return self.getR(self.currentZoom);
                                });
                            d.mouseover = false;
                        });

                self._createNode(newNodes);
                self.nodes.exit().remove();

                //绘制 矩形框
                self.initRect();
                //start force
                self.force.start();
                self.tick(1, mode);
                return self;
            };

            this.getNodeClass = function (d) {
                var self = this,
                    options = self.options;
                var cls = 'node ',
                    label;
                if (typeof d.label === 'undefined') {
                    label = 'Undefined';
                } else {
                    label = d.label;
                }
                cls += options.labelList[label].color;
                return cls;
            };

            this.getLinkClass = function (d) {
                var cls = 'link ';
                cls += this.getClassName(d);
                return cls;
            };

            //通过type类型获取class名称
            this.getClassName = function (d) {
                var cls = '',
                    type = d.type;
                if (!type) {
                    return cls;
                }
                //如果type 属性 为以下几种
                if (type === 'entry') {
                    cls = 'green';
                } else if (type === 'dead') {
                    cls = 'grey';
                } else if (type === 'live') {
                    cls = 'blue';
                }

                //如果是Test Covered Module
                //则优先显示 test coverage
                if (d.isTestCoverage) {
                    cls = 'cyan';
                }
                return cls;
            };
            //根据type 属性获取 marker
            this.getMarker = function (d) {
                var self = this,
                    options = self.options,
                    marker = self.getClassName(d),
                    markerId;

                //通过class 区别 箭头
                if (marker) {
                    markerId = 'url(#' + marker + '_end' + options.randomId + ')';
                } else {
                    markerId = 'url(#' + marker + 'end' + options.randomId + ')';
                }
                return markerId;
            };
            //绘制节点
            this._createNode = function (appendNodes) {
                var self = this,
                    options = self.options;

                //是否显示 module 名称
                appendNodes.append('text')
                    .attr('x', 0)
                    .attr('y', 20)
                    .attr('class', 'node-text')
                    .text(function (d) {
                        return d.name;
                    })
                    .attr('style', function () {
                        if (options.showTitle) {
                            return 'display:block';
                        } else {
                            return 'display:none';
                        }
                    });

                //添加节点
                var circles = appendNodes.append('circle')
                    //设置圆点的半径，圆点的度越大weight属性值越大，可以对其做一点数学变换
                    .attr('r', function () {
                        return options.r;
                    });
                //添加节点title
                appendNodes.append('title')
                    .html(function (d) {
                        return self.getNodeTitle(d);
                    });
                //添加缩略文字
                if (options.showAbbr) {
                    appendNodes.append('text')

                        .attr('dy', '.4em')
                        .text(function (d) {

                            return self.getAbbr(d.label);
                        });
                }
            };

            //根据节点获取包含 x y数组的对象
            function getXY(nodes) {
                var length = nodes.length,
                    x = [],
                    y = [];
                for (var i = 0; i < length; i++) {
                    x.push(nodes[i].x);
                    y.push(nodes[i].y);
                }
                return {
                    x: x,
                    y: y
                };
            }

            //获取节点显示名称
            this.getNodeTitle = function (d) {
                return d.name || '';
            };

            //针对于 c++ 分析使用
            this.getAbbr = function (label) {
                var self = this,
                    options = self.options;
                if (typeof label === 'undefined') {
                    return options.labelList['Undefined'].abbr;
                } else {
                    return options.labelList[label].abbr;
                }
            };

            this.scale = function (newZoom, isTick) {
                var self = this;
                if (isTick != null && !isTick) {
                    return false;
                }
                if (self.currentZoom !== newZoom) {
                    var zoomRatio = newZoom / self.currentZoom,

                    //得到鼠标当前相对于画布的位置
                     mouseX = self.mousemoveOffsetX - self.currentOffset.x,
                        mouseY = self.mousemoveOffsetY - self.currentOffset.y;

                    //算法是
                    //(mouseX/self.currentZoom)*(newZoom-self.currentZoom)

                    self.currentOffset.x = self.currentOffset.x - mouseX * (zoomRatio - 1);
                    self.currentOffset.y = self.currentOffset.y - mouseY * (zoomRatio - 1);

                    self.setSvgOffset(self.currentOffset.x, self.currentOffset.y);
                    self.currentZoom = newZoom;
                    self.zoom && self.zoom.scale(newZoom);
                    self.tick(1);
                }
                return self;
            };

            this.tick = function (e, mode) {
                var self = this;
                for (var name in self.forceNodes) {
                    if (self.forceNodes.hasOwnProperty(name)) {
                        var obj = self.forceNodes[name];
                        obj.x = obj.positionConstraints.x;
                        obj.y = obj.positionConstraints.y;
                    }
                }
                var currentZoom = self.currentZoom;

                self.links && self.links.selectAll('line').attr('x1', function (d) {
                    return currentZoom * d.source.x;
                })
                    .attr('y1', function (d) {
                        return currentZoom * d.source.y;
                    })
                    .attr('x2', function (d) {
                        return currentZoom * d.target.x;
                    })
                    .attr('y2', function (d) {
                        return currentZoom * d.target.y;
                    });
                //更新位置
                self.nodes && self.nodes.selectAll('circle').attr('r', function (d) {
                    if (d.mouseover || self.draged || d.selected) {
                        return d3.select(this).attr('r');
                    }
                    return self.getR(currentZoom);
                });
                self.nodes && self.nodes.attr('transform', function (d) {
                    return 'translate(' + floatCalculation.mul(currentZoom, d.x)
                        + ',' + floatCalculation.mul(currentZoom, d.y) + ')';
                });

                var r = self.getR(currentZoom) * 2;
                //更新矩形框位置

                self.rects && self.rects.selectAll('rect')
                    .attr('width', function (d) {
                        $(this).data('position', getRectPosition(d.nodes));
                        return currentZoom * $(this).data('position').width + r + r + 15;
                    })
                    .attr('height', function () {
                        return currentZoom * $(this).data('position').height + r + r + 15;
                    })
                    .attr('x', function () {
                        var x = currentZoom * $(this).data('position').x - r - 15;
                        $(this).next('text').attr('x', x);
                        return x;
                    })
                    .attr('y', function () {
                        var y = currentZoom * $(this).data('position').y - r - 15;
                        $(this).next('text').attr('y', y);
                        return y;
                    });

                //除了初始化，其他tick事件都会触发grapgchange事件
                if (mode !== 'init') {
                    self.$element.trigger('graphchange');
                }
                return self;
            };

            //根据当前的缩放比得到 R
            this.getR = function (currentZoom) {
                var self = this,
                    options = self.options,
                    _r = options.r * currentZoom;
                if (_r < options.minR) {
                    return options.minR;
                } else if (_r > options.maxR) {
                    return options.maxR;
                }
                return options.r * currentZoom;
            };

            //绘制矩形框
            this.initRect = function () {
                var self = this,
                    groupNodes = self.getGroupedNodes(self.forceNodes);

                //缓存分组后的nodes
                self.groupNodes = groupNodes || [];

                //声明 rect 拖动
                self.rectDrag = d3.behavior.drag().on('dragstart', function () {

                    //屏蔽掉右键拖动
                    if (d3.event.sourceEvent && d3.event.sourceEvent.which === 3) {
                        return false;
                    }
                    d3.event.sourceEvent.stopPropagation();
                }).on('drag', function (d) {
                    var length, nodes = d.nodes;
                    length = nodes.length;
                    if (nodes && length) {
                        for (var i = 0; i < length; i++) {
                            nodes[i].positionConstraints.x += d3.event.dx / self.currentZoom;
                            nodes[i].positionConstraints.y += d3.event.dy / self.currentZoom;
                        }

                        if (d3.event.dx) {
                            self.tick(1);
                        }
                    }
                }).on('dragend', function () {

                });

                self._createRect();
            };

            //将节点按照 group 属性进行分组
            this.getGroupedNodes = function () {
            };


            //绘制矩形框
            this._createRect = function () {
            };

            this.highlightById = function (id) {
                var self = this,
                    node;
                if (id == null) {
                    return;
                }
                node = self.getNodeById(id);
                if (node) {
                    self.highlight(node);
                }
            };

            //高亮节点
            this.highlight = function (node) {
                var self = this,
                    _nodes = [node];

                self.links && self.links.classed('inactive', function (d) {
                    if (node !== d.source && node !== d.target) {
                        return true;
                    } else {
                        if (node === d.source) {
                            _nodes.push(d.target);
                        }
                        if (node === d.target) {
                            _nodes.push(d.source);
                        }
                        return false;
                    }
                });
                self.nodes && self.nodes.classed('inactive', function (d) {
                    return _nodes.indexOf(d) === -1 ? true : false;
                });
                return self;
            };

            this.dehighlight = function () {
                var self = this;
                self.nodes && self.nodes.classed('inactive', false);
                self.links && self.links.classed('inactive', false);
                return self;
            };
            this.setCursor = function (cursor) {
                var self = this;
                self.$element.css('cursor', cursor);
                return self;
            };
            //取消选择所有节点
            this.unselectAll = function () {
                var self = this;
                self.svgContainer.selectAll('.selected').classed('selected', false);
                self.selectedNodes = {};
            };
            this.selectNode = function (d) {
                if (!d) {
                    return;
                }
                var self = this,
                    d3_this = self.$element.find('#node_' + d.id)[0];

                d3.select(d3_this).classed('selected', true);

                self.selectedNodes[d.id] = d;
            };

            this.getColorsByView = function () {
                var self = this,
                    options = self.options;
                $http.get('d3-module/color.json').success(function (data) {
                    if (data && data.status === 200) {
                        options.colors = data.data.json.colors;
                    } else {
                        alert('Server error ,please refresh the page.');
                    }
                });
            };

            /**
             * 按照圆心居中
             */
            this.centerCircle = function () {
                var self = this,
                    nodes = self.forceNodes,
                    _countOffset = { 'x': 0, 'y': 0 };

                nodes.forEach(function (node) {
                    var offset = self.getCenterOffset(node);
                    _countOffset.x += offset.x;
                    _countOffset.y += offset.y;
                });
                if (!!nodes.length) {
                    var _averageOffset = {
                        'x': _countOffset.x / nodes.length,
                        'y': _countOffset.y / nodes.length
                    };
                    self.setSvgOffset(_averageOffset.x, _averageOffset.y, true);
                    self.currentOffset.x = _averageOffset.x;
                    self.currentOffset.y = _averageOffset.y;
                }
                return self;
            };


            //获取需要居中的 offset
            this.getCenterOffset = function (node) {
                var self = this,
                    elementWidth = self.$element.width(),
                    elementHeight = self.$element.height();
                return {
                    x: self.currentOffset.x
                        + (elementWidth / 2 - (node.positionConstraints.x * self.currentZoom + self.currentOffset.x)),
                    y: self.currentOffset.y
                        + (elementHeight / 2 - (node.positionConstraints.y * self.currentZoom + self.currentOffset.y))
                };
            };

            //重新刷新
            this.refresh = function (url, callback) {
                var self = this,
                    options = self.options;
                if (!url) {
                    url = this.options.url;
                }
                self.getData(url, function (data) {
                    //后台集成
                    self._dataset = self.formatData(data.data);
                    self.dataset = data.data;
                    self.empty();

                    //针对java dependency的view初始化节点布局
                        self.force.on('tick', self._initTick);
                        self.force.on('end', function () {
                            for (var name in self.forceNodes) {
                                if (self.forceNodes.hasOwnProperty(name)) {
                                    var obj = self.forceNodes[name];
                                    obj.positionConstraints.x = obj.x;
                                    obj.positionConstraints.y = obj.y;
                                }
                            }
                            self.force.on('tick', null);
                            self.force.on('end', null);
                            self.scale(1);
                            self.centerCircle();
                        });

                        // self.force.links方法会改变self._dataset.links数组结构影响原来渲染逻辑,此处做了次深拷贝
                        var _deeplinks = [];
                        $.extend(true, _deeplinks, self._dataset.links);

                        //将当前source和target匹配的id换算成nodes中的索引值
                        angular.forEach(_deeplinks, function (link, l, links) {
                            var _source = link.source,
                                _target = link.target;
                            angular.forEach(self._dataset.nodes, function (node, n, nodes) {
                                if (node.id === _source) {
                                    link.source = n;
                                }
                                if (node.id === _target) {
                                    link.target = n;
                                }
                            });
                        });

                        self.force
                            .nodes(self._dataset.nodes)
                            .links(_deeplinks)
                            .start();
                    self._getAllLableAndAbbr(self._dataset.nodes);
                    self._createColorMark();
                    //添加nodes与links
                    self.addNodes(self._dataset.nodes);
                    self.addLinks(self._dataset.links);

                    //更新节点
                    self.update();
                    self.unselectAll();
                    callback && callback.call(self);
                    options.refresh && $.isFunction(options.refresh) && options.refresh.call(self);
                    self.centerCircle();
                });
                return self;
            };

            this._getAllLableAndAbbr = function (nodes) {
                var self = this,
                    options = self.options;
                options.labelList = [];
                angular.forEach(nodes, function (node) {
                    if (typeof node.label !== 'undefined') {
                        options.labelList[node.label] = {
                            name: node.label,
                            abbr: node.label[0].toUpperCase(),
                            color: ''
                        };
                    } else {
                        options.labelList['Undefined'] = {
                            name: 'Undefined',
                            abbr: 'U',
                            color: ''
                        };
                    }
                });
                function GetRandomNum(Min,Max){
                    var Range = Max - Min;
                    var Rand = Math.random();
                    return(Min + Math.round(Rand * Range));
                }
                var index = GetRandomNum(0,16);
                for (var name in options.labelList) {
                    options.labelList[name].color = options.colors[index];
                    options.labelList[name].name =
                        options.labelList[name].name + ' (' + options.labelList[name].abbr + ')';
                    index++;
                }
            };

            //清空图
            this.empty = function () {
                var self = this;

                self.forceNodes = [];
                self.forceLinks = [];

                self.links = self.svgContainer.selectAll('.link').data(self.forceLinks, function (d) {
                    return d.source.id + '-' + d.target.id;
                });
                self.links.exit().remove();

                self.nodes = self.svgContainer.selectAll('.node').data(self.forceNodes, function (d) {
                    return d.id;
                });
                self.nodes.exit().remove();
                return self;
            };

            this._initTick = function () {
                var _links = d3.select('#d3graph').selectAll('.link line'),
                    _nodes = d3.select('#d3graph').selectAll('.node');
                _links.attr('x1', function (d) {
                    return d.source.x;
                })
                    .attr('y1', function (d) {
                        return d.source.y;
                    })
                    .attr('x2', function (d) {
                        return d.target.x;
                    })
                    .attr('y2', function (d) {
                        return d.target.y;
                    });
                _nodes.attr('transform', function (d) {
                    return 'translate(' + d.x + ',' + d.y + ')';
                });
            };

        }).call(D3force.prototype)
        return D3force;
    })
