angular.module('jobstatusModule')
	.factory('jobstatusService', function ($modal) {
		var jobstatusModal = {};
		// job progress bar
		jobstatusModal.openProgressBarModal = function (scope, projectId) {
			$modal.open({
				backdrop: "static",
				templateUrl: 'jobstatus-module/jobProgressBarModal.html',//script标签中定义的id
				controller: 'jobProgressBarCtrl',//modal对应的Controller
				size: 'lg',
				resolve: {
					projectId: function () {
						return projectId;
					},
					oriScope: function () {
						return scope;
					}
				}
			})
		}
		// job history
		jobstatusModal.openHistoryModal = function (scope, projectId) {
			$modal.open({
				backdrop: "static",
				templateUrl: 'jobstatus-module/jobHistoryModal.html',
				controller: 'jobHistoryCtrl',
				size: 'lg',
				resolve: {
					projectId: function () {
						return projectId;
					}
				}
			})
		}

		// job history
		jobstatusModal.openJobUpdateStatusModal = function (scope, projectId) {
			$modal.open({
				backdrop: 'static',
				templateUrl: 'jobstatus-module/jobUpdateStatusModal.html',
				controller: 'jobUpdateStatusCtrl',
				resolve: {
					projectId: function () {
						return projectId;
					}
				}
			})
		}

		//fileManager
		// jobstatusModal.fileManager = function (scope, projectId) {
		// 	$modal.open({
		// 		backdrop: 'static',
		// 		templateUrl: 'jobstatus-module/fileManager.html',
		// 		controller: 'fileManagerCtrl',
		// 		size: 'lg',				
		// 		resolve: {
		// 			projectId: function () {
		// 				return projectId;
		// 			}
		// 		}
		// 	})
		// }

		//missCode
		jobstatusModal.openMissCode = function (scope, projectId) {
			$modal.open({
				backdrop: 'static',
				templateUrl: 'jobstatus-module/missCode.html',
				controller: 'missCodeCtrl',
				resolve: {
					projectId: function () {
						return projectId;
					}
				}
			})
		}


		return jobstatusModal;
	}).
	controller('jobProgressBarCtrl', function ($http, $scope, $modalInstance, projectId, $timeout, $interval, $websocket, infoDataService, oriScope) {

		$scope.pbs = [];
		$scope.timer0_100s = [];
		$scope.timer20_100s = [];

		var webSocket = infoDataService.getWebSocket();

		webSocket.onMessage(function (message) {
			var msg = message.data;
			var jobName = msg.substring(0, msg.indexOf("/"));
			var perValue = msg.substring(msg.indexOf("/") + 1);
			var convertStatusToRunning = false;
			var convertStatusToException = false;
			if (perValue === "status:running") {
				convertStatusToRunning = true;
			}
			if (perValue.indexOf("status:exception") != -1) {
				convertStatusToException = true;
			}
			angular.forEach($scope.pbs, function (item) {
				if (item.jobName === jobName && item.style !== 'progress-bar-warning') {
					if (convertStatusToRunning) {
						item.status = 'running';
						queryJobStartTime(item);
					} else if (convertStatusToException) {
						item.status = 'exception';
						item.style = 'progress-bar-warning';
						item.value = '100';
						item.showLabel = false;
						item.infoLog = perValue.substring(perValue.indexOf("&") + 1);
						item.showInfoLog = true;
					} else {
						var value = perValue;
						item.value = value;
						if (value > 0) {
							item.status = 'running';
							// query job startTime
							if (!item.queriedStartTime) {
								queryJobStartTime(item);
							}
						}
						if (value === '100') {
							item.status = 'success';
							$timeout(function () {
								item.style = 'progress-bar-success';
							}, 500);
						}
					}
				}
			});
		});

		function queryJobStartTime(item) {
			$http({
				method: 'GET',
				url: './job/startTime',
				params: {
					'jobName': item.jobName
				}
			}).success(
				function (data) {
					item.startTime = data.data;
					item.queriedStartTime = true;
					item.showStartTime = true;
				}).error(
				function (data) {
					console.info('error');
				});
		};

		initProgressBar();

		$scope.$on('$destroy', function () {
			if (angular.isDefined($scope.timer)) {
				$interval.cancel($scope.timer);
			}
		});


		$scope.onDblClick = function (row) {
			console.info(row.entity);
		}

		function initProgressBar() {
			$http({
				method: 'GET',
				url: './job/jobProgressBarStatus',
				params: {
					'projectId': projectId
				}
			}).success(
				function (data) {
					angular.forEach(data.data, function (pbItem) {
						var pb = {};
						pb.jobName = pbItem.jobName;
						if (pbItem.incremental) {
							pb.analysisName = pbItem.analysisName + "_INCREMENTAL";
						} else {
							pb.analysisName = pbItem.analysisName;
						}
						pb.userName = pbItem.userName;
						pb.codeVersion = pbItem.codeVersion;
						pb.status = pbItem.status;
						// get the last time progress
						if (typeof (infoDataService.getBarPbs()) !== "undefined" && infoDataService.getBarPbs().length > 0) {
							oriScope.pbs = infoDataService.getBarPbs();
							angular.forEach(oriScope.pbs, function (ele) {
								if (ele.jobName === pb.jobName) {
									pb.value = ele.value;
									if (ele.queriedStartTime) {
										pb.queriedStartTime = true;
										pb.showStartTime = true;
										pb.startTime = ele.startTime;
									} else {
										pb.queriedStartTime = false;
										pb.showStartTime = false;
										pb.startTime = pbItem.startTime;
									}
								}
							});
						} else {
							pb.value = '0';
							pb.queriedStartTime = false;
							pb.showStartTime = false;
							pb.startTime = pbItem.startTime;
						}
						pb.style = 'progress-bar-info';
						pb.showLabel = true;
						pb.striped = true;
						pb.infoLog = "";
						pb.showInfoLog = false;
						$scope.pbs.push(pb);
						oriScope.pbs = $scope.pbs;
						infoDataService.setBarPbs(oriScope.pbs);
					});
				}).error(
				function (data) {
					console.info('error');
				});
		};

		$scope.close = function () {
			// oriScope.pbs = $scope.pbs;
			if (angular.isDefined($scope.timer)) {
				$interval.cancel($scope.timer);
				$scope.timer = undefined;
			}
			$modalInstance.close();
		}
	}).controller('jobHistoryCtrl', function ($http, $scope, $modalInstance, projectId, $timeout, $interval, $websocket) {

		$scope.gridOptions = {
			columnDefs: [
				{ field: 'analysisName', displayName: 'Analysis Name', headerTooltip: 'Analysis Name', cellTemplate: '<div title = {{row.entity.analysisName}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.analysisName}}</div>' },
				{ field: 'startTime', displayName: 'Start Time', headerTooltip: 'Start Time', cellTemplate: '<div title = {{row.entity.startTime}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.startTime}}</div>' },
				{ field: 'stopTime', displayName: 'Stop Time', headerTooltip: 'Stop Time', cellTemplate: '<div title = {{row.entity.stopTime}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.stopTime}}</div>' },
				{ field: 'codeVersion', displayName: 'Code Version', headerTooltip: 'Code Version', cellClass: 'textIndent' },
				{ field: 'jobStatus', displayName: 'Job Status', headerTooltip: 'Job Status', cellClass: 'textIndent' }],
			enableSorting: false,
			enableHorizontalScrollbar: 0,
			enableVerticalScrollbar: 0,
			rowTemplate: "<div ng-dblclick=\"grid.appScope.onDblClick(row)\" ng-click=\"grid.appScope.Click(row,element)\"  ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",
		};
		$timeout(function () {
			if (angular.element('.ui-grid-canvas').height() > angular.element('.grid').height()) {
				$scope.gridOptions.enableVerticalScrollbar = 1;
			} else {
				$scope.gridOptions.enableVerticalScrollbar = 0;
			}
		});

		initJobList();

		function initJobList() {
			$http({
				method: 'GET',
				url: './job/jobHistory',
				params: {
					'projectId': projectId
				}
			}).success(function (data) {
				$scope.gridOptions.data = data.data;
			}).error(function (data) {
				console.info('error');
			});
		};

		$scope.close = function () {
			$modalInstance.close();
		}

	}).controller('jobUpdateStatusCtrl', function ($http, $scope, projectId, $modalInstance, $timeout, $interval, $websocket, infoDataService) {
		$scope.updateStatusGridOptions = {
			columnDefs: [
				{ field: 'analysisName', displayName: 'Analysis Name', headerTooltip: 'Analysis Name', cellTemplate: '<div title = {{row.entity.analysisName}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.analysisName}}</div>' },
				{ field: 'codeVersion', displayName: 'Code Version', headerTooltip: 'Code Version', cellClass: 'textIndent' },
				{ field: 'jobStatus', displayName: 'Job Status', headerTooltip: 'Job Status', cellClass: 'textIndent' },
				{ field: 'needUpdate', displayName: 'Need Update', headerTooltip: 'Need Update', cellTemplate: '<div class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.needUpdate == true ?"Y":"N"}}</div>', cellClass: 'textIndent' }],
			enableSorting: false,
			enableHorizontalScrollbar: 0,
			enableVerticalScrollbar: 0,
			rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",
		};
		initJobUpdateStatusList();

		function initJobUpdateStatusList() {
			$http({
				method: 'GET',
				url: './job/jobUpdateStatus',
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(function (data) {
				$scope.updateStatusGridOptions.data = data.data;
			}).error(function (data) {
				console.info('error');
			});
		};

		$scope.close = function () {
			$modalInstance.close();
		}
	}).controller('missCodeCtrl', function ($http, $scope, projectId, $modalInstance, $timeout, $interval, $websocket, infoDataService) {
		$scope.close = function () {
			$modalInstance.close();
		}
		if(infoDataService.getMissCode()){
			$scope.missCodeData=infoDataService.getMissCode();
		}
	});