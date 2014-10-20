package com.groupA.location.world;

import com.groupA.location.world.LoginDialogFragment.LoginDialogListener;
import com.groupA.location.world.LogoutDialogFragment.LogoutDialogListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 
 * Handles interface events (such as button clicks and so on), 
 * opens dialogs if necessary.
 * 
 * Associated with the layout file ui_layout.xml.
 *
 */
public class UIManagerFragment extends Fragment 
	implements OnClickListener, LoginDialogListener, LogoutDialogListener{
	
	/**
	 * Public interface for the UI to communicate with the main activity.
	 * Look at MainActivity.java for their implementation.
	 */
	public interface UIListener {
		/**
		 * This is invoked if the user is attempting to login. 
		 * @param userID The id entered by the user
		 * @param password The password entered by the user (do _not_ store!)
		 */
		public void onLogInCommand(String userID, String password);
		
		/**
		 * This is invoked if the user is attempting to log out.
		 */
		public void onLogOutCommand();
		
		/**
		 * This is invoked if the user wants to log their position. 
		 */
		public void onLogPositionCommand();
	}
	
	/**
	 * Just an instance of the interface, for use to invoke onLogInCommand, onLogOutCommand and 
	 * onLogPositionCommand as required. 
	 */
	UIListener mListener;
	
	/**
	 * True: the user interface is in "logged-in" mode. 
	 * False: the user interface is in  "logged-out" mode. 
	 * 
	 * If you change its value, call updateUI() afterwards to update the look of the interface.
	 */
	public boolean logged_in_UI = false; 
	
	/**
	 * The name of the user, as shown by the interface. 
	 * 
	 * If you change its value, call updateUI() afterwards to update the look of the interface. 
	 */
	public String userID_UI = "";
	
	/**
	 * Handlers for the buttons. 
	 */
    private Button logOutButton, logPosButton, logInButton;
	
    
    /**
     * This function gets called when the fragment is set up by the function setUpUI() in 
     * MainActivity.java. It loads the xml layout file, set ups the handlers and listeners 
     * for the buttons, and remembers the state of the interface if the app had been paused 
     * or rotated. 
     */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
		/* Load the xml layout file in the View */
        View uiView =  inflater.inflate(R.layout.ui_layout, container, false);
        
        /* logOutButton, logPosButton and logInButton must refer to the corresponding
         * buttons of the interface, and clicks on them must be handled by the function
         * onClick
         */
        setUpButtons(uiView);
        
        
        /*
         * If the saved instance state is not empty -- that is, if this is running
         * again after the application has been rotated -- recover the values of 
         * logged_in_UI and userID_UI, and update the look of the user interface 
         * as required.
         */
        if (savedInstanceState != null) {
      	  logged_in_UI = savedInstanceState.getBoolean("loggedInUI");
      	  userID_UI = savedInstanceState.getString("userIDUI");
          update_UI();
        }
        return uiView;
    }
	
	/**
	 * Assigns the private variables logOutButton, logPosButton and logInButton to the corresponding
	 * buttons of the layout, and declares that their click events should be handled by this class
	 * (that is, by the function onClick of this class).
	 * 
	 * @param uiView the View which contains the buttons
	 */
	private void setUpButtons(View uiView)
	{
		
		logOutButton = (Button) uiView.findViewById(R.id.log_out_button); 
        logOutButton.setOnClickListener(this);
        
        logPosButton = (Button) uiView.findViewById(R.id.log_position_button);
        logPosButton.setOnClickListener(this);

        logInButton = (Button) uiView.findViewById(R.id.login_dialog_button);
        logInButton.setOnClickListener(this);
		
	}
	
	/**
	 * Handles click events on the buttons by calling the corresponding functions.
	 */
	@Override
	public void onClick(View view) {
	    if (view == logOutButton)
	    	openLogoutDialog();
	    if (view == logInButton)
	    	openLoginDialog();
	    if (view == logPosButton) 
	    	logPosButtonPressed();
	    	
	}
	
	/**
	 * This gets called when the application is being paused or rotated. 
	 * Saves the values of logged_in_UI (that is, if you are logged in or not) 
	 * and userID_UI (that is, the user ID as shown by the UI) for recovery.
	 */
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loggedInUI", logged_in_UI);
        outState.putString("userIDUI", userID_UI);
    }
	
	/**
	 * If the "log position" button is pressed, just tell the 
	 * main activity that it should send the position to the client.
	 */
    public void logPosButtonPressed() {
    	mListener.onLogPositionCommand();
    }
    
    
    /**
     * Open a login dialog, asking for name and password. 
     */
    public void openLoginDialog(){
    	LoginDialogFragment loginDialog = new LoginDialogFragment();
		loginDialog.show(getFragmentManager(), "OpenLoginDialogFragment");
    }
    
    
    /**
     * This gets invoked by LoginDialogFragment if the user has entered an ID and a password
     * and chose to log in. In that case, we just pass this information back to the activity. 
     */
    @Override
    public void onLoginDialogLogin(String userID, String password) {
    	mListener.onLogInCommand(userID, password);
    }
    
    /**
     * The login attempt was successful: update the user interface as required. 
     * 
     * @param userID The ID of the user, to display. 
     */
    public void loginSucceeded(String userID) {
    	logged_in_UI = true;
    	userID_UI = userID; 
    	update_UI();
    }
    
    /**
     * This sets the user interface in the "logged in" or "logged out" state, 
     * depending on the value of the variable logged_in_UI.
     */
    public void update_UI(){
    	if (logged_in_UI) {
    		logInButton.setVisibility(View.INVISIBLE);
        	logOutButton.setVisibility(View.VISIBLE);
        	logPosButton.setVisibility(View.VISIBLE);
        	logOutButton.setText("Log Out (" + userID_UI +")");	
    	} else {
    		logInButton.setVisibility(View.VISIBLE);
        	logOutButton.setVisibility(View.INVISIBLE);
        	logPosButton.setVisibility(View.INVISIBLE);
    	}
    }
    
    /**
     * The login attempt failed: open the login dialog again, with the userID of the previous 
     * attempt already inserted, and display an error message. 
     * 
     * @param userID the user ID used in the failed login attempt.
     */
    public void loginFailed(String userID) {
    	LoginDialogFragment loginDialog = new LoginDialogFragment();
    	loginDialog.mID = userID; 
    	loginDialog.show(getFragmentManager(), "OpenLoginDialogFragment");
    	
    	ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage="The User ID/Password combination has not been found. Please correct and retry."; 
    	errorDialog.mTitle="Login Failed";
    	errorDialog.show(getFragmentManager(), "LoginErrorDialogFragment");
    }
    
    /**
     * Opens a logout dialog, asking whether the user really wants to log out.
     */
    public void openLogoutDialog() {
    	LogoutDialogFragment logoutDialog = new LogoutDialogFragment();
    	logoutDialog.mId = userID_UI;
    	logoutDialog.show(getFragmentManager(), "LogoutDialogFragment");
    }

	
	
    /**
     * This gets called when the class is instantiated. It checks that 
     * the main activity implements the interface UIListener; and if so, 
     * it stores that implementation in mListener (to be able to invoke
     * it if necessary). 
     * 
     * Otherwise, it raises an exception.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the listener so we can send events to the host
            mListener = (UIListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement UIListener");
        }
    }

    /**
     * The user chose to log out. Send this information back to the main activity. 
     */
	@Override
	public void onLogoutChosen() {
		mListener.onLogOutCommand();
	}
	
	/**
	 * The logout attempt was successful, update the user interface as required. 
	 */
	public void logoutSucceeded(){
		logged_in_UI = false;
		userID_UI = "";		
		update_UI();
	}
	
	/**
	 * The logout attempt was not successful, display an error message. 
	 */
	public void logoutFailed() {
    	LoginDialogFragment loginDialog = new LoginDialogFragment();
		loginDialog.show(getFragmentManager(), "OpenLoginDialogFragment");
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
		errorDialog.mMessage="Could not log out. Are you sure you are logged in?"; 
		errorDialog.mTitle="Logout Failed";
		errorDialog.show(getFragmentManager(), "LogoutFailDialogFragment");	  
	}
	
	/**
	 * Location services are unavailable, display an error message. 
	 */
	public void noLocationServices() {
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
		errorDialog.mMessage="Location Services Unavailable"; 
		errorDialog.mTitle="No Location Services";
		errorDialog.show(getFragmentManager(), "NoLocationDialogFragment");	  
	}
	
	/**
	 * Map is unavailable, display an error message. 
	 */
	public void noMapServices() {
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
		errorDialog.mMessage="Google Map Services Unavailable"; 
		errorDialog.mTitle="No Map";
		errorDialog.show(getFragmentManager(), "NoLocationDialogFragment");	  
	}
	
	public void posNoMap(){
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
		errorDialog.mMessage="Tried to move to positions while Map Services unavailable"; 
		errorDialog.mTitle="No Map";
		errorDialog.show(getFragmentManager(), "NoLocationDialogFragment");	 
	}
	
}
