package test;

import java.util.List;

import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pinyougou.mapper.TbContentCategoryMapper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentCategory;

public class test {

	
	//记得引包
	@Test
	public void x(){
	

		ApplicationContext context = new 
				ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		/*TbContentMapper bean = context.getBean(TbContentMapper.class);
		List<TbContent> list = bean.selectByExample(null);
		for (TbContent tbContent : list) {
			System.out.println(tbContent.toString());
		}*/
		
		TbContentCategoryMapper bean2 = context.getBean(TbContentCategoryMapper.class);
		List<TbContentCategory> list2 = bean2.selectByExample(null);
		for (TbContentCategory tbContentCategory : list2) {
			System.out.println(tbContentCategory.toString());
		}
		
	}
}

