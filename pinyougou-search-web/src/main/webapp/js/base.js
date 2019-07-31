var app=angular.module('pinyougou',[]);       /*不分页的功能*/

app.filter('trustHtml',['$sce',function($sce){
	return function(data){
		return $sce.trustAsHtml(data);
	}
}]);

