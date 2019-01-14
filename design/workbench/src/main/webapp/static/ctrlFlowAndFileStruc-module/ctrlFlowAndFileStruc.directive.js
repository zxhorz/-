'use strict';

angular.module('ctrlFlowAndFileStrucModule').directive('ctrlFlowAndFileStrucDirective', function ($timeout, $http, infoDataService, historyUrlService) {

	return {
		restrict: 'EA',
		scope: false,
		templateUrl: 'ctrlFlowAndFileStruc-module/ctrlFlowAndFileStruc.template.html',
		replace: false,
		controller: ctrlFlowAndFileStrucController,
		link: ctrlFlowAndFileStrucLink
	};

	function ctrlFlowAndFileStrucController($scope, $http, $location, $attrs, $compile, infoDataService, historyUrlService) {
		$scope.showDependencyTable = true;
		$scope.detail_view1 = true;
		$scope.detail_print1 = false;
		$scope.detailNav = [
			{ "name": "ControlFlow" },
			{ "name": "FileStructure" },
			{ "name": "Dependency" }
		];
		$scope.selectedSvgInfo = {};    //存储文件定位信息
		if (historyUrlService.getClickFlag()) {
			$scope.selectedTab = 0;
			$scope.codeBrwRed.controlFlowTab = $scope.selectedTab;
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

		$scope.setSelectedSvgInfo = function (graphName, startline, endline, position) {
			$scope.codeBrwRed = historyUrlService.getCodeBrowRecord();
			if ($scope.selectedProgram in $scope.selectedSvgInfo) {
				$scope.selectedSvgInfo[$scope.selectedProgram].graphName = graphName;
				$scope.selectedSvgInfo[$scope.selectedProgram].startline = startline;
				$scope.selectedSvgInfo[$scope.selectedProgram].endline = endline;
				$scope.selectedSvgInfo[$scope.selectedProgram].autoLink = $scope.autoLinkFlag;
				$scope.selectedSvgInfo[$scope.selectedProgram].position = position;
				$scope.codeBrwRed.selectedSvgInfo = $scope.selectedSvgInfo;
				historyUrlService.setCodeBrowRecord($scope.codeBrwRed);
			} else {
				$scope.selectedSvgInfo[$scope.selectedProgram] = {
					graphName: graphName,
					startline: startline,
					endline: endline,
					autoLink: $scope.autoLinkFlag,
					position: position
				};
				$scope.codeBrwRed.selectedSvgInfo = $scope.selectedSvgInfo;
				historyUrlService.setCodeBrowRecord($scope.codeBrwRed);
			}
		}

		$scope.checkNav = function (nav) {
			if (nav !== "Dependency") {
				return true;
			} else {
				return $scope.showTag;
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
					$scope.setSelectedSvgInfo(graphName, startline, endline, node.getBBox());
					break;
				}
			}
			return find;
		}

	}

	function ctrlFlowAndFileStrucLink(scope, element, attr) {
		if (historyUrlService.getCodeBrowRecord().controlFlowTab) {
			scope.selectedTab = historyUrlService.getCodeBrowRecord().controlFlowTab;
			$timeout(function (scope, element, attr) {
				angular.element('.ctrlAndFileStruTab').eq(historyUrlService.getCodeBrowRecord().controlFlowTab).siblings("li").removeClass("active");
				angular.element('.ctrlAndFileStruTab').eq(historyUrlService.getCodeBrowRecord().controlFlowTab).addClass("active");
			});
		} else {
			scope.selectedTab = 0;
			$timeout(function (scope, element, attr) {
				angular.element('.ctrlAndFileStruTab').eq(0).siblings("li").removeClass("active");
				angular.element('.ctrlAndFileStruTab').eq(0).addClass("active");
			});
		}

		function downloadByFormPost(options) {
			var config = $.extend(true, { method: 'post' }, options);
			var $iframe = $('<iframe id="down-file-iframe" />');
			var $form = $('<form target="down-file-iframe" method="post" />');
			$form.attr('action', config.url);
			for (var key in config.data) {
				$form.append('<input type="hidden" name="' + key + '" value="' + config.data[key] + '" />');
			}
			$iframe.append($form);
			$(document.body).append($iframe);
			$form[0].submit();
			$iframe.remove();
		}

		scope.downloadCurDoc = function () {
			switch (scope.selectedTab) {
				case 0:
					downloadByFormPost({
						url: '/codebrowser/downloadCurCtrlFlowDoc',
						data: {
							projectId: infoDataService.getId(),
							programName: scope.selectedProgram,
							controlFlowName: scope.selectedSvg
						}
					});
					break;
				case 1:
					downloadByFormPost({
						url: '/codebrowser/downloadCurFileStructureDoc',
						data: {
							projectId: infoDataService.getId(),
							programName: scope.selectedProgram,
						}
					});
					break;
				case 2:
					scope.getdependencybase64();
					$timeout(function () {
						var imageDataB64 = scope.denpendencyimage64;
						downloadByFormPost({
							url: '/codebrowser/downloadCurDependencyDoc',
							data: {
								projectId: infoDataService.getId(),
								programName: scope.selectedProgram,
								base64Str: imageDataB64
							}
						});
					}, 500)
					break;
			}
		}

		scope.printCurDoc = function () {
			scope.onModel.modelLoading('loading', 'loading');
			scope.detail_view1 = false;
			scope.detail_print1 = true;

			angular.element("#my-print1").empty();
			var title;
			switch (scope.selectedTab) {
				case 0:
					title = '<div id="docTitle"><h1 class="myTitle1">ControlFlow Documentation</h1>' +
						'<hr style="width:100%;height:10px;">' +
						'<h2 class="myTitle2">project name:</h2>' +
						'<p class="myContent">' + infoDataService.getName() + '</p>' +
						'<h2 class="myTitle2">program name:</h2>' +
						'<p class="myContent">' + scope.selectedProgram + '</p>' +
						'<h2 class="myTitle2">controlFlow name:</h2>' +
						'<p class="myContent">' + scope.selectedSvg + '</p>' +
						'<h2 class="myTitle2">controlFlow graph:</h2></div>';
					$timeout(function () {
						angular.element("#my-print1").append(title);
						angular.element("#docTitle").after(scope.svg);
						$("#my-print1").print({
							noPrintSelector: ".no-print"
						});
					}, 500)
					$timeout(function () {
						scope.onModel.modelHide();
						scope.detail_view1 = true;
						scope.detail_print1 = false;
						angular.element("#svgclass").append(scope.svg);
					}, 500)
					break;
				case 1:
					title = '<div id="docTitle"><h1 class="myTitle1">FileStructure Documentation</h1>' +
						'<hr style="width:100%;height:10px;">' +
						'<h2 class="myTitle2">project name:</h2>' +
						'<p class="myContent">' + infoDataService.getName() + '</p>' +
						'<h2 class="myTitle2">program name:</h2>' +
						'<p class="myContent">' + scope.selectedProgram + '</p>' +
						'<h2 class="myTitle2">FileStructure</h2></div>';
					var fsTree = angular.element("#tree");
					var oriHeight = fsTree.css('height');
					fsTree.css('overflow', '');
					fsTree.css('height', '');
					angular.element("#my-print1").append(title);
					angular.element("#docTitle").after(fsTree);
					var treeObj = $.fn.zTree.getZTreeObj("tree");
					var nodes = treeObj.transformToArray(treeObj.getNodes());
					$.each(nodes, function () {
						this.backupOpen = this.open;
					});
					treeObj.expandAll(treeObj);
					$timeout(function () {
						$("#my-print1").print({
							noPrintSelector: ".no-print"
						});
					}, 500)
					fsTree.css('height', oriHeight);
					$timeout(function () {
						scope.detail_view1 = true;
						scope.detail_print1 = false;
						scope.onModel.modelHide();
						$.each(nodes, function () {
							if (this.backupOpen !== this.open) {
								treeObj.expandNode(this)
							}
							treeObj.updateNode(this);
						});
						var fs = angular.element("#fileStructure");
						fs.append(fsTree);
					}, 500)
					break;
				case 2:
					scope.showDependencyTable = false;
					scope.isMaximize = true;
					title = '<div id="docTitle"><h1 class="myTitle1">Dependency Documentation</h1>' +
						'<hr style="width:100%;height:10px;">' +
						'<h2 class="myTitle2">project name:</h2>' +
						'<p class="myContent">' + infoDataService.getName() + '</p>' +
						'<h2 class="myTitle2">program name:</h2>' +
						'<p class="myContent">' + scope.selectedProgram + '</p>' +
						'<h2 class="myTitle2">dependency graph</h2></div>';
					var d3Graph = angular.element("#dependencyTable");
					angular.element("#my-print1").append(title);
					angular.element("#docTitle").after(d3Graph);
					$timeout(function () {
						$("#my-print1").print({
							noPrintSelector: ".no-print"
						});
					}, 500)
					$timeout(function () {
						scope.onModel.modelHide();
						scope.isMaximize = false;
						scope.showDependencyTable = true;
						scope.detail_view1 = true;
						scope.detail_print1 = false;
						angular.element("#dependency").append(d3Graph);
					}, 500)
					break;
			}
		}

		scope.detailTable = function (event, index) {
			$(event.target).parent().addClass("active");
			$(event.target).parent().siblings("li").removeClass("active");
			scope.codeBrwRed = historyUrlService.getCodeBrowRecord();
			scope.selectedTab = index;
			scope.codeBrwRed.controlFlowTab = scope.selectedTab;
			historyUrlService.setCodeBrowRecord(scope.codeBrwRed);
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
							scope.setSelectedSvgInfo(title, startline, endline, event.currentTarget.getBBox());
							scope.selectedSvg = title;
							scope.changeSvg(true);
						} else {
							scope.setSelectedSvgInfo(scope.selectedSvg, startline, endline, event.currentTarget.getBBox());
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
									var top = 0, left = 0;
									if (item.position.x > 0 || item.position.y > 0) {
										top = item.position.y;
										left = item.position.x * 1.5;
									} else {
										top = nodes[0].getBBox().y;
										left = nodes[0].getBBox().x * 1.5
									}
									$timeout(function () {
										angular.element("#svgclass").scrollTop(top * 2);
										angular.element("#svgclass").scrollLeft(left);
									});
									var editor = ace.edit('codeEditor');
									editor.gotoLine(item.startline);
									editor.selection.selectTo(item.endline - 1, 72);
									if (scope.autoLinkFlag !== item.autoLink) {
										scope.autoLink();
									}
								} else {
									$timeout(function () {
										angular.element("#svgclass").scrollTop(0);
										angular.element("#svgclass").scrollLeft(0);
									});
								}
							}
						} else {
							$timeout(function () {
								angular.element("#svgclass").scrollTop(0);
								angular.element("#svgclass").scrollLeft(0);
							});
						}
					} else {
						$timeout(function () {
							angular.element("#svgclass").scrollTop(0);
							angular.element("#svgclass").scrollLeft(0);
						});
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
			var editor = ace.edit('codeEditor');
			if (scope.autoLinkFlag && editor.getSelectionRange().end.row === editor.getSelectionRange().start.row &&
				editor.getSelectionRange().end.column === editor.getSelectionRange().start.column) {
				var lineNumber = editor.selection.getCursor().row + 1;
				scope.locationSVG(lineNumber);
			}
		})
	}

});
