package com.groupA.location.world;
//Modular branch -- let's see if this works
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.groupA.location.world.LoginDialogFragment.LoginDialogListener;
import com.groupA.location.world.LogoutDialogFragment.LogoutDialogListener;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import android.view.View;
import android.widget.Button;


public class MainActivity extends FragmentActivity
implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, LoginDialogListener, LogoutDialogListener {
	
	
	
	 private GoogleMap mMap;
	 private Handler mHandler;
	 

	    private LocationClient mLocationClient;
	    private LoggingClient mLoggingClient;
	    
	    private Button logOutButton, logPosButton, logInDialogButton;

	    	    
	    private int mLoggingInterval = 20000; // Time span between position updates -- currently 20 seconds

	    private int mZoomLevel = 15;
	    
	    private Boolean isLoggingActive = false;
	    	    	    
	    
	    // These settings are the same as the settings for the map. They will in fact give you updates
	    // at the maximal rates currently possible.
	    private static final LocationRequest REQUEST = LocationRequest.create()
	            .setInterval(5000)         // 5 seconds
	            .setFastestInterval(16)    // 16ms = 60fps
	            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        mLoggingClient = new LoggingClient();
	        setContentView(R.layout.activity_main);
	        mHandler = new Handler();
	        logOutButton = (Button) findViewById(R.id.log_out_button); 
	        logPosButton = (Button) findViewById(R.id.log_position_button);
	        logInDialogButton = (Button) findViewById(R.id.login_dialog_button);

	        

	        
	        //Restore the map without reloading it in case of rotations
	        SupportMapFragment mapFragment =
	                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

