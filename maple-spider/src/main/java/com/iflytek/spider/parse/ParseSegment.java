/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iflytek.spider.parse;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.iflytek.avro.io.UnionData;
import com.iflytek.avro.mapreduce.AvroJob;
import com.iflytek.avro.mapreduce.input.AvroPairInputFormat;
import com.iflytek.spider.metadata.Spider;
import com.iflytek.spider.protocol.Content;
import com.iflytek.spider.util.SpiderConfiguration;

/* Parse content in a segment. */
public class ParseSegment extends Configured implements Tool {
  public static final Log LOG = LogFactory.getLog(ParseSegment.class);
  
  public ParseSegment() {
    this(null);
  }
  
  public ParseSegment(Configuration conf) {
    super(conf);
  }
  
  public void configure(JobConf job) {
    setConf(job);
  }
  
  public void close() {}
  
  public static class ParseMapper extends
      Mapper<String,Content,String,UnionData> {
    @Override
    protected void map(String key, Content value, Context context)
        throws IOException, InterruptedException {
      Parse parse = new ParserFactory().getParsers(key, value);
      List pd = parse.parse(key.toString(), value);
      for (Object o : pd) {
        if (o instanceof Outlink) {
          ((Outlink) o).setExtend(value.getExtendData());
          context.write(key, new UnionData(((Outlink) o)));
        } else context.write(key, new UnionData(o));
      }
    }
    
  }
  
  public void parse(Path segment) throws IOException, InterruptedException,
      ClassNotFoundException {
    
    if (LOG.isInfoEnabled()) {
      LOG.info("Parse: starting");
      LOG.info("Parse: segment: " + segment);
    }
    
    Job job = AvroJob.getAvroJob(getConf());
    job.setJobName("parse " + segment);
    
    FileInputFormat.addInputPath(job, new Path(segment, Content.DIR_NAME));
    job.getConfiguration().set(Spider.SEGMENT_NAME_KEY, segment.getName());
    
    job.setInputFormatClass(AvroPairInputFormat.class);
    job.setMapperClass(ParseMapper.class);
    
    FileOutputFormat.setOutputPath(job, segment);
    job.setOutputFormatClass(ParseOutputFormat.class);
    job.setOutputKeyClass(String.class);
    job.setOutputValueClass(UnionData.class);
    
    job.waitForCompletion(true);
    if (LOG.isInfoEnabled()) {
      LOG.info("Parse: done");
    }
  }
  
  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(SpiderConfiguration.create(), new ParseSegment(),
        args);
    System.exit(res);
  }
  
  public int run(String[] args) throws Exception {
    
    String usage = "Usage: ParseSegment segments";
    
    if (args.length == 0) {
      System.err.println(usage);
      System.exit(-1);
    }
    FileSystem fs = FileSystem.get(getConf());
    for (FileStatus p : fs.listStatus(new Path(args[0]))) {
      if (fs.exists(new Path(p.getPath(), "crawl_parse"))) fs.delete(
          new Path(p.getPath(), "crawl_parse"), true);
      if (fs.exists(new Path(p.getPath(), "parse_data"))) fs.delete(
          new Path(p.getPath(), "parse_data"), true);
      parse(p.getPath());
    }
    return 0;
  }
}
