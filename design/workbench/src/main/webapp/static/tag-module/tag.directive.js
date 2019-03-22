'use strict';

angular.module('tagModule').directive('tagDirective', function ($timeout,infoDataService) {
    return {
        restrict: 'EA',
        scope: false,
        templateUrl: 'tag-module/tag.html',
        replace: false,
        controller: tagController,
        link: function (scope, element) {
            if (infoDataService.getFromPage() !== 'codebrowser'){
                $timeout(function () {
                    scope.getAllSelectedTags();
                }, 1000);
            }

            element.on('initTag', function () {
                $timeout(function () {
                    angular.element('#tagSelect').triggerHandler('change');
                });
            })
        }
    };
    function tagController($scope, $http, infoDataService, $timeout) {
        var isAutoTag = false;
        $scope.tagModelShow = false;
        isDisabled();
        //检测代码是否进行了so分析
        function isDisabled() {
            if (infoDataService.getName() === '') {
            } else {
                $http({
                    method: 'GET',
                    url: './job/specjobstatus',
                    params: {
                        'projectId': infoDataService.getId(),
                        'jobName': 'SO'
                    }
                }).success(function (data) {
                    if (data.code === 'ACK') {
                        if (data.data === 'S') {
                            $scope.status = data.data;
                            getAllTags();
                        }
                    }
                });
            }
        }
        // 获取数据库中已经存在的tag
        function getAllTags() {
            $http({
                method: 'GET',
                url: './search/getAllTags',
                params: {
                    'projectId': infoDataService.getId()
                }
            }).success(function (data) {
                $scope.allTags = data.data;
                uiSelectOption($scope.allTags);
            }).error(function (data) {
                uiSelectOption([]);
                console.info('getAllTags error');
            });
        }

        // 获取当前程序的tag
        $scope.getAllSelectedTags = function () {
            if (infoDataService.getFromPage() !== 'codeSearch') {
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
                        } else {
                            $scope.showTag = true;
                            $http({
                                method: 'GET',
                                url: './search/getAllSelectedTags',
                                params: {
                                    'projectId': infoDataService.getId(),
                                    'selectedNames': infoDataService.getSelectedNames(),
                                    'type': infoDataService.getTagType(),
                                    'fromPage': infoDataService.getFromPage()
                                }
                            }).success(function (data) {
                                if (isAutoTag) {
                                    $('.form-control').empty();
                                    uiSelectOption($scope.allTags);
                                    isAutoTag = false;
                                }
                                $scope.defaultTags = data.data.confirmedTags;
                                // select框的值
                                $scope.modelName = data.data.confirmedTags;
                                $scope.deniedTags = data.data.deniedTags;
                                $scope.updateDB = false;
                                angular.element('tag-directive').triggerHandler('initTag');
                            }).error(function (data) {
                                console.info('getAllSelectedTags error');
                            });
                        }
                    } else {
                        console.info('error');
                    }
                }).error(function (data) {
                    console.info('error');
                });
            }
        }

        // 点击推荐的tag，将值添加到select框
        $scope.clickAutoTag = function (autoTag) {
            isAutoTag = true;
            // 合并
            $scope.modelName.push(autoTag);
            $scope.allTags.push(autoTag);

            // 去重
            $scope.modelName = Array.from(new Set($scope.modelName));
            $scope.allTags = Array.from(new Set($scope.allTags));
            uiSelectOption($scope.allTags);
            angular.element('tag-directive').triggerHandler('initTag');

            var index = $.inArray(autoTag, $scope.autoTagModel);
            $scope.autoTagModel.splice(index, 1);
            // $scope.addTag(autoTag);
        }

        // 添加tag
        $scope.addTag = function (autoTag) {
            $scope.ctrlFlowShow = false;
            $scope.tagModelShow = true;
            var typeInfo = '';
            if (infoDataService.getSelectedNames().length > 0) {
                $scope.onModel.modelLoading('loading', 'loading');
                var addTags = [];
                addTags = addTags.concat(autoTag);
                if (addTags.length === 0) {
                    $scope.onModel.modelShow('success', 'success');
                } else {
                    var tagInfo = {};
                    tagInfo.projectId = infoDataService.getId();
                    tagInfo.selectedNames = infoDataService.getSelectedNames();
                    tagInfo.addTags = addTags;
                    tagInfo.deleteTags = [];
                    tagInfo.type = infoDataService.getTagType();
                    tagInfo.fromPage = infoDataService.getFromPage();
                    tagInfo.action = "ADD";
                    $http({
                        method: 'POST',
                        url: './search/saveTag',
                        data: tagInfo
                    }).success(function (data) {
                        getNewData(tagInfo, addTags, 'ADD');
                    }).error(function (data) {
                        $scope.onModel.modelShow('error', 'Save tags error.');
                    });
                }
            } else {
                $scope.onModel.modelShow('error', 'Should select one item.');
            }
        }

        // 删除tag
        $scope.removeTag = function (autoTag) {
            $scope.ctrlFlowShow = false;
            $scope.tagModelShow = true;
            var typeInfo = '';
            if (infoDataService.getSelectedNames().length > 0) {
                $scope.onModel.modelLoading('loading', 'loading');
                var deleteTags = [];
                // deleteTags.push(autoTag);
                deleteTags = deleteTags.concat(autoTag);
                if (deleteTags.length === 0) {
                    $scope.onModel.modelShow('success', 'success');
                } else {
                    var tagInfo = {};
                    tagInfo.projectId = infoDataService.getId();
                    tagInfo.selectedNames = infoDataService.getSelectedNames();
                    tagInfo.addTags = [];
                    tagInfo.deleteTags = deleteTags;
                    tagInfo.type = infoDataService.getTagType();
                    tagInfo.fromPage = infoDataService.getFromPage();
                    tagInfo.action = "REMOVE";

                    $http({
                        method: 'POST',
                        url: './search/saveTag',
                        data: tagInfo
                    }).success(function (data) {
                        getNewData(tagInfo, deleteTags, 'REMOVE');
                        // getGridData(tagInfo);
                    }).error(function (data) {
                        $scope.onModel.modelShow('error', 'Save tags error.');
                    });
                }
            } else {
                $scope.onModel.modelShow('error', 'Should select one item.');
            }
        }

        //页面重新获取数据
        function getNewData(tagInfo, tag, option) {
            if (infoDataService.getFromPage() === 'codeSearch') {
                $scope.initSearch();
                getAllTags();
                // uiSelectOption(tag);
            } else {
                if (option === 'ADD') {
                    angular.forEach(tag, function (item) {
                        if ($scope.allTags.indexOf(item) < 0) {
                            $scope.allTags.push(item);
                        }
                        if ($scope.defaultTags.indexOf(item) < 0) {
                            $scope.defaultTags.push(item);
                        }
                    })
                    uiSelectOption($scope.allTags);
                } else if (option === 'REMOVE') {
                    $scope.deniedTags.push(tag);
                    angular.forEach(tag, function (item) {
                        if ($scope.defaultTags.indexOf(item) > -1) {
                            $scope.defaultTags.splice($scope.defaultTags.indexOf(item), 1);
                        }
                    })
                }
                getGridData(tagInfo);
            }
        }

        // 获取system documentation 表格数据
        function getGridData(tagInfo) {
            $scope.onModel.modelShow('success', 'success');
            if (tagInfo.type === 'table') {
                var param = {
                    'projectId': infoDataService.getId(),
                    'page': $scope.gridOptions1.paginationCurrentPage,
                    'size': $scope.gridOptions1.paginationPageSize,
                    'query': $scope.query
                }
                getGridOptionsData('./summary/table', param);
            } else if (tagInfo.type === 'file') {
                var param = {
                    'projectId': infoDataService.getId(),
                    'page': $scope.gridOptions1.paginationCurrentPage,
                    'size': $scope.gridOptions1.paginationPageSize,
                    'query': $scope.query
                }
                getGridOptionsData('./summary/file', param);
            } else if (tagInfo.type === 'copybook' && tagInfo.fromPage !== 'codebrowser') {
                var param = {
                    'projectId': infoDataService.getId(),
                    'page': $scope.gridOptions1.paginationCurrentPage,
                    'size': $scope.gridOptions1.paginationPageSize,
                    'query': $scope.query
                }
                getGridOptionsData('./summary/copybookDetail', param);
            } else if ((tagInfo.type === 'JOB' || tagInfo.type === 'PROC') && tagInfo.fromPage !== 'codebrowser') {
                var param = {
                    'projectId': infoDataService.getId(),
                    'page': $scope.gridOptions1.paginationCurrentPage,
                    'size': $scope.gridOptions1.paginationPageSize,
                    'query': $scope.query
                }
                getGridOptionsData('./summary/jclDetail', param);
            }
        }


        function getGridOptionsData(urlInfo, param, ) {
            $http({
                method: 'GET',
                url: urlInfo,
                params: param
            }).success(
                function (data) {
                    if (data && data.data
                        && data.data.content.length > 0) {
                        $scope.gridOptions1.data = data.data.content;
                        $scope.gridOptions1.totalItems = data.data.totalElements;
                        $timeout(function () {
                            if ($scope.gridApi.selection.selectRow) {
                                $scope.gridApi.selection.selectRow($scope.gridOptions1.data[infoDataService.getRowIndex()]);       //默认选中某一行
                            }
                        });
                    } else {
                        $scope.gridOptions1.data = [{ 'name': 'No data', 'tags': '' }];
                    }
                }).error(function (data) {
                    $scope.gridOptions1 = [];
                    console.info('error');
                });
        }

        // 当select框发生变化时执行的函数
        $scope.tagChange = function (modelName) {
            // 在Code Search页面
            if (infoDataService.getFromPage() === 'codeSearch') {
                if ($scope.updateDB) {
                    var addList = tagListAddCheck();
                    $scope.updateTagCount(addList, "ADD");
                    var removeList = tagListRemoveCheck();
                    $scope.updateTagCount(removeList, "REMOVE");
                    //infoDataService.getSelectedNames().length：被选中的nameId的个数
                    if (infoDataService.getSelectedNames().length > 0) {
                        if (addList.length > 0) {
                            $scope.addTag(addList);
                        }

                        if (removeList.length > 0) {
                            $scope.removeTag(removeList);
                        }
                    } else {
                        $scope.modelName = [];
                        $scope.onModel.modelShow('error', 'Should select one item.');
                    }
                } else {
                    $scope.updateDB = true;
                }
                infoDataService.setModelName($scope.modelName);
            // Code Browser、System Document页面
            } else {
                if ($scope.updateDB) {
                    var addList = [];
                    angular.forEach(modelName, function (item) {
                        if ($scope.defaultTags.indexOf(item) < 0) {
                            addList.push(item);
                            $scope.addTag(addList);
                        }
                    })
                    var removeList = [];
                    angular.forEach($scope.defaultTags, function (item) {
                        if (modelName.indexOf(item) < 0) {
                            removeList.push(item);
                            $scope.removeTag(removeList);
                            $(".form-control").select2("close");
                        }
                    })
                } else {
                    $scope.updateDB = true;
                }
            }
        }

        //codeSearch
        //更新tag count的内容
        var tagCount = [];
        $scope.updateTagCount = function (taglist, action) {
            if (action === "ADD") {
                angular.forEach(taglist, function (item) {
                    if (item in tagCount) {
                        //点击select2的文本框添加tag或者删除tag时scope.updateDB=true
                        //点击或取消复选框时scope.updateDB=false；
                        tagCount[item] = $scope.updateDB ? infoDataService.getSelectedNames().length : tagCount[item] + 1;
                    } else {
                        tagCount[item] = $scope.updateDB ? infoDataService.getSelectedNames().length : 1;
                    }
                })
            } else if (action === "REMOVE") {
                angular.forEach(taglist, function (item) {
                    tagCount[item] = $scope.updateDB ? 0 : tagCount[item] - 1;
                })
            }
        }

        //复选框选中时，所有tag的交集
        $scope.modelNameList = function () {
            var modelNameList = [];
            for (var key in tagCount) {
                if (tagCount[key] === infoDataService.getSelectedNames().length) {
                    modelNameList.push(key);
                }
            }
            if (infoDataService.getSelectedNames().length === 0) {
                $scope.modelName = [];
            } else {
                $scope.modelName = modelNameList;
            }
        }

        //清空tagCount
        $scope.clearTagCount = function () {
            tagCount = [];
        }

        //非check box操作下，modelname change时，taglist改动情况的分析，返回变化的taglist以及变化类型（增加）
        //新增且要添加到数据库的tag
        function tagListAddCheck() {
            var addList = [];
            angular.forEach($scope.modelName, function (item) {
                if (item in tagCount && tagCount[item] === infoDataService.getSelectedNames().length) {

                } else {
                    addList.push(item);
                }
            })
            return addList;
        }


        //非check box操作下，modelname change时，taglist改动情况的分析，返回变化的taglist以及变化类型（删除）
        function tagListRemoveCheck() {
            var removeList = [];
            for (var key in tagCount) {
                if (tagCount[key] === infoDataService.getSelectedNames().length && $scope.modelName.indexOf(key) < 0) {
                    removeList.push(key);
                }
            }
            return removeList;
        }

        // 用来展示下拉框的数据
        function uiSelectOption(data) {
            $('.form-control').select2({
                data: data,
                tags: true,
                tokenSeparators: [' ']
            });
        }
    }
})