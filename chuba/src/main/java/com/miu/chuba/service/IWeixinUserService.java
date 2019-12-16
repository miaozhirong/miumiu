package com.miu.chuba.service;

import com.miu.chuba.entity.WeixinUser;
import com.miu.chuba.service.ex.InsertException;
import com.miu.chuba.service.ex.UpdateException;
import com.miu.chuba.service.ex.UserNotFoundException;
import com.miu.chuba.service.ex.UsernameDuplicateException;

/**
 * 处理微信用户数据的业务层接口
 */
public interface IWeixinUserService {
	/**
	 * 根据微信名字来查询微信用户信息 
	 * @param wxName
	 * @return
	 * @throws UserNotFoundException
	 */
	WeixinUser getByWxName(String wxName)throws UserNotFoundException;
	
	/**
	 * 根据微信账号来查询微信用户信息
	 * @param wxAccount
	 * @return
	 * @throws UserNotFoundException
	 */
	WeixinUser getByWxAccount(String wxAccount)throws UserNotFoundException;
	
	/**
	 * 添加微信用户
	 * @param weixinUser
	 * @throws UsernameDuplicateException 
	 * @throws InsertException
	 */
	void addNew(WeixinUser weixinUser,String username)throws UsernameDuplicateException, 
	InsertException;
	
	/**
	 * 修改微信用户信息
	 * @param zhifubaoUser
	 * @throws UserNotFoundException
	 * @throws UpdateException
	 */
	void changeInfo(WeixinUser weixinUser,String username)throws UserNotFoundException,UpdateException;
}
