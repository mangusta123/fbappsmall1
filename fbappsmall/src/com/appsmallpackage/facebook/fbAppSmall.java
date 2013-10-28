package com.appsmallpackage.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import com.facebook.android.Util;
import com.facebook.android.AsyncFacebookRunner.RequestListener;


public class fbAppSmall extends Activity {

   
    public static final String APP_ID = "466481623397336";                      //App ID goes here  

    private static final String[] PERMISSIONS =
        new String[] {"publish_stream", "read_stream", "offline_access"};       //permissions to be granted by the user
    private TextView mText; 
    private static TextView postText;
    private static Button   notifButton;
    static ProgressDialog mSpinner;
    private ArrayList<Friend> friends = new ArrayList<Friend>();
    static ArrayList<String> wallfeeds = new ArrayList<String>();
    private static WallPostsAdapter wallPostsAdapter;
    private static ListView listView;
    static Facebook mFacebook;
    static AsyncFacebookRunner mAsyncRunner;

    static WallPostRunnable runbl;
    private NewPostDetectService s;
    
    static Handler mHandler = new Handler() {                                   //handler to modify wallpost text view
        @Override
        public void handleMessage(Message msg) {
  	
        	String text = (String)msg.obj;
            
            postText.setText(text);
            
            wallPostsAdapter.notifyDataSetChanged();
            
            listView.setAdapter(wallPostsAdapter);
            
        }
    };
        
    static Handler bHandler = new Handler() {            // handler to modify notif.icon. (not called when
        @Override                                        // user posts on his own wall)
        public void handleMessage(Message msg) {
  	        	
        notifButton.setBackgroundColor(Color.RED);
        notifButton.setTextColor(Color.WHITE);
        notifButton.setText(Integer.toString(Integer.parseInt(notifButton.getText().toString())+1));    
            
        }
    };
    
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure the app client_app has been set
        if (APP_ID == null) {
            Util.showAlert(this,
            "Warning", "Facebook Applicaton ID must be set...");
        }

        // Initialize the content view
        setContentView(R.layout.main);
        // Get the status text line resource
        mText = (TextView) fbAppSmall.this.findViewById(R.id.txt);
     postText = (TextView) fbAppSmall.this.findViewById(R.id.textwallbody);
  notifButton = (Button)   fbAppSmall.this.findViewById(R.id.notif_button);  
     
  notifButton.setOnClickListener(new OnClickListener(){

	@Override
	public void onClick(View v) {
	
		if(!notifButton.getText().equals("0"))
		  {
			notifButton.setBackgroundColor(Color.WHITE);
            notifButton.setTextColor(Color.BLACK);
            notifButton.setText("0");
     	  }
			
		Intent intent = new Intent(getApplicationContext(), NotifListActivity.class);
        startActivity(intent);
			
	}});
     
        // Setup the ListView Adapter that is loaded when selecting "get friends"
        listView = (ListView) findViewById(R.id.postsview);
        wallPostsAdapter = new WallPostsAdapter(this, R.layout.notiflayout, SharedData.wallposts);
        listView.setAdapter(wallPostsAdapter);
     
        // Define a spinner used when loading the friends over the network
        mSpinner = new ProgressDialog(listView.getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        // Initialize the Facebook session
        mFacebook = new Facebook(APP_ID);
        mAsyncRunner = new AsyncFacebookRunner(mFacebook);
   //     mAsyncRunner.request("me/feed", new NewWallFeedRequestListener()); //get latest wallpost now

      
   //     startService(new Intent(fbAppSmall.this,NewPostDetectService.class));
        
    }

    //////////////////////////////////////////////////////////////////////

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Log.d("FB AppSmall", "onActivityResult(): " + requestCode);
      mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    //////////////////////////////////////////////////////////////////////
    // Get Friends request listener
    //////////////////////////////////////////////////////////////////////

