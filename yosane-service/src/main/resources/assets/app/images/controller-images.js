/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ImagesController', [ '$scope', '$http', 'imageService', 'restful','configService',
  function($scope, $http, imageService, restful,configService) {
    $scope.images = imageService.images;
    $scope.is = imageService;
    $scope.cs = configService;
    
    $scope.previewImage = function(img) {
        alert('previewing ' + img);
    };

    $scope.toggleSelected = function(idx) {
        if (idx >= $scope.images.length) {
            return;
        }
        $scope.images[idx].selected = !$scope.images[idx].selected;
    };

    $scope.selectedClass = function(idx) {
        if (idx >= $scope.images.length) {
            return;
        }
        return $scope.images[idx].selected ? 'imageSelected' : '';
    };

    $scope.doReset = function() {
        $scope.images = $scope.images.map(function(e) {
            e.deleted = true;
            e.selected = false;
            return e;
        });
        imageService.naming = "";
    };
    
    $scope.selectAll = function() {
        for (var i = 0; i < $scope.images.length; i++) {
            $scope.images[i].selected = true;
        }
    }
    
    $scope.imagesSelected = function() {
        for (var i = 0; i < $scope.images.length; i++) {
            if ($scope.images[i].selected) {
                return true;
            }
        }
        return false;
    };

    $scope.localFileCopyImages = function() {
        var selectedIdentifiers = $scope.images.filter(function(e) {
            return e.selected;
        }).map(function(e) {
            return e.identifier;
        });

        if (selectedIdentifiers.length < 1) {
            console.log("failed to send email");
            return;
        }
        restful.copyImage().send({},{"imageIdentifiers": selectedIdentifiers,"naming": imageService.naming});    
    };

    $scope.emailImages = function() {
        var selectedIdentifiers = $scope.images.filter(function(e) {
            return e.selected;
        }).map(function(e) {
            return e.identifier;
        });

        if (selectedIdentifiers.length < 1) {
            console.log("failed to send email");
            return;
        }
        restful.emailImage().send({}, {
            "imageIdentifiers" : selectedIdentifiers, "naming" : imageService.naming
        });
    };
    
    $scope.emailPdf = function() {
        var selectedIdentifiers = $scope.images.filter(function(e) {
            return e.selected;
        }).map(function(e) {
            return e.identifier;
        });

        if (selectedIdentifiers.length < 1) {
            console.log("failed to send email");
            return;
        }
        restful.emailPdf().send({}, {
            "imageIdentifiers" : selectedIdentifiers, "naming" : imageService.naming
        });
    };
} ]);