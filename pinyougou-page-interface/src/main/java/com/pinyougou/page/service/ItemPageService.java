package com.pinyougou.page.service;


//商品详情页接口
public interface ItemPageService {

	//生成商品详情页
	public boolean getItemHtml(Long goodid);
	
	//删除商品页面
	public boolean deletHtml(Long[]goodsid);
}
