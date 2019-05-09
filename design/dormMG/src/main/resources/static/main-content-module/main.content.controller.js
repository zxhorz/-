(function (angular) {
	'use strict';
	var app = angular.module('mainContentModule',[])
	    .controller('mainContentController',function ($scope,$http,$rootScope,$state) {

			$http({
				method: 'GET',
				url: '/user/getUser',
			}).success(function (data) {
				if (data.message === 'S') {
					$rootScope.userName = data.data['userName'];
                    $http({
                        method: 'GET',
                        url: '/myInfo/myInfoGet',
                        params: {
                            'id': $rootScope.userName
                        }
                    }).success(function (data) {
                        if (data.message === 'S') {
                            $scope.student = data.data;
                        }else{
                            $scope.student = {};
                            $scope.student.name = "admin";
                        }
                    }).error(function (data) {

                    });
				}
			}).error(function (data) {
				console.log("error");
			});

            $http({
                method: 'GET',
                url: '/application/count'
            }).success(function (data) {
                if (data.message === 'S') {
                    $scope.applicationCount = data.data;

                }
            }).error(function (data) {

            });

            $http({
                method: 'GET',
                url: '/application/applicationPreview'
            }).success(function (data) {
                if (data.message === 'S') {
                    $scope.applications = data.data;
                }
            }).error(function (data) {

            });

            $http({
                method: 'GET',
                url: '/notice/noticePreview'
            }).success(function (data) {
                if (data.message === 'S') {
                    $scope.notices = data.data;
                }
            }).error(function (data) {

            });


            $scope.viewNotice = function(id){
                var data = $scope.notices[id]
                $state.go('notice/view', {
                    notice: data
                })
            }

	});


})(angular);