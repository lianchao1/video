package com.zhkj.video.model;

import java.util.Date;

public class Video {
	private Long id;
	private String oldName;
	private String newName;
	private String status;
	private Date createTime;
	private Date updateTime;
	private Long duringSecond;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	public String getNewName() {
		return newName;
	}
	public void setNewName(String newName) {
		this.newName = newName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Long getDuringSecond() {
		return duringSecond;
	}
	public void setDuringSecond(Long duringSecond) {
		this.duringSecond = duringSecond;
	}
	
}
