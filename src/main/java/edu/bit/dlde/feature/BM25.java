package edu.bit.dlde.feature;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import lucene.QueryModel;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;

/**
 * 该类用来获得BM25特征
 * @author lins
 * @date 2012-7-31
 **/
public class BM25 extends FeatureSet {
	public BM25(FeatureArray bridge) {
		super(bridge);
	}

	public final String notation = "BM25";
	public final int idx = 106;

	@Override
	public HashMap<Integer, Double> getFeatures(QueryModel queryModel,
			int docID) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();

		/** 通过反射机制读取所有需要进行该项特征提取的field **/
		String[] terms = queryModel.getTerms();
		long docSum = bridge.indexReader.numDocs();
		double bm25Whole = 0;
		for (int i = 0; i < FieldEnum.class.getFields().length; i++) {
			try {
				Field field = FieldEnum.class.getFields()[i];

				TermFreqVector tfv = bridge.getCachedTermFreqVector(
						docID, field.get(new String()).toString());

				if (tfv == null) {
					map.put(idx + i, 0.0);
					continue;
				}

				/** 计算BM25 [Page8 取k=1,b=1] **/
				Term t = new Term(field.get(new String()).toString());
				double bm25 = 0, lenD = bridge.getFeatures().get(11 + i);
				for (String term : terms) {
					double docFreq, idf, tf;
					int idx = tfv.indexOf(term);
					if (idx != -1) {
						tf = tfv.getTermFrequencies()[idx];
					} else {
						continue;
					}
					docFreq = bridge.indexReader.docFreq(t.createTerm(term));
					idf = Math.log10(docSum/docFreq);
					bm25 += idf * tf * 2 / (tf + lenD / getAVDL(bridge.indexReader, field.get(new String()).toString()));
				}
				
				/** 除了whole-page外的特征都保存进hashmap **/
				map.put(idx + i, bm25);
				bm25Whole+=bm25;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		/** 加入whole-page的特征 **/
		map.put(idx + 4, bm25Whole);//由于df4whole难以计算，故先如此折中
		/** 返回hashmap **/
		return map;
	}

	private static double AVDL = -1.0;
	
	/**
	 * 平均文档长度,sum of doc length/doc number
	 */
	public static double getAVDL(IndexReader indexReader, String field){
		long docSum = Math.max(indexReader.numDocs(), 1);
		if (AVDL == -1) {
			AVDL = 0;
			for (int i = 0; i < docSum; i++) {
				if(!indexReader.isDeleted(i)){
					try {
						String docStr = indexReader.document(i).get(field);
						if (docStr != null)
							AVDL += docStr.length() / docSum;
					} catch (CorruptIndexException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return AVDL;
	}
}
