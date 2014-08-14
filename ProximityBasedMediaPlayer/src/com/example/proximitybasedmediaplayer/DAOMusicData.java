/*****************************************************************
 * Copyright (c) 2013 by CDAC Chennai 
 * @File        MusicMetaData
 * @Created:    08.10.2013
 * @author:     Prasenjit
 * Last Change: 09.10.2013 by Prasenjit
 ******************************************************************/
package com.example.proximitybasedmediaplayer;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class DAOMusicData {
	private SQLiteDatabase database,dbGetArtistAlbum,dbIncreaseCount;
	  private StoreDataSqliteHelper dbHelper;
	  private String song_duration, Loc_Artist;
	  private String[] allColumns = { StoreDataSqliteHelper.COLUMN_ID,StoreDataSqliteHelper.COLUMN_TITLE,StoreDataSqliteHelper.COLUMN_ALBUM, StoreDataSqliteHelper.COLUMN_ARTIST,StoreDataSqliteHelper.COLUMN_GENRE,StoreDataSqliteHelper.COLUMN_DURATION,StoreDataSqliteHelper.COLUMN_COUNTER};
	  private ArrayList<HashMap<String, String>> recentSongsList = new ArrayList<HashMap<String, String>>();
	  public DAOMusicData(Context context) {
		    dbHelper = new StoreDataSqliteHelper(context);
		  }

		  public void open() throws SQLException {
		    database = dbHelper.getWritableDatabase();
		  }
		  public void readOnly() throws SQLException {
			    database = dbHelper.getReadableDatabase();
			  }

		  public void close() {
		    dbHelper.close();
		  }
		  public MusicMetadata createComment(String title,String album,String artist,String genre, String duration,int counter) {
			    MusicMetadata newComment = null; 
			   //Column_id is primary key with autoincrement, Column Counter is ther to count the number of times user clicked the song in the listview
			    song_duration= duration;
			    Loc_Artist = artist;
			    Log.d("Debug","inside createComment DAOmusic");
			    ContentValues values = new ContentValues();
			    int counterval=0;
			    values.put(StoreDataSqliteHelper.COLUMN_TITLE, title);
			    values.put(StoreDataSqliteHelper.COLUMN_ALBUM, album);
			    values.put(StoreDataSqliteHelper.COLUMN_ARTIST, artist);
			    values.put(StoreDataSqliteHelper.COLUMN_GENRE,genre);
			    values.put(StoreDataSqliteHelper.COLUMN_DURATION,duration);
			    values.put(StoreDataSqliteHelper.COLUMN_COUNTER,counter);
			   // database.insertWithOnConflict(StoreDataSqliteHelper.TABLE_MUSIC_METADATA, null, null ,1 );
			    Cursor cursor=null;
			    // Prasen 18.10.13
			    
			    ////---------------------------To get the duplicate columns--------------------------
			    try{
			    // Sql statement to check if the column already in the table
		        String query = "select Id from music_metadata where Duration = ? " +  " and  Artist = ? ";
		       
		        // This is the query to the cursor
		        cursor = database.rawQuery(query, new String[] {song_duration, Loc_Artist});
		       
		        // To get the count if any column has the same Song duration and Artist Name 
		         counterval = cursor.getCount();
			    
			    ////----------------------------------Logic End----------------------------------------------
			    //Integer count = //getduplicaterowCount();
			    Log.d("DEbug","count = " + counterval);
			    
			    //Till here 18.10.13
			    }
			    catch(Exception e)
			    {
			    	e.printStackTrace();
			    }
			    
			    try{
			    if(counterval == 0) // To check if value already in table
			    {	
			    	Log.d("DEbug ","inside count, val = " + counterval);
			    	
			    long insertId = database.insert(StoreDataSqliteHelper.TABLE_MUSIC_METADATA, null,
			        values);
			    
			    Log.d("DEbug ","after insertid, inside counter " );
			  //  Cursor cursor = database.query(StoreDataSqliteHelper.TABLE_MUSIC_METADATA, new String[] {StoreDataSqliteHelper.COLUMN_ALBUM,StoreDataSqliteHelper.COLUMN_ARTIST }, 

			    	//	  null, null, null, null, null);
			    
			    cursor = database.query(StoreDataSqliteHelper.TABLE_MUSIC_METADATA,
			        allColumns, null + " = " + insertId, null,null, null, null); //StoreDataSqliteHelper.COLUMN_ARTIST
			    cursor.moveToFirst();
			    
			 
			    newComment = cursorToComment(cursor);
			   // cursor.close();
			    }
			    else
			    {
			    	System.out.println("debug inside eklse ");
			    	Log.d("DEBUG","else part, insert statement, DAOMusicData class");
			    	//cursor.close();
			    }
			   }
			    catch(Exception e){
			    	Log.d("ERROR","Error in count");
			    	e.printStackTrace();
			    	}

			    cursor.close();
			    return newComment;
			  }
		 
		  	// Method to increase the Counter value on click in list song
		  // 24.10.13 
		  public MusicMetadata createComment(String songTitle)
		  {
			  MusicMetadata recentCount = null;
			  Cursor cursorRecent;
			  try
			  {
				  Log.d("Debug","inside createComment songTitle, DAOMusic");
				  String query = "update music_metadata set Counter = (Counter + 1)  where  Title = ? ";
			       
			        // This is the query to the cursor
				  Log.d("Debug title","here1");
				  dbIncreaseCount =  dbHelper.getWritableDatabase();
				  
				  Log.d("Debug title","here2");
				 
				  if(dbIncreaseCount.isOpen() == true)
				 {
					 Log.d("Debug title","here3"); 
			       cursorRecent = dbIncreaseCount.rawQuery(query, new String[] {songTitle});
			    //   dbIncreaseCount.update(table, values, whereClause, whereArgs);
			        Log.d("Debug title","here4");
			        Integer counterval = cursorRecent.getCount();
			        Log.d("Debug title","Value = "  + counterval);
			        
					cursorRecent.moveToFirst();
					 Log.d("Debug title","here5");
					while (!cursorRecent.isAfterLast()) {
						Log.d("Debug title","here6");  
						recentCount = cursorToCommentIncreaseCount(cursorRecent);
						Log.d("Debug title","here7");
					    cursorRecent.moveToNext();
					    }
				 }
				  else {
					Log.d("DEBUG Title ", "DATabase con not open for writing");  
				  }      
			  }
			  
			  catch(Exception e)
			  {
				  Log.d("Here inside Dao","ERROr");
				  e.printStackTrace();
			  }
			  dbIncreaseCount.close();
			  return recentCount;
		  }
		  // Delete the comment, Not in use
			  public void deleteComment(MusicMetadata comment) {
			    long id = comment.getId();
			    System.out.println("Comment deleted with id: " + id);
			    database.delete(StoreDataSqliteHelper.TABLE_MUSIC_METADATA, StoreDataSqliteHelper.COLUMN_ALBUM
			        + " = " + id, null);
			  }
			  // To list all comments, Not in use
			  public List<MusicMetadata> getAllComments() {
			    List<MusicMetadata> comments = new ArrayList<MusicMetadata>();

			    Cursor cursor = database.query(StoreDataSqliteHelper.TABLE_MUSIC_METADATA,
			        allColumns, null, null, null, null, null);

			    cursor.moveToFirst();
			    while (!cursor.isAfterLast()) {
			      MusicMetadata comment = cursorToComment(cursor);
			      comments.add(comment);
			      cursor.moveToNext();
			    }
			    // Make sure to close the cursor
			    cursor.close();
			    return comments;
			  }
			  // Function to create a music_metadata object to store in the table
			  private MusicMetadata cursorToComment(Cursor cursor) {
			   
				  MusicMetadata comment = new MusicMetadata();
				  try{
				  comment.setTitle(cursor.getString(0));
			    comment.setAlbum(cursor.getString(1));
			    comment.setArtist(cursor.getString(2));
			    comment.setGenre(cursor.getString(3));
			    comment.setDuration(cursor.getString(4));
			    comment.setCounter(cursor.getInt(5));
			   }
			   catch(Exception e){e.printStackTrace();}
			    return comment;
			  }
			  // Prasen 24.10.13
			  // Function to create comment for recently played song
			  private MusicMetadata cursorToCommentIncreaseCount(Cursor cursor) {
				    MusicMetadata comment = new MusicMetadata();
				  try{
				    comment.setTitle(cursor.getString(0));
				  }
				  catch(Exception e)
				  {e.printStackTrace();}
				    return comment;
				  }
			  
			  //new 22.10.13 // See below function, both are same, Check here 
			  private MusicMetadata cursorToCommentRecent(Cursor cursor) {
				    MusicMetadata comment = new MusicMetadata();
				  try{
				    comment.setTitle(cursor.getString(0)); // Check here 25.10.13 Title getting null ? 
				    
				  }catch(Exception e)
				  {
					  e.printStackTrace();
				  }
				  //comment.setArtist(cursor.getString(1));
				    //comment.setGenre(cursor.getString(2));
				    //comment.setDuration(cursor.getString(3));
				    
				    return comment;
				  }
			  private MusicMetadata cursorToCommentUMayLike(Cursor cursor) {
				    MusicMetadata comment = new MusicMetadata();
				    comment.setAlbum(cursor.getString(0));  
				    Log.d("Debug", "umay album = " + comment.getAlbum().toString());
				    comment.setArtist(cursor.getString(1));
				    Log.d("Debug", "umay artist = " + comment.getArtist().toString());
				    //comment.setGenre(cursor.getString(2));
				    //comment.setDuration(cursor.getString(3));
				    
				    return comment;
				  }
			  // Prasen 25.10.13 
			  
			  // To get the list of album and artist for a songtitle, to populate the u may also like list view
			  public ArrayList<MusicMetadata> getAlbumArtist(String songTitle)
			  {	
				  ArrayList<MusicMetadata> albumArtist = new ArrayList<MusicMetadata>();
				  Log.d("Debug","Inside getAlbumArtist function DAO class");
				  try
				  {
					  dbGetArtistAlbum =  dbHelper.getReadableDatabase();
					  String query = "select album, artist from music_metadata where title = ?";
					  Cursor cursor = dbGetArtistAlbum.rawQuery(query,new String[]{songTitle});
					  Log.d("Debug","cursor count = " + cursor.getCount());
					  Log.d("Debug","getAlbumArtist1");
					  cursor.moveToFirst();
					  Log.d("Debug","getAlbumArtist2");
					  while (!cursor.isAfterLast()) {
					      MusicMetadata comment = cursorToCommentUMayLike(cursor);
					      Log.d("Debug","getAlbumArtist3");
					      Log.d("Debug"," test1 = " + comment.getAlbum() + " " + comment.getArtist());
					      albumArtist.add(comment);
					      Log.d("Debug","getAlbumArtist4");
					      Log.d("inside DAO----"," Album = " + comment.getAlbum());
					      cursor.moveToNext();
					  }// while end
				  }
				  catch(Exception e)
				  {
					  Log.d("Error"," Inside getAlbumArtist List");
					  e.printStackTrace();
				  }
				 
				  return albumArtist;
			  }
			  public ArrayList<MusicMetadata> recentPlayedSongList() 
			   {	ArrayList<MusicMetadata> comments = new ArrayList<MusicMetadata>();
				  try
				  {
				 
					  Log.d("DEBUG","Inside DAOMusic, recentPlayedSongList Method");
				  String sql="select Title from music_metadata where Counter >= 1 order by Counter desc"; 
				  				    
				  Cursor cursor = database.rawQuery(sql,null );
				  Log.d("Debug","1");
				  cursor.moveToFirst();
				  Log.d("Debug","2");
				  while (!cursor.isAfterLast()) {
				      MusicMetadata comment = cursorToCommentRecent(cursor);
				      Log.d("Debug","3");
				      comments.add(comment);
				      Log.d("Debug","4");
				  Log.d("inside DAO----"," Title = " + comment.getTitle());
				      cursor.moveToNext();
				    }
				  }
				  catch(Exception e){ e.printStackTrace();}
				   return comments;
			   }
			  // Will generate list based on user's present song choice
			   public ArrayList<MusicMetadata> uMayAlsoLikeSongList(String album,String artist)
			   {
				  
				   ArrayList<MusicMetadata> comments = new ArrayList<MusicMetadata>();
				  // String selectArtist= artist;
				  // String selectAlbum = album;
				   try{
					   Log.d("Debug","Inside uMayAlsoLike DAO Class Method");
					  String sql="select Title from music_metadata where Album = ? or Artist = ?"; 
					  				    
					  Cursor cursor = database.rawQuery(sql,new String[]{album,artist} );;
					  cursor.moveToFirst();
					  while (!cursor.isAfterLast()) {
					     // MusicMetadata comment = cursorToCommentUMayLike(cursor);
						  MusicMetadata comment = cursorToCommentRecent(cursor); // Check Here 
						  comments.add(comment);
					      cursor.moveToNext();
					    }
				   }
				   catch(Exception e){e.printStackTrace();}
					  return comments;
			   }
			   
			   // Prasen 22.10.13
			   // Funtion to update the Counter in the music_metadata table // Not in use 
//			   public void updateCounter(String Artist, String Title)
//			   {
//				   String sql="select Album from music_metadata where Artist = "+ Artist +"or"+"Title = "+Title; 
// 				    
//					  Cursor cursor = database.rawQuery(sql,null );
//					  cursor.moveToFirst();
//					  while (!cursor.isAfterLast()) {
//					      MusicMetadata comment = cursorToComment(cursor);
//					      //comments.add(comment);
//					      cursor.moveToNext();
//					    }
//			   }
			   // prasen 18.10.13 
			   // Not in use Directly checking and inserting into database
//			   public int getduplicaterowCount() {
//				    Cursor c = null;
//				    try {
//				        dbGetdupcount = dbHelper.getReadableDatabase();
//				        String query = "select count(*) from music_metadata where Duration = ?";
//				        c = dbGetdupcount.rawQuery(query, new String[] {song_duration});
//				       Integer conuterval = c.getCount();
//				       Log.d("Debug","Val = " + conuterval);
//				        if (c.moveToFirst()) {
//				            return c.getInt(0);
//				        }
//				        return 0;
//				    }
//				    finally {
//				        if (c != null) {
//				            c.close();
//				        }
//				        if (dbGetdupcount != null) {
//				        	dbGetdupcount.close();
//				        }
//				    }
//				   
//				}

				
		  //Till here 
}
