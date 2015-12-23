package com.beautyhealth.PersonHealth.MyLocation.Activity;

import java.util.List;

import com.beautyhealth.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private List<ItemBean> mList;
	private LayoutInflater mInflater;
	
	
	public MyAdapter(Context context,  List<ItemBean> list){
		mList=list;
	
		mInflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	

	@Override
	public View getView(int Position, View contentView, ViewGroup arg2) {
		
		
		ViewHolder viewHolder;
		
		if(contentView==null){
			viewHolder=new ViewHolder();
			contentView = mInflater.inflate(R.layout.textview, null);
			viewHolder.content = (TextView) contentView.findViewById(R.id.textView);
			contentView.setTag(viewHolder);
		}else{
			
			viewHolder = (ViewHolder) contentView.getTag();
		}
		ItemBean bean=mList.get(Position);
		viewHolder.content.setText(bean.ItemContent);
		return contentView;
		
		
	}
	class ViewHolder{
		public TextView content;
	}

}
