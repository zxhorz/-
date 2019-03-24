(function (angular) {
	'use strict';
	var app = angular.module('mainContentModule',[])
	    .controller('mainContentController',function ($scope,$http,$rootScope) {

			$http({
				method: 'GET',
				url: '/user/getUser',
			}).success(function (data) {
				if (data.message === 'S') {
					$rootScope.userName = data.data;
				}
			}).error(function (data) {
				console.log("error");
			});

	});


})(angular);