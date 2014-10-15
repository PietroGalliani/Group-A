package com.groupA.location.world;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
/**
*
* A simple dialog for displaying error messages. 
* 
* Remember to assign a value to mTitle and to mMessage before calling errorDialog.show(...)! 
*
*/
public class ErrorDialogFragment extends DialogFragment{
	
	/**
	 * Title of the error dialog
	 */
	public String mTitle = "Error error error!"; 
	
	/**
	 * Contents of the dialog message
	 */
	public String mMessage = "Something went wrong somewhere; and further, someone forgot to initialize this error message.";
	
	/**
	 * How to create the error dialog
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		/* Build a dialog for the current activity */
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		/* 
		 * If the dialog is being restored after a rotation or so on, 
		 * restore its status from the saved instance state.
		 */
		if (savedInstanceState != null) {
			mTitle = savedInstanceState.getString("errDialogTitle");
			mMessage = savedInstanceState.getString("errDialogMessage");
		}
		 
        /*
		 * OK, let's define the dialog now
		 */
		builder.setMessage(mMessage) //set up the dialog message 
		
				.setTitle(mTitle) //set up the title 
				
				.setPositiveButton("OK", null); /*Add an OK button -- no need to add an OnClickListener
				 								  here, it must just close the dialog (and does that automatically 
				 								  anyway).*/

		return builder.create();
	}
	
	/**
	 * If the app gets paused or rotated, remember its title and message! 
	 */
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
  
        outState.putString("errDialogTitle", mTitle);
        outState.putString("errDialogMessage", mMessage);
    }
}
