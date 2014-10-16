package edu.psu.ist.mtb_hourworld.adapter;

import java.util.ArrayList;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.profile.MTBProfilePage;
import edu.psu.ist.mtb_hourworld.utilities.MTBImageLoader;
import android.content.Context;
import android.content.Intent;
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

/*
 * My Action View adapter
 */
public class MTBMessageAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<MTBTaskItems> tArr = new ArrayList<MTBTaskItems>();
	private MTBImageLoader imageLoader;
	
	public MTBMessageAdapter(Context context, int layout, ArrayList<MTBTaskItems> arr) {
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = layout;
		tArr = arr;
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
		
		TextView gUsername = (TextView)convertView.findViewById(R.id.grid_item_text2);
		TextView gDate = (TextView)convertView.findViewById(R.id.grid_item_text3);
		TextView gDescription = (TextView)convertView.findViewById(R.id.grid_item_text4);
		ImageView gProfile = (ImageView)convertView.findViewById(R.id.user_profile_img);
		RelativeLayout rLayout = (RelativeLayout)convertView.findViewById(R.id.wrap_icon);
		ImageView gXDate = (ImageView)convertView.findViewById(R.id.xday_image);
		
		gUsername.setText(tArr.get(position).getListMemName().trim());
		
		imageLoader = new MTBImageLoader(mContext);
		gProfile.setTag(tArr.get(position).getProfileImage());
		imageLoader.DisplayImage(tArr.get(position).getProfileImage(), mContext, gProfile);
		
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

		// news description
		//gDescription.setText(Html.fromHtml(tArr.get(position).getEblast()));
		//gDescription.setAutoLinkMask(Linkify.WEB_URLS);
		String description = tArr.get(position).getEblast().replaceAll("<br>", "\n").replaceAll("<span class='cat'>", "\n").replaceAll("</span>", "");
		gDescription.setText(stripHtml(description));
		
		// news date
		gDate.setText(tArr.get(position).getTimeStamp());
		
		// xDays
		if(tArr.get(position).getXDays() > 7) {
			gXDate.setVisibility(View.GONE);
		}
		else if(tArr.get(position).getXDays() > 3 && tArr.get(position).getXDays() <= 7) {
			gXDate.setVisibility(View.VISIBLE);
			gXDate.setImageResource(R.drawable.in_a_week);
		}
		else if(tArr.get(position).getXDays() > 1 && tArr.get(position).getXDays() <= 3) {
			gXDate.setVisibility(View.VISIBLE);
			gXDate.setImageResource(R.drawable.in_three_days);
		}
		else if(tArr.get(position).getXDays() == 1) {
			gXDate.setVisibility(View.VISIBLE);
			gXDate.setImageResource(R.drawable.in_a_day);
		}
		
		return convertView;
	}
	
	public String stripHtml(String html) {
	    return Html.fromHtml(html).toString();
	}
}
