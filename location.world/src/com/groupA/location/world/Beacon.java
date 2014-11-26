package com.groupA.location.world;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Beacon implements Parcelable {
	private LatLng position;

	public int tick;
	
	public Beacon(LatLng p){
		position = p; 
		tick = 0;
	}
	
	public LatLng getPosition() { 
		return position;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void step(){
		tick++;
		if (tick == 3)
			tick = 0;
	}

	
	/** Constructor for restoring from a parcel, if necessary */
	private Beacon(Parcel in) {
		position = in.readParcelable(null);	
		tick = in.readInt();
	}
	
	/** Save the data before pausing/rotationg **/
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(position, 0);
        out.writeInt(tick);
    }
    
    public static final Parcelable.Creator<Beacon> CREATOR
	= new Parcelable.Creator<Beacon>() {
	public Beacon createFromParcel(Parcel in) {
		return new Beacon(in);
	}
	public Beacon[] newArray(int size) {
		return new Beacon[size];
	}
    };
}
