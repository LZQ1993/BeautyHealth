package com.beautyhealth.Infrastructure.CWDataRequest;

/**
 * 网络服务信息
 * 
 */
public class NetworkSetInfo {

//private final static String serviceUrl = "http://123.57.152.6";
   // private final static String serviceUrl = "http://192.168.0.2:30580";
   // private final static String serviceUrl = "http://192.168.1.150:8889";
	private final static String serviceUrl = "http://www.symnyk.com";
    private final static String homePageUrl = "http://pupboss.com/scloud/";
    
    /**
     * 获取服务器地址
     */
    public static String getServiceUrl() {
        return serviceUrl;
    }
    
    /**
     * 获取主页地址
     */
    public static String getHomePageUrl() {
    	return homePageUrl;
    }

}
