'use strict';

angular
	.module('projectModule')
	.controller(
	'projectController',
	function ($http, $scope, infoDataService, $state, $timeout, $modal, jobstatusService, $interval, historyUrlService, $window) {
		if (historyUrlService.getClickFlag()) {
			historyUrlService.setUrlInfo('project/summary');
			historyUrlService.setClickFlag(true);
		} else {
			historyUrlService.setClickFlag(true);
		}

		$scope.disable = false;
		$scope.codestatus = '';
		$scope.project = infoDataService.getId();
		infoDataService.setCloneFinished(false);
		infoDataService.setFromPage('INDEX');
		initBasicSummary();
		var webSocket = infoDataService.getWebSocket();
		if (!!window.ActiveXObject || 'ActiveXObject' in window) {
			$scope.isIE = true;
		} else {
			$scope.isIE = false;
		}

		$scope.soBrowser = function () {
			var neo4jWindow = window.open();
			$http({
				method: 'GET',
				url: './project/open',
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(function (data) {
				if (data.message === 'S') {
					neo4jWindow.location = infoDataService.getNeo4jPath();
				} else {
					neo4jWindow.close();
					$scope.onModel.modelShow('error', data.message);
				}
			}).error(function (data) {
				$scope.onModel.modelShow('error', data.message);
			})
		}

		$scope.updateProject = function () { // update description
			var projectInfo = {};
			projectInfo.projectId = infoDataService.getId();
			projectInfo.projectName = infoDataService.getName();
			projectInfo.description = $scope.openedProjectDesc;
			$http({
				method: 'POST',
				url: './project/updateproject',
				data: projectInfo
			}).success(function (data) {
				infoDataService
					.setDescription($scope.openedProjectDesc);
				$scope.setEditMode();
				$scope.onModel.modelShow('success', 'success');
			}).error(function (data) {
				$scope.onModel.modelShow('error', data.message);
				$scope.setEditMode();
			});
		};

		$scope.setEditMode = function (t) { // set description edit or not
			if (t === 'Cancel') {
				$scope.openedProjectDesc = infoDataService
					.getDescription();
			}
			$scope.disable = !$scope.disable;
			$scope.readOnly = !$scope.readOnly;
		};


		$scope.codeBrowser = function () {  //go to codebroser page
			$state.go('codeBrowser');
		};

		$scope.showJobUpdateStatus = function () { // show job progress bar
			jobstatusService.openJobUpdateStatusModal(infoDataService.getId());
		}

		$scope.fileManager = function () { // show missing code
			$scope.sourceCodeReady = false;
			var modal =
				$modal.open({
					backdrop: 'static',
					templateUrl: 'fileManager-module/fileManager.html',
					controller: 'fileManagerCtrl',
					size: 'lg',
					resolve: {
						projectId: function () {
							return infoDataService.getId();
						}
					}
				});
			modal.result.then(function (result) {
				if(result!=='Y'){
					$state.reload();
				}
			}, function (reason) {
				$state.reload();
			})
		}

		$scope.execJobList = function () { // update all required data sets

			$http({
				method: 'GET',
				url: './job/checkFileCount',
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(function (data) {
				if (data.data === '0') {
					$scope.onModel.modelShow('error', 'No file in source code');
				} else {
					$http({
						method: 'GET',
						url: './job/execjob',
						params: {
							'projectId': infoDataService.getId()
						}
					}).success(
						function (data) {
							if (data.code === 'NACK') {
								$scope.onModel.modelShow('error', data.message);
							} else {
								$scope.codestatus = 'ONDOING';
								$scope.showJobProgressBars();
							}
						}).error(
						function (data) {
							$scope.onModel.modelShow('error', data.message);
						});
				}
			}).error(function (data) {
				$scope.onModel.modelShow('error', 'Check Source Code in disk');
			});
		}

		$scope.showJobProgressBars = function () { // show job progress bar
			jobstatusService.openProgressBarModal($scope, infoDataService.getId());
		}

		$scope.showJobHistory = function () { // show job history
			jobstatusService.openHistoryModal($scope, infoDataService.getId());
		}

		$scope.missCode = function () { // show missing code
			jobstatusService.openMissCode(infoDataService.getId());
		}

		$scope.checkStatus = function (status) { // check button status
			if ($scope.codestatus === status) {
				return true;
			} else {
				return false;
			}
		}

		$scope.configDataSets = function () { // config required data sets
			$scope.modalInstance = $modal.open({
				backdrop: 'static',
				templateUrl: 'project-module/test.html',//script标签中定义的id
				controller: 'modalCtrl',//modal对应的Controller
				resolve: {
					projectId: function () {
						return infoDataService.getId();
					},
					// parentscope: function () { return $scope; }
				}
			});
			$scope.modalInstance.result.then(function (result) {
				$scope.checkProjectStatus(infoDataService.getId());
			}, function (reason) {

			})
		}

		$scope.switchToCloneCode = function () { // go to clone page
			$state.go('cloneCode');
		}

		// open neo4j DB
		function openProject() {
			$scope.onModel.modelLoading('loading', 'loading');
			$http({
				method: 'GET',
				url: './project/open',
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(function (data) {
				if (data.message === 'S') {
					checkMissing();
					initBasicalInfo();
					$scope.soReady = true;
				} else {
					$scope.onModel.modelShow('error', 'Failed in open DB');
					$scope.soReady = false;
				}
			}).error(function (data) {
				$scope.onModel.modelHide(1000);
				$scope.soReady = false;
			});
		}
		;

		function initBasicSummary() {   // set basic info
			$scope.openedProjectDesc = infoDataService.getDescription();
			$scope.disable = false;
			$scope.readOnly = true;
			// initRemoteSourcePath();
			checkFileCount();
		};

		function checkMissing() {
			$http({
				method: 'GET',
				url: './codebrowser/checkMissing',    //codebrowser还要做迁移
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(function (data) {
				if (data && data.data) {
					$scope.copyBooksTotal = data.data.copybookLength;
					$scope.programTotal = data.data.programLength;
					infoDataService.setMissCode(data.data.checkMissiongItems);
					$scope.hasMissingCode = true;
				} else {
					$scope.hasMissingCode = false;
					$scope.missCodeData = [];
				}
			}).error(function (data) {
				console.info('error');
				$scope.hasMissingCode = false;
				$scope.missCodeData = [];
			});
		};

		function initBasicalInfo() {  // get info from neo4j
			$http({
				method: 'GET',
				url: './summary/system',
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(
				function (data) {
					if (data && data.data
						&& data.data.length > 0) {
						$scope.basicalInfo = data.data;
					} else {
						$scope.basicalInfo = [];
						$scope.basicalMsg = 'No Information found, please check your System Ontology';
					}
					initNeo4jUrl();
				}).error(function (data) {
					$scope.onModel.modelShow('error', 'error');
					$scope.basicalInfo = [];
					$scope.basicalMsg = data.message;
				});
		};

		function initAnalysisInfo() {  // get clone info
			$http({
				method: 'GET',
				url: './clone/clonePercentage',
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(
				function (data) {
					if (data && data.data) {
						$scope.totalClone = data.data.substring(0, data.data.indexOf('_'));
						$scope.programCount = data.data.substring(data.data.indexOf('_') + 1, data.data.length);
						$scope.cloneReady = true;
					} else {
						$scope.cloneReady = false;
						$scope.totalClone = '';
						$scope.programCount = 0;
					}
					$scope.onModel.modelShow('success', 'success');
				}).error(function (data) {
					$scope.cloneReady = false;
					$scope.analysisInfo = [];
					$scope.analysisMsg = data.message;
				});
		};

		function initNeo4jUrl() { // get neo4j port
			$http({
				method: 'GET',
				url: './host/neo4j/ip',
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(function (data) {
				$scope.neo4jUrl = data.data;
				infoDataService.setNeo4jPath(data.data);
				// 得到bolt url
				getBoltUri();
				if ($scope.cloneFinished) {
					initAnalysisInfo();
				} else {
					$scope.onModel.modelShow('success', 'success');
				}
				// $scope.display = false;
			}).error(function (data) {
				infoDataService.setNeo4jPath('');
				// $scope.display = false;
			});
		};

		function getBoltUri() {
			$http({
				method: 'GET',
				url: './host/neo4j/boltUri',
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(function (data) {
				if (data.data && data.data.length === 2) {
					infoDataService.setBoltUri(data.data[0]);
					infoDataService.setAutoTagPath(data.data[1]);
				} else {
					infoDataService.setBoltUri('');
					infoDataService.setAutoTagPath('');
				}
			}).error(function (data) {
				infoDataService.setBoltUri('');
				infoDataService.setAutoTagPath('');
			});
		}

		// get  list of all required data set status
		$scope.checkProjectStatus = function (projectId) {
			//$scope.onModel.modelShow('loading','loading');    //ToDo
			$scope.part = 'loading';
			$scope.promptInfo = 'loading';
			$scope.display = true;
			$http({
				method: 'GET',
				url: './job/projectstatus',
				params: {
					'projectId': projectId
				}
			}).success(
				function (data) {
					if (data && data.data) {
						$scope.codestatus = data.data.projectStatus;
						$scope.initJobInfo(data.data.jobStatus);
					} else {
						$scope.onModel.modelHide();
						$scope.onModel.modelShow('sucess', 'success');
						$scope.codestatus = 'OUTOFDATE';
					}
					if (data.data.projectStatus !== 'ONDATE' && infoDataService.getFromPage() === 'MENU') {
						$scope.showJobProgressBars();
						infoDataService.setFromPage('INDEX');
					}
				}).error(function (data) {
					$scope.onModel.modelHide();
					$scope.onModel.modelShow('error', data.message);
					if (data.data.projectStatus !== 'ONDATE' && infoDataService.getFromPage() === 'MENU') {
						$scope.showJobProgressBars();
						infoDataService.setFromPage('INDEX');
					}
					$scope.codestatus = 'OUTOFDATE';
				});
		};

		$scope.initJobInfo = function (jobStatusList) {
			if (jobStatusList.length == 0) {
				$scope.onModel.modelHide();
			} else {
				$scope.cloneFinished = false;
				angular.forEach(jobStatusList, function (item) {
					switch (item.analysisName) {
						case 'SO': if (item.jobStatus === 'S') {
							openProject();
						} else {
							$scope.onModel.modelHide();
						}
							break;
						case 'CLONE_CODE': if (item.jobStatus === 'S') {
							$scope.cloneFinished = true;
							infoDataService.setCloneFinished(true);
						} else {
							//因为在进行clone code分析时显示result的地方会显示一条值为0的数据，所以将$scope.cloneFinished = true改成了false;
							$scope.cloneFinished = false;
							infoDataService.setCloneFinished(false);
						}
							break;
						default: ;
					}
				})
			}
		};

		$scope.getTabMap = function (keyName) {
			return infoDataService.getTabMap(keyName);
		};

		function initProjectStatus() {
			$scope.checkProjectStatus(infoDataService.getId());
		}

		initProjectStatus();

		function checkFileCount() {
			$http({
				method: 'GET',
				url: './job/checkFileCount',
				params: {
					'projectId': infoDataService.getId()
				}
			}).success(function (data) {
				if (data.data === '0') {
					$scope.sourceCodeReady = true;
					// $scope.onModel.modelShow('error', 'No file in source code');
				} else {
					$scope.sourceCodeReady = false;
				}
			}).error(function (data) {
				console.info('error');
			});
		}

		webSocket.onMessage(function (message) {
			// console.info(message.data);
			var msg = message.data;
			var jobName = msg.substring(0, msg.indexOf("/"));
			if (jobName === 'ALL_DONE') {
				// openProject();
				$scope.checkProjectStatus(infoDataService.getId());
			}
		});
	})
	.controller('modalCtrl', function ($http, $scope, $modalInstance, projectId) {
		$scope.selectedItem = [];
		getAllJobAndDenpend();

		//在这里处理要进行的操作   
		$scope.ok = function () {
			getAllSelected();
			$modalInstance.close();
		};
		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		}

		$scope.isSelected = function (id) {
			return $scope.selectedItem.indexOf(id) != -1;
		};

		$scope.isDisabled = function (id, name) {
			if (name === 'SO') {
				return true;
			}

			var flag = false;
			angular.forEach($scope.dependency, function (item) {
				if (!flag) {
					if (id === item.dependId && $scope.isSelected(id)
						&& $scope.isSelected(item.id)) {
						flag = true;
					}
				}
			})
			return flag;
		};

		function getAllJobAndDenpend() {

			$http({
				method: 'GET',
				url: './job/getAllType'
			}).success(
				function (data) {
					if (data.data) {
						$scope.items = data.data.analysisTypes;
						$scope.dependency = data.data.analysisDependencies;
					}
				}).error(
				function (data) {
				});
			getLastConfig();
		}

		$scope.selectBox = function (id) {
			var allDependIds = getDependId(id);
			var dependIds = checkDenpend(allDependIds, id);
			dependIds.push(id);
			if ($scope.isSelected(id)) {
				angular.forEach(dependIds, function (item) {
					var pos = $.inArray(item, $scope.selectedItem);
					if (pos > -1) {
						$scope.selectedItem.splice(pos, 1);
					}
				})
			} else {
				angular.forEach(dependIds, function (item) {
					if ($.inArray(item, $scope.selectedItem) < 0) {
						$scope.selectedItem.push(item);
					}
				})
			}
		}

		function getLastConfig() {
			//成功状态下初始化选中效果
			$http({
				method: 'GET',
				url: './job/getSelectedType',
				params: {
					"projectId": projectId
				}
			}).success(
				function (data) {
					$scope.selectedItem = data.data;
				}).error(
				function (data) {
					$scope.onModel.modelShow(error, data.message);
				});
		}
		function getAllSelected() {
			$scope.selectedItem = [];
			var selectedItems = angular.element("input:checkbox[name='jobselect']:checked")
			angular.forEach(selectedItems, function (item) {
				$scope.selectedItem.push(item.value);
			})

			var dependencyInfo = {};
			dependencyInfo.projectId = projectId;
			dependencyInfo.selectedName = $scope.selectedItem;
			$http({
				method: 'POST',
				url: './job/saveSelectedType',
				data: dependencyInfo
			}).success(function (data) {
				// if(data.data==='A'){
				// 	parentscope.checkProjectStatus(projectId);
				// }
				// parentscope.checkProjectStatus(projectId);
			}).error(function (data) {
				$scope.onModel.modelShow(error, data.message);
			});
		}

		function getDependId(id) {
			var dependIds = [];
			angular.forEach($scope.dependency, function (item) {
				if (id === item.id) {
					dependIds.push(item.dependId);
				}
			})
			return dependIds;
		}

		function getDependedId(id) {
			var dependedIds = [];
			angular.forEach($scope.dependency, function (item) {
				if (id === item.dependId) {
					dependedIds.push(item.id);
				}
			})
			return dependedIds;
		}

		function checkDenpend(allDependIds, oid) {
			var ids = [];
			var flag = false;
			angular.forEach(allDependIds, function (id) {
				var name = getJobName(id);
				if (name === 'SO') {
					flag = true;
				} else {
					flag = false;
				}
				angular.forEach($scope.dependency, function (item) {
					if (!flag) {
						if (id === item.dependId && oid !== item.id) {
							if ($.inArray(item.id, allDependIds) < 0 && $scope.isSelected(item.id)) {
								flag = true;
							}
						}
					}
				})
				if (!flag) {
					ids.push(id);
				}
			})
			return ids;
		}

		function getJobName(jobId) {
			var notFound = true;
			var name = "";
			angular.forEach($scope.items, function (item) {
				if (notFound) {
					if (item.id === jobId) {
						notFound = false;
						name = item.analysisName;
					}
				}
			});
			return name;
		}
	});
