(function(angular) {
    'use strict';
    var app = angular.module('DormManagerApp');

    app.directive('leftMenuModule', ['$parse', 'dromManagerConfig', function($parse, dromManagerConfig) {
        return {
            restrict: 'EA',
            templateUrl: dromManagerConfig.tplPath + '/left-menu.html',
            controller: leftMenuController
        };

        function leftMenuController($scope, $http, $location, $rootScope,$attrs){
        }
    }]);


})(angular);
