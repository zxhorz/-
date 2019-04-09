(function (angular) {
    'use strict';
    var app = angular.module('applicationModule', []);

    app.controller('applicationController', function ($scope, $http, $rootScope, $state,$timeout) {
        $scope.tinymceModel = '';
        $scope.types = ['寝室维修','换寝','其他事务']
        $scope.application = {'content':''};
        var modelContent = '<input id="title" autocomplete="off" ng-model="title" ng-click="addNotice">'

        $scope.noticeSave = function () {
            //            $scope.tinymceModel = 'Time: ' + (new Date());
            openModal('/notice/addNotice', '设置该公告的主题', 500, modelContent, '确定', 'addNotice');
            $('#addNotice').on('click', function () {
                $('#modalAjax .loader').show();
                $http({
                    method: 'POST',
                    url: '/notice/noticeSave',
                    params: {
                        'title': $('#title').val(),
                        'content': $scope.tinymceModel
                    }
                }).success(function (data) {
                    if (data.message === 'S') {
                        $('#modalAjax .loader').fadeOut();
                        $scope.onModel.modelShow('success','发布成功')
                        $timeout(function() {
                            $state.go('notice');
                        },1500)
                    }
                }).error(function (data) {
                    $('#modalAjax .modal-body').html('An error occurred while communicating with the server. Please try again.');
                });
            });
        };

        $scope.submitForm = function () {
            $scope.onModel.modelLoading('loading', '提交中');
            $http({
                method: 'POST',
                url: '/application/applicationAdd',
                data: $.param($scope.application),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).success(function (data) {
                if (data.message === 'S') {
                    $scope.onModel.modelShow('success', '提交成功');
                    $timeout(function () {
                        $state.go("application/list");
                    }, 2000)
                } else {
                    $scope.onModel.modelShow('error', '提交失败');
                }
            }).error(function (data) {
                $scope.onModel.modelShow('error', '提交失败');
            });
        }

        $http({
            method: 'GET',
            url: '/myInfo/myInfoGet',
            params: {
                'id':$rootScope.userName
            }
        }).success(function (data) {
            if (data.message === 'S') {
                $scope.application.studentId = data.data.id;
                $scope.application.email = data.data.email;
            }
        }).error(function (data) {

        });


        $scope.tinymceOptions = {
            menubar: false,
            height: 250,
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


})(angular);