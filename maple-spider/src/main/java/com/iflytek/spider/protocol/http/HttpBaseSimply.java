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
package com.iflytek.spider.protocol.http;

// JDK imports
import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import com.iflytek.spider.crawl.CrawlDatum;
import com.iflytek.spider.protocol.Content;
import com.iflytek.spider.protocol.Protocol;
import com.iflytek.spider.protocol.ProtocolException;
import com.iflytek.spider.protocol.ProtocolOutput;
import com.iflytek.spider.protocol.ProtocolStatus;
import com.iflytek.spider.protocol.Response;
import com.iflytek.spider.util.DeflateUtils;
import com.iflytek.spider.util.GZIPUtils;
import com.iflytek.spider.util.LogUtil;

/**
 * @author J&eacute;r&ocirc;me Charron
 */
public abstract class HttpBaseSimply implements Protocol {

	public static final int BUFFER_SIZE = 8 * 1024;

	private static final byte[] EMPTY_CONTENT = new byte[0];

	/** The proxy hostname. */
	protected String proxyHost = null;

	/** The proxy port. */
	protected int proxyPort = 8080;

	/** Indicates if a proxy is used */
	protected boolean useProxy = false;

	/** The network timeout in millisecond */
	protected int timeout = 10000;

	/** The length limit for downloaded content, in bytes. */
	protected int maxContent = 64 * 1024;

	/** The Nutch 'User-Agent' request header */
	protected String userAgent = getAgentString("NutchCVS", null, "Nutch",
			"http://lucene.apache.org/nutch/bot.html",
			"nutch-agent@lucene.apache.org");

	/** The "Accept-Language" request header value. */
	protected String acceptLanguage = "en-us,en-gb,en;q=0.7,*;q=0.3";

	/** The default logger */
	private final static Log LOGGER = LogFactory.getLog(HttpBaseSimply.class);

	/** The specified logger */
	private Log logger = LOGGER;

	/** The nutch configuration */
	private Configuration conf = null;

	/** Do we use HTTP/1.1? */
	protected boolean useHttp11 = false;

	/** Creates a new instance of HttpBase */
	public HttpBaseSimply() {
		this(null);
	}

	/** Creates a new instance of HttpBase */
	public HttpBaseSimply(Log logger) {
		if (logger != null) {
			this.logger = logger;
		}
	}

	// Inherited Javadoc
	public void setConf(Configuration conf) {
		this.conf = conf;
		this.proxyHost = conf.get("http.proxy.host");
		this.proxyPort = conf.getInt("http.proxy.port", 8080);
		this.useProxy = (proxyHost != null && proxyHost.length() > 0);
		this.timeout = conf.getInt("http.timeout", 10000);
		this.maxContent = conf.getInt("http.content.limit", 64 * 1024);
		this.userAgent = getAgentString(conf.get("http.agent.name"),
				conf.get("http.agent.version"),
				conf.get("http.agent.description"), conf.get("http.agent.url"),
				conf.get("http.agent.email"));
		this.acceptLanguage = conf.get("http.accept.language", acceptLanguage);
		this.useHttp11 = conf.getBoolean("http.useHttp11", false);
		//logConf();
	}

	// Inherited Javadoc
	public Configuration getConf() {
		return this.conf;
	}

	public ProtocolOutput getProtocolOutput(String url, CrawlDatum datum) {

		try {
			URL u = new URL(url);

			Response response;
			try {
				response = getResponse(u, datum, true); // make a request
			} finally {
			}

			int code = response.getCode();
			byte[] content = response.getContent();
			Content c = new Content(u.toString(),(content == null ? EMPTY_CONTENT : content),response.getHeaders());

			if (code == 200) { // got a good response
				return new ProtocolOutput(c); // return it

			} else if (code == 410) { // page is gone
				return new ProtocolOutput(c, new ProtocolStatus(
						ProtocolStatus.GONE, "Http: " + code + " url=" + url));

			} else if (code >= 300 && code < 400) { // handle redirect
				String location = response.getHeader("Location");
				// some broken servers, such as MS IIS, use lowercase header
				// name...
				if (location == null)
					location = response.getHeader("location");
				if (location == null)
					location = "";
				u = new URL(u, location);
				int protocolStatusCode;
				switch (code) {
				case 300: // multiple choices, preferred value in Location
					protocolStatusCode = ProtocolStatus.MOVED;
					break;
				case 301: // moved permanently
				case 305: // use proxy (Location is URL of proxy)
					protocolStatusCode = ProtocolStatus.MOVED;
					break;
				case 302: // found (temporarily moved)
				case 303: // see other (redirect after POST)
				case 307: // temporary redirect
					protocolStatusCode = ProtocolStatus.TEMP_MOVED;
					break;
				case 304: // not modified
					protocolStatusCode = ProtocolStatus.NOTMODIFIED;
					break;
				default:
					protocolStatusCode = ProtocolStatus.MOVED;
				}
				// handle this in the higher layer.
				return new ProtocolOutput(c, new ProtocolStatus(
						protocolStatusCode, u));
			} else if (code == 400) { // bad request, mark as GONE
				if (logger.isTraceEnabled()) {
					logger.trace("400 Bad request: " + u);
				}
				return new ProtocolOutput(c, new ProtocolStatus(
						ProtocolStatus.GONE, u));
			} else if (code == 401) { // requires authorization, but no valid
										// auth provided.
				if (logger.isTraceEnabled()) {
					logger.trace("401 Authentication Required");
				}
				return new ProtocolOutput(c, new ProtocolStatus(
						ProtocolStatus.ACCESS_DENIED,
						"Authentication required: " + url));
			} else if (code == 404) {
				return new ProtocolOutput(c, new ProtocolStatus(
						ProtocolStatus.NOTFOUND, u));
			} else if (code == 410) { // permanently GONE
				return new ProtocolOutput(c, new ProtocolStatus(
						ProtocolStatus.GONE, u));
			} else {
				return new ProtocolOutput(c, new ProtocolStatus(
						ProtocolStatus.EXCEPTION, "Http code=" + code
								+ ", url=" + u));
			}
		} catch (Throwable e) {
			e.printStackTrace(LogUtil.getErrorStream(logger));
			return new ProtocolOutput(null, new ProtocolStatus(e));
		}
	}

