'use strict';

angular.module('smartTableModule').directive('smartTableDirective', function() {

    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'smartTable-module/smartTable.template.html',
        replace: false,
        controller: smartTableController,
        link: smartTableLink
    };

    function smartTableController($scope, $http) {
        $scope.getOrUpdataTableData = function(windowName, titleName) {
            $http.get('/window-module/window.json').success(function(data) {
                // 根据所属窗口和tabTitle，取json中应展示的table数据
                for (var key in data) {
                    if (key === windowName) {
                        var tabOrGridArray = data[key];
                        for (var i = 0, l = tabOrGridArray.length; i < l; i++) {
                            var title = tabOrGridArray[i].title;
                            if (title === titleName) {
                                // 表头
                                $scope.header = tabOrGridArray[i].content.table.header;
                                // undifined说明当前tab或grid展示的数据不是table，json中没有对应的table数据
                                if (typeof($scope.header) !== 'undefined') {
                                    // 表格数据列数
                                    $scope.colspan = $scope.header.length;
                                }
                                $scope.data = tabOrGridArray[i].content.table.data;
                                $scope.displayedCollection = $scope.data;
                            }
                        }
                    }
                }
            });
        };
        //获取detail表格的数据
        $scope.getDetailTableData = function(windowName, titleName) {
            $http.get('/window-module/window.json').success(function(data) {
                // 根据所属窗口和tabTitle，取json中应展示的table数据
                for (var key in data) {
                    if (key === windowName) {
                        var tabOrGridArray = data[key];
                        for (var i = 0, l = tabOrGridArray.length; i < l; i++) {
                            var title = tabOrGridArray[i].title;
                            if (title === titleName) {
                                // 表头
                                $scope.detailHeader = tabOrGridArray[i].content.table.header;
                                // undifined说明当前tab或grid展示的数据不是table，json中没有对应的table数据
                                if (typeof($scope.header) !== 'undefined') {
                                    // 表格数据列数
                                    $scope.colspan = $scope.header.length;
                                }
                                $scope.detailData = tabOrGridArray[i].content.table.data;
                                $scope.rowInTable = $scope.detailData;
                            }
                        }
                    }
                }
            });
        };
        // 根据父scope中的所属窗口和tab两个参数取数据
        $scope.getOrUpdataTableData($scope.belongingWindow, $scope.currentTitle);

        // 当前table包含在tab中，监听父scope(tab组件)中的currentTitle
        // 发生变化即tab切换，当前table需重新获取数据
        if ((typeof($scope.parentIsTab) !== 'undefined') && $scope.parentIsTab) {
            $scope.$watch('currentTitle', function(newValue, oldValue) {
                $scope.getOrUpdataTableData($scope.belongingWindow, newValue);
            });
        }
    }

    function smartTableLink(scope) {
        scope.rowSelected = function () {
            scope.getDetailTableData('output_table_window','opt3');
            scope.selectRow=true;
        };
    }
});
