(function (angular) {
    'use strict';
    var app = angular.module('editorModule', []);

    app.controller('editorController', function ($scope, $http, $rootScope, $state) {
        $scope.tinymceModel = '';

        $scope.getContent = function () {
            console.log('Editor content:', $scope.tinymceModel);
        };

        var modelContent = '<input id="title" ng-model="title" ng-click="addNotice">'

        $scope.setContent = function () {
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
                        $state.go('notice');
                    }
                }).error(function (data) {
                    $('#modalAjax .modal-body').html('An error occurred while communicating with the server. Please try again.');
                });
            });
        };

        $scope.goNotice = function () {
            $state.go('notice');
        }

        $scope.tinymceOptions = {
            menubar: false,
            height: 600,
            toolbar: 'undo redo | formatselect | bold italic backcolor | fontselect fontsizeselect | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat',
            language: 'zh_CN',
            content_css: [
                '//fonts.googleapis.com/css?family=Lato:300,300i,400,400i',
                '//www.tiny.cloud/css/codepen.min.css'
            ]
        };


    })


})(angular);