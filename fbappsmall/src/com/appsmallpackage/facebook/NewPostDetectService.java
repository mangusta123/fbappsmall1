package com.appsmallpackage.facebook;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class NewPostDetectService extends Service {

	
	public class LocalBinder extends Binder {
        NewPostDetectService getService() {
            return NewPostDetectService.this;
        }
    }	
	
    private final IBinder mBinder = new LocalBinder();

    
	@Override
	public IBinder onBind(Intent arg0) {
		
		return mBinder;
	}
	
	@Override
	   public void onCreate() {
	       super.onCreate();
	       Log.i("SERVICE", "Service created...");
	   }
	 
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {      
	    		  
	       Log.i("SERVICE", "Service started...");
	       
	       WallPostRunnable runbl = new WallPostRunnable();    
	       
	       new Thread(runbl).start();
	       
	       return START_STICKY;
	       
	   }
	    
    @Override
	public void onDestroy() {
	       super.onDestroy();
           Log.i("SERVICE", "Service destroyed...");
           
           fbAppSmall.runbl.stop();
     }
    
    
    
////////////////////////////////////////////////////////

}
