package com.groupA.location.world;
import org.json.JSONArray;
import org.json.JSONObject;

import com.groupA.location.world.ChangeGroupActivity.ChangeGroupListener;
import com.groupA.location.world.DownloadOthersActivity.DownloadOthersListener;
import com.groupA.location.world.LoggingClient.ClientListener;
import com.groupA.location.world.LoginActivity.LoginListener;
import com.groupA.location.world.RegisterActivity.RegisterListener;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
/**
*
* Deals with logging-related functions. Also contains an instance of the client, to deal with
* low-level login, logout and message-passing functionalities.
*
*/
public class LoggingManagerFragment extends Fragment implements ClientListener, RegisterListener, DownloadOthersListener,
ChangeGroupListener{
/**
* The activity that deals with the manager -- that is, MainActivity -- must
* implement the following functions:
*/
public interface LoggingManagerListener {
/**
* The logging manager wants a new position to send to the server
*/
public void onRequestPosition();
void onLoginSucceeded(String userID, String groupID);
void onLoginFailed(String userID, String message);
void onRegOK(String userID, String group);
void onRegFailed(String userID, String message);
void onLogoutSucceded();
void onLogoutFailed(String message);
void onSendLocationFailed(String message);
void onDownloadedOthers(JSONArray json);
void onDownloadedMessages(JSONArray json);

public void onDownloadFailure(String message);
public void onChangeGroupSucceeded(String group);
public void onChangeGroupFailed(String message);
}
/**
* An instance of the interface, to call onRequestPosition() when necessary.
*/
LoggingManagerListener mListener;
/**
* Handler for sending periodic location requests to the main activity
*/
private Handler mHandler;
/**
* Logging client for low-level client functionalities
*/
private LoggingClient mLoggingClient;
/**
* Time span between log updates, in milliseconds
*/
private int mLoggingInterval = 10000;
/**
* True if we are logging our position periodically, false otherwise
*/
private Boolean isLoggingActive = false;
/**
* True if the location manager can find the current position, false otherwise
*/
private Boolean locationAvailable = false;
/**
* This gets called when the logging manager is created
*/
@Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
mHandler = new Handler();
if (savedInstanceState == null) //The logging manager is being created for the first time
mLoggingClient = new LoggingClient((ClientListener)this);
else {
//We are restoring the logging manager after a rotation or a pause
isLoggingActive = savedInstanceState.getBoolean("loggingActive");
locationAvailable = savedInstanceState.getBoolean("locationAvailable");
mLoggingClient = savedInstanceState.getParcelable("loggingClient");
}
}
/**
* We are pausing the app, save our data
*/
@Override
public void onSaveInstanceState(Bundle outState) {
super.onSaveInstanceState(outState);
outState.putBoolean("loggingActive", isLoggingActive);
outState.putBoolean("locationAvailable", locationAvailable);
outState.putParcelable("loggingClient", mLoggingClient);
}
/**
* Attempt to log-in with given name and password. If you succeed, start
* logging your position. Return the login attempt outcome.
*
* @param name
* @param password
* @return login_attempt_result
*/
public void logIn(String name, String password){
mLoggingClient.logIn(name, password);
}
/**
* Log out and stop logging your position
* @return logout_attempt_result
*
*/
public void logOut(){
stopLoggingPos();
mLoggingClient.logOut();
/*if (result == LoggingClient.LOGOUT_OK) {
stopLoggingPos();
}
return result;*/
}
/**
* Take a location to log, send it to the logging client
*
* @param location
*/
public void receiveLoc(Location location) {
mLoggingClient.logCoords(getActivity(), location);
}
/**
* Set up an inner subclass to request positions periodically,
* make mPositionLogger into an instance of it
*/
Runnable mPositionLogger = new Runnable() {
/**
* When starting, do this:
*/
@Override
public void run() {
/* if you can find your current position, log it to the server */
if (locationAvailable)
mListener.onRequestPosition();
/* Wait mLoggingInterval milliseconds, then run mPositionLogger again */
mHandler.postDelayed(mPositionLogger, mLoggingInterval);
}
};
/**
* Start logging your positions periodically
*/
void startLoggingPos() {
isLoggingActive = true;
mPositionLogger.run();
}
/**
* Stop logging your positions
*/
void stopLoggingPos() {
isLoggingActive = false;
mHandler.removeCallbacks(mPositionLogger);
}
/**
* If the app is paused, stop logging your positions
* (but do not change isLoggingActive, so that we remember
* to start logging again when we start the application again)
*/
@Override
public void onPause() {
super.onPause();
if (isLoggingActive)
stopLoggingPos();
}
/**
* If the current location is available, start logging locations
* (if possible, that is, if we are logged in)
*/
public void locationIsAvailable() {
locationAvailable = true;
startLoggingIfPossible();
}
/**
* If the current location becomes unavailable, stop logging
* positions.
*/
public void locationIsUnavailable() {
locationAvailable = false;
isLoggingActive = false;
stopLoggingPos();
}
/**
* Start logging your positions, if you can
*/
private void startLoggingIfPossible(){
if (locationAvailable && mLoggingClient.is_logged()) {
isLoggingActive = true;
startLoggingPos();
}
}
/**
* This gets called when the class is instantiated. It checks that
* the main activity implements the interface LoggingManagerListener; and if
* so, it stores that implementation in mListener (to be able to invoke
* it if necessary).
*
* Otherwise, it raises an exception.
*/
@Override
public void onAttach(Activity activity) {
super.onAttach(activity);
// Verify that the host activity implements the callback interface
try {
// Instantiate the listener so we can send events to the host
mListener = (LoggingManagerListener) activity;
} catch (ClassCastException e) {
// The activity doesn't implement the interface, throw exception
throw new ClassCastException(activity.toString()
+ " must implement LoggingManagerListener");
}
}
@Override
public void loginSucceeded(String userID, String groupID) {
Log.d("debug","logmanager");
mListener.onLoginSucceeded(userID, groupID);
}
@Override
public void loginFailed (String userID, String message) {
mListener.onLoginFailed(userID, message);
}

