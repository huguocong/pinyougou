//基本控制层
app.controller('baseController',function($scope){
	/*//重新加载列表 数据
    	$scope.reloadList=function(){
    		 //列表切换页码    	
    	$scope.seach( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    	}  	
    	//分页控件配置 currentPage：当前页  totalItems：总记录数  itemsPerPage：当前页记录数   perPageOptions：选择一页几条数据
    	$scope.paginationConf = {
    			 currentPage: 1,
    			 totalItems: 10,
    			 itemsPerPage: 10,
    			 perPageOptions: [10, 20, 30, 40, 50],
    			 onChange: function(){
    			        	 $scope.reloadList();//重新加载
    			 }
    	};
    	
    	  //选中的id集合
        $scope.selectIds=[];
        
        $scope.updateslecttion=function($event,id){
        	if($event.target.checked){
        		//如果被勾选，就添加到数组
        		$scope.selectIds.push(id);
        	}else{
        		//如果取消勾选，就从数组里移除
        	  var idx=$scope.selectIds.indexOf(id);  //获取id的位置
        	  $scope.selectIds.splice(idx,1);    //删除
        	}
        }*/
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}
	
        //定义搜索对象
        $scope.searchEntity={};
        
        
        //提取json字符串数据里的某个属性，返回字符串，逗号分隔
        $scope.jsonToString=function(jsonString,key){
        	var json=JSON.parse(jsonString);  //将json字符串转换为json对象
        	var value="";
        	for(var i=0;i<json.length;i++){
        		if(i>0){
        			value+=","
        		}
        		value+=json[i][key];
        	}
        	return value;
        }
        
    	//从集合中按照key查询对象
        $scope.searchObjectByKey=function(list,key,keyvalue){
        	for(var i=0;i<list.length;i++){
        		if(list[i][key]==keyvalue){
        			return list[i];
        		}
        	}
        	return null;
        	
        }
   
        
        
        
        
        
        
        
        
        
});