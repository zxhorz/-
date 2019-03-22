'use strict';

angular.module('codeSearchModule')
    .controller('codeSearchController', function ($scope, $http, $timeout, infoDataService, historyUrlService) {
        var codeSearch = historyUrlService.getDetailRecord();
        infoDataService.setPage('codeSearch')
        $scope.code = '';
        $scope.modelName = [];
        $scope.selectedNames = [];
        $scope.keywordSourceCode = '';
        $scope.selectedTags = [];
        $scope.lastKeywords = '';
        $scope.lastTags = [];
        $scope.lastCode = '';
        $scope.lastKeyResult = [];
        $scope.lastCodeResult = [];
        $scope.lastTagResult = [];
        $scope.tabName = [{ 'list': 'Search Keywords' }, { 'list': 'Search Tags' }, { 'list': 'Search Sim Code' }];
        $scope.serverIp = infoDataService.getServerIp();
        getAllTags();

        if (historyUrlService.getClickFlag()) {
            $scope.kerWords = '';
            $scope.selected = 0;
            historyUrlService.setUrlInfo('codeSearch/' + 0);
            historyUrlService.setClickFlag(true);
            codeSearch.kerWords = {};
            codeSearch.code = {};
            codeSearch.selectedTags = {};
            $scope.hasRecords = false;
        }
        if (infoDataService.getPage() === 'codeSearch') {
            var WatchEvent = $scope.$watch('hisRecode', function (newValue) {
                if (newValue !== undefined && historyUrlService.getClickFlag() === false) {
                    tabShowItem(parseInt(historyUrlService.getUrlParams()));
                }
            });
        } else {
            WatchEvent();
        }

        $scope.tabShow = function (index) {
            tabShowItem(index);
            historyUrlService.setUrlInfo('codeSearch/' + index);
        };

        function tabShowItem(index) {
            if ($scope.selected !== index) {
                getAllTags();
                $scope.keywordSourceCode = '';
                $scope.kerWordsSearch = [];
                $scope.SelectAll = false;
                // $scope.hasRecords = false;
                $scope.currentTab = index;
                $scope.selected = index;
                initSearchResult();
            }
        };

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
        function getAllTags() {
            $http({
                method: 'GET',
                url: './search/getAllTags',
                params: {
                    'projectId': infoDataService.getId()
                }
            }).success(function (data) {
                $scope.allTags = data.data;
                $('#tagManage').select2({
                    data: $scope.allTags,
                    tags: true
                });
                $('#tagSearch').select2({
                    data: $scope.allTags,
                    tags: true
                });
            }).error(function (data) {
                $('#tagManage').select2({
                    data: [],
                    tags: true
                });
                $('#tagSearch').select2({
                    data: [],
                    tags: true
                });
                console.info('getAllTags error');
            });
        }

        $scope.addTags = function () {
            $scope.onModel.modelLoading('loading', 'loading');
            $scope.selectedNames = collectionTags();
            if ($scope.selectedNames.length > 0) {
                if ($scope.modelName.length > 0) {
                    var tagInfo = {};
                    tagInfo.projectId = infoDataService.getId();
                    tagInfo.selectedNames = $scope.selectedNames;
                    tagInfo.addTags = $scope.modelName;
                    $http({
                        method: 'POST',
                        url: './search/addTags',
                        data: tagInfo
                    }).success(function (data) {
                        $scope.onModel.modelHide();
                        getAllTags();
                        initSearchResult();
                    }).error(function (data) {
                        $scope.onModel.modelShow('error', 'error to add tags');
                    });
                } else {
                    $scope.onModel.modelShow('error', 'Tag should not be empty');
                }
            } else {
                $scope.onModel.modelShow('error', 'Should select at least one item');
            }
        }

        $scope.removeTags = function () {
            $scope.onModel.modelLoading('loading', 'loading');
            $scope.selectedNames = collectionTags();
            if ($scope.selectedNames.length > 0) {
                if ($scope.modelName.length > 0) {
                    var tagInfo = {};
                    tagInfo.projectId = infoDataService.getId();
                    tagInfo.selectedNames = $scope.selectedNames;
                    tagInfo.deleteTags = $scope.modelName;
                    $http({
                        method: 'POST',
                        url: './search/removeTags',
                        data: tagInfo
                    }).success(function (data) {
                        $scope.onModel.modelHide();
                        getAllTags();
                        initSearchResult();
                    }).error(function (data) {
                        $scope.onModel.modelShow('error', 'error to remove tags');
                    });
                } else {
                    $scope.onModel.modelShow('error', 'Tag should not be empty');
                }
            } else {
                $scope.onModel.modelShow('error', 'Should select at least one item');
            }
        }

        $scope.checkVersion = function (query, mode, fuzzy, t) {
            $http({
                method: 'GET',
                url: './project/codeversion',
                params: {
                    'projectId': infoDataService.getId()
                }
            }).success(function (data) {
                if (data.data) {
                    doSearch(query, mode, infoDataService.getId(), data.data, fuzzy, t);
                } else {
                    $scope.onModel.modelShow('error', 'No Result');
                }
            }).error(function (data) {
                $scope.onModel.modelShow('error', data.message);
            })
        }

        function doSearch(query, mode, projectId, preparedInfo, fuzzy, t) {
            var organization = 'test', businessDomain = 'test', system = 'test';
            $http({
                method: 'POST',
                url: 'http://' + $scope.serverIp + ':5000/codesearch/',
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
                        $scope.kerWordsSearch = [];
                        $scope.kerWordsSearch = data.data;
                        if (!historyUrlService.getClickFlag()) {
                            if (mode === 'keyword' && historyUrlService.getDetailRecord().kerWords.index > -1) {
                                var index = historyUrlService.getDetailRecord().kerWords.index;
                                var resultInfo = historyUrlService.getDetailRecord().kerWords.resultInfo;
                                getSourceCode(index, resultInfo);
                            } else if (mode === 'text' && historyUrlService.getDetailRecord().code.index > -1) {
                                var index = historyUrlService.getDetailRecord().code.index;
                                var resultInfo = historyUrlService.getDetailRecord().code.resultInfo;
                                getSourceCode(index, resultInfo);
                            }
                            historyUrlService.setClickFlag(true);
                        }
                        if (t) {
                            $scope.keywordSourceCode = '';
                        }
                        $scope.onModel.modelHide();
                        $scope.onModel.modelShow('success', 'success');
                    } else {
                        $scope.hasRecords = false;
                        $scope.kerWordsSearch = [];
                        if (t) {
                            $scope.keywordSourceCode = '';
                        }
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
        //TODO
        //使用python server
        $scope.kerWordSearch = function (t) {
            if (!isNull($scope.kerWords)) {
                $scope.onModel.modelLoading('loading', 'loading');
                $scope.lastKeywords = $scope.kerWords;
                $scope.checkVersion($scope.kerWords, 'keyword', 'Y', t);
                codeSearch.kerWords.query = $scope.kerWords;
                historyUrlService.setCodeSearch(codeSearch);
            } else {
                if (t) {
                    $scope.onModel.modelShow('error', 'Search condition should not be empty.');
                }
            }
        };

        //TODO
        //使用python server
        $scope.codeSearch = function (t) {
            if (!isNull($scope.code)) {
                $scope.onModel.modelLoading('loading', 'loading');
                $scope.SelectAll = false;
                $scope.lastCode = $scope.code;
                $scope.checkVersion($scope.code, 'text', 'N', t);
                codeSearch.code.query = $scope.code;
                historyUrlService.setCodeSearch(codeSearch);
            } else {
                if (t) {
                    $scope.onModel.modelShow('error', 'Search condition should not be empty');
                }
            }
        };

        $scope.setFocus = function (e) {             //回车搜索
            var keycode = window.event ? e.keyCode : e.which;
            if (keycode == 13 && $scope.lastKeywords !== $scope.kerWords) {
                $scope.kerWordSearch();
            }
        }

        $scope.openSource = function (index, t) {
            if ($scope.selected == 0) {
                codeSearch.kerWords.index = index;
                codeSearch.kerWords.resultInfo = t;
            } else if ($scope.selected == 1) {
                codeSearch.selectedTags.index = index;
                codeSearch.selectedTags.resultInfo = t;
            } else if ($scope.selected == 2) {
                codeSearch.code.index = index;
                codeSearch.code.resultInfo = t;
            }
            historyUrlService.setCodeSearch(codeSearch);
            getSourceCode(index, t);
        }

        function getSourceCode(index, t) {
            angular.element('.codeinfo').scrollTop(0);
            $timeout(function(){
                angular.element('.searchTableBox').eq(index).find('.searchTableItem').addClass('trColor');
                angular.element('.searchTableBox').eq(index).siblings().find('.searchTableItem').removeClass('trColor');
            })
            $scope.onModel.modelLoading('loading', 'loading');
            var searchurl = '', sourceName = '', searchInfo = {};
            searchInfo.projectId = infoDataService.getId();
            if (t.type == 1) {
                // 通过programName来得到program代码
                searchurl = './search/getProgramSourceCode';
                searchInfo.sourceName = t.program;
            } else if (t.type == 2) {
                // 通过paragraphId来得到paragraph代码
                searchurl = './search/getParagraphSourceCode';
                searchInfo.sourceName = t.paragraphId;
            }
            $http({
                method: 'POST',
                url: searchurl,
                data: searchInfo
            }).success(
                function (data) {
                    $scope.keywordSourceCode = '';
                    $scope.keywordSourceCode = data.data;
                    $scope.onModel.modelHide();
                }).error(
                function (data) {
                    $scope.onModel.modelShow('error', data.message);
                });
        }
        $scope.tagsSearch = function (t) {
            // if (t) {
            $scope.onModel.modelLoading('loading', 'loading');
            $scope.SelectAll = false;
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
                    if (!historyUrlService.getClickFlag() && historyUrlService.getDetailRecord().selectedTags.index > -1) {
                        var index = historyUrlService.getDetailRecord().selectedTags.index;
                        var resultInfo = historyUrlService.getDetailRecord().selectedTags.resultInfo;
                        getSourceCode(index, resultInfo);
                        historyUrlService.setClickFlag(true);
                    } else {
                        codeSearch.selectedTags.query = $scope.selectedTags;
                        historyUrlService.setCodeSearch(codeSearch);
                    }
                    if (t) {
                        $scope.keywordSourceCode = '';
                    }
                    $scope.onModel.modelHide();
                    $scope.onModel.modelShow('success', 'success');
                } else {
                    $scope.hasRecords = false;
                    $scope.kerWordsSearch = [];
                    if (t) {
                        $scope.keywordSourceCode = '';
                    }
                    $scope.onModel.modelShow('error', 'No result');
                }
            }).error(function (data) {
                $scope.kerWordsSearch = [];
                $scope.onModel.modelHide();
                $scope.onModel.modelShow('error', 'No result');
            });
            // }
        };

        function collectionTags() {
            var selectedItems = angular.element("input:checkbox[name='keywordSearch']:checked");
            var selectedId = [];
            angular.forEach(selectedItems, function (item) {
                if (item.id !== '') {
                    selectedId.push(item.id);
                }
            });
            $scope.SelectAll = false;
            return selectedId;
        }

        function initSearchResult(t) {
            if ($scope.selected == 0) {
                if (!historyUrlService.getClickFlag() && Object.keys(historyUrlService.getDetailRecord().kerWords).length>0 ){
                    $scope.onModel.modelLoading('loading', 'loading');
                    $scope.kerWords = historyUrlService.getDetailRecord().kerWords.query;
                    $scope.lastKeywords = $scope.kerWords;
                    $scope.checkVersion($scope.kerWords, 'keyword', 'Y', true);
                } else {
                    $scope.kerWords = $scope.lastKeywords;
                    $scope.kerWordSearch();
                }
            }
            if ($scope.selected == 1) {
                if (!historyUrlService.getClickFlag() && historyUrlService.getDetailRecord().selectedTags) {
                    $scope.selectedTags = historyUrlService.getDetailRecord().selectedTags.query;
                    $scope.tagsSearch();
                } else {
                    $scope.selectedTags = $scope.lastTags;
                    $scope.tagsSearch();
                }
            }
            if ($scope.selected == 2) {
                if (!historyUrlService.getClickFlag() && historyUrlService.getDetailRecord().code) {
                    $scope.onModel.modelLoading('loading', 'loading');
                    $scope.code = historyUrlService.getDetailRecord().code.query;
                    $scope.SelectAll = false;
                    $scope.lastCode = $scope.code;
                    $scope.checkVersion($scope.code, 'text', 'N', true);
                } else {
                    $scope.code = $scope.lastCode;
                    $scope.codeSearch();
                }
            }
            $scope.modelName = [];

        }

        function cleanSelected() {
            if ($scope.kerWordsSearch && $scope.kerWordsSearch.length > 0) {
                angular.forEach($scope.kerWordsSearch, function (item) {
                    item.checked = false;
                })
            }
        }
    })