(function (angular, $) {
	'use strict';
	var app = angular.module('studentModule', ['ui.bootstrap']);
	app.controller('studentController', function ($scope, $state, $compile, $http, $modal, $stateParams,$timeout) {
		//	    var data = [];
		var search = $stateParams.search;
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
					"data": "pos"
				},
				{
					"data": null,
					"render": function (data, type, row, meta) {
						var html = "<button class='btn btn-success' ng-click = showStudentInfo(" + data['id'] + ")>查看信息</button>"
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
		table.search(search).draw();
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
			var modal = $modal.open({
				backdrop: 'static',
				templateUrl: 'student-module/student.add.html', //script标签中定义的id
				controller: 'studentAddCtrl', //modal对应的Controller
				size: 'lg'
			});

			modal.result.then(function (result) {
            	if (result == 'S') {
            		$scope.onModel.modelShow('success', '添加成功');
            		$timeout(function() {
                        $state.reload();
                    },1500)
            	}else{
            		$scope.onModel.modelShow('error', result);
//            		$timeout(function() {
//                        $state.reload();
//                    },1500)
            	}
            }, function (reason) {
            	//        	$state.reload();
            })
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
			//            $scope.onModel.modelShow('success','修改成功');
			//        $custom.openStudentInfoModal(studentId,$scope);
			var modal = $modal.open({
				backdrop: 'static',
				templateUrl: 'student-info-module/student.info.html', //script标签中定义的id
				controller: 'studentInfoCtrl', //modal对应的Controller
				size: 'lg',
				resolve: {
					studentId: function () {
						return studentId;
					}
				}
			});
			modal.result.then(function (result) {
				if (result == 'S') {
					$scope.onModel.modelShow('success', '修改成功')
				}
			}, function (reason) {
				//        	$state.reload();
			})
		}


	}).controller('studentInfoCtrl', function ($scope, $http, $state, $rootScope, $modalInstance, $timeout, studentId) {
		$http({
			method: 'GET',
			url: '/dorm/dormList',
		}).success(function (data) {
			$scope.dorms = data.aaData;
		}).error(function (data) {
			console.log("error")
		});

		$http({
			method: 'GET',
			url: '/myInfo/myInfoGet',
			params: {
				'id': studentId
			}
		}).success(function (data) {
			if (data.message === 'S') {
				$scope.student = data.data;
			}
		}).error(function (data) {

		});

		$scope.submitForm = function () {
			$http({
				method: 'POST',
				url: '/myInfo/myInfoSave',
				data: $.param($scope.student),
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				}
			}).success(function (data) {
				if (data.message === 'S') {
					$modalInstance.close('S')
				} else {}
			}).error(function (data) {});
		}

		$scope.close = function () {
			$modalInstance.close();
		}
	}).controller('studentAddCtrl', function ($scope, $http, $state, $rootScope, $modalInstance, $timeout) {
	    $scope.student = {};

		$http({
			method: 'GET',
			url: '/dorm/dormList',
		}).success(function (data) {
			$scope.dorms = data.aaData;
			$scope.student.dorm = data.aaData[0].id;
		}).error(function (data) {
			console.log("error")
		});

		$scope.submitForm = function () {
			$http({
				method: 'POST',
				url: '/student/studentAdd',
				data: $.param($scope.student),
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				}
			}).success(function (data) {
				if (data.message === 'S') {
					$modalInstance.close('S')
				} else {
				    $modalInstance.close(data.data)
				}
			}).error(function (data) {});
		}

		$scope.close = function () {
			$modalInstance.close();
		}
	});

})(angular, $);