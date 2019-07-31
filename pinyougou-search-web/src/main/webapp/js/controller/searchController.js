app.controller('searchController',function($scope,$location,searchService){  //$location从首页接收关键字
	
	//搜索对象
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageno':1,'pageSize':40,'sortField':'','sort':''};	
	
	//搜索
	$scope.search=function(){
		$scope.searchMap.pageno=parseInt($scope.searchMap.pageno);    //转换为int类型，否则提交到后端有可能变成字符串
		searchService.search($scope.searchMap).success(
		function(response){
			$scope.resultMap=response;   //搜索结果  resposne 的格式  {result:[]}
			buildpageLable();   //调用分页
		}		
		);	
	}
	
	//添加搜索对象
	$scope.addsearchItem=function(key,value){
		if(key=='category'||key=='brand'||key=='price'){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		$scope.search();   //执行搜索
	}
	
	//移除搜索条件
	$scope.removeSearchitem=function(key){
		if(key=='category'||key=='brand'||key=='price'){
			$scope.searchMap[key]="";
		}else{
			delete $scope.searchMap.spec[key];
		}
		$scope.search();   //执行搜索
	}
	
	
	
	//构建分页标签
	buildpageLable=function(){
		$scope.pageLabel=[];           //新增分页栏
		
		var maxpage=$scope.resultMap.totalPages;   //最后一页
		var firstpage=1;
		var lastpage=maxpage;    //截止页
		
		$scope.firstDot=true;   //前面显示省略号
		$scope.lastDot=true;   //后面显示省略号
		
		if($scope.resultMap.totalPages>5){       //如果总页数大于5，显示部分页码
			if($scope.searchMap.pageno<=3){   //当前页小鱼3
				lastpage=5;
				$scope.firstDot=false;   //前面没省略号
			}else if($scope.searchMap.pageno>=lastpage-2){
				firstpage=maxpage-4;
				$scope.lastDot=false;   //后面没省略号
			}else{
				firstpage=$scope.searchMap.pageno-2;
				lastpage=$scope.searchMap.pageno+2;
			}	
		}else{
			$scope.firstDot=false;   //前面不显示省略号
			$scope.lastDot=false;   //后面不显示省略号
		}
		//循环产生页码
		for(var i=firstpage;i<=lastpage;i++){
			$scope.pageLabel.push(i);
		}			
	}
	
	//根据页码查询
	$scope.queryBypage=function(pageno){
		if(pageno<1 || pageno>$scope.resultMap.totalPages){
			return ;
		}
		$scope.searchMap.pageno=pageno;
		$scope.search();
	}
	
	
	//是否为第一页
	$scope.isfirstpage=function(){
		if($scope.searchMap.pageno==1){
			return true;
		}else{
			return false;
		}
	}
	
	
	//是否为最后页
	$scope.islastpage=function(){
		if($scope.searchMap.pageno==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	
	//设置排序sortField':'','sort'
	$scope.sortSearch=function(sortField,sort){
		$scope.searchMap.sortField=sortField;
		$scope.searchMap.sort=sort;
		$scope.search();
	}
	
	
	//判断关键字是否是品牌
	$scope.keywordsidbrand=function(){
		for(var i=0;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//$scope.searchMap.keywords(关键字)包含$scope.resultMap.brandList[i].text品牌）
				return true;
			}
		}
		return false;
	}
	
	//加载首页传过来的关键字      关键字和方法必须和首页一样  ，search.html 初始化调用
	$scope.localkey=function(){
        $scope.searchMap.keywords=$location.search()['shouyekeywords'];
        $scope.search();
	}
	
	
});