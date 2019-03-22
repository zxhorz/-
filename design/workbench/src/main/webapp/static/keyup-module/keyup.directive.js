angular.module('keyUpModule',[]).directive("keyUp", function () {
    return {
        restrict: "A",
        scope: {
            keyupSearch: "&"
        },
        replace: true,
        link: function (scope, element, attrs) {
            element.bind("keyup", function (e) {
                var keycode = window.event ? e.keyCode : e.which;
                if (keycode == 13) {
                    scope.keyupSearch();
                }
            });
        },
    }
})