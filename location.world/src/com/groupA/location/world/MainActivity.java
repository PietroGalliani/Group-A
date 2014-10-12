package com.groupA.location.world;

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

import android.support.v4.app.FragmentActivity;
import android.content.Context;
//import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends FragmentActivity
implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	
	
	
	 private GoogleMap mMap;
	 private Handler mHandler;
	 
	 private TextView userIDText, userPasswdText;

	    private LocationClient mLocationClient;
	    private LoggingClient mLoggingClient;
	    
	    private Button logInButton, logOutButton, logPosButton;
	    
	    private Button menuButton;
	    private View menuLayout;
	    	    
	    private int mLoggingInterval = 20000; // Time span between position updates -- currently 20 seconds

	    private int mZoomLevel = 15;
	    
	    private Boolean isLoggingActive = false;
	    
	    private Boolean isMenuVisible = false;
	    	    
	    
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
	        userIDText = (TextView) findViewById(R.id.userid_text);
	        userPasswdText = (TextView) findViewById(R.id.password_text);
	        logInButton = (Button)findViewById(R.id.log_in_button);
	        logOutButton = (Button) findViewById(R.id.log_out_button); 
	        logPosButton = (Button) findViewById(R.id.log_position_button);
	        menuLayout = findViewById(R.id.menu_layout);
	        menuButton = (Button) findViewById(R.id.menu_button);
	        
	       //Enter on password field tries to log in
	        userPasswdText.setOnKeyListener(new View.OnKeyListener() {
	            public boolean onKey(View v, int keyCode, KeyEvent event) {
	                // If the event is a key-down event on the "enter" button
	                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
	                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
	                  // Perform action on key press
	                	logInButton(v);
	                  return true;
	                }
	                return false;
	            }
	        });
	        
	        //focus should be on user id field, not on password one
	        userIDText.requestFocus();
	        
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
	        outState.putBoolean("menuVisible", isMenuVisible);
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
	      
	      isMenuVisible = savedInstanceState.getBoolean("menuVisible");
	      
	      if (isMenuVisible) {
	    	  menuLayout.setVisibility(View.VISIBLE);
		      menuButton.setVisibility(View.INVISIBLE);
	      }
	      mLoggingClient = savedInstanceState.getParcelable("loggingClient");
	      
	      if (mLoggingClient.is_logged()) {
	    	  logInButton.setVisibility(View.INVISIBLE);
	    		logOutButton.setVisibility(View.VISIBLE);
	    		logPosButton.setVisibility(View.VISIBLE);
	    		userIDText.setVisibility(View.GONE);
	    		userPasswdText.setVisibility(View.GONE);
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
			else
				Toast.makeText(this, "Google Play Unavailable: output = " + result, Toast.LENGTH_SHORT).show();
	    }
	    
	    public void logInButton(View view) {
	    	String userID = userIDText.getText().toString();
	    	String password = userPasswdText.getText().toString();
	    	userPasswdText.setText("");
	    	int result = mLoggingClient.logIn(userID, password);
	    	if (result == LoggingClient.LOGIN_OK) {
	    		logInButton.setVisibility(View.INVISIBLE);
	    		logOutButton.setVisibility(View.VISIBLE);
	    		logPosButton.setVisibility(View.VISIBLE);
	    		logOutButton.setText("Log Out (" + mLoggingClient.getUserID()+")");
	    		userIDText.setVisibility(View.GONE);
	    		userPasswdText.setVisibility(View.GONE);
	    		startLoggingPos();
	    		isLoggingActive = true;
	    		hideMenu(view);
	    	}
	    	else
				Toast.makeText(this, "login failed!", Toast.LENGTH_SHORT).show();
	    }
	    
	    public void logOutButton(View view) {
	    	int result = mLoggingClient.logOut();
	    	if (result == LoggingClient.LOGOUT_OK) {
	    		logInButton.setVisibility(View.VISIBLE);
	    		logOutButton.setVisibility(View.INVISIBLE);
	    		logPosButton.setVisibility(View.INVISIBLE);
	    		userIDText.setVisibility(View.VISIBLE);
	    		userPasswdText.setVisibility(View.VISIBLE);
	    		isLoggingActive = false;
	    		stopLoggingPos();
	    	} else 
				Toast.makeText(this, "logout failed!", Toast.LENGTH_SHORT).show();
	    }
	    
	    
	    public void openMenu(View view) {
	    	menuLayout.setVisibility(View.VISIBLE);
	    	menuButton.setVisibility(View.INVISIBLE);
	    	isMenuVisible = true;
	    }
	    
	    public void hideMenu(View view) {
	    	
	    	//If we were writing in some text field, forget about it
    		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    	    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	    	
	    	menuLayout.setVisibility(View.GONE);
	    	menuButton.setVisibility(View.VISIBLE); 
	    	
	    	isMenuVisible = false;
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
}
