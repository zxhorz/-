'use strict';

angular.module('fileModule').directive('fileDirective', function () {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'file-module/file.html',
        replace: false,
        controller: fileController
    };

    function fileController($scope, $http, $attrs, $compile, $element, infoDataService, $timeout, $state, historyUrlService) {
        // 进入该页面，初始化tagInfo
        $scope.first = true;
        infoDataService.setTagInfo();
        $scope.selectedName = '';
        $scope.readOnly = true;
        $scope.changePage = false;
        infoDataService.setRowIndex(0);
        $scope.query = ''
        var detailRecord = historyUrlService.getDetailRecord();
        // detailRecord.file = {};

        function param(nodeId, page, size) {
            var param = {
                'projectId': infoDataService.getId(),
                'nodeId': nodeId,
                'page': page,
                'size': size
            }
            return param;
        }

        $scope.gridOptions1 = {
            columnDefs: [
                {
                    field: 'name', displayName: 'Name',
                    cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {      //修改列的样式
                        if (grid.getCellValue(row, col) === 'No data') {
                            return 'noData';          //样式的class名
                        } else {
                            return 'dataInfo';       //样式的class名
                        }
                    },
                    cellTemplate: "<div title = {{row.entity.name}}  class=\"ui-grid-cell-contents ng-binding ng-scope\" ><a href='' ng-click='grid.appScope.programClick(row.entity);'>{{row.entity.name}}</a></div>"
                },
                {
                    field: 'openType', displayName: 'IO-Type',
                    cellTemplate: '<div title = {{row.entity.openType}} class="ui-grid-cell-contents ng-binding ng-scope" >{{row.entity.openType}}</div>'
                },
                {
                    field: 'program', displayName: 'Program',
                    cellTemplate: '<div title = {{row.entity.pgmFileName}} class="ui-grid-cell-contents ng-binding ng-scope" ><a href="" ng-click="grid.appScope.programClick(row.entity);" style="text-decoration: underline;color:#000;">{{row.entity.pgmFileName}}</div>'
                },
                {
                    field: 'tags', displayName: 'Tags',
                    cellTemplate: '<div title = {{row.entity.tags}} class="ui-grid-cell-contents ng-binding ng-scope" >{{row.entity.tags}}</div>'
                }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-click='grid.appScope.onDblClick(rowRenderIndex,row.entity);' ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

            enablePagination: true, //是否分页，默认为true
            // enablePaginationControls: true, //使用默认的底部分页
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: 11, //每页显示个数
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {

                    if (file && historyUrlService.getClickFlag()) {
                        $scope.changePage = true;
                        file(newPage, pageSize);
                        infoDataService.setRowIndex(0);
                        detailRecord.file.page = newPage;
                        detailRecord.file.num = 0;
                        historyUrlService.setDetailRecord(detailRecord);
                    }
                });
            }
        };
        $scope.gridOptions1.noUnselect = true;

        $scope.gridOptions2 = {
            columnDefs: [
                {
                    field: 'name', displayName: 'Name',
                    cellTemplate: '<div title = {{row.entity.name}} class="ui-grid-cell-contents ng-binding ng-scope" >{{row.entity.name}}</div>',
                },
                { field: 'type', displayName: 'Type' }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,

            enablePagination: true, //是否分页，默认为true
            // enablePaginationControls: true, //使用默认的底部分页
            paginationPageSizes: [9, 10, 11], //每页显示个数可选项
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: 11, //每页显示个数
            //paginationTemplate:"<div></div>", //自定义底部分页代码
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-click='grid.appScope.selectedRow(rowRenderIndex);' ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",
            onRegisterApi: function (gridApi2) {
                $scope.gridApi2 = gridApi2;
                //分页按钮事件
                gridApi2.pagination.on.paginationChanged($scope, function (newPage, pageSize) {

                    if (fileDetail && historyUrlService.getClickFlag()) {
                        fileDetail(param(infoDataService.getSelectedNames(), newPage, pageSize));
                        detailRecord.file.page2 = newPage;
                        detailRecord.file.num2 = -1;
                        historyUrlService.setDetailRecord(detailRecord);
                    }
                });
            }
        };
        $scope.gridOptions2.noUnselect = true;

        if (!historyUrlService.getClickFlag()) {
            if (Object.keys(historyUrlService.getDetailRecord().file).length > 0 && historyUrlService.getDetailRecord().file.searchInp) {
                $scope.searchInp = historyUrlService.getDetailRecord().file.searchInp
                $scope.query = $scope.searchInp;
            }
            $scope.gridOptions1.paginationCurrentPage = Object.keys(historyUrlService.getDetailRecord().file).length > 0 ? historyUrlService.getDetailRecord().file.page ? historyUrlService.getDetailRecord().file.page : 1 : 1;
        } else {
            detailRecord.file = {};
        }
        function detailHistory() {
            if (Object.keys(historyUrlService.getDetailRecord().file).length > 0 && historyUrlService.getDetailRecord().file.num2 > -1 && !historyUrlService.getClickFlag()) {
                $timeout(function () {
                    if ($scope.gridApi2.selection.selectRow) {
                        $scope.gridApi2.selection.selectRow($scope.gridOptions2.data[historyUrlService.getDetailRecord().file.num2]);      //默认选中某一行
                    }
                    historyUrlService.setClickFlag(true);
                });
            } else {
                historyUrlService.setClickFlag(true);
            }
        }

        $scope.onDblClick = function (index, row) {
            $scope.first = false;
            if (row.name !== 'No data') {
                $scope.selectedName = row.name;
                // var param4 = { 'projectId': infoDataService.getId(), 'nodeId': row.nodeId,'page': $scope.gridOptions2.paginationCurrentPage,'size': $scope.gridOptions2.paginationPageSize };
                fileDetail(param(row.nodeId, $scope.gridOptions2.paginationCurrentPage, $scope.gridOptions2.paginationPageSize));
                var selectedNames = [];
                selectedNames.push(row.nodeId);
                infoDataService.setSelectedNames(selectedNames);
                infoDataService.setRowIndex(index);
                infoDataService.setTagType('file');
                $scope.getAllSelectedTags();
                // detailRecord.file = {};
                detailRecord.file.num = index;
                detailRecord.file.page = $scope.gridOptions1.paginationCurrentPage;
                historyUrlService.setDetailRecord(detailRecord);
            }
        }
        $scope.programClick = function (row) {
            if (row.name !== 'No data') {
                $state.go('codeBrowser', { projectId: infoDataService.getId(), location: row.pgmLocation, definitionStart: row.definitionStart, definitionEnd: row.definitionEnd });
            }
        }

        $scope.selectedRow = function (index) {
            // if (historyUrlService.getDetailRecord().file === undefined) {
            //     detailRecord.file = {};
            // }
            detailRecord.file.num2 = index;
            detailRecord.file.page2 = $scope.gridOptions2.paginationCurrentPage;
            historyUrlService.setDetailRecord(detailRecord);
        }

        function fileDetail(param) {
            $http({
                method: 'GET',
                url: './summary/fileDetail',
                params: param
            }).success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptions2.enablePaginationControls = true;  //使用默认的底部分页
                        $scope.gridOptions2.data = data.data.content;
                        $scope.gridOptions2.totalItems = data.data.totalElements;
                        detailHistory();
                        fileUseCpy(data.data.content);
                    } else {
                        $scope.gridOptions2.enablePaginationControls = false;  //使用默认的底部分页
                        $scope.gridOptions2.data = [{ 'name': 'No data', 'type': '' }];
                        historyUrlService.setClickFlag(true);
                    }
                }).error(function (data) {
                    $scope.gridOptions2 = [];
                    console.info('error');
                });
        }

        function fileUseCpy(data) {
            var cpyNames = new Set();
            data.forEach(function (fileItem, index) {
                cpyNames.add(fileItem.cpyName);
            });
            $scope.fileCpyNames = Array.from(cpyNames);
        }

        $scope.kerWordSearch = function (flag) {
            if (typeof ($scope.searchInp) === 'undefined') {
                $scope.searchInp = '';
            }
            if ($scope.searchInp === '' && $scope.query === '') {
                return
            } else {
                $scope.query = $scope.searchInp;
                $scope.gridOptions1.paginationCurrentPage = 1;
                file($scope.gridOptions1.paginationCurrentPage, $scope.gridOptions1.paginationPageSize);
                detailRecord.file.searchInp = $scope.query;
                detailRecord.file.page = 1;
                historyUrlService.setDetailRecord(detailRecord);
            }
        }

        file($scope.gridOptions1.paginationCurrentPage, $scope.gridOptions1.paginationPageSize);
        function file(page, size) {
            $http({
                method: 'GET',
                url: './summary/file',
                params: {
                    'projectId': infoDataService.getId(),
                    'page': page,
                    'size': size,
                    'query': $scope.query
                }
            }).success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptions1.enablePaginationControls = true;  //使用默认的底部分页
                        $scope.gridOptions1.data = data.data.content;
                        $scope.gridOptions1.totalItems = data.data.totalElements;
                        var index;
                        if (Object.keys(historyUrlService.getDetailRecord().file).length > 0 && !historyUrlService.getClickFlag()) {
                            index = historyUrlService.getDetailRecord().file.num >= 0 ? historyUrlService.getDetailRecord().file.num : 0;
                            $scope.gridOptions2.paginationCurrentPage = historyUrlService.getDetailRecord().file.page2 ? historyUrlService.getDetailRecord().file.page2 : 1;
                        } else {
                            index = 0;
                        }
                        defaultSelectedFile(index);
                    } else {
                        $scope.gridOptions1.enablePaginationControls = false;  //使用默认的底部分页
                        $scope.gridOptions1.data = [{ 'name': 'No data', 'tag': '' }];
                        $scope.gridOptions2.enablePaginationControls = false;
                        $scope.gridOptions2.data = [{ 'name': 'No data', 'type': '' }];
                        $scope.fileCpyNames='';
                    }
                }).error(function (data) {
                    $scope.gridOptions1.data = [{ 'name': 'No data', 'tag': '' }];
                    console.info('error');
                });
        }

        $scope.setEditMode = function (t) {
            if (t === 'Cancel') {
                $scope.openedProjectDesc = infoDataService.getDescription();
            }
            $scope.disable = !$scope.disable;
            $scope.readOnly = !$scope.readOnly;
        };

        function defaultSelectedFile(index) {
            $scope.selectedName = $scope.gridOptions1.data[index].name;
            $timeout(function () {
                if ($scope.gridApi.selection.selectRow) {
                    $scope.gridApi.selection.selectRow($scope.gridOptions1.data[index]);       //默认选中某一行
                }
            });
            var nodeId = $scope.gridOptions1.data[index].nodeId;
            // var param5 = { 'projectId': infoDataService.getId(), 'nodeId': nodeId,'page': $scope.gridOptions2.paginationCurrentPage,'size': $scope.gridOptions2.paginationPageSize };
            fileDetail(param(nodeId, $scope.gridOptions2.paginationCurrentPage, $scope.gridOptions2.paginationPageSize));
            var selectedNames = [];
            selectedNames.push(nodeId);
            infoDataService.setSelectedNames(selectedNames);
            infoDataService.setTagType('file');
            if ($scope.changePage) {
                $scope.getAllSelectedTags();
            }
        }
    }
})