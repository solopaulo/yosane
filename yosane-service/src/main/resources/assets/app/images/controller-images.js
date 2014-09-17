/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ImagesController',['$scope','$http','imageService',function($scope,$http,imageService) {
    $scope.images = imageService.images;
    
    $scope.preview = function(img) {
       alert('previewing '+img);
    };
    
    $scope.toggleSelected = function(idx) {
        if ( idx >= $scope.images.length ) {
            return;
        }
        $scope.images[idx].selected = !$scope.images[idx].selected;        
    };
    
    $scope.selectedClass = function(idx) {
        if ( idx >= $scope.images.length ) {
            return;
        }
        return $scope.images[idx].selected ? 'imageSelected' : '';
    }
}]);
