'use strict';

angular.module('tagModule').directive('tagDirective', function ($timeout) {
    return {
        restrict: 'EA',
        scope: false,
        templateUrl: 'tag-module/tag.html',
        replace: false,
        controller: tagController,
        link: function (scope, element) {
            $timeout(function () {
                scope.getAllSelectedTags();
            }, 1000)

            element.on('initTag', function () {
                $timeout(function () {
                    angular.element('#tagSelect').triggerHandler('change');
                });
            })
        }
    };
    function tagController($scope, $http, infoDataService, $timeout) {
        // $scope.modelName = [];
        $scope.tagModelShow = false;
        isDisabled();
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
                            getAllTags();
                        }
                    }
                });
            }
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
                uiSelectOption($scope.allTags);
            }).error(function (data) {
                uiSelectOption([]);
                console.info('getAllTags error');
            });
        }

        $scope.getAllSelectedTags = function () {
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
                    }

                    if ($scope.showTag === true) {
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
                            $scope.defaultTags = data.data;
                            $scope.modelName = data.data;
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
        // 选中auto tag后，点击Confirm按钮后，将auto tags添加至allTags中
        $scope.confirmAutoTags = function () {
            // 合并
            $scope.modelName = $scope.modelName.concat($scope.autoTagModel);
            $scope.allTags = $scope.allTags.concat($scope.autoTagModel);
            // 去重
            $scope.modelName = Array.from(new Set($scope.modelName));
            $scope.allTags = Array.from(new Set($scope.allTags));
            uiSelectOption($scope.allTags);
            angular.element('tag-directive').triggerHandler('initTag');
        }

        $scope.saveTags = function () {
            $scope.ctrlFlowShow = false;
            $scope.tagModelShow = true;
            var typeInfo = '';
            if (infoDataService.getSelectedNames().length > 0) {
                // if ($scope.modelName.length > 0) {
                $scope.onModel.modelLoading('loading', 'loading');
                var addTags = [], deleteTags = [];
                angular.forEach($scope.modelName, function (item) {
                    if ($scope.defaultTags.indexOf(item) < 0) {
                        addTags.push(item);
                    }
                })
                angular.forEach($scope.defaultTags, function (item) {
                    if ($scope.modelName.indexOf(item) < 0) {
                        deleteTags.push(item);
                    }
                })

                if (addTags.length === 0 && deleteTags.length === 0) {
                    $scope.onModel.modelShow('success', 'success');
                } else {
                    var tagInfo = {};
                    tagInfo.projectId = infoDataService.getId();
                    tagInfo.selectedNames = infoDataService.getSelectedNames();
                    tagInfo.addTags = addTags;
                    tagInfo.deleteTags = deleteTags;
                    tagInfo.type = infoDataService.getTagType();
                    tagInfo.fromPage = infoDataService.getFromPage();

                    $http({
                        method: 'POST',
                        url: './search/saveTags',
                        data: tagInfo
                    }).success(function (data) {
                        angular.forEach(addTags, function (item) {
                            if ($scope.allTags.indexOf(item) < 0) {
                                $scope.allTags.push(item);
                            }
                            if ($scope.defaultTags.indexOf(item) < 0) {
                                $scope.defaultTags.push(item);
                            }
                        })
                        angular.forEach(deleteTags, function (item) {
                            if ($scope.defaultTags.indexOf(item) > -1) {
                                $scope.defaultTags.splice($scope.defaultTags.indexOf(item), 1);
                            }
                        })
                        uiSelectOption($scope.allTags);
                        // auto tag feedback
                        $scope.autoTagFeedback($scope.modelName);
                        // $state.reload();
                        $scope.onModel.modelShow('success', 'success');
                        // var param={'projectId': infoDataService.getId()};
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
                        // infoDataService.setFromPage('');
                    }).error(function (data) {
                        $scope.onModel.modelShow('error', 'Save tags error.');
                    });
                }
            } else {
                $scope.onModel.modelShow('error', 'Should select one item.');
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

        function uiSelectOption(data) {
            $('.form-control').select2({
                data: data,
                tags: true,
                tokenSeparators: [' ']
            });
        }
    }
})