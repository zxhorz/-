'use strict';

angular
	.module('projectSelectModule')
	.controller(
	'projectSelectController',
	function ($http, $scope, infoDataService, $state, $timeout,$window) {

		// $scope.setShowPage = function (pageName, setInfo) {
		// 	initPage(pageName, setInfo);
		// };
		// historyUrlService.setUrlInfo('project/select');
		// historyUrlService.setClickFlag(true);

		infoDataService.setInfo();
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
							$scope.selectedProject = $scope.projectList[0];
							$scope.existProject = true;
						} else {
							$scope.existProject = false;
						}
					} else {
						$scope.selectedProject = "No Projects Exist";
						$scope.existProject = false;
					}
				}).error(function (data) {
					$scope.onModel.modelShow('error', data.message);
				});
		}
		;

		$scope.goToSummary = function () {
			infoDataService.setFromPage('INDEX');
			infoDataService.setInfo();
			infoDataService.setName($scope.selectedProject.name);
			infoDataService.setId($scope.selectedProject.id);
			infoDataService
				.setDescription($scope.selectedProject.description);
			infoDataService
				.setSourcePath($scope.selectedProject.path);

			// create websocket connection
			infoDataService.handShakeWebSocket();

			$state.go('project/summary');

		}

		$scope.goToCreate = function () {
			$state.go('project/create');
		}
	});

