package com.miu.chuba.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miu.chuba.entity.WeiboUser;
import com.miu.chuba.entity.WeixinUser;
import com.miu.chuba.mapper.WeiboUserMapper;
import com.miu.chuba.service.IWeiboUserService;
import com.miu.chuba.service.ex.InsertException;
import com.miu.chuba.service.ex.UpdateException;
import com.miu.chuba.service.ex.UserNotFoundException;
import com.miu.chuba.service.ex.UsernameDuplicateException;
import com.miu.chuba.util.ServiceUtil;

@Service
public class WeiboUserServiceImpl implements IWeiboUserService{
	@Autowired 
	private WeiboUserMapper weiboUserMapper;
	@Autowired
	private WeixinUserServiceImpl weixinUserServiceImpl;
	/**
	 * 根据微博名字来查询微信用户信息 
	 */
	@Override
	public WeiboUser getByWbName(String wbName) throws UserNotFoundException {
		// 根据参数wxName查询用户数据
		WeiboUser result=findByWbName(wbName);
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
	 * 添加微博账号
	 */
	@Override
	public void addNew(WeiboUser weiboUser,String username) throws UsernameDuplicateException, InsertException {
		WeiboUser result=findByWbName(weiboUser.getWbName());
		if(result==null) {
			weiboUser.setIsDelete(0);
			ServiceUtil.packageDate(weiboUser,username);
			//如果存在与微博名相同的微信账号，就获取其微信id
			WeixinUser weixin=weixinUserServiceImpl.getByWxName(weiboUser.getWbName());
			if(weixin!=null) {
				weiboUser.setWxId(weixin.getWxId());
			}
			insert(weiboUser);
		}else {
			throw new UsernameDuplicateException("该微博账号(" + weiboUser.getWbName() + ")已经存在！");
		}
		
	}
	@Override
	public void changeInfo(WeiboUser weiboUser,String username) throws UserNotFoundException, UpdateException {
		Integer wbId = weiboUser.getWbId();
		WeiboUser result=findByWbId(wbId);
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
		weiboUser.setModifiedTime(new Date());
		weiboUser.setModifiedUser(username);
		// 调用持久层对象执行修改，并获取返回值（即受影响的行数）
		update(weiboUser);	
	}
	/****Mappers**********************************************************************/
	/**
	 * 根据微博id查找用户
	 * @param wbId
	 * @return
	 */
	private WeiboUser findByWbId(Integer wbId) {
		return weiboUserMapper.findByWbId(wbId);
	}
	/**
	 * 根据微博名字查找用户
	 * @param wxAccount
	 * @return
	 */
	private WeiboUser findByWbName(String wbName) {
		return weiboUserMapper.findByWbName(wbName);
	}
	/**
	 * 插入数据
	 * @param weiboUser
	 */
	private void insert(WeiboUser weiboUser) {
		if(weiboUserMapper.insert(weiboUser)!=1) {
			throw new InsertException("添加账号时发生未知错误，请联系系统管理员！");
		}
	}
	/**
	 * 修改信息
	 * @param zhifubaoUser
	 */
	private void update(WeiboUser weiboUser) {
		// 判断返回值是否不为1
		if (weiboUserMapper.updateInfo(weiboUser) != 1) {
			// 是：抛出UpdateException
			throw new UpdateException(
				"修改个人资料错误！更新数据时发生未知错误！");
		}
	}
	/****Util**********************************************************************/	


	
	
}
