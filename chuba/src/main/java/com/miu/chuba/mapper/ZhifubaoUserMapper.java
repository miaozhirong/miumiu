package com.miu.chuba.mapper;

import com.miu.chuba.entity.ZhifubaoUser;
/**
 * 处理支付宝用户数据的持久层接口
 */
public interface ZhifubaoUserMapper {
	/**
	 * 根据支付宝id来查询支付宝用户信息
	 * @param wxAccount 支付宝账号
	 * @return 支付宝用户信息
	 */
	ZhifubaoUser findByZfbId(Integer zfbId);
	/**
	 * 根据微信id来查询支付宝用户信息
	 * @param wxAccount 支付宝账号
	 * @return 支付宝用户信息
	 */
	ZhifubaoUser findByWxId(Integer wxId);
	/**
	 * 插入支付宝用户数据
	 * @param user 支付宝用户数据
	 * @return 受影响的行数
	 */
	Integer insert(ZhifubaoUser zhifubaoUser);
	/**
	 * 更新支付宝用户个人资料
	 * @param user 包含了支付宝用户资源的对象
	 * @return 受影响的行数
	 */
	Integer updateInfo(ZhifubaoUser zhifubaoUser);
}
