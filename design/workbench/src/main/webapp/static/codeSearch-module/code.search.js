'use strict';

angular.module('codeSearchModule')
    .controller('codeSearchController', function ($scope, $http, $timeout, infoDataService, historyUrlService, $state) {
        var codeSearch = historyUrlService.getCodeSearch();
        infoDataService.setPage('codeSearch');
        infoDataService.setFromPage('codeSearch');
        infoDataService.setTagType('');
        $scope.code = '';
        $scope.modelName = [];
        $scope.selectedNames = [];
        $scope.selectedTags = [];
        $scope.lastKeywords = '';
        $scope.lastTags = [];
        $scope.lastCode = '';
        $scope.tabName = [{ 'list': 'Search Keywords', 'arr': [] }, { 'list': 'Search Tags', 'arr': [] }, { 'list': 'Search Sim Code', 'arr': [] }];
        $scope.serverIp = infoDataService.getServerIp();
        // getAllTags();

        if (historyUrlService.getClickFlag()) {
            $scope.kerWords = '';
            $scope.selected = 0;
            historyUrlService.setUrlInfo('codeSearch/' + 0);
            historyUrlService.setClickFlag(true);
            codeSearch = [
                { 'query': '', 'index': '', 'resultInfo': '', 'selectedIdArr': [] },
                { 'query': '', 'index': '', 'resultInfo': '', 'selectedIdArr': [] },
                { 'query': '', 'index': '', 'resultInfo': '', 'selectedIdArr': [] }
            ]
            $scope.hasRecords = false;
        }
        if (infoDataService.getPage() === 'codeSearch') {
            var WatchEvent = $scope.$watch('hisRecode', function (newValue) {
                if (newValue !== undefined && historyUrlService.getClickFlag() === false) {
                    $scope.kerWordsSearch = [];
                    $scope.selected = parseInt(historyUrlService.getUrlParams());
                    initSearchResult();
                    $scope.modelName = infoDataService.getModelName();
                    uiSelectOption($scope.modelName);
                    $scope.updateDB = false;
                    historyUrlService.setClickFlag(true);
                }
            });
        } else {
            WatchEvent();
        }

        //回退时，从新搜索数据
        function initSearchResult(t) {
            if ($scope.selected == 0 && historyUrlService.getCodeSearch()[0].query) {
                $scope.kerWords = historyUrlService.getCodeSearch()[0].query;
                $scope.lastKeywords = $scope.kerWords;
                $scope.checkVersion($scope.kerWords, 'keyword', 'Y');
            }
            if ($scope.selected == 1 && historyUrlService.getCodeSearch()[1].query) {
                $scope.selectedTags = historyUrlService.getCodeSearch()[1].query;
                $timeout(function () {
                    angular.element('#tagSearch').triggerHandler('change');
                });
                $scope.tagsSearchRes();
            }
            if ($scope.selected == 2 && historyUrlService.getCodeSearch()[2].query) {
                $scope.code = historyUrlService.getCodeSearch()[2].query;
                $scope.lastCode = $scope.code;
                $scope.checkVersion($scope.code, 'text', 'N');
            }
        }

        $scope.tabShow = function (index) {
            if ($scope.selected !== index) {
                $scope.kerWordsSearch = [];
                $scope.selected = index;
                $scope.initSearch();
                clearSelectedData();
                $scope.hasRecords = false;
            }
            historyUrlService.setUrlInfo('codeSearch/' + index);
        };

        $scope.initSearch = function () {
            switch ($scope.selected) {
                case 0: if (!isNull($scope.kerWords)) {
                    $scope.checkVersion($scope.kerWords, 'keyword', 'Y', true);
                } break;
                case 1: $scope.tagsSearchRes(); break;
                case 2: if (!isNull($scope.code)) {
                    $scope.checkVersion($scope.code, 'text', 'N', true);
                } break;
            }
        }

        // 判断str是否为undefined和是否为空
        function isNull(str) {
            // undefined
            if (typeof (str) === 'undefined') {
                return true;
            }
            // 空字符
            if (str === '') {
                return true;
            }
            // 空字符组成的空字符串
            var regu = '^[ ]+$';
            var re = new RegExp(regu);
            return re.test(str);
        }

        $scope.checkVersion = function (query, mode, fuzzy) {
            $scope.onModel.modelLoading('loading', 'loading');
            $http({
                method: 'GET',
                url: './project/codeversion',
                params: {
                    'projectId': infoDataService.getId()
                }
            }).success(function (data) {
                if (data.data) {
                    doSearch(query, mode, infoDataService.getId(), data.data, fuzzy);
                } else {
                    $scope.onModel.modelShow('error', 'No Result');
                }
            }).error(function (data) {
                console.info(data.message);
                $scope.onModel.modelShow('error', data.message);
            })
        }

        function doSearch(query, mode, projectId, preparedInfo, fuzzy) {
            var organization = 'test', businessDomain = 'test', system = 'test';
            $http({
                method: 'POST',
                url: '/pyserver/codesearch/',
                //mode统一使用keyword，输入多个查询词时，“keyword”表示将查询词分割为多个词语，对每个词语分别计算分数并相加，“text”表示把多个查询词看作一个整体计算分数，
                //如有文档“He is passionate”，“keyword”模式下搜索“is he”，“he is”,“he passionate”返回结果里都会有该文档，“text”模式下只有搜索
                // “he is”时返回结果里才会有该文档。
                data: {
                    'mode': mode,
                    'query': query,
                    'size': '10',
                    'projectId': projectId,
                    'projectPath': preparedInfo.projectPath,
                    'addFiles': preparedInfo.addFiles,
                    'deleteFiles': preparedInfo.deleteFiles,
                    'outputPath': preparedInfo.outputPath,
                    'flag': preparedInfo.flag,
                    'organization': organization,
                    'businessDomain': businessDomain,
                    'system': system,
                    'type': preparedInfo.type,
                    'number_of_fragments': 5,
                    'fragment_size': 100,
                    'dbUrl': infoDataService.getNeo4jPath(),
                    'fuzzy': fuzzy,
                    'rootPath': preparedInfo.rootPath
                },
                headers: { 'Content-Type': 'application/json' }
            }).success(function () {
                $http({
                    method: 'GET',
                    url: './search/searchresult',
                    params: {
                        'projectId': projectId
                    }
                }).success(function (data) {
                    if (data.data && data.data.length > 0) {
                        $scope.hasRecords = true;
                        $scope.kerWordsSearch = data.data;
                        $scope.onModel.modelHide();
                        $scope.onModel.modelShow('success', 'success');
                    } else {
                        $scope.hasRecords = false;
                        $scope.kerWordsSearch = [];
                        $scope.onModel.modelShow('error', 'No result');
                    }
                }).error(function () {
                    $scope.kerWordsSearch = [];
                    $scope.onModel.modelHide();
                    $scope.onModel.modelShow('error', 'No result');
                })
            }).error(function () {
                $scope.kerWordsSearch = [];
                $scope.onModel.modelHide();
                $scope.onModel.modelShow('error', 'No result');
            });
        }

        //使用python server
        $scope.kerWordSearch = function () {
            clearSelectedData();
            if (!isNull($scope.kerWords) && $scope.lastKeywords !== $scope.kerWords) {
                $scope.lastKeywords = $scope.kerWords;
                $scope.checkVersion($scope.kerWords, 'keyword', 'Y');
                codeSearch[0].query = $scope.kerWords;
                historyUrlService.setCodeSearch(codeSearch);
            } else {
                if (isNull($scope.kerWords)) {
                    $scope.onModel.modelShow('error', 'Search condition should not be empty.');
                }
            }
        };

        //回车搜索
        $scope.setFocus = function (e) {
            var keycode = window.event ? e.keyCode : e.which;
            if (keycode == 13 && $scope.lastKeywords !== $scope.kerWords) {
                $scope.kerWordSearch();
            }
        }

        //使用python server
        $scope.codeSearch = function (t) {
            clearSelectedData()
            if (!isNull($scope.code) && $scope.lastCode !== $scope.code) {
                // $scope.SelectAll = false;
                $scope.lastCode = $scope.code;
                $scope.checkVersion($scope.code, 'text', 'N');
                codeSearch[2].query = $scope.code;
                historyUrlService.setCodeSearch(codeSearch);
            } else {
                if (t) {
                    $scope.onModel.modelShow('error', 'Search condition should not be empty');
                }
            }
        };

        $scope.tagsSearch = function () {
            console.info($scope.lastTags);
            console.info($scope.selectedTags);
            console.info($scope.lastTags !== $scope.selectedTags);
            if ($scope.lastTags !== $scope.selectedTags) {
                clearSelectedData();
                $scope.tagsSearchRes();
            }
        };

        //tag搜索
        $scope.tagsSearchRes = function () {
            $scope.onModel.modelLoading('loading', 'loading');
            var tagInfo = {};
            tagInfo.projectId = infoDataService.getId();
            tagInfo.condition = $scope.selectedTags;
            $scope.lastTags = $scope.selectedTags;
            $http({
                method: 'POST',
                url: './search/searchByTags',
                data: tagInfo
            }).success(function (data) {
                if (data.data && data.data.length > 0) {
                    $scope.hasRecords = true;
                    $scope.kerWordsSearch = data.data;
                    if (!historyUrlService.getClickFlag() && historyUrlService.getCodeSearch()[1].index > -1) {
                        historyUrlService.setClickFlag(true);
                    } else {
                        codeSearch[1].query = $scope.selectedTags;
                        historyUrlService.setCodeSearch(codeSearch);
                    }
                    $scope.onModel.modelHide();
                    $scope.onModel.modelShow('success', 'success');
                } else {
                    $scope.hasRecords = false;
                    $scope.kerWordsSearch = [];
                    $scope.onModel.modelShow('error', 'No result');
                }
            }).error(function (data) {
                $scope.kerWordsSearch = [];
                $scope.onModel.modelHide();
                $scope.onModel.modelShow('error', 'No result');
            });
        }

        //搜索时,清空select2的值、清空上次选中的nameId
        function clearSelectedData() {
            $scope.focus = -1;
            $scope.isSelectAll = false;
            codeSearch[$scope.selected].isSelectAll = false;
            //清空上次选中的nameId
            codeSearch[$scope.selected].selectedIdArr = [];
            infoDataService.setSelectedNames(codeSearch[$scope.selected].selectedIdArr);
            //清空select2的值
            $scope.modelName = [];
            $scope.clearTagCount();
            $timeout(function () {
                angular.element('#tagSelect').triggerHandler('change');
            });
            $scope.updateDB = false;
        }

        //选中行，添加背景颜色
        $scope.onClick = function (index) {
            $scope.focus = index;
        }
        //点击name跳转到codebrowser
        $scope.goCodeBrowser = function (location) {
            location = location.substring(0, location.lastIndexOf('/'));
            $state.go('codeBrowser', { projectId: infoDataService.getId(), location: location });
        }

        function uiSelectOption(data) {
            $('.form-control').select2({
                data: data,
                tags: true,
                tokenSeparators: [' ']
            });
            $timeout(function () {
                angular.element('#tagSelect').triggerHandler('change');
            });
        }

        //单选复选框的选中效果
        $scope.isSelected = function (id) {
            return codeSearch[$scope.selected].selectedIdArr.indexOf(id) != -1;
        }

        //数据库更新标志位,默认为true。
        $scope.updateDB = true;
        //单个选择复选框
        $scope.selectOne = function (nameId, taglist) {
            if ($(event.target).is(':checked')) {
                $scope.updateDB = false;
                codeSearch[$scope.selected].selectedIdArr.push(nameId);
                $scope.updateTagCount(taglist, "ADD");
            } else {
                var pos = $.inArray(nameId, codeSearch[$scope.selected].selectedIdArr);
                if (pos > -1) {
                    codeSearch[$scope.selected].selectedIdArr.splice(pos, 1);
                    $scope.updateDB = false;
                    $scope.updateTagCount(taglist, "REMOVE");
                }
            }
            $scope.modelNameList();
            uiSelectOption($scope.modelName);
            var isSelectAll = codeSearch[$scope.selected].selectedIdArr.length === $scope.kerWordsSearch.length
            getTagCondition(isSelectAll);
        }

        function delSelectedId(nameId, taglist) {

        }

        //全选时所有的tag
        var allTagList = [];
        //点击全选按钮
        $scope.selectAll = function () {
            $scope.updateDB = false;
            if ($scope.isSelectAll) {
                $scope.clearTagCount();
                codeSearch[$scope.selected].selectedIdArr = [];
                angular.forEach($scope.kerWordsSearch, function (item) {

                    //获取programId或者paragraphId
                    var itemId = item.programId ? item.programId : item.paragraphId;
                    codeSearch[$scope.selected].selectedIdArr.push(itemId);
                    //获取tag
                    var itemTag = item.programTags ? item.programTags : item.paragraphTags;
                    $scope.updateTagCount(item.programTags, "ADD");
                });
                infoDataService.setSelectedNames(codeSearch[$scope.selected].selectedIdArr);
                $scope.modelNameList();
                getTagCondition(true);
            } else {
                codeSearch[$scope.selected].selectedIdArr = [];
                $scope.clearTagCount();
                $scope.modelName = [];
                getTagCondition(false);
            }
            uiSelectOption($scope.modelName);
        }

        //用来储存：复选框选中状态，select2文本框的tag，被选中的nameId（programId、paragraphId）
        function getTagCondition(isChecked) {
            codeSearch[$scope.selected].isSelectAll = isChecked;
            //存储选中的nameId
            infoDataService.setSelectedNames(codeSearch[$scope.selected].selectedIdArr);
            historyUrlService.setCodeSearch(codeSearch);
        }
        //全选复选框的状态true 或false
        $scope.isSelcAll = function () {
            if (codeSearch[$scope.selected].isSelectAll) {
                return codeSearch[$scope.selected].isSelectAll;
            } else {
                return false;
            }
        }

        //search页面的tag搜索框
        $scope.tagSearchChange = function (selectedTags) {
            //新增的tag不在$scope.allTags里面
            var addList = [];
            //新增的tag在$scope.allTags里面
            var hasAlltags = [];
            angular.forEach(selectedTags, function (item) {
                var pos = $.inArray(item, $scope.allTags);
                if (pos < 0) {
                    addList.push(item);
                } else {
                    hasAlltags.push(item);
                }
            })
            if (addList.length > 0) {
                $scope.selectedTags = hasAlltags;
                $scope.onModel.modelShow('error', addList + ' does not exist, please replace tag and search again', 3000);
            }
        }
    });