'use strict';

angular.module('projectDeleteModule')
	.controller('projectDeleteController', function ($http, $scope, infoDataService, $state, $timeout, $window) {
		$scope.deteleInfo = false;
		$scope.isSelected = false;
		$scope.selectedProjectId = [];
		initProjectList();

		function initProjectList() {
			$http({
				method: 'GET',
				url: './project/list'
			})
				.success(
				function (data) {
					if (data && data.data) {
						if (data.data.length > 0) {
							$scope.projectList = data.data;
						}else{
							$scope.projectList = [];
						}
					} else {
						$scope.projectList = [{ name: "No Projects Exist", description: '' }];
					}
				}).error(function (data) {
					$scope.onModel.modelShow('error', data.message);
				});
		};

		$scope.goToProjectSelect = function () {
			$state.go('project/select');
		}


		$scope.deleteProject = function () {
			if ($scope.selectedProjectId.length === 0)
				$scope.onModel.modelShow('error', 'Selett at least one project');
			else {
				$scope.deteleInfo = true;
				$scope.delSave = function () {
					$scope.deteleInfo = false;
					$http({
						method: 'GET',
						url: './project/delete',
						params: {
							'projectIds': $scope.selectedProjectId
						}
					}).success(function (data) {
						if (data && data.message === 'S') {
							initProjectList();
							$scope.selectedProjectId = [];
							$scope.onModel.modelShow('success', 'success');
						} else {
							$scope.onModel.modelShow('error', data.data);
						}
					}).error(function (data) {
						$scope.onModel.modelShow('error', data.message);
					});
				}
			}
		}

		$scope.delCancel = function () {
			$scope.deteleInfo = false;
		}

		//单选复选框的选中效果
        $scope.isSelected = function (projectId) {
            return $scope.selectedProjectId.indexOf(projectId) != -1;
		}
		
		//点击单个复选框
		$scope.selectOne = function (projectId) {
			if ($(event.target).is(':checked')) {
				$scope.selectedProjectId.push(projectId);
			} else {
				var pos = $.inArray(projectId, $scope.selectedProjectId);
				if (pos > -1) {
					$scope.selectedProjectId.splice(pos, 1);
				}
			}
		}
		//点击全选复选框
		$scope.selectAll = function () {
			if ($(event.target).is(':checked')) {
				$scope.selectedProjectId=[];
				angular.forEach($scope.projectList, function (item) {
					$scope.selectedProjectId.push(item.id);
				});
			} else {
				$scope.selectedProjectId=[];
			}
		}
	});
