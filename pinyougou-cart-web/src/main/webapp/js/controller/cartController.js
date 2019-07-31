//购物车控制层
app.controller('cartController',function($scope,$controller ,cartService,addressService){
	//查询购物车
	$scope.findcartList=function(){
		cartService.findCartList().success(
		     function(response){
		    	 $scope.cartList=response;
		    	 $scope.totalValue=cartService.sum($scope.cartList);    //求合计数
		     }		
		);
	}
	
	
	//添加商品
	$scope.addGoodstoCartList=function(itemid,num){
		cartService.addGoodsToCartList(itemid,num).success(
				function(response){
					if(response.success){
						$scope.findcartList();  //查询购物车，刷新列表
					}else{
						alert(response.message);  
					}
				}			
		);
	}
	
	
	//根据用户查询地址
	$scope.findaddressbyuser=function(){
		cartService.findaddressbyuser().success(
		   function(response){
			   $scope.addressList=response;
			   
			   //获取默认地址
			   for(var i=0;i<$scope.addressList.length;i++){
				   if($scope.addressList[i].isDefault=='1'){
					   $scope.address=$scope.addressList[i];
					   break;
				   }
			   }
			   
		   }		
		);
	}
	
	
	//选择地址
	$scope.selectAddress=function(address){
		$scope.address=address;
	}
	
	//判断是否为当前选中的地址
	$scope.isselectAddress=function(address){
		if(address==$scope.address){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	//保存地址
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=addressService.update( $scope.entity ); //修改  
		}else{
			serviceObject=addressService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert(response.message);
					//重新查询 
					$scope.findaddressbyuser();//重新加载
				
					
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	
	//查询实体 ，根据id查询地址
	$scope.findOne=function(id){				
		addressService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	
	//删除地址
	$scope.dele=function(id){			
		//获取选中的复选框			
		addressService.dele(id).success(
			function(response){
				if(response.success){
					alert(response.message);
					//重新查询 
					$scope.findaddressbyuser();//重新加载
				}else{
					alert(response.message);
				}						
			}		
		);				
	}
	
	
	
	//支付方式
	$scope.order={paymentType:'1'};
	//选择支付
	$scope.selectPaytype=function(type){
		$scope.order.paymentType=type;
	}
	
	
	//保存订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		alert($scope.address.address);
		cartService.submitOrder( $scope.order ).success(
		function(response){
		 if(response.success){
			 //页面跳转
			 if($scope.order.paymentType=='1'){   //微信支付
				 location.href="pay.html";
			 }else{
				 location.href="paysuccess.html";
			 }
		 }
	 }		
	);
	
	}
	
	
	
	
	
});
