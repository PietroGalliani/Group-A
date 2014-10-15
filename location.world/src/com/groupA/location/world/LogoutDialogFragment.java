package com.groupA.location.world;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 *
 * A simple dialog asking the user if they are sure they want to log out
 *
 */
public class LogoutDialogFragment extends DialogFragment{
	
	/**
	 *
	 * The fragment that handles the outcome of this dialog -- that is, UIManagerFragment -- 
	 * must contain a function onLogoutChosen() that gets called if the user confirms that 
	 * they want to log out.
	 *
	 */
	public interface LogoutDialogListener {
		/**
		 * The user confirmed that they want to log out. 
		 */
		public void onLogoutChosen(); 
	}
	
	/**
	 * An instance of the interface, to call onLogoutChosen() when necessary.
	 */
	LogoutDialogListener mListener;
	
	
	/**
	 * Title of the logout dialog
	 */
	public String mTitle = "Log out?"; 
	
	/**
	 *  Message for the logout dialog
	 */
	public String mMessage = "do you really want to log out?"; 
	
	/**
	 * Name of the user who is logging out.
	 */
	public String mId = ""; 
	
	/**
	 * How to create the logout dialog
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		/* Build a dialog for the current activity */
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		/* 
		 * If the dialog is being restored after a rotation or so on, 
		 * restore its status (in particular, the user id) from the 
		 * saved instance state.
		 */
		if (savedInstanceState != null) {
			mTitle = savedInstanceState.getString("logoutDialogTitle");
			mMessage = savedInstanceState.getString("logoutDialogMessage");
			mId = savedInstanceState.getString("logoutDialogId");
		}
		
		
		/*
		 * OK, let's define the dialog now
		 */
		builder.setMessage(mId + ", " + mMessage) 	// The dialog message is "<name>, do you really want to log out?"
		
				.setTitle(mTitle) 					//The title is mTitle
		
				.setNegativeButton("No", null)		/* 	Add a "No" option -- no need to add an OnClickListener here, 
														when "No" is clicked we must only close the dialog and that 
														happens automatically anyway */
				
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() { // onAdd a "Yes" option
						public void onClick(DialogInterface dialog, int id) {
							//If it gets clicked, tell the interface that the user wants to log out
							mListener.onLogoutChosen(); 
						}
					}
				);
		
		/*
		 * All done, now create the dialog
		 */
		return builder.create();
	}
	
	/**
	 * If the app gets paused or rotated, remember its title, message and user id! 
	 */
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
  
        outState.putString("logoutDialogTitle", mTitle);
        outState.putString("logoutDialogMessage", mMessage);
        outState.putString("logoutDialogId", mId);
    }
	
	
	 /**
     * This gets called when the class is instantiated. It checks that 
     * the user interface fragment implements the interface LogoutDialogListener; 
     * and if so, it stores that implementation in mListener (to be able to invoke
     * it if necessary). 
     * 
     * Otherwise, it raises an exception.
     */    
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the UI fragment implements the callback interface
        try {
            // Instantiate the LogoutDialogListener so we can send events to the host
            mListener = (LogoutDialogListener) getFragmentManager().findFragmentById(R.id.uimanagerfragment);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("uimanagerfragment must implement LogoutDialogListener");
        }
    }
}
