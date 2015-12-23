package com.beautyhealth.Infrastructure.CWDataDecode;


public interface IDataDecode {
     public Object decode(String data,String className);
     public void setClassFullName(int dataresultID);
}
