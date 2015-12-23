package com.beautyhealth.Infrastructure.CWSqlite;

import java.util.List;




public interface ISqlHelper {
	public Boolean CreateTable(String TableName);
    public Boolean Insert(Object obj);
    public Boolean Update(Object obj);
    public Boolean Delete(Object obj);
    public Boolean Delete(Object obj,String[] where);
    public void SQLExec(String sql);
    public void CloseDB();
    public List<Object> Query(String className,String WhereStr);
}
