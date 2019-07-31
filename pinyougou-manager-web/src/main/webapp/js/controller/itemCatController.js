 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId=$scope.parentid;
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					alert(response.message);
					$scope.findparentId($scope.parentid);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					alert(response.message);
					$scope.findparentId($scope.parentid);//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.parentid=0;
	//根据父亲id查询
	$scope.findparentId=function(parentId){
		
		//保存时用到，记住这个父亲id
		$scope.parentid=parentId;
		itemCatService.findByparentid(parentId).success(
		   function(response){
			   $scope.list=response;
		   }		
		);
	}
	
	
	//面包屑导航
	$scope.grand=1;
	$scope.setGrand=function(value){
		$scope.grand=value;
	}
	
	$scope.selectlist=function(p_entity){
		if($scope.grand==1){
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if($scope.grand==2){
			$scope.entity_1=p_entity;
			$scope.entity_2=null;
		}
		if($scope.grand==3){
		
			$scope.entity_2=p_entity;
		}
		
		$scope.findparentId(p_entity.id)
		
		
	}
	
    
});	