	        if (savedInstanceState == null) {
	            // First incarnation of this activity.
	            mapFragment.setRetainInstance(true);
	        } else {
	            // Reincarnated activity. The obtained map is the same map instance in the previous
	            // activity life cycle. There is no need to reinitialize it.
	            mMap = mapFragment.getMap();
	        }
	        

	    }
	    
	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        /*outState.putParcelable("loggingClient", mLoggingClient);
	        outState.putBoolean("loggingActive", isLoggingActive);
	        outState.putInt("loggingInterval", mLoggingInterval);
	        outState.putInt("zoomLevel", mZoomLevel);*/
	        outState.putParcelable("loggingClient", mLoggingClient);
	        outState.putBoolean("loggingActive", isLoggingActive);
	        outState.putString("logOutButtonText", logOutButton.getText().toString());
	    }
	    
	    @Override  
	    public void onRestoreInstanceState(Bundle savedInstanceState) {  
	      super.onRestoreInstanceState(savedInstanceState);  


	      // Restore UI state from the savedInstanceState.  
	      // This bundle has also been passed to onCreate.  
	      
	      /*mLoggingClient = savedInstanceState.getParcelable("loggingClient");
	      isLoggingActive = savedInstanceState.getParcelable("loggingActive");
	      mLoggingInterval = savedInstanceState.getInt("loggingInterval");
	      mZoomLevel = savedInstanceState.getInt("zoomLevel");*/
	      

	      mLoggingClient = savedInstanceState.getParcelable("loggingClient");
	      
	      if (mLoggingClient.is_logged()) {
	    		logOutButton.setVisibility(View.VISIBLE);
	    		logPosButton.setVisibility(View.VISIBLE);
	    		logInDialogButton.setVisibility(View.INVISIBLE);
	      }
	      
	      isLoggingActive = savedInstanceState.getBoolean("loggingActive");
	      
	      logOutButton.setText(savedInstanceState.getString("logOutButtonText"));
	    }
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        setUpMapIfNeeded();
	        setUpLocationClientIfNeeded();	        
	        mLocationClient.connect();
	        /*if (isLoggingActive)
	        	startLoggingPos();*/
	        //goToCurrentPosition();
	    }
	    
	    public void goToCurrentPosition()
	    {
	    	Location location = mLocationClient.getLastLocation();
	        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);
	        mMap.animateCamera(cameraUpdate);
	    }

	    @Override
	    public void onPause() {
	    	super.onPause();
	        if (mLocationClient != null) {
	            mLocationClient.disconnect();
	        }
	        if (isLoggingActive)
	        	stopLoggingPos();
	    }

	    private void setUpMapIfNeeded() {
	        // Do a null check to confirm that we have not already instantiated the map.
	        if (mMap == null) {
	            // Try to obtain the map from the SupportMapFragment.
	            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
	                    .getMap();
	            // Check if we were successful in obtaining the map.
	            if (mMap != null) {
	                mMap.setMyLocationEnabled(true);
	            }
	        }
	    }


	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		//return true;
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	
	 private void setUpLocationClientIfNeeded() {
	        if (mLocationClient == null) {
	            mLocationClient = new LocationClient(
	                    getApplicationContext(),
	                    this,  // ConnectionCallbacks
	                    this); // OnConnectionFailedListener
	        }
	    }
	 

	    public void logPositionButton(View view) {
	    	logPosition();
	    }
	    
	    public void logPosition() {
	    	int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if (result == ConnectionResult.SUCCESS) {
				Location mLocation = mLocationClient.getLastLocation();
				mLoggingClient.logCoords(this, mLocation);
				//goToCurrentPosition();
			}
			else {
				ErrorDialogFragment errorDialog = new ErrorDialogFragment();
	    		errorDialog.mMessage="Could not connect to Google Play Services. Output: " + result; 
	    		errorDialog.mTitle="Google Play Services Unavailable";
	    		errorDialog.show(getSupportFragmentManager(), "GooglePlayErrorDialogFragment");
			}
	    }
	    
	    
	    public void openLoginDialog(View view){
	    	LoginDialogFragment loginDialog = new LoginDialogFragment();
    		loginDialog.show(getSupportFragmentManager(), "OpenLoginDialogFragment");
	    }
	    
	    // The dialog fragment receives a reference to this Activity through the
	    // Fragment.onAttach() callback, which it uses to call the following methods
	    // defined by the NoticeDialogFragment.NoticeDialogListener interface
	    @Override
	    public void onLoginDialogLogin(String userID, String password) {
	    	int result = mLoggingClient.logIn(userID, password);
	    	if (result == LoggingClient.LOGIN_OK) {	    		
	    		
	    		logInDialogButton.setVisibility(View.INVISIBLE);
	    		logOutButton.setVisibility(View.VISIBLE);
	    		logPosButton.setVisibility(View.VISIBLE);
	    		logOutButton.setText("Log Out (" + mLoggingClient.getUserID()+")");		
	    		startLoggingPos();
	    		isLoggingActive = true;
	    	}
	    	else {
				//Toast.makeText(this, "login failed!", Toast.LENGTH_SHORT).show();
	    		LoginDialogFragment loginDialog = new LoginDialogFragment();
	    		loginDialog.mID = userID; 
	    		loginDialog.show(getSupportFragmentManager(), "OpenLoginDialogFragment");
	    		
	    		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
	    		errorDialog.mMessage="The User ID/Password combination has not been found. Please correct and retry."; 
	    		errorDialog.mTitle="Login Failed";
	    		errorDialog.show(getSupportFragmentManager(), "LoginErrorDialogFragment");

	    	}
	    }


	    
	    public void logOutButton(View view) {
	    	LogoutDialogFragment logoutDialog = new LogoutDialogFragment();
	    	logoutDialog.mId = mLoggingClient.getUserID();
	    	logoutDialog.show(getSupportFragmentManager(), "LogoutDialogFragment");
	    }
	    
		
	    


	    /**
	     * Update automatically the map whenever the location changes (so it tracks you)
	     */
	    @Override
	    public void onLocationChanged(Location location) {
	       LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);
	        mMap.animateCamera(cameraUpdate);
	    }

	    /**
	     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
	     */
	    @Override
	    public void onConnected(Bundle connectionHint) {
	        mLocationClient.requestLocationUpdates(
	                REQUEST,
	                this);  // LocationListener
	        
	        //Move the map to current position
	        goToCurrentPosition();
	        
	        //Start logging your position (if required)
	        if (isLoggingActive)
	        	startLoggingPos();
	        
	    }

	    /**
	     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
	     */
	    @Override
	    public void onDisconnected() {
	        // Do nothing
	    }

	    /**
	     * Implementation of {@link OnConnectionFailedListener}.
	     */
	    @Override
	    public void onConnectionFailed(ConnectionResult result) {
	        // Do nothing
	    }
	    
	    Runnable mPositionLogger = new Runnable() {
	        @Override 
	        public void run() {
	          logPosition(); //this function can change value of mInterval.
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
		public void onLogoutChosen() {
			int result = mLoggingClient.logOut();
	    	if (result == LoggingClient.LOGOUT_OK) {
	    		logInDialogButton.setVisibility(View.VISIBLE);
	    		logOutButton.setVisibility(View.INVISIBLE);
	    		logPosButton.setVisibility(View.INVISIBLE);
	    		isLoggingActive = false;
	    		stopLoggingPos();
	    	} else {
		    	LoginDialogFragment loginDialog = new LoginDialogFragment();
	    		loginDialog.show(getSupportFragmentManager(), "OpenLoginDialogFragment");
	    		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
	    		errorDialog.mMessage="Could not log out. Are you sure you are logged in?"; 
	    		errorDialog.mTitle="Logout Failed";
	    		errorDialog.show(getSupportFragmentManager(), "LogoutFailDialogFragment");	  
	    	}
		}
}
