package edu.psu.ist.mtb_hourworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.psu.ist.mtb_hourworld.account.MTBLoginPage;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.group.MTBGroupMainPage;
import edu.psu.ist.mtb_hourworld.search.MTBSearchMainPage;
import edu.psu.ist.mtb_hourworld.settings.MTBSettings;
import edu.psu.ist.mtb_hourworld.tasks.MTBReportHoursPage;
import edu.psu.ist.mtb_hourworld.tasks.MTBTaskCategory;
import edu.psu.ist.mtb_hourworld.tasks.MTBTaskDetailPage;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MTBMainMenuPage extends Activity {

	private Button vAnnouncementsBtn;
	private Button vOffersBtn;
	private Button vRequestsBtn;
	private Button vSearchBtn;
	private Button vHoursBtn;
	private Button vMoreBtn;
	private Button vPromotionBtn;
	
	private SharedPreferences mPref;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_main_page);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	
	    vAnnouncementsBtn	 = (Button)findViewById(R.id.announcement_btn);
		vOffersBtn 			 = (Button)findViewById(R.id.offers_btn);
		vRequestsBtn		 = (Button)findViewById(R.id.request_btn);
		vSearchBtn 			 = (Button)findViewById(R.id.search_btn);
		vHoursBtn 			 = (Button)findViewById(R.id.hours_btn);
		vMoreBtn 			 = (Button)findViewById(R.id.more_btn);
		vPromotionBtn 		 = (Button)findViewById(R.id.promotion_btn);
		
		ActionBar actionBar = getActionBar();
		// hide the action bar
		actionBar.hide();

		vAnnouncementsBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MTBMainMenuPage.this, MTBMessagePage.class));
			}
		});
		
		vOffersBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent(MTBMainMenuPage.this, MTBOfferPage.class));
				
				Intent intent = new Intent(MTBMainMenuPage.this, MTBTaskCategory.class);
				intent.putExtra("mIsRequest", "F");
				intent.putExtra("mIsOffer", "T");
				startActivity(intent);
			}
		});
		
		vRequestsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent(MTBMainMenuPage.this, MTBRequestPage.class));
				
				Intent intent = new Intent(MTBMainMenuPage.this, MTBTaskCategory.class);
				intent.putExtra("mIsRequest", "T");
				intent.putExtra("mIsOffer", "F");
				startActivity(intent);
			}
		});
		
		vSearchBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MTBMainMenuPage.this, MTBSearchMainPage.class));
			}
		});
		
		vHoursBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MTBMainMenuPage.this)
				.setTitle("Message")
				.setIcon(R.drawable.hourworld_icon_30_30)
				.setMessage("Did you provide or receive this service?")
				.setPositiveButton("Provided", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// report hours
						Intent intent = new Intent(MTBMainMenuPage.this, MTBHoursPage.class);
						intent.putExtra("Provided", true);
						
						startActivity(intent);
					}
				})
				.setNegativeButton("Received", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// report hours
						Intent intent = new Intent(MTBMainMenuPage.this, MTBHoursPage.class);
						intent.putExtra("Provided", false);
						
						startActivity(intent);
					}
				})
				.show();
			}
		});
		
		vMoreBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MTBMainMenuPage.this, MTBSettings.class));
			}
		});
		
		vPromotionBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MTBMainMenuPage.this);
				dialogBuilder.setTitle("Please pick one");
				dialogBuilder.setCancelable(true);
				//dialogBuilder.setIcon(R.drawable.hourworld_icon_30_30);
				dialogBuilder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
					    sharingIntent.setType("text/html");
					    
					    //@"Hello,<br><br>I want to share this great hOurworld mobile app with you. Download the app now and earn an hour credit!<br><br>Are you an iOS user? Click <a href=\"https://itunes.apple.com/us/app/hourworld/id671499452?mt=8\">here</a> to download!<br><br>Are you an Android user? Click <a href=\"https://play.google.com/store/apps/details?id=edu.psu.ist.mtb_hourworld&hl=en\">here</a> to download!";
					    
					    String shareBody = "Hello,<br><br>I want to share this great hOurworld mobile app with you. Download the app now and earn an hour credit!<br><br>Are you an Android user? "
					    		+ "Click <a href=\"https://play.google.com/store/apps/details?id=edu.psu.ist.mtb_hourworld&hl=en\">here</a> to download!<br><br>Are you an iOS user? Click <a href=\"https://itunes.apple.com/us/app/hourworld/id671499452?mt=8\">here</a> to download!<br><br>"
					    		+ "If you're not a timebank member, go to the \"Join\" tab here: <a pref=\"http://www.hourworld.org\">www.hourworld.org</a> and select your nearest exchange. "
					    		+ "You must do this for the mobile app to work. Please be patient after signing up. The administrator will contact you to confirm your identity.";
					    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the hOurworld mobile app now!");
					    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(shareBody));
					    startActivity(Intent.createChooser(sharingIntent, "Share via"));
						
					}
				});
				dialogBuilder.setNegativeButton("Report Hours (1h)", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						new AlertDialog.Builder(MTBMainMenuPage.this)
						.setTitle("Message")
						.setIcon(R.drawable.hourworld_icon_30_30)
						.setMessage("You will earn an hour credit")
						.setPositiveButton("Report", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// out of the application
								new uploadHoursToServer().execute();
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// nothing to do
							}
						})
						.show();
					}
				});
				
				AlertDialog dialog = dialogBuilder.create();
				dialog.show();
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_sm, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	public void onResume() {
		super.onResume();
		
		if(mPref.getBoolean("complete_logout", false)) {
			Intent intent = new Intent(this, MTBLoginPage.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
        	startActivity(intent);
        	finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(MTBMainMenuPage.this)
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
		else if(keyCode == KeyEvent.KEYCODE_MENU) {
		}
		
		return false;
	}
	
	class uploadHoursToServer extends AsyncTask<Void, Integer, Boolean> {
		
		private ProgressDialog mDialog;
		
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBMainMenuPage.this, "Loading...", "Reporting hours...", true);
		}
		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("RecordHrs,0")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));

		    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    	String formattedDate = dateFormat.format(new Date());
		    	
		    	entity.addPart("transDate", new StringBody(formattedDate));
		    	entity.addPart("transOrigin", new StringBody("M"));
		    	entity.addPart("transHappy", new StringBody("F"));
		    	entity.addPart("transRefer", new StringBody("F"));
		    	entity.addPart("transNote", new StringBody(""));
		    
		    	entity.addPart("Provider", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	entity.addPart("Receiver", new StringBody(Integer.toString(777)));

		    	entity.addPart("TDs", new StringBody("1.0"));
		    	entity.addPart("SvcCatID", new StringBody(Integer.toString(1000)));
		    	entity.addPart("SvcID", new StringBody(Integer.toString(1000)));
	        	
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		   
			httpPost.setEntity(entity);

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
					
					Log.i("K", "Result: " + result);
				
					JSONObject jsonobj = new JSONObject(result);
					
					if(jsonobj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		protected void onPostExecute(Boolean retureValue) {
			
			if(mDialog.isShowing()) {
				mDialog.dismiss();
			}
			
			if(retureValue) {
				Toast.makeText(MTBMainMenuPage.this, "Successfully Reported!", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(MTBMainMenuPage.this, "Report failed. Please try again.", Toast.LENGTH_SHORT).show();
			}
				
		}
	}	
}
