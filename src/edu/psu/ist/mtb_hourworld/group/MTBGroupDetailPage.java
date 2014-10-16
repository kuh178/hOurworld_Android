package edu.psu.ist.mtb_hourworld.group;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
import edu.psu.ist.mtb_hourworld.adapter.MTBMemberAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.profile.MTBProfilePage;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MTBGroupDetailPage extends Activity {

	private int mGroupID;
	private int mGroupOwnerID;
	private String mGroupName;
	private String mGroupDescription;
	private String mGroupOwnerName;
	
	private TextView vGroupNameTxt;
	private TextView vGroupDescriptionTxt;
	private ListView vMemberList;
	private Button vSharedMessagesBtn;
	
	private SharedPreferences mPref;
	
	private ArrayList<MTBTaskItems>mArr = new ArrayList<MTBTaskItems>();

	private ProgressBar vLoadingBar;
	private TextView vLoadingTxt;
	
	private Button vSendMessageBtn;
	private Button vLeaveGroupBtn;
	
	private String mMessage;
	
	private TextView vMemberTitle;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_group_details);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	 
	    Intent gIntent = getIntent();
	    mGroupID = gIntent.getExtras().getInt("groupID");
	    mGroupName = gIntent.getExtras().getString("groupName");
	    mGroupDescription = gIntent.getExtras().getString("groupDescription");
	    mGroupOwnerID = gIntent.getExtras().getInt("groupOwnerID");
	    mGroupOwnerName = gIntent.getExtras().getString("groupOwnerName");
	    
	    vGroupNameTxt = (TextView)findViewById(R.id.group_name);
	    vGroupDescriptionTxt = (TextView)findViewById(R.id.group_description);
	    vMemberList = (ListView)findViewById(R.id.member_list);
	    vLoadingBar = (ProgressBar)findViewById(R.id.loading_bar);
	    vLoadingTxt = (TextView)findViewById(R.id.loading_text);
	    vSendMessageBtn = (Button)findViewById(R.id.message_btn);
	    vLeaveGroupBtn = (Button)findViewById(R.id.leave_btn);
	    vMemberTitle = (TextView)findViewById(R.id.member_list_title);
	    vSharedMessagesBtn = (Button)findViewById(R.id.shared_messages_btn);
	    
	    vGroupNameTxt.setText(mGroupName);
	    vGroupDescriptionTxt.setText(mGroupDescription);
	    
	    vSendMessageBtn.setEnabled(false);
	    vLeaveGroupBtn.setEnabled(false);
	    
	    setTitle("Group details");
	    getActionBar().setIcon(R.drawable.group);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    // get group members list
	    new downloadGroupMembersList().execute();

	    vSharedMessagesBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MTBGroupDetailPage.this, MTBGroupMessagePage.class);
				intent.putExtra("groupID", mGroupID);
				startActivity(intent);
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	class uploadSendGroupMessage extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBGroupDetailPage.this);
			mDialog.setTitle("Sending a message");
			mDialog.setMessage("Please wait...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
					Toast.makeText(MTBGroupDetailPage.this, "Not posted", Toast.LENGTH_SHORT).show();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBGroupDetailPage.this);
			return upload.sendGroupMessage(mGroupID, mMessage);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		if(returnValue) {
	  			Toast.makeText(MTBGroupDetailPage.this, "Message sent", Toast.LENGTH_SHORT).show();
	  		}
	  		else {
	  			Toast.makeText(MTBGroupDetailPage.this, "Failed to send a message. Please try again", Toast.LENGTH_SHORT).show();
	  		}
		}
	}
	
	
	/*
	 * Download Timebank Information from the server
	 */
	class downloadGroupMembersList extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("EditGroups,MBRS")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	entity.addPart("groupID", new StringBody(Integer.toString(mGroupID)));
		    	
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
					JSONObject jObj = new JSONObject(result);
				
					Log.i("K", "results: " + result);
				
					if(!jObj.getBoolean("success")){
						// show the failure message
					}
					else {
						mArr.clear();
						
						JSONArray jAry = jObj.getJSONArray("results");
						
						for(int i = 0; i < jAry.length(); i++){
							
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							MTBTaskItems item = new MTBTaskItems();
							item.setListMemID(jItemObj.getInt("grpMbrID"));
							item.setListMemName(jItemObj.getString("grpMbrName"));
							item.setProfileImage(Constants.HOURWORLD + jItemObj.getString("grpMbrImg"));
							item.setEmailAddress(jItemObj.getString("grpMbrEmail"));
							item.setPhoneNumber(jItemObj.getString("grpMbrPhone"));
							
							if(!jItemObj.getJSONArray("LatLonArray").equals(null)) {
								item.setLatitude(jItemObj.getJSONArray("LatLonArray").getJSONObject(0).getDouble("LAT"));
								item.setLongitude(jItemObj.getJSONArray("LatLonArray").getJSONObject(0).getDouble("LON"));
							}
						
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
				vLoadingBar.setVisibility(View.GONE);
				vLoadingTxt.setVisibility(View.GONE);

				MTBMemberAdapter adapter = new MTBMemberAdapter(MTBGroupDetailPage.this, R.layout.mtb_member_list_item, mArr);

				vMemberList.setAdapter(adapter);
				vMemberList.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						MTBTaskItems item = mArr.get(position);
						
						Intent intent = new Intent(MTBGroupDetailPage.this, MTBProfilePage.class);
						intent.putExtra("userID", item.getListMemID());
						startActivity(intent);

					}
				});
				
				vMemberTitle.setText("Member (" + mArr.size() + ")");
				
				vSendMessageBtn.setEnabled(true);
				vLeaveGroupBtn.setEnabled(true);
				
				if(isGroupMember()) {
					vSendMessageBtn.setText("Send message");
					vSendMessageBtn.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								
								final LinearLayout linear = (LinearLayout) View.inflate(MTBGroupDetailPage.this, R.layout.mtb_group_add_message_dialog, null);
								final EditText vMessage = (EditText)linear.findViewById(R.id.add_message);

								new AlertDialog.Builder(MTBGroupDetailPage.this)
								.setTitle("Send a group message")
								.setView(linear)
								.setPositiveButton("Send", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										mMessage = vMessage.getText().toString().trim();
										
										if(vMessage.equals("")){
											Toast.makeText(MTBGroupDetailPage.this, "Check a message", Toast.LENGTH_SHORT).show();	
										}else{
											new uploadSendGroupMessage().execute();
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
						});
					
					vLeaveGroupBtn.setEnabled(true);
					vLeaveGroupBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							new AlertDialog.Builder(MTBGroupDetailPage.this)
							.setTitle("Leave")
							.setIcon(R.drawable.hourworld_icon_30_30)
							.setMessage("Leave this group?")
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new uploadLeaveGroup().execute();
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
					});
				}
				else {
					vLeaveGroupBtn.setEnabled(false);
					
					vSendMessageBtn.setText("Join this group");
					vSendMessageBtn.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							new AlertDialog.Builder(MTBGroupDetailPage.this)
							.setTitle("Join")
							.setIcon(R.drawable.hourworld_icon_30_30)
							.setMessage("Join this group?")
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new uploadJoinGroup().execute();
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
					});
				}
				
			}
			else {
				vLoadingBar.setVisibility(View.GONE);
				vLoadingTxt.setText("There is no member");
			}
		}
	}
	
	private boolean isGroupMember() {
		
		int userID = mPref.getInt("memID", 0);
		
		for(int i = 0 ; i < mArr.size() ; i++) {
			if(mArr.get(i).getListMemID() == userID) {
				return true;
			}
		}
		return false;
	}
	
	
	class uploadJoinGroup extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBGroupDetailPage.this);
			mDialog.setTitle("Joining a group");
			mDialog.setMessage("Please wait...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBGroupDetailPage.this);
			return upload.joinGroup(mGroupID);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		if(returnValue) {
	  			Toast.makeText(MTBGroupDetailPage.this, "You joined this group!", Toast.LENGTH_SHORT).show();
	  			new downloadGroupMembersList().execute();
	  			
	  		}
	  		else {
	  			Toast.makeText(MTBGroupDetailPage.this, "Failed to join this group. Please try again", Toast.LENGTH_SHORT).show();
	  		}
		}
	}
	
	class uploadLeaveGroup extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBGroupDetailPage.this);
			mDialog.setTitle("Leaving a group");
			mDialog.setMessage("Please wait...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBGroupDetailPage.this);
			return upload.leaveGroup(mGroupID);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		if(returnValue) {
	  			Toast.makeText(MTBGroupDetailPage.this, "You left this group", Toast.LENGTH_SHORT).show();
	  			new downloadGroupMembersList().execute();
	  			
	  		}
	  		else {
	  			Toast.makeText(MTBGroupDetailPage.this, "Failed to left this group. Please try again", Toast.LENGTH_SHORT).show();
	  		}
		}
	}

}
