(function (angular, $) {
  'use strict';
  var app = angular.module('studentModule', ['ui.bootstrap','studentInfoModule']);
  app.controller('studentController',function ($scope, $state, $compile, $http,$modal,$custom) {
      //	    var data = [];

      var alreadyReady = false; // The ready function is being called twice on page load.
      var initTable = $("#tableEmailsList").DataTable({
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
        "ajax": "/student/studentList",
        "columns": [{
          "data": "id"
        },
        {
          "data": "name"
        },
        {
          "data": "dorm"
        },
        {
          "data": null,
          "render": function (data, type, row, meta) {
            var html = "<button class='btn btn-success' ng-click = showStudentInfo(" + data['id'] + ")>删除</button>"
            return html;
          },
          "fnCreatedCell": function (td, cellData, rowData, row, col) {
            $compile(td)($scope);
          }
        }
        ],
        "rowCallback": function (row, data) {
          $('td:eq(0)', row).on('click', function () {
            $scope.viewStudent(data);
          });
          $('td:eq(1)', row).on('click', function () {
            $scope.viewStudent(data);
          });
        }
      });
      $(".dataTables_filter input").attr("placeholder", "输入要搜索的内容...");

      alreadyReady = true;

      var table = $('#tableEmailsList').removeClass('hidden').DataTable();
      table.order(0, 'asc');
      table.draw();
      $('#tableLoading').addClass('hidden');

      var table = $('#tableEmailsList').DataTable();

      $scope.viewStudent = function (data) {
        // //                var data = table.row(this.parentElement).data();
        //                 $state.go('notice/view', {
        //                     notice: data
        //                 })
        //                 //            openModal("/xx",{},"title",200,5,5  )
      }

      $scope.addStudent = function () {
        $state.go('editor');
      }

      $scope.studentDelete = function (id) {
        $http({
          method: 'GET',
          url: '/student/studentDelete',
          params: {
            'id': id
          }
        }).success(function (data) {
          if (data.message === 'S') {
            initTable.ajax.reload();
          } else {
            console.log(error)
          }
        }).error(function (data) {
          console.log('error')
        });
      }

      $scope.showStudentInfo = function (studentId) { // show missing code
        $custom.openStudentInfoModal(studentId);
      }

    });


})(angular, $);