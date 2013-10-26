package com.appsmallpackage.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;

import com.facebook.android.FacebookError;

public class WallPostRunnable implements Runnable {

	private boolean checkIn = true;
	
	@Override
	public void run() {

		try{
		
	while(checkIn)
	{
		Thread.sleep(10000);
		
        fbAppSmall.mAsyncRunner.request("me/feed", new NewWallFeedRequestListener()); //get latest wallpost now

	}		
		
		}//end try
		
		catch(InterruptedException e)
		{
			
		}
		
	}
		
	public void stop(){
		
		checkIn = false;
		
	}
	
	
	/////////////////////////////////////////////////////////
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
       //now check if it is new or not
      
            
         if( fbAppSmall.wallfeeds.size()==0                                                ||
        	!fbAppSmall.wallfeeds.get(0).equals(o.getString("id"))                         ||
        	!fbAppSmall.wallfeeds.get(1).equals(o.getJSONObject("from").getString("name")) ||
            !fbAppSmall.wallfeeds.get(2).equals(o.getString("message")))

            {
        	 //new wall post detected!!
        	 
        	 //we put notification
        	 
        	 SharedData.notiflist.add(o.getJSONObject("from").getString("name"));
        	 
        	 //and write it onto user's app wall
        	 
        	 Message msg1 = new Message();
        	 fbAppSmall.bHandler.sendMessage(msg1);
             
             Message msg2 = new Message();
             msg2.obj = o.getJSONObject("from").getString("name")  + " posted: " + o.getString("message");
             ;

             SharedData.wallposts.add(msg2.obj.toString());
       
             fbAppSmall.mHandler.sendMessage(msg2);
            
             ///save into wallfeeds
             
             
             fbAppSmall.wallfeeds.clear();
             fbAppSmall.wallfeeds.add(o.getString("id"));
             fbAppSmall.wallfeeds.add(o.getJSONObject("from").getString("name"));
             fbAppSmall.wallfeeds.add(o.getString("message"));
            }
            
        

        
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
