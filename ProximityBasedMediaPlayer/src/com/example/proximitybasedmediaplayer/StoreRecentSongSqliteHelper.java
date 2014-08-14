/*****************************************************************
 * Copyright (c) 2013 by CDAC Chennai 
 * @File        MusicMetaData
 * @Created:    13.10.2013
 * @author:     Prasenjit
 * Last Change: 13.10.2013 by Prasenjit
 ******************************************************************/
package com.example.proximitybasedmediaplayer;

//////////////////////////////////////////////////////////////////////////////////////////
//This Class is not in use																//
//////////////////////////////////////////////////////////////////////////////////////////

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StoreRecentSongSqliteHelper extends SQLiteOpenHelper{
	 public static final String TABLE_MUSIC_METADATA = "recent_music_metadata";
	  public static final String COLUMN_ALBUM = "Album";
	  public static final String COLUMN_ARTIST = "Artist";
	  public static final String COLUMN_ID="id";
	  
	  
	  private static final String DATABASE_NAME = "contextaware1.db";
	  private static final int DATABASE_VERSION = 1;
	  private static final String DATABASE_CREATE = "create table "
		      + TABLE_MUSIC_METADATA + "(" + COLUMN_ALBUM
		      + " text, " + COLUMN_ARTIST
		      + " text, "+ COLUMN_ID + " integer  primary key autoincrement );";
	  // Database creation sql statement
	  public StoreRecentSongSqliteHelper(Context context) {
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
