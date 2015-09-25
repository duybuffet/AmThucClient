package com.example.entity;

public class GLOBAL {
	public static class TO_SERVER{
		public static final int CONNECTED = 1;
		public static final int LOGIN = 2;
		public static final int SIGNUP = 3;
		public static final int LOGOUT = 4;
		public static final int CHAT = 5;
	}

	public static class FROM_SERVER{
		public static final int CONNECTED = 1;
		public static final int LOGIN = 2;
		public static final int SIGNUP = 3;
		public static final int UPDATE_USERS_LIST = 4;
		public static final int TIMEOUT = -1; 
		public static final int CHAT = 5;
		public static final int RECEIVED_CHAT_MESSAGE = 6;
	}
	
	public static class CONFIG{
		public static int PORT= 2015;
		public static String IPSERVER = "192.168.1.101";
	}
}
