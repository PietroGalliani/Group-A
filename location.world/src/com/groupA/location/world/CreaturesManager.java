package com.groupA.location.world;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class CreaturesManager implements Parcelable {
	List<Beacon> mListBeacons;
	List<Target> mListTargets;
	List<PlayerCharacter> mListOthers;
	
	PlayerCharacter mCharacter;
		
	public CreaturesManager(){
		mListBeacons = new ArrayList<Beacon>();
		mListTargets = new ArrayList<Target>();
		mListOthers = new ArrayList<PlayerCharacter>();
		mCharacter = new PlayerCharacter();
	}
	
	public void addBeacon(LatLng position){
		mListBeacons.add(new Beacon(position));
	}
	
	public void addTarget(LatLng position){
		mListTargets.add(new Target(position));
	}
	
	public void addOther(LatLng position, int type){
		PlayerCharacter ch = new PlayerCharacter(); 
		ch.moveTo(position);
		ch.pickIcon(type);
		mListOthers.add(ch);
	}
	
	public void setCharacter(LatLng position){
		mCharacter.moveTo(position);
	}
	
	public List<Beacon> getBeacons(){
		return mListBeacons;
	}
	
	public PlayerCharacter getCharacter(){
		return mCharacter;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/** Constructor for restoring from a parcel, if necessary */
	private CreaturesManager(Parcel in) {
		in.readTypedList(mListBeacons, Beacon.CREATOR);
		in.readTypedList(mListTargets, Target.CREATOR);
		in.readTypedList(mListOthers, PlayerCharacter.CREATOR);
		mCharacter = in.readParcelable(null);		
	}
	
	/** Save the data before pausing/rotating **/
    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedList(mListBeacons);
        out.writeTypedList(mListTargets);
        out.writeTypedList(mListOthers);
        out.writeParcelable(mCharacter, 0);
    }
    
    public static final Parcelable.Creator<CreaturesManager> CREATOR
	= new Parcelable.Creator<CreaturesManager>() {
	public CreaturesManager createFromParcel(Parcel in) {
		return new CreaturesManager(in);
	}
	public CreaturesManager[] newArray(int size) {
		return new CreaturesManager[size];
	}
    };

	public void pickCharacter(int charSelected) {
		mCharacter.pickIcon(charSelected);
	}

	public void clear() {
		mListBeacons.clear();
		mListTargets.clear();
		mListOthers.clear();
		mCharacter.clear();
	}

	public List<Target> getTargets() {
		// TODO Auto-generated method stub
		return mListTargets;
	}

	public List<PlayerCharacter> getOthers() {
		// TODO Auto-generated method stub
		return mListOthers;
	}
	
	public void step(){
		for (Beacon b : mListBeacons){
			b.step();
		}
	}

	public LatLng castBeacons() {
		double lat=0, lng=0;
		for (Beacon b : mListBeacons) {
			LatLng posb = b.getPosition();
			lat += posb.latitude;
			lng += posb.longitude;
		}
		lat = lat/mListBeacons.size();
		lng = lng/mListBeacons.size();
		mListBeacons.clear();
		return new LatLng(lat, lng);
	}
	
}
