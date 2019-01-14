'use strict';
var app = angular.module('codeBrowserModule');
app.controller('codeBrowserController', function ($scope, $http, $timeout, infoDataService, detailInfoService, $stateParams, $compile, historyUrlService, $modal, $state) {
  // 进入该页面，初始化tagInfo
  infoDataService.setTagInfo();
  infoDataService.setFromPage('codebrowser');
  $scope.selectedProgram = '';
  $scope.selectedType = '';
  $scope.mode = 'cobol';
  $scope.ctrlFlowShow = true;
  $scope.textShow = false;
  //默认窗口最小化
  $scope.isMaximize = false;
  var isHistory = false;
  var item2 = {};

  //tree 每次展开20条
  var num = 20;

  var tally = [];
  var total = 0;
  var count = 0;
  var dataInfo = [];
  var pre = {};
  var next = {};
  var dataSeleted;
  $scope.treeOptions = {
    allowDeselect: false,   //选中后，再次点击同一个元素，还是选中状态
    nodeChildren: 'children',
    dirSelectable: true,
    expanded: true,
    injectClasses: {
      'ul': 'c-ul',
      'li': 'c-li',
      'liSelected': 'c-liSelected',
      'iExpanded': 'c-iExpanded',
      'iCollapsed': 'c-iCollapsed',
      'iLeaf': 'c-iLeaf',
      'label': 'c-label',
      'labelSelected': 'c-labelSelected'
    }
  };
  $scope.callTree = true;

  var editor = ace.edit('codeEditor');
  //设置只读（true时只读，用于展示代码）
  editor.setReadOnly(true);
  editor.setOption("wrap", "off");
  $scope.aceOptions = {
    require: ['ace/ext/language_tools'],
    advanced: {
      enableSnippets: false,  //启用代码块提示功能
      enableBasicAutocompletion: true,
      enableLiveAutocompletion: true
    },
    useWrapMode: true,
    showGutter: true,
    theme: 'cobol-mainframe',
    mode: $scope.mode.toLowerCase(),
    firstLineNumber: 1,
    onLoad: function (_ace) {
      $scope.editor = _ace;
      $scope.aceLoaded = function () {
        _ace.getSession().setMode('ace/mode/' + $scope.mode.toLowerCase());
      }
    },
  }

  $scope.initAutoLink = function () {
    $scope.autoLinkFlag = false;
    angular.element('#autoLinkIcon').removeClass("autoLink");
  }

  $scope.showTag = false;
  getFolderTree();
  checkSO();
  $scope.initAutoLink();
  $scope.codeBrwRed = historyUrlService.getCodeBrowRecord();

  //打开页面
  if (historyUrlService.getClickFlag()) {
    historyUrlService.setUrlInfo('codeBrowser');
    historyUrlService.setClickFlag(true);
    $scope.codeBrwRed.item = {};
    $scope.codeBrwRed.item2 = {};
    $scope.codeBrwRed.selectedSvgInfo = {};
    // $scope.codeBrwRed.controlFlowTab =true;
  } else {
    //回退功能
    var WatchEvent = $scope.$watch('datafinish', function (newValue) {
      if (newValue !== undefined && $scope.datafinish) {
        var item = historyUrlService.getCodeBrowRecord().item;
        if (item.node.name === 'THE PREVIOUS...' || item.node.name === 'NEXT...') {
          isHistory = true;
          codeBrowserShow(item.node, item.index);
          var node2 = historyUrlService.getCodeBrowRecord().item2.node;
          $scope.expandedNodes.push(dataSeleted);
          acrAndCtr(node2);
        } else {
          findSelectedNode(JSON.parse(JSON.stringify(dataInfo.children[item.index])), item.node.name, item.index);
          codeBrowserShow(item.node, item.index);
        }
        historyUrlService.setClickFlag(true);
      }
    })
  }

  var folderName = $stateParams.location.split('/')[0].toLocaleUpperCase();
  var folderLevel = $stateParams.location.split('/').length;

  //从  paragraph页面进来
  if ($stateParams.endLine || $stateParams.startLine) {
    var fileName = $stateParams.location.substring($stateParams.location.lastIndexOf('/') + 1);
    if ($scope.selectedProgram !== fileName) {
      $scope.selectedProgram = fileName;
      $scope.treeLocation = $stateParams.location;
      // tag模块数据准备
      var selectedNames = [];
      selectedNames.push(fileName);
      infoDataService.setSelectedNames(selectedNames);
      infoDataService.setTagType('program');
      getParaSourceCode();
    }
  }
  //从  program页面进来
  else {
    if ($stateParams.location !== '') {
      var fileName = $stateParams.location.substring($stateParams.location.lastIndexOf('/') + 1);
      if ($scope.selectedProgram !== fileName) {
        $scope.selectedProgram = fileName;
        $scope.treeLocation = $stateParams.location;
        // tag模块数据准备
        var selectedNames = [];
        selectedNames.push(fileName);
        infoDataService.setSelectedNames(selectedNames);
        showControlFlow(folderName, $stateParams.location, false, $stateParams.definitionStart, $stateParams.definitionEnd);
      }
    }
  }
  detailInfoService.setScope($scope);
  infoDataService.setPage('codebrowser');
  $scope.aceChanged = function (_editor) {
  }

  $scope.showSelected = function (node, $parentNode) {
    if (node.type !== 'directory') {
      if (node.name === 'THE PREVIOUS...' || node.name === 'NEXT...') {
        item2 = Object.keys(item2).length > 0 ? item2 : historyUrlService.getCodeBrowRecord().item2;
        $scope.codeBrwRed.item2 = item2;
        $scope.codeBrwRed.item.node = node;
      } else {
        $scope.codeBrwRed.item.node = node;
        item2 = JSON.parse(JSON.stringify($scope.codeBrwRed.item));
      }

      $scope.recommandedTags = [];
      $scope.autoTagModel = [];
      var index = 0;
      for (var i = 0; i < dataInfo.children.length; i++) {
        if (dataInfo.children[i].name === $parentNode.name) {
          index = i;
          $scope.codeBrwRed.item.index = i;
          break;
        }
      }
      historyUrlService.setCodeBrowRecord($scope.codeBrwRed);
      $scope.codeBrwRed.item.startLine = '';
      $scope.codeBrwRed.item.endLine = '';
      codeBrowserShow(node, index);
    }
  }

  //展示页面全部数据
  function codeBrowserShow(node, index) {
    $scope.tagModelShow = false;
    $scope.ctrlFlowShow = true;
    if (node.name === 'THE PREVIOUS...') {
      if (isHistory) {
        isHistory = false;
      } else {
        node.count = node.count - 1;
      }
      dataGrouping(JSON.parse(JSON.stringify(dataInfo.children[index])), index, node.count);
    } else if (node.name === 'NEXT...') {
      if (isHistory) {
        isHistory = false;
      } else {
        node.count = node.count + 1;
      }
      dataGrouping(JSON.parse(JSON.stringify(dataInfo.children[index])), index, node.count);
    }
    else {
      acrAndCtr(node)
    }
  }

  //点击树节点显示中间ace和右边ctr
  function acrAndCtr(node) {
    var fileName = node.fileType.toLocaleUpperCase();
    if (($scope.selectedType + '/' + $scope.selectedProgram) !== (fileName + '/' + node.name)) {
      $scope.initAutoLink();
      $scope.selectedProgram = node.name;
      $scope.selectedType = fileName;
      var selectedFilePath = fileName + '/' + node.name;
      $scope.treeLocation = node.fileType + '/' + node.name;
      if (node.children.length === 0) {
        var selectedNames = [];
        selectedNames.push($scope.selectedProgram);
        infoDataService.setSelectedNames(selectedNames);
        if (historyUrlService.getCodeBrowRecord().item.startLine || historyUrlService.getCodeBrowRecord().item.endLine) {
          showControlFlow(fileName, selectedFilePath, true, historyUrlService.getCodeBrowRecord().item.startLine, historyUrlService.getCodeBrowRecord().item.endLine);
        } else {
          showControlFlow(fileName, selectedFilePath, true, 1, 0);
        }
      }
    }
  }

  //copybook、jcl不显示ControlFlow
  function showControlFlow(name, location, flag, startPos, endPos) {
    // var commonFile = true;
    if (name === 'COBOL') {
      infoDataService.setTagType('program');
      infoDataService.setFromPage('codebrowser');
      tagOperation(flag);
      getSourceCode(location, true, startPos, endPos);
      $scope.aceOptions.mode = 'cobol';
    } else if (name === 'COPYBOOK') {
      infoDataService.setTagType('copybook');
      infoDataService.setFromPage('codebrowser');
      tagOperation(flag);
      clearCtrlStruc();
      getSourceCode(location, false, startPos, endPos);
      $scope.aceOptions.mode = 'cobol';
    } else if (name === 'JOB' || name === 'PROC') {
      infoDataService.setTagType(name);
      infoDataService.setFromPage('codebrowser');
      tagOperation(flag);
      clearCtrlStruc();
      getSourceCode(location, false, startPos, endPos);
      $scope.aceOptions.mode = 'jcl';
    } else {
      // other
      infoDataService.setFromPage('');
      $scope.showTag = false;
      clearCtrlStruc();
    }
  }

  function tagOperation(flag) {
    if (flag) {
      //获取推荐的tag
      $scope.recommandTags();
      // 获取tag信息
      $scope.getAllSelectedTags();
    }
  }

  function clearCtrlStruc() {
    $scope.showSelectedSvg = false;
    $scope.controlFlowSvgMap = [];
    $scope.zNodes = [];
    $scope.selectedSvg = '';
    $scope.svg = '';
    $scope.svgContent = '';
    $scope.callTree = false;
  }
  // 从menu点击进入后，默认打开第一个文件
  function defaultFunc() {
    var node = $scope.selected;
    if ($scope.selectedProgram !== node.name) {
      $scope.initAutoLink();
      var fileName = node.fileType.toLocaleUpperCase();
      $scope.selectedType = fileName;
      $scope.selectedProgram = node.name;
      var selectedFilePath = fileName + '/' + node.name;
      $scope.treeLocation = node.fileType + '/' + node.name;
      if (node.children.length === 0) {
        var selectedNames = [];
        selectedNames.push($scope.selectedProgram);
        infoDataService.setSelectedNames(selectedNames);
        infoDataService.setTagType('program');
        $timeout(function () {
          tagOperation(true);
        });
        getSourceCode(selectedFilePath, true, 1, 0);
      }
    }
  }

  $scope.selectNodeHead = function (node) {
    console.info(node);
  }

  function cleanTable() {
    $scope.table = '';
    $scope.showClean = false;
    $scope.text = '';
  };

  //显示树节点
  function getFolderTree() {
    $http({
      method: 'GET',
      url: './codebrowser/getFolderTree',
      params: {
        'projectId': infoDataService.getId()
      }
    }).success(function (data) {
      $scope.datafinish = true;
      dataInfo = data.data;
      initTreeData(JSON.parse(JSON.stringify(dataInfo)));
      if (historyUrlService.getClickFlag()) {
        defaultTreeSet();
        if ($scope.selected.type === 'file') {
          if ($stateParams.location === '') {
            defaultFunc();
          }
        } else {
          $scope.aceModel = "Welcome to Code Browser, please select a file.";
        }
      }
    }).error(function (data) {
      console.info('getFolderTree error');
    });
  }

  //初始化tree数据
  function initTreeData(data) {
    tally = [];
    angular.forEach(data.children, function (item, index, array) {
      var t = item.children.length / num
      tally.push(t);
      if (t > 1) {
        item.children = item.children.slice(0, num);
        next = { name: "NEXT...", count: 1, fileType: item.children[0].fileType };
        item.children.push(next);
      }
    });
    $scope.dataForTheTree = data;
  }

  //点击树节点处理树节点的数据
  function dataGrouping(data, index, page) {
    if (page === 1) {
      data.children = data.children.slice(0, num * page);
      if (Math.ceil(tally[index]) > 1) {
        next = { name: "NEXT...", count: page, fileType: data.children[0].fileType };
        data.children.push(next);
      }
    } else if (page > 1 && page < Math.ceil(tally[index])) {
      pre = { name: "THE PREVIOUS...", count: page, fileType: data.children[0].fileType };
      next = { name: "NEXT...", count: page, fileType: data.children[0].fileType };
      data.children = data.children.slice(num * (page - 1), num * page);
      data.children.unshift(pre);
      data.children.push(next);
    } else {
      data.children = data.children.slice(num * (page - 1), data.children.length);
      if (page === Math.ceil(tally[index])) {
        pre = { name: "THE PREVIOUS...", count: page, fileType: data.children[0].fileType };
        data.children.unshift(pre);
      }
    }
    dataSeleted = data;
    $scope.dataForTheTree.children[index] = data;
  }

  function getSourceCode(location, flag, startPos, endPos) {
    $scope.canSave = false;
    $http({
      method: 'GET',
      url: './codebrowser/getSourceCode',
      params: {
        'projectId': infoDataService.getId(),
        'filePath': location
      }
    }).success(function (data) {
      $scope.aceModel = data.data;
      $timeout(function () {
        $scope.gotoLine(startPos, endPos);
      });
      // 只有cobol才生成controlflow和filestructure
      // TODO:非cobol时候，界面是否需要隐藏controlflow和filestructure功能
      if (flag) {
        $scope.callTree = true;
        if ($scope.selectedSvgInfo && $scope.selectedProgram in $scope.selectedSvgInfo) {
          var item = $scope.selectedSvgInfo[$scope.selectedProgram];
          if ($scope.autoLinkFlag !== item.autoLink) {
            $scope.autoLink();
          }
          getControlFlow(item.graphName);
        } else {
          getControlFlow(null);
        }
      }
    }).error(function (data) {
      console.info('getParaSourceCode error');
    });
  }

  $scope.showSelectedSvg = false;

  $scope.gotoLine = function (startline, endline) {
    editor.gotoLine(startline);
    editor.selection.selectTo(endline);
  }

  function getControlFlow(paraName) {
    $scope.showSelectedSvg = false;
    $scope.svg = '';
    $scope.onModel.modelLoading('loading', 'loading');
    $http({
      method: 'GET',
      url: './codebrowser/controlFlow',
      params: {
        'projectId': infoDataService.getId(),
        'fileName': $scope.selectedProgram
      },
    }).success(function (data) {
      $scope.showSelectedSvg = true;
      if (data.data.length === 0) {
        $scope.onModel.modelShow('error', 'Failed to generate controlflow');
      } else if ($scope.selectedProgram !== data.data[0].graphName) {
        data.data[0].graphName = $scope.selectedProgram;
        getControlFlow(paraName);
      } else {
        $scope.controlFlowSvgMap = data.data;
        if (data.data.length > 0) {
          if (paraName === null) {
            $scope.selectedSvg = data.data[0].graphName;
            $scope.svgContent = data.data[0].svgContent;
          }
          else {
            $scope.selectedSvg = data.data[0].graphName;
            $scope.svgContent = data.data[0].svgContent;
            var realParaName = paraName.substring(paraName.indexOf('.') + 1);
            for (var i = 0; i < data.data.length; i++) {
              if (data.data[i].graphName === paraName || data.data[i].graphName.indexOf(realParaName) !== -1) {
                $scope.selectedSvg = data.data[i].graphName;
                $scope.svgContent = data.data[i].svgContent;
                break;
              }
            }
          }
          $scope.svg = $compile($scope.svgContent)($scope);
          if ($scope.selectedTab === 0) {
            $scope.onModel.modelShow('success', 'success');
          }
        } else {
          $scope.selectedSvg = '';
          $scope.svg = '';
        }
        getFilestr_Depe();
      }
    }).error(function (data) {
      getFilestr_Depe();
      $scope.onModel.modelShow('error', 'Failed to generate controlflow');
      console.info('get control flow error');
    });
  }

  function getFilestr_Depe() {
    getFileStructrue();
    getDependency();
    getExternalDependencies();
  }
  function getFileStructrue() {
    $http({
      method: 'GET',
      url: './codebrowser/fileStructure',
      params: {
        'projectId': infoDataService.getId(),
        'fileName': $scope.selectedProgram
      }
    }).success(function (data) {
      $scope.zNodes = data.data;
      if ($scope.selectedTab === 1) {
        $scope.onModel.modelShow('success', 'success');
      }
    }).error(function (data) {
      $scope.onModel.modelShow('error', 'Failed to generate filestructure');
      console.info('get filestructure error');
    });
  }

  function getDependency() {
    if ($scope.showTag) {
      $http({
        method: 'GET',
        url: './codebrowser/getDependency',
        params: {
          'projectId': infoDataService.getId(),
          'programName': $scope.selectedProgram
        }
      }).success(function (data) {
        $scope.d3url = data.data.jsonString;
        if ($scope.selectedTab === 2) {
          $scope.onModel.modelShow('success', 'success');
        }
      }).error(function (data) {
        $scope.onModel.modelShow('error', 'Failed to generate dependency');
        console.info('get dependency error');
      });
    }
  }

  function getExternalDependencies() {
    $http({
      method: 'GET',
      url: './codebrowser/getExternalDependencies',
      params: {
        'projectId': infoDataService.getId(),
        'programName': $scope.selectedProgram
      }
    }).success(function (data) {
      if (data && data.data) {
        if (data.data.length > 0) {
          $scope.DependenceTable = data.data;
          $scope.DependenceTableThead = data.data[0];
          $scope.DependenceTable.shift();
        }
      }
    }).error(function (data) {
      console.info("error");
    });
  }

  function getParaSourceCode() {
    $scope.canSave = false;
    $http({
      method: 'GET',
      url: './codebrowser/getSourceCode',
      params: {
        'projectId': infoDataService.getId(),
        'filePath': $stateParams.location
      }
    }).success(function (data) {
      getControlFlow($stateParams.paragraphName);
      // getFileStructrue();
      $scope.aceModel = data.data;
      $timeout(function () {
        $scope.gotoLine($stateParams.startLine, $stateParams.endLine);
      }, 1000);
      $scope.codeBrwRed.item.startLine = $stateParams.startLine;
      $scope.codeBrwRed.item.endLine = $stateParams.endLine;
    }).error(function (data) {
      console.info('getParaSourceCode error');
    });
  }

  //flag默认为true，用来的判断关闭弹框后是否刷新页面
  function checkSO(flag) {
    $http({
      method: 'GET',
      url: './job/specjobstatus',
      params: {
        'projectId': infoDataService.getId(),
        'jobName': 'SO'
      }
    }).success(function (data) {
      if (data.code === 'ACK') {
        if (data.data !== 'S') {
          $scope.showTag = false;
          if (flag) {
            $state.reload();
          }
        } else {
          $scope.showTag = true;
        }
      } else {
        console.info('error');
      }
    }).error(function (data) {
      console.info('error');
    });
  }


  function defaultTreeSet() {
    // default selectedNodes
    var defaultName = '';
    if ($stateParams.location !== '') {
      defaultName = $stateParams.location.substring($stateParams.location.lastIndexOf('/') + 1);
    }
    if (defaultName === '') {
      // 从menu菜单进入
      var treeData = dataInfo;
      $scope.expandedNodes = [];
      while (treeData.children.length > 0) {
        treeData = treeData.children[0];
        $scope.expandedNodes.push(treeData);
      }
      $scope.expandedNodes.pop();
      $scope.selected = treeData;
      $scope.codeBrwRed.item.node = treeData;
      item2 = JSON.parse(JSON.stringify($scope.codeBrwRed.item));
      $scope.codeBrwRed.item2 = item2;
      $scope.codeBrwRed.item.index = 0;
      historyUrlService.setCodeBrowRecord($scope.codeBrwRed);
    } else {
      var treeData = JSON.parse(JSON.stringify(dataInfo));
      $scope.expandedNodes = [];
      $scope.selected = '';
      for (var i = 0; i < treeData.children.length; i++) {
        if (treeData.children[i].name === folderName) {
          findSelectedNode(treeData.children[i], defaultName, i);
          $scope.codeBrwRed.item.node = $scope.selected;
          $scope.codeBrwRed.item.index = i;
          historyUrlService.setCodeBrowRecord($scope.codeBrwRed);
          return;
        }
      }
    }
  }

  function findSelectedNode(treeData, defaultName, index) {
    var tallyItem = treeData.children.length / num;
    for (var i = 0; i < treeData.children.length; i++) {
      if (treeData.children[i].children.length === 0) {
        if (treeData.children[i].name === defaultName) {
          var page = Math.ceil(i / num);
          if (i % num === 0) {
            page = page + 1;
          }
          dataGrouping(JSON.parse(JSON.stringify(treeData)), index, page);
          $scope.expandedNodes.push(dataSeleted);
          for (var k = 0; k < dataSeleted.children.length; k++) {
            if (dataSeleted.children[k].name === defaultName) {
              $scope.selected = dataSeleted.children[k];
              break;
            }
          }
          return;
        } else {
          continue;
        }
      } else {
        findSelectedNode(treeData.children[i], defaultName, index);
      }
    }
  }

  //点击pre 或者 next后再点击ace,选中tree的节点
  angular.element('#codeEditor').on('click', function () {
    if ($scope.selected.type === 'file') {
      var folderName = $scope.treeLocation.split('/')[0].toLocaleUpperCase();
      var defaultName = $scope.treeLocation.split('/')[1];
      if ($scope.selected.fileType === folderName && $scope.selected.name === defaultName) {
        return;
      } else {
        var treeData = JSON.parse(JSON.stringify(dataInfo));
        $scope.expandedNodes = [];
        $scope.selected = '';
        for (var i = 0; i < treeData.children.length; i++) {
          if (treeData.children[i].name === folderName) {
            findSelectedNode(treeData.children[i], defaultName, i);
            $scope.$digest();
            return;
          }
        }
      }
    }
  })
  var mouse = { x: 0, y: 0 };
  editor.on('mousedown', function (event) {
    mouse.x = event.clientX;
    mouse.y = event.clientY;
  });
  //划词
  editor.on('mouseup', function (event) {
    if (event.clientX != mouse.x || event.clientY != mouse.y) {
      var txt = '';
      txt = $scope.editor.getSession().getTextRange($scope.editor.getSelectionRange());
      var reg = new RegExp(/[A-Za-z].*/);
      if (reg.test(txt) && $scope.selected.type === 'file') {
        var line = $scope.editor.getSelectionRange().end.row - $scope.editor.getSelectionRange().start.row + 1
        $scope.text = txt;
        $scope.print(line);
      }
    }
  });

  function convert(datas) {
    // var res = [];
    var abbCandidates = [];
    // var radio = [];
    for (var i = 0; i < datas.length; i++) {
      abbCandidates.push(datas[i][0].join(' '));
    }
    return abbCandidates;
  }

  $scope.print = function (line) {
    if ($scope.text && $scope.text !== '') {
      $scope.ctrlFlowShow = false;
      $scope.tagModelShow = true;
      $scope.onModel.modelLoading('loading', 'loading');
      var selected_name = $scope.text.replace(/ /g, '-');
      selected_name = selected_name.replace(/[.:]/g, '-');
      $http({
        method: 'POST',
        url: '/pyserver/predict_name/',
        data: { 'abbr_name': selected_name },
        headers: { 'Content-Type': 'application/json' }
      }).success(function (data) {
        //显示成功的提示框
        data = data.predictions;
        $scope.onModel.modelHide();
        var result = convert(data);
        $scope.table = result[0];
        $scope.textShow = true;
        tipPosition(line);
      }).error(function () {
        $scope.onModel.modelShow('error', 'error');
      });
    } else {
      $scope.onModel.modelShow('error2', 'Please input at least one item');
    }
  }
  function tipPosition(line) {
    var left = angular.element(".ace_selection").position().left;
    var top = angular.element(".ace_selection").position().top;
    var height = angular.element("#codeEditor").height();
    var titleHeight = angular.element(".titleTxt").height();
    if (line > 1) {
      angular.element(".toolTips").css('left', 200);
    } else {
      angular.element(".toolTips").css('left', left + 120);
    }
    $timeout(function () {
      var tipHeight = angular.element(".toolTips").height();
      if (height - top > tipHeight + 6 || height - top > tipHeight + line * 14 + 6) {
        var tipTop = line > 6 ? titleHeight + top + 60 : titleHeight + top + line * 14 + 6;
        angular.element(".toolTips").css('top', tipTop);
      } else {
        angular.element(".toolTips").css('top', top - tipHeight);
      }
    });
  }
  editor.on('mousewheel', function () {
    if ($scope.textShow) {
      $timeout(function () {
        $scope.textShow = false;
      });
    }
  })
  angular.element('body').on('click', function () {
    if ($scope.textShow) {
      $timeout(function () {
        $scope.textShow = false;
      });
    }
  });

  $scope.fileManager = function () { // show missing code
    var modal = $modal.open({
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
      //检查数据是否进行了so分析，如果未分析，则刷新页面
      checkSO(true);
    },
      function (reason) {
        $state.reload();
      }
    );
  }

  //download program doc
  $scope.downloadAllDOC = function () {
    var imageDataB64;
    if ($scope.showTag && $scope.selected.type === 'file') {
      $scope.getdependencybase64();
      imageDataB64 = $scope.denpendencyimage64;
    } else {
      imageDataB64 = '';
    }
    $timeout(function () {
      var options = {
        url: '/codebrowser/downloadOneProgramDoc',
        data: {
          projectId: infoDataService.getId(),
          programName: $scope.selectedProgram,
          base64Str: imageDataB64
        }
      }
      var config = $.extend(true, { method: 'post' }, options);
      var $iframe = $('<iframe id="down-pgm-file-iframe" />');
      var $form = $('<form target="down-pgm-file-iframe" method="post" />');
      $form.attr('action', config.url);
      for (var key in config.data) {
        $form.append('<input type="hidden" name="' + key + '" value="' + config.data[key] + '" />');
      }
      $iframe.append($form);
      $(document.body).append($iframe);
      $form[0].submit();
      $iframe.remove();
    }, 500)
  }

  $scope.detail_view = true;
  $scope.detail_print = false;

  $scope.printAllDoc = function () {
    $scope.onModel.modelLoading('loading', 'loading');
    angular.element("#my-print2").attr("src", "");
    $scope.detail_view1 = false;
    $scope.detail_print1 = true;
    $http({
      method: 'POST',
      url: './codebrowser/printOneProgramPdf',
      params: {
        projectId: infoDataService.getId(),
        programName: $scope.selectedProgram,
      },
      responseType: 'arraybuffer'
    }).success(function (response) {
      var bin = new Blob([response], {
        type: 'application/pdf'
      });
      var href = URL.createObjectURL(bin);
      $scope.onModel.modelHide();
      angular.element("#my-print2").empty();
      angular.element("#my-print2").attr("src", href);
      $timeout(function () {
        $("#my-print2")[0].contentWindow.print({
          noPrintSelector: ".no-print"
        });
      }, 500)
      $scope.detail_view1 = true;
      $scope.detail_print1 = false;
    }).error(function (data) {

    });
  }

  function detailLink(scope, element, attr) {
    scope.$watch('tempHtml', function (newValue, oldValue) {
      if (typeof (oldValue) !== 'undefined' && newValue !== "") {
        angular.element("#my-print1").empty();
        angular.element("#my-print1").attr("src", newValue);
        $timeout(function () {
          $("#my-print")[0].contentWindow.print({
            noPrintSelector: ".no-print"
          });
        }, 500)
        scope.detail_view1 = true;
        scope.detail_print1 = false;
      }
    });
  }
});
