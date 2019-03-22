'use strict';

angular.module('uiGridModule').directive('uigridDirective', function ($window) {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'uiGrid-module/uiGrid.template.html',
        replace: false,
        controller: uiGridController,
        link: uiGridLink
    };
    function uiGridController($scope, $http, $location, $attrs, $rootScope, $timeout) {
        var outputpath = '';
        $scope.svgList = [];
        $scope.currentTitle4='';
        if ($rootScope.currentCase && $rootScope.configList) {
            outputpath = $rootScope.configList[$rootScope.currentCase].outputpath;
        }
        //默认初始为第一个tab
        // $http({
        //     method: 'POST',
        //     url: './analysis/output',
        //     data: $.param({ 'outputpath': outputpath, 'filterValue': 'summary' }),
        //     headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        // }).success(function (data) {
        //     if (data && data.length > 0) {
        //         console.info('summary');
        //         //json 数据
        //         $scope.tabs = data;
        //         $scope.currentTitle = $scope.tabs[0].title;
        //     } else {
        //         $rootScope.ui_grid_summary = false;
        //     }
        // }).error(function () {
        //     $rootScope.ui_grid_summary = false;
        // });
        // //默认初始为第一个tab
        // $http({
        //     method: 'POST',
        //     url: './analysis/output',
        //     data: $.param({ 'outputpath': outputpath, 'filterValue': 'controlflow' }),
        //     headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        // }).success(function (data) {
        //     if (data && data.length > 0) {
        //         console.info('controlflow');
        //         console.info(data);
        //         $scope.dtabs = data;
        //         $scope.currentTitle1 = $scope.dtabs[0].title;
        //         $scope.svgUrl = '';//svgUrl
        //     } else {
        //         $rootScope.ui_grid_detail = false;
        //     }
        // }).error(function () {
        //     $rootScope.ui_grid_detail = false;
        // });
        // //tab切换
        // $scope.onClickTab = function (tab) {
        //     $scope.currentTitle = tab.title;
        //     if(tab.title === 'Problem Statements'){
        //         $scope.a = true;
        //         $scope.b = false;
        //     }else{
        //         $scope.b = true;
        //         $scope.a = false;
        //         $timeout(function () { $scope.gridApi.core.handleWindowResize(); }, 200);
        //     }
        //     $scope.gridOptions1.data = tab.table;
        // };
        // //tab切换
        // $scope.onClickTab1 = function (tab) {
        //     $scope.currentTitle1 = tab.title;
        //     $scope.gridOptions2.data = tab.table;
        // };
        // //选中tab的样式
        // $scope.isActiveTab = function (tabTitle) {
        //     return tabTitle === $scope.currentTitle;
        // };
        // //选中tab的样式
        // $scope.isActiveTab1 = function (tabTitle) {
        //     return tabTitle === $scope.currentTitle1;
        // };
        //主表
    //     $scope.gridOptions1 = {
    //         enableFiltering: false,
    //         //是否显示grid 菜单
    //         enableGridMenu: false,
    //         //是否点击行任意位置后选中,默认为false,当为true时，checkbox可以显示但是不可选中
    //         enableFullRowSelection: true,
    //         //默认false,选中后是否可以取消选中
    //         noUnselect: true,
    //         //是否可以选择多个,默认为true
    //         multiSelect: false,
    //         //禁止排序
    //         enableSorting: false,
    //         enableRowHeaderSelection: false,
    //         //grid垂直滚动条是否显示, 0-不显示  1-显示
    //         enableVerticalScrollbar: 0,
    //    //     paginationPageSizes: [5, 10, 20, 30],
    //    //     paginationPageSize: 5,
    //         onRegisterApi: function (gridApi) {
    //             $scope.gridApi = gridApi;
    //             $scope.gridResize = $timeout(function () { $scope.gridApi.core.handleWindowResize(); }, 500);
    //             //过滤
    //             $scope.gridApi.grid.registerRowsProcessor($scope.singleFilter, 200);
    //         },
    //         // headerTemplate: '<div class='ui-grid-top-panel' style='text-align: center'></div>',
    //         columnDefs: [{
    //             field: 'Code Impact Analaysis Summary',
    //             name: 'Code Impact Analaysis Summary',
    //             // 是否显示列头部菜单按钮
    //             enableColumnMenu: false
    //         }, {
    //                 field: 'num',
    //                 name: ' ',
    //                 // 是否显示列头部菜单按钮
    //                 enableColumnMenu: false,
    //                 width: 80
    //             }],
    //         customScroller: function myScrolling(uiGridViewport, scrollHandler) {
    //             uiGridViewport.on('scroll', function myScrollingOverride(event) {
    //                 // You should always pass the event to the callback since ui-grid needs it
    //                 scrollHandler(event);
    //             });
    //         }
    //     };
        //detail表
        // $scope.gridOptions2 = {
        //     //是否显示grid 菜单
        //     enableGridMenu: false,
        //     //是否点击行任意位置后选中,默认为false,当为true时，checkbox可以显示但是不可选中
        //     enableFullRowSelection: true,
        //     //默认false,选中后是否可以取消选中
        //     noUnselect: true,
        //     //是否可以选择多个,默认为true
        //     multiSelect: false,
        //     //禁止排序
        //     enableSorting: false,
        //     enableRowHeaderSelection: false,
        //     //grid垂直滚动条是否显示, 0-不显示  1-显示
        //     enableVerticalScrollbar: 1,
        //     onRegisterApi: function (gridApi2) {
        //         $scope.gridApi2 = gridApi2;
        //         $scope.gridResize2 = $timeout(function () { $scope.gridApi2.core.handleWindowResize(); }, 500);
        //         //detail表格点击获取代码
        //         gridApi2.selection.on.rowSelectionChanged($scope, function (row) {
        //             angular.element('.listGridStyle').find('.ui-grid-row').eq(0).removeClass('ui-grid-row-selected');
        //             $scope.resultCodes =[];
        //             $scope.svgUrl='';
        //             var columnName0 = row.grid.columns[0].name;
        //             var name = row.entity[columnName0];
        //             //选中行第一列的内容
        //             $scope.selectedRowName =  name;
        //             var columnName1 = row.grid.columns[1].name;
        //             var parname = row.entity[columnName1];
        //             var codepath = '';
        //             if ($rootScope.configList && $rootScope.currentCase) {
        //                 codepath = $rootScope.configList[$rootScope.currentCase].codepath;
        //             }
        //             var type = '';
        //             if (codepath.indexOf('business_case_1')) {
        //                 type = 'business_case_1';
        //             } else if (codepath.indexOf('business_case_2')) {
        //                 type = 'business_case_2';
        //             } else if (codepath.indexOf('business_case_3')) {
        //                 type = 'business_case_3';
        //             }
        //             getGraphData(codepath, name, parname,type);
        //         });
        //     },
        //     customScroller: function myScrolling(uiGridViewport, scrollHandler) {
        //         uiGridViewport.on('scroll', function myScrollingOverride(event) {
        //             // You should always pass the event to the callback since ui-grid needs it
        //             scrollHandler(event);
        //         });
        //     }
        // };

        // function getParagraphCode(codepath,name,parname,type) {
        //     $http({
        //         method: 'POST',
        //         url: './query/paragraphCode',
        //         data: $.param({ 'pgmname': name, 'parname':parname }),
        //         headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        //     }).success(function (data) {
        //         if(data&&data.length>0){
        //         $scope.resultCodes = data[0].content.split('\r\n');
        //         getControlFlow(codepath,name,parname,type);
        //         }else{
        //              $scope.resultCodes = ['Not Available'];
        //              $scope.svgUrl = 'uiGrid-module/svg.html';
        //         }

        //     });
        // };

        // $scope.getParagraph = function (name) {
        //     $http({
        //         method: 'POST',
        //         url: './query/paragraph',
        //         data: $.param({ 'pgmname': name }),
        //         headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        //     }).success(function (data) {
        //         $scope.gridOptions3.data = data;
        //         $scope.procedure = name;
        //     });
        // };

        // function getGraphData(codepath, name, parname, type){
        //         getParagraphCode(codepath,name,parname,type);
        // };

    // function getControlFlow(codepath,name,parname,type) {
    //     //简化版，快速获取svg，需要预先分析所有controlflow
    //     $http({
    //         method: 'POST',
    //         url: './analysis/getsvg',
    //         data: $.param({ 'pgmname': name, 'case': type, 'paragraph': parname }),
    //         headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    //     }).success(function (data) {
    //         if (data !== '') {
    //             $scope.svgUrl = data;
    //         } else {
    //             genControlFlow(codepath,name,parname,type);
    //         }
    //     }).error(function (data) {
    //         console.info(data);
    //         $scope.svgUrl = 'uiGrid-module/svg.html';
    //     });
    // };

    // function genControlFlow(codepath,name,parname,type) {
    //     $http({
    //         method: 'POST',
    //         url: './analysis/controlflow',
    //         data: $.param({ 'codepath': codepath, 'pgmname': name, 'case': type, 'paragraph': parname }),
    //         headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    //     }).success(function (data) {
    //         if(data!==''){
    //         $scope.svgUrl = data;
    //         }else{
    //         $scope.svgUrl = 'uiGrid-module/svg.html';
    //         }
    //     }).error(function (data) {
    //         console.info(data);
    //         $scope.svgUrl = '';
    //     });
    // };
        $scope.getSourceCode = function (codepath, name, type) {
            $http({
                method: 'POST',
                url: './analysis/getSourceCode',
                data: $.param({ 'codepath': codepath, 'pgmname': name, 'case': type }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                $scope.resultCode = data.split('\r\n');
            });
        };

        // $scope.gridOptions2.columnDefs = [{
        //     field: 'Procedure',
        //     name: 'Procedure',
        //     width:150,
        //     // 是否显示列头部菜单按钮
        //     enableColumnMenu: false
        // },{
        //     field: 'Paragraph',
        //     name: 'Paragraph',
        //     // 是否显示列头部菜单按钮
        //     enableColumnMenu: false
        // }, {
        //     field: 'startLine',
        //     name: 'startLine',
        //     visible: false,
        //     // 是否显示列头部菜单按钮
        //     enableColumnMenu: false
        // }, {
        //     field: 'endLine',
        //     name: 'endLine',
        //     visible: false,
        //     // 是否显示列头部菜单按钮
        //     enableColumnMenu: false
        // }];
        //初始化svgclass
        $scope.svgclass = 'ide-code-controlflow';
        //detail表
        $scope.gridOptions3 = {
            //是否显示grid 菜单
            enableGridMenu: false,
            //是否点击行任意位置后选中,默认为false,当为true时，checkbox可以显示但是不可选中
            enableFullRowSelection: true,
            //默认false,选中后是否可以取消选中
            noUnselect: true,
            //是否可以选择多个,默认为true
            multiSelect: false,
            //禁止排序
            enableSorting: false,
            enableRowHeaderSelection: false,
            //grid垂直滚动条是否显示, 0-不显示  1-显示
            enableVerticalScrollbar: 1,
            columnDefs: [{
                field: 'name',
                name: 'paragraph Name',
                // 是否显示列头部菜单按钮
                enableColumnMenu: false
            }, {
                    field: 'startLine',
                    name: 'startLine',
                    visible: false,
                    // 是否显示列头部菜单按钮
                    enableColumnMenu: false
                }, {
                    field: 'endLine',
                    name: 'endLine',
                    visible: false,
                    // 是否显示列头部菜单按钮
                    enableColumnMenu: false
                }],
            onRegisterApi: function (gridApi3) {
                $scope.gridApi3 = gridApi3;
                $scope.gridResize3 = $timeout(function () { $scope.gridApi3.core.handleWindowResize(); }, 500);
                //选择行显示代码
                gridApi3.selection.on.rowSelectionChanged($scope, function (row) {
                    var name = row.entity['name'];
                    var startline = parseInt(row.entity['startLine'],10);
                    var endline = parseInt(row.entity['endLine'],10);
                    $scope.resultCodes = $scope.resultCode.slice(startline - 1, endline);
                    var pos = $scope.procedure.lastIndexOf('.');
                    var main = $scope.procedure.substring(pos + 1);
                    var tempurl = '';
                    for (var i = 0; i < $scope.svgList.length; i++) {
                        if ($scope.svgList[i].name.indexOf(name + ' ') > -1) {
                            $scope.svgUrl = $scope.svgList[i].svgurl;
                            return;
                        }
                        if ($scope.svgList[i].name.indexOf(name + '.svg') > -1) {
                            $scope.svgUrl = $scope.svgList[i].svgurl;
                            return;
                        }
                        if ($scope.svgList[i].name.indexOf(main + '.svg') === 0) {
                            tempurl = $scope.svgList[i].svgurl;
                        }
                    }
                    $scope.svgUrl = tempurl;
                });
            },
            customScroller: function myScrolling(uiGridViewport, scrollHandler) {
                uiGridViewport.on('scroll', function myScrollingOverride(event) {
                    // You should always pass the event to the callback since ui-grid needs it
                    scrollHandler(event);
                });
            }
        };
        //广播事件，最大化,最小化时修改表格大小
        $scope.$on('changeTable', function (event, type, data) {
            if (type === 'ui_grid_detail') {
                //设置svg元素的class
                $scope.svgclass = data;
            }
        });
        // //获取Summary表格的数据
        // $scope.getSummaryData = function () {
        //     var outputPath = '';
        //     if ($rootScope.currentCase && $rootScope.configList) {
        //         outputPath = $rootScope.configList[$rootScope.currentCase].outputpath;
        //     }
        //     $http({
        //         method: 'POST',
        //         url: './analysis/output',
        //         data: $.param({ 'outputpath': outputPath, 'filterValue': 'summary' }),
        //         headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        //     }).success(function (data) {
        //         if (data && data.length > 0) {
        //             $scope.gridOptions1.data = data[0].table;
        //         } else {
        //             $rootScope.ui_grid_summary = false;
        //         }
        //     }).error(function () {
        //         $rootScope.ui_grid_summary = false;
        //     });
        // };
        //获取detail表格的数据
        // $scope.getDetailData = function (opr) {
        //     $http({
        //         method: 'POST',
        //         url: './analysis/output',
        //         data: $.param({
        //             'outputpath': $rootScope.configList[$rootScope.currentCase].outputpath,
        //             'filterValue': 'controlflow'
        //         }),
        //         headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        //     }).success(function (data) {
        //         if (data && data.length > 0) {
        //             $scope.gridOptions2.data = data[0].table;
        //             $timeout(function () {
        //                 angular.element('.listGridStyle')
        //                     .find('.ui-grid-row').eq(0).addClass('ui-grid-row-selected');
        //                 var name = angular.element('.listGridStyle')
        //                     .find('.ui-grid-row').eq(0).find('.ui-grid-cell-contents')[0].textContent;
        //                 var parname = angular.element('.listGridStyle')
        //                     .find('.ui-grid-row').eq(0).find('.ui-grid-cell-contents')[1].textContent;
        //                 var codepath = '';
        //                 if ($rootScope.configList && $rootScope.currentCase) {
        //                     codepath = $rootScope.configList[$rootScope.currentCase].codepath;
        //                 }
        //                 var type = '';
        //                 if (codepath.indexOf('business_case_1')) {
        //                     type = 'business_case_1';
        //                 } else if (codepath.indexOf('business_case_2')) {
        //                     type = 'business_case_2';
        //                 } else if (codepath.indexOf('business_case_3')) {
        //                     type = 'business_case_3';
        //                 }
        //                 getGraphData(codepath, name, parname,type);
        //             }, 500);
                    
        //         } else {
        //             $rootScope.ui_grid_detail = false;
        //         }
        //     }).error(function () {
        //         $rootScope.ui_grid_detail = false;
        //     });
        // };

        //右边的表格
        //  $scope.gridOptions4 = {
        //     enableFiltering: false,
        //     //是否显示grid 菜单
        //     enableGridMenu: false,
        //     //是否点击行任意位置后选中,默认为false,当为true时，checkbox可以显示但是不可选中
        //     enableFullRowSelection: true,
        //     //默认false,选中后是否可以取消选中
        //     noUnselect: true,
        //     //是否可以选择多个,默认为true
        //     multiSelect: false,
        //     //禁止排序
        //     enableSorting: false,
        //     enableRowHeaderSelection: false,
        //     //grid垂直滚动条是否显示, 0-不显示  1-显示
        //     enableVerticalScrollbar: 1,
        //     onRegisterApi: function (gridApi4) {
        //         $scope.gridApi4 = gridApi4;
        //         $scope.gridResize4 = $timeout(function () { $scope.gridApi4.core.handleWindowResize(); }, 500);
        //     },
        //     columnDefs: [{
        //         field: 'Procedure Name',
        //         name: 'Procedure',
        //         width:120,
        //         // 是否显示列头部菜单按钮
        //         enableColumnMenu: false
        //     }, {
        //             field: 'Paragraph Name',
        //             name: ' Paragraph',
        //             // 是否显示列头部菜单按钮
        //             enableColumnMenu: false
        //         }, {
        //             field: 'Number of Clones',
        //             name: ' #Clones',
        //             width:80,
        //             // 是否显示列头部菜单按钮
        //             enableColumnMenu: false
        //         }],
        //         //滚动条事件
        //     customScroller: function myScrolling(uiGridViewport, scrollHandler) {
        //         uiGridViewport.on('scroll', function myScrollingOverride(event) {
        //             // You should always pass the event to the callback since ui-grid needs it
        //             scrollHandler(event);
        //         });
        //     }
        // };
    //右边第二个表格
        // $scope.gridOptions5 = {
        //     enableFiltering: false,
        //     //是否显示grid 菜单
        //     enableGridMenu: false,
        //     //是否点击行任意位置后选中,默认为false,当为true时，checkbox可以显示但是不可选中
        //     enableFullRowSelection: true,
        //     //默认false,选中后是否可以取消选中
        //     noUnselect: true,
        //     //是否可以选择多个,默认为true
        //     multiSelect: false,
        //     //禁止排序
        //     enableSorting: false,
        //     enableRowHeaderSelection: false,
        //     //grid垂直滚动条是否显示, 0-不显示  1-显示
        //     enableVerticalScrollbar: 1,
        //     onRegisterApi: function (gridApi5) {
        //         $scope.gridApi5 = gridApi5;
        //         $scope.gridResize5 = $timeout(function () { $scope.gridApi5.core.handleWindowResize(); }, 500);
        //     },
        //     columnDefs: [{
        //             field: 'Paragraph Names',
        //             name: 'Paragraph',
        //             // 是否显示列头部菜单按钮
        //             enableColumnMenu: false
        //         },{
        //         field: 'Classified Groups',
        //         name: 'Classified Groups',
        //         // 是否显示列头部菜单按钮
        //         enableColumnMenu: false
        //     }],
        //     //滚动条事件
        //     customScroller: function myScrolling(uiGridViewport, scrollHandler) {
        //         uiGridViewport.on('scroll', function myScrollingOverride(event) {
        //             // You should always pass the event to the callback since ui-grid needs it
        //             scrollHandler(event);
        //         });
        //     }
        // };
        //获取右边表格数据
        // $scope.getScatterData = function (opra) {
        //     $http({
        //         method: 'POST',
        //         url: './analysis/output',
        //         data: $.param({ 'outputpath': outputpath, 'filterValue': 'detail' }),
        //         headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        //     }).success(function (data) {
        //         if (data && data.length > 0) {
        //             $scope.gridOptions4.data = data[0].table;
        //             $scope.gridOptions5.data = data[1].table;
        //             //加载TAB数据
        //             if(opra !== 'reloadTable'){
        //                 $scope.tabs4 = data;
        //                 $scope.currentTitle4 = $scope.tabs4[0].title;
        //             }
        //         } else {
        //             $rootScope.ui_grid_scatter = false;
        //         }
        //     }).error(function () {
        //         $rootScope.ui_grid_sscatter = false;
        //     });
        // };
        // //显示图表的类型
        // $rootScope.type='line';
        // //tab切换
        // $scope.onClickTab4 = function (tab) {
        //     $scope.currentTitle4 = tab.title;
        //     $rootScope.curTab=$scope.currentTitle4;
        //     if(tab.title==='Clone'){
        //         $rootScope.type='line';
        //         //重画表格
        //         $timeout(function () { $scope.gridApi4.core.handleWindowResize(); }, 200);
        //     }else{
        //         $rootScope.type='pie';
        //         //重画表格
        //        $timeout(function () { $scope.gridApi5.core.handleWindowResize(); }, 200);
        //     }
        // };
        //  $scope.isActiveTab4 = function (tabTitle) {
        //     return tabTitle === $scope.currentTitle4;
        // };
        //监听$rootScope.ui_grid_summary，渲染表格内容
        // $scope.$watch(function () {
        //     return $rootScope.ui_grid_summary;
        // }, function () {
        //     if ($rootScope.ui_grid_summary) {
        //         $scope.getSummaryData();
        //         $scope.getDetailData();
        //         $scope.getScatterData();
        //     }
        // });

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
    }
    function uiGridLink(scope) {
        scope.width = $window.innerWidth;
        angular.element($window).bind('resize', function () {
            scope.width = $window.innerWidth;
            scope.$digest();
        });
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
})
