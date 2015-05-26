/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('SettingsController',
 // @Inject
 ['$scope','$http','restful','imageService','scannerService','$modal',
 function($scope,$http,restful,imageService,scannerService,$modal) {
     if ( scannerService.selected == '' ) {
         alert('No scanner selected');
         return;
     }
     
     
     if ( scannerService.currentScannerSettings.length == 0 ) {
         // get settings
         var url = scannerService.settings.filter( function(x) { return x.name == scannerService.selected })[0].href;
         restful.getSettings(url,function(d) {
             scannerService.updateSettings(d.options);
             $scope.settings = scannerService.currentScannerSettings;
             $scope.settingsGroups = scannerService.settingsGroups;
         });         
     } else {
         $scope.settingsGroups = scannerService.settingsGroups;
         $scope.settings = scannerService.currentScannerSettings;
     }
     
     $scope.settingsForGroup = function(group) {
         var groupSettings = scannerService
             .currentScannerSettings
             .filter( function(x) { return x.group.toLowerCase() == group;});
             
         return groupSettings;
     };
     
     $scope.registerChange = function(updated) {
         scannerService.settingChanged(updated);         
     }
 }]
);
