'use strict';
angular.module('corpusModule')
    .controller('corpusController', function ($scope, $http, $timeout, $modal, $state, infoDataService, historyUrlService, $rootScope) {
        infoDataService.setPage('corpus');
        $scope.gridOptionsAbbr = {
            columnDefs: [
                {
                    field: 'abbr', displayName: 'Abbr.', width: '6%', headerTooltip: 'Abbr.',
                    cellTooltip: function (row, col) {
                        return row.entity.abbr;
                    }
                },
                {
                    field: 'fullPhrase', displayName: 'Full Phrase', width: '20%', headerTooltip: 'Full Phrase',
                    cellTooltip: function (row, col) {
                        return row.entity.fullPhrase;
                    }
                },
                {
                    field: 'businessDomain', displayName: 'Business Domain', width: '9%', headerTooltip: 'Business Domain',
                    cellTooltip: function (row, col) {
                        return row.entity.businessDomain;
                    }
                },
                {
                    field: 'businessDomainRank', displayName: 'Business Domain Rank', width: '10%', headerTooltip: 'Business Domain Rank',
                    cellTooltip: function (row, col) {
                        return row.entity.businessDomainRank;
                    }
                },
                {
                    field: 'organization', displayName: 'Organization', width: '9%', headerTooltip: 'Organization',
                    cellTooltip: function (row, col) {
                        return row.entity.organization;
                    }
                },
                {
                    field: 'organizationRank', displayName: 'Organization Rank', width: '10%', headerTooltip: 'Organization Rank',
                    cellTooltip: function (row, col) {
                        return row.entity.organizationRank;
                    }
                },
                {
                    field: 'system', displayName: 'System', width: '6%', headerTooltip: 'System',
                    cellTooltip: function (row, col) {
                        return row.entity.system;
                    }
                },
                {
                    field: 'systemRank', displayName: 'System Rank', width: '6%', headerTooltip: 'System Rank',
                    cellTooltip: function (row, col) {
                        return row.entity.systemRank;
                    }
                },
                {
                    field: 'codeType', displayName: 'Code Type', width: '6%', headerTooltip: 'Code Type',
                    cellTooltip: function (row, col) {
                        return row.entity.codeType;
                    }
                },
                {
                    field: 'frequency', displayName: 'Frequency', width: '8%', headerTooltip: 'Frequency',
                    cellTooltip: function (row, col) {
                        return row.entity.frequency;
                    }
                },
                {
                    field: 'generalRank', displayName: 'General Rank', width: '6%', headerTooltip: 'General Rank',
                    cellTooltip: function (row, col) {
                        return row.entity.generalRank;
                    }
                },
                {
                    field: 'icons', displayName: '',
                    cellTemplate: "<div class=\"ui-grid-cell-contents ng-binding ng-scope\" ng-show=\"row.entity.abbr!=='No data'\"><i class=\"iconInfo fa fa-pencil-square-o\" ng-click=\"grid.appScope.corpusEdit(row.entity)\"></i></div>", width: '2%'
                },
                {
                    field: 'icons', displayName: '',
                    cellTemplate: "<div class=\"ui-grid-cell-contents ng-binding ng-scope\" ng-show=\"row.entity.abbr!=='No data'\"><i class=\"iconInfo fa fa-trash-o\" ng-click=\"grid.appScope.delete(rowRenderIndex,row.entity)\"></i></div>", width: '2%'
                }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableHorizontalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

            enablePagination: true, //是否分页，默认为true
            enablePaginationControls: true, //使用默认的底部分页
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: 12, //每页显示个数
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            onRegisterApi: function (gridApiAbbr) {
                $scope.gridApiAbbr = gridApiAbbr;
                //分页按钮事件
                gridApiAbbr.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                    if (queryAbbrCorpus && historyUrlService.getClickFlag() && addDataInfo) {
                        queryAbbrCorpus(newPage, pageSize);
                        corpus.abbr.page = newPage;
                        historyUrlService.setCorpus(corpus);
                    }
                });
            }
        };
        $scope.gridOptionsAbbr.noUnselect = true;

        $scope.gridOptionsPhrase = {
            columnDefs: [
                {
                    field: 'phrase', displayName: 'Phrase', width: '14%', headerTooltip: 'General',
                    cellTooltip: function (row, col) {
                        return row.entity.phrase;
                    }
                },
                {
                    field: 'tag', displayName: 'Tag', width: '12%', headerTooltip: 'General',
                    cellTooltip: function (row, col) {
                        return row.entity.tag;
                    }
                },
                {
                    field: 'businessDomain', displayName: 'Business Domain', width: '9%', headerTooltip: 'Business Domain',
                    cellTooltip: function (row, col) {
                        return row.entity.businessDomain;
                    }
                },
                {
                    field: 'businessDomainRank', displayName: 'Business Domain Rank', width: '10%', headerTooltip: 'Business Domain Rank',
                    cellTooltip: function (row, col) {
                        return row.entity.businessDomainRank;
                    }
                },
                {
                    field: 'organization', displayName: 'Organization', width: '9%', headerTooltip: 'Organization',
                    cellTooltip: function (row, col) {
                        return row.entity.organization;
                    }
                },
                {
                    field: 'organizationRank', displayName: 'Organization Rank', width: '10%', headerTooltip: 'Organization Rank',
                    cellTooltip: function (row, col) {
                        return row.entity.organizationRank;
                    }
                },
                {
                    field: 'system', displayName: 'System', width: '6%', headerTooltip: 'System',
                    cellTooltip: function (row, col) {
                        return row.entity.system;
                    }
                },
                {
                    field: 'systemRank', displayName: 'System Rank', width: '6%', headerTooltip: 'System Rank',
                    cellTooltip: function (row, col) {
                        return row.entity.systemRank;
                    }
                },
                {
                    field: 'codeType', displayName: 'Code Type', width: '6%', headerTooltip: 'Code Type',
                    cellTooltip: function (row, col) {
                        return row.entity.codeType;
                    }
                },
                {
                    field: 'frequency', displayName: 'Frequency', width: '8%', headerTooltip: 'Frequency',
                    cellTooltip: function (row, col) {
                        return row.entity.frequency;
                    }
                },
                {
                    field: 'generalRank', displayName: 'General Rank', width: '6%', headerTooltip: 'General Rank',
                    cellTooltip: function (row, col) {
                        return row.entity.generalRank;
                    }
                },
                {
                    field: 'icons', displayName: '',
                    cellTemplate: "<div class=\"ui-grid-cell-contents ng-binding ng-scope\" ng-show=\"row.entity.phrase!=='No data'\"><i class=\"iconInfo fa fa-pencil-square-o\" ng-click=\"grid.appScope.corpusEdit(row.entity)\"></i></div>", width: '2%'
                },
                {
                    field: 'icons', displayName: '',
                    cellTemplate: "<div class=\"ui-grid-cell-contents ng-binding ng-scope\" ng-show=\"row.entity.phrase!=='No data'\"><i class=\"iconInfo fa fa-trash-o\" ng-click=\"grid.appScope.delete(rowRenderIndex,row.entity)\"></i></div>", width: '2%'
                }
            ],
            enableSorting: false,
            enableVerticalScrollbar: 0,
            enableHorizontalScrollbar: 0,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            rowTemplate: "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell></div>",

            enablePagination: true, //是否分页，默认为true
            enablePaginationControls: true, //使用默认的底部分页
            paginationCurrentPage: 1, //当前页码
            paginationPageSize: 12, //每页显示个数
            totalItems: 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            onRegisterApi: function (gridApiPhrase) {
                $scope.gridApiPhrase = gridApiPhrase;
                //分页按钮事件
                gridApiPhrase.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                    if (queryWordPhraseCorpus && historyUrlService.getClickFlag() && addDataInfo) {
                        queryWordPhraseCorpus(newPage, pageSize);
                        corpus.phase.page = newPage;
                        historyUrlService.setCorpus(corpus);
                    }
                });
            }
        }
        $scope.gridOptionsPhrase.noUnselect = true;

        $scope.selectedDomainWord = -1;
        $scope.selectedOrganizationWord = -1;
        $scope.selectedSystemWord = -1;
        $scope.selectedDomainAbbr = -1;
        $scope.selectedOrganizationAbbr = -1;
        $scope.selectedSystemAbbr = -1;
        $scope.deteleInfo = false;
        initSelectCondition();
        var addDataInfo = true;
        var corpus = {};
        corpus.abbr = {};
        corpus.phase = {};
        if (historyUrlService.getClickFlag()) {
            $scope.corpusNavTxt = 'abbreviation_dict';
            historyUrlService.setUrlInfo('corpus/' + "abbreviation_dict");
            historyUrlService.setClickFlag(true);
            corpus.abbr.selectedDomain = -1;
            corpus.abbr.selectedOrganization = -1;
            corpus.abbr.selectedSystem = -1;
            historyUrlService.setCorpus(corpus);
            getAbbrCorpus($scope.gridOptionsAbbr.paginationCurrentPage, $scope.gridOptionsAbbr.paginationPageSize);
            $rootScope.hisRecode = $rootScope.hisRecode + 1;
        }
        $scope.changeTab = function (event) {
            changeTabItem(event);
        }

        function changeTabItem(event) {
            $scope.corpusNavTxt = event;
            angular.element('#' + event).siblings("li").removeClass("active");
            angular.element('#' + event).addClass("active");
            if (event === 'abbreviation_dict') {
                historyUrlService.setUrlInfo('corpus/' + "abbreviation_dict");
                if (!angular.isDefined($scope.abbrList)) {
                    getAbbrCorpus($scope.gridOptionsAbbr.paginationCurrentPage, $scope.gridOptionsAbbr.paginationPageSize);
                    $scope.selectedDomain = -1;
                    $scope.selectedOrganization = -1;
                    $scope.selectedSystem = -1;
                } else {
                    $scope.selectedDomain = $scope.selectedDomainAbbr;
                    $scope.selectedOrganization = $scope.selectedOrganizationAbbr;
                    $scope.selectedSystem = $scope.selectedSystemAbbr;
                }
                corpus.abbr.selectedDomain = $scope.selectedDomain;
                corpus.abbr.selectedOrganization = $scope.selectedOrganization;
                corpus.abbr.selectedSystem = $scope.selectedSystem;
                historyUrlService.setCorpus(corpus);
            } else if (event === 'word_phase') {
                historyUrlService.setUrlInfo('corpus/' + "word_phase");
                if (!angular.isDefined($scope.phaseList)) {
                    getWordPhraseCorpus($scope.gridOptionsPhrase.paginationCurrentPage, $scope.gridOptionsPhrase.paginationPageSize);
                    $scope.selectedDomain = -1;
                    $scope.selectedOrganization = -1;
                    $scope.selectedSystem = -1;
                } else {
                    $scope.selectedDomain = $scope.selectedDomainWord;
                    $scope.selectedOrganization = $scope.selectedOrganizationWord;
                    $scope.selectedSystem = $scope.selectedSystemWord;
                }
                corpus.phase.selectedDomain = $scope.selectedDomain;
                corpus.phase.selectedOrganization = $scope.selectedOrganization;
                corpus.phase.selectedSystem = $scope.selectedSystem;
                historyUrlService.setCorpus(corpus);
            }
        }

        $scope.selectAbbr = function (index, event) {
            $scope.abbrSelected = event;
            $scope.abbrIndex = index;
            angular.element('#abbr' + event.id).addClass("trColor").siblings("tr").removeClass("trColor");
        }

        $scope.selectPhase = function (index, event) {
            $scope.phaseSelected = event;
            $scope.phaseIndex = index;
            angular.element('#phase' + event.id).addClass("trColor").siblings("tr").removeClass("trColor");
        }

        $scope.corpusEdit = function (event) {
            if ($scope.corpusNavTxt === 'abbreviation_dict') {
                $scope.modalInstance = $modal.open({
                    backdrop: 'static',
                    templateUrl: 'corpus-module/corpus.abbr.modal.html',
                    controller: 'corpusAbbrModalCtrl',
                    resolve: {
                        abbrData: function () {
                            return event;
                        },
                        action: function () {
                            return 'Edit';
                        },
                        title: function () {
                            return 'Edit abbreviation dictionary corpus '
                        },
                        parentscope: function () {
                            return $scope;
                        },
                        lastRecord: function () {
                            return;
                        }
                    }
                })
            } else if ($scope.corpusNavTxt === "word_phase") {
                $scope.modalInstance = $modal.open({
                    templateUrl: 'corpus-module/corpus.phrase.modal.html',
                    controller: 'corpusPhraseModalCtrl',
                    resolve: {
                        phaseData: function () {
                            return event;
                        },
                        action: function () {
                            return 'Edit';
                        },
                        title: function () {
                            return 'Edit phrase dictionary corpus '
                        },
                        parentscope: function () {
                            return $scope;
                        },
                        lastRecord: function () {
                            return;
                        }
                    }
                })
            }
        }

        $scope.corpusNew = function () {
            if ($scope.corpusNavTxt === 'abbreviation_dict') {

                $scope.modalInstance = $modal.open({
                    templateUrl: 'corpus-module/corpus.abbr.modal.html',
                    controller: 'corpusAbbrModalCtrl',
                    resolve: {
                        abbrData: function () {
                            return;
                        },
                        action: function () {
                            return 'New';
                        },
                        title: function () {
                            return 'New abbreviation dictionary corpus '
                        },
                        parentscope: function () {
                            return $scope;
                        },
                        lastRecord: function () {
                            return;
                        }
                    }
                })
            } else if ($scope.corpusNavTxt === "word_phase") {
                $scope.modalInstance = $modal.open({
                    templateUrl: 'corpus-module/corpus.phrase.modal.html',
                    controller: 'corpusPhraseModalCtrl',
                    resolve: {
                        phaseData: function () {
                            return;
                        },
                        action: function () {
                            return 'New';
                        },
                        title: function () {
                            return 'New phrase dictionary corpus '
                        },
                        parentscope: function () {
                            return $scope;
                        },
                        lastRecord: function () {
                            return;
                        }
                    }
                })
            }

        }

        $scope.corpusImport = function () {
            $scope.modalInstance = $modal.open({
                templateUrl: 'corpus-module/corpusImport.html',
                controller: 'corpusModalCtrl',
                resolve: {
                    oriScope: function () {
                        return $scope;
                    }
                }
            })
        }

        $scope.corpusExport = function () {
            var urlPrefix;
            if ($scope.corpusNavTxt === 'abbreviation_dict') {
                urlPrefix = "/corpus/exportAbbrDictCorpus?";
                window.location.href = urlPrefix + "&domain=" + $scope.selectedDomainAbbr
                    + "&organization=" + $scope.selectedOrganizationAbbr + "&system=" + $scope.selectedSystemAbbr;
            } else if ($scope.corpusNavTxt === 'word_phase') {
                urlPrefix = "/corpus/exportWordAndPhraseTagCorpus?";
                window.location.href = urlPrefix + "&domain=" + $scope.selectedDomainWord
                    + "&organization=" + $scope.selectedOrganizationWord + "&system=" + $scope.selectedSystemWord;
            };
        }

        $scope.delete = function (index, row) {
            $scope.deteleInfo = true;
            $scope.delCancel = function () {
                $scope.deteleInfo = false;
            }
            $scope.delSave = function () {
                $scope.deteleInfo = false;
                var url;
                if ($scope.corpusNavTxt === 'abbreviation_dict') {
                    url = './corpus/deleteAbbCorpus';
                } else if ($scope.corpusNavTxt === 'word_phase') {
                    url = './corpus/deleteWordCorpus';
                };
                $http({
                    method: 'GET',
                    url: url,
                    params: {
                        "corpusId": row.id
                    }
                })
                    .success(
                    function (data) {
                        if (data) {
                            if ($scope.corpusNavTxt === 'abbreviation_dict') {
                                $scope.gridOptionsAbbr.data.splice(index, 1);
                                if ($scope.gridOptionsAbbr.data.length > 0 || $scope.gridOptionsAbbr.paginationCurrentPage === 1) {
                                    $scope.gridOptionsAbbr.paginationCurrentPage = $scope.gridOptionsAbbr.paginationCurrentPage;
                                } else {
                                    $scope.gridOptionsAbbr.paginationCurrentPage = $scope.gridOptionsAbbr.paginationCurrentPage - 1;
                                }
                                $scope.onModel.modelShow('success', 'success');
                                $scope.Search(false);
                            } else if ($scope.corpusNavTxt === 'word_phase') {
                                $scope.gridOptionsPhrase.data.splice(index, 1);
                                if ($scope.gridOptionsPhrase.data.length > 0 || $scope.gridOptionsPhrase.paginationCurrentPage === 1) {
                                    $scope.gridOptionsPhrase.paginationCurrentPage = $scope.gridOptionsPhrase.paginationCurrentPage;
                                } else {
                                    $scope.gridOptionsPhrase.paginationCurrentPage = $scope.gridOptionsPhrase.paginationCurrentPage - 1;
                                }
                                $scope.onModel.modelShow('success', 'success');
                                $scope.Search(false);
                            };
                        } else {
                            $scope.onModel.modelShow('error', 'error');
                        }
                    }).error(function (data) {
                        // $scope.domainList = [];
                        $scope.onModel.modelShow('error', data.message);
                    });
            }
        }

        $scope.Search = function (flag, index) {
            if ($scope.corpusNavTxt === 'abbreviation_dict') {
                if (flag) {
                    $scope.selectedDomainAbbr = $scope.selectedDomain;
                    $scope.selectedOrganizationAbbr = $scope.selectedOrganization;
                    $scope.selectedSystemAbbr = $scope.selectedSystem;
                    if (historyUrlService.getClickFlag()) {
                        corpus.abbr.selectedDomain = $scope.selectedDomain;
                        corpus.abbr.selectedOrganization = $scope.selectedOrganization;
                        corpus.abbr.selectedSystem = $scope.selectedSystem;
                        historyUrlService.setCorpus(corpus);
                    }
                }
                if (index) {
                    $scope.gridOptionsAbbr.paginationCurrentPage = index;
                    $scope.gridApiAbbr.pagination.getTotalPages=function(){
                        return index;
                    }
                }
                queryAbbrCorpus($scope.gridOptionsAbbr.paginationCurrentPage, $scope.gridOptionsAbbr.paginationPageSize);
            } else if ($scope.corpusNavTxt === "word_phase") {
                if (flag) {
                    $scope.selectedDomainWord = $scope.selectedDomain;
                    $scope.selectedOrganizationWord = $scope.selectedOrganization;
                    $scope.selectedSystemWord = $scope.selectedSystem;
                    if (historyUrlService.getClickFlag()) {
                        corpus.phase.selectedDomain = $scope.selectedDomain;
                        corpus.phase.selectedOrganization = $scope.selectedOrganization;
                        corpus.phase.selectedSystem = $scope.selectedSystem;
                        historyUrlService.setCorpus(corpus);
                    }
                }
                if (index) {
                    $scope.gridOptionsPhrase.paginationCurrentPage = index;
                    $scope.gridApiPhrase.pagination.getTotalPages=function(){
                        return index;
                    }
                }
                queryWordPhraseCorpus($scope.gridOptionsPhrase.paginationCurrentPage, $scope.gridOptionsPhrase.paginationPageSize);
            }
        }

        function initSelectCondition() {
            initDomainList();
            initSystemList();
            initOrganizationList();
        }

        function initDomainList() {
            $http({
                method: 'GET',
                url: './corpus/domainlist'
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        if (data.data.length > 0) {
                            data.data.unshift({ 'id': -1, 'domainName': 'ALL' });
                            $scope.domainList = data.data;
                            if (historyUrlService.getCorpus() && historyUrlService.getCorpus().abbr && historyUrlService.getCorpus().abbr.selectedDomain) {
                                $scope.selectedDomain = historyUrlService.getCorpus().abbr.selectedDomain;
                            } else if (historyUrlService.getCorpus() && historyUrlService.getCorpus().phase && historyUrlService.getCorpus().phase.selectedDomain) {
                                $scope.selectedDomain = historyUrlService.getCorpus().phase.selectedDomain;
                            } else {
                                $scope.selectedDomain = -1;
                            }
                            // $scope.selectedDomain = -1;
                        } else {
                            $scope.domainList = [];
                        }
                    } else {
                        $scope.domainList = [];
                    }
                }).error(function (data) {
                    $scope.domainList = [];
                    $scope.onModel.modelShow('error', data.message);
                });
        }

        function initSystemList() {
            $http({
                method: 'GET',
                url: './corpus/systemlist'
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        if (data.data.length > 0) {
                            data.data.unshift({ 'id': -1, 'systemName': 'ALL' });
                            $scope.systemList = data.data;
                            if (historyUrlService.getCorpus() && historyUrlService.getCorpus().abbr && historyUrlService.getCorpus().abbr.selectedSystem) {
                                $scope.selectedSystem = historyUrlService.getCorpus().abbr.selectedSystem;
                            } else if (historyUrlService.getCorpus() && historyUrlService.getCorpus().phase && historyUrlService.getCorpus().phase.selectedSystem) {
                                $scope.selectedSystem = historyUrlService.getCorpus().phase.selectedSystem;
                            } else {
                                $scope.selectedSystem = -1;
                            }
                            // $scope.selectedSystem = -1;
                        } else {
                            $scope.systemList = [];
                        }
                    } else {
                        $scope.systemList = [];
                    }
                }).error(function (data) {
                    $scope.systemList = [];
                    $scope.onModel.modelShow('error', data.message);
                });
        }

        function initOrganizationList() {
            $http({
                method: 'GET',
                url: './corpus/organizationlist'
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        if (data.data.length > 0) {
                            data.data.unshift({ 'id': -1, 'organizationName': 'ALL' });
                            $scope.organizationList = data.data;
                            if (historyUrlService.getCorpus() && historyUrlService.getCorpus().abbr && historyUrlService.getCorpus().abbr.selectedOrganization) {
                                $scope.selectedOrganization = historyUrlService.getCorpus().abbr.selectedOrganization;
                            } else if (historyUrlService.getCorpus() && historyUrlService.getCorpus().phase && historyUrlService.getCorpus().phase.selectedOrganization) {
                                $scope.selectedOrganization = historyUrlService.getCorpus().phase.selectedOrganization;
                            } else {
                                $scope.selectedOrganization = -1;
                            }
                            // $scope.selectedOrganization = -1;
                        } else {
                            $scope.organizationList = [];
                        }
                    } else {
                        $scope.organizationList = [];
                    }
                }).error(function (data) {
                    $scope.organizationList = [];
                    $scope.onModel.modelShow('error', data.message);
                });
        }

        function getAbbrCorpus(page, size) {
            $http({
                method: 'GET',
                url: './corpus/abbrlist',
                params: {
                    'page': page,
                    'size': size
                }
            }).success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptionsAbbr.data = data.data.content;
                        $scope.gridOptionsAbbr.totalItems = data.data.totalElements;
                    } else {
                        $scope.gridOptionsAbbr.data = [{
                            'abbr': 'No data', 'fullPhrase': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                            'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                        }];
                    }
                }).error(function (data) {
                    $scope.gridOptionsAbbr.data = [{
                        'abbr': 'No data', 'fullPhrase': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                        'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                    }];
                    $scope.onModel.modelShow('error', data.message);
                });
        }

        function getWordPhraseCorpus(page, size) {
            $http({
                method: 'GET',
                url: './corpus/wordphraselist',
                params: {
                    'page': page,
                    'size': size
                }
            })
                .success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptionsPhrase.data = data.data.content;
                        $scope.gridOptionsPhrase.totalItems = data.data.totalElements;
                    } else {
                        $scope.gridOptionsPhrase.data = [{
                            'phrase': 'No data', 'tag': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                            'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                        }];
                    }
                }).error(function (data) {
                    $scope.gridOptionsPhrase.data = [{
                        'phrase': 'No data', 'tag': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                        'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                    }];
                    $scope.onModel.modelShow('error', data.message);
                });
        }

        function queryAbbrCorpus(page, size) {
            $http({
                method: 'GET',
                url: './corpus/queryAbbrCorpus',
                params: {
                    'domain': $scope.selectedDomain,
                    'system': $scope.selectedSystem,
                    'organization': $scope.selectedOrganization,
                    'page': page,
                    'size': size
                }
            })
                .success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptionsAbbr.data = data.data.content;
                        $scope.gridOptionsAbbr.totalItems = data.data.totalElements;
                    } else {
                        $scope.gridOptionsAbbr.data = [{
                            'abbr': 'No data', 'fullPhrase': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                            'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                        }];
                        $scope.gridOptionsAbbr.totalItems = 1;
                    }
                    addDataInfo = true;
                }).error(function (data) {
                    $scope.gridOptionsAbbr.data = [{
                        'abbr': 'No data', 'fullPhrase': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                        'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                    }];
                    $scope.onModel.modelShow('error', data.message);
                });
        }


        function queryWordPhraseCorpus(page, size) {
            $http({
                method: 'GET',
                url: './corpus/queryWordPhraseCorpus',
                params: {
                    'domain': $scope.selectedDomain,
                    'system': $scope.selectedSystem,
                    'organization': $scope.selectedOrganization,
                    'page': page,
                    'size': size
                }
            })
                .success(
                function (data) {

                    if (data && data.data && data.data.content.length > 0) {
                        $scope.gridOptionsPhrase.data = data.data.content;
                        $scope.gridOptionsPhrase.totalItems = data.data.totalElements;
                    } else {
                        $scope.gridOptionsPhrase.data = [{
                            'phrase': 'No data', 'tag': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                            'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                        }];
                        $scope.gridOptionsPhrase.totalItems = 1;
                    }
                    addDataInfo = true;
                }).error(function (data) {
                    $scope.gridOptionsPhrase.data = [{
                        'phrase': 'No data', 'tag': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                        'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                    }];
                    $scope.onModel.modelShow('error', data.message);
                });
        }

        function dealData() {
            if ($scope.corpusNavTxt === 'abbreviation_dict') {
                var count = parseInt($scope.gridOptionsAbbr.totalItems / $scope.gridOptionsAbbr.paginationPageSize);
                count = count + 1;
                return count;
            } else if ($scope.corpusNavTxt === "word_phase") {
                var count = parseInt($scope.gridOptionsPhrase.totalItems / $scope.gridOptionsPhrase.paginationPageSize);
                count = count + 1;
                return count;
            }

        }

        $scope.edit = function (editData, urlInfo) {
            $scope.onModel.modelLoading('saving', 'saving......');
            $http({
                method: 'POST',
                url: urlInfo,
                data: editData
            })
                .success(
                function (data) {
                    $scope.onModel.modelShow('success', 'success');
                    $scope.Search(false);
                }).error(function (data) {
                    $scope.onModel.modelShow('error', data.message);
                });
        }
        $scope.addData = function (dataInfo, urlInfo) {
            $scope.onModel.modelLoading('saving', 'saving......');
            $http({
                method: 'POST',
                url: urlInfo,
                data: dataInfo
            })
                .success(
                function (data) {
                    if (data.code === 'NACK') {
                        $scope.onModel.modelShow('error', data.message);
                    } else {
                        addDataInfo = false;
                        $scope.onModel.modelShow('success', 'success');
                        $scope.Search(false, dealData());
                    }
                }).error(function (data) {
                    $scope.onModel.modelShow('error', data.message);
                });
        }

        if (infoDataService.getPage() === 'corpus') {
            var WatchEvent = $scope.$watch('hisRecode', function (newValue) {
                if (newValue !== undefined && historyUrlService.getClickFlag() === false) {
                    if (historyUrlService.getCorpus()) {
                        $scope.corpusNavTxt = historyUrlService.getUrlParams();
                        angular.element('#' + historyUrlService.getUrlParams()).siblings("li").removeClass("active");
                        angular.element('#' + historyUrlService.getUrlParams()).addClass("active");
                        $scope.gridOptionsAbbr.paginationCurrentPage = historyUrlService.getCorpus().abbr ? historyUrlService.getCorpus().abbr.page ? historyUrlService.getCorpus().abbr.page : 1 : 1;
                        $scope.gridOptionsPhrase.paginationCurrentPage = historyUrlService.getCorpus().phase ? historyUrlService.getCorpus().phase.page ? historyUrlService.getCorpus().phase.page : 1 : 1;
                        if ($scope.corpusNavTxt === 'abbreviation_dict' && historyUrlService.getCorpus().abbr && historyUrlService.getCorpus().abbr.selectedDomain) {
                            $scope.selectedDomain = historyUrlService.getCorpus().abbr.selectedDomain;
                            $scope.selectedOrganization = historyUrlService.getCorpus().abbr.selectedOrganization;
                            $scope.selectedSystem = historyUrlService.getCorpus().abbr.selectedSystem;
                            queryAbbrCorpus($scope.gridOptionsAbbr.paginationCurrentPage, $scope.gridOptionsAbbr.paginationPageSize);
                            //getAbbrCorpus($scope.gridOptionsAbbr.paginationCurrentPage, $scope.gridOptionsAbbr.paginationPageSize);
                        }
                        if ($scope.corpusNavTxt === 'word_phase' && historyUrlService.getCorpus().phase && historyUrlService.getCorpus().phase.selectedDomain) {
                            $scope.selectedDomain = historyUrlService.getCorpus().phase.selectedDomain;
                            $scope.selectedOrganization = historyUrlService.getCorpus().phase.selectedOrganization;
                            $scope.selectedSystem = historyUrlService.getCorpus().phase.selectedSystem;
                            queryWordPhraseCorpus($scope.gridOptionsPhrase.paginationCurrentPage, $scope.gridOptionsPhrase.paginationPageSize);
                            //getWordPhraseCorpus($scope.gridOptionsPhrase.paginationCurrentPage, $scope.gridOptionsPhrase.paginationPageSize);
                        }
                    } else {
                        changeTabItem(historyUrlService.getUrlParams());
                    }
                    historyUrlService.setClickFlag(true);
                }
            });
        } else {
            WatchEvent();
        }
    })
    .controller('corpusAbbrModalCtrl', function ($http, $scope, $modalInstance, abbrData, title, action, parentscope, lastRecord, $timeout, $modal) {
        $scope.abbrBlur = function (info) {
            if (info) {
                modelError("Please input letters with no more than 50 of the length.");
            }
        }
        $scope.modalTitle = title;
        initSelectContion();
        $scope.record = {};
        // 界面点击修改图标，第一次打开该条数据对应的edit模态框
        if (typeof (abbrData) != "undefined") {
            $scope.record = JSON.parse(JSON.stringify(abbrData));
        }
        // edit内容有误第二次（或多次）打开edit模态框
        if (typeof (lastRecord) != "undefined") {
            $scope.record = lastRecord;
        }

        if (action === 'Edit') {
            $scope.ok = function () {
                parentscope.edit($scope.record, './corpus/addOrModifyAbbCorpus');
                $modalInstance.close();
            }
        } else if (action === 'New') {
            $scope.record.id = 0;
            if (typeof ($scope.record.businessDomainId) == "undefined") {
                $scope.record.businessDomainId = 1;
            }
            if (typeof ($scope.record.organizationId) == "undefined") {
                $scope.record.organizationId = 1;
            }
            if (typeof ($scope.record.systemId) == "undefined") {
                $scope.record.systemId = 1;
            }
            $scope.ok = function () {
                parentscope.addData($scope.record, './corpus/addOrModifyAbbCorpus');
                $modalInstance.close();
            }
        }

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        }

        function reopenAbbrModal(title, action, time) {
            $timeout(function () {
                parentscope.modalInstance = $modal.open({
                    templateUrl: 'corpus-module/corpus.abbr.modal.html',
                    controller: 'corpusAbbrModalCtrl',
                    resolve: {
                        abbrData: function () {
                            return;
                        },
                        action: function () {
                            return action;
                        },
                        title: function () {
                            return title;
                        },
                        parentscope: function () {
                            return parentscope;
                        },
                        lastRecord: function () {
                            return $scope.record;
                        }
                    }
                })
            }, time);
        }

        function initSelectContion() {
            $http({
                method: 'GET',
                url: './corpus/domainlist'
            })
                .success(
                function (data) {

                    if (data && data.data) {
                        if (data.data.length > 0) {
                            $scope.domains = data.data;
                        } else {
                            $scope.domains = [];
                        }
                    } else {
                        $scope.domains = [];
                    }
                }).error(function (data) {
                    $scope.domains = [];
                    parentscope.onModel.modelShow('error', data.message);
                });

            $http({
                method: 'GET',
                url: './corpus/systemlist'
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        if (data.data.length > 0) {
                            $scope.systems = data.data;
                        } else {
                            $scope.systems = [];
                        }
                    } else {
                        $scope.systems = [];
                    }
                }).error(function (data) {
                    $scope.systems = [];
                    parentscope.onModel.modelShow('error', data.message);
                });

            $http({
                method: 'GET',
                url: './corpus/organizationlist'
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        if (data.data.length > 0) {
                            $scope.organizations = data.data;
                        } else {
                            $scope.organizations = [];
                        }
                    } else {
                        $scope.organizations = [];
                    }
                }).error(function (data) {
                    $scope.organizations = [];
                    parentscope.onModel.modelShow('error', data.message);
                });
        }
        function modelError(info) {
            $scope.errorInfo = true;
            $scope.promptInfo = info;
            $timeout(function () {
                $scope.errorInfo = false;
            }, 3000);

        }
    })
    .controller('corpusPhraseModalCtrl', function ($http, $scope, $modalInstance, phaseData, title, action, parentscope, lastRecord, $timeout, $modal) {
        $scope.phraseBlur = function (info) {
            if (info) {
                modelError("Please input letters with no more than 50 of the length.");
            }
        }
        $scope.modalTitle = title;
        initSelectContion();
        $scope.record = {};
        // 界面点击修改图标，第一次打开该条数据对应的edit模态框
        if (typeof (phaseData) != "undefined") {
            $scope.record = JSON.parse(JSON.stringify(phaseData));
        }
        // edit内容有误第二次（或多次）打开edit模态框
        if (typeof (lastRecord) != "undefined") {
            $scope.record = lastRecord;
        }

        if (action === 'Edit') {
            $scope.ok = function () {
                parentscope.edit($scope.record, './corpus/addOrModifyWordCorpus');
                $modalInstance.close();
            }
        } else if (action === 'New') {
            $scope.record.id = 0;
            if (typeof ($scope.record.businessDomainId) == "undefined") {
                $scope.record.businessDomainId = 1;
            }
            if (typeof ($scope.record.organizationId) == "undefined") {
                $scope.record.organizationId = 1;
            }
            if (typeof ($scope.record.systemId) == "undefined") {
                $scope.record.systemId = 1;
            }

            $scope.ok = function () {
                parentscope.addData($scope.record, './corpus/addOrModifyWordCorpus');
                $modalInstance.close();
            }
        }
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        }

        function reopenPhraseModal(title, action, time) {
            $timeout(function () {
                parentscope.modalInstance = $modal.open({
                    templateUrl: 'corpus-module/corpus.phrase.modal.html',
                    controller: 'corpusPhraseModalCtrl',
                    resolve: {
                        phaseData: function () {
                            return event;
                        },
                        action: function () {
                            return action;
                        },
                        title: function () {
                            return title;
                        },
                        parentscope: function () {
                            return $scope;
                        },
                        lastRecord: function () {
                            return $scope.record;
                        }
                    }
                })
            }, time);
        }

        function initSelectContion() {
            $http({
                method: 'GET',
                url: './corpus/domainlist'
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        if (data.data.length > 0) {
                            $scope.domains = data.data;
                        } else {
                            $scope.domains = [];
                        }
                    } else {
                        $scope.domains = [];
                    }
                }).error(function (data) {
                    $scope.domains = [];
                    parentscope.onModel.modelShow('error', data.message);
                });

            $http({
                method: 'GET',
                url: './corpus/systemlist'
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        if (data.data.length > 0) {
                            $scope.systems = data.data;
                        } else {
                            $scope.systems = [];
                        }
                    } else {
                        $scope.systems = [];
                    }
                }).error(function (data) {
                    $scope.systems = [];
                    parentscope.onModel.modelShow('error', data.message);
                });

            $http({
                method: 'GET',
                url: './corpus/organizationlist'
            })
                .success(
                function (data) {
                    if (data && data.data) {
                        if (data.data.length > 0) {
                            $scope.organizations = data.data;
                        } else {
                            $scope.organizations = [];
                        }
                    } else {
                        $scope.organizations = [];
                    }
                }).error(function (data) {
                    $scope.organizations = [];
                    parentscope.onModel.modelShow('error', data.message);
                });
        }

        function modelError(info) {
            $scope.errorInfo = true;
            $scope.promptInfo = info;
            $timeout(function () {
                $scope.errorInfo = false;
            }, 2000);

        }
    }).controller('corpusModalCtrl', function ($http, $scope, $timeout, $modal, $modalInstance, FileUploader, oriScope) {
        $scope.corpusNavTxt = oriScope.corpusNavTxt;
        $scope.paginationCurrentPagea = oriScope.gridOptionsAbbr.paginationCurrentPage;
        $scope.paginationPageSizea = oriScope.gridOptionsAbbr.paginationPageSize;
        $scope.paginationCurrentPagep = oriScope.gridOptionsPhrase.paginationCurrentPage;
        $scope.paginationPageSizep = oriScope.gridOptionsPhrase.paginationPageSize;

        $scope.ok = function () {
            $modalInstance.close();
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        }

        var uploader = $scope.uploader = new FileUploader({
            // 根据当前tab选择请求url
            url: './corpus/batchImport/' + oriScope.corpusNavTxt,
            method: 'POST'/*,*/
        });

        $scope.clearItems = function () {
            uploader.clearQueue();
        }

        uploader.onAfterAddingFile = function (fileItem) {
            var fileExt = fileItem._file.name.substring(fileItem._file.name.lastIndexOf('.') + 1);
            if (fileExt === 'csv' || fileExt === 'CSV') {
                $scope.fileItem = fileItem._file;
            } else {
                // close modal instance to show the error info.
                $modalInstance.close();
                // show error info.
                oriScope.onModel.modelShow('error', 'Please upload csv file!', 3000);
                // create a new modal instance. 
                reopenImportMoadl(3000);
            }
        };

        uploader.onSuccessItem = function (fileItem, response, status, headers) {
            $scope.uploadStatus = true;
            angular.element('#upload-btn').button('reset');
            if (response.data) {
                if ($scope.corpusNavTxt === 'abbreviation_dict') {
                    queryAbbrCorpus($scope.paginationCurrentPagea, $scope.paginationPageSizea);
                } else {
                    queryWordPhraseCorpus($scope.paginationCurrentPagep, $scope.paginationPageSizep);
                }
                var success = response.data.successCount;
                var failed = response.data.failedCount;
                if (failed > 0) {
                    $modalInstance.close();
                    oriScope.onModel.modelShow('success', success + ' rows successfully imported and ' + failed + ' rows failed!', 3000);
                } else {
                    if (success === 0) {
                        $modalInstance.close();
                        oriScope.onModel.modelShow('error', 'No data successfully imported!', 3000);
                    } else {
                        $modalInstance.close();
                        oriScope.onModel.modelShow('success', 'All rows successfully imported!', 3000);
                    }
                }
            } else {
                $modalInstance.close();
                oriScope.onModel.modelShow('error', 'Exception occured!', 3000);
            }
        };

        $scope.UploadFile = function () {
            angular.element('#upload-btn').button('importing');
            uploader.uploadAll();
        }

        function queryAbbrCorpus(page, size) {
            $http({
                method: 'GET',
                url: './corpus/queryAbbrCorpus',
                params: {
                    'domain': oriScope.selectedDomain,
                    'system': oriScope.selectedSystem,
                    'organization': oriScope.selectedOrganization,
                    'page': page,
                    'size': size
                }
            })
                .success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        oriScope.gridOptionsAbbr.data = data.data.content;
                        oriScope.gridOptionsAbbr.totalItems = data.data.totalElements;
                    } else {
                        oriScope.gridOptionsAbbr.data = [{
                            'abbr': 'No data', 'fullPhrase': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                            'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                        }];
                    }
                }).error(function (data) {
                    oriScope.gridOptionsAbbr = [];
                    $modalInstance.close();
                    oriScope.onModel.modelShow('error', data.message, 3000);
                });
        }

        function queryWordPhraseCorpus(page, size) {
            $http({
                method: 'GET',
                url: './corpus/queryWordPhraseCorpus',
                params: {
                    'domain': oriScope.selectedDomain,
                    'system': oriScope.selectedSystem,
                    'organization': oriScope.selectedOrganization,
                    'page': page,
                    'size': size
                }
            })
                .success(
                function (data) {
                    if (data && data.data && data.data.content.length > 0) {
                        oriScope.gridOptionsPhrase.data = data.data.content;
                        oriScope.gridOptionsPhrase.totalItems = data.data.totalElements;
                    } else {
                        oriScope.gridOptionsPhrase.data = [{
                            'phrase': 'No data', 'tag': '', 'businessDomain': '', 'organization': '', 'system': '', 'codeType': '',
                            'frequency': '', 'systemRank': '', 'organizationRank': '', 'businessDomainRank': '', 'generalRank': '', 'icons': '', 'icons': ''
                        }];
                    }
                }).error(function (data) {
                    oriScope.gridOptionsPhrase = [];
                    $modalInstance.close();
                    oriScope.onModel.modelShow('error', data.message, 3000);
                });
        }

        function reopenImportMoadl(time) {
            $timeout(function () {
                $scope.modalInstance = $modal.open({
                    templateUrl: 'corpus-module/corpusImport.html',
                    controller: 'corpusModalCtrl',
                    resolve: {
                        oriScope: function () {
                            return oriScope;
                        }
                    }
                })
            }, time);
        }

    });
