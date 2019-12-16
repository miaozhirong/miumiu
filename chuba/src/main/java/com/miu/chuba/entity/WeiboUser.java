package com.miu.chuba.entity;

public class WeiboUser extends BaseEntity {
	private Integer wbId; // 微博用户的id
	private Integer wxId; // 微信用户的id
	private String wbName;// 微博名称
	private String wbUrl;// 微博主页
	private Integer isDelete;// 是否删除，0-未删除，1-已删除
	
	public Integer getWbId() {
		return wbId;
	}
	public void setWbId(Integer wbId) {
		this.wbId = wbId;
	}
	public Integer getWxId() {
		return wxId;
	}
	public void setWxId(Integer wxId) {
		this.wxId = wxId;
	}
	public String getWbName() {
		return wbName;
	}
	public void setWbName(String wbName) {
		this.wbName = wbName;
	}
	public String getWbUrl() {
		return wbUrl;
	}
	public void setWbUrl(String wbUrl) {
		this.wbUrl = wbUrl;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	@Override
	public String toString() {
		return "Weibo [wbId=" + wbId + ", wxId=" + wxId + ", wbName=" + wbName + ", wbUrl=" + wbUrl + ", isDelete="
				+ isDelete + "]";
	}

	
}
