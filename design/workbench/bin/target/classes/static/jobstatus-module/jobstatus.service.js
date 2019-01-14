 angular.module('jobstatusModule')
.factory('jobstatusService', function($modal){
 var jobstatusModal ={};
 // job progress bar
 jobstatusModal.openProgressBarModal = function(scope, projectId){
	 $modal.open({
		 backdrop:"static",
		 templateUrl : 'jobstatus-module/jobProgressBarModal.html',//script标签中定义的id
		 controller : 'jobProgressBarCtrl',//modal对应的Controller
		 resolve : {
			 projectId : function(){
				 return projectId;
			 },
			 oriScope : function() {
				 return scope;
			 }
		 }
	 })
 }
 // job history
 jobstatusModal.openHistoryModal = function(scope, projectId){
	 $modal.open({
		 backdrop:"static",
		 templateUrl : 'jobstatus-module/jobHistoryModal.html',
		 controller : 'jobHistoryCtrl',
		 resolve : {
			 projectId : function(){
				 return projectId;
			 }
		 }
	 })
 }

 // job history
 jobstatusModal.openJobUpdateStatusModal = function(scope, projectId){
	$modal.open({
		backdrop:'static',
		templateUrl : 'jobstatus-module/jobUpdateStatusModal.html',
		controller : 'jobUpdateStatusCtrl',
		resolve : {
			projectId : function(){
				return projectId;
			}
		}
	})
}

 return jobstatusModal;
}).
controller('jobProgressBarCtrl', function($http, $scope, $modalInstance, projectId,$timeout,$interval,$websocket,infoDataService,oriScope){
	
	$scope.pbs = [];
	$scope.timer0_100s = [];
	$scope.timer20_100s = [];
	
	var webSocket = infoDataService.getWebSocket();
	
	webSocket.onMessage(function (message) {
		var msg = message.data;
		var jobName = msg.substring(0, msg.indexOf("/"));
		var perValue = msg.substring(msg.indexOf("/") + 1);
		var convertStatusToRunning = false;
		var convertStatusToException = false;
		if (perValue === "status:running") {
			convertStatusToRunning = true;
		}
		if (perValue.indexOf("status:exception") != -1) {
			convertStatusToException = true;
		}
		angular.forEach($scope.pbs, function(item) {
			if (item.jobName === jobName && item.style !== 'progress-bar-warning') {
				if (convertStatusToRunning) {
					item.status = 'running';
					queryJobStartTime(item);
				} else if (convertStatusToException) {
					item.status = 'exception';
					item.style = 'progress-bar-warning';
					item.value = '100';
					item.showLabel = false;
					item.infoLog = perValue.substring(perValue.indexOf("&") + 1);
					item.showInfoLog = true;
				} else {
					var value = perValue;
					// so exception
//					if (item.analysisName === 'SO' && value === '100' && item.value !== '95.00') {
//						item.style = 'progress-bar-warning';
//						item.status = 'stop';
//					} else {
						// job without progress info, imitate 
						// give 20% at the beginning of job start 
						// and promote 0.5% each 0.1 second
						// temporary designed for complexity job(no 
						// progress info)、clone job(temporary lack 
						// of progress info) and so on.
						/*if (value === '100' && item.value === '20') {
							var timer;
							var timer20_100 = {};
							timer20_100.jobName = item.jobName;
							timer20_100.timer = timer;
							$scope.timer20_100s.push(timer20_100);
							
							var progress = 20.00;
							$scope.timer = $interval(function() {
								progress = parseFloat(item.value) + 7.00;
								if (progress >= 100) {
									item.value = '100';
									item.status = 'success';
									item.style = 'progress-bar-success';
//									$interval.cancel($scope.timer);
									angular.forEach($scope.timer20_100s, function(ele) {
										if (ele.jobName === item.jobName) {
											$interval.cancel(ele.timer);
										}
									});
								} else {
									item.value = progress + "";
								}
							}, 200);
						} else*/ if (value === '100' && item.value === '0') {
							var timer;
							var timer0_100 = {};
							timer0_100.jobName = item.jobName;
							timer0_100.timer = timer;
							$scope.timer0_100s.push(timer0_100);
							
							var progress = 0.00;
							timer = $interval(function() {
								progress = parseFloat(item.value) + 7.00;
								if (progress >= 100) {
									item.value = '100';
									item.status = 'success';
									item.style = 'progress-bar-success';
									angular.forEach($scope.timer0_100s, function(ele) {
										if (ele.jobName === item.jobName) {
											$interval.cancel(ele.timer);
										}
									});
								} else {
									item.status = 'running';
									// query job startTime
									if (!item.queriedStartTime) {
										queryJobStartTime(item);
									}
									item.value = progress + "";
								}
							}, 100);
						} else {
							item.value = value;
							if (value > 0) {
								item.status = 'running';
								// query job startTime
								if (!item.queriedStartTime) {
									queryJobStartTime(item);
								}
							}
							if (value === '100') {
								item.status = 'success';
								$timeout(function () {
									item.style = 'progress-bar-success';
								}, 500);
							}
						}
//					}
				}
			}
		});
    });
	
	function queryJobStartTime(item) {
		$http({
			method: 'GET',
			url: './job/startTime',
			params:{
				'jobName': item.jobName
			}
		}).success(
			function (data) {
				item.startTime = data.data;
				item.queriedStartTime = true;
				item.showStartTime = true;
		}).error(
			function (data) {
				console.info('error');
		});
	};
	
//            $scope.gridOptions = {
//            columnDefs: [
//                { field: 'analysisName', displayName: 'Analysis Name' },
//                { field: 'startTime', displayName: 'Start Time' },
//                { field: 'stopTime', displayName: 'Stop Time' },
//                { field: 'codeVersion', displayName: 'Code Version' },
//                { field: 'jobStatus', displayName: 'Job Status' }],
//            enablePagination: true,    //分页
//            paginationPageSizes: [10, 15, 20], //每页显示个数选项  
//            paginationCurrentPage: 1, //当前的页码  
//            paginationPageSize: 10, //每页显示个数  
//            useExternalPagination: true,//是否使用分页按钮
//            paginationTemplate: "<div></div>", //自定义底部分页代码 
//            enableSorting: false,
//            enableVerticalScrollbar: 0,
//            rowTemplate: "<div ng-dblclick=\"grid.appScope.onDblClick(row)\" ng-click=\"grid.appScope.Click(row,element)\"  ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",
//        };

        initProgressBar();
        
        $scope.$on('$destroy',function(){
			if(angular.isDefined($scope.timer)){
				$interval.cancel($scope.timer);
			}
		});


        $scope.onDblClick = function (row) {
            console.info(row.entity);
        }

        function initProgressBar(){
        	$http({
			method: 'GET',
			url: './job/jobProgressBarStatus',
			params:{
				'projectId':projectId
			}
		}).success(
			function (data) {
				angular.forEach(data.data, function(pbItem) {
					var pb = {};
					pb.jobName = pbItem.jobName;
					if (pbItem.incremental) {
						pb.analysisName = pbItem.analysisName + "_INCREMENTAL";
					} else {
						pb.analysisName = pbItem.analysisName;
					}
					pb.userName = pbItem.userName;
					pb.codeVersion = pbItem.codeVersion;
					pb.status = pbItem.status;
					// get the last time progress
					if (typeof(oriScope.pbs) !== "undefined" && oriScope.pbs.length > 0) {
						angular.forEach(oriScope.pbs, function(ele) {
							if (ele.jobName === pb.jobName) {
								pb.value = ele.value;
								if (ele.queriedStartTime) {
									pb.queriedStartTime = true;
									pb.showStartTime = true;
									pb.startTime = ele.startTime;
								} else {
									pb.queriedStartTime = false;
									pb.showStartTime = false;
									pb.startTime = pbItem.startTime;
								}
							}
						});
					} else {
						pb.value = '0';
						pb.queriedStartTime = false;
						pb.showStartTime = false;
						pb.startTime = pbItem.startTime;
					}
				    pb.style = 'progress-bar-info';
				    pb.showLabel = true;
				    pb.striped = true;
				    pb.infoLog = "";
				    pb.showInfoLog = false;
					$scope.pbs.push(pb);
	            });
			}).error(
			function (data) {
				console.info('error');
			});
        };	
    
//        function checkStatus(items){
//            var uptodate = true;
//            angular.forEach(items,function(item){
//                if(item.jobStatus==='NS'||item.jobStatus==='P'){
//                    uptodate = false;
//                }
//            });
//            if(!uptodate){
//                if(!angular.isDefined($scope.timer)){
//    				$scope.timer = $interval(function(){
//					initJobLis();
//				    },10000);
//                }
//            }else{
//                if(angular.isDefined($scope.timer)){
//					$interval.cancel($scope.timer);
//					$scope.timer = undefined;
//                }
//            }
//        }
        
        // $scope.ok = function() {
        //     console.info('ok');
	    // $modalInstance.close();
        // };

        //  $scope.cancel = function() {
        //      console.info('cancel');
        // $modalInstance.dismiss('cancel');
        // }

        $scope.close = function(){
        	oriScope.pbs = $scope.pbs;
        	if(angular.isDefined($scope.timer)){
        		$interval.cancel($scope.timer);
        		$scope.timer = undefined;
        	}            
        	$modalInstance.close();
        }
}).controller('jobHistoryCtrl', function($http, $scope, $modalInstance, projectId,$timeout,$interval,$websocket){
	
	$scope.gridOptions = {
          columnDefs: [
              { field: 'analysisName', displayName: 'Analysis Name',headerTooltip: 'Analysis Name',cellTemplate: '<div title = {{row.entity.analysisName}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.analysisName}}</div>'},
              { field: 'startTime', displayName: 'Start Time' ,headerTooltip: 'Start Time',cellTemplate: '<div title = {{row.entity.startTime}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.startTime}}</div>'},
              { field: 'stopTime', displayName: 'Stop Time' ,headerTooltip: 'Stop Time',cellTemplate: '<div title = {{row.entity.stopTime}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.stopTime}}</div>'},
              { field: 'codeVersion', displayName: 'Code Version' ,headerTooltip: 'Code Version' },
              { field: 'jobStatus', displayName: 'Job Status' ,headerTooltip: 'Job Status'}],
		  enableSorting: false,
		  enableHorizontalScrollbar :0,
          enableVerticalScrollbar: 1,
          rowTemplate: "<div ng-dblclick=\"grid.appScope.onDblClick(row)\" ng-click=\"grid.appScope.Click(row,element)\"  ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",
	};

	initJobList();

	function initJobList(){
		$http({
			method: 'GET',
			url: './job/jobHistory',
			params:{
				'projectId':projectId
			}
		}).success(function (data) {
			$scope.gridOptions.data = data.data;
		}).error(function (data) {
			console.info('error');
		});
	};	
	
	$scope.close = function(){
    	$modalInstance.close();
    }

}).controller('jobUpdateStatusCtrl',function($http, $scope, projectId,$modalInstance,$timeout,$interval,$websocket,infoDataService){
	$scope.updateStatusGridOptions = {
		columnDefs: [
			{ field: 'analysisName', displayName: 'Analysis Name',headerTooltip: 'Analysis Name',cellTemplate: '<div title = {{row.entity.analysisName}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.analysisName}}</div>'},
			// { field: 'startTime', displayName: 'Start Time' ,headerTooltip: 'Start Time',cellTemplate: '<div title = {{row.entity.startTime}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.startTime}}</div>'},
			// { field: 'stopTime', displayName: 'Stop Time' ,headerTooltip: 'Stop Time',cellTemplate: '<div title = {{row.entity.stopTime}} class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.stopTime}}</div>'},
			{ field: 'codeVersion', displayName: 'Code Version' ,headerTooltip: 'Code Version' },
			{ field: 'jobStatus', displayName: 'Job Status' ,headerTooltip: 'Job Status'},
			{ field: 'needUpdate', displayName: 'Need Update' ,headerTooltip: 'Need Update',cellTemplate: '<div class="ui-grid-cell-contents ng-binding ng-scope">{{row.entity.needUpdate == true ?"Y":"N"}}</div>'}],
		enableSorting: false,
		enableHorizontalScrollbar :0,
		enableVerticalScrollbar: 1,
		rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",
  };

  initJobUpdateStatusList();

  function initJobUpdateStatusList(){
	  $http({
		  method: 'GET',
		  url: './job/jobUpdateStatus',
		  params:{
			  'projectId':infoDataService.getId()
		  }
	  }).success(function (data) {
		  $scope.updateStatusGridOptions.data = data.data;
	  }).error(function (data) {
		  console.info('error');
	  });
  };

  $scope.close = function(){
	  $modalInstance.close();
  }
});