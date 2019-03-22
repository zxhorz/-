'use strict';

angular.module('commonTabModule').directive('commonTabDirective', function () {
    return {
        restrict: 'EA',
        scope:true,
        templateUrl: 'commonTab-module/commonTab.template.html',
        replace: false,
        controller: commonTabController,
        link:function ($scope,$element,$attrs) {
            var select=$attrs.iselect;
            if(select=="true"){
                $element.find(".tabLine").addClass("selected")
                $element.find('.tabContent').removeClass("disnone");
            }
            var index=$element.index();
            var left=180*(index-1)+'px';
            $element.find(".tabName").css('left',left);
            $scope.tabClick=function (event) {
                var t=$(event.target);
                t.addClass('selected');
                $element.siblings().find('.tabLine').removeClass('selected');
                var tc=$(event.target).parent().next();
                if(tc.hasClass('disnone')){
                    tc.removeClass('disnone');                       
                    $element.siblings().find('.tabContent').addClass('disnone');
                }
            }
        }
    };
    function commonTabController($scope, $http, $location, $attrs, $rootScope, $timeout,$compile,$element) {
        var outputpath = '';
         if ($rootScope.currentCase && $rootScope.configList) {
            outputpath = $rootScope.configList[$rootScope.currentCase].outputpath;
        }
         function getParagraphCode(codepath,name,parname,type) {
            $http({
                method: 'POST',
                url: './query/paragraphCode',
                data: $.param({ 'pgmname': name, 'parname':parname }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if(data&&data.length>0){
                $scope.resultCodes = data[0].content.split('\r\n');
                getControlFlow(codepath,name,parname,type);
                }else{
                     $scope.resultCodes = ['Not Available'];
                     $scope.svgUrl = 'uiGrid-module/svg.html';
                }

            });
        };
        function getGraphData(codepath, name, parname, type){
                getParagraphCode(codepath,name,parname,type);
        };
        function getControlFlow(codepath,name,parname,type) {
        //简化版，快速获取svg，需要预先分析所有controlflow
        $http({
            method: 'POST',
            url: './analysis/getsvg',
            data: $.param({ 'pgmname': name, 'case': type, 'paragraph': parname }),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
            if (data !== '') {
                $scope.svgUrl = data;
            } else {
                genControlFlow(codepath,name,parname,type);
            }
        }).error(function (data) {
            console.info(data);
            $scope.svgUrl = 'uiGrid-module/svg.html';
        });
    };

    function genControlFlow(codepath,name,parname,type) {
        $http({
            method: 'POST',
            url: './analysis/controlflow',
            data: $.param({ 'codepath': codepath, 'pgmname': name, 'case': type, 'paragraph': parname }),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
            if(data!==''){
            $scope.svgUrl = data;
            }else{
            $scope.svgUrl = 'uiGrid-module/svg.html';
            }
        }).error(function (data) {
            console.info(data);
            $scope.svgUrl = '';
        });
    };
         $scope.table0= {
            enableFiltering: false,
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
            onRegisterApi: function (gridApi6) {
                $scope.gridApi6 = gridApi6;
                $scope.gridResize6 = $timeout(function () { $scope.gridApi6.core.handleWindowResize(); }, 500);
                var st=$attrs.selectable;
                if(st==="0"){
                    gridApi6.selection.on.rowSelectionChanged($scope, function (row) {
                    angular.element('.listGridStyle').find('.ui-grid-row').eq(0).removeClass('ui-grid-row-selected');
                    $scope.resultCodes =[];
                    $scope.svgUrl='';
                    var columnName0 = row.grid.columns[0].name;
                    var name = row.entity[columnName0];
                    //选中行第一列的内容
                    $scope.selectedRowName =  name;
                    var columnName1 = row.grid.columns[1].name;
                    var parname = row.entity[columnName1];
                    var codepath = '';
                    if ($rootScope.configList && $rootScope.currentCase) {
                        codepath = $rootScope.configList[$rootScope.currentCase].codepath;
                    }
                    var type = '';
                    if (codepath.indexOf('business_case_1')) {
                        type = 'business_case_1';
                    } else if (codepath.indexOf('business_case_2')) {
                        type = 'business_case_2';
                    } else if (codepath.indexOf('business_case_3')) {
                        type = 'business_case_3';
                    }
                    getGraphData(codepath, name, parname,type);
                });
                }
            },
                //滚动条事件
            customScroller: function myScrolling(uiGridViewport, scrollHandler) {
                uiGridViewport.on('scroll', function myScrollingOverride(event) {
                    // You should always pass the event to the callback since ui-grid needs it
                    scrollHandler(event);
                });
            }
        };
        $scope.table1= {
            enableFiltering: false,
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
            onRegisterApi: function (gridApi6) {
                $scope.gridApi6 = gridApi6;
                $scope.gridResize6 = $timeout(function () { $scope.gridApi6.core.handleWindowResize(); }, 500);
                var st=$attrs.selectable;
                if(st=="1"){
                    gridApi6.selection.on.rowSelectionChanged($scope, function (row) {
                    angular.element('.listGridStyle').find('.ui-grid-row').eq(0).removeClass('ui-grid-row-selected');
                    $scope.resultCodes =[];
                    $scope.svgUrl='';
                    var columnName0 = row.grid.columns[0].name;
                    var name = row.entity[columnName0];
                    //选中行第一列的内容
                    $scope.selectedRowName =  name;
                    var columnName1 = row.grid.columns[1].name;
                    var parname = row.entity[columnName1];
                    var codepath = '';
                    if ($rootScope.configList && $rootScope.currentCase) {
                        codepath = $rootScope.configList[$rootScope.currentCase].codepath;
                    }
                    var type = '';
                    if (codepath.indexOf('business_case_1')) {
                        type = 'business_case_1';
                    } else if (codepath.indexOf('business_case_2')) {
                        type = 'business_case_2';
                    } else if (codepath.indexOf('business_case_3')) {
                        type = 'business_case_3';
                    }
                    getGraphData(codepath, name, parname,type);
                });
                }
            },
                //滚动条事件
            customScroller: function myScrolling(uiGridViewport, scrollHandler) {
                uiGridViewport.on('scroll', function myScrollingOverride(event) {
                    // You should always pass the event to the callback since ui-grid needs it
                    scrollHandler(event);
                });
            }
        };
        var data=$attrs.tabdata;
        var data2=data.split(",");
        var data3="";
        var text=$attrs.text;
        var includeBox="."+$attrs.includein;
        var boxHeight=$(includeBox).height()-55;
        var ls=$attrs.ls;
        $scope.tabName=$attrs.tabname;
            console.log(data);
            var type=$attrs.showtype;
            var type2=type.split(',');
            var len=type2.length;
            var html="";
            for(var i=0;i<len;i++){
                var t=type2[i]
                if(t=='table'){
                    html += "<div ui-grid='table"+ls+"' class='summaryGrid table' ui-grid-selection ui-grid-auto-resize style='width:"+100/len+"%;height:"+boxHeight+"px'></div>"
                    data3=data2[i]?data2[i]:data3;
                }else if(t=='piechart'){
                    html += "<pie-chart-directive chartdata='"+data2[i]+"'></pie-chart-directive>"
                }else if (t=='linechart'){
                    html += "<line-chart-directive chartdata='"+data2[i]+"'></line-chart-directive>"
                }else if (t=='text'){
                    html += '<div class="summaryText" style="width:'+96/len+'%">'+text+'</div>'
                }else if(t=="svg"){
                    html +="<div class='svg' style='width:"+100/len+"%'><h3>Flow</h3><div class='svgBox'><div id='ide-code-controlflow' class='svgclass' ng-include='svgUrl'></div></div></div>"
                }else if(t=='code'){
                    html +="<div class='code' style='width:"+100/len+"%'> <h3>Code</h3><div class='codeBox'><pre ng-repeat='line in resultCodes track by $index'>{{line}}</pre></div></div>"
                }
            }
            //获取表格数据
         $scope.getScatterData = function () {
            $http({
                method: 'POST',
                url: './analysis/output',
                data: $.param({ 'outputpath': outputpath, 'filterValue': data3 }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data && data.length > 0) {
                    $scope.table0.data = data[0].table;
                    if(data.length>1){
                        $scope.table1.data = data[1].table;
                    }
                } else {
                    $rootScope.ui_grid_scatter = false;
                }
            }).error(function () {
                $rootScope.ui_grid_sscatter = false;
            });
        };
        $scope.$watch(function () {
            return $rootScope.ui_grid_summary;
        }, function () {
            if ($rootScope.ui_grid_summary) {
                $scope.getScatterData();
            }
        });    
            var template = angular.element(html);
            var mobileDialogElement = $compile(template)($scope);
            $element.find(".tabContent").append(mobileDialogElement);
             $scope.getScatterData();
    }
});
