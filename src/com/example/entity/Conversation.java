package com.example.entity;

import java.util.ArrayList;

public class Conversation {
	private User target;
	private ArrayList<PikachuMessage> messages;
	public User getTarget() {
		return target;
	}
	public void setTarget(User target) {
		this.target = target;
	}
	public ArrayList<PikachuMessage> getMessages() {
		return messages;
	}
	public void setMessages(ArrayList<PikachuMessage> messages) {
		this.messages = messages;
	}
	public Conversation(User target, ArrayList<PikachuMessage> messages) {
		super();
		this.target = target;
		this.messages = messages;
	}
	public Conversation() {
		super();
		target = new User();
		messages = new ArrayList<PikachuMessage>();
	}
	
	
	
}
