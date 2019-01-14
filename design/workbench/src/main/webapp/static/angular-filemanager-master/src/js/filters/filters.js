(function(angular) {
    'use strict';
    var app = angular.module('FileManagerApp');

    app.filter('strLimit', ['$filter', function($filter) {
        return function(input, limit, more) {
            if (input.length <= limit) {
                return input;
            }
            return $filter('limitTo')(input, limit) + (more || '...');
        };
    }]);

    app.filter('fileExtension', ['$filter', function($filter) {
        return function(input) {
            return /\./.test(input) && $filter('strLimit')(input.split('.').pop(), 3, '..') || '';
        };
    }]);

    app.filter('formatDate', ['$filter', function() {
        return function(input) {
            if(input instanceof Date){
                var year=input.getFullYear();//year
                if (year< 1900) year = year + 1900;
                var month = input.getMonth() + 1;//month
                if (month < 10) month = '0' + month;
                var day = input.getDate();//day
                if (day < 10) day = '0' + day;
                var hour = input.getHours();//hour
                if (hour < 10) hour = '0' + hour;
                var minute = input.getMinutes();//minutes
                if (minute < 10) minute = '0' + minute;
                var second = input.getSeconds();//seconds
                if (second < 10) second = '0' + second;
                var str=year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
                return str;
            }else{
                return (input.toLocaleString || input.toString).apply(input);
            }
        };
    }]);

    app.filter('trust2Html', ['$sce',function($sce) {
        return function(val) {
            return $sce.trustAsHtml(val);
        };
    }]);

    app.filter('humanReadableFileSize', ['$filter', 'fileManagerConfig', function($filter, fileManagerConfig) {
      // See https://en.wikipedia.org/wiki/Binary_prefix
      var decimalByteUnits = [' kB', ' MB', ' GB', ' TB', 'PB', 'EB', 'ZB', 'YB'];
      var binaryByteUnits = ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];

      return function(input) {
        var i = -1;
        var fileSizeInBytes = input;

        do {
          fileSizeInBytes = fileSizeInBytes / 1024;
          i++;
        } while (fileSizeInBytes > 1024);

        var result = fileManagerConfig.useBinarySizePrefixes ? binaryByteUnits[i] : decimalByteUnits[i];
        return Math.max(fileSizeInBytes, 0.1).toFixed(1) + ' ' + result;
      };
    }]);
})(angular);
