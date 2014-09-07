/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.config(['$routeProvider',function($routeProvider) {
    $routeProvider
        .when('/scanning', {templateUrl: '/assets/app/scanning/partial-scanning.html', controller : 'ScanningController'})        
        .when('/images',{templateUrl:'/assets/app/images/partial-images.html',controller : 'ImagesController'})
        .when('/documents',{templateUrl: '/assets/app/documents/partial-documents.html',controller : 'DocumentsController'})
        .when('/settings',{templateUrl: '/assets/app/settings/partial-settings.html',controller : 'SettingsController'})
        .otherwise({redirectTo : '/scanning'});
}]);