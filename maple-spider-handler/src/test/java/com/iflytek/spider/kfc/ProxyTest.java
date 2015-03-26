package com.iflytek.spider.kfc;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

public class ProxyTest {
  private static final String PROXY_HOST = "60.220.212.12";
  private static final int PROXY_PORT = 80;
  
  @Test
  public void mainTest() {
    HttpClient client = new HttpClient();
    HttpMethod method = new GetMethod("http://www.baidu.com");
    
    HostConfiguration config = client.getHostConfiguration();
    config.setProxy(PROXY_HOST, PROXY_PORT);
    
    String username = "guest";
    String password = "s3cr3t";
    Credentials credentials = new UsernamePasswordCredentials(username,
        password);
    AuthScope authScope = new AuthScope(PROXY_HOST, PROXY_PORT);
    
//    client.getState().setProxyCredentials(authScope, credentials);
    client.getState().setProxyCredentials(authScope, null);
    InputStream response = null;
    try {
      client.executeMethod(method);
      
      if (method.getStatusCode() == HttpStatus.SC_OK) {
        response = method.getResponseBodyAsStream();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } 
    
    // 将InputStream 对象转换为String
    StringBuffer sbout = new StringBuffer();
    if (null != response) {
      byte[] b = new byte[4096];
      try {
        for (int n; (n = response.read(b)) != -1;) {
          sbout.append(new String(b, 0, n,"utf-8"));
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          response.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    System.out.println(sbout.toString());
    method.releaseConnection();
  }
  
  @Test
  public void secondTest() {
    
  }
  
  @Test
  public void thirdTest() {
    
  }
}