public void register(String userID, String groupID, String password, int charSelected) {
	new RegisterActivity((RegisterListener)this, charSelected).execute(userID, groupID, password);
	//if (password.length() < 8)
	//  mListener.onRegFailed(userID, "The password must be at least eight characters long)"); 
  //else
  //mListener.onRegOK(charSelected, userID);
}
@Override
public void RegistrationSucceeded(String userID, String group) {
	mListener.onRegOK(userID, group);
}
@Override
public void RegistrationFailed(String userID, String message) {
	mListener.onRegFailed(userID, message);
}
@Override
public void logoutSucceeded() {
	stopLoggingPos();
	mListener.onLogoutSucceded();
}
@Override
public void logoutFailed(String message) {
	mListener.onLogoutFailed(message);
}
@Override
public void sendLocationFailed(String message) {
	mListener.onSendLocationFailed(message);
}

public void downloadOthers(){
	new DownloadOthersActivity((DownloadOthersListener)this).execute(mLoggingClient.getUserID()); 
}
@Override
public void downloadedOthers(JSONArray json) {
	mListener.onDownloadedOthers(json);
}
@Override
public void downloadFailure(String message) {
    mListener.onDownloadFailure(message);	
}
public void changeGroup(String newGroup) {
	new ChangeGroupActivity((ChangeGroupListener)this, mLoggingClient.getUserID(), newGroup).execute();
}
@Override
public void changeGroupSucceeded(String group) {
	mListener.onChangeGroupSucceeded(group);
}
@Override
public void changeGroupFailed(String message) {
	mListener.onChangeGroupFailed(message);
}
@Override
public void downloadedMessages(JSONArray json) {
	mListener.onDownloadedMessages(json);
}
public void sendMessage(String message) {
	new SendMessageActivity(mLoggingClient.getUserID(), message).execute();
}

}