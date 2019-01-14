'use strict';

angular.module('codeSearchModule')
    .controller('codeSearchController', function ($scope, $http, $timeout, infoDataService,historyUrlService) {
        $scope.kerWords = '';
        $scope.code = '';
        $scope.modelName = [];
        $scope.selectedNames = [];
        $scope.keywordSourceCode = '';
        $scope.selectedTags = [];
        $scope.hasRecords = false;
        $scope.lastKeywords = '';
        $scope.lastTags = [];
        $scope.lastCode = '';
        $scope.lastKeyResult = [];
        $scope.lastCodeResult = [];
        $scope.lastTagResult = [];
        $scope.tabName = [{ 'list': 'Search Keywords' }, { 'list': 'Search Tags' }, { 'list': 'Search Sim Code' }];
        getAllTags();

        $scope.tabShow = function (index) {
            if ($scope.selected !== index) {
                getAllTags();
                $scope.keywordSourceCode = '';
                $scope.kerWordsSearch = [];
                $scope.SelectAll = false;
                $scope.hasRecords = false;
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
        $scope.kerWordSearch = function (t) {
            if (!isNull($scope.kerWords)) {
                $scope.onModel.modelLoading('loading', 'loading');
                $scope.lastKeywords = $scope.kerWords;
                $http({
                    method: 'POST',
                    url: './search/searchByKeyOrCode',
                    params: {
                        'projectId': infoDataService.getId(),
                        'queryCondition': 'key',
                        'condition': $scope.kerWords
                    }
                }).success(function (data) {
                    if (data.data && data.data.length > 0) {
                        $scope.hasRecords = true;
                        $scope.kerWordsSearch = [];
                        $scope.kerWordsSearch = data.data;
                        if (t) {
                            $scope.keywordSourceCode = '';
                        }
                        $scope.onModel.modelHide();
                        $scope.onModel.modelShow('success', 'success');
                        $
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
            } else {
                if (t) {
                    $scope.onModel.modelShow('error', 'Search condition should not be empty.');
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
            angular.element('#searchTable').find('tr').eq(index).addClass('trColor').siblings().removeClass('trColor');
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

        $scope.codeSearch = function (t) {
            if (!isNull($scope.code)) {
                $scope.onModel.modelLoading('loading', 'loading');
                $scope.SelectAll = false;
                $scope.lastCode = $scope.code;
                $http({
                    method: 'POST',
                    url: './search/searchByKeyOrCode',
                    params: {
                        'projectId': infoDataService.getId(),
                        'queryCondition': 'content',
                        'condition': $scope.code
                    }
                }).success(function (data) {
                    if (data.data && data.data.length > 0) {
                        $scope.hasRecords = true;
                        $scope.kerWordsSearch = data.data;
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
            } else {
                if (t) {
                    $scope.onModel.modelShow('error', 'Search condition should not be empty');
                }
            }
        };

        $scope.tagsSearch = function (t) {
            // if (t) {
            $scope.onModel.modelLoading('loading','loading');
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
                $scope.kerWords = $scope.lastKeywords;
                $scope.kerWordSearch();
            }
            if ($scope.selected == 1) {
                $scope.selectedTags = $scope.lastTags;
                $scope.tagsSearch();
            }
            if ($scope.selected == 2) {
                $scope.code = $scope.lastCode;
                $scope.codeSearch();
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