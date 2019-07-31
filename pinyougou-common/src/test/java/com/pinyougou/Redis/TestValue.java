package com.pinyougou.Redis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/applicationContext-redis.xml")
public class TestValue {

	@Autowired
	private RedisTemplate redis;
	
	
	/*@Test
	public void setValue(){
		redis.boundValueOps("name").set("icast");
		String s=(String) redis.boundValueOps("name").get();
		System.out.println(s);
	}*/
	
/*	@Test
	public void find() {
		String phone="18570361426";
		String object = (String) redis.boundHashOps("smscode").get(phone);
		System.out.println(object);
	}*/
	
	@Test
	public void delete(){
	
		List lis=new ArrayList<>(redis.boundHashOps("seckillGoods").keys());
		System.out.println(lis.size());
		for (Object object : lis) {
			
			Long delete = redis.boundHashOps("seckillGoods").delete(object);
			System.out.println(delete);
		}
		for (Object object : lis) {
			System.out.println(object.toString());
			
		}
		
	}
}
