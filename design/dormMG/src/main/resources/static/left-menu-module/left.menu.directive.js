(function(angular) {
    'use strict';
    var app = angular.module('DormManagerApp');

    app.directive('leftMenuModule', ['$parse', 'dromManagerConfig', function($parse, dromManagerConfig) {
        return {
            restrict: 'EA',
            templateUrl: dromManagerConfig.tplPath + '/left-menu.html',
            controller: leftMenuController
        };

        function leftMenuController($scope, $http, $location, $rootScope,$attrs){
            angular.element('.menu .item');
//            var element = angular.element('.menu .item');
//            angular.element('.menu .item').bind('click',function() {
//                if (jQuery(this).hasClass("active")) {
//                    preventDefault();
//                }
//                if (!jQuery(this).hasClass("active")) {
//                    jQuery("ul", jQuery(this).parents("ul:first")).slideUp(350);
//                    jQuery(this).siblings().removeClass("active");
//                    jQuery(this).find("ul").slideDown(350);
//                    jQuery(this).addClass("active");
//                } else if (jQuery(this).hasClass("active")) {
//                    jQuery("ul", jQuery(this).parents("ul:first")).slideUp(350);
//                    jQuery(this).removeClass("active");
//                }
//            });
        }
    }]);


})(angular);
