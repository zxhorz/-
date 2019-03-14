(function (angular,$) {
	'use strict';
	var app = angular.module('noticeModule',[])
	    .controller('noticeController',function ($scope,$state) {
//	    var data = [];

	    var table = $('#tableEmailsList').DataTable();
//        var tableEmailsList = $('#tableEmailsList').DataTable();
//        tableEmailsList.ajax.url = "/data/noticeList";
//        tableEmailsList.ajax.reload();
        $('#tableEmailsList tbody').on('click', 'tr', function () {
            var data = table.row( this ).data();
//            alert( 'You clicked on '+data[0]+'\'s row' );
            openModal("/xx",{},"title",200,5,5  )
        } );

        $scope.addNotice = function (){
            $state.go('editor');
        }


	});


})(angular,$);