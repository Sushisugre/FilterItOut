package com.akeng.filteritout.entity;

public class Tag {
	
	public static final String ID="id";
	public static final String USERID="user_id";
	public static final String TAG_NAME="tag_name";
	public static final String TYPE="type";
	public static final String TIME="time";
	public static final int FAVOR = 0;
	public static final int DISLIKE = 1;
	
	public enum TagType {favor,dislike};
	
	private String id;
	private String userId;
	private String tagName;
	private int type;
	private long time;
	
	public Tag(){}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	

}
