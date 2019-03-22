'use strict';
var app = angular.module('costEstimationModule');
app.controller('codeEstimationController', function ($scope, $http, $timeout, infoDataService, $filter, historyUrlService) {
    historyUrlService.setUrlInfo('costEstimation');
    historyUrlService.setClickFlag(true);
    $scope.factor = [];
    $scope.generatedReport = false;
    $scope.reportInfo = true;
    $scope.costRec={};
    if(historyUrlService.getCost()){
        $scope.costRec=historyUrlService.getCost();
        // $scope.costRec.Resource_Needs=0;
        programTable(historyUrlService.getCost());
    }else{
        $http.get('costEstimation-module/cost.json').success(function (data) {
            if (data) {
                var complexity = data[0]['Complexity_Ratio'][0];
                var Median_Code_Size = data[0]['Median_Code_Size'][0];
                var Fixed_Parameters = data[0]['Fixed_Parameters'][0];
                var Project_Summary = data[0]['Project_Summary'][0];
                $scope.costRec.loc = complexity['LOC'];
                $scope.costRec.loop = complexity['LOOP'];
                $scope.costRec.conditionalStatements = complexity['Conditional_Statements'];
                $scope.costRec.tables = complexity['Tables'];
                $scope.costRec.variables = complexity['Variables'];
                $scope.costRec.medianLoc = Median_Code_Size['Median_LOC'];
                $scope.costRec.medianLoops = Median_Code_Size['Median_Loops'];
                $scope.costRec.medianConditions = Median_Code_Size['Median_Condition'];
                $scope.costRec.medianTables = Median_Code_Size['Median_Tables'];
                $scope.costRec.medianVariables = Median_Code_Size['Median_Variables'];
                $scope.costRec.costPoint = Fixed_Parameters['Cost_Point'];
                $scope.costRec.manHour = Fixed_Parameters['Man_Hour'];
                $scope.costRec.availableTimeline = Project_Summary['Available_Timeline'];
                $scope.costRec.availableBudget = Project_Summary['Available_Budget'];
                $scope.costRec.hourlyRate = Project_Summary['Hourly_Rate'];
                $scope.Resource_Needs = Project_Summary['Resource_Needs'];
                $scope.pp = data[0]['Complexity_Ratio'][0];
            }
        });
    }
    function getCostData() {
        var cost = {};
        cost.projectId = infoDataService.getId();
        cost.loc = $scope.costRec.loc;
        cost.loop = $scope.costRec.loop;
        cost.conditionalStatements = $scope.costRec.conditionalStatements;
        cost.tables = $scope.costRec.tables;
        cost.variables = $scope.costRec.variables;
        cost.medianLoc = $scope.costRec.medianLoc;
        cost.medianLoops = $scope.costRec.medianLoops;
        cost.medianConditions = $scope.costRec.medianConditions;
        cost.medianTables = $scope.costRec.medianTables;
        cost.medianVariables = $scope.costRec.medianVariables;
        cost.costPoint = $scope.costRec.costPoint;
        cost.manHour = $scope.costRec.manHour;
        cost.availableTimeline = $scope.costRec.availableTimeline;
        cost.availableBudget = $scope.costRec.availableBudget;
        cost.hourlyRate = $scope.costRec.hourlyRate;
        $scope.cost = cost;
    }

    $scope.paint = function () {
        // 每次点击report的时候，都重新收集页面scope值
        getCostData();
        historyUrlService.setCost($scope.cost);
        $scope.onModel.modelLoading('loading', 'loading');
        programTable($scope.cost);
    };
    function programTable(para){
        $http({
            method: 'POST',
            url: './costEstimation/getCostData',
            data: para
        }).success(function (data) {
            $scope.reportInfo = false;
            $scope.excelDownloadData = data.data;
            $scope.programTable = data.data.programResults;
            $scope.cloneResults = data.data.cloneResults;
            $scope.grandTotal = data.data.grandTotal;
            $scope.withBudget = data.data.withBudget;
            // $scope.load = false;
            $scope.onModel.modelHide();
            // $scope.Resource_Needs = $filter('number')($scope.grandTotal / $scope.Timeline, 2);
            $scope.Resource_Needs=$filter('number')($scope.grandTotal / $scope.costRec.availableTimeline, 2);
            $scope.generatedReport = true;
        })
    }

    $scope.getParagraphs = function (e, pid) {
        var $tr = $(e.target).parents("tr");
        var $i = $(e.target);
        $i.addClass("disnone").siblings().removeClass('disnone');
        $tr.siblings().find('.jian').addClass('disnone');
        $tr.siblings().find('.jia').removeClass('disnone')
        var mouseX = event.clientX + document.body.scrollLeft;//鼠标x位置
        var mouseY = event.clientY + document.body.scrollTop;//鼠标y位置
        $scope.paragraphTable = pid;
        $(".trmore").css('top', mouseY + 12);
        $scope.trshow = true;
        $scope.jian = true;
    }
    $scope.trmoreClose = function () {
        $scope.trshow = false;
        $(".jia").removeClass('disnone');
        $('.jian').addClass('disnone');
    }
    $scope.downloadExcel = function () {
        if ($scope.generatedReport) {
            $http.post("/costEstimation/costExcel", $scope.cost, { responseType: 'arraybuffer' }).success(function (data) {
                var blob = new Blob([data], { type: "application/vnd.ms-excel" });
                var objectUrl = URL.createObjectURL(blob);
                var aForExcel = $("<a id='costLink'><span class='forExcel'>dowload excel</span></a>")
                    .attr("href", objectUrl).attr("download", "CostEstimation.xls");
                $("body").append(aForExcel);
                $(".forExcel").click();
                aForExcel.remove();
            })
        } else {
            $scope.onModel.modelShow('error', 'Report not generated!');
        }
    }
})