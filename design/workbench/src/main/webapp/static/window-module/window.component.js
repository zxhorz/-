'use strict';

angular.
module('windowModule').
component('windowModule', {
    templateUrl: 'window-module/window.template.html',
    controller: function($scope, $rootScope, $http, $sce,
        $attrs, output_graph_window_style, output_table_window_style, neo4j_window_style) {
        //初始化
        $rootScope.tml='window-module/template1.html';
        $scope.leftBox_show=false;
        //默认设置最小化按钮隐藏，最大化按钮显示
        $rootScope.min = false;
        $rootScope.max = true;
        //监听$rootScope.shiny_report_show设置SRC的值
        $rootScope
            .$watch('$root.shiny_report_show+$root.ui_grid_summary+$root.ui_grid_detail+$root.neo4j_window_show',
            function() {
            if ($rootScope.ui_grid_summary || $rootScope.ui_grid_detail) {
                $scope.leftBox_show=true;
            }
            if (($rootScope.ui_grid_summary && $rootScope.neo4j_window_show)
                || ($rootScope.ui_grid_detail && $rootScope.neo4j_window_show)) {
                angular.element('.leftBox').addClass('bigPartWidth');
                angular.element('.rightBox').addClass('smallPartWidth');
                //广播
                $scope.$broadcast('changeTable');
                // 元素大小缩放
                $('.rightBox').resizable({
                    handles:' e, w',
                    //当用户鼠标没有悬浮在元素上时是否隐藏手柄。
                    autoHide: true,
                    alsoResize: '#leftBox',
                    resize: function( event, ui) {
                        $scope.wrapWidth = $('#wrap').width();
                        $scope.wrapHeight = $('#wrap').height();
                        $scope.leftBoxStyle={
                            'width': ($scope.wrapWidth - ui.size.width - 45) + 'px'
                        };
                        $scope.rightBoxStyle={
                            'width':(ui.size.width)+ 'px',
                            'left':0,
                            'height': (ui.originalSize.height + 12) + 'px'
                        };
                    }
                });
                // 元素大小缩放
                $('.leftBox').resizable({
                    handles:' e, w',
                    //当用户鼠标没有悬浮在元素上时是否隐藏手柄。
                    autoHide: true,
                    alsoResize: '#rightBox',
                    resize: function( event, ui) {
                        $scope.wrapWidth = $('#wrap').width();
                        $scope.wrapHeight = $('#wrap').height();
                        $scope.leftBoxStyle={
                            'width': (ui.size.width)+ 'px',
                            'left':0
                        };
                        $scope.rightBoxStyle={
                            'width':($scope.wrapWidth - ui.size.width - 45) + 'px',
                            'height': (ui.originalSize.height + 12) + 'px'
                        };
                    }
                });
            }
            //加载iframe页面
            if ($rootScope.shiny_report_show) {
                if($rootScope.currentCase&&$rootScope.configList[$rootScope.currentCase]){
                    //设置iframe的src,ng里面有个属性是专门用来解决跨域问题的 $sce。
                    $scope.myURL=$sce.trustAsResourceUrl($rootScope.configList[$rootScope.currentCase].shinyurl);
                }else{
                  $rootScope.shiny_report_show = false;
                 }
            }
        });
        //关闭窗口事件
        $scope.hideWindow=function(type){
            //默认设置最小化按钮隐藏，最大化按钮显示
            $rootScope.min = false;
            $rootScope.max = true;
            //关闭iframe
            if(type === 'shiny_report_show'){
                $rootScope.shiny_report_show=false;
                //设置iframe的src,ng里面有个属性是专门用来解决跨域问题的 $sce。
                $scope.myURL='';
                angular.element('.mainBox').removeClass('height70');
                //关闭neo4j
            }else if(type === 'neo4j_window_show'){
                $rootScope.neo4j_window_show=false;
                angular.element('.leftBox').removeClass('bigPartWidth');
            }else if(type === 'ui_grid_summary'){
                //ui_grid_summary表关闭
                $rootScope.ui_grid_summary=false;
                angular.element('.rightBox').removeClass('smallPartWidth');
            }else{
                $rootScope.ui_grid_detail=false;
                angular.element('.rightBox').removeClass('smallPartWidth');
            }
        };

        //窗口最大化事件
        $scope.maxWindow = function($event,type){
            $($event.target).parent().parent().parent().addClass('maxWindow');
            $($event.target).parents('.mainBox').addClass('over');
            //显示最小化按钮
            $rootScope.min = true;
            $rootScope.max = false;
            if(type==='ui_grid_summary'){
                angular.element('.Summary').addClass('maxSummary');
                angular.element('.summaryGrid').addClass('maxSummaryGrid');
            }else if(type==='ui_grid_detail'){
                angular.element('.listGridBox').addClass('maxListGridBox');
                angular.element('.listGrid').addClass('maxListGrid');
                angular.element('.codeBox').addClass('maxCodeBox');
                angular.element('.svgBox').addClass('maxSvgBox');
            }else if(type==='ui_grid_graph'){
                angular.element('.summaryGrid').addClass('maxSummaryGrid');
                 $scope.$broadcast('changeChart',type);
            }else if(type==='btsecond'){
                angular.element('.listGridBox').removeClass('maxListGridBox');
                angular.element('.listGrid').removeClass('maxListGrid');
                angular.element('.codeBox').removeClass('maxCodeBox');
                angular.element('.svgBox').removeClass('maxSvgBox');
                angular.element('.btsecond').removeClass("bts");
                //广播
                $scope.$broadcast('changeTable',type,'ide-code-controlflow');
            }else if(type==='btthree'){
                angular.element('.listGridBox').removeClass('maxListGridBox');
                angular.element('.listGrid').removeClass('maxListGrid');
                angular.element('.codeBox').removeClass('maxCodeBox');
                angular.element('.svgBox').removeClass('maxSvgBox');
                angular.element('.btthree').removeClass("btt");
                //广播
                $scope.$broadcast('changeTable',type,'ide-code-controlflow');
            }
        };
        //窗口最小化事件
        $scope.minWindow = function($event,type){
            $($event.target).parent().parent().parent().removeClass('maxWindow');
            $($event.target).parents('.mainBox').removeClass('over');
            //隐藏最小化按钮
            $rootScope.min = false;
            $rootScope.max = true;
            if(type==='ui_grid_summary'){
                angular.element('.Summary').removeClass('maxSummary');
                angular.element('.summaryGrid').removeClass('maxSummaryGrid');
            }else if(type==='ui_grid_detail'){
                angular.element('.listGridBox').removeClass('maxListGridBox');
                angular.element('.listGrid').removeClass('maxListGrid');
                angular.element('.codeBox').removeClass('maxCodeBox');
                angular.element('.svgBox').removeClass('maxSvgBox');
                //广播
                $scope.$broadcast('changeTable',type,'ide-code-controlflow');
            }else if(type==='ui_grid_graph'){
                angular.element('.summaryGrid').removeClass('maxSummaryGrid');
                $scope.$broadcast('changeChart',type);

            }else if(type==='btsecond'){
                angular.element('.listGridBox').removeClass('maxListGridBox');
                angular.element('.listGrid').removeClass('maxListGrid');
                angular.element('.codeBox').removeClass('maxCodeBox');
                angular.element('.svgBox').removeClass('maxSvgBox');
                angular.element('.btsecond').addClass("bts");
                //广播
                $scope.$broadcast('changeTable',type,'ide-code-controlflow');
            }else if(type==='btthree'){
                angular.element('.listGridBox').removeClass('maxListGridBox');
                angular.element('.listGrid').removeClass('maxListGrid');
                angular.element('.codeBox').removeClass('maxCodeBox');
                angular.element('.svgBox').removeClass('maxSvgBox');
                angular.element('.btthree').addClass("btt");
                //广播
                $scope.$broadcast('changeTable',type,'ide-code-controlflow');
            }
        };
        // 根据value对象传入的参数，设定各个窗口的展示形式
        $scope.output_graph_window_style = output_graph_window_style;
        $scope.output_table_window_style = output_table_window_style;
        $scope.neo4j_window_style = neo4j_window_style;

        $scope.changeStyle = function(belongingWindow) {
             if (belongingWindow === 'output_graph_window') {
                if ($scope.output_graph_window_style === 'tab') {
                    $scope.output_graph_window_style = 'grid';
                } else {
                    $scope.output_graph_window_style = 'tab';
                }
             } else if (belongingWindow === 'output_table_window') {
                if ($scope.output_table_window_style === 'tab') {
                    $scope.output_table_window_style = 'grid';
                } else {
                    $scope.output_table_window_style = 'tab';
                }
             } else {
                if ($scope.neo4j_window_style === 'tab') {
                    $scope.neo4j_window_style = 'grid';
                } else  {
                    $scope.neo4j_window_style = 'tab';
                }
             }
        };
        //****************************************resize***********************************************

        var clickX, clickY, leftOffset, topOffset, inx;
        var dragging = false;
        var labBtn = $('#wrap').find('label');
        var wrapWidth = $('#wrap').width();
        var wrapHeight = $('#wrap').height();

        labBtn.bind('mousedown', function() {
            dragging = true;
            leftOffset = $('#wrap').offset().left;
            topOffset = $('#wrap').offset().top;
            inx = $(this).index('label');
        });

        $(document).mousemove(function(e) {
            if (dragging) {
                //左边水平第一个拖条
                if ('0' === inx) {
                    clickY = e.pageY;
                    if (clickY > (topOffset + 25)) {
                        //拖条移动
                        labBtn.eq(inx).css('top', clickY - 5 - topOffset + 'px');
                        labBtn.eq(inx).prev().height(clickY + 5 - topOffset + 'px');
                        labBtn.eq(inx).next().height(wrapHeight - labBtn.eq(inx).prev().height() + 'px');
                    } else {
                        // 拖条上边出界
                        labBtn.eq(inx).css('top', '20px');
                    }
                    //拖条下边出界
                    if (clickY >= (topOffset + wrapHeight - 25)) {
                        labBtn.eq(inx).css('top', wrapHeight - 30 + 'px');
                        labBtn.eq(inx).prev().height(wrapHeight - 20 + 'px');
                        labBtn.eq(inx).next().height('20px');
                    }
                } else if ('1' === inx) {
                    //中间竖直拖条
                    clickX = e.pageX;
                    if (clickX >= (leftOffset + 255)) {
                        //拖条移动
                        labBtn.eq(inx).css('left', clickX - 5 - leftOffset + 'px');
                            //向左拖，先缩小左边块的宽度
                            labBtn.eq(inx).prev().width(clickX - leftOffset + 5 + 'px');
                            labBtn.eq(inx).next().width($(document.body).width()
                                - labBtn.eq(inx).prev().width() + 'px');

                            //再缩短左边水平拖条的长度
                            $('#lab1').width(labBtn.eq(inx).prev().width() - 10 + 'px');

                    } else {
                        // 拖条左边出界
                        labBtn.eq(inx).css('left', '250px');
                    }
                    //拖条右边出界
                    if (clickX >= (leftOffset + wrapWidth - 255)) {
                        labBtn.eq(inx).css('left', wrapWidth - 250 - 10 + 'px');
                        labBtn.eq(inx).prev().width(wrapWidth - 250 + 'px');
                        labBtn.eq(inx).next().width('250px');
                        //设置左边的水平拖条最大只能为最外wrap框宽度
                        $('#lab1').width(wrapWidth - 250 - 10 + 'px');
                    }
                } else if ('2' === inx) {
                    //右边水平第一个拖条
                    clickY = e.pageY;
                    if (clickY >= (topOffset + 25)) {
                        //拖条移动
                        labBtn.eq(inx).css('top', clickY - 5 - topOffset + 'px');
                        labBtn.eq(inx).prev().height(clickY - topOffset + 'px');
                        labBtn.eq(inx).next().height(wrapHeight - labBtn
                            .eq(inx).prev().height() - labBtn.eq('3').next().height() + 'px');
                    } else {
                        // 拖条上边出界
                        labBtn.eq(inx).css('top', '20px');
                    }
                    //拖条移动超过下边第二个拖条
                    if ((clickY + 25) > labBtn.eq('3').offset().top) {
                        // 25:设定window不能被拉到小于20px的高度
                        labBtn.eq(inx).css('top', labBtn.eq('3').offset().top - topOffset - 30 + 'px');
                        labBtn.eq(inx).prev().height(labBtn.eq(inx).offset().top - topOffset + 10 + 'px');
                        labBtn.eq(inx).next().height('30px');
                    }
                } else if ('3' === inx) {
                    //右边水平第二个拖条
                    clickY = e.pageY;
                    if (clickY >= (labBtn.eq('2').offset().top + 35)) {
                        //拖条移动
                        labBtn.eq(inx).css('top', clickY - 5 - topOffset + 'px');
                        labBtn.eq(inx).prev().height(clickY + 5 - topOffset - labBtn.eq('2').prev().height() + 'px');
                        labBtn.eq(inx).next().height(wrapHeight - (clickY + 5 - topOffset) + 'px');
                    } else {
                        //拖条上边超过上边第一个拖条
                        labBtn.eq(inx).css('top', labBtn.eq('2').offset().top - topOffset + 30 + 'px');
                    }
                    //拖条下边出界
                    if ((clickY + 25) >= (wrapHeight + topOffset)) {
                        labBtn.eq(inx).css('top', wrapHeight - 30 + 'px');
                        labBtn.eq(inx).prev().height(wrapHeight - labBtn.eq('2').prev().height() - 20 + 'px');
                        labBtn.eq(inx).next().height('20px');
                    }
                }
            }
        });

        $(document).mouseup(function(e) {
            dragging = false;
            e.cancelBubble = true;
        });
    }
});
