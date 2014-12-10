package com.groupA.location.world;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.groupA.location.world.ChangeGroupDialogFragment.ChangeGroupDialogListener;
import com.groupA.location.world.LoginDialogFragment.LoginDialogListener;
import com.groupA.location.world.LogoutDialogFragment.LogoutDialogListener;
import com.groupA.location.world.MessageDialogFragment.MessageDialogListener;
import com.groupA.location.world.RegisterDialogFragment.RegisterDialogListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Handles interface events (such as button clicks and so on), 
 * opens dialogs if necessary.
 * 
 * Associated with the layout file ui_layout.xml.
 *
 */
public class UIManagerFragment extends Fragment 
	implements OnClickListener, LoginDialogListener, LogoutDialogListener, RegisterDialogListener, MessageDialogListener, ChangeGroupDialogListener{
	
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
		
		public void onAddBeacon(LatLng point);

		public void onAddTarget(LatLng point);

		//public void onCastButtonPressed();
		
		public void onRegisterRequest(String userID, String groupID, String password, int charSelected);
		
		public void onChangeGroup(String newGroup);

		public void onSendMessage(String message);
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
	
	public static final int NOT_LOGGED = 0;
	public static final int LOGGING = 1; 
	public static final int LOGGED_IN = 2; 
	public static final int LOGGING_OUT = 3;
	
	public int status_UI = NOT_LOGGED;
	//public boolean logged_in_UI = false; 
	
	public boolean messages_downloaded = false; 
	
	
	/**
	 * The name of the user, as shown by the interface. 
	 * 
	 * If you change its value, call updateUI() afterwards to update the look of the interface. 
	 */
	public String userID_UI = "";
	public String groupID_UI = "";
	
	/**
	 * Handlers for the buttons. 
	 */
    private Button logOutButton, logPosButton, logInButton, messageButton, changeGroupButton;
    private RadioGroup radioGroup;
	private TextView loggingText, loggingOutText, groupIDText;
    
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
      	  status_UI = savedInstanceState.getInt("status");
      	  userID_UI = savedInstanceState.getString("userIDUI");
      	  groupID_UI = savedInstanceState.getString("groupIDUI");
          update_UI();
 //         Toast.makeText(uiView.getContext(), "Restoring: logged in = " + logged_in_UI + ", user ID = " + userID_UI, Toast.LENGTH_SHORT).show();
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
        
        messageButton = (Button) uiView.findViewById(R.id.messageButton);
        messageButton.setOnClickListener(this);

        changeGroupButton = (Button) uiView.findViewById(R.id.changeGroupButton);
        changeGroupButton.setOnClickListener(this);
        
        radioGroup = (RadioGroup) uiView.findViewById(R.id.radioGroup);		
        
        loggingText = (TextView) uiView.findViewById(R.id.loggingOnMessage);
        loggingOutText = (TextView) uiView.findViewById(R.id.loggingOutMessage);
        groupIDText = (TextView) uiView.findViewById(R.id.groupIDText);
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
	    if (view == messageButton)
	    	messageButtonPressed();
	    if (view == changeGroupButton)
	    	changeGroupButtonPressed();
	}
	
	private void changeGroupButtonPressed() {
		
		ChangeGroupDialogFragment messageDialog = new ChangeGroupDialogFragment();
		messageDialog.show(getFragmentManager(), "ChangegroupDialogFragment");	
		/*
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage="change group button pressed"; 
    	errorDialog.mTitle="button pressed";
    	errorDialog.show(getFragmentManager(), "changeGroupButtonPressedFragment");*/
	}

	private void messageButtonPressed() {
		MessageDialogFragment messageDialog = new MessageDialogFragment();
		messageDialog.show(getFragmentManager(), "OpenLoginDialogFragment");	
	}

	/**
	 * This gets called when the application is being paused or rotated. 
	 * Saves the values of logged_in_UI (that is, if you are logged in or not) 
	 * and userID_UI (that is, the user ID as shown by the UI) for recovery.
	 */
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("status", status_UI);
        outState.putString("userIDUI", userID_UI);
        outState.putString("groupIDUI", groupID_UI);
    }
	
	/**
	 * If the "log position" button is pressed, just tell the 
	 * main activity that it should send the position to the client.
	 */
    public void logPosButtonPressed() {
        //Toast.makeText(view.getContext(), "logged in = " + logged_in_UI + ", CheckedID = " + radioGroup.getCheckedRadioButtonId()
        //		 + ", beacon = " + R.id.button_beacon + ", target = " + R.id.button_target, Toast.LENGTH_SHORT).show();
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
    	status_UI = LOGGING; 
    	update_UI();
    	mListener.onLogInCommand(userID, password);
    }
    
    /**
     * The login attempt was successful: update the user interface as required. 
     * 
     * @param userID The ID of the user, to display. 
     */
    public void loginSucceeded(String userID, String groupID) {
    	status_UI = LOGGED_IN;
    	userID_UI = userID; 
    	groupID_UI = groupID;
    	update_UI();
    }
    
    /**
     * This sets the user interface in the "logged in" or "logged out" state, 
     * depending on the value of the variable logged_in_UI.
     */
    public void update_UI(){
    	switch(status_UI) {
    		case NOT_LOGGED:
    			logInButton.setVisibility(View.VISIBLE);
            	logOutButton.setVisibility(View.INVISIBLE);
            	logPosButton.setVisibility(View.INVISIBLE);
            	radioGroup.setVisibility(View.INVISIBLE);	
            	loggingText.setVisibility(View.INVISIBLE);
            	loggingOutText.setVisibility(View.INVISIBLE);
            	changeGroupButton.setVisibility(View.INVISIBLE); 
            	messageButton.setVisibility(View.INVISIBLE);
            	groupIDText.setVisibility(View.INVISIBLE);
            	
        	break;
    		case LOGGED_IN: 
    			logInButton.setVisibility(View.INVISIBLE);
            	logOutButton.setVisibility(View.VISIBLE);
            	logPosButton.setVisibility(View.VISIBLE);
            	logOutButton.setText("Log Out (" + userID_UI +")");	
            	radioGroup.setVisibility(View.VISIBLE);
            	loggingText.setVisibility(View.INVISIBLE);
            	loggingOutText.setVisibility(View.INVISIBLE);
            	changeGroupButton.setVisibility(View.VISIBLE); 
            	messageButton.setVisibility(View.VISIBLE);
            	groupIDText.setVisibility(View.VISIBLE);
            	groupIDText.setText("Group: " + groupID_UI);
            break;
    		case LOGGING: 
    			logInButton.setVisibility(View.INVISIBLE);
            	logOutButton.setVisibility(View.INVISIBLE);
            	logPosButton.setVisibility(View.INVISIBLE);
            	radioGroup.setVisibility(View.INVISIBLE);
            	loggingText.setVisibility(View.VISIBLE);
            	loggingOutText.setVisibility(View.INVISIBLE);
            	changeGroupButton.setVisibility(View.INVISIBLE); 
            	messageButton.setVisibility(View.INVISIBLE);
            	groupIDText.setVisibility(View.INVISIBLE);
            break;
    		case LOGGING_OUT: 
    			logInButton.setVisibility(View.INVISIBLE);
            	logOutButton.setVisibility(View.INVISIBLE);
            	logPosButton.setVisibility(View.INVISIBLE);
            	radioGroup.setVisibility(View.INVISIBLE);
            	loggingText.setVisibility(View.INVISIBLE);
            	loggingOutText.setVisibility(View.VISIBLE);
            	changeGroupButton.setVisibility(View.INVISIBLE); 
            	messageButton.setVisibility(View.INVISIBLE);
            	groupIDText.setVisibility(View.INVISIBLE);
            break;
            
    		default: 
				ErrorDialogFragment errorDialog = new ErrorDialogFragment();
				errorDialog.mMessage= "UI status " + status_UI + " not recognized";
				errorDialog.mTitle="Error";
				errorDialog.show(getFragmentManager(), "NoBeaconOrTargetDialogFragment");	
			}
    	}
    	/*if (logged_in_UI) {
    		logInButton.setVisibility(View.INVISIBLE);
        	logOutButton.setVisibility(View.VISIBLE);
        	logPosButton.setVisibility(View.VISIBLE);
        	logOutButton.setText("Log Out (" + userID_UI +")");	
        	radioGroup.setVisibility(View.VISIBLE);
    	} else {
    		logInButton.setVisibility(View.VISIBLE);
        	logOutButton.setVisibility(View.INVISIBLE);
        	logPosButton.setVisibility(View.INVISIBLE);
        	radioGroup.setVisibility(View.INVISIBLE);
    	}
    }*/
    
    /**
     * The login attempt failed: open the login dialog again, with the userID of the previous 
     * attempt already inserted, and display an error message. 
     * 
     * @param userID the user ID used in the failed login attempt.
     */
    public void loginFailed(String userID, String message) {
    	LoginDialogFragment loginDialog = new LoginDialogFragment();
    	loginDialog.mID = userID; 
    	loginDialog.show(getFragmentManager(), "OpenLoginDialogFragment");
    	
    	ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage=message; 
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
		status_UI = LOGGING_OUT; 
		update_UI();
		mListener.onLogOutCommand();
	}
	
	/**
	 * The logout attempt was successful, update the user interface as required. 
	 */
	public void logoutSucceeded(){
		Log.d("debug", "logout: updating user interface ");
		status_UI = NOT_LOGGED;
		//userID_UI = "";		
		update_UI();
	}
	
	/**
	 * The logout attempt was not successful, display an error message. 
	 */
	public void logoutFailed(String message) {
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
		errorDialog.mMessage=message; 
		errorDialog.mTitle="Logout Failed";
		errorDialog.show(getFragmentManager(), "LogoutFailDialogFragment");	
		status_UI = NOT_LOGGED;
		update_UI();
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
		errorDialog.show(getFragmentManager(), "NoMapDialogFragment");	  
	}
	
	public void posNoMap(){
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
		errorDialog.mMessage="Tried to move to positions while Map Services unavailable"; 
		errorDialog.mTitle="No Map";
		errorDialog.show(getFragmentManager(), "NoMapDialogFragment");	 
	}

	public void mapClicked(LatLng point) {
		if (status_UI == LOGGED_IN) {
			int r = radioGroup.getCheckedRadioButtonId();
			switch(r){
			case R.id.button_beacon : mListener.onAddBeacon(point); break;
			case R.id.button_target : mListener.onAddTarget(point); break; 			
			default: 
				ErrorDialogFragment errorDialog = new ErrorDialogFragment();
				errorDialog.mMessage= "Select first what you want to add";
				errorDialog.mTitle="Error";
				errorDialog.show(getFragmentManager(), "NoBeaconOrTargetDialogFragment");	
			}
		}
	}

	@Override
	public void onRegisterButton(String userID) {
		RegisterDialogFragment regDialog = new RegisterDialogFragment();
		regDialog.mID = userID;
		regDialog.show(getFragmentManager(), "OpenRegDialogFragment");
	}

	@Override
	public void onRegisterRequest(String userID, String groupID, String password, int charSelected) {
		mListener.onRegisterRequest(userID, groupID, password, charSelected);
	}

	public void registration_failed(String userID, String message) {
		RegisterDialogFragment regDialog = new RegisterDialogFragment();
    	regDialog.mID = userID; 
    	regDialog.show(getFragmentManager(), "OpenRegDialogFragment");	
    	
    	ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage=message; 
    	errorDialog.mTitle="Registration Failed";
    	errorDialog.show(getFragmentManager(), "RegErrorDialogFragment");
	}

	public void registration_succeeded(String userID) {
		LoginDialogFragment loginDialog = new LoginDialogFragment();
    	loginDialog.mID = userID; 
    	loginDialog.show(getFragmentManager(), "OpenLoginDialogFragment");
		
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage="You can now login."; 
    	errorDialog.mTitle="Registration Succeeded";
    	errorDialog.show(getFragmentManager(), "RegSuccededDialogFragment");
	}

	public void sendLocationFailed(String message) {
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage=message; 
    	errorDialog.mTitle="Could not send location";
    	errorDialog.show(getFragmentManager(), "RegSendLocationErrorDialogFragment");
	}

	@Override
	public void onRegisterDialogError(String userID, String message) {
		
		RegisterDialogFragment regDialog = new RegisterDialogFragment();
    	regDialog.mID = userID; 
    	regDialog.show(getFragmentManager(), "OpenRegDialogFragment");	
		
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage=message; 
    	errorDialog.mTitle="Registration Failed";
    	errorDialog.show(getFragmentManager(), "RegErrorDialogFragment");
		
	}

	@Override
	public void onSendMessage(String message) {
		mListener.onSendMessage(message);
	}

	@Override
	public void onChangeGroupRequest(String newgroup) {
		
		groupID_UI = newgroup; 
		update_UI();
		mListener.onChangeGroup(newgroup);
	}

	public void changeGroupSucceded(String group) {
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage="You are now in group ".concat(group); 
    	errorDialog.mTitle="Change Group Succeeded";
    	errorDialog.show(getFragmentManager(), "ChangeGroupErrorDialogFragment");		
	}

	public void changeGroupFailed(String message) {
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage=message; 
    	errorDialog.mTitle="Change Group Failed";
    	errorDialog.show(getFragmentManager(), "ChangeGroupErrorDialogFragment");		
	}

	public void downloadedMessages(JSONArray json) {
		
		/*if (!messages_downloaded) {
			messages_downloaded = true;*/ 
			/*ErrorDialogFragment errorDialog = new ErrorDialogFragment();
			errorDialog.mTitle = "Messages Received"; 
			errorDialog.mMessage = "AAA";
	    	errorDialog.show(getFragmentManager(), "MessagesReceivedFragment");		*/
			
			int numMessages = json.length(); 
			/*ErrorDialogFragment errorDialog = new ErrorDialogFragment();
			errorDialog.mTitle = numMessages + " messages Received"; 
			errorDialog.mMessage = json.toString();
	    	errorDialog.show(getFragmentManager(), "MessagesReceivedFragment");	*/
	    	
			for (int i = numMessages - 1; i >= 0; i--){
				try {
					JSONObject o = json.getJSONObject(i);
					String sender = o.getString("userID"); 
					String message = o.getString("content"); 
					ErrorDialogFragment errorDialog = new ErrorDialogFragment();
					errorDialog.mTitle="Message from " + sender + " (" + (i+1) + " of " + numMessages + ")";
					errorDialog.mMessage = message;
		    	    errorDialog.show(getFragmentManager(), "NewMessagesReceivedFragment");
					
					//uiManager.sendLocationFailed(json.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					ErrorDialogFragment errorDialog = new ErrorDialogFragment();
					errorDialog.mTitle = "Message Download Error"; 
					errorDialog.mMessage = e.toString();
					e.printStackTrace();
				}
			}
		}
	
}