angular.module("historyModule").factory("historyUrlService", function ($state) {
    var infoData = {};   //把我们定义的方法和数据都放到一个对象中，并且返回这个对象，这就是factory
    var info = {};
    info.urlInfo = [];
    info.isHistory = 1;
    info.clickFlag = true;
    info.detailRecord={};
    info.codeSearch={};
    info.codeBrowRecord={};
    var index;
    var curIndex;

    //处理地址

    function dealUrl(url) {
        switch (url.split('/')[0]) {
            // case 'detail': info.urlParams = url.split('/')[1]; $state.go(url.split('/')[0], { tab: url.split('/')[1] }); break;
            case 'detail': info.urlParams = url.split('/')[1]; $state.go(url.split('/')[0], { tab: info.detailRecord.tab }); break;
            case 'corpus': info.urlParams = url.split('/')[1]; $state.go(url.split('/')[0]); break;
            case 'cloneCode': info.urlParams = url.split('/')[1]; $state.go(url.split('/')[0]); break;
            case 'codeSearch': info.urlParams = url.split('/')[1]; $state.go(url.split('/')[0]); break;
            default: $state.go(url);
        }
    }
    infoData.getUrlParams = function () {
        return info.urlParams;
    }

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
                index = info.urlInfo.indexOf(url);
                if (index - curIndex > 1) {
                    info.urlInfo.splice(curIndex + 1, index - curIndex - 1);
                }
            }
            index = info.urlInfo.indexOf(url);
            curIndex = index;
        }
    }
    infoData.getUrlInfo = function () {
        return info.urlInfo;
    }

    infoData.goBackUrl = function () {
        if (index > 0) {
            index = index - 1;
        } else {
            index = 0;
        }
        curIndex = index;
        dealUrl(info.urlInfo[index]);
    }
    infoData.goForwardUrl = function () {
        if (index === info.urlInfo.length - 1) {
            index = info.urlInfo.length - 1
        } else {
            index = index + 1;
        }
        curIndex = index;
        dealUrl(info.urlInfo[index]);
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

    //code Search
    infoData.setCorpus = function (corpus) {
        info.corpus = corpus;
    }
    infoData.getCorpus = function () {
        return info.corpus;
    }

    return infoData
});  