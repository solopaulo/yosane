/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ImagesController', [ '$scope', '$http', 'imageService', 'restful', function($scope, $http, imageService, restful) {
    $scope.images = imageService.images;

    $scope.preview = function(img) {
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
        restful.copyImage().send({},{"imageIdentifiers": selectedIdentifiers});    
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
            "imageIdentifiers" : selectedIdentifiers
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
            "imageIdentifiers" : selectedIdentifiers
        });
    };
} ]);