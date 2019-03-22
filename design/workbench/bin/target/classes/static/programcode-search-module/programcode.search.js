var app = angular.module('programcodesearch', ['angularjs-dropdown-multiselect']);
app.controller('programcodeSearchtController', function ($scope, $http, $timeout) {
    //初始化
    $scope.avaliable = true;
    //选中的标签数组
    $scope.tagsArray = [];
    //代码块内容
    $scope.codes = '';
    //controlflow图
    $scope.svgUrl = '';
    //add tag下拉框默认选下
    $scope.addTagSelect = 'Select';
    //remove tag下拉框默认选下
    $scope.removeTagSelect = 'Select';
    //查询结果统计值
    $scope.count = '';
    $scope.tagSettings = { smartButtonMaxItems: 5 };
    //获取预存的tag内容，所有下拉框使用相同tag内容
    $http.get('programcode-search-module/select.json').success(function (data) {
        $scope.tags = data;
        $scope.tagsOptions = data;
    });

    // 设置checkbox默认不选中,全选默认不选中
    $scope.ifTags = false;
    $scope.ifCheckedAll = false;
    // 搜索校验
    $scope.focusCondition = function (params) {
        if (params === 'keywords') {
            //keywords查询模式
            $scope.searchCondition = 'keywords';
        } else if (params === 'Code_Similarity'){
            //similar code查询模式
            $scope.searchCondition = 'Code_Similarity';
        }else{
             $scope.searchCondition = 'program';
        }
    };
    //搜索方法
    var condition;
    var isKeyWords;
    var isProgram;
    //查询事件响应
    $scope.search = function (source) {
        $scope.count = '';
        $scope.table = [];
        $scope.codes = '';
        $scope.svgUrl = '';
        $scope.selectAll = false;
        $scope.checked = [];
       if (source === 'search') {
            if ($scope.searchCondition === 'keywords' || !$scope.searchCondition) {
                $scope.searchCondition = 'keywords';
                isKeyWords = true;
                isProgram = false;
                //keywords的值
                condition = $scope.keywords;
            } else if($scope.searchCondition === 'Code_Similarity' || !$scope.searchCondition){
                //Code_Similarity的值
                $scope.searchCondition = 'Code_Similarity';
                condition = $scope.Code_Similarity;
                isKeyWords = false;
                isProgram = false;
            }else{
                //program
                $scope.searchCondition = 'program';
                condition=$scope.program;
                isKeyWords = false;
                isProgram = true;
            }
        }
        //keywords和programname任意模式有输入值时
        if (!isNull(condition)) {
            $scope.avaliable = false;
            $scope.display=true;
            $scope.part='';
            // 1.只根据keyword或代码文本搜索program
            $http({
                method: 'POST',
                url: './programcodesearch/searchByKeywordOrText/',
                data: $.param({ 'condition': condition, 'isKeyWords': isKeyWords,'isProgram':isProgram}),
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                var result;
                var programsId = generateId(data);
                var programIdStr = programsId.join(',');
                // 先将data中的tag属性全部补齐
                $http({
                    method: 'POST',
                    url: './programcodesearch/completingTagForParas',
                    data: $.param({ 'programIds': programIdStr }),
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                }).success(function (tagsData) {
                    var tagsResult = tagsData;
                    if ($scope.ifTags && $scope.tagsArray.length > 0) {
                        //查询得到匹配的tag时
                        var tags = [];
                        angular.forEach($scope.tagsArray, function (i, index) {
                            tags.push(i.id);
                        });
                        result = fillSelectedTag(data, programsId, tagsResult, tags);

                    } else {
                        //没有匹配的tag结果
                        result = fillTag(data, programsId, tagsResult);
                    }
                    // match_Score降序排序
                    result.sort(function (a, b) {
                        return b.match_Score - a.match_Score;
                    });
                    $scope.count = ' : ' + result.length;
                    $scope.table = result;
                    $scope.avaliable = true;
                    $scope.part='success';
                    $timeout(function(){
                        $scope.display=false;
                    },2000);
                }).error(function () {
                    //错误信息展示
                    $scope.avaliable = true;
                    $scope.part='error';
                    $scope.errorInfo = 'error';
                    $timeout(function(){
                        $scope.display=false;
                    },2000);
                });
            }).error(function () {
                //错误信息展示
                $scope.avaliable = true;
                $scope.part='error';
                $scope.errorInfo = 'error';
                $timeout(function(){
                    $scope.display=false;
                },2000);
            });
        } else {
            // 3.只根据tag搜索program
            if ($scope.ifTags && $scope.tagsArray.length > 0) {
                $scope.avaliable = false;
                var tags = [];
                angular.forEach($scope.tagsArray, function (i) {
                    tags.push(i.id);
                });
                $scope.display=true;
                $scope.part='';
                $http({
                    method: 'POST',
                    url: './programcodesearch/searchByTags/',
                    data: $.param({ 'tagsArray': tags }),
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                }).success(function (data) {
                    var result = fillMatch_Score(data);
                    $scope.count = ' : ' + result.length;
                    $scope.table = result;
                    $scope.avaliable = true;
                    $scope.part='success';
                    $timeout(function(){
                        $scope.display=false;
                    },2000);
                }).error(function () {
                    $scope.display=true;
                    $scope.part='error';
                    $scope.errorInfo = 'error';
                    $timeout(function(){
                        $scope.display=false;
                    },2000);
                });
            } else {
                $scope.codes = '';
                $scope.svgUrl = '';
                if(!$scope.ifTags){
                    if(isKeyWords){
                       $scope.errorInfo = 'please input keywords';
                    }else{
                       $scope.errorInfo = 'please input similar code';
                    }
                }else{
                    $scope.errorInfo = 'please select at least one tag';
                }
                $scope.display=true;
                $scope.part='error';
                $timeout(function(){
                    $scope.display=false;
                },2000);
            }
        }
    };
    //checkbox选中的ID
    $scope.checked = [];
    //复选框全选点击事件
    $scope.select_All = function (selectAll) {
        //清空数组
        $scope.checked = [];
        for(var i=0;i<$scope.table.length;i++){
            if (selectAll) {
                //全选选中
                $scope.checked.push($scope.table[i].program + '.' + $scope.table[i].program);
                $scope.table[i].checked=true;
            } else {
                $scope.table[i].checked= false;
            }
        }
    };

    //复选框单击事件
    $scope.selectOne = function (t) {
        var select = t.program;//+ '.' + t.program;
        if ($scope.checked.length > 0) {
            var index = $.inArray(select, $scope.checked);
            if (index === -1) {
                //当前点击内容没有记录时，确认为选中
                $scope.checked.push(select);
            } else {
                //当前点击内容有记录时，确认为取消选中
                $scope.checked.splice(index, 1);
            }
        } else {
            $scope.checked.push(select);
        }
        if ($scope.table.length === $scope.checked.length) {
            //选中的个数等于表的总数时，标记全选选中状态
            $scope.selectAll = true;
        } else {
            $scope.selectAll = false;
        }
    };

    //add tag事件
    $scope.addTag = function () {
        $scope.avaliable = false;
        updateTags('ADD', $scope.addTagSelect);
    };
    //remove tag事件
    $scope.removeTag = function () {
        $scope.avaliable = false;
        updateTags('REMOVE', $scope.removeTagSelect);
    };
    //点击表格显示代码,显示数据绑定在$scope.result
    $scope.tableSelect = function ($event, t) {
        angular.element('.resultTable table tr').removeClass('selectedTr');
        $($event.target).parent().addClass('selectedTr');
        getControlFlow(t);
        getSourceCode(t);
    };
    //更新数据库tag内容 add/remove
    function updateTags(action, tag) {
        //验证是否选中TAG
        if ($scope.checked.length < 1) {
            //没有选中任何记录时，报错，不执行操作
            $scope.display=true;
            $scope.part='error';
            $scope.errorInfo = 'Please select at least one record';
            $timeout(function(){
                $scope.display=false;
            },2000);
            $scope.avaliable = true;
            return;
        } else if (tag === 'Select') {
            //没有选中任何tag时，报错，不执行操作
            $scope.display=true;
            $scope.part='error';
            $scope.errorInfo = 'Select is not a valid option';
            $timeout(function(){
                $scope.display=false;
            },2000);
            $scope.avaliable = true;
            return;
        }
        $http({
            method: 'POST',
            url: './programcodesearch/updatetags',
            data: $.param({ 'name': $scope.checked, 'action': action, 'tag': tag }),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function () {
            $scope.search('tag');
            $scope.checked = [];
            $scope.ifCheckedAll = false;
            $scope.avaliable = true;
        }).error(function (data) {
            console.info(data);
            $scope.avaliable = true;
            $scope.display=true;
            $scope.part='error';
            $scope.errorInfo = 'Please checked';
            $timeout(function(){
                $scope.display=false;
            },2000);
        });
    }
    function getControlFlow(t) {
        //简化版，快速获取svg，需要预先分析所有controlflow
        $http({
            method: 'POST',
            url: './programcodesearch/getSvg',
            data: $.param({ 'program': t.program, 'case': 'programcodesearch'}),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
            if (data !== '') {
                $scope.svgUrl = data;
            } else {
                //没有预先分析好的svg时，实时执行controlflow生成功能
                genControlFlow(t);
            }
        }).error(function (data) {
            console.info(data);
            $scope.svgUrl = '';
        });
    }
    //实时生成controlflow图b3d8fb138665629a5fcc
    function genControlFlow(t) {
        $http({
            method: 'POST',
            url: './programcodesearch/controlflow',
            data: $.param({
                'codepath': './code/codesearch/',
                'program': t.program,
                'case': 'programcodesearch'
            }),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
            // 返回svg文件路径
            $scope.svgUrl = data;
        }).error(function () {
            $scope.svgUrl = '';
        });
    }

    // 获取段落源代码
    function getSourceCode(t) {
        // 带program name参数请求至后台
        $http({
            method: 'POST',
            url: './programcodesearch/getSourceCode',
            data: $.param({ 'codepath': './code/codesearch/', 'program': t.program }),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
            //var resultCode = data.split('\r\n');
           // $scope.codes = resultCode.slice(t.startLine - 1, t.endLine).join('\r\n');
           $scope.codes=data;
        }).error(function (data) {
            console.info(data);
            $scope.codes = 'Not Available';
        });
    }

    // 判断str是否为undefined和是否为空
    function isNull(str) {
        // undefined
        if (typeof (str) === 'undefined') {
            return true;
        }
        // 空字符
        if (str === '') {
            return true;
        }
        // 空字符组成的空字符串
        var regu = '^[ ]+$';
        var re = new RegExp(regu);
        return re.test(str);
    }

    // 生成id: program.program
    function generateId(data) {
        var paraIds = [];
        for (var index in data) {
            paraIds.push(data[index].program );//+ '.' + data[index].program);
        }
        return paraIds;
    }

    //根据search的program结果查询neo4j得到结果，将结果中的tag填充到search结果中
    function fillTag(data, programsId, tagsResult) {
        var result = [];
        for (var index in tagsResult) {
            // 判断tag返回结果中的program在program集合中的index
            var indexOfId = $.inArray(tagsResult[index].programId, programsId);
            var tag = tagsResult[index].tag;
            // split tag
            tag = tag.replace(/</g, '');
            tag = tag.substring(0, tag.length - 1);
            var tags = tag.split('>');
            // 多个tag 以逗号+空格；连接
            data[indexOfId].exiting_tags = tags.join(', ');
            // data[indexOfId].startLine = tagsResult[index].start_line;
            // data[indexOfId].endLine = tagsResult[index].end_line;
            result.push(data[indexOfId]);
        }
        return result;
    }

    // 根据search的program结果查询neo4j得到结果，再用tagsArray过滤，将结果中的tag填充到search结果中
    function fillSelectedTag(data, programsId, tagsResult, tagsArray) {
        var result = [];
        for (var index in tagsResult) {
            var tag = tagsResult[index].tag;
            tag = tag.replace(/</g, '');
            tag = tag.substring(0, tag.length - 1);
            var tags = tag.split('>');
            if (hasTag(tags, tagsArray)) {
                var indexOfId = $.inArray(tagsResult[index].programId, programsId);
                data[indexOfId].exiting_tags = tags.join(', ');
                // data[indexOfId].startLine = tagsResult[index].start_line;
                // data[indexOfId].endLine = tagsResult[index].end_line;
                result.push(data[indexOfId]);
            }
        }
        return result;
    }

    // 仅仅是选择tag做搜索时候，将match_Score字段的初始空值填充到返回的result中
    function fillMatch_Score(tagsResult) {
        var result = [];
        for (var index in tagsResult) {
            result.push({
                'program': tagsResult[index].program,
                'match_Score': '',
                'exiting_tags': splitTag(tagsResult[index].exiting_tags).join(', ')
                // 'startLine': tagsResult[index].start_line,
                // 'endLine': tagsResult[index].end_line
            });
        }
        return result;
    }

    // 判断tagsArray中是否包含tags中的任意一个tag
    function hasTag(tags, tagsArray) {
        for (var index in tags) {
            if ($.inArray(tags[index], tagsArray) !== -1) {
                return true;
            }
        }
        return false;
    }
    // neo4j库中的tag是以<>来区分，提取tag
    function splitTag(tag) {
        tag = tag.replace(/</g, '');
        tag = tag.substring(0, tag.length - 1);
        var tags = tag.split('>');
        return tags;
    }
});
