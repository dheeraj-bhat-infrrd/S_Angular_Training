angular.module('socialSurvey.dashBoard',['ui.router'])

.config(['$stateProvider',function($stateProvider){

  $stateProvider.state('dashBoard',{
    url: '/dashBoard',
    templateUrl: 'dashBoard/dashBoard.html',
    controller: 'dashBoardCtrl'
  });
  
}])

.controller('dashBoardCtrl',['$scope',function($scope){

}])
