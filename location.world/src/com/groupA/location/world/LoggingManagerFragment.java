package com.groupA.location.world;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

public class LoggingManagerFragment extends Fragment {
	
	private Handler mHandler;
    private LoggingClient mLoggingClient;
    
    	    
    private int mLoggingInterval = 10000; // Time span between position updates -- currently 10 seconds
    
    private Boolean isLoggedOn = false;
    private Boolean isLoggingActive = false;
    private Boolean locationAvailable = false;
    
    public interface LoggingManagerListener {
		public void onRequestPosition();
	}
    
    LoggingManagerListener mListener; 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        mHandler = new Handler();
    	if (savedInstanceState == null)
    		mLoggingClient = new LoggingClient();
    	else {
   	      	isLoggingActive = savedInstanceState.getBoolean("loggingActive");
   	        isLoggedOn = savedInstanceState.getBoolean("loggedOn");
   	        locationAvailable = savedInstanceState.getBoolean("locationAvailable");
    		mLoggingClient = savedInstanceState.getParcelable("loggingClient");
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loggingActive", isLoggingActive);
        outState.putBoolean("loggedOn", isLoggedOn);
        outState.putBoolean("locationAvailable", locationAvailable);
        outState.putParcelable("loggingClient", mLoggingClient);
    }
    
    public int logIn(String name, String password){
    	int result = mLoggingClient.logIn(name, password);
    	if (result == LoggingClient.LOGIN_OK) {
			isLoggedOn = true;
			startLoggingIfPossible();
    	}
    	return result;
    }
    
    public int logOut(){
    	int result =  mLoggingClient.logOut(); 
    	if (result == LoggingClient.LOGOUT_OK) {
    		isLoggingActive = false;
        	isLoggedOn = false; 
    		stopLoggingPos();
    	}
    	return result;
    }
    
    public void receiveLoc(Location location) {
    	mLoggingClient.logCoords(getActivity(), location);
    }
    

    Runnable mPositionLogger = new Runnable() {
        @Override 
        public void run() {
        	if (locationAvailable)
        		mListener.onRequestPosition(); 
          mHandler.postDelayed(mPositionLogger, mLoggingInterval);
        }
      };
      
      void startLoggingPos() {
    	 mPositionLogger.run(); 
      }

      void stopLoggingPos() {
    	   mHandler.removeCallbacks(mPositionLogger);
      }
      
      @Override
	    public void onPause() {
	    	super.onPause();
	        if (isLoggingActive)
	        	stopLoggingPos();
	    }
      
      public void locationIsAvailable() {
    	  locationAvailable = true; 
    	  startLoggingIfPossible();
      }
      
      public void locationIsUnavailable() {
    	  locationAvailable = false; 
    	  isLoggingActive = false;
    	  stopLoggingPos();
      }
      
      private void startLoggingIfPossible(){ 
    	  if (locationAvailable && isLoggedOn) {
    		  isLoggingActive = true; 
    		  startLoggingPos();
    	  }
      }
   // Override the Fragment.onAttach() method to instantiate the listener
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
}
