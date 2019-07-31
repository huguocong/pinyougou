package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

//品牌服务接口
public interface BrandService {

	
	//返回全部列表
	public List<TbBrand> findall();
	
	//返回分页列表
	public PageResult findpage(int pageNum,int pageSize);
	
	//新增品牌
	public void add(TbBrand brand);
	
	//根据id修改品牌
	//查询
	public TbBrand findone(Long id);
	public void updata(TbBrand brand);

	
	//删除品牌
	public void delete(Long [] ids);
	
	//模糊查询
	public PageResult findpage(TbBrand brand,int pageNum,int pageSize);
	
	//品牌下拉
	List<Map> selectOptionList();
}
