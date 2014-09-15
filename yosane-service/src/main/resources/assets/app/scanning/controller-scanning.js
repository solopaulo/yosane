/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ScanningController',
 // @Inject
 ['$scope','$http','restful','imageService','scannerService','$modal',
 function($scope,$http,restful,imageService,scannerService,$modal) {
    /** When the scanning tab is selected, refresh the list of scanners */
    $scope.$on('scanningTabSelected',function(event) {
      $scope.refreshScannerList();  
    });
    
    /** Refresh the list of scanners */
    $scope.refreshScannerList = function() {
        scannerService.currentScanner = undefined;
        restful.getScanners(function(data) {   
            scannerService.scanners = data._links.scanner;            
        }, function(r) {
            scannerService.scanners = scannerService.emptyScannerList;
            scannerService.selected = name;
            scannerService.currentScanner = undefined;
        });
    };
   
    
    /** Retrieve the scanner list from the scanner service */
    $scope.getScanners = function() {        
        return scannerService.scanners;
    };
    
    /** Get the current scanned image from the scanner service */
    $scope.getScannedImage = function() {
        return scannerService.scannedImage;
    };
    
    /** Get the name of the currently selected scanner */
    $scope.getSelectedScannerName = function() {
        return scannerService.selected;
    };
    
    $scope.getSelectedScanner = function() {
        return scannerService.currentScanner;
    }
    
    
    /** Set the current scanner to the one specified by the name */
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
    
    
    /** Acquire a new image from the scanner using the restful service */
    $scope.scan = function() {
        if ( scannerService.currentScanner === undefined ) {
            return;
        }
        restful.scanImage({url:scannerService.currentScanner.href},function(response) {
            setTimeout( function() { $scope.scanningProgress( response ); },0);
            $scope.$modalInstance = $modal.open({
                size:'sm',
                templateUrl:'/assets/app/components/partial-dialog-progress.html',
                scope:$scope
            });
        }, function(r) {
            $scope.refreshScannerList();
        });
    };
    
    /** Update the scanning progress */
    $scope.scanningProgress = function(response) {
        var url = response._links.self.href;
        $scope.status = response.status;
        setTimeout( function() { $scope.checkImageStatus(url); }, 500);      
    };
    
    $scope.getStatus = function() {
        return $scope.status;
    };

    
    /** Call the restful image service to ascertain the scanned image's current status */
    $scope.checkImageStatus = function(url) {
        $http.get(document.location.origin+url).success( function(response) {
            $scope.status = response.status;
            if ( [undefined,'READY','MISSING','FAILED'].indexOf(response.status) >= 0 ) {
                if ( response.status == 'READY' ) {
                    setTimeout( function() { 
                        $scope.updateCompletedImage(response);
                        $scope.$modalInstance.close();
                    },0);
                }
                return;
            } 
            setTimeout( function() { $scope.checkImageStatus(url); },1000);
        });
    };
    
    /** Update the image in the template with the newly scanned image */
    $scope.updateCompletedImage = function(response) {
        scannerService.scannedImage = response._links.imageDownloadThumb[0].href;
        $scope.$apply();
        imageService.addImage(response);
    }
    
    /** Discard the image showing in the template */
    $scope.discardImage = function() {
      scannerService.scannedImage = scannerService.defaultImage;
    };
    
    /** Return false if we are showing placeholder (i.e. post-init or after discard image) */
    $scope.hasScanned = function() {
        return scannerService.scannedImage != scannerService.defaultImage;
    };
    
    $scope.emailImage = function() {
        var images = imageService.images.slice(-1);
        if ( images.length != 1 ) {
            console.log("failed to send email");
            return;
        } 
        restful.emailImage().send({},{"imageIdentifiers": [ images[0].identifier]});        
    };
    
    $scope.localfileCopyImage = function() {
        var images = imageService.images.slice(-1);
        if ( images.length != 1 ) {
            console.log("failed to local copy file");
            return;
        } 
        restful.copyImage().send({},{"imageIdentifiers": [ images[0].identifier]});        
    };
}]);