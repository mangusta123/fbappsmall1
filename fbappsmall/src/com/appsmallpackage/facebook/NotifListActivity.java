package com.appsmallpackage.facebook;

import java.util.ArrayList;
import android.app.ListActivity;
import android.os.Bundle;

public class NotifListActivity extends ListActivity{

	
	ArrayList<String> list;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.notif_list);        //display list from notif_list.xml
      registerForContextMenu(getListView());
    }
    
    
	@Override
	  public void onStart() {
	  	
		super.onStart();
	    list = SharedData.notiflist;
        setListAdapter(new NotifArrayAdapter(this, R.layout.notiflayout, list));
    }
	
	//no need to implement item click

}
