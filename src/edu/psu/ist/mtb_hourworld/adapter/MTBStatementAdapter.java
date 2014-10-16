package edu.psu.ist.mtb_hourworld.adapter;

import java.util.ArrayList;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.profile.MTBStatementPage.StatementItem;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * MTB Statement Adapter
 */
public class MTBStatementAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<StatementItem> tArr = new ArrayList<StatementItem>();
	private SharedPreferences mPref;

	public MTBStatementAdapter(Context context, int layout, ArrayList<StatementItem> arr) {
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
		
		TextView providerTxt = (TextView)convertView.findViewById(R.id.provider_text);
		TextView receiverTxt = (TextView)convertView.findViewById(R.id.receiver_text);
		TextView serviceTxt = (TextView)convertView.findViewById(R.id.service);
		TextView dateTxt = (TextView)convertView.findViewById(R.id.date);
		TextView hourTxt = (TextView)convertView.findViewById(R.id.hour);
		
		Log.i("K", "tArr.get(position).provider : " + tArr.get(position).provider);
		
		providerTxt.setText(tArr.get(position).provider);
		receiverTxt.setText(tArr.get(position).receiver);
		serviceTxt.setText(tArr.get(position).service);
		dateTxt.setText(tArr.get(position).transDate);
		
		//if(tArr.get(position).providerID == mPref.getInt("memID", 0)) {
		//	hourTxt.setText("+" + tArr.get(position).TDs + " hour(s)");
		//}
		//else {
		//	hourTxt.setText("-" + tArr.get(position).TDs + " hour(s)");
		//}
		
		hourTxt.setText("" + tArr.get(position).TDs + " hour(s)");
		
		
	
		return convertView;
	}
}
