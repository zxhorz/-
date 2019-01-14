'use strict';

angular.module('diffcheckerModule').directive('diffcheckerDirective', function () {
    return {
        restrict: 'EA',
        templateUrl: 'diffchecker-module/diffchecker.html',
        replace: false,
        scope: {
            name1: "=",
            name2: "=",
            left: '=',
            right: "="
        }
    };
})