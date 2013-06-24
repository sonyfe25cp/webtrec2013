package edu.bit.dlde.feature;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import lucene.QueryModel;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;

/**
 * 这是一个用来获得多个特征的类
 * 1.该类用来获得IDF. 问题是每一个term都会有一个idf，所有就取了个平均。
 * 2.sum of tf*idf
 * 3.min of tf*idf
 * 4.max of tf*idf
 * 5.mean of tf*idf
 *@author lins
 *@date 2012-7-31
 **/
public class ThirdFeatureSet extends FeatureSet {
	public ThirdFeatureSet(FeatureArray bridge) {
		super(bridge);
	}

	public final String[] notation = { "IDF", "sum of tf*idf", "min of tf*idf",
			"max of tf*idf", "mean of tf*idf" };
	public final int[] idx = { 16, 71, 76, 81, 86 };
	
	@Override
	public HashMap<Integer, Double> getFeatures(QueryModel queryModel,
			int docID) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		
		/** 通过反射机制读取所有需要进行该项特征提取的field **/
		String[] terms = queryModel.getTerms();
		double idf4Whole = 0, sumtfidf4Whole = 0, mintfidf4Whole=0, maxtfidf4Whole=0;
		long docSum = bridge.indexReader.numDocs();
		for (int i = 0; i < FieldEnum.class.getFields().length; i++) {
			try {
				Field field = FieldEnum.class.getFields()[i];
				
				/** 处理每一个term的DF,求得IDF **/
				Term t = new Term(field.get(new String()).toString());
				double docFreq  = 0;
				for (String term : terms) {
					docFreq += bridge.indexReader.docFreq(t.createTerm(term));
				}
				docFreq /= terms.length;
				double idf = Math.log10(docSum/docFreq);
				idf4Whole += idf;
				
				/** tf*idf **/
				/** 对每一个field根据docid读取indexreader里面的频率向量 **/
				TermFreqVector tfv = bridge.indexReader.getTermFreqVector(docID, field
						.get(new String()).toString());
				if(tfv == null){
					map.put(idx[0] + i, 0.0);
					map.put(idx[1] + i, 0.0);
					map.put(idx[2] + i, 0.0);
					map.put(idx[3] + i, 0.0);
					map.put(idx[4] + i, 0.0);
					continue;
				}
				double sumtfidf = 0, mintfidf = 0, maxtfidf = 0;
				for (String term : terms) {
					int idx = tfv.indexOf(term);
					if (idx != -1){
						int TF = tfv.getTermFrequencies()[idx];
						sumtfidf += TF*idf;
						sumtfidf4Whole += TF*idf;
						if (TF < mintfidf)
							mintfidf = TF;
						if (TF < mintfidf4Whole)
							mintfidf4Whole = TF;
						if (TF > maxtfidf)
							maxtfidf = TF;
						if (TF > maxtfidf4Whole)
							maxtfidf4Whole = TF;
					}
				}				
				
				/** 除了whole-page外的特征都保存进hashmap **/
				map.put(idx[0] + i, idf);
				map.put(idx[1] + i, sumtfidf);
				map.put(idx[2] + i, mintfidf);
				map.put(idx[3] + i, maxtfidf);
				map.put(idx[4] + i, sumtfidf / terms.length);
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		/** 加入whole-page的特征 **/
		map.put(idx[0]+4, idf4Whole/FieldEnum.class.getFields().length);//由于df4whole难以计算，故先如此折中
		map.put(idx[1]+4, sumtfidf4Whole);
		map.put(idx[2]+4, mintfidf4Whole);
		map.put(idx[3]+4, maxtfidf4Whole);
		map.put(idx[4]+4, sumtfidf4Whole/terms.length);//meanTF
		/** 返回hashmap **/
		return map;
	}

}
