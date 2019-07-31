package com.pinyougou.page.service.impl;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

@Component
public class PageDeleteListener implements MessageListener {

	
	@Autowired
	private ItemPageService page; 
	
	@Override
	public void onMessage(Message message) {
	
		ObjectMessage mess=(ObjectMessage) message;
		
		    
		try {
			Long[]goodis = (Long[]) mess.getObject();
			boolean b = page.deletHtml(goodis);
			System.out.println("成功删除"+b);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
