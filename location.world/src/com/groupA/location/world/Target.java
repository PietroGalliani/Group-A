package com.groupA.location.world;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Target implements Parcelable {
	private LatLng position; 
	
	public Target(LatLng p){
		position = p; 
	}
	
	public LatLng getPosition() { 
		return position;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	/** Constructor for restoring from a parcel, if necessary */
	private Target(Parcel in) {
		position = in.readParcelable(null);	
	}
	
	/** Save the data before pausing/rotationg **/
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(position, 0);
    }
    
    public static final Parcelable.Creator<Target> CREATOR
	= new Parcelable.Creator<Target>() {
	public Target createFromParcel(Parcel in) {
		return new Target(in);
	}
	public Target[] newArray(int size) {
		return new Target[size];
	}
    };
}
