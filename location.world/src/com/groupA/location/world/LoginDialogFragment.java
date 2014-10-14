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
import android.widget.TextView;

public class LoginDialogFragment extends DialogFragment{
	
	public interface LoginDialogListener {
		public void onLoginDialogLogin(String userID, String password); 
	}
	
	public String mTitle = "Log in";
	public String mID = ""; 
	
	LoginDialogListener mListener; 
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    final View view = inflater.inflate(R.layout.login_dialog, null);
	    

        TextView userIDText = (TextView) view.findViewById(R.id.username_field);
        TextView userPasswdText = (TextView) view.findViewById(R.id.password_field); 

        userIDText.setText(mID);
        
        if (savedInstanceState != null) {
        	mTitle = savedInstanceState.getString("logDialogTitle");
        	mID = savedInstanceState.getString("logDialogID");
        }
        
        if (!(mID.equals(""))){
        	userPasswdText.requestFocus();
        	showKeyboard(view);
        }
	    
		builder.setView(view);
		
	    builder.setTitle(mTitle)
	    		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							hideKeyboard(view);
							//dialog.dismiss();
						}
					}
				)
				.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							returnIDPasswd(dialog, view);
						}
					}
				).setOnKeyListener(new DialogInterface.OnKeyListener() {
					
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				        TextView passwText = (TextView) view.findViewById(R.id.password_field);
						if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
			                    (keyCode == KeyEvent.KEYCODE_ENTER) && passwText.hasFocus())  {
							returnIDPasswd(dialog, view);
							return true;
						}
						return false;
					}
				});
		return builder.create();
		 
	}
	
    // Override the Fragment.onAttach() method to instantiate the LoginDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
        	mListener = (LoginDialogListener) getFragmentManager().findFragmentById(R.id.uimanagerfragment);
        } catch (ClassCastException e) {
            // The user interface doesn't implement the interface, throw exception
        	throw new ClassCastException("uimanagerfragment must implement LoginDialogListener");
        }
    }
    
    private void hideKeyboard(View view) {
    	//If we were writing in some text field, forget about it
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
    
    private void showKeyboard(View view) {
    	//If we were writing in some text field, forget about it
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.showSoftInput(view, 0);
	}
    
    public void returnIDPasswd(DialogInterface dialog, View view) {
		//LoginDialogListener activity = (LoginDialogListener) getActivity();
        TextView userIDText = (TextView) view.findViewById(R.id.username_field);
        TextView userPasswdText = (TextView) view.findViewById(R.id.password_field); 
		mListener.onLoginDialogLogin(userIDText.getText().toString(), userPasswdText.getText().toString());
		hideKeyboard(view);
		dialog.dismiss();
	}
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
  
        outState.putString("logDialogTitle", mTitle);
        outState.putString("logDialogID", mID);
    }
}
