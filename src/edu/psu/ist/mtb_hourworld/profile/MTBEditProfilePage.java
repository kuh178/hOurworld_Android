package edu.psu.ist.mtb_hourworld.profile;

import java.util.ArrayList;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.utilities.MTBImageLoader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MTBEditProfilePage extends Activity {

	private ImageView userImageView;
	private TextView userNameView;
	private EditText contactView;
	private EditText addressView;
	private EditText bioView;

	private Button messageBtn;
	
	private SharedPreferences mPref;

	private int mUserID;
	
	private MTBImageLoader imageLoader;
	
	private String mContact = "";
	private String mUsername;
	private String mProfileImage;
	private String mCity;
	private String mBio;
	
	private TableLayout groupSuperView;
	
	private LinearLayout mFooter;
	
	private LayoutInflater vi;
	private ArrayList<groupItem> mGroupArr = new ArrayList<groupItem>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mtb_profile_edit);

	    Intent gIntent = getIntent();
	    mUserID = gIntent.getExtras().getInt("userID", 0);
	    mUsername = gIntent.getExtras().getString("username");
	    mProfileImage = gIntent.getExtras().getString("profileImage");
	    mContact = gIntent.getExtras().getString("contact");
	    mCity = gIntent.getExtras().getString("city");
	    mBio = gIntent.getExtras().getString("bio");
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    imageLoader = new MTBImageLoader(this);
	    
	    groupSuperView = (TableLayout)findViewById(R.id.group_super_view);

	    userImageView = (ImageView)findViewById(R.id.user_image);
	    userNameView = (TextView)findViewById(R.id.username);
	    addressView = (EditText)findViewById(R.id.address_text);
	    bioView = (EditText)findViewById(R.id.bio_text);
	    contactView	 = (EditText)findViewById(R.id.contact);
	    messageBtn = (Button)findViewById(R.id.message_btn);
	    mFooter = (LinearLayout)findViewById(R.id.footer);
	    
	    if(mUserID == mPref.getInt("memID", 10017)) {
	    	
	    	mFooter.setVisibility(View.VISIBLE);
	    	
	    	messageBtn.setText("Update");
	    	messageBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// update profile details
					if(addressView.getText().toString().trim().length() == 0) {
						Toast.makeText(MTBEditProfilePage.this, "Please check the address", Toast.LENGTH_SHORT).show();
					}
					else if(bioView.getText().toString().trim().length() == 0) {
						Toast.makeText(MTBEditProfilePage.this, "Please check the bio", Toast.LENGTH_SHORT).show();
					}
					else if(contactView.getText().toString().trim().length() == 0) {
						Toast.makeText(MTBEditProfilePage.this, "Please check the contact", Toast.LENGTH_SHORT).show();
					}
					else {
						
					}
				}
			});
	    }
	    else {
	    	mFooter.setVisibility(View.GONE);
	    }
		
		// username
		userNameView.setText(mUsername);
		
		// user profile image
		userImageView.setTag(mProfileImage);
		imageLoader.DisplayImage(mProfileImage, MTBEditProfilePage.this, userImageView);
				
		// contact
		contactView.setText(mContact);
		
		// address
		addressView.setText(mCity);
		
		// bio (linkify)
		bioView.setText(Html.fromHtml(mBio));
		bioView.setAutoLinkMask(Linkify.WEB_URLS);
		
		// group
		for(int i = 0 ; i < mGroupArr.size() ; i++) {
			View groupView = vi.inflate(R.layout.mtb_profile_dynamic_item, null);
			groupView.setTag(mGroupArr.get(i));
			TextView description = (TextView)groupView.findViewById(R.id.description);
			
			description.setText(mGroupArr.get(i).groupName);
			
			groupSuperView.addView(groupView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}
		
		setTitle("Edit profile");
		getActionBar().setIcon(R.drawable.profile);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	class groupItem {
		public int groupID;
		public String groupName;
		public String groupProfile;
		public String groupOwner;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		
	}
}