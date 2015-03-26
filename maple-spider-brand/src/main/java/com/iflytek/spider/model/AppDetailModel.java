package com.iflytek.spider.model;

import org.apache.avro.reflect.Nullable;

import com.iflytek.indexer.solr.DynamicIndex;

public class AppDetailModel {
  @DynamicIndex("*_i")
  public int url;
  @DynamicIndex("*_t")
  @Nullable
  public String appName;
  @Nullable
  @DynamicIndex("*_t")
  public String mark;
  @Nullable
  @DynamicIndex("*_t")
  public String commentNum;
  @DynamicIndex("*_i")
  public int downloads;
  @Nullable
  @DynamicIndex("*_t")
  public String size;
  @Nullable
  @DynamicIndex("*_t")
  public String desc;
  @Nullable
  @DynamicIndex("*_t")
  public String author;
  @Nullable
  @DynamicIndex("*_t")
  public String updateTime;
  @Nullable
  @DynamicIndex("*_t")
  public String version;
  @Nullable
  @DynamicIndex("*_t")
  public String os;
  @Nullable
  @DynamicIndex("*_t")
  public String language;
  @Nullable
  @DynamicIndex("*_t")
  public String safe;
  @Nullable
  @DynamicIndex("*_t")
  public String ad;
  @Nullable
  @DynamicIndex("*_t")
  public String charge;
  @Nullable
  @DynamicIndex("*_t")
  public String authorityLevel;
  @Nullable
  @DynamicIndex("*_t")
  public String authorities;
  @Nullable
  @DynamicIndex("*_t")
  public String greenAction;
  @Nullable
  @DynamicIndex("*_t")
  public String label;
  @Nullable
  @DynamicIndex("*_t")
  public String type;
  @Nullable
  @DynamicIndex("*_t")
  public String store;
  @Nullable
  @DynamicIndex("*_t")
  public String vspider;
}
