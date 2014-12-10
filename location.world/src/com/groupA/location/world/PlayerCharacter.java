package com.groupA.location.world;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class PlayerCharacter implements Parcelable {
	private LatLng position; 
	private Boolean positioned = false;
	private Boolean iconChosen = false; 
	public int ch_type;
	public String user_name;
	
	
	public PlayerCharacter(){
		positioned = false;
		iconChosen = false;
		
	}
	
	public LatLng getPosition() { 
		return position;
	}
	
	public void moveTo(LatLng pos){
		position = pos;
		positioned = true;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	/** Constructor for restoring from a parcel, if necessary */
	private PlayerCharacter(Parcel in) {
		position = in.readParcelable(null);	
		positioned = (in.readInt() == 1);
		iconChosen = (in.readInt() == 1);
		if (iconChosen) {
			ch_type = in.readInt(); 
		}
		
	}
	
	/** Save the data before pausing/rotations **/
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(position, 0);
        out.writeInt(positioned?1:0);
        out.writeInt(iconChosen?1:0);
        if (iconChosen)
        	out.writeInt(ch_type);
    }
    
    public static final Parcelable.Creator<PlayerCharacter> CREATOR
	= new Parcelable.Creator<PlayerCharacter>() {
	public PlayerCharacter createFromParcel(Parcel in) {
		return new PlayerCharacter(in);
	}
	public PlayerCharacter[] newArray(int size) {
		return new PlayerCharacter[size];
	}
    };

	public boolean isVisible() {
		// TODO Auto-generated method stub
		return (positioned && iconChosen);
	}

	public void pickIcon(int charSelected) {
		ch_type = charSelected; 
		iconChosen = true;	
	}

	public void clear() {
		// TODO Auto-generated method stub
		iconChosen = false;
	}

}
