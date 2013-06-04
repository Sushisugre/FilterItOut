package com.akeng.filteritout.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.entity.UserInfo;

public class DataHelper {
	private static String DB_NAME = "filteritout.db";
	// data base version
	private static int DB_VERSION = 2;
	private SQLiteDatabase db;
	private SqliteHelper dbHelper;

	public DataHelper(Context context) {
		dbHelper = new SqliteHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		db.close();
		dbHelper.close();
	}

	public List<UserInfo> getUserList(Boolean isSimple) {
		List<UserInfo> userList = new ArrayList<UserInfo>();
		Cursor cursor = db.query(SqliteHelper.TB_USER, null, null, null, null,
				null, UserInfo.ID + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
			UserInfo user = new UserInfo();
			user.setId(cursor.getString(0));
			user.setUserId(cursor.getString(1));
			user.setToken(cursor.getString(2));
			// user.setTokenSecret(cursor.getString(3));
			if (!isSimple) {
				user.setUserName(cursor.getString(3));
				ByteArrayInputStream stream = new ByteArrayInputStream(
						cursor.getBlob(4));
				Drawable icon = Drawable.createFromStream(stream, "image");
				user.setUserIcon(icon);
			}
			userList.add(user);
			cursor.moveToNext();
		}
		cursor.close();
		return userList;
	}

	public Boolean hasUserInfo(String UserId) {
		Boolean b = false;
		Cursor cursor = db.query(SqliteHelper.TB_USER, null, UserInfo.USERID
				+ "=" + UserId, null, null, null, null);
		b = cursor.moveToFirst();
		Log.e("HaveUserInfo", b.toString());
		cursor.close();
		return b;
	}

	// update image and name according to userId
	public int updateUserInfo(String userName, Bitmap userIcon, String UserId) {
		ContentValues values = new ContentValues();
		values.put(UserInfo.USERNAME, userName);
		// BLOB type
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// compress Bitmap into PNG stored as 100% quality
		userIcon.compress(Bitmap.CompressFormat.PNG, 100, os);
		// construct SQLite Content object, can also use raw
		values.put(UserInfo.USERICON, os.toByteArray());
		int id = db.update(SqliteHelper.TB_USER, values, UserInfo.USERID + "="
				+ UserId, null);
		Log.e("UpdateUserInfo2", id + "");
		return id;
	}

	public int updateUserInfo(UserInfo user) {
		ContentValues values = new ContentValues();
		values.put(UserInfo.USERID, user.getUserId());
		values.put(UserInfo.TOKEN, user.getToken());
		// values.put(UserInfo.TOKENSECRET, user.getTokenSecret());
		int id = db.update(SqliteHelper.TB_USER, values, UserInfo.USERID + "="
				+ user.getUserId(), null);
		Log.e("UpdateUserInfo", id + "");
		return id;
	}

	public Long addUserInfo(UserInfo user) {
		ContentValues values = new ContentValues();
		values.put(UserInfo.USERID, user.getUserId());
		values.put(UserInfo.TOKEN, user.getToken());
		// values.put(UserInfo.TOKENSECRET, user.getTokenSecret());
		Long uid = db.insert(SqliteHelper.TB_USER, UserInfo.ID, values);
		Log.e("SaveUserInfo", uid + "");
		return uid;
	}

	public int deleteUserInfo(String UserId) {
		int id = db.delete(SqliteHelper.TB_USER,
				UserInfo.USERID + "=" + UserId, null);
		Log.e("DelUserInfo", id + "");
		return id;
	}

	public List<Tag> getUserTags(String userId, int type) {
		List<Tag> tags = new ArrayList<Tag>();
		String where = Tag.USERID + "=" + userId + " and " + Tag.TYPE + "="
				+ type;
		Cursor cursor = db.query(SqliteHelper.TB_TAGS, null, where, null, null,
				null, Tag.TIME + " DESC");

		if (!cursor.moveToFirst())
			return null;

		while (!cursor.isAfterLast()) {
			Tag tag = new Tag();
			tag.setId(cursor.getString(0));
			tag.setTagName(cursor.getString(2));
			tag.setUserId(userId);
			tag.setType(type);
			tag.setTime(cursor.getLong(4));
			tag.setSelected(true);

			tags.add(tag);

			cursor.moveToNext();
		}
		cursor.close();

		return tags;
	}

	public List<Map<String, Integer>> getModels(int type) {

		String where = Status.TYPE + "=" + type;
		Cursor cursor = db.query(SqliteHelper.TB_STATUS, null, where, null,
				null, null, null);

		List<Map<String, Integer>> modelList = new ArrayList<Map<String, Integer>>();

		if (!cursor.moveToFirst())
			return null;

		while (!cursor.isAfterLast()) {

			Map<String, Integer> keyMap = new HashMap<String, Integer>();

			String keyString = cursor.getString(2);
			if(keyString==null){
				cursor.moveToNext();
				continue;
			}
			
				
			
			String[] TF = keyString.split(",");
			for (String term : TF) {
				String[] pair = term.split(":");
				keyMap.put(pair[0], Integer.parseInt(pair[1]));
			}
			modelList.add(keyMap);

			cursor.moveToNext();
		}

		cursor.close();

		return modelList;
	}

	public boolean hasStatus(long id) {
		Boolean hasStatus = false;
		String where = Status.ID + "=" + id;
		Cursor cursor = db.query(SqliteHelper.TB_STATUS, null, where, null,
				null, null, null);
		hasStatus = cursor.moveToFirst();
		cursor.close();

		return hasStatus;
	}
	
