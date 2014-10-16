package edu.psu.ist.mtb_hourworld.location;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.psu.ist.mtb_hourworld.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MTBAddTaskLocationPage extends MapActivity {

	private MapView mMapView;
	private MyLocationOverlay mMyOverLay;
	
	private double mLatitude 		= 40.793409;
	private double mLongitude 		= -77.861824;
	
	private List<Overlay> mOverlays;
	private MapController mapController;
	
	private Button mSaveBtn;
	private Button mCancelBtn;
	
	private boolean mPickOrigin = false;
	private boolean mPickDestination = false;
	
	private TouchedLocationOverlay mLocationOverlay;
	
	private Button vOriginBtn;
    private Button vDestinationBtn;
    
    private double mOLat;
    private double mOLon;
    private double mDLat;
    private double mDLon;
	
	// Handles Taps on the Google Map (called from TouchedLocationOverlay)
    Handler handler = new Handler(){
 
        // Invoked by the method onTap()
        // in the class CurrentLocationOverlay
        @Override
            public void handleMessage(Message msg) {
        	
            Bundle data = msg.getData();
 
            // Getting the Latitude of the location
            mLatitude = data.getDouble("latitude");
 
            // Getting the Longitude of the location
            mLongitude = data.getDouble("longitude");
            
            // Show the location in the google map
            if(data.getBoolean("origin")) {
            	
            	mPickOrigin = true;

            	// if the destination is clicked, there is no way to add another values to the origin latitude and longitude
            	if(!mPickDestination) {
            		vOriginBtn.setText("Origin added");
                	mOLat = mLatitude;
                    mOLon = mLongitude;
                    
                    showLocation(mOLat, mOLon, true, false);
            	}
            }
            else {
            	
            	mPickDestination = true;
            	
            	// nothing conflict with the origin location. simply store the values to the mDLat and mDLon variables
            	vDestinationBtn.setText("Destination added");
            	mDLat = mLatitude;
                mDLon = mLongitude;
                
                showLocation(mDLat, mDLon, true, false);
            }
            
        }
    };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_custom_location_map);
	   
	    Intent gIntent = getIntent();
	    mLatitude = gIntent.getExtras().getDouble("latitude");
	    mLongitude = gIntent.getExtras().getDouble("longitude");
	    
	    mSaveBtn = (Button)findViewById(R.id.save_btn);
	    mCancelBtn = (Button)findViewById(R.id.cancel_btn);
	    mCancelBtn.setEnabled(false);
	    
	    vOriginBtn = (Button)findViewById(R.id.origin_btn);
	    vDestinationBtn = (Button)findViewById(R.id.destination_btn);
	    
	    // inform a user of picking an origin destination first
	    new AlertDialog.Builder(MTBAddTaskLocationPage.this)
		.setTitle("Message")
		.setIcon(R.drawable.hourworld_icon_30_30)
		.setMessage("Please pick an origin and a destination location (click buttons at the top)")
		.setPositiveButton("Close", null)
		.show();
	    
	    // when the origin btn clicked
	    vOriginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				vOriginBtn.setEnabled(false);
				
				showLocation(mLatitude, mLongitude, false, true);
			}
		});
	    
	    // when the destination btn clicked
	    vDestinationBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(!mPickOrigin) {
					Toast.makeText(MTBAddTaskLocationPage.this, "Pick an origin location first", Toast.LENGTH_SHORT).show();
				}
				else {
					
					vDestinationBtn.setEnabled(false);
					
					showLocation(mLatitude, mLongitude, true, true);
				}
				
			}
		});
	    
	    // when the saved btn clicked
	    mSaveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MTBAddTaskLocationPage.this)
				.setTitle("Message")
				.setIcon(R.drawable.hourworld_icon_30_30)
				.setMessage("Save location info?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// send picked lat and lon back to the previous activity
						Intent in = new Intent();
						in.putExtra("olatitude", mOLat);
						in.putExtra("olongitude", mOLon);
						in.putExtra("dlatitude", mDLat);
						in.putExtra("dlongitude", mDLon);
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
				new AlertDialog.Builder(MTBAddTaskLocationPage.this)
				.setTitle("Message")
				.setIcon(R.drawable.hourworld_icon_30_30)
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
	    
	    mMapView = (MapView)findViewById(R.id.map);
		mMapView.setSatellite(false);
		
		// Add mylocation overlay
		mMyOverLay = new MyLocationOverlay(this, mMapView);

		mOverlays = mMapView.getOverlays();
		mOverlays.add(mMyOverLay);
		
        // Getting the MapController
        mapController = mMapView.getController();
        mapController.setZoom(17);
		
		mMyOverLay.runOnFirstFix(new Runnable() {

			@Override
			public void run() {
				mMapView.getController().animateTo(mMyOverLay.getMyLocation());
			}
		});
	}
	
	public void onResume() {
		super.onResume();
		mMyOverLay.enableMyLocation();
	}
	
	public void onPause() {
		super.onPause();
		mMyOverLay.disableMyLocation();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mMyOverLay.disableMyLocation();
	}
	
	private void showLocation(double latitude, double longitude, boolean isOriginDone, boolean isInitiated) {
 
		// there are no overlays initially. only remove an existing overlay after tapping the map
		if(!isInitiated) {
			 // Clearing the overlays (not remove myLocation, which is the blue dot icon)
			 mOverlays.remove(mOverlays.size()-1);
			 
			 if(isOriginDone) {
				// set the cancelBtn to "enable," users can start over this process
		        mCancelBtn.setEnabled(true);
			 }
		}
		
		mLocationOverlay = null;
		
		if(mOverlays.size() == 2 && isOriginDone) {
			Drawable marker = getResources().getDrawable(R.drawable.mm_20_blue);
        	
			// set the bound of the marker
    		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

    		// Creating an ItemizedOverlay
    		mLocationOverlay = new TouchedLocationOverlay(marker, handler, false);
		}
		else if(mOverlays.size() == 1){
			Drawable marker = getResources().getDrawable(R.drawable.mm_20_red);
			
			// set the bound of the marker
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

			// Creating an ItemizedOverlay
			mLocationOverlay = new TouchedLocationOverlay(marker, handler, true);
		}

		// Creating an instance of GeoPoint, to display in Google Map
        GeoPoint p = getPoint(latitude, longitude);
 
        // Creating an OverlayItem to mark the point
        OverlayItem overlayItem = new OverlayItem(p, "Item", "Item");
        
        // Locating the point in the Google Map
        mapController.animateTo(p);
        
        // Adding the OverlayItem in the LocationOverlay
        mLocationOverlay.addOverlay(overlayItem);
        
        // Adding locationOverlay to the overlay
        mOverlays.add(mLocationOverlay);
    }
	

	private GeoPoint getPoint(double lat, double lon) {
		return(new GeoPoint((int)(lat*1E6), (int)(lon*1E6)));
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}	

	// handling back button action
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {

			new AlertDialog.Builder(this)
			.setTitle("Message")
			.setIcon(R.drawable.hourworld_icon_30_30)
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
