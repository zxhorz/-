'use strict';

angular.module('ctrlFlowAndFileStrucModule').directive('ctrlFlowAndFileStrucDirective', function ($timeout, historyUrlService) {

	return {
		restrict: 'EA',
		scope: false,
		templateUrl: 'ctrlFlowAndFileStruc-module/ctrlFlowAndFileStruc.template.html',
		replace: false,
		controller: ctrlFlowAndFileStrucController,
		link: ctrlFlowAndFileStrucLink
	};

	function ctrlFlowAndFileStrucController($scope, $http, $location, $attrs, $rootScope, $compile, historyUrlService) {
		$scope.detailNav = [
			{ "name": "ControlFlow" },
			{ "name": "FileStructure" }
		]
		$scope.selectedSvgInfo = {};    //存储文件定位信息
		// $scope.codeBrwRed=historyUrlService.getCodeBrowRecord()
		if (historyUrlService.getClickFlag()) {
			$scope.controlFlowTab = true;
			$scope.fileStructureTab = false;
			$scope.codeBrwRed.controlFlowTab = $scope.controlFlowTab;
			historyUrlService.setCodeBrowRecord($scope.codeBrwRed);
		} else {
			$scope.$watch('datafinish', function (newValue) {
				if (newValue !== undefined && $scope.datafinish && Object.keys(historyUrlService.getCodeBrowRecord().selectedSvgInfo).length > 0) {
					$scope.selectedSvgInfo = historyUrlService.getCodeBrowRecord().selectedSvgInfo;
				}
			})
		}

		$scope.changeSvg = function (flag) {
			$scope.svg = findSvgByName($scope.selectedSvg);
			if (flag) {
				$scope.autoLocation = true;
				$scope.$digest();
			} else {
				$scope.autoLocation = false;
			}
		}

		$scope.locationSVG = function (linenum) {
			var tempHtml = '';
			var flag = false;
			for (var i = 0; i < $scope.controlFlowSvgMap.length; i++) {
				tempHtml = $compile($scope.controlFlowSvgMap[i].svgContent)($scope);
				flag = findSvgByLine(linenum, tempHtml, $scope.controlFlowSvgMap[i].graphName);
				if (flag) {
					$scope.selectedSvg = $scope.controlFlowSvgMap[i].graphName;
					break;
				}
			}
			if (flag) {
				$scope.svg = tempHtml;
			} else {
				$scope.svg = '';
			}
			$scope.$digest();
		}

		$scope.autoLink = function () {
			if ($scope.autoLinkFlag) {
				angular.element('#autoLinkIcon').removeClass("autoLink");
			} else {
				angular.element('#autoLinkIcon').addClass("autoLink");
			}
			$scope.autoLinkFlag = !$scope.autoLinkFlag;
			if ($scope.selectedProgram in $scope.selectedSvgInfo) {
				$scope.selectedSvgInfo[$scope.selectedProgram].autoLink = $scope.autoLinkFlag;
			}
		}

		$scope.setSelectedSvgInfo = function (graphName, startline, endline) {
			$scope.codeBrwRed=historyUrlService.getCodeBrowRecord();
			if ($scope.selectedProgram in $scope.selectedSvgInfo) {
				$scope.selectedSvgInfo[$scope.selectedProgram].graphName = graphName;
				$scope.selectedSvgInfo[$scope.selectedProgram].startline = startline;
				$scope.selectedSvgInfo[$scope.selectedProgram].endline = endline;
				$scope.selectedSvgInfo[$scope.selectedProgram].autoLink = $scope.autoLinkFlag;
				$scope.codeBrwRed.selectedSvgInfo = $scope.selectedSvgInfo;
				historyUrlService.setCodeBrowRecord($scope.codeBrwRed);
			} else {
				$scope.selectedSvgInfo[$scope.selectedProgram] = {
					graphName: graphName,
					startline: startline,
					endline: endline,
					autoLink: $scope.autoLinkFlag
				};
				$scope.codeBrwRed.selectedSvgInfo = $scope.selectedSvgInfo;
				historyUrlService.setCodeBrowRecord($scope.codeBrwRed);
			}
		}

		function findSvgByName(graphName) {
			var svgContent = '';
			for (var i = 0; i < $scope.controlFlowSvgMap.length; i++) {
				if (graphName === $scope.controlFlowSvgMap[i].graphName) {
					svgContent = $compile($scope.controlFlowSvgMap[i].svgContent)($scope);
					i = $scope.controlFlowSvgMap.length;
					break;
				}
			}
			return svgContent;
		}

		function findSvgByLine(line, tempHtml, graphName) {
			var find = false;
			var nodes = tempHtml.find('.node.svg-title');
			for (var i = 0; i < nodes.length; i++) {
				var node = nodes[i];
				var startline = node.getAttribute('data-startline');
				var endline = node.getAttribute('data-endline');
				if (line >= startline && line <= endline) {
					find = true;
					$scope.setSelectedSvgInfo(graphName, startline, endline);
					break;
				}
			}
			return find;
		}
	}


	function ctrlFlowAndFileStrucLink(scope, element, attr) {
		if (historyUrlService.getCodeBrowRecord().controlFlowTab) {
			scope.controlFlowTab = true;
			scope.fileStructureTab = false;
			$timeout(function (scope, element, attr) {
				angular.element('#ControlFlow').siblings("li").removeClass("active");
				angular.element('#ControlFlow').addClass("active");
			});
		} else {
			scope.controlFlowTab = false;
			scope.fileStructureTab = true;
			$timeout(function (scope, element, attr) {
				angular.element('#FileStructure').siblings("li").removeClass("active");
				angular.element('#FileStructure').addClass("active");
			});
		}
		scope.detailTable = function (event) {
			$(event.target).parent().addClass("active");
			$(event.target).parent().siblings("li").removeClass("active");
			scope.codeBrwRed=historyUrlService.getCodeBrowRecord();
			if (scope.controlFlowTab) {
				scope.controlFlowTab = false;
				scope.fileStructureTab = true;
				scope.codeBrwRed.controlFlowTab = scope.controlFlowTab;
				 historyUrlService.setCodeBrowRecord(scope.codeBrwRed);
			} else {
				scope.controlFlowTab = true;
				scope.fileStructureTab = false;
				scope.codeBrwRed.controlFlowTab = scope.controlFlowTab;
				historyUrlService.setCodeBrowRecord(scope.codeBrwRed);
			}
		}

		scope.$watch('svg', function (newValue) {
			if (typeof newValue !== 'undefined') {
				if (newValue !== '') {
					angular.element("#svgclass").empty();
					angular.element("#svgclass").append(newValue);
					var nodes = angular.element('.node.svg-title');
					nodes.on('click', function (event) {
						var startline = event.currentTarget.getAttribute('data-startline');
						var endline = event.currentTarget.getAttribute('data-endline');
						var title = event.currentTarget.getAttribute('data-original-title');
						var refid = event.currentTarget.getAttribute('ref-id');
						var editor = ace.edit('codeEditor');
						editor.gotoLine(startline);
						editor.selection.selectTo(endline - 1, 72);
						if (refid !== 'null' && refid !== null) {
							scope.setSelectedSvgInfo(title, startline, endline);
							scope.selectedSvg = title;
							scope.changeSvg(true);
						} else {
							scope.setSelectedSvgInfo(scope.selectedSvg, startline, endline);
							angular.element('.svgLink').removeClass('svgLink ');
							event.currentTarget.classList.add('svgLink');
						}
					})

					if (Object.keys(scope.selectedSvgInfo).length > 0) {
						if (scope.selectedProgram in scope.selectedSvgInfo) {
							var item = scope.selectedSvgInfo[scope.selectedProgram];
							if (angular.isDefined(item.startline) && angular.isDefined(item.endline) && item.startline > 0 && item.endline > 0) {
								angular.element('.svgLink').removeClass('svgLink ');
								var nodes = angular.element('g[data-startline="' + item.startline + '"][data-endline="' + item.endline + '"]');
								if (nodes.length > 0) {
									nodes.addClass('svgLink');
									// console.info(nodes[0]);
									var top = nodes[0].getBBox().y;
									angular.element("#svgclass").scrollTop(top * 2);
									angular.element("#svgclass").scrollLeft(nodes[0].getBBox().x * 1.5);
									var editor = ace.edit('codeEditor');
									editor.gotoLine(item.startline);
									editor.selection.selectTo(item.endline - 1, 72);
									if (scope.autoLinkFlag !== item.autoLink) {
										scope.autoLink();
									}
								} else {
									angular.element("#svgclass").scrollTop(0);
									angular.element("#svgclass").scrollLeft(0);
								}
							}
						}else{
						angular.element("#svgclass").scrollTop(0);
						angular.element("#svgclass").scrollLeft(0);							
						}
					} else {
						angular.element("#svgclass").scrollTop(0);
						angular.element("#svgclass").scrollLeft(0);
					}
				} else {
					angular.element('.svgLink').removeClass('svgLink ');
				}
			}
		});

		// fileStructure
		scope.setting = {
			data: {
				simpleData: {
					enable: true,
					idKey: "id",
					pIdKey: "pId",
					rootPId: 0
				}
			},
			callback: {
				onClick: zTreeOnClick
			}
		};

		scope.$watch('zNodes', function (newValue) {
			$.fn.zTree.init($("#tree"), scope.setting, newValue);
			var treeObj = $.fn.zTree.getZTreeObj("tree");
			var nodes = treeObj.transformToArray(treeObj.getNodes());
			$.each(nodes, function () {
				var name = this.name;
				var pos = name.indexOf('EXIT') + 5;
				if (pos < name.length) {
					if (this.level === 0) {
						this.icon = 'img/view_tree_classic.png';
						treeObj.expandNode(this)
					} else if (this.level === 1) {
						this.icon = 'img/bullet_blue.png';
					} else {
						this.icon = 'img/bullet_blue_small.png';
					}
					treeObj.updateNode(this);
				} else {
					treeObj.removeNode(this);
				}
			});
		});

		function zTreeOnClick(event, treeId, treeNode) {
			var startLine = 1;
			var editor = ace.edit('codeEditor');
			if (event.ctrlKey) {
				startLine = treeNode.startLine;
			} else {
				startLine = treeNode.performStartLine;
			}
			editor.gotoLine(startLine);
			editor.selection.selectTo(startLine - 1, 72);
		};
		angular.element('.ace_content').on('click', function () {
			if (scope.autoLinkFlag) {
				var editor = ace.edit('codeEditor');
				var lineNumber = editor.selection.getCursor().row + 1;
				scope.locationSVG(lineNumber);
			}
		})
	}

});
