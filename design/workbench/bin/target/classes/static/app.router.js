'use strict';

/**
 * Config for the router
 */
angular.module('mainApp')
    .run(
    ['$rootScope', '$state', '$stateParams',
        function ($rootScope, $state, $stateParams) {
            $rootScope.$state = $state;
            $rootScope.$stateParams = $stateParams;
        }
    ]
    )
    .config(
    ['$stateProvider', '$urlRouterProvider',
        function ($stateProvider, $urlRouterProvider) {
            $urlRouterProvider
                .otherwise('/');
            $stateProvider
                .state('Output', {
                    url: '/output',
                    controller: 'OutputController'
                })
                .state('project/select', {
                    url: '/project/select',
                    templateUrl: 'projectSelect-module/projectSelect.html',
                    controller: 'projectSelectController'
                })
                .state('project/create', {
                    url: '/project/create',
                    templateUrl: 'projectCreate-module/projectCreate.html',
                    controller: 'projectCreateController',
                    params: {
                        name: '',
                        fullName: '',
                        parameters: ''
                    }
                })
                .state('project/summary', {
                    url: '/project/summary',
                    templateUrl: 'project-module/project.html',
                    controller: 'projectController',
                    params: {
                        name: '',
                        fullName: '',
                        parameters: ''
                    }
                })
                 .state('codeBrowser', {
                     url: '/codeBrowser',
                     templateUrl: 'code-browser-module/codeBrowser.html',
                     controller: 'codeBrowserController',
                     params: {
                         paragraphName: '',
                         projectId:'',
                         location:'',
                         endLine:'',
                         startLine:'',
                         definitionStart:'',
                         definitionEnd:''
                     }
                 })
                 .state('detail', {
                    url: '/detail?tab',
                    templateUrl: 'detail-module/detail.html',
                    params: {
                        tab:''
                    }
                })
                .state('codeSearch', {
                    url: '/codeSearch',
                    templateUrl: 'codeSearch-module/codeSearch.html',
                    controller: 'codeSearchController',
                    params: {
                        name:''
                    }
                })
                 .state('cloneCode', {
                     url: '/cloneCode',
                     templateUrl: 'clonecode-module/cloneCode.new.html',
                     controller: 'clonecodeController'
                 })
                 .state('corpus', {
                     url: '/corpus',
                     templateUrl: 'corpus-module/corpus.html',
                     controller: 'corpusController'
                 })
                 .state('costEstimation', {
                    url: '/costEstimation',
                    templateUrl: 'costEstimation-module/costEstimation.html',
                    controller: 'codeEstimationController'
                }).state('predict', {
                    url: '/predict',
                    templateUrl: 'predict-module/predict.html',
                    controller: 'predictController'
                }).state('script', {
                    url: '/script',
                    templateUrl: 'script-module/script.html',
                    controller: 'scriptController'
                });
        }
    ]
    );
