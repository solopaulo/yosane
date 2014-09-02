/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ScanningController',['$scope','yosaneServices',function($scope,yosaneServices) {
    $scope.selected = "";
    $scope.refreshScannerList = function() {
        yosaneServices.get({},function(data) {
            $scope.items = data._links.scanner;
        }); 
    };
    $scope.selectScanner = function(name) {
      $scope.selected = name;
    };
    $scope.refreshScannerList();
    
}]);