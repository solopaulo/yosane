/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.config(['$routeProvider',function($routeProvider) {
    $routeProvider
        .when('/scanning', {templateUrl: '/yosane/assets/app/scanning/partial-scanning.html', controller : 'ScanningController'})        
        .when('/images',{templateUrl:'/yosane/assets/app/images/partial-images.html',controller : 'ImagesController'})
        .when('/documents',{templateUrl: '/yosane/assets/app/documents/partial-documents.html',controller : 'DocumentsController'})
        .when('/settings',{templateUrl: '/yosane/assets/app/settings/partial-settings.html',controller : 'SettingsController'})
        .otherwise({redirectTo : '/scanning'});
}]);