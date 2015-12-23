package com.beautyhealth.SafeGuardianship.BloodPressureGuardianship.Activity;




import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beautyhealth.Infrastructure.CWComponent.CWChartLine2D;
import com.beautyhealth.Infrastructure.CWComponent.CWChartPoint2D;
import com.beautyhealth.Infrastructure.CWComponent.SplineChartView;


public class ChartSample extends Activity {

	private SplineChartView mspchar;	
	private List<Pressure> testDataPressure=new ArrayList<Pressure>();
	private List<CWChartLine2D> Lines=new ArrayList<CWChartLine2D>();
	private List<Integer> colors=new ArrayList<Integer>();
	private double YAxistMax=0;
	private double YAxistMin=0;

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setLandscape();
		
		loadDataFromDataSourece();
		set2DLines();
		
	    setChart();
	    
	    initActivity();
	    this.setTitle("myChart");
		
	}



	private void setLandscape() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		  WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
	}
	
	//修改这里
	private void set2DLines(){
		if(testDataPressure.size()>0){
			CWChartLine2D heartValueLine=new CWChartLine2D();
			heartValueLine.LineTitle="心率";
			heartValueLine.XUnit="时间";
			heartValueLine.YUnit="次数";
			
			CWChartLine2D hightValueLine=new CWChartLine2D();
			hightValueLine.LineTitle="高压";
			hightValueLine.XUnit="时间";
			hightValueLine.YUnit="mms/l";
			
			CWChartLine2D lowValueLine=new CWChartLine2D();
			lowValueLine.LineTitle="低压";
			lowValueLine.XUnit="时间";
			lowValueLine.YUnit="mms/l";
			
			for(int i=0;i<testDataPressure.size();i++){
				Pressure ap=testDataPressure.get(i);
				CWChartPoint2D heartValuePoint=new CWChartPoint2D();
				heartValuePoint.AY=	ap.HeartValue;
				heartValuePoint.AX=	i*10;
				heartValuePoint.AXLabel=ap.MeasuerTime;
				
				CWChartPoint2D hightValuePoint=new CWChartPoint2D();
				hightValuePoint.AY=	ap.HightValue;
				hightValuePoint.AX=	i*10;
				hightValuePoint.AXLabel=ap.MeasuerTime;
				
				CWChartPoint2D lowValuePoint=new CWChartPoint2D();
				lowValuePoint.AY=	ap.LowValue;
				lowValuePoint.AX=	i*10;
				lowValuePoint.AXLabel=ap.MeasuerTime;
				
				if(i==0){
					if(ap.HightValue>ap.HeartValue){
						YAxistMax=ap.HightValue;
					}else{
						YAxistMax=ap.HeartValue;
					}
					if(ap.LowValue<ap.HeartValue){
						YAxistMin=ap.LowValue;
					}else{
						YAxistMin=ap.HeartValue;
					}
					
				}else{
					setMaxValue(ap.HeartValue);
					setMaxValue(ap.HightValue);
					setMaxValue(ap.LowValue);
					setMinValue(ap.HeartValue);
					setMinValue(ap.HightValue);
					setMinValue(ap.LowValue);
				}
				heartValueLine.ChartPoints.add(heartValuePoint);
				hightValueLine.ChartPoints.add(hightValuePoint);
				lowValueLine.ChartPoints.add(lowValuePoint);
			}
			Lines.add(heartValueLine);
			Lines.add(hightValueLine);
			Lines.add(lowValueLine);
		}
	}
	
	//修改这里
	private void loadDataFromDataSourece(){
		Pressure ap=null;
		for(int i=0;i<15;i++){
			ap=new Pressure();
			ap.HeartValue=60+Math.random()*10;
			ap.HightValue=110+Math.random()*30;
			ap.LowValue=80+Math.random()*30;
			ap.UserID="13591995551";
			if(i==2){
				ap.MeasuerTime="2015/09/02 21:23:30";
			}else{
				ap.MeasuerTime="2015/09/01 21:23:30";
			}
			testDataPressure.add(ap);
		}
	}
	
	//修改这里
	private void setChart() {  
	    colors.add(Color.rgb(54, 141, 238));
	    colors.add(Color.rgb(255, 165, 132));
	    colors.add(Color.rgb(84, 206, 231));
		mspchar=new SplineChartView(this,Lines,colors);	//平滑曲线图	
        mspchar.getChart().setTitle("血压测量");
		//数据轴最大值
        mspchar.getChart().getDataAxis().setAxisMax(Math.floor(YAxistMax*1.2));
        mspchar.getChart().getDataAxis().setAxisMin(Math.floor(YAxistMin*0.8));
		//数据轴刻度间隔
        mspchar.getChart().getDataAxis().setAxisSteps(20);
        mspchar.getChart().getPlotGrid().getHorizontalLinePaint().setColor(Color.rgb(179, 147, 197));
	}
	
	private void setMinValue(double value){
		if(YAxistMin>value){
			YAxistMin=value;
		}
	}
	
	private void setMaxValue(double value){
		if(YAxistMax<value){
			YAxistMax=value;
		}
	}
	
	
	protected void onResume() {
		/**
		 * 设置为横屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();
	}
	
	private void initActivity()
	{
	       FrameLayout content = new FrameLayout(this);    
	       
	       //缩放控件放置在FrameLayout的上层，用于放大缩小图表
		   FrameLayout.LayoutParams frameParm = new FrameLayout.LayoutParams(
		   LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
		   frameParm.gravity = Gravity.BOTTOM|Gravity.RIGHT;  
		   
		   
		   //图表显示范围在占屏幕大小的98%的区域内
		   DisplayMetrics dm = getResources().getDisplayMetrics();		   
		   int scrWidth = (int) (dm.widthPixels); 	
		   int scrHeight = (int) (dm.heightPixels); 			   		
	       RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
	    		   													scrWidth, scrHeight);
	       
	       //居中显示
           layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);   
           //图表view放入布局中，也可直接将图表view放入Activity对应的xml文件中
           final RelativeLayout chartLayout = new RelativeLayout(this);  
      
           chartLayout.addView(mspchar, layoutParams);
           
	        //增加控件
		   ((ViewGroup) content).addView(chartLayout);
		 
		   //增加一个按钮用于打开时间选择框
		   setTimeSelected(content);	
		   setContentView(content);	
	}

	private void setTimeSelected(FrameLayout content) {
		
		Button setTime=new Button(this);
		setTime.setText("setTime");
		RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams (200,40);  //设置按钮的宽度和高度
		btParams.leftMargin = 0;   //横坐标定位        
		btParams.topMargin = 0;   //纵坐标定位         
		((ViewGroup) content).addView(setTime,btParams);//将按钮放入layout组件

		setTime.setOnClickListener(new OnClickListener()	
		{				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "打开时间选择", Toast.LENGTH_SHORT).show();
			}
		});
	}
    
}
