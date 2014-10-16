package edu.psu.ist.mtb_hourworld.tasks;

import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.psu.ist.mtb_hourworld.MTBOfferPage;
import edu.psu.ist.mtb_hourworld.MTBRequestPage;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.location.MTBAddTaskLocationSimplifiedPage;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MTBEditTaskPage extends FragmentActivity {

	private String mSvcCat;
	private String mService;
	private int mSvcCatID;
	private int mSvcID;
	private String mDescription;
	private int mMemID;
	
	private String mIsOffer;
	private String mIsRequest;
	
	private SharedPreferences mPref;
	
	private EditText vMessageTxt;
	private Button vMapBtn;
	private Button vSubmitBtn;
	private GoogleMap mMap;
	private GoogleMapOptions options = new GoogleMapOptions();
	private SupportMapFragment mMapFragment;
	private TextView vCategory;
	
	private RadioGroup vRadioGroup;
	private RadioButton vRequestOfferBtn;
	
	private double mLatitude = 0.0;
	private double mLongitude = 0.0;
	
	private double mOLat = 0.0;
	private double mOLon = 0.0;
	private double mDLat = 0.0;
	private double mDLon = 0.0;
	
	private ProgressDialog mDialog;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.mtb_edit_request_offer);
	    
	    Intent gIntent = getIntent();
	    mSvcCat = gIntent.getExtras().getString("SvcCat");
	    mService = gIntent.getExtras().getString("Service");
	    mSvcCatID = gIntent.getExtras().getInt("SvcCatID");
	    mSvcID = gIntent.getExtras().getInt("SvcID");
	    mDescription = gIntent.getExtras().getString("Descr");
	    mMemID = gIntent.getExtras().getInt("userID");
	    mLatitude = gIntent.getExtras().getDouble("latitude");
	    mLongitude = gIntent.getExtras().getDouble("longitude");
	    mIsOffer = gIntent.getExtras().getString("isOffer");
	    mIsRequest = gIntent.getExtras().getString("isRequest");
	    
	    mOLat = gIntent.getExtras().getDouble("oLat");
	    mOLon = gIntent.getExtras().getDouble("oLon");
	    mDLat = gIntent.getExtras().getDouble("dLat");
	    mDLon = gIntent.getExtras().getDouble("dLon");
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);

	    vMessageTxt = (EditText)findViewById(R.id.message_edittext);
	    vMapBtn = (Button)findViewById(R.id.location_btn);
	    vSubmitBtn = (Button)findViewById(R.id.submit_btn);
	    vCategory = (TextView)findViewById(R.id.category);
	    
	    vCategory.setText(mSvcCat + " > " + mService);
	    vMessageTxt.setText(mDescription);
	    
	    setTitle("Edit your task");
	    //getActionBar().setIcon(R.drawable.news);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    mMapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));
	    mMap = mMapFragment.getMap();
	    
	    options.mapType(GoogleMap.MAP_TYPE_SATELLITE).compassEnabled(false)
	    .rotateGesturesEnabled(false)
	    .zoomControlsEnabled(true)
	    .compassEnabled(false)
	    .tiltGesturesEnabled(false);
	
	    if (mDLat == 0.0 || mDLon == 0.0) {
	    	 // hide the map
		    mMapFragment.getView().setVisibility(View.INVISIBLE);
	    }
	    else {
		    mMapFragment.getView().setVisibility(View.VISIBLE);
	    }
	    
	    // location
	    mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
	    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mDLat, mDLon)));	
	    mMap.addMarker(new MarkerOptions().position(new LatLng(mDLat, mDLon)));
		
		Log.i("K", "olat: " + mOLat);
		Log.i("K", "olon: " + mOLon);
		Log.i("K", "dlat: " + mDLat);
		Log.i("K", "dlon: " + mDLon);
	
		vMapBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLocationLevelDialog();
			}
		});
	    
	    vSubmitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(vMessageTxt.getText().toString().trim().length() == 0) {
					Toast.makeText(MTBEditTaskPage.this, "Please add a message", Toast.LENGTH_SHORT).show();
				}
				else {
					
					new EditRequestOffer().execute();
				}
			}
		});
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
	
	private void showLocationLevelDialog() {
		Intent intent = new Intent(MTBEditTaskPage.this, MTBAddTaskLocationSimplifiedPage.class);
		
		intent.putExtra("latitude", Double.parseDouble(mPref.getString("latitude", "0.0")));
		intent.putExtra("longitude",Double.parseDouble(mPref.getString("longitude", "0.0")));
		startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 
		 if(resultCode == 1){
			 mOLat = data.getDoubleExtra("olatitude", 0.0);
			 mOLon = data.getDoubleExtra("olongitude", 0.0);
			 
			 mDLat = data.getDoubleExtra("dlatitude", 0.0);
			 mDLon = data.getDoubleExtra("dlongitude", 0.0);
			 
			 Log.i("K", "olat: " + mOLat);
			 Log.i("K", "olon: " + mOLon);
			 Log.i("K", "dlat: " + mDLat);
			 Log.i("K", "dlon: " + mDLon);
			 
			 vMapBtn.setText("Location info added");
			 
			 if	(mOLat != 0.0 || mDLat != 0.0) {
				 mMapFragment.getView().setVisibility(View.VISIBLE);
				 
				 mMap.clear();
				 
				 mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
				 mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mDLat, mDLon)));	
				 mMap.addMarker(new MarkerOptions().position(new LatLng(mDLat, mDLon)));
			 }
		 }
	 }

	/*
	 * Download Timebank Information from the server
	 */
	class EditRequestOffer extends AsyncTask<Void, Integer, Boolean>{

		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBEditTaskPage.this, "Uploading...", "Uploading edits...", true);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBEditTaskPage.this);
			return upload.editOfferRequest(mSvcCatID, mSvcID, mIsOffer, mIsRequest, vMessageTxt.getText().toString().trim(), mLatitude, mLongitude, mOLat, mOLon, mDLat, mDLon);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			if(mDialog.isShowing()) {
				mDialog.dismiss();
			}
			
			if(returnValue) {
				// if this is an offer, go back to the Offer page.
				/*
				if(mIsOffer.equals("T")) {
					Intent intent = new Intent(MTBEditTaskPage.this, MTBOfferPage.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				else {
					Intent intent = new Intent(MTBEditTaskPage.this, MTBRequestPage.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				*/
				
				finish();
				
			}
			else {
				Toast.makeText(MTBEditTaskPage.this, "Failed to post the task. Please try again...", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
