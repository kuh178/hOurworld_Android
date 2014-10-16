package edu.psu.ist.mtb_hourworld.sidebar;

import com.korovyansk.android.slideout.SlideoutActivity;

import edu.psu.ist.mtb_hourworld.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

public class MTBMenuBarActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    /*
	    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
	    	finish();
	    }
	    */
	    setContentView(R.layout.mtb_actionbar);
	    
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, 1, 0, getString(R.string.my_location)).setIcon(android.R.drawable.ic_menu_mylocation);
		//menu.add(0, 2, 0, getString(R.string.satellite_view)).setIcon(android.R.drawable.ic_menu_mapmode);
		
		return true;
	}
	
	//Menu option selection handling
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case 1:
				
				int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
				SlideoutActivity.prepare(MTBMenuBarActivity.this, R.id.inner_content, width);
				startActivity(new Intent(MTBMenuBarActivity.this, MTBSideBarActivity.class));
				overridePendingTransition(0, 0);
				
				return true;		

		}
		return false;
	}
	
}
