package edu.psu.ist.mtb_hourworld.adapter;

import java.util.ArrayList;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.items.MTBGroupItems;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * My Action View adapter
 */
public class MTBGroupAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<MTBGroupItems> mArr = new ArrayList<MTBGroupItems>();
	
	public MTBGroupAdapter(Context context, int layout, ArrayList<MTBGroupItems> arr) {
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = layout;
		mArr = arr;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArr.size();
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
		
		TextView gName = (TextView)convertView.findViewById(R.id.group_name);
		TextView gDescription = (TextView)convertView.findViewById(R.id.group_description);
		
		if(mArr.get(position).getGroupProfile().equals("description")) {
			gDescription.setVisibility(View.GONE);
		}
		else {
			gDescription.setVisibility(View.VISIBLE);
			gDescription.setText(mArr.get(position).getGroupProfile());
		}
		
		gName.setText(mArr.get(position).getGroupName());
		
		
		return convertView;
	}
}
