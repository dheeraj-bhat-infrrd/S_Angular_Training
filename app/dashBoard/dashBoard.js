angular.module('socialSurvey.dashBoard',['ui.router'])

.config(['$stateProvider',function($stateProvider){

  $stateProvider.state('dashBoard',{
    url: '/dashBoard',
    templateUrl: 'dashBoard/dashBoard.html',
    controller: 'dashBoardCtrl'
  });

}])

.controller('dashBoardCtrl',['$scope',function($scope){

  FusionCharts.ready(function () {
      var revenueChart = new FusionCharts({
          type: 'stackedcolumn3dlinedy',
          renderAt: 'reviewRatingChart',
          width: '100%',
          height: '350',
          dataFormat: 'jsonUrl',
          dataSource: 'resources/json/averageRating.json'
      });
      revenueChart.render();
  });

  FusionCharts.ready(function () {
    var topStores = new FusionCharts({
        type: 'bar2d',
        renderAt: 'experienceChart',
        width: '400',
        height: '300',
        dataFormat: 'jsonUrl',
        dataSource: "resources/json/ratingsJson.json"
    });
    topStores.render();
});
}])
