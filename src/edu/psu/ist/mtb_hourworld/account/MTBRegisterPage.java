package edu.psu.ist.mtb_hourworld.account;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.utilities.MTBDownloadHandler;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

public class MTBRegisterPage extends FragmentActivity {

	private ArrayList<MTBExchange> mArr;
	
	private GoogleMap mMap;
	private GoogleMapOptions options = new GoogleMapOptions();
	private SupportMapFragment mMapFragment;
	
	private double mLatitude 		= 40.793409;
	private double mLongitude 		= -77.861824;
	
	private SharedPreferences mPref;
	
	public class MTBExchange {
		public double mLatitude;
		public double mLongitude;
		public int mExchangeMemID;
		public String mExchangeMemName;
		public int mActiveMems;
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_register_page);

	    mArr = new ArrayList<MTBExchange>();
	    
	    // preference setting
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    // initialize LocationManager
	 	mLatitude = Double.parseDouble(mPref.getString("latitude", "40.793409"));
	 	mLongitude = Double.parseDouble(mPref.getString("longitude", "-77.861824"));
	    
	    // download mArr
	    new getListOfExchange().execute();
	    
	    mMapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));
	    mMap = mMapFragment.getMap();
	    
	    options.mapType(GoogleMap.MAP_TYPE_SATELLITE).compassEnabled(false)
	    .rotateGesturesEnabled(false)
	    .zoomControlsEnabled(true)
	    .compassEnabled(true)
	    .tiltGesturesEnabled(false);
	    mMap.setMyLocationEnabled(true);
	 
	    mMap.moveCamera(CameraUpdateFactory.zoomTo(9));
	    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude,mLongitude)));	
	  
		setTitle("Pick an exchange to join");
		//getActionBar().setIcon(R.drawable.news);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	/*
	 * Download a list of exchanges
	 */
	class getListOfExchange extends AsyncTask<Void, Integer, Boolean>{
		
		private ProgressDialog mDialog;
		
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBRegisterPage.this, "Loading...", "Loading exchanges...", true);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			MTBDownloadHandler dHandler = new MTBDownloadHandler();
			String getResponse = dHandler.downloadJson(Constants.EXCHANGE);
			
			if(getResponse == null) {
				return false;
			}
			else {
				try{
					JSONObject jObj = new JSONObject(getResponse);
					
					/*
					if(jObj.getBoolean("success")) {

						
					}
					*/
					
					JSONArray jAry = jObj.getJSONArray("results");
					
					if(jAry.length() > 0) {
						for(int i = 0; i < jAry.length(); i++){
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							MTBExchange item = new MTBExchange();
							item.mLatitude = Double.parseDouble(jItemObj.getString("latitude"));
							item.mLongitude = Double.parseDouble(jItemObj.getString("longitude"));
							item.mExchangeMemID = Integer.parseInt(jItemObj.getString("memberID"));
							item.mExchangeMemName = jItemObj.getString("mbrName");
							//item.mActiveMems = Integer.parseInt(jItemObj.getString("mbrName")); // this one returns null, commented out
							
							mArr.add(item);
						}
						
						if(mArr.size() == 0) {
							return false;
						}
						
						return true;
					}
					
				} catch(Exception e){}
			}
			
			return true;
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			Log.i("K", "Num of exchanges : " + mArr.size());
			
			if(mDialog.isShowing()) {
				mDialog.dismiss();
			}
			
			if(returnValue) {
				setMapComponents();
			}
			else {
			
			}
		}
	}

	public void setMapComponents() {
		for(int i = 0 ; i < mArr.size() ; i++) {
			
			MTBExchange ex = mArr.get(i);
			mMap.addMarker(new MarkerOptions().position(new LatLng(ex.mLatitude, ex.mLongitude)).title(ex.mExchangeMemName).snippet("" + ex.mExchangeMemID));
		}
		
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent intent = new Intent(MTBRegisterPage.this, MTBRegisterPage2.class);
				intent.putExtra("exchangeID", Integer.parseInt(marker.getSnippet()));
				startActivity(intent);
			}
		});
	}

}
