package com.miu.chuba.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miu.chuba.entity.WeixinUser;
import com.miu.chuba.service.IWeixinUserService;
import com.miu.chuba.util.ResponseResult;

@RestController
@RequestMapping("weixin")
public class WeixinUserController extends BaseController{
	
	@Autowired
	private IWeixinUserService WeixinUserService;
	/**
	 * 根据微信名字来查询用户
	 * @param wxAccount
	 * @return
	 */
	@RequestMapping("get/{wxName}/name")
	public ResponseResult<WeixinUser> getBywxName(@PathVariable("wxName") String wxName) {
		// 执行注册
		WeixinUser data=WeixinUserService.getByWxName(wxName);
		// 返回成功
		return new ResponseResult<>(SUCCESS,data);
	}
	
	/**
	 * 根据微信账号来查询用户
	 * @param wxAccount
	 * @return
	 */
	@RequestMapping("get/{wxAccount}/account")
	public ResponseResult<WeixinUser> getByAccount(@PathVariable("wxAccount") String wxAccount) {
		// 执行注册
		WeixinUser data=WeixinUserService.getByWxAccount(wxAccount);
		// 返回成功
		return new ResponseResult<>(SUCCESS,data);
	}
	
	
	/**
	 * 添加微信用户
	 * @param weixinUser
	 * @param session
	 * @return
	 */
	@RequestMapping("add")
	public ResponseResult<Void> addNew(WeixinUser weixinUser,HttpSession session) {
		// 执行注册
		WeixinUserService.addNew(weixinUser, getUsernameSession(session));
		// 返回成功
		return new ResponseResult<>(SUCCESS);
	}
}
