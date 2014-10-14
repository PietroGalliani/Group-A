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

public class LocationManagerFragment extends Fragment
implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
	
	public interface LocationManagerListener {
		public void onLocationChanged(Location location); 
		public void onLocManConnected();
		public void onLocManDisconnected();
	}
	
    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	LocationManagerListener mListener;

	private LocationClient mLocationClient;

	
	 private void setUpLocationClientIfNeeded() {
	        if (mLocationClient == null) {
	            mLocationClient = new LocationClient(
	                    getActivity(),
	                    this,  // ConnectionCallbacks
	                    this); // OnConnectionFailedListener
	        }
	    }

	 public Location getLocation(){
		 return mLocationClient.getLastLocation();
	 }

	@Override
	public void onLocationChanged(Location location) {
		// If the location changes, pass this information back to MainActivity
		mListener.onLocationChanged(location);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// do nothing (for now)
	}

	@Override
	public void onConnected(Bundle arg0) {
		// when connected, set up the client and inform MainActivity
		
		mLocationClient.requestLocationUpdates(REQUEST, this);
		
		getLocation();
		
		mListener.onLocManConnected();
	}

	@Override
	public void onDisconnected() {
		// do nothing (for now)
		mListener.onLocManDisconnected();
	}

	@Override
    public void onResume() {
        super.onResume();
        setUpLocationClientIfNeeded();	        
        mLocationClient.connect();
    }
	
	 @Override
	    public void onPause() {
	    	super.onPause();
	        if (mLocationClient != null) {
	            mLocationClient.disconnect();
	        }
	    }
	
	 // Override the Fragment.onAttach() method to instantiate the listener
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
