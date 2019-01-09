'use strict';
angular.module('loginApp', []).controller('loginController', function ($rootScope, $scope, $http, $timeout) {
	
	$rootScope.show_login = true;
	$rootScope.show_register = false;
	$rootScope.showRegsiterError = false;
	$rootScope.showRegsiterSuccess = false;
	$rootScope.showActivationSuccess = false;
	$rootScope.showActivationError = false;
	$rootScope.showAccountError = true;
	
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
    
    $rootScope.register = function() {
        $http({
            method: 'POST',
            url: '/register',
            params: {
                'email': $scope.email
            }
        }).success(function (data) {
        	$rootScope.register_msg = data.data;
        	var code = data.code;
        	if (code === "S") {
        		$rootScope.showRegsiterSuccess = true;
        		$rootScope.showRegsiterError = false;
        		$timeout(function () {
        			$rootScope.show_login = !$rootScope.show_login;
	            	$rootScope.show_register = !$rootScope.show_register;
	            	$rootScope.showAccountError = false;
	            	$rootScope.userName = $scope.email;
	            	$rootScope.showActivationSuccess = true;
	            	$rootScope.showActivationError = false;
	            	$rootScope.activation_msg = "Activate your account in your email and login!";
				}, 2000);
        	} else if (code === "E") {
        		$rootScope.showRegsiterSuccess = false;
        		$rootScope.showRegsiterError = true;
        	}
        }).error(function (data) {
            console.info(data);
        });
    }
    
    $rootScope.toggleLoginAndRegister = function() {
    	$rootScope.show_login = !$rootScope.show_login;
    	$rootScope.show_register = !$rootScope.show_register;
    	$rootScope.showRegsiterError = false;
    	$rootScope.showRegsiterSuccess = false;
    	$rootScope.showAccountError = false;
    }
    
});