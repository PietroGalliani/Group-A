package com.groupA.location.world;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ErrorDialogFragment extends DialogFragment{
	public String mTitle = "Error error error!"; 
	public String mMessage = "Something went wrong somewhere; and further, someone forgot to initialize this error message.";
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		if (savedInstanceState != null) {
			mTitle = savedInstanceState.getString("errDialogTitle");
			mMessage = savedInstanceState.getString("errDialogMessage");
		}
		
		builder.setMessage(mMessage)
				.setTitle(mTitle)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
  
        outState.putString("errDialogTitle", mTitle);
        outState.putString("errDialogMessage", mMessage);
    }

}
