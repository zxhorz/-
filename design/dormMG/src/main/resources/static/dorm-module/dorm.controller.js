(function (angular, $) {
	'use strict';
	var app = angular.module('dormModule', ['ngFileUpload'])
		.controller('dormController', function ($scope, $state, $compile, $http, $modal,$timeout,Upload) {
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
				"ajax": "/dorm/dormList",
				"columns": [{
						"data": "id"
					},
					{
						"data": null,
						"render": function (data, type, row, meta) {
							var html = data['volume'] - data['remain'];
							html = "<a href='#' ng-click='openDorm($event)'>" + html + " </a>"
							return html;
						},
						"fnCreatedCell": function (td, cellData, rowData, row, col) {
							$compile(td)($scope);
						}
					},
					{
						"data": "remain"
					},
					{
						"data": null,
						"render": function (data, type, row, meta) {
							var html = "<button class='btn btn-success' ng-click = dormDelete(" + data['id'] + ") ng-show = 'isAdmin'>删除</button>"
							return html;
						},
						"fnCreatedCell": function (td, cellData, rowData, row, col) {
							$compile(td)($scope);
						}
					}
				],
				"rowCallback": function (row, data) {
					//					$('td:eq(0)', row).on('click', function () {
					//						$scope.viewDorm(data);
					//					});
					//					$('td:eq(1)', row).on('click', function () {
					//						$scope.viewDorm(data);
					//					});
				}
			});
			$(".dataTables_filter input").attr("placeholder", "输入要搜索的内容...");
			$(".dataTables_filter input").val("");

			alreadyReady = true;

			var table = $('#tableEmailsList').removeClass('hidden').DataTable();
			table.order(0, 'asc');
			table.search("").draw();
			$('#tableLoading').addClass('hidden');

			var table = $('#tableEmailsList').DataTable();

			$scope.openDorm = function ($event) {
				//			    var data = table.row($event.target.parentElement).data();
				// //                var data = table.row(this.parentElement).data();
				//                 $state.go('notice/view', {
				//                     notice: data
				//                 })
				//                 //            openModal("/xx",{},"title",200,5,5  )
			}

			$scope.addDorm = function () {
    			var modal = $modal.open({
    				backdrop: 'static',
    				templateUrl: 'dorm-module/dorm.add.html', //script标签中定义的id
    				controller: 'dormAddCtrl', //modal对应的Controller
    				size: 'md'
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

            $scope.upload = function (file) {
            $scope.onModel.modelLoading('loading', '导入中');
            Upload.upload({
                    url: 'dorm/importDorms',
                    file: file
                })
                .progress(function (evt) {
//                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
//                    console.log('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                })
                .success(function (data) {
                    if (data.message === 'S') {
                        $scope.onModel.modelShow('success', '导入成功');
                        $timeout(function () {
                            $state.reload();
                        }, 3000)
                    } else if (data.message === 'W') {
                        $scope.onModel.modelShow('warning', data.data);
                        downloadByFormPost({
                            url: 'dorm/downloadFailedImport',
                            data: {
                            }
                        });
                        $timeout(function () {
                            $state.reload();
                        }, 3000)
                    } else {
                        $scope.onModel.modelShow('error', data.data);
                    }
                })
                .error(function (data) {
                    console.log('error status: ' + status);
                    $scope.onModel.modelHide();
                })
            //                .success(function (data, status, headers, config) {
            //                    console.log('file ' + config.file.name + 'uploaded. Response: ' + data);
            //                })
            //                .error(function (data, status, headers, config) {
            //                    console.log('error status: ' + status);
            //                })
        }

			$scope.dormDelete = function (id) {
				$http({
					method: 'GET',
					url: '/dorm/dormDelete',
					params: {
						'id': id
					}
				}).success(function (data) {
					if (data.message === 'S') {
						initTable.ajax.reload();
					} else {
						$scope.onModel.modelShow('error', data.data);
					}
				}).error(function (data) {
					$scope.onModel.modelShow('error');
				});
			}

			$scope.openDorm = function ($event) {
				var data = table.row($event.target.parentElement.parentElement).data();
				$modal.open({
					backdrop: 'static',
					templateUrl: 'dorm-module/dorm.student.html',
					controller: 'dormStudentCtrl',
					resolve: {
						students: function () {
							return data['students']
						}
					}
				})
			}

            function downloadByFormPost(options) {
                var config = $.extend(true, { method: 'post' }, options);
                var $iframe = $('<iframe id="down-file-iframe" />');
                var $form = $('<form target="down-file-iframe" method="post" />');
                $form.attr('action', config.url);
                $form.attr('target', '');
                for (var key in config.data) {
                    $form.append('<input type="hidden" name="' + key + '" value="' + config.data[key] + '" />');
                }
                $iframe.append($form);
                $(document.body).append($iframe);
                $form[0].submit();
                $iframe.remove();
            }

		}).controller('dormStudentCtrl', function ($scope, $state, $modalInstance, $http, students) {
		    $scope.title = "寝室成员";

			$scope.switchToStudent = function (id) {
				$state.go('student', {
					search: id
				});
			}

			$scope.close = function () {
				$modalInstance.close();
			}
			if (students) {
				$scope.students = students;
			}
		}).controller('dormAddCtrl', function ($scope, $state, $modalInstance, $http) {
        $scope.title = "添加寝室";

		$scope.submitForm = function () {
			$http({
				method: 'POST',
				url: '/dorm/dormAdd',
				data: $.param($scope.dorm),
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
			$modalInstance.dismiss();
		}
		});



})(angular, $);