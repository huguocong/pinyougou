package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbSellerMapper SellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getGoods());	
	
		
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());
		
		/*if("1".equals(goods.getGoods().getIsEnableSpec())){
		for(TbItem item:goods.getItemList()){
			//标题
			String title = goods.getGoods().getGoodsName();
			Map <String,Object> specMap = JSON.parseObject(item.getSpec());
			for (String key : specMap.keySet()) {
				title+=" "+specMap.get(key);
			}
			item.setTitle(title);
			setItemValus(goods,item);	
			itemMapper.insert(item);
		}
		
		}else{
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
			item.setPrice( goods.getGoods().getPrice() );//价格			
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认			
			item.setNum(99999);//库存数量
			item.setSpec("{}");			
			setItemValus(goods,item);					
			itemMapper.insert(item);
		}*/
		
		saveItem(goods);        //插入SKU
		
		
		
		
	}
	
	
	private void saveItem(Goods goods){
		if("1".equals(goods.getGoods().getIsEnableSpec())){
			for(TbItem item:goods.getItemList()){
				//标题
				String title = goods.getGoods().getGoodsName();
				Map <String,Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title+=" "+specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(goods,item);	
				itemMapper.insert(item);
			}
			
			}else{
				TbItem item=new TbItem();
				item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
				item.setPrice( goods.getGoods().getPrice() );//价格			
				item.setStatus("1");//状态
				item.setIsDefault("1");//是否默认			
				item.setNum(99999);//库存数量
				item.setSpec("{}");			
				setItemValus(goods,item);					
				itemMapper.insert(item);
			}
	}
	
	private void setItemValus(Goods goods,TbItem item){
		
		item.setGoodsId(goods.getGoods().getId());  //商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId()); //商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id()); //商品分类id
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		
		//品牌、
	     TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
	     item.setBrand(brand.getName());
		//分类名称
	     TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
	     item.setCategory(itemCat.getName());
	     //商家名
	     TbSeller seller = SellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
	     item.setSeller(seller.getNickName());
	     //图片地址
	     List<Map> list = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
	     if(list.size()>0){
	    	 item.setImage((String) list.get(0).get("url"));
	     }
	
	}
	

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//goodsMapper.updateByPrimaryKey(goods);
		goods.getGoods().setAuditStatus("0");
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		
		//删除SKU列表
		TbItemExample example = new  TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		
		saveItem(goods);
		
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){		
	   Goods goods = new Goods();
	   TbGoods goods1 = goodsMapper.selectByPrimaryKey(id);
	   goods.setGoods(goods1);
	   TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
	   goods.setGoodsDesc(goodsDesc);
	   
	   TbItemExample example = new TbItemExample();
	   com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
         criteria.andGoodsIdEqualTo(id);
         List<TbItem> list = itemMapper.selectByExample(example);
         goods.setItemList(list);
         
  
	   
	   
	   return goods;
		//return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
							criteria.andSellerIdEqualTo(goods.getSellerId());							
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		
		//审核商品
		@Override
		public void updateStatus(Long[] ids, String status) {
			// TODO Auto-generated method stub
			for (Long long1 : ids) {
				TbGoods goods = goodsMapper.selectByPrimaryKey(long1);
				goods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(goods);
			}
			
		}

		//商品下架
		@Override
		public void xiajia(Long[] ids) {
			for (Long long1 : ids) {
				TbGoods goods = goodsMapper.selectByPrimaryKey(long1);
				goods.setIsMarketable("0");
				goodsMapper.updateByPrimaryKey(goods);
			}
			
		}

		@Override
		public List<TbItem> fincdItemandsta(Long[] ids, String status) {
			TbItemExample example = new TbItemExample();
			com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdIn(Arrays.asList(ids));
			criteria.andStatusEqualTo(status);
			return itemMapper.selectByExample(example);
		}
	
}
