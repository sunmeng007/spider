package com.iflytek.spider.model;

import org.apache.avro.reflect.Nullable;

import com.iflytek.avro.util.AvroUtils;

public class RecruitModel {
  @Nullable
  public String position;
  
  public String toString()
  {
    return AvroUtils.toAvroString(this);
  }
}
