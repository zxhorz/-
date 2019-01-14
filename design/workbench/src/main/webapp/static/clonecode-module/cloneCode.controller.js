
'use strict';

angular.module('cloneCodeModule')
    .controller('clonecodeController', function ($scope, $http, $timeout, infoDataService, historyUrlService) {
        infoDataService.setPage('cloneCode');
        var cloneCode = historyUrlService.getCloneCode();
        initCloneCodeSummary();
        if (typeof (cloneCode) !== 'undefined' && typeof (cloneCode.groupNo) !== 'undefined') {
            getGroupList(cloneCode.groupNo);
        } else {
            getGroupList(0);
        }
        function getGroupList(index) {
            $http({
                method: 'GET',
                url: './clone/groupList',
                params: {
                    'projectId': infoDataService.getId()
                }
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        $scope.tierGroup = data.data;
                        if (data.data.length > 0) {
                            var groups = [];
                            angular.forEach(data.data, function (group, index) {
                                groups.push({
                                    'id': index + 1,
                                    'groupNo': group.groupNo
                                });
                            });
                            $scope.cloneGroup = groups;
                            $scope.selectedGroup = $scope.cloneGroup[index];
                        }
                    } else {
                        $scope.tierGroup = [];
                    }
                }).error(function (data) {
                    $scope.tierGroup = [];
                });
        }
        if (historyUrlService.getClickFlag()) {
            $scope.cloneNavTxt = 'summary';
            $scope.hasGroup = false;
            historyUrlService.setClickFlag(true);
            historyUrlService.setUrlInfo('cloneCode/' + 'summary');
            cloneCode = {};
        }
        if (infoDataService.getPage() === 'cloneCode') {
            var WatchEvent = $scope.$watch('hisRecode', function (newValue) {
                if (newValue !== undefined && historyUrlService.getClickFlag() === false) {
                    var tabInfo = historyUrlService.getUrlParams().split('/')[0];
                    switch (tabInfo) {
                        case 'summary': cloneTabItem(tabInfo); break;
                        case 'group': cloneTabItem(tabInfo); break;
                        case 'codecompare': cloneTabItem(tabInfo);
                            if (cloneCode && cloneCode.listId) {
                                var WatchEvent2 = $scope.$watch('selectedGroup', function (newValue) {
                                    if (newValue !== undefined) {
                                        prepareDiff();

                                        $scope.thumbnail = true;
                                        if (cloneCode.name2) {
                                            $scope.name1 = cloneCode.name1;
                                            $scope.name2 = cloneCode.name2;
                                            var diffInfo = {};
                                            diffInfo.projectId = infoDataService.getId();
                                            diffInfo.leftParaName = $scope.name1;
                                            diffInfo.rightParaName = $scope.name2;
                                            getSourceCode(diffInfo);
                                        }
                                        WatchEvent2();
                                    }
                                })
                                //显示Tier
                            } else {
                                $scope.changeDiffInfo();
                            }
                            break;
                        default: break;
                    }
                }
            });
        } else {
            WatchEvent();
        }

        $scope.cloneTab = function (event, flag) {
            $scope.ps = false;
            cloneTabItem(event);
            historyUrlService.setUrlInfo('cloneCode/' + event);
            if (flag && angular.isDefined($scope.selectedGroup)) {
                $scope.changeDiffInfo();
            }
        }
        function cloneTabItem(event) {
            $scope.cloneNavTxt = event;
            angular.element('#' + event).siblings('li').removeClass('active');
            angular.element('#' + event).addClass('active');
        }

        $scope.changeDiffInfo = function (obj) {
            angular.element('.clicked').removeClass('clicked');
            clearTier();
            prepareDiff();
            cloneCode.groupNo = obj ? obj.groupNo - 1 : 0;
            historyUrlService.setCloneCode(cloneCode);
        }

        function initCloneCodeSummary() {
            $http({
                method: 'GET',
                url: './clone/summary',
                params: {
                    'projectId': infoDataService.getId()
                }
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        $scope.cloneinfo = data.data;
                        if (data.data.groupCount && data.data.groupCount > 0) {
                            $scope.hasGroup = true;
                            initChart();
                        } else {
                            $scope.hasGroup = false;
                        }
                    } else {
                        $scope.hasGroup = false;
                        $scope.cloneinfo = [];
                    }
                }).error(function (data) {
                    $scope.hasGroup = false;
                    $scope.cloneinfo = [];
                });
        };


        function initChart() {
            // 根据data结果统计出每个group中tier1,tier2,tier3,tier4有多少个段，段的代码总行数为多少
            // 根据data结果统计出所有group中，tier1,tier2, tier3, tier4有多少个段，段的代码总行数为多少，占project代码总行数的比例为多少
            // $scope.load = true;
            // $scope.part = 'default';
            $scope.onModel.modelLoading('loading', 'loading');
            var percentage = [];
            //饼图需要的块
            $scope.labels1 = [
                'Tier-1',
                'Tier-2',
                'Tier-3',
                'None-Clone'
            ];
            //数据
            percentage.push($scope.cloneinfo.percent1);
            percentage.push($scope.cloneinfo.percent2);
            percentage.push($scope.cloneinfo.percent3);
            percentage.push($scope.cloneinfo.percent4);
            $scope.data1 = percentage;
            //颜色
            $scope.colors1 = [
                '#46BFBD',
                '#FDB45C',
                '#949FB1',
                '#87CEFA'];
            //其他的参数
            $scope.options1 = {
                'legend': {
                    'position': 'left',
                    'labels': {
                        'fontSize': 14,
                        'fontColor': '#000',
                        'boxWidth': 15,
                        'padding': 5
                    }
                }
            };
            //表格标题
            $scope.chartTitle = 'Clone Code Analysis';
            $scope.onModel.modelHide();
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////

        $scope.lastClicked = null;

        $scope.buttonClick = function ($event, node) {
            $scope.lastClicked = node;
            $event.stopPropagation();
        }

        // 准备diff需要的数据
        $scope.to_diff = function (groupNo) {
            clearTier();
            $scope.selectedGroup = $scope.cloneGroup[groupNo];
            prepareDiff();
            cloneCode.groupNo = groupNo;
            historyUrlService.setCloneCode(cloneCode);
        }

        function prepareDiff() {
            $scope.diffResult = '';
            $scope.left = '';
            $scope.right = '';
            $scope.name1 = '';
            $scope.name2 = '';
            $scope.onModel.modelLoading('loading', 'loading');
            $http({
                method: 'GET',
                url: './clone/prepareDiff',
                params: {
                    'projectId': infoDataService.getId(),
                    'groupNo': $scope.selectedGroup.groupNo
                }
            }).success(function (data) {
                // 选中某个group后，得到该group的各个tier的段落信息  
                $scope.tierParasInGroup = data.data;
                //回退:点击了paragraph，显示Tier段落信息
                if (!historyUrlService.getClickFlag() && cloneCode && cloneCode.listId) {
                    getTier(cloneCode.focus1, cloneCode.listId);
                }
                $scope.onModel.modelHide();
                $scope.diff_show = true;
                $scope.cloneTab('codecompare');
                historyUrlService.setClickFlag(true);
            }).error(function (data) {
                console.log(data);
            });
        }

        // 选中该group中的某一个段
        $scope.pargraph_select = function (index, e, id) {
            var tli = $(e.target);
            cloneCode.focus1 = index;
            cloneCode.listId = id;
            historyUrlService.setCloneCode(cloneCode);
            getTier(index, id);
            $scope.focus2 = -1;
            if ($scope.focus1 === index) {
                $scope.ps = true;
            } else {
                clearTier();
            }
        }

        //选中该group中的某一个段,获取Tier值,tli是选中的li节点
        function getTier(index, id) {
            //focus1用来判断是否需要添加clicked，clicked是class类名
            $scope.focus1 = index;
            var memberTier = $scope.tierParasInGroup.memberTier;
            var l = memberTier.length;
            for (var i = 0; i < l; i++) {
                if (memberTier[i].memberId == id) {
                    $scope.Tier = memberTier[i];
                    break;
                }
            }
        }

        //清除Tier，不显示tier信息
        function clearTier() {
            $scope.Tier = [];
            $scope.ps = false;
            $scope.thumbnail = false;
            $scope.focus1 = -1;
            $scope.focus2 = -1;
        }

        $scope.isLeftArrow=false;
        $scope.arrowBtn=function(){
            $scope.isLeftArrow=$scope.isLeftArrow?false:true;
        }

        // diff操作，选择第二个段之后，直接调用diff算法
        $scope.diff_select = function (index, event, text) {
            $scope.diffResult = '';
            $scope.left = '';
            $scope.right = '';
            $scope.name1 = '';
            $scope.name2 = '';
            $scope.focus2 = index;
            var tli = $(event.target);

            if ($scope.focus2 === index) {
                $scope.left = null;
                $scope.right = null;
                $scope.name1 = $('.first_list').find('.clicked').text();
                $scope.name2 = text;

                var diffInfo = {};
                diffInfo.projectId = infoDataService.getId();
                diffInfo.leftParaName = $scope.name1;
                diffInfo.rightParaName = $scope.name2;
                getSourceCode(diffInfo);
                $timeout(function(){
                    $scope.isLeftArrow=true;
                });
                //保存name1、name2
                cloneCode.name1 = $scope.name1;
                cloneCode.name2 = $scope.name2;
                historyUrlService.setCloneCode(cloneCode);
            } else {
                $timeout(function(){
                    $scope.isLeftArrow=false;
                });
                $scope.diffResult = '';
                $scope.name1 = '';
                $scope.name2 = '';
                $scope.left = null;
                $scope.right = null;
            }
        }
        function getSourceCode(diffInfo) {
            $http({
                method: 'POST',
                url: './clone/getSourceCode',
                data: diffInfo
            }).success(function (data) {
                if (data.data && data.data.length === 2) {
                    $scope.left = data.data[0];
                    $scope.right = data.data[1];
                    searchTier();
                } else {
                    console.log('No data');
                }
            }).error(function (data) {
                console.log('error');
            });
        }
        // diff 结果
        function searchTier() {
            $http({
                method: 'POST',
                url: './clone/diff',
                data: $.param({
                    'projectId': infoDataService.getId(), 'leftParaName': $scope.name1, 'rightParaName': $scope.name2
                }),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                //$scope.diffResult值为0-4，0表示非clone code,1是Tier-1,2是Tier-2,3是Tier-3,4是Tier-4
                if (data.data === 0) {
                    $scope.diffResult = 'None Clone';
                } else if (data.data === 1000) {
                    // TODO diff分析过程中出现error，此处应该做error提示框处理
                    console.log('error');
                } else {
                    $scope.diffResult = 'Tier-' + data.data;
                }
                // alert($scope.diffResult);
            }).error(function (data) {
                console.log('error');
            });
        }
    });
