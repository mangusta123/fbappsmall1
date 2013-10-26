package com.appsmallpackage.facebook;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;


public class FriendProfileActivity extends Activity{

    private ArrayList<Friend> friends = new ArrayList<Friend>();
	EditText postBox;
	
	static TextView postText;
	
	
	static Handler mHandler = new Handler() {             //handler to modify wallpost text view
        @Override
        public void handleMessage(Message msg) {
  	
        	String text = (String)msg.obj;
            
            postText.setText(text);
                        
        }
    };
 	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        
        int i;
        
        TextView friendID   = (TextView) findViewById(R.id.friend_id);
        TextView friendnm   = (TextView) findViewById(R.id.friend_name);
                 postText   = (TextView) findViewById(R.id.textwallbody1); 
        
        Button   friendList = (Button)   findViewById(R.id.friendlist_button);
  final Button   addFriend  = (Button)   findViewById(R.id.add_button);      
        Button   postOnWall = (Button)   findViewById(R.id.post_button);       
        
                 postBox    = (EditText) findViewById(R.id.editText);
                                 
        //
        
        friendID.setText(getIntent().getStringExtra("id"));        
        friendnm.setText(getIntent().getStringExtra("name"));
        
        for(i=0; i<SharedData.getFriendsList().size(); i++)
            {
        	
        	if(getIntent().getStringExtra("id").equals(SharedData.getFriendsList().get(i).id))
        		
        	   // already friend so we disable button	
        	  {
        	   addFriend.setEnabled(false);
        	   addFriend.setText("Friends");        		
        		
        	   break;
        	  }		
        	   
        	
            }//end for
        
        /////
        //get last feed
        /////
        
