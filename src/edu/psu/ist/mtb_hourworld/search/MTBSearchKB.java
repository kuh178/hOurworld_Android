package edu.psu.ist.mtb_hourworld.search;

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
import edu.psu.ist.mtb_hourworld.adapter.MTBNewTaskAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.search.MTBSearchBio.startSearch;
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

public class MTBSearchKB extends Activity {

	private ProgressBar mLoadingBar;
    private TextView mLoadingText;
    private ListView mListView;
    private ArrayList<MTBTaskItems> mArr = new ArrayList<MTBTaskItems>();
    
    private String mSearchTerm;
    private String mSearchType;
    
    private SharedPreferences mPref;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_search_list_view);
	    
	    mLoadingBar = (ProgressBar)findViewById(R.id.spinner);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mListView = (ListView)findViewById(R.id.listview);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    Intent gIntent = getIntent();
	    mSearchTerm = gIntent.getExtras().getString("term");
	    mSearchType = gIntent.getExtras().getString("type");
	    
	    setTitle("KB");
	    getActionBar().setIcon(R.drawable.search);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	
	    new startSearch().execute();
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
	
	class startSearch extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			String url = new String(Constants.AUTHENTICATE);
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("Search," + mSearchType)); // specify a type of this request
		    	entity.addPart("SearchChars", new StringBody(mSearchTerm));
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
				
					Log.i("K", "results: " + result);
				
					if(!jObj.getBoolean("success")){
						// show the failure message
					}
					else {
						mArr.clear();
						
						JSONArray jAry = jObj.getJSONArray("results");
						
						// return false if there's no value
						if(jAry.length() == 0) {
							return false;
						}
						
						for(int i = 0; i < jAry.length(); i++){
							
							JSONObject jItemObj = jAry.getJSONObject(i);

							MTBTaskItems item = new MTBTaskItems();
							//item.setEID(jItemObj.getInt("EID"));
							//item.setMemID(jItemObj.getInt("memID"));
							item.setRequestType(jItemObj.getString("requestType"));
							item.setRequestMode(jItemObj.getString("requestMod"));
							item.setSearchChar(jItemObj.getString("SearchChars"));
							item.setQuestion(jItemObj.getString("Question"));
							item.setAnswer(jItemObj.getString("Answer"));
							
							mArr.add(item);
						}
						
						return true;
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return false;
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			if(returnValue) {
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
				
				
				MTBNewTaskAdapter adapter = new MTBNewTaskAdapter(MTBSearchKB.this, R.layout.mtb_list_view_item, mArr);

				mListView.setAdapter(adapter);
				mListView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

						MTBTaskItems item = mArr.get(position);

						/*
						Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchDetailPage.class);
						intent.putExtra("EID", item.getEID());
						intent.putExtra("memID", item.getMemID());
						intent.putExtra("username", item.getFName() + " " + item.getLName());
						intent.putExtra("SvcCat", item.getSvcCat());
						intent.putExtra("Descr", item.getDescription());
						intent.putExtra("timestamp", item.getTimeStamp());
						intent.putExtra("profileImage", item.getProfileImage());
						intent.putExtra("emailAddress", item.getEmailAddress());
						intent.putExtra("phoneNumber", item.getPhoneNumber());

						startActivity(intent);
						*/
					}

				});
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				//mLoadingText.setVisibility(View.GONE);
				mLoadingText.setText("No new task available");
			}
		}
	}

}