package com.android.note.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.note.db.MyItem;
import com.android.note.operation.SetFactory;
import com.xzwxn.notes.R;

public class MyCursorAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	public List<MyItem> mList;
	public List<Boolean> mCheckStates = null;
	public List<Boolean> mVisableStates = null;
	public List<Boolean> mResetCheckStates = null;
	public List<Boolean> mSetCheckStates = null;

	public MyCursorAdapter(Context mContext, List<MyItem> list) {
		this.mContext = mContext;
		this.mInflater = LayoutInflater.from(this.mContext);
		this.mList = list;
		this.mCheckStates = new ArrayList<Boolean>();
		for (int i = 0; i < mList.size(); i++) {
			mCheckStates.add(false);
		}
		this.mVisableStates = new ArrayList<Boolean>();
		for (int i = 0; i < mList.size(); i++) {
			mVisableStates.add(false);
		}
		this.mResetCheckStates = new ArrayList<Boolean>();
		for (int i = 0; i < mList.size(); i++) {
			mResetCheckStates.add(false);
		}
		this.mSetCheckStates = new ArrayList<Boolean>();
		for (int i = 0; i < mList.size(); i++) {
			mSetCheckStates.add(true);
		}
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewParent mViewParent;
		if (convertView == null) {
			mViewParent = new ViewParent();
			convertView = mInflater.inflate(R.layout.items, null);
			mViewParent.m_id = (TextView) convertView.findViewById(R.id._id);
			mViewParent.mTitle = (TextView) convertView
					.findViewById(R.id.list_title);
			mViewParent.mDate = (TextView) convertView.findViewById(R.id.date);
			mViewParent.mCheckBox = (CheckBox) convertView
					.findViewById(R.id.check);
			convertView.setTag(mViewParent);
		} else {
			mViewParent = (ViewParent) convertView.getTag();
		}
		MyItem mItem = mList.get(position);
		mViewParent.m_id.setText(mItem.getId());
		mViewParent.mTitle.setText(mItem.getTitle());
		mViewParent.mDate.setText(mItem.getDate());
		mViewParent.mCheckBox
				.setVisibility(mVisableStates.get(position) == true ? View.VISIBLE
						: View.INVISIBLE);
		mViewParent.mCheckBox.setChecked(mCheckStates.get(position));
		mViewParent.mCheckBox.setOnClickListener(new MyOnClick(position, mItem
				.getId()));
		return convertView;
	}

	class ViewParent {
		public TextView m_id;
		public TextView mTitle;
		public TextView mDate;
		public CheckBox mCheckBox;
	}

	class MyOnClick implements OnClickListener {
		private int position;
		private String _id;

		public MyOnClick(int position, String _id) {
			this.position = position;
			this._id = _id;
		}

		@Override
		public void onClick(View v) {
			CheckBox cb = (CheckBox) v;
			mCheckStates.set(position, cb.isChecked());
			if (cb.isChecked()) {
				SetFactory.getInstance().addData(_id);
			} else {
				SetFactory.getInstance().delItem(_id);
			}
		}
	}
}
