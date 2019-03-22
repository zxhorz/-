'use strict';

angular.module('tableModule').directive('tableItemDirective', function () {
    return {
        restrict: 'EA',
        scope: false,
        templateUrl: 'table-module/table.html',
        replace: false,
        controller: tableController,
        // link: function (scope, element, attr,historyUrlService) {
        //     historyUrlService.setClickFlag(true);
        // }
    };

    function tableController($scope, $http, $attrs, $compile, $element, infoDataService, $rootScope, $timeout, $state, historyUrlService) {
        // 进入该页面，初始化tagInfo
        $scope.first = true;
        infoDataService.setTagInfo();
        $scope.selectedName = '';
        $scope.readOnly = true;
        $scope.show = false;
        infoDataService.setRowIndex(0);
        $scope.modelName = [];
        $scope.changePage = false;
        var tabFinished2 = false;
        var tabFinished3 = false;
        $scope.query = '';
        var detailRecord = historyUrlService.getDetailRecord();

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
                    cellTemplate: '<div title = {{row.entity.name}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="">{{row.entity.name}}</a></div>'
                },
                {
                    field: 'tags', displayName: 'Tags',
                    cellTemplate: '<div title = {{row.entity.tags}} class="ui-grid-cell-contents ng-binding ng-scope tagsInfo" >{{row.entity.tags}}</div>'
                }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-click=\"grid.appScope.onDblClick(rowRenderIndex,row.entity);\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

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

                    if (table && historyUrlService.getClickFlag()) {
                        $scope.changePage = true;
                        table(newPage, pageSize);
                        infoDataService.setRowIndex(0);
                        detailRecord.table.page = newPage;
                        detailRecord.table.num = 0;
                        historyUrlService.setDetailRecord(detailRecord);
                    }
                });
            }
        };
        $scope.gridOptions1.noUnselect = true;

        $scope.gridOptions2 = {
            columnDefs: [
                {
                    field: 'programName', displayName: 'Program',
                    cellTemplate: '<div title = {{row.entity.programName}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="" ng-click="grid.appScope.tableClick(row.entity);">{{row.entity.programName}}</a></div>',
                    cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {      //修改列的样式
                        if (grid.getCellValue(row, col) === 'No data') {
                            return 'noData';          //样式的class名
                        } else {
                            return 'dataInfo';       //样式的class名
                        }
                    }
                },
                {
                    field: 'paragraphName', displayName: 'Paragraph',
                    cellTemplate: '<div title = {{row.entity.paragraphName.substring(row.entity.paragraphName.indexOf(".")+1,row.entity.paragraphName.length)}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="" ng-click="grid.appScope.tableClick(row.entity);">{{row.entity.paragraphName.substring(row.entity.paragraphName.indexOf(".")+1,row.entity.paragraphName.length)}}</a></div>',
                    cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {      //修改列的样式
                        if (grid.getCellValue(row, col) === 'No data') {
                            return 'noData';          //样式的class名
                        } else {
                            return 'dataInfo';       //样式的class名
                        }
                    }
                }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-click=\"grid.appScope.selectedRow2(rowRenderIndex);\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

            enablePagination: true, //是否分页，默认为true
            // enablePaginationControls: true, //使用默认的底部分页
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: 11, //每页显示个数
            //paginationTemplate:"<div></div>", //自定义底部分页代码
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            onRegisterApi: function (gridApi2) {
                $scope.gridApi2 = gridApi2;
                //分页按钮事件
                gridApi2.pagination.on.paginationChanged($scope, function (newPage, pageSize) {

                    if (useTable && historyUrlService.getClickFlag()) {
                        useTable(param(infoDataService.getSelectedNames(), newPage, pageSize));
                        detailRecord.table.page2 = newPage;
                        detailRecord.table.num2 = -1;
                        historyUrlService.setDetailRecord(detailRecord);
                    }
                });
            }
        };
        $scope.gridOptions2.noUnselect = true;

        $scope.gridOptions3 = {
            columnDefs: [
                {
                    field: 'name', displayName: 'Name',
                    cellTemplate: '<div title = {{row.entity.name}} class="ui-grid-cell-contents ng-binding ng-scope" >{{row.entity.name}}</div>'
                },
                { field: 'type', displayName: 'Type' }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-click=\"grid.appScope.selectedRow3(rowRenderIndex);\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

            enablePagination: true, //是否分页，默认为true
            // enablePaginationControls: true, //使用默认的底部分页
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: 11, //每页显示个数
            //paginationTemplate:"<div></div>", //自定义底部分页代码
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            onRegisterApi: function (gridApi3) {
                $scope.gridApi3 = gridApi3;
                //分页按钮事件
                gridApi3.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                    if (tableDetail && historyUrlService.getClickFlag()) {
                        tableDetail(param(infoDataService.getSelectedNames(), newPage, pageSize));
                        detailRecord.table.page3 = newPage;
                        detailRecord.table.num3 = -1;
                        historyUrlService.setDetailRecord(detailRecord);
                    }
                });
            }
        };
        $scope.gridOptions3.noUnselect = true;

        if (!historyUrlService.getClickFlag()) {
            if (Object.keys(historyUrlService.getDetailRecord().table).length > 0 && historyUrlService.getDetailRecord().table.searchInp) {
                $scope.searchInp = historyUrlService.getDetailRecord().table.searchInp
                $scope.query = $scope.searchInp;
            }
            $scope.gridOptions1.paginationCurrentPage = Object.keys(historyUrlService.getDetailRecord().table).length > 0 ? historyUrlService.getDetailRecord().table.page ? historyUrlService.getDetailRecord().table.page : 1 : 1;
        } else {
            detailRecord.table = {};
        }

        function detailHistory() {
            if (!historyUrlService.getClickFlag()) {
                if (Object.keys(historyUrlService.getDetailRecord().table).length > 0) {
                    if (historyUrlService.getDetailRecord().table.num2 > -1 && tabFinished2 === false) {
                        $timeout(function () {
                            if ($scope.gridApi2.selection.selectRow) {
                                $scope.gridApi2.selection.selectRow($scope.gridOptions2.data[historyUrlService.getDetailRecord().table.num2]);      //默认选中某一行
                            }
                        });
                    }
                    if (historyUrlService.getDetailRecord().table.num3 > -1 && tabFinished3 === false) {
                        $timeout(function () {
                            if ($scope.gridApi3.selection.selectRow) {
                                $scope.gridApi3.selection.selectRow($scope.gridOptions3.data[historyUrlService.getDetailRecord().table.num3]);      //默认选中某一行
                            }
                        });
                    }
                    historyUrlService.setClickFlag(true);
                }
            }
        }


        $scope.onDblClick = function (index, row) {
            if (row.name !== 'No data') {
                $scope.first = false;
                $scope.selectedName = row.name;
                useTable(param(row.nodeId, $scope.gridOptions2.paginationCurrentPage, $scope.gridOptions2.paginationPageSize));
                tableDetail(param(row.nodeId, $scope.gridOptions3.paginationCurrentPage, $scope.gridOptions3.paginationPageSize));
                var selectedNames = [];
                selectedNames.push(row.nodeId);
                infoDataService.setSelectedNames(selectedNames);

                infoDataService.setRowIndex(index);

                infoDataService.setTagType('table');
                $scope.getAllSelectedTags();
                // detailRecord.table = {};
                detailRecord.table.num = index;
                detailRecord.table.page = $scope.gridOptions1.paginationCurrentPage;
                historyUrlService.setDetailRecord(detailRecord);
            }
        }

        $scope.paragraphClick = function (row) {
            // console.info(row.location);
            if (row.name !== 'No data') {
                $state.go('codeBrowser', { location: row.programLocation, endLine: row.endLine, startLine: row.startLine, paragraphName: row.paragraphName });
            }
        }

        $scope.selectedRow2 = function (index) {
            // if (historyUrlService.getDetailRecord().table === undefined) {
            //     detailRecord.table = {};
            // }
            detailRecord.table.num2 = index;
            detailRecord.table.page2 = $scope.gridOptions2.paginationCurrentPage;
            historyUrlService.setDetailRecord(detailRecord);
        }

        $scope.selectedRow3 = function (index) {
            // if (historyUrlService.getDetailRecord().table === undefined) {
            //     detailRecord.table = {};
            // }
            detailRecord.table.num3 = index;
            detailRecord.table.page3 = $scope.gridOptions3.paginationCurrentPage;
            historyUrlService.setDetailRecord(detailRecord);
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
                table($scope.gridOptions1.paginationCurrentPage, $scope.gridOptions1.paginationPageSize);
                detailRecord.table.searchInp = $scope.query;
                detailRecord.table.page = 1;
                historyUrlService.setDetailRecord(detailRecord);
            }
        }

        table($scope.gridOptions1.paginationCurrentPage, $scope.gridOptions1.paginationPageSize);

        function table(page, size) {
            $http({
                method: 'GET',
                url: './summary/table',
                params: {
                    'projectId': infoDataService.getId(),
                    'page': page,
                    'size': size,
                    'query': $scope.query
                }
            }).success(
                function (data) {
                    // console.info(data.data);
                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptions1.enablePaginationControls = true;  //使用默认的底部分页
                        $scope.gridOptions1.data = data.data.content;
                        $scope.gridOptions1.totalItems = data.data.totalElements;
                        var index;
                        if (Object.keys(historyUrlService.getDetailRecord().table).length > 0 && !historyUrlService.getClickFlag()) {
                            index = historyUrlService.getDetailRecord().table.num >= 0 ? historyUrlService.getDetailRecord().table.num : 0;
                            $scope.gridOptions2.paginationCurrentPage = historyUrlService.getDetailRecord().table.page2 ? historyUrlService.getDetailRecord().table.page2 : 1;
                            $scope.gridOptions3.paginationCurrentPage = historyUrlService.getDetailRecord().table.page3 ? historyUrlService.getDetailRecord().table.page3 : 1;
                        } else {
                            index = 0;
                        }
                        defaultSelectedTable(index);
                    } else {
                        $scope.gridOptions1.enablePaginationControls = false;  //使用默认的底部分页
                        $scope.gridOptions1.data = [{ 'name': 'No data', 'tag': '' }];
                        $scope.gridOptions2.enablePaginationControls = false;
                        $scope.gridOptions2.data = [{ 'programName': 'No data', 'paragraphName': '' }];
                        $scope.gridOptions3.enablePaginationControls = false;
                        $scope.gridOptions3.data = [{ 'name': 'No data', 'type': '' }];
                        $scope.selectedName = '';
                    }
                }).error(function (data) {
                    $scope.gridOptions1.data = [{ 'name': 'No data', 'tag': '' }];
                    console.info('error');
                });
        }

        function useTable(param1) {
            $http({
                method: 'GET',
                url: './summary/useTable',
                params: param1
            }).success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptions2.enablePaginationControls = true;  //使用默认的底部分页
                        $scope.gridOptions2.data = data.data.content;
                        $scope.gridOptions2.totalItems = data.data.totalElements;
                        detailHistory();
                        tabFinished2 = true;
                    } else {
                        $scope.gridOptions2.enablePaginationControls = false;  //使用默认的底部分页
                        $scope.gridOptions2.data = [{ 'programName': 'No data', 'paragraphName': '' }];
                    }
                }).error(function (data) {
                    $scope.gridOptions2.data = [{ 'programName': 'No data', 'paragraphName': '' }];
                    console.info('error');
                });
        }

        function tableDetail(param1) {
            $http({
                method: 'GET',
                url: './summary/tableDetail',
                params: param1
            }).success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptions3.enablePaginationControls = true;  //使用默认的底部分页
                        $scope.gridOptions3.data = data.data.content;
                        $scope.gridOptions3.totalItems = data.data.totalElements;
                        detailHistory();
                        tabFinished3 = true;
                    } else {
                        $scope.gridOptions3.enablePaginationControls = false;  //使用默认的底部分页
                        $scope.gridOptions3.data = [{ 'name': 'No data', 'type': '' }];
                        historyUrlService.setClickFlag(true);
                    }
                }).error(function (data) {
                    $scope.gridOptions3.data = [{ 'name': 'No data', 'type': '' }];
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

        function defaultSelectedTable(index) {
            $scope.selectedName = $scope.gridOptions1.data[index].name;
            $timeout(function () {
                if ($scope.gridApi.selection.selectRow) {
                    $scope.gridApi.selection.selectRow($scope.gridOptions1.data[index]);       //默认选中第一行
                }
            });
            var nodeId = $scope.gridOptions1.data[index].nodeId;
            useTable(param(nodeId, $scope.gridOptions2.paginationCurrentPage, $scope.gridOptions2.paginationPageSize));
            tableDetail(param(nodeId, $scope.gridOptions3.paginationCurrentPage, $scope.gridOptions3.paginationPageSize));

            var selectedNames = [];
            selectedNames.push(nodeId);
            infoDataService.setSelectedNames(selectedNames);
            infoDataService.setTagType('table');
            if ($scope.changePage) {
                $scope.getAllSelectedTags();
            }
        }
    }
})