(function(angular) {
    'use strict';
    angular.module('DormManagerApp').factory('item', ['dormManagerConfig', 'chmod', function(dormManagerConfig, Chmod) {

        var Item = function(model, path) {
            var rawModel = {
                name: model && model.name || '',
                path: path || [],
                type: model && model.type || 'file',
                size: model && parseInt(model.size || 0),
                date: parseMySQLDate(model && model.date),
                perms: new Chmod(model && model.rights),
                content: model && model.content || '',
                recursive: false,
                fullPath: function() {
                    var path = this.path.filter(Boolean);
                    return ('/' + path.join('/') + '/' + this.name).replace(/\/\//, '/');
                }
            };

            this.error = '';
            this.processing = false;

            this.model = angular.copy(rawModel);
            this.tempModel = angular.copy(rawModel);

            function parseMySQLDate(mysqlDate) {
                var d = (mysqlDate || '').toString().split(/[- :]/);
                return new Date(d[0], d[1] - 1, d[2], d[3], d[4], d[5]);
            }
        };

        Item.prototype.update = function() {
            angular.extend(this.model, angular.copy(this.tempModel));
        };

        Item.prototype.revert = function() {
            angular.extend(this.tempModel, angular.copy(this.model));
            this.error = '';
        };

        Item.prototype.isFolder = function() {
            return this.model.type === 'dir';
        };

        Item.prototype.isEditable = function() {
            return !this.isFolder() && dormManagerConfig.isEditableFilePattern.test(this.model.name);
        };

        Item.prototype.isDocument = function() {
            return !this.isFolder() && dormManagerConfig.isDocumentFilePattern.test(this.model.name);
        };

        Item.prototype.isImage = function() {
            return dormManagerConfig.isImageFilePattern.test(this.model.name);
        };

        Item.prototype.isCompressible = function() {
            return this.isFolder();
        };

        Item.prototype.isExtractable = function() {
            return !this.isFolder() && dormManagerConfig.isExtractableFilePattern.test(this.model.name);
        };

        Item.prototype.isSelectable = function() {
            return (this.isFolder() && dormManagerConfig.allowedActions.pickFolders) || (!this.isFolder() && dormManagerConfig.allowedActions.pickFiles);
        };

        return Item;
    }]);
})(angular);