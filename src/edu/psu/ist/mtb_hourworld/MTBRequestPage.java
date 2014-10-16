package edu.psu.ist.mtb_hourworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

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

import edu.psu.ist.mtb_hourworld.MTBRequestPage.downloadTBRequestAndOffer;
import edu.psu.ist.mtb_hourworld.adapter.MTBNewTaskAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.group.MTBGroupMainPage;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.search.MTBSearchMainPage;
import edu.psu.ist.mtb_hourworld.settings.MTBSettings;
import edu.psu.ist.mtb_hourworld.sidebar.MTBSideBarActivity;
import edu.psu.ist.mtb_hourworld.tasks.MTBAddRequestOfferPage;
import edu.psu.ist.mtb_hourworld.tasks.MTBTaskCategory;
import edu.psu.ist.mtb_hourworld.tasks.MTBTaskDetailPage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MTBRequestPage extends Activity {

	private ArrayList<MTBTaskItems> mArr = new ArrayList<MTBTaskItems>();
	private ArrayList<MTBTaskItems> mNewTaskFilteredArr = new ArrayList<MTBTaskItems>();
	private ProgressBar mLoadingBar;
    private TextView mLoadingText;
    private TextView mFooterText;
    private ListView mListView;
    private Button refreshBtn;
    private Button filterBtn;
    private Button vMapBtn;
    private Button vCategoryBtn;
    
    private SharedPreferences mPref;
    
    private Button menuBtn;
	private TextView mMenuText;
	private Button addBtn;
	
	private ArrayList<Integer> mUserGroupID = new ArrayList<Integer>();
	private ArrayList<String> mCategoryArr = new ArrayList<String>();

	private int mOffset = 30;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_task_list_view);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	
	    mLoadingBar = (ProgressBar)findViewById(R.id.spinner);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mListView = (ListView)findViewById(R.id.listview);
	    menuBtn = (Button)findViewById(R.id.menu_btn);
	    
	    // updated Jan. 22
        menuBtn.setEnabled(false);
        menuBtn.setVisibility(View.GONE);
	    
        mMenuText = (TextView)findViewById(R.id.menu_text);
        mFooterText = (TextView)findViewById(R.id.footer_text);
        addBtn = (Button)findViewById(R.id.add_request);
        refreshBtn = (Button)findViewById(R.id.refresh);
        filterBtn = (Button)findViewById(R.id.fliter);
        vMapBtn = (Button)findViewById(R.id.map_btn);
        vCategoryBtn = (Button)findViewById(R.id.category_btn);
        
        mLoadingText.setText("Loading Request...");
        mFooterText.setText("Press '+' to add Request");
        mMenuText.setText(" Requests (last 30 days)");
        
        setTitle("Requests");
        getActionBar().setIcon(R.drawable.requests);
        
        /*
        addBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MTBRequestPage.this, MTBAddRequestOfferPage.class);
				intent.putExtra("mIsOffer", "F");
				intent.putExtra("mIsRequest", "T");
				startActivity(intent);
			}
		});
        
        filterBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MTBRequestPage.this, MTBTaskCategory.class);
				intent.putExtra("mIsOffer", "F");
				intent.putExtra("mIsRequest", "T");
				startActivity(intent);
			}
		});
    	
        refreshBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mArr.clear();
				mNewTaskFilteredArr.clear();
				mUserGroupID.clear();
				
				mLoadingBar.setVisibility(View.VISIBLE);
			    mLoadingText.setVisibility(View.VISIBLE);
			    mLoadingText.setText("Loading data...");
			    mListView.setVisibility(View.GONE);
				
				new downloadTBRequestAndOffer().execute();
				
			}
		});
        
        menuBtn.setOnClickListener(
    		new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
    				SlideoutActivity.prepare(MTBRequestPage.this, R.id.inner_content, width);
    					
    				Intent intent = new Intent(MTBRequestPage.this, MTBSideBarActivity.class);
    				intent.putExtra("from", Constants.FROM_OFFERS);
    				startActivityForResult(intent, Constants.SIDEBAR_MENU);
    				overridePendingTransition(0, 0);
    			}
    		});
        
        vMapBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MTBRequestPage.this, MTBOfferRequestMapPage.class);
				intent.putExtra("isFrom", 0);
				startActivity(intent);
			}
		});
        
        vCategoryBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final CharSequence[] items = mCategoryArr.toArray(new CharSequence[mCategoryArr.size()]);
				
			        AlertDialog.Builder builder = new AlertDialog.Builder(MTBRequestPage.this);
			        builder.setTitle("Select Category");
			        builder.setItems(items, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int item) {
			                // Do something with the selection
			            	
			            	final ArrayList<MTBTaskItems> mTempArr = new ArrayList<MTBTaskItems>();
			            	
			            	if (items[item].equals("All")) {
			            		mTempArr.addAll(mArr);
			            	}
			            	else if (items[item].equals("My Request")) {
			            		
			            		for(int i = 0 ; i < mArr.size() ; i++) {
				            		
				            		if (mPref.getInt("memID", 0) == mArr.get(i).getListMemID()) {
				            			mTempArr.add(mArr.get(i));
				            		}
				            	}
			            	}
			            	else {
			            
				            	for(int i = 0 ; i < mArr.size() ; i++) {
				            		
				            		if (items[item].equals(mArr.get(i).getSvcCat())) {
				            			mTempArr.add(mArr.get(i));
				            		}
				            	}
			            	}
			      
			            
			            	MTBNewTaskAdapter adapter = new MTBNewTaskAdapter(MTBRequestPage.this, R.layout.mtb_list_view_item, mTempArr);
							mListView.setAdapter(adapter);
							mListView.setOnItemClickListener(new OnItemClickListener(){

								@Override
								public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
									
									MTBTaskItems item = mTempArr.get(position);
									
									Intent intent = new Intent(MTBRequestPage.this, MTBTaskDetailPage.class);
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
									
									intent.putExtra("isOffer", "F");
									intent.putExtra("isRequest", "T");
									
									startActivityForResult(intent, Constants.MTB_DETAIL_PAGE);
								}
								
							});
			            }
			        });
			        AlertDialog alert = builder.create();
			        alert.show();
				
			}
		});
		*/

        mArr.clear();
		new downloadTBRequestAndOffer().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_actions, menu);
	 
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
		
		if(resultCode == RESULT_OK && requestCode == Constants.SIDEBAR_MENU) {
			
			switch(data.getExtras().getInt("flag")) {
			case 0:
				finish();
				startActivity(new Intent(MTBRequestPage.this, MTBMessagePage.class));
				break;
			case 1:
				// oneself
				break;
			case 2:
				finish();
				startActivity(new Intent(MTBRequestPage.this, MTBRequestPage.class));
				break;
			case 3:
				finish();
				startActivity(new Intent(MTBRequestPage.this, MTBSearchMainPage.class));
				break;
			case 4:
				finish();
				startActivity(new Intent(MTBRequestPage.this, MTBGroupMainPage.class));
				break;
			case 5:
				finish();
				startActivity(new Intent(MTBRequestPage.this, MTBSettings.class));
				break;
			case 6:
				new AlertDialog.Builder(MTBRequestPage.this)
				.setTitle("Exit")
				.setIcon(R.drawable.hourworld_icon_30_30)
				.setMessage("Exit Timebank?")
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
			    
			    mLoadingText.setText("Refreshing data...");

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
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();  	
			//String url = new String(Constants.REGISTER_LINK);
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("Requests," + mOffset)); // specify a type of this request
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
						ArrayList<String> tempCategoryArr = new ArrayList<String>();
						
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
							if(!tempCategoryArr.contains(jItemObj.getString("SvcCat"))) {
								tempCategoryArr.add(jItemObj.getString("SvcCat"));
							}
						}
						
						Collections.sort(tempCategoryArr);
						mCategoryArr.add("All");
						mCategoryArr.add("My Offers");
						mCategoryArr.addAll(tempCategoryArr);						

						// add data into the db
						//MobileTimeBankingDatabase mDB = new MobileTimeBankingDatabase(MobileTimeBankingNewRequest.this);
						//mDB.insertTBItem(mNewTaskArr);
					
						
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
				
		
				MTBNewTaskAdapter adapter = new MTBNewTaskAdapter(MTBRequestPage.this, R.layout.mtb_list_view_item, mArr);
				
				mListView.setAdapter(adapter);
				
				mListView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						
						MTBTaskItems item = mArr.get(position);
						
						Intent intent = new Intent(MTBRequestPage.this, MTBTaskDetailPage.class);
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
						
						intent.putExtra("isOffer", "F");
						intent.putExtra("isRequest", "T");
						
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
	
	/**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_search:
            // search action
        	search();
            return true;
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
    
    public void search() {
    	Intent intent = new Intent(MTBRequestPage.this, MTBTaskCategory.class);
		intent.putExtra("mIsOffer", "T");
		intent.putExtra("mIsRequest", "F");
		startActivity(intent);
    }
    
    public void refresh() {
    	mArr.clear();
		mNewTaskFilteredArr.clear();
		mUserGroupID.clear();
		
		mLoadingBar.setVisibility(View.VISIBLE);
	    mLoadingText.setVisibility(View.VISIBLE);
	    mLoadingText.setText("Loading data...");
	    mListView.setVisibility(View.GONE);
		
		new downloadTBRequestAndOffer().execute();
    }
    
    public void add() {
    	Intent intent = new Intent(MTBRequestPage.this, MTBAddRequestOfferPage.class);
		intent.putExtra("mIsRequest", "F");
		intent.putExtra("mIsOffer", "T");
		startActivity(intent);
    }
}
