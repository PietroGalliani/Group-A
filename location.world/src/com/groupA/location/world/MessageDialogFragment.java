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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
*
* A simple dialog asking the user for their login name and password. 
* The dialogue layout can be found in ui_layout.xml
*
*/
public class MessageDialogFragment extends DialogFragment{
	
	/**
	 *
	 * The fragment that handles the outcome of this dialog -- that is, UIManagerFragment -- 
	 * must contain a function for dealing with the output of this dialog (that is, the id/password
	 * combination that the user inserted)
	 *
	 */
	public interface MessageDialogListener {
		/**
		 * The user confirmed that they want to login, and gave their ID and password
		 * 
		 * @param userID the user's ID
		 * @param password the user's password
		 */
		public void onSendMessage(String message);	}
	
	/**
	 * An instance of the interface, to call onLoginDialogLogin() when necessary.
	 */
	MessageDialogListener mListener; 
	
	/**
	 * The login dialog's title
	 */
	public String mTitle = "Send a message";
	
	TextView messageText;
	
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
	    final View view = inflater.inflate(R.layout.message_dialog, null);
		builder.setView(view);		

        messageText = (TextView) view.findViewById(R.id.messageText);        
        
		/* 
		 * If the dialog is being restored after a rotation or so on, 
		 * restore its status from the saved instance state.
		 */
        if (savedInstanceState != null) {
        	mTitle = savedInstanceState.getString("dialogTitle");
        	messageText.setText(savedInstanceState.getString("messageContent"));
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
				.setPositiveButton("Send", new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int id) {
							/*
							 * If it gets clicked, deal with the dialog's input
							 */
							sendMessage(dialog, view);
						}
					}
				);
	    
	    /*
		 * All done, now create the dialog
		 */
		return builder.create();
		 
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
     * Deals with the id-password combination entered in the dialog. 
     * @param dialog
     * @param view
     */
    public void sendMessage(DialogInterface dialog, View view) {

                       
        /**
         * Tell the user interface of the id/password combination
         */
		mListener.onSendMessage(messageText.getText().toString());
		
		/**
		 * Hide the keyboard
		 */
		hideKeyboard(view);
	}
    
    /**
	 * If the app gets paused or rotated, remember its title and user id! 
	 */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
  
        outState.putString("dialogTitle", mTitle);
        outState.putString("messageContent", messageText.getText().toString());
    }
    
    /**
     * This gets called when the class is instantiated. It checks that 
     * the user interface fragment implements the interface LoinDialogListener; 
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
        	mListener = (MessageDialogListener) getFragmentManager().findFragmentById(R.id.uimanagerfragment);
        } catch (ClassCastException e) {
            // The user interface doesn't implement the interface, throw exception
        	throw new ClassCastException("uimanagerfragment must implement MessageDialogListener");
        }
    }
    
    
}
