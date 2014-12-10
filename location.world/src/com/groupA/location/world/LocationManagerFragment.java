package com.groupA.location.world;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 *
 * Deals contacts with Google Location Services
 *
 */
public class LocationManagerFragment extends Fragment
implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
	
	/**
	 * The activity that deals with the manager -- that is, MainActivity -- must 
	 * implement the following functions:
	 */
	public interface LocationManagerListener {
		/**
		 * The current location changed
		 * @param location
		 */
		public void onLocationChanged(Location location); 
		/**
		 * The location services are now available
		 */
		public void onLocationAvailable();
		/**
		 * The location services are now unavailable
		 */
		public void onLocationUnavailable();	

		/**
		 * Location Services not available
		 */
		public void onNoLocationServices();
	}
	
	/**
	 * An instance of the interface, to call its functions when necessary.
	 */
	LocationManagerListener mListener;

	/**
	 * Settings for location updates. Do not decrease further, they are already as low as possible.
	 */
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    
    
	/**
	 * The LocationClient connecting to Google's location services
	 */
	private LocationClient mLocationClient;
	
	/**
	 * Are the location services available? 
	 */

	/*public boolean location_available() {
		return (mLocationClient != null);
	}*/
	
	/**
	 * Set up the location client (if it is not set up already)
	 */
	 private void setUpLocationClientIfNeeded() {
	        if (mLocationClient == null) {
	            mLocationClient = new LocationClient(
	                    getActivity(),
	                    this,  // ConnectionCallbacks
	                    this); // OnConnectionFailedListener
	        }
	    }

	 /**
	  * 
	  * @return the current location
	  */
	 public Location getLocation(){
		 Location loc =  mLocationClient.getLastLocation();
		 if (loc == null) {
			 mListener.onNoLocationServices();
		 }
		 return loc;
	 }

	 /*
	  * If the location changes, pass this information back to the main activity
	  */
	@Override
	public void onLocationChanged(Location location) {
		mListener.onLocationChanged(location);
	}

	/*
	 * If the connection failed, do nothing for now (later will add it to the interface, let the main activity handle it)
	 */
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		//do nothing (for now)	
		mListener.onLocationUnavailable();
	}

	/*
	 * If you are now connected, tell the main activity so
	 */
	@Override
	public void onConnected(Bundle arg0) {
		// when connected, set up the client and inform MainActivity
		
		mLocationClient.requestLocationUpdates(REQUEST, this);
		
		//getLocation();
		
		mListener.onLocationAvailable();
		
	}
	/*
	 * If you are now connected, tell the main activity so
	 */
	@Override
	public void onDisconnected() {
		mListener.onLocationUnavailable();
	}

	/*
	 * If the app is being paused, disconnect
	 */
	 @Override
	 public void onPause() {
		 super.onPause();
	     if (mLocationClient != null) {
	    	 mLocationClient.disconnect();
	     }
	 }
	 
	/*
	 * If the app is being resumed, reconnect
	 */
	@Override
    public void onResume() {
        super.onResume();
        setUpLocationClientIfNeeded();	        
        mLocationClient.connect();
    }

	
	/**
     * This gets called when the class is instantiated. It checks that 
     * the main activity implements the interface LocationManagerListener; and if 
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
            mListener = (LocationManagerListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LocationManagerListener");
        }
    }
}
