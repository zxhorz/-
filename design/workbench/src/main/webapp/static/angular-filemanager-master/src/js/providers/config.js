angular.module('FileManagerApp').provider('fileManagerConfig', function () {

    var values = {
        appName: 'SourceCode Manager',
        defaultLang: 'en',
        multiLang: true,

        listUrl: '/fileManager/list/',
        uploadUrl: '/fileManager/upload/',
        cancelUploadUrl: '/fileManager/cancelUpload/',
        renameUrl: '/fileManager/rename/',
        copyUrl: '/fileManager/copy/',
        moveUrl: '/fileManager/move/',
        removeUrl: '/fileManager/remove/',
        editUrl: '/fileManager/edit/',
        getContentUrl: '/fileManager/getContent/',
        createFolderUrl: '/fileManager/createFolder/',
        downloadFileUrl: '/fileManager/preview/',
        downloadMultipleUrl: '/fileManager/downloadMulti',
        compressUrl: '/fileManager/compress/',
        extractUrl: '/fileManager/extract/',
        permissionsUrl: '/fileManager/changePermissions/',
        scriptLaunchUrl: './customScript/customScriptLaunch',
        getProjectList: './project/list',
        addHistory:'./customScript/addHistory',
        scriptRun:'./customScript/run',
        getOutputList:'./customScript/getOutputList',
        customScriptHistory:'./customScript/customScriptHistory',
        scriptHistoryDetail:'./customScript/scriptHistoryDetail',
        deleteHistory:'./customScript/deleteHistory',
        basePath: '/',

        searchForm: true,
        sidebar: true,
        breadcrumb: true,
        allowedActions: {
            upload: true,
            rename: true,
            move: true,
            copy: true,
            edit: false,
            changePermissions: false,
            compress: true,
            compressChooseName: true,
            extract: true,
            download: true,
            downloadMultiple: true,
            preview: false,
            remove: true,
            createFolder: false,
            pickFiles: false,
            pickFolders: false
        },

        multipleDownloadFileName: 'angular-filemanager.zip',
        filterFileExtensions: [],
        showExtensionIcons: true,
        showSizeForDirectories: false,
        useBinarySizePrefixes: false,
        downloadFilesByAjax: true,
        previewImagesInModal: true,
        enablePermissionsRecursive: true,
        compressAsync: false,
        extractAsync: false,
        pickCallback: function (item) {
            var msg = 'Picked %s "%s" for external use'
                .replace('%s', item.type)
                .replace('%s', item.fullPath());
            window.alert(msg);
        },

            isEditableFilePattern: /\.(txt|json|py|diff?|patch|svg|csv|asc|cnf|cfg|conf|html?|.html|cfm|cgi|aspx?|ini|pl|py|md|css|cs|js|jsp|log|htaccess|htpasswd|gitignore|gitattributes|env|json|atom|eml|rss|markdown|sql|xml|xslt?|sh|rb|as|bat|cmd|cob|for|ftn|frm|frx|inc|lisp|scm|coffee|php[3-6]?|java|c|go|h|scala|vb|tmpl|lock|go|yml|yaml|tsv|lst)$/i,
            isImageFilePattern: /\.(jpe?g|gif|bmp|png|svg|tiff?)$/i,
            isExtractableFilePattern: /\.(gz|tar|7z|rar|g?zip)$/i,
            tplPath: 'templates'
        };

    return {
        $get: function () {
            return values;
        },
        set: function (constants) {
            angular.extend(values, constants);
        }
    };

});

