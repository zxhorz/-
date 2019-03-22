'use strict';

angular.module('detailModule')
	.directive('detailDirective', function ($timeout, infoDataService, historyUrlService, $window) {
		return {
			restrict: 'EA',
			scope: true,
			templateUrl: 'detail-module/detailtab.html',
			replace: false,
			controller: detailController,
			link: detailLink
		};
		function detailController($http, $scope, $state, infoDataService, $stateParams, historyUrlService, $compile) {
			$scope.detail_view = true;
			$scope.detail_print = false;

			$scope.detailNavTxt = '';
			infoDataService.setPage('detail');
			infoDataService.setFromPage('');
			$scope.disable = false;

			infoDataService.setDetailInfo($stateParams.tab);

			if (historyUrlService.getClickFlag()) {
				var detailRec = historyUrlService.getDetailRecord();
				detailRec.tab = $stateParams.tab;
				historyUrlService.setDetailRecord(detailRec);
				$scope.detailNavTxt = infoDataService.getTabMap($stateParams.tab).tabName;
				historyUrlService.setUrlInfo('detail/' + $stateParams.tab);
				// historyUrlService.setClickFlag(true);
			}

			if (infoDataService.getPage() === 'detail') {
				var WatchEvent = $scope.$watch('hisRecode', function (newValue) {
					if (newValue !== undefined && historyUrlService.getClickFlag() === false) {
						$scope.detailNavTxt = infoDataService.getTabMap(historyUrlService.getUrlParams()).tabName;
						angular.element('#' + $scope.detailNavTxt).siblings('li').removeClass('active');
						angular.element('#' + $scope.detailNavTxt).addClass('active');
						// historyUrlService.setClickFlag(true);
					}
				});
			} else {
				WatchEvent();
			}
			//get info from neo4j
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
						var nameCheck = [];
						$scope.basicalInfo = [];
						$scope.basicalInfo.push({ 'detailName': 'Summary' });
						nameCheck[$scope.getTabMap('Summary').tabName] = 0;
						angular.forEach(data.data, function (item) {
							if (!($scope.getTabMap(item.detailName).tabName in nameCheck)) {
								$scope.basicalInfo.push({ 'detailName': item.detailName });
								nameCheck[$scope.getTabMap(item.detailName).tabName] = 0;
							}
						})
						$scope.detailNav = $scope.basicalInfo;
						$timeout(function () {
							angular.element('#' + $scope.detailNavTxt).siblings('li').removeClass('active');
							angular.element('#' + $scope.detailNavTxt).addClass('active');
						});
					} else {
						$scope.basicalInfo = [];
						$scope.basicalMsg = 'No Information found, please check your System Ontology';
					}
					// initNeo4jUrl();
				}).error(function (data) {
					$scope.basicalInfo = [];
					$scope.basicalMsg = data.message;
					console.info($scope.basicalMsg);
				});

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

			//从点击nav 切换页面
			$scope.detailTable = function (detailName) {
				// $scope.flag = !$scope.flag;
				$(event.target).parent().addClass('active');
				$(event.target).parent().siblings('li').removeClass('active');
				$scope.detailNavTxt = detailName;
				historyUrlService.setClickFlag(true);
				historyUrlService.setUrlInfo('detail/' + $scope.detailNavTxt);
			}

			//点击system里面的内容切换页面
			$scope.SystemDblClick = function (row) {
				// if (row.detailName !== 'LOC') {
				infoDataService.setDetailInfo(row.detailName);
				$scope.detailNavTxt = $scope.getTabMap(row.detailName).tabName;
				angular.element('#' + $scope.detailNavTxt).siblings('li').removeClass('active');
				angular.element('#' + $scope.detailNavTxt).addClass('active');
				historyUrlService.setUrlInfo('detail/' + $scope.detailNavTxt);
			}

			$scope.getTabMap = function (keyName) {
				return infoDataService.getTabMap(keyName);
			};

			//从table页面切换到 sqlLogic   ToDo:应该放在sqlLogic.directive.js
			$scope.tableClick = function (row) {
				// $scope.detailNavTxt = 'SqlLogic';
				$scope.detailNavTxt = $scope.getTabMap('SqlLogic').tabName
				angular.element('#' + $scope.detailNavTxt).siblings('li').removeClass('active');
				angular.element('#' + $scope.detailNavTxt).addClass('active');
				row.tableName = infoDataService.getId();
				infoDataService.setTableInfo(row);
				historyUrlService.setUrlInfo('detail/' + $scope.detailNavTxt);
			}

			$scope.downloadSystemDocumentation = function () {
				window.location.href = '/summary/detailDoc?projectId=' + infoDataService.getId();
			}

			$scope.tempHtml = "";

			$scope.printSystemDocumentation = function () {
				var htmlContent = "";
				$scope.detail_view = false;
				$scope.detail_print = true;
				$scope.onModel.modelLoading('loading', 'loading');
				$http({
					method: 'POST',
					url: './summary/printContent',
					params: {
						'projectId': infoDataService.getId()
					},
					responseType: 'arraybuffer'
				}).success(function (response) {
					var bin = new Blob([response], {
						type: 'application/pdf'
					});
					var href = URL.createObjectURL(bin);
					$scope.tempHtml = href;
					$scope.onModel.modelHide();
				}).error(function (data) {

				});
			}
		}

		function detailLink(scope, element, attr) {
			scope.$watch('tempHtml', function (newValue, oldValue) {
				if (typeof (oldValue) !== 'undefined' && newValue !== "") {
					angular.element("#my-print").empty();
					angular.element("#my-print").attr("src",newValue);
					// angular.element("#my-print").append(newValue);
					$timeout(function () {
						$("#my-print")[0].contentWindow.print({
							noPrintSelector: ".no-print"
						});
					},500)
					scope.detail_view = true;
					scope.detail_print = false;
				}
			});
		}

	})
