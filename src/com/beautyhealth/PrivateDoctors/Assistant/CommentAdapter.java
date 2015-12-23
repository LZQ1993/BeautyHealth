package com.beautyhealth.PrivateDoctors.Assistant;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.PrivateDoctors.Entity.MedicalIssue;
import com.beautyhealth.PrivateDoctors.Entity.Reply;

public class CommentAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<MedicalIssue> doctorInfo;
	private List<List<Reply>> replyInfo;
	private Context context;
	private OnClickListener mycListener;  
	private CommentReplyAdapter myAdapter;
	
	public CommentAdapter(Context context,
			List<MedicalIssue> doctorInfo,
			List<List<Reply>> replyInfo,
			OnClickListener mycListener,CommentReplyAdapter myAdapter) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.doctorInfo = new ArrayList<MedicalIssue>();
		this.replyInfo = new ArrayList<List<Reply>>();
		this.mycListener = mycListener;
		this.myAdapter = myAdapter;	
		this.doctorInfo.addAll(doctorInfo);
		this.replyInfo.addAll(replyInfo);

	}

	public void clearList() {
		this.doctorInfo.clear();
		this.replyInfo.clear();
	}

	public void updateList(List<MedicalIssue> doctorInfo,
			List<List<Reply>> replyInfo) {
		this.doctorInfo.addAll(doctorInfo);
		this.replyInfo.addAll(replyInfo);
	}

	public static class ViewHolder {
		TextView tv_issue, tv_asker,tv_issueNum,tv_mz;
		Button btn_issue_pic;
		ListView reply_area;
		LinearLayout ll_mianze;
	}

	@Override
	public int getCount() {
		return doctorInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return doctorInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// 可以理解为从vlist获取view 之后把view返回给ListView
			convertView = inflater.inflate(
					R.layout.activity_pd_medicalconsult_item, null);
			holder.tv_issue = (TextView) convertView
					.findViewById(R.id.tv_pd_mc_item_issue);
			holder.tv_asker = (TextView) convertView
					.findViewById(R.id.tv_pd_mc_item_asker);
			holder.ll_mianze = (LinearLayout) convertView
					.findViewById(R.id.ll_mianze);
			holder.btn_issue_pic = (Button) convertView
					.findViewById(R.id.btn_issue_pic);
			
			holder.reply_area = (ListView) convertView
					.findViewById(R.id.reply_area);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MedicalIssue medicalIssue = (MedicalIssue) doctorInfo.get(position);
		holder.tv_issue.setText(medicalIssue.getQuestionContent());
		holder.tv_asker.setText(medicalIssue.getUserID());
		if(medicalIssue.getReply().size()>0){
			holder.ll_mianze.setVisibility(View.VISIBLE);
		}
		if (replyInfo.get(position) != null)
			holder.reply_area.setAdapter(new CommentReplyAdapter(context,replyInfo.get(position), position ,mycListener));
		holder.btn_issue_pic.setTag(position+":");		
		holder.btn_issue_pic.setOnClickListener(mycListener);
		return convertView;
	}

}
