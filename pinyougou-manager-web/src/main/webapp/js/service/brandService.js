//品牌服务层
    app.service('brandService',function($http){
    	//查询所有
    	this.findall=function(){
    		return $http.get('../brand/findall.do');
    	}
    	 //分页查询
    	this.findPage=function(page,rows){	
    		return $http.get('../brand/findpage.do?page='+page+'&rows='+rows);
    	}
    	//新增
    	this.add=function(entity){
    		return $http.post('../brand/add.do',entity);
    	}    	
    	//查询实体
    	this.findOne=function(id){
    		return $http.get('../brand/findone.do?id='+id);
    	}
    	//修改
    	this.update=function(entity){
    		return $http.post('../brand/update.do',entity);
    	}
    	//删除
    	this.del=function(ids){
    		return $http.get('../brand/delete.do?ids='+ids)
    	}
    	//搜素
    	this.search=function(page,rows,searchEntity){
    		return $http.post('../brand/seach.do?page='+page+"&rows="+rows,searchEntity);
    	}
    	//品牌下拉列表
    	this.selectOptionList=function(){
    		return $http.get('../brand/selectOptionList.do');
    	}
    	
    });
    