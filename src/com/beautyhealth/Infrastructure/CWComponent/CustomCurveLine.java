package com.beautyhealth.Infrastructure.CWComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class CustomCurveLine extends View {
	
	private static final int XTIPPAINTID=0;
	private static final int YTIPPAINTID=1;
	private static final int XTITILEPAINTID=2;
	private static final int YTITILEPAINTID=3;
	private static final int CHATTITLEPAINTID=4;
	
	private int bgcolor;
	private List<Map<String,Point>> points=new ArrayList<Map<String,Point>>();
	private int curveNumber;
	private int canvasOffsetX,canvasOffsetY;
	private List<Paint> paints=new ArrayList<Paint>();
	private List<Paint> textpaints=new ArrayList<Paint>();
	private List<Path> paths=new ArrayList<Path>();
	private List<PathEffect> pathEffects=new ArrayList<PathEffect>();
	private int Xwidth,Xstep,Xspace,AxisXTipOffsetX,AxisXTipOffsetY,XTitlePostionX,XTitlePostionY;
	private int Ywidth,Ystep,Yspace,AxisYTipOffsetX,AxisYTipOffsetY,YTitlePostionX,YTitlePostionY;
    private int TitleX,TitleY;
    private String TitleName,XTipName,YTipName;
	
	public CustomCurveLine(Context context)
	{
		super(context);
		curveNumber=2;
		initial();		
	}
	public CustomCurveLine(Context context,int _curveNumber){
		super(context);
		curveNumber=_curveNumber;
		initial();
	}
	
	public CustomCurveLine(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		curveNumber=2;
		initial();
	}
	
	private void initial(){
		
		AxisXTipOffsetX=0;
		AxisXTipOffsetY=-10;
		AxisYTipOffsetX=-20;
		AxisYTipOffsetY=0;
		
		XTitlePostionX=620;
		XTitlePostionY=-20;
		YTitlePostionX=-30;
		YTitlePostionY=350;
	    TitleX=300;
	    TitleY=-50;
	    
	    TitleName="My Chat";
	    XTipName="date(yyyy-mm-dd)";
	    YTipName="value(mms)";
		
		
		canvasOffsetX=40;
		canvasOffsetY=100;
		
		for(int i=0;i<4+curveNumber;i++){
			Paint apaint=new Paint();
			paints.add(apaint);
			
			Path apath=new Path();
			apath.moveTo(0, 0);
			paths.add(apath);
			
			PathEffect apatheffect=new CornerPathEffect(10);
			pathEffects.add(apatheffect);	
		}
		
		for(int i=0;i<5;i++){
			Paint bpaint=new Paint();
			bpaint.setStrokeWidth(2);
			bpaint.setStyle(Paint.Style.STROKE);
			bpaint.setColor(Color.BLACK);
			textpaints.add(bpaint);	
		}
		
		bgcolor=Color.WHITE;
		setAxisXWidth(600,7,300,10);
		setAxisXStyle(4,Paint.Style.STROKE,Color.BLACK);
		setAxisYStyle(4,Paint.Style.STROKE,Color.BLACK);
		setTableXLineStyle(1,Paint.Style.STROKE,Color.GRAY);
		setTableYLineStyle(1,Paint.Style.STROKE,Color.GRAY);
		
		for(int j=0;j<curveNumber;j++){
			setCurveLineStyle(3,Paint.Style.STROKE,Color.RED,j+1);
			for(int i=1;i<=Xstep;i++){
				addDataPoints(Xspace*i,(int)(Math.random()*100)+20,j+1);
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);		
		drawViewLines();
		// 将背景填充成白色
		canvas.drawColor(bgcolor);		
		// 对Canvas执行坐标变换：将画布“整体位移”到canvasOffsetX, canvasOffsetY处开始绘制
		canvas.translate(canvasOffsetX, canvasOffsetY);
		//paint.setPathEffect(effect);		
		canvas.drawPath(paths.get(0), paints.get(0));
		canvas.drawPath(paths.get(1), paints.get(1));
		canvas.drawPath(paths.get(2), paints.get(2));
		canvas.drawPath(paths.get(3), paints.get(3));
		for(int j=0;j<curveNumber;j++){
			canvas.drawPath(paths.get(4+j), paints.get(4+j));
		}
		

		drawXTips(canvas);
		drawYTips(canvas);
		drawXTitle(canvas);
		drawYTitle(canvas);
		drawChatTitle(canvas);
	}
	
	private void drawXTips(Canvas canvas){
		for(int i=1;i<=Xstep;i++){
			canvas.drawText(String.valueOf(Xspace*i), Xspace*i+AxisXTipOffsetX, AxisXTipOffsetY, textpaints.get(XTIPPAINTID));
		}
	}
	private void drawYTips(Canvas canvas){
		for(int i=1;i<=Ystep;i++){
			canvas.drawText(String.valueOf(Yspace*i), AxisYTipOffsetX, Yspace*i+AxisYTipOffsetY, textpaints.get(YTIPPAINTID));
		}
	}
	private void drawXTitle(Canvas canvas){
		canvas.drawText(XTipName, XTitlePostionX, XTitlePostionY, textpaints.get(XTITILEPAINTID));
	}
	private void drawYTitle(Canvas canvas){
		canvas.drawText(YTipName, YTitlePostionX, YTitlePostionY, textpaints.get(YTITILEPAINTID));
	}
	private void drawChatTitle(Canvas canvas){
		canvas.drawText(TitleName, TitleX, TitleY, textpaints.get(CHATTITLEPAINTID));
	}
	
	
	
	private void drawViewLines(){
		drawLine();
		drawAxisX();
		drawAxisY();
		drawTableLine();
	}
	private void drawTableLine(){
		drawTableXLine();
		drawTableYLine();
	}
	private void drawTableXLine(){
		for(int i=1;i<=Xstep;i++){
			paths.get(2).moveTo(Xspace*i, 0);///////////////
			paths.get(2).lineTo(Xspace*i, Ywidth);
		}
	}
	private void drawTableYLine(){
		int temp=Ywidth/Ystep;
		for(int i=1;i<=Ystep;i++){
			paths.get(3).moveTo(0, Yspace*i);///////////////
			paths.get(3).lineTo(Xwidth, Yspace*i);
		}
	}
	private void drawAxisX(){
		paths.get(0).lineTo(Xwidth,0);
	}	
	private void drawAxisY(){
		paths.get(1).lineTo(0,Ywidth);
	}
	
	//0-x轴,1-y轴,2-tableline X轴间隔线,3-Y轴间隔线，4以后都 是曲线curveline
	private void drawLine(){
		
		for(int j=0;j<curveNumber;j++){
			for (int i = 0; i <points.size(); i++)
			{
				Point pointTemp=points.get(i).get(String.valueOf(j+1));
				// 生成15个点，随机生成它们的Y座标。并将它们连成一条Path
				if(pointTemp!=null){
					paths.get(4+j).lineTo((float)pointTemp.x, (float)pointTemp.y);
				}
			}
		}

	}
	//设置画布平移X、Y量
	public void setCanvasOffset(int _canvasOffsetX, int _canvasOffsetY){
		canvasOffsetX=_canvasOffsetX;
		canvasOffsetY=_canvasOffsetY;
	}
	
	//设置X、Y轴长度以及段数
	public void setAxisXWidth(int _Xwidth,int _Xstep,int _Ywidth,int _Ystep){
		Xwidth=_Xwidth;
		Xstep=_Xstep;
		Xspace=Xwidth/Xstep;
		Ywidth=_Ywidth;
		Ystep=_Ystep;
		Yspace=Ywidth/Ystep;
	}

	//设置X轴绘制的样式
	public void setAxisXStyle(int _strokeWidth,Style _sytle,int _color){
		paints.get(0).setStrokeWidth(_strokeWidth);
		paints.get(0).setStyle(_sytle);
		paints.get(0).setColor(_color);
	}
	//设置X轴绘制的样式
	public void setAxisXStyle(Paint _paint){
		paints.set(0, _paint);
	}
	//设置Y轴绘制的样式
	public void setAxisYStyle(int _strokeWidth,Style _sytle,int _color){
		paints.get(1).setStrokeWidth(_strokeWidth);
		paints.get(1).setStyle(_sytle);
		paints.get(1).setColor(_color);
	}
	//设置Y轴绘制的样式
	public void setAxisYStyle(Paint _paint){
		paints.set(1, _paint);
	}
	//设置X分段线绘制的样式
	public void setTableXLineStyle(int _strokeWidth,Style _sytle,int _color){
		paints.get(2).setStrokeWidth(_strokeWidth);
		paints.get(2).setStyle(_sytle);
		paints.get(2).setColor(_color);
	}
	//设置X分段线绘制的样式
	public void setTableXLineStyle(Paint _paint){
		paints.set(2, _paint);
	}
	//设置Y分段线绘制的样式
	public void setTableYLineStyle(int _strokeWidth,Style _sytle,int _color){
		paints.get(3).setStrokeWidth(_strokeWidth);
		paints.get(3).setStyle(_sytle);
		paints.get(3).setColor(_color);
	}
	//设置Y分段线绘制的样式
	public void setTableYLineStyle(Paint _paint){
		paints.set(3, _paint);
	}
	//设置曲线绘制的样式
	public void setCurveLineStyle(int _strokeWidth,Style _sytle,int _color,int curveId){
		int index=4+curveId-1;
		paints.get(index).setStrokeWidth(_strokeWidth);
		paints.get(index).setStyle(_sytle);
		paints.get(index).setColor(_color);
	}
	
	//设置曲线绘制的样式
	public void setCurveLineStyle(Paint _paint,int curveId){
		int index=4+curveId-1;
		paints.set(index, _paint);
	}

	//设置曲线数量
	public void setCurveNumber(int _curveNumber){
		curveNumber=_curveNumber;
	}
	
    public void addDataPoints(int _x,int _y,int _curveId){
    	Point apoint=new Point();
    	apoint.x=_x;
    	apoint.y=_y;
    	
    	String sCurveId = String.valueOf(_curveId);
    	HashMap<String, Point> witchCurvePoint = new HashMap<String, Point>();  
    	witchCurvePoint.put(sCurveId, apoint);  
    	points.add(witchCurvePoint);  
    }
	
    //设置X轴上分段数值属性
	public void setAxisXTextTip(int _offsetX,int _offsetY){

		AxisXTipOffsetX=_offsetX;
		AxisXTipOffsetY=_offsetY;
	}
    //设置Y轴上分段数值属性
	public void setAxisYTextTip(int _offsetX,int _offsetY){
		AxisYTipOffsetX=_offsetX;
		AxisYTipOffsetY=_offsetY;
	}
    //设置X轴上标题
	public void setAxisXTitle(int _posX,int _posY,String _content){
		XTitlePostionX=_posX;
		XTitlePostionY=_posY;
		XTipName=_content;
	}
    //设置Y轴上标题
	public void setAxisYTitle(int _posX,int _posY,String _content){
		YTitlePostionX=_posX;
		YTitlePostionY=_posY;
		 YTipName=_content;
	}
	
    //设置图形标题
	public void setAxisTitle(int _posX,int _posY,String _content){
	    TitleX=_posX;
	    TitleY=_posY;
	    TitleName=_content;
	}
	
 
   
}