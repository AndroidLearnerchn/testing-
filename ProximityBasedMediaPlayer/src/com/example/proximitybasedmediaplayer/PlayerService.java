/*****************************************************************
 * Copyright (c) 2013 by CDAC Chennai 
 * @File        MusicMetaData
 * @Created:    08.10.2013
 * @author:     Prasenjit
 * Last Change: 20.12.2013 by Prasenjit
 ******************************************************************/
/***************************************************************
 * 
 * 1. Added method to play pause song on proximty sensor
 * 2. Added method to increase the player volume
 */
package com.example.proximitybasedmediaplayer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.contextawareframework.GlobalVariable.GlobalVariable;
import com.contextawareframework.contextawarefunctions.ContextAwareFunction;
import com.contextawareframework.controller.SensorController;
import com.contextawareframework.dataAnalysis.AccelerometerDataAnalysis;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

//import android.media.MediaPlayer.TrackInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerService extends Service implements OnCompletionListener,
		OnClickListener, OnSeekBarChangeListener{
	
	private WeakReference<ImageButton> btnRepeat, btnShuffle;
	private WeakReference<ImageView> btnPlay, btnForward, btnBackward, btnNext,
			btnPrevious;
	private WeakReference<SeekBar> songProgressBar;
	private WeakReference<TextView> songTitleLabel;
	private WeakReference<TextView> songCurrentDurationLabel;
	private WeakReference<TextView> songTotalDurationLabel;
	public static MediaPlayer mp;
	private Handler progressBarHandler = new Handler();;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	// Added date 20.12.13
	static long startTime[] = {0,0}, endTime[] = {0,0};
	static long starttime, endtime;
	static int i = 0, j=0,k = 0;
	static long DIFF[] = {0,0};
	static boolean flag= false;
	private ContextAwareFunction caf;
	// Till here
	private ArrayList<HashMap<String, String>> songsListingSD = new ArrayList<HashMap<String, String>>();
	// Prasen 22.10.13
	private ArrayList<MusicMetadata> songListingRecent = new ArrayList<MusicMetadata>();
	
	public static int currentSongIndex = -1;
	// change Prasen 5.9.13
	private SensorManager mSensorManager;
	SensorController controller;
	private Sensor mSensor;
	//SensorEventListener accListener;
	//till here
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
//		String trackinfo_field1;
//		String trackinfo_field2;
//		TrackInfo[] trackinfo=mp.getTrackInfo();
//		for(int i=0; i<trackinfo.length;i++)
//					{
//						trackinfo_field1=trackinfo[0];
//					}
		
		mp.reset();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);//
		utils = new Utilities();
		songsListingSD = MainActivity.songsList;
		// prasen 22.10.13
		//songListingRecent = MainActivity.RecentSongsList;
		
		songCurrentDurationLabel = new WeakReference<TextView>(MainActivity.songCurrentDurationLabel);
		//9.12.13
		controller = new SensorController(getApplicationContext());
		GlobalVariable.SENSOR_ACCELEROMETER = true;
		GlobalVariable.SENSOR_PROXIMITY = true;
		controller.registerProximityService(proximityListener);
		
		controller.registerAccelerometerService(accListener);
		// Prasen 5.9.13
		
	//	mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
       // mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
       // mSensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL );
        //till here
		super.onCreate();

	}

	
	//------------------------------------My framework part---------------------------//
			
			SensorEventListener accListener = new SensorEventListener() {
				
				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub
					Log.d("Debug","accListener called");
					
					// Date 17.12.2013
					
					AccelerometerDataAnalysis acceldata = new AccelerometerDataAnalysis(getApplicationContext());
					float val[] = acceldata.AccelDataAnalysis(event.values[0], event.values[1], event.values[2] );
					caf = new ContextAwareFunction(getApplicationContext());

					if(val[3] > 10)
					{
						
					    //Log.d("Play","Play Next");
						//caf.volumeIncrease();
						if (currentSongIndex < (songsListingSD.size() - 1)) {
							playSong(currentSongIndex + 1);
							currentSongIndex = currentSongIndex + 1;
						} else {
							// play first song
							playSong(0);
							currentSongIndex = 0;
						}
					    //caf.playNextSong();
					    //caf.next1();
						Log.d("Next","Next");
					}
					if(val[3]< -10)
					{
						//caf.volumeDecrease();
						//caf.playPrevSong();
						//caf.previous1();
						if (currentSongIndex < (songsListingSD.size() - 1)) {
							playSong(currentSongIndex + 1);
							currentSongIndex = currentSongIndex - 1;
						} else {
							// play first song
							playSong(0);
							currentSongIndex = 0;
						}
						Log.d("Previous","Previous");
					}
					if(val[4] > 10)
					{
						caf.volumeIncrease();
						Log.d("Increase","Volume Increase");
					}
					if(val[4] < -10)
					{
						//Log.d("Debug","val less val[4]=" + val[4]);
						caf.volumeDecrease();
						//caf.pauseSong();
						//caf.pause1(); 
						Log.d("Decrease Vol","Volume Decrease");
					}
					if(val[5] > 10)
					{
						if (mp != null) {
							mp.start();
							// Changing button image to pause button
							btnPlay.get().setImageResource(
									R.drawable.ic_media_pause);
							Log.d("Player Service", "Play");
						}
					}
					if(val[5] < -8)
					{
						if (currentSongIndex != -1) {
							if (mp.isPlaying()) {
								if (mp != null) {
									mp.pause();
									// Changing button image to play button
									btnPlay.get().setImageResource(R.drawable.ic_media_play);
									Log.d("Player Service", "Pause");

								}
							}
						}
					}
					
					
				}
				
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
					
				}
			};
			
			SensorEventListener proximityListener = new SensorEventListener(){

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSensorChanged(SensorEvent event) 
				{
					// TODO Auto-generated method stub
					
						
					try{
						caf = new ContextAwareFunction(getApplicationContext());
						
						if(i>1)
							i=0;
						if(j>1)
							j=0;
						if(k>1)
							k=0;
						if(event.values[0] > 1 )
						{
							
							startTime[i] = System.currentTimeMillis();
							
							DIFF[k] =  Math.abs( startTime[i] - endTime[j] );
							
							long f1,f2;
							f1 = Math.abs(DIFF[0]-DIFF[1]);
							f2 = DIFF[k];
							
							Log.d("Debug","DIFF Val= " + DIFF[0] +" " + DIFF[1]);
							Log.d("Debug","f1 = " + f1 + " DIFF["+ k +"] = " +f2);
							if(f1 > 0 && f1 < 250)
							{
								Log.d("Debug","Play Random Song");
								
								Random rand = new Random();
								//Play random Song
								try{
								currentSongIndex = rand.nextInt((songsListingSD.size() - 1) - 0 + 1) + 0;
								}
								catch(Exception e)
								{
									e.printStackTrace();
									
								}
								try{
								playSong(currentSongIndex);
									Log.d("Test","Test");
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
								//caf.volumeIncrease();
								
							}
							
							if(DIFF[k] > 3000  && DIFF[k] < 5000)
							{   
								Log.d("Debug","Pause / Play Song");
								if(!flag)
								{
									Log.d("Debug","Pause Song");
									// Pause Song
									if (currentSongIndex != -1) {
										if (mp.isPlaying()) {
											if (mp != null) {
												mp.pause();
												// Changing button image to play button
												btnPlay.get().setImageResource(R.drawable.ic_media_play);
												Log.d("Player Service", "Pause");

											}
										}
									}
									flag=true;
									
								}
								else
								{
									flag = false;
									//Play Song
									if (mp != null) {
										mp.start();
										// Changing button image to pause button
										btnPlay.get().setImageResource(
												R.drawable.ic_media_pause);
										Log.d("Player Service", "Play");
									}
									Log.d("Debug","Play Song");
								}
							}
							i++;
							k++;
							
							
							
						}
						else
						{   
							endTime[j] = System.currentTimeMillis();
							j++;
							
						}
						i++;
					}


					catch(Exception e)
					{
							e.printStackTrace();
					}
				}
			};
			
	// --------------onStartCommand-------------------------------------------------------------------------//
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initUI();
		try{
		int songIndex = intent.getIntExtra("songIndex", 0);
		Log.d("songIndex " ," val = " + songIndex);
		Log.d("currentSongIndex " ," val = " + currentSongIndex);
		if (songIndex != currentSongIndex) {
			playSong(songIndex);
			initNotification(songIndex);
			currentSongIndex = songIndex;
		} else if (currentSongIndex != -1) {
			songTitleLabel.get().setText(
					songsListingSD.get(currentSongIndex).get("songTitle"));
			if (mp.isPlaying())
				btnPlay.get().setImageResource(R.drawable.ic_media_pause);
			else
				btnPlay.get().setImageResource(R.drawable.ic_media_play);
		}

		super.onStart(intent, startId);
		}catch(Exception e)
		{e.printStackTrace();}
		return START_STICKY;
	}

	/**
 * 	
 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Init UI
	 */
	private void initUI() {
		try{
		songTitleLabel = new WeakReference<TextView>(MainActivity.songTitle);
		
		songCurrentDurationLabel = new WeakReference<TextView>(
				MainActivity.songCurrentDurationLabel);
		songTotalDurationLabel = new WeakReference<TextView>(
				MainActivity.songTotalDurationLabel);

		btnPlay = new WeakReference<ImageView>(MainActivity.btnPlay);
		if(btnPlay!= null)
		{
			Log.d("Debug", "btnPlay not null11");
		}
		btnForward = new WeakReference<ImageView>(MainActivity.btnForward);
		btnBackward = new WeakReference<ImageView>(MainActivity.btnBackward);
		btnNext = new WeakReference<ImageView>(MainActivity.btnNext);
		btnPrevious = new WeakReference<ImageView>(MainActivity.btnPrevious);
		btnRepeat = new WeakReference<ImageButton>(MainActivity.btnRepeat);
		btnShuffle = new WeakReference<ImageButton>(MainActivity.btnShuffle);
		
		btnPlay.get().setOnClickListener(this);
		btnForward.get().setOnClickListener(this);
		btnBackward.get().setOnClickListener(this);
		btnNext.get().setOnClickListener(this);
		btnPrevious.get().setOnClickListener(this);
		btnRepeat.get().setOnClickListener(this);
		btnShuffle.get().setOnClickListener(this);
		// TODO Auto-generated method stub

		songProgressBar = new WeakReference<SeekBar>(
				MainActivity.songProgressBar);
		songProgressBar.get().setOnSeekBarChangeListener(this);
		}
		catch(Exception e){
			Log.d("ERROR","Inside initComp Service Class");
			e.printStackTrace();}
	}
	//Prasen 11.10.13
	private void disableInitUI() {
		songTitleLabel = new WeakReference<TextView>(MainActivity.songTitle);
		songCurrentDurationLabel = new WeakReference<TextView>(
				MainActivity.songCurrentDurationLabel);
		songTotalDurationLabel = new WeakReference<TextView>(
				MainActivity.songTotalDurationLabel);

		btnPlay = new WeakReference<ImageView>(MainActivity.btnPlay);
		btnForward = new WeakReference<ImageView>(MainActivity.btnForward);
		btnBackward = new WeakReference<ImageView>(MainActivity.btnBackward);
		btnNext = new WeakReference<ImageView>(MainActivity.btnNext);
		btnPrevious = new WeakReference<ImageView>(MainActivity.btnPrevious);
		btnRepeat = new WeakReference<ImageButton>(MainActivity.btnRepeat);
		btnShuffle = new WeakReference<ImageButton>(MainActivity.btnShuffle);
		
		btnPlay.get().setOnClickListener(this);
		btnForward.get().setOnClickListener(this);
		btnBackward.get().setOnClickListener(this);
		btnNext.get().setOnClickListener(this);
		btnPrevious.get().setOnClickListener(this);
		btnRepeat.get().setOnClickListener(this);
		btnShuffle.get().setOnClickListener(this);
		// TODO Auto-generated method stub

		songProgressBar = new WeakReference<SeekBar>(
				MainActivity.songProgressBar);
		songProgressBar.get().setOnSeekBarChangeListener(this);
	}
