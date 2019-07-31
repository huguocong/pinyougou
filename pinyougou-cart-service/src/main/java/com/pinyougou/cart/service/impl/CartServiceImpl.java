package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

@Service
public class CartServiceImpl implements CartService {


		
		@Autowired
		private TbItemMapper item;
		
		
	public List<Cart> addgoods(List<Cart> cartList, Long itemID, Integer num) {
		/*1、根据商品查询sku
		 * 2、获取商家id
		 * 3、根据购物车列表判断是否有该商家的购物车
		 * 4、如果不存在该商家购物车，新建购物车对象添加到购物车列表
		 * 5、如果存在，查询购物车明细中是否有该商品
		 * 6如果有，在原商品上添加数量，
		 * 7、如果没有，新增购物车明细
		 * 
		 * */
	    TbItem tbItem = item.selectByPrimaryKey(itemID);      //根据商品查询sku
	    if(tbItem==null) {
	    	throw new RuntimeException("商品不存在");
	    }
	    if(!tbItem.getStatus().equals("1")) {
	    	throw new RuntimeException("商品无效");
	    }
	    
	    String id = tbItem.getSellerId();                   //获取商家id
	    
	    Cart cart = searchCartbysellerID(cartList,id);
	    if(cart==null) {
	    	 cart = new Cart();
	    	 cart.setSellerId(id);
	    	 cart.setSellerName(tbItem.getSeller());   	 
	    	 TbOrderItem orderItemList=create(tbItem,num) ;
	    	 List list = new ArrayList<>();
	    	 list.add(orderItemList); 	 
			 cart.setOrderItemList(list);			 			 
			 cartList.add(cart);
	    }else {
	    	
	    	//判断该购物车明细里是否存在该商品
	    	TbOrderItem orderItem = searchOrderItemByitemID(cart.getOrderItemList(),itemID);
	    	if(orderItem==null) {
	    		orderItem=create(tbItem,num);
	    		cart.getOrderItemList().add(orderItem);
	    		
	    	}else {
	    		orderItem.setNum(orderItem.getNum()+num);
	    		orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
	    		
	    		
	    		//如果操作后数量小于等于0，则移除
	    		if(orderItem.getNum()<=0) {
	    			cart.getOrderItemList().remove(orderItem);
	    		}
	    		
	    		//如果移除该商品购物车明细数量为0，则移除该购物车    		
	    		if(cart.getOrderItemList().size()<=0) {
	    			cartList.remove(cart);
	    		}
	    		
	    	}
	    	
	    }
		return cartList;
	}

	// 判断购物车明细列表中是否存在该商品
	private TbOrderItem searchOrderItemByitemID(List<TbOrderItem> orderItemList, Long itemID) {		
		for (TbOrderItem tbOrderItem : orderItemList) {
			if(tbOrderItem.getItemId().longValue()==(itemID.longValue())) {
				
				return tbOrderItem;
			}
		}
		return null;
	
		
	}
	//创建购物车订单明细
	private TbOrderItem create(TbItem item2, Integer num) {
		if(num<=0) {
			throw new RuntimeException("数量非法");
		}
		TbOrderItem tbOrderItem = new TbOrderItem();
		tbOrderItem.setNum(num);
		tbOrderItem.setGoodsId(item2.getGoodsId());
	    tbOrderItem.setItemId(item2.getId());
		tbOrderItem.setPrice(item2.getPrice());
		tbOrderItem.setPicPath(item2.getImage());
		tbOrderItem.setSellerId(item2.getSellerId());
		tbOrderItem.setTitle(item2.getTitle());
		tbOrderItem.setTotalFee(new BigDecimal(num*item2.getPrice().doubleValue()));
		return tbOrderItem;
	}

	//判断购物车列表是否存在该商家的购物车
	private Cart searchCartbysellerID(List<Cart> cartList, String id) {

		for (Cart cart : cartList) {
			if(cart.getSellerId().equals(id)) {
				return cart;
			}
		}
		return null;
	
	}

	@Autowired
	private RedisTemplate redis;
	
	//从redis取购物车
	@Override
	public List<Cart> findCartListFromredis(String name) {
		List<Cart>cartList=(List<Cart>) redis.boundHashOps("cartList").get(name);		
		System.out.println("从redis取购物车");
		if(cartList==null) {
			cartList=new ArrayList<>();
		}
		
		
		return cartList;
	}

	
	//购物车保存到redis
	@Override
	public void saveCarttoredis(String name, List<Cart> cartList) {
		System.out.println("存放到redis");
		redis.boundHashOps("cartList").put(name, cartList);
		
		
		
	}

	
	//合并购物车
	@Override
	public List<Cart> mergeCartList(List<Cart> cartlist1, List<Cart> cartlist2) {
		System.out.println("合并购物车");
		for (Cart cart : cartlist2) {
			for (TbOrderItem orderitem : cart.getOrderItemList()) {
				cartlist1= addgoods(cartlist1,orderitem.getItemId(),orderitem.getNum());
				
			}
		}
		
		
		return cartlist1;
	}



}
