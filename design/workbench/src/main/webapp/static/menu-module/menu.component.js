'use strict';

angular.
	module('menuModule').
	component('menuModule', {

		templateUrl: 'menu-module/menu.template.html',
		controller: ['$http', '$scope', '$state', 'infoDataService', 'joblistService', 'jobstatusService', '$timeout', 'historyUrlService', '$rootScope', function ($http, $scope, $state, infoDataService, joblistService, jobstatusService, $timeout, historyUrlService, $rootScope) {

			$rootScope.hisRecode = 0;
			$scope.logout = function () {
				window.location.href = "/logout"
			};

			$scope.goBack = function () {
				if(!$scope.goBackUrl){
					historyUrlService.goBackUrl();
					$rootScope.hisRecode = $rootScope.hisRecode - 1;
					historyUrlService.setClickFlag(false);
				}
			};
			$scope.goForward = function () {
				if(!$scope.goForwardUrl){
					historyUrlService.goForwardUrl();
					$rootScope.hisRecode = $rootScope.hisRecode + 1;
					historyUrlService.setClickFlag(false);
				}
			};

			if (infoDataService.getServerIp() === "") {
				infoDataService.setServerIp();
			}

			var WatchEvent = $scope.$watch(function () {
				return historyUrlService.getHistoryIndex();
			}, function () {
				if (historyUrlService.getHistoryIndex() === 0) {
					if (historyUrlService.getUrlInfo().length === 1) {
						$scope.goBackUrl = true;
						$scope.goForwardUrl = true;
					} else {
						$scope.goBackUrl = true;
						$scope.goForwardUrl = false;
					}
				} else if (historyUrlService.getHistoryIndex() === historyUrlService.getUrlInfo().length-1) {
					$scope.goBackUrl = false;
					$scope.goForwardUrl = true;
				} else {
					$scope.goBackUrl = false;
					$scope.goForwardUrl = false;
				}
			});

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
				url: './config/user'
			}).success(function (data) {
				$scope.userName = data.message;
			}).error(function (data) {
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

			// CORPUS_MANAGER
			$scope.showProjectBar = function () {
				if (infoDataService.getPage() === 'corpus') {
					return true;
				} else if(infoDataService.getPage() === 'custom'){
					return true;
				}else if (infoDataService.getName() === '') {
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

			$scope.swithToPage = function (page, description) {
				if (description === 'CORPUS_MANAGER') {
					infoDataService.setPage(description);
					$state.go('corpus');
					return;
				}
				if (description === 'CUSTOM_SCRIPT') {
					infoDataService.setPage(description);
					$state.go('custom');
					return;
				}
				if (infoDataService.getName() === '') {
					$scope.onModel.modelShow("error", "Please Select a project first");
				} else {
					if (page.trim() === 'SO Browser') {
						$scope.neo4jWindow = window.open();
					}
					$http({
						method: 'GET',
						url: './job/checkFileCount',
						params: {
							'projectId': infoDataService.getId()
						}
					}).success(function (data) {
						if (data.data === '0') {
							if(typeof($scope.neo4jWindow) !== "undefined"){
								$scope.neo4jWindow.close();
							}
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
										case 'E': $scope.closeNeo4jWindow(page, description); $scope.onModel.modelShow("error", "Project is not existed"); break;
										case 'N': $scope.closeNeo4jWindow(page, description); addToRequiredList(description); break;
										case 'P': $scope.closeNeo4jWindow(page, description); $scope.onModel.modelShow("prepare", "preparing"); $scope.showJobStatus(); break;
										case 'NS': $scope.closeNeo4jWindow(page, description); $scope.onModel.modelShow("prepare", "preparing"); $scope.showJobStatus(); break;
										default: ;
									}
								} else {
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

			$scope.closeNeo4jWindow = function (page, description) {
				if (page.trim() === 'SO Browser' && $scope.neo4jWindow) {
					$scope.neo4jWindow.close();
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
					case 'Code Browser': $state.go('codeBrowser'); break;
					case 'Code Search': $state.go('codeSearch'); break;
					case 'System Documentation': $state.go('detail', { tab: 'Summary' }); break;
					case 'Clone Code': $state.go('cloneCode'); break;
					case 'Clusters': break;
					case 'Cost Estimation': $state.go('costEstimation'); historyUrlService.setCost(); break;
					case 'Data Mapping': break;
					case 'Prediction': $state.go('predict'); break;
					case 'SO Browser': $scope.neo4jWindow.location = infoDataService.getNeo4jPath(); break; // 后更改页面地址
					default: ;
				}
			}

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
