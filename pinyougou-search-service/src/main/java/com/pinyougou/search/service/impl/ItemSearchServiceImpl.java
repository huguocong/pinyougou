package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private RedisTemplate redis;

	private SimpleHighlightQuery simpleHighlightQuery;
	
	@Override
	public Map<String,Object> search(Map searchMap) {
		String  object = (String) searchMap.get("keywords");
		searchMap.put("keywords", object.replace(" ", ""));    //去空格   要放在调用函数下
		
		Map<String,Object> map=new HashMap<>();
		
	
		map.putAll(searchList(searchMap));		
		//商品分类
		List categoryList = searchCategoryList( searchMap);	
		map.put("categoryList", categoryList);
		
		//查询品牌和规格
		String cateName=(String)searchMap.get("category");
		if(!"".equals(cateName)){
			map.putAll(searchbrandandspe(cateName));
		}else{
		if(categoryList.size()>0){
			map.putAll(searchbrandandspe((String) categoryList.get(0)));
		
		}
		}
		return map;
	}

	

	private Map searchList(Map searchMap){
		Map map=new HashMap();
		//	高亮显示初始化	
		HighlightQuery query=new SimpleHighlightQuery();
		//设置高亮域
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		//设置前缀
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		//设置后缀
		highlightOptions.setSimplePostfix("</em>");		
		//设置高亮选项
		query.setHighlightOptions(highlightOptions);
		
		
		//1.1关键字查询     由复制域查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//1.2商品分类过滤   
		if(!"".equals(searchMap.get("category"))){
			Criteria criteria2 = new Criteria("item_category").is(searchMap.get("category"));
			 FilterQuery fi= new SimpleHighlightQuery(criteria2);
			 query.addFilterQuery(fi);					
		}
				
		//1.3商品品牌过滤
		if(!"".equals(searchMap.get("brand"))){
			Criteria criteria3 = new Criteria("item_brand").is(searchMap.get("brand"));
		    FilterQuery fi2 = new SimpleHighlightQuery(criteria3);
		    query.addFilterQuery(fi2);
			
		}
		
		//1.4 商品规格过滤  动态域
		if(searchMap.get("spec")!=null){
			Map<String,String> specMap=(Map<String,String>)searchMap.get("spec");
			for (String key : specMap.keySet()) {
				Criteria criteria4 = new Criteria("item_spec_"+key).is(specMap.get(key));
				 FilterQuery fi4= new SimpleHighlightQuery(criteria4);
				    query.addFilterQuery(fi4);
			}
			
		}
		
		//1.5按价格帅选
		if(!"".equals(searchMap.get("price"))){
			String[] price=((String) searchMap.get("price")).split("-");   //将价格区间切开，如500-1000 price[0]=500,price[1]=1000
			if(!price[0].equals("0")){ //区间起点不为0
				 Criteria criteria2 = new Criteria("item_price").greaterThanEqual(price[0]);     //价格大于price[0]
				 FilterQuery fi= new SimpleHighlightQuery(criteria2);
				 query.addFilterQuery(fi);					
			}
			if(!price[1].equals("*")){ //区间终点不为*
				 Criteria criteria2= new Criteria("item_price").lessThanEqual(price[1]);     //价格大于price[0]
				 FilterQuery fi= new SimpleHighlightQuery(criteria2);
				 query.addFilterQuery(fi);					
			}						
		}
		
		
		//1.6分页查询
		
		Integer pageno= (Integer) searchMap.get("pageno");//提取页码
		if(pageno==null){
			pageno=1;    //默认为1
		}
		Integer pageSize= (Integer) searchMap.get("pageSize");
		if(pageSize==null){
			pageSize=20;
		}
		query.setOffset(pageSize*(pageno-1));
		query.setRows(pageSize);
		
		//1.7排序
		String object = (String) searchMap.get("sort");    //排序方式
		String object2 = (String) searchMap.get("sortField");   //排序字段
		
		if(object!=null && !object.equals("") ){
			if(object.equals("ASC")){
				Sort sort = new Sort(Sort.Direction.ASC,"item_"+object2);
				query.addSort(sort);
			}
			if(object.equals("DESC")){
				Sort sort = new Sort(Sort.Direction.DESC,"item_"+object2);
				query.addSort(sort);
			}
			
		}
		
		
		
		
		//高亮显示
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		//循环高亮入口集合
		for (HighlightEntry<TbItem> h : page.getHighlighted()) {
			//获取原实体类
			TbItem item = h.getEntity();
			if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
			}			
		}	
		map.put("result", page.getContent());
		map.put("totalPages", page.getTotalPages());    //总页数
		map.put("total", page.getTotalElements());   //总记录数
		return map;
	}
	
	
	//查询分类列表
	private List searchCategoryList(Map searchMap){
		
		List<String> list=new ArrayList();
		
		
		
		Query query = new SimpleQuery();
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//设置分组选项
		GroupOptions grop=new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(grop);		
		//分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		
		//根据列得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		
		for(GroupEntry<TbItem> enty:content){
			list.add(enty.getGroupValue()); //将分组结果的名称封装到返回值中
		}
	
		return list;	
	}
	
	
	// redis查询品牌和规格
	  private Map searchbrandandspe(String category){
		  Map map=new HashMap();
		 Long typeid= (Long) redis.boundHashOps("itemCat").get(category);
		 
		 if(typeid!=null){
			 //品牌列表
			List brandList= (List) redis.boundHashOps("brandList").get(typeid);
			map.put("brandList", brandList);
			//规格列表
			List specList= (List) redis.boundHashOps("specList").get(typeid);
			map.put("specList", specList);
			
		 }	  
		  return map;
	  }



	   //导入索引库
	@Override
	public void importlist(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		
	}


 
	@Override
	public void deletebyGood(List goodlist) {
		SimpleQuery query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsid").in(goodlist);
		
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
		
	}
	
	
	
	
	
}
