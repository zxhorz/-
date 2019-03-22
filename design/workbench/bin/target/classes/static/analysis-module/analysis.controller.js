'use strict';

// AnalysisController
angular.module('analysisModule').factory('locals',['$window',function($window){
      return{        //存储单个属性
        set :function(key,value){
          $window.localStorage[key]=value;
        },        //读取单个属性
        get:function(key,defaultValue){
          return  $window.localStorage[key] || defaultValue;
        },        //存储对象，以JSON格式存储
        setObject:function(key,value){
          $window.localStorage[key]=JSON.stringify(value);
        },        //读取对象
        getObject: function (key) {
          return JSON.parse($window.localStorage[key] || '{}');
        }

      }
  }])
    .controller('AnalysisController', ['$scope', '$rootScope', '$stateParams', '$http', '$modal', '$interval','locals',
    function ($scope, $rootScope, $stateParams, $http, $modal,$interval,locals) {
        // 获取参数
        var name = $stateParams.value;
        var parms = $stateParams.parameters;
        $rootScope.configList=[];
        if(name == ''){
             name=locals.get('lastName');
             parms=locals.getObject('lastParms');
        }
        // 点击Analysis按钮和重新刷新页面时，不触发分析操作
        if (name !== 'Analysis' && name !== '') {
            // 设置全局变量rootScope
            $rootScope.ui_grid_summary = true;
            $rootScope.ui_grid_detail = true;
            if (parms.shinyurl === '') {
                $rootScope.shiny_report_show = false;
            } else {
                $rootScope.shiny_report_show = true;
            }
                locals.set('lastName',name);
                locals.setObject('lastParms',parms);
        $rootScope.configList[name] = parms;
        $rootScope.currentCase = name;
        }
        // 请求至后台
        $http({
            method: 'POST',
            url: './config/pagetemplate',
            data:$.param({'rootpath':parms.rootpath,'outputpath':parms.outputpath,'configpath':parms.configpath}),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
            // 后台成功返回
            console.info(data);
            $rootScope.tml='window-module/template'+data.configurations.pagelayout+'.html';
            // 监控$rootScope.tml变化
            $rootScope.$watch('$rootScope.tml',function() {
                // if (($rootScope.ui_grid_summary && $rootScope.neo4j_window_show)
                //     || ($rootScope.ui_grid_detail && $rootScope.neo4j_window_show)) {
                //     $interval(function(){
                //         angular.element('.leftBox').addClass('bigPartWidth');
                //         angular.element('.rightBox').addClass('smallPartWidth');
                //     },1);
                // }
                // 加载iframe页面
                if ($rootScope.shiny_report_show) {
                    $interval(function(){
                    },1);
                }
            });
        }).error(function (data) {
            console.info(data);
        });
    }]);
