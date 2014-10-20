package com.groupA.location.world;

import com.groupA.location.world.LocationManagerFragment.LocationManagerListener;
import com.groupA.location.world.LoggingManagerFragment.LoggingManagerListener;
import com.groupA.location.world.MapManagerFragment.MapManagerListener;
import com.groupA.location.world.UIManagerFragment.UIListener;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * 
 * The main activity, called when the app is started. Mostly it just initializes the fragments 
 * managing the user interface, the map, the location services and the logging services and passes 
 * messages back and forth between them as required. 
 *
 */
public class MainActivity extends FragmentActivity
implements  LocationManagerListener, UIListener, MapManagerListener, LoggingManagerListener {
		
	 /*
	  * These fragments manage the user interface, the map, the location services and the 
	  * logging services respectively.
	  */
	 UIManagerFragment uiManager; 	 
	 MapManagerFragment mapManager;  
	 LocationManagerFragment locationManager;
	 LoggingManagerFragment loggingManager;
	 
	 /*
	  * The functions of this class are divided in five class as follows: 
	  * 
	  * 1. 	Initializing functions: for setting up the app correctly. 
	  * 
	  * 2. 	User Interface handling functions: for implementing the uiManagerListener interface, 
	  *    	that is, for reacting to actions of the user (note, the low-level stuff about buttons and 
	  * 	dialogues and so on is dealt with by the UI Manager, here we deal with messages from the UI 
	  * 	like "the user is attempting to logout" and so on).
	  * 
	  * 3. 	Location Manager handling function: for implementing the locationManagerListener interface, 
	  * 	that is, for reacting whenever the location becomes available or unavailable, or changes. 
	  * 
	  * 4. 	Logging Manager handling functions, for implementing the loggingManagerListener interface, 
	  * 	that is, for reacting whenever the logging manager tells us that the login did or did not 
	  * 	succeed, or it asks for a position to log, or so on.
	  * 
	  * 5. 	Other functions: functions that do not implement an interface nor serve for initialization. 
	  * 	For now, we only have the logPosition function, that gets the current location from 
	  * 	locationManager and tells loggingManager to log it. 
	  */
	    	    	   
	 	/* 1. Initializing functions */
	 
	 	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main); //Load the activity_main.xml layout file
	        setUpUI();
	        setUpMapManager();
	        setUpLocationManager();
	        setUpLoggingManager();
	    }
	    
	    /**
	     * Sets up the user interface manager. 
	     * 
	     * Nothing much to say here, it looks in the layout file of the main activity for 
	     * a reference to a fragment named uimanagerfragment (see it on activity_main.xml) and 
	     * loads the corresponding fragment into uimanager. 
	     * 
	     */
	    public void setUpUI() {
	        uiManager = (UIManagerFragment) getSupportFragmentManager().findFragmentById(R.id.uimanagerfragment);
	    }
	    
	    /**
	     * Sets up the map manager. 
	     * 
	     * Nothing much to say here, it looks in the layout file of the main activity for 
	     * a reference to a fragment named mapmanagerfragment (see it on activity_main.xml) and 
	     * loads the corresponding fragment into mapmanager. 
	     * 
	     */
	    public void setUpMapManager(){
	        mapManager = (MapManagerFragment) getSupportFragmentManager().findFragmentById(R.id.mapmanagerfragment);
	    }
	    
	    
	    /**
	     * Sets up the location manager. This requires a little more care, as the location manager 
	     * fragment is not associated to a layout file (it does not have any graphical elements). 
	     * 
	     * So we have to instantiate it directly, first checking of course if it was not instantiated 
	     * already (it might happen if the app is restored). 
	     */
	    public void setUpLocationManager(){
	    	 //Check if there is already a fragment with tag "LocationManagerFragment". 	    	
	    	 Fragment locmanager = getSupportFragmentManager().findFragmentByTag("LocationManagerFragment");
		    
	    	 if (locmanager == null) { //There is not, so: 
		        
		        	//Create a new one, assign locationManager to it;
		        	locationManager = new LocationManagerFragment(); 
		        	
		        	//Add it to the fragment manager, with tag "LocationManagerFragment".
		        	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		        	fragmentTransaction.add(locationManager, "LocationManagerFragment").commit();
		        
		      	}
		     
	    	 else //There is such a fragment already, just assign locationManager to it. 
	    		 locationManager = (LocationManagerFragment) locmanager;
	    }
	    
	    /* 2. User Interface handling functions */ 
	    
	    /**
	     * The user told the user interface that they want to log in, 
	     * with userID and password as given. Tell the logging manager 
	     * this; and then tell the user interface if the login attempt
	     * was successful or not.
	     */
	    @Override
	    public void onLogInCommand(String userID, String password) {
	    	int result = loggingManager.logIn(userID, password);
	    	if (result == LoggingClient.LOGIN_OK) 		    		
	    		uiManager.loginSucceeded(userID);
	    	else 
	    		uiManager.loginFailed(userID);
	    	
	    }
	    
	    /**
	     * The user told the user interface that they want to log 
	     * their position. So we do that. 
	     */
	    @Override
	    public void onLogPositionCommand() {
	    	logPosition();
	    }
	    
		/**
		 * The user told the UI that they want to log out; do that, tell the 
		 * user interface whether the logout was successful
		 */
		@Override
		public void onLogOutCommand() {
			int result = loggingManager.logOut();
	    	if (result == LoggingClient.LOGOUT_OK) {
	    		uiManager.logoutSucceeded();
	    	} else 
	    		uiManager.logoutFailed();
		}
		
		/* 3. Location Manager handling functions */
		
		
	    /**
	     * The location manager told us that the user's position changed, 
	     * tell the map to update its position. 
	     */
	    @Override
	    public void onLocationChanged(Location location) {
	    	goToCurrentPosition();
	    }

	    /**
	     * The location manager is connected to Google location services, tell the logging manager 
	     * that it can start tracking the position (if it wants).
	     */
	    @Override
	    public void onLocationAvailable() {
	       
	        //Move the map to current position
	        goToCurrentPosition();
	        
	        //Inform the logging manager that locations are now available
	        loggingManager.locationIsAvailable();
	    }
	    
	    /**
	     * The location manager disconnected from Google location services, tell the logging manager to stop 
	     * requesting the current position.
	     */
		@Override
		public void onLocationUnavailable() {
			loggingManager.locationIsUnavailable();
		}
		
		@Override
	    public void  onNoLocationServices() {
	    	loggingManager.locationIsUnavailable();
	    	uiManager.noLocationServices();
	    }
	    
	    /* 4. Logging Manager handling functions */
	    
	    /**
	     * Sets up the logging manager. Like in the location manager case, logging fragments are not 
	     * associated to layout files, so we have to create it directly (first checking if there 
	     * is not one already).
	     */
	    public void setUpLoggingManager(){
	    	//Check if there is already a fragment with tag "LoggingManagerFragment". 	    	
	    	Fragment logmanager = getSupportFragmentManager().findFragmentByTag("LoggingManagerFragment"); 
	    	
	    	if (logmanager == null) { //There is not, so: 
	    		
	        	//Create a new one, assign locationManager to it;
	    		loggingManager = new LoggingManagerFragment();
	    		
	    		//Add it to the fragment manager, with tag "LoggingManagerFragment".
	        	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	        	fragmentTransaction.add(loggingManager, "LoggingManagerFragment").commit();
	    	}
	    	else //There is such a fragment already, just assign loggingManager to it. 
	    		loggingManager = (LoggingManagerFragment) logmanager;
	    }
	    
	    /**
	     * Move the map to the current position.
	     */
	    public void goToCurrentPosition()
	    {
	    	Location location = locationManager.getLocation();
	    	if (mapManager.map_available() && location != null)
	    		mapManager.goToPosition(location);
	    }

	    
	    /**
	     * The logging manager requested to be given a position to log. 
	     * So we do that. 
	     */
	    @Override
	    public void onRequestPosition(){
	    	logPosition();
	    }
	    
	    
	    /* 5. Other functions */
	    /**
	     * Log the current position. 
	     */
	    public void logPosition() {
			Location mLocation = locationManager.getLocation();
			if (mLocation != null) {
				loggingManager.receiveLoc(mLocation);
			}
	    }

	    /**
	     * Google Map services cannot be reached
	     */
		@Override
		public void onMapUnavailable() {
			uiManager.noMapServices();
		}

		@Override
		public void onGoToPosNoMap() {
			uiManager.posNoMap();
		}
	    	    
}
