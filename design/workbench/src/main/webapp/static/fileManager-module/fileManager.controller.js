'use strict';
angular.module('fileManagerModule')
.controller('fileManagerCtrl', function ($http, $scope, $modalInstance,infoDataService,$state) {
    infoDataService.setPage('fileManager');
    $scope.close = function () {
        $modalInstance.close();
    }
    $scope.goCodeBrowser=function(){
        $modalInstance.close("Y");
        $state.go('codeBrowser');
    }
});