(function (angular) {
    'use strict';
    angular.module('FileManagerApp')
        .controller('FileManagerCtrl', [
            '$scope', '$rootScope', '$window', '$translate', 'fileManagerConfig', 'item', 'fileNavigator', 'apiMiddleware', 'infoDataService', '$websocket', '$timeout',
            function ($scope, $rootScope, $window, $translate, fileManagerConfig, Item, FileNavigator, ApiMiddleware, infoDataService, $websocket, $timeout) {

                $scope.getFromPage = infoDataService.getFromPage();
                var $storage = $window.localStorage;
                $scope.config = fileManagerConfig;
                $scope.reverse = false;
                $scope.predicate = ['model.type', 'model.name'];
                $scope.order = function (predicate) {
                    $scope.reverse = ($scope.predicate[1] === predicate) ? !$scope.reverse : false;
                    $scope.predicate[1] = predicate;
                };
                $scope.query = '';
                $scope.fileNavigator = new FileNavigator();
                $scope.apiMiddleware = new ApiMiddleware();
                $scope.uploadFileList = [];
                $scope.viewTemplate = $storage.getItem('viewTemplate') || 'main-icons.html';
                $scope.fileList = [];
                $scope.temps = [];
                $scope.webSocket=[];
                //选中的projectId
                $scope.apiMiddleware.para = {};
                //cmd命令
                $scope.commandOpt = { txt: "" };

                //控制custom的loadind、success图标是否出现
                $scope.loadShow = true;

                //在custom的history页面点击右键
                $scope.hisSelRight = false;

                //custom：判断是否显示output页面
                $scope.isOutput = false;

                //custom：区分是执行脚本后实时输出的log，还是历史记录中的log
                $scope.isHistoryLog = false;

                //删除历史记录的文件名和runId
                $scope.delScriptName = '';
                $scope.delRunid = '';

                //重写config配置
                $scope.config.appName = rewriteConfig() ? 'SourceCode Manager' : 'Custom Script';
                $scope.config.allowedActions = {
                    upload: true,
                    rename: rewriteConfig(),
                    move: rewriteConfig(),
                    copy: rewriteConfig(),
                    edit: false,
                    changePermissions: false,
                    compress: rewriteConfig(),
                    compressChooseName: true,
                    extract: true,
                    download: true,
                    downloadMultiple: true,
                    preview: false,
                    remove: true,
                    createFolder: false,
                    pickFiles: false,
                    pickFolders: false
                }
                //在custom页面,需要新增run按钮
                $scope.config.isCustom = !rewriteConfig();

                //在custom页面，不需要rename，move，copy，compress等按钮
                function rewriteConfig() {
                    if (infoDataService.getPage() === 'fileManager') {
                        return true;
                    } else {
                        return false;
                    }
                }
                if (angular.element('#context-menu').length > 0) {
                    angular.element('#context-menu').remove();
                }

                $scope.$watch('temps', function () {
                    if ($scope.singleSelection()) {
                        $scope.temp = $scope.singleSelection();
                    } else {
                        $scope.temp = new Item({ rights: 644 });
                        $scope.temp.multiple = true;
                    }
                    $scope.temp.revert();
                });

                $scope.fileNavigator.onRefresh = function () {
                    $scope.temps = [];
                    $scope.query = '';
                    $rootScope.selectedModalPath = $scope.fileNavigator.currentPath;
                };

                $scope.setTemplate = function (name) {
                    $storage.setItem('viewTemplate', name);
                    $scope.viewTemplate = name;
                };

                $scope.changeLanguage = function (locale) {
                    if (locale) {
                        $storage.setItem('language', locale);
                        return $translate.use(locale);
                    }
                    $translate.use($storage.getItem('language') || fileManagerConfig.defaultLang);
                };

                $scope.isSelected = function (item) {
                    return $scope.temps.indexOf(item) !== -1;
                };

                $scope.selectOrUnselect = function (item, $event) {
                    var indexInTemp = $scope.temps.indexOf(item);
                    var isRightClick = $event && $event.which == 3;

                    if ($event && $event.target.hasAttribute('prevent')) {
                        $scope.temps = [];
                        return;
                    }
                    if (!item || (isRightClick && $scope.isSelected(item))) {
                        return;
                    }
                    if ($event && $event.shiftKey && !isRightClick) {
                        var list = $scope.fileList;
                        var indexInList = list.indexOf(item);
                        var lastSelected = $scope.temps[0];
                        var i = list.indexOf(lastSelected);
                        var current = undefined;
                        if (lastSelected && list.indexOf(lastSelected) < indexInList) {
                            $scope.temps = [];
                            while (i <= indexInList) {
                                current = list[i];
                                !$scope.isSelected(current) && $scope.temps.push(current);
                                i++;
                            }
                            return;
                        }
                        if (lastSelected && list.indexOf(lastSelected) > indexInList) {
                            $scope.temps = [];
                            while (i >= indexInList) {
                                current = list[i];
                                !$scope.isSelected(current) && $scope.temps.push(current);
                                i--;
                            }
                            return;
                        }
                    }
                    if ($event && !isRightClick && ($event.ctrlKey || $event.metaKey)) {
                        $scope.isSelected(item) ? $scope.temps.splice(indexInTemp, 1) : $scope.temps.push(item);
                        return;
                    }
                    $scope.temps = [item];
                };

                $scope.singleSelection = function () {
                    return $scope.temps.length === 1 && $scope.temps[0];
                };

                $scope.totalSelecteds = function () {
                    return {
                        total: $scope.temps.length
                    };
                };

                $scope.selectionHas = function (type) {
                    return $scope.temps.find(function (item) {
                        return item && item.model.type === type;
                    });
                };

                $scope.prepareNewFolder = function () {
                    var item = new Item(null, $scope.fileNavigator.currentPath);
                    $scope.temps = [item];
                    return item;
                };

                $scope.smartClick = function (item) {
                    var pick = $scope.config.allowedActions.pickFiles;
                    //文件后缀名
                    var suffixname = item.model.name.substring(item.model.name.lastIndexOf(".") + 1, item.model.name.length)
                    if (suffixname === 'docx' || suffixname === 'doc' || suffixname === 'xlsx' || suffixname === 'xls') {
                        $scope.modal('outputTips');
                    }

                    if (item.isFolder()) {
                        return $scope.fileNavigator.folderClick(item);
                    }

                    if (typeof $scope.config.pickCallback === 'function' && pick) {
                        var callbackSuccess = $scope.config.pickCallback(item.model);
                        if (callbackSuccess === true) {
                            return;
                        }
                    }

                    if (item.isImage()) {
                        if ($scope.config.previewImagesInModal) {
                            return $scope.openImagePreview(item);
                        }
                        return $scope.apiMiddleware.download(item, $scope.runId, $scope.isOutput, true);
                    }

                    if (item.isEditable()) {
                        return $scope.openEditItem(item);
                    }
                };

                $scope.scriptLaunch = function (id, item, hide) {
                    //打开scriptLaunch弹框
                    if (item.isFolder()) {
                        $scope.apiMiddleware.scriptLaunch(item).then(function (data) {
                            $scope.launchDes = data.data;
                        });
                        $scope.apiMiddleware.getProjectList().then(function (data) {
                            $scope.projectNames = data.data;
                            $scope.commandOpt.txt = '';
                            if (infoDataService.getId()) {
                                angular.forEach($scope.projectNames, function (value, key) {
                                    if (value.id === infoDataService.getId()) {
                                        $scope.apiMiddleware.para.projectName = $scope.projectNames[key];
                                    }
                                });
                            } else {
                                $scope.apiMiddleware.para.projectName = $scope.projectNames[0];
                            }
                        });
                    }
                    $scope.modal(id);
                }

                $scope.run = function () {
                    $scope.consoleLog = '';
                    $scope.loadShow = true;
                    $scope.isAnalyzed = false;
                    var item = $scope.singleSelection();
                    var name = item.model.name;
                    $scope.scriptName = $scope.singleSelection() && $scope.singleSelection().model.name;
                    //数据库添加 Runid
                    $scope.apiMiddleware.addHistory($scope.apiMiddleware.para.projectName.id, name, $scope.commandOpt.txt).then(function (data) {
                        $scope.runId = data.data;
                        if ($scope.runId === null) {
                            $scope.consoleLog = "The project is not analyzed, please analyze it first.";
                            $scope.isAnalyzed = false;
                            $scope.isHistoryLog = false;
                            $scope.loadShow = false;
                        } else {
                            webSocket();
                        }
                    });
                    //关闭scriptLaunch弹框
                    $scope.modal('scriptLaunch', true);
                    //打开scriptConsole弹框
                    $scope.modal('scriptConsole');
                };

                function webSocket() {
                    var webSocket;
                    if($scope.runId in $scope.webSocket){
                        webSocket = $scope.webSocket[$scope.runId];
                    }else{
                        var hostUrl = window.location.host;
                        webSocket = $websocket('ws://' + hostUrl + '/websocket/customScript/' + $scope.runId);
                        $scope.webSocket[$scope.runId] = webSocket;
                    }
                    
                    webSocket.onOpen(function(){
                        scriptRun();
                    });
                    webSocket.onMessage(function (message) {
                        //实时输出的log
                        $scope.consoleLog += message.data;
                        $timeout(function () {
                            angular.element('#consoleInfo').scrollTop(200);
                        });
                    })
                    $scope.isHistoryLog = false;
                }

                function scriptRun() {
                    $scope.apiMiddleware.scriptRun($scope.runId).then(function (data) {
                        if (data && data.message) {
                            $timeout(function () {
                                $scope.loadShow = false;
                            });

                            switch (data.message) {
                                case 'custom script was successfully run.': $scope.isAnalyzed = true;break;
                                case 'No neo4j path existed': $scope.consoleLog = "The project is not analyzed, please analyze it first."; $scope.isAnalyzed = false; break;
                                case 'exception': $scope.isAnalyzed = false; break;
                                default: ;
                            }
                        }
                    })
                }

                function getOutputList() {
                    $scope.apiMiddleware.getOutputList().then(function (data) {
                        if (data && data.data) {
                            //isAnalyzed未true时，显示output按钮
                            $scope.isAnalyzed = false;
                            $scope.outputInfo = data.data;
                        }
                    })
                }

                //output查看与下载
                $scope.outputLink = function () {
                    $scope.isOutput = true;
                    $scope.modal('scriptConsole', true);
                    $scope.fileNavigator.outputLink($scope.runId);
                    $scope.config.appName = 'Output Detail'
                }

                //从output页面回退到最开始的script页面
                $scope.goBackScript = function () {
                    $scope.isOutput = false;
                    $scope.fileNavigator.goBackScript();
                    $scope.config.appName = rewriteConfig() ? 'SourceCode Manager' : 'Custom Script';
                }

                //点击关闭按钮
                $scope.closeScriptCom = function (flag) {
                    //custom的history点击view跳转到console（log），关闭console（log）页面时，从新打开history页面
                    if (flag) {
                        $scope.modal('scriptHistory');
                    }
                }

                //custom: history
                $scope.history = function () {
                    //打开scriptHistory弹框
                    $scope.apiMiddleware.customScriptHistory().then(function (data) {
                        if (data && data.data && data.data.length > 0) {
                            $scope.historyTable = data.data;
                        } else {
                            $scope.historyTable = [{ 'runId': 'No data', 'scriptName': '', 'projectName': '', 'startTime': '', 'commandLineOptions': '' }];
                        }
                    });
                    $scope.modal('scriptHistory');
                }

                //custom: history点击view查看console（log）
                $scope.scriptHistoryDetail = function (runId, status) {
                    //关闭scriptHistory
                    $scope.modal('scriptHistory', true);
                    //打开scriptConsole弹框
                    $scope.modal('scriptConsole');
                    $scope.runId = runId;
                    switch (status) {
                        case 'S': $scope.isAnalyzed = true; break;
                        case 'P': $scope.isAnalyzed = false; break;
                        case 'F': $scope.isAnalyzed = false; break;
                        case 'NS': $scope.isAnalyzed = false; break;
                        default: ;
                    }
                    if (status === 'P') {
                        webSocket();
                    } else {
                        $scope.consoleLog = '';
                        $scope.apiMiddleware.scriptHistoryDetail(runId).then(function (data) {
                            if (data && data.data) {
                                $timeout(function () {
                                    $scope.loadShow = false;
                                });
                                $scope.scriptName = data.data.scriptName;
                                $scope.commandOpt.txt = data.data.commandLineOptions;
                                $scope.consoleLog = data.data.log ? data.data.log : "The project is not analyzed, please analyze it first.";
                                $scope.outputInfo = data.data.outPuts;
                            } else {
                                $scope.consoleLog = "The project is not analyzed, please analyze it first.";
                                $scope.isAnalyzed = false;
                            }
                        });
                    }
                    $scope.isHistoryLog = true;
                }
                $scope.deleteHistory = function (runId, scriptName) {
                    $scope.delScriptName = scriptName;
                    $scope.delRunid = runId;
                    $scope.modal('removeHistory');
                    $scope.removeHistory = function () {
                        $scope.apiMiddleware.deleteHistory(runId).then(function (data) {
                            $scope.history();
                            $scope.modal('removeHistory', true);
                        })
                    }
                };

                $scope.openImagePreview = function () {
                    var item = $scope.singleSelection();
                    $scope.apiMiddleware.apiHandler.inprocess = true;
                    $scope.modal('imagepreview', null, true)
                        .find('#imagepreview-target')
                        .attr('src', $scope.getUrl(item))
                        .unbind('load error')
                        .on('load error', function () {
                            $scope.apiMiddleware.apiHandler.inprocess = false;
                            $scope.$apply();
                        });
                };

                $scope.openEditItem = function () {
                    var item = $scope.singleSelection();
                    $scope.outputContent = '';
                    $scope.apiMiddleware.getContent(item, $scope.runId, $scope.isOutput).then(function (data) {
                        if (data.result) {
                            item.tempModel.content = item.model.content = data.result;
                        } else {
                            //后缀为csv
                            $scope.outputContent = data;
                        }
                    });
                    $scope.modal('edit');
                };

                $scope.modal = function (id, hide, returnElement) {
                    var element = angular.element('#' + id);
                    element.modal(hide ? 'hide' : 'show');
                    $scope.apiMiddleware.apiHandler.error = '';
                    $scope.apiMiddleware.apiHandler.asyncSuccess = false;
                    return returnElement ? element : true;
                };

                $scope.modalWithPathSelector = function (id) {
                    $rootScope.selectedModalPath = $scope.fileNavigator.currentPath;
                    return $scope.modal(id);
                };

                $scope.isInThisPath = function (path) {
                    var currentPath = $scope.fileNavigator.currentPath.join('/') + '/';
                    return currentPath.indexOf(path + '/') !== -1;
                };

                $scope.edit = function () {
                    $scope.apiMiddleware.edit($scope.singleSelection()).then(function () {
                        $scope.modal('edit', true);
                    });
                };

                $scope.changePermissions = function () {
                    $scope.apiMiddleware.changePermissions($scope.temps, $scope.temp).then(function () {
                        $scope.fileNavigator.refresh();
                        $scope.modal('changepermissions', true);
                    });
                };

                $scope.download = function (path, runId) {
                    if($scope.isOutput){
                        var item=$scope.singleSelection()?$scope.singleSelection():path;
                    }else{
                        var item = path ? path : $scope.singleSelection();
                    }
                    $scope.runId = runId ? runId : $scope.runId;
                    if (item) {
                        return $scope.apiMiddleware.download(item, $scope.runId, $scope.isOutput);
                    }
                    return $scope.apiMiddleware.downloadMultiple($scope.temps,$scope.runId, $scope.isOutput);
                };

                $scope.copy = function () {
                    var item = $scope.singleSelection();
                    if (item) {
                        var name = item.tempModel.name.trim();
                        var nameExists = $scope.fileNavigator.fileNameExists(name);
                        if (nameExists && validateSamePath(item)) {
                            $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                            return false;
                        }
                        if (!name) {
                            $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                            return false;
                        }
                    }
                    $scope.apiMiddleware.copy($scope.temps, $rootScope.selectedModalPath).then(function () {
                        $scope.fileNavigator.refresh();
                        $scope.modal('copy', true);
                    });
                };

                $scope.compress = function () {
                    var name = $scope.temp.tempModel.name.trim();
                    var nameExists = $scope.fileNavigator.fileNameExists(name);

                    if (nameExists && validateSamePath($scope.temp)) {
                        $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                        return false;
                    }
                    if (!name) {
                        $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                        return false;
                    }

                    $scope.apiMiddleware.compress($scope.temps, name, $rootScope.selectedModalPath).then(function () {
                        $scope.fileNavigator.refresh();
                        if (!$scope.config.compressAsync) {
                            return $scope.modal('compress', true);
                        }
                        $scope.apiMiddleware.apiHandler.asyncSuccess = true;
                    }, function () {
                        $scope.apiMiddleware.apiHandler.asyncSuccess = false;
                    });
                };

                $scope.extract = function () {
                    var item = $scope.temp;
                    // var name = $scope.temp.tempModel.name.trim();
                    //解压时原本有个input框，值为$scope.temp.tempModel.name，用来重新命名
                    var name = 'foldername'
                    var nameExists = $scope.fileNavigator.fileNameExists(name);

                    if (nameExists && validateSamePath($scope.temp)) {
                        $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                        return false;
                    }
                    if (!name) {
                        $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                        return false;
                    }

                    $scope.apiMiddleware.extract(item, name, $rootScope.selectedModalPath).then(function () {
                        $scope.fileNavigator.refresh();
                        if (!$scope.config.extractAsync) {
                            return $scope.modal('extract', true);
                        }
                        $scope.apiMiddleware.apiHandler.asyncSuccess = true;
                    }, function () {
                        $scope.apiMiddleware.apiHandler.asyncSuccess = false;
                    });
                };

                $scope.remove = function () {
                    $scope.apiMiddleware.remove($scope.temps).then(function () {
                        $scope.fileNavigator.refresh();
                        $scope.modal('remove', true);
                    });
                };

                $scope.move = function () {
                    var anyItem = $scope.singleSelection() || $scope.temps[0];
                    if (anyItem && validateSamePath(anyItem)) {
                        $scope.apiMiddleware.apiHandler.error = $translate.instant('error_cannot_move_same_path');
                        return false;
                    }
                    $scope.apiMiddleware.move($scope.temps, $rootScope.selectedModalPath).then(function () {
                        $scope.fileNavigator.refresh();
                        $scope.modal('move', true);
                    });
                };

                $scope.rename = function () {
                    var item = $scope.singleSelection();
                    var name = item.tempModel.name;
                    var samePath = item.tempModel.path.join('') === item.model.path.join('');
                    if (!name || (samePath && $scope.fileNavigator.fileNameExists(name))) {
                        $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                        return false;
                    }
                    $scope.apiMiddleware.rename(item).then(function () {
                        $scope.fileNavigator.refresh();
                        $scope.modal('rename', true);
                    });
                };

                $scope.createFolder = function () {
                    var item = $scope.singleSelection();
                    var name = item.tempModel.name;
                    if (!name || $scope.fileNavigator.fileNameExists(name)) {
                        return $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                    }
                    $scope.apiMiddleware.createFolder(item).then(function () {
                        $scope.fileNavigator.refresh();
                        $scope.modal('newfolder', true);
                    });
                };

                $scope.addForUpload = function ($files) {
                    $scope.uploadFileList = $scope.uploadFileList.concat($files);
                    $scope.modal('uploadfile');
                };

                $scope.removeFromUpload = function (index) {
                    $scope.uploadFileList.splice(index, 1);
                };

                $scope.uploadFiles = function () {
                    $scope.apiMiddleware.upload($scope.uploadFileList, $scope.fileNavigator.currentPath).then(function () {
                        $scope.fileNavigator.refresh();
                        $scope.uploadFileList = [];
                        $scope.modal('uploadfile', true);
                    }, function (data) {
                        var errorMsg = data.result && data.result.error || $translate.instant('error_uploading_files');
                        $scope.apiMiddleware.apiHandler.error = errorMsg;
                    });
                };

                $scope.cancelUpload = function () {
                    $scope.apiMiddleware.cancelUpload().then(function () {

                    }, function (data) {
                        var errorMsg = data && data.result && data.result.error || $translate.instant('error_uploading_files');
                        $scope.apiMiddleware.apiHandler.error = errorMsg;
                    })
                }

                $scope.getUrl = function (_item) {
                    return $scope.apiMiddleware.getUrl(_item);
                };

                //点击搜索按钮，input框自动聚焦
                $scope.dropDownMenuSearch = function () {
                    $timeout(function(){
                        angular.element('.dropDownMenuInp').focus();
                    })
                };

                var validateSamePath = function (item) {
                    var selectedPath = $rootScope.selectedModalPath.join('');
                    var selectedItemsPath = item && item.model.path.join('');
                    return selectedItemsPath === selectedPath;
                };

                var getQueryParam = function (param) {
                    var found = $window.location.search.substr(1).split('&').filter(function (item) {
                        return param === item.split('=')[0];
                    });
                    return found[0] && found[0].split('=')[1] || undefined;
                };

                $scope.changeLanguage(getQueryParam('lang'));
                $scope.isWindows = getQueryParam('server') === 'Windows';
                $scope.fileNavigator.refresh();

            }]);
})(angular);
