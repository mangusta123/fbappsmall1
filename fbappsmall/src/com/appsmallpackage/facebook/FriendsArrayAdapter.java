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
public class FriendsArrayAdapter extends ArrayAdapter<Friend> {
    private final Activity context;
    private final ArrayList<Friend> friends;
    private int resourceId;

    /**
     * Constructor
     * @param context the application content
     * @param resourceId the ID of the resource/view
     * @param friends the bound ArrayList
     */
    public FriendsArrayAdapter(
            Activity context, 
            int resourceId, 
            ArrayList<Friend> friends) {
        super(context, resourceId, friends);
        this.context = context;
        this.friends = friends;
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
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = vi.inflate(resourceId, null);
        }
        Friend f = friends.get(position);
        TextView rowTxt = (TextView) rowView.findViewById(R.id.rowtext_top);
        rowTxt.setText(f.name);
        return rowView;
    }

}   

