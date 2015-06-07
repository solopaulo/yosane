/**
 * 
 */
var yosaneApp = angular.module('yosaneApp');
yosaneApp.controller('NotificationsController',
 // @Inject
 ['$scope','$http','restful','$timeout','notificationService',
 function($scope,$http,restful,$timeout,notificationService) {
     notificationService.flush();
     $scope.notifications = [];
     $scope.ns = notificationService;
     $scope.$watch( function() {
         notificationService.getNotifications();
     }, function() {
         $scope.notifications = notificationService.notifications;
     });
 }]);
