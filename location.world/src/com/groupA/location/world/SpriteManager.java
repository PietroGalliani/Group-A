package com.groupA.location.world;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class SpriteManager {
	public BitmapDescriptor characters_img[]; 
	public static int n_charsprites = 4; 
	public BitmapDescriptor beacons_img[];
	public static int n_beasprites = 3;
	
	public SpriteManager(){
		characters_img = new BitmapDescriptor[n_charsprites];
		characters_img[0] = BitmapDescriptorFactory.fromResource(R.drawable.character1);
		characters_img[1] = BitmapDescriptorFactory.fromResource(R.drawable.character2);
		characters_img[2] = BitmapDescriptorFactory.fromResource(R.drawable.character3);
		characters_img[3] = BitmapDescriptorFactory.fromResource(R.drawable.character4);
	
		beacons_img = new BitmapDescriptor[n_beasprites];
		beacons_img[0] = BitmapDescriptorFactory.fromResource(R.drawable.flame1);
		beacons_img[1] = BitmapDescriptorFactory.fromResource(R.drawable.flame2);
		beacons_img[2] = BitmapDescriptorFactory.fromResource(R.drawable.flame3);


	}
	
	public BitmapDescriptor getCharIcon(int n){
		return characters_img[n-1];
	}
	
	public BitmapDescriptor getBeaconIcon(int t){
		return beacons_img[t % n_beasprites];
	}
}
