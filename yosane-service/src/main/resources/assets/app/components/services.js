/**
 * 
 */
var yosaneServices = angular.module('yosaneServices', ['ngResource']);

yosaneServices.factory('yosaneServices', ['$resource',
  function($resource){
    return $resource('/scanners', {}, {
      query: {method:'GET', params:{}, isArray:false}
    });
    
  }]);