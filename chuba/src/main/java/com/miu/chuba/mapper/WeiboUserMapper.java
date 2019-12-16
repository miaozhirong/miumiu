package com.miu.chuba.mapper;

import com.miu.chuba.entity.WeiboUser;
/**
 * 处理微博用户数据的持久层接口
 */
public interface WeiboUserMapper {
	
	/**
	 * 根据微博id来查询微信用户信息
	 * @param wxAccount 微信账号
	 * @return 微信用户信息
	 */
	WeiboUser findByWbId(Integer wbId);
	/**
	 * 根据微博名字来查询微信用户信息
	 * @param wxAccount 微信账号
	 * @return 微信用户信息
	 */
	WeiboUser findByWbName(String wbName);
	/**
	 * 插入微博用户数据
	 * @param user 微信用户数据
	 * @return 受影响的行数
	 */
	Integer insert(WeiboUser weiboUser);
	/**
	 * 更新微博用户个人资料
	 * @param user 包含了微信用户资源的对象
	 * @return 受影响的行数
	 */
	Integer updateInfo(WeiboUser weiboUser);
}
