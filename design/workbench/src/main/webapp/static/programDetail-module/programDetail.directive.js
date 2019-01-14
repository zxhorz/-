'use strict';

angular.module('programDetailModule').directive('programDetailDirective', function () {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'programDetail-module/programDetail.html',
        replace: false,
        controller: programDetailController
    };
    function programDetailController($scope, $http, $state, $element, infoDataService, historyUrlService, $timeout) {
        var detailRecord = historyUrlService.getDetailRecord();
        $scope.query = '';
        infoDataService.setFromPage('');
        if (infoDataService.getCloneFinished()) {
            $scope.cloneCell = '<div class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.clonePercentage.substring(0,row.entity.clonePercentage.length-1)}}</div>';
        } else {
            $scope.cloneCell = '<div class="ui-grid-cell-contents ng-binding ng-scope"></div>';
        }
        $scope.gridOptions = {
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
                    cellTemplate: '<div ng-if="row.entity.name===\'No data\'" title = {{row.entity.name}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="javascript:void(0);">{{row.entity.name}}</a></div><div ng-if="row.entity.name!==\'No data\'" title = {{row.entity.location.substring(row.entity.location.lastIndexOf("/")+1,row.entity.location.length)}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="javascript:void(0);" ng-click="grid.appScope.onDblClick(row.entity);" style="color:black;text-decoration:underline">{{row.entity.location.substring(row.entity.location.lastIndexOf("/")+1,row.entity.location.length)}}</a></div>',
                    width: '30%'
                },
                { field: 'type', displayName: 'Type', width: '20%' },
                { field: 'lines', displayName: 'Lines', width: '10%' },
                { field: 'complexity', displayName: 'Complexity', width: '10%' },
                {
                    field: 'clonePercentage', displayName: 'Clone%',
                    cellTemplate: $scope.cloneCell, width: '10%'
                },
                {
                    field: 'tags', displayName: 'Tags',
                    cellTemplate: '<div title = {{row.entity.tags}} class="ui-grid-cell-contents ng-binding ng-scope" >{{row.entity.tags}}</div>'
                }
            ],
            enableVerticalScrollbar: 0,
            enableSorting: false,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-click=\"grid.appScope.selectedRow(rowRenderIndex);\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

            enablePagination: true, //是否分页，默认为true
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: infoDataService.pageSize(), //每页显示个数
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                    if (programDetail && historyUrlService.getClickFlag()) {
                        programDetail(newPage, pageSize);
                        detailRecord.program.page = newPage;
                        detailRecord.program.num = -1;
                        historyUrlService.setDetailRecord(detailRecord);
                    }
                });
            }
        };
        $scope.gridOptions.noUnselect = true;

        if (!historyUrlService.getClickFlag()) {
            if (Object.keys(historyUrlService.getDetailRecord().program).length > 0 && historyUrlService.getDetailRecord().program.searchInp) {
                $scope.searchInp = historyUrlService.getDetailRecord().program.searchInp;
                $scope.query = $scope.searchInp;
            }
            infoDataService.setDetailInfo(historyUrlService.getDetailRecord().program.type);
            $scope.gridOptions.paginationCurrentPage = historyUrlService.getDetailRecord().program.page ? historyUrlService.getDetailRecord().program.page : 1;
        } else {
            detailRecord.program = {};
            detailRecord.program.type = infoDataService.getDetailInfo();
            if (infoDataService.getProgramInfo()) {
                $scope.searchInp= infoDataService.getProgramInfo();
                $scope.query = $scope.searchInp;
                detailRecord.program.searchInp = $scope.query;
                historyUrlService.setDetailRecord(detailRecord);
                infoDataService.setProgramInfo('');
            }
        }

        $scope.onDblClick = function (row) {
            $state.go('codeBrowser', { projectId: infoDataService.getId(), location: row.location });
        }

        $scope.selectedRow = function (index) {
            detailRecord.program.num = index;
            detailRecord.program.page = $scope.gridOptions.paginationCurrentPage;
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
                programDetail($scope.gridOptions.paginationCurrentPage, $scope.gridOptions.paginationPageSize);
                detailRecord.program.searchInp = $scope.query;
                detailRecord.program.page = 1;
                historyUrlService.setDetailRecord(detailRecord);
            }
        }

        function detailHistory() {
            if (!historyUrlService.getClickFlag() && Object.keys(historyUrlService.getDetailRecord().program).length > 0 && historyUrlService.getDetailRecord().program.num > -1) {
                $timeout(function () {
                    if ($scope.gridApi.selection.selectRow) {
                        $scope.gridApi.selection.selectRow($scope.gridOptions.data[historyUrlService.getDetailRecord().program.num]);      //默认选中某一行
                    }
                    historyUrlService.setClickFlag(true);
                });
            } else {
                historyUrlService.setClickFlag(true);
            }
        }

        programDetail($scope.gridOptions.paginationCurrentPage, $scope.gridOptions.paginationPageSize);
        function programDetail(page, size) {
            $http({
                method: 'GET',
                url: './summary/programDetail',
                params: {
                    'projectId': infoDataService.getId(),
                    'type': infoDataService.getDetailInfo(),
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
                        detailHistory();
                    } else {
                        $scope.gridOptions.enablePaginationControls = false;  //使用默认的底部分页
                        $scope.gridOptions.data = [{ 'name': 'No data', 'type': '', 'lines': '', 'complexity': '', 'clonePercentage': '', 'tags': '' }];
                    }
                }).error(function (data) {
                    $scope.gridOptions.data = [{ 'name': 'No data', 'type': '', 'lines': '', 'complexity': '', 'clonePercentage': '', 'tags': '' }];
                    console.info('error');
                });
        }
    }
})