'use strict';
var app = angular.module('costEstimationModule');
app.controller('codeEstimationController', function ($scope, $http, $timeout, infoDataService, $filter, historyUrlService) {
    historyUrlService.setUrlInfo('costEstimation');
    historyUrlService.setClickFlag(true);
    $scope.factor = [];
    $scope.generatedReport = false;
    $scope.reportInfo = true;
    $scope.costRec = {};
    $scope.manHour=[];
    if (historyUrlService.getCost()) {
        $scope.costRec = historyUrlService.getCost();
        // $scope.costRec.Resource_Needs=0;
        programTable(historyUrlService.getCost());
    } else {
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
                $scope.costRec.teamSize = Project_Summary['Team_Size'];
                $scope.costRec.availableBudget = Project_Summary['Available_Budget'];
                $scope.costRec.hourlyRate = Project_Summary['Hourly_Rate'];
                $scope.Time_Window = Project_Summary['Time_Window'];
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

    $scope.EstimationItems = [
        { name: 'general migration' },  
        { name: 'reporting migration' }
    ]
    $scope.EstimationInit = $scope.EstimationItems[0];

    $scope.paint = function () {
        // 每次点击report的时候，都重新收集页面scope值
        getCostData();
        historyUrlService.setCost($scope.cost);
        $scope.onModel.modelLoading('loading', 'loading');
        programTable($scope.cost);
    };
    function programTable(para) {
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
            $scope.onModel.modelHide();
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
    $scope.manHourFun=function(){
        var sum=0;
        for(var i=0;i<$scope.manHour.length;i++){
            sum+=$scope.manHour[i];
        }
        return sum;
    };

    $scope.costPoit = {
        "inputFiles": 0.5,
        "outputFile": 0.5,
        "fields": 0.1,
        "computatins": 1.2
    };
    $scope.costCalculation = {
        "costPoint": 1.5,
        "hourlyRate": 27
    };
    $scope.constraints = {
        "teamSize": 2,
        "availableBudget": 50000,
        "timeWindow": 45
    };
    $scope.geneRec = {};
    $scope.geneRec.inputFiles = $scope.costPoit['inputFiles'];
    $scope.geneRec.outputFile = $scope.costPoit['outputFile'];
    $scope.geneRec.fields = $scope.costPoit['fields'];
    $scope.geneRec.computatins = $scope.costPoit['computatins'];

    $scope.geneRec.costPoint = $scope.costCalculation['costPoint'];
    $scope.geneRec.hourlyRate = $scope.costCalculation['hourlyRate'];

    $scope.geneRec.teamSize = $scope.constraints['teamSize'];
    $scope.geneRec.availableBudget = $scope.constraints['availableBudget'];
    $scope.geneRec.timeWindow = $scope.constraints['timeWindow'];


    $scope.generalCost1 = [
        { 'reportNo': '1', 'program': 'PROG0001', 'inputFiles': '1', 'outputFile': '4', 'fields': '24', 'computatins': '14' },
        { 'reportNo': '2', 'program': 'PROG0002', 'inputFiles': '1', 'outputFile': '1', 'fields': '16', 'computatins': '8' },
        { 'reportNo': '3', 'program': 'PROG0003', 'inputFiles': '2', 'outputFile': '2', 'fields': '32', 'computatins': '112' },
        { 'reportNo': '4', 'program': 'PROG0004', 'inputFiles': '1', 'outputFile': '3', 'fields': '116', 'computatins': '11' },
        { 'reportNo': '5', 'program': 'PROG0005', 'inputFiles': '1', 'outputFile': '2', 'fields': '0', 'computatins': '33' },
        { 'reportNo': '6', 'program': 'PROG0006', 'inputFiles': '1', 'outputFile': '1', 'fields': '36', 'computatins': '20' },
        { 'reportNo': '7', 'program': 'PROG0007', 'inputFiles': '1', 'outputFile': '13', 'fields': '332', 'computatins': '50' },
        { 'reportNo': '8', 'program': 'PROG0008', 'inputFiles': '1', 'outputFile': '11', 'fields': '277', 'computatins': '41' },
        { 'reportNo': '9', 'program': 'PROG0009', 'inputFiles': '1', 'outputFile': '4', 'fields': '587', 'computatins': '8' },
        { 'reportNo': '10', 'program': 'PROG0010', 'inputFiles': '1', 'outputFile': '5', 'fields': '612', 'computatins': '12' },
        { 'reportNo': '11', 'program': 'PROG0011', 'inputFiles': '1', 'outputFile': '2', 'fields': '76', 'computatins': '14' },
        { 'reportNo': '12', 'program': 'PROG0012', 'inputFiles': '3', 'outputFile': '2', 'fields': '372', 'computatins': '102' },
        { 'reportNo': '13', 'program': 'PROG0013', 'inputFiles': '3', 'outputFile': '1', 'fields': '90', 'computatins': '29' },
        { 'reportNo': '14', 'program': 'PROG0014', 'inputFiles': '3', 'outputFile': '1', 'fields': '91', 'computatins': '35' },
        { 'reportNo': '15', 'program': 'PROG0015', 'inputFiles': '1', 'outputFile': '1', 'fields': '29', 'computatins': '22' },
        { 'reportNo': '16', 'program': 'PROG0016', 'inputFiles': '1', 'outputFile': '2', 'fields': '97', 'computatins': '52' }
    ];
    $scope.generalCost2 = [
        { 'cloneGroup': '1', 'paragrapfNo': '2', 'totalLoc': '8', 'DuplicatedLoc': '8', 'costPoint': '1' },
        { 'cloneGroup': '2', 'paragrapfNo': '2', 'totalLoc': '42', 'DuplicatedLoc': '42', 'costPoint': '1' },
        { 'cloneGroup': '3', 'paragrapfNo': '2', 'totalLoc': '66', 'DuplicatedLoc': '52', 'costPoint': '0.4' },
        { 'cloneGroup': '4', 'paragrapfNo': '2', 'totalLoc': '112', 'DuplicatedLoc': '112', 'costPoint': '2' },
        { 'cloneGroup': '5', 'paragrapfNo': '2', 'totalLoc': '34', 'DuplicatedLoc': '20', 'costPoint': '0.3' },
        { 'cloneGroup': '6', 'paragrapfNo': '2', 'totalLoc': '270', 'DuplicatedLoc': '270', 'costPoint': '5' },
        { 'cloneGroup': '7', 'paragrapfNo': '2', 'totalLoc': '34', 'DuplicatedLoc': '34', 'costPoint': '1' },
        { 'cloneGroup': '8', 'paragrapfNo': '2', 'totalLoc': '212', 'DuplicatedLoc': '204', 'costPoint': '3.6' },
        { 'cloneGroup': '9', 'paragrapfNo': '2', 'totalLoc': '12', 'DuplicatedLoc': '8', 'costPoint': '1' },
        { 'cloneGroup': '10', 'paragrapfNo': '2', 'totalLoc': '108', 'DuplicatedLoc': '102', 'costPoint': '0.8' },
        { 'cloneGroup': '11', 'paragrapfNo': '2', 'totalLoc': '48', 'DuplicatedLoc': '40', 'costPoint': '0.6' },
        { 'cloneGroup': '12', 'paragrapfNo': '2', 'totalLoc': '10', 'DuplicatedLoc': '10', 'costPoint': '1' },
        { 'cloneGroup': '13', 'paragrapfNo': '3', 'totalLoc': '3', 'DuplicatedLoc': '3', 'costPoint': '1' },
        { 'cloneGroup': '14', 'paragrapfNo': '2', 'totalLoc': '32', 'DuplicatedLoc': '32', 'costPoint': '1' },
        { 'cloneGroup': '15', 'paragrapfNo': '2', 'totalLoc': '8', 'DuplicatedLoc': '8', 'costPoint': '1' },
        { 'cloneGroup': '16', 'paragrapfNo': '3', 'totalLoc': '36', 'DuplicatedLoc': '36', 'costPoint': '1' },
        { 'cloneGroup': '17', 'paragrapfNo': '2', 'totalLoc': '20', 'DuplicatedLoc': '20', 'costPoint': '1' },
        { 'cloneGroup': '18', 'paragrapfNo': '2', 'totalLoc': '30', 'DuplicatedLoc': '30', 'costPoint': '1' },
        { 'cloneGroup': '19', 'paragrapfNo': '2', 'totalLoc': '4', 'DuplicatedLoc': '4', 'costPoint': '1' },
        { 'cloneGroup': '20', 'paragrapfNo': '2', 'totalLoc': '114', 'DuplicatedLoc': '100', 'costPoint': '0.6' },
        { 'cloneGroup': '21', 'paragrapfNo': '3', 'totalLoc': '15', 'DuplicatedLoc': '15', 'costPoint': '1' },
        { 'cloneGroup': '22', 'paragrapfNo': '3', 'totalLoc': '6', 'DuplicatedLoc': '6', 'costPoint': '1' },
        { 'cloneGroup': '23', 'paragrapfNo': '4', 'totalLoc': '80', 'DuplicatedLoc': '72', 'costPoint': '0.6' },
        { 'cloneGroup': '24', 'paragrapfNo': '2', 'totalLoc': '33', 'DuplicatedLoc': '26', 'costPoint': '0.8' },
        { 'cloneGroup': '25', 'paragrapfNo': '2', 'totalLoc': '50', 'DuplicatedLoc': '50', 'costPoint': '1' },
        { 'cloneGroup': '26', 'paragrapfNo': '2', 'totalLoc': '12', 'DuplicatedLoc': '12', 'costPoint': '1' },
        { 'cloneGroup': '27', 'paragrapfNo': '2', 'totalLoc': '34', 'DuplicatedLoc': '30', 'costPoint': '1' },
        { 'cloneGroup': '28', 'paragrapfNo': '2', 'totalLoc': '56', 'DuplicatedLoc': '50', 'costPoint': '0.8' },
        { 'cloneGroup': '29', 'paragrapfNo': '2', 'totalLoc': '72', 'DuplicatedLoc': '72', 'costPoint': '1' },
        { 'cloneGroup': '30', 'paragrapfNo': '2', 'totalLoc': '6', 'DuplicatedLoc': '6', 'costPoint': '1' },
        { 'cloneGroup': '31', 'paragrapfNo': '8', 'totalLoc': '36', 'DuplicatedLoc': '28', 'costPoint': '0.6' },
        { 'cloneGroup': '32', 'paragrapfNo': '2', 'totalLoc': '56', 'DuplicatedLoc': '56', 'costPoint': '1' },
        { 'cloneGroup': '33', 'paragrapfNo': '2', 'totalLoc': '84', 'DuplicatedLoc': '84', 'costPoint': '1.6' },
        { 'cloneGroup': '34', 'paragrapfNo': '2', 'totalLoc': '72', 'DuplicatedLoc': '72', 'costPoint': '1' },
        { 'cloneGroup': '35', 'paragrapfNo': '2', 'totalLoc': '18', 'DuplicatedLoc': '18', 'costPoint': '1' },
        { 'cloneGroup': '36', 'paragrapfNo': '2', 'totalLoc': '156', 'DuplicatedLoc': '148', 'costPoint': '2.1' },
        { 'cloneGroup': '37', 'paragrapfNo': '2', 'totalLoc': '98', 'DuplicatedLoc': '98', 'costPoint': '2' },
        { 'cloneGroup': '38', 'paragrapfNo': '2', 'totalLoc': '110', 'DuplicatedLoc': '110', 'costPoint': '2' },
        { 'cloneGroup': '39', 'paragrapfNo': '2', 'totalLoc': '94', 'DuplicatedLoc': '88', 'costPoint': '0.4' },
        { 'cloneGroup': '40', 'paragrapfNo': '2', 'totalLoc': '70', 'DuplicatedLoc': '70', 'costPoint': '1' },
        { 'cloneGroup': '41', 'paragrapfNo': '3', 'totalLoc': '87', 'DuplicatedLoc': '60', 'costPoint': '1' },
        { 'cloneGroup': '42', 'paragrapfNo': '2', 'totalLoc': '24', 'DuplicatedLoc': '24', 'costPoint': '1' },
        { 'cloneGroup': '43', 'paragrapfNo': '4', 'totalLoc': '116', 'DuplicatedLoc': '116', 'costPoint': '2' },
        { 'cloneGroup': '44', 'paragrapfNo': '2', 'totalLoc': '12', 'DuplicatedLoc': '12', 'costPoint': '1' },
        { 'cloneGroup': '45', 'paragrapfNo': '2', 'totalLoc': '178', 'DuplicatedLoc': '156', 'costPoint': '1.2' },
        { 'cloneGroup': '46', 'paragrapfNo': '4', 'totalLoc': '148', 'DuplicatedLoc': '132', 'costPoint': '1.6' },
        { 'cloneGroup': '47', 'paragrapfNo': '2', 'totalLoc': '6', 'DuplicatedLoc': '6', 'costPoint': '1' },
        { 'cloneGroup': '48', 'paragrapfNo': '4', 'totalLoc': '148', 'DuplicatedLoc': '124', 'costPoint': '1.4' },
        { 'cloneGroup': '49', 'paragrapfNo': '2', 'totalLoc': '50', 'DuplicatedLoc': '50', 'costPoint': '1' },
        { 'cloneGroup': '50', 'paragrapfNo': '3', 'totalLoc': '63', 'DuplicatedLoc': '48', 'costPoint': '0.6' },
        { 'cloneGroup': '51', 'paragrapfNo': '2', 'totalLoc': '22', 'DuplicatedLoc': '22', 'costPoint': '1' },
        { 'cloneGroup': '52', 'paragrapfNo': '2', 'totalLoc': '44', 'DuplicatedLoc': '44', 'costPoint': '1' },
        { 'cloneGroup': '53', 'paragrapfNo': '2', 'totalLoc': '40', 'DuplicatedLoc': '40', 'costPoint': '1' },
        { 'cloneGroup': '54', 'paragrapfNo': '3', 'totalLoc': '66', 'DuplicatedLoc': '56', 'costPoint': '0.8' },
        { 'cloneGroup': '55', 'paragrapfNo': '2', 'totalLoc': '212', 'DuplicatedLoc': '158', 'costPoint': '2.1' },
        { 'cloneGroup': '56', 'paragrapfNo': '2', 'totalLoc': '40', 'DuplicatedLoc': '40', 'costPoint': '1' },
        { 'cloneGroup': '57', 'paragrapfNo': '2', 'totalLoc': '50', 'DuplicatedLoc': '50', 'costPoint': '1' },
        { 'cloneGroup': '58', 'paragrapfNo': '2', 'totalLoc': '244', 'DuplicatedLoc': '194', 'costPoint': '2.4' },
        { 'cloneGroup': '59', 'paragrapfNo': '2', 'totalLoc': '48', 'DuplicatedLoc': '48', 'costPoint': '1' },
        { 'cloneGroup': '60', 'paragrapfNo': '2', 'totalLoc': '18', 'DuplicatedLoc': '18', 'costPoint': '1' },
        { 'cloneGroup': '61', 'paragrapfNo': '2', 'totalLoc': '40', 'DuplicatedLoc': '40', 'costPoint': '1' },
        { 'cloneGroup': '62', 'paragrapfNo': '2', 'totalLoc': '28', 'DuplicatedLoc': '28', 'costPoint': '1' }
    ];




})