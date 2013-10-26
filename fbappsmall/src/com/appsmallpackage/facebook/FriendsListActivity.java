package com.appsmallpackage.facebook;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FriendsListActivity extends ListActivity{

	
	ArrayList<Friend> list;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.friend_list);        //display list from friend_list.xml
      registerForContextMenu(getListView());
          
    }
    
    
	@Override
	  public void onStart() {
	  	
		super.onStart();
	  
		Bundle bundle = this.getIntent().getExtras();  
	      
	      if(bundle.getString("friends").equals("my"))
			
	    	list = SharedData.getFriendsList();
        
	 else if(bundle.getString("friends").equals("other"))
	      
		    list = SharedData.otherfriendlist;
		 
		 
	     setListAdapter(new FriendsArrayAdapter(this, R.layout.rowlayout, list));
    }
	
	@Override
	  public void onListItemClick(ListView parent, View v, int position, long id) {
	
		Intent intent = new Intent(this, FriendProfileActivity.class);
	    intent.putExtra("id", list.get(position).id);
	    intent.putExtra("name", list.get(position).name);
	    startActivity(intent);
	}
	
	

}
