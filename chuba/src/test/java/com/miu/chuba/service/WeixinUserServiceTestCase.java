package com.miu.chuba.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.miu.chuba.entity.WeiboUser;
import com.miu.chuba.entity.WeixinUser;
import com.miu.chuba.entity.ZhifubaoUser;
import com.miu.chuba.service.ex.ServiceException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeixinUserServiceTestCase {
	
	@Autowired     // 微信
	public IWeixinUserService weixinService;
	@Autowired    //微博
	public IWeiboUserService weiboService;
	@Autowired    //支付宝
	public IZhifubaoUserService zhifubaoService;
	//TODO******************************************************添加*******************
	//添加支付宝
	@Test
	public void addNewZhifubao() {
		try {
			ZhifubaoUser User=new ZhifubaoUser();
			User.setWxId(1);
			User.setZfbAccount("18734567667");
			User.setZfbName("李怡媛");;
			zhifubaoService.addNew(User, "root");
			System.err.println("OK.1");
			/*****************************************/
			User.setWxId(2);
			User.setZfbAccount("15280212375");
			User.setZfbName("高歌");;
			zhifubaoService.addNew(User, "root");
			System.err.println("OK.2");
			/*****************************************/
			User.setWxId(3);
			User.setZfbAccount("18150628339");
			User.setZfbName("王晶");;
			zhifubaoService.addNew(User, "root");
			System.err.println("OK.3");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	//添加微博
	@Test
	public void addNewByWeibo() {
		try {
			WeiboUser weiboUser=new WeiboUser();
			weiboUser.setWbName("CT_333");
			weiboUser.setWbUrl("https://weibo.com/u/2252317311?is_all=1");
			weiboService.addNew(weiboUser,"root");
			System.err.println("OK.1");
			/*****************************************/
			weiboUser.setWbName("dear喵小美");
			weiboUser.setWbUrl("https://weibo.com/fogoya?is_all=1");
			weiboService.addNew(weiboUser,"root");
			System.err.println("OK.2");
			/*****************************************/
			weiboUser.setWbName("today你吃了吗");
			weiboUser.setWbUrl("https://weibo.com/u/2339108622?is_all=1");
			weiboService.addNew(weiboUser,"root");
			System.err.println("OK.3");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	//添加微信用户
	@Test
	public void addNewByWeixin() {
		try {
			WeixinUser weixinUser=new WeixinUser();
			weixinUser.setWxAccount("ss-599");;
			weixinUser.setWxName("CT_333");
			weixinUser.setPlatform(1);
			weixinUser.setGender(0);
			weixinService.addNew(weixinUser,"root");
			System.err.println("OK.1");
			/*****************************************/
			weixinUser.setWxAccount("fogoya");;
			weixinUser.setWxName("dear喵小美");
			weixinUser.setPlatform(1);
			weixinUser.setGender(1);
			weixinService.addNew(weixinUser,"root");
			System.err.println("OK.2");
			/*****************************************/
			weixinUser.setWxAccount("WWWWWWW-1");;
			weixinUser.setWxName("today你吃了吗");
			weixinUser.setPlatform(1);
			weixinUser.setGender(0);
			weixinService.addNew(weixinUser,"root");
			System.err.println("OK.3");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	//TODO**************************************************************更新******************************
	//修改微信用户信息
	@Test
	public void changInfoByWx() {
		try {
			WeixinUser users=new WeixinUser();
			users.setWxId(1);
			users.setWxAccount("ss-599");
			users.setWxName("CT_333");
			users.setPlatform(1);
			users.setGender(0);
			weixinService.changeInfo(users,"root");
			System.err.println("ok");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	//修改微博用户信息
	@Test
	public void changInfoByWb() {
		try {
			WeiboUser users=new WeiboUser();
			users.setWbId(1);
			users.setWxId(null);
			users.setWbName("CT_333");
			users.setWbUrl("https://weibo.com/u/2252317311?is_all=1");
			weiboService.changeInfo(users,"root");
			System.err.println("ok");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	//修改支付宝信息
	@Test
	public void changInfoByZfb() {
		try {
			ZhifubaoUser zhifubaoUser=new ZhifubaoUser();
			zhifubaoUser.setZfbId(1);
			zhifubaoUser.setWxId(1);
			zhifubaoUser.setZfbAccount("18734567667");
			zhifubaoUser.setZfbName("李怡媛");
			zhifubaoService.changeInfo(zhifubaoUser,"root");
			System.err.println("ok");
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	//TODO**************************************************************查询******************************
	//根据微信名字查询用户信息
	@Test
	public void getByWxName() {
		try {
			String wxName="CT_333";
			WeixinUser weixinUser=weixinService.getByWxName(wxName);
			System.err.println(weixinUser);
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	//根据微信账号查询用户信息
	@Test
	public void getByWxAccount() {
		try {
			String wxAccount="fogoya";
			WeixinUser weixinUser=weixinService.getByWxAccount(wxAccount);
			System.err.println(weixinUser);
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	//根据微博名字查询微博用户信息
	@Test
	public void getByWbName() {
		try {
			String WbName="today你吃了吗";
			WeiboUser weiboUser=weiboService.getByWbName(WbName);
			System.err.println(weiboUser);
		} catch (ServiceException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
	}
	
}








