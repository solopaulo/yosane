/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ImagesController',['$scope','$http','imageService',function($scope,$http,imageService) {
    $scope.images = imageService.images;    
}]);
