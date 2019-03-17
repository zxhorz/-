(function (angular,$) {
	'use strict';
	var app = angular.module('noticeModule',[])
	    .controller('noticeController',function ($scope,$state,$compile) {
//	    var data = [];

        var alreadyReady = false; // The ready function is being called twice on page load.
        var table = $("#tableEmailsList").DataTable({
            "dom": '<"listtable"fit>pl',
            "responsive": true,
            "oLanguage": {
                "sEmptyTable": "没有记录",
                "sInfo": "共有 _TOTAL_ 项，正在展示第 _START_ 到 _END_ 项",
                "sInfoEmpty": "无记录",
                "sInfoFiltered": "(从 _MAX_ 条中筛选)",
                "sInfoPostFix": "",
                "sInfoThousands": ",",
                "sLengthMenu": "每页显示 _MENU_ 条",
                "sLoadingRecords": "加载中...",
                "sProcessing": "处理中...",
                "sSearch": "",
                "sZeroRecords": "没有记录",
                "oPaginate": {
                    "sFirst": "最初的",
                    "sLast": "最后的",
                    "sNext": "下一页",
                    "sPrevious": "上一页"
                }
            },
            "pageLength": 10,
            "order": [
                [0, "asc"]
            ],
            "lengthMenu": [
                [10, 25, 50, -1],
                [10, 25, 50, "所有"]
            ],
            "aoColumnDefs": [{
                "bSortable": false,
                "aTargets": [-1]
            }],
            "stateSave": true,
            "ajax":"/notice/noticeList",
            "columns":[
                {"data": "date"},
                {"data": "title"},
                {"data":null,"render":function(data,type,row,meta){
                    var html ="<button class='btn btn-success' ng-click = noticeDelete("+ data['id'] +")>删除</button>"
                    return html;
                },"fnCreatedCell": function (td, cellData, rowData, row, col) {
                    $compile(td)($scope);
                }}
            ]
        });
        $(".dataTables_filter input").attr("placeholder", "输入要搜索的内容...");

        alreadyReady = true;


        var table = $('#tableEmailsList').removeClass('hidden').DataTable();
        table.order(0, 'desc');
        table.draw();
        $('#tableLoading').addClass('hidden');



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
//            $http({
//                method: 'POST',
//                url: '/notice/noticeSave',
//                params: {
//                    'id': $('#title').val(),
//                    'content': $scope.tinymceModel
//                }
//            }).success(function (data) {
//                if (data.message === 'S') {
//                    $('#modalAjax .loader').fadeOut();
//                    $state.go('notice');
//                }
//            }).error(function (data) {
//                $('#modalAjax .modal-body').html('An error occurred while communicating with the server. Please try again.');
//            });
        }

	});


})(angular,$);