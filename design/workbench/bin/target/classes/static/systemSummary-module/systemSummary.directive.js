'use strict';

angular.module('systemSummaryModule').directive('systemSummaryDirective', function () {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'systemSummary-module/systemSummary.html',
        replace: false,
        controller: systemSummaryController
    };

    function systemSummaryController($scope, $http, $attrs, $compile, $element, infoDataService, historyUrlService, $timeout) {
        var detailRecord = historyUrlService.getDetailRecord();
        $scope.gridOptions = {
            columnDefs: [
                {
                    field: 'detailName',
                    displayName: 'System Summary',
                    cellTemplate: '<div class="ui-grid-cell-contents ng-binding ng-scope"><a href="javascript:void(0);" ng-click="grid.appScope.SystemDblClick(row.entity);" class="summTxt">{{grid.appScope.getTabMap(row.entity.detailName).tableColumn}}</a></div>'
                },
                {
                    field: 'detailData',
                    displayName: 'Total'
                }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" ng-click=\"grid.appScope.selectedRow(rowRenderIndex);\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
            }
        };

        function detailHistory() {
            if (!historyUrlService.getClickFlag() && historyUrlService.getDetailRecord().system && !historyUrlService.getClickFlag()) {
                $timeout(function () {
                    if ($scope.gridApi.selection.selectRow) {
                        $scope.gridApi.selection.selectRow($scope.gridOptions.data[historyUrlService.getDetailRecord().system.num]);      //默认选中第一行
                    }
                    historyUrlService.setClickFlag(true);
                });
            }else{
                historyUrlService.setClickFlag(true);
            }
        }

        system();
        function system() {
            $http({
                method: 'GET',
                url: './summary/system',
                params: {
                    'projectId': infoDataService.getId()
                }
            }).success(
                function (data) {
                    if (data && data.data
                        && data.data.length > 0) {
                        $scope.gridOptions.data = data.data;
                        detailHistory();
                    } else {
                        $scope.gridOptions = [];
                    }
                }).error(function (data) {
                    $scope.gridOptions = [];
                    console.info('error');
                });
        }

        $scope.selectedRow = function (index) {
            detailRecord.system = {};
            detailRecord.system.num = index;
            historyUrlService.setDetailRecord(detailRecord);
        }
    }

})