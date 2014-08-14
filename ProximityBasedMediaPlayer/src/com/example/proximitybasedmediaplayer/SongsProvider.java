/*****************************************************************
 * Copyright (c) 2013 by CDAC Chennai 
 * @File        MusicMetaData
 * @Created:    08.10.2013
 * @author:     Prasenjit
 * Last Change: 09.10.2013 by Prasenjit
 ******************************************************************/
package com.example.proximitybasedmediaplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
public class SongsProvider {
	// Put your Music Folder in SD Card here
	//private String title;
	//private String artist;
	
	//File musicFolder= new File(android.os.Environment.getExternalStorageDirectory()+"/teststorage");// for samsung s4 testing device. Its been hardcoded. Have to implement it for all phones.
	File musicFolder2= new File(android.os.Environment.getExternalStorageDirectory()+"/Music"); // My cell phone[It will work on every phone which has internal storage + Songs inside Music folder]
	
	File musicFolder1= new File("/storage/sdcard1/Music"); // Current state for my phone
	File musicFolder= new File("/mnt/sdcard/Music"); // For Emulator
	File musicFolder3= new File("/storage/sdcard0/NewMusic"); // Divya Samsung, Not needed...........
	// --------------------------------------------------//
	
	
	
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	// Constructor
	public SongsProvider() {

	}

	/**
	 * Function to read all mp3 files from sdcard and store the details in
	 * ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList() {
		//File musicFolder = new File(MEDIA_PATH);
		try{
		Log.i("Path","Path = "+ musicFolder);
		Log.i("Path","Path = "+ musicFolder1);
		Log.i("Path","Path = "+ musicFolder2);
		
		if (musicFolder.listFiles(new FileExtensionFilter()).length > 0) 
		{
			
			for (File file : musicFolder.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle",file.getName().substring(0,(file.getName().length()-4)));
				song.put("songPath", file.getPath());
				// Adding each song to SongList
				songsList.add(song);
				Log.d("Debug","Inside musicFolder");
				
			}
		}
		else if (musicFolder1.listFiles(new FileExtensionFilter()).length > 0) 
		{
			for (File file : musicFolder1.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle",file.getName().substring(0,(file.getName().length()-4)));
				song.put("songPath", file.getPath());
				// Adding each song to SongList
				songsList.add(song);
				Log.d("Debug","Inside musicFolder1");
			}
		}
		else if(musicFolder2.listFiles(new FileExtensionFilter()).length > 0) 
		{
			for (File file : musicFolder2.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle",file.getName().substring(0,(file.getName().length()-4)));
				song.put("songPath", file.getPath());
				// Adding each song to SongList
				songsList.add(song);
				Log.d("Debug","Inside musicFolder2");
			}
		}
		else if(musicFolder3.listFiles(new FileExtensionFilter()).length > 0) 
		{
			for (File file : musicFolder3.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle",file.getName().substring(0,(file.getName().length()-4)));
				song.put("songPath", file.getPath());
				// Adding each song to SongList
				songsList.add(song);
				Log.d("Debug","Inside musicFolder3");
			}
		}
		else
		{
			Log.d("Debug", "No music found");
			
		}
		
		}catch(Exception e){Log.d("ERROR","Sorry, catch in songprovider .");}
		// return songs list array
		return songsList;
	}

	/**
	 * The class is used to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
