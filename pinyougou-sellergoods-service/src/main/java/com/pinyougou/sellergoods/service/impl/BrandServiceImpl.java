package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper mappser;
	
	//返回全部列表
	@Override
	public List<TbBrand> findall() {
	
		return mappser.selectByExample(null);
	}

	
	//返回分页列表
	@Override
	public PageResult findpage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) mappser.selectByExample(null);
		
		return new PageResult(page.getTotal(), page.getResult());
	}


	
	//新增
	@Override
	public void add(TbBrand brand) {
		mappser.insert(brand);
		
	}

	
	//根据id查询
	@Override
	public TbBrand findone(Long id) {	
		TbBrand brand = mappser.selectByPrimaryKey(id);
		return brand;				
	}	
	//修改
	@Override
	public void updata(TbBrand brand) {
		 mappser.updateByPrimaryKey(brand);
	
	}


	//删除
	@Override
	public void delete(Long[] ids) {
		for (Long long1 : ids) {
			mappser.deleteByPrimaryKey(long1);
		}
		
	}


	//模糊查询
	@Override
	public PageResult findpage(TbBrand brand, int pageNum, int pageSize) {
		
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if(brand!=null){
			if(brand.getName()!=null && brand.getName().length()>0){
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
				criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
			
		}
		
		
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) mappser.selectByExample(example);
		
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Override
	public List<Map> selectOptionList() {
		
		return mappser.selectOptionList();
	}

}
