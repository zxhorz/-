'use strict';

angular.module('tabModule').directive('tabDirective', function() {

    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'tab-module/tab.template.html',
        replace: false,
        controller: tabController
    };

    function tabController($scope, $http, $location, $attrs, $rootScope) {
        // 所属窗口
        var belongingWindow = $attrs.includein;
        $scope.belongingWindow = belongingWindow;

        // 根据所属窗口，取父scope（window组件）中的对应style数据
        // 判断当前所属窗口展示形式（grid or tab），决定当前tab组件是否展示
        var showStyle;
        if (belongingWindow === 'output_graph_window') {
            // 当前tab在output_graph_window窗口
            // 取output_graph_window_style
            showStyle = $scope.output_graph_window_style;

            //*******临时（暂时默认output graph窗口只展示饼图，
            //output table窗口只展示table，neo4j只展示text）***********
            $scope.showPieChart = true;
            $scope.showSmartTable = false;
            $scope.showNeo4j = false;

            // 监听父scope（window组件）中窗口展示形式切换事件
            // 改变$scope.isTabStyle的值
            $scope.$watch('output_graph_window_style', function(newValue) {
                if (typeof($scope.isTabStyle) !== 'undefined') {
                    if (newValue === 'tab') {
                        $scope.isTabStyle = true;
                    } else {
                        $scope.isTabStyle = false;
                    }
                }
            });
        } else if (belongingWindow === 'output_table_window') {
            // 当前tab在output_table_window窗口
            // 取output_table_window_style
            showStyle = $scope.output_table_window_style;

            //*******临时（暂时默认output graph窗口只展示饼图，
            //output table窗口只展示table，neo4j只展示text）***********
            $scope.showPieChart = false;
            $scope.showSmartTable = true;
            $scope.showNeo4j = false;

            // 监听父scope（window组件）中窗口展示形式切换事件
            // 改变$scope.isTabStyle的值
            $scope.$watch('output_table_window_style', function(newValue) {
                if (typeof($scope.isTabStyle) !== 'undefined') {
                    if (newValue === 'tab') {
                        $scope.isTabStyle = true;
                    } else {
                        $scope.isTabStyle = false;
                    }
                }
            });

        } else if (belongingWindow === 'neo4j_window') {
            // 当前tab在neo4j_window窗口
            // 取neo4j_window_style
            showStyle = $scope.neo4j_window_style;

            //*******临时（暂时默认output graph窗口只展示饼图，
            //output table窗口只展示table，neo4j只展示text）***********
            $scope.showPieChart = false;
            $scope.showSmartTable = false;
            $scope.showNeo4j = true;
            $scope.tabtitle =   'neo4j_window';
            // 监听父scope（window组件）中窗口展示形式切换事件
            // 改变$scope.isTabStyle的值
            $scope.$watch('neo4j_window_style', function(newValue) {
                if (typeof($scope.isTabStyle) !== 'undefined') {
                    if (newValue === 'tab') {
                        $scope.isTabStyle = true;
                    } else {
                        $scope.isTabStyle = false;
                    }
                }
            });
        }
        if (showStyle === 'tab') {
            $scope.isTabStyle = true;
        } else {
            $scope.isTabStyle = false;
        }

        $http.get('/window-module/window.json').success(function(data) {

            // 判断当前是哪个窗口，取json中当前窗口应展示的数据
            if (belongingWindow === 'output_graph_window') {
                $scope.tabs = data.output_graph_window;
            } else if (belongingWindow === 'output_table_window') {
                $scope.tabs = data.output_table_window;
            } else if (belongingWindow === 'neo4j_window') {
                $scope.tabs = data.neo4j_window;
            }
            $scope.currentTitle = $scope.tabs[0].title;
            $scope.currentContent = $scope.tabs[0].content;

        });

        $scope.onClickTab = function(tab) {
            $scope.currentTitle = tab.title;
            $scope.currentContent = tab.content;
        };

        $scope.isActiveTab = function(tabTitle) {
            return tabTitle === $scope.currentTitle;
        };

        $scope.$watch(function() {
            return $rootScope.outputTab;
        }, function(newValue) {
            $http.get('/window-module/window.json').success(function(data) {

                // 判断当前是哪个窗口，取json中当前窗口应展示的数据
                if ($scope.belongingWindow === 'output_graph_window') {
                    $scope.tabs = data.output_graph_window;
                } else if ($scope.belongingWindow === 'output_table_window') {
                    $scope.tabs = data.output_table_window;
                } else if ($scope.belongingWindow === 'neo4j_window') {
                    $scope.tabs = data.neo4j_window;
                }
                for (var key in $scope.tabs) {
                    if ($scope.tabs[key].title === newValue) {
                        $scope.currentTitle = $scope.tabs[key].title;
                        $scope.currentContent = $scope.tabs[key].content;
                        break;
                    }
                }
            });
        });

        $scope.$watch(function() {
            return $rootScope.outputGrid;
        }, function(newValue) {
            $http.get('/window-module/window.json').success(function(data) {
                // 判断当前是哪个窗口，取json中当前窗口应展示的数据
                if ($scope.belongingWindow === 'output_graph_window') {
                    $scope.tabs = data.output_graph_window;
                } else if ($scope.belongingWindow === 'output_table_window') {
                    $scope.tabs = data.output_table_window;
                } else if ($scope.belongingWindow === 'neo4j_window') {
                    $scope.tabs = data.neo4j_window;
                }
                for (var key in $scope.tabs) {
                    if ($scope.tabs[key].title === newValue) {
                        $scope.currentTitle = $scope.tabs[key].title;
                        $scope.currentContent = $scope.tabs[key].content;
                        break;
                    }
                }
            });
        });

        // 标记当前为tab组件
        // 其子组件监听currentTitle变量（即tab切换)
        // 变化时重新获取tab的数据
        $scope.parentIsTab = true;

    }

});
