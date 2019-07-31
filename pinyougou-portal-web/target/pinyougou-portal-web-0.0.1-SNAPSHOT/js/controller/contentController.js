app.controller("contentController",function($scope,contentService){
	$scope.contentList=[];     //广告集合
	
	$scope.indByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(
			function(response){
			$scope.contentList[categoryId]=response;	
		}	
		);
	}
	
	
	//搜索跳转
	$scope.shouyesearch=function(){
		location.href="http://localhost:9104/search.html#?shouyekeywords="+$scope.keywords;
	}
});