package com.cit312.clientlegacybooks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.analytics.tracking.android.EasyTracker;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ReadBook extends Activity {

	int count  = 0;
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayShowTitleEnabled(false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readbook);

		EasyTracker.getInstance().activityStart(this);

		//Assign layout ID to variable
		WebView webview = (WebView) findViewById(R.id.webview);
		ImageView back = (ImageView) findViewById(R.id.back);
		TextView joke = (TextView) findViewById(R.id.joke);
		joke.setTextColor(Color.WHITE);

		//Get URL from main view
		Intent mainview = getIntent();
		final String bookURL = mainview.getStringExtra("bookURL");

		//Load web URL
		webview.setInitialScale(105); 
		webview.loadUrl(bookURL);

		//On click Listener for back button
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				//Return to main activity
				Intent backtomain = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(backtomain);
			}
		});
		//On click Listener for Joke
		joke.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				
				URL url;
				//http://api.icndb.com/jokes/random?limitTo=[nerdy]&amp;exclude=[explicit]&amp;escape=javascript&firstName=Bro&lastName=Barney
				String[] jokeNum = {"473","451","508","559","556","489","467","525","461","510","495","498","455","527"};
				
				String jokeURL = "http://api.icndb.com/jokes/"+jokeNum[count]+"?lastName=Barney&firstName=Bro";
				if(count == 13){
					count = 0;
				}
				count++;
				
				String streamIn = null;
				String[] parseData = new String[10];
				try {
					url = new URL(jokeURL);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					streamIn = reader.readLine();
					parseData = streamIn.split(":");
					streamIn = parseData[4];
					streamIn = streamIn.substring(0, streamIn.length()-14);;
					
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)
					findViewById(R.id.toast_layout_root));
					
					TextView text = (TextView) layout.findViewById(R.id.toastText);
					text.setText(streamIn);
					 
					final Toast tDisplay = new Toast(getApplicationContext());
					tDisplay.setDuration(Toast.LENGTH_LONG);
					tDisplay.setView(layout);
					tDisplay.show();

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}