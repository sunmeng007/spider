package com.iflytek.spider.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.thirdparty.guava.common.collect.Maps;

import com.iflytek.spider.model.ZhiDaoKfcModel;
import com.iflytek.spider.protocol.Content;
import com.iflytek.spider.protocol.httpclient.HttpSimply;

public class KfcParse implements Parse {
  
  
  public static final String viewPage = "http://zhidao.baidu.com/question/\\d+.html|http://zhidao.baidu.com/search\\?.*word=%BF%CF%B5%C2%BB%F9.*";
  
  HtmlParser hp = new HtmlParser();
  
  @Override
  public List<Object> parse(String url, Content content) {
    ArrayList list = new ArrayList();
    try {
      
      System.out.println(url);
      if (url.matches(viewPage)) {
//      if (url.matches(rootPage)) {
        System.out.println("matching");
        // get the other outlink
        Outlink[] ols = hp.getOutLink(content, viewPage);
        for (Outlink ol : ols)
          list.add(ol);
        
        String html = new String(content.getContent(), "gbk");
//        System.out.println(html);
        ZhiDaoKfcModel km = new ZhiDaoKfcModel();
        
        km.parseAskTitle(html);
        km.parseBestAnswer(html);
        km.parseOtherAnswer(html);
        
        list.add(km);
      } else {
        System.out.println("nothing matching!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }
  
  public String match(String re, String html) {
    Pattern purl = Pattern.compile(re, Pattern.DOTALL);
    
    Matcher murl = purl.matcher(html);
    String cd = "";
    if (murl.find()) {
      cd = murl.group();
    }
    while (murl.find()) {
      cd = murl.group();
    }
    return cd.replaceAll("<.*?>", "").replaceAll("\n", " ").trim();
  }
  
  public Map<String,String> matchAskAndAnswer(String askre, String answerre,
      String html) {
    if (null == askre || null == html || null == answerre) {
      return null;
    }
    Map<String,String> matMap = Maps.newHashMap();
    Pattern purl = Pattern.compile(askre, Pattern.DOTALL);
    
    Matcher murl = purl.matcher(html);
    String cd = "";
    String ask = "";
    String answer = "";
    if (murl.find()) {
      cd = murl.group();
      ask = cd.replaceAll("<.*?>", "").replaceAll("\n", " ").trim();
    }
    
    purl = Pattern.compile(answerre, Pattern.DOTALL);
    murl = purl.matcher(html);
    if (murl.find()) {
      cd = murl.group();
      answer = cd.replaceAll("<.*?>", "").replaceAll("\n", " ").trim();
    }
    matMap.put(ask, answer);
    return matMap;
  }
  
  public static void main(String[] args) throws Exception {
    System.out.println(ZhiDaoKfcModel.class.getName());
    String url;
    url = "http://zhidao.baidu.com/search?lm=0&rn=10&pn=0&fr=search&ie=gbk&word=%BF%CF%B5%C2%BB%F9";
//    url = "http://zhidao.baidu.com/question/292747901.html";
    // url = "http://zhidao.baidu.com/question/221265007.html";
    HttpSimply hp = new HttpSimply();
    Content content = hp.getProtocolOutput(url).getContent();
    KfcParse parse = new KfcParse();
    List po = parse.parse(url, content);
    for (Object o : po)
      System.out.println(o.toString());
  }
}
