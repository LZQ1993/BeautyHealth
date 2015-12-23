package com.beautyhealth.Infrastructure.CWDataRequest;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class RequestUtility {
	private String IP=null;
    private String ComponentName=null;
    private String MethodName=null;
    private String NotificationName=null;
    private Map Params=new HashMap();
    private Context context;
    public RequestUtility(Context _context){
    	context=_context;
    	setIP(null);
    	setMethod(null,null);
    	setNotification(null);

    }
    public RequestUtility(String _ip,String _componentName,String _methodName,String _notificationName){
    	setIP(_ip);
    	setMethod(_componentName,_methodName);
    	setNotification(_notificationName);
    }
    public void setIP(String _ip){
    	if (_ip==null){
    		IP=NetworkSetInfo.getServiceUrl();
    	}
    	else{
    		IP=_ip;
    	}
    }
    public void setMethod(String _componentName,String _methodName){
    	if (_componentName==null){
    		ComponentName="Client";
    	}
    	else{
    		ComponentName=_componentName;
    	}
    	if (_methodName==null){
    		MethodName="Logon.ashx";
    	}
    	else{
    		MethodName=_methodName;
    	}
    }
    public void setNotification(String _notificationName){
    	if (_notificationName==null){
    		NotificationName=context.getResources().getString(context.getResources().getIdentifier("datareload_action", "string",context.getPackageName()));
    	}
    	else{
    		NotificationName=_notificationName;
    	}
    }
    public String getIP(){
    	return IP;
    }
    public String getComponentName(){
    	return ComponentName;
    }
    public String getMethodName(){
    	return MethodName;
    }
    public String getNotificationName(){
    	return NotificationName;
    }
    
    public void setParams(Map _params){
    	Params=_params;
    }
    public Map getParams(){
    	return Params;
    }
    
    public String getURLString(){
    	return String.format("%1$s/%2$s/%3$s", IP,ComponentName,MethodName);
    }

}
