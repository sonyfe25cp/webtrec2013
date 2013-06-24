package edu.bit.dlde.feature;

import java.io.IOException;
import java.util.HashMap;

import lucene.QueryModel;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;

/**
 *该类用来获得：
 *1. Number of slash in URL;
 *2.	Length of URL;
 *3.	Inlink number暂无;
 *4.	Outlink number暂无;
 *5.	PageRank暂无;
 *6. SiteRank暂无.
 *@author lins
 *@date 2012-7-31
 **/
public class ETC extends FeatureSet {
	public ETC(FeatureArray bridge) {
		super(bridge);
	}

	public final String[] notation = { "Number of slash in URL",
			"Length of URL", "Inlink number", "Outlink number", "PageRank",
			"SiteRank" };
	public final int[] idx = { 126, 127, 128, 129, 130, 131 };
	
	@Override
	public HashMap<Integer, Double> getFeatures(QueryModel queryModel,
			int docID) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();

		try {
			Document doc = bridge.indexReader.document(docID);
			String url = doc.get(FieldEnum.URL);
			if(url!=null){
				/** Number of slash in URL **/
				map.put(idx[0], (double) (url.replaceFirst("http://", "").split("/").length-1));
				/** Length of URL **/
				map.put(idx[1], (double) url.length());
			}else{
				/** Number of slash in URL **/
				map.put(idx[0], 0.0);
				/** Length of URL **/
				map.put(idx[1], 0.0);
			}		
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/** 返回hashmap **/
		return map;
	}

}
