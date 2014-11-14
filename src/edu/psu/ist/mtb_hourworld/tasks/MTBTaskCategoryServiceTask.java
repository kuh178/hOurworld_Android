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
import edu.psu.ist.mtb_hourworld.adapter.MTBNewTaskAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MTBTaskCategoryServiceTask extends Activity {

	private ArrayList<MTBTaskItems> mArr = new ArrayList<MTBTaskItems>();
	private ProgressBar mLoadingBar;
    private TextView mLoadingText;
    private ListView mListView;
    
    private SharedPreferences mPref;
	private TextView mMenuText;
	
	private int mSvcCatID;
	private int mSvcID;
	private String mSvcCat;
	private String mService;
	private String mIsRequest;
	private String mIsOffer;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_task_service_category_task);
	    
	    Intent gIntent = getIntent();
	    mSvcID = gIntent.getExtras().getInt("SvcID");
	    mService = gIntent.getExtras().getString("Service");
	    mSvcCatID = gIntent.getExtras().getInt("SvcCatID");
	    mSvcCat = gIntent.getExtras().getString("SvcCat");
	    mIsRequest = gIntent.getExtras().getString("mIsRequest");
	    mIsOffer = gIntent.getExtras().getString("mIsOffer");
	    
	    Log.i("K", "Service : " + mService);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	
	    mLoadingBar = (ProgressBar)findViewById(R.id.spinner);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mListView = (ListView)findViewById(R.id.listview);
        mMenuText = (TextView)findViewById(R.id.menu_text);

        mLoadingText.setText("Loading ...");
        
        if(mIsOffer.equals("T")) {
	    	setTitle("Offers");
	    	getActionBar().setIcon(R.drawable.offers);
		    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    }
	    else {
	    	setTitle("Requests");
	    	getActionBar().setIcon(R.drawable.requests);
		    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    }
        
        mArr.clear();
		//new downloadTBRequestAndOffer().execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_add, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		new downloadTBRequestAndOffer().execute();
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
	class downloadTBRequestAndOffer extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();  	
			//String url = new String(Constants.REGISTER_LINK);
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	
		    	if(mIsOffer.equals("T")) {
		    		entity.addPart("requestType", new StringBody("OfferSvc," + mSvcCatID + ":" + mSvcID)); // specify a type of this request
		    	}
		    	else {
		    		entity.addPart("requestType", new StringBody("RequestSvc," + mSvcCatID + ":" + mSvcID)); // specify a type of this request
		    	}
		    	
		    	
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
				
					Log.i("K", "login result: " + result);
				
					if(!jObj.getBoolean("success")){
						// show the failure message
					}
					else {
						mArr.clear();
						
						JSONArray jAry = jObj.getJSONArray("results");
						
						for(int i = 0; i < jAry.length(); i++){
							
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							MTBTaskItems item = new MTBTaskItems();
							item.setListMemID(jItemObj.getInt("listMbrID"));
							
							if(!jItemObj.getString("Descr").equals("null") && jItemObj.getString("Descr").length() > 0) {
								item.setDescription(jItemObj.getString("Descr"));
							}
							else {
								item.setDescription("No description found");
							}
							
							item.setSvcCat(jItemObj.getString("SvcCat"));
							
							String fullName = jItemObj.getString("Fname") + " " + jItemObj.getString("Lname");
							if(!fullName.equals("")) {
								item.setListMemName(jItemObj.getString("Fname") + " " + jItemObj.getString("Lname"));
							}
							else {
								item.setListMemName("No username found");
							}
							
							item.setSvcCatID(jItemObj.getInt("SvcCatID"));
							item.setSvcID(jItemObj.getInt("SvcID"));
							item.setService(jItemObj.getString("Service"));
							
							// only use date for timestamp
							if(!jItemObj.getString("timestamp").equals("null")) {
								String []timeStamp = jItemObj.getString("timestamp").split(" ");
								item.setTimeStamp(timeStamp[0]);
							}
							else {
								item.setTimeStamp("No datetime found");
							}
							
							
							item.setProfileImage(Constants.HOURWORLD + jItemObj.getString("Profile"));
							item.setEmailAddress(jItemObj.getString("Email1"));
							item.setPhoneNumber(jItemObj.getString("Phone").replaceAll(" ", "-"));
							
							JSONObject jLocObj = jItemObj.getJSONObject("mobLatLon");
							if(!jLocObj.getString("oLat").equals("null") && !jLocObj.getString("oLat").equals("")) {
								item.setOLat(Double.parseDouble(jLocObj.getString("oLat")));
							}
							if(!jLocObj.getString("oLon").equals("null") && !jLocObj.getString("oLon").equals("")) {
								item.setOLon(Double.parseDouble(jLocObj.getString("oLon")));
							}
							if(!jLocObj.getString("dLat").equals("null") && !jLocObj.getString("dLat").equals("")) {
								item.setDLat(Double.parseDouble(jLocObj.getString("dLat")));
							}
							if(!jLocObj.getString("dLon").equals("null") && !jLocObj.getString("dLon").equals("")) {
								item.setDLon(Double.parseDouble(jLocObj.getString("dLon")));
							}
							
							//if(!jItemObj.getJSONObject("LatLonArray").equals(null)) {
							//	item.setLatitude(Double.parseDouble(jItemObj.getJSONArray("LatLonArray").getJSONObject(0).getString("LAT")));
							//	item.setLongitude(Double.parseDouble(jItemObj.getJSONArray("LatLonArray").getJSONObject(0).getString("LON")));
							//}
							//else {
							//	item.setLatitude(0.0);
							//	item.setLongitude(0.0);
							//}
						
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
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return true;
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			if(returnValue) {
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
				
		
				MTBNewTaskAdapter adapter = new MTBNewTaskAdapter(MTBTaskCategoryServiceTask.this, R.layout.mtb_list_view_item, mArr);
				
				mListView.setAdapter(adapter);
				
				mListView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						
						MTBTaskItems item = mArr.get(position);
						
						Intent intent = new Intent(MTBTaskCategoryServiceTask.this, MTBTaskDetailPage.class);
						intent.putExtra("EID", item.getEID());
						intent.putExtra("userID", item.getListMemID());
						intent.putExtra("username", item.getListMemName());
						intent.putExtra("SvcCat", item.getSvcCat());
						intent.putExtra("Descr", item.getDescription());
						intent.putExtra("timestamp", item.getTimeStamp());
						intent.putExtra("profileImage", item.getProfileImage());
						intent.putExtra("emailAddress", item.getEmailAddress());
						intent.putExtra("phoneNumber", item.getPhoneNumber());
						
						intent.putExtra("SvcCatID", item.getSvcCatID());
						intent.putExtra("SvcID", item.getSvcID());
						intent.putExtra("Service", item.getService());
						
						intent.putExtra("latitude", item.getLatitude());
						intent.putExtra("longitude", item.getLongitude());
						
						intent.putExtra("oLat", item.getOLat());
						intent.putExtra("oLon", item.getOLon());
						intent.putExtra("dLat", item.getDLat());
						intent.putExtra("dLon", item.getDLon());
						
						intent.putExtra("isOffer", "T");
						intent.putExtra("isRequest", "F");
						
						startActivityForResult(intent, Constants.MTB_DETAIL_PAGE);
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
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        if (item.getItemId() == R.id.action_new) {
            // add
        	add();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
	
	public void add() {
    	Intent intent = new Intent(MTBTaskCategoryServiceTask.this, MTBAddRequestOfferPage3.class);
    	
    	intent.putExtra("SvcCatID", mSvcCatID);
    	intent.putExtra("SvcID", mSvcID);
    	intent.putExtra("SvcCat", mSvcCat);
    	intent.putExtra("Service", mService);
		intent.putExtra("mIsRequest", mIsRequest);
		intent.putExtra("mIsOffer", mIsOffer);
		startActivity(intent);
    }
}
