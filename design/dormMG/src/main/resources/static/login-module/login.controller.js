'use strict';
angular.module('loginApp', []).controller('loginController', function ($rootScope,$location, $scope, $http,$interval,$timeout) {
	$rootScope.show_login = true;
	$rootScope.show_register = false;
	$rootScope.show_forget = false;
	$rootScope.show_change = false;
	$rootScope.showError = false;
	$rootScope.showSuccess = false;
	$rootScope.key = null;
	$rootScope.activation_button = "获取验证码";
	var second = 60,timePromise = undefined;
	getToken();
	getKey();


	function getKey() {
		$http({
			method: 'GET',
			url: '/login/getKey',
		}).success(function (data) {
			// window.location.href = 'http://'+window.location.host + '/static/index.html';
			if (data.message === 'S') {
				$rootScope.key = data.data;
			} else {
				$rootScope.msg = "Error";
				$rootScope.showError = true;
			}
		}).error(function (data) {
			console.info(data);
		});
	}

	function getToken(){
		var token = $location.absUrl();
		if(token){
			$http({
				method: 'POST',
				url: '/login/getMessage',
				params: {
					'token': token
				}
			}).success(function (data) {
				// window.location.href = 'http://'+window.location.host + '/static/index.html';
				if (data.message === 'S'){
					var user = data.data;
					$rootScope.userName = user.username;
					$rootScope.password = user.password;
				}
				else if (data.message === 'N'){
				}
				else {
					$rootScope.msg = "链接错误或超时";
					$rootScope.showError = true;
					$timeout(function () {
						window.location.href = 'http://' + window.location.host;
					}, 3000);
				}
			}).error(function (data) {
				console.info(data);
			});
		}
	}

    $scope.signIn = function () {
    	var loginDto = {};
    	loginDto.userName = $scope.userName;
    	loginDto.password = $scope.password;
    	loginDto.captcha = $scope.captcha_login;

    	if (!loginDto.password && !loginDto.userName) {
    		$rootScope.msg = "请输入用户名/密码"
    		$rootScope.showError = true;
    	} else if (!loginDto.password) {
    		$rootScope.msg = "请输入密码"
    		$rootScope.showError = true;
    	} else if (!loginDto.userName) {
    		$rootScope.msg = "请输入用户名"
    		$rootScope.showError = true;
    	} else if (!loginDto.captcha){
    		$rootScope.msg = "请输入验证码"
    		$rootScope.showError = true;
    	}else {
			var encrypt = new JSEncrypt();
			encrypt.setPublicKey($rootScope.key);
			loginDto.userName = encrypt.encrypt(loginDto.userName);
			loginDto.password = encrypt.encrypt(loginDto.password);
			loginDto.captcha = encrypt.encrypt(loginDto.captcha);
    		$http({
    			method: 'POST',
    			url: '/login/login',
    			headers: {
    				'Content-Type': 'application/x-www-form-urlencoded'
    			},
    			data: $.param(loginDto)
    		}).success(function (data) {
    			// window.location.href = 'http://'+window.location.host + '/static/index.html';
    			if (data.message === 'S') {
    				$rootScope.showError = false;
    				window.location.href = 'http://' + window.location.host + '/index.html';
    			} else {
    				$scope.changeImg("#imgCaptcha_login");
    				$rootScope.msg = data.data;
    				$rootScope.showError = true;
    			}
    		}).error(function (data) {
    			console.info(data);
    		});
    	}
    }
	
	$rootScope.changePassword = function () {
		var loginDto = {};
		if (checkPassword()) {
			loginDto.userName = $scope.email;
			loginDto.newPassword = $scope.newPassword1;

			var encrypt = new JSEncrypt();
			encrypt.setPublicKey($rootScope.key);
			loginDto.userName = encrypt.encrypt(loginDto.userName);
			loginDto.newPassword = encrypt.encrypt(loginDto.newPassword);
			$http({
				method: 'POST',
				url: '/login/changePassword',
				headers: {'Content-Type': 'application/x-www-form-urlencoded'},
				data:  $.param(loginDto)
			}).success(function (data) {
				// window.location.href = 'http://'+window.location.host + '/static/index.html';
			    $rootScope.msg = data.data;
				if (data.message === 'S'){
				    $rootScope.showSuccess = true;
				    $timeout(function () {
                        window.location.href = 'http://' + window.location.host;
        			}, 3000);
				}
				else {
					$rootScope.showError = true;
				}
			}).error(function (data) {
				console.info(data);
			});
		} else {
			$rootScope.showError = true;
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
					window.location.href = 'http://' + window.location.host;
				}, 3000);
        	} else if (code === "E") {
				$rootScope.changeImg("#imgCaptcha_register");
        		$rootScope.showSuccess = false;
        		$rootScope.showError = true;
        	}
        }).error(function (data) {
            console.info(data);
        });
	}
	
	$rootScope.checkForgotActivationCode = function () {
		if ($scope.email) {
			$http({
				method: 'POST',
				url: '/login/checkForgotActivationCode',
				params: {
					'email': $scope.email,
					'activationCode': $scope.activationCode?$scope.activationCode:""
				}
			}).success(function (data) {
				$rootScope.msg = data.data;
				var message = data.message;
				if (message === "S") {
					$rootScope.showSuccess = false;
					$rootScope.showError = false;
					$rootScope.show_forget = false;
					$rootScope.show_change = true;
				} else if (message === "F") {
					$rootScope.showSuccess = false;
					$rootScope.showError = true;
				}
				$scope.activationCode = "";
			}).error(function (data) {
				console.info(data);
			});
		} else {
			$rootScope.msg = "请输入用户名";
			$rootScope.showError = true;
		}
	}

    $rootScope.forget = function () {
    	if ($scope.email) {
    		timePromise = $interval(function () {
    			if (second <= 0) {
    				$interval.cancel(timePromise);
    				timePromise = undefined;
    				second = 60;
    				$scope.activation_button = "重发验证码";
    				$scope.canClick = false;
    			} else {
    				$scope.activation_button = second + "秒后可重发";
    				$scope.canClick = true;
    				second--;
    			}
    		}, 1000, 100);
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
    			} else if (code === "E") {
    				$rootScope.showSuccess = false;
    				$rootScope.showError = true;
    			}
    		}).error(function (data) {
    			console.info(data);
    		});
    	} else {
    		$rootScope.msg = "请输入用户名";
    		$rootScope.showError = true;
    	}
    }

	$rootScope.index = function(){
		window.location.href = 'http://' + window.location.host;
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
	
	function checkPassword() {

		if ($scope.newPassword1 !== $scope.newPassword2) {
			$rootScope.msg = "密码不一致";
			return false;
		}

		if (!$scope.newPassword1 || !$scope.newPassword2) {
			$rootScope.msg = "请输入密码";
			return false;
		}

		if (!$scope.newPassword1 || $scope.newPassword1.length < 6) {
			$rootScope.msg = "密码长度不可以小于6";
			return false;
		}

		if (!$scope.newPassword1.match(/(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])/)) {
			$rootScope.msg = "密码过于简单(需包含大小写、数字)";
			return false;
		}

		return true;
	}

	$scope.$watch('newPassword1', function () {
		if (!$scope.newPassword1 && !$scope.newPassword2) {
			$rootScope.showError = false;
			return;
		}
		if (!checkPassword())
			$rootScope.showError = true;
		else
			$rootScope.showError = false;
	});

	$scope.$watch('newPassword2', function () {
		if (!$scope.newPassword1 && !$scope.newPassword2) {
			$rootScope.showError = false;
			return;
		}
		if (!checkPassword())
			$rootScope.showError = true;
		else
			$rootScope.showError = false;
	});

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
		$rootScope.show_change = false;
		$rootScope.showError = false;
		$rootScope.showSuccess = false;
	}
	
	$rootScope.toggleForget = function() {
		$rootScope.show_login = false;
		$rootScope.show_register = false;
		$rootScope.show_forget = true;
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
		$rootScope.show_change = false;
		$rootScope.showError = false;
		$rootScope.showSuccess = false;
	}

	$rootScope.toggleChange = function() {
		$rootScope.show_login = false;
		$rootScope.show_register = false;
		$rootScope.show_forget = false;
		$rootScope.show_change = true;
		$rootScope.showError = false;
		$rootScope.showSuccess = false;
	}
});