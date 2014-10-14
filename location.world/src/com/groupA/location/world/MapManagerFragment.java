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

public class MapManagerFragment extends Fragment {
	 private GoogleMap mMap;

	 private int mZoomLevel = 15;

		private void setUpMapIfNeeded() {
			        // Do a null check to confirm that we have not already instantiated the map.
			        if (mMap == null) {
			            // Try to obtain the map from the SupportMapFragment.
			            mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
			                    .getMap();
			            // Check if we were successful in obtaining the map.
			            if (mMap != null) {
			                mMap.setMyLocationEnabled(true);
			        }
			    }
		}
		
		
		@Override
		public void onResume() {
	        super.onResume();
	        setUpMapIfNeeded();
		}
		
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
		
		
		public void goToPosition(Location location) {
			
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);
	        mMap.animateCamera(cameraUpdate);
		}
	    
	    

}
