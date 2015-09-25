package com.example.test;

import java.util.ArrayList;

import com.example.entity.Conversation;
import com.example.entity.User;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

public class MyApplication extends Application {
	public static TCPClient mClient;
	public static String TAG = "PIKACHU ONLINE";
	public static ArrayList<User> arrUsers;
	public static ArrayList<User> arrFriends;
	public static User user;
	public static ArrayList<Conversation> arrConversations;

	public void onCreate() {
		arrUsers = new ArrayList<User>();
		arrFriends = new ArrayList<User>();
		arrConversations = new ArrayList<Conversation>();
		mClient = new TCPClient();
//		Connect();
	}
	
	public void setUsersList(ArrayList<User> _arrUsers){
		for(User _user: _arrUsers){
			if(this.user.getUsername().equals(_user.getUsername())){
				_arrUsers.remove(_user);
				break;
			}
		}
		this.arrUsers = _arrUsers;
	}
	
	public void Connect(){
		new ConnectionTask().execute("");
	}

	public void resetData() {
		arrUsers = new ArrayList<User>();
		arrFriends = new ArrayList<User>();
		user = null;
	}

	public class ConnectionTask extends AsyncTask<String, String, TCPClient> {

		@Override
		protected TCPClient doInBackground(String... app) {
			// TODO Auto-generated method stub
			try {
				mClient.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
