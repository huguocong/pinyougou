package com.pinyougou.seckill.service;

public interface SeckillOrderService {

	/**
	 * 提交订单
	 * @param seckillId
	 * @param userId
	 */
	public void submitOrder(Long seckillId,String userId);
}
