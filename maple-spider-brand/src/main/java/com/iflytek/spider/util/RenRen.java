package com.iflytek.spider.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class RenRen {
  
  public static void main(String[] args) throws ClientProtocolException,
      IOException {
//    String loginurl = "http://www.renren.com/PLogin.do";
    String loginurl = "http://www.zhihu.com/login";
    String username = "miaolunshou@126.com";
    String password = "zxm20062550";
    System.out.println(RenRen.posturl(loginurl, username, password));
  }
  
  public static String getulr(String url)
  {
    HttpClient httpclient = new DefaultHttpClient();
    HttpGet httpget = new HttpGet(url);
    return null;
  }
  
  public static String posturl(String loginurl, String username, String password)
      throws ClientProtocolException, IOException {
    
    HttpClient httpclient1 = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(loginurl);
    List<NameValuePair> formparams = new ArrayList<NameValuePair>();
    formparams.add(new BasicNameValuePair("_xsrf", "576fe83a45404078ab6676e306f96382"));
    formparams.add(new BasicNameValuePair("email", username));
    formparams.add(new BasicNameValuePair("password", password));
    formparams.add(new BasicNameValuePair("captcha", ""));
    formparams.add(new BasicNameValuePair("rememberme", "y"));
    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "utf-8");
    httppost.setEntity(entity);
    
    httppost.setHeader("Accept","*/*");
//    httppost.setHeader("Accept-Encoding","gzip,deflate,sdch");
//    httppost.setHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
//    httppost.setHeader("Cache-Control","max-age=0");
    httppost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
    httppost.setHeader("Host","www.zhihu.com");
//    httppost.setHeader("Origin","http://www.zhihu.com");
    httppost.setHeader("Connection","keep-alive");
    httppost.setHeader("Referer","http://www.zhihu.com/#signin");
//    httppost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
    
    httppost.setHeader("Cookie", "zata=zhihu.com.4a7bba738d544fd29391b11efa29baea.772781; zatb=zhihu.com");
    
    String str = "";
    HttpClient httpclient2 = null;
    try {
      HttpResponse response1 = httpclient1.execute(httppost);
      
      Header locationHeader = response1.getFirstHeader("Location");
      String login_success = locationHeader.getValue();// 获取登陆成功之后跳转链接
      
      System.out.println(login_success);
      
      HttpGet httpget = new HttpGet(login_success);
      httpclient2 = new DefaultHttpClient();
      HttpResponse response2 = httpclient2.execute(httpget);
      str = EntityUtils.toString(response2.getEntity());
      httppost.abort();
      httpget.abort();
    } finally {
      httpclient1.getConnectionManager().shutdown();
      httpclient2.getConnectionManager().shutdown();
    }
    
    return str;
  }
}