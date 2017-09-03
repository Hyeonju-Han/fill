package com.hhj.android.fill;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/*
	Han Hyeonju
	- 각각의 아이템을 화면에 보여주는 데이터 어뎁터
*/

public class FillListAdapter extends BaseAdapter {
	//BaseAdapter를 상속하여 새로운 어댑터 클래스 정의

	private Context mContext;

	private List<FillListItem> mItems = new ArrayList<FillListItem>();
	//각 아이템의 데이터를 담고 있는 FillListItem객체를 저장할 ArrayList객체 생성

	public FillListAdapter(Context context) {
		mContext = context;
	}
	public void clear() {
		mItems.clear();
	}
	public void addItem(FillListItem it) {
		mItems.add(it);
	}
	public void setListItems(List<FillListItem> lit) {
		mItems = lit;
	}
	public boolean areAllItemsSelectable() {
		return false;
	}
	public boolean isSelectable(int position) {
		try {
			return mItems.get(position).isSelectable();
		} catch (IndexOutOfBoundsException ex) {
			return false;
		}
	}

	public int getCount() {
		//리스트의 갯수를 반환해주는 메소드, ArrayList의 size를 반환하면 된다
		return mItems.size();
	}

	public Object getItem(int position) {
		//리스트뷰 어댑터에 연결된 ArrayList의 특정위치(position)에 있는 item을 가져오는 메소드
		return mItems.get(position);
	}
	//해당 item을 나타내는 고유한 정보인 positioin을 반환(ID값을 가져옴)
	public long getItemId(int position) {
		return position;
	}

	//아이템에서 표시할 뷰를 리턴하는 메소드 정의
	public View getView(int position, View convertView, ViewGroup parent) {

		FillListItemView itemView;

		if (convertView == null) {
			//최초에 한번 null값이 들어온다
			itemView = new FillListItemView(mContext);
		} else {
			itemView = (FillListItemView) convertView;
		}

		//현재 데이터를 넘겨줌
		itemView.setContents(0, ((String) mItems.get(position).getData(0)));
		itemView.setContents(1, ((String) mItems.get(position).getData(1)));
		itemView.setContents(2, ((String) mItems.get(position).getData(3)));

		return itemView;
	}

}
