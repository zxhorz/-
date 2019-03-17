(function (angular,$) {
	'use strict';
	var app = angular.module('noticeModule',[])
	    .controller('noticeController',function ($scope,$state) {
//	    var data = [];

	    var table = $('#tableEmailsList').DataTable();
//        var tableEmailsList = $('#tableEmailsList').DataTable();
//        tableEmailsList.ajax.url = "/data/noticeList";
//        tableEmailsList.ajax.reload();
        $('#tableEmailsList tbody').on('click', 'tr td:eq(1)', function () {
            var data = table.row( this ).data();
//            alert( 'You clicked on '+data[0]+'\'s row' );
            openModal("/xx",{},"title",200,5,5  )
        } );

        $scope.addNotice = function (){
            $state.go('editor');
        }

        $scope.noticeDelete = function (id){
            console.log(1)
            $http({
                method: 'POST',
                url: '/notice/noticeSave',
                params: {
                    'id': $('#title').val(),
                    'content': $scope.tinymceModel
                }
            }).success(function (data) {
                if (data.message === 'S') {
                    $('#modalAjax .loader').fadeOut();
                    $state.go('notice');
                }
            }).error(function (data) {
                $('#modalAjax .modal-body').html('An error occurred while communicating with the server. Please try again.');
            });
        }

	});


})(angular,$);