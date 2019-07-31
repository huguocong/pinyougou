package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

//购物车接口
public interface CartService {

	//添加商品到购物车
	public List<Cart>addgoods(List<Cart> cartList,Long itemID,Integer num );
	
	// 从redis中查询购物车
	public List<Cart> findCartListFromredis(String name);
	
     //保存购物车到redis
	public void saveCarttoredis(String name,List<Cart>cartList);
	
	//合并购物车
	public List<Cart> mergeCartList(List<Cart>cartlist1,List<Cart>cartlist2);
}
