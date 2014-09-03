/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ScanningController',['$scope','$http','restful','imageService',function($scope,$http,restful,imageService) {
    $scope.selected = "";
    $scope.currentScanner = undefined;
    $scope.scannedImage = 'http://placehold.it/180x240';
    
    $scope.refreshScannerList = function() {
        $scope.currentScanner = undefined;
        restful.get({},function(data) {
            $scope.items = data._links.scanner;
        }); 
    };
    $scope.selectScanner = function(name) {
        $scope.currentScanner = undefined;
        for ( var i = 0; i < $scope.items.length; i++) {
            if ( $scope.items[i].name != name ) {
                continue;
            }
            $scope.selected = name;
            $scope.currentScanner = $scope.items[i];
        }
    };
    $scope.refreshScannerList();
    $scope.scan = function() {
        if ( $scope.currentScanner === undefined ) {
            return;
        }
        $http.post($scope.currentScanner.href,[]).success( function(response) {
            setTimeout( function() { $scope.scanningProgress( response ); },0);
        });
    };
    
    $scope.scanningProgress = function(response) {
        var url = response._links.self.href;
        var status = response.status;
        console.log(status);
        setTimeout( function() { $scope.checkImageStatus(url); }, 500);      
    };

    $scope.checkImageStatus = function(url) {
        $http.get(document.location.origin+url).success( function(response) {
            console.log(response.status);
            if ( [undefined,'READY','MISSING','FAILED'].indexOf(response.status) >= 0 ) {
                if ( response.status == 'READY' ) {
                    setTimeout( function() { $scope.updateCompletedImage(response); },0);
                }
                return;
            } 
            setTimeout( function() { $scope.checkImageStatus(url); },1000);
        });        
    };
    
    $scope.updateCompletedImage = function(response) {
        $scope.scannedImage = response._links.imageDownloadThumb[0].href;
        $scope.$apply();
        imageService.addImage(response);
        console.log(response);
    }
    
    $scope.discardImage = function() {
      $scope.scannedImage = 'http://placehold.it/180x240';
    };
}]);