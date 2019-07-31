//服务层
app.service('seckillGoodsService',function($http){	    	
	//读取列表数据绑定到表单中
	this.findList=function(){
		return $http.get('seckillGoods/findList.do');		
	}	
	////从缓存里获取id实体，秒杀详情页
	this.findOne=function(id){
		return $http.get('seckillGoods/findoneredis.do?id='+id);		
	}
	
	//提交订单
	this.submitOrder=function(seckillId){
		return $http.get('submitOrder/submitOrder.do?seckillId='+seckillId);
	}
});