package com.example.control;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d11cnpm3.pikachuonline.R;
import com.example.entity.PikachuMessage;
import com.example.test.MyApplication;

public class ChatMessageAdapter extends ArrayAdapter<PikachuMessage> {
	private LinearLayout messageContainer;
	private TextView singleMessage;
	Activity context;
	private int layoutId;

	public ChatMessageAdapter(Activity context, int layoutId) {
		super(context, layoutId);
		this.context = context;
		this.layoutId = layoutId;
		// TODO Auto-generated constructor stub
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutId, parent, false);
		}
		messageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
		PikachuMessage message = getItem(position);
		String messageStr = message.getMsg();
		singleMessage = (TextView) row.findViewById(R.id.singleMessage);
		
		processMessage(message.getMsg());
		
//		singleMessage.setText(messageStr);
		if(message.getUser().getUsername().equals(((MyApplication)context.getApplication()).user.getUsername())){
			singleMessage.setBackgroundResource(R.drawable.bubble_a);
			messageContainer.setGravity(Gravity.RIGHT);	
		}else{
			singleMessage.setBackgroundResource(R.drawable.bubble_b);
			messageContainer.setGravity(Gravity.LEFT);
		}
		return row;
	}

	public void processMessage(String msg) {
		/*ArrayList<Spanned> emoticons = new ArrayList<Spanned>();

		HashMap<Integer, Object> msgs = new HashMap<Integer, Object>();

		String[] ws = msg.split(" ");

		for (String w : ws) {
			if (isNumber(w)) {
				Spanned spanned = getSpanned(w);
				if (spanned != null) {
					emoticons.add(spanned);
				}
			}
		}*/
		
		EditText editText = new EditText(context);
		
		for(int i = 0; i < msg.length(); i++) {
			if(msg.charAt(i) > '0' && msg.charAt(i) <= '9') {
				int num = 0;
				while(i < msg.length() && (msg.charAt(i) >= '0' && msg.charAt(i) <= '9')) {
					num = num * 10 + msg.charAt(i) - '0';
					i++;
				}
				
				Log.d("NUMBER", num + "");
				
				Spanned spanned = getSpanned(num + "");
				
				if(spanned != null) {
					editText.getText().insert(editText.getText().toString().length(), spanned);
				} else {
					editText.getText().insert(editText.getText().toString().length(), num + "");
				}
				
				if(i < msg.length()) {
					editText.getText().insert(editText.getText().toString().length(), msg.charAt(i) + "");
				}
			} else {
				editText.getText().insert(editText.getText().toString().length(), msg.charAt(i) + "");
			}
		}
		
		singleMessage.setText(editText.getText());

//		return emoticons;
	}

	/**
	 * Hàm này trả về Spanned để hiển thị ảnh
	 * @param w là tên của ảnh trong assets
	 * @return Spanned
	 */
	public Spanned getSpanned(final String w) {
		final Bitmap image = getImage(w); // anh
		if(image == null) return null;
		
		ImageGetter imageGetter = new ImageGetter() {

			@Override
			public Drawable getDrawable(String source) {
				Drawable d = new BitmapDrawable(context.getResources(),image);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		};
		
		if(imageGetter == null) return null;

		Spanned cs = Html.fromHtml("<img src='" + w + "'/>", imageGetter, null);
		return cs;
	}

	/**
	 * Get smiley from assets
	 * 
	 * @param name name of smileys
	 * @return Bitmap
	 */
	public Bitmap getImage(String name) {
		AssetManager assetManager = context.getAssets();
		InputStream in = null;
		try {
			in = assetManager.open("emoticons/" + name + ".png");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Bitmap temp = BitmapFactory.decodeStream(in, null, null);
		return temp;
	}

	public boolean isNumber(String w) {
		for (int i = 0; i < w.length(); i++) {
			if (w.charAt(i) < '1' || w.charAt(i) > '9')
				return false;
		}
		return true;
	}

}
