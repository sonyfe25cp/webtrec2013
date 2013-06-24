package edu.bit.dlde.feature;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import lucene.QueryModel;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.TermFreqVector;

/**
 * 这是一个用来获得多个特征的类，之所以如此实现，是出于效率和代码重复的考虑
 * 1.该类用来获得stream length
 *  3.sum of stream length normalized term frequency
 *  4.min of stream length normalized term frequency
 *  5.max of stream length normalized term frequency
 *  6.mean of stream length normalized term frequency
 * 
 *@author lins
 *@date 2012-7-30
 **/
public class SecondFeatureSet extends FeatureSet {
	public SecondFeatureSet(FeatureArray bridge) {
		super(bridge);
	}

	public final String[] notation = { "stream length",
			"sum of stream length normalized term frequency",
			"min of stream length normalized term frequency",
			"max of stream length normalized term frequency",
			"mean of stream length normalized term frequency"};
	public final int[] idx = { 11, 46, 51, 56, 61 };
	
	public HashMap<Integer, Double> getFeatures(QueryModel queryModel,
			int docID) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();

		/** 通过反射机制读取所有需要进行该项特征提取的field **/
		String[] terms = queryModel.getTerms();
		double sl4Whole = 0, sumslntf4Whole = 0, minslntf4Whole = 0, maxslntf4Whole = 0;
		for (int i = 0; i < FieldEnum.class.getFields().length; i++) {
			try {
				Field field = FieldEnum.class.getFields()[i];
				/** stream length**/
				Double sl = Double.valueOf(bridge.indexReader.document(docID)
						.getFieldable(field.get(new String()).toString()).stringValue().length());
				sl4Whole += sl;

				/** stream length normalized term frequency **/
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
				double sumslntf = 0, minslntf = 0, maxslntf = 0;
				for (String term : terms) {
					int idx = tfv.indexOf(term);
					if (idx != -1){
						double slntf = sl / tfv.getTermFrequencies()[idx];
						sumslntf += slntf;
						sumslntf4Whole += slntf;
						if (slntf < minslntf)
							minslntf = slntf;
						if (slntf < minslntf4Whole)
							minslntf4Whole = slntf;
						if (slntf > maxslntf)
							maxslntf = slntf;
						if (slntf > maxslntf4Whole)
							maxslntf4Whole = slntf;
					}
				}
				
				/** 除了whole-page外的特征都保存进hashmap **/
				map.put(idx[0] + i, sl);
				map.put(idx[1] + i, sumslntf);
				map.put(idx[2] + i, minslntf);
				map.put(idx[3] + i, maxslntf);
				map.put(idx[4] + i, sumslntf / terms.length);
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
		map.put(idx[0]+4, sl4Whole);
		map.put(idx[1]+4, sumslntf4Whole);
		map.put(idx[2]+4, minslntf4Whole);
		map.put(idx[3]+4, maxslntf4Whole);
		map.put(idx[4]+4, sumslntf4Whole/terms.length);//meanTF
		/** 返回hashmap **/
		return map;
	}

}
