package edu.psu.ist.mtb_hourworld.group;

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

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.adapter.MTBGroupAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.items.MTBGroupItems;
import android.app.ActivityGroup;
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

public class MTBGroupJoinPage extends ActivityGroup {

	private SharedPreferences mPref;

	private ProgressBar mLoadingBar;
    private TextView mLoadingText;
    
    private ArrayList<MTBGroupItems> mArr = new ArrayList<MTBGroupItems>();
    
	private ListView mListView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mtb_group_new_view);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);

	    mLoadingBar = (ProgressBar)findViewById(R.id.loading_bar);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mListView = (ListView)findViewById(R.id.listview);

	    setTitle("Group list");
	    getActionBar().setIcon(R.drawable.group);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    new downloadGroupList().execute();

	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }

	/*
	 * Download Timebank Information from the server
	 */
	class downloadGroupList extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("EditGroups,LIST")); // specify a type of this request
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
					
					Log.i("K", "results: " + result);
					
					JSONObject jObj = new JSONObject(result);
				
					if(!jObj.getBoolean("success")){
						// show the failure message
					}
					else {
						//mNewTaskArr.clear();
						
						JSONArray jAry = jObj.getJSONArray("results");
						
						for(int i = 0; i < jAry.length(); i++){
							
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							MTBGroupItems item = new MTBGroupItems();
							item.setGroupID(jItemObj.getInt("groupID"));
							item.setGroupName(jItemObj.getString("groupName"));
							if(!jItemObj.isNull("groupProfile")) {
								item.setGroupProfile(jItemObj.getString("groupProfile"));
							}
							if(!jItemObj.isNull("groupOwner")) {
								item.setGroupOwnerID(jItemObj.getInt("groupOwner"));
							}
							
							mArr.add(item);
						}
						
						if(mArr.size() == 0) {
							return false;
						}
						
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				
				return false;
			}
	
			return true;
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			if(returnValue) {
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);

				loadListView();
				
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				//mLoadingText.setVisibility(View.GONE);
				mLoadingText.setText("No group found");
			}
		}
	}
	
	
	private void loadListView() {
		MTBGroupAdapter adapter = new MTBGroupAdapter(MTBGroupJoinPage.this, R.layout.mtb_group_view_item, mArr);
		mListView.setAdapter(adapter);
        
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> view, View v, int position, long arg3) {
				
				MTBGroupItems item = mArr.get(position);

				Intent intent = new Intent(MTBGroupJoinPage.this, MTBGroupDetailPage.class);
				intent.putExtra("groupID", item.getGroupID());
				intent.putExtra("groupName", item.getGroupName());
				intent.putExtra("groupDescription", item.getGroupProfile());
				intent.putExtra("groupOwnerID", item.getGroupOwnerID());
				
				startActivity(intent);
				//startActivityForResult(intent, Constants.MTB_DETAIL_PAGE);
				
			}
        });
	}
}