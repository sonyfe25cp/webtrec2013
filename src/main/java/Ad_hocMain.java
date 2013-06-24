import java.util.List;

import lemurproject.indri.ParsedDocument;
import lucene.IndexTopKResults;
import lucene.QueryModel;
import lucene.QueryReader;
import edu.bit.dlde.utils.DLDELogger;


public class Ad_hocMain {
	
	/*
	 * 流程：
	 * 1.手工扩展50个查询(加入同义词，排除一些垃圾词)
	 * 2.根据查询从每份索引中取top1000
	 * 3.将10份结果存到一个索引中
	 * 4.针对每个查询计算各自文档的features
	 * 5.利用listrank实现排序
	 * 6.截取前1000个作为ad-hoc结果
	 */
	
	private static DLDELogger logger=new DLDELogger();
	public static void main(String[] args) {
		/*
		 * 从文件中读出50个查询
		 */
		List<QueryModel> queryList=QueryReader.readAll();
		/*
		 * 从索引服务器取出索引
		 */
		RawTopKQuery rawQuery=new RawTopKQuery();
		IndexTopKResults indexTopK=new IndexTopKResults();
		for(QueryModel model:queryList){
			String query=model.getQueryExpan();
			int queryId=model.getId();
			try {
				logger.info("begin to parse queryNum:"+queryId);
				List<ParsedDocument> parsedDocumentList=rawQuery.query(query, 1000);
				
				/*
				 * 索引该query原始的文档
				 */
				indexTopK.index(parsedDocumentList, queryId);
				
			} catch (Exception e) {
				logger.error("can't parse the query!!! id:"+queryId+" -- "+ query);
				e.printStackTrace();
				continue;
			}
			
		}
		indexTopK.close();
		rawQuery.close();
	}

}
