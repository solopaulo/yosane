/** Yosane app booting
 * 
 */
'use strict';
var yosaneApp = angular.module('yosaneApp',[
    'ui.bootstrap',
    'yosaneServices',
    'ngRoute'
]);

function TabsController($scope,$rootScope,imageService) {
    $scope.tabs = [
      { link : '#/scanning', label: 'Scan', icon: 'glyphicon-inbox'},
      { link : '#/images', label : 'Img', icon : 'glyphicon-picture'},
      { link : '#/documents', label : 'Doc', icon: 'glyphicon-file'},
      { link : '#/settings', label : '', icon: 'glyphicon-cog'}
    ];
    
    $scope.selectedTab = $scope.tabs[0];    
    $scope.setSelectedTab = function(tab) {
      $scope.selectedTab = tab;
    };

    
    $scope.tabClass = function(tab) {
      if ($scope.selectedTab == tab) {
        return "active";
      } else {
        return "";
      }
    };

    
    if ( $scope.selectedTab.label == 'Scan' ) {
        setTimeout(function() {$rootScope.$broadcast('scanningTabSelected');},500);
    }
    
};