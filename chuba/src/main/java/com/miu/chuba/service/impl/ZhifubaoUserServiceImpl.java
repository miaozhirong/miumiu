package com.miu.chuba.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miu.chuba.entity.ZhifubaoUser;
import com.miu.chuba.mapper.ZhifubaoUserMapper;
import com.miu.chuba.service.IZhifubaoUserService;
import com.miu.chuba.service.ex.InsertException;
import com.miu.chuba.service.ex.UpdateException;
import com.miu.chuba.service.ex.UserNotFoundException;
import com.miu.chuba.service.ex.UsernameDuplicateException;
import com.miu.chuba.util.ServiceUtil;

@Service
public class ZhifubaoUserServiceImpl implements IZhifubaoUserService{
	@Autowired 
	private ZhifubaoUserMapper zhifubaoUserMapper;
	@Override
	public ZhifubaoUser getByZfbId(Integer zfbId) throws UserNotFoundException {
		// 根据参数wxId查询用户数据
		ZhifubaoUser result=findByZfbId(zfbId);
		// 判断查询结果是否为null
		if (result == null) {
		// 是：抛出UserNotFoundException
			throw new UserNotFoundException("获取用户信息失败！尝试访问的用户数据不存在！");
		}
		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException("获取用户信息失败！尝试访问的用户数据不存在！");
		}
		return result;
	}
	
	@Override
	public ZhifubaoUser getByWxId(Integer wxId) throws UserNotFoundException {
		// 根据参数wxId查询用户数据
		ZhifubaoUser result=findByWxId(wxId);
		// 判断查询结果是否为null
		if (result == null) {
		// 是：抛出UserNotFoundException
			throw new UserNotFoundException("获取用户信息失败！尝试访问的用户数据不存在！");
		}
		// 判断查询结果中的isDelete是否为1
		if (result.getIsDelete() == 1) {
			// 是：抛出UserNotFoundException
			throw new UserNotFoundException("获取用户信息失败！尝试访问的用户数据不存在！");
		}
		return result;
	}

	@Override
	public void addNew(ZhifubaoUser zhifubaoUser, String username) throws UsernameDuplicateException, InsertException {
		zhifubaoUser.setIsDelete(0);
		ServiceUtil.packageDate(zhifubaoUser, username);
		insert(zhifubaoUser);
	}

	@Override
	public void changeInfo(ZhifubaoUser zhifubaoUser,String username) throws UserNotFoundException, UpdateException {
		// 从参数user中获取zfb_id
		Integer zfbId = zhifubaoUser.getZfbId();
		// 调用持久层对象的方法，根据zfbid查询用户数据
		ZhifubaoUser result=findByZfbId(zfbId);
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
		zhifubaoUser.setModifiedTime(new Date());
		zhifubaoUser.setModifiedUser(username);
		// 调用持久层对象执行修改，并获取返回值（即受影响的行数）
		update(zhifubaoUser);	
	}
	
	/****Mappers**********************************************************************/
	/**
	 * 根据支付宝id查找支付宝用户
	 * @param wxAccount
	 * @return
	 */
	private ZhifubaoUser findByZfbId(Integer zfbId) {
		return zhifubaoUserMapper.findByZfbId(zfbId);
	}
	/**
	 * 根据微信id查找支付宝用户
	 * @param wxAccount
	 * @return
	 */
	private ZhifubaoUser findByWxId(Integer wxId) {
		return zhifubaoUserMapper.findByWxId(wxId);
	}
	/**
	 * 插入数据
	 * @param weiboUser
	 */
	private void insert(ZhifubaoUser zhifubaoUser) {
		if(zhifubaoUserMapper.insert(zhifubaoUser)!=1) {
			throw new InsertException("添加账号时发生未知错误，请联系系统管理员！");
		}
	}
	/**
	 * 修改信息
	 * @param zhifubaoUser
	 */
	private void update(ZhifubaoUser zhifubaoUser) {
		// 判断返回值是否不为1
		if (zhifubaoUserMapper.updateInfo(zhifubaoUser) != 1) {
			// 是：抛出UpdateException
			throw new UpdateException(
				"修改个人资料错误！更新数据时发生未知错误！");
		}
	}
	
	
	
	/****Util**********************************************************************/	


	
	
}
