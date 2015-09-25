package com.example.test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

import com.example.entity.GLOBAL;
import com.example.entity.PikachuMessage;
import com.google.gson.Gson;

public class TCPClient {
	private onMessageReceived mMessageListener;
	private String TAG = "TCPClient";
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	public static boolean mException = true;

	public TCPClient() {
	}

	public void setOnMessageListener(onMessageReceived listener) {
		this.mMessageListener = listener;
	}

	public void run() {
		try {
			Log.e(TAG, "Connecting...");
			InetAddress serverAddr = InetAddress.getByName(GLOBAL.CONFIG.IPSERVER);
			Socket client = new Socket(serverAddr,
					GLOBAL.CONFIG.PORT);
			oos = new ObjectOutputStream(client.getOutputStream());
			ois = new ObjectInputStream(client.getInputStream());
			while (true) {
				String msg = (String) ois.readObject();
				if (msg != null) {
					Log.i(TAG, "Dữ liệu gửi về từ Server");
					Log.e(TAG, msg);
					mMessageListener.MessageReceived(msg);
				}
			}
		} catch (Exception e) {
			Gson gson = new Gson();
			PikachuMessage message = new PikachuMessage();
			message.setMsgID(GLOBAL.FROM_SERVER.TIMEOUT);
			mMessageListener.MessageReceived(gson.toJson(message));
		}
	}

	public interface onMessageReceived {
		public void MessageReceived(String msg);
	}

	public void sendMessage(PikachuMessage msg) {
		try {
			Gson gson = new Gson();
			Log.i(TAG, "Gửi dữ liệu đến máy chủ");
			Log.i(TAG, gson.toJson(msg));
			oos.writeObject(gson.toJson(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