    /**
     * FriendsRequestListener implements a request lister/callback
     *  for "get friends" requests
     */
    public class FriendsRequestListener implements
            com.facebook.android.AsyncFacebookRunner.RequestListener {

        /**
         * Called when the request to get friends has been completed.
         * Retrieve and parse and display the JSON stream.
         */
        public void onComplete(final String response, Object state) {
            mSpinner.dismiss();
           
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
                
                SharedData.setFriendsList(friends);
                
                // Only the original owner thread can touch its views
                fbAppSmall.this.runOnUiThread(new Runnable() {
                    public void run() {
                       
                    	
                    	Intent intent = new Intent(getApplicationContext(), FriendsListActivity.class);
                        
                    	intent.putExtra("friends", "my");
                    	
                        startActivity(intent);
                    	                    	
                    	/*
                    	friendsArrayAdapter = new FriendsArrayAdapter(
                        fbAppSmall.this, R.layout.rowlayout, friends);
                        listView.setAdapter(friendsArrayAdapter);
                        friendsArrayAdapter.notifyDataSetChanged();
                        */
                        
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            }
        }

        @Override
        public void onFacebookError(FacebookError e, Object state) {
            // Ignore Facebook errors
            mSpinner.dismiss();
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e, Object state) {
            // Ignore File not found errors
            mSpinner.dismiss();
        }

        @Override
        public void onIOException(IOException e, Object state) {
            // Ignore IO Facebook errors
            mSpinner.dismiss();
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object state) {
            // Ignore Malformed URL errors
            mSpinner.dismiss();
        }
				
		
    }

    //////////////////////////////////////////////////////////////////////
    // Wall Post request listener
    //////////////////////////////////////////////////////////////////////

    /**
     * WallPostRequestListener implements a request lister/callback
     *  for "wall post requests"
     */
    public class WallPostRequestListener implements
            com.facebook.android.AsyncFacebookRunner.RequestListener {

    	
        /**
         * Called when the wall post request has completed
         */
        public void onComplete(final String response, Object state) {
            Log.d("Facebook-Example", "Got response: " + response);
                       
            //parse response to get tokens and message body
           
    /*        String posterName;
            
            StringTokenizer st = new StringTokenizer(response, "{}\":,");
           
            while (st.hasMoreTokens()) {
            
            	if(st.nextToken().equals("name"))
            	break;
            }
                      
            posterName = st.nextToken();
                      
         //   SharedData.notiflist.add(posterName);
                         
            //make it red and increase label by 1
             
         //   Message msg1 = new Message();
         //   bHandler.sendMessage(msg1);
            
            
            while (st.hasMoreTokens()) {
            
            	if(st.nextToken().equals("message"))
            	break;
            }
                         
           Message msg2 = new Message();
           msg2.obj = posterName + " posted: " + st.nextToken();
           mHandler.sendMessage(msg2);
           
           SharedData.wallposts.add(msg2.obj.toString());
           
      //     listView.setAdapter(wallPostsAdapter);
      */     
        }

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

    
    public class MyInfoRequestListener implements
    com.facebook.android.AsyncFacebookRunner.RequestListener {

/**
 * Called when self-info request has completed
 */
public void onComplete(final String response, Object state) {
    Log.d("Facebook-Example", "My info: " + response);
}

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
    
    
    public class NewWallFeedRequestListener implements
    com.facebook.android.AsyncFacebookRunner.RequestListener {

/**
 * Called when self-info request has completed
 */
public void onComplete(final String response, Object state) {
    //Log.d("WALL FEED INFO =>", response);
	
	wallfeeds = new ArrayList<String>();
	
    try {
        
        final JSONObject json = new JSONObject(response);
        JSONArray d = json.getJSONArray("data");
        int l = (d != null ? d.length() : 0);
        Log.d("Facebook-Example-WallFeed Request", "d.length(): " + l);

        
        //get the latest wallpost
        
            JSONObject o = d.getJSONObject(0);
            Log.d("json data: ", o.getString("id")+" "+o.getJSONObject("from").getString("name")+
            		                               " "+o.getString("message"));
                        
       //and save it
            
            wallfeeds.add(o.getString("id"));
            wallfeeds.add(o.getJSONObject("from").getString("name"));
            wallfeeds.add(o.getString("message"));

            SharedData.wallposts.add(o.getJSONObject("from").getString("name")+" posted: "+o.getString("message"));
            
            Log.e("WALLPOSTS", "this is initial fetch => "+SharedData.wallposts.get(0));
            
            
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
    
    
    
    
    
    //////////////////////////////////////////////////////////////////////
    // Wall post dialog completion listener
    //////////////////////////////////////////////////////////////////////

    /**
     * WallPostDialogListener implements a dialog lister/callback
     */
    public class WallPostDialogListener implements
            com.facebook.android.Facebook.DialogListener {

        /**
         * Called when the dialog has completed successfully
         */
        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null) {
                Log.d("FB AppSmall", "Dialog Success! post_id=" + postId);
                                
                mAsyncRunner.request(postId, new WallPostRequestListener());
            } else {
                Log.d("FB AppSmall", "No wall post made");
            }
        }

        @Override
        public void onCancel() {
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

    /////////////////////////////////////////////////////////
    // Login / Logout Listeners
    /////////////////////////////////////////////////////////

    /**
     * Listener for login dialog completion status
     */
    private final class LoginDialogListener implements
            com.facebook.android.Facebook.DialogListener {

        /**
         * Called when the dialog has completed successfully
         */
        public void onComplete(Bundle values) {
            // Process onComplete
        	
        	Log.d("FB AppSmall", "LoginDialogListener.onComplete()");
     
        	if(SharedData.wallposts.isEmpty())
        	Log.e("WALLPOSTS","right after logging in, wallposts empty" );
        	else
        		Log.e("WALLPOSTS", "not empty wtf");
        	
        	
            mAsyncRunner.request("me/feed", new NewWallFeedRequestListener());  // now fetch last wallpost
        	                                                                    // without notification
   	        doBindService();                                                    //start service
     
            
            // Dispatch on its own thread
            mHandler.post(new Runnable() {
                public void run() {
                    mText.setText("Facebook login successful. Press Menu...");
                }
            });
        }

        /**
         *
         */
        public void onFacebookError(FacebookError error) {
            // Process error
            Log.d("FB AppSmall", "LoginDialogListener.onFacebookError()");
        }

        /**
         *
         */
        public void onError(DialogError error) {
            // Process error message
            Log.d("FB AppSmall", "LoginDialogListener.onError()");
        }

        /**
         *
         */
        public void onCancel() {
            // Process cancel message
            Log.d("FB AppSmall", "LoginDialogListener.onCancel()");
   }
    }

    /**
     * Listener for logout status message
     */
    private class LogoutRequestListener implements RequestListener {

        /** Called when the request completes w/o error */
        public void onComplete(String response, Object state) {

            // Only the original owner thread can touch its views
           
        	doUnbindService(); //stop service
               
        	SharedData.wallposts.clear();
        	
        	Message msg = new Message();
        	msg.obj = "No data";
        	mHandler.sendMessage(msg);     	
        	
        	SharedData.notiflist.clear();                                       //clear notifications list
        	                                                                    //so that they don't mess up with
        	fbAppSmall.this.runOnUiThread(new Runnable() {                      //next user's notifications
                public void run() {
                    mText.setText("Thanks for using FB AppSmall. Bye bye...");
                    friends.clear();
                    //friendsArrayAdapter.notifyDataSetChanged();
                }
            });

            // Dispatch on its own thread
            mHandler.post(new Runnable() {
                public void run() {
                }
            });
        }

        @Override
        public void onFacebookError(FacebookError e, Object state) {
            // Process Facebook error message
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e, Object state) {
            // Process Exception
        }

        @Override
        public void onIOException(IOException e, Object state) {
            // Process Exception
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object state) {
            // Process Exception
        }

    }
    
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            
        	
        	s = ((NewPostDetectService.LocalBinder)service).getService();

            Log.i("SERVICE", "service connected");            
        	
            runbl = new WallPostRunnable();    
 	       
 	        new Thread(runbl).start();
        	
        }

        public void onServiceDisconnected(ComponentName className) {
           
            s = null;
            
            Log.i("SERVICE", "service disconnected");
            
            runbl.stop();
            
        }
    };
    
    void doBindService() {
        
    	bindService(new Intent(fbAppSmall.this, 
                NewPostDetectService.class), mConnection, Context.BIND_AUTO_CREATE);
        
    }

    void doUnbindService() {
       
            // Detach our existing connection.
       unbindService(mConnection);
             
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
        
    ///////////////////////////////////////////////////////////////////
    // Menu handlers
    ///////////////////////////////////////////////////////////////////

    /**
     * Invoked at the time to create the menu
     * @param menu is the menu to create
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Invoked when preparing to display the menu
     * @param menu is the menu to prepare
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem loginItem = menu.findItem(R.id.login);
        MenuItem postItem = menu.findItem(R.id.wallpost);
        MenuItem getfriendsItem = menu.findItem(R.id.getfriends);
        if (mFacebook.isSessionValid()) {
            loginItem.setTitle("Logout");
            postItem.setEnabled(true);
            getfriendsItem.setEnabled(true);
        } else {
            loginItem.setTitle("Login");
            postItem.setEnabled(false);
            getfriendsItem.setEnabled(false);
        }
        loginItem.setEnabled(true);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Invoked when a menu item has been selected
     * @param item is the selected menu items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Login/logout toggle
            case R.id.login:
                // Toggle the button state.
                //  If coming from login transition to logout.
                if (mFacebook.isSessionValid()) {
                    AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
                    asyncRunner.logout(this.getBaseContext(), new LogoutRequestListener());
                } else {
                    // Toggle the button state.
                    //  If coming from logout transition to login (authorize).
                    mFacebook.authorize(this, PERMISSIONS, new LoginDialogListener());
                }
                break;

            // Wall Post
            case R.id.wallpost: // Wall Post
                mFacebook.dialog(fbAppSmall.this, "stream.publish", new WallPostDialogListener());
                break;

            // Get Friend's List
            case R.id.getfriends: // Wall Post
                // Get the authenticated user's friends
                mSpinner.show();
                mAsyncRunner.request("me/friends", new FriendsRequestListener());  //this guy lists only MY friends
       //         mAsyncRunner.request("me", new MyInfoRequestListener());  
                
       //         mAsyncRunner.request("me/feed", new NewWallFeedRequestListener());
                
                break;

            default:
                return false;

        }
        return true;
    }

}
