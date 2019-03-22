'use strict';

angular.module('tableFilesModule').directive('tableFilesDirective', function () {
    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'tableFiles-module/tableFiles.html',
        replace: false,
        controller: tableFilesController
    };

    function tableFilesController($scope, $http, $attrs, $compile, $element,infoDataService) {
        $scope.readOnly = true;
        $scope.gridOptions1 = {
            columnDefs: [
                { field: 'name', displayName: 'Name',
                cellTemplate : '<div class="ui-grid-cell-contents ng-binding ng-scope"><a href="" ng-click="grid.appScope.onDblClick(row.entity);" style="color:black;text-decoration:underline">{{row.entity.name}}</a></div>'},
                { field: 'type', displayName: 'Type' }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",
        };
        $scope.gridOptions1.multiSelect = false;
        $scope.gridOptions1.modifierKeysToMultiSelect = false;
        $scope.gridOptions1.noUnselect = true;
        $scope.gridOptions1.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;
            // gridApi.selection.clearSelectedRows();
        };

        $scope.gridOptions2 = {
            columnDefs: [
                { field: 'name', displayName: 'Name' },
                { field: 'type', displayName: 'Type' },
                { field: 'comment', displayName: 'Comments' }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
        };
        $scope.gridOptions2.multiSelect = false;
        $scope.gridOptions2.modifierKeysToMultiSelect = false;
        $scope.gridOptions2.noUnselect = true;
        $scope.gridOptions2.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;
            // gridApi.selection.clearSelectedRows();
        };

        $scope.onDblClick = function (row) {
            $http({
                method: 'GET',
                url: './summary/tableAndFileDetail',
                params: {
                    "projectId": infoDataService.getId(),
                    "nodeId":row.nodeId,
                    "type":row.type
                }
            })
                .success(
                function (data) {
                    if (data && data.data
                        && data.data.length > 0) {
                        $scope.gridOptions2.data = data.data;
                    } else {
                        $scope.gridOptions2 = [];
                    }
                }).error(function (data) {
                    $scope.gridOptions2 = [];
                    console.info("error");
                });
    
        }
        $http({
            method: 'GET',
            url: './summary/tableAndFile',
            params: {
                "projectId": infoDataService.getId()
            }
        })
            .success(
            function (data) {
                if (data && data.data
                    && data.data.length > 0) {
                    $scope.gridOptions1.data = data.data;
                } else {
                    $scope.gridOptions1 = [];
                }
            }).error(function (data) {
                $scope.gridOptions1 = [];
                console.info("error");
            });

            $scope.setEditMode = function (t) {
                if (t === 'Cancel') {
                    $scope.openedProjectDesc = infoDataService.getDescription();
                }
                $scope.disable = !$scope.disable;
                $scope.readOnly = !$scope.readOnly;
            };
    }

})