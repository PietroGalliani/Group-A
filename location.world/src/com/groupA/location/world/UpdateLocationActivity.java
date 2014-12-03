package com.groupA.location.world;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class UpdateLocationActivity extends AsyncTask<Double, Void, String> {
	// flag 0 means get and 1 means post.(By default it is get.)
	public UpdateLocationActivity() {
	}

	protected void onPreExecute() {
	}

	@Override
	protected String doInBackground(Double... arg0) {
		try {
			int missionID = 1;
			double longitude = (double) arg0[0];
			double latitude = (double) arg0[1];
			
			
			String link = "http://54.77.125.52/app/uploadLocation.php";
			String data = URLEncoder.encode("longitude", "UTF-8") + "="
					+longitude;
			data += "&" + URLEncoder.encode("latitude", "UTF-8") + "="
					+latitude;
			data += "&" + URLEncoder.encode("activity", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(missionID), "UTF-8");
			data += "&" + URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode("admin", "UTF-8");
			
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
			Log.e("debug", sb.toString());
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	@Override
	protected void onPostExecute(String result) {
	}
}