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
//import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	
	 private GoogleMap mMap;
	 private Handler mHandler;

	    private LocationClient mLocationClient;
	    private LoggingClient mLoggingClient;
	    
	    private int mLoggingInterval = 20000; // Time span between position updates -- currently 20 seconds

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
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();
	        setUpMapIfNeeded();
	        setUpLocationClientIfNeeded();	        
	        mLocationClient.connect();
	        //goToCurrentPosition();
	    }
	    
	    public void goToCurrentPosition()
	    {
	    	Location location = mLocationClient.getLastLocation();
	        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
	        mMap.animateCamera(cameraUpdate);
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	        if (mLocationClient != null) {
	            mLocationClient.disconnect();
	        }
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
	 
	 /**
	     * Button to get current Location. This demonstrates how to get the current Location as required
	     * without needing to register a LocationListener.
	     */
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
	    	int result = mLoggingClient.logIn("Test User", "AAAAA");
	    	if (result == LoggingClient.LOGIN_OK) {
	    		View b = findViewById(R.id.log_in_button);
	    		b.setVisibility(View.INVISIBLE);
	    		b = findViewById(R.id.log_out_button);
	    		b.setVisibility(View.VISIBLE);
	    		b = findViewById(R.id.log_position_button);
	    		b.setVisibility(View.VISIBLE);
	    		startLoggingPos();
	    	}
	    	else
				Toast.makeText(this, "login failed!", Toast.LENGTH_SHORT).show();
	    }
	    
	    public void logOutButton(View view) {
	    	int result = mLoggingClient.logOut();
	    	if (result == LoggingClient.LOGOUT_OK) {
	    		View b = findViewById(R.id.log_out_button);
	    		b.setVisibility(View.GONE);
	    		b = findViewById(R.id.log_in_button);
	    		b.setVisibility(View.VISIBLE);
	    		b = findViewById(R.id.log_position_button);
	    		b.setVisibility(View.INVISIBLE);
	    		stopLoggingPos();
	    	} else 
				Toast.makeText(this, "logout failed!", Toast.LENGTH_SHORT).show();
	    }
	    

	    /**
	     * Implementation of {@link LocationListener}.
	     * Add the commented line to make it update automatically the map whenever the location changes (so it tracks you)
	     */
	    @Override
	    public void onLocationChanged(Location location) {
	      /* LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
	        mMap.animateCamera(cameraUpdate);*/
	    }

	    /**
	     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
	     */
	    @Override
	    public void onConnected(Bundle connectionHint) {
	        mLocationClient.requestLocationUpdates(
	                REQUEST,
	                this);  // LocationListener
	        goToCurrentPosition();
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
