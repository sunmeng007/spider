package com.iflytek.spider.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.thirdparty.guava.common.collect.Maps;

import com.iflytek.spider.model.ZhiDaoKfcModel;
import com.iflytek.spider.model.ZhiHuKfcModel;
import com.iflytek.spider.protocol.Content;
import com.iflytek.spider.protocol.httpclient.HttpSimply;

public class ZhiHuKfcParse implements Parse {
  
  
  public static final String viewPage = "http://www.zhihu.com/question/\\d+|http://www\\.zhihu\\.com/search\\?q=%E8%82%AF%E5%BE%B7%E5%9F%BA*";
  
  HtmlParser hp = new HtmlParser();
  
  @Override
  public List<Object> parse(String url, Content content) {
    ArrayList list = new ArrayList();
    try {
      
      System.out.println(url);
      if (url.matches(viewPage)) {
        // get the other outlink
        Outlink[] ols = hp.getOutLink(content, viewPage);
        for (Outlink ol : ols)
          list.add(ol);
        
        String html = new String(content.getContent(), "utf-8");

        ZhiHuKfcModel kh = new ZhiHuKfcModel();
        
        kh.sid = url;
        kh.parseQuestionTitle(html);
        kh.parseQuestionAnswers(html);
        
        list.add(kh);
      } else {
        System.out.println("nothing matching!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }
  
  public static void main(String[] args) throws Exception {
    System.out.println(ZhiHuKfcModel.class.getName());
    String url;
    url = "http://www.zhihu.com/question/20111412";
    HttpSimply hp = new HttpSimply();
    Content content = hp.getProtocolOutput(url).getContent();
    ZhiHuKfcParse parse = new ZhiHuKfcParse();
    List po = parse.parse(url, content);
    for (Object o : po)
      System.out.println(o.toString());
  }
}
