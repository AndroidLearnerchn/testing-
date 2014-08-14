/*****************************************************************
 * Copyright (c) 2013 by CDAC Chennai 
 * @File        MusicMetaData
 * @Created:    08.10.2013
 * @author:     Prasenjit
 * Last Change: 09.10.2013 by Prasenjit
 ******************************************************************/
package com.example.proximitybasedmediaplayer;

public class MusicMetadata {
	//Meta Data Members of a song 
	private Integer Id;
	private Integer counter;
	private String	artist;
	private String track;
	private String album;
	private Integer tracknumber;
	private String duration;
	
	// Added on 24.10.13 prasen 
	private String Title;
	
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public MusicMetadata()
	{
		
	}
	public MusicMetadata(String Album)
	{
		this.album =  Album;
	}
	// Public Setters and Getters
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	private String genre;
	// Public Setters and getters methods for all Data Menbers
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public Integer getTracknumber() {
		return tracknumber;
	}
	public void setTracknumber(Integer tracknumber) {
		this.tracknumber = tracknumber;
	}
	public Integer getCounter() {
		return counter;
	}
	public void setCounter(Integer counter) {
		this.counter = counter;
	}
	
}
