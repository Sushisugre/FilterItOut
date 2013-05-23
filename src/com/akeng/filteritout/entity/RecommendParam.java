package com.akeng.filteritout.entity;

import java.util.List;

public class RecommendParam {
	private List<Status> status;
	private int section;
	public List<Status> getStatus() {
		return status;
	}
	public void setStatus(List<Status> status) {
		this.status = status;
	}
	public int getSection() {
		return section;
	}
	public void setSection(int section) {
		this.section = section;
	}
}
