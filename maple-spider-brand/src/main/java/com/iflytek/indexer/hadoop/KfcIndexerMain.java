package com.iflytek.indexer.hadoop;

import java.util.HashMap;
import java.util.Map;

import com.iflytek.indexer.hadoop.client.KfcolrClient;
import com.iflytek.spider.model.ZhiDaoKfcModel;

public class KfcIndexerMain {

	public static String CORE_NAME = "core-da";

	public static void main(String[] args) throws Exception {
		Map<String, String> initMap = new HashMap<String, String>();;
		initMap.put("core.name", "core-dynamic");
		initMap.put("data.dir", "c:/index");

		ZhiDaoKfcModel c = new ZhiDaoKfcModel();
		c.sid = "asdfsdf";
		KfcolrClient client = new KfcolrClient();
		client.init(initMap);
		client.createIndex( c);
		client.close();
	}
}
