package edu.bit.dlde.feature;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import lucene.QueryModel;

import org.apache.lucene.index.TermFreqVector;

/**
 * 这是一个用来获得多个特征的类，之所以如此实现，是出于效率和代码重复的考虑
 * 1.该类用来获得covered query term number. PS: 个人以为covered query term number就是query被分解成一个个term后，所有term在各个位置出现与否的累加;
 * 2.该类用来获得covered query term ratio. PS: 个人以为covered query term ratio是covered query term number/term数目;
 *  3.sum of term frequency
 *  4.min of term frequency
 *  5.max of term frequency
 *  6.mean of term frequency
 *  7.boolean model. 都有为1，存在没有则为0
 * @author lins
 * @date 2012-7-30
 **/
public class FirstFeatureSet extends FeatureSet {
	public FirstFeatureSet(FeatureArray bridge) {
		super(bridge);
	}

	public final String[] notation = { "covered query term number",
			"covered query term ratio", "sum of term frequency",
			"min of term frequency", "max of term frequency","mean of term frequency","boolean model" };
	public final int[] idx = { 1, 6, 21, 26, 31,36,96 };

	/**
	 * 1.通过反射机制读取所有需要进行该项特征提取的field 
	 * 2.对每一个field进行处理
	 * 3.保存进hashmap 
	 * 4.返回hashmap
	 * 
	 * @see edu.bit.dlde.feature.FeatureSet#getFeatures(edu.bit.dlde.feature.ProcessedQuery,
	 *      int, org.apache.lucene.index.IndexReader)
	 */
	public HashMap<Integer, Double> getFeatures(QueryModel queryModel,
			int docID) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		
		/** 通过反射机制读取所有需要进行该项特征提取的field **/
		String[] terms = queryModel.getTerms();
		boolean[] exits = new boolean[terms.length];
		double sumTF4Whole = 0,minTF4Whole = 0, maxTF4Whole=0;//meanTF已经计算
		for (int i = 0; i < FieldEnum.class.getFields().length; i++) {
			try {
				Field field = FieldEnum.class.getFields()[i];
				double cqtn = 0, sumTF = 0, minTF = 0, maxTF = 0;
				/** 对每一个field根据docid读取indexreader里面的频率向量 **/
				TermFreqVector tfv = bridge.indexReader.getTermFreqVector(docID, field
						.get(new String()).toString());

				if(tfv == null){
					map.put(idx[0] + i, 0.0);
					map.put(idx[1] + i, 0.0);
					map.put(idx[2] + i, 0.0);
					map.put(idx[3] + i, 0.0);
					map.put(idx[4] + i, 0.0);
					map.put(idx[5] + i, 0.0);
					map.put(idx[6] + i, 0.0);
					continue;
				}
				
				/** 累加每一个term的出现与否 **/
				for (int j =0; j < terms.length; j++) {
					String term = terms[j];
					int idx = tfv.indexOf(term);
					if (idx != -1){
						cqtn ++;
						exits[j]=true;
						double TF = tfv.getTermFrequencies()[idx];
						sumTF += TF;
						sumTF4Whole += TF;
						if (TF < minTF)
							minTF = TF;
						if (TF < minTF4Whole)
							minTF4Whole = TF;
						if (TF > maxTF)
							maxTF = TF;
						if (TF > maxTF4Whole)
							maxTF4Whole = TF;
					}
				}
				
				/** 除了whole-page外的特征都保存进hashmap **/
				map.put(idx[0] + i, cqtn);
				map.put(idx[1] + i, cqtn / terms.length);
				map.put(idx[2] + i, sumTF);
				map.put(idx[3] + i, minTF);
				map.put(idx[4] + i, maxTF);
				map.put(idx[5] + i, sumTF / terms.length);// meanTF
				map.put(idx[6], terms.length == cqtn?1.0:0.0);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		/** 加入whole-page的特征 **/
		double cqtn4Whole = Double.valueOf(countTrue(exits));
		map.put(idx[0] + 4, cqtn4Whole);
		map.put(idx[1] + 4, cqtn4Whole / terms.length);
		map.put(idx[2] + 4, sumTF4Whole);
		map.put(idx[3] + 4, minTF4Whole);
		map.put(idx[4] + 4, maxTF4Whole);
		map.put(idx[5] + 4, sumTF4Whole / terms.length);// meanTF
		map.put(idx[6] + 4, cqtn4Whole == terms.length ? 1.0 : 0.0);
	
		/** 返回hashmap **/
		return map;
	}
	
	private int countTrue(boolean[] args){
		int count = 0;
		for(boolean b: args){
			if(b)
				count++;
		}
		return count;
	}
	
	 public static void main(String[] args) {
		 boolean[] exits = new boolean[5];
		 System.out.println(exits[0]);
	 }
}
