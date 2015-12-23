package com.beautyhealth.PersonHealth.AbilityFunction.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;
import com.beautyhealth.Infrastructure.CWMobileDevice.MyAlarm;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetAlarmActivity extends NavAndTabBarActivity implements OnClickListener {
	private TabBarFragment tabBarFragment;
	EditText tipTitle;
	EditText timespan;
	EditText tipCount;
	Button alarm_save;
	Button alarm_stop;
	Button alarm_delete;
	Button music_select;
	private final int SELECTMUSIC = 1;
	String Path;
	String musicName;

	ISqlHelper mysql = null;
	MyAlarm alarm = null;

	private final String tag_intent = "HAVE_ALERMID";
	private final String tag_alarmobj = "ALERMOBJ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm);
		initNavBar("提醒设置", "<返回","帮助");
		initVariable();
		fetchUIFromLayout();
		setListener();
		loadAlarm();
	}
	
	public void onNavBarRightButtonClick(View view) {
    	 AlertDialog help = new AlertDialog.Builder(SetAlarmActivity.this).create(); 
    	 help.setTitle("帮助");  
    	 help.setMessage("1.每间隔您设置的提醒间隔即会发出提醒。"+"\n"+"2.当未处理的提醒累计超过您设置的提醒次数，呼叫将自动启动，即向您设定的亲情号发送短信。");  
    	 help.setButton("我知道了", listener);  
    	 help.show();  
    }
	
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()  
    {  
        public void onClick(DialogInterface dialog, int which)  
        {  
            switch (which)  
            {  
            case AlertDialog.BUTTON_POSITIVE:       					  
            	dialog.dismiss();
                break;    
            default:  
                break;  
            }  
        }  
    };

	private void initVariable() {
		mysql = new SqliteHelper(null, getApplicationContext());
	}

	private void setListener() {
		alarm_save.setOnClickListener((OnClickListener) this);
		alarm_stop.setOnClickListener((OnClickListener) this);
		alarm_delete.setOnClickListener((OnClickListener) this);
		music_select.setOnClickListener((OnClickListener) this);
	}

	private void fetchUIFromLayout() {
		alarm_save = (Button) findViewById(R.id.alarm_save);
		alarm_stop = (Button) findViewById(R.id.alarm_stop);
		alarm_delete = (Button) findViewById(R.id.alarm_delete);
		tipTitle = (EditText) findViewById(R.id.tipTitle);
		timespan = (EditText) findViewById(R.id.timespan);
		tipCount = (EditText) findViewById(R.id.tipCount);
		
		timespan.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		tipCount.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		
		music_select= (Button) findViewById(R.id.btn_selectMusic);
	}

	private void loadAlarm() {
		Intent intent = this.getIntent();
		boolean haveAlarm = intent.getBooleanExtra(tag_intent, false);
		if (haveAlarm) {
			alarm = (MyAlarm) intent.getSerializableExtra(tag_alarmobj);
			// ��UI���������
			tipTitle.setText(alarm.title);
			timespan.setText(alarm.timespan);
			tipCount.setText(alarm.count);
			
			if(alarm.enabled.equals("0"))
				alarm_stop.setText("启用");
			else alarm_stop.setText("停用");

			if(alarm.musicfile.equals(""))
				music_select.setText("默认铃声（点击设置）");
			else music_select.setText(getfileName(alarm.musicfile)+"（点击设置）");
		}else music_select.setText("默认铃声（点击设置）");
	}
    
	private String  getfileName(String path){
		String filename="";
		String[] filenames=path.split("/");
		filename=filenames[filenames.length-1];

		String[] names_load=filename.split("\\.");  // “.”和“|”都是转义字符，必须得加"\\"     如果有多个分隔符，可以用"|"作为连字符
		filename = names_load[0];
		if(filename.length()>10)
			filename = filename.substring(0, 7)+"...";
		return filename;
	}
	
	@Override
	public void onClick(View v) {		
		if (v == alarm_save) {
			mysql.CreateTable("com.beautyhealth.Infrastructure.CWMobileDevice.MyAlarm");
			// ������������
			// �ж��ı�������ȷ
			MyAlarm myalarm = new MyAlarm();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String StartTime = formatter.format(curDate);
			String identity = java.util.UUID.randomUUID().toString();
			if (alarm != null) {
				myalarm.alertID = alarm.alertID;
			} else {
				myalarm.alertID = identity;
			}
			myalarm.title = tipTitle.getText().toString();
			myalarm.timespan = timespan.getText().toString();
			myalarm.starttime = StartTime;
			myalarm.count = tipCount.getText().toString();
			myalarm.enabled = "1";// Ĭ��ֵ
			myalarm.sampleCount="0";
			myalarm.musicfile=Path;
			
			if(!"".equals(myalarm.title)&& !"".equals(myalarm.timespan)&& !"".equals(myalarm.count)){ 
				
				 if(0<Integer.valueOf(myalarm.timespan)&&Integer.valueOf(myalarm.timespan)<=2048){
				 }else{
					 Toast.makeText(getApplicationContext(), "提醒间隔输入不合法", Toast.LENGTH_LONG).show();
				 }
			 
				 if(0<Integer.valueOf(myalarm.count)&&Integer.valueOf(myalarm.count)<=2048){
				 }else{
					 Toast.makeText(getApplicationContext(), "提醒次数输入不合法", Toast.LENGTH_LONG).show();
				 }
								 
				if (alarm != null) {
					// ɾ����ǰ��
					myalarm.cancelRepeatAlarm(getApplicationContext(),AlarmActivity.class);
					mysql.Delete(myalarm, new String[] { "alertID" });	
				}
				// ���һ���µ�
				myalarm.setRepeatAlarm(getApplicationContext(), AlarmActivity.class);
				// //�����ӱ��������ݿ���
				alarm = myalarm;
					
				mysql.Insert(myalarm);//�����޸ĺ��
				Toast.makeText(getApplicationContext(), "闹铃设置成功", Toast.LENGTH_LONG).show();
				finish();				
			}else Toast.makeText(getApplicationContext(), "请将信息输入完整",Toast.LENGTH_LONG).show();
		} else if (v == alarm_stop) {
			String result="";
			if (alarm != null) {
				if(alarm_stop.getText().equals("停用")){
					alarm.enabled="0";
					alarm_stop.setText("启用");
					alarm.cancelRepeatAlarm(getApplicationContext(),AlarmActivity.class);
					result="闹钟暂停";
				}else if(alarm_stop.getText().equals("启用")){
					alarm.enabled="1";
					alarm.sampleCount="0";
					alarm_stop.setText("停用");
					alarm.setRepeatAlarm(getApplicationContext(), AlarmActivity.class);
					result="闹钟已恢复";
				}
				mysql.Update(alarm);
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
				.show();
			}else{
				Toast.makeText(getApplicationContext(), "您没有设置闹铃",Toast.LENGTH_LONG).show();
			}
			// ���Ƚ�����enable��������Ϊ0,��������
			// �����ݿ��н�enable�������Ϊ0
			// ��������ֹͣ
			Toast.makeText(getApplicationContext(), "闹铃保存成功", Toast.LENGTH_LONG)
					.show();
		} else if (v == alarm_delete) {
			if (alarm != null) {
				mysql.CreateTable("com.beautyhealth.Infrastructure.CWMobileDevice.MyAlarm");
				// ɾ����ǰ��
				alarm.cancelRepeatAlarm(getApplicationContext(),AlarmActivity.class);
				mysql.Delete(alarm, new String[] { "alertID" });
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "您没有设置闹铃",
						Toast.LENGTH_LONG).show();
			}
		}else if (v == music_select) {
			Intent _aintent=new Intent();
			_aintent.setClass(getApplicationContext(), MusicList.class);
			startActivityForResult(_aintent, SELECTMUSIC);
		}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==SELECTMUSIC&&resultCode==-1){
			String filePath=data.getStringExtra("fileName");
			music_select.setText(getfileName(filePath)+"（点击设置）");		
			Path=filePath;
		}
	}	
}
