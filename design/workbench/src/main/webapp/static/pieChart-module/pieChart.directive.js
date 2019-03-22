'use strict';

angular.module('pieChart').directive('pieChartDirective', function () {

    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'pieChart-module/pieChart.template.html',
        replace: false,
        controller: tabController
    };

    function tabController($scope, $http, $location, $rootScope,$attrs) {
        $scope.showPieChart = true;
        var outputpath;
        var newValue=$attrs.chartdata;
        console.info('newvalue,' + newValue);
        //监控tab切换
        // $scope.$watch('$parent.currentTitle4', function (newValue, oldValue) {
        //     if (typeof (newValue) !== 'undefined' && newValue !== '') {
        //         $scope.getOrUpdataPieChartData(newValue);
        //     }
        // });
        console.info($scope.$parent.currentTitle4);
        // 获取或更新饼图数据
        $scope.getOrUpdataPieChartData = function () {
            if ($rootScope.currentCase && $rootScope.configList) {
                outputpath = $rootScope.configList[$rootScope.currentCase].outputpath;
            }
            genPieChart(newValue,outputpath);
            // if ($rootScope.type === 'line') {
            //     // 折线图
            //     genScatterChart(newValue,outputpath);
            // } else if ($rootScope.type === 'pie') {
            //     // 饼图
            //     genPieChart(newValue,outputpath);
            // }
        };

        function genPieChart(newValue, outputpath) {
            $http({
                method: 'POST',
                url: './analysis/chartdata',
                data: $.param({ 'outputpath': outputpath, 'type': 'pie','name': newValue}),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                if (data) {
                    //chart类型
                    $scope.type1 = data[0].type;
                    //饼图需要的块
                    $scope.labels1 = data[0].labels;
                    //数据
                    $scope.data1 = data[0].data;
                    //颜色
                    $scope.colors1 = data[0].colors;
                    //其他的参数
                    $scope.options1 = data[0].options;
                    //表格标题
                    // $scope.chartTitle=data[0].name;
                    $scope.chartTitle = newValue + ' Analysis';
                }
            }).error(function () {
            });
        }
        $scope.getOrUpdataPieChartData();
        // function genScatterChart(newValue, outputpath) {
        //     $http({
        //         method: 'POST',
        //         url: './analysis/chartdata',
        //         data: $.param({ 'outputpath': outputpath, 'type': 'line','name': newValue}),
        //         headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        //     }).success(function (data) {
        //         if (data) {
        //             //chart类型
        //             $scope.type = data[0].type;
        //             //饼图需要的块
        //             $scope.labels = data[0].labels;
        //             //数据
        //             $scope.data = data[0].data;
        //             //颜色
        //             $scope.colors = data[0].colors;
        //             //其他的参数
        //             $scope.options = data[0].options;
        //             $scope.chartTitle=data[0].name;
        //         }
        //     }).error(function () {
        //     });
        // }
        //放大缩小重新加载图表
        $scope.$on('changeChart', function (){
            $scope.getOrUpdataPieChartData();
        });

    }

});
