/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('ScanningController',['$scope','yosaneServices',function($scope,yosaneServices) {
    $scope.scanners = yosaneServices.get({},function(data) {
            $scope.items = data._links.scanner;
    });
}]);