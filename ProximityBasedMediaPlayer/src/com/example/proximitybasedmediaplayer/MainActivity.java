/*****************************************************************
 * Copyright (c) 2013 by CDAC Chennai 
 * @File        MainActivity
 * @Created:    08.10.2013
 * @author:     Prasenjit
 * Last Change: 22.10.2013 by Prasenjit
 ******************************************************************/
package com.example.proximitybasedmediaplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;
import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, AnimationListener {

	public static ImageView btnPlay, btnForward, btnBackward, btnNext,
			btnPrevious, listSongBtn, btnRecent, btnU_May_Also_Like;
	public static ImageButton btnShuffle, btnRepeat, btnStop;
	public static SeekBar songProgressBar;

	// Songs Array list
	public static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	public static ArrayList<MusicMetadata> RecentSongsList = new ArrayList<MusicMetadata>();
	public static ArrayList<MusicMetadata> UMayAlsoLikeSongsList = new ArrayList<MusicMetadata>();
	
	private ListAdapter adapter, adapter_Recent_Song, adapter_U_May_Also_Like;
	private ListView listSongLv, listRecent, list_U_May_Also_Like;
	private LinearLayout playerScreen, listSongScreen, recent_song,
			u_may_also_like;
	private Button backBtn, back_Recent, back_U_May_Also_Like;
	public static TextView songTitle, songCurrentDurationLabel,
			songTotalDurationLabel;
	// private SensorManager mSensorManager;
	// private Sensor mSensor;
	public Intent playerService;
	public Intent playerServiceRecent;

	// public PlayerService playerclass;
	AdapterView<?> tempParent; // To store the parent from the listview adapter
	public static int cur_song_Position;
	private DAOMusicData datasource, datasourcelist, datasourceumayalsolike,datasourceIncreaseCount;
	//private DAORecentMusicData datasourceRecentMusic;
	private String duration;
	private String genre;
	
	private String album;
	private String artist;
	
	private String title1;
	private String artist1;
	private String path;
	private String Title, curTitle, curAlbum, curArtist;
	MediaMetadataRetriever metaRetriver;
	//private Runnable viewParts; // listview runnable
	private Handler handler, handlertoInsertRecentSong, populateDataHandler, populateUMayLikeHandler;
	SharedPreferences preferences;
	SharedPreferences preferencesforretrieval;

	// Debug strings
	private final String DEBUG_List = "Inside showList";
	private final String DEBUG_Recent = "Inside RecentshowList";
	private final String DEBUG_U_May_Like = "Inside U_May_Also_Like_showList";

	// Till here
	//Main method
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();

		try {
			Log.d("Debug","Before populateListThread in onCreate Method");
			populateListThread();// first thread
			//Log.d("DEBUG", "Before sleep");
			Thread.sleep(4000);
		//	Log.d("DEBUG", "After sleep");
			
			// Now these methods are being called in populatelist thread
			// populateUMayAlsoLikeList();
			// populateList();
		}

		catch (Exception e) 
		{
			Log.d("inside main", "error in calling thread method");

			e.printStackTrace();
		}
		try 
		{
			Log.d("Debug","Before insertDataThread in onCreate Method");
			insertDataThread(); // second thread
			Log.d("DEBUG", "oncreate");
		}
		catch (Exception e)
		{
			Log.d("ERRRor", "Inside catch insertDataThread");
			e.printStackTrace();
		}
		

	} //  Main activity onCreate ends
	// Added 17.12.13
	@Override
	public void onPause()
	{
		super.onPause();
		Log.d("onPause","On Pause called");
		//PlayerService ps = new PlayerService();
		//ps.controller.unregisterProximityService(ps.proximityListener);
		//Log.d("Debug", "stop in main activity");
		
	}
	@Override
	public void onStop()
	{
		super.onStop();
		//PlayerService ps = new PlayerService();
		//ps.controller.unregisterProximityService(ps.proximityListener);
		Log.d("Debug", "stop in main activity");
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("DEBUG", "Inside Destroy");
		Log.d("DEBUG", "Before retriveData Called");
		// Prasen 9.10.13
//		Not in use 22.10.13
		//try {
//			retrieveData(); // method to see if data getting stored in the
//							// shared pref
//		} catch (Exception e) {
//			Log.d("ERROr", " Catch block for retrieveData Call");
//		}
		//Log.d("DEBUG", "After retriveData Called");
		// Till here
		try {
			if (!PlayerService.mp.isPlaying()) {
				Log.d("DEBUG1", "Inside destroy");
				stopService(playerService);
				Log.d("DEBUG2", "Inside destroy");
				cancelNotification();
				Log.d("DEBUG3", "Inside destroy");

			}
		} catch (Exception e) {
			Log.d("DEBUG4", "Inside destroy");
			e.printStackTrace();
		}
		try {
			if (stopService(playerService)) {

				cancelNotification();
				Log.d("DEBUG5", "Inside destroy");
			}
		} catch (Exception e) {
			Log.d("DEBUG", "Inside destroy");
			e.printStackTrace();
		}
	}

	// -- Cancel Notification
	public void cancelNotification() {
		String notificationServiceStr = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(notificationServiceStr);
		mNotificationManager.cancel(PlayerService.NOTIFICATION_ID);
	}

	/**
	 * Initiaze Views
	 */
	private void initViews() {

		playerScreen = (LinearLayout) findViewById(R.id.playerScreen);
		listSongScreen = (LinearLayout) findViewById(R.id.list_song_layout);
		listSongScreen.setVisibility(View.INVISIBLE);
		// Prasen 11.10.13
		u_may_also_like = (LinearLayout) findViewById(R.id.list_song_layout3); // changed
																				// here
																				// 15.10.13
																				// corrected
																				// the
																				// UI
																				// issue
		u_may_also_like.setVisibility(View.INVISIBLE);

		recent_song = (LinearLayout) findViewById(R.id.list_song_layout2); // changed
																			// here
																			// 15.10.13
		recent_song.setVisibility(View.INVISIBLE);
		// Till here
		// prasen 12.9.13
		btnStop = (ImageButton) findViewById(R.id.btnStop);
		// till here
		// Prasen 12.10.13
		btnU_May_Also_Like = (ImageView) findViewById(R.id.listsong_btn_u_may_also_like);
		btnRecent = (ImageView) findViewById(R.id.listsong_btn_recent);
		// TIll here
		btnPlay = (ImageView) findViewById(R.id.btn_play_imageview);
		btnForward = (ImageView) findViewById(R.id.btn_forward_imageview);
		btnBackward = (ImageView) findViewById(R.id.btn_backward_imagview);
		btnNext = (ImageView) findViewById(R.id.btn_next_imageview);
		btnPrevious = (ImageView) findViewById(R.id.btn_previous_imageview);
		listSongBtn = (ImageView) findViewById(R.id.listsong_btn);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);

		songProgressBar = (SeekBar) findViewById(R.id.song_playing_progressbar);

		songTitle = (TextView) findViewById(R.id.song_title_txt);
		songCurrentDurationLabel = (TextView) findViewById(R.id.current_time_txt);
		songTotalDurationLabel = (TextView) findViewById(R.id.total_time_txt);
		backBtn = (Button) findViewById(R.id.back_btn);
		// Prasen 14.10.13
		back_Recent = (Button) findViewById(R.id.back_btn2);
		back_U_May_Also_Like = (Button) findViewById(R.id.back_btn3);

		// Till here
		// from here
		btnStop.setOnClickListener(this);
		// till here
		btnPlay.setOnClickListener(this);
		btnForward.setOnClickListener(this);
		btnBackward.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnPrevious.setOnClickListener(this);
		btnShuffle.setOnClickListener(this);
		btnRepeat.setOnClickListener(this);
		listSongBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		// Prasen 13.10.13
		btnRecent.setOnClickListener(this);
		btnU_May_Also_Like.setOnClickListener(this);

		// Till here
		// 14.10.13
		back_Recent.setOnClickListener(this);
		back_U_May_Also_Like.setOnClickListener(this);

		// Till here

	}

	// Prasen 11.10.13
	private void disableinitViews() {
		// btnPlay = (ImageView) findViewById(R.id.btn_play_imageview);
		// btnForward = (ImageView) findViewById(R.id.btn_forward_imageview);
		// btnBackward = (ImageView) findViewById(R.id.btn_backward_imagview);
		// btnNext = (ImageView) findViewById(R.id.btn_next_imageview);
		// btnPrevious = (ImageView) findViewById(R.id.btn_previous_imageview);
		//
		// To disable all buttons
		btnPlay.setEnabled(false);
		btnForward.setEnabled(false);
		btnBackward.setEnabled(false);
		btnNext.setEnabled(false);
		btnPrevious.setEnabled(false);

	}

	// Till Here
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * View OnclickListener Implement
	 * 
	 * @param v
	 */
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.d("DEBUG", "view id = " + v.getId());
		switch (v.getId()) {
		case R.id.listsong_btn:
			showListSongScreen();
			listSongLv.setAdapter(adapter);
			listSongLv.setOnItemClickListener(this);

			break;
		// Prasen 12.10.13
		case R.id.listsong_btn_recent:
			// showListSongScreen();
			try{
				populateRecentList();  // call a thread (synchronized with other database operation in the main thread)instead as here one database operation is being done, change needed here
				showRecentSongScreen();
			// listRecent.setAdapter(adapter);//Prasen change the adapter here
			listRecent.setAdapter(adapter_Recent_Song);
			listRecent.setOnItemClickListener(this);
			}catch(Exception e){
				Log.d("Debug","Here in Onclick view");
				e.printStackTrace();}
			break;

		case R.id.listsong_btn_u_may_also_like:
			// showListSongScreen();
			try{
				//populateUMayLikeThread();
				populateUMayAlsoLikeList();
				Log.d("Debug","Before sleep in switch case");
				//Thread.sleep(1000);
				Log.d("Debug"," After, here1");
				showUMayAlsoLikeSongScreen();
				Log.d("Debug","here2");
			   //list_U_May_Also_Like.setAdapter(adapter);
				try{
					Log.d("Debug","Inside imageview click umayalso like");
			//	populateUMayLikeThread();
				}
				catch(Exception e){
					Log.d("Error","Inside imageview click umayalso like");
					e.printStackTrace();}
				try{
					Log.d("Debug","*************************************1");
					list_U_May_Also_Like.setAdapter(adapter_U_May_Also_Like);// This line causing the error
			Log.d("Debug","************************************2");
					list_U_May_Also_Like.setOnItemClickListener(this);
			Log.d("Debug","*************************************3");
				}
				catch(Exception e){
					Log.d("Catch Block err","******************inside listsong_btn_u_may_also_like*******************");
					e.printStackTrace();}
				}catch(Exception e){e.printStackTrace();}
			break;
		// Till here

		case R.id.back_btn:
			Log.d("DEBUG", "back_btn clicked");
			showListSongScreen();
			break;
		// from here
		case R.id.back_btn2: // Recent Song Back button
			 Log.d("DEBUG", "btn2 clicked");
			 showRecentSongScreen();
			break;
		case R.id.back_btn3: // U May Also Like Back Button
			Log.d("DEBUG", "btn3 clicked");
			showUMayAlsoLikeSongScreen();
			break;
		case R.id.btnStop:
			// stopService(new Intent(MainActivity.this,PlayerService.class));

			// Disable the player buttons
			// disableinitViews();
			// To stop the service
			
			stopService(playerService);
			// cancelNotification();

			Log.i("StopService", "stop service called");
			break;
		 // till here 12.9.13
		}
	}

	/**
	 * onItemClick Listener Implement.
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			final Integer localposition;
			// TODO Auto-generated method stub
			tempParent = parent; // To store the view type, to be used to make
									// invisible when required
			Log.d("Debug ", " tempParent = " + tempParent);
			switch (parent.getId()) {

			case R.id.listsong_listview:
				try{
				cur_song_Position = position; // To make the current position available in full application from available song-list 
				Log.d("Debug","check crash");
				playerService = new Intent(getApplicationContext(), PlayerService.class); // playerservice
																		// intent
				playerService.putExtra("songIndex", position); // pass the extra
				}
				catch(Exception e){e.printStackTrace();}
				//playerService.putExtra("id", id);   			// to the
																// intent, to be
																// fetched in
																// service to
																// get the
																// position
				
				
				// Not needed, Now we have to use the DAOMusicData class 22.10.13
				
				//datasourceRecentMusic = new DAORecentMusicData(this); // Added
																		// 17.10.13
				//datasourceRecentMusic.open(); // Added 17.10.13 this method
												// added on 17.10.13
				datasourceIncreaseCount = new DAOMusicData(this);
				datasourceIncreaseCount.open();

				try {
					localposition = position;
					Runnable addRecentThread = new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							//RecentMusicPojo commentRecent;// Added 17.10.13
							//MusicMetadata commentRecent;
							
							Log.i("DEBUG",
									"Before, Data inserted in recent table");
							//create a method in DAOMusicData class to update the Counter value for this song.
							// Correct the database Counter column, as null is allowed, set all null value column to 0.
							// Onclick increament the Counter+1, update the table.
							
							//	commentRecent = datasourceRecentMusic.createComment(artist, title);
							Log.i("DEBUG",
									"After, Data inserted in recent table");
							// from here Prasen testing purpose 17.10.13

							String details = listSongLv.getAdapter().getItem(
									localposition).toString();
						//	listSongLv.getAdapter().getItem(localposition).
							String path = details.toString().substring(10, 40);
							try{
								Log.d("Debug", "inside try, increase counter");
								//Log.d("Debug", " title = "+ Title );
								
								//--------------------------------------------------Get Title for the selected song------//
								curTitle = (String)findCurTitle(localposition);
								Log.d("Debug title","title = " + curTitle);
								//----------------------------------------------------------------------------------------//
								Log.d("Debug", "below");
								datasourceIncreaseCount.createComment(curTitle);
								try{
									Log.d("Debug","After counter change, Populate the recent list");
								//populateRecentList();
								Log.d("Error","After funtion call, Populate the recent list");
								}
								catch(Exception e){
									Log.d("Error","After counter change, Populate the recent list");
									e.printStackTrace();}
								Log.d("Debug", "above");								
							}
							catch(Exception e)
							{
								Log.d("ERROR","Inside increase Counter run statement");
								e.printStackTrace();
							}
							// String classname =
							// listSongLv.getAdapter().getItem(localposition).getClass().toString();
							Integer count = listSongLv.getAdapter().getCount();
							Log.d("Deee ", path);
							Log.d("Deee", "count = " + count + " string "+ details );
							// till here testing
						}
						
					};
					new Thread(addRecentThread).start();
					try
					{
						Log.d("Debug","populateUMayLikeThread()1");
						//populateUMayLikeThread();
						//Log.d("Debug","populateUMayLikeThread()2");
					}
					catch(Exception e)
					{
						Log.d("Error","populateUMayLikeThread()");
						e.printStackTrace();
					}
					// handlertoInsertRecentSong.postDelayed(addRecentThread,
					// 1000);
				} catch (Exception e) {
					
					Log.d("ERROR", "Inside run, recent mUsic insert");
					e.printStackTrace();
				}
				datasourceIncreaseCount.close();
				try{
				startService(playerService);
				}
				catch(Exception e){
						Log.d("ERROR","inside listsong imageview case");
						e.printStackTrace();}
				showListSongScreen();

				break;

			// From Here 12.10.13
			case R.id.listsong_recent_listview:
				// Create a thread, store the value in database, populate the
				// adapter here then start the service
				try{
					Log.d("Debug","Here1");
				populateRecentList();
				Log.d("Debug","Here2");
				}
				catch(Exception e){
					Log.d("Error","while call populateRecentList");
					e.printStackTrace();}
				//playerServiceRecent = new Intent(this, PlayerServiceforRecent.class); 
				// intent
				//playerServiceRecent.putExtra("songIndex", position);
                //playerService.putExtra("id", id);
				Log.d(DEBUG_Recent,
						"inside case, Adapterlistener listsong_recent_listview, position = " + position);
				
			  //  startService(playerServiceRecent);
			    
				showRecentSongScreen();
				break;

			case R.id.listsong_umayalsolike_listview:

				// playerService = new Intent(this, PlayerService.class);
				// playerService.putExtra("songIndex", position);
				// startService(playerService);
				try{
					Log.d("Debug","Inside imageview click umayalso like");
				populateUMayLikeThread();
				}
				catch(Exception e){
					Log.d("Error","Inside imageview click umayalso like");
					e.printStackTrace();}
				showUMayAlsoLikeSongScreen();
				

				break;

			// Till Here
			}
		} catch (Exception e) {
			Log.d("ERROR", "Inside selecting List view");
			e.printStackTrace();
		}

	}

	/**
	 * Show All Listsong Screen with Sliding Animation
	 */

	private boolean isVisibleRecentList = false;

	// Two New function to hide and start the animation on the newly added
	// listview[recent_listview, u_may_also_like] 14.10.13
	
	//Method to recent list song layout visible/invisible
	private void showRecentSongScreen() {
		Animation anim;
		// Prasen 12.10.13
		//LinearLayout tempLayout;

		// Till here
		Log.d(DEBUG_Recent, DEBUG_Recent + "recent called");
		if (isVisibleRecentList == false) {
			Log.d("value = ", "" + isVisibleRecentList);
			Log.d(DEBUG_Recent, DEBUG_Recent);
			recent_song.setVisibility(View.VISIBLE);
			anim = AnimationUtils.loadAnimation(this, R.anim.push_down_in);
			isVisibleRecentList = true;

		} else {
			Log.d("value = ", "" + isVisibleRecentList);
			Log.d(DEBUG_Recent, DEBUG_Recent + "else part");
			anim = AnimationUtils.loadAnimation(this, R.anim.push_down_out);
			isVisibleRecentList = false;
			playerScreen.setVisibility(View.VISIBLE);
			Log.d(DEBUG_Recent, DEBUG_Recent + "else part");
		}
		anim.setAnimationListener(this);
		recent_song.startAnimation(anim);

	}

	
	// u_may_also_like
	private boolean isVisibleUMayAlsoLike = false;
	//Method to set visible / invisible the u may Also like Layout
	private void showUMayAlsoLikeSongScreen() {
		Animation anim;
		// Prasen 12.10.13
		//LinearLayout tempLayout;
		// Till here
		if (isVisibleUMayAlsoLike == false) {
			Log.d("value = ", "" + isVisibleUMayAlsoLike);
			Log.d(DEBUG_U_May_Like, DEBUG_U_May_Like + "*******************Here1");
			u_may_also_like.setVisibility(View.VISIBLE);
			Log.d("Debug","**********************Here2");
			anim = AnimationUtils.loadAnimation(this, R.anim.push_down_in);
			isVisibleUMayAlsoLike = true;
			Log.d(DEBUG_U_May_Like, DEBUG_U_May_Like+ "************************Here3");
		} else {
			Log.d("value = ", "" + isVisibleUMayAlsoLike);
			Log.d(DEBUG_U_May_Like, DEBUG_U_May_Like + "else part");
			anim = AnimationUtils.loadAnimation(this, R.anim.push_down_out);
			isVisibleUMayAlsoLike = false;
			playerScreen.setVisibility(View.VISIBLE);
			Log.d(DEBUG_U_May_Like, DEBUG_U_May_Like + "else part");

		}
		anim.setAnimationListener(this);
		u_may_also_like.startAnimation(anim);

	}

	// Till here
	
	private boolean isVisible = false;
	
	//Method to  Available song list layout visible/invisible
	private void showListSongScreen() {
		Animation anim;
		// Prasen 12.10.13
		//LinearLayout tempLayout;
		// Till here
		if (isVisible == false) {
			Log.d("value = ", "" + isVisible);
			Log.d(DEBUG_List, DEBUG_List);
			listSongScreen.setVisibility(View.VISIBLE);
			anim = AnimationUtils.loadAnimation(this, R.anim.push_down_in);
			isVisible = true;
			Log.d(DEBUG_List, DEBUG_List);
		} else {
			Log.d("value = ", "" + isVisible);
			Log.d(DEBUG_List, DEBUG_List + "else part");
			anim = AnimationUtils.loadAnimation(this, R.anim.push_down_out);
			isVisible = false;
			playerScreen.setVisibility(View.VISIBLE);
			Log.d(DEBUG_List, DEBUG_List + "else part");

		}
		anim.setAnimationListener(this); 
		listSongScreen.startAnimation(anim); 

	}

	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		try {
			if (isVisible == true) {
				playerScreen.setVisibility(View.INVISIBLE);
				Log.d("DEBUg", "is visible1=true");
			} else {
				Log.d("DEBUg", "is visible1=false");
				listSongScreen.setVisibility(View.INVISIBLE);
			}
			if (isVisibleRecentList == true) {
				Log.d("DEBUg", "is visible2=true");
				playerScreen.setVisibility(View.INVISIBLE);
			} else {
				Log.d("DEBUg", "is visible2=false");
				recent_song.setVisibility(View.INVISIBLE);
			}
			if (isVisibleUMayAlsoLike == true) {
				Log.d("DEBUg", "is visible3=true");
				playerScreen.setVisibility(View.INVISIBLE);
			} else {
				Log.d("DEBUg", "is visible3=false");
				u_may_also_like.setVisibility(View.INVISIBLE);
			}

		} catch (Exception e) {
			Log.d("ERROR", "onAnimationEnd function");
			e.printStackTrace();
		}

	}

	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}


	// Prasen 9.10.13 from here
	// These functions not in use, purpose was to store metadata on
	// SharedPreference for faster application
	public void storeData(String titlefromMain, String artistfromMain) {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		title1 = titlefromMain;
		artist1 = artistfromMain;
		handler = new Handler();
		Runnable r = new Runnable() {
			public void run() {

				try {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("title", title1);
					editor.putString("Artist", artist1);
					editor.commit();
					Log.d("inside run", "No error, inside run");

				} catch (Exception e) {
					Log.d("inside run", "error inside run");
				}
			}
		};
		handler.postDelayed(r, 1000);

	}
