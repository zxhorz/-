'use strict';

angular.module('menuModule')
    .controller('PreferenceController', ['$scope', '$stateParams', '$modal', function($scope, $stateParams, $modal) {
        var myModal = $modal({ title: 'My Title', content: 'My Content', show: true });
        myModal.show;
    }]);
