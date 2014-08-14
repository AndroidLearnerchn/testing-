/*****************************************************************
 * Copyright (c) 2013 by CDAC Chennai 
 * @File        MusicMetaData
 * @Created:    08.10.2013
 * @author:     Prasenjit
 * Last Change: 09.10.2013 by Prasenjit
 ******************************************************************/
package com.example.proximitybasedmediaplayer;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StoreDataSqliteHelper extends SQLiteOpenHelper{
	 public static final String TABLE_MUSIC_METADATA = "music_metadata";
	 public static final String COLUMN_ID = "Id";
	 public static final String COLUMN_TITLE = "Title";
	  public static final String COLUMN_ALBUM = "Album";
	  public static final String COLUMN_ARTIST = "Artist";
	  public static final String COLUMN_GENRE="Genre";
	  public static final String COLUMN_DURATION="Duration";
	  public static final String COLUMN_COUNTER = "Counter";
	  
	  
	  private static final String DATABASE_NAME = "contextaware.db";
	  private static final int DATABASE_VERSION = 1;
	  private static final String DATABASE_CREATE = "create table "
		      + TABLE_MUSIC_METADATA + "("  + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TITLE
		      + " text, " + COLUMN_ALBUM + " text, " + COLUMN_ARTIST + " text, "+ COLUMN_GENRE + " text,"+ COLUMN_DURATION + " text," + COLUMN_COUNTER + " integer  );";
	  // Database creation sql statement
	  public StoreDataSqliteHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
		  }

		  @Override
		  public void onCreate(SQLiteDatabase database) {
		    database.execSQL(DATABASE_CREATE);
		  }

		  @Override
		  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		    Log.w(StoreRecentSongSqliteHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSIC_METADATA);
		    onCreate(db);
		  }

}