//not in use
	public void retrieveData() {
		preferencesforretrieval = PreferenceManager
				.getDefaultSharedPreferences(this);
		String name = preferencesforretrieval.getString("title", "");
		String artist = preferencesforretrieval.getString("artist", "");
		try {
			if (!name.equalsIgnoreCase("")) {
				name = name + "   value found";
				Log.d("Artist val ", artist);
				Log.d("title val", name);
			} else {
				name = "No value found";
				Log.d("title", name);
			}
		} catch (Exception e) {
			Log.d("ERROR", "Error in retrieveData Method");
		}
	}

	// Prasen 16.10.13 To mudularize the code
	// All 3(listview, recentlist and umayalsolike) list view getting populated
	// by this thread
	public void populateListThread() {
		try {
			populateDataHandler = new Handler();
			Runnable populatedatarun = new Runnable() {
				public void run() {
					
					try {
						populateList(); // Populate the Available songs in the
										// list view
						Log.d("inside run populatedatarun",
								"No error, inside run populatedatarun");
						try {
							Log.d("populatedata",
									"try before call, inside run populatedatarun");
							populateRecentList(); // To populate the recent list
													// view

							Log.d("populatedata",
									"after call, inside run populatedatarun");
						} catch (Exception e) {
							Log.d("populatedata ERROR",
									"Error, inside run populatedatarun");
							e.printStackTrace();
						}
						//changed here
//						try {
//							Log.d("populatedata",
//									"try for UMayAlsoLike, inside run populatedatarun");
//							populateUMayAlsoLikeList(); // Populate the U May
//														// Also Like list view
//						} catch (Exception e) {
//							Log.d("populateUMayAlsoLike ERROR",
//									"Error, inside run populatedatarunThread");
//							e.printStackTrace();
//						}
					} catch (Exception e) {
						Log.d("inside run", "error inside run");
					}
				}
			};
			// handler.postDelayed(populatedatarun, 10000);
			Log.d("DEBUG", "inside run, before start");
			new Thread(populatedatarun).start();
			Log.d("DEBUG", "inside run, after start");
		} catch (Exception e) {
			Log.d("Inside run Thread Method", "Error here");
			e.printStackTrace();
		}
	}
	
	
	