	public Status getStatus(long id){
		Status status=new Status();
		String where = Status.ID + "=" + id;
		Cursor cursor = db.query(SqliteHelper.TB_STATUS, null, where, null,
				null, null, null);
		if(cursor.moveToFirst()){
			status.setId(cursor.getLong(0));
			status.setUserId(cursor.getString(1));
			status.setType(cursor.getInt(3));
			status.setSection(cursor.getInt(4));
			status.setTime(cursor.getString(5));
			status.setText(cursor.getString(6));
			status.setUsername(cursor.getString(7));
			if (!cursor.isNull(8)) {
				Status rtStatus = new Status();
				rtStatus.setId(cursor.getLong(8));
				status.setRetweetedStatus(rtStatus);
			}
			status.setMiddlePic(cursor.getString(9));
			status.setThumbnailPic(cursor.getString(10));
		}
		else{
			cursor.close();
			return null;
		}
		
		cursor.close();
		return status;
	}

	public void updateStatus(long id, int type,String keys) {
		String where = Status.ID + "=" + id;
		ContentValues values = new ContentValues();
		values.put(Status.TYPE, type);
		if(keys!=null)
			values.put(Status.KEYS, keys);
		db.update(SqliteHelper.TB_STATUS, values, where, null);
	}

	public void addStatus(String userId, Status status, String keys) {

		if (hasStatus(status.getId())) {
			updateStatus(status.getId(), status.getType(),keys);
			Log.e("Update Status", "User id:" + userId + ",Status text:"
					+ status.getText() + ",Status type:" + status.getType());
			return;
		}

		ContentValues values = new ContentValues();
		values.put(Status.ID, status.getId());
		values.put(Status.KEYS, keys);
		values.put(Status.PICTURE, status.getMiddlePic());
		values.put(Status.TEXT, status.getText());
		values.put(Status.THUMB, status.getThumbnailPic());
		values.put(Status.TIME, status.getTime());
		values.put(Status.TYPE, status.getType());
		values.put(Status.SECTION, status.getSection());
		values.put(Status.USERID, status.getUserId());
		values.put(Status.USERNAME, status.getUsername());

		if (status.getRetweetedStatus() != null) {
			values.put(Status.RETWEETED, status.getRetweetedStatus().getId());
			addStatus(userId, status.getRetweetedStatus(), null);
		}

		db.insert(SqliteHelper.TB_STATUS, null, values);

		Log.e("Add Status",
				"User id:" + userId + ",Status text:" + status.getText()
						+ ",Status type:" + status.getType());
	}
	
	public List<Status> readCachedStatus(int section) {
		String where = Status.SECTION + "=" + section+" and "+Status.TYPE+"="+Status.CACHED;
		Cursor cursor = db.query(SqliteHelper.TB_STATUS, null, where, null,
				null, null, null);

		List<Status> cachedStatuses = new ArrayList<Status>();

		if (!cursor.moveToFirst()){
			System.out.println("Status not found");
			cursor.close();
			return null;
			}

		while (!cursor.isAfterLast()) {
			Status status = new Status();
			status.setId(cursor.getLong(0));
			status.setUserId(cursor.getString(1));
			status.setType(cursor.getInt(3));
			status.setSection(cursor.getInt(4));
			status.setTime(cursor.getString(5));
			status.setText(cursor.getString(6));
			status.setUsername(cursor.getString(7));
			if (!cursor.isNull(8)) {
				Status rtStatus = getStatus(cursor.getLong(8));
				status.setRetweetedStatus(rtStatus);
			}
			status.setMiddlePic(cursor.getString(9));
			status.setThumbnailPic(cursor.getString(10));

			cachedStatuses.add(status);

			cursor.moveToNext();
		}
		cursor.close();

		return cachedStatuses;
	}
	
	public int clearCachedStatus(int section) {
		String where =Status.SECTION + "=" + section +
				" and ("+Status.TYPE + "=" + Status.RETWEET + 
				" or "+Status.TYPE + "=" + Status.CACHED+")" ;
		int num = db.delete(SqliteHelper.TB_STATUS, where, null);
		Log.e("Clear cached statuses", num + " items deleted");
		return num;
	}

	public boolean hasTag(String userId, String tagName, int type) {
		Boolean hasTag = false;
		String where = Tag.USERID + "=" + userId + " and " + Tag.TAG_NAME
				+ "=\"" + tagName + "\" and " + Tag.TYPE + "=" + type;
		Cursor cursor = db.query(SqliteHelper.TB_TAGS, null, where, null, null,
				null, null);
		hasTag = cursor.moveToFirst();
		Log.e("Has Tag", "User id:" + userId + ",Tag name:" + tagName
				+ ",Tag type:" + type + ",exist:" + hasTag.toString());
		cursor.close();

		return hasTag;
	}

	public void addTag(String userId, String tagName, int type) {

		if (hasTag(userId, tagName, type))
			return;

		ContentValues values = new ContentValues();
		values.put(Tag.TAG_NAME, tagName);
		values.put(Tag.USERID, userId);
		values.put(Tag.TYPE, type);
		values.put(Tag.TIME, (new Date()).getTime());

		db.insert(SqliteHelper.TB_TAGS, null, values);
		Log.e("Add Tag", "User id:" + userId + ",Tag name:" + tagName
				+ ",Tag type:" + type);

	}

	public int removeTag(String userId, String tagName, int type) {
		String where = Tag.USERID + "=" + userId + " and " + Tag.TAG_NAME
				+ "=\"" + tagName + "\" and " + Tag.TYPE + "=" + type;
		int num = db.delete(SqliteHelper.TB_TAGS, where, null);
		Log.e("Delete Tag", "User id:" + userId + ",Tag name:" + tagName
				+ ",Tag type:" + type);
		return num;
	}

	public int clearAllTags() {
		int num = db.delete(SqliteHelper.TB_TAGS, null, null);
		Log.e("Clear All Tags", num + " items deleted");
		return num;
	}




}
