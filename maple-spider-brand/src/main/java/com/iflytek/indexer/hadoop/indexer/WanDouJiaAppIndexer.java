package com.iflytek.indexer.hadoop.indexer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.solr.common.SolrInputDocument;

import com.iflytek.avro.io.UnionData;
import com.iflytek.avro.mapreduce.AvroJob;
import com.iflytek.index.util.DynamicIndexUtil;
import com.iflytek.indexer.TopIndexer;
import com.iflytek.spider.model.AppWanDouJiaModel;

public class WanDouJiaAppIndexer extends TopIndexer<String,AppWanDouJiaModel,String,AppWanDouJiaModel>{

  @Override
  public List<SolrInputDocument> createDocment(AppWanDouJiaModel value) {
    List<SolrInputDocument> ret = new ArrayList<SolrInputDocument>(1);
    try {
      SolrInputDocument doc =DynamicIndexUtil.genDynamicDoc(value);
//      doc.addField("sid", value.appName);
      doc.addField("sid", value.url);
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
  public AppWanDouJiaModel getData(String key, AppWanDouJiaModel value, Context arg2) {
    
    return value;
  }

  @Override
  public String getIndexKey(String key, AppWanDouJiaModel value) {
    // TODO Auto-generated method stub
    return key;
  }

  @Override
  public void init(AvroJob job) {
    job.setOutputKeyClass(String.class);
    job.setOutputValueClass(AppWanDouJiaModel.class);
    UnionData.setUnionClass(job.getConfiguration(), AppWanDouJiaModel.class);
  }
}
