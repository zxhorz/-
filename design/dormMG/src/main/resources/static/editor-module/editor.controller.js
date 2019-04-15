(function (angular,$) {
    'use strict';
    var app = angular.module('editorModule', []);

    app.controller('editorController', function ($scope, $http, $rootScope, $state,$timeout) {
        $scope.tinymceModel = '';

        $scope.cancel = function () {
            $state.go('notice')
        };

        var modelContent = '<input id="title" autocomplete="off" ng-model="title" ng-click="addNotice">'

        $scope.submitForm = function () {
            //            $scope.tinymceModel = 'Time: ' + (new Date());
            $scope.onModel.modelLoading('loading', '发布中');
            $http({
                method: 'POST',
                url: '/notice/noticeSave',
                data: $.param($scope.application),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).success(function (data) {
                if (data.message === 'S') {
                    $scope.onModel.modelShow('success','发布成功')
                    $timeout(function() {
                        $state.go('notice');
                    },2000)
                }else{
                    $scope.onModel.modelShow('error','发布失败');
                }
            }).error(function (data) {
                $scope.onModel.modelShow('error','发布失败');
            });
        };

        $scope.goNotice = function () {
            $state.go('notice');
        }

        $scope.tinymceOptions = {
            menubar: false,
            height: 500,
            plugins: "nonbreaking",
            nonbreaking_force_tab: true,
            toolbar: 'undo redo | formatselect | bold italic backcolor | fontselect fontsizeselect | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat',
            language: 'zh_CN',
            content_css: [
                '//fonts.googleapis.com/css?family=Lato:300,300i,400,400i',
                '//www.tiny.cloud/css/codepen.min.css'
            ]
        };


    })


})(angular,$);