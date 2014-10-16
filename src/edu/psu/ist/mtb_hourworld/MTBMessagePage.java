package edu.psu.ist.mtb_hourworld;

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
import com.korovyansk.android.slideout.SlideoutActivity;

import edu.psu.ist.mtb_hourworld.MTBOfferPage.downloadTBRequestAndOffer;
import edu.psu.ist.mtb_hourworld.R.color;
import edu.psu.ist.mtb_hourworld.account.MTBLoginPage;
import edu.psu.ist.mtb_hourworld.adapter.MTBMessageAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.group.MTBGroupMainPage;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.search.MTBSearchMainPage;
import edu.psu.ist.mtb_hourworld.settings.MTBSettings;
import edu.psu.ist.mtb_hourworld.sidebar.MTBSideBarActivity;
import edu.psu.ist.mtb_hourworld.tasks.MTBAddMessagePage;
import edu.psu.ist.mtb_hourworld.tasks.MTBAddRequestOfferPage;
import edu.psu.ist.mtb_hourworld.tasks.MTBMessageDetailPage;
import edu.psu.ist.mtb_hourworld.tasks.MTBTaskCategory;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MTBMessagePage extends Activity {

	private Button addBtn;
	private Button refreshBtn;
	private Button mapBtn;
	private SharedPreferences mPref;
	private Button menuBtn;
	private TextView mMenuText;
	private ProgressBar mLoadingBar;
    private TextView mLoadingText;
    
    private ArrayList<MTBTaskItems> mArr = new ArrayList<MTBTaskItems>();
    
	private ListView mListView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mtb_message_view);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);

        addBtn = (Button)findViewById(R.id.add_request);
        refreshBtn = (Button)findViewById(R.id.refresh);
        menuBtn = (Button)findViewById(R.id.menu_btn);
        
        // updated Jan. 22
        menuBtn.setEnabled(false);
        menuBtn.setVisibility(View.GONE);
        
        mMenuText = (TextView)findViewById(R.id.menu_text);
        
	    mLoadingBar = (ProgressBar)findViewById(R.id.spinner);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mListView = (ListView)findViewById(R.id.listview);
	    mapBtn = (Button)findViewById(R.id.map_btn);
        
	    new downloadTBRequestAndOffer().execute();
	    //setQuickAction();
	    
	    mMenuText.setText(" Announcements");
	    
	    setTitle("Announcements");
        getActionBar().setIcon(R.drawable.news);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(color.skype_blue));
	    
        /*
	    addBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MTBMessagePage.this, MTBAddMessagePage.class);
				startActivity(intent);
			}
		});
	    
	    refreshBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				mArr.clear();

			    mListView.setVisibility(View.GONE);
				mLoadingBar.setVisibility(View.VISIBLE);
			    mLoadingText.setVisibility(View.VISIBLE);
			    
			    mLoadingText.setText("Loading announcements...");

			    downloadTBRequestAndOffer myTB = new downloadTBRequestAndOffer();
				myTB.execute();
			}
		});
		

	    menuBtn.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
					SlideoutActivity.prepare(MTBMessagePage.this, R.id.inner_content, width);
					
					Intent intent = new Intent(MTBMessagePage.this, MTBSideBarActivity.class);
					intent.putExtra("from", Constants.FROM_MESSAGES);
					
					startActivityForResult(intent, Constants.SIDEBAR_MENU);
					overridePendingTransition(0, 0);
				}
			});
	    
	    mapBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MTBMessagePage.this, MTBMessageMapPage.class));
			}
		});
		*/
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_sm, menu);
 
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK && requestCode == 1){
			Intent intent = new Intent(MTBMessagePage.this, MTBLoginPage.class);
        	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
        	startActivity(intent);
	    	finish();
		}
		else if(resultCode == RESULT_OK && requestCode == Constants.SIDEBAR_MENU) {
			
			switch(data.getExtras().getInt("flag")) {
			case 0:
				// announcement
				break;
			case 1:
				finish();
				startActivity(new Intent(MTBMessagePage.this, MTBOfferPage.class));
				break;
			case 2:
				finish();
				startActivity(new Intent(MTBMessagePage.this, MTBRequestPage.class));
				break;
			case 3:
				finish();
				startActivity(new Intent(MTBMessagePage.this, MTBSearchMainPage.class));
				break;
			case 4:
				finish();
				startActivity(new Intent(MTBMessagePage.this, MTBGroupMainPage.class));
				break;
			case 5:
				finish();
				startActivity(new Intent(MTBMessagePage.this, MTBSettings.class));
				break;
			case 6:
				new AlertDialog.Builder(MTBMessagePage.this)
				.setTitle("Exit")
				.setIcon(R.drawable.hourworld_icon_30_30)
				.setMessage("Exit hOurworld?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// out of the application
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// nothing to do
					}
				})
				.show();
			}
		}
		else if(resultCode == RESULT_OK && requestCode == Constants.MTB_DETAIL_PAGE) {
			if(data.getExtras().getBoolean("refresh")) {
				mArr.clear();

			    mListView.setVisibility(View.GONE);
				mLoadingBar.setVisibility(View.VISIBLE);
			    mLoadingText.setVisibility(View.VISIBLE);
			    
			    mLoadingText.setText("Refreshing announcements...");

			    downloadTBRequestAndOffer myTB = new downloadTBRequestAndOffer();
				myTB.execute();
			}
			else {
				
			}
		}
	}
	
	/*
	 * Download Timebank Information from the server
	 */
	class downloadTBRequestAndOffer extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("Messages,0")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	
	        	Log.i("K", mPref.getString("access_token", "") + " / " + Integer.toString(mPref.getInt("EID", 0)) + " / " + Integer.toString(mPref.getInt("memID", 0)));
		    	
		    	
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
						//mNewTaskArr.clear();
						
						JSONArray jAry = jObj.getJSONArray("results");
						
						for(int i = 0; i < jAry.length(); i++){
							
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							MTBTaskItems item = new MTBTaskItems();
							item.setExp(jItemObj.getString("Exp"));
							item.setEblast(jItemObj.getString("Eblast"));
							item.setListMemID(jItemObj.getInt("listMbrID"));
							item.setPostNum(jItemObj.getInt("PostNum"));
							//item.setOwner(jItemObj.getString("Owner"));
							item.setProfileImage("http://www.hourworld.org/" + jItemObj.getString("Profile"));
							
							// only use date for timestamp
							if(!jItemObj.getString("timestamp").equals("null")) {
								String []timeStamp = jItemObj.getString("timestamp").split(" ");
								item.setTimeStamp(timeStamp[0]);
							}
							else {
								item.setTimeStamp("No datetime found");
							}
							
							
							item.setEmailAddress(jItemObj.getString("Email1"));
							item.setPhoneNumber(jItemObj.getString("Phone").replaceAll(" ", "-"));
							item.setListMemName(jItemObj.getString("Name"));
							
							JSONObject jLocObj = jItemObj.getJSONObject("mobLatLon");
							if(!jLocObj.getString("oLat").equals("null") && !jLocObj.getString("oLat").equals("") && jLocObj.getString("oLat").length() > 5) {
								item.setOLat(Double.parseDouble(jLocObj.getString("oLat")));
							}
							if(!jLocObj.getString("oLon").equals("null") && !jLocObj.getString("oLon").equals("") && jLocObj.getString("oLat").length() > 5) {
								item.setOLon(Double.parseDouble(jLocObj.getString("oLon")));
							}
							if(!jLocObj.getString("dLat").equals("null") && !jLocObj.getString("dLat").equals("") && jLocObj.getString("oLat").length() > 5) {
								item.setDLat(Double.parseDouble(jLocObj.getString("dLat")));
							}
							if(!jLocObj.getString("dLon").equals("null") && !jLocObj.getString("dLon").equals("") && jLocObj.getString("oLat").length() > 5) {
								item.setDLon(Double.parseDouble(jLocObj.getString("dLon")));
							}
							if(!jLocObj.getString("xDays").equals("null") && !jLocObj.getString("xDays").equals("")) {
								item.setXDays(Integer.parseInt(jLocObj.getString("xDays")));
							}
							
							
							//if(jItemObj.getBoolean("LatLonArray")) {
							//	item.setLatitude(jItemObj.getJSONArray("LatLonArray").getJSONObject(0).getDouble("LAT"));
							//	item.setLongitude(jItemObj.getJSONArray("LatLonArray").getJSONObject(0).getDouble("LON"));
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

				loadListView();
				
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				//mLoadingText.setVisibility(View.GONE);
				mLoadingText.setText("No new task available");
			}
		}
	}
	
	
	private void loadListView() {
		MTBMessageAdapter adapter = new MTBMessageAdapter(MTBMessagePage.this, R.layout.mtb_message_view_item, mArr);
		mListView.setAdapter(adapter);
        
		mListView.setTag(mArr);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> view, View v, int position, long arg3) {
				
				MTBTaskItems item = mArr.get(position);
				
				Intent intent = new Intent(MTBMessagePage.this, MTBMessageDetailPage.class);
				intent.putExtra("EID", item.getEID());
				intent.putExtra("postID", item.getPostNum());
				intent.putExtra("userID", item.getListMemID());
				intent.putExtra("username", item.getListMemName());
				intent.putExtra("SvcCat", item.getSvcCat());
				intent.putExtra("Descr", item.getEblast());
				intent.putExtra("timestamp", item.getTimeStamp());
				intent.putExtra("profileImage", item.getProfileImage());
				intent.putExtra("emailAddress", item.getEmailAddress());
				intent.putExtra("phoneNumber", item.getPhoneNumber());
				//intent.putExtra("latitude", item.getLatitude());
				//intent.putExtra("longitude", item.getLongitude());
				intent.putExtra("oLat", item.getOLat());
				intent.putExtra("oLon", item.getOLon());
				intent.putExtra("dLat", item.getDLat());
				intent.putExtra("dLon", item.getDLon());
				intent.putExtra("xDays", item.getXDays());
				
				startActivityForResult(intent, Constants.MTB_DETAIL_PAGE);
				
			}
        });
	}
	
	/**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_refresh:
            // refresh
        	refresh();
            return true;
        case R.id.action_new:
            // new action
        	add();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void refresh() {
    	mArr.clear();

	    mListView.setVisibility(View.GONE);
		mLoadingBar.setVisibility(View.VISIBLE);
	    mLoadingText.setVisibility(View.VISIBLE);
	    
	    mLoadingText.setText("Loading announcements...");

	    downloadTBRequestAndOffer myTB = new downloadTBRequestAndOffer();
		myTB.execute();
    }
    
    public void add() {
    	Intent intent = new Intent(MTBMessagePage.this, MTBAddMessagePage.class);
		startActivity(intent);
    }
}