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
package com.iflytek.spider.metadata;



/**
 * A collection of Nutch internal metadata constants.
 *
 * @author Chris Mattmann
 * @author J&eacute;r&ocirc;me Charron
 */
public interface Spider {
  
  public static final String ORIGINAL_CHAR_ENCODING =
          "OriginalCharEncoding";
  
  public static final String CHAR_ENCODING_FOR_CONVERSION =
          "CharEncodingForConversion";

  public static final String SIGNATURE_KEY = "content.digest";

  public static final String SEGMENT_NAME_KEY = "segment.name";

  public static final String SCORE_KEY = "crawl.score";

  public static final String GENERATE_TIME_KEY = "_ngt_";

  public static final String PROTO_STATUS_KEY = "_pst_";

  
  public static final String FETCH_TIME_KEY = "_ftk_";
  
  public static final String FETCH_STATUS_KEY = "_fst_";

  /** Sites may request that search engines don't provide access to cached documents. */
  public static final String CACHING_FORBIDDEN_KEY = "caching.forbidden";

  /** Show both original forbidden content and summaries (default). */
  public static final String CACHING_FORBIDDEN_NONE = "none";

  /** Don't show either original forbidden content or summaries. */
  public static final String CACHING_FORBIDDEN_ALL = "all";

  /** Don't show original forbidden content, but show summaries. */
  public static final String CACHING_FORBIDDEN_CONTENT = "content";

  public static final String REPR_URL_KEY = "_repr_";

  
  public static final String META_FETCH_TIME = "fetch-time";
  
  public static final String PARSE_CLASS = "parse_class";
  public static final String PARSE_DIR_NAME = "crawl_parse";
  
  public static final String PARSE_DATA_DIR_NAME = "parse_data";
  
}
