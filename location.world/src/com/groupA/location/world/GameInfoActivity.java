package com.groupA.location.world;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.util.Log;

public class GameInfoActivity extends AsyncTask<String,Void,String>
{
	
	private int mode;
	
	
	
	
	
	@Override
	protected String doInBackground(String... params) {
		
		   switch (mode) {
              case 1:  return retriveActivitys(params);
		      case 2:  return retriveActivityPlayers(params);
		      case 3:  return retriveUserInformation(params);
		      case 4:  return addUserToActivity(params);
		      case 5:  return retriveUserActivity(params);
		   default: return null;
       }
		
	}
	
	/**
	 *  
	*retreves all activities on the server, 
	*information retreves contains activity name, activity ID 
	*objective location and discription of activity
	 * @return 
	*/
	private String retriveActivitys(String...params){
		try {
			String username = (String) params[0];
			String link = "http://54.77.125.52/app/activities.php";
			String data = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(username, "UTF-8");
			data += "&" + URLEncoder.encode("retreve", "UTF-8") + "="
					+ URLEncoder.encode("activities", "UTF-8");
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				break;
			}
			if (sb.toString().equals(username)) {
				Log.d("debug", sb.toString());
				Log.d("debug", username);
				return sb.toString();

			}  else{
				Log.d("debug", sb.toString());
				return sb.toString();
				}
		} catch (Exception e) {
			Log.e("exception", "login", e);
			return null;
		}
	}
	
	/**
	*retreves all player information on an activity, 
	*information retreves contains user name, 
	*/
	private String retriveActivityPlayers(String...params){
		
		try {
			String username = (String) params[0];
			String activityID = (String)params[1];
			String link = "http://54.77.125.52/app/activities.php";
			String data = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(username, "UTF-8");
			data += "&" + URLEncoder.encode("retreve", "UTF-8") + "="
					+ URLEncoder.encode("usersInActivity", "UTF-8");
			data += "&" + URLEncoder.encode("activityID", "UTF-8") + "="
					+ URLEncoder.encode(activityID, "UTF-8");
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				break;
			}
			if (sb.toString().equals(username)) {
				Log.d("debug", sb.toString());
				Log.d("debug", username);
				return sb.toString();

			}  else{
				Log.d("debug", sb.toString());
				return sb.toString();
				}
		} catch (Exception e) {
			Log.e("exception", "login", e);
			return null;
		}
		
	}
	
	/**
	*retreves a single user information on the server, 
	*information retreves contains user name, userID, avatar number 
	*and other personal information
	*/
	private String retriveUserInformation(String...params){
		
		try {
			String username = (String) params[0];
			String retreveID = (String)params[1];
			String link = "http://54.77.125.52/app/activities.php";
			String data = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(username, "UTF-8");
			data += "&" + URLEncoder.encode("retreve", "UTF-8") + "="
					+ URLEncoder.encode("userInfo", "UTF-8");
			data += "&" + URLEncoder.encode("retreveUserID", "UTF-8") + "="
					+ URLEncoder.encode(retreveID, "UTF-8");
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				break;
			}
			if (sb.toString().equals(username)) {
				Log.d("debug", sb.toString());
				Log.d("debug", username);
				return sb.toString();

			}  else{
				Log.d("debug", sb.toString());
				return sb.toString();
				}
		} catch (Exception e) {
			Log.e("exception", "login", e);
			return null;
		}
		
	}
	
	/**
	*adds a user to a given activity
	*server reads: username, add = true, activityID
	*/
	private String addUserToActivity(String...params){
		try {
			String username = (String) params[0];
			String activityID = (String)params[1];
			String link = "http://54.77.125.52/app/activities.php";
			String data = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(username, "UTF-8");
			data += "&" + URLEncoder.encode("add", "UTF-8") + "="
					+ URLEncoder.encode("true", "UTF-8");
			data += "&" + URLEncoder.encode("activityID", "UTF-8") + "="
					+ URLEncoder.encode(activityID, "UTF-8");
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				break;
			}
			if (sb.toString().equals(username)) {
				Log.d("debug", sb.toString());
				Log.d("debug", username);
				return sb.toString();

			} else{
			Log.d("debug", sb.toString());
			return sb.toString();
			}
		} catch (Exception e) {
			Log.e("exception", "login", e);
			return null;
		}
	} 
	
	/**
	*retreves all user movements on the activity, 
	*information retrives contains activity name, user names
	*and current user locations
	*/
	private String retriveUserActivity(String...params){
		
		try {
			String username = (String) params[0];
			String activityID = (String)params[1];
			String link = "http://54.77.125.52/app/activities.php";
			String data = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(username, "UTF-8");
			data += "&" + URLEncoder.encode("retreve", "UTF-8") + "="
					+ URLEncoder.encode("usersLocationInActivity", "UTF-8");
			data += "&" + URLEncoder.encode("activityID", "UTF-8") + "="
					+ URLEncoder.encode(activityID, "UTF-8");
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				break;
			}
			if (sb.toString().equals(username)) {
				Log.d("debug", sb.toString());
				Log.d("debug", username);
				return sb.toString();

			} else{
			Log.d("debug", sb.toString());
			return sb.toString();}
		} catch (Exception e) {
			Log.e("exception", "login", e);
			return null;
		}
		
		
	}

}
