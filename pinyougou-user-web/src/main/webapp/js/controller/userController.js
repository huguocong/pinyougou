 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	
	//注册
	$scope.reg=function(){
	
		if($scope.entity.password!=$scope.password){
			alert("两次密码不一致");
			$scope.entity.password="";
			$scope.password="";
			return ;
		}
		userService.add($scope.entity,$scope.smscode).success(
				function(response){
					alert(response.message);
				}
		);
		
	}
	
	//发送验证码
	$scope.sengcode=function(){
		if($scope.entity.phone==null){
			alert("手机号不能为空");
			return ;
		}
		userService.sengcode($scope.entity.phone).success(
		    function(response){
		    	alert(response.message);
		    }		
		);
		
	}
	
    
});	
