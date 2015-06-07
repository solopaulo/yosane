/** Yosane app booting
 * 
 */
'use strict';
var yosaneApp = angular.module('yosaneApp',[
    'ui.bootstrap',
    'yosaneServices',
    'ngRoute',
    'ngAnimate'
]);

function TabsController($scope,$rootScope,$location,$timeout,imageService,notificationService,configService) {
    $scope.notificationTab = { link : '#/notifications', label : '', icon : '', notifications : function() {
        return notificationService.newNotifications.length;
    }};
    $scope.tabs = [
      { link : '#/scanning', label: 'Scan', icon: 'glyphicon-inbox'},
      { link : '#/images', label : 'Img', icon : 'glyphicon-picture'},
      { link : '#/documents', label : 'Doc', icon: 'glyphicon-file'},
      { link : '#/settings', label : '', icon: 'glyphicon-wrench'}
    ];
    
    $scope.setSelectedTab = function(tab) {
        $scope.selectedTab = tab || [ $scope.tabs[0] ].concat( $scope.tabs.filter( function(ftab) { 
            return $location.$$path === ftab.link.replace(/#/,'') 
        } )).pop();
        if ( $scope.selectedTab.label == 'Scan' ) {
            setTimeout(function() {$rootScope.$broadcast('scanningTabSelected');},500);
        }
    };
    
    $scope.tabClass = function(tab) {
      if ($scope.selectedTab == tab) {
        return "active";
      } else {
        return "";
      }
    };
    
    
    $scope.setSelectedTab();
    (function tick() {
        notificationService.poll();
        $timeout(tick,7500);
      })(); 
    
    configService.updateLocalPaths();
    configService.updateRecipients();
};