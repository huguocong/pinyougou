//定义控制器
    app.controller('brandController',function($scope,$controller,brandService){
    	$controller('baseController',{$scope:$scope});  //继承
    	
    	
    	$scope.findAll=function(){
    		brandService.findall().success(
    				function(response){
    					$scope.list=response;
    				});
    	}

    	
    	//分页
    	$scope.findPage=function(page,rows){	
    		brandService.findPage(page,rows).success(
    				function(response){
    					$scope.list=response.rows;	
    					$scope.paginationConf.totalItems=response.total;//更新总记录数
    				}			
    		);
    	}
    	
    	//保存   新增和修改
    	$scope.save=function(){
    		var serviceObject;  //服务层对象
    		if($scope.entity.id!=null){
    			serviceObject=brandService.update($scope.entity);
    		}else{
    			serviceObject=brandService.add($scope.entity);
    		} 		
    		serviceObject.success(  				
    		function(response){
    			if(response.success){
    				//重新查询
    				$scope.reloadList();
    				alert(response.message);
    			}else{
    				alert(response.message);
    			}   			
    		}
    		);
    	}
    	
    	//查询实体
    	$scope.findOne=function(id){
    		brandService.findOne(id).success(
    				function(response){
    					$scope.entity=response;
    				}
    		);
    	}
    	
   /* 	
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
    }
    
       */
    //批量删除
    $scope.del=function(){
    	//获取复选框
         brandService.del($scope.selectIds).success(
    	function(response){
    		if(response.success){
    			//刷新列表
    			$scope.reloadList();
    		}
    	}		
    	); 	
    }
    	
    	
   /*  //定义搜索对象
     $scope.searchEntity={};*/
     //条件查询
     $scope.search=function(page,rows){
    	 brandService.search(page,rows,$scope.searchEntity).success(
    		function(response){
    			$scope.paginationConf.totalItems=response.total;//总记录数 
				$scope.list=response.rows;//给列表变量赋值 
    		}	 
    	 );
     }
    	
    
    
    });   