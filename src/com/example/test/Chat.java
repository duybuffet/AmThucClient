package com.example.test;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.chat.emoticons.EmoticonsGridAdapter;
import com.chat.emoticons.EmoticonsGridAdapter.KeyClickListener;
import com.d11cnpm3.pikachuonline.R;
import com.example.control.ChatMessageAdapter;
import com.example.entity.Conversation;
import com.example.entity.GLOBAL;
import com.example.entity.PikachuMessage;
import com.example.entity.User;
import com.google.gson.Gson;

public class Chat extends Activity implements KeyClickListener {
	private String TAG = "CHAT ACTIVITY";
	private TextView textUsername;
	private ListView lvChatMessage;
	private EditText editMessage;
	private Button btnSend;
	private ChatMessageAdapter chatAdapter;
	private TCPClient mClient;
	private TCPClient.onMessageReceived mOnMessageReceived;
	private ProgressDialog progress;
	private Handler handler;
	private User target;
	private User self;
	private Conversation conversation;

	// Emoticons
	private static final int NO_OF_EMOTICONS = 54;
	private View popUpView;
	private LinearLayout emoticonsCover;
	private PopupWindow popupWindow;
	private int keyboardHeight;
	private EditText content;
	private LinearLayout parentLayout;
	private boolean isKeyBoardVisible;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		handler = new Handler();
		progress = new ProgressDialog(this);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setCancelable(false);
		textUsername = (TextView) findViewById(R.id.textChatUsername);
		lvChatMessage = (ListView) findViewById(R.id.listviewChatMessage);
		editMessage = (EditText) findViewById(R.id.chat_content);
		btnSend = (Button) findViewById(R.id.post_button);
		chatAdapter = new ChatMessageAdapter(this, R.layout.single_chat_message);

		// emoticons
		parentLayout = (LinearLayout) findViewById(R.id.chat);
		emoticonsCover = (LinearLayout) findViewById(R.id.footer_for_emoticons);

		// Defining default height of keyboard which is equal to 230 dip
		final float popUpheight = getResources().getDimension(
				R.dimen.keyboard_height);
		changeKeyboardHeight((int) popUpheight);

