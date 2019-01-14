'use strict';

angular.module('queryModule')
    .controller('QueryController', ['$rootScope', function($rootScope) {
        //do something
        $rootScope.neo4j_window_show = true;
    }]);
