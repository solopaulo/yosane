/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ScanningController',['$scope','$http','restful','imageService','scannerService',
 function($scope,$http,restful,imageService,scannerService) {
  
    $scope.refreshScannerList = function() {
        scannerService.currentScanner = undefined;
        restful.get({},function(data) {            
            scannerService.scanners = data._links.scanner;            
        }); 
    };
    
    $scope.getScanners = function() {        
        return scannerService.scanners;
    };
    
    $scope.getScannedImage = function() {
        return scannerService.scannedImage;
    };
    
    $scope.getSelected = function() {
        return scannerService.selected;
    };
    
    $scope.selectScanner = function(name) {
        scannerService.currentScanner = undefined;
        for ( var i = 0; i < scannerService.scanners.length; i++) {
            if ( scannerService.scanners[i].name != name ) {
                continue;
            }
            scannerService.selected = name;
            scannerService.currentScanner = scannerService.scanners[i];
        }        
    };
    
    $scope.scan = function() {
        if ( scannerService.currentScanner === undefined ) {
            return;
        }
        var murl = scannerService.currentScanner.href;
        console.log(murl);
        $http.post(murl,[]).success( function(response) {
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
        scannerService.scannedImage = response._links.imageDownloadThumb[0].href;
        $scope.$apply();
        imageService.addImage(response);
        console.log(response);
    }
    
    $scope.discardImage = function() {
      scannerService.scannedImage = 'http://placehold.it/180x240';
    };
}]);