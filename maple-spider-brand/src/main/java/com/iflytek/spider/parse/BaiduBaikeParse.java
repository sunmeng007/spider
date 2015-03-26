package com.iflytek.spider.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iflytek.spider.model.BrandModel;
import com.iflytek.spider.protocol.Content;
import com.iflytek.spider.protocol.httpclient.HttpSimply;

public class BaiduBaikeParse implements Parse {
  
  public static final String viewPage = "http://baike.baidu.com/view/\\d+.htm";
  HtmlParser hp = new HtmlParser();
  
  @Override
  public List<Object> parse(String url, Content content) {
    ArrayList list = new ArrayList();
    try {
      
      if (url.matches(viewPage)) {
        // get the other outlink
        Outlink[] ols = hp.getOutLink(content, viewPage);
        for (Outlink ol : ols)
          list.add(ol);
        
        String html = new String(content.getContent(), "utf-8");
        String title = match("<title>.*?</title>", html);
        BrandModel bm = new BrandModel();
        bm.title = title;
        list.add(bm);
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
    return cd.replaceAll("<.*?>", "").replaceAll("\n", " ").trim();
  }
  
  public static void main(String[] args) throws Exception {
    System.out.println(BrandModel.class.getName());
    String url;
    url = "http://baike.baidu.com/view/36809.htm";
    HttpSimply hp = new HttpSimply();
    Content content = hp.getProtocolOutput(url).getContent();
    BaiduBaikeParse parse = new BaiduBaikeParse();
    List po = parse.parse(url, content);
    for (Object o : po)
      System.out.println(o.toString());
    
  }
}
