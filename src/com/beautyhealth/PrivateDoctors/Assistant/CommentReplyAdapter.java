package com.beautyhealth.PrivateDoctors.Assistant;

import java.util.List;

import com.beautyhealth.R;
import com.beautyhealth.PrivateDoctors.Entity.Reply;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CommentReplyAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Reply> list;
	private Context context;
	private OnClickListener myOnitemcListener;
	private int parentPostion;

	public CommentReplyAdapter(Context context, List<Reply> list,int parentPostion,OnClickListener myOnitemcListener) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.myOnitemcListener = myOnitemcListener;
		this.parentPostion= parentPostion;
		this.list = list;
	}

	public static class ViewHolder {
		TextView reply_people, reply_time, reply_content;
		Button btn_reply_pic;
	}

	public void clearList() {
		this.list.clear();
	}

	public void updateList(List<Reply> list) {
		this.list.addAll(list);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
	
		if (convertView == null) {
			holder = new ViewHolder();
			// 可以理解为从vlist获取view 之后把view返回给ListView;
			convertView = inflater.inflate(R.layout.activity_pd_medicalconsult_item_reply_item, null);
			holder.reply_time = (TextView) convertView
					.findViewById(R.id.reply_time);
			holder.reply_people = (TextView) convertView
					.findViewById(R.id.reply_people);
			holder.reply_content = (TextView) convertView
					.findViewById(R.id.reply_content);
			holder.btn_reply_pic = (Button) convertView
					.findViewById(R.id.btn_reply_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.reply_time.setText(list.get(position).getTime());
		holder.reply_people.setText(list.get(position).getReplyPeople());
		holder.reply_content.setText(list.get(position).getContent());
		holder.btn_reply_pic.setTag(position+":"+parentPostion);
		// 给Button添加单击事件 添加Button之后ListView将失去焦点 需要的直接把Button的焦点去掉
		holder.btn_reply_pic.setOnClickListener(myOnitemcListener);
		return convertView;
	}
}
