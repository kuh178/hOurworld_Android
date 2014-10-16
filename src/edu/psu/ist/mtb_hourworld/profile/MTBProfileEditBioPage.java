package edu.psu.ist.mtb_hourworld.profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MTBProfileEditBioPage extends Activity {

	private SharedPreferences mPref;
	
	private TextView mLoadingText;
	private ProgressBar mLoadingBar;
	private String mBio;

	private EditText vBioTxt;
	private Button vBioUpdateBtn;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_profile_edit_bio);
	
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mLoadingBar = (ProgressBar)findViewById(R.id.loading_bar);
	    
	    vBioTxt = (EditText)findViewById(R.id.bio_edit);
	    vBioUpdateBtn = (Button)findViewById(R.id.bio_submit_btn);
	    
	    vBioUpdateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(vBioTxt.getText().toString().trim().length() == 0) {
					Toast.makeText(MTBProfileEditBioPage.this, "Please check bio information", Toast.LENGTH_SHORT).show();
				}
				else {
					new updateBio().execute();
				}
			}
		});
	    
	    setTitle("Edit bio");
	    getActionBar().setIcon(R.drawable.profile);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	 
	    new downloadBio().execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }

	
	class updateBio extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBProfileEditBioPage.this);
			mDialog.setTitle("Updating bio");
			mDialog.setMessage("Please waiting...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
					Toast.makeText(MTBProfileEditBioPage.this, "Not posted", Toast.LENGTH_SHORT).show();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBProfileEditBioPage.this);
			return upload.updateBio(vBioTxt.getText().toString().trim());
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		Intent intent = getIntent();
	  		
	  		if(returnValue) {
	  			Toast.makeText(MTBProfileEditBioPage.this, "Upadted successfully", Toast.LENGTH_SHORT).show();
				intent.putExtra("refresh", true);
	  		}
	  		else {
	  			Toast.makeText(MTBProfileEditBioPage.this, "Error while updating. Please try again...", Toast.LENGTH_SHORT).show();
				intent.putExtra("refresh", false);
	  		}
	  		
	  		setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	/*
	 * 
	 */
	class downloadBio extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("EditBio,EDIT")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	
		    	Log.i("K", "requestType: " + "EditBio,EDIT");
		    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
		    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
		    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
		    	
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		   
			httpPost.setEntity(entity);

			// output string
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
					
					Log.i("K", "results: " + result);
					
					JSONObject jObj = new JSONObject(result);
				
					if(!jObj.getBoolean("success")){
						// show the failure message
						return false;
					}
					else {
						mBio = jObj.getJSONArray("results").getJSONObject(0).getString("Bio");
						
						return true;
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return false;
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			if(returnValue) {
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setVisibility(View.GONE);
				
				vBioTxt.setText(mBio);
				
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				//mLoadingText.setVisibility(View.GONE);
				mLoadingText.setText("No new task available");
			}
		}
	}
}
