package com.iflytek.spider.solr;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.google.common.collect.Lists;
import com.iflytek.spider.kfc.KfcKeyWordQuery;

public class KfcSolrRequestHandler extends RequestHandlerBase {
  
  @Override
  public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp)
      throws Exception {
    // extract params from request
    SolrParams params = req.getParams();
    
    String start = "";
    String rows = "";
    String q = "";
    String wt = "json";
    try {
      start = params.get(CommonParams.START);
      rows = params.get(CommonParams.ROWS);
      q = params.get(CommonParams.Q);
    } catch (Exception e) 
    {
      e.printStackTrace();
    }
    
    KfcKeyWordQuery kfc = new KfcKeyWordQuery("filter.spider", "weight.spider","solr.server");
    
    List<String> ik = kfc.getIKAnalyzerResultsOfQuery(q);
    //检索的时候，不能remove,新建对象，最后一下remove
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
    
    ik.removeAll(garbage);
    
    for(String seg : ik)
    {
      par += seg;
      par += "*";
    }
    if("".equals(par))
    {
      par = q;
    }
    else
    {
      par = par.substring(0, par.length() - 1);
    }
    JSONObject response = kfc.sendGet(par, start, rows, wt);
    rsp.add("responseHeader", response.get("responseHeader"));
    rsp.add("response", response.get("response"));
  }
  
  @Override
  public String getDescription() {
    return null;
  }
  
  @Override
  public String getSource() {
    return null;
  }
}
