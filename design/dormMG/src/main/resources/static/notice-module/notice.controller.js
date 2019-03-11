(function (angular,$) {
	'use strict';
	var app = angular.module('noticeModule',[])
	    .controller('noticeController', ['$scope',function ($scope) {
	    var data = [];

//	    $('#tableEmailsList').DataTable(data);
        var tableEmailsList = $('#tableEmailsList').DataTable();
        tableEmailsList.settings()[0].ajax.url = "/notice/noticeList";
        tableEmailsList.ajax.reload();

	}]);


})(angular,$);