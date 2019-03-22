angular.module('joblistModule')
    .factory('joblistService', function ($modal, infoDataService) {
        var joblistModal = {};
        joblistModal.openModal = function (projectId, analysisname) {
            $modal.open({
                backdrop: 'static',
                templateUrl: 'joblist-module/joblist.html',//script标签中定义的id
                controller: 'joblistCtrl',//modal对应的Controller
                resolve: {
                    projectId: function () {
                        return projectId;
                    },
                    analysisname: function () {
                        return analysisname;
                    }
                }
            })
        }
        return joblistModal;
    }).
    controller('joblistCtrl', function ($http, $scope, $state, $modalInstance, projectId, analysisname, infoDataService,jobstatusService) {
        // $scope.jobSelected = false;

        $http({
            method: 'GET',
            url: './job/jobdependency',
            params: {
                "projectId": projectId,
                "jobName": analysisname
            }
        }).success(function (data) {
            if (data.code === 'ACK') {
                $scope.items = data.data;
            } else {
                $scope.items = [];
            }
        }).error(function (data) {
            $scope.items = [];
            console.info(data);
        });

        $scope.generateNow = function () {
            infoDataService.setFromPage('MENU');
            addToSelectedDatSet();
            generateJob();
            console.info('generate now');
            $modalInstance.close();
        };

        $scope.cancel = function () {
            console.info('cancel');
            infoDataService.setFromPage('INDEX');
            $modalInstance.dismiss('cancel');
        }

        $scope.close = function () {
            infoDataService.setFromPage('INDEX');
            $modalInstance.close();
        }

        function addToSelectedDatSet() {
            var dependencyInfo = {};
            dependencyInfo.projectId = projectId;
            dependencyInfo.selectedName = [];
            angular.forEach($scope.items, function (item) {
                dependencyInfo.selectedName.push(item.analysisName);
            });
            $http({
                method: 'POST',
                url: './job/updateSelectedType',
                data: dependencyInfo
            }).success(function (data) {
                console.info(data.message);
            }).error(function (data) {
                console.info(data.message);
            });
        }

        function generateJob() {
            var dependencyInfo = {};
            dependencyInfo.projectId = projectId;
            dependencyInfo.selectedName = [];
            angular.forEach($scope.items, function (item) {
                dependencyInfo.selectedName.push(item.id);
            });
            $http({
                method: 'POST',
                url: './job/generateJob',
                data: dependencyInfo
            }).success(function (data) {
                console.info(data.message);
                $state.reload('project/summary');
                jobstatusService.openProgressBarModal($scope, projectId);
            }).error(function (data) {
                console.info(data.message);
                $state.reload('project/summary');
            });
        }
    });