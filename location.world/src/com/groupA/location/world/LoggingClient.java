package com.groupA.location.world;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

/*Implements Parcelable in order to be able to save and restore its state if necessary (e.g. if the tablet is rotated)*/

public class LoggingClient implements Parcelable{
	
	/** Just some constants for returning errors and the like. 
	Perhaps using exceptions would be better, but let's get this to work first.**/
	public static final int LOGIN_OK = 1; 
	public static final int LOGIN_FAIL = 0; 
	public static final int ALREADY_LOGGED = -1;
	public static final int WRONG_PASSWD = -2;
	
	public static final int LOGOUT_OK = 1; 
	public static final int LOGOUT_FAIL = 0;
	public static final int SENTDATA_OK = 1; 
	public static final int SENTDATA_FAIL = 0;
	
	
	/**Client ID, logged on -- logged off**/
	private String mUserID;
	private Boolean logged;
	
	public LoggingClient() {
		logged = false;
	}
	
	/**Login and Logout -- for now, they just accept anything**/
	public int logIn(String name, String password){
		//Toast.makeText(mContext, mClientID + "connected!", Toast.LENGTH_SHORT).show();
		if (logged == false) { 
			if ((name.equals("aaa") && password.equals("aaa")) || (name.equals("bbb") && password.equals("bbb")) || (password.equals("ccc"))) {
				logged = true;
				mUserID = name;
				return LOGIN_OK;
			} else
				return WRONG_PASSWD;
		}
		else 
			return ALREADY_LOGGED; 
	}
	
	public int logOut(){
		//Toast.makeText(mContext, mClientID + "disconnected!", Toast.LENGTH_SHORT).show();
		logged = false;
		mUserID = "";
		return LOGOUT_OK;
	}
	
	public Boolean is_logged() {
		return logged;
	}
	
	public String getUserID(){
		return mUserID;
	}
	
	// The Context is just for sending the message to the screen
	// Once the rest works, we should remove it and pass the message to MainActivity instead.
	public int logCoords(Context mContext, Location mLocation){
		if (logged) {
			Toast.makeText(mContext, mUserID + " has logged position (" + mLocation.getLatitude() + ", " + mLocation.getLongitude() + ")", Toast.LENGTH_SHORT).show();
			return SENTDATA_OK;
		}
		else {
			Toast.makeText(mContext, "Log in first!", Toast.LENGTH_SHORT).show();
			return SENTDATA_FAIL;
		}
	}
	
	
	/**Stuff for saving and reloading the client in case of rotation and so on**/
	
	//describeContents instance -- just to implement Parcelable, leave alone
	public int describeContents() {
        return 0;
    }
	
	/* save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mUserID); 
        out.writeInt(logged?1:0);
    }
    
    public static final Parcelable.Creator<LoggingClient> CREATOR
    = new Parcelable.Creator<LoggingClient>() {
public LoggingClient createFromParcel(Parcel in) {
    return new LoggingClient(in);
}

public LoggingClient[] newArray(int size) {
    return new LoggingClient[size];
}
};

/** recreate object from parcel */
private LoggingClient(Parcel in) {
	mUserID = in.readString();
	logged = (in.readInt() == 1);
}
	
}
