package com.iflytek.spider.avro;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.thirdparty.guava.common.collect.Lists;

public class GetFileTool {
  
  static class MyFilter implements FilenameFilter {
    private String type;
    
    public MyFilter(String type) {
      this.type = type;
    }
    
    public boolean accept(File dir, String name) {
      return name.endsWith(type);
    }
  }
  
  public static List<String> getFilePath(String path) {
    if (null == path) {
      return null;
    }
    File f = new File(path);
    MyFilter filter = new MyFilter(".avro");
    String[] files = f.list(filter);
    return Arrays.asList(files);
  }
  
  // 读取一个文件夹下所有文件及子文件夹下的所有文件
  public static int ReadAllFile(String filePath,List<String> list) {
    if(null == filePath)
    {
      return 0;
    }
    File f = null;
    f = new File(filePath);
    MyFilter filter = new MyFilter(".avro");
    File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。

    for (File file : files) {
      if (file.isDirectory()) {
        // 如何当前路劲是文件夹，则循环读取这个文件夹下的所有文件
        ReadAllFile(file.getAbsolutePath(),list);
      }
    }
    for (File file : files) {
      if(file.isFile())
      {
        if(file.getName().equals("data.avro"))
        {
          String path = file.getAbsolutePath();
          if(path.contains("ZhiDaoKfcModel"))
          {
            list.add(file.getAbsolutePath());
//            System.out.println(file.getAbsolutePath());
          }
        }
      }
    }
    return 1;
  }
  
  // 读取一个文件夹下的所有文件夹和文件
  public static void ReadFile(String filePath) {
    File f = null;
    f = new File(filePath);
    File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。
    List<File> list = new ArrayList<File>();
    for (File file : files) {
      list.add(file);
    }
    for (File file : files) {
      System.out.println(file.getAbsolutePath());
    }
  }
}
