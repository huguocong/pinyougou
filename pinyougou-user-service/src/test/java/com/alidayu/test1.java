package com.alidayu;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class test1 {
	
	
	@Value("${zookeeper}")
	private String sign_name;   //签名
	
	
	@Test
	public void  x() {
		System.out.println(sign_name);
	}

}
