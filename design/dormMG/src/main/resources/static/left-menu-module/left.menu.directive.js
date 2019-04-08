(function(angular) {
    'use strict';
    var app = angular.module('DormManagerApp');

    app.directive('leftMenuDirective',function($parse, dormManagerConfig,infoDataService) {
        return {
            restrict: 'EA',
            templateUrl: 'left-menu-module/left-menu.html',
            controller: leftMenuController
        };

        function leftMenuController($scope, $http, $location,infoDataService, $rootScope,$attrs,$state){
            $http({
                method: 'GET',
                url: '/user/getUser',
            }).success(function(data){
                if(data.message == 'S'){
                    $rootScope.userName = data.data['userName']
                    infoDataService.setUser(data.data);
                }
            }).error(function(){
            });

            $state.go('info');


            $scope.switchToInfo = function(){
                $state.go('info')
            }
            $scope.switchToNotice = function(){
                $state.go('notice')
            }
            $scope.switchToMyInfo = function(){
                $state.go('myInfo')
            }


            $rootScope.switchToPage = function(key){
                $state.go(key)
            }

        }
    });


})(angular);
