package com.groupA.location.world;

import com.groupA.location.world.LoginDialogFragment.LoginDialogListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

//Scheduler fragment -- encapsulates a handler, provides a listener for logging repeatedly the position
public class TrackerSchedulerFragment extends Fragment {
    
	public interface TrackerSchedulerListener {
		public void onTrackImpulse(); 
	}
	
	private int mLoggingInterval = 20000; // Time span between position updates -- currently 20 seconds
	private Handler mHandler;
	
	TrackerSchedulerListener mListener;
	
	public void onCreate (Bundle savedInstanceState){
		
	}
	
	
	
	// Override the Fragment.onAttach() method to instantiate the listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the listener so we can send events to the host
            mListener = (TrackerSchedulerListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement TrackerSchedulerListener");
        }
    }
	
	

	

}
