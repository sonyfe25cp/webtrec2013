package edu.bit.dlde.feature;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import lucene.QueryModel;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;

/**
 * vsm
 * @author lins
 * @date 2012-7-31
 **/
public class VSM extends FeatureSet {
	public VSM(FeatureArray bridge) {
		super(bridge);
	}

	public final String notation = "vector space model";
	public final int idx = 101;

	@Override
	public HashMap<Integer, Double> getFeatures(QueryModel queryModel,
			int docID) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();

		/** 通过反射机制读取所有需要进行该项特征提取的field **/
		List<String> queryTerms = Arrays.asList(queryModel.getTerms());
		for (int i = 0; i < FieldEnum.class.getFields().length; i++) {
			try {
				Field field = FieldEnum.class.getFields()[i];

				/** 对每一个field根据docid读取indexreader里面的频率向量 **/
				TermFreqVector tfv = bridge.indexReader.getTermFreqVector(
						docID, field.get(new String()).toString());
				if (tfv == null) {
					map.put(idx + i, 0.0);
					continue;
				}

				/** 计算文档中的每一个term的tf*idf作为vsms的权重  **/
				double[] docVector = new double[tfv.size()];
				double[] queryVector = new double[tfv.size()];
				String[] docTerms = tfv.getTerms();
				for (int j = 0; j < tfv.size(); j++) {
					double tfidf = getTFIDF(docTerms[j], field
							.get(new String()).toString(), bridge.indexReader, tfv.getTermFrequencies()[j]);
					docVector[j] = tfidf;//附权重
					if (queryTerms.contains(docTerms[j])) {
						queryVector[j] = tfidf;//附权重
					}
				}
				/** 计算两个向量的cosine **/
				double vsm = getCosine(docVector, queryVector);

				/** 除了whole-page外的特征都保存进hashmap **/
				map.put(idx + i, vsm);
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
		map.put(idx+4, map.get(idx));//将body作为whole
		/** 返回hashmap **/
		return map;
	}

	private static TreeMap<Term, Double> term2tfidf  = new TreeMap<Term, Double>();
	
	/**
	 * 计算tfidf
	 */
	private double getTFIDF(String text, String field, IndexReader indexReader, int tf) {
		/** 先查看缓存里面有没有 **/
		Term term = new Term(field, text);
		if (term2tfidf.containsKey(term))
			return term2tfidf.get(term);

		/** 没有就计算 **/
		long docSum = indexReader.numDocs();
		double tfidf = 0;
		try {
			double df = Math.max(indexReader.docFreq(term), 1);
			double idf = Math.log10(docSum / df);
			tfidf = tf * idf;
			term2tfidf.put(term, tfidf);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return tfidf;
	}

	/**
	 * 用来计算两个向量夹角cosine的函数
	 */
	private double getCosine(double[] docVector, double[] queryVector) {
		double a = 0, b = 0, c = 0;

		for (int i = 0; i < docVector.length; i++) {
			a += docVector[i] * queryVector[i];
			b += docVector[i] * docVector[i];
			c += queryVector[i] * queryVector[i];
		}

		return a / (Math.sqrt(b) * Math.sqrt(c));
	}

}
