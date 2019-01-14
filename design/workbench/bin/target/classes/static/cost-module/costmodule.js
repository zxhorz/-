var app = angular.module('costmodule', ['chart.js', 'angularjs-dropdown-multiselect']);
app.controller('codemoduleController', function ($scope, $http, $timeout) {
    $scope.factor = [];
    $scope.load=true;
    $http.get('cost-module/cost_factor.json').success(function (data) {
        console.info(data);
        if (data) {
            var complexity = data[0]['Complexity_Ratio'][0];
            var Median_Code_Size = data[0]['Median_Code_Size'][0];
            var Fixed_Parameters = data[0]['Fixed_Parameters'][0];
            var Project_Summary = data[0]['Project_Summary'][0];
            $scope.LOC = complexity['LOC'];
            $scope.Loops = complexity['LOOP'];
            $scope.cond = complexity['Conditional_Statements'];
            $scope.Tables = complexity['Tables'];
            $scope.Variables = complexity['Variables'];
            $scope.M_LOC = Median_Code_Size['Median_LOC'];
            $scope.M_Loops = Median_Code_Size['Median_Loops'];
            $scope.M_cond = Median_Code_Size['Median_Condition'];
            $scope.M_Tables = Median_Code_Size['Median_Tables'];
            $scope.M_Variables = Median_Code_Size['Median_Variables'];
            $scope.pre_loc = Fixed_Parameters['Cost_Point'];
            $scope.Man_hour_per = Fixed_Parameters['Man_Hour'];
            $scope.Timeline = Project_Summary['Available_Timeline'];
            $scope.Budget = Project_Summary['Available_Budget'];
            $scope.Hourly_Rate = Project_Summary['Hourly_Rate'];
            $scope.Resource_Needs = Project_Summary['Resource_Needs'];
            $scope.pp = data[0]['Complexity_Ratio'][0];
        }
    });
    $scope.listProgram = function () {
        var path = './resource/business_case_1/output/cost/cost_summary.csv'
        $http({
            method: 'POST',
            url: './cost-estimation/summary',
            data: $.param({ 'path': path }),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
            console.info('test');
            console.info(data);
            $scope.programTable = data;
            $scope.load=false;
        })
    };
    $scope.closetr = function (e) {
         var $tr = $(e.target).parents("tr");
        var $i=$(e.target);
        $scope.trshow=false;
        $scope.jian=false;
        $i.addClass("disnone").siblings().removeClass('disnone');
        // $tr.siblings().find('.jian').addClass('disnone').find('.jia').removeClass('disnone');
    }
    $scope.trmoreClose=function(){
        $scope.trshow=false;
        $(".jia").removeClass('disnone');
        $('.jian').addClass('disnone');
        console.log($scope.comRatio);
    }
    $scope.getParagraphs = function (e, pid) {
        var $tr = $(e.target).parents("tr");
        var $i=$(e.target);
        $i.addClass("disnone").siblings().removeClass('disnone');
        $tr.siblings().find('.jian').addClass('disnone');
        $tr.siblings().find('.jia').removeClass('disnone')
        var mouseX = event.clientX + document.body.scrollLeft;//鼠标x位置
        var mouseY = event.clientY + document.body.scrollTop;//鼠标y位置
        var path = './resource/business_case_1/output/cost/' + pid + '.csv'
        $http({
            method: 'POST',
            url: './cost-estimation/detail',
            data: $.param({ 'path': path }),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
            console.info('test');
            console.log(data);
            $scope.paragraphTable = data;
            $(".trmore").css('top',mouseY+13);
            $scope.trshow=true;
            $scope.jian=true;
        })

    }
    $scope.listProgram();
})