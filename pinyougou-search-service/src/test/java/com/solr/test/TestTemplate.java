package com.solr.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Crotch;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pinyougou.pojo.TbItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/applicationContext*.xml")
public class TestTemplate {

	@Autowired
	private SolrTemplate solr;
	
	/*@Test
	public void add(){
		TbItem item=new TbItem();
		item.setId(1L);
		item.setBrand("华为");
		item.setCategory("手机");
		item.setGoodsId(1L);
		item.setSeller("华为2号专卖店");
		item.setTitle("华为Mate9");
		item.setPrice(new BigDecimal(2000));
		solr.saveBean(item);
		solr.commit();
	}*/
	
	//按住键查询
	/*@Test
	public void findone(){
		TbItem item = solr.getById(1, TbItem.class);
		System.out.println(item.getTitle());
	}*/
	
	
	//按主键删除
   /*	@Test
	public void dele(){
		solr.deleteById("1");
		solr.commit();
	    TbItem item = solr.getById(1, TbItem.class);
		if(item==null){
			System.out.println("为空了");
		}
	}*/
	

	
	
	//分页查询
/*	@Test
	public void pagelist(){
		List<TbItem> list=new ArrayList();
		//存入数据
		for(int i=0;i<100;i++){
			TbItem item=new TbItem();
			item.setId(i+1L);
			item.setBrand("华为");
			item.setCategory("手机");
			item.setGoodsId(1L);
			item.setSeller("华为2号专卖店");
			item.setTitle("华为Mate"+i);
			item.setPrice(new BigDecimal(2000+i));	
			list.add(item);
		}		
		solr.saveBeans(list);
		solr.commit();
		

		SimpleQuery query = new SimpleQuery("*:*");
		query.setOffset(20);       //开始索引
		query.setRows(20);    //每页记录数
		ScoredPage<TbItem> page = solr.queryForPage(query, TbItem.class);
		System.out.println("总记录数"+page.getTotalElements());		
		List<TbItem> content = page.getContent();
		showList(content);
	}*/
	
	
	//条件查询
/*	@Test
	public void pageQueryList(){
		SimpleQuery query = new SimpleQuery("*:*");
		Criteria criteria = new  Criteria("item_title").is(2);
		//criteria = criteria.and("item_title").contains("5");
		query.addCriteria(criteria);
		
		ScoredPage<TbItem> page = solr.queryForPage(query, TbItem.class);
		System.out.println("总记录数："+page.getTotalElements());
		List<TbItem> list = page.getContent();
		showList(list);
	}
	*/
	
	//删除全部数据
	@Test
	public void deleall(){
		SimpleQuery query = new SimpleQuery("*:*");
		 solr.delete(query);
		 solr.commit();
	}
	
	
		//显示记录数据
	private void showList(List<TbItem> list){		
		for(TbItem item:list){
			System.out.println(item.getTitle() +item.getPrice());
		}	
	}
	
	
	
	
	
	
	
	
	
}







