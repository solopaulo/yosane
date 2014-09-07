/** Yosane app booting
 * 
 */
'use strict';
var yosaneApp = angular.module('yosaneApp',[
    'ui.bootstrap',
    'yosaneServices',
    'ngRoute'
]);

function TabsController($scope,imageService) {
    $scope.tabs = [
      { link : '#/scanning', label: 'Scan', icon: 'glyphicon-inbox'},
      { link : '#/images', label : 'Img', icon : 'glyphicon-picture'},
      { link : '#/documents', label : 'Doc', icon: 'glyphicon-file'},
      { link : '#/settings', label : '', icon: 'glyphicon-cog'}
    ];
    
    $scope.selectedTab = $scope.tabs[0];    
    $scope.setSelectedTab = function(tab) {
      $scope.selectedTab = tab;
      if ( tab.label == 'Scan' ) {
          $scope.$emit('hallo');
      }
    };
    
    $scope.tabClass = function(tab) {
      if ($scope.selectedTab == tab) {
        return "active";
      } else {
        return "";
      }
    };
        
};