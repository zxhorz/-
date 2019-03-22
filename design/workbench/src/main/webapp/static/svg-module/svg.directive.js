'use strict';

angular.module('svgModule').directive('svgDirective', function ($window) {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'svg-module/svg.template.html',
        replace: false,
        controller: svgController,
        link: svgLink
    };
    function svgController($scope, $http, $location, $attrs, $rootScope, $timeout) {
        var outputpath = '';
        $scope.svgList = [];
        $scope.currentTitle4 = '';
        $scope.left = '';
        $scope.right = '';
        $scope.programinfo = {};
        $scope.cloecode = { 'program': 0, 'paragraph': 0 };
        $scope.cloneList = [];
        if ($rootScope.currentCase && $rootScope.configList) {
            outputpath = $rootScope.configList[$rootScope.currentCase].outputpath;
        }
        $scope.compareTable = {
            enableFiltering: false,
            //是否显示grid 菜单
            enableGridMenu: false,
            //是否点击行任意位置后选中,默认为false,当为true时，checkbox可以显示但是不可选中
            enableFullRowSelection: true,
            //默认false,选中后是否可以取消选中
            noUnselect: false,
            //是否可以选择多个,默认为true
            multiSelect: false,
            //禁止排序
            enableSorting: false,
            enableRowHeaderSelection: false,
            //grid垂直滚动条是否显示, 0-不显示  1-显示
            enableVerticalScrollbar: 1,
            enableHorizontalScrollbar: 1,
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                $scope.gridResize = $timeout(function () { $scope.gridApi.core.handleWindowResize(); }, 500);
                gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                    row.isSelected = true;
                    $scope.columns = [];
                    $scope.svgUrl = '';
                    var cl = row.grid.columns.length;
                    for (var l = 0; l < cl; l++) {
                        var cn = row.grid.columns[l].name;
                        var ss = row.entity[cn];
                        if (row.entity[cn] !== "null" && row.entity[cn] !== "") {
                            $scope.columns.push({ "name": row.grid.columns[l].name, "rowValue": row.entity[cn] });
                        }
                    }
                    console.log($scope.columns);
                    //选中行第一列的内容
                    var rowName1 = row.grid.rows[0];
                    var parname = row.entity;
                    $scope.parname = parname;
                    angular.forEach(parname, function (data) {
                        console.log(data);
                    })
                    var codepath = '';
                    if ($rootScope.configList && $rootScope.currentCase) {
                        codepath = $rootScope.configList[$rootScope.currentCase].codepath;
                    }
                    var w = angular.element(".compareBox").find(".ui-grid-header-cell").width();
                    var w1 = angular.element(".compareBox").width();
                    var w2 = w1 - w;
                    var w3 = (w2 / w1) * 100;
                    angular.element(".compare").css("width", w3 + "%");
                    $scope.showcompare = true;
                    var firstCol = row.grid.columns[0].name;
                    $scope.getBaseSouce(firstCol, row.entity[firstCol], false);
                });
            },
            //滚动条事件
            customScroller: function myScrolling(uiGridViewport, scrollHandler) {
                uiGridViewport.on('scroll', function myScrollingOverride(event) {
                    // You should always pass the event to the callback since ui-grid needs it
                    scrollHandler(event);
                });
            }
        };

        $scope.getBaseSouce = function (program, paragraph, flag) {
            $http({
                method: 'POST',
                url: '/query/paragraphText',
                data: $.param({ 'pgmname': program, 'paragraph': paragraph }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                console.info(data);
                if (flag) {
                    $scope.right = data[0].content;
                } else {
                    $scope.left = data[0].content;
                    $scope.right = '';
                }
            }).error(function (data) {
                console.error(data);
            });
        }

        $scope.genCloneCode = function (program, flag) {
            if (flag) {
                program = $scope.prefix + program + '__' + program;
            }
            $http({
                method: 'POST',
                url: './analysis/clonecode',
                data: $.param({ 'program': program }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.length > 0) {
                    data = data.replace("/client", ".");
                    $scope.cloneList[program] = data;
                    if (flag) {
                        $scope.genTable(data);
                    } else {
                        $scope.calculateProgramclonecode(program);
                    }
                } else {
                    $rootScope.ui_grid_scatter = false;
                    $scope.similar = 0;
                }
            }).error(function () {
                $rootScope.ui_grid_sscatter = false;
                $scope.similar = 0;
            });
        };

        $scope.getScatterData = function (program) {
            if (program == '') {
                $scope.genTable('');
            } else {
                $scope.genCloneCode(program, true);
            }
        };
        $scope.diff_close = function () {
            $scope.showmodel = false;
            $('.first_list').find('.clicked').removeClass("clicked");
        }
        $scope.genTable = function (data) {
            $scope.showmodel = true;
            $(".ttlist").find(".clicked").removeClass(".clicked");
            // 得到$scope.similarParagraphName的源代码
            var inputPath = 'resource/clone_analysis/input/business_case/paragraphs';
            $http({
                method: 'POST',
                url: './project/cloneCodeAnalysis/getParaCodeForDiffOne',
                data: $.param({ 'paraName': $scope.similarParagraphName, 'inputPath': inputPath }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                $scope.left = data[0];
                $scope.right = null;
            }).error(function (data) {
                console.log('error');
            });
        }
        //diff select
        $scope.diff_select = function (event, lname) {
            var tli = $(event.target);
            tli.addClass('clicked').siblings().removeClass('clicked');
            tli.parents('li').siblings().find('li').removeClass('clicked');
            //lname选中的段名
            // 得到第二段的源代码，并进行diff，返回diff结果
            var inputPath = 'resource/clone_analysis/input/business_case/paragraphs';
            $http({
                method: 'POST',
                url: './project/cloneCodeAnalysis/getParaCodeForDiffOne',
                data: $.param({ 'paraName': lname, 'inputPath': inputPath }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                $scope.right = data[0];
                $scope.name2 = lname;
                var configFile = 'resource/clone_analysis/config/business_case/arguments.properties';
                $http({
                    method: 'POST',
                    url: './project/cloneCodeAnalysis/diff',
                    data: $.param({
                        'action': 'diff', 'leftParaName': $scope.similarParagraphName, 'rightParaName': $scope.name2, 'configFile': configFile
                    }),
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                }).success(function (data) {
                    //$scope.diffResult值为0-4，0表示非clone code,1是Tier-1,2是Tier-2,3是Tier-3,4是Tier-4
                    if (data === '0') {
                        $scope.diffResult = 'None Clone';
                    } else {
                        $scope.diffResult = 'Tier-' + data;
                    }
                }).error(function (data) {
                    console.log('error');
                });
            }).error(function (data) {
                console.log('error');
            });
        }

        $scope.calculateProgramclonecode = function (program) {
            var path = $scope.cloneList[program];
            $http.get(path).success(function (response) {
                var count = 0, pcount = 0;
                for (var key in response[0]) {
                    count++;
                }
                $scope.cloecode['program'] = count;
                $scope.similar = count;
            }).error(function () {
                $scope.similar = 0;
            })
        }

        $scope.calculateParagraphclonecode = function (name) {
            var cloneSummary = 'resource/clone_analysis/output/clone/paragraph_summary.csv';
            var paragraphName = name.substr(name.lastIndexOf('/') + 1);
            $http({
                method: 'POST',
                url: './project/cloneCodeAnalysis/getCloneParagraphs',
                data: $.param({ 'cloneSummary': cloneSummary, 'paragraphName': paragraphName }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data === null || data === '') {
                    $scope.paragraphsimilar = 0;
                } else {
                    $scope.similarParagraphName = paragraphName;
                    $scope.paragraphsimilar = data.simliarParagraphCount;
                    $scope.similarParagraphsInfo = data;
                }
            }).error(function () {
                $scope.paragraphsimilar = 0;
            })
        }


        //广播事件，最大化,最小化时修改表格大小
        $scope.$on('changeTable', function (event, type, data) {
            if (type === 'ui_grid_detail') {
                //设置svg元素的class
                $scope.svgclass = data;
            }
        });

        $scope.filter = function () {
            $scope.gridApi.grid.refresh();
        };
        $scope.singleFilter = function (renderableRows) {
            var matcher = new RegExp($scope.filterValue);
            renderableRows.forEach(function (row) {
                var match = false;
                ['Code Impact Analaysis Summary'].forEach(function (field) {
                    if (row.entity[field] && row.entity[field].match(matcher)) {
                        match = true;
                    }
                });
                if (!match) {
                    row.visible = false;
                }
            });
            return renderableRows;
        };

        // $scope.left = ['I am the very model of a modern Major-General,',
        //     'I\'ve information vegetable, animal, and mineral,',
        //     'I know the kings of England, and I quote the fights historical,',
        //     'From Marathon to Waterloo, in order categorical.'
        // ].join('\n');

        // $scope.right = ['I am the very model of a cartoon individual,',
        //     'My animation\'s comical, unusual, and whimsical,',
        //     'I know the kings of England, and I quote the fights historical,',
        //     'From wicked puns and stupid jokes to anvils that drop on your head.'
        // ].join('\n');

        $scope.options = {
            editCost: 4,
            attrs: {
                insert: {
                    'data-attr': 'insert',
                    'class': 'insertion'
                },
                delete: {
                    'data-attr': 'delete'
                },
                equal: {
                    'data-attr': 'equal'
                }
            }
        };
        // 读取d3数据文件夹下business flow文件名列表
        // $http({
        //     method: "GET",
        //     url: "/codesearch/getBusinessFlowFileNameList"
        // }).success(function (result) {
        //     // TODO svg展示改为d3展示
        //     $scope.svgoptions = result;
        //     $rootScope.selectedName = result[0];
        //     $scope.d3url = "../d3-file/businessFlow/" + result[0] + ".json";
        // })

        $scope.svgclick = function (e, t) {
            $scope.similarParagraphName = '';
            $scope.name2 = '';
            $scope.diffResult = '';
            $scope.left = '';
            $scope.right = '';
            var f = angular.isString(e);
            var dataText;
            $scope.iftext=false;
            if (f) {
                dataText = e;
            } else {
                // dataText = $(e.target).parent("g").find("title").text();
                if (e.target.innerHTML !== null && e.target.innerHTML !== '') {
                    dataText = e.target.innerHTML;
                } else {
                    var text = $(e.target).parent().find("text").text()
                    if (text !== null && text !== '') {
                        dataText = text;
                    } else {
                        dataText = $(e.target).parent("g").find("title").text();
                    }
                }
            }
            console.log(dataText);
            //测试用
            // dataText = 'BatchProgram__MIPCLM85__MIPCLM85';
            var order = t;
            // t==3即program call图现在为d3展示，点击事件由d3处理
            if (t == 3) {
                // 当前选中的program，用于生成controlflow svg图时拼接program.paragraph全名
                $scope.svgurl2="";
                $scope.currentProgarm = dataText.substr(dataText.lastIndexOf("__") + 2);
                $scope.prefix = dataText.substr(0, dataText.indexOf($scope.currentProgarm));
                //dataText = 'BatchProgram__MIPCLM85__MIPCLM85';
                var path = "svg-file/paragraphPerformSvg/" + dataText + ".svg";
                $scope.png=true;
                // 判断本次d3点击，对应perform图是否已生成过
                $http.get(path).success(function (response) {
                    $scope.svgurl2 = path;
                }).error(function (data, status, headers, config) {
                    // svg不存在，找png
                    path = "svg-file/paragraphPerformSvg/" + dataText + ".png";
                    $scope.png=false;
                    $http.get(path).success(function (response) {
                        $scope.svgurl2 = path;
                    }).error(function (data, status, headers, config) {
                        // png不存在，后台请求调用jar包生成
                        $http({
                            method: 'POST',
                            url: './svg/generatePerform',
                            data: $.param({ 'programName': dataText }),
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                        }).success(function (data) {
                            if (data) {
                                $scope.svgurl2 = data;
                            }
                        }).error(function () {
                            $scope.svgurl2 = '';
                        });
                    });
                });
                // $scope.svgurl2 = "svg-file/paragraphPerformSvg/" + dataText + ".svg";
                $scope.second = true;
                $scope.three = false;
                angular.element(".btfirst").removeClass("one").addClass("two");
                angular.element(".btsecond").addClass('two')
                var tw = angular.element(".two").width();
                angular.element(".btfirst").find("svg").attr("width", tw);
                // $scope.extractProgramInfo("BatchProgram__IPDD0008__IPDD0008");
                $scope.program = dataText;
                $scope.extractProgramInfo(dataText);
                $scope.genCloneCode(dataText);
                $scope.d3width = tw;
            } else if (t == 2) {
                dataText = $scope.currentProgarm + '.' + dataText;
                $scope.svgurl3="";
                var path = "svg-file/paragraphControlflowSvg/" + dataText + ".svg";
                // 判断本次svg点击，对应control flow图是否已生成过
                $http.get(path).success(function (response) {
                    $scope.svgurl3 = path;
                }).error(function (data, status, headers, config) {
                    // svg不存在，后台请求调用jar包生成
                    $http({
                        method: 'POST',
                        url: './svg/generateControlflow',
                        data: $.param({ 'paragraphName': dataText }),
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                    }).success(function (data) {
                        if (data) {
                            $scope.svgurl3 = data;
                        }
                    }).error(function () {
                        $scope.svgurl3 = '';
                    });
                });
                var programName = getProgramName($scope.currentProgarm);
                var paragraphName = getParagraphName(programName, dataText);
                $scope.extractParagraphInfo($scope.program + paragraphName);
                angular.element(".btthree").siblings().removeClass("two")
                $scope.three = true;
            }
        }
        $scope.d3textClick=function(text){
                $scope.second = true;
                $scope.three = false;
                $scope.iftext=true;
                $http({
                        method: 'GET',
                        url: './text-file/'+text+'.txt'
                    }).success(function (data) {
                        if (data) {
                            angular.element(".programtext").html(data) ;
                        }
                    }).error(function () {
                        angular.element(".programtext").html('') ;
                    });
                angular.element(".btfirst").removeClass("one").addClass("two");
                angular.element(".btsecond").addClass('two')
                var tw = angular.element(".two").width();
                angular.element(".btfirst").find("svg").attr("width", tw);
        }
        $scope.extractProgramInfo = function (id) {
            $http({
                method: 'POST',
                url: '/query/programinfo',
                data: $.param({ 'pgmname': id }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.info.length > 0) {
                    $scope.programName = data.info[0].name ? data.info[0].name : "NA";
                    $scope.description = data.info[0].description ? data.info[0].description : "NA";
                } else {
                    $scope.programName = "NA";
                    $scope.description = "NA";
                }
                $scope.businessFlow = $rootScope.selectedName ? $rootScope.selectedName : "NA";
                $scope.file = data.file ? removedup(data.file) : [{ 'name': 'NA' }];
                $scope.table = data.table ? removedup(data.table) : [{ 'name': 'NA' }];
                console.info(data);
            }).error(function () {
                $scope.programName = "NA";
                $scope.businessFlow = "NA";
                $scope.description = "NA";
                $scope.file = [{ 'name': 'NA' }];
                $scope.table = [{ 'name': 'NA' }];
            });
        }

        $scope.extractParagraphInfo = function (name) {
            name = name.replace(/__/g, '/').replace(/_/g, '-');
            $http({
                method: 'POST',
                url: '/query/paragrahinfo',
                data: $.param({ 'paragraph': name }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.info.length > 0) {
                    $scope.paragraphName = data.info[0].name ? data.info[0].name : 'NA';
                    $scope.prediction = data.info[0].prediction ? data.info[0].prediction : 'NA';
                    $scope.paragraphTag = data.info[0].tag ? data.info[0].tag : 'NA';
                    $scope.calculateParagraphclonecode(name);
                } else {
                    $scope.paragraphName = 'NA';
                    $scope.prediction = 'NA';
                    $scope.paragraphTag = 'NA';
                    $scope.paragraphsimilar = 0;
                }
            }).error(function () {
                $scope.paragraphName = 'NA';
                $scope.prediction = 'NA';
                $scope.paragraphTag = 'NA';
                $scope.paragraphsimilar = 0;
            });
        }
    }

    function removedup(list) {
        var result = [];
        var dup = {};
        if (list.length == 0) {
            return [{ 'name': 'NA' }];
        }
        for (var i = 0; i < list.length; i++) {
            if (typeof dup[list[i].name] === 'undefined') {
                dup[list[i].name] = 1;
                result.push(list[i]);
            }
        }
        return result;
    }

    function getProgramName(name) {
        var program;
        var pos = name.lastIndexOf('_');
        return pos > -1 ? name.substring(pos + 1) : name;
    }

    function getParagraphName(pgmname, name) {
        var prparagraph;
        var prparagraph = name.replace(pgmname, '');
        if (prparagraph.substring(0, 1) === '_') {
            prparagraph = prparagraph.substring(1)
        }
        return prparagraph.replace(/_/g, '-');
    }

    function svgLink($scope, $attrs) {
        $scope.width = $window.innerWidth;
        angular.element($window).bind('resize', function () {
            $scope.width = $window.innerWidth;
            $scope.$digest();
        });

        $scope.link = function (e, l) {
            if (l == 'paragraph') {
                if ($scope.paragraphsimilar == 0) {
                    return;
                }
            } else if (l == 'program') {
                if ($scope.similar == 0) {
                    return;
                }
            }
            $scope.getScatterData('');
        }

        $scope.link2 = function () {
            // $scope.showcompare=$scope.showcompare==false?true:false;
            if ($scope.codeText) {
                $scope.compareTable.columnDefs = [];
                $scope.getScatterData($scope.codeText);
                $scope.showcompare = false;
                $scope.codeText = "";
            }
        }
        $scope.close = function () {
            $scope.showmodel = false;
        }
        $scope.close2 = function () {
            $scope.showcompare = false;
        }

        $scope.copli = function (e) {
            var program = $(e.target).attr("data-c");
            var paragraph = e.target.textContent;
            $scope.getBaseSouce(program, paragraph, true);
        }

    }
}).directive('svgDirective2', function ($window) {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'svg-module/svgtemplate.html',
        replace: false,
        controller: svgController,
        link: svgLink
    };
    function svgController($scope, $http, $location, $attrs, $rootScope, $timeout) {
        var outputpath = '';
        $scope.svgList = [];
        $scope.currentTitle4 = '';
        $scope.left = '';
        $scope.right = '';
        $scope.programinfo = {};
        $scope.cloecode = { 'program': 0, 'paragraph': 0 };
        $scope.cloneList = [];
        if ($rootScope.currentCase && $rootScope.configList) {
            outputpath = $rootScope.configList[$rootScope.currentCase].outputpath;
        }
        $scope.compareTable = {
            enableFiltering: false,
            //是否显示grid 菜单
            enableGridMenu: false,
            //是否点击行任意位置后选中,默认为false,当为true时，checkbox可以显示但是不可选中
            enableFullRowSelection: true,
            //默认false,选中后是否可以取消选中
            noUnselect: false,
            //是否可以选择多个,默认为true
            multiSelect: false,
            //禁止排序
            enableSorting: false,
            enableRowHeaderSelection: false,
            //grid垂直滚动条是否显示, 0-不显示  1-显示
            enableVerticalScrollbar: 1,
            enableHorizontalScrollbar: 1,
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                $scope.gridResize = $timeout(function () { $scope.gridApi.core.handleWindowResize(); }, 500);
                gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                    row.isSelected = true;
                    $scope.columns = [];
                    $scope.svgUrl = '';
                    var cl = row.grid.columns.length;
                    for (var l = 0; l < cl; l++) {
                        var cn = row.grid.columns[l].name;
                        var ss = row.entity[cn];
                        if (row.entity[cn] !== "null" && row.entity[cn] !== "") {
                            $scope.columns.push({ "name": row.grid.columns[l].name, "rowValue": row.entity[cn] });
                        }
                    }
                    console.log($scope.columns);
                    //选中行第一列的内容
                    var rowName1 = row.grid.rows[0];
                    var parname = row.entity;
                    $scope.parname = parname;
                    angular.forEach(parname, function (data) {
                        console.log(data);
                    })
                    var codepath = '';
                    if ($rootScope.configList && $rootScope.currentCase) {
                        codepath = $rootScope.configList[$rootScope.currentCase].codepath;
                    }
                    var w = angular.element(".compareBox").find(".ui-grid-header-cell").width();
                    var w1 = angular.element(".compareBox").width();
                    var w2 = w1 - w;
                    var w3 = (w2 / w1) * 100;
                    angular.element(".compare").css("width", w3 + "%");
                    $scope.showcompare = true;
                    var firstCol = row.grid.columns[0].name;
                    $scope.getBaseSouce(firstCol, row.entity[firstCol], false);
                });
            },
            //滚动条事件
            customScroller: function myScrolling(uiGridViewport, scrollHandler) {
                uiGridViewport.on('scroll', function myScrollingOverride(event) {
                    // You should always pass the event to the callback since ui-grid needs it
                    scrollHandler(event);
                });
            }
        };

        $scope.getBaseSouce = function (program, paragraph, flag) {
            $http({
                method: 'POST',
                url: '/query/paragraphText',
                data: $.param({ 'pgmname': program, 'paragraph': paragraph }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                console.info(data);
                if (flag) {
                    $scope.right = data[0].content;
                } else {
                    $scope.left = data[0].content;
                    $scope.right = '';
                }
            }).error(function (data) {
                console.error(data);
            });
        }

        $scope.genCloneCode = function (program, flag) {
            if (flag) {
                program = $scope.prefix + program + '__' + program;
            }
            $http({
                method: 'POST',
                url: './analysis/clonecode',
                data: $.param({ 'program': program }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.length > 0) {
                    data = data.replace("/client", ".");
                    $scope.cloneList[program] = data;
                    if (flag) {
                        $scope.genTable(data);
                    } else {
                        $scope.calculateProgramclonecode(program);
                    }
                } else {
                    $rootScope.ui_grid_scatter = false;
                    $scope.similar = 0;
                }
            }).error(function () {
                $rootScope.ui_grid_sscatter = false;
                $scope.similar = 0;
            });
        };

        $scope.getScatterData = function (program) {
            $scope.compareTable.columnDefs = [];
            if (program == '') {
                $scope.genTable('');
            } else {
                $scope.genCloneCode(program, true);
            }
        };

        $scope.diff_close = function () {
            $scope.showmodel = false;
        }
        $scope.genTable = function (data) {
            $scope.showmodel = true;
            // 得到$scope.similarParagraphName的源代码
            var inputPath = 'resource/clone_analysis/input/business_case/paragraphs';
            $http({
                method: 'POST',
                url: './project/cloneCodeAnalysis/getParaCodeForDiffOne',
                data: $.param({ 'paraName': $scope.similarParagraphName, 'inputPath': inputPath }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                $scope.left = data[0];
                $scope.right = null;
            }).error(function (data) {
                console.log('error');
            });
        }
        //diff select
        $scope.diff_select = function (event, lname) {
            var tli = $(event.target);
            tli.addClass('clicked').siblings().removeClass('clicked');
            tli.parents('li').siblings().find('li').removeClass('clicked');
            //lname选中的段名
            // 得到第二段的源代码，并进行diff，返回diff结果
            var inputPath = 'resource/clone_analysis/input/business_case/paragraphs';
            $http({
                method: 'POST',
                url: './project/cloneCodeAnalysis/getParaCodeForDiffOne',
                data: $.param({ 'paraName': lname, 'inputPath': inputPath }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                $scope.right = data[0];
                $scope.name2 = lname;
                var configFile = 'resource/clone_analysis/config/business_case/arguments.properties';
                $http({
                    method: 'POST',
                    url: './project/cloneCodeAnalysis/diff',
                    data: $.param({
                        'action': 'diff', 'leftParaName': $scope.similarParagraphName, 'rightParaName': $scope.name2, 'configFile': configFile
                    }),
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                }).success(function (data) {
                    //$scope.diffResult值为0-4，0表示非clone code,1是Tier-1,2是Tier-2,3是Tier-3,4是Tier-4
                    if (data === '0') {
                        $scope.diffResult = 'None Clone';
                    } else {
                        $scope.diffResult = 'Tier-' + data;
                    }
                }).error(function (data) {
                    console.log('error');
                });
            }).error(function (data) {
                console.log('error');
            });
        }

        $scope.calculateProgramclonecode = function (program) {
            var path = $scope.cloneList[program];
            $http.get(path).success(function (response) {
                var count = 0, pcount = 0;
                for (var key in response[0]) {
                    count++;
                }
                $scope.cloecode['program'] = count;
                $scope.similar = count;
            }).error(function () {
                $scope.similar = 0;
            })
        }

        $scope.calculateParagraphclonecode = function (name) {
            var cloneSummary = 'resource/clone_analysis/output/clone/paragraph_summary.csv';
            var paragraphName = name.substr(name.lastIndexOf('/') + 1);
            $http({
                method: 'POST',
                url: './project/cloneCodeAnalysis/getCloneParagraphs',
                data: $.param({ 'cloneSummary': cloneSummary, 'paragraphName': paragraphName }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data === null || data === '') {
                    $scope.paragraphsimilar = 0;
                } else {
                    $scope.similarParagraphName = paragraphName;
                    $scope.paragraphsimilar = data.simliarParagraphCount;
                    $scope.similarParagraphsInfo = data;
                }
            }).error(function () {
                $scope.paragraphsimilar = 0;
            })
        }

        //广播事件，最大化,最小化时修改表格大小
        $scope.$on('changeTable', function (event, type, data) {
            if (type === 'ui_grid_detail') {
                //设置svg元素的class
                $scope.svgclass = data;
            }
        });

        $scope.filter = function () {
            $scope.gridApi.grid.refresh();
        };
        $scope.singleFilter = function (renderableRows) {
            var matcher = new RegExp($scope.filterValue);
            renderableRows.forEach(function (row) {
                var match = false;
                ['Code Impact Analaysis Summary'].forEach(function (field) {
                    if (row.entity[field] && row.entity[field].match(matcher)) {
                        match = true;
                    }
                });
                if (!match) {
                    row.visible = false;
                }
            });
            return renderableRows;
        };

        // $scope.left = ['I am the very model of a modern Major-General,',
        //     'I\'ve information vegetable, animal, and mineral,',
        //     'I know the kings of England, and I quote the fights historical,',
        //     'From Marathon to Waterloo, in order categorical.'
        // ].join('\n');

        // $scope.right = ['I am the very model of a cartoon individual,',
        //     'My animation\'s comical, unusual, and whimsical,',
        //     'I know the kings of England, and I quote the fights historical,',
        //     'From wicked puns and stupid jokes to anvils that drop on your head.'
        // ].join('\n');

        $scope.options = {
            editCost: 4,
            attrs: {
                insert: {
                    'data-attr': 'insert',
                    'class': 'insertion'
                },
                delete: {
                    'data-attr': 'delete'
                },
                equal: {
                    'data-attr': 'equal'
                }
            }
        };
        // 读取d3数据文件夹下business flow文件名列表
        // $http({
        //     method: "GET",
        //     url: "/codesearch/getBusinessFlowFileNameList"
        // }).success(function (result) {
        //     // TODO svg展示改为d3展示
        //     $scope.svgoptions = result;
        //     $rootScope.selectedName = result[0];
        //     $scope.d3url = "../d3-file/businessFlow/" + result[0] + ".json";
        // })

        $scope.svgclick = function (e, t) {
            $scope.similarParagraphName = '';
            $scope.name2 = '';
            $scope.diffResult = '';
            $scope.left = '';
            $scope.right = '';
            var f = angular.isString(e);
            var dataText;
            $scope.iftext=false;
            if (f) {
                dataText = e;
            } else {
                // dataText = $(e.target).parent("g").find("title").text();
                if (e.target.innerHTML !== null && e.target.innerHTML !== '') {
                    dataText = e.target.innerHTML;
                } else {
                    var text = $(e.target).parent().find("text").text()
                    if (text !== null && text !== '') {
                        dataText = text;
                    } else {
                        dataText = $(e.target).parent("g").find("title").text();
                    }
                }
            }
            console.log(dataText);
            //测试用
            // dataText = 'BatchProgram__MIPCLM85__MIPCLM85';
            var order = t;
            // t==3即program call图现在为d3展示，点击事件由d3处理
            if (t == 3) {
                // 当前选中的program，用于生成controlflow svg图时拼接program.paragraph全名
                $scope.svgurl2='';
                $scope.currentProgarm = dataText.substr(dataText.lastIndexOf("__") + 2);
                $scope.prefix = dataText.substr(0, dataText.indexOf($scope.currentProgarm));
                //dataText = 'BatchProgram__MIPCLM85__MIPCLM85';
                var path = "svg-file/paragraphPerformSvg/" + dataText + ".svg";
                // 判断本次d3点击，对应perform图是否已生成过
                $http.get(path).success(function (response) {
                    $scope.svgurl2 = path;
                }).error(function (data, status, headers, config) {
                    // svg不存在，后台请求调用jar包生成
                    $http({
                        method: 'POST',
                        url: './svg/generatePerform',
                        data: $.param({ 'programName': dataText }),
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                    }).success(function (data) {
                        if (data) {
                            $scope.svgurl2 = data;
                        }
                    }).error(function () {
                        $scope.svgurl2 = '';
                    });
                });
                // $scope.svgurl2 = "svg-file/paragraphPerformSvg/" + dataText + ".svg";
                $scope.second = true;
                $scope.three = false;
                angular.element(".btfirst").removeClass("one").addClass("two");
                angular.element(".btsecond").addClass('two')
                var tw = angular.element(".two").width();
                angular.element(".btfirst").find("svg").attr("width", tw);
                // $scope.extractProgramInfo("BatchProgram__IPDD0008__IPDD0008");
                $scope.program = dataText;
                $scope.extractProgramInfo(dataText);
                $scope.genCloneCode(dataText);
                $scope.d3width = tw;
            } else if (t == 2) {
                $scope.svgurl3='';
                dataText = $scope.currentProgarm + '.' + dataText;
                var path = "svg-file/paragraphControlflowSvg/" + dataText + ".svg";
                // 判断本次svg点击，对应control flow图是否已生成过
                $http.get(path).success(function (response) {
                    $scope.svgurl3 = path;
                }).error(function (data, status, headers, config) {
                    // svg不存在，后台请求调用jar包生成
                    $http({
                        method: 'POST',
                        url: './svg/generateControlflow',
                        data: $.param({ 'paragraphName': dataText }),
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                    }).success(function (data) {
                        if (data) {
                            $scope.svgurl3 = data;
                        }
                    }).error(function () {
                        $scope.svgurl3 = '';
                    });
                });
                var programName = getProgramName($scope.currentProgarm);
                var paragraphName = getParagraphName(programName, dataText);
                $scope.extractParagraphInfo($scope.program + paragraphName);
                angular.element(".btthree").siblings().removeClass("two")
                $scope.three = true;
            }
        }
        $scope.d3textClick=function(text){
                $scope.second = true;
                $scope.three = false;
                $scope.iftext=true;
                angular.element(".btfirst").removeClass("one").addClass("two");
                angular.element(".btsecond").addClass('two')
                var tw = angular.element(".two").width();
                angular.element(".btfirst").find("svg").attr("width", tw);
        }
        $scope.extractProgramInfo = function (id) {
            $http({
                method: 'POST',
                url: '/query/programinfo',
                data: $.param({ 'pgmname': id }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.info.length > 0) {
                    $scope.programName = data.info[0].name ? data.info[0].name : "NA";
                    $scope.description = data.info[0].description ? data.info[0].description : "NA";
                } else {
                    $scope.programName = "NA";
                    $scope.description = "NA";
                }
                $scope.businessFlow = $rootScope.selectedName ? $rootScope.selectedName : "NA";
                $scope.file = data.file ? removedup(data.file) : [{ 'name': 'NA' }];
                $scope.table = data.table ? removedup(data.table) : [{ 'name': 'NA' }];
                console.info(data);
            }).error(function () {
                $scope.programName = "NA";
                $scope.businessFlow = "NA";
                $scope.description = "NA";
                $scope.file = [{ 'name': 'NA' }];
                $scope.table = [{ 'name': 'NA' }];
            });
        }

        $scope.extractParagraphInfo = function (name) {
            name = name.replace(/__/g, '/').replace(/_/g, '-');
            $http({
                method: 'POST',
                url: '/query/paragrahinfo',
                data: $.param({ 'paragraph': name }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.info.length > 0) {
                    $scope.paragraphName = data.info[0].name ? data.info[0].name : 'NA';
                    $scope.prediction = data.info[0].prediction ? data.info[0].prediction : 'NA';
                    $scope.paragraphTag = data.info[0].tag ? data.info[0].tag : 'NA';
                    $scope.calculateParagraphclonecode(name);
                } else {
                    $scope.paragraphName = 'NA';
                    $scope.prediction = 'NA';
                    $scope.paragraphTag = 'NA';
                    $scope.paragraphsimilar = 0;
                }
            }).error(function () {
                $scope.paragraphName = 'NA';
                $scope.prediction = 'NA';
                $scope.paragraphTag = 'NA';
                $scope.paragraphsimilar = 0;
            });
        }
    }

    function removedup(list) {
        var result = [];
        var dup = {};
        if (list.length == 0) {
            return [{ 'name': 'NA' }];
        }
        for (var i = 0; i < list.length; i++) {
            if (typeof dup[list[i].name] === 'undefined') {
                dup[list[i].name] = 1;
                result.push(list[i]);
            }
        }
        return result;
    }

    function getProgramName(name) {
        var program;
        var pos = name.lastIndexOf('_');
        return pos > -1 ? name.substring(pos + 1) : name;
    }

    function getParagraphName(pgmname, name) {
        var prparagraph;
        var prparagraph = name.replace(pgmname, '');
        if (prparagraph.substring(0, 1) === '_') {
            prparagraph = prparagraph.substring(1)
        }
        return prparagraph.replace(/_/g, '-');
    }

    function svgLink($scope, $attrs) {
        $scope.width = $window.innerWidth;
        angular.element($window).bind('resize', function () {
            $scope.width = $window.innerWidth;
            $scope.$digest();
        });

        $scope.link = function (e, l) {
            if (l == 'paragraph') {
                if ($scope.paragraphsimilar == 0) {
                    return;
                }
            } else if (l == 'program') {
                if ($scope.similar == 0) {
                    return;
                }
            }
            $scope.getScatterData('');
        }

        $scope.link2 = function () {
            // $scope.showcompare=$scope.showcompare==false?true:false;
            if ($scope.codeText) {
                $scope.compareTable.columnDefs = [];
                $scope.getScatterData($scope.codeText);
                $scope.showcompare = false;
                $scope.codeText = "";
            }
        }
        $scope.close = function () {
            $scope.showmodel = false;
        }
        $scope.close2 = function () {
            $scope.showcompare = false;
        }

        $scope.copli = function (e) {
            var program = $(e.target).attr("data-c");
            var paragraph = e.target.textContent;
            $scope.getBaseSouce(program, paragraph, true);
        }

    }
}).directive('svgDirective3', function ($window) {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'svg-module/svgtemplate2.html',
        replace: false,
        controller: svgController,
        link: svgLink
    };
    function svgController($scope, $http, $location, $attrs, $rootScope, $timeout) {
        var outputpath = '';
        $scope.svgList = [];
        $scope.currentTitle4 = '';
        $scope.left = '';
        $scope.right = '';
        $scope.programinfo = {};
        $scope.cloecode = { 'program': 0, 'paragraph': 0 };
        $scope.cloneList = [];
        if ($rootScope.currentCase && $rootScope.configList) {
            outputpath = $rootScope.configList[$rootScope.currentCase].outputpath;
        }
        $scope.compareTable = {
            enableFiltering: false,
            //是否显示grid 菜单
            enableGridMenu: false,
            //是否点击行任意位置后选中,默认为false,当为true时，checkbox可以显示但是不可选中
            enableFullRowSelection: true,
            //默认false,选中后是否可以取消选中
            noUnselect: false,
            //是否可以选择多个,默认为true
            multiSelect: false,
            //禁止排序
            enableSorting: false,
            enableRowHeaderSelection: false,
            //grid垂直滚动条是否显示, 0-不显示  1-显示
            enableVerticalScrollbar: 1,
            enableHorizontalScrollbar: 1,
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                $scope.gridResize = $timeout(function () { $scope.gridApi.core.handleWindowResize(); }, 500);
                gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                    row.isSelected = true;
                    $scope.columns = [];
                    $scope.svgUrl = '';
                    var cl = row.grid.columns.length;
                    for (var l = 0; l < cl; l++) {
                        var cn = row.grid.columns[l].name;
                        var ss = row.entity[cn];
                        if (row.entity[cn] !== "null" && row.entity[cn] !== "") {
                            $scope.columns.push({ "name": row.grid.columns[l].name, "rowValue": row.entity[cn] });
                        }
                    }
                    console.log($scope.columns);
                    //选中行第一列的内容
                    var rowName1 = row.grid.rows[0];
                    var parname = row.entity;
                    $scope.parname = parname;
                    angular.forEach(parname, function (data) {
                        console.log(data);
                    })
                    var codepath = '';
                    if ($rootScope.configList && $rootScope.currentCase) {
                        codepath = $rootScope.configList[$rootScope.currentCase].codepath;
                    }
                    var w = angular.element(".compareBox").find(".ui-grid-header-cell").width();
                    var w1 = angular.element(".compareBox").width();
                    var w2 = w1 - w;
                    var w3 = (w2 / w1) * 100;
                    angular.element(".compare").css("width", w3 + "%");
                    $scope.showcompare = true;
                    var firstCol = row.grid.columns[0].name;
                    $scope.getBaseSouce(firstCol, row.entity[firstCol], false);
                });
            },
            //滚动条事件
            customScroller: function myScrolling(uiGridViewport, scrollHandler) {
                uiGridViewport.on('scroll', function myScrollingOverride(event) {
                    // You should always pass the event to the callback since ui-grid needs it
                    scrollHandler(event);
                });
            }
        };

        $scope.getBaseSouce = function (program, paragraph, flag) {
            $http({
                method: 'POST',
                url: '/query/paragraphText',
                data: $.param({ 'pgmname': program, 'paragraph': paragraph }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                console.info(data);
                if (flag) {
                    $scope.right = data[0].content;
                } else {
                    $scope.left = data[0].content;
                    $scope.right = '';
                }
            }).error(function (data) {
                console.error(data);
            });
        }

        $scope.genCloneCode = function (program, flag) {
            if (flag) {
                program = $scope.prefix + program + '__' + program;
            }
            $http({
                method: 'POST',
                url: './analysis/clonecode',
                data: $.param({ 'program': program }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.length > 0) {
                    data = data.replace("/client", ".");
                    $scope.cloneList[program] = data;
                    if (flag) {
                        $scope.genTable(data);
                    } else {
                        $scope.calculateProgramclonecode(program);
                    }
                } else {
                    $rootScope.ui_grid_scatter = false;
                    $scope.similar = 0;
                }
            }).error(function () {
                $rootScope.ui_grid_sscatter = false;
                $scope.similar = 0;
            });
        };

        $scope.getScatterData = function (program) {
            $scope.compareTable.columnDefs = [];
            if (program == '') {
                $scope.genTable('');
            } else {
                $scope.genCloneCode(program, true);
            }
        };

        $scope.diff_close = function () {
            $scope.showmodel = false;
        }
        $scope.genTable = function (data) {
            $scope.showmodel = true;
            // 得到$scope.similarParagraphName的源代码
            var inputPath = 'resource/clone_analysis/input/business_case/paragraphs';
            $http({
                method: 'POST',
                url: './project/cloneCodeAnalysis/getParaCodeForDiffOne',
                data: $.param({ 'paraName': $scope.similarParagraphName, 'inputPath': inputPath }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                $scope.left = data[0];
                $scope.right = null;
            }).error(function (data) {
                console.log('error');
            });
        }
        //diff select
        $scope.diff_select = function (event, lname) {
            var tli = $(event.target);
            tli.addClass('clicked').siblings().removeClass('clicked');
            tli.parents('li').siblings().find('li').removeClass('clicked');
            //lname选中的段名
            // 得到第二段的源代码，并进行diff，返回diff结果
            var inputPath = 'resource/clone_analysis/input/business_case/paragraphs';
            $http({
                method: 'POST',
                url: './project/cloneCodeAnalysis/getParaCodeForDiffOne',
                data: $.param({ 'paraName': lname, 'inputPath': inputPath }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                $scope.right = data[0];
                $scope.name2 = lname;
                var configFile = 'resource/clone_analysis/config/business_case/arguments.properties';
                $http({
                    method: 'POST',
                    url: './project/cloneCodeAnalysis/diff',
                    data: $.param({
                        'action': 'diff', 'leftParaName': $scope.similarParagraphName, 'rightParaName': $scope.name2, 'configFile': configFile
                    }),
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                }).success(function (data) {
                    //$scope.diffResult值为0-4，0表示非clone code,1是Tier-1,2是Tier-2,3是Tier-3,4是Tier-4
                    if (data === '0') {
                        $scope.diffResult = 'None Clone';
                    } else {
                        $scope.diffResult = 'Tier-' + data;
                    }
                }).error(function (data) {
                    console.log('error');
                });
            }).error(function (data) {
                console.log('error');
            });
        }

        $scope.calculateProgramclonecode = function (program) {
            var path = $scope.cloneList[program];
            $http.get(path).success(function (response) {
                var count = 0, pcount = 0;
                for (var key in response[0]) {
                    count++;
                }
                $scope.cloecode['program'] = count;
                $scope.similar = count;
            }).error(function () {
                $scope.similar = 0;
            })
        }

        $scope.calculateParagraphclonecode = function (name) {
            var cloneSummary = 'resource/clone_analysis/output/clone/paragraph_summary.csv';
            var paragraphName = name.substr(name.lastIndexOf('/') + 1);
            $http({
                method: 'POST',
                url: './project/cloneCodeAnalysis/getCloneParagraphs',
                data: $.param({ 'cloneSummary': cloneSummary, 'paragraphName': paragraphName }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data === null || data === '') {
                    $scope.paragraphsimilar = 0;
                } else {
                    $scope.similarParagraphName = paragraphName;
                    $scope.paragraphsimilar = data.simliarParagraphCount;
                    $scope.similarParagraphsInfo = data;
                }
            }).error(function () {
                $scope.paragraphsimilar = 0;
            })
        }

        //广播事件，最大化,最小化时修改表格大小
        $scope.$on('changeTable', function (event, type, data) {
            if (type === 'ui_grid_detail') {
                //设置svg元素的class
                $scope.svgclass = data;
            }
        });

        $scope.filter = function () {
            $scope.gridApi.grid.refresh();
        };
        $scope.singleFilter = function (renderableRows) {
            var matcher = new RegExp($scope.filterValue);
            renderableRows.forEach(function (row) {
                var match = false;
                ['Code Impact Analaysis Summary'].forEach(function (field) {
                    if (row.entity[field] && row.entity[field].match(matcher)) {
                        match = true;
                    }
                });
                if (!match) {
                    row.visible = false;
                }
            });
            return renderableRows;
        };

        // $scope.left = ['I am the very model of a modern Major-General,',
        //     'I\'ve information vegetable, animal, and mineral,',
        //     'I know the kings of England, and I quote the fights historical,',
        //     'From Marathon to Waterloo, in order categorical.'
        // ].join('\n');

        // $scope.right = ['I am the very model of a cartoon individual,',
        //     'My animation\'s comical, unusual, and whimsical,',
        //     'I know the kings of England, and I quote the fights historical,',
        //     'From wicked puns and stupid jokes to anvils that drop on your head.'
        // ].join('\n');

        $scope.options = {
            editCost: 4,
            attrs: {
                insert: {
                    'data-attr': 'insert',
                    'class': 'insertion'
                },
                delete: {
                    'data-attr': 'delete'
                },
                equal: {
                    'data-attr': 'equal'
                }
            }
        };
        // 读取d3数据文件夹下business flow文件名列表
        // $http({
        //     method: "GET",
        //     url: "/codesearch/getBusinessFlowFileNameList"
        // }).success(function (result) {
        //     // TODO svg展示改为d3展示
        //     $scope.svgoptions = result;
        //     $rootScope.selectedName = result[0];
        //     $scope.d3url = "../d3-file/businessFlow/" + result[0] + ".json";
        // })

        $scope.svgclick = function (e, t) {
            $scope.similarParagraphName = '';
            $scope.name2 = '';
            $scope.diffResult = '';
            $scope.left = '';
            $scope.right = '';
            var f = angular.isString(e);
            var dataText;
            $scope.iftext=false;
            if (f) {
                dataText = e;
            } else {
                // dataText = $(e.target).parent("g").find("title").text();
                if (e.target.innerHTML !== null && e.target.innerHTML !== '') {
                    dataText = e.target.innerHTML;
                } else {
                    var text = $(e.target).parent().find("text").text()
                    if (text !== null && text !== '') {
                        dataText = text;
                    } else {
                        dataText = $(e.target).parent("g").find("title").text();
                    }
                }
            }
            console.log(dataText);
            //测试用
            // dataText = 'BatchProgram__MIPCLM85__MIPCLM85';
            var order = t;
            // t==3即program call图现在为d3展示，点击事件由d3处理
            if (t == 3) {
                // 当前选中的program，用于生成controlflow svg图时拼接program.paragraph全名
                $scope.svgurl2="";
                $scope.currentProgarm = dataText.substr(dataText.lastIndexOf("__") + 2);
                $scope.prefix = dataText.substr(0, dataText.indexOf($scope.currentProgarm));
                //dataText = 'BatchProgram__MIPCLM85__MIPCLM85';
                var path = "svg-file/paragraphPerformSvg/" + dataText + ".svg";
                // 判断本次d3点击，对应perform图是否已生成过
                $http.get(path).success(function (response) {
                    $scope.svgurl2 = path;
                }).error(function (data, status, headers, config) {
                    // svg不存在，后台请求调用jar包生成
                    $http({
                        method: 'POST',
                        url: './svg/generatePerform',
                        data: $.param({ 'programName': dataText }),
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                    }).success(function (data) {
                        if (data) {
                            $scope.svgurl2 = data;
                        }
                    }).error(function () {
                        $scope.svgurl2 = '';
                    });
                });
                // $scope.svgurl2 = "svg-file/paragraphPerformSvg/" + dataText + ".svg";
                $scope.second = true;
                $scope.three = false;
                angular.element(".btfirst").removeClass("one").addClass("two");
                angular.element(".btsecond").addClass('two')
                var tw = angular.element(".two").width();
                angular.element(".btfirst").find("svg").attr("width", tw);
                // $scope.extractProgramInfo("BatchProgram__IPDD0008__IPDD0008");
                $scope.program = dataText;
                $scope.extractProgramInfo(dataText);
                $scope.genCloneCode(dataText);
                $scope.d3width = tw;
            } else if (t == 2) {
                $scope.svgurl3="";
                dataText = $scope.currentProgarm + '.' + dataText;
                var path = "svg-file/paragraphControlflowSvg/" + dataText + ".svg";
                // 判断本次svg点击，对应control flow图是否已生成过
                $http.get(path).success(function (response) {
                    $scope.svgurl3 = path;
                }).error(function (data, status, headers, config) {
                    // svg不存在，后台请求调用jar包生成
                    $http({
                        method: 'POST',
                        url: './svg/generateControlflow',
                        data: $.param({ 'paragraphName': dataText }),
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                    }).success(function (data) {
                        if (data) {
                            $scope.svgurl3 = data;
                        }
                    }).error(function () {
                        $scope.svgurl3 = '';
                    });
                });
                var programName = getProgramName($scope.currentProgarm);
                var paragraphName = getParagraphName(programName, dataText);
                $scope.extractParagraphInfo($scope.program + paragraphName);
                angular.element(".btthree").siblings().removeClass("two")
                $scope.three = true;
            }
        }
        $scope.d3textClick=function(text){
                $scope.second = true;
                $scope.three = false;
                $scope.iftext=true;
                angular.element(".btfirst").removeClass("one").addClass("two");
                angular.element(".btsecond").addClass('two')
                var tw = angular.element(".two").width();
                angular.element(".btfirst").find("svg").attr("width", tw);
        }
        $scope.extractProgramInfo = function (id) {
            $http({
                method: 'POST',
                url: '/query/programinfo',
                data: $.param({ 'pgmname': id }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.info.length > 0) {
                    $scope.programName = data.info[0].name ? data.info[0].name : "NA";
                    $scope.description = data.info[0].description ? data.info[0].description : "NA";
                } else {
                    $scope.programName = "NA";
                    $scope.description = "NA";
                }
                $scope.businessFlow = $rootScope.selectedName ? $rootScope.selectedName : "NA";
                $scope.file = data.file ? removedup(data.file) : [{ 'name': 'NA' }];
                $scope.table = data.table ? removedup(data.table) : [{ 'name': 'NA' }];
                console.info(data);
            }).error(function () {
                $scope.programName = "NA";
                $scope.businessFlow = "NA";
                $scope.description = "NA";
                $scope.file = [{ 'name': 'NA' }];
                $scope.table = [{ 'name': 'NA' }];
            });
        }

        $scope.extractParagraphInfo = function (name) {
            name = name.replace(/__/g, '/').replace(/_/g, '-');
            $http({
                method: 'POST',
                url: '/query/paragrahinfo',
                data: $.param({ 'paragraph': name }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.info.length > 0) {
                    $scope.paragraphName = data.info[0].name ? data.info[0].name : 'NA';
                    $scope.prediction = data.info[0].prediction ? data.info[0].prediction : 'NA';
                    $scope.paragraphTag = data.info[0].tag ? data.info[0].tag : 'NA';
                    $scope.calculateParagraphclonecode(name);
                } else {
                    $scope.paragraphName = 'NA';
                    $scope.prediction = 'NA';
                    $scope.paragraphTag = 'NA';
                    $scope.paragraphsimilar = 0;
                }
            }).error(function () {
                $scope.paragraphName = 'NA';
                $scope.prediction = 'NA';
                $scope.paragraphTag = 'NA';
                $scope.paragraphsimilar = 0;
            });
        }
    }

    function removedup(list) {
        var result = [];
        var dup = {};
        if (list.length == 0) {
            return [{ 'name': 'NA' }];
        }
        for (var i = 0; i < list.length; i++) {
            if (typeof dup[list[i].name] === 'undefined') {
                dup[list[i].name] = 1;
                result.push(list[i]);
            }
        }
        return result;
    }

    function getProgramName(name) {
        var program;
        var pos = name.lastIndexOf('_');
        return pos > -1 ? name.substring(pos + 1) : name;
    }

    function getParagraphName(pgmname, name) {
        var prparagraph;
        var prparagraph = name.replace(pgmname, '');
        if (prparagraph.substring(0, 1) === '_') {
            prparagraph = prparagraph.substring(1)
        }
        return prparagraph.replace(/_/g, '-');
    }

    function svgLink($scope, $attrs) {
        $scope.width = $window.innerWidth;
        angular.element($window).bind('resize', function () {
            $scope.width = $window.innerWidth;
            $scope.$digest();
        });

        $scope.link = function (e, l) {
            if (l == 'paragraph') {
                if ($scope.paragraphsimilar == 0) {
                    return;
                }
            } else if (l == 'program') {
                if ($scope.similar == 0) {
                    return;
                }
            }
            $scope.getScatterData('');
        }

        $scope.link2 = function () {
            // $scope.showcompare=$scope.showcompare==false?true:false;
            if ($scope.codeText) {
                $scope.compareTable.columnDefs = [];
                $scope.getScatterData($scope.codeText);
                $scope.showcompare = false;
                $scope.codeText = "";
            }
        }
        $scope.close = function () {
            $scope.showmodel = false;
        }
        $scope.close2 = function () {
            $scope.showcompare = false;
        }

        $scope.copli = function (e) {
            var program = $(e.target).attr("data-c");
            var paragraph = e.target.textContent;
            $scope.getBaseSouce(program, paragraph, true);
        }

    }
}).directive('toggle', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            if (attrs.toggle === 'tooltip') {
                $(element).tooltip();
            }
            if (attrs.toggle === 'popover') {
                $(element).popover();
            }
        }
    };
}).directive('menutree', function () {
    //阻止下拉框点击退出
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'svg-module/selectTree.html',
        replace: false,
        controller: treeController,
        link: function (scope, element) {
            element.bind('click', function (event) {
                event.stopPropagation();
            });
        }
    };

    function treeController($scope, $http, $location, $attrs, $rootScope, $timeout) {
        $scope.panels = [
            {
                "name": "Reporting",
                "children": [
                    {
                        "name": "Reports_on_info_identify",
                        'selected':true
                    },
                    {
                        "name": "Reports_on_balance"
                    },
                    {
                        "name": "Reports_on_error_summary"
                    },
                    {
                        "name": "Reports_on_member_dtl_records"
                    },
                    {
                        "name": "Reports_on_month_to_date_fee_sum"
                    },
                    {
                        "name": "Reports_on_overrides_comparation"
                    },
                    {
                        "name": "Reports_on_records_process"
                    },
                    {
                        "name": "Reports_on_specific_customer_or_network"
                    },
                    {
                        'name':'DataFlow_MEMBER_ID'
                    },
                    {
                        'name':'DataFlow_PHARMACY'
                    },
                    {
                        'name':'DataFlow_PHARMACY_ID'
                    },
                    {
                        'name':'UseCase_Price_Protection'
                    },
                    {
                        'name':'DataModel_pharmacy_cost_schedule'
                    },
                    {
                        'name':'DataModel_ER_pharmacy_cost_schedule'
                    }
                ]

            }
        ];

        this.init = function (panels) {
            //初始化信息
            var selected = false;
            for (var index in panels) {
                if (panels[index].selected) {
                    selected = true;
                    $scope.nodes = panels[index].children;
                    $rootScope.selectedName = panels[0].children[0]['name'];
                    break;
                }
            }
            if (!selected) {
                panels[0].selected = true;
                $scope.nodes = panels[0].children;
                $rootScope.selectedName = panels[0].children[0]['name'];
            }
            this.addParent(panels);
        }

        this.addParent = function (panels) {
            for (var index in panels) {
                if (panels[index].children && panels[index].children.length > 0) {
                    for (var _index in panels[index].children) {
                        panels[index].children[_index].parent = panels[index];
                    }
                    this.addParent(panels[index].children);
                }
            }
        };

        this.init($scope.panels);
        $scope.update = function (e) {
            //进行数据查询，更新
            console.info('select and update');
            var $i = $(e.target);
            var icd = $i.is(':checked');
            if (icd) {
                $rootScope.selectedName = $i.val();
            } else {
                return;
            }
        };


        $scope.select = function (elem) {
            for (var i = 0; i < $scope.panels.length; i++) {
                if ($scope.panels[i] === elem) {
                    $scope.nodes = $scope.panels[i].children;
                    $scope.panels[i].selected = true;
                }
                else {
                    $scope.panels[i].selected = false;
                }
            }
        };

        $scope.forward = function (node, selected) {
            //上层节点被选中，下层节点全被选中
            if (node === undefined) {
                return;
            }
            for (var index in node.children) {
                node.children[index].selected = selected;

                if (node.children !== undefined) {
                    $scope.forward(node.children[index], node.children[index].selected);
                }
            }
        };

        $scope.backward = function (node, selected) {
            //下层节点被选或取消选中，修改相对应的上层节点
            if (node === undefined || node.value === null) {
                return;
            }
            var parent = node.parent;
            if (!parent) return
            var parentSelected = selected;

            if (selected === true) {
                for (var index in parent.children) {
                    if (!parent.children[index].selected) {
                        parentSelected = false;
                        break
                    }
                }
            }
            if (parent.parent) {
                parent.selected = parentSelected;
            }
            if (node.parent !== undefined) {
                $scope.backward(parent, parentSelected);
            };

        };
    };
}).directive('menutree1', function () {
    //阻止下拉框点击退出
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'svg-module/selectTree.html',
        replace: false,
        controller: treeController,
        link: function (scope, element) {
            element.bind('click', function (event) {
                event.stopPropagation();
            });
        }
    };

    function treeController($scope, $http, $location, $attrs, $rootScope, $timeout) {
        $scope.panels = [
            {
                "name": '',
                "children": [
                    {
                        "name": "Check_claims",
                        'selected':true
                    },
                    {
                        "name": "File_claims"
                    },
                    {
                        "name": "Count_records"
                    },
                    {
                        "name": "Benefit_plans"
                    },
                    {
                        "name": "RxNova_member_file_load_process"
                    },
                ]
            }
        ];

        this.init = function (panels) {
            //初始化信息
            var selected = false;
            for (var index in panels) {
                if (panels[index].selected) {
                    selected = true;
                    $scope.nodes = panels[index].children;
                    $rootScope.selectedName = panels[0].children[0]['name'];
                    break;
                }
            }
            if (!selected) {
                panels[0].selected = true;
                $scope.nodes = panels[0].children;
                $rootScope.selectedName = panels[0].children[0]['name'];
            }
            this.addParent(panels);
        }

        this.addParent = function (panels) {
            for (var index in panels) {
                if (panels[index].children && panels[index].children.length > 0) {
                    for (var _index in panels[index].children) {
                        panels[index].children[_index].parent = panels[index];
                    }
                    this.addParent(panels[index].children);
                }
            }
        };

        this.init($scope.panels);
        $scope.update = function (e) {
            //进行数据查询，更新
            console.info('select and update');
            var $i = $(e.target);
            var icd = $i.is(':checked');
            if (icd) {
                $rootScope.selectedName = $i.val();
            } else {
                return;
            }
        };


        $scope.select = function (elem) {
            for (var i = 0; i < $scope.panels.length; i++) {
                if ($scope.panels[i] === elem) {
                    $scope.nodes = $scope.panels[i].children;
                    $scope.panels[i].selected = true;
                }
                else {
                    $scope.panels[i].selected = false;
                }
            }
        };

        $scope.forward = function (node, selected) {
            //上层节点被选中，下层节点全被选中
            if (node === undefined) {
                return;
            }
            for (var index in node.children) {
                node.children[index].selected = selected;

                if (node.children !== undefined) {
                    $scope.forward(node.children[index], node.children[index].selected);
                }
            }
        };

        $scope.backward = function (node, selected) {
            //下层节点被选或取消选中，修改相对应的上层节点
            if (node === undefined || node.value === null) {
                return;
            }
            var parent = node.parent;
            if (!parent) return
            var parentSelected = selected;

            if (selected === true) {
                for (var index in parent.children) {
                    if (!parent.children[index].selected) {
                        parentSelected = false;
                        break
                    }
                }
            }
            if (parent.parent) {
                parent.selected = parentSelected;
            }
            if (node.parent !== undefined) {
                $scope.backward(parent, parentSelected);
            };

        };
    };
}).directive('menutree2', function () {
    //阻止下拉框点击退出
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'svg-module/selectTree.html',
        replace: false,
        controller: treeController,
        link: function (scope, element) {
            element.bind('click', function (event) {
                event.stopPropagation();
            });
        }
    };

    function treeController($scope, $http, $location, $attrs, $rootScope, $timeout) {
        $scope.panels = [
            {
                "name": '',
                "children": [
                    {
                        "name": "Account_payable",
                        'selected':true
                    },
                    {
                        "name": "Account_receivable"
                    },
                    {
                        "name": "Pharmacy_balance"
                    }
                ]
            }
        ];

        this.init = function (panels) {
            //初始化信息
            var selected = false;
            for (var index in panels) {
                if (panels[index].selected) {
                    selected = true;
                    $scope.nodes = panels[index].children;
                    $rootScope.selectedName = panels[0].children[0]['name'];
                    break;
                }
            }
            if (!selected) {
                panels[0].selected = true;
                $scope.nodes = panels[0].children;
                $rootScope.selectedName = panels[0].children[0]['name'];
                console.log("sn:" + $rootScope.selectedName);
            }
            this.addParent(panels);
        }

        this.addParent = function (panels) {
            for (var index in panels) {
                if (panels[index].children && panels[index].children.length > 0) {
                    for (var _index in panels[index].children) {
                        panels[index].children[_index].parent = panels[index];
                    }
                    this.addParent(panels[index].children);
                }
            }
        };

        this.init($scope.panels);
        $scope.update = function (e) {
            //进行数据查询，更新
            console.info('select and update');
            var $i = $(e.target);
            var icd = $i.is(':checked');
            if (icd) {
                $rootScope.selectedName = $i.val();
            } else {
                return;
            }
        };


        $scope.select = function (elem) {
            for (var i = 0; i < $scope.panels.length; i++) {
                if ($scope.panels[i] === elem) {
                    $scope.nodes = $scope.panels[i].children;
                    $scope.panels[i].selected = true;
                }
                else {
                    $scope.panels[i].selected = false;
                }
            }
        };

        $scope.forward = function (node, selected) {
            //上层节点被选中，下层节点全被选中
            if (node === undefined) {
                return;
            }
            for (var index in node.children) {
                node.children[index].selected = selected;

                if (node.children !== undefined) {
                    $scope.forward(node.children[index], node.children[index].selected);
                }
            }
        };

        $scope.backward = function (node, selected) {
            //下层节点被选或取消选中，修改相对应的上层节点
            if (node === undefined || node.value === null) {
                return;
            }
            var parent = node.parent;
            if (!parent) return
            var parentSelected = selected;

            if (selected === true) {
                for (var index in parent.children) {
                    if (!parent.children[index].selected) {
                        parentSelected = false;
                        break
                    }
                }
            }
            if (parent.parent) {
                parent.selected = parentSelected;
            }
            if (node.parent !== undefined) {
                $scope.backward(parent, parentSelected);
            };

        };
    };
}).directive('svgd3', function () {
    return {
        restrict: 'EA',
        scope: true,
        template: '<svg width="960" height="600"></svg>',
        replace: false,
        controller: d3Controller
    };
    function d3Controller($scope, $http, $location, $attrs, $rootScope, $timeout) {
        $scope.d3link = function () {
            $scope.$watch('d3url', function (newValue) {
                if (newValue !== undefined) {
                    var svg = d3.select("svg"),
                        width = +svg.attr("width"),
                        height = +svg.attr("height");
                    $scope.d3width = width;
                    var color = d3.scaleOrdinal(d3.schemeCategory20);

                    var simulation = d3.forceSimulation()
                        .force("link", d3.forceLink().id(function (d) { return d.id; }).distance(100))
                        .force("charge", d3.forceManyBody())
                        .force("center", d3.forceCenter(width / 3, height / 3));

                    svg.selectAll('*').remove();
                    d3.json(newValue, function (error, graph) {
                        if (error) throw error;
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
                            .attr("class", "links")
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
                                var type=$rootScope.selectedName;
                                var str = type.split("_");
                                var fileType = str[0];
                                
                                // 只有Program结点点击后有效果
                                if (fileType === "Reports"&&group === "Program") {//原来的点击program事件
                                    $scope.svgclick(d3.select(this).text(), 3);
                                    $scope.d3width = angular.element(".two").width();
                                    $rootScope.iftwo=false;
                                }else if(fileType==='UseCase'&&group==='Documentation'){//点击C类型下出现text文字的情况,text内容来自哪里?
                                    $scope.d3textClick(d3.select(this).text());
                                    $scope.d3width = angular.element(".two").width();
                                }else if(fileType==='DataFlow'&&group==='Variable'){//点击A,B类型下的事件
                                    $scope.svgclick(d3.select(this).text(), 3);
                                    $rootScope.programName=$rootScope.selectedName;
                                    $rootScope.iftwo=true;
                                    $scope.d3width = angular.element(".two").width();
                                }else if(fileType==='DataModel'&&group==='Domain'){
                                    $scope.svgclick(d3.select(this).text(), 3);
                                    $rootScope.programName=$rootScope.selectedName;
                                    $rootScope.iftwo=true;
                                    $scope.d3width = angular.element(".two").width();
                                }
                            });
                        var text = svg.append("g")
                            .attr("class", "text")
                            .selectAll("text")
                            .data(graph.nodes)
                            .enter()
                            .append("text")
                            .text(function (d) {
                                var str = d.id.split("_");
                                var l = str.length;
                                return str[l - 1];
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
                                .attr("x", 100)
                                .attr("y", function (d, i) {
                                    return i > 0 ? 50 + 25 * i : 50;
                                })
                                .data(seriesData.series)
                                .attr("dy", ".35em")
                                .style("text-anchor", "end")
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
                    });
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
        // $scope.$watch('d3width', function (newValue) {
        //     if (newValue !== 960) {
        //         var svg = d3.select("svg");
        //         svg.selectAll('*').remove();
        //        $scope.d3link();
        //     }
        // });
        $scope.$watch('$root.selectedName', function (newValue) {
            if (newValue !== undefined) {
                $scope.d3url = "../d3-file/businessFlow/" + newValue + ".json";
            }
        });
    }
}).directive('tableDirective', function () {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'svg-module/table_file.html',
        replace: false,
        controller: commonController,
        link: function (scope, element) {
            element.bind('click', function (event) {
                event.stopPropagation();
            });
        }
    };

    function commonController($scope, $http, $location, $rootScope) {
        $scope.tableClose = function () {
            $rootScope.tableShow = false;
            $(".left_nav").find('.selected').removeClass('selected');
        }
        $rootScope.tableshowClick = function () {
            $rootScope.tableShow = true;
            $scope.tableDetail = '';
            $scope.file = true;
            // 表格中展示table数字为86,于是cypher查询中限定table个数为86
            var limitNum = 86;
            $http({
                method: 'POST',
                url: './query/getTableList',
                data: $.param({ 'limitNum': limitNum }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                // data有nodeId和name，name用户界面展示，nodeId是唯一标识，用于获取file detail
                $scope.tableList = data;
                $(".left_nav ul").find('li').eq(0).addClass('selected');
                $scope.select_table('', data[0].nodeId);
            }).error(function (data) {
                // error handling
            });
        }
        $scope.select_table = function (e, nodeId) {
            $(e.target).addClass("selected").siblings().removeClass("selected");
            $http({
                method: 'POST',
                url: './query/getTableDetail',
                data: $.param({ 'tableNodeId': nodeId }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                $scope.tableDetail = data;
            }).error(function (data) {
                // error handling
            });
        }
    }
})
