package edu.psu.ist.mtb_hourworld.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.search.MTBSearchBio;
import edu.psu.ist.mtb_hourworld.search.MTBSearchKB;
import edu.psu.ist.mtb_hourworld.search.MTBSearchMainPage;
import edu.psu.ist.mtb_hourworld.search.MTBSearchMessage;
import edu.psu.ist.mtb_hourworld.search.MTBSearchName;
import edu.psu.ist.mtb_hourworld.search.MTBSearchProvider;
import edu.psu.ist.mtb_hourworld.utilities.MTBImageLoader;

/*
 * Timebank Request Adapter
 */
public class MTBSearchAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mLayout;
	private ArrayList<MTBTaskItems> tArr = new ArrayList<MTBTaskItems>();
	
	private MTBImageLoader imageLoader;
	
	public MTBSearchAdapter(Context context, int layout, ArrayList<MTBTaskItems> arr) {
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
		
		
		/*
		 * 
		 * if(item.getRequestMode().equals("Pro")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchProvider.class);
							intent.putExtra("type", "Pro");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
						else if(item.getRequestMode().equals("Rcv")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchProvider.class);
							intent.putExtra("type", "Rcv");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);						
						}
						else if(item.getRequestMode().equals("Bio")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchBio.class);
							intent.putExtra("type", "Bio");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
						else if(item.getRequestMode().equals("Name")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchName.class);
							intent.putExtra("type", "Name");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
						else if(item.getRequestMode().equals("Msg")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchMessage.class);
							intent.putExtra("type", "Msg");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
						else if(item.getRequestMode().equals("KB")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchKB.class);
							intent.putExtra("type", "KB");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
		 * 
		 */
		
		TextView gTaskType = (TextView)convertView.findViewById(R.id.task_type);
		TextView gDescription = (TextView)convertView.findViewById(R.id.task_description);
		ImageView gIcon = (ImageView)convertView.findViewById(R.id.user_profile_img);
	
		imageLoader = new MTBImageLoader(mContext);
		
		// profile
		gIcon.setTag(tArr.get(position).getIcon());
		imageLoader.DisplayImage(tArr.get(position).getIcon(), mContext, gIcon);
		
		// this is a temporal text update. (done: Dec.4.2013)
		if(tArr.get(position).getRequestMode().equals("Pro")) {
			gTaskType.setText("Offers");
			gDescription.setText(tArr.get(position).getLinkText().replace("Providers", "Offers") + " ");
		}
		else if(tArr.get(position).getRequestMode().equals("Rcv")) {
			gTaskType.setText("Requests");
			gDescription.setText(tArr.get(position).getLinkText().replace("Receivers", "Requests") + " ");
		}
		else if(tArr.get(position).getRequestMode().equals("Bio")) {
			gTaskType.setText("Profiles");
			gDescription.setText(tArr.get(position).getLinkText().replace("Bios", "Profiles") + " ");
		}
		else if(tArr.get(position).getRequestMode().equals("Msg")) {
			gTaskType.setText("Messages");
			gDescription.setText(tArr.get(position).getLinkText() + " ");
		}
		else {
			gTaskType.setText(tArr.get(position).getRequestMode());

			// lets just keep this at this moment
			gDescription.setText(tArr.get(position).getLinkText() + " ");
		}
		
		

		return convertView;
	}
}
