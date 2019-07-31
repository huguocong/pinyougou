package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Component
public class ItemSearchListener implements MessageListener {

	@Autowired
	private ItemSearchService  itemSearch;
	
	
	@Override
	public void onMessage(Message message) {
		System.out.println("接收监听");

		TextMessage text=(TextMessage) message;
		try {
			String texts = text.getText();
			List<TbItem> list = JSON.parseArray(texts, TbItem.class);
			for (TbItem tbItem : list) {
				 Map specMAp = JSON.parseObject(tbItem.getSpec()); //将spec字段中的json字符串转换为map
				//给注解的字段赋值
				 tbItem.setSpecMap(specMAp);			 
			}
			itemSearch.importlist(list);    //导入
			System.out.println("成功导入");
			
			
			
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
