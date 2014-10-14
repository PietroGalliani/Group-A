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

public class UIManagerFragment extends Fragment 
	implements OnClickListener, LoginDialogListener, LogoutDialogListener{
	
	public interface UIListener {
		public void onLogInCommand(String userID, String password);
		public void onLogOutCommand();
		public void onLogPositionCommand();
	}
	
	UIListener mListener;
	
	public boolean logged_in_UI = false; 
	
	public String userID_UI = "";
	
    private Button logOutButton, logPosButton, logInButton;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View uiView =  inflater.inflate(R.layout.ui_layout, container, false);
        
        setUpButtons(uiView);
        
        
        if (savedInstanceState != null) {
      	  logged_in_UI = savedInstanceState.getBoolean("loggedInUI");
      	  userID_UI = savedInstanceState.getString("userIDUI");
          update_UI();
        }
        return uiView;
    }
	
	private void setUpButtons(View uiView)
	{
		
		logOutButton = (Button) uiView.findViewById(R.id.log_out_button); 
        logOutButton.setOnClickListener(this);
        
        logPosButton = (Button) uiView.findViewById(R.id.log_position_button);
        logPosButton.setOnClickListener(this);

        logInButton = (Button) uiView.findViewById(R.id.login_dialog_button);
        logInButton.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View view) {
	    if (view == logOutButton)
	    	logOutButtonPressed();
	    if (view == logInButton)
	    	openLoginDialog();
	    if (view == logPosButton) 
	    	logPosButtonPressed();
	    	
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loggedInUI", logged_in_UI);
        outState.putString("userIDUI", userID_UI);
    }
	

    public void logPosButtonPressed() {
    	mListener.onLogPositionCommand();
    }
    
    
    
    public void openLoginDialog(){
    	LoginDialogFragment loginDialog = new LoginDialogFragment();
		loginDialog.show(getFragmentManager(), "OpenLoginDialogFragment");
    }
    
    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onLoginDialogLogin(String userID, String password) {
    	mListener.onLogInCommand(userID, password);
    }
    
    public void loginSucceeded(String userID) {
    	logged_in_UI = true;
    	userID_UI = userID; 
    	update_UI();
    }
    
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
    
    public void loginFailed(String userID) {
    	LoginDialogFragment loginDialog = new LoginDialogFragment();
    	loginDialog.mID = userID; 
    	loginDialog.show(getFragmentManager(), "OpenLoginDialogFragment");
    	
    	ErrorDialogFragment errorDialog = new ErrorDialogFragment();
    	errorDialog.mMessage="The User ID/Password combination has not been found. Please correct and retry."; 
    	errorDialog.mTitle="Login Failed";
    	errorDialog.show(getFragmentManager(), "LoginErrorDialogFragment");
    }
    
    public void logOutButtonPressed() {
    	LogoutDialogFragment logoutDialog = new LogoutDialogFragment();
    	logoutDialog.mId = userID_UI;
    	logoutDialog.show(getFragmentManager(), "LogoutDialogFragment");
    }

	
	
	// Override the Fragment.onAttach() method to instantiate the listener
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

	@Override
	public void onLogoutChosen() {
		mListener.onLogOutCommand();
	}
	
	public void logoutSucceeded(){
		logged_in_UI = false;
		userID_UI = "";		
		update_UI();
	}
	
	public void logoutFailed() {
    	LoginDialogFragment loginDialog = new LoginDialogFragment();
		loginDialog.show(getFragmentManager(), "OpenLoginDialogFragment");
		ErrorDialogFragment errorDialog = new ErrorDialogFragment();
		errorDialog.mMessage="Could not log out. Are you sure you are logged in?"; 
		errorDialog.mTitle="Logout Failed";
		errorDialog.show(getFragmentManager(), "LogoutFailDialogFragment");	  
	}
}
