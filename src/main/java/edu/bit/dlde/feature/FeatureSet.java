package edu.bit.dlde.feature;

import java.util.HashMap;

import lucene.QueryModel;

/**
 * 指定了用来获取feature的接口
 * 
 * @author lins
 * @date 2012-7-30
 **/
public abstract class FeatureSet {
	FeatureArray bridge;// 像桥一样联通各个FeatureSet

	public FeatureSet(FeatureArray bridge) {
		this.bridge = bridge;
	}

	public abstract HashMap<Integer, Double> getFeatures(
			QueryModel queryModel, int docID);
}
