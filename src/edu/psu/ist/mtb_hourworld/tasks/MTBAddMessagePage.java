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

import edu.psu.ist.mtb_hourworld.MTBMessagePage;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.location.MTBAddTaskLocationPage;
import edu.psu.ist.mtb_hourworld.location.MTBAddTaskLocationSimplifiedPage;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MTBAddMessagePage extends FragmentActivity {

	private SharedPreferences mPref;
	private ProgressDialog mDialog;
	
	private double mOLat = 0.0;
	private double mOLon = 0.0;
	private double mDLat = 0.0;
	private double mDLon = 0.0;
	
	private EditText vMessageTxt;
	private Button vMapBtn;
	private Button vSubmitBtn;
	private Spinner vXDayBtn;
	
	private GoogleMap mMap;
	private GoogleMapOptions options = new GoogleMapOptions();
	private SupportMapFragment mMapFragment;

	private int mXDay = 14;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_upload_message);
	
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);

	    vMessageTxt = (EditText)findViewById(R.id.message_edittext);
	    vMapBtn = (Button)findViewById(R.id.location_btn);
	    vSubmitBtn = (Button)findViewById(R.id.submit_btn);
	    vXDayBtn = (Spinner)findViewById(R.id.select_xDay);
	    
	    setTitle("Add Announcement");
        getActionBar().setIcon(R.drawable.news);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    mMapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));
	    mMap = mMapFragment.getMap();
	    
	    options.mapType(GoogleMap.MAP_TYPE_SATELLITE).compassEnabled(false)
	    .rotateGesturesEnabled(false)
	    .zoomControlsEnabled(true)
	    .compassEnabled(false)
	    .tiltGesturesEnabled(false);
	    
	    // hide the map
	    mMapFragment.getView().setVisibility(View.INVISIBLE);
	    
	    // map
	    mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
	    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mDLat, mDLon)));	
	    mMap.addMarker(new MarkerOptions().position(new LatLng(mDLat, mDLon)));
	    
	    vMapBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLocationLevelDialog();
			}
		});
	    
	    /*
	    vSubmitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(vMessageTxt.getText().toString().trim().length() == 0) {
					Toast.makeText(MTBAddMessagePage.this, "Please add an announcement", Toast.LENGTH_SHORT).show();
				}
				else {
					new AddMessage().execute();
				}
			}
		});
		*/
	    
	    vXDayBtn.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				String pickedDay = parent.getItemAtPosition(position).toString(); 
				if(pickedDay.equals("14 days")) {
					mXDay = 14;
				}
				else if(pickedDay.equals("13 days")) {
					mXDay = 13;
				}
				else if(pickedDay.equals("12 days")) {
					mXDay = 12;
				}
				else if(pickedDay.equals("11 days")) {
					mXDay = 11;
				}
				else if(pickedDay.equals("10 days")) {
					mXDay = 10;
				}
				else if(pickedDay.equals("9 days")) {
					mXDay = 9;
				}
				else if(pickedDay.equals("8 days")) {
					mXDay = 8;
				}
				else if(pickedDay.equals("7 days")) {
					mXDay = 7;
				}
				else if(pickedDay.equals("6 days")) {
					mXDay = 6;
				}
				else if(pickedDay.equals("5 days")) {
					mXDay = 5;
				}
				else if(pickedDay.equals("4 days")) {
					mXDay = 4;
				}
				else if(pickedDay.equals("3 days")) {
					mXDay = 3;
				}
				else if(pickedDay.equals("2 days")) {
					mXDay = 2;
				}
				else if(pickedDay.equals("1 day")) {
					mXDay = 1;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	    });
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_send, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	private void showLocationLevelDialog() {
		Intent intent = new Intent(MTBAddMessagePage.this, MTBAddTaskLocationSimplifiedPage.class);
		
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
			 
			 vMapBtn.setText("Location info added");
			 
			 Log.i("K", "mOLat : " + mOLat + " mDLat : " + mDLat);
			 
			 if	(mOLat != 0.0 || mDLat != 0.0) {
				 mMapFragment.getView().setVisibility(View.VISIBLE);
				 
				 mMap.clear();
				 
				 mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
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
	class AddMessage extends AsyncTask<Void, Integer, Boolean>{

		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBAddMessagePage.this, "Uploading...", "Uploading announcement...", true);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBAddMessagePage.this);
			return upload.addMessage(vMessageTxt.getText().toString().trim(), mOLat, mOLon, mDLat, mDLon, mXDay);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			if(mDialog.isShowing()) {
				mDialog.dismiss();
			}
			
			if(returnValue) {
				// go back to the message page...
				Intent intent = new Intent(MTBAddMessagePage.this, MTBMessagePage.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
			else {
				
			}
		}
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
	
	/**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        if(item.getItemId() == R.id.action_send_now) {
        	// new action
        	upload();
            return true;
        }
        else {
        	return super.onOptionsItemSelected(item);
        }
    }
    
    public void upload() {
    	if(vMessageTxt.getText().toString().trim().length() == 0) {
			Toast.makeText(MTBAddMessagePage.this, "Please add an announcement", Toast.LENGTH_SHORT).show();
		}
		else {
			new AddMessage().execute();
		}
		
    }
}
