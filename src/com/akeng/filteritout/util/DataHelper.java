package com.akeng.filteritout.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.entity.UserInfo;

public class DataHelper {
	 private static String DB_NAME = "filteritout.db";
	    //data base version
	    private static int DB_VERSION = 2;
	    private SQLiteDatabase db;
	    private SqliteHelper dbHelper;
	    
	    public DataHelper(Context context){
	        dbHelper=new SqliteHelper(context,DB_NAME, null, DB_VERSION);
	        db= dbHelper.getWritableDatabase();
	    }
	    
	    public void Close()
	    {
	        db.close();
	        dbHelper.close();
	    }
	    
	    public List<UserInfo> GetUserList(Boolean isSimple)
	    {
	        List<UserInfo> userList = new ArrayList<UserInfo>();
	        Cursor cursor=db.query(SqliteHelper.TB_USER, null, null, null, null, null, UserInfo.ID+" DESC");
	        cursor.moveToFirst();
	        while(!cursor.isAfterLast()&& (cursor.getString(1)!=null)){
	            UserInfo user=new UserInfo();
	            user.setId(cursor.getString(0));
	            user.setUserId(cursor.getString(1));
	            user.setToken(cursor.getString(2));
	           // user.setTokenSecret(cursor.getString(3));
	            if(!isSimple){
	            user.setUserName(cursor.getString(3));
	            ByteArrayInputStream stream = new ByteArrayInputStream(cursor.getBlob(4)); 
	            Drawable icon= Drawable.createFromStream(stream, "image");
	            user.setUserIcon(icon);
	            }
	            userList.add(user);
	            cursor.moveToNext();
	        }
	        cursor.close();
	        return userList;
	    }
	    
	    public Boolean HaveUserInfo(String UserId)
	    {
	        Boolean b=false;
	        Cursor cursor=db.query(SqliteHelper.TB_USER, null, UserInfo.USERID + "=" + UserId, null, null, null,null);
	        b=cursor.moveToFirst();
	        Log.e("HaveUserInfo",b.toString());
	        cursor.close();
	        return b;
	    }
	    
	    //update image and name according to userId
	    public int UpdateUserInfo(String userName,Bitmap userIcon,String UserId)
	    {
	        ContentValues values = new ContentValues();
	        values.put(UserInfo.USERNAME, userName);
	        // BLOB type
	        final ByteArrayOutputStream os = new ByteArrayOutputStream();  
	        // compress Bitmap into PNG stored as 100% quality          
	        userIcon.compress(Bitmap.CompressFormat.PNG, 100, os);   
	        // construct SQLite Content object, can also use raw  
	        values.put(UserInfo.USERICON, os.toByteArray());
	        int id= db.update(SqliteHelper.TB_USER, values, UserInfo.USERID + "=" + UserId, null);
	        Log.e("UpdateUserInfo2",id+"");
	        return id;
	    }
	    
	    public int UpdateUserInfo(UserInfo user)
	    {
	        ContentValues values = new ContentValues();
	        values.put(UserInfo.USERID, user.getUserId());
	        values.put(UserInfo.TOKEN, user.getToken());
	       // values.put(UserInfo.TOKENSECRET, user.getTokenSecret());
	        int id= db.update(SqliteHelper.TB_USER, values, UserInfo.USERID + "=" + user.getUserId(), null);
	        Log.e("UpdateUserInfo",id+"");
	        return id;
	    }
	    
	    public Long SaveUserInfo(UserInfo user)
	    {
	        ContentValues values = new ContentValues();
	        values.put(UserInfo.USERID, user.getUserId());
	        values.put(UserInfo.TOKEN, user.getToken());
	        //values.put(UserInfo.TOKENSECRET, user.getTokenSecret());
	        Long uid = db.insert(SqliteHelper.TB_USER, UserInfo.ID, values);
	        Log.e("SaveUserInfo",uid+"");
	        return uid;
	    }
	    
	    public int DelUserInfo(String UserId){
	        int id=  db.delete(SqliteHelper.TB_USER, UserInfo.USERID +"="+UserId, null);
	        Log.e("DelUserInfo",id+"");
	        return id;
	    }
	    
	    public List<Tag> getUserTags(String userId,int type){
	    	List<Tag> tags=new ArrayList<Tag>();
	    	String where=Tag.USERID+"="+userId+" and "+ Tag.TYPE+"="+type;
	        Cursor cursor=db.query(SqliteHelper.TB_USER, null, where, null, null, null, UserInfo.ID+" DESC");
	        
	        if (!cursor.moveToFirst())
	        	return null;
	        
	        while(!cursor.isAfterLast()){
	        	Tag tag=new Tag();
	        	tag.setId(cursor.getString(0));
	        	tag.setTagName(cursor.getString(2));
	        	tag.setUserId(userId);
	        	tag.setType(type);
	        	tag.setTime(cursor.getLong(4));

	            cursor.moveToNext();
	        }
	        cursor.close();
	        
	        return tags;
	    }
	    
	    public boolean hasTag(String userId,String tagName,int type){
	        Boolean hasTag=false;
	    	String where=Tag.USERID+"="+userId+" and "+ Tag.TAG_NAME+"=\""+tagName+"\" and "+Tag.TYPE+"="+type;
	        Cursor cursor=db.query(SqliteHelper.TB_TAGS, null, where, null, null, null,null);
	        hasTag=cursor.moveToFirst();
	        Log.e("Has Tag","User id:"+userId+",Tag name:"+tagName+",Tag type:"+type+",exist:"+hasTag.toString());
	        cursor.close();
	        
	        return hasTag;
	    }
	    
	    public void addTag(String userId,String tagName,int type){
	    	
	    	if(hasTag(userId,tagName,type))
	    		return;
	    	
	    	 ContentValues values = new ContentValues();
	    	 values.put(Tag.TAG_NAME, tagName);
	    	 values.put(Tag.USERID, userId);
	    	 values.put(Tag.TYPE, type);
	    	 values.put(Tag.TIME, (new Date()).getTime());
	    	 
	    	 db.insert(SqliteHelper.TB_TAGS, null, values);
		     Log.e("Add Tag","User id:"+userId+",Tag name:"+tagName+",Tag type:"+type);

	    }
	    
	    public int removeTag(String userId,String tagName,int type){
	    	String where=Tag.USERID+"="+userId+" and "+ Tag.TAG_NAME+"=\""+tagName+"\" and "+Tag.TYPE+"="+type;
	        int num =  db.delete(SqliteHelper.TB_TAGS, where, null);
		     Log.e("Delete Tag","User id:"+userId+",Tag name:"+tagName+",Tag type:"+type);
	        return num;
	    }
}
