package com.beautyhealth.Infrastructure.CWSqlite;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;




import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqliteHelper implements ISqlHelper{
     private SQLiteDatabase dataBase;
     private String _dataBaseName;
     private Context _context;
     
     public SqliteHelper(String DBName,Context context){
    	 if(DBName==""||DBName==null){
    		 _dataBaseName="Course.db"; 
    	 }else{
    		 _dataBaseName=DBName;	  
    	 }
    	 _context=context;
    	  DBOpen();
     }
     public void SQLExec(String sql){
    	 dataBase.execSQL(sql);
     }
     private void DBOpen(){
    	 dataBase = _context.openOrCreateDatabase(_dataBaseName, Context.MODE_PRIVATE, null);
     }
     public Boolean CreateTable(String TableName){
    	 DBOpen();
    	 String Propertys="_id INTEGER PRIMARY KEY AUTOINCREMENT,";
    	 DBOpen();
    	 Class<?> cls=null;
    	 String _classAbsName="";
		try {
			cls = Class.forName(TableName);
			_classAbsName=getAbsClassName(cls);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	 Field[] fs = cls.getFields ();
    	for ( int i = 0 ; i < fs. length ; i++){
            Field f = fs[i];
            String FieldName=f.getName();
            f.setAccessible( true ); // ����Щ�����ǿ��Է��ʵ�
            Propertys+=FieldName+" VARCHAR,";
    	}
    	 Propertys=Propertys.substring(0, Propertys.length()-2);
    	 String sql="CREATE TABLE IF NOT EXISTS "+_classAbsName +"("+Propertys+")";
    	 SQLExec(sql);
    	 return true;
     }
     
     private String getAbsClassName(Class _class){
    	 String classFullName = _class.getName(); 
    	 String[] names=classFullName.split("\\.");
    	 String className=names[names.length-1];
    	 return className;
     }
     
     public Boolean Insert(Object obj){ 
    	 ReturnObj ro=InsertOrUpdate(obj);
         dataBase.insert(ro.className, null, ro.cv);  
    	 return true;
     }
     public Boolean Update(Object obj){
    	 ReturnObj ro=InsertOrUpdate(obj);
         dataBase.update(ro.className, ro.cv, ro.Primarykey+" = ?", new String[]{ro.PrimaryValue});  
    	 return true;
     }
     public Boolean Delete(Object obj){
       	 DBOpen();
    	 String Primarykey="";
    	 String PrimaryValue="";
    	 Class<?> cls=obj.getClass();
    	 String className=getAbsClassName(cls);
    	 Field[] fs = cls.getFields ();
    	 Field f=fs[0];
    	 Primarykey=f.getName();
    	 Object val=null;
         try{
            val=f.get(obj);// �õ������Ե�ֵ    
         }catch(Exception ex){
        	 Log.d("errors:", ex.getMessage());
         }
         if(val==null){
    	     val="";
         }
    	 PrimaryValue=val.toString();
    	 dataBase.delete(className, Primarykey+" = ?", new String[]{PrimaryValue});  
    	 return true;
     }
     
     public Boolean Delete(Object obj,String[] where){
    	 String Whereclause="";
    	 if(where!=null&&where.length>0)
    	 {
    		 String[] whereValue=new String[where.length]; 
        	 Class<?> cls=obj.getClass();
        	 String className=getAbsClassName(cls);
        	 Field[] fs = cls.getFields ();
    		 for(int i=0;i<where.length;i++){
    			 Whereclause+=where[i]+"=?";
        		 Whereclause+=" and ";
        		 
        		 for ( int j = 0 ; j < fs.length ; j++){
                     Field f = fs[j];
                     String fieldName=f.getName();
                     if(fieldName.equals(where[i])){
                    	 try {
             				whereValue[i]=f.get(obj).toString();
             				break;
             			} catch (IllegalArgumentException e) {
             				e.printStackTrace();
             			} catch (IllegalAccessException e) {
             				e.printStackTrace();
             			}
                     }
             	} 
    		 }
    		 Whereclause=Whereclause.substring(0, Whereclause.length()-4);

        	 
         	
         	DBOpen();
         	dataBase.delete(className, Whereclause, whereValue); 	 
    	 }
    	 return true;
     }
     
     private class ReturnObj
     {
    	public String Primarykey;
    	public String PrimaryValue;
    	public String className;
    	public ContentValues cv;
     }
     
     private ReturnObj InsertOrUpdate(Object obj){
    	 DBOpen();
    	 String Primarykey="";
    	 String PrimaryValue="";
    	 ContentValues cv = new ContentValues(); 
    	 Class<?> cls=obj.getClass();
    	 String className=getAbsClassName(cls);
    	 Field[] fs = cls.getFields ();
         for ( int i = 0 ; i < fs. length ; i++){
             Field f = fs[i];
             String FieldName=f.getName();
             f.setAccessible( true ); // ����Щ�����ǿ��Է��ʵ�
             Object val=null;
             try{
                val=f.get(obj);// �õ������Ե�ֵ    
             }catch(Exception ex){
            	 Log.d("errors:", ex.getMessage());
             }
             if(val==null){
        	     val="";
             }
             if(i==0){
            	 Primarykey=f.getName();
            	 PrimaryValue=val.toString();
             }
             cv.put(FieldName, val.toString());  
         } 
         ReturnObj ro=new ReturnObj();
         ro.className=className;
         ro.cv=cv;
         ro.Primarykey=Primarykey;
         ro.PrimaryValue=PrimaryValue;
         return ro;
     }
     
     
     public List<Object> Query(String className,String WhereStr){
    	 DBOpen();
    	 Class<?> cls=null;
    	 String _classAbsName="";
		try {
			cls = Class.forName(className);
			_classAbsName=getAbsClassName(cls);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	 Field[] fs = cls.getFields ();
    	 String SqlPRE="SELECT * FROM "+_classAbsName;
         if(WhereStr!=null){
        	 SqlPRE+=" where "+WhereStr;
         }
    	 Cursor c = dataBase.rawQuery(SqlPRE, null);  
    	 List<Object> lists=new ArrayList<Object>();
    	 while (c.moveToNext()) { 
    		 Object objectCopy=null;
			try {
				objectCopy = cls.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}  
    		 for ( int i = 0 ; i < fs. length ; i++){
                 Field f = fs[i];
                 String FieldName=f.getName();
                 f.setAccessible( true ); // ����Щ�����ǿ��Է��ʵ�
                 String type = f.getType().toString(); // �õ������Ե�����
                 if (type.endsWith("String")) {
                    try {
						f.set(objectCopy, c.getString(c.getColumnIndex(FieldName))) ;
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}        // ��������ֵ
                 } 
                 else if (type.endsWith("int") || type.endsWith("Integer")){
                	 try {
						f.set(objectCopy, c.getInt(c.getColumnIndex(FieldName)));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                 } 
                 else {
                	 try {
						f.set(objectCopy, c.getString(c.getColumnIndex(FieldName)));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                 }
             } 
        	 lists.add(objectCopy);
         }
         c.close();
         return lists;
     }

     
     public void CloseDB(){
    	 dataBase.close();  
     }
}
