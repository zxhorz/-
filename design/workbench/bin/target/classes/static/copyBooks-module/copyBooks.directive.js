'use strict';

angular.module('copyBooksModule').directive('copyBooksDirective', function () {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'copyBooks-module/copyBooks.html',
        replace: false,
        controller: copyBooksController
    };
    function copyBooksController($scope, $http, $attrs, $compile, $element, $state, infoDataService, $timeout, historyUrlService) {
        // 进入该页面，初始化tagInfo
        infoDataService.setFromPage('');
        $scope.first = true;
        infoDataService.setTagInfo();
        $scope.selectedName = '';
        $scope.readOnly = true;
        $scope.changePage = false;
        infoDataService.setRowIndex(0);
        $scope.query = '';
        var detailRecord = historyUrlService.getDetailRecord();

        $scope.gridOptions1 = {
            columnDefs: [
                {
                    field: 'cpyName', displayName: 'Name',
                    cellTemplate: '<div ng-if="row.entity.cpyName===\'No data\'" title = {{row.entity.cpyName}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="">{{row.entity.cpyName}}</a></div><div ng-if="row.entity.cpyName!==\'No data\'" title = {{row.entity.location.substring(row.entity.location.lastIndexOf("/")+1,row.entity.location.length)}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="" ng-click="grid.appScope.onDblClick(row.entity);">{{row.entity.location.substring(row.entity.location.lastIndexOf("/")+1,row.entity.location.length)}}</a></div>',
                    cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {      //修改列的样式
                        if (grid.getCellValue(row, col) === 'No data') {
                            return 'noData';          //样式的class名
                        } else {
                            return 'dataInfo';       //样式的class名
                        }
                    }
                },
                {
                    field: 'type', displayName: 'Type',
                    cellTemplate: '<div title = {{row.entity.type}} class="ui-grid-cell-contents ng-binding ng-scope" >{{row.entity.type}}</div>'
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
            //paginationTemplate: "<div class='pageBtn'><button clas='preBtn btn btnColor'></button><button clas='nextBtn btn btnColor'></button></div>", //自定义底部分页代码   
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" ng-click=\"grid.appScope.onClick(rowRenderIndex,row.entity);\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

            enablePagination: true, //是否分页，默认为true
            // enablePaginationControls: true, //使用默认的底部分页
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: 11, //每页显示个数
            //paginationTemplate:"<div></div>", //自定义底部分页代码
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {

                    if (copybookDetail && historyUrlService.getClickFlag()) {
                        $scope.changePage = true;
                        copybookDetail(newPage, pageSize);
                        infoDataService.setRowIndex(0);
                        detailRecord.copyBook.page = newPage;
                        detailRecord.copyBook.num = 0;
                        historyUrlService.setDetailRecord(detailRecord);
                    }
                });
            }
        };
        $scope.gridOptions1.noUnselect = true;
        $scope.gridOptions2 = {
            columnDefs: [
                {
                    field: 'location', displayName: 'Used In Program',
                    //cellTemplate: '<div title = {{row.entity.name}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="" ng-click="grid.appScope.programClick(row.entity);">{{row.entity.name}}</a></div>',                    
                    cellTemplate: '<div title = {{row.entity.location.substring(row.entity.location.lastIndexOf("/")+1,row.entity.location.length)}} class="ui-grid-cell-contents ng-binding ng-scope"><a href="" ng-click="grid.appScope.programClick(row.entity);">{{row.entity.location.substring(row.entity.location.lastIndexOf("/")+1,row.entity.location.length)}}</a></div>',
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
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\"  ng-click='grid.appScope.selectedRow(rowRenderIndex);' ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

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

                    if (geProgramWithCpyId && historyUrlService.getClickFlag()) {
                        var param4 = {
                            'projectId': infoDataService.getId(),
                            'cpyId': infoDataService.getSelectedNames(),
                            'page': newPage,
                            'size': pageSize
                        }
                        geProgramWithCpyId(param4);
                        detailRecord.copyBook.page2 = newPage;
                        detailRecord.copyBook.num2 = -1;
                        historyUrlService.setDetailRecord(detailRecord);
                    }
                });
            }
        };
        $scope.gridOptions2.noUnselect = true;

        if (!historyUrlService.getClickFlag()) {
            if (Object.keys(historyUrlService.getDetailRecord().copyBook).length > 0 && historyUrlService.getDetailRecord().copyBook.searchInp) {
                $scope.searchInp = historyUrlService.getDetailRecord().copyBook.searchInp
                $scope.query = $scope.searchInp;
            }
            $scope.gridOptions1.paginationCurrentPage = historyUrlService.getDetailRecord().copyBook ? historyUrlService.getDetailRecord().copyBook.page ? historyUrlService.getDetailRecord().copyBook.page : 1 : 1;
        } else {
            detailRecord.copyBook = {};
        }
        function detailHistory() {
            if (Object.keys(historyUrlService.getDetailRecord().copyBook).length > 0 && historyUrlService.getDetailRecord().copyBook.num2 > -1 && !historyUrlService.getClickFlag()) {
                $timeout(function () {
                    if ($scope.gridApi2.selection.selectRow) {
                        $scope.gridApi2.selection.selectRow($scope.gridOptions2.data[historyUrlService.getDetailRecord().copyBook.num2]);      //默认选中某一行
                    }
                    historyUrlService.setClickFlag(true);
                });
            } else {
                historyUrlService.setClickFlag(true);
            }
        }

        $scope.onClick = function (index, row) {
            $scope.first = false;
            if (row.cpyName !== 'No data') {
                infoDataService.setRowIndex(index);
                handleCpyDetail(row.cpyName, row.nodeId, row.location);
                $scope.getAllSelectedTags();
                angular.element("pre").scrollTop(0);
                // detailRecord.copyBook = {}；
                detailRecord.copyBook.num = index;
                detailRecord.copyBook.page = $scope.gridOptions1.paginationCurrentPage;
                historyUrlService.setDetailRecord(detailRecord);
            }
        }
        $scope.onDblClick = function (row) {
            // console.info(row.location);
            if (row.location) {
                $state.go('codeBrowser', { projectId: infoDataService.getId(), location: row.location });
            }
        }

        $scope.programClick = function (row) {
            // console.info(row.location);
            if (row.name !== 'No data') {
                $state.go('codeBrowser', { projectId: infoDataService.getId(), location: row.location, startLine: row.lines, endLine: row.lines });
            }
        }

        $scope.selectedRow = function (index) {
            // if (historyUrlService.getDetailRecord().copyBook === undefined) {
            //     detailRecord.copyBook = {};
            // }
            detailRecord.copyBook.num2 = index;
            detailRecord.copyBook.page2 = $scope.gridOptions2.paginationCurrentPage;
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
                copybookDetail($scope.gridOptions1.paginationCurrentPage, $scope.gridOptions1.paginationPageSize);
                detailRecord.copyBook.searchInp = $scope.query;
                detailRecord.copyBook.page = 1;
                historyUrlService.setDetailRecord(detailRecord);
            }
        }

        copybookDetail($scope.gridOptions1.paginationCurrentPage, $scope.gridOptions1.paginationPageSize);
        function copybookDetail(page, size) {
            $http({
                method: 'GET',
                url: './summary/copybookDetail',
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
                        if (Object.keys(historyUrlService.getDetailRecord().copyBook).length > 0 && !historyUrlService.getClickFlag()) {
                            index = historyUrlService.getDetailRecord().copyBook.num >= 0 ? historyUrlService.getDetailRecord().copyBook.num : 0;
                            $scope.gridOptions2.paginationCurrentPage = historyUrlService.getDetailRecord().copyBook.page2 ? historyUrlService.getDetailRecord().copyBook.page2 : 1;
                        } else {
                            index = 0;
                        }
                        var nodeId = $scope.gridOptions1.data[index].nodeId;
                        var location = $scope.gridOptions1.data[index].location;
                        handleCpyDetail($scope.gridOptions1.data[index].cpyName, nodeId, location);
                        if ($scope.changePage) {
                            $scope.getAllSelectedTags();
                        }
                        $timeout(function () {
                            if ($scope.gridApi.selection.selectRow) {
                                $scope.gridApi.selection.selectRow($scope.gridOptions1.data[index]);       //默认选中第一行
                            }
                        });
                    } else {
                        $scope.gridOptions1.enablePaginationControls = false;  //使用默认的底部分页
                        $scope.gridOptions1.data = [{ 'cpyName': 'No data', 'type': '', 'tag': '' }];
                        $scope.gridOptions2.enablePaginationControls = false;
                        $scope.gridOptions2.data = [{ 'location': 'No data' }];
                        $scope.FieldsCode = '';
                        $scope.selectedName = '';
                    }
                }).error(function (data) {
                    $scope.gridOptions1.data = [{ 'cpyName': 'No data', 'type': '', 'tag': '' }];
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


        function geProgramWithCpyId(param1) {
            $http({
                method: 'GET',
                url: './summary/geProgramWithCpyId',
                params: param1
            }).success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptions2.enablePaginationControls = true;  //使用默认的底部分页
                        $scope.gridOptions2.data = data.data.content;
                        $scope.gridOptions2.totalItems = data.data.totalElements;
                        detailHistory();
                    } else {
                        $scope.gridOptions2.enablePaginationControls = false;  //使用默认的底部分页
                        $scope.gridOptions2.data = [{ 'location': 'No data' }];
                        historyUrlService.setClickFlag(true);
                    }
                }).error(function (data) {
                    $scope.gridOptions2 = [];
                    console.info('error');
                });
        }

        function handleCpyDetail(selectedName, nodeId, location) {
            $scope.selectedName = selectedName;
            var param2 = {
                'projectId': infoDataService.getId(),
                'cpyId': nodeId,
                'page': $scope.gridOptions2.paginationCurrentPage,
                'size': $scope.gridOptions2.paginationPageSize
            }
            geProgramWithCpyId(param2);

            $http({
                method: 'GET',
                url: './codebrowser/getSourceCode',
                params: {
                    'projectId': infoDataService.getId(),
                    'filePath': location
                }
            }).success(
                function (data) {
                    if (data && data.data
                        && data.data.length > 0) {
                        $scope.FieldsCode = data.data;
                    } else {
                        $scope.FieldsCode = 'No data';
                    }
                }).error(function (data) {
                    $scope.FieldsCode = [];
                    console.info('error');
                });

            var selectedNames = [];
            selectedNames.push(nodeId);
            infoDataService.setSelectedNames(selectedNames);
            infoDataService.setTagType('copybook');
        }
    }
})