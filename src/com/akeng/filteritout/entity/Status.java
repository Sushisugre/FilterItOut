package com.akeng.filteritout.entity;

public class Status {
	
	public static final String ID="id";
	public static final String USERID="user_id";
	public static final String KEYS="keys";
	public static final String TYPE="type";
	public static final String TIME="time";
	public static final String TEXT="text";
	public static final String USERNAME="username";
	public static final String PICTURE="picture";
	public static final String THUMB="thumb";
	public static final String RETWEETED="rt_id";
	public static final int FAVOR = 0;
	public static final int DISLIKE = 1;
	public static final int NORMAL = 2;
	
    private long id;
    private String text;
    private String time;
    private String userId;
    private String username;
    private String userProfilePic;
    private Boolean haveImage;
    private String thumbnailPic;
    private String middlePic;
    private String source;
    private int repostsCount;
    private int commentsCount;
    private int attitudesCount;
    private Status retweetedStatus;
    private boolean isLike;
    private boolean isDeleted;
    private int type;
    
    public Status(){
    	this.haveImage=false;
    	this.thumbnailPic="";
    	this.middlePic="";
    	this.isLike=false;
    	this.isDeleted=false;
    	this.type = NORMAL;
    	this.retweetedStatus=null;
    }
    
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserProfilePic() {
		return userProfilePic;
	}
	public void setUserProfilePic(String userProfilePic) {
		this.userProfilePic = userProfilePic;
	}
	public Boolean getHaveImage() {
		return haveImage;
	}
	public void setHaveImage(Boolean haveImage) {
		this.haveImage = haveImage;
	}
	public String getThumbnailPic() {
		return thumbnailPic;
	}
	public void setThumbnailPic(String thumbnailPic) {
		this.thumbnailPic = thumbnailPic;
	}
	public String getMiddlePic() {
		return middlePic;
	}
	public void setMiddlePic(String middlePic) {
		this.middlePic = middlePic;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getRepostsCount() {
		return repostsCount;
	}
	public void setRepostsCount(int repostsCount) {
		this.repostsCount = repostsCount;
	}
	public int getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}
	public int getAttitudesCount() {
		return attitudesCount;
	}
	public void setAttitudesCount(int attitudesCount) {
		this.attitudesCount = attitudesCount;
	}
	public Status getRetweetedStatus() {
		return retweetedStatus;
	}
	public void setRetweetedStatus(Status retweetedStatus) {
		this.retweetedStatus = retweetedStatus;
	}

	public boolean isLike() {
		return isLike;
	}

	public void setLike(boolean isLike) {
		this.isLike = isLike;
		if(isLike)
			this.type=FAVOR;
		else
			this.type=NORMAL;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
		this.type = FAVOR;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}



        
}
