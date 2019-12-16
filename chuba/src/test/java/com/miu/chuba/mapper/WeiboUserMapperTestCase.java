package com.miu.chuba.mapper;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.miu.chuba.entity.WeiboUser;
import com.miu.chuba.entity.WeixinUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeiboUserMapperTestCase {

	@Autowired
	public WeiboUserMapper mapper;
	
	@Test
	public void insert() {
		WeiboUser user = new WeiboUser();
		user.setWbName("CT_333");;
		user.setWxId(1);
		Integer rows = mapper.insert(user);
		System.err.println("rows=" + rows);
	}
	
/*	@Test
	public void findByWeixinAccount() {
		WeixinUser u=mapper.findByWxAccount("111");
		System.err.println("weixinUser=" + u);
	}*/
}








