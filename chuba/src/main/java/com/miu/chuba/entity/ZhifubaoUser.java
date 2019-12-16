package com.miu.chuba.entity;

public class ZhifubaoUser extends BaseEntity {
	private Integer zfbId; // 支付宝用户的id
	private Integer wxId; // 微信用户的id
	private String zfbAccount;// 支付宝账号
	private String zfbName;// 支付宝姓名
	private Integer isDelete;// 是否删除，0-未删除，1-已删除
	public Integer getZfbId() {
		return zfbId;
	}
	public void setZfbId(Integer zfbId) {
		this.zfbId = zfbId;
	}
	public Integer getWxId() {
		return wxId;
	}
	public void setWxId(Integer wxId) {
		this.wxId = wxId;
	}
	public String getZfbAccount() {
		return zfbAccount;
	}
	public void setZfbAccount(String zfbAccount) {
		this.zfbAccount = zfbAccount;
	}
	public String getZfbName() {
		return zfbName;
	}
	public void setZfbName(String zfbName) {
		this.zfbName = zfbName;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	@Override
	public String toString() {
		return "ZhifubaoUser [zfbId=" + zfbId + ", wxId=" + wxId + ", zfbAccount=" + zfbAccount + ", zfbName=" + zfbName
				+ ", isDelete=" + isDelete + "]";
	}
	
}
