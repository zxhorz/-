'use strict';

//remove all whitespaces in input string
angular.module('codeSearchModule')
.filter('trust2Html', ['$sce',function($sce) {
	return function(val) {
		return $sce.trustAsHtml(val); 
	};
}])
