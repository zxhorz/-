'use strict';

angular
		.module('projectCreateModule')
		.controller(
				'projectCreateController',
				function($http, $scope, infoDataService,$state, $timeout) {
					// historyUrlService.setUrlInfo('project/create');
					// historyUrlService.setClickFlag(true);
					$scope.saveProject = function () {
						var projectInfo = {};
						projectInfo.projectName = $scope.newProjectName === undefined ? null
							: $scope.newProjectName;
						projectInfo.description = $scope.newProjectDsc === undefined ? null
							: $scope.newProjectDsc;
						var rex = /^[0-9a-zA-Z_]{1,}$/;
						var rexResult = rex.exec(projectInfo.projectName);
						if (rexResult != null) {
							$http({
								method: 'POST',
								url: './project/add',
								data: projectInfo
							}).success(function (data) {
								console.info(data);
								if (data.code === 'ACK') {
									setInfoData(data.data);
									infoDataService.setFromPage('CREATE');
								// create websocket connection
									infoDataService.handShakeWebSocket();
								
								// 默认so写入分析配置
									defaultWriteSoConfig(data.data.id);
									$state.go('project/summary');
								} else {
									$scope.onModel.modelShow('error', data.message);
								}
							}).error(function (data) {
								$scope.onModel.modelShow('error', data.message);
							});
						}else{
							$scope.onModel.modelShow('error', 'Project name must contain only characters, numbers or underlines.Project name must contain only characters, numbers or underlines.',4000);
						}
					};

		$scope.goToSelect = function(){
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
//			$http({
//				method: 'GET',
//				url: './job/getAllType'
//			}).success(function (data) {
//				if(data.data){
//					var soId = -1;
//					angular.forEach(data.data.analysisTypes, function(item) {
//						if (item.analysisName === 'SO') {
//							soId = item.id;
//						}
//					})
//					if (soId !== -1) {
//						var dependencyInfo = {};
//						dependencyInfo.projectId = projectId;
//						dependencyInfo.selectedName = [];
//						dependencyInfo.selectedName.push(soId);
//						$http({
//							method: 'POST',
//							url: './job/saveSelectedType',
//							data: dependencyInfo
//						}).success(function (data) {
//						}).error(function (data) {
//						});
//					}
//				}
//			}).error(function (data) {
//			});
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

