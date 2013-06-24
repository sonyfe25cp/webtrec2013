package lucene;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * model for ranking
 * 
 * @author ChenJie
 * 
 */
public class RankModel {

	private int queryId;
	private int docId;
	private ArrayList<Double> features;//事实上完全不建议用ArrayList这么做，因为这样很没有扩展性
	private ArrayList<Integer> index;
	
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("queryId:"+queryId+" docId: "+docId);
		sb.append("features:\t");
		for(double feature:features){
			sb.append(feature+ " ");
		}
		sb.append("\t");
		return sb.toString();
	}

	public RankModel() {

	}

	public RankModel(int docId, int queryId) {
		this.docId = docId;
		this.queryId = queryId;
	}

	public int getQueryId() {
		return queryId;
	}

	public int getDocId(){
		return docId;
	}
	
	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}

	public ArrayList<Double> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<Double> features) {
		this.features = features;
	}

	/** ms l2r dataset最多136个特征 **/
	final int MAX_FEATURE_COUNT = 136;

	/**
	 * 从hashmap里面读入特征，保存为ArrayList。index额外保存特征对应与ms l2r dataset的序号
	 */
	public RankModel loadFeaturesFromHashMap(HashMap<Integer, Double> map) {
		features = new ArrayList<Double>();
		index = new ArrayList<Integer>();
		for (int i = 0; i < MAX_FEATURE_COUNT; i++) {
			if (map.containsKey(i)) {
				features.add(map.get(i));
				index.add(i);
			}
		}
		return this;
	}
}
