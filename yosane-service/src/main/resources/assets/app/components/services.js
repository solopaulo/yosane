/**
 * 
 */
var yosaneServices = angular.module('yosaneServices', ['ngResource']);

yosaneServices.factory('restful', ['$resource',
  function($resource){
    return {
        getScanners : function(callback) {
            return $resource('/scanners', {}, {
              query: {method:'GET', params:{}, isArray:false}
            }).get({},callback);
        },
        scanImage : function(parameters,callback) {
            var url = parameters.url;
            return $resource(url, {}, {
                fetch: {method:'POST',params: { deviceOptions: [] } }
              }).fetch({},callback);
        }
    };
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
        defaultImage : '/assets/images/180x240.gif',
        scannedImage : '/assets/images/180x240.gif',
        scanners : []
    };
}]);