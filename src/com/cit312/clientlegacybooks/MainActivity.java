package com.cit312.clientlegacybooks;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import org.quickconnect.json.JSONException;
import org.quickconnect.json.JSONInputStream;
import org.quickconnect.json.JSONOutputStream;
import org.quickconnect.json.JSONUtilities;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class MainActivity extends Activity {
	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayShowTitleEnabled(false);
		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		EasyTracker.getInstance().activityStart(this); 
		
		new GetBookDataTask().execute();
	}


	@SuppressWarnings("rawtypes")
	public class GetBookDataTask extends AsyncTask {
		

		GridView gridview;
		ArrayList bookList;
		Tracker tracker = EasyTracker.getTracker();


		protected void onPreExecute() {
			gridview = (GridView) findViewById(R.id.gridview);
		}

		protected String doInBackground(Object... args) {
			try {
				Socket socket = new Socket("172.16.142.130", 4444);

				JSONOutputStream outToServer = new JSONOutputStream(socket.getOutputStream());

				outToServer.writeObject("run");
				JSONInputStream inFromServer = new JSONInputStream(socket.getInputStream());

				bookList = (ArrayList) inFromServer.readObject();

				socket.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return null;
		}

		protected void onPostExecute(Object objResult) {
			final String[] bookArray = new String[bookList.size()];
			final String[] urlArray = new String[bookList.size()];

			for(int index=0; index < bookList.size(); index++) {
				try {
					bookArray[index] = JSONUtilities.stringify((Serializable) bookList.get(index));
					urlArray[index] = setURL(bookArray[index]);
					bookArray[index] = setBookString(bookArray[index]);

				}catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			ArrayAdapter<String> ad = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,bookArray);
			gridview.setNumColumns(2);
			gridview.setGravity(Gravity.CENTER);
			gridview.setAdapter(ad);
			gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,int tileNum, long arg3) {
					
					Intent readmode = new Intent(getApplicationContext(), ReadBook.class);
					readmode.putExtra("bookURL", urlArray[tileNum]);
					startActivity(readmode);
					tracker.sendEvent("Grid view action", bookArray[tileNum], "", arg3);

				}
			});	
		}
		//Sets the display information for the tiles
		public String setBookString(String bookString) {
			String[] stringBuilder = bookString.split(",");
			stringBuilder[0] = stringBuilder[0].substring(1) + "\n";
			stringBuilder[1] = stringBuilder[1] + "\n";
			stringBuilder[2] = "Ebook #: " + stringBuilder[2];
			return stringBuilder[0] + stringBuilder[1] + stringBuilder[2];

		}
		//Extracts the book html location from string
		public String setURL(String bookString) {
			String url;
			String[] findURL = new String[3];
			findURL = bookString.split(",");
			url = findURL[3].substring(1, findURL[3].length()-2);
			return url;
		}
	}
}
