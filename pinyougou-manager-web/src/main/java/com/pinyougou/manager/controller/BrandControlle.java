package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;


//品牌管理
@RestController
@RequestMapping("/brand")
public class BrandControlle {
	
	@Reference
	private BrandService Br;
	
	
	//返回全部列表
	@RequestMapping("/findall")
	public List<TbBrand> findall(){
		return Br.findall();
	}
	
	//返回分页列表
	@RequestMapping("/findpage")
	public PageResult findpage(int page,int rows){
		PageResult result = Br.findpage(page, rows);
		return result;
		
	}
	
	//新增
	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand brand){
		try {
			Br.add(brand);
			return new Result(true, "新增成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "新增失败");
		}
	}
	
	
	//修改
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand brand){
		try {
			Br.updata(brand);
			return new Result(true, "修改成功");
		} catch (Exception e) {
		
			e.printStackTrace();
			return new Result(true, "修改失败");
		}
	}
	
	//根据id获取实体
	@RequestMapping("/findone")
	public TbBrand findone(Long id){
		return Br.findone(id);
	}
	
	
	//删除
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			Br.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
		
			e.printStackTrace();
			return new Result(true, "删除失败");
		}
	}
	
	
	//分页查询
	@RequestMapping("/seach")
	public PageResult seach(@RequestBody TbBrand brand,int page,int rows){
		return Br.findpage(brand, page, rows);
	}
	
	//下拉列表
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return Br.selectOptionList();
	}

}
