/**
 *
 */
package com.beautyhealth.PersonHealth.AbilityFunction.Activity;

import java.io.IOException;
import java.util.List;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWMobileDevice.AbsMobilePhone;
import com.beautyhealth.Infrastructure.CWMobileDevice.MobilePhone422;
import com.beautyhealth.Infrastructure.CWMobileDevice.MyAlarm;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.UserCenter.Entity.FamilyNumberMessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends Activity
{
	MediaPlayer alarmMusic;
	WakeLock mWakelock;
	String AlarmID=null;
	ISqlHelper iSqlHelper=null;    		
	MyAlarm alarm=null;
	MyCount mc = null;
	String message="您的亲人长时间处于非活动状态，请您多加留意。";
	AbsMobilePhone smssender=null;
	int count = 0;
	int sampleCount =0;
	Button stop;
	TextView title;
	TextView times;
	
	@Override
	protected void onPause() {
		super.onPause();
		mWakelock.release();
	}

	@Override
	protected void onResume() {
		super.onResume();
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
	    mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
		mWakelock.acquire();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.alarm_activity);
	    
		setWinWake();
		
		iSqlHelper=new SqliteHelper(null,getApplicationContext());    
		smssender=new MobilePhone422();
		
		getAlarmSample();
		
		setTimer();
		
		if (alarm != null) {
			loadMustic();
			//设置计数器加1
		    count = Integer.parseInt(alarm.count);
		    sampleCount =  Integer.parseInt(alarm.sampleCount);
		    sampleCount++;
		    alarm.sampleCount = String.valueOf(sampleCount);
		    iSqlHelper.Update(alarm);// 更新数据库samplecount
		    
		    title = (TextView) findViewById(R.id.title);
		    title.setText(alarm.title);
		    times = (TextView) findViewById(R.id.times);
		    times.setText("已提醒："+alarm.sampleCount+"次");		    
	        
		}
		else{
			AlarmActivity.this.finish();
		}	
		stop = (Button) findViewById(R.id.stop);
	    stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mc.cancel();
				alarmMusic.stop();
				alarmMusic.release();
				alarm.sampleCount="0";
				sampleCount = 0;
				alarm.enabled="1";
				iSqlHelper.Update(alarm);// 更新数据库samplecount
				alarm.setRepeatAlarm(getApplicationContext(), AlarmActivity.class);
				AlarmActivity.this.finish();
			}
        });		
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
            // 创建退出对话框  
            AlertDialog isExit = new AlertDialog.Builder(AlarmActivity.this).create();  
            // 设置对话框标题  
            isExit.setTitle("系统提示");  
            // 设置对话框消息  
            isExit.setMessage("确定要退出吗");  
            // 添加选择按钮并注册监听  
            isExit.setButton("确定", listener);  
            isExit.setButton2("取消", listener);  
            // 显示对话框  
            isExit.show();  
        }            
        return false;            
    }  
    /**监听对话框里面的button点击事件*/  
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()  
    {  
        public void onClick(DialogInterface dialog, int which)  
        {  
            switch (which)  
            {  
            case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序 
            	mc.cancel();
            	alarmMusic.stop();	
            	alarmMusic.release();
				alarm.sampleCount="0";
				sampleCount = 0;
				alarm.enabled="0";
				iSqlHelper.Update(alarm);// 更新数据库samplecount
				alarm.cancelRepeatAlarm(getApplicationContext(),
						AlarmActivity.class);				  
				AlarmActivity.this.finish();
                break;  
            case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框  
                break;  
            default:  
                break;  
            }  
        }  
    };

	private void setTimer() {
		mc=new MyCount(60000, 60000);  //////warning
		mc.start();
	}
	
	private void loadMustic() {
		try {
			/* 重置MediaPlayer */
			alarmMusic=new MediaPlayer();
			if (alarm.musicfile.equals("") || alarm.musicfile.equals("默认铃声")) {
				// 加载指定音乐，并为之创建MediaPlayer对象
				alarmMusic = MediaPlayer.create(this, R.raw.alarm);
			} else {
				/* 设置要播放的文件的路径 */
				alarmMusic.setDataSource(alarm.musicfile);
				/* 准备播放 */
				alarmMusic.prepare();
				alarmMusic.setLooping(true);
			}
			/* 开始播放 */
			alarmMusic.start();
		} catch (IOException e) {
			
		}
	}

	private void getAlarmSample() {
		Intent _intent=getIntent();
		AlarmID= _intent.getAction();
		if(AlarmID!=null){
			//取数据库值
			String wheres=" alertID='"+AlarmID+"'";
	 		List<Object> alarmList=iSqlHelper.Query("com.beautyhealth.Infrastructure.CWMobileDevice.MyAlarm", wheres);	
	 		if(alarmList.size()>0)
	 		{
	 			alarm=(MyAlarm)alarmList.get(0);
	 		}	 		
		}
	}

	private void setWinWake() {
		final Window win = getWindow();
		 win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		 | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		 win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		 | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}
	
	class MyCount extends CountDownTimer {     
        public MyCount(long millisInFuture, long countDownInterval) {     
            super(millisInFuture, countDownInterval);     
        }     
        
        @Override     
        public void onFinish() {     // 非手动            
			alarmMusic.stop(); // 停止
			alarmMusic.release();
			if (alarm != null) {
				// 计算 count<=samplecount,：samplecount/count/title/
				if (count <= sampleCount) { 					
			 		List<Object> numberMessage=iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.FamilyNumberMessage", null);	
			 		if(numberMessage.size()>0)
			 		{
				 		for(int i=0;i<numberMessage.size();i++){			 			
				 			FamilyNumberMessage fnm=(FamilyNumberMessage) numberMessage.get(i);  
				 			if(!fnm.Tel.equals("")){
				 				smssender.sendMessage(fnm.Tel,message+"("+alarm.title+"  已提醒："+alarm.sampleCount+"次"+")");	
				 			}
				 			
				 		}
				 		mc.cancel();
						sampleCount = 0;
						alarm.enabled="1";
						alarm.sampleCount="0";
				 		alarm.setRepeatAlarm(getApplicationContext(), AlarmActivity.class);
				 		iSqlHelper.Update(alarm);// 更新数据库samplecount
						AlarmActivity.this.finish();
			 		}
					alarm.sampleCount="0";
				}
				iSqlHelper.Update(alarm);// 更新数据库samplecount
			}
			AlarmActivity.this.finish();
        }  
        
        @Override     
        public void onTick(long millisUntilFinished) {              
        	
        }          
    }
}

