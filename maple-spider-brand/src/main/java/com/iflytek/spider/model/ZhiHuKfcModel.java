package com.iflytek.spider.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avro.reflect.Nullable;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.thirdparty.guava.common.collect.Lists;
import org.apache.hadoop.thirdparty.guava.common.collect.Maps;

import com.iflytek.avro.util.AvroUtils;
import com.iflytek.indexer.solr.DynamicIndex;
import com.iflytek.indexer.solr.DynamicIndexConstant;

public class ZhiHuKfcModel {
  
  @Nullable
  @DynamicIndex(DynamicIndexConstant.TEXT_GENERAL)
  public String questionTitle;
  
  @Nullable
  @DynamicIndex(DynamicIndexConstant.TEXT_GENERAL)
  public String answers; 
  
  @DynamicIndex("*")
  public String sid = "";
  
  private static Configuration conf  = new Configuration();
  
  public ZhiHuKfcModel()
  {
    conf.addResource("zhihu.xml");
  }
  
  public String toString()
  {
    return AvroUtils.toAvroString(this);
  }
  
  public boolean parseQuestionTitle(String html)
  {
    if(null == html)
    {
      return false;
    }
    
    String askRegular = conf.get("questionTitle");
    questionTitle = match(askRegular,html);
    
    return true;
  }
  
  public boolean parseQuestionAnswers(String html)
  {
    if(null == html)
    {
      return false;
    }
    
    Map<String,String> answersRegular = Maps.newHashMap();
    answersRegular.put("zm_item_answer", conf.get("zm_item_answer"));
    answersRegular.put("count", conf.get("count"));
    answersRegular.put("content", conf.get("content"));
    if(!allMatch(answersRegular, html).isEmpty())
    {
      answers = allMatch(answersRegular, html).get(0);
    }
    
    return true;
  }
  
  private String match(String re, String html) {
    Pattern purl = Pattern.compile(re, Pattern.DOTALL);
    
    Matcher murl = purl.matcher(html);
    String cd = "";
    if (murl.find()) {
      cd = murl.group();
    }
    while(murl.find())
    {
      cd = murl.group();
    }
    return cd.replaceAll("<.*?>", "").replaceAll("\n", " ").trim();
  }
  
  private List<String> allMatch(Map<String,String> re, String html) {
    if(null == re || null == html)
    {
      return null;
    }
    Pattern purl = Pattern.compile("<div class=\"fixed-summary zm-editable-content clearfix\">.*?</div>",Pattern.DOTALL);
    Matcher murl = purl.matcher(html);
    String cd = "";
    List<String> ret = Lists.newArrayList();
    if(murl.find())
    {
      cd = murl.group();
      cd = cd.replaceAll("<.*?>", "").replaceAll("\n", " ").trim();
      ret.add(cd);
    }
    
    return ret;
  }
  
  /**
   * 执行登录过程
   * @param user
   * @param pwd
   * @param debug
   * @throws IOException
   */
  public void login(String user, String pwd) throws IOException {
    HttpClient client = new HttpClient();
    PostMethod method = new PostMethod("http://www.zhihu.com/login");
    method.setParameter("email", "miaolunshou@126.com");
    method.setParameter("password", "123456");
    
    BufferedReader br = null;
    
    try{
      int returnCode = client.executeMethod(method);

      if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
        System.err.println("The Post method is not implemented by this URI");
        // still consume the response body
        method.getResponseBodyAsString();
      } else {
        br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
        String readLine;
        while(((readLine = br.readLine()) != null)) {
          System.err.println(readLine);
      }
      }
    } catch (Exception e) {
      System.err.println(e);
    } finally {
      method.releaseConnection();
      if(br != null) try { br.close(); } catch (Exception fe) {}
    }

  }
}
