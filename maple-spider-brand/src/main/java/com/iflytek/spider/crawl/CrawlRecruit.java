package com.iflytek.spider.crawl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.StringUtils;

import com.iflytek.avro.io.MapAvroFile;
import com.iflytek.spider.fetcher.FetcherSmart;
import com.iflytek.spider.parse.ParseSegment;
import com.iflytek.spider.util.SpiderConfiguration;
import com.iflytek.spider.util.SpiderJob;

public class CrawlRecruit {
	public static final Log			LOG		= LogFactory.getLog(CrawlRecruit.class);

	public static Configuration	conf	= null;

	public CrawlRecruit() {
		conf = SpiderConfiguration.create();
	}

	/* Perform complete crawling and indexing given a set of root urls. */
	public static void main(String args[]) throws Exception {
		// Crawl cr = new Crawl();
		conf = SpiderConfiguration.create();
		JobConf job = new SpiderJob(conf);

		Path dir = new Path("recruit");

		Path crawlDb = null;
		Path seg = null;
		int threads = job.getInt("fetcher.threads.fetch", 10);

		FileSystem fs = FileSystem.get(job);
		Injector injector = new Injector(conf);
		GeneratorSmart generator = new GeneratorSmart(conf);
		FetcherSmart fetcher = new FetcherSmart(conf);
		CrawlDb crawlDbTool = new CrawlDb(conf);
		ParseSegment parse = new ParseSegment(conf);
		crawlDb = new Path(dir + "/crawldb");
		seg = new Path(dir + "/segments");
		injector.inject(crawlDb, new Path(dir, "recruit.seed"));
		try {
			while (true) {

				Path[] segments = null;
				segments = generator.generate(crawlDb, seg, 1,
						System.currentTimeMillis(), false);
				if (segments == null) {
					LOG.info("Stopping dute no more URLs to fetch.");
					break;
					// return;
				}
				for (Path segment : segments) {
					fetcher.fetch(segment, threads); // fetch it
					parse.parse(segment);
				}
				crawlDbTool.update(crawlDb, segments); // update
			}
		} catch (Exception e) {
			if (LOG.isFatalEnabled())

				LOG.fatal("in CrawlInfo main() Exception "
						+ StringUtils.stringifyException(e) + "\n");
			return;
		}
		LOG.info("Crawl is done!\n");
	}

}
