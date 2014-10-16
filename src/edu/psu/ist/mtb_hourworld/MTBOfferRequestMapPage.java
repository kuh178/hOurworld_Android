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
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.location.GPSHandler;
import edu.psu.ist.mtb_hourworld.location.MTBMapOverlay2;
import edu.psu.ist.mtb_hourworld.location.MTBMapOverlay3;
import edu.psu.ist.mtb_hourworld.tasks.MTBAddMessagePage;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MTBOfferRequestMapPage extends MapActivity {

	private ArrayList<MTBTaskItems> mArr;
	
	private MapView mMapView = null;
	private MyLocationOverlay mMyOverLay = null;
	private MapController mMapController = null;
	
	private double mLatitude 		= 0.0;
	private double mLongitude 		= 0.0;
	
	private Drawable mMarker;
	
	private boolean mSatelliteView = false;
	
	private SharedPreferences mPref;
	
	private GPSHandler gpsHandler;
	private boolean mIsBound;
	private boolean mFirstGps;
	private TextView mMenuText;
	
	private int mOffset = 50;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			gpsHandler = ((GPSHandler.GpsBinder) binder).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			gpsHandler = null;
		}
	};

	private void doBindService() {
		bindService(new Intent(MTBOfferRequestMapPage.this, GPSHandler.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;	
	}

	private void doUnbindService() {
		if(mIsBound) {
			if(mConnection != null) {

			}
			unbindService(mConnection);
			mIsBound = false;
		}
	}
	
	private int mIsFrom = 0;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_map);
	    
	    Intent gIntent = getIntent();
	    mIsFrom = gIntent.getExtras().getInt("isFrom");
	
        mArr = new ArrayList<MTBTaskItems>();
	    
	    // preference setting
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    // download mArr
	    new downloadTBRequestAndOffer().execute();
	    
	    // Set MapView and the longPressListener
		mMapView = (MapView)findViewById(R.id.map);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setSatellite(false);
	    
		// Add mylocation overlay
		mMyOverLay = new MyLocationOverlay(this, mMapView);
		
		// get map controller
		mMapController = mMapView.getController();
		mMapController.setZoom(14);

		// initialize LocationManager
		mLatitude = Double.parseDouble(mPref.getString("latitude", "40.793409"));
		mLongitude = Double.parseDouble(mPref.getString("longitude", "-77.861824"));
		mMapController.setCenter(getPoint(mLatitude, mLongitude));

		IntentFilter inFilter = new IntentFilter(GPSHandler.GPSHANDLERFILTER);
		registerReceiver(gpsReceiver, inFilter);
		
		// initialize marker
		mMarker = getResources().getDrawable(R.drawable.mm_20_blue);
		mMarker.setBounds(0, 0, mMarker.getIntrinsicWidth(), mMarker.getIntrinsicHeight());
        
		mMenuText = (TextView)findViewById(R.id.menu_text);
		mMenuText.setText(" Map view");
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

		private ProgressDialog mDialog;
		
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBOfferRequestMapPage.this, "Message", "Loading locations...", true);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	// offer
		    	if(mIsFrom == 0) {
		    		entity.addPart("requestType", new StringBody("Offers," + mOffset)); // specify a type of this request
		    	}
		    	else {
		    		entity.addPart("requestType", new StringBody("Requests," + mOffset)); // specify a type of this request
		    	}
		    	
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
							item.setListMemID(jItemObj.getInt("listMbrID"));
							if(jItemObj.getString("Descr").length() == 0) {
								item.setDescription("No description found");
							}
							else {
								item.setDescription(jItemObj.getString("Descr"));
							}
							
							item.setSvcCat(jItemObj.getString("SvcCat"));
							String fullName = jItemObj.getString("Fname") + " " + jItemObj.getString("Lname");
							if(fullName.length() == 0) {
								item.setListMemName("No username found");
							}
							else {
								item.setListMemName(fullName);
							}
							
							item.setSvcCatID(jItemObj.getInt("SvcCatID"));
							item.setSvcID(jItemObj.getInt("SvcID"));
							item.setService(jItemObj.getString("Service"));
							
							// only use date for timestamp
							String []timeStamp = jItemObj.getString("timestamp").split(" ");
							item.setTimeStamp(timeStamp[0]);
							
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
			
			if(mDialog.isShowing()) {
				mDialog.dismiss();
			}
			
			if(returnValue) {
				setMapComponents();
			}
			else {
				Toast.makeText(MTBOfferRequestMapPage.this, "Error while fetching data. Please try again", Toast.LENGTH_SHORT).show();
			}
		}
	}

	// get updated GPS data by the broadcast receiver
	private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle extras = intent.getExtras();

			mLatitude = extras.getDouble("latitude");
			mLongitude = extras.getDouble("longitude");

			// convert points into GeoPoint
		    GeoPoint gPoint = getPoint(mLatitude, mLongitude);

		    // center the map
		    if(mFirstGps) {
		    	mMapController.setCenter(gPoint);
		    	mFirstGps = false;
		    }
		}	
	};
	
	private GeoPoint getPoint(double lat, double lon) {
		return(new GeoPoint((int)(lat*1E6), (int)(lon*1E6)));
	}
	
	
	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		
		// resume my location overlay
		mMyOverLay.enableMyLocation();
		mMyOverLay.enableCompass();
		
		// bind Service
		doBindService();
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		
		mMyOverLay.disableCompass();
		mMyOverLay.disableMyLocation();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		doUnbindService();
		if(gpsReceiver != null) {
			unregisterReceiver(gpsReceiver);
	
		}
	}
	
	private void setMapComponents() {
		
		Log.i("K", "mArr size  : " + mArr.size());

		mMapView.getOverlays().clear();
		mMapView.invalidate();
		
		mMapView.getOverlays().add(new MTBMapOverlay3(mMapView, mMarker, mArr, mIsFrom));
		mMapView.getOverlays().add(mMyOverLay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, 1, 0, getString(R.string.my_location)).setIcon(android.R.drawable.ic_menu_mylocation);
		menu.add(0, 2, 0, getString(R.string.satellite_view)).setIcon(android.R.drawable.ic_menu_mapmode);
		
		return true;
	}
	
	//Menu option selection handling
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case 1:
				if(mLatitude != 0.0 && mLongitude != 0.0) {
					GeoPoint currentPoint = new GeoPoint((int)(mLatitude * 1E6), (int)(mLongitude * 1E6));

					mMapController = mMapView.getController();
					mMapController.animateTo(currentPoint);
				}
				
				return true;		
			case 2:
				if(mSatelliteView) {
					mMapView.setSatellite(false);
					mSatelliteView = false;
				}
				else {
					mMapView.setSatellite(true);
					mSatelliteView = true;
				}
				return true;
		}
		return false;
	}
}
