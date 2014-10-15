package com.groupA.location.world;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * Loads up the map from Google Maps, displays it, moves it as required. 
 * Associated with the layout file map_manager.xml.
 * 
 */
public class MapManagerFragment extends Fragment {
	/**
	 * The map being displayed.
	 */
	 private GoogleMap mMap;
	 
	 /**
	  * The zoom level. Max = 20.
	  */
	 public int mZoomLevel = 15;

	 /**
	  * If a map is not already loaded, load it.
	  */
	 private void setUpMapIfNeeded() {
		        // Do a null check to confirm that we have not already instantiated the map in mMap.
		        if (mMap == null) {
		            // Try to obtain the map from the SupportMapFragment.
		            mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			            // Check if we were successful in obtaining the map.
			            if (mMap != null) {
			                mMap.setMyLocationEnabled(true);
			        }
			    }
		}
		
		/**
		 * If the app is being resumed, set up the map again if necessary.
		 */
		@Override
		public void onResume() {
	        super.onResume();
	        setUpMapIfNeeded();
		}
		
		/**
		 * 
		 * This function gets called when the fragment is set up by the function setUpUI() in 
	     * MainActivity.java. It loads the xml layout file and assigns mMap to the google map 
	     * declared in the layout (unless the app is being restored after a rotation or so on, 
	     * in which case it recovers it from the saved instance state).
	     * 
		 */
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
			
	        // Inflate the layout for this fragment
	        View mapView =  inflater.inflate(R.layout.map_manager, container, false);
	        
	        //Restore the map without reloading it in case of rotations
	        SupportMapFragment mapFragment =
	                (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);

	        if (savedInstanceState == null) {
	            // First incarnation of this activity.
	            mapFragment.setRetainInstance(true);
	        } else {
	            // Reincarnated activity. The obtained map is the same map instance in the previous
	            // activity life cycle. There is no need to reinitialize it.
	            mMap = mapFragment.getMap();
	        }
	        
	        return mapView;
	        
	    }
		
		/**
		 * 
		 * Moves the map to the location given as a parameter. The zoom level is given by the variable 
		 * mZoomLevel. 
		 * 
		 * @param location The location to which we must move the map.
		 */
		public void goToPosition(Location location) {
			
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);
	        mMap.animateCamera(cameraUpdate);
		}
	    
	    

}
