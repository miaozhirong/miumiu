package com.miu.chuba.mapper;

import com.miu.chuba.entity.WeixinUser;
/**
 * 处理微信用户数据的持久层接口
 */
public interface WeixinUserMapper {
	/**
	 * 根据微信id来查询微信用户信息
	 * @param wxAccount 微信账号
	 * @return 微信用户信息
	 */
	WeixinUser findByWxId(Integer wxId);
	/**
	 * 根据微信名字来查询微信用户信息
	 * @param wxAccount 微信账号
	 * @return 微信用户信息
	 */
	WeixinUser findByWxName(String wxName);
	/**
	 * 根据微信账号来查询微信用户信息
	 * @param wxAccount 微信账号
	 * @return 微信用户信息
	 */
	WeixinUser findByWxAccount(String wxAccount);	
	/**
	 * 插入微信用户数据
	 * @param user 微信用户数据
	 * @return 受影响的行数
	 */
	Integer insert(WeixinUser weixinUser);
	/**
	 * 更新微信用户个人资料
	 * @param user 包含了微信用户资源的对象
	 * @return 受影响的行数
	 */
	Integer updateInfo(WeixinUser weixinUser);
}
