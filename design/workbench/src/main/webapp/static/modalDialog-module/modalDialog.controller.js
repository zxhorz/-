'use strict';
angular.
    module('modalDialogModule')
    .controller('modalDialogController', function ($scope) {
        $scope.items = ['item1', 'item2', 'item3'];
        $scope.myVar = false;
        $scope.open = function () {
            $scope.myVar = true;
        }
        $scope.all = function (m) {
            if (m === true) {
                $scope.persons[i].state = true;
            } else {
                $scope.persons[i].state = false;
            }
        }
        $scope.ok = function () {
            $scope.myVar = false;
        };
        $scope.cancel = function () {
            $scope.myVar = false;
        };
        $scope.close = function () {
            $scope.myVar = false;
        };

    })
