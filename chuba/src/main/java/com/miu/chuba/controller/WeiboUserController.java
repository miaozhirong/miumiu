package com.miu.chuba.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miu.chuba.entity.WeiboUser;
import com.miu.chuba.entity.WeixinUser;
import com.miu.chuba.service.IWeiboUserService;
import com.miu.chuba.service.IWeixinUserService;
import com.miu.chuba.util.ResponseResult;

@RestController
@RequestMapping("weibo")
public class WeiboUserController extends BaseController{
	
	@Autowired
	private IWeiboUserService WeiboUserService;
	/**
	 * 根据微信账号来查询用户
	 * @param wxAccount
	 * @return
	 */
	@RequestMapping("get/{wbName}/account")
	public ResponseResult<WeiboUser> getBywbName(@PathVariable("wbName") String wbName) {
		// 执行注册
		WeiboUser data=WeiboUserService.getByWbName(wbName);
		// 返回成功
		return new ResponseResult<>(SUCCESS,data);
	}
	/**
	 * 添加微信用户
	 * @param weiboUser
	 * @param session
	 * @return
	 */
	@RequestMapping("add")
	public ResponseResult<Void> addNew(WeiboUser weiboUser,HttpSession session) {
		// 执行注册
		WeiboUserService.addNew(weiboUser, getUsernameSession(session));
		// 返回成功
		return new ResponseResult<>(SUCCESS);
	}
}
