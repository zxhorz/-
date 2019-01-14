'use strict';

angular
	.module('projectCreateModule')
	.controller(
	'projectCreateController',
	function ($http, $scope, infoDataService, $state, $timeout) {
		// historyUrlService.setUrlInfo('project/create');
		// historyUrlService.setClickFlag(true);
		$scope.saveProject = function () {
			$scope.onModel.modelLoading('loading', 'loading')
			var projectInfo = {};
			projectInfo.projectName = $scope.newProjectName === undefined ? null
				: $scope.newProjectName;
			projectInfo.description = $scope.newProjectDsc === undefined ? null
				: $scope.newProjectDsc;
			$http({
				method: 'POST',
				url: './project/add',
				data: projectInfo
			}).success(function (data) {
				if (data.code === 'ACK') {
					setInfoData(data.data);
					infoDataService.setFromPage('CREATE');
					// create websocket connection
					infoDataService.handShakeWebSocket();

					// 默认so写入分析配置
					defaultWriteSoConfig(data.data.id);
					$scope.onModel.modelHide();
					$state.go('project/summary');
				} else {
					$scope.onModel.modelShow('error', data.message);
				}
			}).error(function (data) {
				$scope.onModel.modelShow('error', data.message);
			});
		};

		$scope.goToSelect = function () {
			$state.go('project/select');
		}

		function defaultWriteSoConfig(projectId) {
			var dependencyInfo = {};
			dependencyInfo.projectId = projectId;
			dependencyInfo.selectedName = [];
			dependencyInfo.selectedName.push("SO");
			$http({
				method: 'POST',
				url: './job/saveSelectedType',
				data: dependencyInfo
			}).success(function (data) {
			}).error(function (data) {
			});
		}

		function setInfoData(newProject) {
			infoDataService.setInfo();
			infoDataService.setName(newProject.name);
			infoDataService.setId(newProject.id);
			infoDataService
				.setDescription(newProject.description);
			infoDataService
				.setSourcePath(newProject.path);
		};
	});

