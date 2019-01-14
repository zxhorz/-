'use strict';

angular.module('gridModule').directive('gridDirective', function() {

    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'grid-module/grid.template.html',
        replace: false,
        controller: gridController,
        link: gridLink
    };

    function gridController($scope, $http, $location, $attrs, $rootScope) {

        // 所属窗口
        var belongingWindow = $attrs.includein;
        $scope.belongingWindow = belongingWindow;

        // 根据所属窗口，取父scope（window组件）中的对应style数据
        // 判断当前所属窗口展示形式（grid or tab），决定当前grid组件是否展示
        var showStyle;
        if (belongingWindow === 'output_graph_window') {
            showStyle = $scope.output_graph_window_style;

            //*******临时（暂时默认output graph窗口只展示饼图，output table窗口只展示table，neo4j只展示text）***********
            $scope.showPieChart = true;
            $scope.showSmartTable = false;
            $scope.showNeo4j = false;

            // 监听父scope（window组件）中窗口展示形式切换事件
            // 改变$scope.isGridStyle的值
            $scope.$watch('output_graph_window_style', function(newValue) {
                if (typeof($scope.isGridStyle) !== 'undefined') {
                    if (newValue === 'grid') {
                        $scope.isGridStyle = true;
                    } else {
                        $scope.isGridStyle = false;
                    }
                }
            });

        } else if (belongingWindow === 'output_table_window') {
            showStyle = $scope.output_table_window_style;

            //*******临时（暂时默认output graph窗口只展示饼图，output table窗口只展示table，neo4j只展示text）***********
            $scope.showPieChart = false;
            $scope.showSmartTable = true;
            $scope.showNeo4j = false;

            // 监听父scope（window组件）中窗口展示形式切换事件
            // 改变$scope.isGridStyle的值
            $scope.$watch('output_table_window_style', function(newValue) {
                if (typeof($scope.isGridStyle) !== 'undefined') {
                    if (newValue === 'grid') {
                        $scope.isGridStyle = true;
                    } else {
                        $scope.isGridStyle = false;
                    }
                }
            });

        } else if (belongingWindow === 'neo4j_window') {
            showStyle = $scope.neo4j_window_style;

            //*******临时（暂时默认output graph窗口只展示饼图，output table窗口只展示table，neo4j只展示text）***********
            $scope.showPieChart = false;
            $scope.showSmartTable = false;
            $scope.showNeo4j = true;

            // 监听父scope（window组件）中窗口展示形式切换事件
            // 改变$scope.isGridStyle的值
            $scope.$watch('neo4j_window_style', function(newValue) {
                if (typeof($scope.isGridStyle) !== 'undefined') {
                    if (newValue === 'grid') {
                        $scope.isGridStyle = true;
                    } else {
                        $scope.isGridStyle = false;
                    }
                }
            });

        }
        if (showStyle === 'grid') {
            $scope.isGridStyle = true;
        } else {
            $scope.isGridStyle = false;
        }

        $http.get('/window-module/window.json').success(function(data) {

            // 判断当前是哪个窗口，取json中当前窗口应展示的数据
            if (belongingWindow === 'output_graph_window') {
                $scope.grids = data.output_graph_window;
            } else if (belongingWindow === 'output_table_window') {
                $scope.grids = data.output_table_window;
            } else if (belongingWindow === 'neo4j_window') {
                $scope.grids = data.neo4j_window;
            }

        });

        $scope.$watch(function() {
            return $rootScope.outputGrid;
        }, function() {
            $http.get('/window-module/window.json').success(function(data) {

                // 判断当前是哪个窗口，取json中当前窗口应展示的数据
                if (belongingWindow === 'output_graph_window') {
                    $scope.grids = data.output_graph_window;
                } else if (belongingWindow === 'output_table_window') {
                    $scope.grids = data.output_table_window;
                } else if (belongingWindow === 'neo4j_window') {
                    $scope.grids = data.neo4j_window;
                }
            });
        });

        // 标记当前不是tab组件
        // 其子组件不需要监听currentTitle变量（即tab切换)
        $scope.parentIsTab = false;

    }

    function gridLink() {
        // grid可拖拽排序
        $('.sortableGrids').sortable();
    }

});
