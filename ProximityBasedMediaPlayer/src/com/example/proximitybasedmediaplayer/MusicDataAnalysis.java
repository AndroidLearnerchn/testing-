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
//////////////////////////////////////////////////////////////////////////////////////////
//This Class is not in use																//
//////////////////////////////////////////////////////////////////////////////////////////
public class MusicDataAnalysis {
	private SQLiteDatabase database;
	  private StoreDataSqliteHelper dbHelper;
	  private String[] allColumns = { StoreDataSqliteHelper.COLUMN_ALBUM, StoreDataSqliteHelper.COLUMN_ARTIST,StoreDataSqliteHelper.COLUMN_GENRE,StoreDataSqliteHelper.COLUMN_DURATION};
	  private DAOMusicData datasource;
	   public MusicDataAnalysis(Context context) {
		   datasource = new DAOMusicData(context);
		    datasource.open();
		    
		  }
//	   public void recentPlayedSongList()
//	   {
//		    
//	   }
//	   public void uMayAlsoLikeSongList()
//	   {
//		   
//	   }
}
