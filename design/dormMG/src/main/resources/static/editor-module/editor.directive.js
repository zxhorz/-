(function (angular) {
    'use strict';
    var app = angular.module('DormManagerApp');

    app.directive('editorModule', ['$parse', 'dromManagerConfig', function ($parse, dromManagerConfig) {
        return {
            restrict: 'EA',
            templateUrl: 'editor-module/editor.html',
            controller: editorController
        };

        function editorController($scope, $http, $location, $rootScope, $attrs, $state) {
            $scope.tinymceModel = 'Initial content';

            $scope.getContent = function () {
                console.log('Editor content:', $scope.tinymceModel);
            };

            $scope.setContent = function () {
                $scope.tinymceModel = 'Time: ' + (new Date());
            };

            $scope.tinymceOptions = {
                menubar: false,
                toolbar: 'undo redo | formatselect | bold italic backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat',
                language: 'zh_CN',
                content_css: [
                    '//fonts.googleapis.com/css?family=Lato:300,300i,400,400i',
                    '//www.tiny.cloud/css/codepen.min.css'
                ]
            };
        }

    }]);


})(angular);