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
import edu.psu.ist.mtb_hourworld.location.MTBAddTaskLocationPage;
import edu.psu.ist.mtb_hourworld.location.MTBAddTaskLocationSimplifiedPage;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MTBAddRequestOfferPage3 extends FragmentActivity {

	private int mSvcCatID;
	private int mSvcID;
	private String mService;
	private double mOLat = 0.0;
	private double mOLon = 0.0;
	private double mDLat = 0.0;
	private double mDLon = 0.0;
	private String mIsOffer;
	private String mIsRequest;
	private String mExpiration = "365";
	private String mSvcCat;
	
	private GoogleMap mMap;
	private GoogleMapOptions options = new GoogleMapOptions();
	private SupportMapFragment mMapFragment;
	
	private SharedPreferences mPref;
	private ProgressDialog mDialog;
	
	private EditText vMessageTxt;
	private Button vMapBtn;
	private Button vSubmitBtn;
	private TextView vCategory;
	private TextView vHeaderTxt;
	
	private Spinner vSpinner;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_upload_request_offer);
	    
	    Intent gIntent = getIntent();
	    mSvcCatID = gIntent.getExtras().getInt("SvcCatID");
	    mSvcID = gIntent.getExtras().getInt("SvcID");
	    mSvcCat = gIntent.getExtras().getString("SvcCat");
	    mService = gIntent.getExtras().getString("Service");
	    mIsRequest = gIntent.getExtras().getString("mIsRequest");
	    mIsOffer = gIntent.getExtras().getString("mIsOffer");
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);

	    vMessageTxt = (EditText)findViewById(R.id.message_edittext);
	    vMapBtn = (Button)findViewById(R.id.location_btn);
	    vSubmitBtn = (Button)findViewById(R.id.submit_btn);
	    vCategory = (TextView)findViewById(R.id.category);
	    vHeaderTxt = (TextView)findViewById(R.id.header_title);
	    
	    if(mIsRequest.equals("T")) {
	    	//vHeaderTxt.setText(" [3/3] Finalize your request");
	    	
	    	setTitle("Add Request");
		    getActionBar().setIcon(R.drawable.requests);
		    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    	
	    	vCategory.setText("This request will appear in " + mSvcCat + " > " + mService);
	    }
	    else {
	    	//vHeaderTxt.setText(" [3/3] Finalize your offer");
	    	
	    	setTitle("Add Offer");
		    getActionBar().setIcon(R.drawable.offers);
		    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    	
	    	vCategory.setText("This offer will appear in " + mSvcCat + " > " + mService);
	    }
	    
	    vSpinner = (Spinner)findViewById(R.id.spinner);
	
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
	    
		vSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
				mExpiration = parent.getItemAtPosition(position).toString();
				
				if(mExpiration.equals("1 Year")) {
					mExpiration = "365";
				}
				else if(mExpiration.equals("9 Months")) {
					mExpiration = "270";
				}
				else if(mExpiration.equals("6 Months")) {
					mExpiration = "180";
				}
				else if(mExpiration.equals("3 Months")) {
					mExpiration = "90";
				}
				else if(mExpiration.equals("1 Month")) {
					mExpiration = "30";
				}
				else if(mExpiration.equals("3 Weeks")) {
					mExpiration = "21";
				}
				else if(mExpiration.equals("2 Weeks")) {
					mExpiration = "14";
				}
				else if(mExpiration.equals("1 Week")) {
					mExpiration = "7";
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
			
		});
		
	    vMapBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLocationLevelDialog();
			}
		});
	    
	    vSubmitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_send, menu);
 
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
		Intent intent = new Intent(MTBAddRequestOfferPage3.this, MTBAddTaskLocationSimplifiedPage.class);
		
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
			 
			 Log.i("K", "mOLat : " + mOLat + " mDLat : " + mDLat);
			 
			 if(mOLat != 0.0 || mOLon != 0.0) {
				 mMapFragment.getView().setVisibility(View.VISIBLE);
				 
				 mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
				 mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mDLat, mDLon)));	
				 mMap.addMarker(new MarkerOptions().position(new LatLng(mDLat, mDLon)));
			 }
		 }
		 else {
			 
		 }
	 }

	/*
	 * Download Timebank Information from the server
	 */
	class AddRequestOffer extends AsyncTask<Void, Integer, Boolean>{

		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBAddRequestOfferPage3.this, "Uploading...", "Uploading message...", true);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBAddRequestOfferPage3.this);
			return upload.addOfferRequest(mSvcCatID, mSvcID, mIsOffer, mIsRequest, vMessageTxt.getText().toString().trim(), mExpiration, mOLat, mOLon, mDLat, mDLon);
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
					Intent intent = new Intent(MTBAddRequestOfferPage3.this, MTBOfferPage.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				else {
					Intent intent = new Intent(MTBAddRequestOfferPage3.this, MTBRequestPage.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				*/
				
				finish();
				
				Toast.makeText(MTBAddRequestOfferPage3.this, "Task posted", Toast.LENGTH_SHORT).show();
				
			}
			else {
				Toast.makeText(MTBAddRequestOfferPage3.this, "Failed to post the task. Please try again...", Toast.LENGTH_SHORT).show();
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
        case R.id.action_send_now:
            // new action
        	upload();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void upload() {
    	if(vMessageTxt.getText().toString().trim().length() == 0) {
			Toast.makeText(MTBAddRequestOfferPage3.this, "Please add a description", Toast.LENGTH_SHORT).show();
		}
		else {
			new AddRequestOffer().execute();
		}
    }
}
