package com.example.entity;

import java.net.Socket;

public class Client {
	private User user;
	private Socket socket;
	
	public Client(Socket socket){
		this.socket = socket;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	
}
