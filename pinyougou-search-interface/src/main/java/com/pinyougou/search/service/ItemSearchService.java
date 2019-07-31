package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

	//搜索
	public Map<String,Object> search(Map searchmap);
	
	//导入数据到索引库
	public void importlist(List list);
	
	//删除数据同步索引库
	public void deletebyGood(List goodlist);
}
