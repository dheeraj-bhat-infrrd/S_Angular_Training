'use strict';

// Declare app level module which depends on views, and components
angular.module('socialSurvey', [
  'ui.router',
  'socialSurvey.dashBoard'
]).
config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {


  $urlRouterProvider.otherwise('/dashBoard');
}]);
