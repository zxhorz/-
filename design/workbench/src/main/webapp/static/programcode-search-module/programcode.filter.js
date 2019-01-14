'use strict';

//remove all whitespaces in input string
angular.module('programcodesearch')
    .filter('toExponential', ['$filter', function ($filter) {
        return function (input) {
            // input为空，不操作，直接返回空
            if (input === '') {
                return input;
            }

            var result = $filter('number')(input, 2);
            // 如果input为0.00xxx，则科学计数法保留2位小数，否则，直接number filter保留2位小数
            if (result == 0.00) {
                return input.toExponential(2);
            }
            return result;
        };
    }]);
