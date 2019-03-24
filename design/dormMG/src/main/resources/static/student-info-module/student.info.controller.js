'use strict';
angular.module('studentInfoModule')
.factory('$custom', function ($modal) {
        var studentInfoModal = {};
        studentInfoModal.openStudentInfoModal = function (studentId) {
            $modal.open({
                backdrop: 'static',
                templateUrl: 'student-info-module/student.info.html',//script标签中定义的id
                controller: 'studentInfoCtrl',//modal对应的Controller
                resolve: {
                    studentId: function () {
                        return studentId;
                    }
                }
            })
        }
        return studentInfoModal;
    })
.controller('studentInfoCtrl',function ($scope,$http,$state,$rootScope,$modalInstance,$timeout,studentId){

			$http({
				method: 'GET',
				url: '/myInfo/myInfoGet',
				params: {
					'id': studentId
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

        $scope.close = function() {
            $modalInstance.close();
        }


	});


