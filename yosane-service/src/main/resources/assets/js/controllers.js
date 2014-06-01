/**
 * 
 */
var yosaneApp = angular.module('yosaneApp', []);
angular.module('yosaneApp', ['ui.bootstrap']);
function YosaneController($scope) {
   $scope.items = [
                   {"name":"Canon Pixma","href":"PIXMA"}
                   ]; 
}
