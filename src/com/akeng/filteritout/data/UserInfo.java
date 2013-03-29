package com.akeng.filteritout.data;

import android.graphics.drawable.Drawable;

public class UserInfo {
	public static final String ID="id";
	public static final String USERID="user_id";
	public static final String TOKEN="token";
	public static final String USERNAME="username";
	public static final String USERICON="user_icon";
	
	private String id;
	private String userId;
	private String token;
	private String userName;
	private Drawable userIcon;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Drawable getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(Drawable userIcon) {
		this.userIcon = userIcon;
	}
	
	
}
