angular.module('infoDataModule')
    .factory('infoDataService', function ($http, $websocket) { // factory的名字和注入的方法
        var infoData = {};   //把我们定义的方法和数据都放到一个对象中，并且返回这个对象，这就是factory
        var info = {};
        var progressBarWebSocket;
        var jobstatusWebSocket;
        var serverIp = "";
        info.page = "";
        info.projectSelect = "Select";
        info.reset = true;
        info.fromPage = 'INDEX';
        info.tabMap = {};
        info.tableInfo = {};
        info.id='0';
        infoData.handShakeWebSocket = function () {
            progressBarWebSocket = $websocket('ws://' + serverIp + ':8080/websocket/' + info.id);
            jobstatusWebSocket = $websocket('ws://' + serverIp + ':8080/websocket/jobstatus/' + info.id);
        }

        infoData.getWebSocket = function () {
            return progressBarWebSocket;
        }
        
        infoData.getJobStatusWebSocket = function () {
            return jobstatusWebSocket;
        }

        infoData.getServerIp = function(){
            return serverIp;
        }

        infoData.setServerIp = function(){
                $http({
                    method: 'GET',
                    url: './host/server/ip'
                }).success(function (data) {
                    serverIp = data.data;
                }).error(function (data) {
                    serverIp="";
                    console.info("get server ip error, websocket connection error.");
                });
        }

        infoData.setInfo = function () {//clean data info
            info.id = ''; //project Id
            info.name = ''; //project name
            info.description = ''; //project description
            info.sourcePath = ''; // project source code path
            info.detailInfo = ''; //detail page
            info.programInfo = ''; //program page  
            info.neo4jPath = ''; //neo4j path of current project
            info.selectedNames = []; // tag操作中需要的选中数据
            info.tagType = '';
        };

        infoData.setTagInfo = function () {
            info.selectedNames = [];
            info.tagType = '';
        }

        infoData.setTagType = function (tagType) {
            info.tagType = tagType;
        };

        infoData.setSelectedNames = function (selectedNames) {
            info.selectedNames = selectedNames;
        };
        infoData.setRowIndex = function (RowIndex) {    //detail表格 行的索引
            info.RowIndex = RowIndex;
        };
        infoData.setId = function (id) {  //project Id
            info.id = id;
        };

        infoData.setName = function (name) {//project name
            info.name = name;
        };

        infoData.setDescription = function (description) {  //project description
            info.description = description;
        };

        infoData.setSourcePath = function (sourcePath) {// project source code path
            info.sourcePath = sourcePath;
        };

        infoData.setTableInfo = function(tableInfo){
            info.tableInfo = tableInfo;
        }

        infoData.getTableInfo = function () {
            return info.tableInfo;
        }

        infoData.getTagType = function () {
            return info.tagType;
        }

        infoData.getSelectedNames = function () {
            return info.selectedNames;
        }
        infoData.getRowIndex = function () {
            return info.RowIndex;
        }
        infoData.getInfo = function () {
            return info;
        };

        infoData.getId = function () {//project Id
            return info.id;
        };

        infoData.getName = function () {//project name
            return info.name;
        };

        infoData.getDescription = function () { //project description
            return info.description;
        };

        infoData.getSourcePath = function () {// project source code path		   
            return info.sourcePath;
        };

        infoData.setDetailInfo = function (detailInfo) { //detail page
            info.detailInfo = detailInfo;
        };

        infoData.getDetailInfo = function () {//detail page		   
            return info.detailInfo;
        };

        infoData.setProgramInfo = function (programInfo) { //program page
            info.programInfo = programInfo;
        };

        infoData.getProgramInfo = function () {//program page        
            return info.programInfo;
        };

        infoData.setPage = function (page) { //current show page
            info.page = page;
        };

        infoData.getPage = function () {//current show page
            return info.page;
        };

        infoData.setProjectSelect = function (projectSelect) { //current tab of project page
            info.projectSelect = projectSelect;
        };

        infoData.getProjectSelect = function () {//current tab of project page
            return info.projectSelect;
        };

        infoData.getNeo4jPath = function () { //neo4j path of current project
            return info.neo4jPath;
        };

        infoData.setNeo4jPath = function (neo4jPath) {
            info.neo4jPath = neo4jPath;
        };

        infoData.setFromPage = function (fromPage) {
            info.fromPage = fromPage;
        }

        infoData.getFromPage = function () {
            return info.fromPage;
        }

        infoData.setTabMap = function(tabMap){
            //nodeName-> node name in neo4j
            //tableColumn -> visible name in summary table
            //tabName -> tab name in systen documentation
            info.tabMap = {};
            angular.forEach(tabMap,function(item){
                info.tabMap[item.nodeName] = {
                    tableColumn:item.tableColumn,
                    summaryName:item.summaryName,
                    tabName:item.tabName
                }
            })
        }

        infoData.getAllTabMap = function () {
            return info.tabMap;
        }

        infoData.getTabMap = function(key){
            if(key in info.tabMap){
                return info.tabMap[key];
            }else{
                
                return  {tableColumn:key,
                        summaryName:key,
                         tabName:key
                };
            }
        }
        infoData.setCloneFinished = function (value) {
            info.cloneFinished = value;
        }

        infoData.getCloneFinished = function () {
            return info.cloneFinished;
        }

        infoData.setLastModifiedTime = function(lastModifiedTime){
            info.lastModifiedTime = lastModifiedTime;
        }
        infoData.getLastModifiedTime = function(){
            return info.lastModifiedTime;
        }

        infoData.setCodeVersion = function(codeVersion){
            info.codeVersion = codeVersion;
        }

        infoData.getCodeVersion = function(){
            return info.codeVersion;
        }

        // neo4j bolt uri of current project
        infoData.getBoltUri = function () {
            return info.boltUri;
        };

        infoData.setBoltUri = function (boltUri) {
            info.boltUri = boltUri;
        };

        // auto tag result path
        infoData.getAutoTagPath = function () {
            return info.autoTagPath;
        };

        infoData.setAutoTagPath = function (autoTagPath) {
            info.autoTagPath = autoTagPath;
        };
        return infoData;
    });