package com.example.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class PikachuMessage implements Serializable {
	private int msgID;
	private String msg;
	private User user;
	private ArrayList<User> arrUsers;
	private User target;

	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}

	public PikachuMessage() {
		super();
	}

	public ArrayList<User> getArrUsers() {
		return arrUsers;
	}

	public void setArrUsers(ArrayList<User> arrUsers) {
		this.arrUsers = arrUsers;
	}

	public PikachuMessage(int msgID, String msg, User user,
			ArrayList<User> arrUsers) {
		super();
		this.msgID = msgID;
		this.msg = msg;
		this.user = user;
		this.arrUsers = arrUsers;
	}

	public PikachuMessage(int msgID, String msg, ArrayList<User> arrUsers) {
		super();
		this.msgID = msgID;
		this.msg = msg;
		this.arrUsers = arrUsers;
	}

	public PikachuMessage(int msgID, String msg) {
		super();
		this.msgID = msgID;
		this.msg = msg;
	}

	public PikachuMessage(int msgID, String msg, User user) {
		super();
		this.msgID = msgID;
		this.msg = msg;
		this.user = user;
	}

	public int getMsgID() {
		return msgID;
	}

	public void setMsgID(int msgID) {
		this.msgID = msgID;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
