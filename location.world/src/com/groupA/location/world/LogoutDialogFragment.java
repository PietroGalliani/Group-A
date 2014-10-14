package com.groupA.location.world;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class LogoutDialogFragment extends DialogFragment{
	public interface LogoutDialogListener {
		public void onLogoutChosen(); 
	}
	
	LogoutDialogListener mListener;
	
	public String mTitle = "Log out?"; 
	public String mMessage = "do you really want to log out?";
	public String mId = "";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		if (savedInstanceState != null) {
			mTitle = savedInstanceState.getString("logoutDialogTitle");
			mMessage = savedInstanceState.getString("logoutDialogMessage");
			mId = savedInstanceState.getString("logoutDialogId");
		}
		
		builder.setMessage(mId + ", " + mMessage)
				.setTitle(mTitle)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mListener.onLogoutChosen();
						}
					}
				).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				}
			);
		return builder.create();
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
  
        outState.putString("logoutDialogTitle", mTitle);
        outState.putString("logoutDialogMessage", mMessage);
        outState.putString("logoutDialogId", mId);
    }
	// Override the Fragment.onAttach() method to instantiate the LogoutDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LogoutDialogListener so we can send events to the host
            mListener = (LogoutDialogListener) getFragmentManager().findFragmentById(R.id.uimanagerfragment);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("uimanagerfragment must implement LogoutDialogListener");
        }
    }
}
