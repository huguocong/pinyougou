package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;
import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout=60000)
	private CartService cartservice;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	
	
	
	@RequestMapping("/addgoodsToCartList")
	//@CrossOrigin(origins="http://localhost:9105",allowCredentials="true")    //springMVC的版本在4.2或以上版本，可以使用注解实现跨域
	public Result add(Long itemid,Integer num) {
		
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");    //可以访问的域，如果没有cookie，可以只加这一句
		response.setHeader("Access-Control-Allow-Credentials", "true");         //如果需要cookie，必须加这个
		
		
		String name = SecurityContextHolder.getContext().getAuthentication().getName();  //获取登录名
		System.out.println("当前登录："+name);
		
						
						try {
							List<Cart>cartList=fingCartList();
							cartList = cartservice.addgoods(cartList, itemid, num);
							
							if(name.equals("anonymousUser")) {
							                     //                  购物车      购物车列表                  保存的时间              
								CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600*24, "utf-8");
									System.out.println("添加到cookie");					
							}else {
								cartservice.saveCarttoredis(name, cartList);
								System.out.println("添加到redis");
							}
							
							
							
							return new Result(true, "添加购物车成功");
						}catch (RuntimeException e) {
							e.printStackTrace();
							return new Result(false, e.getMessage());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return new Result(false, "添加购物车失败");
						}
	}

	//获取购物车列表
	@RequestMapping("/findcartList")
	private List<Cart> fingCartList() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();  //获取登录名
		String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		
		if(cartListString==null||cartListString.equals("")) {
			cartListString="[]";
		}		
		List<Cart> list_cookie=JSON.parseArray(cartListString, Cart.class);
		
		
		
		if(name.equals("anonymousUser")) {                    //未登录
			System.out.println("从cookie里面提取");							
				return list_cookie;
	     }else {            //登录        
		   List<Cart> listcart_redis = cartservice.findCartListFromredis(name);  //从redis提取
		   if(list_cookie.size()>0) {  //存在cookie购物车
			   listcart_redis= cartservice.mergeCartList(listcart_redis, list_cookie);
			   //清除cookie
			   CookieUtil.deleteCookie(request, response, "cartList");
			   cartservice.saveCarttoredis(name, listcart_redis);
		   }
		
		   return listcart_redis; 	
	      }
		
	}
	
	
	
	
}
