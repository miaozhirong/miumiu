package com.miu.chuba.service;

import com.miu.chuba.entity.WeiboUser;
import com.miu.chuba.service.ex.InsertException;
import com.miu.chuba.service.ex.UpdateException;
import com.miu.chuba.service.ex.UserNotFoundException;
import com.miu.chuba.service.ex.UsernameDuplicateException;

/**
 * 处理微博用户数据的业务层接口
 */
public interface IWeiboUserService {
	/**
	 * 根据微博名字来查询微信用户信息 
	 * @param wbName
	 * @return
	 * @throws UserNotFoundException
	 */
	WeiboUser getByWbName(String wbName)throws UserNotFoundException;
	
	/**
	 * 添加微博用户
	 * @param weiboUser
	 * @throws UsernameDuplicateException 
	 * @throws InsertException
	 */
	void addNew(WeiboUser weiboUser,String username)throws UsernameDuplicateException, 
	InsertException;
	
	/**
	 * 修改微博用户信息
	 * @param zhifubaoUser
	 * @throws UserNotFoundException
	 * @throws UpdateException
	 */
	void changeInfo(WeiboUser weiboUser,String username)throws UserNotFoundException,UpdateException;
}
