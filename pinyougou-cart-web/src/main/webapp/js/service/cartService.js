//购物车服务层
app.service('cartService',function($http){
	//购物车列表
	this.findCartList=function(){
		return $http.get('cart/findcartList.do');
	}
	
	//添加商品到购物车
	this.addGoodsToCartList=function(itemid,num){
		return $http.get('cart/addgoodsToCartList.do?itemid='+itemid+'&num='+num);
		
	}
	
	
	//求合计
	this.sum=function(cartList){
		var totalValue={totalNum:0,totalMoney:0.00};     //合计实体
	    for(var i=0;i<cartList.length;i++){
	    	var cart=cartList[i];
	    	for(var j=0;j<cart.orderItemList.length;j++){
	    		var orderitem=cart.orderItemList[j];
	    		totalValue.totalNum+=orderitem.num;
	    		totalValue.totalMoney+=orderitem.totalFee;
	    	}
	    	
	    }	
	    return totalValue;
	}
		
	//根据用户查询地址
	this.findaddressbyuser=function(){
		return $http.get('/address/findListaddressbyuser.do');
	}
	
	
	
	//保存订单
	this.submitOrder=function(order){
		return $http.post('order/add.do',order);		
	}
	
	
	
	
});