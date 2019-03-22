angular.module('tooltipsModule', ['720kb.tooltips'])
  .config(['tooltipsConfProvider', function configConf(tooltipsConfProvider) {
    tooltipsConfProvider.configure({
      'size': 'large',
      'speed': 'slow'
    });
  }]);