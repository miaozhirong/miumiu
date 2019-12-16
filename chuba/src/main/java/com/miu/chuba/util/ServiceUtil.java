package com.miu.chuba.util;

import java.util.Date;

import com.miu.chuba.entity.BaseEntity;

public class ServiceUtil{
	/**
	 * 分装日志
	 * @param user
	 * @param username
	 */
	public static void packageDate(BaseEntity user,String username) {
		Date now = new Date();
		user.setCreatedUser(username);
		user.setCreatedTime(now);
		user.setModifiedUser(username);
		user.setModifiedTime(now);
	}

}
