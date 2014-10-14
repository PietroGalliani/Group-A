package com.groupA.location.world;
//Modular branch -- let's see if this works


import com.groupA.location.world.LocationManagerFragment.LocationManagerListener;
import com.groupA.location.world.LoggingManagerFragment.LoggingManagerListener;
import com.groupA.location.world.UIManagerFragment.UIListener;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;


public class MainActivity extends FragmentActivity
implements  LocationManagerListener, UIListener, LoggingManagerListener {
		
	 UIManagerFragment uiManager;
	 MapManagerFragment mapManager;
	 LocationManagerFragment locationManager;
	 LoggingManagerFragment loggingManager;
	    	    	   

	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        setUpUI();
	        setUpMapManager();
	        setUpLocationManager();
	        setUpLoggingManager();
	    }
	    
	    
	    public void setUpLocationManager(){
	    	 Fragment locmanager = getSupportFragmentManager().findFragmentByTag("LocationManagerFragment");
		        if (locmanager == null) {
		        	locationManager = new LocationManagerFragment();
		        	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		        	fragmentTransaction.add(locationManager, "LocationManagerFragment").commit();
		        }
		        else
		        	locationManager = (LocationManagerFragment) locmanager;
	    }
	    
	    public void setUpMapManager(){
	        mapManager = (MapManagerFragment) getSupportFragmentManager().findFragmentById(R.id.mapmanagerfragment);
	    }
	    
	    public void setUpUI() {
	        uiManager = (UIManagerFragment) getSupportFragmentManager().findFragmentById(R.id.uimanagerfragment);
	    }
	    
	    public void setUpLoggingManager(){
	    	Fragment logmanager = getSupportFragmentManager().findFragmentByTag("LoggingManagerFragment"); 
	    	if (logmanager == null) {
	    		loggingManager = new LoggingManagerFragment();
	        	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	        	fragmentTransaction.add(loggingManager, "LoggingManagerFragment").commit();
	    	}
	    	else 
	    		loggingManager = (LoggingManagerFragment) logmanager;
	    }
	    
	    
	    public void goToCurrentPosition()
	    {
	    	Location location = locationManager.getLocation();
	    	mapManager.goToPosition(location);
	   
	    }

	    @Override
	    public void onLogPositionCommand() {
	    	logPosition();
	    }
	    
	    public void logPosition() {
			Location mLocation = locationManager.getLocation();
			loggingManager.receiveLoc(mLocation);
	    }
	    
	    public void onRequestPosition(){
	    	logPosition();
	    }
	    
	    
	    @Override
	    public void onLogInCommand(String userID, String password) {
	    	int result = loggingManager.logIn(userID, password);
	    	if (result == LoggingClient.LOGIN_OK) 		    		
	    		uiManager.loginSucceeded(userID);
	    	else 
	    		uiManager.loginFailed(userID);
	    	
	    }

		
	    /**
	     * Update automatically the map whenever the location changes (so it tracks you)
	     */
	    @Override
	    public void onLocationChanged(Location location) {
	    	mapManager.goToPosition(location);
	    }

	    /**
	     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
	     */
	    @Override
	    public void onLocManConnected() {
	       
	        //Move the map to current position
	        goToCurrentPosition();
	        
	        //Inform the logging manager that locations are now available
	        loggingManager.locationIsAvailable();
	    }
	    
	    

		@Override
		public void onLogOutCommand() {
			int result = loggingManager.logOut();
	    	if (result == LoggingClient.LOGOUT_OK) {
	    		uiManager.logoutSucceeded();
	    	} else 
	    		uiManager.logoutFailed();
		}


		@Override
		public void onLocManDisconnected() {
			loggingManager.locationIsUnavailable();
		}
}
