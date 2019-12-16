package com.miu.chuba.service.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.miu.chuba.entity.User;
import com.miu.chuba.mapper.UserMapper;
import com.miu.chuba.service.IUserService;
import com.miu.chuba.service.ex.InsertException;
import com.miu.chuba.service.ex.PasswordNotMatchException;
import com.miu.chuba.service.ex.UpdateException;
import com.miu.chuba.service.ex.UserNotFoundException;
import com.miu.chuba.service.ex.UsernameDuplicateException;
import com.miu.chuba.util.ServiceUtil;

@Service
public class UserServiceImpl 
	implements IUserService {

	@Autowired 
	private UserMapper userMapper;
	
	@Override
	public void reg(User user) throws UsernameDuplicateException, InsertException {
		// 根据尝试注册的用户名查询数据
		User result = findByUsername(user);
		// 判断查询结果是否为null
		if (result == null) {
			// 是：允许注册
			user.setIsDelete(0);
			// 封装日志
			ServiceUtil.packageDate(user, user.getUsername());
			// 密码加密
			// - 生成随机盐
			String salt = UUID.randomUUID().toString().toUpperCase();
			// - 基于原密码和盐值执行加密
			String md5Password = getMd5Password(user.getPassword(), salt);
			// - 将盐和加密结果封装到user对象中
			user.setSalt(salt);
			user.setPassword(md5Password);
			// 执行注册
			insert(user);
			
		} else {
			// 否：不允许注册，抛出异常
			throw new UsernameDuplicateException(
				"尝试注册的用户名(" + user.getUsername() + ")已经被占用！");
		}
	}
	

	@Override
	public User login(String username, String password) throws UserNotFoundException, PasswordNotMatchException {
		// 根据参数username查询匹配的用户信息
		User result = findByUsername(username);
		
	    // 判断查询结果是否为null
		if (result == null) {
			// 是：用户名对应的数据不存在，抛出UserNotFoundException
			throw new UserNotFoundException(
				"登录失败，用户名不存在！");
		}

	    // 判断isDelete值是否为1
		if (result.getIsDelete() == 1) {
			// 是：用户标记为“已删除”，抛出UserNotFoundException
			throw new UserNotFoundException(
				"登录失败，用户名不存在！");
		}

	    // 基于盐值和参数password执行加密
		String salt = result.getSalt();
		String md5Password = getMd5Password(password, salt);
		
	    // 判断加密后的密码与查询结果中的密码是否不匹配
		if (!result.getPassword().equals(md5Password)) {
			// 是：密码不匹配，抛出PasswordNotMatchException
			throw new PasswordNotMatchException(
				"登录失败！密码错误！");
		}

	    // 将查询结果中的salt, password, isDelete设置为null
		result.setSalt(null);
		result.setPassword(null);
		result.setIsDelete(null);
	    // 返回查询结果
		return result;
	}

	@Override
	public void changeInfo(User user) throws UserNotFoundException, UpdateException {
		// 从参数user中获取uid
		Integer uid = user.getUid();
		// 调用持久层对象的方法，根据uid查询用户数据
		User result = userMapper.findByUid(uid);

		// 判断查询结果是否为null
		if (result == null) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改个人资料错误！尝试访问的用户数据不存在！");
		}

		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改个人资料错误！尝试访问的用户数据不存在！");
		}

		// 创建当前时间对象，封装到user中
		user.setModifiedTime(new Date());
		// TODO 确保modifiedUser属性是有值的

		// 调用持久层对象执行修改，并获取返回值（即受影响的行数）
		Integer rows = userMapper.updateInfo(user);
		// 判断返回值是否不为1
		if (rows != 1) {
			// 是：抛出UpdateException
			throw new UpdateException(
				"修改个人资料错误！更新数据时发生未知错误！");
		}
	}
	
	@Override
	public void changePassword(Integer uid, String oldPassword, String newPassword, String username)
			throws UserNotFoundException, PasswordNotMatchException, UpdateException {
		// 根据参数uid查询用户数据
		User result = userMapper.findByUid(uid);

		// 判断查询结果是否为null
		if (result == null) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改密码错误！尝试访问的用户数据不存在！");
		}

		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改密码错误！尝试访问的用户数据不存在！");
		}

		// 取出查询结果中的盐值
		String salt = result.getSalt();
		// 基于参数oldPassword和盐值执行加密
		String oldMd5Password = getMd5Password(oldPassword, salt);
		// 判断加密结果与查询结果中的密码是否不匹配
		if (!result.getPassword().equals(oldMd5Password)) {
			// 是：抛出PasswordNotMatchException
			throw new PasswordNotMatchException(
				"修改密码错误！原密码错误！");
		}

		// 基于参数newPassword和盐值执行加密
		String newMd5Password = getMd5Password(newPassword, salt);
		// 创建当前时间对象，作为最后修改时间
		Date now = new Date();
		// 调用持久层执行更新密码（新密码是以上加密的结果），并获取返回值
		Integer rows = userMapper.updatePassword(uid, newMd5Password, username, now);
		// 判断返回值是否不为1
		if (rows != 1) {
			// 是：抛出UpdateException
			throw new UpdateException(
				"修改密码错误！更新数据时发生未知错误！");
		}
	}
	
	@Override
	public void changeAvatar(Integer uid, String avatar, String username)
			throws UserNotFoundException, UpdateException {
		// 根据参数uid查询用户数据
		User result = userMapper.findByUid(uid);

		// 判断查询结果是否为null
		if (result == null) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改头像错误！尝试访问的用户数据不存在！");
		}

		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"修改头像错误！尝试访问的用户数据不存在！");
		}
		
		// 创建当前时间对象
		Date now = new Date();
		// 调用持久层的方法执行更新头像，并获取返回值
		Integer rows = userMapper.updateAvatar(uid, avatar, username, now);
		// 判断返回值是否不为1，是，抛出异常
		if (rows != 1) {
			// 是：抛出UpdateException
			throw new UpdateException(
				"修改头像错误！更新数据时发生未知错误！");
		}
	}
	
	@Override
	public User getByUid(Integer uid) throws UserNotFoundException
	{
		// 根据参数uid查询用户数据
		User result = userMapper.findByUid(uid);

		// 判断查询结果是否为null
		if (result == null) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"获取用户信息失败！尝试访问的用户数据不存在！");
		}

		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException(
				"获取用户信息失败！尝试访问的用户数据不存在！");
		}
		
		// 将查询结果中的password/salt/isDelete设置为null
		result.setPassword(null);
		result.setSalt(null);
		result.setIsDelete(null);
		
		// 返回查询结果
		return result;
	}
	/******************************************************************/
	/**
	 * 插入数据
	 * @param user
	 * @return
	 */
	private void insert(User user) {
		if (userMapper.insert(user) != 1) {
			throw new InsertException(
				"发生未知错误，请联系系统管理员！");
		}
	}
	/**
	 * 根据用户名查用户信息
	 * @param user
	 * @return
	 */
	private User findByUsername(User user) {
		return userMapper.findByUsername(user.getUsername());
	}
	/**
	 * 根据用户名查用户信息
	 * @param user
	 * @return
	 */
	private User findByUsername(String username) {
		return userMapper.findByUsername(username);
	}
	
/*******************************************************************/
	/**
	 * 将密码执行加密
	 * @param password 原密码
	 * @param salt 盐值
	 * @return 加密后的结果
	 */
	private String getMd5Password(String password, String salt) {
		// 拼接原密码与盐值
		String str = salt + password + salt;
		// 循环加密5次
		for (int i = 0; i < 5; i++) {
			str = DigestUtils.md5DigestAsHex(
					str.getBytes()).toUpperCase();
		}
		// 返回结果
		return str;
	}

}







