'use strict';

angular.module('autoTagModule').directive('autoTagDirective', function ($timeout) {
    return {
        restrict: 'EA',
        scope: false,
        templateUrl: 'autoTag-module/autoTag.html',
        replace: false,
        controller: autoTagController,
        link: function (scope, element) {
            $timeout(function () {
                scope.recommandTags();
            }, 1000)

            element.on('initAutoTag', function () {
                $timeout(function () {
                    angular.element('#autoTagSelect').triggerHandler('change');
                });
            })
        }
    };

    function autoTagController($scope, $http, infoDataService, $timeout) {
        // 变量的初始化
        $scope.outputPath = '';
        $scope.recommandedTags = [];
        $scope.serverIp = infoDataService.getServerIp();
        $scope.boltUri = infoDataService.getBoltUri();
        $scope.autoTagPath = infoDataService.getAutoTagPath();

        $scope.confirmTags = function () {
            // 调用tag-directive中的confirmAutoTags()方法
            $scope.confirmAutoTags();
        }

        $scope.recommandTags = function () {
            if ($scope.showTag === true) {
                $scope.recommandedTags = [];
                $scope.autoTagModel = [];
                var requestMethod = 'autoTag';
                var organization = 'test', businessDomain = 'test', system = 'test', type = typeMapping(infoDataService.getTagType());
                // 通过server，调用autoTag
                $http({
                    method: 'POST',
                    url: 'http://' + $scope.serverIp + ':5000/' + requestMethod + '/',
                    data: {
                        'projectId': infoDataService.getId(),
                        'params': handleParams(infoDataService.getSelectedNames(), type),
                        'neo4jUri': $scope.boltUri,
                        'outputPath': $scope.autoTagPath,
                        'organization': organization,
                        'businessDomain': businessDomain,
                        'system': system
                    },
                    headers: { 'Content-Type': 'application/json' }
                }).success(function () {
                    getRecommandTags($scope.autoTagPath);
                }).error(function (data) {
                    console.log('error');
                });
            }
        }

        function typeMapping(type) {
            var finalType = 'Program';
            type = type.toLocaleUpperCase();
            if (type === 'PROGRAM') {
                finalType = 'Program';
            } else if (type === 'COPYBOOK') {
                finalType = 'Copybook';
            } else if (type === 'JOB' || type === 'PROC') {
                finalType = 'JclFile'
            }
            return finalType;
        }

        $scope.autoTagFeedback = function (tags) {
            var type = infoDataService.getTagType();
            var projectId = infoDataService.getId();
            var name = infoDataService.getSelectedNames()[0];

            $http({
                method: 'GET',
                url: './autoTag/feedback',
                params: {
                    'projectId': projectId, 'name': name, 'type': type, 'tag': tags.join(' ')
                }
            }).success(function () {
                // do nothing
            }).error(function (data) {
                console.log('error');
            });
        }

        // 将names组装成params格式
        function handleParams(names, type) {
            var params = [];
            angular.forEach(names, function (name, index) {
                var param = { 'name': name, 'type': type, 'tags': [] };
                params.push(param);
            });
            return params;
        }

        // 从outputPath下读取返回的tag结果
        function getRecommandTags(outputPath) {
            $http({
                method: 'GET',
                url: './autoTag/getAutoTags',
                params: {
                    'outputPath': outputPath
                },
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                // 目前只考虑一个程序
                if (data.data && data.data.length === 1) {
                    var tagResult = handleTag(data.data[0].tags);
                    tagResult.sort();
                    $scope.recommandedTags = tagResult;
                    $scope.autoTagModel = tagResult;
                } else {
                    $scope.recommandedTags = [];
                    $scope.autoTagModel = [];
                }
                uiSelectOption($scope.recommandedTags);
                angular.element('auto-tag-directive').triggerHandler('initAutoTag');
            }).error(function (data) {
                console.log('error');
            });
        }

        function handleTag(autoTags) {
            var result = [];
            angular.forEach(autoTags, function (tag, index) {
                var tags = tag.split(' ');
                tags.sort();
                result.push(tags.join('_'));
            });
            return result;
        }

        function uiSelectOption(data) {
            $('.auto-tags-control').select2({
                data: data,
                tags: true,
                tokenSeparators: [' ']
            });
        }
    }
})