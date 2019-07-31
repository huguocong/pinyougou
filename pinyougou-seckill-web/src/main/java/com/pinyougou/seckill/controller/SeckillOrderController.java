package com.pinyougou.seckill.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.Result;

@RestController
@RequestMapping("/submitOrder")
public class SeckillOrderController {

	@Reference(timeout=1000000)
	private SeckillOrderService seckillOrderService;
	
	
	@RequestMapping("/submitOrder")
	public Result submitOrder(Long seckillId) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if("anonymousUser".equals(name)){
			return new Result(false, "用户未登录");
		}
		try {
			seckillOrderService.submitOrder(seckillId, name);
			return new Result(true, "提交成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "提交失败");
		}
		
		
		
	}
}
