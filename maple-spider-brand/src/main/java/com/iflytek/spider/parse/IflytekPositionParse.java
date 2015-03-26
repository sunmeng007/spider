package com.iflytek.spider.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iflytek.spider.model.RecruitModel;
import com.iflytek.spider.protocol.Content;
import com.iflytek.spider.protocol.httpclient.HttpSimply;

public class IflytekPositionParse implements Parse  {

  public static final String viewPage = "http://join.iflytek.com/xfzp/channels/\\d+.html";
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
        String re1="()";  // Tag 1
        String re2="(<)"; // Any Single Character 1
        String re3="(a)"; // Variable Name 1
        String re4="(\\s+)";  // White Space 1
        String re5="(href)";  // Word 1
        String re6="(=)"; // Any Single Character 2
        String re7="(\")";  // Any Single Character 3
        String re8="(\\/)"; // Any Single Character 4
        String re9="(xfzp)";  // Word 2
        String re10="(\\/)";  // Any Single Character 5
        String re11="(contents)"; // Word 3
        String re12="(\\/)";  // Any Single Character 6
        String re13="(\\d)";  // Any Single Digit 1
        String re14="(\\/)";  // Any Single Character 7
        String re15=".*?";  // Non-greedy match on filler
        String re16="(\")"; // Any Single Character 8
        String re17="(\\s+)"; // White Space 2
        String re18="(target)"; // Word 4
        String re19="(=)";  // Any Single Character 9
        String re20="(\"_blank\")"; // Double Quote String 1
        String re21="(>)";  // Any Single Character 10
        String re22=".*?";  // Non-greedy match on filler
        String re23="(<\\/a>)"; // Tag 2
        String re24="(<\\/span>)";  // Tag 3
        String re = re1+re2+re3+re4+re5+re6+re7+re8+re9+re10+re11+re12+re13+re14+re15+re16+re17+re18+re19+re20+re21+re22+re23+re24;
        String position = match(re, html);
        RecruitModel rm = new RecruitModel();
        rm.position = position;
        list.add(rm);
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
    while(murl.find())
    {
      cd = murl.group();
    }
    return cd.replaceAll("<.*?>", "").replaceAll("\n", " ").trim();
  }
  
  public static void main(String[] args) throws Exception {
    System.out.println(RecruitModel.class.getName());
    String url;
    url = "http://join.iflytek.com/xfzp/channels/3.html";
    HttpSimply hp = new HttpSimply();
    Content content = hp.getProtocolOutput(url).getContent();
    IflytekPositionParse parse = new IflytekPositionParse();
    List po = parse.parse(url, content);
    for (Object o : po)
      System.out.println(o.toString());
    
  }
}
