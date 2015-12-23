package com.beautyhealth.Infrastructure.CWComponent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.beautyhealth.R;

public class CWViewGage extends View {

	private int firstBitmap,secondBitpmap;
	private Bitmap gageplate;
	private Bitmap gagerow;
	private Bitmap tip;
	private Context cxt;
	
	private int x;
	private int y;
	private int yoffset;
	private int xoffset;
	private double scalerate;
	//
	private double degree,rotatePivotX,rotatePivotY;
	// Matrix 实例
	private Matrix matrix = new Matrix();
	private CWMediaHelper aimageHelper;
	
	public CWViewGage(Context context)
	{
		super(context);
		cxt=context;
		initail();
		setBitmap();
	}
	public CWViewGage(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		cxt=context;
		initail();
		setBitmap();
	}
	
	
	private void setBitmap(){
		gageplate=aimageHelper.CreateBitmap(firstBitmap, scalerate);
		gagerow = aimageHelper.CreateBitmap(secondBitpmap,scalerate);
		tip = aimageHelper.CreateBitmap(R.drawable.tips,1);
	}
	
	private void initail(){
		firstBitmap=R.drawable.pressuregage;
		secondBitpmap=	R.drawable.gagerow;
		x=0;
		y=0;
		yoffset=0;
		xoffset=0;
		scalerate=0.8;
		degree=0;
		rotatePivotX=0;
		rotatePivotY=0;
		// 获得位图helper
		aimageHelper=new CWMediaHelper(cxt);
	}
	
	public void setRotate(double _degree,double _rotatePivotX,double _rotatePivotY){
		degree=_degree;
		rotatePivotX=_rotatePivotX;
		rotatePivotY=_rotatePivotY;
	}
	
	public void addBitmap(int _firstbitmap,int _secondbitmap){
		firstBitmap=_firstbitmap;
		secondBitpmap=_secondbitmap;
		setBitmap();
	}
	
	
	public void setdrawLocation(int _x,int _y){
		x=_x;
		y=_y;
	}
	
	public void setScaleRate(double _scalerate){
		scalerate=_scalerate;
		setBitmap();
	}
	public void setSecondPicOffset(int _xoffset,int _yoffset){
		xoffset=_xoffset;
		yoffset=_yoffset;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{	super.onDraw(canvas);
	
	    
	    
	
		int platewidth=gageplate.getWidth();
		int rowswidth=gagerow.getWidth();
		canvas.drawBitmap(gageplate, x, y, null);
		/////////////cancel
		//canvas.drawBitmap(tip,266,282, null);
		
		int translatex=x+platewidth/2-rowswidth/2+xoffset;
		int translatey=y+yoffset;
		matrix.setTranslate(translatex, y+yoffset);
		matrix.preRotate((float)degree, (float)rotatePivotX-translatex,(float)rotatePivotY-translatey);
		canvas.drawBitmap(gagerow, matrix, null);
		
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore(); 
	}
}

