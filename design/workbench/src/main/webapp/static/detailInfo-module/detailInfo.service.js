 angular.module('detailInfoModule')
.factory('detailInfoService', function() { // factory的名字和注入的方法
 var store = {};    //把我们定义的方法和数据都放到一个对象中，并且返回这个对象，这就是factory
 var info ={};
 
 store.setOptions= function(options){
	   info.options = options;
   } ; 
   
 store.getOptions= function(){
	 return info.options;
 }

 store.getFolderInfo = function(){
	 return info.folderInfo;
 }

 store.setFolderInfo = function(folderInfo){
	info.folderInfo = folderInfo;
 }

store.setScope = function(scope){
   info.scope = scope;
}

store.getScope = function(){
	return info.scope;
}

store.setStatus = function(status){
info.status = status;
}

store.getStatus = function(){
return info.status;
}

 return store;
});