package com.akeng.filteritout.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.entity.UserInfo;

public class SqliteHelper extends SQLiteOpenHelper {

	//save the table name of UserID,Access Token,Access Secret
	public static final String TB_USER="users";
	public static final String TB_TAGS="tags";
	public static final String TB_STATUS="status";

	
	public SqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+
				TB_USER+"("+
                UserInfo.ID+" integer primary key autoincrement,"+
                UserInfo.USERID+" varchar,"+
                UserInfo.TOKEN+" varchar,"+
                UserInfo.USERNAME+" varchar,"+
                UserInfo.USERICON+" blob"+
                ")"
                );
		
		db.execSQL("CREATE TABLE IF NOT EXISTS "+
				TB_TAGS+"("+
                Tag.ID+" integer primary key,"+
                Tag.USERID+" varchar,"+
                Tag.TAG_NAME+" varchar,"+
                Tag.TYPE+" integer,"+
                Tag.TIME+" integer,"+
                "foreign key ("+Tag.USERID+") "+"references "+TB_USER+" ("+UserInfo.USERID+"));"
                );
		
		db.execSQL("CREATE TABLE IF NOT EXISTS "+
				TB_STATUS+"("+
                Status.ID+" integer primary key,"+
                Status.USERID+" varchar,"+
                Status.KEYS+" varchar,"+
                Status.TYPE+" integer,"+
                Status.TIME+" varchar,"+
                Status.TEXT+" varchar,"+
                Status.USERNAME+" varchar,"+
                Status.RETWEETED+" integer,"+
                Status.PICTURE+" varchar,"+
                Status.THUMB+" varchar,"+
                "foreign key ("+Status.USERID+") "+"references "+TB_USER+" ("+UserInfo.USERID+"));"
                );
		
        Log.e("Database","onCreate");
	}

	 //update table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_USER);
        onCreate(db);
        Log.e("Database","onUpgrade");
    }

    public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn){
        try{
            db.execSQL("ALTER TABLE " +
            		TB_USER + " CHANGE " +
                    oldColumn + " "+ newColumn +
                    " " + typeColumn
            );
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
