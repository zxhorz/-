'use strict';
var app = angular.module('codeBrowserModule');
app.controller('codeBrowserController', function ($scope, $http, $timeout, infoDataService, detailInfoService, $stateParams, $compile, historyUrlService, $rootScope) {
  // 进入该页面，初始化tagInfo
  infoDataService.setTagInfo();
  infoDataService.setFromPage('codebrowser');
  $scope.selectedProgram = '';
  $scope.selectedType = '';
  $scope.mode = 'cobol';
  $scope.ctrlFlowShow = true;
  $scope.textShow = false;
  // $scope.datafinish = false;
  var isHistory = false;
  var item2 = {};

  // var $scope.codeBrwRed2 = {};
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
  // editor.setOption("wrap", "off");
  $scope.aceOptions = {
    // require: ['ace/ext/language_tools'],
    advanced: {
      enableSnippets: true,  //启用代码块提示功能
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
  var langTools = ace.require("ace/ext/language_tools");

  $scope.initAutoLink = function () {
    $scope.autoLinkFlag = false;
    angular.element('#autoLinkIcon').removeClass("autoLink");
  }

  $scope.showTag = true;
  getFolderTree();
  checkSO();
  $scope.initAutoLink();
  $scope.codeBrwRed = historyUrlService.getCodeBrowRecord();

  //回退功能
  if (historyUrlService.getClickFlag()) {
    historyUrlService.setUrlInfo('codeBrowser');
    historyUrlService.setClickFlag(true);
    $scope.codeBrwRed.item = {};
    $scope.codeBrwRed.item2 = {};
    $scope.codeBrwRed.selectedSvgInfo = {};
    // $scope.codeBrwRed.controlFlowTab =true;
  } else {
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
        // infoDataService.setTagType('program');
        showControlFlow(folderName, $stateParams.location, false, $stateParams.definitionStart, $stateParams.definitionEnd);
      }
    }
  }
  detailInfoService.setScope($scope);

  infoDataService.setPage('codebrowser');

  $scope.aceChanged = function (_editor) {

  }

  $scope.showSelected = function (node, $parentNode) {
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
    // $('.auto-tags-control').select2({
    //   data: [],
    //   tags: true
    // });
    angular.element('auto-tag-directive').triggerHandler('initAutoTag');
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

  //点击树节点显示中间ace或者右边ctr
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
      // $scope.showTag = true;
    } else if (name === 'COPYBOOK') {
      infoDataService.setTagType('copybook');
      // $scope.showTag = true;
      infoDataService.setFromPage('codebrowser');
      tagOperation(flag);
      clearCtrlStruc();
      getSourceCode(location, false, startPos, endPos);
      $scope.aceOptions.mode = 'cobol';
    } else if (name === 'JOB' || name === 'PROC') {
      infoDataService.setTagType(name);
      // $scope.showTag = true;
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
      // commonFile = false;
    }
  }

  function tagOperation(flag) {
    if (flag) {
      $scope.recommandTags();
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
        if ($stateParams.location === '') {
          defaultFunc();
        }
      }
    }).error(function (data) {
      console.info('getFolderTree error');
    });
  }

  //初始化tree数据
  function initTreeData(data) {
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
          if ($scope.autoLinkFlag!==item.autoLink) {
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
      if ($scope.selectedProgram !== data.data[0].graphName) {
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
        } else {
          $scope.selectedSvg = '';
          $scope.svg = '';
        }
        getFileStructrue();
      }
    }).error(function (data) {
      getFileStructrue();
      $scope.onModel.modelShow('error', 'Failed to generate controlflow');
      console.info('get control flow error');
    });
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
      $scope.onModel.modelShow('success', 'success');
    }).error(function (data) {
      $scope.onModel.modelShow('error', 'Failed to generate filestructure');
      console.info('get filestructure error');
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

  function checkSO() {
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
          // page = page == 0 ? 1 : page;
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
    var folderName = $scope.treeLocation.split('/')[0].toLocaleUpperCase();
    var defaultName = $scope.treeLocation.split('/')[1];
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
  })

  //划词
  angular.element(".ace_content").on('mouseup', function () {
    var txt = '';
    txt = $scope.editor.getSession().getTextRange($scope.editor.getSelectionRange());
    var reg=new RegExp(/[A-Za-z].*/);
    if (reg.test(txt)) {
      // if (txt && txt!=='') {
      $scope.text = txt;
      $scope.print();
    }
  })

  function convert(datas) {
    // var res = [];
    var abbCandidates = [];
    // var radio = [];
    for (var i = 0; i < datas.length; i++) {
      abbCandidates.push(datas[i][0].join(' '));
    }
    return abbCandidates;
  }

  $scope.print = function () {
    if ($scope.text && $scope.text !== '') {
      $scope.ctrlFlowShow = false;
      $scope.tagModelShow = true;
      $scope.onModel.modelLoading('loading', 'loading');
      var selected_name = $scope.text.replace(/ /g, '-');
      selected_name = selected_name.replace(/[.:]/g, '-');
      $http({
        method: 'POST',
        url: 'http://' + $scope.serverIp + ':5000/predict_name/',
        data: { 'abbr_name': selected_name },
        headers: { 'Content-Type': 'application/json' }
      }).success(function (data) {
        //显示成功的提示框
        data = data.predictions;
        $scope.onModel.modelHide();
        var result = convert(data);
        $scope.table = result;
        $scope.textShow = true;
        var left = angular.element(".ace_selection").position().left;
        var top = angular.element(".ace_selection").position().top;
        angular.element(".toolTips").css('left', left + 120);
        angular.element(".toolTips").css('top', top + 60);
      }).error(function () {
        $scope.onModel.modelShow('error', 'error');
      });
    } else {
      $scope.onModel.modelShow('error2', 'Please input at least one item');
    }
  }
  editor.on('mousewheel', function () {
    if ($scope.textShow) {
      $timeout(function () {
        $scope.textShow = false;
      });
    }
  })
  angular.element('body').on('mousedown', function () {
    if ($scope.textShow) {
      $scope.textShow = false;
    }
  });
});