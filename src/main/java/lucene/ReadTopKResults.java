package lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.NIOFSDirectory;

import edu.bit.dlde.feature.FeatureArray;

/**
 * 根据queryId从索引里面读取前K个文档，或者读取所有的文档。
 * 这些文档均是被抽取过特征的，并被保存在List<RankModel>里面。
 */
public class ReadTopKResults {
	private String indexPath = "/data/webtrec/index_10docs";
	private NIOFSDirectory dir;
	private IndexReader indexReader;
	private FeatureArray features;// 建议不要轻易重置
	private List<QueryModel> queryList;
	private boolean isCorrectlyInitialed = false;

	/**
	 * 初始化各种需要长久存放的变量,需要释放则调用close()
	 */
	public ReadTopKResults() {
		try {
			dir = new NIOFSDirectory(new File(indexPath));
			indexReader = IndexReader.open(dir, true);
			features = FeatureArray.getInstacne(indexReader);
			queryList = QueryReader.readAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isCorrectlyInitialed = true;
	}

	/**
	 * @return 返回对应queryid的前topK个文档。这些文档已经抽取过特征。
	 */
	public List<RankModel> getTopKDocumentFromIndexById(int queryId, int topK) {
		/** 判断初始化成功 **/
		if (!isCorrectlyInitialed)
			return null;

		/** 根据queryId读索引 **/
		List<RankModel> rankModelList = new ArrayList<RankModel>();
		IndexSearcher searcher = null;
		try {
			searcher = new IndexSearcher(indexReader);
			Query query = new TermQuery(
					new Term("qid", String.valueOf(queryId)));
			TopDocs topDocs = searcher.search(query, topK);
			System.out.println("qid=" + queryId + " -- total="
					+ topDocs.totalHits);

			/** 对每一个对应queryId的文档进行抽取特征并包存进rankModelList **/
			for (int i = 0; i < topDocs.totalHits; i++) {
				int docID = topDocs.scoreDocs[i].doc;
				HashMap<Integer, Double> map = features.processDocument(
						queryList.get(queryId - 151), docID);
				rankModelList.add(new RankModel(docID, queryId)
						.loadFeaturesFromHashMap(map));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (searcher != null)
				try {
					searcher.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		/** 返回结果 **/
		return rankModelList;
	}
	
	/**
	 * @return 返回对应queryid的所有文档。这些文档已经抽取过特征。
	 */
	public List<RankModel> getAllDocumentFromIndexById(int queryId) {
		return getTopKDocumentFromIndexById(queryId, Integer.MAX_VALUE);
	}

	/**
	 * 关闭IndexReader和MMapDirectory
	 */
	public void close() {
		try {
			indexReader.close();
			dir.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			isCorrectlyInitialed = false;
		}
	}

	public static void main(String[] args) {
		ReadTopKResults rtr = new ReadTopKResults();
		List<RankModel> models = rtr.getAllDocumentFromIndexById(160);
		for(RankModel model:models){
			System.out.println(model.toString());
		}
		rtr.close();
	}
}
