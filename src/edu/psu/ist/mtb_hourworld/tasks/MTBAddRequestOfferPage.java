package edu.psu.ist.mtb_hourworld.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.adapter.MTBCategoryAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.items.MTBServiceCategoryItem;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MTBAddRequestOfferPage extends Activity {

	private ListView vListView;
	private ProgressBar vLoadingBar;
	private TextView vLoadingText;
	private TextView vHeaderText;
	
	private SharedPreferences mPref;
	private ArrayList<MTBServiceCategoryItem> mArr = new ArrayList<MTBServiceCategoryItem>();
	
	private String mIsRequest;
	private String mIsOffer;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mtb_add_request_offer);
	    
	    Intent gIntent = getIntent();
	    mIsRequest = gIntent.getExtras().getString("mIsRequest");
	    mIsOffer = gIntent.getExtras().getString("mIsOffer");
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    vListView = (ListView)findViewById(R.id.listview);
	    vLoadingBar = (ProgressBar)findViewById(R.id.loading_bar);
	    vLoadingText = (TextView)findViewById(R.id.loading_text);
	    vHeaderText = (TextView)findViewById(R.id.header_text);
	    
	    if(mIsOffer.equals("T")) {
	    	//vHeaderText.setText(" [1/3] Pick a category for your offer");
	    	//setTitle("[1/3] Pick a category for your offer");
	    	setTitle(" Offer category");
	    	getActionBar().setIcon(R.drawable.offers);
		    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    }
	    else {
	    	//vHeaderText.setText(" [1/3] Pick a category for your request");
	    	//setTitle("[1/3] Pick a category for your request");
	    	setTitle(" Request category");
	    	getActionBar().setIcon(R.drawable.requests);
		    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    }
	    
	    new getServiceCategory().execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}

	@Override
	public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
	
	/*
	 * Download Timebank Information from the server
	 */
	class getServiceCategory extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {

			HttpClient httpClient = new DefaultHttpClient();  	
			//String url = new String(Constants.REGISTER_LINK);
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("AddTask,CAT")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		   
			httpPost.setEntity(entity);

			// output string
			String result = "";
			
			try {
				HttpResponse response = httpClient.execute(httpPost);
			
				Log.i("K", "getStatusCode : " + response.getStatusLine().getStatusCode());
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					
					StringBuilder result_str = new StringBuilder();
					for(;;){
						String line = br.readLine();
						if (line == null) 
							break;
						result_str.append(line+'\n');
					}
					result = result_str.toString();
					JSONObject jObj = new JSONObject(result);
				
					Log.i("K", "results: " + result + " jObj.getBoolean : " + jObj.getBoolean("success"));
				
					if(jObj.getBoolean("success")){
						JSONArray jAry = jObj.getJSONArray("results");
						
						for(int i = 0; i < jAry.length(); i++) {
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							MTBServiceCategoryItem sc = new MTBServiceCategoryItem();
							sc.SvcCatID = jItemObj.getInt("SvcCatID");
							sc.SvcCat = jItemObj.getString("SvcCat");
							
							if(sc.SvcCat.contains("0")) {
								//sc.SvcCat = sc.SvcCat.replaceAll("0", "").trim();
								continue;
							}
							
							mArr.add(sc);
						}
						
						return true;
					}
					else {
						return false;
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return true;
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			if(returnValue) {
				
				Log.i("K", "mArr : " + mArr.size());
				
				vLoadingBar.setVisibility(View.GONE);
				vLoadingText.setVisibility(View.GONE);
				vListView.setVisibility(View.VISIBLE);
				
				MTBCategoryAdapter adapter = new MTBCategoryAdapter(MTBAddRequestOfferPage.this, R.layout.mtb_single_text_list_item, mArr, 0);
				vListView.setAdapter(adapter);
				vListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View parent, int position, long arg3) {
						Intent intent = new Intent(MTBAddRequestOfferPage.this, MTBAddRequestOfferPage2.class);
						intent.putExtra("SvcCatID", mArr.get(position).SvcCatID);
						intent.putExtra("SvcCat", mArr.get(position).SvcCat);
						intent.putExtra("mIsRequest", mIsRequest);
						intent.putExtra("mIsOffer", mIsOffer);
						startActivity(intent);
					}
				});
			}
			else {
				vLoadingBar.setVisibility(View.GONE);
				vLoadingText.setText("Error while loading. Please try again.");
			}
		}
	}
}
