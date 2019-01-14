'use strict';

// OutputController
angular.module('outputModule')
    .controller('OutputController', ['$rootScope', '$stateParams', '$http', function($rootScope, $stateParams, $http) {
        var name = $stateParams.name;
        // 点击Output或者刷新操作不触发事件
        if (name !== 'Output' && name !== '') {
            // 参数
            var parameters = $stateParams.parameters;
            var params = {
                params: { name: $stateParams.name, fullName: $stateParams.fullName, type: parameters.type }
            };

            var paramName = $stateParams.name;
            // 设置$rootScope参数
            if (paramName === 'Shiny Report') {
                $rootScope.shiny_report_show = true;
            } else if (paramName === 'Summary Report') {
                $rootScope.ui_grid_summary = true;
            } else {
                // 请求至后台
                $http.get('/output', params).success(function(data) {
                    if (data[1] === 'table') {
                        $rootScope.outputTab = data[0];
                    } else if (data[1] === 'chart') {
                        $rootScope.outputGrid = data[0];
                    }
                });
            }
        }
    }])
    .run(['$rootScope', function($rootScope) {
        $rootScope.outputTab = '';
        $rootScope.outputGrid = '';
    }]);
