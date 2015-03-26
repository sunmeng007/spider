package com.iflytek.indexer.hadoop.client;

import java.util.Collections;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;

import com.iflytek.index.client.EmbeddedSolrClient;
import com.iflytek.index.util.DynamicIndexUtil;
import com.iflytek.spider.model.ZhiDaoKfcModel;

public class KfcolrClient extends EmbeddedSolrClient<ZhiDaoKfcModel>{

	@Override
	public List<SolrInputDocument> createDocment(ZhiDaoKfcModel stu)
			throws Exception {
		SolrInputDocument solrdoc = DynamicIndexUtil.genDynamicDoc(stu);
		return Collections.singletonList(solrdoc);
	}
}
