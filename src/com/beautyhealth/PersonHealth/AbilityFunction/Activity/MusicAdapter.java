package com.beautyhealth.PersonHealth.AbilityFunction.Activity;

import java.util.List;

import com.beautyhealth.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter {
	
	private List<MusicItemBean> musicList;
	private LayoutInflater inflater;
	
	public MusicAdapter(Context context,List<MusicItemBean> list){
		musicList=list;
		inflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return musicList.size();
	}

	@Override
	public Object getItem(int position) {
		return musicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView , ViewGroup parent) {
		
		ViewHolder viewHolder;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.music_item,null);
			viewHolder.name=(TextView)convertView.findViewById(R.id.item);

			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		} 
		viewHolder.name.setText(musicList.get(position).musicName);	///////////
		return convertView;
	}
	
	class ViewHolder{
		public TextView name;
	}  

}
