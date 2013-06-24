package edu.bit.dlde.feature;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.TreeMap;

import lucene.QueryModel;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;

/**
 * 计算lmir
 * @author lins
 * @date 2012-7-31
 **/
public class LMIR extends FeatureSet {
	public LMIR(FeatureArray bridge) {
		super(bridge);
	}
	public final String notation = "LMIR.ABS";
	public final int idx = 111;
	final double delta = 0.5;

	@Override
	public HashMap<Integer, Double> getFeatures(QueryModel queryModel,
			int docID) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();

		/** 通过反射机制读取所有需要进行该项特征提取的field **/
		String[] terms = queryModel.getTerms();
		for (int i = 0; i < FieldEnum.class.getFields().length; i++) {
			try {
				Field field = FieldEnum.class.getFields()[i];

				TermFreqVector tfv = bridge.getCachedTermFreqVector(docID,
						field.get(new String()).toString());

				if (tfv == null) {
					map.put(idx + i, 0.0);
					continue;
				}

				/** 计算LMIR [Page8 取delta=0.5] **/
				double lmir = 1, len = Math.max(1,
						bridge.getFeatures().get(11 + i));
				for (String term : terms) {
					double tf, p;
					Double tc;
					int idx = tfv.indexOf(term);
					if (idx != -1) {
						tf = tfv.getTermFrequencies()[idx];
						tc = term2count.get(new Term(field.get(new String())
								.toString(), term));
						if (tc == null)
							p = 0;
						else
							p = tc / termsTtlCount;
						lmir *= (0.5 * tf / len + 0.5 * p);
					} else {
						continue;
					}
				}
				
				/** 除了whole-page外的特征都保存进hashmap **/
				map.put(idx + i, lmir);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		/** 加入whole-page的特征 **/
		map.put(idx + 4, map.get(idx));//使用body的值充当whole
		
		/** 返回hashmap **/
		return map;
	}

	private static long termsTtlCount = -1;
	private static TreeMap<Term, Double> term2count = new TreeMap<Term, Double>();

	/**
	 * 非uniq的term
	 */
	public static long getTermsCount(IndexReader indexReader) {
		if (termsTtlCount == -1) {
			try {
				termsTtlCount = 0;
				TermEnum tEnum = indexReader.terms();
				while (tEnum.next()) {
					Term t = tEnum.term();
					TermDocs tDocs = indexReader.termDocs(t);
					double termCount = 0;
					while (tDocs.next()) {
						termsTtlCount += tDocs.freq();
						termCount += tDocs.freq();
					}
					term2count.put(t, termCount);
				}
			} catch (IOException e) {
				e.printStackTrace();
				termsTtlCount = -1;
			}
		}
		return termsTtlCount;
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException{
		for (int i = 0; i < FieldEnum.class.getFields().length; i++) {
			Field field = FieldEnum.class.getFields()[i];
			System.out.println(field.get(new String()).toString());
		}
	}
}
