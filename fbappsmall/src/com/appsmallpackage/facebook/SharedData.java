package com.appsmallpackage.facebook;

import java.util.ArrayList;

public class SharedData {
	
	
	
	private static ArrayList<Friend> friendlist;
	
    public  static ArrayList<Friend> otherfriendlist;
	
	public  static ArrayList<String> notiflist = new ArrayList<String>();
	
	public  static ArrayList<String> wallposts = new ArrayList<String>();  // obtained since user's login 	
		                                                                   // are kept here
	
	public static void setFriendsList(ArrayList<Friend> list) {
	    friendlist = list;
	  }

	public static ArrayList<Friend> getFriendsList() {
	
		return friendlist;
	  }
		
}
