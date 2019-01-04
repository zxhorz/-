'use strict';
angular.module('loginApp', []).controller('loginController', function ($scope, $http, $timeout) {
    $scope.signIn = function () {
        var loginDto = {};
        loginDto.userName = $scope.userName;
        loginDto.password = $scope.password;

        $http({
            method: 'POST',
            url: '/login',
            data: loginDto
        }).success(function (data) {
            // window.location.href = 'http://'+window.location.host + '/static/index.html';
        }).error(function (data) {
            console.info(data);
        });
    }
});