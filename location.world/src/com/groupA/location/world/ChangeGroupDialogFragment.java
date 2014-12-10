package com.groupA.location.world;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
*
* A simple dialog asking the user for their login name and password. 
* The dialogue layout can be found in ui_layout.xml
*
*/
public class ChangeGroupDialogFragment extends DialogFragment{
	
	/**
	 *
	 * The fragment that handles the outcome of this dialog -- that is, UIManagerFragment -- 
	 * must contain a function for dealing with the output of this dialog (that is, the id/password
	 * combination that the user inserted)
	 *
	 */
	public interface ChangeGroupDialogListener {
		/**
		 * The user confirmed that they want to register, and gave their ID and password
		 * 
		 * @param userID the user's ID
		 * @param password the user's password
		 */
		public void onChangeGroupRequest(String newgroup);
	}
	
	/**
	 * An instance of the interface
	 */
	ChangeGroupDialogListener mListener; 
	
	/**<qa
	 * The login dialog's title
	 */
	public String mTitle = "Change group";
	
	
	
	/**
	 * How to create the login dialog
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		/* Build a dialog for the current activity */
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		/* 
		 * Grab the dialogue layout from login_dialog.xml, and assign it to the 
		 * dialogue which we are building 
		 */
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    final View view = inflater.inflate(R.layout.changegroup_dialog, null);
		builder.setView(view);		

	    

        
		/* 
		 * If the dialog is being restored after a rotation or so on, 
		 * restore its status from the saved instance state.
		 */
        if (savedInstanceState != null) {
        	mTitle = savedInstanceState.getString("logDialogTitle");
        }
        
        /*
		 * OK, let's define the dialog now
		 */
	    builder.setTitle(mTitle) //Set up the title
	    
	    		//Add a "cancel" button
	    		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int id) {
							/*
							 * If it gets clicked, hide the keyboard and close the dialogue (note, the dialogue 
							 * closes automatically after clicking the button anyway)
							 */
							hideKeyboard(view); 
						}
					}
				)
			
				//Add a "log in" button
				.setPositiveButton("Change", new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int id) {
					        changeGroup(dialog, view);
						}
					}
				)
				
				/*
				 * If the user clicks enter on the keyboard when they are in the password field, 
				 * it is the same as if they clicked on the log in button
				 */
				.setOnKeyListener(new DialogInterface.OnKeyListener() {
					
					/**
					 * This gets called whenever a key is pressed in the dialog
					 */
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				    
						
						/*
						 * If: a key is being pressed and it is enter
						 */
						if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
			                    (keyCode == KeyEvent.KEYCODE_ENTER))  {
							
							//Then deal with the dialog's input,
							changeGroup(dialog, view);
							
							//close the dialog,
							dialog.dismiss();
							
							//and tell the key handler that we already dealt with the keypress
							return true;
						}
						
						//Nevermind, tell the key handler to deal with the keypress as usual 
						return false;
					}
				});
	    
	    /*
		 * All done, now create the dialog
		 */
		return builder.create();
		 
	}
	
	public void changeGroup(DialogInterface dialog, View view) {
        String newgroup = ((TextView) view.findViewById(R.id.newgroup_field)).getText().toString(); ;
		
        mListener.onChangeGroupRequest(newgroup);
		
		/**
		 * Hide the keyboard
		 */
		hideKeyboard(view);
	}
	
    
	/**
	 * Tells the app to hide the keyboard from the screen
	 * @param view 
	 */
    private void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

    /**
     * Tells the app to show the keyboard on the screen
     * @param view
     */
    private void showKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.showSoftInput(view, 0);
	}

    
    /**
	 * If the app gets paused or rotated, remember its title! 
	 */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
  
        outState.putString("logDialogTitle", mTitle);
    }
    
    /**
     * This gets called when the class is instantiated. It checks that 
     * the user interface fragment implements the interface; 
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
            // Instantiate the NoticeDialogListener so we can send events to the host
        	mListener = (ChangeGroupDialogListener) getFragmentManager().findFragmentById(R.id.uimanagerfragment);
        } catch (ClassCastException e) {
            // The user interface doesn't implement the interface, throw exception
        	throw new ClassCastException("uimanagerfragment must implement LoginDialogListener");
        }
    }
    
    
}
