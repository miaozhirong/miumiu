package com.miu.chuba.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.miu.chuba.entity.User;
import com.miu.chuba.service.ex.ServiceException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTestCase {
	
	@Autowired
	public IUserService service;

	@Test
	public void reg() {
		try {
			User user = new User();
			user.setUsername("root");
			user.setPassword("8888");
			service.reg(user);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void login() {
		try {
			String username = "root";
			String password = "8888";
			User result = service.login(username, password);
			System.err.println(result);
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void changeInfo() {
		try {
			User user = new User();
			user.setUid(11);
			user.setModifiedUser("超级管理员");
			user.setPhone("13800138011");
			user.setEmail("root@tedu.cn");
			user.setGender(1);
			service.changeInfo(user);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void changePassword() {
		try {
			Integer uid = 13;
			String username = "Admin";
			String oldPassword = "8888";
			String newPassword = "1234";
			service.changePassword(uid, oldPassword, newPassword, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void changeAvatar() {
		try {
			Integer uid = 14;
			String username = "Admin";
			String avatar = "新头像地址";
			service.changeAvatar(uid, avatar, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void getByUid() {
		try {
			Integer uid = 11;
			User user = service.getByUid(uid);
			System.err.println(user);
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	
}








