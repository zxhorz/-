'use strict';

var svg = angular.module('svgModule', ['ui.grid','ui.grid.selection','ui.grid.pagination', 'ui.grid.autoResize', 'diff-match-patch','ui.bootstrap'])
.run(['$rootScope', function($rootScope) {
        $rootScope.ui_grid_summary = false;
        $rootScope.ui_grid_detail = false;
}]);
