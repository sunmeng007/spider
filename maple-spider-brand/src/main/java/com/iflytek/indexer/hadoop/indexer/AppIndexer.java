package com.iflytek.indexer.hadoop.indexer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.solr.common.SolrInputDocument;

import com.iflytek.avro.io.UnionData;
import com.iflytek.avro.mapreduce.AvroJob;
import com.iflytek.index.util.DynamicIndexUtil;
import com.iflytek.indexer.TopIndexer;
import com.iflytek.spider.model.AppDetailModel;

public class AppIndexer extends TopIndexer<String,AppDetailModel,String,AppDetailModel>{

  @Override
  public List<SolrInputDocument> createDocment(AppDetailModel value) {
    List<SolrInputDocument> ret = new ArrayList<SolrInputDocument>(1);
    try {
      SolrInputDocument doc =DynamicIndexUtil.genDynamicDoc(value);
      doc.addField("sid", value.url);
//      doc.addField("sid", value.appName);
      ret.add(doc);
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ret;
  }

  @Override
  public AppDetailModel getData(String key, AppDetailModel value, Context arg2) {
    
    return value;
  }

  @Override
  public String getIndexKey(String key, AppDetailModel value) {
    // TODO Auto-generated method stub
    return key;
  }

  @Override
  public void init(AvroJob job) {
    job.setOutputKeyClass(String.class);
    job.setOutputValueClass(AppDetailModel.class);
    UnionData.setUnionClass(job.getConfiguration(), AppDetailModel.class);
  }
}
