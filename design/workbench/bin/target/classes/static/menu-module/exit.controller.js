'use strict';

angular.module('menuModule')
    .controller('ExitController', ['$scope', '$stateParams', function() {
        if (navigator.userAgent.indexOf('MSIE') > -1) {
              if (navigator.userAgent.indexOf('MSIE 6.0') > -1) {
                   window.opener = null;
                   window.close();
              } else {
                   window.open('', '_top');
                   window.top.close();
              }
         }else if (navigator.userAgent.indexOf('Firefox') > -1) {
              window.close();
         } else {
              window.opener = null;
              window.open('', '_self', '');
              window.close();
         }
    }]);
