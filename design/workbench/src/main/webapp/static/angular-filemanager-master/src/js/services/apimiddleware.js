(function(angular) {
    'use strict';
    angular.module('FileManagerApp').service('apiMiddleware', ['$window', 'fileManagerConfig', 'apiHandler', 
        function ($window, fileManagerConfig, ApiHandler) {

        var ApiMiddleware = function() {
            this.apiHandler = new ApiHandler();
        };

        ApiMiddleware.prototype.getPath = function(arrayPath) {
            return '/' + arrayPath.join('/');
        };

        ApiMiddleware.prototype.getFileList = function(files) {
            return (files || []).map(function(file) {
                return file && file.model.fullPath();
            });
        };

        ApiMiddleware.prototype.getFilePath = function(item) {
            return item && item.model.fullPath();
        };

        ApiMiddleware.prototype.list = function(path,runId,customDeferredHandler) {
            return this.apiHandler.list(fileManagerConfig.listUrl, this.getPath(path),runId,customDeferredHandler);
        };

        ApiMiddleware.prototype.copy = function(files, path) {
            var items = this.getFileList(files);
            var singleFilename = items.length === 1 ? files[0].tempModel.name : undefined;
            return this.apiHandler.copy(fileManagerConfig.copyUrl, items, this.getPath(path), singleFilename);
        };

        ApiMiddleware.prototype.move = function(files, path) {
            var items = this.getFileList(files);
            return this.apiHandler.move(fileManagerConfig.moveUrl, items, this.getPath(path));
        };

        ApiMiddleware.prototype.remove = function(files) {
            var items = this.getFileList(files);
            return this.apiHandler.remove(fileManagerConfig.removeUrl, items);
        };

        //custom: scriptLaunch
        ApiMiddleware.prototype.scriptLaunch = function(files) {
            var items = files.model.fullPath().split('/').splice(1);
            return this.apiHandler.scriptLaunch(fileManagerConfig.scriptLaunchUrl, items);
        };

        ApiMiddleware.prototype.getProjectList = function() {
            return this.apiHandler.getProjectList(fileManagerConfig.getProjectList);
        };

        //custom: scriptConsole
        ApiMiddleware.prototype.addHistory = function(projectId,scriptName,commandLineOptions) {
            return this.apiHandler.addHistory(fileManagerConfig.addHistory,projectId,scriptName,commandLineOptions);
        };

        ApiMiddleware.prototype.scriptRun = function(runId) {
            return this.apiHandler.scriptRun(fileManagerConfig.scriptRun,runId);
        };

        ApiMiddleware.prototype.getOutputList = function(runId) {
            return this.apiHandler.getOutputList(fileManagerConfig.getOutputList,runId);
        };

        ApiMiddleware.prototype.customScriptHistory = function() {
            return this.apiHandler.customScriptHistory(fileManagerConfig.customScriptHistory);
        };
        
        ApiMiddleware.prototype.scriptHistoryDetail = function(itemId) {
            return this.apiHandler.scriptHistoryDetail(fileManagerConfig.scriptHistoryDetail,itemId);
        };

        ApiMiddleware.prototype.deleteHistory = function(itemId) {
            return this.apiHandler.deleteHistory(fileManagerConfig.deleteHistory,itemId);
        };

        ApiMiddleware.prototype.upload = function(files, path) {
            if (! $window.FormData) {
                throw new Error('Unsupported browser version');
            }

            var destination = this.getPath(path);

            return this.apiHandler.upload(fileManagerConfig.uploadUrl, destination, files);
        };

        ApiMiddleware.prototype.cancelUpload = function() {
            return this.apiHandler.cancelUpload(fileManagerConfig.cancelUploadUrl);
        };

        ApiMiddleware.prototype.getContent = function(item,runId,isOutput) {
            var itemPath = this.getFilePath(item);
            return this.apiHandler.getContent(fileManagerConfig.getContentUrl, itemPath,runId,isOutput);
        };

        ApiMiddleware.prototype.edit = function(item) {
            var itemPath = this.getFilePath(item);
            return this.apiHandler.edit(fileManagerConfig.editUrl, itemPath, item.tempModel.content);
        };

        ApiMiddleware.prototype.rename = function(item) {
            var itemPath = this.getFilePath(item);
            var newPath = item.tempModel.fullPath();

            return this.apiHandler.rename(fileManagerConfig.renameUrl, itemPath, newPath);
        };

        ApiMiddleware.prototype.getUrl = function(item) {
            var itemPath = this.getFilePath(item);
            return this.apiHandler.getUrl(fileManagerConfig.downloadFileUrl, itemPath);
        };

        ApiMiddleware.prototype.download = function(item, runId,isOutput, forceNewWindow) {
            //TODO: add spinner to indicate file is downloading
            if(item.model){
                var itemPath = this.getFilePath(item);
                var toFilename = item.model.name;
            }else{
                var itemPath = '/';
                var toFilename = '';
                isOutput=true;
            }

            // if (item.isFolder()) {
            //     return;
            // }
            
            return this.apiHandler.download(
                fileManagerConfig.downloadFileUrl,
                itemPath,
                toFilename,
                fileManagerConfig.downloadFilesByAjax,
                runId,
                isOutput,
                forceNewWindow
            );
        };

        ApiMiddleware.prototype.downloadMultiple = function(files,runId,isOutput,forceNewWindow) {
            var items = this.getFileList(files);
            // var timestamp = new Date().getTime().toString().substr(8, 13);
            // var toFilename = timestamp + '-' + fileManagerConfig.multipleDownloadFileName;
            // var toFileName ='script';
            
            return this.apiHandler.downloadMultiple(
                fileManagerConfig.downloadMultipleUrl, 
                items,
                // toFileName,
                fileManagerConfig.downloadFilesByAjax,
                forceNewWindow,
                runId,
                isOutput,
            );
        };

        ApiMiddleware.prototype.compress = function(files, compressedFilename, path) {
            var items = this.getFileList(files);
            return this.apiHandler.compress(fileManagerConfig.compressUrl, items, compressedFilename, this.getPath(path));
        };

        ApiMiddleware.prototype.extract = function(item, folderName, path) {
            var itemPath = this.getFilePath(item);
            return this.apiHandler.extract(fileManagerConfig.extractUrl, itemPath, folderName, this.getPath(path));
        };

        ApiMiddleware.prototype.changePermissions = function(files, dataItem) {
            var items = this.getFileList(files);
            var code = dataItem.tempModel.perms.toCode();
            var octal = dataItem.tempModel.perms.toOctal();
            var recursive = !!dataItem.tempModel.recursive;

            return this.apiHandler.changePermissions(fileManagerConfig.permissionsUrl, items, code, octal, recursive);
        };

        ApiMiddleware.prototype.createFolder = function(item) {
            var path = item.tempModel.fullPath();
            return this.apiHandler.createFolder(fileManagerConfig.createFolderUrl, path);
        };

        return ApiMiddleware;

    }]);
})(angular);