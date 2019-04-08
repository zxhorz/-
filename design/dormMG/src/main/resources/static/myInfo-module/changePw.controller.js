(function (angular) {
	'use strict';
	var app = angular.module('changePwModule',[])
	    .controller('changePwController',function ($scope,$http,$state,$rootScope,$timeout){
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

		$scope.changePw = function(){
			var encrypt = new JSEncrypt();
			encrypt.setPublicKey($rootScope.key);
            $scope.loginDto.userName = $rootScope.userName
            var loginDto = {};
            angular.copy($scope.loginDto,loginDto)
			loginDto.userName = encrypt.encrypt(loginDto.userName);
			loginDto.password = encrypt.encrypt(loginDto.password);
			loginDto.newPassword = encrypt.encrypt(loginDto.newPassword);
//			loginDto.captcha = encrypt.encrypt(loginDto.captcha);
			$http({
				method: 'POST',
				url: '/login/changePassword',
				data:$.param(loginDto),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
			}).success(function (data) {
				if (data.message === 'S') {
//					console.log('success');
					// $scope.student = data.data;
					//
					$scope.onModel.modelShow('success','修改成功')
                    $timeout(function() {
                        $state.reload();
                    },1500)
//					$state.go('info');
				}else{
					$scope.onModel.modelShow('error',data.data)
				}
			}).error(function (data) {
				$scope.onModel.modelShow('error','修改失败')
			});
		}



	});


})(angular);