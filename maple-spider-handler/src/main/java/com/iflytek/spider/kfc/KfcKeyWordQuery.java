package com.iflytek.spider.kfc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.google.common.collect.Lists;

public class KfcKeyWordQuery {
  public  Map<String,String> keyWordsWeight = null;
  public  Map<String,String> serviceConfiguration = null;
  public  Map<String,String> filterWords = null;

  public KfcKeyWordQuery(String filterWordsPath,String weightKeyWords,String serviceConfig)
  {
    keyWordsWeight = ReadPropertyUtil.getPropertiesToMap(weightKeyWords);
    filterWords = ReadPropertyUtil.getPropertiesToMap(filterWordsPath);
    serviceConfiguration = ReadPropertyUtil.getPropertiesToMap(serviceConfig);
  }
  
  public List<String> getIKAnalyzerResultsOfQuery(String query) {
    if (null == query) {
      return null;
    }
    
    try {
      List<String> seg = Lists.newArrayList();
      StringReader re = new StringReader(query);
      IKSegmenter ik = new IKSegmenter(re, true);
      Lexeme lex = null;
      while ((lex = ik.next()) != null) {
        System.out.print(lex.getLexemeText() + "|");
        seg.add(lex.getLexemeText());
      }
      return seg;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public JSONObject sendGet(String Value,String start, String rows,String wt) {
    if (null == Value) {
      return null;
    }
    int timeout = 3;
    HttpClient client = new HttpClient();
    client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
    // 设置httpclient超时连接时间未3000ms中
    client.getParams().setIntParameter(
        HttpConnectionParams.CONNECTION_TIMEOUT, timeout * 1000);
    client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
        timeout * 1000);

    InputStream response = null;
    
    String url = serviceConfiguration.get("url");
    if (null == url) {
      return null;
    }

    GetMethod get = new GetMethod(url);
    
    // 设置http 方法的超时时间
    get.getParams().setIntParameter(
        HttpConnectionParams.CONNECTION_TIMEOUT, timeout * 1000);
    get.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
        timeout * 1000);

    NameValuePair qKeyValue = new NameValuePair("q", "askTitle_s:*" + Value + "*");
    NameValuePair wtKeyValue = new NameValuePair("wt", wt);
    NameValuePair startKeyValue = new NameValuePair("start", start);
    NameValuePair rowsKeyValue = new NameValuePair("rows", rows);
    get.setQueryString(new NameValuePair[] { qKeyValue, wtKeyValue,
        startKeyValue,rowsKeyValue });

    System.out.println(get.getQueryString());
    try {
      int status = client.executeMethod(get);
      if (status == HttpStatus.SC_OK) {
        // get.getResponseBodyAsString();
        response = get.getResponseBodyAsStream();
      } else {
        throw new Exception(
            "send http request to cqa failure,please check the network,the status is:"
                + status);
      }
    } catch (Exception e) {
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
    
    JSONObject json = JSONObject.fromObject(sbout.toString());
    get.releaseConnection();
    return json;
  }
}
