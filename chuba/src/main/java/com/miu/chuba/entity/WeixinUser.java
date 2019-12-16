package com.miu.chuba.entity;

public class WeixinUser extends BaseEntity {
	private Integer wxId; // 微信用户的id
	private String wxAccount;// 微信账号
	private String wxName;// 微信名称
	private Integer platform;// 推广平台类型，0-无，1-微博
	private Integer gender;// 性别，0-女，1-男
	private Integer isDelete;// 是否删除，0-未删除，1-已删除

	public Integer getWxId() {
		return wxId;
	}

	public void setWxId(Integer wxId) {
		this.wxId = wxId;
	}

	public String getWxAccount() {
		return wxAccount;
	}

	public void setWxAccount(String wxAccount) {
		this.wxAccount = wxAccount;
	}

	public String getWxName() {
		return wxName;
	}

	public void setWxName(String wxName) {
		this.wxName = wxName;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public String toString() {
		return "WeixinUser [wxId=" + wxId + ", wxAccount=" + wxAccount + ", wxName=" + wxName + ", platform=" + platform
				+ ", gender=" + gender + ", isDelete=" + isDelete + "]";
	}
	
}
