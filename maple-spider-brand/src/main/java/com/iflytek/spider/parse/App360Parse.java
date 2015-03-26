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
import com.iflytek.spider.model.AppDetailModel;
import com.iflytek.spider.protocol.Content;
import com.iflytek.spider.protocol.httpclient.HttpSimply;

@SuppressWarnings("rawtypes")
public class App360Parse implements Parse {
//  public static final String listPage = "http://zhushou.360.cn/list/index/cid/\\d+\\?page=\\d+";
//  public static final String listPage = "http://zhushou.360.cn/search/index/kw/.*?page=\\d+";
  public static final String listPage = "http://127.0.0.1:8080/360app\\d+.html";
  public static final String detailPage = "http://zhushou.360.cn/detail/index/soft_id/\\d+.*";
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
        for (Outlink ol : ols){
          ol.url = ol.url.split("\\?")[0];
          list.add(ol);
        }
      } else if (url.matches(detailPage)) {
        AppDetailModel adm = new AppDetailModel();
        adm.url = Integer.parseInt(url.substring(43));
        TagNode node = cleaner.clean(new ByteArrayInputStream(content
            .getContent()));
        DomSerializer ds = new DomSerializer(prop);
        Document doc = ds.createDOM(node);
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node nd = (Node) xpath.evaluate("//span/@title", doc,
            XPathConstants.NODE);
        adm.appName = getContent(nd);
        if ("" == adm.appName)
          return list;
        nd = (Node) xpath.evaluate("//span[contains(@class,'js-comments')]/text()", doc, XPathConstants.NODE);
        //评价数是Ajax请求，默认值是0，暂时使用默认值
        adm.commentNum = getContent(nd);
        NodeList ndList = (NodeList) xpath.evaluate("//div[@class='pf']/span/text()", doc,
            XPathConstants.NODESET);
        for(int i = 0; i < ndList.getLength(); i++)
        {
          if(i == 0) adm.mark = getContent(ndList.item(i));
          if(i == 1)
          {
            String downloads = getContent(ndList.item(i)).split("：")[1];
            downloads = downloads.contains("万") ? downloads.split("万")[0] + "0000" : downloads.split("次")[0];
            adm.downloads = Integer.parseInt(downloads);
          }
          if(i == 2) adm.size = getContent(ndList.item(i));
        }
        nd = (Node) xpath.evaluate("//div[@id='html-brief']/*", doc,
            XPathConstants.NODE);
        adm.desc = getContent(nd);
        if("".equals(adm.desc))
        {
          nd = (Node) xpath.evaluate("//div[@id='sdesc']/*", doc,
              XPathConstants.NODE);
          adm.desc = getContent(nd);
        }
        nd = (Node) xpath.evaluate("//div[@class='base-info']/table/tbody/tr/*", doc,
            XPathConstants.NODE);
        ndList = (NodeList) xpath.evaluate("//div[@class='base-info']/table/tbody/tr/*", doc,
            XPathConstants.NODESET);
       
//        adm.author = match("<h2.*?</h2>",getContent(nd));
        for (int i = 0; i < ndList.getLength(); i++)
        {
          if(getContent(ndList.item(i)).split("：").length > 1)
          {
            if(i == 0)  adm.author = getContent(ndList.item(i)).split("：")[1];
            if(i == 1)  adm.updateTime = getContent(ndList.item(i)).split("：")[1];
            if(i == 2)  adm.version = getContent(ndList.item(i)).split("：")[1];
            if(i == 3)  adm.os = getContent(ndList.item(i)).split("：")[1];
            if(i == 4)  adm.language = getContent(ndList.item(i)).split("：")[1];
          }
//          System.out.print(getContent(ndList.item(i)).split("：")[1] + " ");
        }
        ndList = (NodeList) xpath.evaluate("//div[@class='title']/ul/*", doc,
            XPathConstants.NODESET);
        for(int i = 0; i < ndList.getLength(); i ++)
        {
          if(i == 0)  adm.safe = getContent(ndList.item(i));
          if(i == 1)  adm.ad = getContent(ndList.item(i));
          if(i == 2)  adm.charge = getContent(ndList.item(i));
          if(i == 3)  adm.authorityLevel = getContent(ndList.item(i)).split("：").length > 1 ? getContent(ndList.item(i)).split("：")[1] : "0";
          if(i == 4)  adm.greenAction = getContent(ndList.item(i));
        }
        ndList = (NodeList) xpath.evaluate("//div[@id='authority-panel']/*", doc,
            XPathConstants.NODESET);
        for(int i = 0; i < ndList.getLength(); i ++)
        {
          if(i == 2)  adm.authorities = getContent(ndList.item(2));
        }
        ndList = (NodeList) xpath.evaluate("//div[@class='app-tags']/a/text()", doc,
            XPathConstants.NODESET);
        for(int i = 0; i < ndList.getLength(); i ++)
        {
          if(adm.label == null)  
          {
            adm.label = getContent(ndList.item(i));
            continue;
          }
          adm.label += " " + getContent(ndList.item(i));
        }
        nd = (Node) xpath.evaluate("//li[contains(@class,'cur')]", doc,
            XPathConstants.NODE);
        adm.type = getContent(nd).substring(1);
        adm.store = "360手机助手";
        adm.vspider = "4.0";
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
//    url = "http://zhushou.360.cn/detail/index/soft_id/10283";
//    url = "http://zhushou.360.cn/detail/index/soft_id/30644";
//    url = "http://zhushou.360.cn/search/index/kw/%E6%8B%8D%E7%85%A7/cid/1";
//    url = "http://zhushou.360.cn/search/index/kw/%E6%8B%8D%E7%85%A7?page=1";
//    url = "http://zhushou.360.cn/detail/index/soft_id/2747?recrefer=SE_D_拍照";
    url = "http://zhushou.360.cn/detail/index/soft_id/2747";
    HttpSimply hp = new HttpSimply();
    Content content = hp.getProtocolOutput(url).getContent();
//    System.out.println(new String(content.getContent()));
    App360Parse parse = new App360Parse();
    List po = parse.parse(url, content);
    for (Object o : po)
      System.out.println(AvroUtils.toAvroString(o));
    
  }
}
