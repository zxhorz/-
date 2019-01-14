'use strict';

//查询neo4j，并生成svg图
angular.module('d3Module').directive('d3Directive', function () {

    return {
        restrict: 'EA',
        scope: true,
        templateUrl: 'd3-module/graphShow.html',
        replace: false,
        controller: d3Controller,
        link: function (scope, element, attrs) {
            // 判断本table是在tab中还是grid中
            var tableContainInTab = attrs.parentistab;

            // 如果本table包含在tab中，监听父scope中$scope.currentTitle的变化（即tab发生切换）
            // 以更新table的数据
            if (tableContainInTab) {
                scope.$watch('currentTitle', function (newValue, oldValue) {
                    scope.openTab(newValue);
                });
            }
        }
    };

    function d3Controller($scope, $http, $location, $attrs, d3ForceShow) {
        $scope.isShow = 'none';
        $scope.buttonName = 'show';
        $scope.cypherstmt = '';

        //固定tabtitle，svg只在d3graph中生成
        if(typeof $scope.tabtitle !=='undefined'){
            $scope.cypherId = 'd3graph';
        }

        //调用neo4j，获取数据
        $scope.getNode = function () {
            if ($scope.cypherstmt) {
                //输入cypher语句有值时
                $http({
                    method: 'GET',
                    url: './query/getNodes',
                    params: { cypherStmt: $scope.cypherstmt }
                }).success(function (data) {
                    $scope.records = data.data.records;
                    //页面如果已经绘制，则在当前页面修改
                    //如果是新的tab，则重新创建d3对象
                    if(!$scope.forceShow||$scope.forceShow.randomId!==$scope.cypherId){
                        $scope.forceShow = new d3ForceShow($scope.cypherId);
                    }
                    $scope.forceShow.refresh(data);
                    $scope.cypherstmt = '';
                }).error(function () {
                });
            } else {
                //初始化时创建d3对象
                $scope.forceShow = new d3ForceShow($scope.cypherId);
                $scope.records = [];
                $scope.isShow = 'none';
                $scope.buttonName = 'show';
            }
        };

        //获取已经保存的cypher语句
        $scope.getCypher = function(){
             $http.get('/d3-module/cypher.json').success(function(data){
                $scope.cypherLists = data.cypher_list;
             }).error(function(data){
                 console.info(data);
             });
        };

        //是否利用table文本形式展示获取的数据
        $scope.show = function () {
            if ($scope.buttonName === 'show') {
                $scope.isShow = '';
                $scope.buttonName = 'hide';
            } else {
                $scope.isShow = 'none';
                $scope.buttonName = 'show';
            }
        };

        //打开所有保存的tab窗口
        $scope.openTab = function(tabName){
            $http.get('/window-module/window.json').success(function(data){
                for(var name in data.neo4j_window){
                    if(data.neo4j_window[name].title===tabName){
                        $scope.cypherstmt = data.neo4j_window[name].content.text;
                        break;
                    }
                }
                $scope.getNode();
            });
        };

        //下拉框cypher选择
        $scope.getChange = function(){
            if($scope.selectedCypher.content){
                $scope.cypherstmt = $scope.selectedCypher.content;
            }
        };

        //初始化自动获取已经预存的cypher语句
        $scope.getCypher();
    };

});
