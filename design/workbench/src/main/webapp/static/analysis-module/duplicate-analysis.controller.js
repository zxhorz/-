'use strict';

angular.module('analysisModule')
    .controller('DuplicateAnalysisController',
        ['$scope', '$stateParams', '$http', '$modal', function($scope, $stateParams, $http, $modal) {
        // 配置后台请求参数
        var path = $stateParams.parameters;
        var params = {
            params: { scriptPath: path.scriptPath, outputPath: path.outputPath, jsonPath: path.menuJsonPath }
        };
        // 后台请求
        var request = $http.get('/analysis/duplicateAnalysis', params);
        // 弹出框，表明正在分析中
        var myModal = $modal({ scope: $scope, templateUrl: 'analysis-module/analyzing.template.html', show: true });
        myModal.show;
        // 分析完成后，弹出提示框，成功或失败
        request.success(function(data) {
            myModal.hide();
            var myModal2 = $modal({
                content: data,
                show: true
            });
            myModal2.show;
        });
    }]);
