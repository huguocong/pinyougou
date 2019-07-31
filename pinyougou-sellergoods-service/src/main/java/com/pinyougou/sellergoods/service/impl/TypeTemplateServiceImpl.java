package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	@Autowired
	private RedisTemplate redis;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
		saveToredis(); //将数据存入缓存
		
		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Autowired
		private TbSpecificationOptionMapper specificationOptionMapper;		
		//返回规格及详细规格数
		@Override
		public List<Map> findSpecList(Long id) {
			//查询模板
			TbTypeTemplate selectByPrimaryKey = typeTemplateMapper.selectByPrimaryKey(id);
			
			         List<Map>list=JSON.parseArray(selectByPrimaryKey.getSpecIds(), Map.class);
			         for (Map map : list) {
						TbSpecificationOptionExample example = new TbSpecificationOptionExample();
						com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
						criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
						List<TbSpecificationOption> options=specificationOptionMapper.selectByExample(example);
						map.put("options", options);
					}
			
			
			return list;
		}
		
		
		//将数据存入缓存
		private void saveToredis(){
			List<TbTypeTemplate> list = findAll();
			for (TbTypeTemplate tbTypeTemplate : list) {
				//存储
				List <Map> brandList = JSON.parseArray(tbTypeTemplate.getBrandIds(),Map.class);
				redis.boundHashOps("brandList").put(tbTypeTemplate.getId(), brandList);
				
				List<Map> findSp = findSp(tbTypeTemplate.getId());
				redis.boundHashOps("specList").put(tbTypeTemplate.getId(), findSp);
				
				
			}
			
		}
		
		
		//详细规格
		@Autowired
		private TbSpecificationOptionMapper sp;
		
		public List<Map> findSp(Long id){
			//
			TbTypeTemplate template = typeTemplateMapper.selectByPrimaryKey(id);
			String specIds = template.getSpecIds();
			List<Map> list = JSON.parseArray(specIds,Map.class);		
			for (Map map : list) {
				TbSpecificationOptionExample example = new TbSpecificationOptionExample();
				com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
				criteria.andSpecIdEqualTo(new Long ((Integer)map.get("id")));
				
				List<TbSpecificationOption> list2 = sp.selectByExample(example);
				map.put("options", list2);
				
			}
			return list;
			
		}


}
