/**
 * 
 */
var yosaneServices = angular.module('yosaneServices', ['ngResource']);

yosaneServices.factory('restful', ['$resource',
  function($resource){
    return {
        getScanners : function(cb,errcb) {
            return $resource('/yosane/scanners', {}, {
              query: {method:'GET', params:{}, isArray:false}
            }).get({},cb, errcb);
        },
        scanImage : function(parameters,cb,errcb) {
            var url = parameters.url;
            return $resource(url, {}, {
                fetch: {method:'POST',params: { deviceOptions: [] } }
              }).fetch({},cb,errcb);
        },
        emailImage : function() {
            return $resource('/yosane/send/email/image',{}, {
               send : { method:'POST' }
            });
        },
        emailPdf : function() {
            return $resource('/yosane/send/email/pdf',{}, {
               send : { method:'POST' }
            });
        },        
        copyImage : function() {
            return $resource('/yosane/send/localfile/image',{}, {
                send : { method:'POST' }
             });
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
        defaultImage : 'yosane/assets/images/180x240.gif',
        scannedImage : 'yosane/assets/images/180x240.gif',
        scanners : [],
        status : "",
        emptyScannerList : [ { name : "", title : "No scanners available"}]
    };
}]);