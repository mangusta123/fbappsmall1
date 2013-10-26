
package com.appsmallpackage.facebook;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * ListView Friends ArrayAdapter
 */
public class NotifArrayAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> notifs;
    private int resourceId;

    /**
     * Constructor
     * @param context the application content
     * @param resourceId the ID of the resource/view
     * @param friends the bound ArrayList
     */
    public NotifArrayAdapter(
            Activity context, 
            int resourceId, 
            ArrayList<String> notifs) {
        super(context, resourceId, notifs);
        this.context = context;
        this.notifs = notifs;
        this.resourceId = resourceId;
    }

    /**
     * Updates the view
     * @param position the ArrayList position to update
     * @param convertView the view to update/inflate if needed
     * @param parent the groups parent view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View notifView = convertView;
        if (notifView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            notifView = vi.inflate(resourceId, null);
        }
        String n = notifs.get(position);
        TextView notifTxt = (TextView) notifView.findViewById(R.id.notiftext_top);
        notifTxt.setText(n + " has posted on your wall");
        return notifView;
    }

}   

