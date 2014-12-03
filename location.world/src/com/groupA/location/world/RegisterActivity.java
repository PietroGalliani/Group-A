package com.groupA.location.world;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.groupA.location.world.LoginActivity.LoginListener;

import android.os.AsyncTask;
import android.util.Log;
public class RegisterActivity extends AsyncTask<String, Void, String> {

	public interface RegisterListener {
		void RegistrationSucceeded(String userID);
		void RegistrationFailed(String userID, String message);
	}
	RegisterListener mListener;
	int mAvatar;
	
	public RegisterActivity(RegisterListener listener, int avatar) {
		mListener = listener;
		mAvatar = avatar;
	}
	
	@Override
protected String doInBackground(String... params) {
try{
String username = (String)params[0];
String pass = (String)params[1];
String link="http://54.77.125.52/app/doRegisterAndr.php";
String data = URLEncoder.encode("Name", "UTF-8")
+ "=" + URLEncoder.encode(username, "UTF-8");
data += "&" + URLEncoder.encode("password", "UTF-8")
+ "=" + URLEncoder.encode(pass, "UTF-8");
data += "&" + URLEncoder.encode("avatar", "UTF-8")
+ "=" + mAvatar;
URL url = new URL(link);
URLConnection conn = url.openConnection();
conn.setDoOutput(true);
OutputStreamWriter wr = new OutputStreamWriter
(conn.getOutputStream());
wr.write( data );
wr.flush();
BufferedReader reader = new BufferedReader
(new InputStreamReader(conn.getInputStream()));
StringBuilder sb = new StringBuilder();
String line = null;
// Read Server Response
while((line = reader.readLine()) != null)
{
sb.append(line);
break;
}
if (sb.toString().equals("OK"))
	mListener.RegistrationSucceeded(username);
else
	mListener.RegistrationFailed(username, sb.toString());
return sb.toString();
}catch(Exception e){
	mListener.RegistrationFailed("", e.toString());

Log.e("exception","login", e);
return e.getMessage();}
}
}