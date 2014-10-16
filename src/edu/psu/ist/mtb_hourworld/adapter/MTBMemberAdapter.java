package edu.psu.ist.mtb_hourworld.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.utilities.MTBImageLoader;

/*
 * Timebank Request Adapter
 */
public class MTBMemberAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<MTBTaskItems> tArr = new ArrayList<MTBTaskItems>();
	
	private MTBImageLoader imageLoader;
	private SharedPreferences mPref;
	
	public MTBMemberAdapter(Context context, int layout, ArrayList<MTBTaskItems> arr) {
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = layout;
		tArr = arr;
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	@Override
	public int getCount() {
		return tArr.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = mInflater.inflate(mLayout, parent, false);

		TextView gUsername = (TextView)convertView.findViewById(R.id.username);
		ImageView gProfile = (ImageView)convertView.findViewById(R.id.user_profile_img);
		RelativeLayout rLayout = (RelativeLayout)convertView.findViewById(R.id.wrap_icon);
	
		imageLoader = new MTBImageLoader(mContext);
		
		// profile
		gProfile.setTag(tArr.get(position).getProfileImage());
		imageLoader.DisplayImage(tArr.get(position).getProfileImage(), mContext, gProfile);
		
		// username
		gUsername.setText(tArr.get(position).getListMemName());
	
		// when click the profile image
		rLayout.setTag(tArr.get(position));
		rLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//MTBItems tItem = (MTBItems)v.getTag();
				//Intent intent = new Intent(mContext, MTBProfilePage.class);
				//intent.putExtra("user_id", tItem.getUserID());
				//mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}
}
