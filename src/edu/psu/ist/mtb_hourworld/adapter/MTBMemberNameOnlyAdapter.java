package edu.psu.ist.mtb_hourworld.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.hours.MTBHoursPickMember.Member;

/*
 * Timebank Request Adapter
 */
public class MTBMemberNameOnlyAdapter extends BaseAdapter implements Filterable{

	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<Member> tArr = new ArrayList<Member>();
	private ArrayList<Member> fullArr = new ArrayList<Member>();
	
	public MTBMemberNameOnlyAdapter(Context context, int layout, ArrayList<Member> arr) {
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = layout;
		tArr = arr;
		fullArr = arr;
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

		// username
		TextView gUsername = (TextView)convertView.findViewById(R.id.username);
		gUsername.setText(tArr.get(position).name);
		convertView.setTag(tArr.get(position));

		return convertView;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {

	        /* (non-Javadoc)
	         * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
	         */
	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            /*
	             * Here, you take the constraint and let it run against the array
	             * You return the result in the object of FilterResults in a form
	             * you can read later in publichResults.
	             */
	        	FilterResults result = new FilterResults();
	        	
	        	//if constraint is empty return the original names
	        	if(constraint.length() == 0) {
	        		result.values = fullArr;
	        		result.count = fullArr.size();
	        		return result;
	        	}
	        	
	        	ArrayList<Member> fArr = new ArrayList<Member>();
	        	String filterString = constraint.toString().toLowerCase();
	        	String filterableString;
	        	
	        	for(int i = 0 ; i < fullArr.size() ; i++) {
	        		filterableString = fullArr.get(i).name;
	        		if(filterableString.toLowerCase().contains(filterString)) {
	        			fArr.add(fullArr.get(i));
	        		}
	        	}
	        	
	        	result.values = fArr;
	        	result.count = fArr.size();
	        	
	            return result;
	        }

	        /* (non-Javadoc)
	         * @see android.widget.Filter#publishResults(java.lang.CharSequence, android.widget.Filter.FilterResults)
	         */
	        @Override
	        protected void publishResults(CharSequence constraint, FilterResults results) {
	            /*
	             * Here, you take the result, put it into Adapters array
	             * and inform about the the change in data.
	             */
	        	tArr = (ArrayList<Member>) results.values;
	        	notifyDataSetChanged();
	        }
	    };
	}
}
