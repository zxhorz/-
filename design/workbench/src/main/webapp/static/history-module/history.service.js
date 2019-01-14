angular.module("historyModule").factory("historyUrlService", function ($state) {
    var infoData = {};   //把我们定义的方法和数据都放到一个对象中，并且返回这个对象，这就是factory
    var info = {};
    info.urlInfo = [];
    info.isHistory = 1;
    info.clickFlag = true;
    info.detailRecord={};
    info.codeSearch={};
    info.corpus={};
    info.codeBrowRecord={};
    info.custom={};
    // var index;
    var curIndex;

    //处理地址
    function dealUrl(url) {
        switch (url.split('/')[0]) {
            case 'detail': info.urlParams = url.split('/')[1]; $state.go(url.split('/')[0], { tab: info.detailRecord.tab }); break;
            case 'corpus': info.urlParams = url.split('/')[1]; $state.go(url.split('/')[0]); break;
            case 'cloneCode': info.urlParams = url.split('/')[1]; $state.go(url.split('/')[0]); break;
            case 'codeSearch': info.urlParams = url.split('/')[1]; $state.go(url.split('/')[0]); break;
            default: $state.go(url);
        }
    }
    //存储页面的某个tab
    infoData.getUrlParams = function () {
        return info.urlParams;
    }

    infoData.getHistoryIndex = function () {
        return info.historyIndex;
    }

    //clickFlag是true：表示正常打开页面  clickFlag是false：表示回退
    infoData.setClickFlag = function (clickFlag) {
        info.clickFlag = clickFlag;
    }
    infoData.getClickFlag = function () {
        return info.clickFlag;
    }

    //用来存储地址
    infoData.setUrlInfo = function (url) {
        if (info.clickFlag) {
            var pos = $.inArray(url, info.urlInfo);
            if (pos > -1) {
                info.urlInfo.splice(pos + 1);
                if (pos - curIndex > 1) {
                    info.urlInfo.splice(curIndex + 1, pos - curIndex - 1);
                }
            } else {
                info.urlInfo.push(url);
                info.historyIndex = info.urlInfo.indexOf(url);
                if (info.historyIndex - curIndex > 1) {
                    info.urlInfo.splice(curIndex + 1, info.historyIndex - curIndex - 1);
                }
            }
            info.historyIndex = info.urlInfo.indexOf(url);
            curIndex =info.historyIndex;
        }
    }
    infoData.getUrlInfo = function () {
        return info.urlInfo;
    }

    //实现回退
    infoData.goBackUrl = function () {
        if (info.historyIndex > 0) {
           info.historyIndex =info.historyIndex - 1;
        } else {
           info.historyIndex = 0;
        }
        curIndex =info.historyIndex;
        dealUrl(info.urlInfo[info.historyIndex]);
    }
    //前进
    infoData.goForwardUrl = function () {
        if (info.historyIndex === info.urlInfo.length - 1) {
           info.historyIndex = info.urlInfo.length - 1
        } else {
           info.historyIndex =info.historyIndex + 1;
        }
        curIndex =info.historyIndex;
        dealUrl(info.urlInfo[info.historyIndex]);
    }

    //system documentation
    infoData.setDetailRecord = function (detailRecord) {
        info.detailRecord = detailRecord;
    }
    infoData.getDetailRecord = function () {
        return info.detailRecord;
    }

    //cost
    infoData.setCost = function (cost) {
        info.cost = cost;
    }
    infoData.getCost = function () {
        return info.cost;
    }

    //code Browser
    infoData.setCodeBrowRecord = function (codeBrowRecord) {
        info.codeBrowRecord = codeBrowRecord;
    }
    infoData.getCodeBrowRecord = function () {
        return info.codeBrowRecord;
    }

    //code Search
    infoData.setCodeSearch = function (codeSearch) {
        info.codeSearch = codeSearch;
    }
    infoData.getCodeSearch = function () {
        return info.codeSearch;
    }

    //code corpus
    infoData.setCorpus = function (corpus) {
        info.corpus = corpus;
    }
    infoData.getCorpus = function () {
        return info.corpus;
    }
    //code custom
    infoData.setcustom = function (custom) {
        info.custom = custom;
    }
    infoData.getcustom = function () {
        return info.custom;
    }

    //code cloneCode
    infoData.setCloneCode = function (cloneCode) {
        info.cloneCode = cloneCode;
    }
    infoData.getCloneCode = function () {
        return info.cloneCode;
    }

    return infoData
});  