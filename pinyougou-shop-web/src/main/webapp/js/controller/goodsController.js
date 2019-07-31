 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id= $location.search()['id'];  //获取参数值
		if(id==null){
			return ;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;	
				editor.html($scope.entity.goodsDesc.introduction);  //富文本编辑器
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);//图片
				$scope.entity.goodsDesc.customAttributeItems=
					JSON.parse($scope.entity.goodsDesc.customAttributeItems);//扩展属性
				$scope.entity.goodsDesc.specificationItems=
					JSON.parse($scope.entity.goodsDesc.specificationItems);   //规格
				//sku列表规格转换
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
								
			}
		);				
	}
	
	//保存 
	$scope.save=function(){		
		  $scope.entity.goodsDesc.introduction=editor.html();   //富文本编辑器
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}		
		//alert($scope.entity.goods.id);
		
	     serviceObject.success(
			function(response){
				if(response.success){
					alert(response.message);
					$scope.entity={};
					editor.html('');//清空富文本编辑器
					$scope.reloadList();
					location.href="goods.html"           //返回列表
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	

	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.status=['未审核','已审核','审核未通过','关闭']    //商品状态
	//$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//上传图片
	 $scope.uploadFile=function(){
		 uploadService.uploadFile().success(function(response){
			 if(response.success){
				 $scope.image_entity.url=response.message;
				 
			 }else{
				 alert(response.message);
			 }
		 }).error(function(){
			 alert("上传发生错误");
		 });
	 }
	
	 
	 //商品列表
	 $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};   //页面实体结构
	 //添加图片列表
	 $scope.add_image_entity=function(){
		 $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	 }
    
	  //列表中移除图片
	    $scope.remove_image_entity=function(index){
	    	    $scope.entity.goodsDesc.itemImages.splice(index,1);
	    }
	    
	    
	    
	   //读取一级分类
	    $scope.selectItemCat1List=function(){
	    	itemCatService.findByparentid(0).success(
	    	  function(response){
	    		  $scope.itemCat1List=response;
	    	  }		
	    	);
	    }
	    
	    //读取二级分类
	    $scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
	    	itemCatService.findByparentid(newValue).success(
	    	   function(response){
	    		   $scope.itemCat2List=response;
	    	   }		
	    	);
	    })
	    
	    //读取三级分类
	    $scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
	    	itemCatService.findByparentid(newValue).success(
	 	    	   function(response){
	 	    		   $scope.itemCat3List=response;
	 	    	   }		
	 	    	);
	    })
	    //读取模板
	      $scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
	    	itemCatService.findOne(newValue).success(
	 	    	   function(response){
	 	    		   $scope.entity.goods.typeTemplateId=response.typeId;
	 	    	   }		
	 	    	);
	    })
	    //根据模板读取
	    $scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
	    	typeTemplateService.findOne(newValue).success(
	    	  function(response){
	    		  $scope.typeTemplate=response;        //获取类型模板
	    		  $scope.typeTemplate.brandIds=JSON.parse( $scope.typeTemplate.brandIds);//品牌列表
	    		  if($location.search()['id']==null){
	    		  $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems); //拓展属性列表	    		  
	    		  } 
	    	  }		
	    	);
	    	typeTemplateService.findSpecList(newValue).success(
	    	function(response){
	    		$scope.specList=response;
	    	}		
	    	);
	    })
	    
	    //勾选规格
	    $scope.updateSpecAttribute=function($event,name,value){
	    	var object=$scope.searchObjectByKey(
	    			$scope.entity.goodsDesc.specificationItems,'attributeName',name);
	    	if(object!=null){
	    		if($event.target.checked){
	    			object.attributeValue.push(value);
	    		}else{
	    			//取消勾选
	    			object.attributeValue.splice(object.attributeValue.indexOf(value),1);
	    			
	    			//如果选项都取消，记录移除
	    			if(object.attributeValue.length==0){
	    				$scope.entity.goodsDesc.specificationItems.splice(
	    						$scope.entity.goodsDesc.specificationItems.indexOf(object),1	
	    				);
	    			}
	    			
	    		}
	    	}else{
	    		$scope.entity.goodsDesc.specificationItems.push(
	    		{"attributeName":name,"attributeValue":[value]}	);
	    	}
	    	
	    }
	    
	    //创建SKU
	    $scope.createItemList=function(){
	    	$scope.entity.itemList=[{spec:{},price:0,num:0,status:'0',isDefault:'0' } ];//初始
	    	var items=$scope.entity.goodsDesc.specificationItems;	
	    	for(var i=0;i<items.length;i++){
	    		$scope.entity.itemList=	addColu($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
	    	}
	    }
	    	addColu=function(list,colueName,colueValue){
	    		var newList=[];	    		
	    		for(var i=0;i<list.length;i++){
	    			var oldRow=list[i];
	    			for(var j=0;j<colueValue.length;j++){
	    				var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆
	    				newRow.spec[colueName]=colueValue[j];
	    				newList.push(newRow);
	    			}
	    		}
	    		return newList;
	    	}
	    	
	    	
	    	
	    //商品分类列表    异步查询
	    	$scope.itemCatlist=[];
	    	$scope.findItemCatlist=function(){
	    		itemCatService.findAll().success(
	    		function(response){
	    			for(var i=0;i<response.length;i++){
	    				$scope.itemCatlist[response[i].id]= response[i].name;
	    			}
	    		}		
	    		);
	    	}
	    
	    	//根据规格名称和选项名称返回是否被勾选
	    	$scope.checkAttributeValue=function(name,option){
	    		var items=$scope.entity.goodsDesc.specificationItems;
	    		var object=$scope.searchObjectByKey(items,"attributeName",name);	    		          
	    		if(object==null){
	    			return false;
	    		}else{
	    			if(object.attributeValue.indexOf(option)>=0){
	    				return true;
	    			}else{
	    				return false;
	    			}
	    		}
	    	}
	    
	    	
	    	//下架
	    	$scope.xiajia=function(){
	    		goodsService.xiajia($scope.selectIds ).success(
	    		   function(response){
	    			   if(response.success){
	    				   alert(response.message);
	   					$scope.reloadList();//刷新列表
	   					$scope.selectIds=[];
	   				}
	    		   }		
	    		);
	    	}
	    
	 
});	
