package com.pinyougou.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class LoginController {

	
	@RequestMapping("/name")
	public Map showName() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		HashMap  map = new HashMap<>();
		
		System.out.println("登录名是："+name);
		
		map.put("loginname", name);
		return map;
	}
}
