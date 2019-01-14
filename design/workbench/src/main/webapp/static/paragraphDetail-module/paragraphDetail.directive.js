'use strict';

angular.module('paragraphDetailModule').directive('paragraphDetailDirective', function () {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'paragraphDetail-module/paragraphDetail.html',
        replace: false,
        controller: paragraphDetailController
    };

    function paragraphDetailController($scope, $http, $attrs, $compile, $element, infoDataService, $state, historyUrlService, $timeout) {
        var detailRecord = historyUrlService.getDetailRecord();
        $scope.dataLength = '';
        $scope.query = '';
        $scope.searchInp = '';
        if (infoDataService.getCloneFinished()) {
            $scope.cloneCell = '<div class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.clonePercentage.substring(0,row.entity.clonePercentage.length-1)}}</div>';
        } else {
            $scope.cloneCell = '<div class="ui-grid-cell-contents ng-binding ng-scope"></div>';
        }
        $scope.gridOptions = {
            columnDefs: [
                {
                    field: 'paragraphName', displayName: 'Name',
                    cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {      //修改列的样式
                        if (grid.getCellValue(row, col) === 'No data') {
                            return 'noData';          //样式的class名
                        } else {
                            return 'dataInfo';       //样式的class名
                        }
                    },
                    cellTemplate: '<div ng-if="row.entity.paragraphName===\'No data\'" title = {{row.entitytity.paragraphName}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="">{{row.entity.paragraphName}}</a></div><div ng-if="row.entity.paragraphName!==\'No data\'" title = {{row.entity.programLocation.substring(row.entity.programLocation.lastIndexOf("/")+1,row.entity.programLocation.length)+"."+row.entity.paragraphName.substring(row.entity.paragraphName.lastIndexOf(".")+1,row.entity.paragraphName.length)}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="" ng-click="grid.appScope.onDblClick(row.entity);">{{row.entity.programLocation.substring(row.entity.programLocation.lastIndexOf("/")+1,row.entity.programLocation.length)+"."+row.entity.paragraphName.substring(row.entity.paragraphName.lastIndexOf(".")+1,row.entity.paragraphName.length)}}</a></div>', 
                    width: '50%'
                },
                {
                    field: 'lines', displayName: 'Lines', width: '10%'
                },
                { field: 'complexity', displayName: 'Complexity', width: '10%' },
                { field: 'clonePercentage', displayName: 'Clone%', cellTemplate: $scope.cloneCell, width: '10%' },
                {
                    field: 'tags', displayName: ''
                }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\"  ng-click=\"grid.appScope.selectedRow(rowRenderIndex);\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

            enablePagination: true, //是否分页，默认为true
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: infoDataService.pageSize(), //每页显示个数
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                    if (paragraphDetail && historyUrlService.getClickFlag()) {
                        paragraphDetail(newPage, pageSize);
                        detailRecord.paragraph.page = newPage;
                        detailRecord.paragraph.num = -1;                        
                        historyUrlService.setDetailRecord(detailRecord);
                    }
                });
            }
        };
        $scope.gridOptions.noUnselect = true;

        if (!historyUrlService.getClickFlag()) {
            if (Object.keys(historyUrlService.getDetailRecord().paragraph).length>0 && historyUrlService.getDetailRecord().paragraph.searchInp) {
            $scope.searchInp = historyUrlService.getDetailRecord().paragraph.searchInp;
                $scope.query = $scope.searchInp;
            }
            $scope.gridOptions.paginationCurrentPage = historyUrlService.getDetailRecord().paragraph.page?historyUrlService.getDetailRecord().paragraph.page:1;
        }else{
            detailRecord.paragraph = {};
        }

        $scope.onDblClick = function (row) {
            $state.go('codeBrowser', { location: row.programLocation, endLine: row.endLine, startLine: row.startLine, paragraphName: row.paragraphName });
        }

        $scope.selectedRow = function (index) {
            detailRecord.paragraph.num = index;
            detailRecord.paragraph.page = $scope.gridOptions.paginationCurrentPage;
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
                paragraphDetail($scope.gridOptions.paginationCurrentPage, $scope.gridOptions.paginationPageSize);
                detailRecord.paragraph.searchInp = $scope.query;
                detailRecord.paragraph.page=1;
                historyUrlService.setDetailRecord(detailRecord);
            }
        }

        function detailHistory() {
            if (!historyUrlService.getClickFlag() && Object.keys(historyUrlService.getDetailRecord().paragraph).length>0 && historyUrlService.getDetailRecord().paragraph.num>-1 ) {
                $timeout(function () {
                    if ($scope.gridApi.selection.selectRow) {
                        $scope.gridApi.selection.selectRow($scope.gridOptions.data[historyUrlService.getDetailRecord().paragraph.num]);      //默认选中某一行
                    }
                    historyUrlService.setClickFlag(true);
                });
            }else{
                historyUrlService.setClickFlag(true);
            }
        }
        paragraphDetail($scope.gridOptions.paginationCurrentPage, $scope.gridOptions.paginationPageSize);
        function paragraphDetail(page, size) {
            $http({
                method: 'GET',
                url: './summary/paragraphDetail',
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
                        detailHistory();
                    } else {
                        $scope.gridOptions.enablePaginationControls=false;  //使用默认的底部分页
                        $scope.gridOptions.data = [{ 'paragraphName': 'No data', 'lines': '', 'complexity': '', 'clonePercentage': '', 'tags': ''}];
                    }
                }).error(function (data) {
                    $scope.gridOptions.data = [{ 'paragraphName': 'No data', 'lines': '', 'complexity': '', 'clonePercentage': '', 'tags': ''}];
                    console.info('error');
                });
        }

        // infoDataService.getGridOptions(infoDataService.getId(), $scope.gridOptions, './summary/paragraphDetail');

        // function getPage(curPage, pageSize) {
        //     var firstRow = (curPage - 1) * pageSize;
        //     $scope.gridOptions.data = $scope.gridOptionsInfo.slice(firstRow, firstRow + pageSize);
        // };
    }

})