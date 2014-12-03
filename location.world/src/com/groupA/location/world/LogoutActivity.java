package com.groupA.location.world;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
public class LogoutActivity extends AsyncTask<String,Void,String>{
public interface LogoutListener {
void logoutSucceeded();
void logoutFailed(String message);
}
LogoutListener mListener;

public LogoutActivity(LogoutListener listener) {
mListener = listener;
}
protected void onPreExecute(){
}
@Override
protected String doInBackground(String... params) {
// TODO Auto-generated method stub
try{
String username = (String)params[0];
String link="http://54.77.125.52/app/logout.php";
String data = URLEncoder.encode("username", "UTF-8")
+ "=" + URLEncoder.encode(username, "UTF-8");
/*String data = URLEncoder.encode("username", "UTF-8")
+ "=" + URLEncoder.encode("GGggggggGGGGG", "UTF-8");*/
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
if (sb.toString().equals(username)){
Log.d("debug", "logout succeeded: " + sb.toString());
Log.d("debug", username);
mListener.logoutSucceeded();
}
else
mListener.logoutFailed(sb.toString());
Log.d("debug", sb.toString());
return sb.toString();
}catch(Exception e){
Log.e("exception","login", e);
return e.getMessage();}
}
@Override
protected void onPostExecute(String result){
}
}