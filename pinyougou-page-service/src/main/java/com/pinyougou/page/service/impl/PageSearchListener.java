package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

@Component
public class PageSearchListener implements MessageListener {

	@Autowired
	private ItemPageService page;
	
	
	@Override
	public void onMessage(Message message) {
		TextMessage  mess=(TextMessage) message;
		try {
			String text = mess.getText();
			System.out.println("接收到消息"+text);
			boolean b = page.getItemHtml(Long.parseLong(text));
			System.out.println("生成了"+b);
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
