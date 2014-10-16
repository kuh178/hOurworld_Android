package edu.psu.ist.mtb_hourworld.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.profile.MTBProfilePage;
import edu.psu.ist.mtb_hourworld.utilities.MTBImageLoader;

/*
 * Timebank Request Adapter
 */
public class MTBNewTaskAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<MTBTaskItems> tArr = new ArrayList<MTBTaskItems>();
	
	private MTBImageLoader mImageLoader;
	private SharedPreferences mPref;
	
	public MTBNewTaskAdapter(Context context, int layout, ArrayList<MTBTaskItems> arr) {
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = layout;
		tArr = arr;
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
		mImageLoader = new MTBImageLoader(mContext);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tArr.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null)
			convertView = mInflater.inflate(mLayout, parent, false);
		
		TextView gTaskType = (TextView)convertView.findViewById(R.id.task_type);
		TextView gUsername = (TextView)convertView.findViewById(R.id.grid_item_text2);
		TextView gDate = (TextView)convertView.findViewById(R.id.grid_item_text3);
		TextView gDescription = (TextView)convertView.findViewById(R.id.grid_item_text4);
		ImageView gProfile = (ImageView)convertView.findViewById(R.id.user_profile_img);
		RelativeLayout rLayout = (RelativeLayout)convertView.findViewById(R.id.wrap_icon);

		// profile
		gProfile.setTag(tArr.get(position).getProfileImage());
		mImageLoader.DisplayImage(tArr.get(position).getProfileImage(), mContext, gProfile);
		
		if(tArr.get(position).getSvcCat().equals(null)) {
			gTaskType.setText("Not applicable");
		}
		else {
			gTaskType.setText(tArr.get(position).getSvcCat());
		}
		
		
		// show text on the textViews.
		//gTitle.setText(tArr.get(position).getTitle() + " ");
		if(mPref.getInt("memID", 0) == tArr.get(position).getMemID()) {
			
			String name = tArr.get(position).getListMemName();
			
			if(name.length() > 25) {
				name = name.substring(0, 24) + "...";
			}
			
			gUsername.setText(name);
		}
		else {
			String name = tArr.get(position).getListMemName();
			
			if(name.length() > 25) {
				name = name.substring(0, 24) + "...";
			}
			
			gUsername.setText(name);
		}
	
		// description
		gDescription.setText(tArr.get(position).getDescription().replaceAll("<br>", "\n").replaceAll("<span class='cat'>", "\n").replaceAll("</span>", ""));

		// date
		if(tArr.get(position).getTimeStamp().equals("")
				|| tArr.get(position).getTimeStamp().equals(null)
				|| tArr.get(position).getTimeStamp().length() == 0) {
			gDate.setText("");
		}
		else {
			gDate.setText(tArr.get(position).getTimeStamp());
		}
		
		
		// when click the profile image
		rLayout.setTag(tArr.get(position));
		rLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MTBTaskItems tItem = (MTBTaskItems)v.getTag();
				
				Log.i("K", "tItem.getMemID() : " + tItem.getListMemID());
				
				if(tItem.getListMemID() != 0) {
					Intent intent = new Intent(mContext, MTBProfilePage.class);
					intent.putExtra("userID", tItem.getListMemID());
					
					mContext.startActivity(intent);
				}
			}
		});
		
		return convertView;
	}
}
