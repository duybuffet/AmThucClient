package com.example.control;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.d11cnpm3.pikachuonline.R;
import com.example.entity.User;

public class UserAdapter extends ArrayAdapter<User> {
	Activity context = null;
	ArrayList<User> myArray = null;
	private String TAG = "User LISTVIEW";
	int layoutId;

	/**
	 * Constructor này dùng để khởi tạo các giá trị từ MainActivity truyền vào
	 * 
	 * @param context
	 *            : là Activity từ Main
	 * @param layoutId
	 *            : Là layout custom do ta tạo (my_item_layout.xml)
	 * @param arr
	 *            : Danh sách nhân viên truyền từ Main
	 */
	public UserAdapter(Activity context, int layoutId, ArrayList<User> arr) {
		super(context, layoutId, arr);
		this.context = context;
		this.layoutId = layoutId;
		this.myArray = arr;
	}

	/**
	 * 
	 * @param position
	 *            : là vị trí của phần tử trong danh sách nhân viên
	 * @param convertView
	 *            : convertView, dùng nó để xử lý Item
	 * @param parent
	 *            : Danh sách nhân viên truyền từ Main
	 * @return View: trả về chính convertView
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(layoutId, null);
		final User user = myArray.get(position);
		final TextView txtName = (TextView) convertView
				.findViewById(R.id.txtName);
		final TextView txtPoint = (TextView) convertView
				.findViewById(R.id.txtPoint);
		Log.i(TAG, user.getUserLevel() + "");
		txtPoint.setText(user.getUserLevel()+"");
		txtName.setText(user.getUsername());
		final TextView txtStatus = (TextView) convertView
				.findViewById(R.id.textStatus);		
		return convertView;
	}
}
