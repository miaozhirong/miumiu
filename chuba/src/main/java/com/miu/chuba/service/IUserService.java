package com.miu.chuba.service;

import com.miu.chuba.entity.User;
import com.miu.chuba.service.ex.InsertException;
import com.miu.chuba.service.ex.PasswordNotMatchException;
import com.miu.chuba.service.ex.UpdateException;
import com.miu.chuba.service.ex.UserNotFoundException;
import com.miu.chuba.service.ex.UsernameDuplicateException;

/**
 * 处理用户数据的业务层接口
 */
public interface IUserService {
	
	/**
	 * 用户注册 
	 * @param user 将要注册的用户数据
	 * @throws UsernameDuplicateException
	 * @throws InsertException
	 */
	void reg(User user) 
		throws UsernameDuplicateException, 
			InsertException;
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 成功登录的用户的信息
	 * @throws UserNotFoundException
	 * @throws PasswordNotMatchException
	 */
	User login(String username, String password) 
		throws UserNotFoundException, 
			PasswordNotMatchException;

	/**
	 * 修改用户个人资料
	 * @param user 用户个人资料
	 * @throws UserNotFoundException
	 * @throws UpdateException
	 */
	void changeInfo(User user) 
		throws UserNotFoundException, 
			UpdateException;
	
	/**
	 * 修改用户密码
	 * @param uid 用户id
	 * @param oldPassword 原密码
	 * @param newPassword 新密码
	 * @param username 当前登录的用户的用户名
	 * @throws UserNotFoundException
	 * @throws PasswordNotMatchException
	 * @throws UpdateException
	 */
	void changePassword(
		Integer uid, String oldPassword, 
		String newPassword, String username) 
			throws UserNotFoundException, 
				PasswordNotMatchException, 
					UpdateException;
	
	/**
	 * 修改用户头像
	 * @param uid 用户id
	 * @param avatar 头像的路径
	 * @param username 当前登录的用户的用户名
	 * @throws UserNotFoundException
	 * @throws UpdateException
	 */
	void changeAvatar(
		Integer uid, String avatar, 
			String username) 
			throws UserNotFoundException, 
				UpdateException;
	
	/**
	 * 根据用户id查询用户数据
	 * @param uid 用户id
	 * @return 匹配的用户数据，如果没有匹配的数据，则返回null
	 */
	User getByUid(Integer uid) 
			throws UserNotFoundException;
	
}






