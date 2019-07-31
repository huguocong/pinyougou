package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.PageResult;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	
	
	

		
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	@Autowired
	private IdWorker idorker;

	@Override
	public void submitOrder(Long seckillId, String userId) {
		
		//1.查询缓存中的商品
		TbSeckillGoods seckillGoods= (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
		if(seckillGoods==null){
			throw new RuntimeException("商品不存在");			
		}
		if(seckillGoods.getStockCount()<=0){
			throw new RuntimeException("商品已经被抢光");			
		}
		
		//2.减少库存
		seckillGoods.setStockCount( seckillGoods.getStockCount()-1  );//减库存
		redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);//存入缓存
		if(seckillGoods.getStockCount()==0){
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);	//更新数据库
			redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
			System.out.println("商品同步到数据库...");
		}
		
		//3.存储秒杀订单 (不向数据库存 ,只向缓存中存储 )
		TbSeckillOrder seckillOrder=new TbSeckillOrder();
		seckillOrder.setId(idorker.nextId());
		seckillOrder.setSeckillId(seckillId);
		seckillOrder.setMoney(seckillGoods.getCostPrice());
		seckillOrder.setUserId(userId);
		seckillOrder.setSellerId(seckillGoods.getSellerId());//商家ID
		seckillOrder.setCreateTime(new Date());
		seckillOrder.setStatus("0");//状态
		
		
		redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
		System.out.println("保存订单成功(redis)");
	}


	
		
		
		
		
	
	
}
