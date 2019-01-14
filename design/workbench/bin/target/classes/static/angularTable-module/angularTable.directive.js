'use strict';

angular.module('angularTableModule').directive('angularTableDirective', function() {

    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'angularTable-module/angularTable.template.html',
        replace: false,
        controller: angularTableController,
        link: angularTableLink
    };

    function angularTableController($scope, $http) {

        $scope.getTableData = function(belongingWindow, belongingtabTitle) {
            $http.get('/window-module/window.json').success(function(data) {
                // 根据所属窗口和tabTitle，取json中应展示的饼图数据
                for (var key in data) {
                    if (key === belongingWindow) {
                        var tabOrGridArray = data[key];
                        for (var i = 0, l = tabOrGridArray.length; i < l; i++) {
                            var title = tabOrGridArray[i].title;
                            if (title === belongingtabTitle) {
                                $scope.columns = tabOrGridArray[i].content.table.header;
                                $scope.data = tabOrGridArray[i].content.table.data;
                            }
                        }
                    }
                }
            });
        };

    }

    function angularTableLink(scope, element, attrs) {

        var belongingWindow = attrs.includein;
        var belongingtabTitle = attrs.tabtitle;

        scope.getTableData(belongingWindow, belongingtabTitle);
    }

});
