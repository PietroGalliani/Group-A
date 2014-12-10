package com.groupA.location.world;
import com.groupA.location.world.LoginActivity.LoginListener;
import com.groupA.location.world.LogoutActivity.LogoutListener;
import com.groupA.location.world.UpdateLocationActivity.LocListener;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;
/*Implements Parcelable in order to be able to save and restore its state if necessary (e.g. if the tablet is rotated)*/
/**
*
* Low-level login, logout and message-sending functionalities.
* Until we implement the server, this is mostly a "dummy" client:
* instead of sending messages to the server, it just displays them
* on screen.
*
* If you add more variables to it, remember to update the definitions of
* LoggingClient(Parcel in) and writeToParcel(Parcel out), or their values might
* be forgotten when you rotate your device or pause-unpause your app!
*
*/
public class LoggingClient implements Parcelable, LoginListener, LogoutListener, LocListener {
public interface ClientListener {
void loginSucceeded(String userID, String groupID);
void loginFailed(String userID, String message);
void logoutSucceeded(); 
void logoutFailed(String message);
void sendLocationFailed(String message);
}
ClientListener mListener;
/* Just some constants for returning errors and the like.
Perhaps using exceptions would be better, but let's get this to work first.*/
public static final int LOGIN_OK = 1;
public static final int LOGIN_FAIL = 0;
public static final int ALREADY_LOGGED = -1;
public static final int WRONG_PASSWD = -2;
public static final int LOGOUT_OK = 1;
public static final int LOGOUT_FAIL = 0;
public static final int SENTDATA_OK = 1;
public static final int SENTDATA_FAIL = 0;
/**ID of the (connected) user**/
private String mUserID;
/**true if logged on; otherwise false*/
private Boolean logged;
/**
* Constructor for initializing the client (for now, it only tells it that it's not already logged in)
*/
public LoggingClient(ClientListener listener){
logged = false;
mListener = listener;
}
/** Constructor for restoring the client from a parcel, if necessary */
private LoggingClient(Parcel in) {
mUserID = in.readString();
logged = (in.readInt() == 1);
}
/** Save the data of the client before pausing/rotationg **/
public void writeToParcel(Parcel out, int flags) {
out.writeString(mUserID);
out.writeInt(logged?1:0);
}
/**
* Attempt to log in, returns the outcome of the attempt. For now, tests just a few arbitrary cases instead of
* contacting the server.
*
* @return the outcome of the login attempt
* **/
public void logIn(String name, String password){
new LoginActivity((LoginListener)this).execute(name,password);
}
/**
* Logs out.
* @retu
* rn the outcome of the logout attempt
*/
public void logOut(){
	new LogoutActivity((LogoutListener)this).execute(mUserID);
/*logged = false;
mUserID = "";
return LOGOUT_OK;*/
}
/**
*
* @return true if the client is logged in, false otherwise
*/
public Boolean is_logged() {
return logged;
}
/**
*
* @return the ID of the user currently logged in (or the empty string if no user is logged in)
*/
public String getUserID(){
return mUserID;
}
// The Context is just for sending the message to the screen
// Once the rest works, we should remove it and pass the message to MainActivity instead.
/**
* Logs up the location to the server (or will do so once we have a server, for now it just displays them
* on screen)
* @param mContext -- the context used for displaying messages on screen (eventually we will remove it)
* @param mLocation -- the location to log
* @return the outcome of the message sending attempt
*/
public int logCoords(Context mContext, Location mLocation){
if (logged) {
	new UpdateLocationActivity((LocListener)this, mUserID).execute(mLocation.getLongitude(), mLocation.getLatitude());
    //Toast.makeText(mContext, mUserID + " has logged position (" + mLocation.getLatitude() + ", " + mLocation.getLongitude() + ")", Toast.LENGTH_SHORT).show();
    return SENTDATA_OK;
}
else {
Toast.makeText(mContext, "Log in first!", Toast.LENGTH_SHORT).show();
return SENTDATA_FAIL;
}
}
/* Stuff for implementing the Parcelable interface, which we use for saving and restoring the client.
* Leave these three declarations alone unless you know what you are doing.
*/
public int describeContents() {
return 0;
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
@Override
public void loginSucceeded(String userID, String groupID) {
Log.d("debug", userID);
Log.d("debug",mListener.toString());
this.mUserID = userID;
this.logged = true;
mListener.loginSucceeded(userID, groupID);
}
@Override
public void loginFailed(String userID, String message) {
mListener.loginFailed(userID, message);
this.logged = false;
}
@Override
public void logoutSucceeded() {
	this.logged = false; 
	this.mUserID = "";
	mListener.logoutSucceeded();	
}
@Override
public void logoutFailed(String message) {
	mListener.logoutFailed(message);	
}
@Override
public void sendLocationFailed(String message) {
	mListener.sendLocationFailed(message);
}
}