	/*
	 * -------------------------- * </implementation:Protocol> *
	 * --------------------------
	 */

	public String getProxyHost() {
		return proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public boolean useProxy() {
		return useProxy;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getMaxContent() {
		return maxContent;
	}

	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * Value of "Accept-Language" request header sent by Nutch.
	 * 
	 * @return The value of the header "Accept-Language" header.
	 */
	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public boolean getUseHttp11() {
		return useHttp11;
	}

	private static String getAgentString(String agentName, String agentVersion,
			String agentDesc, String agentURL, String agentEmail) {

		if ((agentName == null) || (agentName.trim().length() == 0)) {
			// TODO : NUTCH-258
			if (LOGGER.isFatalEnabled()) {
				LOGGER.fatal("No User-Agent string set (http.agent.name)!");
			}
		}

		StringBuffer buf = new StringBuffer();

		buf.append(agentName);
		if (agentVersion != null) {
			buf.append("/");
			buf.append(agentVersion);
		}
		if (((agentDesc != null) && (agentDesc.length() != 0))
				|| ((agentEmail != null) && (agentEmail.length() != 0))
				|| ((agentURL != null) && (agentURL.length() != 0))) {
			buf.append(" (");

			if ((agentDesc != null) && (agentDesc.length() != 0)) {
				buf.append(agentDesc);
				if ((agentURL != null) || (agentEmail != null))
					buf.append("; ");
			}

			if ((agentURL != null) && (agentURL.length() != 0)) {
				buf.append(agentURL);
				if (agentEmail != null)
					buf.append("; ");
			}

			if ((agentEmail != null) && (agentEmail.length() != 0))
				buf.append(agentEmail);

			buf.append(")");
		}
		return buf.toString();
	}

	protected void logConf() {
		if (logger.isInfoEnabled()) {
			logger.info("http.proxy.host = " + proxyHost);
			logger.info("http.proxy.port = " + proxyPort);
			logger.info("http.timeout = " + timeout);
			logger.info("http.content.limit = " + maxContent);
			logger.info("http.agent = " + userAgent);
			logger.info("http.accept.language = " + acceptLanguage);
		}
	}

	public byte[] processGzipEncoded(byte[] compressed, URL url)
			throws IOException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("uncompressing....");
		}

		byte[] content;
		if (getMaxContent() >= 0) {
			content = GZIPUtils.unzipBestEffort(compressed, getMaxContent());
		} else {
			content = GZIPUtils.unzipBestEffort(compressed);
		}

		if (content == null)
			throw new IOException("unzipBestEffort returned null");

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("fetched " + compressed.length
					+ " bytes of compressed content (expanded to "
					+ content.length + " bytes) from " + url);
		}
		return content;
	}

	public byte[] processDeflateEncoded(byte[] compressed, URL url)
			throws IOException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("inflating....");
		}

		byte[] content = DeflateUtils.inflateBestEffort(compressed,
				getMaxContent());

		if (content == null)
			throw new IOException("inflateBestEffort returned null");

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("fetched " + compressed.length
					+ " bytes of compressed content (expanded to "
					+ content.length + " bytes) from " + url);
		}
		return content;
	}

	protected static void main(HttpBaseSimply http, String[] args)
			throws Exception {
		boolean verbose = false;
		String url = null;

		String usage = "Usage: Http [-verbose] [-timeout N] url";

		if (args.length == 0) {
			System.err.println(usage);
			System.exit(-1);
		}

		for (int i = 0; i < args.length; i++) { // parse command line
			if (args[i].equals("-timeout")) { // found -timeout option
				http.timeout = Integer.parseInt(args[++i]) * 1000;
			} else if (args[i].equals("-verbose")) { // found -verbose option
				verbose = true;
			} else if (i != args.length - 1) {
				System.err.println(usage);
				System.exit(-1);
			} else
				// root is required parameter
				url = args[i];
		}

		// if (verbose) {
		// LOGGER.setLevel(Level.FINE);
		// }

		ProtocolOutput out = http.getProtocolOutput(url,
				new CrawlDatum());
		Content content = out.getContent();

		System.out.println("Status: " + out.getStatus());
		if (content != null) {
			System.out.println("Content Length: "
					+ content.getMetadata().get(Response.CONTENT_LENGTH));
			System.out.println("Content:");
			String text = new String(content.getContent());
			System.out.println(text);
		}

	}

	public ProtocolOutput getProtocolOutput(String url) {
		return getProtocolOutput(url, new CrawlDatum());
	}

	protected abstract Response getResponse(URL url, CrawlDatum datum,
			boolean followRedirects) throws ProtocolException, IOException;

}
