/**
 * 
 */
var yosaneServices = angular.module('yosaneServices', ['ngResource']);

yosaneServices.factory('restful', ['$resource',
  function($resource){
    return $resource('/scanners', {}, {
      query: {method:'GET', params:{}, isArray:false}
    });    
  }]);

yosaneServices.factory('imageService',[function() {
    return {
      images : [],
      clearImages : function() {
          images = [];
      },
      addImage : function(image) {
          this.images.push(image);
      }  
    };
}]);

yosaneServices.factory('scannerService',[function() {
    return {
        selected : "",
        currentScanner : {},
        scannedImage : 'http://placehold.it/180x240',
        scanners : []
    };
}]);