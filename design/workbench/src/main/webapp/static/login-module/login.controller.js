'use strict';
angular.module('loginApp', []).controller('loginController', function ($rootScope, $scope, $http, $timeout) {
	$rootScope.show_login = true;
	$rootScope.show_register = false;
	$rootScope.show_forget = false;
	$rootScope.show_activationCode = false;
	$rootScope.show_change = false;
	$rootScope.showError = false;
	$rootScope.showSuccess = false;

    $scope.signIn = function () {
		var loginDto = {};
        loginDto.userName = $scope.userName;
		loginDto.password = $scope.password;
		loginDto.captcha = $scope.captcha_login;

        $http({
            method: 'POST',
            url: '/login/login',
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			data:  $.param(loginDto)
        }).success(function (data) {
			// window.location.href = 'http://'+window.location.host + '/static/index.html';
			if (data.message === 'S'){
				$rootScope.showError = false;
				window.location.href = 'http://' + window.location.host + '/index.html';
			}
			else {
				$scope.changeImg("#imgCaptcha_login");
				$rootScope.msg = data.data;
				$rootScope.showError = true;
			}
        }).error(function (data) {
            console.info(data);
        });
	}
	
	$rootScope.changePassword = function () {
		var loginDto = {};
		if ($scope.newPassword1 === $scope.newPassword2) {
			loginDto.userName = $scope.userName;
			loginDto.password = $scope.oldPassword;
			loginDto.newPassword = $scope.newPassword1;
			$http({
				method: 'POST',
				url: '/login/changePassword',
				headers: {'Content-Type': 'application/x-www-form-urlencoded'},
				data:  $.param(loginDto)
			}).success(function (data) {
				// window.location.href = 'http://'+window.location.host + '/static/index.html';
				if (data.message === 'S')
					window.location.href = 'http://' + window.location.host + '/index.html';
				else {
					$rootScope.msg = "userName/password not correct or the account is not activated!";
					$rootScope.showError = true;
				}
			}).error(function (data) {
				console.info(data);
			});
		} else {
			$rootScope.showError = true;
			$rootScope.msg = "different password";
		}
    }
    
    $rootScope.register = function() {
		var regex = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/;
		if(!regex.test($scope.email)){
			$rootScope.msg = "Invalid Email"
			$rootScope.showError = true;
			return;
		}
        $http({
            method: 'POST',
            url: '/login/register',
            params: {
				'email': $scope.email,
				'captcha': $scope.captcha_register
            }
        }).success(function (data) {
        	$rootScope.msg = data.data;
        	var code = data.code;
        	if (code === "S") {
        		$rootScope.showSuccess = true;
        		$rootScope.showError = false;
        		$timeout(function () {
        			// $rootScope.show_login = !$rootScope.show_login;
	            	// $rootScope.show_register = !$rootScope.show_register;
	            	// $rootScope.userName = $scope.email;
					// $rootScope.msg = "Activate your account in your email and login!";
					window.location.href = 'http://' + window.location.host;
				}, 2000);
        	} else if (code === "E") {
				$rootScope.changeImg("#imgCaptcha_register");
        		$rootScope.showSuccess = false;
        		$rootScope.showError = true;
        	}
        }).error(function (data) {
            console.info(data);
        });
	}
	
    $rootScope.checkForgotActivationCode = function() {
        $http({
            method: 'POST',
            url: '/login/checkForgotActivationCode',
            params: {
				'email': $scope.email,
				'activationCode':$scope.activationCode
            }
        }).success(function (data) {
        	$rootScope.msg = data.data;
        	var message = data.message;
        	if (message === "S") {
				$scope.activationCode = "";
        		$rootScope.showSuccess = false;
        		$rootScope.showError = false;
				$rootScope.show_forget = false;
				$rootScope.show_change = true;
        	} else if (message === "F") {
        		$rootScope.showSuccess = false;
        		$rootScope.showError = true;
        	}
        }).error(function (data) {
            console.info(data);
        });
    }

	$rootScope.changeForgotPassword = function () {
		var loginDto = {};
		if ($scope.newPassword1 === $scope.newPassword2) {
			loginDto.userName = $scope.email;
			loginDto.password = $scope.newPassword1;
			$http({
				method: 'POST',
				url: '/login/changeForgotPassword',
				headers: {'Content-Type': 'application/x-www-form-urlencoded'},
				data:  $.param(loginDto)
			}).success(function (data) {
				$rootScope.msg = data.data;
				if (data.message === 'S'){
					$rootScope.showSuccess = true;
					$scope.userName = loginDto.userName;
					// $scope.password = "";
					$timeout(function () {
						// $rootScope.show_login = !$rootScope.show_login;
						// $rootScope.show_change = !$rootScope.show_change;
						// $rootScope.userName = $scope.email;
						// $rootScope.showError = false;
						// $rootScope.showSuccess = false;
						window.location.href = 'http://' + window.location.host;
					}, 2000);
				}
				else {
					$rootScope.showError = true;
				}
			}).error(function (data) {
				console.info(data);
			});
		} else {
			$rootScope.showError = true;
			$rootScope.msg = "different password";
		}
    }

    $rootScope.forget = function() {
        $http({
            method: 'POST',
            url: '/login/forget',
            params: {
                'email': $scope.email
            }
        }).success(function (data) {
        	$rootScope.msg = data.data;
        	var code = data.code;
        	if (code === "S") {
        		$rootScope.showSuccess = true;
        		$rootScope.showError = false;
				$rootScope.show_activationCode = true;
        	} else if (code === "E") {
        		$rootScope.showSuccess = false;
        		$rootScope.showError = true;
        	}
        }).error(function (data) {
            console.info(data);
        });
    }

	$rootScope.changeImg = function(id) {  
        var imgSrc = $(id);  
        var src = imgSrc.attr("src");  
        imgSrc.attr("src", $rootScope.changeUrl(src));  
	}  
	
    //为了使每次生成图片不一致，即不让浏览器读缓存，加上时间戳  
    $rootScope.changeUrl = function(url) {  
        var timestamp = (new Date()).valueOf();  
        var index = url.indexOf("?",url);  
        if (index > 0) {  
            url = url.substring(index, url.indexOf(url, "?"));  
        }  
        if ((url.indexOf("&") >= 0)) {  
            url = url + "×tamp=" + timestamp;  
        } else {  
            url = url + "?timestamp=" + timestamp;  
        }  
        return url;  
    } 
	
	$rootScope.toggleLogin = function() {
		$rootScope.changeImg("#imgCaptcha_login");
		$scope.userName = "";
		$scope.password = "";
		$scope.oldPassword = "";
		$scope.newPassword1 = "";
		$scope.newPassword2 = "";
		$rootScope.captcha_login = "";
		$rootScope.activationCode = "";
		$rootScope.email = "";
		$rootScope.show_login = true;
		$rootScope.show_register = false;
		$rootScope.show_forget = false;
		$rootScope.show_activationCode = false;
		$rootScope.show_change = false;
		$rootScope.showError = false;
		$rootScope.showSuccess = false;
	}
	
	$rootScope.toggleForget = function() {
		$rootScope.show_login = false;
		$rootScope.show_register = false;
		$rootScope.show_forget = true;
		$rootScope.show_activationCode = false;
		$rootScope.show_change = false;
		$rootScope.showError = false;
		$rootScope.showSuccess = false;
	}

	$rootScope.toggleRegister = function() {
		$rootScope.changeImg("#imgCaptcha_register");
		$rootScope.captcha_register = "";
		$rootScope.show_login = false;
		$rootScope.show_register = true;
		$rootScope.show_forget = false;
		$rootScope.show_activationCode = false;
		$rootScope.show_change = false;
		$rootScope.showError = false;
		$rootScope.showSuccess = false;
	}

	$rootScope.toggleChange = function() {
		$rootScope.show_login = false;
		$rootScope.show_register = false;
		$rootScope.show_forget = false;
		$rootScope.show_activationCode = false;
		$rootScope.show_change = true;
		$rootScope.showError = false;
		$rootScope.showSuccess = false;
	}
});