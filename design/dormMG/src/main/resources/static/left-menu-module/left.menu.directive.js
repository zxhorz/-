(function(angular) {
    'use strict';
    var app = angular.module('DormManagerApp');

    app.directive('leftMenuDirective',function($parse,$modal,dormManagerConfig,infoDataService) {
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
//                $(".menu .item ul", $(".menu .item").parents("ul:first")).slideUp(350);
//                $(".menu .item").siblings().removeClass("active");
                if(key =='myInfo' && $scope.isAdmin)
                    return;
                $state.go(key)
            }


        $scope.showAuthority = function () {

            var modal = $modal.open({
                backdrop: 'static',
                templateUrl: 'left-menu-module/authority.html',
                controller: 'authorityCtrl', //modal对应的Controller
                size: 'md'
            });
            modal.result.then(function (result) {
                if (result == 'S') {
                    $scope.onModel.modelShow('success', '修改成功')
                } else{
                    $scope.onModel.modelShow('error', result);
                }
            }, function (reason) {
                //        	$state.reload();
            })
        }

        }
    }).controller('authorityCtrl', function ($scope, $http, $state, $rootScope, $modalInstance, $timeout) {
        $scope.authority = {};
        $scope.authority.username ="";
        $scope.$watch('authority.username', function () {
            $http({
                method: 'GET',
                url: '/user/getAuthority',
                params: {
                    'username': $scope.authority.username
                }
            }).success(function(data){
                if(data.message == 'S'){
                    if(data.data == 'admin')
                        $scope.authority.authority = '管理员'
                    else if(data.data == 'user')
                        $scope.authority.authority = '学生'
                }else{
                    $scope.authority.authority = "";
                }
            }).error(function(){
            });
        });
        $scope.submitForm = function () {
            $http({
                method: 'POST',
                url: '/user/updateAuthority',
                data: $.param($scope.authority),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).success(function (data) {
                if (data.message === 'S') {
                    $modalInstance.close('S')
                } else {
                    $modalInstance.close(data.data);
                }
            }).error(function (data) {

            });
        }

        $scope.close = function () {
            $modalInstance.dismiss();
        }
    });


})(angular);
