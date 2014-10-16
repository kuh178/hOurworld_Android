package edu.psu.ist.mtb_hourworld.sidebar;

import com.korovyansk.android.slideout.SlideoutHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MTBSideBarActivity extends FragmentActivity {

	private SlideoutHelper mSlideoutHelper;
	private int mFrom = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    Intent gIntent = getIntent();
	    mFrom = gIntent.getExtras().getInt("from");
	    
	    mSlideoutHelper = new SlideoutHelper(this);
	    mSlideoutHelper.activate();
	    getSupportFragmentManager().beginTransaction().add(com.korovyansk.android.slideout.R.id.slideout_placeholder, new MenuFragment(), "menu").commit();
	    mSlideoutHelper.open();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			mSlideoutHelper.close();
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_MENU) {
			mSlideoutHelper.close();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	public SlideoutHelper getSlideoutHelper(){
		return mSlideoutHelper;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public class MenuFragment extends ListFragment {

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			// not consider a group at this point (Feb. 11. 2013)
			setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new String[] {" Announcements", " Offers", " Requests", " Search", " Group", " More", " Exit"}));
			getListView().setCacheColorHint(0);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			MTBSideBarActivity.this.getSlideoutHelper().close();
			
			if(mFrom == position) {
				// do nothing, just hide the menu area
			}
			else {
				Intent intent = getIntent();
				intent.putExtra("flag", position);
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}
}
