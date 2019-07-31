package com.pinyougou.manager.controller;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;

import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	@Autowired
	private  JmsTemplate  jms;
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	
	@Autowired
	private Destination topicPageDeleteDestination;   //删除页面
	
	
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			
			//删除页面
			jms.send(topicPageDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					return session.createObjectMessage(ids);
				}
			});
			
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
	//审核商品
	
	
	@Autowired
	private Destination SolrqueueTextDestination;   //发送solr导入的信息，名 必须和id一样
	
	@Autowired
	private Destination topicPageDestination;     //如果订阅模式和队列模式都有，需要声明两个Destination
	

	
	//审核通过 修改状态更新索引库
	@RequestMapping("/updateStatus")
	public Result updatestatus(Long[] ids,String status){
		
		try {
			goodsService.updateStatus(ids, status);			
			//根据spu id查询sku（状态为1）
			if("1".equals(status)){  //审核通过
			List<TbItem> list = goodsService.fincdItemandsta(ids, status);
			  if(list.size()>0){
				 // search.importlist(list);
				  
				  String jsonString = JSON.toJSONString(list);
				  jms.send(SolrqueueTextDestination, new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						
						return session.createTextMessage(jsonString);
					}
				});
		  
			  }else{
				  System.out.println("没有明细数据");
			  }
			 /* 
			  for (Long goodid : ids) {
				  page.getItemHtml(goodid);
			}*/
			  
			  //静态页面生成
			  for (Long goodid : ids) {
				jms.send(topicPageDestination, new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						
						return session.createTextMessage(goodid+"");
					}
				});
			}
			  
			  
			}
			return new Result(true, "修改状态成功");
		} catch (Exception e) {
		
			e.printStackTrace();
			return new Result(false, "修改状态失败");
		}	
	}
	
}