// 24.10.13 Prasen 
	//This  method should be moved to SongProvider class
	// To find the current song title [working]
	public String findCurTitle(int curposition)
	{
		// Logic part
		String localcurTitle=null;
		String localcurPath=null;
		Log.d("Debug","inside findcurTitle");
		try{
		localcurTitle = songsList.get(curposition).get("songTitle");
		localcurPath = songsList.get(curposition).get("songPath");
		Log.d("Debug", "Curtitle = " + localcurTitle + " And cur Path = " + localcurPath);
		}
		catch(Exception e){e.printStackTrace();}
		
		return localcurTitle; 
	}
	
	
	// Method to populate the available song list in the list view
	public void populateList() throws InterruptedException {

		Thread.sleep(2000);
		
		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
		SongsProvider plm = new SongsProvider();
		// get all songs from sdcard. If there no directory contains
		// musing-->throw exception
		try {

			songsList = plm.getPlayList();

			// looping through playlist

			Log.d("no. of songs = ", "no. of songs = " + songsList.size());

			for (int i = 0; i < songsList.size(); i++) {
				// creating new HashMap
				HashMap<String, String> song = songsList.get(i);
				//System.out.println("I m here " + path);
				Log.d("song", "song" + song);
				// Add song on the Array list
				songsListData.add(song);

			}

			// Adding song Items to List View Adapter
			adapter = new SimpleAdapter(this, songsListData,
					R.layout.listsong_item, new String[] { "songTitle" },
					new int[] { R.id.songTitle });

			// Till here
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		listSongLv = (ListView) findViewById(R.id.listsong_listview);
		// Prasen 12.10.13
		// listRecent = (ListView) findViewById(R.id.listsong_recent_listview);
		//list_U_May_Also_Like = (ListView) findViewById(R.id.listsong_umayalsolike_listview);

		// Till here
		// ----start service.
		try{
			
			Log.d("Debug", "Inside if statement here 1 ********");
		playerService = new Intent(this, PlayerService.class);
		if (playerService.resolveActivity(getPackageManager()) != null) {
			Log.d("Debug", "Inside if statement here 2 ********");
		playerService.putExtra("songIndex", PlayerService.currentSongIndex);
		try{
		startService(playerService);
		}
		catch(Exception e){
			Log.d("ERROR","Inside populateList()");
			e.printStackTrace();}
		}
		}
		catch(Exception e)
		{
			Log.d("ERROR","Error here in populate list");
			e.printStackTrace();
			}
	}

	// 18.10.13
	
//-------------------------------------------Thread to run insertData Method--------------------------------//
	public void insertDataThread() {
		try {
			populateDataHandler = new Handler();
			Runnable insertdatarun = new Runnable() {
				public void run() {

					try {
						insertData();
						Log.d("inside run populatedatarun",
								"No error, inside run populatedatarun");

					} catch (Exception e) {
						Log.d("inside run", "error inside run");
					}
				}
			};

			Log.d("DEBUG", "inside run, before start");
			new Thread(insertdatarun).start();
			Log.d("DEBUG", "inside run, after start");
		} catch (Exception e) {
			Log.d("Inside run Thread Method", "Error here");
			e.printStackTrace();
		}
	}
	//----------------------------------------------------------------------------------------------------//
	
//--------------------------------Thread method for populating U may also like list----------------------//
	public void populateUMayLikeThread() {
		try {
			populateUMayLikeHandler = new Handler();
			Runnable populateUMayLikerun = new Runnable() {
				public void run() {

					try {
						
						Log.d("Debug","Inside Run populateUMay.......");
						populateUMayAlsoLikeList();
						Log.d("Debug","Before sleep.............................................");
						Thread.sleep(3000);
						Log.d("inside run populateumaylike",
								"No error, populateUMayAlsoLikeList() end here ");

					} catch (Exception e) {
						Log.d("inside run", "error inside run");
					}
				}
			};

			Log.d("DEBUG", "inside run, before start");
			new Thread(populateUMayLikerun).start();
			Log.d("DEBUG", "inside run, after start");
		} catch (Exception e) {
			Log.d("Inside run Thread Method", "Error here");
			e.printStackTrace();
		}
	}
	//-----------------------------------------------------------------------------------------------//
	// Prasen 21.10.13
	//Method to populate the Recent song list. here one database query object present. To query the database for counter value
	public void populateRecentList() {
		try {
			Log.d("Debud","Inside recent Song List Method");
			listRecent = (ListView) findViewById(R.id.listsong_recent_listview);
			Log.d("Debug1*****************", "******************");
			try{
				
				datasourcelist = new DAOMusicData(this);
				datasourcelist.readOnly();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			Log.d("Debug2*****************", "******************");
			try
			{
			RecentSongsList = datasourcelist.recentPlayedSongList();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			Log.d("Debug3*****************", "******************");

			// ---------------------------------------To get the value of item
			// inside the
			// ArrayList---------------------------------------------------//

			for (int i = 0; i < RecentSongsList.size(); i++) {
				// creating new HashMap
				MusicMetadata recentsong = RecentSongsList.get(i);
				// System.out.println("SONG::::" +song.size());
				//
				String title = recentsong.getTitle();
			//	curAlbum = Album;
				//curTitle = recentsong.getTitle();
				//curArtist = recentsong.getArtist();
				Log.d("Debug", "title = " + title);
				//Log.d("song", "" + recentsong.toString());

			}
			// -------------------------------------------------------------------------------------------//
			// Log.d("Debug2*****************","******************");
			Integer test = RecentSongsList.size();
			// Log.d("Debug3*****************","******************");
			Log.d("DEBUG123", "size of recentlist " + test);
			// Log.d("Debug4*****************","******************");

			adapter_Recent_Song = new MetadataArrayAdapter(this,
					R.layout.recentlistsong_item, RecentSongsList);

			// Log.d("Debug5*****************","******************");

			// Log.d("Debug6*****************","******************");

			listRecent.findViewById(R.id.listsong_recent_listview);

			// Log.d("Debug7*****************","******************");
			listRecent.setAdapter(adapter_Recent_Song);
			datasourcelist.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Till Here 21.10.13

	// Prasen 22.10.13

	// Method to populate the U May Also Like List View
	public void populateUMayAlsoLikeList() {
		try {
			ArrayList<MusicMetadata> albumArtist = null;
			list_U_May_Also_Like = (ListView) findViewById(R.id.listsong_umayalsolike_listview);

			// Populate the array list
			Log.d("Debug1*****************", "Inside populateUMayAlsoLikeList()main activity method ************");
			// RecentSongsList = datasource.recentPlayedSongList();
//			UMayAlsoLikeSongsList.add(new MusicMetadata(
//					"U may Also Like album1"));
//			UMayAlsoLikeSongsList.add(new MusicMetadata(
//					"U may Also Like album2"));
//			UMayAlsoLikeSongsList.add(new MusicMetadata(
//					"U may Also Like album3"));
			// Date 25.10.13 Prasen
			try
			{
				Log.d("DEBUG","Here1");
				datasourceumayalsolike 	= new DAOMusicData(this);
				Log.d("DEBUG","Here2");		
				datasourceumayalsolike.readOnly();
				// get current title
				
				// Use curTitle to pass to a method and get the album and title
				Log.d("DEBUG","Here3");
				Log.d("DEBUG","Here4 " + cur_song_Position );
				if(cur_song_Position >= 0)
				{
					Log.d("DEBUG","Here5" );
					if(curTitle!=null)
					{
						Log.d("DEBUG","Here6" );
						albumArtist = datasourceumayalsolike.getAlbumArtist(curTitle);
						Log.d("DEBUG","Here7" );
					}
					else
						{
						Log.d("DEBUG","Here8" );
						curTitle = songsList.get(cur_song_Position).get("songTitle");
						Log.d("DEBUG","Here9 = " + curTitle );
						albumArtist = datasourceumayalsolike.getAlbumArtist(curTitle);
						}
						
				}
				else
				{
					Log.d("Debug"," cur_song_Position is not >=0 " );
				}
				Log.d("DEBUG","Here10");
				//albumArtist 25.10.13 prasenjit
				//-----------------------------Get the current Album and Artist----------------------////
				   for (int i = 0; i < albumArtist.size(); i++) {
					// creating new HashMap
					MusicMetadata umayalsolikesong = albumArtist.get(i);
					// System.out.println("SONG::::" +song.size());
					//
					curAlbum = umayalsolikesong.getAlbum();
					curArtist = umayalsolikesong.getArtist();
					Log.d("Debug", "Album = " + curAlbum +" Artist = "+ curArtist);
					Log.d("song", "" + umayalsolikesong.toString());
				   }
				//-----------------------------------------------------------------------------------////
				   // Set the Arraylist Empty
				   UMayAlsoLikeSongsList.clear();
				UMayAlsoLikeSongsList = datasourceumayalsolike.uMayAlsoLikeSongList(curAlbum, curArtist);// To be changed after fetching the current album and artist
			}
			catch(Exception e)
			{
				Log.d("ERROR","db con, u may also like ");
				e.printStackTrace();
			}
			// Till here 25.10.13
			// ---------------------------------------To get the value of item
			// inside the
			// ArrayList---------------------------------------------------//

			for (int i = 0; i < UMayAlsoLikeSongsList.size(); i++) {
				// creating new HashMap
				MusicMetadata umayalsolikesong = UMayAlsoLikeSongsList.get(i);
				// System.out.println("SONG::::" +song.size());
				//
				String Title = umayalsolikesong.getTitle();
				Log.d("Debug", "Title = " + Title);
				//Log.d("song", "" + umayalsolikesong.toString());

			}
			// -------------------------------------------------------------------------------------------//
			// Log.d("Debug2*****************","******************");
			Integer test = UMayAlsoLikeSongsList.size();
			Log.d("Debug3*****************", "******************");
			// Log.d("DEBUG123","size of recentlist "+test);
			// Log.d("Debug4*****************","******************");

			adapter_U_May_Also_Like = new MetadataAdapter_U_May_Also_Like(this,
					R.layout.umayalsolike_listsong_item, UMayAlsoLikeSongsList);

			// Log.d("Debug5*****************","******************");

			// Log.d("Debug6*****************","******************");

			list_U_May_Also_Like
					.findViewById(R.id.listsong_umayalsolike_listview);

			// Log.d("Debug7*****************","******************");
			list_U_May_Also_Like.setAdapter(adapter_Recent_Song); // Listview.setAdapter(ur
			//datasourceumayalsolike.close();													// adapter)

		} catch (Exception e) {
			e.printStackTrace();
			datasourceumayalsolike.close();
		}
		datasourceumayalsolike.close();
	}

	// Till Here 22.10.13
	// Method to insert Metadata information to the database  
	public void insertData() throws InterruptedException {

		Thread.sleep(30000);
	//	ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
		// Prasen 10.10.13
		datasource = new DAOMusicData(this);// DAO class object for CRUD
											// operations
		datasource.open(); // Open the database

		//MusicMetadata comment;

		// Till here
		// Added Prasen 8.10.13
		metaRetriver = new MediaMetadataRetriever(); // To store the path to
														// fetch the metadata
														// information
		// Till here
		SongsProvider plm = new SongsProvider();
		// get all songs from sdcard. If there no directory contains
		// musing-->throw exception
		try {

			songsList = plm.getPlayList();

			// looping through playlist

			Log.i("no. of songs = ", "no. of songs = " + songsList.size());

			for (int i = 0; i < songsList.size(); i++) {
				// creating new HashMap
				HashMap<String, String> song = songsList.get(i);

				Log.d("song", "" + song.toString());

				Log.d("de", " = " + i);
				for (Map.Entry<String, String> e : song.entrySet()) {
					// do something with the entry

					System.out.println(e.getKey() + " - " + e.getValue());
					// the getters are typed:
					String key = e.getKey();
					String value = e.getValue();
					Log.d("Check here", " CHeck key= " + key + " and value = " + value );
					// Prasen 8.10.13
					// Working Set from here
					try {
						if (key == "songPath") {
							path = value;

						}
						if(key == "songTitle")
						{
							Title = value;
						}
					} catch (Exception e3) {
						Log.d("Error",
								"Error inside songPath assignment, inside MainActivity.java");
					}

				}

				try {
					metaRetriver.setDataSource(path); // To set the path for
														// fetching the metadata
														// of the song
				} catch (Exception e2) {
					Log.d("ERROR inside mainactivity",
							"Inside path assignment in MainActivity.java");
				}
				// Id=metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
				try {
					album = metaRetriver
							.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM); // Get
																							// the
																							// album
																							// name
																							// of
																							// the
																							// song
					artist = metaRetriver
							.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST); // Get
																							// the
																							// artist
																							// name
																							// of
																							// the
																							// song
					duration = metaRetriver
							.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // Get
																							// the
																							// total
																							// length
																							// of
																							// the
																							// song
					genre = metaRetriver
							.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE); // Get
																							// the
																							// genre
																						// of
																							// the
																							// song
					// Prasen 10.10.13
					System.out.println("hi, just to debug");
					datasource.createComment(Title,album, artist, genre,duration,0); // To store the data in the database //
										// will shift to another function
				} catch (Exception e) {

					Log.d("ERROR", " Inside catch for insert statement here");

					e.printStackTrace();
				}
				Log.d("de", "before song added to list");

			}

		} catch (RuntimeException e) {
			e.printStackTrace();
			datasource.close();
		}
		datasource.close(); // 27.10.13
	}

	// Till Here
}
