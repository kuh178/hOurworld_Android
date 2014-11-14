package edu.psu.ist.mtb_hourworld.tasks;

import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.profile.MTBProfilePage;
import edu.psu.ist.mtb_hourworld.utilities.MTBImageLoader;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MTBMessageDetailPage extends FragmentActivity {

	private TextView vType;
	private TextView vDescription;
	private TextView vUsername;
	private TextView vDateTime;
	private TextView vLocationNotAvailable;
	private ImageView vImageView;
	
	private LinearLayout vOriginLocLayout;
	private LinearLayout vDestLocLayout;
	
	private int mEID;
	private int mMemID;
	private int mPostID;
	private int mXDays;
	private String mDescription;
	private String mUsername;
	private String mTimeStamp;
	private double mOLat = 0.0;
	private double mOLon = 0.0;
	private double mDLat = 0.0;
	private double mDLon = 0.0;
	
	private String mProfileImage;
	private String mEmailAddress;
	private String mPhoneNumber;
	
	private Button vReplyMessageBtn;
	private Button vReportHoursBtn;
	private Button vRemoveMessageBtn;
	private Button vEmailMessageBtn;
	
	private SharedPreferences mPref;
	
	private GoogleMap mMap;
	private GoogleMapOptions options = new GoogleMapOptions();
	private SupportMapFragment mMapFragment;
	
	private String mMessage;
	
	private ImageView vXDaysImage;
	private TextView vXDaysText;
	
	private LinearLayout vLocLayout;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mtb_message_detail);

	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    // get data from previous intent
	    Intent gIntent = getIntent();
	    mEID = gIntent.getExtras().getInt("EID");
	    mMemID = gIntent.getExtras().getInt("userID");
	    mPostID = gIntent.getExtras().getInt("postID");
	    mUsername = gIntent.getExtras().getString("username");
	    mDescription = gIntent.getExtras().getString("Descr");
	    mTimeStamp = gIntent.getExtras().getString("timestamp");
	    mProfileImage = gIntent.getExtras().getString("profileImage");
	    mEmailAddress = gIntent.getExtras().getString("emailAddress");
	    mPhoneNumber = gIntent.getExtras().getString("phoneNumber");
	    mOLat = gIntent.getExtras().getDouble("oLat");
	    mOLon = gIntent.getExtras().getDouble("oLon");
	    mDLat = gIntent.getExtras().getDouble("dLat");
	    mDLon = gIntent.getExtras().getDouble("dLon");
	    mXDays = gIntent.getExtras().getInt("xDays");

		// initialize layouts
	    vType = (TextView)findViewById(R.id.type);
	    vDescription = (TextView)findViewById(R.id.description);
	    vUsername = (TextView)findViewById(R.id.username);
	    vDateTime = (TextView)findViewById(R.id.datetime);
	    vLocationNotAvailable = (TextView)findViewById(R.id.no_location);
	    vImageView = (ImageView)findViewById(R.id.user_image);
	    vReportHoursBtn = (Button)findViewById(R.id.report_hour);
	    vRemoveMessageBtn = (Button)findViewById(R.id.remove_message);
	    vReplyMessageBtn = (Button)findViewById(R.id.reply_message);
	    vEmailMessageBtn = (Button)findViewById(R.id.email_message);
	    vLocLayout = (LinearLayout)findViewById(R.id.location_layout);
	    
	    vXDaysImage = (ImageView)findViewById(R.id.xDays_image);
		vXDaysText = (TextView)findViewById(R.id.xDays_description);
		
		vOriginLocLayout = (LinearLayout)findViewById(R.id.origin_loc_layout);
		vDestLocLayout = (LinearLayout)findViewById(R.id.dest_loc_layout);
		
		setTitle("Announcement details");
		getActionBar().setIcon(R.drawable.news);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	    
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
	    vDescription.setText(Html.fromHtml(mDescription));
	    vDescription.setAutoLinkMask(Linkify.WEB_URLS);
	    
	    // if this task is from me or others?
	    if(mMemID == mPref.getInt("memID", 0)) {
	    	vUsername.setText("You");
	    	vRemoveMessageBtn.setVisibility(View.VISIBLE);
	    }
	    else {
	    	vUsername.setText(mUsername);
	    	vRemoveMessageBtn.setVisibility(View.GONE);
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
					Intent intent = new Intent(MTBMessageDetailPage.this, MTBProfilePage.class);
					intent.putExtra("userID", mMemID);
					startActivity(intent);
				}
			}
		});
		
		// xDays
		if(mXDays > 7) {
			vXDaysImage.setVisibility(View.GONE);
			vXDaysText.setVisibility(View.GONE);
		}
		else if(mXDays > 3 && mXDays <= 7) {
			vXDaysImage.setVisibility(View.VISIBLE);
			vXDaysText.setVisibility(View.VISIBLE);
			vXDaysImage.setImageResource(R.drawable.in_a_week);
			vXDaysText.setText("Task will be expired in a week");
		}
		else if(mXDays > 1 && mXDays <= 3) {
			vXDaysImage.setVisibility(View.VISIBLE);
			vXDaysText.setVisibility(View.VISIBLE);
			vXDaysImage.setImageResource(R.drawable.in_three_days);
			vXDaysText.setText("Task will be expired in three days");
		}
		else if(mXDays == 1) {
			vXDaysImage.setVisibility(View.VISIBLE);
			vXDaysText.setVisibility(View.VISIBLE);
			vXDaysImage.setImageResource(R.drawable.in_a_day);
			vXDaysText.setText("Task will be expired in a day");
		}
		else {
			vXDaysImage.setVisibility(View.GONE);
			vXDaysText.setVisibility(View.GONE);
		}
		
		
		vReportHoursBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// report hours
				
			}
		});
		
		
		vRemoveMessageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		// when the reply button clicked
		vReplyMessageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		vEmailMessageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_announcement, menu);
        
        if(mMemID == mPref.getInt("memID", 0)) {
        	menu.findItem(R.id.action_remove).setVisible(true);
	    }
	    else {
	    	menu.findItem(R.id.action_remove).setVisible(false);
	    }
 
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
	
	class uploadReplyMessage extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBMessageDetailPage.this);
			mDialog.setTitle("Sending a reply");
			mDialog.setMessage("Please wait...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
					Toast.makeText(MTBMessageDetailPage.this, "Not posted", Toast.LENGTH_SHORT).show();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBMessageDetailPage.this);
			return upload.replyMessage(mPostID, mMemID, mMessage);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		Intent intent = getIntent();
	  		
	  		if(returnValue) {
	  			Toast.makeText(MTBMessageDetailPage.this, "Message sent", Toast.LENGTH_SHORT).show();
				intent.putExtra("refresh", true);
	  		}
	  		else {
	  			Toast.makeText(MTBMessageDetailPage.this, "Failed to send a message. Please try again", Toast.LENGTH_SHORT).show();
				intent.putExtra("refresh", false);
	  		}
	  		
	  		setResult(RESULT_OK, intent);
			finish();
	 
		}
	}
	
	private GeoPoint getPoint(double lat, double lon) {
		return(new GeoPoint((int)(lat*1000000.0), (int)(lon*1000000.0)));
	}
	
	class SimpleOverlay extends ItemizedOverlay<OverlayItem> {
		
		private double lat;
		private double lon;
		public SimpleOverlay (Drawable defaultMarker, double lat, double lon) {
			super(defaultMarker);
			boundCenterBottom(defaultMarker);
			
			this.lat = lat;
			this.lon = lon;
			
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return new OverlayItem(getPoint(lat, lon), "", "");
		}

		@Override
		public int size() {
			return 1;
		}
	}
	
	class TimeInfo {
		public String from;
		public String to;
		public long fromUnix;
		public long toUnix;
	}
	
	class deleteMessageItem extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBMessageDetailPage.this);
			mDialog.setTitle("Removing the message");
			mDialog.setMessage("Please waiting...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
					Toast.makeText(MTBMessageDetailPage.this, "Not posted", Toast.LENGTH_SHORT).show();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBMessageDetailPage.this);
			return upload.deleteMessage(mPostID, mMemID);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		Intent intent = getIntent();
	  		
	  		if(returnValue) {
	  			Toast.makeText(MTBMessageDetailPage.this, "Removed successfully", Toast.LENGTH_SHORT).show();
				intent.putExtra("refresh", true);
	  		}
	  		else {
	  			Toast.makeText(MTBMessageDetailPage.this, "Error while removing. Please try again...", Toast.LENGTH_SHORT).show();
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
        //if(item.getItemId() == R.id.action_reply) {
            // reply action
        //	reply();
        //    return true;
        //}
        if(item.getItemId() == R.id.action_report) {
            // report action
        	report();
            return true;
        }
        else if(item.getItemId() == R.id.action_email) {
            // email action
        	email();
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
        else {
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void reply() {
    	// reply to this message
		final LinearLayout linear = (LinearLayout) View.inflate(MTBMessageDetailPage.this, R.layout.mtb_group_add_message_dialog, null);
		final EditText vMessage = (EditText)linear.findViewById(R.id.add_message);

		new AlertDialog.Builder(MTBMessageDetailPage.this)
		.setTitle("Type your response here and click [Reply]")
		.setView(linear)
		.setPositiveButton("Reply", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mMessage = vMessage.getText().toString().trim();
				
				if(vMessage.equals("")){
					Toast.makeText(MTBMessageDetailPage.this, "Check a response", Toast.LENGTH_SHORT).show();	
				}else{
					new uploadReplyMessage().execute();
				}
			}
		})
		.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.show();
    }
    
	public void report() {
		new AlertDialog.Builder(MTBMessageDetailPage.this)
		.setTitle("Message")
		.setIcon(R.drawable.hourworld_icon_30_30)
		.setMessage("Did you provide or receive this service?")
		.setPositiveButton("Provided", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// report hours
				Intent intent = new Intent(MTBMessageDetailPage.this, MTBReportHoursPage.class);
				intent.putExtra("Descr", mDescription);
				intent.putExtra("userID", mMemID);
				
				// SvcCatID and SvcID are hard-coded
				intent.putExtra("SvcCatID", 1000);
				intent.putExtra("SvcID", 1009);
				
				intent.putExtra("Provided", true);
				
				startActivity(intent);
			}
		})
		.setNegativeButton("Received", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// report hours
				Intent intent = new Intent(MTBMessageDetailPage.this, MTBReportHoursPage.class);
				intent.putExtra("Descr", mDescription);
				intent.putExtra("userID", mMemID);
				
				// SvcCatID and SvcID are hard-coded
				intent.putExtra("SvcCatID", 1000);
				intent.putExtra("SvcID", 1009);
				
				intent.putExtra("Provided", false);
				
				startActivity(intent);
			}
		})
		.show();
	}
	
	public void email() {
		
		if(mEmailAddress != null) {
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mEmailAddress, null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email " + mUsername);
			emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi there,\n\nI'm inquiring about your listing. \n\n" + mDescription + "\n\n");
			startActivity(Intent.createChooser(emailIntent, "Service inquiry"));
		}
	}
	
	public void text() {
		
		if(!mPhoneNumber.equals(null) && !mPhoneNumber.equals("false") && mPhoneNumber.length() >= 7) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mPhoneNumber, null)));
		}
		else {
			Toast.makeText(MTBMessageDetailPage.this, "No phone number provided by this member", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void remove() {
		new AlertDialog.Builder(MTBMessageDetailPage.this)
		.setTitle("Message")
		.setIcon(R.drawable.hourworld_icon_30_30)
		.setMessage("Remove this announcement?")
		.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// delete task
				new deleteMessageItem().execute();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.show();
	}
}
