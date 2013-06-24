package edu.bit.dlde.feature;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import lucene.QueryModel;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;


/**
 * 用于特征抽取的类，通过getFeatures()获得特征的hashmap， 通过getInstacne()获得该类的实例，
 * 通过processDocument()处理文本
 * 
 * @author lins
 * @date 2012-7-30
 **/
public class FeatureArray{
	private HashMap<Integer, Double> featureMap;

	private FeatureArray() {
	}

	IndexReader indexReader;
	LinkedList<FeatureSet> featureSets = new LinkedList<FeatureSet>();
	/**
	 * 获得一个FeatureArray实例
	 */
	public static FeatureArray getInstacne(IndexReader indexReader) {
		if (indexReader == null)
			return null;
		FeatureArray features = new FeatureArray();
		
		features.indexReader = indexReader;
		features.featureSets.add(new FirstFeatureSet(features));
		features.featureSets.add(new SecondFeatureSet(features));	
		features.featureSets.add(new ThirdFeatureSet(features));
		features.featureSets.add(new BM25(features));
		features.featureSets.add(new VSM(features));
		features.featureSets.add(new ETC(features));
		features.featureSets.add(new LMIR(features));

		return features;
	}

	/**
	 * 处理文本，提取特征
	 * @param doc lucene裏面的document
	 * @param queryModel 预先处理的query，比如加入一些查询扩展，分词等等处理
	 */
	public HashMap<Integer, Double> processDocument(QueryModel queryModel, int docID) {
		featureMap = new HashMap<Integer, Double>();
		for (FeatureSet p : featureSets) {
			featureMap.putAll(p.getFeatures(queryModel, docID));
		}
		return featureMap;
	}

	/**
	 * 单独读取某一特征对应的值
	 * 
	 * @param 键值
	 */
	public Double get(String key) {
		if (featureMap != null)
			return featureMap.get(key);
		return null;
	}

	/**
	 * 手动设置featureMap
	 */
	public void set(Integer key, Double value) {
		if (featureMap != null)
			featureMap.put(key, value);
	}

	/**
	 * @return 直接返回储存特征的Hashmap
	 */
	public HashMap<Integer, Double> getFeatures() {
		return featureMap;
	}
	
	HashMap<String, TermFreqVector> cache = new HashMap<String, TermFreqVector>();
	/**
	 * 获得缓存TermFreqVector
	 * @throws IOException 
	 */
	public TermFreqVector getCachedTermFreqVector(int docID, String field) throws IOException{
		if (!cache.containsKey(docID + field)) {
			if (cache.size() >= FieldEnum.class.getFields().length) {
				cache.clear();
			}
			TermFreqVector tfv = indexReader
					.getTermFreqVector(docID, field);
			cache.put(docID + field, tfv);
		}
		return cache.get(docID + field);
	}
}