        SampleApp.mAsyncRunner.request(getIntent().getStringExtra("id")+"/feed", new NewWallFeedRequestListener());
        
               
        friendList.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
              SampleApp.mAsyncRunner.request(getIntent().getStringExtra("id")+"/friends", new FriendsRequestListener());

				
				
			}});
        
        
        
        addFriend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				addFriend.setEnabled(false);
				addFriend.setText("Request sent");
				
				Bundle params = new Bundle();
				params.putString("id", getIntent().getStringExtra("id"));
								
				SampleApp.mFacebook.dialog(FriendProfileActivity.this, "friends", params, new AddFriendDialogListener());
				
				//implement friend request here

			}});
               
        postOnWall.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				try{
				
				if(postBox.getText().toString().length()!=0)	
			
				   {			       				
					
					Bundle myParams = new Bundle();
		            myParams.putString("message", postBox.getText().toString());	
		            
		            SampleApp.mAsyncRunner.request(getIntent().getStringExtra("id")+"/feed", myParams, "POST", new WallPostRequestListener(), null);
		            	   
		            
		            Thread.sleep(5000);
		            
		            SampleApp.mAsyncRunner.request(getIntent().getStringExtra("id")+"/feed", new NewWallFeedRequestListener());
				   
				   }
				}
				
				catch(Exception e){}
				
			}});
                  
        
	}
			
	
	public class WallPostRequestListener implements
    com.facebook.android.AsyncFacebookRunner.RequestListener {

/**
 * Called when the request to get friends has been completed.
 * Retrieve and parse and display the JSON stream.
 */
public void onComplete(final String response, Object state) {
      
    Log.d("Another user wall post response", "Got response: " + response);
	
	
}

@Override
public void onFacebookError(FacebookError e, Object state) {
    
	Log.d("Another user wall post response", "Error occured" );
	// Ignore Facebook errors
}

@Override
public void onFileNotFoundException(FileNotFoundException e, Object state) {
   
	Log.d("Another user wall post response", "FileNotFoundException occured" );
	// Ignore File not found errors
}

@Override
public void onIOException(IOException e, Object state) {
    
	Log.d("Another user wall post response", "IOException occured" );
	// Ignore IO Facebook errors
}

@Override
public void onMalformedURLException(MalformedURLException e, Object state) {

	Log.d("Another user wall post response", "onMalformedURLException occured" );
	// Ignore Malformed URL errors
}
		

}	

	
    public class FriendsRequestListener implements
    com.facebook.android.AsyncFacebookRunner.RequestListener {

/**
 * Called when the request to get friends has been completed.
 * Retrieve and parse and display the JSON stream.
 */
public void onComplete(final String response, Object state) {
    
	SampleApp.mSpinner.dismiss();
   
    friends = new ArrayList<Friend>();
    
    try {
        // process the response here: executed in background thread
        Log.d("Facebook-Example-Friends Request", "response.length(): " + response.length());
        Log.d("Facebook-Example-Friends Request", "Response: " + response);

        final JSONObject json = new JSONObject(response);
        JSONArray d = json.getJSONArray("data");
        int l = (d != null ? d.length() : 0);
        Log.d("Facebook-Example-Friends Request", "d.length(): " + l);

        
        
        for (int i=0; i<l; i++) {
            JSONObject o = d.getJSONObject(i);
            String n = o.getString("name");
            String id = o.getString("id");
            Friend f = new Friend();
            f.id = id;
            
            Log.d("My friend IDs", f.id);
            
            f.name = n;
            friends.add(f);
        }

        //here the friends list has already been populated and we call FriendsListActivity
        
        SharedData.otherfriendlist = friends;
        
        // Only the original owner thread can touch its views
        FriendProfileActivity.this.runOnUiThread(new Runnable() {
            public void run() {
               
            	Intent intent = new Intent(getApplicationContext(), FriendsListActivity.class);
                
            	intent.putExtra("friends", "other");
            	
                startActivity(intent);           	
                
            }
        });
    } catch (JSONException e) {
        Log.w("Facebook-Example", "JSON Error in response");
    
        Intent i = new Intent(getApplicationContext(), PermissionDenial.class);
        startActivity(i);
    
    }
}

@Override
public void onFacebookError(FacebookError e, Object state) {
    // Ignore Facebook errors
    SampleApp.mSpinner.dismiss();
}

@Override
public void onFileNotFoundException(FileNotFoundException e, Object state) {
    // Ignore File not found errors
    SampleApp.mSpinner.dismiss();
}

@Override
public void onIOException(IOException e, Object state) {
    // Ignore IO Facebook errors
    SampleApp.mSpinner.dismiss();
}

@Override
public void onMalformedURLException(MalformedURLException e, Object state) {
    // Ignore Malformed URL errors
    SampleApp.mSpinner.dismiss();
}
		

}
    
    
    public class AddFriendDialogListener implements
    com.facebook.android.Facebook.DialogListener {

/**
 * Called when the dialog has completed successfully
 */
public void onComplete(Bundle values) {
    
	
	Log.d("ADDFRIEND dialog", "completed");
	
	/*
	final String postId = values.getString("post_id");
    if (postId != null) {
        Log.d("FB Sample App", "Dialog Success! post_id=" + postId);
                        
        SampleApp.mAsyncRunner.request(postId, new WallPostRequestListener());
    } else {
        Log.d("FB Sample App", "No wall post made");
    }
    */
    
}

@Override
public void onCancel() {
   
	Log.d("ADDFRIEND dialog", "CANCELLED");
	
	// No special processing if dialog has been canceled
}

@Override
public void onError(DialogError e) {
    // No special processing if dialog has been canceled
}

@Override
public void onFacebookError(FacebookError e) {
    // No special processing if dialog has been canceled
}
}
    
    
    public class NewWallFeedRequestListener implements
    com.facebook.android.AsyncFacebookRunner.RequestListener {

/**
 * Called when self-info request has completed
 */
public void onComplete(final String response, Object state) {
    //Log.d("WALL FEED INFO =>", response);
	
    try {
        
        final JSONObject json = new JSONObject(response);
        JSONArray d = json.getJSONArray("data");
        int l = (d != null ? d.length() : 0);
        Log.d("Facebook-Example-WallFeed Request", "d.length(): " + l);

        
        //get the latest wallpost
        
            JSONObject o = d.getJSONObject(0);
            Log.d("json data: ", o.getString("id")+" "+o.getJSONObject("from").getString("name")+
             		                               " "+o.getString("message"));
         
            
       //and write on the app wall too
            Message msg = new Message();
            msg.obj = o.getJSONObject("from").getString("name")+" posted: "+o.getString("message");
            
            mHandler.sendMessage(msg);          
            
            
        
    } catch (JSONException e) {
        Log.w("Facebook-Example", "JSON Error in response");
    }
		
}// end onComplete

@Override
public void onFacebookError(FacebookError e, Object state) {
    // Ignore Facebook errors
}

@Override
public void onFileNotFoundException(FileNotFoundException e, Object state) {
    // Ignore File not found errors
}

@Override
public void onIOException(IOException e, Object state) {
    // Ignore IO Facebook errors
}

@Override
public void onMalformedURLException(MalformedURLException e, Object state) {
    // Ignore Malformed URL errors
}

}
    

	
}
    
	
	
	
	
	

