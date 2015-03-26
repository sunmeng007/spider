package com.iflytek.spider.kfc;

import java.util.List;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.iflytek.spider.solr.KfcSolrRequestHandler;

public class KfcKeyWordQueryTest {
  @Test
  public void mainTest()
  {
    KfcKeyWordQuery kfc = new KfcKeyWordQuery("filter.spider", "weight.spider","solr.server");
    List<String> ik = kfc.getIKAnalyzerResultsOfQuery("为啥肯德基这么贵");
    List<String> garbage = Lists.newArrayList();
    String par = "";
    
    for(String seg : ik)
    {
      for(String key : kfc.filterWords.keySet())
      {
        if(key.contains(seg))
        {
          garbage.add(seg);
        }
      }
    }
    System.out.println(kfc.filterWords.size());
    ik.removeAll(garbage);
    
    for(String seg : ik)
    {
      par += seg;
      par += "*";
    }
    par = par.substring(0, par.length() - 1);
    JSONObject response = kfc.sendGet(par, "0", "10", "json");
    System.out.println(response);
  }
}
