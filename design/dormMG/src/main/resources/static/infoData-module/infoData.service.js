angular.module('infoDataModule')
    .factory('infoDataService', function ($http,$modal,$rootScope) {
        var infoData = {};   //把我们定义的方法和数据都放到一个对象中，并且返回这个对象，这就是factory
        var info = {};
        var user ={};
        $rootScope.isAdmin = false;
        infoData.setUser = function(user){
            info.user = user;
            if(user['role'] == 'root')
                $rootScope.isAdmin = true;
            else if(user['role'] == 'admin')
                $rootScope.isAdmin = true;
            else
                $rootScope.isAdmin = false;
        }

        infoData.getUser = function(){
            return user;
        }


        return infoData;
    });