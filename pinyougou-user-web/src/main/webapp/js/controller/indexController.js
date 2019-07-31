//首页控制器
app.controller('indexController',function($scope,loginService){
	$scope.showName=function(){
		loginService.showName().success(
			function(response){		
				//alert('kaishi');
				$scope.loginName=response.loginname;
			//	alert($scope.loginName);
			}
		);		
	}	
});