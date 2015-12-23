package com.beautyhealth.Infrastructure.CWDataDecode;

import android.content.Context;


public class DataDecode implements IDataDecode {

	private Context context;
	private int objClassFullName=0;

	public DataDecode(Context _context) {
		context = _context;
	}
	
	public void setClassFullName(int dataresultID){
		objClassFullName=dataresultID;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object decode(String data, String className){
		
		DataResult dataResult = new DataResult();
		JsonDecode jsonDecode = new JsonDecode();
		String obj="";
		if(objClassFullName!=0){
			obj=context.getResources().getString(objClassFullName);
		}else{
			obj=context.getResources().getString(context.getResources().getIdentifier("dataresult_default", "string",context.getPackageName()));
		}

		try {
			try {
				try {
					if(!(jsonDecode.fromJson(data, obj,className) instanceof DataResult)){
						dataResult = null;					
					}else{
					dataResult = (DataResult)jsonDecode.fromJson(data, obj,className);
					}
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return  dataResult;
	}

}
