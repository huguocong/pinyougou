app.controller("itemController",function($scope,$http){
	
	$scope.specificationItems={};//用户存储的规格
	
	//数量加减
	$scope.addNum=function(x){
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}		
	}
	
	//用户选择规格
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key]=value;		
		searchSku();//��ѯSKU
	}
	
	//判断规格是否被选中
	$scope.isSelected=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}	
	}
	
	$scope.sku={};//当前选择的SKU
	
	//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}
	
	//匹配两个对象是否相等
	matchObject=function(map1,map2){
		
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}			
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}			
		}		
		return true;
		
	}
	
	//根据规格查询sku
	searchSku=function(){
		
		for(var i=0;i<skuList.length;i++){
			 if(matchObject( skuList[i].spec ,$scope.specificationItems)){
				 $scope.sku=skuList[i];
				 return ;
			 }			
		}
		$scope.sku={id:0,title:'-----',price:0};
	}
	
	//添加到购物车ﳵ
	$scope.addToCart=function(){
		alert('SKUID:'+$scope.sku.id );	
		alert('num:'+$scope.num);
		
		$http.get('http://localhost:9107/cart/addgoodsToCartList.do?itemid='				
				+$scope.sku.id+'&num='+$scope.num, {'withCredentials':true} ).success(
				function(response){
					if(response.success){
						location.href='http://localhost:9107/cart.html';    //调到购物车页面
					}else{
						alert(response.message);
					}
				}		
				);
	}
	
	
});