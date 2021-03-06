package edu.psu.ist.mtb_hourworld.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MTBReportHoursPage extends Activity {

	private EditText vNoteTxt;
	private Button vDateBtn;
	private Button vSubmitBtn;
	
	private RadioGroup vSatisfactionGroup;
	private RadioGroup vReferenceGroup;
	
	private RadioButton	vSatisfactionBtn;
	private RadioButton	vReferenceBtn;
	
	private TextView vTaskDescription;
	
	private String mDescription;
	private String mSatisfaction = "T";
	private String mReference = "T";
	private String mHours = "0.0";
	
	private Calendar mCal;
	
	private String mSvcCat;
	private String mService;
	private int mSvcCatID;
	private int mSvcID;
	private int mUserID;
	
	private ProgressDialog mDialog;
	private SharedPreferences mPref;
	
	private Spinner vSpinner;
	
	private boolean mProvided = false;
	
	private LinearLayout vSatisfaction;
	private LinearLayout vReference;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mtb_report_hours);
	    
	    Intent gIntent = getIntent();
	    mSvcCat = gIntent.getExtras().getString("SvcCat");
	    mService = gIntent.getExtras().getString("Service");
	    mSvcCatID = gIntent.getExtras().getInt("SvcCatID");
	    mSvcID = gIntent.getExtras().getInt("SvcID");
	    mDescription = gIntent.getExtras().getString("Descr");
	    mUserID = gIntent.getExtras().getInt("userID");
	    mProvided = gIntent.getExtras().getBoolean("Provided");
	    
	    Log.i("K", "mSvcCat: " + mSvcCat);
	    Log.i("K", "mService: " + mService);
	    Log.i("K", "mSvcCatID: " + mSvcCatID);
	    Log.i("K", "mSvcCatID: " + mSvcCat);
	    Log.i("K", "mDescription: " + mDescription);
	    Log.i("K", "mUserID: " + mUserID);
	    Log.i("K", "mProvided : " + mProvided);
	    
	    vNoteTxt = (EditText)findViewById(R.id.note_edit);
	    vDateBtn = (Button)findViewById(R.id.date_btn);
	    vSubmitBtn = (Button)findViewById(R.id.report_hours);
	    vTaskDescription = (TextView)findViewById(R.id.task_description);
	    
	    vSatisfaction = (LinearLayout)findViewById(R.id.satisfaction_layout);
	    vReference = (LinearLayout)findViewById(R.id.reference_layout);
	    
	    // satisfaction and reference are only visible to the receiver
	    if(mProvided) {
	    	vSatisfaction.setVisibility(View.GONE);
	    	vReference.setVisibility(View.GONE);
	    }
	    else {
	    	vSatisfaction.setVisibility(View.VISIBLE);
	    	vReference.setVisibility(View.VISIBLE);
	    }
	    
	    setTitle("Report hours");
	    getActionBar().setIcon(R.drawable.hours);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    vSatisfactionGroup = (RadioGroup)findViewById(R.id.radioSatisfaction);
	    vReferenceGroup = (RadioGroup)findViewById(R.id.radioReference);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    mCal = Calendar.getInstance();
	    
	    vTaskDescription.setText(mDescription);
	    
	    vSpinner = (Spinner)findViewById(R.id.spinner);
	    vSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				mHours = parent.getItemAtPosition(position).toString(); 
				if(mHours.equals("15 mins")) {
					mHours = "0.25";
				}
				else if(mHours.equals("30 mins")) {
					mHours = "0.50";
				}
				else if(mHours.equals("45 mins")) {
					mHours = "0.75";
				}
				else if(mHours.equals("60 mins")) {
					mHours = "1.00";
				}
				else if(mHours.equals("75 mins")) {
					mHours = "1.25";
				}
				else if(mHours.equals("90 mins")) {
					mHours = "1.50";
				}
				else if(mHours.equals("105 mins")) {
					mHours = "1.75";
				}
				else if(mHours.equals("120 mins")) {
					mHours = "2.00";
				}
				else if(mHours.equals("135 mins")) {
					mHours = "2.25";
				}
				else if(mHours.equals("150 mins")) {
					mHours = "2.50";
				}
				else if(mHours.equals("165 mins")) {
					mHours = "2.75";
				}
				else if(mHours.equals("180 mins")) {
					mHours = "3.00";
				}
				else if(mHours.equals("195 mins")) {
					mHours = "3.25";
				}
				else if(mHours.equals("210 mins")) {
					mHours = "3.50";
				}
				else if(mHours.equals("225 mins")) {
					mHours = "3.75";
				}
				else if(mHours.equals("240 mins")) {
					mHours = "4.00";
				}
				else if(mHours.equals("255 mins")) {
					mHours = "4.25";
				}
				else if(mHours.equals("270 mins")) {
					mHours = "4.50";
				}
				else if(mHours.equals("285 mins")) {
					mHours = "4.75";
				}
				else if(mHours.equals("300 mins")) {
					mHours = "5.00";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	    });
	    
	    vDateBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DatePickerDialog(MTBReportHoursPage.this, d,
					mCal.get(Calendar.YEAR),
					mCal.get(Calendar.MONTH),
					mCal.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
	    
	    vSubmitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(vNoteTxt.getText().toString() == "") {
					Toast.makeText(MTBReportHoursPage.this, "Please check note", Toast.LENGTH_SHORT).show();
				}
				else if(vNoteTxt.getText().toString().length() > 500){
					Toast.makeText(MTBReportHoursPage.this, "Note cannot exceed 500 characters", Toast.LENGTH_SHORT).show();
				}
				else if(mHours == "0.0") {
					Toast.makeText(MTBReportHoursPage.this, "Please check hours", Toast.LENGTH_SHORT).show();
				}
				else {
					

					// vSatisfactionBtn selected
					int vSatisfactionBtnSelectedID = vSatisfactionGroup.getCheckedRadioButtonId();
					vSatisfactionBtn = (RadioButton)findViewById(vSatisfactionBtnSelectedID);
					mSatisfaction = vSatisfactionBtn.getText().toString();
					
					Log.i("K", "satisfaction: " + mSatisfaction);
					
					if(mSatisfaction.equals("Yes")) {
						mSatisfaction = "T";
					}
					else {
						mSatisfaction = "F";
					}
					
					// vReferenceBtn selected
					int vReferenceBtnSelectedID = vReferenceGroup.getCheckedRadioButtonId();
					vReferenceBtn = (RadioButton)findViewById(vReferenceBtnSelectedID);
					mReference = vReferenceBtn.getText().toString();
					
					Log.i("K", "reference: " + mReference);
					
					if(mReference.equals("Yes")) {
						mReference = "T";
					}
					else {
						mReference = "F";
					}
					
					new uploadHoursToServer().execute();
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
	
	class uploadHoursToServer extends AsyncTask<Void, Integer, Boolean> {
		
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBReportHoursPage.this, "Loading...", "Reporting hours...", true);
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

		    	entity.addPart("transDate", new StringBody(vDateBtn.getText().toString()));
		    	entity.addPart("transOrigin", new StringBody("M"));
		    	entity.addPart("transHappy", new StringBody(mSatisfaction));
		    	entity.addPart("transRefer", new StringBody(mReference));
		    	entity.addPart("transNote", new StringBody(vNoteTxt.getText().toString()));
		    
		    	if(mProvided) { // I am a provider, so Provider should have my memID
		    		entity.addPart("Provider", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
			    	entity.addPart("Receiver", new StringBody(Integer.toString(mUserID)));
		    	}
		    	else { // I am a receiver, so Provider should be that user's memID
		    		entity.addPart("Provider", new StringBody(Integer.toString(mUserID)));
			    	entity.addPart("Receiver", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	}

		    	entity.addPart("TDs", new StringBody(mHours));
		    	entity.addPart("SvcCatID", new StringBody(Integer.toString(mSvcCatID)));
		    	entity.addPart("SvcID", new StringBody(Integer.toString(mSvcID)));
		    	
		    	/*
		    	Log.i("K", "accessToken:" + mPref.getString("access_token", ""));
		    	Log.i("K", "EID:" + mPref.getInt("EID", 0));
		    	Log.i("K", "memID:" + mPref.getInt("memID", 0));
		    	Log.i("K", "transDate:" + vDateBtn.getText().toString());
		    	Log.i("K", "transOrigin:" + mOrigin);
		    	Log.i("K", "transHappy:" + mSatisfaction);
		    	Log.i("K", "transRefer:" + mReference);
		    	Log.i("K", "transNote" + vNoteTxt.getText().toString());
		    	Log.i("K", "Provider:" + mPref.getInt("memID", 0));
		    	Log.i("K", "Receiver:" + Integer.toString(mUserID));
		    	Log.i("K", "TDs:" + mHours);
		    	Log.i("K", "SvcCat:" + mSvcCat);
		    	Log.i("K", "Service:" + mService);
		    	Log.i("K", "SvcCatID:" + mSvcCatID);
		    	Log.i("K", "SvcID:" + mSvcID);
		    	*/
	        	
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
				Toast.makeText(MTBReportHoursPage.this, "Successfully Reported!", Toast.LENGTH_SHORT).show();
				
				finish();
			}
			else {
				Toast.makeText(MTBReportHoursPage.this, "Report failed. Please try again.", Toast.LENGTH_SHORT).show();
			}
				
		}
	}
	
	DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			//"yyyy-MM-dd"
			
			String month;
			String day;
			
			if((monthOfYear+1) < 10) {
				month = "0" + (monthOfYear+1); 
			}
			else {
				month = (monthOfYear+1) + "";
			}
			
			if(dayOfMonth < 10) {
				day = "0" + dayOfMonth;
			}
			else {
				day = dayOfMonth + "";
			}
			
			vDateBtn.setText(year + "-" + month + "-" + day);
			
		}
	};
}
