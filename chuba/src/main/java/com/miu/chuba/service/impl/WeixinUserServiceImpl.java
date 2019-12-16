package com.miu.chuba.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miu.chuba.entity.WeiboUser;
import com.miu.chuba.entity.WeixinUser;
import com.miu.chuba.entity.ZhifubaoUser;
import com.miu.chuba.mapper.WeiboUserMapper;
import com.miu.chuba.mapper.WeixinUserMapper;
import com.miu.chuba.service.IWeixinUserService;
import com.miu.chuba.service.ex.InsertException;
import com.miu.chuba.service.ex.UpdateException;
import com.miu.chuba.service.ex.UserNotFoundException;
import com.miu.chuba.service.ex.UsernameDuplicateException;
import com.miu.chuba.util.ServiceUtil;

@Service
public class WeixinUserServiceImpl implements IWeixinUserService{
	@Autowired 
	private WeixinUserMapper weixUserMapper;
	@Autowired
	private WeiboUserServiceImpl WeiboUserServiceImpl;
	/**
	 * 根据微信名字来查询微信用户信息 
	 */
	@Override
	public WeixinUser getByWxName(String wxName) throws UserNotFoundException {
		// 根据参数wxName查询用户数据
		WeixinUser result=findByWxName(wxName);
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
	/**
	 * 根据微信账号来查询微信用户信息
	 */
	@Override
	public WeixinUser getByWxAccount(String wxAccount) throws UserNotFoundException {
		// 根据参数wxName查询用户数据
		WeixinUser result=findByWxAccount(wxAccount);
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
	/**
	 * 添加微信账号
	 */
	@Override
	public void addNew(WeixinUser weixinUser,String username) throws UsernameDuplicateException, InsertException {
		WeixinUser result=findByWxAccount(weixinUser.getWxAccount());
		if(result==null) {
			weixinUser.setIsDelete(0);
			ServiceUtil.packageDate(weixinUser,username);
			insert(weixinUser);
			//查找是否存在微博与微信名字相同的账号
			WeiboUser weibo=WeiboUserServiceImpl.getByWbName(weixinUser.getWxName());
			if(weibo!=null) {
				if(weibo.getWbId()==null) {
					weibo.setWxId(findByWxName(weixinUser.getWxName()).getWxId());
					weibo.setModifiedUser(username);
					WeiboUserServiceImpl.changeInfo(weibo,username);
				}
			}
		}else {
			throw new UsernameDuplicateException("该微信账号(" + weixinUser.getWxAccount() + ")已经存在！");
		}	
	}
	@Override
	public void changeInfo(WeixinUser weixinUser,String username) throws UserNotFoundException, UpdateException {
		// 从参数user中获取zfb_id
		Integer wxId = weixinUser.getWxId();
		// 调用持久层对象的方法，根据zfbid查询用户数据
		WeixinUser result=findByWxId(wxId);
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
		weixinUser.setModifiedTime(new Date());
		weixinUser.setModifiedUser(username);
		// 调用持久层对象执行修改，并获取返回值（即受影响的行数）
		update(weixinUser);	
	}
	/****Mappers**********************************************************************/
	/**
	 * 根据微信id查找用户
	 * @param wxId
	 * @return
	 */
	private WeixinUser findByWxId(Integer wxId) {
		return weixUserMapper.findByWxId(wxId);
	}
	/**
	 * 根据微信账号查找用户
	 * @param wxAccount
	 * @return
	 */
	private WeixinUser findByWxName(String wxName) {
		return weixUserMapper.findByWxName(wxName);
	}
	
	/**
	 * 根据微信账号查找用户
	 * @param wxAccount
	 * @return
	 */
	private WeixinUser findByWxAccount(String wxAccount) {
		return weixUserMapper.findByWxAccount(wxAccount);
	}
	
	/**
	 * 插入数据
	 * @param weixinUser
	 */
	private void insert(WeixinUser weixinUser) {
		if(weixUserMapper.insert(weixinUser)!=1) {
			throw new InsertException("添加微信账号时发生未知错误，请联系系统管理员！");
		}
	}
	
	/**
	 * 修改信息
	 * @param zhifubaoUser
	 */
	private void update(WeixinUser weixinUser) {
		// 判断返回值是否不为1
		if (weixUserMapper.updateInfo(weixinUser) != 1) {
			// 是：抛出UpdateException
			throw new UpdateException(
				"修改个人资料错误！更新数据时发生未知错误！");
		}
	}
	/****Util**********************************************************************/	
	

	
	
}
