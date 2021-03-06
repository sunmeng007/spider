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

// JDK imports

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.StringUtils;

import com.iflytek.spider.metadata.Spider;
import com.iflytek.spider.protocol.Content;



public final class ParserFactory
{

	public static final Log LOG = LogFactory.getLog(ParserFactory.class);

	public Parse getParsers(String url, Content content)
	{
		try
		{
			String parse_class = content.getExtend(Spider.PARSE_CLASS).toString();
			Class<?> tclass = Class.forName(parse_class);
			return (Parse) tclass.newInstance();
		} catch (ClassNotFoundException e)
		{
			LOG.info("IParserFactory.getParsers"
					+ StringUtils.stringifyException(e));
		} catch (IllegalAccessException e)
		{
			LOG.info("IParserFactory.getParsers"
					+ StringUtils.stringifyException(e));
		} catch (InstantiationException e)
		{
			LOG.info("IParserFactory.getParsers"
					+ StringUtils.stringifyException(e));
		}
		return null;
		
	}
}
