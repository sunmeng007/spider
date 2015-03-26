package com.iflytek.spider.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class ValidateCode {
  public static void main(String[] args) throws Exception {
    String imageUrl = "http://captcha.qq.com/getimage";
    String destinationFile = "image";
    
    for(int i = 0; i < 10; i++)
    {
      saveImage(imageUrl, destinationFile+i+".bmp");
    }
  }

  public static void saveImage(String imageUrl, String destinationFile) throws IOException {
    URL url = new URL(imageUrl);
    InputStream is = url.openStream();
    OutputStream os = new FileOutputStream(destinationFile);

    byte[] b = new byte[2048];
    int length;

    while ((length = is.read(b)) != -1) {
      os.write(b, 0, length);
    }

    is.close();
    os.close();
  }
}
