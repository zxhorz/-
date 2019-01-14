'use strict';

//remove all whitespaces in input string
angular.module('menuModule')
    .filter('trim', [function() {
        return function(input) {
            // replace whitespaces with empty char
            return input.replace(/\s+/g, '');
        };
    }]);