//Till here
	// -------------------------------------------------------------------------//
	public void onClick(View v) {
		try{
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_play_imageview:
			if (currentSongIndex != -1) {
				if (mp.isPlaying()) {
					if (mp != null) {
						mp.pause();
						// Changing button image to play button
						btnPlay.get()
								.setImageResource(R.drawable.ic_media_play);
						Log.d("Player Service", "Pause");

					}
				} else {
					// Resume song
					if (mp != null) {
						mp.start();
						// Changing button image to pause button
						btnPlay.get().setImageResource(
								R.drawable.ic_media_pause);
						Log.d("Player Service", "Play");
					}
				}
			}
			break;

		case R.id.btn_next_imageview:
			// check if next song is there or not
			Log.d("Player Service", "Next");
			if (currentSongIndex < (songsListingSD.size() - 1)) {
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			} else {
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
			break;

		case R.id.btn_forward_imageview:

			// get current song position
			int currentPosition = mp.getCurrentPosition();
			// check if seekForward time is lesser than song duration
			if (currentPosition + seekForwardTime <= mp.getDuration()) {
				// forward song
				mp.seekTo(currentPosition + seekForwardTime);
			} else {
				// forward to end position
				mp.seekTo(mp.getDuration());
			}
			break;

		case R.id.btn_backward_imagview:
			// get current song position
			int currentPosition2 = mp.getCurrentPosition();
			// check if seekBackward time is greater than 0 sec
			if (currentPosition2 - seekBackwardTime >= 0) {
				// forward song
				mp.seekTo(currentPosition2 - seekBackwardTime);
			} else {
				// backward to starting position
				mp.seekTo(0);
			}
			break;

		case R.id.btn_previous_imageview:

			if (currentSongIndex > 0) {
				playSong(currentSongIndex - 1);
				currentSongIndex = currentSongIndex - 1;
			} else {
				// play last song
				playSong(songsListingSD.size() - 1);
				currentSongIndex = songsListingSD.size() - 1;
			}
			break;

		case R.id.btnRepeat:

			if (isRepeat) {
				isRepeat = false;
				Toast.makeText(getApplicationContext(), "Repeat is OFF",
						Toast.LENGTH_SHORT).show();
				btnRepeat.get().setImageResource(R.drawable.btn_repeat);
			} else {
				// make repeat to true
				isRepeat = true;
				Toast.makeText(getApplicationContext(), "Repeat is ON",
						Toast.LENGTH_SHORT).show();
				// make shuffle to false
				isShuffle = false;
				btnRepeat.get().setImageResource(R.drawable.btn_repeat_focused);
				btnShuffle.get().setImageResource(R.drawable.btn_shuffle);
			}
			break;

		case R.id.btnShuffle:

			if (isShuffle) {
				isShuffle = false;
				Toast.makeText(getApplicationContext(), "Shuffle is OFF",
						Toast.LENGTH_SHORT).show();
				btnShuffle.get().setImageResource(R.drawable.btn_shuffle);
			} else {
				// make repeat to true
				isShuffle = true;
				Toast.makeText(getApplicationContext(), "Shuffle is ON",
						Toast.LENGTH_SHORT).show();
				// make shuffle to false
				isRepeat = false;
				btnShuffle.get().setImageResource(
						R.drawable.btn_shuffle_focused);
				btnRepeat.get().setImageResource(R.drawable.btn_repeat);
			}
			break;
		}
		}
		catch(Exception e){
			Log.d("ERROR","In click event in Playerservice class");
		}
	}

	// -------------------------------------------------------------//

	/**
	 * @author www.9android.net
	 * @param songIndex: index of song
	 * 
	 */
	public void playSong(int songIndex) {
		// Play song
		try {
			//Added here 29.10.13
			//initUI();
			
			mp.reset();
			mp.setDataSource(songsListingSD.get(songIndex).get("songPath"));
			mp.prepare();
			mp.start();
			// Displaying Song title
			String songTitle = songsListingSD.get(songIndex).get("songTitle");
			if(songTitle != null)
			{
			songTitleLabel.get().setText(songTitle);
			}
			// Changing Button Image to pause image
			if(btnPlay!=null)
			{	
			btnPlay.get().setImageResource(R.drawable.ic_media_pause);
			}
			// set Progress bar values
			songProgressBar.get().setProgress(0);
			songProgressBar.get().setMax(100);
			// Updating progress bar
			updateProgressBar();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(Exception e)
		{e.printStackTrace();}
	}

	// ----------------onSeekBar Change Listener------------------------------//
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		progressBarHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = 0;
			try {
				totalDuration = mp.getDuration();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			long currentDuration = 0;
			try {
				currentDuration = mp.getCurrentPosition();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			// Displaying Total Duration time
			songTotalDurationLabel.get().setText(
					"" + utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			songCurrentDurationLabel.get().setText(
					"" + utils.milliSecondsToTimer(currentDuration));

			// Updating progress bar
			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			// Log.d("Progress", ""+progress);
			songProgressBar.get().setProgress(progress);
			// Running this thread after 100 milliseconds
			progressBarHandler.postDelayed(this, 100);
			// Log.d("AndroidBuildingMusicPlayerActivity","Runable  progressbar");
		}
	};

	// ----------------on Seekbar Change
	// Listener---------------------------------------//
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {
	}

	/**
	 * When user starts moving the progress handler
	 * */
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		progressBarHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	public void onStopTrackingTouch(SeekBar seekBar) {
		progressBarHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(),
				totalDuration);

		// forward or backward to certain seconds
		mp.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
	}

	/**
	 * On Song Playing completed if repeat is ON play same song again if shuffle
	 * is ON play random song
	 * */
	public void onCompletion(MediaPlayer arg0) {

		// check for repeat is ON or OFF
		if (isRepeat) {
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if (isShuffle) {
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand
					.nextInt((songsListingSD.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else {
			// no repeat or shuffle ON - play next song
			if (currentSongIndex < (songsListingSD.size() - 1)) {
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			} else {
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}
	
	// ---------------------------------------------------------//
	@Override
	public void onDestroy() {
		super.onDestroy();
		//mSensorManager.unregisterListener(this);
		controller.unregisterAccelerometerService(accListener);
		controller.unregisterProximityService(proximityListener);
		GlobalVariable.SENSOR_ACCELEROMETER = false;
		GlobalVariable.SENSOR_PROXIMITY = false;
		currentSongIndex = -1;
		// Remove progress bar update Hanlder callBacks
		progressBarHandler.removeCallbacks(mUpdateTimeTask);
		Log.d("Player Service", "Player Service Stopped");
		if (mp != null) {
			if (mp.isPlaying()) {
				mp.stop();
			}
			mp.release();
		}

	}
	// Added 17.12.13
	public void stopService()
	{
		super.stopSelf();
		controller.unregisterAccelerometerService(accListener);
		controller.unregisterProximityService(proximityListener);
		GlobalVariable.SENSOR_ACCELEROMETER = false;
		GlobalVariable.SENSOR_PROXIMITY = false;
		Log.d("Debug","Stop Service called");
		
		
	}

	// --------------------Push Notification
	// Set up the notification ID
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;

	// Create Notification
	private void initNotification(int songIndex) {
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.ic_media_play;
		CharSequence tickerText = "Proximity Based Audio Player";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		Context context = getApplicationContext();
		CharSequence songName = songsListingSD.get(songIndex).get("songTitle");

		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, songName, null, contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
	
//Prasen
	// This won't work until you create a IBinder.. I guess.
//	public void moveNextSong() {
//		// TODO Auto-generated method stub
//		Log.d("Player Service", "Next");
//		if (currentSongIndex < (songsListingSD.size() - 1)) {
//			playSong(currentSongIndex + 1);
//			currentSongIndex = currentSongIndex + 1;
//		} else {
//			// play first song
//			playSong(0);
//			currentSongIndex = 0;
//		
//		}
//		
//	}

	//@Override
	//public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		//Prasen
		// Not getting called, will look other day
//		Log.d("Player Service", "service called for sensor event, Next called");
//		Random rand = new Random();
//		currentSongIndex = rand
//				.nextInt((songsListingSD.size() - 1) - 0 + 1) + 0;
//		playSong(currentSongIndex);
	//	if(sensor.getType() == mSensor.TYPE_PROXIMITY)
		//Log.i("check this","CHeck if this method is getting called");
		
	//}

	//@Override
	//public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		//Log.d("Player Service", "service called for sensor event, Next called1");
// Prasen 5.9.13 U can use this to forward to next song on wave. It will advance the index by 2 as the same method is called twice [near,far]
//		if (currentSongIndex < (songsListingSD.size() - 1)) {
//			playSong(currentSongIndex + 1);
//			currentSongIndex = currentSongIndex + 1;
//		} else {
//			// play first song
//			playSong(0);
//			currentSongIndex = 0;
//		
//		}
		//till here
		
		// Another logic to get a random song
		//Log.i("Player Service", "service called for sensor event, Next called2");
		
//			Log.d("DEBUG","value of event = " +event);
//			Integer t1= event.sensor.getType();
//			Log.d("DEBUG","value of event = " +t1);
//		    float power = mSensor.getPower();
//		
//		Log.i("Power","power = " + power);
//		
//		if( t1.MAX_VALUE == 2147483647 )
//		{
//			Log.d("Debug",t1.MAX_VALUE +" and " + t1.MIN_VALUE );
//			Log.d("Debug","Here");
//		Random rand = new Random();
//		currentSongIndex = rand
//				.nextInt((songsListingSD.size() - 1) - 0 + 1) + 0;
//		try{
//		playSong(currentSongIndex);
//		}
//		catch(Exception e){e.printStackTrace();}
//		}
		//Till here
		// 17.10.13
//		if( t1.MAX_VALUE == 2147483647 ){
//			
//			Log.d("Debug","Here inside song change");
//		if (currentSongIndex < (songsListingSD.size() - 1)) {
//			playSong(currentSongIndex + 1);
//			currentSongIndex = currentSongIndex + 1;
//		} else {
//			// play first song
//			playSong(0);
//			currentSongIndex = 0;
//		
//		}
//		}
		//till here 17.10.13
	
		
	}

