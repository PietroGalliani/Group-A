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
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
public class DownloadOthersActivity extends AsyncTask<String,Void,String>{
public interface DownloadOthersListener {
void downloadedOthers(JSONArray json);
void downloadFailure(String message);
void downloadedMessages(JSONArray json);
}
DownloadOthersListener mListener;
public DownloadOthersActivity(DownloadOthersListener listener) {
mListener = listener;
}
protected void onPreExecute(){
}
@Override
protected String doInBackground(String... params) {
// TODO Auto-generated method stub
try{
String username = (String)params[0];
//String pass = (String)params[1];
String link="http://54.77.125.52/app/activityManager.php";
String data = URLEncoder.encode("username", "UTF-8")
+ "=" + URLEncoder.encode(username, "UTF-8");
//data += "&" + URLEncoder.encode("password", "UTF-8")
//+ "=" + URLEncoder.encode(pass, "UTF-8");
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
//mListener.downloadFailure(sb.toString());

JSONArray json=new JSONArray(sb.toString());
mListener.downloadedOthers(json.getJSONArray(0));
mListener.downloadedMessages(json.getJSONArray(1));

/*sb = new StringBuilder();
line = null;
// Read Server Response
while((line = reader.readLine()) != null)
{
sb.append(line);
break;
}

JSONArray messages = new JSONArray(sb.toString());
mListener.downloadedMessages(messages);*/

return sb.toString();
}catch(Exception e){
Log.e("exception","login", e);
mListener.downloadFailure(e.toString());
return e.getMessage();}
}
@Override
protected void onPostExecute(String result){
}
}