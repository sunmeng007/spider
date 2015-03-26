package com.iflytek.spider.fetcher;

import java.io.IOException;

import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.InvalidJobConfException;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.iflytek.avro.io.UnionData;
import com.iflytek.avro.mapreduce.output.AvroMapOutputFormat;
import com.iflytek.avro.mapreduce.output.MultipleOutputs;
import com.iflytek.spider.crawl.CrawlDatum;
import com.iflytek.spider.protocol.Content;

public class FetcherOutputFormat extends FileOutputFormat<String,UnionData> {
  
  @Override
  public RecordWriter<String,UnionData> getRecordWriter(TaskAttemptContext job)
      throws IOException, InterruptedException {
    
    final MultipleOutputs mos = new MultipleOutputs(job);
    
    return new RecordWriter<String,UnionData>() {
      
      @Override
      public void write(String key, UnionData value) throws IOException,
          InterruptedException {
        
        if (value.datum instanceof CrawlDatum) {
          mos.write(AvroMapOutputFormat.class, CrawlDatum.FETCH_DIR_NAME + "/", key, value.datum);
        } else if (value.datum instanceof Content) {
          mos.write(AvroMapOutputFormat.class, FetcherSmart.CONTENT_REDIR + "/",key, value.datum);
        }
      }
      
      @Override
      public void close(TaskAttemptContext context) throws IOException,
          InterruptedException {
        mos.close();
        
      }
    };
  }
  
  public void checkOutputSpecs(JobContext job)
      throws FileAlreadyExistsException, IOException {
    // Ensure that the output directory is set and not already there
    Path outDir = getOutputPath(job);
    if (outDir == null) {
      throw new InvalidJobConfException("Output directory not set.");
    }
    
    // get delegation token for outDir's file system
    // TokenCache.obtainTokensForNamenodes(new Path[] { outDir },
    // job.getConfiguration());
    
    // if (outDir.getFileSystem(job.getConfiguration()).exists(outDir)) {
    // throw new FileAlreadyExistsException("Output directory " + outDir
    // + " already exists");
    // }
  }
}
