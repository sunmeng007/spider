package com.iflytek.spider.avro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.hadoop.thirdparty.guava.common.collect.Lists;
import org.codehaus.jettison.json.JSONObject;

import com.iflytek.indexer.hadoop.client.KfcolrClient;
import com.iflytek.spider.model.ZhiDaoKfcModel;

public class KfcIndex {
  
  public static void main(String args[])
  {
    AvroReadTool avro = new AvroReadTool();
    List<String> a = Lists.newArrayList();
    try {
//      String filePath = "D:\\pdf\\segments";
      String filePath = "D:\\pdf\\segments";
      List<String> ret = Lists.newArrayList();
      GetFileTool.ReadAllFile(filePath,ret);
      
      Map<String, String> initMap = new HashMap<String, String>();;
      initMap.put("core.name", "core-dynamic");
      initMap.put("data.dir", "c:/index");
      
      KfcolrClient client = new KfcolrClient();
      client.init(initMap);
      ZhiDaoKfcModel c = new ZhiDaoKfcModel();
      String reg = "http://zhidao.baidu.com/question/\\d+.html";
      for(String path : ret)
      {
        a.clear();
        a.add(path);
        a.add("-num");
        a.add("100000");
        List<JSONObject> json = avro.run(System.in, System.out, System.err, a);
        
        for(JSONObject j : json)
        {
          if(j.getString("key").matches(reg))
          {
            String tmpSid = j.get("key").toString();
            String tmpAskTitle = new JSONObject(j.get("value").toString()).get("askTitle").toString();
            String tmpBestAnswer = new JSONObject(j.get("value").toString()).get("bestAnswer").toString();
            List<String> tmpOtherAnswer = JSONArray.toList(JSONArray.fromObject(new JSONObject(j.get("value").toString()).get("otherAnswer").toString()));
            
            if(null == tmpBestAnswer || tmpBestAnswer.isEmpty() || "".equals(tmpBestAnswer))
            {
              for(String oth : tmpOtherAnswer)
              {
                tmpBestAnswer = oth;
                break;
              }
            }
            
            if(null == tmpBestAnswer || tmpBestAnswer.isEmpty() || "".equals(tmpBestAnswer))
            {
              continue;
            }
            else
            {
              c.sid = tmpSid;
              c.askTitle = tmpAskTitle;
              c.bestAnswer = tmpBestAnswer;
              c.otherAnswer = null;
              client.createIndex(c);
            }
          }
        }
      }
      
      client.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
