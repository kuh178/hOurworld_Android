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

import edu.psu.ist.mtb_hourworld.MTBMessagePage;
import edu.psu.ist.mtb_hourworld.MTBOfferPage;
import edu.psu.ist.mtb_hourworld.MTBRequestPage;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.account.MTBLoginPage;
import edu.psu.ist.mtb_hourworld.adapter.MTBGroupAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.items.MTBGroupItems;
import edu.psu.ist.mtb_hourworld.quickaction.ActionItem;
import edu.psu.ist.mtb_hourworld.quickaction.QuickAction;
import edu.psu.ist.mtb_hourworld.search.MTBSearchMainPage;
import edu.psu.ist.mtb_hourworld.settings.MTBSettings;
import edu.psu.ist.mtb_hourworld.tasks.MTBAddMessagePage;
import edu.psu.ist.mtb_hourworld.tasks.MTBAddRequestOfferPage;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.ActivityGroup;
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
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class MTBGroupMainPage extends ActivityGroup {

	private Button vFindGroupBtn;
	private Button vCreateGroupBtn;
	private Button vGroupMessageBtn;
	private SharedPreferences mPref;
	private Button menuBtn;
	private TextView mMenuText;
	private QuickAction mQuickAction;
	private ProgressBar mLoadingBar;
    private TextView mLoadingText;
    
    private ArrayList<MTBGroupItems> mArr = new ArrayList<MTBGroupItems>();
    
	private ListView mListView;
	
	private EditText vGroupName;
	private EditText vGroupDescription;
	
	private String mGroupName;
	private String mGroupDescription;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mtb_group_view);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);

        mQuickAction = new QuickAction(this);
        vFindGroupBtn = (Button)findViewById(R.id.find_groups);
        vCreateGroupBtn = (Button)findViewById(R.id.create_groups);
        vGroupMessageBtn = (Button)findViewById(R.id.messages);
        menuBtn = (Button)findViewById(R.id.menu_btn);
        
        // updated Jan. 22
        menuBtn.setEnabled(false);
        menuBtn.setVisibility(View.GONE);
        
        
        mMenuText = (TextView)findViewById(R.id.menu_text);
        
	    mLoadingBar = (ProgressBar)findViewById(R.id.spinner);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mListView = (ListView)findViewById(R.id.listview);
        
	    setQuickAction();
	    
        //mMenuText.setText(" My Groups");
        
        setTitle("My Groups");
        getActionBar().setIcon(R.drawable.group);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        /*
	    menuBtn.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
					SlideoutActivity.prepare(MTBGroupMainPage.this, R.id.inner_content, width);
					
					Intent intent = new Intent(MTBGroupMainPage.this, MTBSideBarActivity.class);
					intent.putExtra("from", Constants.FROM_MESSAGES);
					
					startActivityForResult(intent, Constants.SIDEBAR_MENU);
					overridePendingTransition(0, 0);
				}
			});
	    */
	    
        /*
	    vFindGroupBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	    
	    vCreateGroupBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	    
	    vGroupMessageBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	    */
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_group, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		new downloadGroupList().execute();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK && requestCode == 1){
			Intent intent = new Intent(MTBGroupMainPage.this, MTBLoginPage.class);
        	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
        	startActivity(intent);
	    	finish();
		}
		else if(resultCode == RESULT_OK && requestCode == Constants.SIDEBAR_MENU) {
			
			switch(data.getExtras().getInt("flag")) {
			case 0:
				finish();
				startActivity(new Intent(MTBGroupMainPage.this, MTBMessagePage.class));
				break;
			case 1:
				finish();
				startActivity(new Intent(MTBGroupMainPage.this, MTBOfferPage.class));
				break;
			case 2:
				finish();
				startActivity(new Intent(MTBGroupMainPage.this, MTBRequestPage.class));
				break;
			case 3:
				finish();
				startActivity(new Intent(MTBGroupMainPage.this, MTBSearchMainPage.class));
				break;
			case 4:
				// oneself
				break;
			case 5: 
				finish();
				startActivity(new Intent(MTBGroupMainPage.this, MTBSettings.class));
				break;
			case 6:
				new AlertDialog.Builder(MTBGroupMainPage.this)
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
		}
		else if(resultCode == RESULT_OK && requestCode == Constants.MTB_DETAIL_PAGE) {
			if(data.getExtras().getBoolean("refresh")) {
				mArr.clear();

			    mListView.setVisibility(View.GONE);
				mLoadingBar.setVisibility(View.VISIBLE);
			    mLoadingText.setVisibility(View.VISIBLE);
			    
			    mLoadingText.setText("Refreshing data...");

			    //new downloadGroupList().execute();
			}
			else {
				
			}
		}
	}
	
	private void setQuickAction() {
		
		// add message
		ActionItem addActionMSg = new ActionItem();
		addActionMSg.setTitle("Message");
		addActionMSg.setIcon(getResources().getDrawable(R.drawable.hourworld_icon_30_30));
		
		// add request or offer
		ActionItem addActionReq = new ActionItem();
		addActionReq.setTitle("Task");
		addActionReq.setIcon(getResources().getDrawable(R.drawable.hourworld_icon_30_30));

		// add item into action
		mQuickAction.addActionItem(addActionMSg);
		mQuickAction.addActionItem(addActionReq);
		
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {
				if(pos == 0) {
					Intent intent = new Intent(MTBGroupMainPage.this, MTBAddMessagePage.class);
					startActivity(intent);
				}
				else {
					Intent intent = new Intent(MTBGroupMainPage.this, MTBAddRequestOfferPage.class);
					startActivity(intent);
				}
			}
		});

	}
	
	
	class uploadGroupCreation extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBGroupMainPage.this);
			mDialog.setTitle("Updating bio");
			mDialog.setMessage("Please waiting...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
					Toast.makeText(MTBGroupMainPage.this, "Not posted", Toast.LENGTH_SHORT).show();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBGroupMainPage.this);
			return upload.createGroup(mGroupName, mGroupDescription, mPref.getInt("memID", 0));
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		//new downloadGroupList().execute();
		}
	}
	
	/*
	 * Download Timebank Information from the server
	 */
	class downloadGroupList extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("EditGroups,EDIT")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	
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
						mArr.clear();
						
						JSONArray jAry = jObj.getJSONArray("results");

						for(int i = 0; i < jAry.length(); i++){
							
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							MTBGroupItems item = new MTBGroupItems();
							item.setGroupID(jItemObj.getInt("groupID"));
							item.setGroupName(jItemObj.getString("groupName"));
							if(!jItemObj.isNull("groupProfile")) {
								item.setGroupProfile(jItemObj.getString("groupProfile"));
							}
							if(!jItemObj.isNull("groupOwner")) {
								item.setGroupOwnerID(jItemObj.getInt("groupOwner"));
							}
							if(!jItemObj.isNull("ownerName")) {
								item.setGroupOwnerName(jItemObj.getString("ownerName"));
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
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);

				loadListView();
				
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				//mLoadingText.setVisibility(View.GONE);
				mLoadingText.setText("No new task available");
			}
		}
	}
	
	
	private void loadListView() {
		MTBGroupAdapter adapter = new MTBGroupAdapter(MTBGroupMainPage.this, R.layout.mtb_group_view_item, mArr);
		mListView.setAdapter(adapter);
        
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> view, View v, int position, long arg3) {
				
				MTBGroupItems item = mArr.get(position);

				Intent intent = new Intent(MTBGroupMainPage.this, MTBGroupDetailPage.class);
				intent.putExtra("groupID", item.getGroupID());
				intent.putExtra("groupName", item.getGroupName());
				intent.putExtra("groupDescription", item.getGroupProfile());
				intent.putExtra("groupOwnerID", item.getGroupOwnerID());
				intent.putExtra("groupOwnerName", item.getGroupOwnerName());
				
				startActivity(intent);
				//startActivityForResult(intent, Constants.MTB_DETAIL_PAGE);
				
			}
        });
	}
	
	/**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        if(item.getItemId() == R.id.search_group) {
        	// new action
        	searchGroup();
        	return true;
        }
        else if(item.getItemId() == R.id.create_group) {
        	// new action
        	createGroup();
        	return true;
        }
        else if(item.getItemId() == R.id.check_messages) {
        	 // new action
        	checkMessages();
            return true;
        }
        else {
        	return super.onOptionsItemSelected(item);
        }
    }
    
    public void searchGroup() {
    	startActivity(new Intent(MTBGroupMainPage.this, MTBGroupJoinPage.class));
    }
    
    public void createGroup() {
    	final LinearLayout linear = (LinearLayout) View.inflate(MTBGroupMainPage.this, R.layout.mtb_group_creation_dialog, null);
		
		vGroupName = (EditText)linear.findViewById(R.id.group_name_text);
		vGroupDescription = (EditText)linear.findViewById(R.id.group_description_text);
		
		new AlertDialog.Builder(MTBGroupMainPage.this)
		.setTitle("Create a group name")
		.setView(linear)
		.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mGroupName = vGroupName.getText().toString().trim();
				mGroupDescription = vGroupDescription.getText().toString().trim();
				
				if(mGroupName.equals("") || mGroupDescription.equals("")){
					Toast.makeText(MTBGroupMainPage.this, "Check group name and description", Toast.LENGTH_SHORT).show();	
				}else{
					new uploadGroupCreation().execute();
				}
			}
		})
		.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		})
		.show();
    }
    
    public void checkMessages() {
    	Intent intent = new Intent(MTBGroupMainPage.this, MTBGroupMessagePage.class);
		intent.putExtra("groupID", 0);
		startActivity(intent);
    }
}