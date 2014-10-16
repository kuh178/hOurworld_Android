package edu.psu.ist.mtb_hourworld.location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.psu.ist.mtb_hourworld.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MTBAddTaskLocationSimplifiedPage extends Activity {

	private GoogleMap mMap;
	private GoogleMapOptions options = new GoogleMapOptions();
	
	private double mLatitude 		= 40.793409;
	private double mLongitude 		= -77.861824;
	
	private Button mSaveBtn;
	private Button mCancelBtn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_custom_location_map_simplified);
	   
	    Intent gIntent = getIntent();
	    mLatitude = gIntent.getExtras().getDouble("latitude");
	    mLongitude = gIntent.getExtras().getDouble("longitude");
	    
	    mSaveBtn = (Button)findViewById(R.id.save_btn);
	    mCancelBtn = (Button)findViewById(R.id.cancel_btn);
	    mCancelBtn.setEnabled(false);
	    
	    // inform a user of picking an origin destination first
	    new AlertDialog.Builder(MTBAddTaskLocationSimplifiedPage.this)
		.setTitle("Message")
		//.setIcon(R.drawable.hourworld_icon_30_30)
		.setMessage("Tap the map to add a location")
		.setPositiveButton("Close", null)
		.show();

	    // when the saved btn clicked
	    mSaveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MTBAddTaskLocationSimplifiedPage.this)
				.setTitle("Message")
				//.setIcon(R.drawable.hourworld_icon_30_30)
				.setMessage("Save location info?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// send picked lat and lon back to the previous activity
						Intent in = new Intent();
						in.putExtra("olatitude", mLatitude);
						in.putExtra("olongitude", mLongitude);
						in.putExtra("dlatitude", mLatitude);
						in.putExtra("dlongitude", mLongitude);
						setResult(1, in);
						
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.show();
				
			}
		});
	    
	    // not used yet (06.21.2013)
	    mCancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MTBAddTaskLocationSimplifiedPage.this)
				.setTitle("Message")
				//.setIcon(R.drawable.hourworld_icon_30_30)
				.setMessage("Reset the locations?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						/* handle this later
						if(mPickOrigin && mPickDestination) {
							mOverlays.remove(0);
							mOverlays.remove(1);
						}
						if(mPickOrigin && !mPickDestination) {
							mOverlays.remove(0);
						}

						mPickOrigin = false;
						mPickDestination = false;
						
						vOriginBtn.setEnabled(true);
						vDestinationBtn.setEnabled(true);
						
						vOriginBtn.setText("Add origin");
						vDestinationBtn.setText("Add destination");
						
						mCancelBtn.setEnabled(false);
						*/
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
			}
		});
	    
	    mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    options.mapType(GoogleMap.MAP_TYPE_SATELLITE).compassEnabled(false)
	    .rotateGesturesEnabled(false)
	    .zoomControlsEnabled(true)
	    .compassEnabled(true)
	    .tiltGesturesEnabled(false);
	    mMap.setMyLocationEnabled(true);
	 
	    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
	    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude,mLongitude)));	
	  
	    mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)));
	    mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				mLatitude = point.latitude;
				mLongitude = point.longitude;
				
				mMap.clear();
				mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)));
			}
	    });
	}
	
	public void onResume() {
		super.onResume();
		//mMyOverLay.enableMyLocation();
	}
	
	public void onPause() {
		super.onPause();
		//mMyOverLay.disableMyLocation();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//mMyOverLay.disableMyLocation();
	}

	// handling back button action
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {

			new AlertDialog.Builder(this)
			.setTitle("Message")
			//.setIcon(R.drawable.hourworld_icon_30_30)
			.setMessage("Save location info?")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent in = new Intent();
					in.putExtra("loc_latitude", mLatitude);
					in.putExtra("loc_longitude", mLongitude);
					setResult(1, in);
					
					finish();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.show();
		}
		
		return false;
	}
}
