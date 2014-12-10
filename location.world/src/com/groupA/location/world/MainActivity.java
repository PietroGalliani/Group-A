package com.groupA.location.world;

import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.groupA.location.world.LocationManagerFragment.LocationManagerListener;
import com.groupA.location.world.LoggingManagerFragment.LoggingManagerListener;
import com.groupA.location.world.MapManagerFragment.MapManagerListener;
import com.groupA.location.world.UIManagerFragment.UIListener;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

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
	 CreaturesManager creatureManager;
	 
	 Random randomGenerator;
	 
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
	 
	 	
	    /* (non-Javadoc)
	     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	     */
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main); //Load the activity_main.xml layout file
	        setUpUI();
	        setUpCManager(savedInstanceState);
	        setUpMapManager();
	        setUpLocationManager();
	        setUpLoggingManager();
	        randomGenerator = new Random();
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
	    
	    /**
	     * Sets up the creature manager, to represent the positions and properties of other players, beacons, 
	     * and targets
	     * 
	     * @param savedInstanceState
	     */
	    public void setUpCManager(Bundle savedInstanceState){
	    	if (savedInstanceState == null) {
	    		creatureManager = new CreaturesManager();
	    	} else {
	    		creatureManager = savedInstanceState.getParcelable("cManager");	    		
	    	}
	    }
	    
	    /**
	     * We are pausing the app, save our data
	     */
	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        outState.putParcelable("cManager", creatureManager);
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
	    	loggingManager.logIn(userID, password);
	    }
	    
	    /**
	     * The user told the user interface that they want to log 
	     * their position. So we do that. 
	     */
	    @Override
	    public void onLogPositionCommand() {
	    	logPosition(); // log your position
	    	downloadPositions(); //download the other players' positions
	    }
	    
	    /**
	     * Download the other players' positions
	     */
		private void downloadPositions() {
			loggingManager.downloadOthers();
		}

		/**
		 * The user told the UI that they want to log out; do that, tell the 
		 * user interface whether the logout was successful
		 */
		@Override
		public void onLogOutCommand() {
			loggingManager.logOut();
			/*int result = loggingManager.logOut();
	    	if (result == LoggingClient.LOGOUT_OK) {
	    		uiManager.logoutSucceeded();
	    	} else 
	    		uiManager.logoutFailed();
	    	creatureManager.clear();
	    	mapManager.updateGraphics();*/
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
		
		/**
		 * Location services are not available: take care of that
		 */
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
	    	if (mapManager.map_available() && location != null){
	    		//creatureManager.setCharacter(new LatLng(location.getLatitude(), location.getLongitude()));
	    		mapManager.goToPosition(location);
	    	}
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

		/**
		 * We want to move the map to some position, but the map is not available
		 */
		@Override
		public void onGoToPosNoMap() {
			uiManager.posNoMap();
		}
		
		/**
		 * The map has been clicked
		 */

		@Override
		public void onMapClick(LatLng point) {
			uiManager.mapClicked(point);
		}

		/**
		 * Adding a beacon to the map
		 */
		@Override
		public void onAddBeacon(LatLng point) {
			creatureManager.addBeacon(point);
			
		}
		
		/**
		 * Request beacons from the creature manager
		 */

		@Override
		public List<Beacon> requestBeacons() {
			//creatureManager.step();

			return creatureManager.getBeacons();
		}

		/**
		 * Add a target to the manager
		 */
		@Override
		public void onAddTarget(LatLng point) {
			creatureManager.addTarget(point);
		}
		
		/**
		 * Receive the list of targets
		 */

		@Override
		public List<Target> requestTargets() {
			return creatureManager.getTargets();
		}
		
		/**
		 * Receive the list of the other players
		 */
		@Override
		public List<PlayerCharacter> requestOthers() {
			return creatureManager.getOthers();
		}

		@Override
		public void onTick() {
			creatureManager.step();
		}

		
		/**
		 * Registration request received, take care of it
		 */
		@Override
		public void onRegisterRequest(String userID, String groupID,
				String password, int charSelected) {
			loggingManager.register(userID, groupID, password, charSelected);
		}


		/**
		 * The registration attempt succeeded
		 */
		@Override
		public void onRegOK(final String userID, final String group) {
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					uiManager.registration_succeeded(userID);		
					mapManager.updateGraphics();
					uiManager.loginSucceeded(userID, group);
				}});
		}
		
		
		/**
		 * The login attempt succeeded
		 */
		@Override
		public void onLoginSucceeded(final String userID, final String groupID) {
		Log.d("debug", "main");
		runOnUiThread(new Runnable(){
		@Override
		public void run() {
		uiManager.loginSucceeded(userID, groupID);
		loggingManager.downloadOthers();
		mapManager.zoomToStandardLevel();
		mapManager.updateGraphics();
		mapManager.startAnimating();
		}
		});
		}
		
		/**
		 * The login attempt failed
		 */
		@Override
		public void onLoginFailed(final String userID, final String message) {
		runOnUiThread(new Runnable(){
		@Override
		public void run() {
		uiManager.loginFailed(userID, message);
		}
		});
		}

		/**
		 * The registration attempt failed
		 */
		@Override
		public void onRegFailed(String userID, String message) {
			uiManager.registration_failed(userID, message);
		}

		/**
		 * The logout attempt succeeded
		 */
		@Override
		public void onLogoutSucceded() {
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					uiManager.logoutSucceeded();
					creatureManager.clear();
					mapManager.updateGraphics();
				}
			});
		}

		
		/**
		 * The logout attempt failed
		 */
		@Override
		public void onLogoutFailed(final String message) {
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					uiManager.logoutFailed(message);
					creatureManager.clear();
					mapManager.updateGraphics();
				}
			});
		}

		/**
		 * The attempt to send a position failed
		 */
		@Override
		public void onSendLocationFailed(final String message) {
			/*runOnUiThread(new Runnable(){
				@Override
				public void run() {
					uiManager.sendLocationFailed(message);
				}
			});*/
		}

		/**
		 * The list of other players has been downloaded
		 */
		@Override
		public void onDownloadedOthers(final JSONArray json) {
			//creatureManager.addOther(point, randomGenerator.nextInt(4)+1);
			//uiManager.sendLocationFailed(json.toString());
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
			int numOthers = json.length(); 
			creatureManager.clearOthers();
			for (int i = 0; i < numOthers; i++){
				try {
					JSONObject o = json.getJSONObject(i);
					creatureManager.addOther(new LatLng(o.getDouble("latitude"), o.getDouble("longitude")), 
							o.getInt("avatar"), o.getString("user_name"));
					//uiManager.sendLocationFailed(json.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					uiManager.sendLocationFailed(e.toString());
					e.printStackTrace();
				}
			}
			mapManager.updateGraphics();
			
	    	downloadPositions();
		
				}
			});
		}

		/**
		 * We could not succeed in downloading the other players
		 */
		@Override
		public void onDownloadFailure(String message) {
		}

		/**
		 * We want to change the group we belong in
		 */
		@Override
		public void onChangeGroup(String newGroup) {
			loggingManager.changeGroup(newGroup);
		}

		/**
		 * We succeeded in changing the group
		 */
		@Override
		public void onChangeGroupSucceeded(String group) {
			uiManager.changeGroupSucceded(group);
		}

		/**
		 * We failed in changing the group
		 */
		@Override
		public void onChangeGroupFailed(String message) {
			uiManager.changeGroupFailed(message);
		}
		
		/**
		 * We downloaded the messages
		 */
		@Override
		public void onDownloadedMessages(final JSONArray json) {	
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					uiManager.downloadedMessages(json);
				}
			});
		}
		/**
		 * We want to send a message
		 */
		@Override
		public void onSendMessage(String message) {
			loggingManager.sendMessage(message);
		}
   	    
}
