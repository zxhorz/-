var app = angular.module('predict', ['treeControl']);
app.controller('predictController', function ($scope, $http, $timeout, infoDataService) {
    $scope.mode = 'cobol';
    $scope.treeOptions = {
        nodeChildren: "children",
        dirSelectable: true,
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
    }
    $scope.aceOptions = {
        require: ['ace/ext/language_tools'],
        advanced: {
            enableSnippets: true,  //启用代码块提示功能
            enableBasicAutocompletion: true,
            enableLiveAutocompletion: true
        },
        useWrapMode: true,
        showGutter: false,
        theme: 'cobol-mainframe',
        mode: $scope.mode.toLowerCase(),
        firstLineNumber: 1,
        onLoad: function (_ace) {
                $scope.editor = _ace;
                // _ace.getSession().setMode('ace/mode/' + $scope.mode.toLowerCase());
        },
        // onChange: aceChanged
    }
    // $http({
    //     method: 'GET',
    //     url: './host/server/ip'
    // }).success(
    //     function (data) {
    //         $scope.serverIp = data.data;
    //     }).error(function (data) {
    //         console.log('error to get server ip');
    //     });
    $scope.serverIp = infoDataService.getServerIp();
    getFolderTree();
    function getFolderTree() {
        $http({
            method: 'GET',
            url: './codebrowser/getFolderTree',
            params: {
                'projectId': infoDataService.getId()
            }
        }).success(function (data) {
            $scope.dataForTheTree = data.data;
            defaultTreeSet();
        }).error(function (data) {
            console.info('getFolderTree error');
        });
    }

    $scope.showSelected = function (node, selected, $parentNode) {
        var fileName = node.fileType.toLocaleLowerCase();
        $scope.selectedProgram = node.name;
        var selectedFilePath = fileName + '/' + node.name;
        if (node.children.length == 0) {
            if (fileName === 'cobol' || fileName === 'copybook') {
                $scope.aceOptions.mode = 'cobol';
            } else {
                $scope.aceOptions.mode = 'jcl';
            }
            getSourceCode(selectedFilePath);
        }
    }

    function getSourceCode(location, fileName) {
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
        }).error(function (data) {
            console.info('getParaSourceCode error');
        });
    }

    $scope.select_copy = function (t) {
        var txt = '';
        txt = $scope.editor.getSession().getTextRange($scope.editor.getSelectionRange());
        if(txt && txt !==''){
            $scope.text = txt;
            $scope.print();
        }
    }
  
    $scope.checkIndex = function (i) {
        if (i == 0) {
            return true;
        } else {
            return false;
        }
    }
    //所有搜索过的词组成的数组
    var text = [];
    //提示框隐藏
    $scope.display = false;
    //pridict按钮click事件
    $scope.print = function () {
        $scope.table = '';
        $scope.radioTt = '';
        if ($scope.text && $scope.text !== '') {
            $scope.disable = true;
            $scope.display = true;
            $scope.part = 'loading';
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
                $scope.part = 'success';
                $timeout(function () {
                    $scope.display = false;
                }, 1000);
                $scope.disable = false;
                var result = convert(data);
                $scope.table = result;
                $scope.show = true;
                $scope.showClean = true;
            }).error(function () {
                $scope.part = 'error';
                $timeout(function () {
                    $scope.display = false;
                }, 1000);
            });
        } else {
            $scope.part = 'error2';
            $scope.errortip = 'Please input at least one item';
            $scope.display = true;
            $timeout(function () {
                $scope.display = false;
            }, 1000);
        }
    }
    $scope.keydown = function (event) {
        if (event.keyCode == '13') {
            // 回车执行分析
            $scope.print();
        }
    }
    function convert(datas) {
        // var res = [];
        var abbCandidates = [];
        // var radio = [];
        for (var i = 0; i < datas.length; i++) {
            abbCandidates.push(datas[i][0].join(' '));
        }
        return abbCandidates;
    }
    $scope.clean = function () {
        $scope.table = '';
        $scope.showClean = false;
        text = [];
    };
    function cleanTable() {
        $scope.table = '';
        $scope.showClean = false;
        $scope.text = '';
    };
    $scope.submit = function () {
        var abbr_name = $scope.text;
        var tr = angular.element('.lie');
        var full_name = tr.find('input[type=radio]:checked').val();
        // 调用feed_back
        if (full_name) {
            $scope.part = 'loading';
            $scope.display = true;
            $timeout(function () {
                $scope.display = false;
            }, 1000);
            $http({
                method: 'POST',
                url: '/pyserver/feedback/',
                data: { 'abbr_name': abbr_name, 'full_name': full_name },
                headers: { 'Content-Type': 'application/json' }
            }).success(function (data) {
                $scope.part = 'success';
                $timeout(function () {
                    $scope.display = false;
                }, 1000);
            }).error(function () {
                $scope.part = 'error';
                $timeout(function () {
                    $scope.display = false;
                }, 1000);
            });
        } else {
            $scope.part = 'error2';
            $scope.errortip = 'Please select at least one option';
            $scope.display = true;
            $timeout(function () {
                $scope.display = false;
            }, 1000);
            return;
        }
    };

    //选择输入框之外的radio,清空输入框的内容
    $scope.change = function ($event) {
        angular.element($event.target).parents('td').find('.tt').attr('ng-model', '');
        angular.element($event.target).parents('td').find('.tt').text('');
        angular.element($event.target).parents('td').find('.tt').val('');
    };

    function defaultTreeSet() {
        // 从menu菜单进入
        var treeData = $scope.dataForTheTree;
        $scope.expandedNodes = [];
        while (treeData.children.length > 0) {
            treeData = treeData.children[0];
            $scope.expandedNodes.push(treeData);
        }
        $scope.expandedNodes.pop();
        $scope.selected = treeData;
        var fileName = $scope.selected.fileType.toLocaleLowerCase();
        var selectedFilePath = fileName + '/' + $scope.selected.name;
        $scope.selectedProgram = $scope.selected.name;
        getSourceCode(selectedFilePath);
    }
});