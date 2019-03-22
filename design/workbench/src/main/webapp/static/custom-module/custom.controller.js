'use strict';
angular.module('customModule')
    .controller('customController', function ($scope, $http, $timeout, $modal, $state, infoDataService, historyUrlService) {
        infoDataService.setPage('custom');
        infoDataService.setFromPage('custom');
        // infoDataService.setFromPage('MENU');
        var custom = historyUrlService.getcustom();

        if (historyUrlService.getClickFlag()) {
            custom.program = {};
            historyUrlService.setUrlInfo('custom');
        }
    })