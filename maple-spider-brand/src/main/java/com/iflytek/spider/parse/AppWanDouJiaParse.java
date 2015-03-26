package com.iflytek.spider.parse;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.iflytek.avro.util.AvroUtils;
import com.iflytek.spider.model.AppWanDouJiaModel;
import com.iflytek.spider.protocol.Content;
import com.iflytek.spider.protocol.httpclient.HttpSimply;

@SuppressWarnings("rawtypes")
public class AppWanDouJiaParse implements Parse {
  public static final String listPage = "http://127.0.0.1:8080/app2.html";
//  public static final String detailPage = "^http://www.wandoujia.com/app/\\w+\\.?\\w+\\.?\\w+\\.?\\w+\\.?\\w+\\.?\\w+\\.?\\w+\\.?\\w+\\.?\\w*$";
  public static final String detailPage = "http://www.wandoujia.com/app/.*";
  HtmlParser hp = new HtmlParser();
  HtmlCleaner cleaner = new HtmlCleaner();
  public static CleanerProperties prop = new CleanerProperties();
  static {
    prop.setAdvancedXmlEscape(true);
  }
  @Override
  public List<Object> parse(String url, Content content) {
    
    ArrayList list = new ArrayList();
    try {
      
      if (url.matches(listPage)) {
        // get the other outlink
        Outlink[] ols = hp.getOutLink(content, detailPage);
        for (Outlink ol : ols)
          list.add(ol);
      } else if (url.matches(detailPage)) {
        AppWanDouJiaModel adm = new AppWanDouJiaModel();
        adm.url = url;
        TagNode node = cleaner.clean(new ByteArrayInputStream(content
            .getContent()));
        DomSerializer ds = new DomSerializer(prop);
        Document doc = ds.createDOM(node);
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node nd = (Node) xpath.evaluate("//span[contains(@class,'title')]/text()", doc,
            XPathConstants.NODE);
        adm.appName = getContent(nd);
        nd = (Node) xpath.evaluate("//a[contains(@class,'comment-open')]/i/text()", doc, XPathConstants.NODE);
        adm.commentNum = getContent(nd);
        nd = (Node) xpath.evaluate("//i[contains(@itemprop,'interactionCount')]/text()", doc, XPathConstants.NODE);
        String downloads = getContent(nd);
//        adm.downloads = (int)(downloads.contains("万") ? Double.parseDouble(downloads.split(" 万")[0]) * 10000 : Double.parseDouble(downloads));
        adm.downloads = (int)(downloads.contains("亿") ? Double.parseDouble(downloads.split(" 亿")[0]) * 100000000 : downloads.contains("万") ? Double.parseDouble(downloads.split(" 万")[0]) * 10000 : Double.parseDouble(downloads));
        nd = (Node) xpath.evaluate("//dl[contains(@class,'infos-list')]/dd/text()", doc, XPathConstants.NODE);
        adm.size = getContent(nd);
        nd = (Node) xpath.evaluate("//div[contains(@itemprop,'description')]/text()", doc, XPathConstants.NODE);
        adm.desc = getContent(nd);
        nd = (Node) xpath.evaluate("//span[contains(@class,'dev-sites')]/text()", doc, XPathConstants.NODE);
        adm.author = getContent(nd);
        nd = (Node) xpath.evaluate("//time[contains(@itemprop,'datePublished')]/text()", doc, XPathConstants.NODE);
        adm.updateTime = getContent(nd);
        nd = (Node) xpath.evaluate("//dd[contains(@itemprop,'operatingSystems')]/text()", doc, XPathConstants.NODE);
        adm.os = getContent(nd);
        NodeList ndList;
        ndList = (NodeList) xpath.evaluate("//ul[contains(@class,'perms-list')]/*", doc, XPathConstants.NODESET);
        for(int i = 0; i < ndList.getLength(); i++)
        {
          if(adm.authorities == null)  
          {
            adm.authorities = getContent(ndList.item(i));
            continue;
          }
          adm.authorities += " " + getContent(ndList.item(i));
        }
        ndList = (NodeList) xpath.evaluate("//dd[contains(@class,'tag-box')]/*", doc, XPathConstants.NODESET);
        for(int i = 0; i < ndList.getLength(); i++)
        {
          if(adm.label == null)  
          {
            adm.label = getContent(ndList.item(i));
            continue;
          }
          adm.label += " " + getContent(ndList.item(i));
        }
        
        
        
        adm.type = "软件";
        adm.store = "豌豆荚";
        adm.vspider = "2.0";
        list.add(adm);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }
  
  public String getContent(Node nd) {
    if (nd == null) return "";
    if (nd.getTextContent() == null) return "";
    else {
      return Utils.escapeXml(nd.getTextContent().trim(), prop, true)
          .replaceAll(" ", " ").trim();
    }
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
    String url;
//    url = "http://zhushou.360.cn/list/index/cid/2?page=1";
    url = "http://www.wandoujia.com/app/org.orangenose.games";
//    url = "http://127.0.0.1:8080/app.html";
    HttpSimply hp = new HttpSimply();
    Content content = hp.getProtocolOutput(url).getContent();
//    System.out.println(new String(content.getContent()));
    AppWanDouJiaParse parse = new AppWanDouJiaParse();
    List po = parse.parse(url, content);
    for (Object o : po)
      System.out.println(AvroUtils.toAvroString(o));
    
  }
}
