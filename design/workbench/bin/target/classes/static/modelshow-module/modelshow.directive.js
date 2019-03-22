'use strict';

angular.module('modelshowModule').directive('modelshowDirective', function ($timeout) {
    return {
        restrict: 'EA',
        scope: false,
        templateUrl: 'modelshow-module/modelshow.html',
        replace: false,
        controller: modelshowController,
        // scope: {
        //     onModel: "="
        //   }
    };
    function modelshowController($scope, $timeout) {
        $scope.onModel = {};
        $scope.onModel.modelShow = function (name, info,time) {
            $scope.part = name;
            $scope.promptInfo = info;
            $scope.display = true;
            if(time){
                $timeout(function () {
                    $scope.display = false;
                }, time);
            }else{
                $timeout(function () {
                    $scope.display = false;
                }, 1000);
            }
        }

        $scope.onModel.modelLoading = function (name, info) {     //显示loading
            $scope.part = name;
            $scope.promptInfo = info;
            $scope.display = true;
        }

        $scope.onModel.modelHide = function (time) {     //隐藏提示框
            if(time){
                $timeout(function () {
                    $scope.display = false;
                }, time);
            }else{
                $scope.display = false;
            }
        }

        $scope.onModel.initModel = function () {     //设置默认值
            $scope.part = 'default';
            $scope.display = false;
        }
    }

})