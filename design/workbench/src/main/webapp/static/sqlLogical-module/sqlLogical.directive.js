'use strict';

angular.module('sqlLogicalModule').directive('sqlLogicalDirective', function () {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'sqlLogical-module/sqlLogical.html',
        replace: false,
        controller: sqlLogicalController
    };

    function sqlLogicalController($scope, $http, $attrs, $compile, $element, infoDataService, $state, $timeout, historyUrlService) {
        var detailRecord = historyUrlService.getDetailRecord();
        $scope.dataLength = '';
        var page = 0;
        var location = 0;
        $scope.query = '';
        // $scope.totalPages=1;
        $scope.gridOptions = {
            columnDefs: [
                {
                    field: 'programName', displayName: 'Program', cellTemplate: '<div title = {{row.entity.programName}} class="ui-grid-cell-contents ng-binding ng-scope" >{{row.entity.programName}}</div>',
                    width: '30%'
                },
                {
                    field: 'paragraphName', displayName: 'Paragraph', cellTemplate: '<div title = {{row.entity.paragraphName.substring(row.entity.paragraphName.lastIndexOf(".")+1,row.entity.paragraphName.length)}} class="ui-grid-cell-contents ng-binding ng-scope" >{{row.entity.paragraphName.substring(row.entity.paragraphName.lastIndexOf(".")+1,row.entity.paragraphName.length)}}</div>',
                    width: '30%'
                },
                {
                    field: 'operation', displayName: 'Command',
                    cellTemplate: '<div title = {{row.entity.operation}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="" ng-click="grid.appScope.sqlClick(row.entity);" style="color:black;text-decoration:underline">{{row.entity.operation}}</a></div>',
                    width: '20%'
                },
                {
                    field: 'tableName', displayName: 'Table', cellTemplate: '<div title = {{row.entity.tableName}} class="ui-grid-cell-contents ng-binding ng-scope" >{{row.entity.tableName}}</div>'
                }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-click=\"grid.appScope.selectedRow(rowRenderIndex);\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

            enablePagination: true, //是否分页，默认为true
            // enablePaginationControls: true, //使用默认的底部分页
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: infoDataService.pageSize(), //每页显示个数
            //paginationTemplate:"<div></div>", //自定义底部分页代码
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                    if (historyUrlService.getClickFlag() && sqlLogic) {
                        if (infoDataService.getTableInfo() !== '' && infoDataService.getTableInfo() !== null && !angular.equals({}, infoDataService.getTableInfo())) {
                            sqlLogic(newPage, pageSize, location);
                            infoDataService.setTableInfo('');
                        } else {
                            sqlLogic(newPage, pageSize);
                            detailRecord.sqlLogic.page = newPage;
                            detailRecord.sqlLogic.num = -1;
                            historyUrlService.setDetailRecord(detailRecord);
                        }
                    }
                });
            }
        };
        $scope.gridOptions.noUnselect = true;

        if (!historyUrlService.getClickFlag()) {
            if (Object.keys(historyUrlService.getDetailRecord().sqlLogic).length > 0 && historyUrlService.getDetailRecord().sqlLogic.searchInp) {
                $scope.searchInp = historyUrlService.getDetailRecord().sqlLogic.searchInp
                $scope.query = $scope.searchInp;
            }
            $scope.gridOptions.paginationCurrentPage = historyUrlService.getDetailRecord().sqlLogic.page ? historyUrlService.getDetailRecord().sqlLogic.page : 1;
        } else {
            detailRecord.sqlLogic = {};
        }
        function detailHistory() {
            if (Object.keys(historyUrlService.getDetailRecord().sqlLogic).length > 0 && historyUrlService.getDetailRecord().sqlLogic.num > -1 && !historyUrlService.getClickFlag()) {
                $timeout(function () {
                    if ($scope.gridApi.selection.selectRow) {
                        $scope.gridApi.selection.selectRow($scope.gridOptions.data[historyUrlService.getDetailRecord().sqlLogic.num]);      //默认选中某一行
                    }
                    historyUrlService.setClickFlag(true);
                });
            } else {
                historyUrlService.setClickFlag(true);
            }
        }

        $scope.sqlClick = function (row) {
            $state.go('codeBrowser', { location: row.programLocation, endLine: row.endLine, startLine: row.startLine, paragraphName: row.paragraphName });
        }

        $scope.selectedRow = function (index) {
            // detailRecord.sqlLogic = {};
            detailRecord.sqlLogic.num = index;
            detailRecord.sqlLogic.page = $scope.gridOptions.paginationCurrentPage;
            historyUrlService.setDetailRecord(detailRecord);
        }


        $scope.keyWordSearch = function () {
            if (typeof ($scope.searchInp) === 'undefined') {
                $scope.searchInp = '';
            }
            if ($scope.searchInp === '' && $scope.query === '') {
                return
            } else {
                $scope.query = $scope.searchInp;
                $scope.gridOptions.paginationCurrentPage = 1;
                sqlLogic($scope.gridOptions.paginationCurrentPage, $scope.gridOptions.paginationPageSize);
                detailRecord.sqlLogic.searchInp = $scope.query;
                detailRecord.sqlLogic.page = 1;
                historyUrlService.setDetailRecord(detailRecord);
            }
        }

        function sqlLogic(page, size, location) {
            $http({
                method: 'GET',
                url: './summary/sqlLogic',
                params: {
                    'projectId': infoDataService.getId(),
                    'page': page,
                    'size': size,
                    'query': $scope.query
                }
            }).success(
                function (data) {
                    if (data && data.data
                        && data.data.content.length > 0) {
                        $scope.gridOptions.enablePaginationControls = true;  //使用默认的底部分页
                        $scope.gridOptions.data = data.data.content;
                        $scope.gridOptions.totalItems = data.data.totalElements;
                        $scope.totalPages = data.data.totalPages;
                        detailHistory();
                        if (location !== '' && location !== undefined) {
                            $timeout(function () {
                                if ($scope.gridApi.selection.selectRow) {
                                    $scope.gridApi.selection.selectRow($scope.gridOptions.data[location]);       //选中某一行
                                }
                            });
                        }
                    } else {
                        $scope.gridOptions.enablePaginationControls = false;  //使用默认的底部分页
                        $scope.gridOptions.data = [{ 'programName': 'No data', 'paragraphName': '', 'operation': '', 'tableName': '' }];
                        // $scope.gridOptions = [];
                    }
                }).error(function (data) {
                    $scope.gridOptions.data = [{ 'programName': 'No data', 'paragraphName': '', 'operation': '', 'tableName': '' }];
                    console.info('error');
                });
        }

        if (infoDataService.getTableInfo() !== '' && infoDataService.getTableInfo() !== null && !angular.equals({}, infoDataService.getTableInfo())) {
            $http({
                method: 'POST',
                url: './summary/findSqlLogic',
                data: infoDataService.getTableInfo()
            }).success(
                function (data) {
                    if (data && data.data) {
                        // detailRecord.sqlLogic = {};
                        //第几页
                        page = parseInt(data.data / $scope.gridOptions.paginationPageSize);
                        page = parseInt(data.data % $scope.gridOptions.paginationPageSize) == 0 ? page : page + 1;
                        //第几条
                        if (Object.keys(historyUrlService.getDetailRecord().sqlLogic).length == 0) {
                            location = data.data - (page - 1) * $scope.gridOptions.paginationPageSize - 1;
                            detailRecord.sqlLogic.num = location;
                            detailRecord.sqlLogic.page = page;
                            historyUrlService.setDetailRecord(detailRecord);
                        }
                        if (page === 1) {
                            sqlLogic(page, $scope.gridOptions.paginationPageSize, location);
                            infoDataService.setTableInfo('');
                        } else {
                            $scope.gridOptions.paginationCurrentPage = page;
                        }
                    }
                }).error(function (data) {
                    console.info('error');
                });
        } else {
            sqlLogic($scope.gridOptions.paginationCurrentPage, $scope.gridOptions.paginationPageSize);
        }
    }

})