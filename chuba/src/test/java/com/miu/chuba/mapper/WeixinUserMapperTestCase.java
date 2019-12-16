package com.miu.chuba.mapper;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.miu.chuba.entity.WeixinUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeixinUserMapperTestCase {

	@Autowired
	public WeixinUserMapper mapper;
	
	@Test
	public void insert() {
		WeixinUser user = new WeixinUser();
		user.setWxAccount("111");
		user.setWxName("111");;
		Integer rows = mapper.insert(user);
		System.err.println("rows=" + rows);
	}
	
	@Test
	public void findByWeixinAccount() {
		WeixinUser u=mapper.findByWxAccount("111");
		System.err.println("weixinUser=" + u);
	}
}








