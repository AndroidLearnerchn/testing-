/*****************************************************************
 * Copyright (c) 2013 by CDAC Chennai 
 * @File        MusicMetaData
 * @Created:    13.10.2013
 * @author:     Prasenjit
 * Last Change: 13.10.2013 by Prasenjit
 ******************************************************************/
package com.example.proximitybasedmediaplayer;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
//////////////////////////////////////////////////////////////////////////////////////////
//This Class is not in use																//
//////////////////////////////////////////////////////////////////////////////////////////
public class DAORecentMusicData {
	private SQLiteDatabase database;
	private final StoreRecentSongSqliteHelper dbHelper;
	private final String[] allColumns = {
			StoreRecentSongSqliteHelper.COLUMN_ALBUM,
			StoreRecentSongSqliteHelper.COLUMN_ARTIST,
			StoreRecentSongSqliteHelper.COLUMN_ID };

	public DAORecentMusicData(final Context context) {
		dbHelper = new StoreRecentSongSqliteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public RecentMusicPojo createComment(final String title, final String artist) {
		 RecentMusicPojo newComment = null;
		final ContentValues values = new ContentValues();
		values.put(StoreRecentSongSqliteHelper.COLUMN_ALBUM, title);
		values.put(StoreRecentSongSqliteHelper.COLUMN_ARTIST, artist);
		//values.put(StoreRecentSongSqliteHelper.COLUMN_ID, id);
		
		final long insertId = database.insert(
				StoreRecentSongSqliteHelper.TABLE_MUSIC_METADATA, null, values);

		// Cursor cursor =
		// database.query(StoreDataSqliteHelper.TABLE_MUSIC_METADATA, new
		// String[]
		// {StoreDataSqliteHelper.COLUMN_ALBUM,StoreDataSqliteHelper.COLUMN_ARTIST
		// },
		//
		// null, null, null, null, null);
		try{
		final Cursor cursor = database.query(
				StoreRecentSongSqliteHelper.TABLE_MUSIC_METADATA, allColumns,
				StoreRecentSongSqliteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		final RecentMusicPojo newComment1 = cursorToComment(cursor);
		newComment = newComment1;
		cursor.close();
		}
		catch(Exception e)
		{
			Log.d("ERROR","Inside DAORecent Music");
			e.printStackTrace();
		}
		
		return newComment;
	}

	public void deleteComment(final MusicMetadata comment) {
		final long id = comment.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(StoreDataSqliteHelper.TABLE_MUSIC_METADATA,
				StoreDataSqliteHelper.COLUMN_ALBUM + " = " + id, null);
	}

	public List<RecentMusicPojo> getAllComments() {
		final List<RecentMusicPojo> comments = new ArrayList<RecentMusicPojo>();

		final Cursor cursor = database.query(
				StoreDataSqliteHelper.TABLE_MUSIC_METADATA, allColumns, null,
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			final RecentMusicPojo comment = cursorToComment(cursor);
			comments.add(comment);       
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return comments;
	}

	private RecentMusicPojo cursorToComment(final Cursor cursor) {
		final RecentMusicPojo comment = new RecentMusicPojo();
		comment.setAlbum(cursor.getString(0));
		comment.setArtist(cursor.getString(1));
		comment.setId(cursor.getInt(2));
		return comment;
	}
	private MusicMetadata cursorTwoComment(final Cursor cursor) {
		final MusicMetadata comment = new MusicMetadata();
		comment.setAlbum(cursor.getString(0));
		comment.setArtist(cursor.getString(1));
		comment.setId(cursor.getInt(2));
		return comment;
	}

	// Prasen 11.10.13
	public List<RecentMusicPojo> recentPlayedSongList(final String artist) {

		final List<RecentMusicPojo> comments = new ArrayList<RecentMusicPojo>();
		final String selectArtist = artist;
		final String sql = "select Title from music_metadata where Artist="
				+ selectArtist;

		final Cursor cursor = database.rawQuery(sql, null);
		;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			final RecentMusicPojo comment = cursorToComment(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		return comments;
	}

	public  List<MusicMetadata> uMayAlsoLikeSongList(final String album,
			final String artist) {

		final List<MusicMetadata> comments = new ArrayList<MusicMetadata>();
		final String selectArtist = artist;
		final String selectAlbum = album;
		final String sql = "select Title from music_metadata where Artist="
				+ selectAlbum + "or" + "Album = " + selectAlbum;

		final Cursor cursor = database.rawQuery(sql, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			final MusicMetadata comment = cursorTwoComment(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		return comments;
	}
	// Till here
}
