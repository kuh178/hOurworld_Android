package edu.psu.ist.mtb_hourworld.tasks;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.profile.MTBProfilePage;
import edu.psu.ist.mtb_hourworld.utilities.MTBImageLoader;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MTBTaskDetailPage extends FragmentActivity {

	private TextView vType;
	private TextView vDescription;
	private TextView vUsername;
	private TextView vDateTime;
	private TextView vLocationNotAvailable;
	private TextView vTitleText;
	private ImageView vImageView;
	
	private LinearLayout vOriginLocLayout;
	private LinearLayout vDestLocLayout;
	
	private int mEID;
	private int mMemID;
	private String mDescription;
	private String mSvcCat;
	private String mUsername;
	private int mSvcCatID;
	private int mSvcID;
	private String mService;
	private String mTimeStamp;
	private double mLatitude;
	private double mLongitude;
	
	private double mOLat;
	private double mOLon;
	private double mDLat;
	private double mDLon;
	
	private String mProfileImage;
	private String mEmailAddress;
	private String mPhoneNumber;
	
	private Button vReplyMessageBtn;
	private Button vRemoveMessageBtn;
	private Button vReportHoursBtn;
	private Button vEditMessageBtn;
	
	private SharedPreferences mPref;
	
	private GoogleMap mMap;
	private GoogleMapOptions options = new GoogleMapOptions();
	private SupportMapFragment mMapFragment;
	
	private String mIsOffer;
	private String mIsRequest;
	
	private LinearLayout vLocLayout;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mtb_task_detail);

	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    // get data from previous intent
	    Intent gIntent = getIntent();
	    mEID = gIntent.getExtras().getInt("EID");
	    mMemID = gIntent.getExtras().getInt("userID");
	    mUsername = gIntent.getExtras().getString("username");
	    mSvcCat = gIntent.getExtras().getString("SvcCat");
	    mDescription = gIntent.getExtras().getString("Descr");
	    mTimeStamp = gIntent.getExtras().getString("timestamp");
	    mProfileImage = gIntent.getExtras().getString("profileImage");
	    mEmailAddress = gIntent.getExtras().getString("emailAddress");
	    mPhoneNumber = gIntent.getExtras().getString("phoneNumber");
	    mSvcCatID = gIntent.getExtras().getInt("SvcCatID");
	    mSvcID = gIntent.getExtras().getInt("SvcID");
	    mService = gIntent.getExtras().getString("Service");
	    mLatitude = gIntent.getExtras().getDouble("latitude");
	    mLongitude = gIntent.getExtras().getDouble("longitude");
	    
	    Log.i("K", "mPhoneNumber : " + mPhoneNumber);
	    
	    mOLat = gIntent.getExtras().getDouble("oLat");
	    mOLon = gIntent.getExtras().getDouble("oLon");
	    mDLat = gIntent.getExtras().getDouble("dLat");
	    mDLon = gIntent.getExtras().getDouble("dLon");
	    
	    mIsOffer = gIntent.getExtras().getString("isOffer");
	    mIsRequest = gIntent.getExtras().getString("isRequest");

		// initialize layouts
	    vType 			 = (TextView)findViewById(R.id.type);
	    vTitleText		 = (TextView)findViewById(R.id.title_text);
	    vDescription 	 = (TextView)findViewById(R.id.description);
	    vUsername 		 = (TextView)findViewById(R.id.username);
	    vDateTime 		 = (TextView)findViewById(R.id.datetime);
	    vLocationNotAvailable = (TextView)findViewById(R.id.no_location);
	    
	    vImageView = (ImageView)findViewById(R.id.user_image);
	    
	    vReportHoursBtn 	= (Button)findViewById(R.id.report_hour);
	    vRemoveMessageBtn 	= (Button)findViewById(R.id.remove_message);
	    vReplyMessageBtn 	= (Button)findViewById(R.id.reply_message);
	    vEditMessageBtn		= (Button)findViewById(R.id.edit_message);
	    
	    vOriginLocLayout 	= (LinearLayout)findViewById(R.id.origin_loc_layout);
		vDestLocLayout	 	= (LinearLayout)findViewById(R.id.dest_loc_layout);
	    vLocLayout = (LinearLayout)findViewById(R.id.location_layout);
		
		if(mIsOffer.equals("T")) {
			//vTitleText.setText(" Offer details");
			setTitle("Offer details");
			getActionBar().setIcon(R.drawable.offers);
			//getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		else {
			//vTitleText.setText(" Request details");
			setTitle("Request details");
			getActionBar().setIcon(R.drawable.requests);
			//getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	    
		mMapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));
	    mMap = mMapFragment.getMap();
	    
	    options.mapType(GoogleMap.MAP_TYPE_SATELLITE).compassEnabled(false)
	    .rotateGesturesEnabled(false)
	    .zoomControlsEnabled(true)
	    .compassEnabled(false)
	    .tiltGesturesEnabled(false);
	    
	    if (mDLat == 0.0 || mDLon == 0.0 || (mPref.getBoolean("sharing_location", true) == false && mMemID == mPref.getInt("memID", 0))) {
	    	 // hide the map
		    mMapFragment.getView().setVisibility(View.INVISIBLE);
		    vLocLayout.setVisibility(View.GONE);
	    }
	    else {
		    mMapFragment.getView().setVisibility(View.VISIBLE);
		    vLocLayout.setVisibility(View.VISIBLE);
	    }
	
	    // location
	    mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
	    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mDLat, mDLon)));	
	    mMap.addMarker(new MarkerOptions().position(new LatLng(mDLat, mDLon)));
	    
	    // add description
	    vType.setText(mSvcCat + " > " + mService);
	    vDescription.setText(Html.fromHtml(mDescription));
	    vDescription.setAutoLinkMask(Linkify.WEB_URLS);
	    
	    // if this task is from me or others?
	    if(mMemID == mPref.getInt("memID", 0)) {
	    	vUsername.setText(mUsername + " (You)");
	    	//vEditMessageBtn.setVisibility(View.VISIBLE);
	    	//vEditMessageBtn.setEnabled(true);
	    	vRemoveMessageBtn.setEnabled(true);
	    }
	    else {
	    	vUsername.setText(mUsername);
	    	//vEditMessageBtn.setVisibility(View.GONE);
	    	//vEditMessageBtn.setEnabled(true);
	    	vRemoveMessageBtn.setEnabled(false);
	    }
	    
	    // datetime
	    vDateTime.setText(mTimeStamp);
	   
	    // profile image
	    MTBImageLoader imageLoader = new MTBImageLoader(this);
		vImageView.setTag(mProfileImage);
		imageLoader.DisplayImage(mProfileImage, this, vImageView);
		
		// when the profile image clicked
		vImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mMemID != 0) {
					// open user profile page
					Intent intent = new Intent(MTBTaskDetailPage.this, MTBProfilePage.class);
					intent.putExtra("userID", mMemID);
					startActivity(intent);
				}
			}
		});
		
		/*
		vReportHoursBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(MTBTaskDetailPage.this)
				.setTitle("Message")
				.setIcon(R.drawable.hourworld_icon_30_30)
				.setMessage("Did you provide or receive this service?")
				.setPositiveButton("Provided", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// report hours
						Intent intent = new Intent(MTBTaskDetailPage.this, MTBReportHoursPage.class);
						intent.putExtra("SvcCat", mSvcCat);
						intent.putExtra("Service", mService);
						intent.putExtra("SvcCatID", mSvcCatID);
						intent.putExtra("SvcID", mSvcID);
						intent.putExtra("Descr", mDescription);
						intent.putExtra("userID", mMemID);
						intent.putExtra("Provided", true);
						
						startActivity(intent);
					}
				})
				.setNegativeButton("Received", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// report hours
						Intent intent = new Intent(MTBTaskDetailPage.this, MTBReportHoursPage.class);
						intent.putExtra("SvcCat", mSvcCat);
						intent.putExtra("Service", mService);
						intent.putExtra("SvcCatID", mSvcCatID);
						intent.putExtra("SvcID", mSvcID);
						intent.putExtra("Descr", mDescription);
						intent.putExtra("userID", mMemID);
						intent.putExtra("Provided", false);
						
						startActivity(intent);
					}
				})
				.show();
			}
		});
		*/
		
		/*
		// when then report hour button clicked
		vEditMessageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// edit task
				Intent intent = new Intent(MTBTaskDetailPage.this, MTBEditTaskPage.class);
				intent.putExtra("SvcCat", mSvcCat);
				intent.putExtra("Service", mService);
				intent.putExtra("SvcCatID", mSvcCatID);
				intent.putExtra("SvcID", mSvcID);
				intent.putExtra("Descr", mDescription);
				intent.putExtra("userID", mMemID);
				intent.putExtra("latitude", mLatitude);
				intent.putExtra("longitude", mLongitude);
				intent.putExtra("isOffer", mIsOffer);
				intent.putExtra("isRequest", mIsRequest);
				intent.putExtra("oLat", mOLat);
				intent.putExtra("oLon", mOLon);
				intent.putExtra("dLat", mDLat);
				intent.putExtra("dLon", mDLon);
				
				startActivity(intent);
				
			}
		});
		*/
		
		/*
		// when the reply button clicked
		vRemoveMessageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mMemID == mPref.getInt("memID", 0)) {
			   		new AlertDialog.Builder(MTBTaskDetailPage.this)
			   		.setMessage("Remove this task?")
			   		.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//new uploadRemoveMessage().execute();
							new deleteTaskItem().execute();
						}			   			
			   		})
			   		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// do nothing
						}
			   		})
			   		.show();
				}
				else {
					// reply to this message
				}
			}
		});
		*/
		
		/*
		vReplyMessageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mEmailAddress, null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Service inquiry");
				emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi there,\n\nI'm inquiring about your listing. \n\n" + mDescription + "\n\n");
				startActivity(Intent.createChooser(emailIntent, "Service inquiry"));

			}
		});
		*/
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_task, menu);
        
        if(mMemID == mPref.getInt("memID", 0)) {
        	menu.findItem(R.id.action_edit).setVisible(true);
        	menu.findItem(R.id.action_remove).setVisible(true);
	    }
	    else {
	    	menu.findItem(R.id.action_edit).setVisible(false);
	    	menu.findItem(R.id.action_remove).setVisible(false);
	    }
 
        return super.onCreateOptionsMenu(menu);
    }
	
	
	/*
	class uploadRemoveMessage extends AsyncTask<Void, Void, Boolean>{

		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(MTBTaskDetailPage.this, "Removing the task", "Please wait...");
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			MTBUploadHandler upload = new MTBUploadHandler(MTBTaskDetailPage.this);
			return upload.cancelTask(mMemID, mEID);
		}
		
		@Override
		protected void onPostExecute(Boolean returnValue) {
			if(dialog.isShowing()) {
				dialog.dismiss();
			}
			
			if(returnValue) {
				Toast.makeText(MTBTaskDetailPage.this, "Task removed.", Toast.LENGTH_SHORT).show();
				
				finish();
				
				Intent intent = new Intent(MTBTaskDetailPage.this, MTBCreateNewTaskPage.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			else {
				Toast.makeText(MTBTaskDetailPage.this, "Error while removing the task. Please try again", Toast.LENGTH_SHORT).show();
			}
		}
	}
	*/
	
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
	

	class TimeInfo {
		public String from;
		public String to;
		public long fromUnix;
		public long toUnix;
	}
		
	class deleteTaskItem extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBTaskDetailPage.this);
			mDialog.setTitle("Removing the task");
			mDialog.setMessage("Please wait...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
					Toast.makeText(MTBTaskDetailPage.this, "Not posted", Toast.LENGTH_SHORT).show();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBTaskDetailPage.this);
			return upload.deleteItem(mSvcCatID, mSvcID);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		Intent intent = getIntent();
	  		
	  		if(returnValue) {
	  			Toast.makeText(MTBTaskDetailPage.this, "Successfully removed", Toast.LENGTH_SHORT).show();
				intent.putExtra("refresh", true);
	  		}
	  		else {
	  			Toast.makeText(MTBTaskDetailPage.this, "Error while removing. Please try again...", Toast.LENGTH_SHORT).show();
				intent.putExtra("refresh", false);
	  		}
	  		
	  		setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	/**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        if (item.getItemId() == R.id.action_report) {
            // report action
        	report();
            return true;
        }
        else if(item.getItemId() == R.id.action_email) {
            // reply action
        	if(!mEmailAddress.equals("")) {
        		email();
        	}
            return true;
        }
        else if(item.getItemId() == R.id.action_text) {	
        	// text action
        	text();
        	return true;
        }
        else if(item.getItemId() == R.id.action_remove) {
            // remove action
        	remove();
            return true;
        }
        else if(item.getItemId() == R.id.action_edit) {
            // remove action
        	edit();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void report() {
    	new AlertDialog.Builder(MTBTaskDetailPage.this)
		.setTitle("Message")
		.setIcon(R.drawable.hourworld_icon_30_30)
		.setMessage("Did you provide or receive this service?")
		.setPositiveButton("Provided", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// report hours
				Intent intent = new Intent(MTBTaskDetailPage.this, MTBReportHoursPage.class);
				intent.putExtra("SvcCat", mSvcCat);
				intent.putExtra("Service", mService);
				intent.putExtra("SvcCatID", mSvcCatID);
				intent.putExtra("SvcID", mSvcID);
				intent.putExtra("Descr", mDescription);
				intent.putExtra("userID", mMemID);
				intent.putExtra("Provided", true);
				
				startActivity(intent);
			}
		})
		.setNegativeButton("Received", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// report hours
				Intent intent = new Intent(MTBTaskDetailPage.this, MTBReportHoursPage.class);
				intent.putExtra("SvcCat", mSvcCat);
				intent.putExtra("Service", mService);
				intent.putExtra("SvcCatID", mSvcCatID);
				intent.putExtra("SvcID", mSvcID);
				intent.putExtra("Descr", mDescription);
				intent.putExtra("userID", mMemID);
				intent.putExtra("Provided", false);
				
				startActivity(intent);
			}
		})
		.show();
    }
    
    
    public void email() {
    	if(!mEmailAddress.equals(null) && !mEmailAddress.equals("")) {
    		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mEmailAddress, null));
    		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Service inquiry");
    		emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi there,\n\nI'm inquiring about your listing. \n\n" + mDescription + "\n\n");
    		startActivity(Intent.createChooser(emailIntent, "Service inquiry"));
    	}

    }
    
    public void text() {
		
		if(!mPhoneNumber.equals(null) && !mPhoneNumber.equals("false") && mPhoneNumber.length() >= 7) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mPhoneNumber, null)));
		}
		else {
			Toast.makeText(MTBTaskDetailPage.this, "No phone number provided by this member", Toast.LENGTH_SHORT).show();
		}
	}
    
    public void remove() {
    	if(mMemID == mPref.getInt("memID", 0)) {
	   		new AlertDialog.Builder(MTBTaskDetailPage.this)
	   		.setMessage("Remove this task?")
	   		.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//new uploadRemoveMessage().execute();
					new deleteTaskItem().execute();
				}			   			
	   		})
	   		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do nothing
				}
	   		})
	   		.show();
		}
		else {
			// reply to this message
		}
    }
    
    public void edit() {
    	// edit task
		Intent intent = new Intent(MTBTaskDetailPage.this, MTBEditTaskPage.class);
		intent.putExtra("SvcCat", mSvcCat);
		intent.putExtra("Service", mService);
		intent.putExtra("SvcCatID", mSvcCatID);
		intent.putExtra("SvcID", mSvcID);
		intent.putExtra("Descr", mDescription);
		intent.putExtra("userID", mMemID);
		intent.putExtra("latitude", mLatitude);
		intent.putExtra("longitude", mLongitude);
		intent.putExtra("isOffer", mIsOffer);
		intent.putExtra("isRequest", mIsRequest);
		intent.putExtra("oLat", mOLat);
		intent.putExtra("oLon", mOLon);
		intent.putExtra("dLat", mDLat);
		intent.putExtra("dLon", mDLon);
		
		startActivity(intent);
    }
}
