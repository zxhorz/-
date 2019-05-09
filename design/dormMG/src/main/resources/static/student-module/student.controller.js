(function (angular, $) {
    'use strict';
    var app = angular.module('studentModule', ['ui.bootstrap', 'ngFileUpload']);
    app.controller('studentController', function ($scope, $state, $compile, $http, $modal, $stateParams, $rootScope,$timeout, Upload) {
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
                    "data": "studentClass"
                },
                {
                    "data": "pos"
                },
                {
                    "data": null,
                    "render": function (data, type, row, meta) {
                        if ($rootScope.isAdmin) {
                            var html = "<button class='btn btn-success' ng-click = showStudentInfo(" + data['id'] + ") ng-show = 'isAdmin'>查看信息</button><button class='btn btn-success' ng-click = studentDelete(" + data['id'] + ") ng-show = 'isAdmin'>删除</button>"
                            return html;
                        } else {
                            return "";
                        }
                    },
                    "fnCreatedCell": function (td, cellData, rowData, row, col) {
                        $compile(td)($scope);
                    }
                }
            ],
            "rowCallback": function (row, data) {
                //				$('td:eq(0)', row).on('click', function () {
                //					$scope.viewStudent(data);
                //				});
                //				$('td:eq(1)', row).on('click', function () {
                //					$scope.viewStudent(data);
                //				});
            }
        });
        $(".dataTables_filter input").attr("placeholder", "输入要搜索的内容...");


        alreadyReady = true;

        var table = $('#tableEmailsList').removeClass('hidden').DataTable();
        table.order(0, 'asc');
        table.search(search).draw();
        $('#tableLoading').addClass('hidden');

        var table = $('#tableEmailsList').DataTable();

        //		$scope.viewStudent = function (data) {
        //			// //                var data = table.row(this.parentElement).data();
        //			//                 $state.go('notice/view', {
        //			//                     notice: data
        //			//                 })
        //			//                 //            openModal("/xx",{},"title",200,5,5  )
        //		}

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
                    $timeout(function () {
                        $state.reload();
                    }, 1500)
                } else {
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
                    url: 'student/importStudents',
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
                            url: 'student/downloadFailedImport',
                            data: {}
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
        };

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
                    console.log('error')
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

        function downloadByFormPost(options) {
            var config = $.extend(true, {
                method: 'post'
            }, options);
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


    }).controller('studentInfoCtrl', function ($scope, $http, $state, $rootScope, $modalInstance, $timeout, studentId) {
        $scope.title = "学生信息";
        $http({
            method: 'GET',
            url: '/dorm/availableDormList',
        }).success(function (data) {
            $scope.dorms = data.data;

            $http({
                method: 'GET',
                url: '/myInfo/myInfoGet',
                params: {
                    'id': studentId
                }
            }).success(function (data) {
                if (data.message === 'S') {
                    $scope.dorms.unshift({
                        'id': data.data.dorm
                    });
                    $scope.student = data.data;
                }
            }).error(function (data) {

            });
        }).error(function (data) {
            console.log("error")
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
                } else {

                }
            }).error(function (data) {});
        }

        $scope.close = function () {
            $modalInstance.dismiss();
        }
    }).controller('studentAddCtrl', function ($scope, $http, $state, $rootScope, $modalInstance, $timeout) {
        $scope.title = "添加学生";
        $scope.student = {};

        $http({
            method: 'GET',
            url: '/dorm/availableDormList',
        }).success(function (data) {
            $scope.dorms = data.data;
            $scope.student.dorm = data.data[0].id;
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
            $modalInstance.dismiss();
        }
    });

})(angular, $);