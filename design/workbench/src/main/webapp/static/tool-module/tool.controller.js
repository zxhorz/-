'use strict';

angular.module('toolModule').factory('locals',['$window',function($window){
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
    .controller('ToolController', ['$scope', '$rootScope', '$stateParams','locals', function ($scope, $rootScope, $stateParams,locals) {
        console.info($stateParams);
        $rootScope.tml='window-module/template5.html';

        var value = $stateParams.value;
         if(value == ''){
             value=locals.get('tool-value');
        }else{
            locals.set('tool-value',value);
        }
        $scope.show = false;
        $scope.funcUrl = '';
        if (value === 'Prediction') {
            $scope.funcUrl = 'http://'+window.location.host+'/predict';
            $scope.show = true;
        } else if (value === 'Code Search') {
            $scope.funcUrl = 'http://'+window.location.host+'/search';
            $scope.show = true;
        }else if (value === 'Program Search') {
            $scope.funcUrl = 'http://'+window.location.host+'/programsearch';
            $scope.show = true;
        }else if (value === 'Cost Estimation') {
            $scope.funcUrl = 'http://'+window.location.host+'/cost-estimation';
            $scope.show = true;
        }else if (value === 'Clone Code') {
            $scope.funcUrl = 'http://'+window.location.host+'/clonecode';
            $scope.show = true;
        }else if (value == 'Neo4j') {
            // $scope.neo = true;
            // $scope.show = false;
            window.open('http://127.0.0.1:7474','newwindow');
        }
    }]);
