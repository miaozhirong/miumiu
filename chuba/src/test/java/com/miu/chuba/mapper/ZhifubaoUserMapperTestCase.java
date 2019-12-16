package com.miu.chuba.mapper;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.miu.chuba.entity.WeiboUser;
import com.miu.chuba.entity.WeixinUser;
import com.miu.chuba.entity.ZhifubaoUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZhifubaoUserMapperTestCase {

	@Autowired
	public ZhifubaoUserMapper mapper;
	
	@Test
	public void insert() {
		ZhifubaoUser user = new ZhifubaoUser();
		user.setWxId(2);
		user.setZfbAccount("12345");
		user.setZfbName("aaaa");
		Integer rows = mapper.insert(user);
		System.err.println("rows=" + rows);
	}
	
/*	@Test
	public void findByWeixinAccount() {
		WeixinUser u=mapper.findByWxAccount("111");
		System.err.println("weixinUser=" + u);
	}*/
}








