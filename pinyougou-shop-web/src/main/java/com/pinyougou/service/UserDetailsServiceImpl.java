package com.pinyougou.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService {

	
	private SellerService selleerService;
	
	
	
	public void setSelleerService(SellerService selleerService) {
		this.selleerService = selleerService;
	}



	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		
		//构建角色列表
	   List<GrantedAuthority> grant=new ArrayList<GrantedAuthority>();
		grant.add(new SimpleGrantedAuthority("ROLE_SHOP"));
		
		//得到商家对象
		    TbSeller tbSeller=null;
			try {
				
				tbSeller = selleerService.findOne(username);
			System.out.println(tbSeller.getPassword());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

			if(tbSeller!=null){
				if(tbSeller.getStatus().equals("1")){
				return new User(username,tbSeller.getPassword(),grant);
				}else{
					return null;
					
				}
				}else{
					return null;
				}
	
		
		
		
		
		/*List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();  
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_SHOP"));          
        return new User(username,"123456", grantedAuths);
	
		*/
		
	
	}

}