		// Showing and Dismissing pop up on clicking emoticons button
		ImageView emoticonsButton = (ImageView) findViewById(R.id.emoticons_button);
		emoticonsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!popupWindow.isShowing()) {

					popupWindow.setHeight((int) (keyboardHeight));

					if (isKeyBoardVisible) {
						emoticonsCover.setVisibility(LinearLayout.GONE);
					} else {
						emoticonsCover.setVisibility(LinearLayout.VISIBLE);
					}
					popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0,
							0);

				} else {
					popupWindow.dismiss();
				}

			}
		});

		enablePopUpView();
		checkKeyboardHeight(parentLayout);
		enableFooterView();
		
		// end emotions popup

		editMessage.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					return sendChatMessage();
				}
				return false;
			}
		});
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendChatMessage();
			}
		});

		lvChatMessage
				.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		lvChatMessage.setAdapter(chatAdapter);

		// to scroll the list view to bottom on data change
		chatAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				lvChatMessage.setSelection(chatAdapter.getCount() - 1);
			}
		});

		mClient = ((MyApplication) getApplication()).mClient;
		mOnMessageReceived = new TCPClient.onMessageReceived() {

			@Override
			public void MessageReceived(String msg) {
				// TODO Auto-generated method stub
				processMessage(msg);
			}
		};
		mClient.setOnMessageListener(mOnMessageReceived);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("data");

		// Trường hợp msgID != GLOBAL.FROM_SERVER.RECEIVED_CHAT_MESSAGE thì khởi
		// tạo đối tượng gửi message mới

		PikachuMessage message = (PikachuMessage) bundle
				.getSerializable("message");
		self = ((MyApplication) getApplication()).user;
		target = message.getUser();
		Log.i(TAG,
				"Dữ liệu chat tồn tại trong phần mềm: \n"
						+ (new Gson())
								.toJson(((MyApplication) getApplication()).arrConversations));
		boolean foundThis = false;
		// for(Conversation conver:
		// ((MyApplication)getApplication()).arrConversations){
		// if(conver.getTarget().getName().equals(target.getName())){
		// this.conversation = conver;
		// conver = this.conversation;
		// foundThis = true;
		// break;
		// }
		// }
		if (!foundThis) {
			conversation = new Conversation();
			conversation.setTarget(target);
			conversation.setMessages(new ArrayList<PikachuMessage>());
			((MyApplication) getApplication()).arrConversations
					.add(conversation);
			Log.i(TAG, "Tạo conversation mới : ");
			Log.i(TAG,
					((new Gson())
							.toJson(((MyApplication) getApplication()).arrConversations)));
		}
		chatAdapter.clear();
		chatAdapter.addAll(conversation.getMessages());
		if (message.getMsgID() == GLOBAL.FROM_SERVER.RECEIVED_CHAT_MESSAGE) {
			updateChatMessage(message);
		} else
			target = message.getTarget();
		textUsername.setText(target.getUsername());
	}
	
	/**
	 * Enabling all content in footer i.e. post window
	 */
	private void enableFooterView() {

		content = (EditText) findViewById(R.id.chat_content);
		content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (popupWindow.isShowing()) {

					popupWindow.dismiss();

				}

			}
		});
	}

	/**
	 * Overriding onKeyDown for dismissing keyboard on key down
	 */
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}*/

	/**
	 * Checking keyboard height and keyboard visibility
	 */
	int previousHeightDiffrence = 0;

	private void checkKeyboardHeight(final View parentLayout) {

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);

						int screenHeight = parentLayout.getRootView()
								.getHeight();
						int heightDifference = screenHeight - (r.bottom);

						if (previousHeightDiffrence - heightDifference > 50) {
							popupWindow.dismiss();
						}

						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {

							isKeyBoardVisible = true;
							changeKeyboardHeight(heightDifference);

						} else {

							isKeyBoardVisible = false;

						}
						
						Log.d("AAAAAAAAAAAAA", "" + heightDifference);

					}
				});

	}

	/**
	 * change height of emoticons keyboard according to height of actual
	 * keyboard
	 * 
	 * @param height
	 *            minimum height by which we can make sure actual keyboard is
	 *            open or not
	 */
	private void changeKeyboardHeight(int height) {

		if (height > 100) {
			keyboardHeight = height;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, keyboardHeight);
			emoticonsCover.setLayoutParams(params);
		}

	}

	/**
	 * Hiện thị 
	 */
	private void enablePopUpView() {

		ArrayList<String> paths = new ArrayList<String>();

		for (short i = 1; i <= NO_OF_EMOTICONS; i++) {
			paths.add(i+"");
		}
		
		popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);
		
		GridView gridView = (GridView)popUpView.findViewById(R.id.emoticons_grid);
		EmoticonsGridAdapter adapter = new EmoticonsGridAdapter(getApplicationContext(), paths, this);
		gridView.setAdapter(adapter);

		// Creating a pop window for emoticons keyboard
		popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT,
				(int) keyboardHeight, false);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				emoticonsCover.setVisibility(LinearLayout.GONE);
			}
		});
	}

	public void processMessage(String msg) {
		try {

			Gson gson = new Gson();
			PikachuMessage message = gson.fromJson(msg, PikachuMessage.class);
			Log.i(TAG, "Nhận dữ liệu từ server");
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
						Log.i(TAG, "Thử kết nối lại đến máy chủ");
						// TODO Auto-generated method stub
						((MyApplication) getApplication()).Connect();
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
				Intent i = new Intent(this, MainActivity.class);
				((MyApplication) getApplication()).resetData();
				startActivity(i);
				finish();
				break;
			case GLOBAL.FROM_SERVER.UPDATE_USERS_LIST:
				updateUsersList(message);
				break;
			case GLOBAL.FROM_SERVER.RECEIVED_CHAT_MESSAGE:
				final PikachuMessage messageChat = message;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						updateChatMessage(messageChat);
					}
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updateChatMessage(PikachuMessage message) {
		conversation.getMessages().add(message);
		chatAdapter.add(message);
		chatAdapter.notifyDataSetChanged();
	}

	public void updateUsersList(PikachuMessage message) {
		((MyApplication) getApplication()).setUsersList(message
				.getArrUsers());
	}

	public boolean sendChatMessage() {
		PikachuMessage msg = new PikachuMessage();
		msg.setUser(self);
		msg.setTarget(target);
		msg.setMsgID(GLOBAL.TO_SERVER.CHAT);
		msg.setMsg(editMessage.getText().toString());
		conversation.getMessages().add(msg);
		Log.i(TAG, ((new Gson())
				.toJson(((MyApplication) getApplication()).arrConversations)));
		chatAdapter.add(msg);
		chatAdapter.notifyDataSetChanged();
		mClient.sendMessage(msg);
		editMessage.setText("");
		return false;
	}

	public void onBackPressed() {
		Log.i(TAG, "Ấn vào nút trở lại của điện thoại");
		setResult(ApplicationMain.ACTIVITY_RESULT);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "resultCode: " + resultCode);
		Log.i(TAG, "requestCode: " + requestCode);
	}

	@Override
	public void keyClickedIndex(String index) {
		// TODO Auto-generated method stub
		editMessage.getText().insert(editMessage.getSelectionStart(), index);
	}
}
