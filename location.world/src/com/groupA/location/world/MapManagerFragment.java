package com.groupA.location.world;

import java.util.List;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
	 * The activity that deals with the manager -- that is, MainActivity -- must 
	 * implement the following functions:
	 */
	public interface MapManagerListener {
		/**
		 * The map is not available
		 */
		public void onMapUnavailable(); 
		public void onGoToPosNoMap();
		public void onMapClick(LatLng point);
		public List<Beacon> requestBeacons();
		public PlayerCharacter requestCharacter();
		public List<Target> requestTargets();
		public List<PlayerCharacter> requestOthers();
		public void onTick();
	}
	
	/**
	 * An instance of the interface, to call its functions when necessary.
	 */
	MapManagerListener mListener;

	SpriteManager mSpriteManager;
	
	private Handler mHandler;
	
	/**
	 * The map being displayed.
	 */
	 private GoogleMap mMap;
	 
	 /**
	  * The zoom level. Max = 20.
	  */
	 public int mZoomLevel = 15;
	 	 
	 Marker playerIcon;
	 
	 /**
	  * Is the map available? 
	  */
		public boolean map_available() {
			return (mMap != null);
		}
		
	 /**
	  * If a map is not already loaded, load it.
	  */
	 private void setUpMapIfNeeded()  {
		        // Do a null check to confirm that we have not already instantiated the map in mMap.
		        if (!map_available()) {
		            // Try to obtain the map from the SupportMapFragment.
		            mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			            // Check if we were successful in obtaining the map.
			            if (mMap != null) {
			                mMap.setMyLocationEnabled(false);			  
			                mMap.getUiSettings().setAllGesturesEnabled(false);
			                mMap.getUiSettings().setZoomControlsEnabled(false);
			                mMap.getUiSettings().setCompassEnabled(true);			                

			            }
			        //check if now map is available, otherwise send error back
			        if (!map_available())
			        	mListener.onMapUnavailable();
			    }
		}
	 
	 	private void loadCharacterIcon(PlayerCharacter ch){
	 		MarkerOptions userCharacterOptions = new MarkerOptions()			               
            .position(new LatLng(0,0))
            .visible(false)
            .icon(mSpriteManager.getCharIcon(ch.ch_type));
            playerIcon = mMap.addMarker(userCharacterOptions);
	 	}
		
		/**
		 * If the app is being resumed, set up the map again if necessary.
		 */
		@Override
		public void onResume() {
	        super.onResume();
			setUpMapIfNeeded();
			
			bindMapClick();
			
			updateGraphics();
		}
		
		private void bindMapClick(){
			mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
                    //mMap.addMarker(new MarkerOptions().position(point));
                	// if map is clicked, pass the information back to MainActivity
                	mListener.onMapClick(point);
                	updateGraphics();
                }
            });
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
	        mSpriteManager = new SpriteManager();
	        mHandler = new Handler();
	        
	        //startAnimating();
	        
	        

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
			if (map_available() && location != null) {
				LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel);
				mMap.animateCamera(cameraUpdate);
				updateGraphics();
			}
			else 
				mListener.onGoToPosNoMap();
		}
		
		/**
	     * This gets called when the class is instantiated. It checks that 
	     * the main activity implements the interface MapManagerListener; and if 
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
	            mListener = (MapManagerListener) activity;
	        } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	            throw new ClassCastException(activity.toString()
	                    + " must implement MapManagerListener");
	        }
	    }
		
		public void updateGraphics(){
			mMap.clear();
			
			drawBeacons();
			
			drawTargets();
			
			drawPC();
			
			drawOthers();
		}
		
		public void drawBeacons(){
			List<Beacon> beaconPositions = mListener.requestBeacons(); 
			for (Beacon b : beaconPositions){ 
				drawBeacon(b);
			}
		}
		
		public void drawTargets(){
			List<Target> targetPositions = mListener.requestTargets(); 
			for (Target t : targetPositions){ 
				drawTarget(t.getPosition());
			}
		}
		
		public void drawPC(){
			PlayerCharacter pCharacter = mListener.requestCharacter(); 
			if (pCharacter.isVisible()) {
				loadCharacterIcon(pCharacter);
				playerIcon.setPosition(pCharacter.getPosition());
				playerIcon.setVisible(true);
			}
		}
		
		public void drawOthers(){
			List<PlayerCharacter> others = mListener.requestOthers(); 
			for (PlayerCharacter p : others) {
				drawCharacter(p);
			}
		}
		
		public void drawCharacter(PlayerCharacter p){
			MarkerOptions userCharacterOptions = new MarkerOptions()			               
            .position(p.getPosition())
            .icon(mSpriteManager.getCharIcon(p.ch_type));
            mMap.addMarker(userCharacterOptions);
		}
		
		public void drawBeacon(Beacon b){
			mMap.addMarker(new MarkerOptions().position(b.getPosition())
			.icon(mSpriteManager.getBeaconIcon(b.tick)));
		}
		
		public void drawTarget(LatLng pos){
			mMap.addMarker(new MarkerOptions().position(pos).
					icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		}
		
	    /**
	     * Animations of sprites
	     */
	    Runnable mSpriteAnimator = new Runnable() {
	    	
	    	/**
	    	 * When starting, do this: 
	    	 */
	        @Override 
	        public void run() {
	        	
	        	mListener.onTick();
		        //updateGraphics();
	        	drawBeacons();
	        	
	        	/* Wait one second, then run mSpriteAnimator again */
	        	mHandler.postDelayed(mSpriteAnimator, 300);
	        }
	      };
	      
	      /**
	       * Start logging your positions periodically
	       */
	      void startAnimating() {
	    	 mListener.onTick();
	    	 mSpriteAnimator.run(); 
	      }

	      /**
	       * Stop logging your positions
	       */
	      void stopAnimating() {
	    	   mHandler.removeCallbacks(mSpriteAnimator);
	      }

		public void showExplosion(LatLng point) {
			// TODO Auto-generated method stub
			mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.explosion)));
			
		}

}