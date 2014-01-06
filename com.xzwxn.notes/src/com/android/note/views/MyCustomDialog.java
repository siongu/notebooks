package com.android.note.views;

import com.xzwxn.notes.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.PaintDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class MyCustomDialog extends PopupWindow {
	private Context mContext;
	private View mParent;
	private View root;
	private LayoutInflater mInflater;
	private LayoutParams mLayoutParams;
	private PopupWindow mPopupWindow = null;

	public MyCustomDialog(Context mContext, View parent) {
		this.mContext = mContext;
		this.mParent = parent;
		this.mInflater = LayoutInflater.from(this.mContext);
		this.mLayoutParams = new LayoutParams();
		this.mLayoutParams.width = LayoutParams.MATCH_PARENT;
		this.mLayoutParams.height = LayoutParams.WRAP_CONTENT;
		this.root = mInflater.inflate(R.layout.popup_dialog, null);
	}

	public MyCustomDialog setIcon(int resid) {
		ImageView mIcon = (ImageView) root.findViewById(R.id.icon);
		mIcon.setImageResource(resid);
		return this;
	}

	public MyCustomDialog setTitle(CharSequence mtitle) {
		TextView mTitle = (TextView) root.findViewById(R.id.title);
		mTitle.setText(mtitle);
		return this;
	}

	public MyCustomDialog setMessage(CharSequence msg) {
		TextView mContent = (TextView) root.findViewById(R.id.content);
		mContent.setText(msg);
		return this;
	}

	public MyCustomDialog setPositiveButton(String positive,
			OnClickListener listener) {
		Button mButton = (Button) root.findViewById(R.id.positivie);
		mButton.setText(positive);
		mButton.setOnClickListener(listener);
		return this;
	}

	public MyCustomDialog setPositiveButtonVisiable(boolean visiable) {
		Button pButton = (Button) root.findViewById(R.id.positivie);
		Button nButton = (Button) root.findViewById(R.id.negative);
		pButton.setVisibility(visiable == true ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		// lp.gravity=Gravity.RIGHT;
		lp.setMargins(10, 0, 10, 5);
		nButton.setLayoutParams(lp);
		// nButton.setGravity(Gravity.CENTER);
		return this;
	}

	public MyCustomDialog setNegativeButton(String negative,
			OnClickListener listener) {
		Button mButton = (Button) root.findViewById(R.id.negative);
		mButton.setText(negative);
		mButton.setOnClickListener(listener);
		return this;
	}

	public MyCustomDialog setNegativeButtonVisiable(boolean visiable) {
		Button nButton = (Button) root.findViewById(R.id.negative);
		Button pButton = (Button) root.findViewById(R.id.positivie);
		nButton.setVisibility(visiable == true ? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(10, 0, 10, 5);
		pButton.setLayoutParams(lp);
		// mButton.setLayoutParams(params);
		return this;
	}

	public MyCustomDialog setLayoutParamas(double width, double height) {
		if (width != 0 && height != 0) {
			WindowManager wm = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay(); // 获取默认显示器
			Point size = new Point();
			display.getSize(size); // 取得默认显示器的尺寸大小即size
			Configuration config = mContext.getResources().getConfiguration();
			// 根据当前显示器所处的状态是横屏还是竖屏来设置对话框的大小
			if (config.orientation == Configuration.ORIENTATION_PORTRAIT) { 
				mLayoutParams.width = (int) (size.x * width); 
				mLayoutParams.height = (int) (size.y * height);
			} else {
				mLayoutParams.width = (int) (size.y * width);
				mLayoutParams.height = (int) (size.x * height);
			}
		}
		return this;
	}

	public void show() {
		if (mPopupWindow == null) {
			mPopupWindow = new PopupWindow(root, mLayoutParams.width,
					mLayoutParams.height);
			mPopupWindow.setBackgroundDrawable(new PaintDrawable());
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			// root.setFocusable(true);
			// root.setFocusableInTouchMode(true);
			/*
			 * root.setOnKeyListener(new OnKeyListener() {
			 * 
			 * @Override public boolean onKey(View v, int keyCode, KeyEvent
			 * event) { if(keyCode==KeyEvent.KEYCODE_BACK){
			 * mPopupWindow.dismiss(); } return false; } });
			 */
		}
		mPopupWindow.showAtLocation(mParent, Gravity.CENTER, 0, 0);
	}

	public void dismiss() {

		if (mPopupWindow == null) {
			return;
		}

		mPopupWindow.dismiss();
	}
}
