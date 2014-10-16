package edu.psu.ist.mtb_hourworld.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.items.MTBServiceCategoryItem;

public class MTBCategoryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<MTBServiceCategoryItem> mArr = new ArrayList<MTBServiceCategoryItem>();
	
	private int mType;
	
	public MTBCategoryAdapter(Context context, int layout, ArrayList<MTBServiceCategoryItem> arr, int type) {
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = layout;
		mArr = arr;
		mType = type;
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

		TextView categoryView = (TextView)convertView.findViewById(R.id.single_text);
		if(mType==0) {
			categoryView.setText(mArr.get(position).SvcCat);
		}
		else {
			categoryView.setText(mArr.get(position).Service + " (" + mArr.get(position).count + ")");
		}
		
		
		
		
		return convertView;
	}
}
