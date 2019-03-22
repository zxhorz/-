'use strict';

angular.module('lineChart').directive('lineChartDirective', function () {

    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'lineChart-module/lineChart.template.html',
        replace: false,
        controller: tabController
    };

    function tabController($scope, $http, $location, $rootScope,$attrs) {
        $scope.showLineChart = true;
        var outputpath;
        var newValue=$attrs.chartdata;
        console.info($scope.$parent.currentTitle4);
        // 获取或更新饼图数据
        $scope.getOrUpdatalineChartData = function () {
            if ($rootScope.currentCase && $rootScope.configList) {
                outputpath = $rootScope.configList[$rootScope.currentCase].outputpath;
            }
                // 折线图
             genScatterChart(outputpath);
           
        };
        function genScatterChart(outputpath) {
            $http({
                method: 'POST',
                url: './analysis/chartdata',
                data: $.param({ 'outputpath': outputpath, 'type': 'line','name': newValue}),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data) {
                    //chart类型
                    $scope.type = data[0].type;
                    //饼图需要的块
                    $scope.labels = data[0].labels;
                    //数据
                    $scope.data = data[0].data;
                    //颜色
                    $scope.colors = data[0].colors;
                    //其他的参数
                    $scope.options = data[0].options;
                    $scope.chartTitle=data[0].name;
                }
            }).error(function () {
            });
        }
        $scope.getOrUpdatalineChartData();
        //放大缩小重新加载图表
        $scope.$on('changeChart', function (){
            $scope.getOrUpdatalineChartData();
        });

    }

});
