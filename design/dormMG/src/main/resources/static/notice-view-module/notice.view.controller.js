(function (angular) {
    'use strict';
    var app = angular.module('noticeViewModule', []);

    app.controller('noticeViewController', function ($scope, $http, $rootScope, $state,$stateParams) {

        var notice = $stateParams.notice;
        var content = notice.content;
        $('#noticeContent').html(content);


    })


})(angular);