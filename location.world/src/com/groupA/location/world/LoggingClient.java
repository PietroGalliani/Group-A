package com.groupA.location.world;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

public class LoggingClient {
	
	/** Just some constants for returning errors and the like. 
	Perhaps using exceptions would be better, but let's get this to work first.**/
	public static final int LOGIN_OK = 1; 
	public static final int LOGIN_FAIL = 0; 
	public static final int ALREADY_LOGGED = -1;
	public static final int LOGOUT_OK = 1; 
	public static final int LOGOUT_FAIL = 0;
	public static final int SENTDATA_OK = 1; 
	public static final int SENTDATA_FAIL = 0;
	
	
	/**Client ID, logged on -- logged off**/
	private String mClientID;
	private Boolean logged;
	
	public LoggingClient() {
		logged = false;
	}
	
	/**Login and Logout -- for now, they just accept anything**/
	public int logIn(String name, String password){
		//Toast.makeText(mContext, mClientID + "connected!", Toast.LENGTH_SHORT).show();
		if (logged == false) { 
			logged = true;
			mClientID = name;
			return LOGIN_OK;
		}
		else 
			return ALREADY_LOGGED; 
	}
	
	public int logOut(){
		//Toast.makeText(mContext, mClientID + "disconnected!", Toast.LENGTH_SHORT).show();
		logged = false;
		mClientID = "";
		return LOGOUT_OK;
	}
	
	// The Context is just for sending the message to the screen
	// Once the rest works, we should remove it and pass the message to MainActivity instead.
	public int logCoords(Context mContext, Location mLocation){
		if (logged) {
			Toast.makeText(mContext, mClientID + " has logged position (" + mLocation.getLatitude() + ", " + mLocation.getLongitude() + ")", Toast.LENGTH_SHORT).show();
			return SENTDATA_OK;
		}
		else {
			Toast.makeText(mContext, "Log in first!", Toast.LENGTH_SHORT).show();
			return SENTDATA_FAIL;
		}
	}
}
