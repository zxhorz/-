(function (angular) {
	'use strict';
	var app = angular.module('myInfoModule',[])
	    .controller('myInfoController',function ($scope,$http,$state,$rootScope,$timeout){
			$http({
				method: 'GET',
				url: '/myInfo/myInfoGet',
				params: {
					'id': $rootScope.userName
				}
			}).success(function (data) {
				if (data.message === 'S') {
					$scope.student = data.data;

				}
			}).error(function (data) {
				
			});



		$scope.submitForm = function(){

			$http({
				method: 'POST',
				url: '/myInfo/myInfoSave',
				data:$.param($scope.student),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
			}).success(function (data) {
				if (data.message === 'S') {
//					console.log('success');
					// $scope.student = data.data;
					//
                    $scope.onModel.modelShow('success','修改成功')

				}else{
				    $scope.onModel.modelShow('error','修改失败')
				}
                $timeout(function() {
                    $state.reload();
                },1500)
			}).error(function (data) {
                $scope.onModel.modelShow('success','修改成功')
			});
		}



	});


})(angular);