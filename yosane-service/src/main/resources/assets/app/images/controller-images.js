/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ImagesController',['$scope','$http','imageService',function($scope,$http,imageService) {
    $scope.images = imageService.images;
    
    $scope.preview = function(img) {
       alert('previewing '+img);
    };
}]);
