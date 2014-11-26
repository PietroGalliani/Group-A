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
	
	public RegisterActivity(RegisterListener listener) {
		mListener = listener;
	}
	
	@Override
protected String doInBackground(String... params) {
try{
String username = (String)params[0];
String pass = (String)params[1];
String confirmPass = (String)params[2];
String email = (String)params[3];
String confirmEmail = (String)params[4];
String link="http://54.77.125.52/app/doRegister.php";
String data = URLEncoder.encode("Name", "UTF-8")
+ "=" + URLEncoder.encode(username, "UTF-8");
data += "&" + URLEncoder.encode("Email", "UTF-8")
+ "=" + URLEncoder.encode(email, "UTF-8");
data += "&" + URLEncoder.encode("confirm_email", "UTF-8")
+ "=" + URLEncoder.encode(confirmEmail, "UTF-8");
data += "&" + URLEncoder.encode("password", "UTF-8")
+ "=" + URLEncoder.encode(pass, "UTF-8");
data += "&" + URLEncoder.encode("confirm_password'", "UTF-8")
+ "=" + URLEncoder.encode(confirmPass, "UTF-8");
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
mListener.RegistrationFailed(username, sb.toString());
return sb.toString();
}catch(Exception e){
Log.e("exception","login", e);
return e.getMessage();}
}
}