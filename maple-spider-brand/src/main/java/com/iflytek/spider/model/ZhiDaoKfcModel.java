package com.iflytek.spider.model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avro.reflect.Nullable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.thirdparty.guava.common.collect.Lists;

import com.iflytek.avro.util.AvroUtils;
import com.iflytek.indexer.solr.DynamicIndex;
import com.iflytek.indexer.solr.DynamicIndexConstant;

public class ZhiDaoKfcModel {
  
  @Nullable
  @DynamicIndex(DynamicIndexConstant.STRING)
  public String askTitle;
  
  @Nullable
  @DynamicIndex(DynamicIndexConstant.STRING)
  public String bestAnswer;

  @Nullable
  @DynamicIndex(DynamicIndexConstant.MSTRING)
  public List<String> otherAnswer;
  
  @DynamicIndex("*")
  public String sid = "";
  
  private static Configuration conf  = new Configuration();
  
  public ZhiDaoKfcModel()
  {
    conf.addResource("zhidao.xml");
  }
  
  public String toString()
  {
    return AvroUtils.toAvroString(this);
  }
  
  public boolean parseAskTitle(String html)
  {
    if(null == html)
    {
      return false;
    }
    String askRegular = conf.get("askTitle");
    askTitle = match(askRegular,html);
    
    return true;
  }
  
  public boolean parseBestAnswer(String html)
  {
    if(null == html)
    {
      return false;
    }
    String bestRegular = conf.get("bestAnswer");
    bestAnswer = match(bestRegular,html);
    
    return true;
  }
  
  public boolean parseOtherAnswer(String html)
  {
    if(null == html)
    {
      return false;
    }
    String otherRegular = conf.get("otherAnswers");
    otherAnswer = allMatch(otherRegular,html);
    
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
  
  private List<String> allMatch(String re, String html) {
    if(null == re || null == html)
    {
      return null;
    }
    Pattern purl = Pattern.compile(re, Pattern.DOTALL);
    Matcher murl = purl.matcher(html);
    String cd = "";
    List<String> ret = Lists.newArrayList();
    while(murl.find())
    {
      cd = murl.group();
      ret.add(cd.replaceAll("<.*?>", "").replaceAll("\n", " ").trim());
    }
    return ret;
  }
}
