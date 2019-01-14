'use strict';

var uigrid = angular.module('uiGridModule', ['ui.grid','ui.grid.selection','ui.grid.pagination', 'ui.grid.autoResize'])
.run(['$rootScope', function($rootScope) {
        $rootScope.ui_grid_summary = false;
        $rootScope.ui_grid_detail = false;
}]);
