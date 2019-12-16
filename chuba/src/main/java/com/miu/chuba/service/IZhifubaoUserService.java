package com.miu.chuba.service;

import com.miu.chuba.entity.ZhifubaoUser;
import com.miu.chuba.service.ex.InsertException;
import com.miu.chuba.service.ex.UpdateException;
import com.miu.chuba.service.ex.UserNotFoundException;
import com.miu.chuba.service.ex.UsernameDuplicateException;

/**
 * 处理支付宝用户数据的业务层接口
 */
public interface IZhifubaoUserService {
	/**
	 * 根据支付宝id来查询支付宝用户信息 
	 * @param wxId
	 * @return
	 * @throws UserNotFoundException
	 */
	ZhifubaoUser getByZfbId(Integer zfbId)throws UserNotFoundException;
	
	/**
	 * 根据微信id来查询支付宝用户信息 
	 * @param wxId
	 * @return
	 * @throws UserNotFoundException
	 */
	ZhifubaoUser getByWxId(Integer wxId)throws UserNotFoundException;
	
	
	/**
	 * 添加支付宝用户
	 * @param weixinUser
	 * @throws UsernameDuplicateException 
	 * @throws InsertException
	 */
	void addNew(ZhifubaoUser zhifubaoUser,String username)throws UsernameDuplicateException,InsertException;
	/**
	 * 修改支付宝用户信息
	 * @param zhifubaoUser
	 * @throws UserNotFoundException
	 * @throws UpdateException
	 */
	void changeInfo(ZhifubaoUser zhifubaoUser,String username)throws UserNotFoundException,UpdateException;
}
