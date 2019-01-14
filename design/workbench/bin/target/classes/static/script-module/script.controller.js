'use strict';

angular
	.module('scriptModule')
	.controller('scriptController', function ($scope, $http, infoDataService) {
		infoDataService.setPage('script');
		initScriptList();

		function initScriptList() {
			$http({
				method: 'GET',
				url: './script/list'
			})
				.success(
					function (data) {
						if (data && data.data) {
							if (data.data.length > 0) {
								$scope.scriptList = data.data;
								$scope.selectedScript = $scope.scriptList[0];
								$scope.existScript = true;
							} else {
								$scope.existScript = false;
							}
						} else {
							$scope.selectedScript = "No Script Exist";
							$scope.existScript = false;
						}
					}).error(function (data) {
						$scope.onModel.modelShow('error', data.message);
					});
		}


		$scope.selectScript = function () {
			$http({
				method: 'GET',
				url: './script/select',
				params: {
					'scriptName': $scope.selectedScript
				}
			})
				.success(
					function (data) {
						if (data && data.data && data.data.inputArgs) {
							$scope.enableRun = true;
							if (data.data.inputArgs.length > 0) {
								$scope.argList = data.data.inputArgs;
								// for (var i = 0; i < $scope.argList.length; i++) {
								// 	$scope.argList[i] = "";
								// }
								$scope.existArgs = true;
							} else {
								$scope.existArgs = false;
							}
						} else {
							$scope.enableRun = false;
						}
					}).error(function (data) {
						$scope.onModel.modelShow('error', data.message);
					});
		}

		$scope.runScript = function () {
			$http({
				method: 'GET',
				url: './script/run',
				params: {
					'projectId': infoDataService.getId(),
					'argList': $scope.argList,
					'scriptName': $scope.selectedScript
				}
			})
			for (var i = 0; i < $scope.argList.length; i++) {
				console.log($scope.argList[i]);
			}
		}
	});