package com.pinyougou.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

@Component
public class stack {

	@Autowired
	private RedisTemplate redis;
	@Autowired
	private TbSeckillGoodsMapper tbSeckillGoodsMapper;
	
	/*
	@Scheduled(cron="* * * * * ?")
	public void s(){
		System.out.println("执行了调度"+new Date());
	}*/
	
	//刷新秒杀商品  增加
	@Scheduled(cron="0 * * * * ?")
	public void refreshSeckillGoods() {
		System.out.println("执行了任务调度");
		//从redis查询所有键
		List list = new ArrayList<>(redis.boundHashOps("seckillGoods").keys());
		System.out.println(list.size());
		
		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		criteria.andStockCountGreaterThan(0);   //剩余大于0
		criteria.andStartTimeLessThanOrEqualTo(new Date());
		criteria.andEndTimeGreaterThanOrEqualTo(new Date());
		if(list.size()>0) {
		criteria.andIdNotIn(list);     
		}//排除缓存里有的商品
		List<TbSeckillGoods> listgoods = tbSeckillGoodsMapper.selectByExample(example);
		System.out.println(listgoods.size());
		
		//存入
		for (TbSeckillGoods tbSeckillGoods : listgoods) {
			redis.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(), tbSeckillGoods);
			
		}
		System.out.println("将"+listgoods.size()+"存入");
	
	}
	
	
	//移除
	@Scheduled(cron="0/5 * * * * ?")
	public void removeSeckilloods() {
		System.out.println("执行移除调度"+new Date());
		
		List<TbSeckillGoods> list = redis.boundHashOps("seckillGoods").values();  
		
		     for (TbSeckillGoods tbSeckillGoods : list) {
		    	 System.out.println("结束时间"+tbSeckillGoods.getEndTime());
		    	if( tbSeckillGoods.getEndTime().getTime()<new Date().getTime() ) {
		    		//过期
		    		redis.boundHashOps("seckillGoods").delete(tbSeckillGoods.getId()); //移除
		    		System.out.println("移除商品"+tbSeckillGoods.getId());
		    	}
			}
		     System.out.println("结束");
		
	}
	
	
	
}
