/*****************************************************************
 * Copyright (c) 2013 by CDAC Chennai 
 * @File        MetadataAdapter_U_May_Also_Like
 * @Created:    22.10.2013
 * @author:     Prasenjit
 * Last Change: 22.10.2013 by Prasenjit
 ******************************************************************/
package com.example.proximitybasedmediaplayer;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MetadataAdapter_U_May_Also_Like extends ArrayAdapter<MusicMetadata> {

	// declaring our ArrayList of items
	private ArrayList<MusicMetadata> objects;
	    LayoutInflater inflater ;
	    
    public MetadataAdapter_U_May_Also_Like(Context context, int textViewResourceId, ArrayList<MusicMetadata> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        inflater = LayoutInflater.from(context);
    }

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	public View getView(int position, View convertView, ViewGroup parent){

		// assign the view we are converting to a local variable
		//View v = convertView;
		 ViewHolder holder;
			try
			{
			if (convertView == null) {
				
				//Log.d("Debug1","inside getview");
	            convertView = inflater.inflate(R.layout.umayalsolike_listsong_item, null);
	           
	            //Log.d("Debug2","inside getview");
	            holder = new ViewHolder();
	            
	            //Log.d("Debug3","inside getview");
	            holder.tv= (TextView) convertView.findViewById(R.id.songTitleumayalsolike);
	           
	            //Log.d("Debug4","inside getview");
	            convertView.setTag(holder); 
	           
	            //Log.d("Debug5","inside getview");
	        } 
			else
			{ 
	        	//Log.d("Debug6","inside getview");
	            holder = (ViewHolder) convertView.getTag();
	           
	            //Log.d("Debug7","inside getview");
	        } 
			MusicMetadata i = objects.get(position);
	       // Data i = objects.get(position);
			Log.d("Debug", "i inside Adapter Class = " +i.getTitle());
	        if (i != null) 
	        {
	        //	Log.d("Debug8","inside getview");
	         holder.tv.setText("title : "+i.getTitle());
	         //Log.d("Debug9","inside getview");
	        }
	        } //try
			catch(Exception e){
				Log.d("Error","inside getview");
				e.printStackTrace();}
		//}
	        return convertView;
		

		// the view must be returned to our activity
		//return v;
	}
	static class ViewHolder
    {
        TextView tv;
    }
}
