package edu.psu.ist.mtb_hourworld.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
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
public class MTBGroupMessageAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<MTBTaskItems> tArr = new ArrayList<MTBTaskItems>();
	
	private MTBImageLoader imageLoader;

	public MTBGroupMessageAdapter(Context context, int layout, ArrayList<MTBTaskItems> arr) {
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = layout;
		tArr = arr;
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

		TextView gUsername = (TextView)convertView.findViewById(R.id.user_name);
		TextView gDatetime = (TextView)convertView.findViewById(R.id.datetime);
		TextView gDescription = (TextView)convertView.findViewById(R.id.description);
		TextView gGroupName = (TextView)convertView.findViewById(R.id.group_name);
		ImageView gProfile = (ImageView)convertView.findViewById(R.id.user_profile_img);
		RelativeLayout rLayout = (RelativeLayout)convertView.findViewById(R.id.wrap_icon);
	
		imageLoader = new MTBImageLoader(mContext);
		
		// profile
		gProfile.setTag(tArr.get(position).getProfileImage());
		imageLoader.DisplayImage(tArr.get(position).getProfileImage(), mContext, gProfile);
		
		// username
		gUsername.setText(tArr.get(position).getListMemName());
		
		// description
		gDescription.setText(Html.fromHtml(tArr.get(position).getDescription() + " "));
		gDescription.setAutoLinkMask(Linkify.WEB_URLS);
		
		// datetime
		gDatetime.setText(tArr.get(position).getTimeStamp());
	
		// group name
		gGroupName.setText(tArr.get(position).getGroupName());
		
		// when click the profile image
		rLayout.setTag(tArr.get(position));
		rLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		
		return convertView;
	}
}
