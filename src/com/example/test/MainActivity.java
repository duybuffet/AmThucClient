package com.example.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.d11cnpm3.pikachuonline.R;
import com.example.entity.GLOBAL;
import com.example.entity.PikachuMessage;
import com.example.entity.User;
import com.google.gson.Gson;

public class MainActivity extends Activity implements OnClickListener {
	private Toast toast;
	private static final String TAG = "SERVER MESSAGE";
	private Intent intent;
	private TCPClient mTcpClient;
	private EditText editUsername, editPass;
	private Button btnTest;
	private Button btnNext;
	private Handler handler;
	private ProgressDialog progress;
	private Context context;
	private TextView textError;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "KHỞI TẠO ACTIVITY MAIN");
		setContentView(R.layout.activity_main_login);
		// intent = new Intent(this, BroadcastService.class);
		editUsername = (EditText) findViewById(R.id.editName);
		editPass = (EditText) findViewById(R.id.editPass);
		btnTest = (Button) findViewById(R.id.btnLogin);
		btnTest.setOnClickListener(this);
		textError = (TextView) findViewById(R.id.textError);
		progress = new ProgressDialog(this);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setCancelable(false);
		handler = new Handler();
		mTcpClient = ((MyApplication) getApplication()).mClient;
		mTcpClient.setOnMessageListener(new TCPClient.onMessageReceived() {
			@Override
			public void MessageReceived(String msg) {

				// TODO Auto-generated method stub
				processMessage(msg);
			}
		});
		((MyApplication)getApplication()).Connect();
		toast = Toast.makeText(this, "", 2000);
	}
	

	public void processMessage(String msg) {
		try {
			Gson gson = new Gson();
			PikachuMessage message = gson.fromJson(msg, PikachuMessage.class);
			switch (message.getMsgID()) {
			case GLOBAL.FROM_SERVER.TIMEOUT:

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.setMessage("Không thể kết nối đến máy chủ");
						progress.setTitle("Lỗi kết nối");
						progress.show();
					}
				});
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						Log.i(TAG,"Thử kết nối lại đến máy chủ");
						// TODO Auto-generated method stub
						((MyApplication)getApplication()).Connect();
					}
				};
				handler.postDelayed(runnable, 2000);

				break;
			case GLOBAL.FROM_SERVER.CONNECTED:

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.dismiss();
					}
				});
				break;
			case GLOBAL.FROM_SERVER.LOGIN:
				checkLogin(message);
				break;

			case GLOBAL.FROM_SERVER.UPDATE_USERS_LIST:
				((MyApplication)getApplication()).setUsersList(message.getArrUsers());
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Login() {
		User user = new User();
		String username = editUsername.getText().toString();
		String password = editPass.getText().toString();
		if (username.equals("") || password.equals("")) {
			toast.setText("Bạn chưa nhập đủ thông tin");
			toast.show();
		} else {
			user.setUsername(username);
			user.setPassword(password);
			PikachuMessage message = new PikachuMessage(GLOBAL.TO_SERVER.LOGIN, "Login",
					user);
			Log.i(TAG, "Đăng nhập");
			Log.i(TAG, (new Gson()).toJson(message));
			mTcpClient.sendMessage(message);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == R.id.btnLogin) {
			Login();
		}
	}

	public void checkLogin(PikachuMessage msg) {
//		openWelcomeActivity();

		Log.i(TAG, "CHECK LOGIN");
		if (msg.getUser() != null) {
			Log.i(TAG, "LOGIN SUCCESSFULLY");
			((MyApplication)getApplication()).user = msg.getUser();
			openWelcomeActivity();
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					textError.setText("Thông tin đăng nhập không chính xác");
				}
			});
		}
	}

	public void openWelcomeActivity() {
		Intent i = new Intent(this, ApplicationMain.class);
		startActivity(i);
		finish();
	}

	// @Override
	// public boolean onKey(View v, int keyCode, KeyEvent event) {
	// // TODO Auto-generated method stub
	// btnTest.performClick();
	// return false;
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "Cài đặt lại Listener" + resultCode);
		mTcpClient.setOnMessageListener(new TCPClient.onMessageReceived() {
			@Override
			public void MessageReceived(String msg) {
				// TODO Auto-generated method stub
				processMessage(msg);
			}
		});
		if (resultCode == 1) {
			String username = data.getStringExtra("username");
			String password = data.getStringExtra("password");
			editUsername.setText(username);
			editPass.setText(password);
			Login();
		}
	}
}