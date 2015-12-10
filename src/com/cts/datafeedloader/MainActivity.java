package com.cts.datafeedloader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cts.datafeedloader.vo.DataHolder;
import com.cts.datafeedloader.vo.Rows;

public class MainActivity extends Activity {

	String contentURl = "https://dl.dropboxusercontent.com/u/746330/facts.json";
	ListView listView;
	TextView actionbarTitle;
	Button loadDataButton;
	ProgressBar mProgressBar;
	Context mContext;
	CustomListAdapter itemAdapter;
	ArrayList<Rows> dataList = null;
	static boolean FIRST_TIME_LOAD_FLAG = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		
		// Initialize the views in the screen
		listView = (ListView) findViewById(R.id.listView);
		actionbarTitle = (TextView) findViewById(R.id.actionBarTitleView);
		loadDataButton = (Button) findViewById(R.id.loadButton);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		// Setting up the onclick listener for the button
		loadDataButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Downloading the contents in a async task
				new DownloadDataAsyncTask().execute(contentURl);
			}
		});
		
		// setting up the selection listener for each item in the list view.
		listView.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Toast.makeText(mContext, "You have selected row no " + arg2,
						Toast.LENGTH_LONG).show();				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * Async task for downloading the json contents from server and updating the
	 * listView.
	 * 
	 */
	private class DownloadDataAsyncTask extends
			AsyncTask<String, Void, DataHolder> {

		DataHolder mDataHolder = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressBar.setVisibility(View.VISIBLE);
			loadDataButton.setEnabled(false);
		}

		@Override
		protected DataHolder doInBackground(String... params) {

			try {
				URL contentURL = new URL(params[0]);

				ObjectMapper objectMapper = new ObjectMapper();
				JsonFactory jsonFactory = new JsonFactory();
				
				// Creating a parser object with the connection stream that contains the response from service.
				JsonParser jp = jsonFactory.createJsonParser(contentURL
						.openStream());
				// reading the json into the DataHolder object.
				mDataHolder = objectMapper.readValue(jp, DataHolder.class);
			} catch (IOException e) {
				Log.e("error", "Error downloading data - " + e.getStackTrace());
			}
			return mDataHolder;
		}

		@Override
		protected void onPostExecute(DataHolder result) {
			mProgressBar.setVisibility(View.GONE);
			loadDataButton.setEnabled(true);
			if (mDataHolder != null) {				
				actionbarTitle.setText(result.title);
				dataList = result.dataArray;
				if(FIRST_TIME_LOAD_FLAG){
				itemAdapter = new CustomListAdapter(mContext,
						R.layout.list_item, dataList);
				listView.setAdapter(itemAdapter);
				FIRST_TIME_LOAD_FLAG = false;
				}else {
					itemAdapter.notifyDataSetChanged();
				}
				
			} else {
				Toast.makeText(mContext, "Error loading data.",
						Toast.LENGTH_LONG).show();
			}
		}
	}

}
