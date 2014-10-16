package edu.psu.ist.mtb_hourworld.profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MTBProfileEditContactPage extends Activity {

	private SharedPreferences mPref;
	private ArrayList<contactItem> mArr = new ArrayList<contactItem>();
	
	private LinearLayout mLayout;
	
	private TextView mLoadingText;
	private ProgressBar mLoadingBar;
	
	private LayoutInflater vi;
	
	private LinkedHashMap<String,Boolean> mContactType = new LinkedHashMap<String,Boolean>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_profile_edit_contact);
	
	    vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    mLayout = (LinearLayout)findViewById(R.id.main_view);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mLoadingBar = (ProgressBar)findViewById(R.id.loading_bar);
	 
	    setTitle("Edit contact");
	    getActionBar().setIcon(R.drawable.profile);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    setContactTypeHashMap();
	    new downloadContact().execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	class contactItem {
		public String contactType;
		public String contactInfo;
		public String contactPrivate;
	}
	
	private void setContactTypeHashMap () {

        mContactType.put("Address", false);
        mContactType.put("Cell", false);
        mContactType.put("Email1", false);
        mContactType.put("Email2", false);
        mContactType.put("Facebook", false);
        mContactType.put("FAX1", false);
        mContactType.put("FAX2", false);
        mContactType.put("Home1", false);
        mContactType.put("Home2", false);
        mContactType.put("LinkedIn", false);
        mContactType.put("Phone1", false);
        mContactType.put("Phone2", false);
        mContactType.put("Twitter", false);
        mContactType.put("Website", false);
        mContactType.put("Website2", false);
        mContactType.put("Work1", false);
        mContactType.put("Work2", false);

	}

	
	/*
	 * Download Timebank Information from the server
	 */
	class downloadContact extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("EditContact,EDIT")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	
		    	Log.i("K", "requestType: " + "EditContact,EDIT");
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
					}
					else {
						// clear existing values
						mArr.clear();
						
						JSONArray jAry = jObj.getJSONArray("results");
						
						for(int i = 0; i < jAry.length(); i++){
							
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							contactItem item = new contactItem();
							item.contactType = jItemObj.getString("contactType");
							item.contactInfo = jItemObj.getString("contactInfo");
							item.contactPrivate = jItemObj.getString("contactPriv");
						
							mArr.add(item);
						}
						
						if(mArr.size() == 0) {
							return false;
						}
						
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return true;
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			if(returnValue) {
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setVisibility(View.GONE);
				
				displayItems();
				
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				//mLoadingText.setVisibility(View.GONE);
				mLoadingText.setText("No new task available");
			}
		}
	}
	
	private void displayItems() {

		mLayout.removeAllViews();
		
		for(int i = 0 ; i < mArr.size() ; i++) {
			View offerView = vi.inflate(R.layout.mtb_profile_edit_contact_item, null);

			TextView contactType = (TextView)offerView.findViewById(R.id.contact_type);
			final EditText contactInfo = (EditText)offerView.findViewById(R.id.contact_info);
			Button contactUpdate = (Button)offerView.findViewById(R.id.add_contact);
			contactUpdate.setTag(mArr.get(i));

			contactType.setText(mArr.get(i).contactType);
			contactInfo.setText(mArr.get(i).contactInfo);
			//mContactType.put(mArr.get(i).contactType, true);
			
			contactUpdate.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// update contact info
					contactItem cItem = (contactItem)v.getTag();
					
					updateContact updateC = new updateContact(cItem.contactType, contactInfo.getText().toString().trim());
					updateC.execute();
					
				}
			});
			
			mLayout.addView(offerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}
	
	
	class updateContact extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		private String mType;
		private String mInfo;
		
		public updateContact(String type, String info) {
			mType = type;
			mInfo = info;
		}
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBProfileEditContactPage.this);
			mDialog.setTitle("Updating contact");
			mDialog.setMessage("Please waiting...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
					Toast.makeText(MTBProfileEditContactPage.this, "Not posted", Toast.LENGTH_SHORT).show();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBProfileEditContactPage.this);
			return upload.updateContact(mType, mInfo);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		if(returnValue) {
	  			Toast.makeText(MTBProfileEditContactPage.this, "Successfully updated", Toast.LENGTH_SHORT).show();
	  			new downloadContact().execute();
	  		}
	  		else {
	  			Toast.makeText(MTBProfileEditContactPage.this, "Error while updating. Please try again", Toast.LENGTH_SHORT).show();
	  		}
		}
	}
	
}
