package edu.psu.ist.mtb_hourworld.hours;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
import edu.psu.ist.mtb_hourworld.adapter.MTBMemberNameOnlyAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MTBHoursPickMember extends Activity {

	private ProgressBar mLoadingBar;
    private TextView mLoadingText;
    private ListView mListView;
    private ArrayList<Member> mArr = new ArrayList<Member>();
    private MTBMemberNameOnlyAdapter mAdapter;
    
    private SharedPreferences mPref;
    
    private static int PICKMEMBER = 1;
    
    public class Member {
    	public String name;
    	public int memID;
    }
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_hours_member);
	    
	    mLoadingBar = (ProgressBar)findViewById(R.id.spinner);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mListView = (ListView)findViewById(R.id.listview);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    setTitle("Search Member");
	    getActionBar().setIcon(R.drawable.search);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	
	    new startSearch().execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the options menu from XML
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_actions_search, menu);

	    // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
	       
	    	@Override
	        public boolean onQueryTextSubmit(String text) {
	            return false;
	        }

	        @Override
	        public boolean onQueryTextChange(String text) {
	        	
	        	if(!mAdapter.equals(null)) {
	        		mAdapter.getFilter().filter(text);
		            return true;
	        	}
	        	else{
	        		return false;
	        	}
	        }
	    });

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
	
	@Override
	public void onBackPressed() {
		Intent in = new Intent();
		in.putExtra("memID", 0);
		in.putExtra("memName", "");
		setResult(PICKMEMBER, in);
		
		finish();
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
		    	entity.addPart("requestType", new StringBody("getMbrs,0")); // specify a type of this request
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
				
					// clear the array first
					mArr.clear();
					JSONArray jAry = jObj.getJSONArray("results");
					
					// return false if there's no value
					if(jAry.length() == 0) {
						return false;
					}
					
					for(int i = 0; i < jAry.length(); i++){
						
						JSONObject jItemObj = jAry.getJSONObject(i);
						Member m = new Member();
						m.memID = jItemObj.getInt("MbrID");
						m.name = jItemObj.getString("Fname").trim() + " " + jItemObj.getString("Lname").trim();
						mArr.add(m);
					}
					
					// sort mArr by member name
					Collections.sort(mArr, new Comparator<Member>() {
				        @Override
				        public int compare(Member s1, Member s2) {
				            return s1.name.compareToIgnoreCase(s2.name);
				        }
				    });
					
					return true;
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
				
				// set adapter
				mAdapter = new MTBMemberNameOnlyAdapter(MTBHoursPickMember.this, R.layout.mtb_hour_member_list_item, mArr);

				mListView.setAdapter(mAdapter);
				mListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

						Member m = (Member) arg1.getTag();
						Intent in = new Intent();
						in.putExtra("memID", m.memID);
						in.putExtra("memName", m.name);
						setResult(PICKMEMBER, in);
						
						finish();
					}
				});
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				//mLoadingText.setVisibility(View.GONE);
				mLoadingText.setText("No data");
			}
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_search:
            // search action
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}