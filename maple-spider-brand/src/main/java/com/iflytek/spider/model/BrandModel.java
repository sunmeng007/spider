package com.iflytek.spider.model;

import org.apache.avro.reflect.Nullable;

import com.iflytek.avro.util.AvroUtils;

public class BrandModel {
  @Nullable
  public String title;
  
  public String toString()
  {
    return AvroUtils.toAvroString(this);
  }
}
