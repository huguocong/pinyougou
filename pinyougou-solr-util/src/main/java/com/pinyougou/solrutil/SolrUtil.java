package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {

	@Autowired
	private TbItemMapper mapper;
	
	@Autowired
	private SolrTemplate solr;
	
	//查询数据
	public void importdata(){
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		
		List<TbItem> list = mapper.selectByExample(example);
		
		System.out.println("商品列表");
		for (TbItem tbItem : list) {
			Map specmap=JSON.parseObject( tbItem.getSpec());    //将spec字段中的json字符串转换为map
			tbItem.setSpecMap(specmap);                 //给带注解的字段赋值	
			System.out.println(tbItem.getTitle()+",,"+tbItem.getSpecMap());
			
		}
		
		solr.saveBeans(list);             //!!!!!!!!!!!记得集合是savebeans
		solr.commit();
		
		System.out.println("结束++++");
	}
	
	
	
	
	//把数据库的数据存入solr
	public static void main(String[] args){
	   ApplicationContext con=	new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil Solr = (SolrUtil) con.getBean("solrUtil");
		Solr.importdata();
		
	}
	
	
	
	
}
