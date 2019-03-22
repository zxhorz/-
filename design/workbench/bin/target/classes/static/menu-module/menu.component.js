'use strict';

angular.
	module('menuModule').
	component('menuModule', {

		templateUrl: 'menu-module/menu.template.html',
		controller: ['$http', '$scope', '$rootScope', '$state', 'infoDataService', 'joblistService', 'jobstatusService', '$timeout', 'historyUrlService', function ($http, $scope, $rootScope, $state, infoDataService, joblistService, jobstatusService, $timeout, historyUrlService) {

			$rootScope.hisRecode = 0;
			// $scope.goBackUrl = true;
			// $scope.goForwardUrl = true;

			$scope.goBack = function () {
				historyUrlService.goBackUrl();
				$rootScope.hisRecode = $rootScope.hisRecode - 1;
				historyUrlService.setClickFlag(false);
			};
			$scope.goForward = function () {
				historyUrlService.goForwardUrl();
				$rootScope.hisRecode = $rootScope.hisRecode + 1;
				historyUrlService.setClickFlag(false);
			};

			if(infoDataService.getServerIp()===""){
				infoDataService.setServerIp();
			}
			// var WatchEvent = $scope.$watch('hisRecode', function (newValue) {
			// 		if (newValue !== undefined) {
			// 			if ($rootScope.hisRecode === 0) {
			// 				$scope.goBackUrl = true;
			// 			} else if ($rootScope.hisRecode === historyUrlService.getUrlInfo().length - 1) {
			// 				$scope.goForwardUrl = true;
			// 			} else {
			// 				$scope.goBackUrl = false;
			// 				$scope.goForwardUrl = false;
			// 			}
			// 		}
			// 	});

			$http({
				method: 'GET',
				url: './config/menu'
			}).success(function (data) {
				if (data.code === 'ACK') {
					$scope.navbar = data.data;
				} else {
					$scope.navbar = [];
				}
			}).error(function (data) {
				$scope.navbar = [];
				console.info(data);
			});

			$http({
				method: 'GET',
				url: './config/summarytotab'
			}).success(function (data) {
				if (data.code === 'ACK') {
					infoDataService.setTabMap(data.data);
				} else {
					infoDataService.setTabMap([]);
				}
				//  console.info($rootScope.navbar);
			}).error(function (data) {
				infoDataService.setTabMap([]);
			});

			$scope.getProjectName = function () {
				return infoDataService.getName();
			};

			$scope.showProjectBar = function () {
				if (infoDataService.getPage() === 'CORPUS_MANAGER') {
					return true;
				} else if (infoDataService.getName() === '') {
					return false;
				} else {
					return true;
				}
			}

			$scope.switchToOpenPorject = function () {
				$state.go('project/select');
				infoDataService.setPage('select');
				// create websocket connection
				// infoDataService.handShakeWebSocket();
			}

			$scope.switchToSummaryPage = function () {
				if (infoDataService.getName() === '') {
					$scope.onModel.modelShow('error', 'Please select a project first');
				} else {
					$state.go('project/summary');
				}
			}

			// $scope.swithToNewPage = function (page, description) {
			// 	if (infoDataService.getName() === '') {
			// 		modelShow("error", "Please Select a project first");
			// 	} else if (infoDataService.getNeo4jPath() === '') {
			// 		modelShow("error", "SO is not prepared now");
			// 	} else {
			// 		var neo4jWindow = window.open(); // 防止被浏览器拦截
			// 		neo4jWindow.location = infoDataService.getNeo4jPath(); // 后更改页面地址
			// 		// window.open(infoDataService.getNeo4jPath());
			// 	}
			// }

			$scope.swithToPage = function (page, description) {
				if (description === 'CORPUS_MANAGER') {
					infoDataService.setPage(description);
					$state.go('corpus');
					return;
				}
				if (infoDataService.getName() === '') {
					$scope.onModel.modelShow("error", "Please Select a project first");
				} else {
					$scope.neo4jWindow = window.open();
					$http({
						method: 'GET',
						url: './job/checkFileCount',
						params: {
							'projectId': infoDataService.getId()
						}
					}).success(function (data) {
						if (data.data === '0') {
							$scope.neo4jWindow.close();
							$scope.onModel.modelShow('error', 'No file in source code');
						} else {
							$http({
								method: 'GET',
								url: './job/specjobstatus',
								params: {
									'projectId': infoDataService.getId(),
									'jobName': description
								}
							}).success(function (data) {
								if (data.code === 'ACK') {
									var msg;
									switch (data.data) {
										case 'S': handlePage(page, description); break;
										case 'E': $scope.neo4jWindow.close();$scope.onModel.modelShow("error", "Project is not existed"); break;
										case 'N': $scope.neo4jWindow.close();addToRequiredList(description); break;
										case 'P': $scope.neo4jWindow.close(); $scope.onModel.modelShow("prepare", "preparing"); $scope.showJobStatus(); break;
										case 'NS': $scope.neo4jWindow.close(); $scope.onModel.modelShow("prepare", "preparing"); $scope.showJobStatus(); break;
										default: ;
									}
								}else{
									$scope.neo4jWindow.close();
								}
							}).error(function (data) {
								$scope.neo4jWindow.close();
								$scope.onModel.modelShow('error', data.message);
							});
						}
					})
				}
			}
			function addToRequiredList(description) {
				joblistService.openModal(infoDataService.getId(), description);
			}

			function handlePage(page, description) {
				var realPage = page.trim();
				switch (realPage) {
					// case 'SO Browser' :
					// var neo4jWindow=window.open(); // 防止被浏览器拦截
					// neo4jWindow.location=infoDataService.getNeo4jPath(); // 后更改页面地址
					// window.open(infoDataService.getNeo4jPath());
					// break;
					case 'Code Browser': $scope.neo4jWindow.close();$state.go('codeBrowser'); break;
					case 'Code Search': $scope.neo4jWindow.close();$state.go('codeSearch'); break;
					case 'System Documentation': $scope.neo4jWindow.close();$state.go('detail', { tab: 'Summary' }); break;
					case 'Clone Code': $scope.neo4jWindow.close();$state.go('cloneCode'); break;
					case 'Clusters': $scope.neo4jWindow.close();break;
					case 'Cost Estimation': $scope.neo4jWindow.close();$state.go('costEstimation'); historyUrlService.setCost(); break;
					case 'Data Mapping': $scope.neo4jWindow.close();break;
					case 'Prediction': $scope.neo4jWindow.close();$state.go('predict'); break;
					case 'SO Browser': $scope.neo4jWindow.location = infoDataService.getNeo4jPath();break; // 后更改页面地址
					case 'My Script': $scope.neo4jWindow.close(); $state.go('script'); break;
					default: $scope.neo4jWindow.close();
				}
			}

			// function modelShow(name, info) {
			// 	$scope.part = name;
			// 	$scope.promptInfo = info;
			// 	$scope.display = true;
			// 	$timeout(function () {
			// 		$scope.display = false;
			// 	}, 2000);
			// }

			$scope.showJobStatus = function () {
				$timeout(function () {
					jobstatusService.openModal($scope, infoDataService.getId());
				}, 3000);
			}
			$state.go('project/select');
			// $state.go("modalDialog");
			return;
		}]

	});
