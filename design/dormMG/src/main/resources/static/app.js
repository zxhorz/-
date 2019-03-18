(function (window, angular) {
    'use strict';
    var app = angular.module('DormManagerApp');

    app.run(['$rootScope', '$state', '$stateParams',
        function ($rootScope, $state, $stateParams) {
            $rootScope.$state = $state;
            $rootScope.$stateParams = $stateParams;
        }
    ]).config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/');
        $stateProvider
            .state('info', {
                url: '/info',
                templateUrl: 'main-content-module/main-content.html',
                controller: 'mainContentController'
            })
            .state('notice', {
                url: '/notice',
                templateUrl: 'notice-module/notice.html',
                controller: 'noticeController'
            })
            .state('editor', {
                url: '/editor',
                templateUrl: 'editor-module/editor.html',
                controller: 'editorController'
            })
            .state('notice/view', {
                url: 'notice/view',
                templateUrl: 'notice-view-module/notice-view.html',
                controller: 'noticeViewController',
                params:{
                    notice:''
                }
            })
    }]);
})(window, angular);